package cyou.akworld;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.Platform;
import cyou.mrd.event.Event;
import cyou.mrd.event.EventListener;
import cyou.mrd.io.OP;
import cyou.mrd.io.OPHandler;
import cyou.mrd.io.tcp.ClientSession;
import cyou.mrd.io.tcp.IoEvent;
import cyou.mrd.io.tcp.OpCode;
import cyou.mrd.io.tcp.TcpPacket;
import cyou.mrd.service.Service;
import cyou.mrd.updater.Updatable;


@OPHandler(TYPE = OPHandler.TCP)
public class GameServerService implements Service, EventListener ,Updatable{

	private static Logger log = LoggerFactory.getLogger(GameServerService.class);
	public Map<Integer,WebServer> clients = new HashMap<Integer,WebServer>();
	public Map<Integer,WebServer> removeClients = new HashMap<Integer,WebServer>();
	public static JSONObject skillData;
	public static JSONObject monsterData;
	public static JSONObject buffData;
	public static JSONObject sceneData;
	public static JSONObject campData;
	
	
	public static HashMap<Integer, String> serverUrlMap = new HashMap<Integer, String>();
	public static HashMap<Integer, Integer> serverPortMap = new HashMap<Integer, Integer>();
	
	public HashMap<Integer,PlayerLogin> playerLoginMap = new HashMap<Integer,PlayerLogin>();
	@Override
	public String getId() {
		return "ClientService";
	}

	@Override
	public void startup() throws Exception {
		Platform.getUpdater().addSyncUpdatable(this);
		
		File file = new File(Platform.getConfiguration().getString("mapdir")+"skill.json");
		InputStream stream = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(stream, "utf-8"));
		String line = "";
		while (br.ready()) {
			line += br.readLine();
		}
		skillData = JSONObject.fromObject(line);
		
		file = new File(Platform.getConfiguration().getString("mapdir")+"monster.json");
		stream = new FileInputStream(file);
		br = new BufferedReader(new InputStreamReader(stream, "utf-8"));
		line = "";
		while (br.ready()) {
			line += br.readLine();
		}
		monsterData = JSONObject.fromObject(line);
		
		file = new File(Platform.getConfiguration().getString("mapdir")+"buff.json");
		stream = new FileInputStream(file);
		br = new BufferedReader(new InputStreamReader(stream, "utf-8"));
		line = "";
		while (br.ready()) {
			line += br.readLine();
		}
		buffData = JSONObject.fromObject(line);
		
		file = new File(Platform.getConfiguration().getString("mapdir")+"scene.json");
		stream = new FileInputStream(file);
		br = new BufferedReader(new InputStreamReader(stream, "utf-8"));
		line = "";
		while (br.ready()) {
			line += br.readLine();
		}
		sceneData = JSONObject.fromObject(line);
		
		file = new File(Platform.getConfiguration().getString("mapdir")+"camp.json");
		stream = new FileInputStream(file);
		br = new BufferedReader(new InputStreamReader(stream, "utf-8"));
		line = "";
		while (br.ready()) {
			line += br.readLine();
		}
		campData = JSONObject.fromObject(line);
		
		//服务器对应的ip和端口
		serverUrlMap.put(Platform.getConfiguration().getInt("server_one_id"),Platform.getConfiguration().getString("server_one_url"));
		serverUrlMap.put(Platform.getConfiguration().getInt("server_two_id"),Platform.getConfiguration().getString("server_two_url"));
		serverPortMap.put(Platform.getConfiguration().getInt("server_one_id"),Platform.getConfiguration().getInt("server_one_port"));
		serverPortMap.put(Platform.getConfiguration().getInt("server_two_id"),Platform.getConfiguration().getInt("server_two_port"));
	}

	@Override
	public void shutdown() throws Exception {
	}
	
	@OP(code = OpCode.WORLD_LOGIN_C)
	public void login(TcpPacket packet, final ClientSession session) {
		
		int serverId = packet.getInt();
		

		Object[] keys = clients.keySet().toArray();
		for(int i = 0; i < keys.length ; i ++){
			WebServer webServer1 = clients.get(keys[i]);
			//如果不是一个连接id并且是一个用户id
			if(webServer1.getSession().getId() != session.getId() && webServer1.getServerId() == serverId){
				//直接踢 上一个连接的
				if(webServer1.getSession().getIoSession() != null){
					webServer1.getSession().getIoSession().close(true);
				}else{
					log.info("webServer1.getSession().getIoSession(),确实有为空的情况");
				}
				//不关闭连接了，告诉客户端再次连接服务器试试
				TcpPacket pt1 = new TcpPacket(OpCode.WORLD_AGAIN_CONNECT_S);
				session.send(pt1);
				//session.getIoSession().close();
				return;
				
			}
			//是一个连接id并且多次发登录，不作处理
			if(webServer1.getSession().getId() == session.getId() && webServer1.getServerId() == serverId){
				return;
			}
			if(webServer1.getSession().getId() == session.getId() && webServer1.getServerId() != serverId){
				//同一个连接不能登录多个账户
				return;
			}
		}
		
		
		WebServer webServer = new WebServer();
		webServer.setServerId(serverId);
		session.setClient(webServer);
		
		clients.put(webServer.getServerId(), webServer);
		
		//没什么用
		
		webServer.session.authenticate(null);
		//登录成功
		TcpPacket pt1 = new TcpPacket(OpCode.WORLD_LOGIN_S);
		webServer.getSession().send(pt1);
	}
	//用户登录
	@OP(code = OpCode.WORLD_PLAYER_LOGIN_C)
	public void playerLoginHanndle(TcpPacket packet, ClientSession session) {
		int playerId = packet.getInt();
		int sessionId = packet.getInt();
		WebServer webServer = (WebServer)session.getClient();
		//判断
		PlayerLogin playerLogin = playerLoginMap.get(playerId);
		if(playerLogin != null){
			
			//这次请求在别的服务器上，原因一般是切换场景
			if(playerLogin.serverId != webServer.getServerId()){
				//告诉那台服务器踢出这个用户下线
				WebServer kickwebServer = clients.get(playerLogin.serverId);
				//通知下线
				TcpPacket pt1 = new TcpPacket(OpCode.WORLD_KICK_PLAYER_S);
				pt1.putInt(playerLogin.playerId);
				pt1.putInt(playerLogin.sessionId);
				kickwebServer.getSession().send(pt1);
			}else{
				//是同一个sessionid，这个是不可能的，sessionid一样的话，会驳回登录请求
				if(playerLogin.sessionId == sessionId){
					
				//不一样也是不可能的，同一个server会告诉玩家等会再登录，不会发这个消息
				}else{
					
				}
			}
			//告诉这个客户端等待一下在登录
			TcpPacket pt1 = new TcpPacket(OpCode.WORLD_PLAYER_RELOGIN_S);
			pt1.putInt(playerId);
			pt1.putInt(sessionId);
			webServer.getSession().send(pt1);
		}else{
			//可以登录
			PlayerLogin playerLoginNew = new PlayerLogin();
			playerLoginNew.playerId = playerId;
			playerLoginNew.sessionId = sessionId;
			playerLoginNew.serverId = webServer.getServerId();
			playerLoginMap.put(playerId,playerLoginNew);
			//通知玩家可以登录了
			TcpPacket pt1 = new TcpPacket(OpCode.WORLD_PLAYER_CAN_LOGIN_S);
			pt1.putInt(playerLoginNew.playerId);
			pt1.putInt(playerLoginNew.sessionId);
			webServer.getSession().send(pt1);
		}
		
	}
	@OP(code = OpCode.WORLD_PLAYER_LOGOUT_C)
	public void playerLogOutHanndle(TcpPacket packet, ClientSession session) {
		int playerId = packet.getInt();
		//WebServer webServer = (WebServer)session.getClient();
		playerLoginMap.remove(playerId);
	}
	@Override
	public int[] getEventTypes() {
		return new int[] { IoEvent.EVENT_SESSION_REMOVEING };
	}
	public boolean update() {
		//遍历移除列表，确实场景移除了之后，再删除这个列表的玩家
		Object[] keyArray = removeClients.keySet().toArray();
		for(int i = 0;i<keyArray.length;i++){
			int key = Integer.parseInt(String.valueOf(keyArray[i]));
			WebServer webServer = removeClients.get(key);
			if(webServer.getState() == 2){
				Platform.getEventManager().addEvent(new Event(IoEvent.EVENT_SESSION_REMOVED, webServer.getSession()));
				continue;
			}else if(webServer.getState() == 3){
				removeClients.remove(key);
				clients.remove(key);
				webServer.clear();
				continue;
			}
		}
		return true;
	}
	@Override
	public void handleEvent(Event event) {
		switch (event.type) {
		case IoEvent.EVENT_SESSION_REMOVEING:
			ClientSession session = (ClientSession) event.param1;
			if (session != null) {
				if(session.getClient() instanceof WebServer){
					WebServer server = (WebServer) session.getClient();
					if (server != null) {
						removeClients.put(server.getServerId(), server);
						server.setState(2);
						log.info("[WebServer] clients remove:{}", server.getServerId());
					} else {
						log.info("[WebServer] clients remove:{}", session.getClientIP());
					}
				}
			}
			break;

		default:
			break;
		}

	}

}
