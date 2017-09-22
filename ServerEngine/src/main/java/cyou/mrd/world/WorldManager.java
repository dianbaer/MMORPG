package cyou.mrd.world;


import java.text.MessageFormat;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.Platform;
import cyou.mrd.charge.Charge;
import cyou.mrd.charge.ChargeClientService;
import cyou.mrd.event.Event;
import cyou.mrd.io.AsyncCall;
import cyou.mrd.io.OP;
import cyou.mrd.io.OPHandler;
import cyou.mrd.io.http.HOpCode;
import cyou.mrd.io.http.HeartServlet;
import cyou.mrd.io.http.JSONPacket;
import cyou.mrd.io.http.SessionManager;
import cyou.mrd.io.tcp.ClientSession;
import cyou.mrd.io.tcp.IoEvent;
import cyou.mrd.io.tcp.OpCode;
import cyou.mrd.io.tcp.TcpPacket;
import cyou.mrd.io.tcp.connector.single.SingleConnector;
import cyou.mrd.io.tcp.connector.single.SingleConnectorManager;
import cyou.mrd.service.Service;
import cyou.mrd.updater.Updatable;

@OPHandler(TYPE = OPHandler.TCP)
public class WorldManager implements Updatable, Service, SingleConnectorManager {

	private static final Logger log = LoggerFactory.getLogger(WorldManager.class);

	

	protected SingleConnector singleConnector = new SingleConnector();
	public IoSession ioSession;
	@Override
	public String getId() {
		return "WorldManager";
	}

	@Override
	public void startup() throws Exception {
		Platform.getUpdater().addSyncUpdatable(this);
		
		
		
		String[] urlInfo = Platform.getConfiguration().getString("world.address").split("-");
		String[] portInfo = Platform.getConfiguration().getString("world.port").split("-");
		
		singleConnector.initConnector(urlInfo[0], Integer.parseInt(portInfo[0]), this, false);
		

		log.info("[WorldManager] initConnector");
	}

	@Override
	public void shutdown() throws Exception {
		log.info("[WorldManager] shutdown..");
		//singleConnector.logout();
		log.info("[World] logout.");
	}

	@Override
	public boolean update() {
//		if (!singleConnector.isLogin()) {
//			Time.update(System.currentTimeMillis());
//		}
		return true;
	}

	/**
	 * 
	 * @param session
	 */
	@Override
	public void login(IoSession session) {
		TcpPacket pt = new TcpPacket(OpCode.WORLD_LOGIN_C);
		pt.putInt(Platform.getConfiguration().getInt("server_id"));
		session.write(pt);
	}
	public void PlayerLogin(IoSession session,RequestWorld requestWorld){
		TcpPacket pt = new TcpPacket(OpCode.WORLD_PLAYER_LOGIN_C);
		pt.putInt(requestWorld.serverId);
		pt.putInt(requestWorld.session.getId());
		session.write(pt);
	}
	public void PlayerLogOut(IoSession session,int serverId){
		TcpPacket pt = new TcpPacket(OpCode.WORLD_PLAYER_LOGOUT_C);
		pt.putInt(serverId);
		session.write(pt);
	}
	/**
	 * @param player
	 */
//	public void playerChanged(Player player) {
//		TcpPacket pt = new TcpPacket(OpCode.PLAYER_CHANGED_CLIENT);
//		pt.putInt(player.getInstanceId());
//		Platform.worldServer().send(pt);
//		log.info("playerChanged(): notify world player changed. playerId[{}]",player.getId());
//	}
	
	@Override
	@OP(code = OpCode.WORLD_LOGIN_S)
	public void loginSucess(TcpPacket packet, ClientSession session) {
		//log.info("[TCPRequest] packet:{} session :{}]", packet.toString(), session.getId());
		//SingleClient server = new SingleClient();
		//session.setClient(server);
		//Platform.setWorldServer(server);
		ioSession = session.getIoSession();
		//singleConnector.loginSucess();

		//log.info("+++++++++++++++++++++[GameServer Login World Sucess!]+++++++++++++++++++++++");
	}
	
	@OP(code = OpCode.WORLD_AGAIN_CONNECT_S)
	public void againConnect(TcpPacket packet, ClientSession session) {
		TcpPacket pt = new TcpPacket(OpCode.WORLD_LOGIN_C);
		pt.putInt(Platform.getConfiguration().getInt("server_id"));
		session.send(pt);
	}
	
	@OP(code = OpCode.WORLD_KICK_PLAYER_S)
	public void kickPlayer(TcpPacket packet, ClientSession session) {
		int serverId = packet.getInt();
		int sessionId = packet.getInt();
		Platform.getEventManager().addEvent(new Event(IoEvent.EVENT_KICK_PLAYER, serverId, sessionId));
	}
	
	@OP(code = OpCode.WORLD_PLAYER_CAN_LOGIN_S)
	public void playerCanLogin(TcpPacket packet, ClientSession session) {
		int serverId = packet.getInt();
		int sessionId = packet.getInt();
		Platform.getEventManager().addEvent(new Event(IoEvent.EVENT_PLAYER_CAN_LOGIN, serverId, sessionId));
	}
	
	@OP(code = OpCode.WORLD_PLAYER_RELOGIN_S)
	public void playerReLogin(TcpPacket packet, ClientSession session) {
		int serverId = packet.getInt();
		int sessionId = packet.getInt();
		Platform.getEventManager().addEvent(new Event(IoEvent.EVENT_PLAYER_RE_LOGIN, serverId, sessionId));
	}
//	private long lastLogSyncTime = 0;
	@OP(code = OpCode.WORLD_SYNC_TIME_SERVER)
	public void syncTime(TcpPacket packet, ClientSession session) {
//		log.info("[TCPRequest] packet:{} session :{}]", packet.toString(), session.getId());
//		long time = packet.getLong();
//		Time.update(time);
//		TcpPacket pt = new TcpPacket(OpCode.WORLD_SYNC_TIME_CLIENT);
//		pt.put(1);
//		session.send(pt);
//		if (time - lastLogSyncTime > 5000) {
//			lastLogSyncTime = time;
			//log.info("[world heart!]");
//		}
//		log.info("[server] online user :{}", ObjectAccessor.size());
	}

	@OP(code = OpCode.SESSION_CHANGED_SERVER)
	public void syncSessionTable(TcpPacket packet, ClientSession session) {
//		log.info("[TCPRequest] packet:{} session :{}]", packet.toString(), session.getId());
		final SessionManager manager = Platform.getAppContext().get(SessionManager.class);
		Platform.getThreadPool().execute(new AsyncCall() {
			@Override
			public void run() {
				manager.syncSessionTable();
			}

			@Override
			public void callFinish() throws Exception {
			}
		});
		// manager.syncSessionTable();

		// log.info("[SessionTable] sync:{}", ret); 
	}

	/**
	 * world 服务器验证返回 server--->client result:xxx int 结果（-1：失败 1：成功） diamond:xxx
	 * int 充值的数量
	 * 
	 * 
	 */
	@OP(code = OpCode.WORLD_VERIFY_SERVER)
	public void billingResponseVerify(TcpPacket packet, ClientSession session) {
		log.info("[TCPRequest] packet:{} session :{}]", packet.toString(), session.getId());
		int result = packet.getInt();
		int rmb = packet.getInt();
		int accountId = packet.getInt();
		int playerId = packet.getInt();
		long serialNum = packet.getLong();
		int noADTime = packet.getInt();
		int type = packet.getInt();
		int extraImoneyRatio = packet.getInt();
		Charge charge = Platform.getAppContext().get(ChargeClientService.class).getChargeBySerialNum(serialNum);
		if(charge == null){
			Platform.getLog().logCharge(MessageFormat.format("[CHARGE]STEP[12]ERROR[{0}]ACCOUNTID[{1}]PLAYERID[{2}]SERIALNUM[{3}]","charge is null",accountId,playerId,serialNum));
			return;
		}
		charge.setServerReceiveWorld(1);
		charge.setType(type);
		charge.setExtraImoneyRatio(extraImoneyRatio);
		Platform.getLog().logCharge(MessageFormat.format( "[CHARGE]STEP[12]RESULT[{0}]ACCOUNTID[{1}]PLAYERID[{2}]SERIALNUM[{3}]",result,accountId,playerId,serialNum));
		JSONPacket retP = new JSONPacket(HOpCode.BILLING_VERIFY_SERVER);
		ChargeClientService ccs = Platform.getAppContext().get(ChargeClientService.class);
		retP.put("result", result);//注意： 这里的result是没有加成过的值，客户端需要通过extraRatio计算并四舍五入得出最终值
		retP.put("accountId", accountId);
		retP.put("noADTime", noADTime);
		retP.put("rmb", rmb);
		retP.put("type", type);
		retP.put("extraImoneyRatio", extraImoneyRatio);
		ccs.callbackClient(serialNum , retP);
		log.info("billingResponseVerify:end");
		log.info("[charge] billing return. result:{}", retP.toString());
	}
	

	@OP(code = OpCode.SERVER_REQUEST_WORLD_SERVER)
	public void worldResponseConnectState(TcpPacket packet, ClientSession session){
		int billingState = packet.getInt();
		String retString = packet.getString();
		if(billingState == 0){
			HeartServlet.setState(3,retString);
		}else if(billingState == 1){
			HeartServlet.setState(1,retString);
		}
	}
	
	
}
