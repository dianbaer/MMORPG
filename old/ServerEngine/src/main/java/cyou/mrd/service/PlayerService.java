package cyou.mrd.service;

import java.util.Date;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.ObjectAccessor;
import cyou.mrd.Platform;
import cyou.mrd.account.Account;
import cyou.mrd.account.Account.ResetImoneyRet;
import cyou.mrd.account.AccountService;
import cyou.mrd.charge.ChargeClientService;
import cyou.mrd.data.Data;
import cyou.mrd.data.DataKeys;
import cyou.mrd.entity.Player;
import cyou.mrd.event.Event;
import cyou.mrd.event.GameEvent;
import cyou.mrd.event.OPEvent;
import cyou.mrd.io.AsyncCall;
import cyou.mrd.io.OP;
import cyou.mrd.io.OPHandler;
import cyou.mrd.io.Packet;
import cyou.mrd.io.http.HOpCode;
import cyou.mrd.io.http.HSession;
import cyou.mrd.io.http.JSONPacket;
import cyou.mrd.io.http.SessionManager;
import cyou.mrd.util.ErrorHandler;
import cyou.mrd.util.IdUtil;
import cyou.mrd.util.RunTimeMonitor;
import cyou.mrd.util.Time;

@OPHandler(TYPE = OPHandler.HTTP_EVENT)
public class PlayerService implements Service {

	private static final Logger log = LoggerFactory.getLogger(PlayerService.class);
	protected static final int DB_SAVE_SPLIT_SECOND = Platform.getConfiguration().getInt("db_save_split_second");

	public static final int NOTIFY_CLIENT = 1;

	public static final int NOT_NOTIFY_CLIENT = 0;

	public static final String PLAYER_NOTIFY_CLIENT = "notifyClient";

	@Override
	public String getId() {
		return "PlayerService";
	}

	@Override
	public void startup() throws Exception {
	}

	@Override
	public void shutdown() throws Exception {
	}

	@OP(code = HOpCode.PLAYER_LOGIN_CLIENT)
	public void login(Packet packet, HSession session) throws Exception {
		log.info("[HTTPRequest] session:{}  packet:{}]", session.getSessionId(), packet.toString());
		String name = packet.getString("userName");
		String password = packet.getString("password");
		String type = packet.getString("loginType");
		String version = packet.getString("version");

		if (name == null || password == null || type == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_41, packet.getopcode());
			return;
		}

		VersionService versionService = Platform.getAppContext().get(VersionService.class);
		if (!versionService.allowAbleVision(version)) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_27, packet.getopcode());
			return;
		}

		AccountService accountService = Platform.getAppContext().get(AccountService.class);
		Account account = null;

		if (type.equals("mid")) {
			String mid = packet.getString("mid");
			if (mid == null || mid.length() == 0) {
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_27, packet.getopcode());
				return;
			} else {
				account = accountService.getAccountByMid(mid, 0);
			}
		}

		Player player = Platform.getEntityManager().fetch("from Player where accountId = ? and exist = 0", account.getId());

		if (player == null) {
			log.info("自动创建新角色");
			player = new Player();
			Date now = new Date();
			player.setCreateTime(now);
			player.setLastLoginTime(now);
			Platform.getEntityManager().createSync(player);
		}

		Data data = Platform.dataCenter().getData(DataKeys.playerKey(player.getInstanceId()));
		if (data == null) {
			Platform.dataCenter().sendNewData(DataKeys.playerKey(player.getInstanceId()), player);
		} else {
			player = (Player) data.value;
		}

		loginSucess(player, session, account);
		initPlayerTongji(player, packet);
		Packet pt = new JSONPacket(HOpCode.PLAYER_LOGIN_SERVER);
		pt.put("player", player.toClientData());
		session.send(pt);
		log.info("[login] return[null]");
	}

	public void initPlayerTongji(Player p, Packet packet) {
		String unknow = "UNKNOWN";
		if (packet.containsKey("area")) {
			p.setArea(packet.getString("area"));
		} else {
			p.setArea(unknow);
		}
		if (packet.containsKey("country")) {
			p.setCountry(packet.getString("country"));
		} else {
			p.setCountry(unknow);
		}
		if (packet.containsKey("device")) {
			p.setDevice(packet.getString("device"));
		} else {
			p.setDevice(unknow);
		}
		if (packet.containsKey("deviceSystem")) {
			p.setDeviceSystem(packet.getString("deviceSystem"));
		} else {
			p.setDeviceSystem(unknow);
		}
		if (packet.containsKey("downloadType")) {
			p.setDownloadType(packet.getString("downloadType"));
		} else {
			p.setDownloadType(unknow);
		}
		if (packet.containsKey("networkType")) {
			p.setNetworkType(packet.getString("networkType"));
		} else {
			p.setNetworkType(unknow);
		}
		if (packet.containsKey("prisonBreak")) {
			p.setPrisonBreak(packet.getString("prisonBreak"));
		} else {
			p.setPrisonBreak(unknow);
		}
		if (packet.containsKey("operator")) {
			p.setOperator(packet.getString("operator"));
		} else {
			p.setOperator(unknow);
		}
	}

	protected void loginSucess(Player player, HSession session, Account account) {
		player.setAccount(account);
		session.convert(player.getInstanceId());
		player.setSession(session);
		player.setLoginServerId(Platform.getServerId());
		ObjectAccessor.addGameObject(player);
		Platform.getEventManager().addEvent(new Event(GameEvent.EVENT_PLAYER_LOGINED, player));
		SessionManager.updatePlayerSession(player);
		Platform.getLog().logPlayerLogin(player);
		log.info("[login]loginSucess:playerId:{},accountId:{},ip:{},sessionId:{}",
				new Object[] { player.getId(), account.getId(), session.ip(), session.getSessionId() });
	}

	@OP(code = HOpCode.PLAYER_LOGOUT_CLIENT)
	public void logout(Packet packet, HSession session) throws Exception {
		log.info("[HTTPRequest] session:{}  packet:{}]", session.getSessionId(), packet.toString());

		Player p = (Player) session.client();
		if (p == null) {
			return;
		}
		Platform.getEventManager().addEvent(new Event(GameEvent.EVENT_PLAYER_LOGOUTED, p));
		log.info("logout playerId:{},accountId:{}", p.getId(), p.getAccountId());
	}

	// public void addMoney(Packet packet, HSession session) throws Exception {
	// Player p = (Player) session.client();
	// if (p == null) {
	// ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1,
	// packet.getopcode());
	// return;
	// }
	// p = new Player();
	//
	// int addMoney = packet.getInt("money");
	// p.setMoney(p.getMoney() + addMoney);
	//
	// Packet pt = new JSONPacket(1);
	// pt.put("money", p.getMoney());
	//
	// session.send(pt);
	// log.info("[METHODEND] return[null]");
	// }

	/**
	 * 第一步从DataCenter中读取, 没有读到测从数据库中读取
	 * 
	 * @param playerId
	 * @return player 或 null
	 */
	public Player loadPlayer(int playerId) {
		Data data = Platform.dataCenter().getData(DataKeys.playerKey(playerId));
		Player player = null;
		if (data == null) {
			player = Platform.getEntityManager().find(Player.class, playerId);
			if (player != null) {
				Platform.dataCenter().sendNewData(DataKeys.playerKey(playerId), player);
				log.info("[loadPlayer] return[player({})]", player.getId());
			}
			return player;
		} else {
			log.info("[loadPlayer] return[player({})]", ((Player) data.value).getId());
			return (Player) data.value;
		}

	}

	@OPEvent(eventCode = GameEvent.EVENT_PLAYER_LOGOUTED)
	protected void playerLogoutEvent(Event event) {
		final Player player = (Player) event.param1;
		player.setLoginServerId(-1);
		player.setLastLogoutTime(new Date());
		player.getSession().invalid();
		removePlayerFromCache(player);
		Platform.getThreadPool().execute(new AsyncCall() {
			@Override
			public void run() {
				savePlayer(player, true, true);
			}

			@Override
			public void callFinish() throws Exception {
			}
		});
	}

	@OPEvent(eventCode = GameEvent.EVENT_PLAYER_CHANGEED)
	protected void playerChangeEvent(Event event) {
		final Player player = (Player) event.param1;
		Platform.getThreadPool().execute(new AsyncCall() {
			@Override
			public void run() {
				savePlayer(player, false , false);
			}

			@Override
			public void callFinish() throws Exception {
			}
		});
	}

	@OPEvent(eventCode = GameEvent.EVENT_PLAYER_CHANGEED_FORCE)
	protected void playerChangeEventForce(Event event) {
		final Player player = (Player) event.param1;
		Platform.getThreadPool().execute(new AsyncCall() {
			@Override
			public void run() {
				savePlayer(player, true, false);
			}

			@Override
			public void callFinish() throws Exception {
			}
		});
	}

	/**
	 * 保存玩家的接口不一定会马上执行, 对于设置了强制保存为false的调用, 会根据玩家上次保存时间, 与当前时间的间隔来判断是否会保存.
	 * 
	 * @param player
	 * @param isForce
	 */
	public void savePlayer(Player player, boolean isForce,boolean islogout) {
		// 更新远程数据.
//		RunTimeMonitor rt = new RunTimeMonitor();
		String key = DataKeys.playerKey(player.getInstanceId());

		Data data = Platform.dataCenter().getData(key);
//		rt.knock("getData");
		boolean ret = false;
		if (data == null) {
			ret = Platform.dataCenter().sendNewData(key, player);
//			rt.knock("sendNewData");
		} else {
			data.value = player;
			ret = Platform.dataCenter().sendData(key, data);
//			rt.knock("sendData");
		}

		boolean needSave = false;
		if (ret && Platform.worldServer() != null) {
			if (isForce) {// 强制保存
				needSave = true;
			} else {// 其他时候按照上次保存时间保存
				if (Time.currTime / 1000 - player.getSaveTime() > DB_SAVE_SPLIT_SECOND) {
					needSave = true;
				}
			}
			if (needSave) {
				player.setSaveTime((int) (Time.currTime / 1000));
//				WorldManager wmanager = Platform.getAppContext().get(WorldManager.class);
//				wmanager.playerChanged(player);// 通知保存数据库.
//				rt.knock("wmanager.playerChanged");
//				// 清理本地缓存。
//				Platform.getEntityManager().clearFromCache(player);
//				rt.knock("clearFromCache.player");
//				if (player.getAccount() != null) {
//					Platform.getEntityManager().clearFromCache(player.getAccount());
//				}
				
//				rt.knock("updateSync.player");
				if (player.getAccount() != null) {
					Platform.getEntityManager().updateSync(player.getAccount());// 自己保存数据库
				}
				Platform.getEntityManager().updateSync(player);// 自己保存数据库
//				rt.knock("updateSync.Account");
			}
		} else {
			if (isForce) {// 强制保存
				needSave = true;
			} else {// 其他时候按照上次保存时间保存
				if (Time.currTime / 1000 - player.getSaveTime() > DB_SAVE_SPLIT_SECOND) {
					needSave = true;
				}
			}
			if (needSave) {
				player.setSaveTime((int) (Time.currTime / 1000));
				
//				rt.knock("updateSync.player");
				if (player.getAccount() != null) {
					Platform.getEntityManager().updateSync(player.getAccount());// 自己保存数据库
				}
				Platform.getEntityManager().updateSync(player);// 自己保存数据库
//				rt.knock("updateSync.Account");
			}
		}
//		log.info("event 106 :" + rt.toString(0));
	}

	private void removePlayerFromCache(Player player) {
		ObjectAccessor.removeGameObject(player);
		//log.info("=====================removePlayerFromCache end:playerId:{}", player.getId());
	}

	/**
	 * 绑定sns
	 */
	@OP(code = HOpCode.SNS_BINDING_CLIENT)
	public void bindingSNS(Packet packet, HSession session) throws Exception {
		log.info("[HTTPRequest] session:{}  packet:{}]", session.getSessionId(), packet.toString());
		Player player = (Player) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			return;
		}
		RunTimeMonitor rt = packet.getRunTimeMonitor();
		String type = packet.getString("type");
		String snsId = packet.getString("id");
		String token = packet.getString("token");
		if(type == null || type.length() == 0 ||snsId == null || snsId.length() == 0 ||token == null || token.length() == 0 ) {
			log.info("[sns]sns bind error.playerId:{} type:{} snsId:{}, token:{}", new Object[] { player.getInstanceId(), type, snsId, token });
			Packet pt = new JSONPacket(HOpCode.SNS_BINDING_SERVER);
			pt.put("result", 0);
			session.send(pt);
			return;
		}
		rt.knock("packet.getString");
		if (player.getAccount().getSnsIdByType(type) != null) {
			rt.knock("Account.getSnsIdByType");
			log.info("[sns]sns reset. player:{} snsId:{}, snsType:{}", new Object[] { player.getInstanceId(), snsId, type });
			player.getAccount().bindSNS(type, snsId);
			Platform.getLog().logSNSBind(player, type, snsId);
			rt.knock("Account.bindSNS");
			// Platform.getEntityManager().updateSync(player.getAccount());
		} else {
			log.info("[sns]sns bind. player:{} snsId:{}, snsType:{}", new Object[] { player.getInstanceId(), snsId, type });
			player.getAccount().bindSNS(type, snsId);
			Platform.getLog().logSNSBind(player, type, snsId);
			rt.knock("Account.bindSNS");
			// Platform.getEntityManager().updateSync(player.getAccount());
		}
		player.getPool().setString(Player.POOL_KEY_SNSTOKEY + type, token);
		player.notifySave();
		Packet pt = new JSONPacket(HOpCode.SNS_BINDING_SERVER);
		pt.put("result", 1);
		session.send(pt);
		log.info("bindingSNS playerId:{},type:{},snsId:{},token:{}", new Object[] { player.getId(), type, snsId, token });
		rt.knock("bindingSNS.ok");
	}

	/**
	 * 查看账号id
	 */

	@OP(code = HOpCode.PLAYER_ID_CLIENT)
	public void lookPlayerId(Packet packet, HSession session) {
		log.info("[lookPlayerId] ip:{} packet:{}", session.ip(), packet.toString());
		Player player = (Player) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			log.info("[lookPlayerId] error:player is null. ip:{} packet:{}", session.ip(), packet.toString());
			return;
		}
		Packet retPt = new JSONPacket(HOpCode.PLAYER_ID_SERVER);
		String enCodeId = null;
		if (Platform.getConfiguration().getBoolean("ID_ENCODE")) {
			enCodeId = IdUtil.enCode(player.getAccount().getId());
		} else {
			enCodeId = String.valueOf(player.getAccount().getId());
		}
		
		retPt.put("id", enCodeId);
		session.send(retPt);
		log.info("[lookPlayerId] ip:{}, playerId:{},enCodeId:{}", new Object[] { session.ip(), player.getId(), enCodeId });
	}

	/**
	 * 客户端上传日志记录
	 */
	@OP(code = HOpCode.LOG_CLIENT)
	public void clientLog(Packet packet, HSession session) {
		log.info("[clientLog] ip:{} packet:{}", session.ip(), packet.toString());
		Player player = (Player) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			log.info("[clientLog] error,player is null, ip:{} packet:{}", session.ip(), packet.toString());
			return;
		}
		if (packet.containsKey("rmbGoods")) {
			JSONArray goodsList = packet.getJSONArray("rmbGoods");
			Object[] goodsArray = goodsList.toArray();
			for (int i = 0; i < goodsArray.length; i++) {
				JSONObject goods = (JSONObject) goodsArray[i];
				if (goods.containsKey("amount") && goods.containsKey("usage")) {
					int amount = 1;
					String usage = "NULL";
					String name = "NULL";
					int commodityType = 1;//付费道具
					if(goods.containsKey("amount")){
						amount = goods.getInt("amount");
					}
					if(goods.containsKey("usage")){
						usage = goods.getString("usage");
					}
					if(goods.containsKey("name")){
						name = goods.getString("name");
					}
					if (usage.equals("")) {
						usage = "NULL";
					}
					if (name.equals("")) {
						name = "NULL";
					}
					if (goods.containsKey("time")) {
						long time = goods.getInt("time") * 1000L;
						Platform.getLog().commodity(time, player, name, usage, amount,commodityType);
					} else {
						Platform.getLog().commodity(player, name, usage, amount,commodityType);
					}
					Platform.getLog().diamond(player, amount, usage);
				}
			}
		}
		if (packet.containsKey("goods")) {
			JSONArray goodsList = packet.getJSONArray("goods");
			Object[] goodsArray = goodsList.toArray();
			for (int i = 0; i < goodsArray.length; i++) {
				JSONObject goods = (JSONObject) goodsArray[i];
				int amount = 1;
				String usage = "NULL";
				String name = "NULL";
				int commodityType = 2;//免费道具
				if(goods.containsKey("amount")){
					amount = goods.getInt("amount");
				}
				if(goods.containsKey("name")){
					name = goods.getString("name");
				}
				if (usage.equals("")) {
					usage = "NULL";
				}
				if (name.equals("")) {
					name = "NULL";
				}
				if (goods.containsKey("time")) {
					long time = goods.getInt("time") * 1000L;
					Platform.getLog().commodity(time, player, name, usage, amount,commodityType);
				} else {
					Platform.getLog().commodity(player, name, usage, amount,commodityType);
				}
			}
		}
		if (packet.containsKey("game")) {
			JSONArray toyGames = packet.getJSONArray("game");
			Object[] toyGameArray = toyGames.toArray();
			for (int i = 0; i < toyGameArray.length; i++) {
				JSONObject toyGame = (JSONObject) toyGameArray[i];
				String name = toyGame.getString("name");
				if (name.equals("")) {
					name = "NULL";
				}
				if (toyGame.containsKey("time")) {
					long time = toyGame.getInt("time") * 1000L;
					Platform.getLog().applet(time, player, name);
				} else {
					Platform.getLog().applet(player, name);
				}
			}
		}
		Packet retPt = new JSONPacket(HOpCode.LOG_SERVER);
		retPt.put("result", "ok");
		session.send(retPt);
		log.info("clientLog:playerId:{}", player.getId());
	}

	/**
	 * 刷新人民币道具数 client-->server actId:xxx int 账号id eg.{"opcode":103,
	 * "data"{"actId":113}}
	 */
	@OP(code = HOpCode.REFRESH_IMONEY_CLIENT)
	public void refreshImoney(Packet packet, HSession session) {
		log.info("[refreshImoney] ip:{} packet:{}", session.ip(), packet.toString());
		Player player = (Player) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			log.info("[refreshImoney] error:player is null,ip:{} packet:{}", session.ip(), packet.toString());
			return;
		}
		int accountId = packet.getInt("actId");
		if (player.getAccount() == null || player.getAccountId() != accountId) {
			log.info("[PlayerService] refreshImoney attack: player({}) accountId({}), playerAccount({})",
					new Object[] { player.getInstanceId(), accountId, player.getAccountId() });
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_25, packet.getopcode());
			return;
		}
		Packet retPt = new JSONPacket(HOpCode.REFRESH_IMONEY_SERVER);
		int imoney = player.getAccount().getImoney();
		retPt.put("imoney", imoney);
		session.send(retPt);
		log.info("refreshImoney:palyerId:{},accountId:{},imoney:{}", new Object[] { player.getId(), accountId, imoney });

	}

	/**
	 * 客户端心跳包 (每5分钟发一次) client-->server eg.{"opcode":105,"data":{}}
	 */
	@OP(code = HOpCode.HEART_CLIENT)
	public void clientHeart(Packet packet, HSession session) {
		// log.info("[clientHeart] ip:{} packet:{}", session.ip(),
		// packet.toString());
		Player player = (Player) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			log.info("clientHeart error:player is null,ip:{}", session.ip());
			return;
		}
		int notify = 0;
		if(player.getPool().getInt(PLAYER_NOTIFY_CLIENT, NOT_NOTIFY_CLIENT) == NOTIFY_CLIENT){
			notify = 1;
			player.getPool().setInt(PLAYER_NOTIFY_CLIENT, NOT_NOTIFY_CLIENT);
			player.notifySave();
		}
		Packet retPt = new JSONPacket(HOpCode.HEART_SERVER);
		retPt.put("notify", notify);
		retPt.put("time", (int)(System.currentTimeMillis()/1000L));
		session.send(retPt);
		log.info("[clientHeart] playerId:{},ip:{}", player.getId(), session.ip());
	}

	/**
	 * 同步元宝 //verify:
	 * {"BuyDollar":20,"CompensateDollar":0,"LocalTime":1333251822
	 * ,"RemainDollar":16,"RewardDollar":6,"UseDollar":10} // 购买20 补偿0 时间 剩余16
	 * 游戏内获得6 花费10
	 */
	protected ResetImoneyRet verifyDollar(JSONObject jsonInfo, Player player) {
		int buyDollar = 0;
		int compensateDollar = 0;
		int remainDollar = 0;
		int rewardDollar = 0;
		int usedDollar = 0;
		int initDollar = 0;

		if (jsonInfo.containsKey("BuyDollar")) {
			buyDollar = jsonInfo.getInt("BuyDollar");
		}
		if (jsonInfo.containsKey("CompensateDollar")) {
			compensateDollar = jsonInfo.getInt("CompensateDollar");
		}
		if (jsonInfo.containsKey("RemainDollar")) {
			remainDollar = jsonInfo.getInt("RemainDollar");
		}
		if (jsonInfo.containsKey("RewardDollar")) {
			rewardDollar = jsonInfo.getInt("RewardDollar");
		}
		if (jsonInfo.containsKey("UseDollar")) {
			usedDollar = jsonInfo.getInt("UseDollar");
		}
		if (jsonInfo.containsKey("InitDollar")) {
			initDollar = jsonInfo.getInt("InitDollar");
		}

		ResetImoneyRet ret = player.getAccount().verifyImoney(player.getId(), buyDollar, compensateDollar, remainDollar, rewardDollar,
				usedDollar, initDollar);
		if (!ret.result) {
			log.info("verifyDollar():failed playerId:{}, accountId:{}, errorCode:{}",
					new Object[] { player.getInstanceId(), player.getAccountId(), ret.errorCode });
		}
		return ret;
	}

	/**
	 * client-->server eg.{"opcode":108,"data":{language:"1"}}
	 */
	@OP(code = HOpCode.INTERNATIONAL_CLIENT)
	public void languageInternation(Packet packet, HSession session) {
		log.info("[languageInternation] ip:{} packet:{}", session.ip(), packet.toString());
		Player player = (Player) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			return;
		}
		int language = packet.getInt("language");
		player.setLanguage(language);
		log.info("[POOL_KEY_I18N_LANGUAGE] playerId:{} language:{}", player.getId(), language);
		Packet pt = new JSONPacket(HOpCode.INTERNATIONAL_SERVER);
		pt.put("language", language);
		session.send(pt);
		log.info("[languageInter()] return[null]");
	}

	/**
	 * 请求是否显示广告 client-->server eg.{"opcode":111,"data":{}}
	 */
	@OP(code = HOpCode.PLAYER_NOAD_TIME_CLIENT)
	public void requestShowAD(Packet packet, HSession session) {
		log.info("[requestShowAD] ip:{} packet:{}", session.ip(), packet.toString());
		Player player = (Player) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			log.info("[requestShowAD] error:player is null, ip:{}", session.ip());
			return;
		}
		int showAD = 1;
		if (player.getLevel() < 5) {
			showAD = 0;
		} else {
			if (player.getPool() != null && player.getPool().getInt(ChargeClientService.PROPERTY_NOADTIME, 0) > 0) {
				if (System.currentTimeMillis() / 1000 < player.getPool().getInt(ChargeClientService.PROPERTY_NOADTIME, 0)) {
					showAD = 0;
				}
			}
		}
		Packet pt = new JSONPacket(HOpCode.PLAYER_NOAD_TIME_SERVER);
		pt.put("showAD", showAD);
		session.send(pt);
		log.info("requestShowAD end:playerId:{},showAD:{}", player.getId(), showAD == 1 ? "YES" : "NO");
	}

	/**
	 * 客户端请求恢复数据 client-->server opcode:7 data: id:xxx int playerId; acId:xxx
	 * int acId; eg.{"opcode":7,"data":{"id":2,"acId":2}}
	 */
	@OP(code = HOpCode.PLAYER_RECOVER_CLIENT)
	public void requestRecoverData(Packet packet, HSession session) {
		log.info("[requestRecoverData]: ip :{}] packet:{}", session.ip(), packet.toString());
		Player player = (Player) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			log.info("requestRecoverData() : error[player is null]");
			return;
		}
		int playerId = packet.getInt("id");
		int accountId = packet.getInt("acId");
		log.info("[RecoverData] request recoverData,playerId:{},accountId:{}", playerId, accountId);
		if (player.getId() != playerId || player.getAccountId() != accountId) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_15, packet.getopcode());
			log.info("requestRecoverData() : error[id error] playerId:{},upPlayerId:{},accountId:{},upAccountId:{}",
					new Object[] { player.getId(), playerId, player.getAccountId(), accountId });
			return;
		}
		Packet pt = new JSONPacket(HOpCode.PLAYER_RECOVER_SERVER);
		if (player.getLastSynchInfoTime() == null) {
			pt.put("time", "0");
		} else {
			pt.put("time", Long.toString(player.getLastSynchInfoTime().getTime()));
		}
		pt.put("player", player.toClientData());
		session.send(pt);
	}

	protected String getADByPlat(String device) {
		device = device.toLowerCase();
		if (device.indexOf("ipad") != -1) {
			return Platform.getConfiguration().getString("ad_ipad_num");
		} else if (device.indexOf("iphone") != -1) {
			return Platform.getConfiguration().getString("ad_iphone_num");
		} else {
			log.info("getADByPlat:info[device:{}]",device);
			return Platform.getConfiguration().getString("ad_iphone_num");
		}
	}

	public void notifyReceiveNewMail(int playerId) {
		if (ObjectAccessor.players.get(playerId) != null) {
			Player p = ObjectAccessor.getPlayer(playerId);
			synchronized (p) {
				if (p.getPool().getInt(PLAYER_NOTIFY_CLIENT, NOT_NOTIFY_CLIENT) == NOT_NOTIFY_CLIENT) {
					p.getPool().setInt(PLAYER_NOTIFY_CLIENT, NOTIFY_CLIENT);
				}
			}
		}
	}
}
