package cyou.akworld;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tcp.SceneData;
import tcp.ThreadService;
import _45degrees.com.friendsofed.isometric.IsoUtils;
import _45degrees.com.friendsofed.isometric.Point3D;
import _astar.Node;
import cyou.mrd.Platform;
import cyou.mrd.event.Event;
import cyou.mrd.event.EventListener;
import cyou.mrd.io.OP;
import cyou.mrd.io.OPHandler;
import cyou.mrd.io.tcp.ClientSession;
import cyou.mrd.io.tcp.IoEvent;
import cyou.mrd.io.tcp.TcpPacket;
import cyou.mrd.service.Service;
import cyou.mrd.updater.Updatable;
import cyou.mrd.world.RequestWorld;
import cyou.mrd.world.WorldManager;


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
	
	public ArrayList<RequestWorld> requestWorldList = new ArrayList<RequestWorld>();
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
	
	@OP(code = cyou.akworld.OpCodeEx.LOGIN_C)
	public void login(TcpPacket packet, final ClientSession session) {
		
		int serverId = packet.getInt();
		int monsterId = packet.getInt();
		int campId = packet.getInt();
		int sceneId = packet.getInt();

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
				TcpPacket pt1 = new TcpPacket(cyou.akworld.OpCodeEx.AGAIN_CONNECT_SERVER_S);
				session.send(pt1);
				//session.getIoSession().close();
				return;
				
			}
			//是一个连接id并且多次发登录，不作处理
			if(webServer1.getSession().getId() == session.getId() && webServer1.getServerId() == serverId){
				//这里可以发个消息包，告诉用户不用重复登录
				return;
			}
			if(webServer1.getSession().getId() == session.getId() && webServer1.getServerId() != serverId){
				//同一个连接不能登录多个账户
				return;
			}
		}
		JSONObject camp = campData.getJSONObject(campId+"");
		//如果没有这个阵营，关闭连接
		if(camp == null){
			session.getIoSession().close(true);
			return;
		}
		//1.同一个session不可能发两次登陆，
		//2.同一个session在同一台服务器如果已经登录，不会接受登录
		
		
		
		//判断等待world反馈队列里，是否已有登录请求,有的话，这回登录直接驳回
		for(int j = 0; j<requestWorldList.size();j++){
			RequestWorld requestWorld = requestWorldList.get(j);
			//说明这个socket连接已经有登录请求了，这次驳回
			if(requestWorld.session.getId() == session.getId()){
				//这里可以发个消息包，告诉用户不用重复登录
				return;
			}
		}
		//放入等待队列
		RequestWorld requestWorld = new RequestWorld();
		requestWorld.serverId = serverId;
		requestWorld.monsterId = monsterId;
		requestWorld.campId = campId;
		requestWorld.sceneId = sceneId;
		requestWorld.session = session;
		requestWorldList.add(requestWorld);
		//向world发送登录请求
		WorldManager wmanager = Platform.getAppContext().get(WorldManager.class);
		wmanager.PlayerLogin(wmanager.ioSession,requestWorld);
		
		
	}
	@OP(code = cyou.akworld.OpCodeEx.CHANGE_SCENE_C)
	public void changeScene(TcpPacket packet, ClientSession session) {
		int sceneId = packet.getInt();
		WebServer webServer = (WebServer)session.getClient();
		ThreadService threadService = Platform.getAppContext().get(ThreadService.class);
		threadService.removeUser(webServer, sceneId);
		
		
	}
	@OP(code = cyou.akworld.OpCodeEx.LOADING_OK_C)
	public void loadingOk(TcpPacket packet, ClientSession session) {
		WebServer webServer = (WebServer)session.getClient();
		webServer.isLoadOk = true;
	}
	@OP(code = cyou.akworld.OpCodeEx.RECEIVE_CHANGE_SERVER_C)
	public void receiveChangeServer(TcpPacket packet, ClientSession session) {
		WebServer webServer = (WebServer)session.getClient();
		//大于1，就说明这个用户发过来的包是断开后处理的,不叫他换服务器了，直接下线
		if(webServer.getState() >= 1){
			return;
		}
		//客户端收到换服务器的消息了
		webServer.isReceiveChangeServer = true;
		
		//把最新的数据存入memcached里，用户标记为可以换服务器
		
		
		//在这里移除有着不确定性，在变成1的中间地段移除的话，会出问题，造成无法删除用户
		//ThreadService.changeServerUserList.remove(webServer);
		
		//断开用户链接
		webServer.getSession().getIoSession().close(true);
		
	}
	//取消buff
	@OP(code = cyou.akworld.OpCodeEx.CANCEL_BUFF_C)
	public void cancelBuff(TcpPacket packet, ClientSession session) {
		WebServer webServer = (WebServer)session.getClient();
		int buffId = packet.getInt();
		webServer.cancelMyBuffById(buffId);
	}
	@OP(code = cyou.akworld.OpCodeEx.MOVE_C)
	public void move(TcpPacket packet, ClientSession session) {
//		log.info("[TCPRequest] packet:{} session :{}", packet.toString(), session.getId());
		double x = packet.getDouble();
		double y = packet.getDouble();
		double z = packet.getDouble();
		int dir = packet.getByte();
		int len = packet.getInt();
		ArrayList<Node> arr = new ArrayList<Node>(len);
		for(int i = 0;i<len;i++){
			Node node = new Node();
			node.x = packet.getInt();
			node.y = packet.getInt();
			arr.add(node);
		}
		WebServer webServer = (WebServer)session.getClient();
		if(webServer.isDead || webServer.isDizzy() || webServer.isIceBox() || webServer.isCast() || webServer.isCanNotMove()){
			//这里应该写，重置玩家所在坐标点
			return;
		}
		if(webServer.isUserSkill){
			//如果是服务器的距离性行为，
			//1.被距离性行为影响前走路，服务器不能记录这些走路
			//2.被距离性行为影响后走路，不会出现这个情况，因为在走路时，这个技能在服务器肯定释放完成了。
			if(webServer.skillData.getInt("type") == SkillData.CLICK_THE_FLY || webServer.skillData.getInt("type") == SkillData.RUSH || webServer.skillData.getInt("type") == SkillData.OMNISLASH){
				log.info("已经被技能影响了，走路无效");
				return;
			}else{
				log.info("正在施放技能中，不能广播出去");
				webServer.savePath = arr;
				webServer.saveDir = dir;
				webServer.savePoint = new Point3D(x, y, z);
				return;
			}
		}
		webServer.dir = dir;
		webServer.position = new Point3D(x, y, z);
		
		move1(webServer,arr);
		
		
		
		
//		Point3D point3d = new Point3D(x, y, z);
//		Point2D.Double point2d = IsoUtils.isoToScreen(point3d);
//		double distance = Point.Double.distance(point2d.x, point2d.y, arr.get(1).x, arr.get(1).y);
//		log.info("[x:{},z:{}]", x,z);
//		log.info("[x:{},y:{}]", point2d.x,point2d.y);
//		log.info("[distance:{}]", distance);
		
		
		
	}
	public static void move1(ActivityThing webServer,ArrayList<Node> arr){
		webServer.setPath(arr);
		//用来移动monster,webserver update
		webServer.time = System.currentTimeMillis();
		
		//SceneData sceneData = webServer.sceneData;
		ArrayList<WebServer> userList;
		//if(sceneData.type == 2){
		//	userList = sceneData.userList;
		//}else{
			userList = webServer.horizonPlayer;
		//}
		
		//走路包
		TcpPacket pt1 = new TcpPacket(cyou.akworld.OpCodeEx.THING_MOVE_S);
		pt1.putInt(webServer.getServerId());
		pt1.putDouble(webServer.position.x);
		pt1.putDouble(webServer.position.y);
		pt1.putDouble(webServer.position.z);
		pt1.put(webServer.dir);
		int pathSize = webServer._path.size();
		pt1.putInt(pathSize);
		for(int j = 0;j<pathSize;j++){
			Node node = webServer._path.get(j);
			pt1.putInt(node.x);
			pt1.putInt(node.y);
		}
		int userListSize = userList.size();
		for(int i = 0; i < userListSize; i++){
			WebServer webServer1 = userList.get(i);
			//视野里的人肯定没有自己(这里不用做场景判断，因为这个都是基于action和消息包的，可以保证视野里的人肯定当时还在这个场景)
			//if(webServer.getServerId() != webServer1.getServerId()){
				
				webServer1.getSession().send(pt1);
			//}
		}
	}
	
	@OP(code = cyou.akworld.OpCodeEx.ATTACK_C)
	public void attack(TcpPacket packet, ClientSession session) {
		
		
		int skillId = packet.getInt();
		int type = packet.getInt();
		Point3D toPosition = null;
		int targetId = 0;
		if(type == 2){
			toPosition = new Point3D(packet.getDouble(),packet.getDouble(),packet.getDouble());
		}else{
			targetId = packet.getInt();
		}
		
		int dir = packet.getByte();
		
		int attackNum = packet.getInt();
		int attackAndSkillNum = packet.getInt();
		WebServer webServer = (WebServer)session.getClient();
		//log.info("skillId:{}",skillId);
		
		SceneData sceneData = webServer.sceneData;
		//如果没有这个技能
		JSONObject skill = skillData.getJSONObject(String.valueOf(skillId));
		//攻击错误包
		TcpPacket pt1 = new TcpPacket(cyou.akworld.OpCodeEx.SKILL_RESULT_S);
		pt1.putInt(0);
		pt1.putInt(skillId);
		
		pt1.putInt(attackNum);
		pt1.putInt(attackAndSkillNum);
		pt1.putInt(type);
		if(type == 2){
			pt1.putDouble(toPosition.x);
			pt1.putDouble(toPosition.y);
			pt1.putDouble(toPosition.z);
		}else{
			pt1.putInt(targetId);
		}
		if(skill == null || webServer.isDead){
			
			webServer.getSession().send(pt1);
			return;
		}
		if(webServer.isDizzy() && skill.getInt("type") != SkillData.FLASH){
			webServer.getSession().send(pt1);
			return;
		}
		//定身时不能施放冲锋
		if(webServer.isCanNotMove() && skill.getInt("type") == SkillData.RUSH){
			webServer.getSession().send(pt1);
			return;
		}
		if(webServer.isIceBox() || webServer.isCast()){
			webServer.getSession().send(pt1);
			return;
		}
		//需要目标而没传目标，直接取消技能
		if((targetId == 0 && skill.getInt("target") == 1) || (toPosition == null && skill.getInt("point") == 1)){
			
			webServer.getSession().send(pt1);
			return;
		}
		if(webServer.nowReadSkill != null){
			log.info("当前正在读条，不能释放其他技能");
			webServer.getSession().send(pt1);
			return;
		}
		if(webServer.isUserSkill && skill.getInt("read") > 0){
			log.info("正在施放大跳，不能放读条技能");
			webServer.getSession().send(pt1);
			return;
		}
		//无敌斩时，不能施放其他技能
		if(webServer.isUserSkill && webServer.skillData.getInt("type") == SkillData.OMNISLASH){
			log.info("无敌斩时，不能施放技能");
			webServer.getSession().send(pt1);
			return;
		}
		//如果这个玩家已经不再该场景了
		ActivityThing target = null;
		//有目标的话（只针对需要选目标，并且客户端选自己为目标的逻辑）
		boolean isMySelf = false;
		if(targetId != 0){
			if(targetId == webServer.getServerId()){
				isMySelf = true;
			}else{
				if(targetId > 0){
					
					ArrayList<WebServer> userList = webServer.horizonPlayer;
					int userListSize = userList.size();
					for(int j = 0; j < userListSize; j++){
						//这里也是基于消息包的，没问题，不需要判断场景
						WebServer webServer1 = userList.get(j);
						if(webServer1.getServerId() == targetId){
							target = webServer1;
							break;
						}
					}
					if(target == null || target.isDead){
						
						webServer.getSession().send(pt1);
						return;
					}
				}else{
					ArrayList<Monster> userList = webServer.horizonNpc;
					int userListSize = userList.size();
					for(int j = 0; j < userListSize; j++){
						Monster webServer1 = userList.get(j);
						if(webServer1.getServerId() == targetId){
							target = webServer1;
							break;
						}
					}
					if(target == null || target.isDead){
						
						webServer.getSession().send(pt1);
						return;
					}
				}
			}
			

		}
		if(skill.getInt("target") == 1){
			if(isMySelf){
				if(skill.getInt("isEnemy") != 0){
					webServer.getSession().send(pt1);
					return;
				}
			}else{
				//如果是对敌方的技能，但是这两个是一个阵营，不能释放
				if(skill.getInt("isEnemy") == 1 && (target.camp != null && target.camp.getInt("id") == webServer.camp.getInt("id"))){
					webServer.getSession().send(pt1);
					return;
				}
				//如果是对友方的，但是这个thing没有阵营或者他不跟施法者在一个阵营
				if(skill.getInt("isEnemy") == 0 && (target.camp == null || (target.camp != null && target.camp.getInt("id") != webServer.camp.getInt("id")))){
					webServer.getSession().send(pt1);
					return;
				}
			}
			
		}
		double distance = 0;
		//如果是自己就不用判断距离了
		if(target != null && !isMySelf){
			//从距离映射表里面取，这样效率高，提前都算完了
			distance = sceneData.distanceMap.get(webServer.getServerId() < target.getServerId() ? webServer.getServerId()+"_"+target.getServerId() : target.getServerId()+"_"+webServer.getServerId());
			//+50可以减少客户端与服务器的错误交互，服务器放宽一点距离
			long nowTime = System.currentTimeMillis();
			if(webServer.isWalk){
				if(distance > skill.getInt("attackDistance")+60+webServer.getNowMoveV()* ((ThreadService.tickTime+nowTime-webServer.time)/1000.0)){
					webServer.getSession().send(pt1);
					return;
				}
			}else{
				if(distance > skill.getInt("attackDistance")+60){
					
					webServer.getSession().send(pt1);
					return;
				}
			}
			
		}else if(toPosition != null){
			//闪现不用判断距离
			//if(skill.getInt("type") != SkillData.FLASH){
				Point3D positionClone = webServer.position.clone();
				positionClone.y = 0.0;
				Point2D.Double point = IsoUtils.isoToScreen(positionClone);
				Point2D.Double point1 = IsoUtils.isoToScreen(toPosition);
				distance = Point.distance(point.x,point.y,point1.x,point1.y);
				//如果正在走路，放宽点限制，计算一下，当前速度和间隔时间
				long nowTime = System.currentTimeMillis();
				if(webServer.isWalk){
					if(distance > skill.getInt("attackDistance")+60+webServer.getNowMoveV()* ((ThreadService.tickTime+nowTime-webServer.time)/1000.0)){
						webServer.getSession().send(pt1);
						return;
					}
				}else{
					//+50可以减少客户端与服务器的错误交互，服务器放宽一点距离
					if(distance > skill.getInt("attackDistance")+60){
						
						webServer.getSession().send(pt1);
						return;
					}
				}
				
			//}
		}
		
		TcpPacket pt = new TcpPacket(cyou.akworld.OpCodeEx.SKILL_RESULT_S);
		pt.putInt(1);
		pt.putInt(skillId);
		pt.putInt(attackNum);
		pt.putInt(attackAndSkillNum);
		pt.putInt(type);
		if(type == 2){
			pt.putDouble(toPosition.x);
			pt.putDouble(toPosition.y);
			pt.putDouble(toPosition.z);
		}else{
			pt.putInt(targetId);
			if(targetId != 0){
				if(target != null){
					pt.putDouble(target.position.x);
					pt.putDouble(target.position.y);
					pt.putDouble(target.position.z);
				}else{
					pt.putDouble(0.0);
					pt.putDouble(0.0);
					pt.putDouble(0.0);
				}
				
			}
			
		}
		
		//如果不是读条的飞行技能
		int flyId =  0;
		if(skill.getInt("read") <= 0 && skill.getBoolean("isfly") == true){
			flyId = webServer.sceneData.totemId--;
		}
		pt.putInt(flyId);
		
		webServer.getSession().send(pt);
		if(isMySelf){
			attack(webServer,skill,webServer,type,toPosition,dir,distance,false,flyId);
		}else{
			attack(webServer,skill,target,type,toPosition,dir,distance,false,flyId);
		}
		
		
		
		
	}

	public static void attack(ActivityThing webServer,JSONObject skill,ActivityThing target,int type,Point3D toPosition,int dir,double distance,boolean isComplete,int flyId) {
		//给飞行道具设置id
		int flyThingId = 0;
		if(flyId == 0){
			if((skill.getInt("read") <= 0 ||(skill.getInt("read") > 0 && isComplete)) && skill.getBoolean("isfly") == true){
				flyThingId = webServer.sceneData.totemId--;
			}
		}else{
			flyThingId = flyId;
		}
		ArrayList<WebServer> attackUserList;
		//if(sceneData.type == 2){
		//	attackUserList = sceneData.userList;
		//}else{
			attackUserList = webServer.horizonPlayer;
		//}
		
		//施放技能包
		TcpPacket pt1 = new TcpPacket(cyou.akworld.OpCodeEx.ATTACK_S);
		pt1.putInt(webServer.getServerId());
		pt1.putInt(skill.getInt("id"));
		pt1.putInt(type);
		if(type == 2){
			pt1.putDouble(toPosition.x);
			pt1.putDouble(toPosition.y);
			pt1.putDouble(toPosition.z);
		}else{
			if(target != null){
				pt1.putInt(target.getServerId());
				pt1.putDouble(target.position.x);
				pt1.putDouble(target.position.y);
				pt1.putDouble(target.position.z);
			}else{
				pt1.putInt(0);
			}
			
		}
		pt1.put(dir);
		if(isComplete){
			pt1.put(1);
		}else{
			pt1.put(0);
		}
		pt1.putInt(flyThingId);
		ArrayList<WebServer> horizonPlayer = null;
		if(flyThingId != 0){
			horizonPlayer = new ArrayList<WebServer>();
			//如果放技能的人是玩家，也要加入技能释放完成要广播的列表
			if(webServer.getClass() == WebServer.class){
				horizonPlayer.add((WebServer)webServer);
			}
		}
		//攻击时广播给攻击者的区域，而受到伤害则是广播给被攻击者的区域（他俩不在一个点上，所以区域是不一样的）
		int userListSize = attackUserList.size();
		for(int i = 0; i < userListSize; i++){
			WebServer webServer1 = attackUserList.get(i);
			//if(webServer.getServerId() != webServer1.getServerId()){
				//这里取消判断场景，因为有些人，可能已经移除这个场景了，不需要给他发消息了
				if(webServer1.sceneData != null && webServer1.sceneData.sceneId == webServer.sceneData.sceneId){
					webServer1.getSession().send(pt1);
					if(horizonPlayer != null){
						horizonPlayer.add(webServer1);
					}
				}
				
			//}
		}
		if(isComplete && webServer.getClass() == WebServer.class){
			((WebServer)webServer).getSession().send(pt1);
		}
		if(!isComplete && skill.getInt("read") > 0){
			webServer.readSkill(skill,target,type,toPosition,dir,distance);
		}else{
			//如果技能是飞行道具
			if(skill.containsKey("isfly") && skill.getBoolean("isfly") == true){
				FlySkillEffect effect = new FlySkillEffect();
				effect.size = webServer.sceneData.cellWidth;
				effect.setServerId(flyThingId);
				effect.skillData = skill;
				effect.master = webServer;
				if(skill.getInt("point") ==1){
					effect.toPosition = toPosition.clone();
					effect.toPoint = IsoUtils.isoToScreen(toPosition);
				}else if(skill.getInt("target") ==1){
					effect.toTarget = target;
				}
				if(horizonPlayer != null){
					effect.horizonPlayer = horizonPlayer;
				}
				effect.position = webServer.position.clone();
				effect.sceneData = webServer.sceneData;
				//用来飞行的移动FlySkillEffect.update
				effect.time = System.currentTimeMillis();
				webServer.sceneData.otherList.add(effect);
				//log.info("isfly");
			}else{
				if(skill.getInt("type") == SkillData.ATTACK || skill.getInt("type") == SkillData.BUFF || skill.getInt("type") == SkillData.TREAT|| skill.getInt("type") == SkillData.CLICK_THE_FLY){
					SkillData.selectTarget(skill,webServer,target,toPosition,webServer.sceneData,null);
				}else if(skill.getInt("type") == SkillData.BIG_JUMP){
					webServer.userSkill(skill,toPosition,distance,null);
				}else if(skill.getInt("type") == SkillData.RUSH){
					webServer.userSkill(skill,target.position,distance,null);
					SkillData.selectTarget(skill,webServer,target,toPosition,webServer.sceneData,null);
				//无敌斩
				}else if(skill.getInt("type") == SkillData.OMNISLASH){
					webServer.userSkill(skill,target.position,distance,target);
				}else if(skill.getInt("type") == SkillData.FLASH){
					//做闪现的处理
					webServer.calculateDir(webServer.position, toPosition, false);
					Point3D positionClone = webServer.position.clone();
					positionClone.y = 0.0;
					Point2D.Double point = IsoUtils.isoToScreen(positionClone);
					Point2D.Double point1 = IsoUtils.isoToScreen(toPosition);
					//Point2D.Double point2 = Thing.interpolate(point1, point, skill.getInt("flashDistance"));
					//限制传送位置
					if(point1.x < 0){
						point1.x = 0;
					}
					if(point1.x > webServer.sceneData.mapWidth){
						point1.x = webServer.sceneData.mapWidth;
					}
					if(point1.y < 0){
						point1.y = 0;
					}
					if(point1.y > webServer.sceneData.mapHeight){
						point1.y = webServer.sceneData.mapHeight;
					}
					webServer.position = IsoUtils.screenToIso(point1);
					webServer.setPath(null);
					TcpPacket pt2 = new TcpPacket(cyou.akworld.OpCodeEx.FLASH_S);
					pt2.putInt(webServer.getServerId());
					pt2.putDouble(webServer.position.x);
					pt2.putDouble(webServer.position.y);
					pt2.putDouble(webServer.position.z);
					pt2.put(webServer.dir);
					pt2.putInt(skill.getInt("id"));
					//如果当前正在使用距离技能，则取消
					if(webServer.isUserSkill){
						webServer.clearUseSkill(false);
						pt2.putInt(1);
					}else{
						pt2.putInt(0);
					}
					for(int i = 0; i < userListSize; i++){
						WebServer webServer1 = attackUserList.get(i);
						//if(webServer.getServerId() != webServer1.getServerId()){
							//其实这个是不需要判断的，因为人物的闪现是基于消息包，以后怪物要是增加闪现技能的话，这个就需要判断了
							if(webServer1.sceneData != null && webServer1.sceneData.sceneId == webServer.sceneData.sceneId){
								webServer1.getSession().send(pt2);
							}
							
						//}
					}
					if(webServer.getClass() == WebServer.class){
						((WebServer)webServer).getSession().send(pt2);
					}
				}else if(skill.getInt("type") == SkillData.TOTEM){
					JSONObject monster = GameServerService.monsterData.getJSONObject(skill.getString("totem"));
					MonsterTotem thing = new MonsterTotem();
					thing.size = webServer.sceneData.cellWidth;
					thing.setAtt(monster.getInt("att"));
					thing.setDef(monster.getInt("def"));
					thing.setHp(monster.getInt("maxHp"));
					thing.setMaxHp(monster.getInt("maxHp"));
					thing.moveV = monster.getInt("moveV");
					thing.attackSpeed = monster.getInt("attackSpeed");
					thing.jumpVerticalV = monster.getInt("jumpVerticalV");
					
					thing.jumpVerticalA = monster.getInt("jumpVerticalA");
					thing.monster = monster;
					if(monster.getInt("skillId") > 0){
						thing.monsterSkillData = GameServerService.skillData.getJSONObject(""+monster.getInt("skillId"));
					}
					
					if(monster.containsKey("skill")){
						JSONArray skillArray = monster.getJSONArray("skill");
						for(int j = 0;j < skillArray.size();j++){
							Skill skillData = new Skill();
							skillData.skillData = GameServerService.skillData.getJSONObject(""+skillArray.getInt(j));
							skillData.userTime = 0;
							thing.skillArray.add(skillData);
						}
					}
					
					thing.dir = 5;
					//没什么用
					thing.time = System.currentTimeMillis();
					thing.position = toPosition.clone();
					
					thing.setServerId(webServer.sceneData.totemId--);
					thing.sceneData = webServer.sceneData;
					thing.camp = webServer.camp;
					thing.master = webServer;
					thing.startTime = thing.time;
					webServer.sceneData.npcList.add(thing);
				}
				
			}
		}

	}
	@Override
	public int[] getEventTypes() {
		return new int[] { IoEvent.EVENT_SESSION_REMOVEING ,IoEvent.EVENT_KICK_PLAYER , IoEvent.EVENT_PLAYER_CAN_LOGIN,IoEvent.EVENT_PLAYER_RE_LOGIN};
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
				//通知world服务器用户已经下线
				WorldManager wmanager = Platform.getAppContext().get(WorldManager.class);
				wmanager.PlayerLogOut(wmanager.ioSession,webServer.getServerId());
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
						server.setState(1);
						log.info("[WebServer] clients remove:{}", server.getServerId());
					} else {
						log.info("[WebServer] clients remove:{}", session.getClientIP());
					}
				}
			}
			break;
		case IoEvent.EVENT_KICK_PLAYER:
			
			
			int serverId = Integer.valueOf(event.param1.toString());
			int sessionId = Integer.valueOf(event.param2.toString());
			WebServer webServer1 = clients.get(serverId);
			if(webServer1 != null){
				if(webServer1.getSession().getIoSession() != null){
					if(webServer1.getSession().getId() == sessionId){
						webServer1.getSession().getIoSession().close(true);
					}else{
						log.info("警告：不可能事情发生了，sessionid不匹配");
					}
					
				}
			}
			break;
		case IoEvent.EVENT_PLAYER_RE_LOGIN:
			
			
			int serverId2 = Integer.valueOf(event.param1.toString());
			int sessionId2 = Integer.valueOf(event.param2.toString());
			RequestWorld nowRequestWorld1 = null;
			for(int j = 0; j<requestWorldList.size();j++){
				RequestWorld requestWorld = requestWorldList.get(j);
				if(requestWorld.session.getId() == sessionId2 && requestWorld.serverId == serverId2){
					nowRequestWorld1 = requestWorld;
					break;
				}
			}
			if(nowRequestWorld1 == null){
				log.info("警告：不可能事情发生了，nowRequestWorld不存在");
				return;
			}else{
				if(nowRequestWorld1.session.getIsClear()){
					log.info("改连接已经断开了，无需在告诉玩家重连了");
					requestWorldList.remove(nowRequestWorld1);
					return;
				}
			}
			requestWorldList.remove(nowRequestWorld1);
			TcpPacket pt2 = new TcpPacket(cyou.akworld.OpCodeEx.AGAIN_CONNECT_SERVER_S);
			nowRequestWorld1.session.send(pt2);
			break;
		case IoEvent.EVENT_PLAYER_CAN_LOGIN:
			int serverId1 = Integer.valueOf(event.param1.toString());
			int sessionId1 = Integer.valueOf(event.param2.toString());
			
			RequestWorld nowRequestWorld = null;
			for(int j = 0; j<requestWorldList.size();j++){
				RequestWorld requestWorld = requestWorldList.get(j);
				if(requestWorld.session.getId() == sessionId1 && requestWorld.serverId == serverId1){
					nowRequestWorld = requestWorld;
					break;
				}
			}
			if(nowRequestWorld == null){
				log.info("警告：不可能事情发生了，nowRequestWorld不存在");
				return;
			}else{
				if(nowRequestWorld.session.getIsClear()){
					log.info("改连接已经断开了，无需再创建玩家了");
					//移除登录列表
					requestWorldList.remove(nowRequestWorld);
					//这里通知world服务器，这个用户已经断开了
					WorldManager wmanager = Platform.getAppContext().get(WorldManager.class);
					wmanager.PlayerLogOut(wmanager.ioSession,serverId1);
					return;
				}
			}
			//移除登录列表
			requestWorldList.remove(nowRequestWorld);
			
			JSONObject camp = campData.getJSONObject(nowRequestWorld.campId+"");
			
			
			WebServer webServer = new WebServer();
			webServer.setServerId(nowRequestWorld.serverId);
			nowRequestWorld.session.setClient(webServer);
			
			clients.put(webServer.getServerId(), webServer);
			//设置默认方向，坐标，时间
			webServer.dir = 5;
			
			//没什么用
			webServer.time = System.currentTimeMillis();
			
			JSONObject data = monsterData.getJSONObject(""+nowRequestWorld.monsterId);
			
			
			webServer.moveV = data.getInt("moveV");
			webServer.jumpVerticalV = data.getInt("jumpVerticalV");
			
			webServer.jumpVerticalA = data.getInt("jumpVerticalA");
			webServer.attackSpeed = data.getInt("attackSpeed");
			webServer.setHp(data.getInt("maxHp"));
			webServer.setMaxHp(data.getInt("maxHp"));
			webServer.setAtt(data.getInt("att"));
			webServer.setDef(data.getInt("def"));
			webServer.monster = data;
			webServer.monsterSkillData = skillData.getJSONObject(""+data.getInt("skillId"));
			webServer.camp = camp;
			webServer.session.authenticate(null);
			webServer.setToSceneId(nowRequestWorld.sceneId);
			webServer.isLoadOk = false;
			//分配到场景1
			ThreadService threadService = Platform.getAppContext().get(ThreadService.class);
			boolean changeServer = threadService.addLoadUse(webServer, webServer.getToSceneId());
			if(changeServer){
				TcpPacket pt1 = new TcpPacket(cyou.akworld.OpCodeEx.CHANGE_SERVER_S);
				pt1.putString(GameServerService.serverUrlMap.get(ThreadService.sceneServerMap.get(webServer.getToSceneId())));
				pt1.putInt(GameServerService.serverPortMap.get(ThreadService.sceneServerMap.get(webServer.getToSceneId())));
				pt1.putInt(webServer.getToSceneId());
				webServer.getSession().send(pt1);
			}else{
				TcpPacket pt1 = new TcpPacket(cyou.akworld.OpCodeEx.START_LOADING_S);
				pt1.putInt(webServer.monster.getInt("id"));
				pt1.putInt(webServer.getToSceneId());
				webServer.getSession().send(pt1);
			}
			
			break;
		default:
			break;
		}

	}

}
