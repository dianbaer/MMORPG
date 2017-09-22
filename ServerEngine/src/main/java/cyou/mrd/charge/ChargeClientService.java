package cyou.mrd.charge;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.LogService;
import cyou.mrd.Platform;
import cyou.mrd.entity.Player;
import cyou.mrd.entity.PropertyPool;
import cyou.mrd.event.Event;
import cyou.mrd.event.GameEvent;
import cyou.mrd.event.OPEvent;
import cyou.mrd.io.OP;
import cyou.mrd.io.OPHandler;
import cyou.mrd.io.Packet;
import cyou.mrd.io.http.HOpCode;
import cyou.mrd.io.http.HSession;
import cyou.mrd.io.http.JSONPacket;
import cyou.mrd.io.tcp.OpCode;
import cyou.mrd.io.tcp.TcpPacket;
import cyou.mrd.io.tcp.connector.single.SingleClient;
import cyou.mrd.service.PlayerService;
import cyou.mrd.service.Service;
import cyou.mrd.util.ErrorHandler;

/**
 * 管理用户付费请求 HTTP
 * 
 * @author mengpeng
 */
@OPHandler(TYPE = OPHandler.HTTP_EVENT)
public class ChargeClientService implements Service {
	private static final Logger log = LoggerFactory.getLogger(ChargeClientService.class);

	protected String gameCode;

	protected int gameId;

	protected int serverId;

	public static final int WAIT_TIME = 1000 * 30;// http请求等待时间

	public static final int CHARGE_STATE_NOT_VERIFY = -1;// 未验证通过

	public static final int CHARGE_STATE_TIMEOUT = -2;// 连接超时 未验证通过

	public static final int CHARGE_STATE_REPEAT = -3;// 重复的账单信息

	private ConcurrentHashMap<Long, Charge> serial2charges = new ConcurrentHashMap<Long, Charge>();

	public static long BASETIME = 0L;

	protected BlockingQueue<Charge> chargeToDB = new LinkedBlockingQueue<Charge>();

	private AtomicInteger inc_Atom = new AtomicInteger(0);

	public static final String PROPERTY_NOADTIME = "property_noadtime";
	
	public static final String PROPERTY_LAST_BUY_TIME = "property_lastbuytime";
	
	private static final String SANDBOXVERSION = Platform.getConfiguration().getString("sandboxversion");


	public String getId() {
		return "ChargeClientService";
	}

	public void startup() throws Exception {
		gameId = Integer.parseInt(Platform.getGameId());
		gameCode = Platform.getGameCode();
		serverId = Platform.getServerId();
		BASETIME = System.currentTimeMillis();
		Thread daemonSaveCharge = new Thread(new DaemonSaveChargeToDB(), "Daemon-ChargeToDB[Server]");
		daemonSaveCharge.start();
	}

	/**
	 * 判断是否是苹果测试账号
	 */
	private boolean verfiySandBoxPlayer(Player player){
		return true; //本地调试使用，正式注掉！！！
		//return player.getClientVersion() != null && player.getClientVersion().startsWith(SANDBOXVERSION);
	}
	
	public void shutdown() throws Exception {
	}

	/**
	 * 请求校验账单 client--->server bid:xxx String 应用bandle id pid:xxx String 商品的id
	 * receipt:xxx String Base64编码的单据信息
	 * eg.{"opcode":95,"data":{"bid":"com.cyou.dracula"
	 * ,"pid":"com.cyou.dracula.productid_0.99"
	 * ,"receipt":"eivmxlzjieowqhfewpqtojma"}}
	 */
	@OP(code = HOpCode.BILLING_VERIFY_CLIENT)
	public void requestCharge(Packet packet, HSession session) {
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		Platform.getLog().logCharge("[HTTPRequest] packet:{} session :{}]", packet.toString(), session.getSessionId());
		packet.getRunTimeMonitor().knock("requestCharge");
		Player p = (Player) session.client();
		if (p == null) {
			Platform.getLog().logCharge("[requestCharge] need login; session:{}, packet:{}", session.getSessionId(), packet.toString());
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			return;
		}
		packet.getRunTimeMonitor().knock("chargePlayers.contains");
		boolean finallySetStateBack = false;
		try {
			synchronized (p) {
				if (p.isTransactionStatus()) {
					Platform.getLog().logCharge("[requestCharge] has not complet order; playerId:{}, packet:{}", p.getInstanceId(),
							packet.toString());
					ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_32, packet.getopcode());
					return;
				} else {
					p.getPool().setLong(PROPERTY_LAST_BUY_TIME, System.currentTimeMillis());
					p.setTransactionStatus(true);
					log.info("charge : change player transactionStatus true,playerId:{}",p.getId());
					finallySetStateBack = true;
				}
			}
			packet.getRunTimeMonitor().knock("contains. ok");
			String bid = packet.getString("bid");
			String pid = packet.getString("pid");
			String receipt = packet.getString("receipt");
			String tid = "";
			if(packet.containsKey("tid")){
				tid = packet.getString("tid");
			}
			//处理sandbox验证
			if(verfiySandBoxPlayer(p)){
				this.sandboxVerfiy(p,pid,tid,session);
				return;
			}
			// 构造支付对象
			Charge charge = new Charge(session);
			charge.setPlayerId(p.getId());
			charge.setAccountId(p.getAccountId());
			charge.setBundleId(bid);
			charge.setProductId(pid);
			charge.setReceipt(receipt);
			charge.setRequestTime(new Date());
			charge.setServerReceiveClient(1);
			packet.getRunTimeMonitor().knock("new Charge");
			charge.setSerialNumber(getSerialNum());
			packet.getRunTimeMonitor().knock("createSync Charge");
			Platform.getLog().logCharge(
					MessageFormat.format("[CHARGE]STEP[1]BID[{0}]PID[{1}]RECEIPT[{2}ACCOUNTID[{3}]PLAYERID[{4}]SERIALNUM[{5}]", bid, pid,
							receipt, p.getAccountId(), p.getId(), charge.getSerialNumber()));
			TcpPacket pt = new TcpPacket(OpCode.WORLD_VERIFY_CLIENT);
			pt.putString(gameCode);
			pt.putLong(charge.getSerialNumber());
			pt.putInt(serverId);
			pt.putInt(p.getAccountId());
			pt.putInt(p.getId());
			pt.putString(bid);
			pt.putString(pid);
			pt.putString(receipt);
			// 需要加入worldServer为空的判断.
			SingleClient sc = Platform.worldServer();
			if (sc != null) {
				packet.getRunTimeMonitor().knock("serial2charges");
				serial2charges.put(charge.getSerialNumber(), charge);
				log.info("requestCharge() loop:new Charage serial2charges size:{}", serial2charges.size());
				packet.getRunTimeMonitor().knock("serial2charges.put chargePlayers.add");
				sc.send(pt);
				charge.setServerSendWorld(1);
				packet.getRunTimeMonitor().knock("updateSync(charge)");
				Platform.getLog().logCharge(
						MessageFormat.format("[CHARGE]STEP[2]CHARGESIZE[{0}]ACCOUNTID[{1}]PLAYERID[{2}]SERVERID[{3}]SERIALNUM[{4}]",
								serial2charges.size(), p.getAccountId(), p.getId(), Platform.getServerId(), charge.getSerialNumber()));
				packet.getRunTimeMonitor().knock("updateSync(charge)");
				synchronized (charge.lock) {
					try {
						packet.getRunTimeMonitor().knock("synchronized(charge");
						charge.lock.wait(WAIT_TIME);
						packet.getRunTimeMonitor().knock("synchronized(wait");
					} catch (InterruptedException e) {
						log.error("InterruptedException", e);
						Platform.getLog().logCharge("InterruptedException", e.getMessage());
					}
					packet.getRunTimeMonitor().knock("synchronized(fetch");
					if (charge.state == 0) {// 未收到回复 返回超时错误
						JSONPacket pa = new JSONPacket(HOpCode.BILLING_VERIFY_SERVER);
						pa.put("result", CHARGE_STATE_TIMEOUT);
						pa.put("accountId", p.getAccountId());
						pa.put("tid",tid);
						session.send(pa);
						charge.setErrorCode(Charge.CHARGE_ERROR_CODE_2);
						charge.setFinishTime(new Date());
						log.info("[charge] GameServer not receive world return:{}", pa.toString());
						Platform.getLog()
								.logCharge(
										MessageFormat.format(
												"[CHARGE]ERROR[{0}]STEP[13]ACCOUNTID[{1}]PLAYERID[{2}]SERVERID[{3}]SERIALNUM[{4}]",
												"not receive result", p.getAccountId(), p.getId(), Platform.getServerId(),
												charge.getSerialNumber()));
						chargeToDB.add(charge);
						packet.getRunTimeMonitor().knock("state == 0");

					}
					if (charge.state == 1) {// 已收到回复
						JSONPacket jp = charge.getRetPacket();
						jp.put("tid", tid);
						session.send(jp);
						Platform.getLog().logCharge(
								MessageFormat.format("[CHARGE]STEP[13]PLAYERID[{0}]ACCOUNTID[{1}]MSG[{2}]SERVERID[{3}]SERIALNUM[{4}]",
										p.getId(), p.getAccountId(), "return client charge result", Platform.getServerId(),
										charge.getSerialNumber()));
						int amount = charge.getResult();
						int imoney = amount;
						int extraImoneyRatio = charge.getExtraImoneyRatio();
						if (amount > 0) {// 增加促销比例字段处理
							amount = Math.round((float)amount * ((float)(100 + extraImoneyRatio))/100);
							if(charge.getType() == 0) {
								imoney = p.getAccount().getImoney() + amount;
								p.getAccount().setImoney(imoney);
							}else if(charge.getType() == 1){
								int money = p.getMoney();
								money += amount;
								p.setMoney(money);//不能保证好使， 为回档做一点贡献。
							}else {
								log.info("[charge] charge.getType() == {}!! not have handler!", charge.getType());
							}
							if (charge.getNoADTime() > 0) {
								if (p.getPool() == null) {
									p.setPool(new PropertyPool());
								}
								int oldTime = p.getPool().getInt(PROPERTY_NOADTIME, 0);
								// 第一次获得 || 过期
								if (oldTime == 0 || System.currentTimeMillis() / 1000 > oldTime) {
									p.getPool().setInt(PROPERTY_NOADTIME, (int) (System.currentTimeMillis() / 1000 + charge.noADTime));
								} else {// 以前的时间还未过期
									int newTime = p.getPool().getInt(PROPERTY_NOADTIME) + charge.getNoADTime();
									p.getPool().setInt(PROPERTY_NOADTIME, newTime);
								}
							}
							PlayerService playerService = Platform.getAppContext().get(PlayerService.class);
							//playerService.savePlayer(p, true,false);
							p.notifySaveForce();
							Platform.getAppContext().get(LogService.class).reCharge(p, charge.getPrice());
							Platform.getLog().logCharge(
									MessageFormat.format(
											"[CHARGE]STEP[14]PLAYERID[{0}]ACCOUNTID[{1}]AMOUNT[{2}]SERVERID[{3}]MSG[{4}]SERIALNUM[{5}]TOTLEMONEY[{6}]",
											p.getId(), p.getAccountId(), amount, Platform.getServerId(), "charge success add money",
											charge.getSerialNumber(),imoney));
						} else {
							Platform.getLog().logCharge(
									"[CHARGE]ERROR:{}STEP[14]AMOUNT:{}PLAYERID:{}ACCOUNTID:{}SERVERID:{}SERIALNUM:{}",
									new Object[] { "result < 0", amount, p.getId(), p.getAccountId(), Platform.getServerId(),
											charge.getSerialNumber() });
						}
						charge.setResult(amount);
						charge.setFinishTime(new Date());
						chargeToDB.add(charge);
						packet.getRunTimeMonitor().knock("state == 1");
					}
				}
				packet.getRunTimeMonitor().knock("synchronized(charge.lock)");
				serial2charges.remove(charge.getSerialNumber());
				log.info("requestCharge() loop:remove Charage serial2charges size:{}", serial2charges.size());
				packet.getRunTimeMonitor().knock("serial2charges.remove(charge)");
				p.setTransactionStatus(false);
				log.info("charge : change player transactionStatus false,playerId:{}",p.getId());
				packet.getRunTimeMonitor().knock("chargePlayers.remove");
			} else {
				JSONPacket pa = new JSONPacket(HOpCode.BILLING_VERIFY_SERVER);
				pa.put("result", CHARGE_STATE_TIMEOUT);
				pa.put("accountId", p.getAccountId());
				pa.put("tid", tid);
				session.send(pa);
				charge.setErrorCode(Charge.CHARGE_ERROR_CODE_1);
				charge.setFinishTime(new Date());
				chargeToDB.add(charge);
				log.info("[charge]world is not connected !! return:{}", pa.toString());
				Platform.getLog().logCharge(
						MessageFormat.format("[CHARGE]ERROR[{0}]STEP[3]PLAYERID[[1]]ACCOUNTID[{2}]SERVERID[{3}]SERIALNUM[{4}]",
								"not receive result", p.getId(), p.getAccountId(), Platform.getServerId(), charge.getSerialNumber() + ""));
			}
			Platform.getLog().logCharge("requestCharge end,playerId:{}", p.getInstanceId());
		} finally {
			if(finallySetStateBack) {
				p.setTransactionStatus(false);
				log.info("charge : change player transactionStatus false,playerId:{}",p.getId());
			}
			p.notifySaveForce();
		}
	}

	/**
	 * 下发沙盒验证成功状态 需要在此方法内手动增加钱或灯笼
	 * @param p
	 * @param pid
	 * @param tid
	 * @param session
	 */
	protected void sandboxVerfiy(Player p, String pid, String tid, HSession session) {
		int amount = 100;
		JSONPacket pa = new JSONPacket(HOpCode.BILLING_VERIFY_SERVER);
		pa.put("result", amount);
		pa.put("accountId", p.getAccountId());
		pa.put("tid",tid);
		int imoney = p.getAccount().getImoney() + amount;
		p.getAccount().setImoney(imoney);
		p.notifySave();
		session.send(pa);
	}

	/**
	 * 请求应用商品列表 client-->server bid:xxx String 应用的bandle id
	 * eg.{"opcode":97,"data":{ "bid":"com.cyou.dracula"}}
	 */
	@OP(code = HOpCode.PRODUCE_LIST_CLIENT)
	public void requestProductList(Packet packet, HSession session) {
		Platform.getLog().logCharge("[HTTPRequest] packet:{} session :{}]", packet.toString(), session.getSessionId());
		Player p = (Player) session.client();
		if (p == null) {
			Platform.getLog()
					.logCharge("[requestProductList] need login; session:{}, packet:{}", session.getSessionId(), packet.toString());
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			return;
		}
		String bid = packet.getString("bid");
		Platform.getLog().logCharge("requestProductList loop:productId:{}", bid);
		List<AppStoreProduct>  products = null;
		if(verfiySandBoxPlayer(p)){
			products = this.getSandBoxProducts();
			
		}else{
			if(Platform.worldServer() == null || !Platform.worldServer().getSession().isConnected()){
				Platform.getAppContext().get(ProductListService.class).clearProductList();
			}
			products = Platform.getAppContext().get(ProductListService.class).getProducts();
		}
		JSONPacket retP = new JSONPacket(HOpCode.PRODUCE_LIST_SERVER);
		JSONArray ja = new JSONArray();
		for(AppStoreProduct asp:products){
			JSONObject ob = new JSONObject();
			ob.put("id", asp.getProductID());
			ob.put("name", asp.getProductName());
			ob.put("des",asp.getTitle());
			ob.put("price", asp.getPrice());
			ob.put("demon", asp.getImoney());
			ob.put("icon",asp.getIcon());
			ob.put("noAD", asp.getNoADTime());
			ob.put("type", asp.getType());
			ob.put("extraImoneyRatio", asp.getExtraImoneyRatio());
			ja.add(ob);
		}
		retP.put("products", ja);
		//发送消息包
		session.send(retP);
	}

	/**
	 * 工程自己实现   伪造商品列表的功能（苹果审核使用）
	 * @return
	 */
	protected List<AppStoreProduct> getSandBoxProducts() {
		List<AppStoreProduct>  products = new ArrayList<AppStoreProduct>();
		AppStoreProduct product = new AppStoreProduct();
		product.setProductID("com.mrd.test");
		product.setExtraImoneyRatio(10);
		product.setIcon("icon.png");
		product.setImoney(200);
		product.setNoADTime(1000000);
		product.setPrice(10);
		product.setProductName("灯笼111");
		product.setTitle("测试灯笼");
		product.setType(0);
		products.add(product);
		return products;
	}

	// 返回付费结果
	public void callbackClient(long serialNum, JSONPacket json) {
		Charge charge = serial2charges.get(serialNum);
		if (charge != null) {
			charge.setAlreadyHandle();
			charge.setRetPacket(json);
			int result = 0;
			int rmb = 0;
			if (json.containsKey("result")) {
				result = json.getInt("result");
			}
			if(json.containsKey("rmb")){
				rmb = json.getInt("rmb");
			}
			if (json.containsKey("noADTime")) {
				charge.setNoADTime(json.getInt("noADTime"));
			}
			charge.setResult(result);
			charge.setPrice(rmb);
			synchronized (charge.lock) {
				charge.lock.notify();
			}
		}
	}

	/**
	 * 8 GameId 8 ServerId TODO 这里应该加入playerId的限制, 防止多线程下并发生产订单号
	 */
	public long getSerialNum() {
		Platform.getLog().logCharge("getSerialNum()");
		long redoLong = 0;
		long a1 = ((long) gameId << 52) & 0xFFF0000000000000L;
		long b1 = ((long) serverId << 44) & 0x000FF00000000000L;
		long c1 = (((System.currentTimeMillis() - BASETIME) / 1000) << 20) & 0x00000FFFFFF00000L;
		long d1 = ((long) inc_Atom.getAndIncrement()) & 0x00000000000FFFFFL;
		redoLong = a1 | b1 | c1 | d1;
		Platform.getLog().logCharge("[Serial]getSerialNum end:return :{}", redoLong);
		return redoLong;
	}

	public Charge getChargeBySerialNum(long serialNum) {
		if (serial2charges.containsKey(serialNum)) {
			return serial2charges.get(serialNum);
		} else {
			return null;
		}

	}

	class DaemonSaveChargeToDB implements Runnable {
		public void run() {
			while (true) {
				try {
					Charge charge = chargeToDB.take();
					long t1 = System.nanoTime();
					Platform.getEntityManager().createSync(charge);
					Platform.getLog().logCharge("+++++++++++++++++++++charge To DB waste Time:{}ms", (System.nanoTime() - t1) / 1000000);
				} catch (Throwable e) {
					Platform.getLog().logCharge("Charge to DB error", e);
				}
			}
		}
	}
	
	//避免交易状态30S后没有回置   玩家登陆时进行检验 （双保险)
	@OPEvent(eventCode = GameEvent.EVENT_PLAYER_LOGINED)
	protected void playerLogined(Event event) {
		Player player = (Player) event.param1;
		if(player == null)
			return;
		if(player.isTransactionStatus()){
			long time = System.currentTimeMillis() - player.getPool().getLong(PROPERTY_LAST_BUY_TIME, 0L);
			if(time > WAIT_TIME){
				player.setTransactionStatus(false);
				player.notifySave();
				log.info("login reset player transaction status  playerId:{},account:{}",player.getId(),player.getAccount().toString());
			}
		}
	}

}
