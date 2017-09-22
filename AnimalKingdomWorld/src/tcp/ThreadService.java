package tcp;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import _45degrees.com.friendsofed.isometric.IsoUtils;
import _45degrees.com.friendsofed.isometric.Point3D;
import _astar.Grid;
import _astar.Node;
import cyou.akworld.ActivityThing;
import cyou.akworld.BuffArray;
import cyou.akworld.BuffInfo;
import cyou.akworld.GameServerService;
import cyou.akworld.Monster;
import cyou.akworld.MonsterNormal;
import cyou.akworld.MonsterTotem;
import cyou.akworld.Skill;
import cyou.akworld.Thing;
import cyou.akworld.WebServer;
import cyou.mrd.Platform;
import cyou.mrd.io.tcp.TcpPacket;
import cyou.mrd.packethandler.TcpHandlerDispatch;
import cyou.mrd.packethandler.TcpHandlerDispatchManager;
import cyou.mrd.packethandler.TcpPacketHandler;
import cyou.mrd.service.Service;
import cyou.mrd.updater.Updatable;

public class ThreadService implements Service ,Updatable{

	public static int DEFAULT_THREAD = 10;
	private HashMap<Integer, AsyncRunner> threadMap = new HashMap<Integer, ThreadService.AsyncRunner>();
	private HashMap<Integer, Integer> sceneThreadMap = new HashMap<Integer, Integer>();
	public static JSONObject sceneData;
	private HashMap<Integer, JSONObject> sceneMap = new HashMap<Integer, JSONObject>();
	private static Logger log = LoggerFactory.getLogger(ThreadService.class);
	//此值并不准确没关系，只是做一个大概的测试
	private long maxTime = 0;
	private long updateTime = 0;
	private int monsterNum = 0;
	private int sceneNum = 0;
	//玩家的视野半径
	public static int viewScope = 1024;
	//当某个事物，要比视野半径大着个值的时候，才会移除视野，这样可以防止，某些人或者某些怪，进进出出玩家的视野，提高效率
	public static int removeExpand = 400;
	public static int tickTime = Platform.getConfiguration().getInt("thread_tick");
	public static int serverId = Platform.getConfiguration().getInt("server_id");
	public static HashMap<Integer, Integer> sceneServerMap = new HashMap<Integer, Integer>();
	public ArrayList<WebServer> loadlingUserList = new ArrayList<WebServer>();
	
	public ArrayList<WebServer> changeServerUserList = new ArrayList<WebServer>();
	@Override
	public String getId() {
		
		return "ThreadService";
	}

	@Override
	public void startup() throws Exception {
		
		TcpPacketHandler handler = TcpHandlerDispatchManager.get(TcpHandlerDispatch.PLAYER);
		for(int i = 1; i <= DEFAULT_THREAD ;i++){
			AsyncRunner as = new AsyncRunner(handler);
			threadMap.put(i, as);
			
			
		}
		sceneData = JSONObject.fromObject(GameServerService.sceneData);
		JSONArray jsArray = sceneData.getJSONArray("scene");
		for(int i = 0;i<jsArray.size();i++){
			String mapId = jsArray.getString(i);
			
			String[] strArray = mapId.split("_");
			
			sceneServerMap.put(Integer.parseInt(strArray[0]),Integer.parseInt(strArray[1]));
			//这个场景不在这个服
			if(Integer.parseInt(strArray[1]) != serverId){
				continue;
			}
			File file = new File(Platform.getConfiguration().getString("mapdir")+"map/"+strArray[0]+".json");
			InputStream stream = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(stream, "utf-8"));
			String line = "";
			while (br.ready()) {
				line += br.readLine();
			}
			JSONObject js = JSONObject.fromObject(line);
			sceneMap.put(Integer.parseInt(strArray[0]),js);
		}
		Object[] keyArray = sceneMap.keySet().toArray();
		for(int i = 0;i<keyArray.length;i++){
			JSONObject scene = sceneMap.get(keyArray[i]);
			addScene(scene,scene.getInt("threadId"));
			sceneThreadMap.put(scene.getInt("id"), scene.getInt("threadId"));
			sceneNum ++;
		}
		log.info("rpgServer:一共"+monsterNum+"只怪");
		log.info("rpgServer:一共"+sceneNum+"个场景");
		
		//后启动线程，因为初始化场景和怪物的时候，可能异步线程已经调用了的同时，主线程又插入了，导致数量不一致
		for(int i = 1; i <= DEFAULT_THREAD ;i++){
			Thread t = new Thread(threadMap.get(i),"scene_thread_"+i);
			t.start();
		}
		Platform.getUpdater().addSyncUpdatable(this);
	}
	public boolean addScene(JSONObject scene,int threadKey){
		SceneData sceneData = new SceneData();
		sceneData.sceneId = scene.getInt("id");
		sceneData.type = scene.getInt("type");
		sceneData.cellWidth = scene.getInt("cellWidth");
		sceneData.mapWidth = scene.getInt("mapWidth");
		sceneData.mapHeight = scene.getInt("mapHeight");
		sceneData.scene = scene;
		
		Point3D point0 = IsoUtils.screenToIso(new Point2D.Double(0.0,0.0));
		Point point00 = new Point((int)Math.round(point0.x/sceneData.cellWidth),(int)Math.round(point0.z/sceneData.cellWidth));
		sceneData.countMaxAndMin(point00);
		
		Point3D point1 = IsoUtils.screenToIso(new Point2D.Double(sceneData.mapWidth,0.0));
		Point point11 = new Point((int)Math.round(point1.x/sceneData.cellWidth),(int)Math.round(point1.z/sceneData.cellWidth));
		sceneData.countMaxAndMin(point11);
		
		Point3D point2 = IsoUtils.screenToIso(new Point2D.Double(0,sceneData.mapHeight));
		Point point22 = new Point((int)Math.round(point2.x/sceneData.cellWidth),(int)Math.round(point2.z/sceneData.cellWidth));
		sceneData.countMaxAndMin(point22);
		
		Point3D point3 = IsoUtils.screenToIso(new Point2D.Double(sceneData.mapWidth,sceneData.mapHeight));
		Point point33 = new Point((int)Math.round(point3.x/sceneData.cellWidth),(int)Math.round(point3.z/sceneData.cellWidth));
		sceneData.countMaxAndMin(point33);
		
		sceneData.grid = new Grid(sceneData.minX, sceneData.maxX, sceneData.minY, sceneData.maxY);
		JSONArray monsterArray = scene.getJSONArray("monster");
		for(int i = 0;i < monsterArray.size();i++){
			String str = monsterArray.getString(i);
			String[] strArray = str.split("_");
			JSONObject monster = GameServerService.monsterData.getJSONObject(strArray[0]);
			Node node = sceneData.grid.getNode(Integer.parseInt(strArray[1]), Integer.parseInt(strArray[2]));
			if(monster == null || node == null){
				log.info("rpgServer:初始化怪出问题");
				continue;
			}
			MonsterNormal thing = new MonsterNormal();
			thing.size = sceneData.cellWidth;
			thing.setAtt(monster.getInt("att"));
			thing.setDef(monster.getInt("def"));
			thing.setHp(monster.getInt("maxHp"));
			thing.setMaxHp(monster.getInt("maxHp"));
			thing.moveV = monster.getInt("moveV");
			thing.attackSpeed = monster.getInt("attackSpeed");
			thing.jumpVerticalV = monster.getInt("jumpVerticalV");
			
			thing.jumpVerticalA = monster.getInt("jumpVerticalA");
			thing.monster = monster;
			thing.monsterSkillData = GameServerService.skillData.getJSONObject(""+monster.getInt("skillId"));
			if(monster.containsKey("skill")){
				JSONArray skillArray = monster.getJSONArray("skill");
				for(int j = 0;j < skillArray.size();j++){
					Skill skill = new Skill();
					skill.skillData = GameServerService.skillData.getJSONObject(""+skillArray.getInt(j));
					skill.userTime = 0;
					thing.skillArray.add(skill);
				}
			}
			
			thing.dir = 5;
			//没什么用
			thing.time = System.currentTimeMillis();
			thing.position = new Point3D(node.x*sceneData.cellWidth+0.0,0.0,node.y*sceneData.cellWidth+0.0);
			thing.initialPosition = new Point3D(node.x*sceneData.cellWidth+0.0,0.0,node.y*sceneData.cellWidth+0.0);
			thing.setInitialNode(node);
			thing.setServerId(-i-1);
			thing.sceneData = sceneData;
			if(monster.getInt("camp") != 0){
				JSONObject camp = GameServerService.campData.getJSONObject(monster.getInt("camp")+"");
				if(camp == null){
					log.info("rpgServer:初始化怪的阵营错误");
					continue;
				}
				thing.camp = camp;
			}
			sceneData.npcList.add(thing);
			monsterNum++;
			
		}
		
		return threadMap.get(threadKey).addScene(sceneData);
	}
	/**
	 * 只允许主线程调用
	 * @param user
	 * @param sceneId
	 * @return
	 */
	public boolean addUser(WebServer user, int sceneId){
		//如果toSceneId大于0，就是正在进入场景中，不能在进入其他场景了
		if(user.getToSceneId() > 0){
			log.info("rpgServer:玩家进入场景失败，因为这个玩家已经加入了另一个场景的进入队列");
			return false;
		}
		
		if(sceneThreadMap.get(sceneId) == null){
			log.info("rpgServer:玩家进入场景失败，不存在这个场景");
			return false;
		}
		return threadMap.get(sceneThreadMap.get(sceneId)).addUser(user, sceneId);
	}
	
	/**
	 * 只允许主线程调用
	 * @param user
	 * @param fromSceneId
	 * @param toSceneId
	 * @return
	 */
	public boolean removeUser(WebServer user,int toSceneId){
		//如果toSceneId大于0，就是正在进入场景中，不能在进入其他场景了
		if(user.getToSceneId() > 0){
			log.info("rpgServer:玩家更换场景失败，因为这个玩家已经加入了离开场景列表，设置好了要更换的场景");
			return false;
		}
		//如果要去的场景，并不存在，就不去了
		if(sceneServerMap.get(toSceneId) == null){
			log.info("rpgServer:玩家更换场景失败，不存在这个场景");
			return false;
		}
		//这里的user.getSceneId()也没有问题，因为当toSceneId=-1时，改玩家肯定进入了场景线程
		return threadMap.get(sceneThreadMap.get(user.sceneData.sceneId)).removeUser(user,toSceneId);
	}
	
	@Override
	public boolean update() {
		putRemoveUserListInScene();
		putLoadingUserListInScene();
		handleChangeServerUserList();
		return true;
	}
	public boolean addLoadUse(WebServer user, int sceneId){
		//如果要去的场景不在这个服务器上
		if(sceneServerMap.get(sceneId) != serverId){
			//加入换服务器用户列表
			user.isReceiveChangeServer = false;
			//这里应该
			changeServerUserList.add(user);
			return true;
		}
		if(loadlingUserList.contains(user)){
			log.info("rpgServer:等在加载列表已经有这个玩家了，肯定是线程间出问题了");
			return false;
		}
		//user.setToSceneId(sceneId);
		loadlingUserList.add(user);
		user.loadTime = System.currentTimeMillis();
		return false;
	}
	//处理换服务器的用户列表
	public void handleChangeServerUserList(){
		int size = changeServerUserList.size();
		long time = System.currentTimeMillis();
		for(int j = 0;j<size;j++){
			WebServer webServer = changeServerUserList.get(j);
			if(webServer.getState() == 1){
				webServer.setState(2);
				changeServerUserList.remove(webServer);
				j--;
				size--;
				continue;
			}
		}
	}
	public void putLoadingUserListInScene(){
		int size = loadlingUserList.size();
		long time = System.currentTimeMillis();
		for(int j = 0;j<size;j++){
			WebServer webServer = loadlingUserList.get(j);
			if(webServer.getState() == 1){
				webServer.setState(2);
				loadlingUserList.remove(webServer);
				j--;
				size--;
				continue;
			}
			if(!webServer.isLoadOk){
				if((time - webServer.loadTime)/1000.0 > 5000){
					log.info("rpgServer:玩家卡在进度条了，可以查问题了");
					webServer.loadTime = time;
				}
				continue;
			}
			
			int sceneId = webServer.getToSceneId();
			//因为addUser有判断
			webServer.setToSceneId(-1);
			addUser(webServer, sceneId);
			loadlingUserList.remove(webServer);
			j--;
			size--;
		}
	}
	/**
	 * 主线程调用自动执行（这里也得处理已经断开的用户，因为马上要加上得等他们加载资源成功，才可以进入场景）
	 */
	public void putRemoveUserListInScene(){
		Object[] s = threadMap.keySet().toArray();
		//循环所有场景
		for(int i = 0; i < threadMap.size(); i++){
			AsyncRunner asyncRunner = threadMap.get(s[i]);
			Object[] ss = asyncRunner.sceneMap.keySet().toArray();
			//循环所有场景
			for(int ii = 0; ii < asyncRunner.sceneMap.size(); ii++){
				SceneData sceneData = asyncRunner.sceneMap.get(ss[ii]);
				ArrayList<WebServer> userList = new ArrayList<WebServer>();
				sceneData.removedUserList.drainTo(userList);
				int size = userList.size();
				for(int j = 0;j<size;j++){
					WebServer webServer = userList.get(j);
					//如果这个用户在这个更换场景的中间地段掉线了，则给他设置为可以被清理
					if(webServer.getState() == 1){
						webServer.setState(2);
						log.info("rpgServer:玩家从切换场景的中间地带掉线了，不叫他进入场景了");
						continue;
					}
					//玩家正在加载要去的场景资源，加载好了再给他放入场景
					if(!webServer.isLoadOk)
					{
						log.info("rpgServer:玩家加载资源中，别着急");
						boolean changeServer = addLoadUse(webServer,webServer.getToSceneId());
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
						continue;
					}
					//底下这三句是不可能的
					//int sceneId = webServer.getToSceneId();
					//因为addUser有判断
					//webServer.setToSceneId(-1);
					//addUser(webServer, sceneId);
				}
			}
		}
	}
	@Override
	public void shutdown() throws Exception {
		

	}
	class AsyncRunner implements Runnable{
		private TcpPacketHandler handler;
		//目前场景没有动态生成的，都是上来主线程初始化的，所以这个暂时不用安全类
		public HashMap<Integer, SceneData> sceneMap = new HashMap<Integer, SceneData>();

		public AsyncRunner(TcpPacketHandler handler){
			this.handler = handler;
			
			
		}
		//添加一个场景到这个线程（初始化时，调用）
		public boolean addScene(SceneData sceneData){
			
			sceneMap.put(sceneData.sceneId, sceneData);
			return true;
			
		}
		
		//添加用户到此线程某个场景（这块可以在优化，进口只有一个主线程，出口也只有一个场景线程）
		public boolean addUser(WebServer user, int sceneId){
				
			try {
				
				user.setToSceneId(sceneId);
				sceneMap.get(sceneId).addUserList.put(user);
				
				return true;
			} catch (InterruptedException e) {
				log.info("rpgServer:玩家从主线程进入场景失败");
				return false;
			}
		}
		//移除用户从此线程的某个场景
		public boolean removeUser(WebServer user){
			try {
				
				
				sceneMap.get(user.sceneData.sceneId).removeUserList.put(user);
				return true;
			} catch (InterruptedException e) {
				log.info("rpgServer:玩家掉线后,从场景线程离开失败");
				return false;
			}
			
		}
		//移除用户从此线程的某个场景
		public boolean removeUser(WebServer user, int toSceneId){
			try {
				
				user.setToSceneId(toSceneId);
				sceneMap.get(user.sceneData.sceneId).removeUserList.put(user);
				
				return true;
			} catch (InterruptedException e) {
				log.info("rpgServer:玩家从主线程去其他场景失败");
				return false;
			}
			
		}
		@Override
		public void run() {
			while(true){
				
				//原来的人都相互知道，只需要把现在新加入的人广播给原来的人
				//新加入的人全不知道，需要广播所以人给
				//删除的人广播给，原来的用户，
				//删除人不需要广播给刚进入的用户，
				long startTime = System.currentTimeMillis();
				Object[] s = sceneMap.keySet().toArray();
				//循环所有场景
				for(int i = 0; i < sceneMap.size(); i++){
					SceneData sceneData = sceneMap.get(s[i]);
					
					
					//上来先把要进入场景和要出场景的人物，取出来，保证这一轮训，不会再有别人加入，这个做法很安全
					ArrayList<WebServer> addUserArray = new ArrayList<WebServer>();
					sceneData.addUserList.drainTo(addUserArray);
					ArrayList<WebServer> removeUserArray = new ArrayList<WebServer>();
					sceneData.removeUserList.drainTo(removeUserArray);
					//添加
					int addUserArraySize = addUserArray.size();
					for(int j = 0;j<addUserArraySize;j++){
						WebServer webServer = addUserArray.get(j);
						webServer.size = sceneData.cellWidth;
						sceneData.userList.add(webServer);
						if(webServer.position == null){
							webServer.position = new Point3D(77*32.0, 0.0, 28*32.0);
						}
						webServer.sceneData = sceneData;
						//设置要到的场景为-1，如果设置完之后，这个玩家在进入别人场景也不会有问题，因为上来已经把要加入和要删除的玩家，定死了
						webServer.setToSceneId(-1);
						enterScene(webServer);
					}
					
					//移除
					int removeUserArraySize = removeUserArray.size();
					for(int m = 0;m<removeUserArraySize;m++){
						WebServer webServer = removeUserArray.get(m);
						sceneData.userList.remove(webServer);
						//清除他这个场景区域广播的数据
						webServer.outScene();
						webServer.sceneData = null;
						if(webServer.getState() == 1){
							webServer.setState(2);
							continue;
						}
						if(webServer.getToSceneId() > -1){
							outScene(webServer);
							try {
								//放入移除列表的同时，主线程调用移除列表，叫用户进入另一个场景，是没有问题，因为这个移除的用户，不会再这次参与数值计算，只是做一个广播而已
								sceneData.removedUserList.put(webServer);
							} catch (InterruptedException e) {
								//应该做一个保护措施
								e.printStackTrace();
								log.info("rpgServer:场景线程把玩家放入已经移除的列表，等待进入其他场景失败");
							}
						}
					}
					
					
					//复活npc
					int reviveNpcSize = sceneData.deadNpcList.size();
					for(int j = 0;j<reviveNpcSize;j++){
						MonsterNormal monster = (MonsterNormal)sceneData.deadNpcList.get(j);
						boolean isRefresh = monster.refresh();
						if(isRefresh){
							sceneData.deadNpcList.remove(j);
							sceneData.npcList.add(monster);
							j--;
							reviveNpcSize--;
						}
					}
					/**
					 * 
					 *对于玩家来讲，死了就等于隐藏了，对于怪物来讲死了就等于消失在场景里了，
					 *因为玩家还想看到大家的战斗，所以不能玩家视野里的怪和人，也不能其他人有这个人的视野
					 */
					//对某个场景的人物进行刷新
					int userListSize = sceneData.userList.size();
					for(int j = 0;j<userListSize;j++){
						WebServer webServer = sceneData.userList.get(j);
						//如果状态==1，就说明这个用户已经断开，要给他删除
						if(webServer.getState() == 1){
							removeUser(webServer);
						}
						//死了就不做更新了
						if(webServer.isDead){
							if(!webServer.deadIsBoradCast){
								webServer.deadIsBoradCast = true;
								
								
								boardCastDead(webServer);
							}else{
								if((System.currentTimeMillis() - webServer.time) > 5000){
									webServer.isDead = false;
									webServer.deadIsBoradCast = false;
									webServer.setHp(webServer.getMaxHp());
									boardCastLife(webServer);
								}
							}
							continue;
						}
						webServer.update();
					}
					//对某个场景的npc进行刷新
					int npcListSize = sceneData.npcList.size();
					ArrayList<Monster> removeMonsterArray = new ArrayList<Monster>();
					for(int m = 0;m < npcListSize;m++){
						Monster npc = sceneData.npcList.get(m);
						if(npc.getClass() == MonsterNormal.class){
							if(npc.isDead){
								npc.horizonNpc.clear();
								npc.horizonPlayer.clear();
								//加入移除列表
								removeMonsterArray.add(npc);
								//加入死的npc列表，根据刷新时间，在刷新
								sceneData.deadNpcList.add(npc);
								sceneData.npcList.remove(m);
								m--;
								npcListSize--;
								continue;
							}
						}else if(npc.getClass() == MonsterTotem.class){
							if(npc.isDead){
								npc.horizonNpc.clear();
								npc.horizonPlayer.clear();
								//加入移除列表
								removeMonsterArray.add(npc);
								sceneData.npcList.remove(m);
								m--;
								npcListSize--;
								continue;
							}
						}
						npc.update();
					}
					
					//对某个场景的其他事物做刷新
					int otherListSize = sceneData.otherList.size();
					for(int n = 0;n<otherListSize;n++){
						Thing thing = sceneData.otherList.get(n);
						thing.update();
						if(thing.isDispose){
							sceneData.otherList.remove(n);
							n--;
							otherListSize--;
						}
					}
					//清理距离映射表
					sceneData.distanceMap.clear();
					//（有的场景需要广播所有玩家， 有的场景需要区域广播，分类型吧）
					//需要区域广播
					if(sceneData.type == 1){
						areaBroadcast(removeUserArray,removeMonsterArray,sceneData,true);
					}else{
						//不需要区域广播，广播给所有场景里的玩家
						//这段暂时不要了，下面的循环就足够了，不需要了，提高效率
						/**userListSize = sceneData.userList.size();
						for(int m = 0;m<userListSize;m++){
							WebServer webServer = sceneData.userList.get(m);
							if(addUserArray.contains(webServer)){
								broadCastAllThing(webServer,sceneData.userList);
							}else{
								broadCastAllThing(webServer,addUserArray);
								outGame(webServer,removeUserArray);
							}
						}
						**/
						areaBroadcast(removeUserArray,removeMonsterArray,sceneData,false);
					}
					//这里写人物保存的行为（在怪物行为之前广播吧）
					for(int n = 0;n < userListSize;n++){
						WebServer webServer = sceneData.userList.get(n);
						webServer.action();
					}
					//这里写怪物要产生的行为（攻击，追人，还是走路）
					for(int m = 0;m < npcListSize;m++){
						Monster npc = sceneData.npcList.get(m);
						npc.action();
					}
					ArrayList<TcpPacket> handleMessage = sceneData.getHandleMessage();
					int handleMessageSize = handleMessage.size();
					for(int c = 0;c<handleMessageSize;c++){
						TcpPacket tcpPacket = handleMessage.get(c);
						WebServer webServer = (WebServer)tcpPacket.getClient();
						//这个包可能是中间包，也可能是玩家进入相同线程两个不同场景的包，如果玩家所在的这个场景
						if(webServer.sceneData != null && sceneData.sceneId == webServer.sceneData.sceneId){
							try {
								handler.handle(tcpPacket, tcpPacket.getClient().getSession());
							} catch (Exception e) {
								
								e.printStackTrace();
							}
						}else{
							
						}
						tcpPacket.clear();
					}
				}
				if(System.currentTimeMillis() - updateTime  > 1000*30){
					updateTime = System.currentTimeMillis();
					log.info("某个线程30秒之中使用最大时间："+maxTime);
					maxTime = 0;
				}
				long useTime = System.currentTimeMillis()-startTime;
				if(useTime >= tickTime){
					log.info("某个线程使用时间大于100秒："+useTime);
				}
				if(useTime > maxTime){
					maxTime = useTime;
				}
				if(useTime < tickTime){
					try {
						//这里得做修改，不能直接睡眠那么长时间，导致客户端更新过快，打怪的时候不是特别同步
						//Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
						Thread.sleep(tickTime-useTime);
					} catch (InterruptedException e) {
						
					}
				}
				
				
			}
			
		}
		//区域广播(计算距离，广播添加与删除的人)
		public void areaBroadcast(ArrayList<WebServer> removeUserArray,ArrayList<Monster> removeMonsterArray,SceneData sceneData,boolean isAreaBroadcast){
			int size = sceneData.userList.size();
			int removeUserArraySize = removeUserArray.size();
			int removeMonsterArraySize = removeMonsterArray.size();
			for(int i = 0;i < size;i++){
				WebServer webServer = sceneData.userList.get(i);
				for(int j = i+1;j<size;j++){
					WebServer webServer1 = sceneData.userList.get(j);
					Point3D postionClone = webServer.position.clone();
					postionClone.y = 0.0;
					Point3D postionClone1 = webServer1.position.clone();
					postionClone1.y = 0.0;
					Point2D.Double point = IsoUtils.isoToScreen(postionClone);
					Point2D.Double point1 = IsoUtils.isoToScreen(postionClone1);
					double distance = Point.distance(point.x,point.y,point1.x,point1.y);
					//设置距离对应表
					sceneData.distanceMap.put(webServer.getServerId() < webServer1.getServerId() ? webServer.getServerId()+"_"+webServer1.getServerId() : webServer1.getServerId()+"_"+webServer.getServerId(), distance);
					//可以广播
					if(distance <= viewScope || !isAreaBroadcast){
						//如果某事物正在释放技能，这种技能会移动距离，需要时间的，就先不广播给玩家
						//如果没有广播给玩家，玩家的某些技能是无法对他造成伤害的，例如面积杀伤，
						//有一些技能是可以对他造成伤害的，例如打别人的时候，别的的视野里有他，而且是面积技能，他会受到伤害
						if(!webServer1.isUserSkill){
							if(!webServer.horizonPlayer.contains(webServer1)){
								webServer.horizonPlayer.add(webServer1);
								thingEnterGame(webServer,webServer1);
							}
						}
						if(!webServer.isUserSkill){
							if(!webServer1.horizonPlayer.contains(webServer)){
								webServer1.horizonPlayer.add(webServer);
								thingEnterGame(webServer1,webServer);
							}
						}
						
						
					//不可以广播	
					}else if(distance > viewScope + removeExpand){
						if(webServer.horizonPlayer.contains(webServer1)){
							webServer.horizonPlayer.remove(webServer1);
							thingOutGame(webServer,webServer1);
							
						}
						if(webServer1.horizonPlayer.contains(webServer)){
							webServer1.horizonPlayer.remove(webServer);
							thingOutGame(webServer1,webServer);
							
						}
					}
					
				}
				//如果这个删除列表里面有玩家在视野里，删除
				for(int m = 0;m < removeUserArraySize;m++){
					WebServer removeWebServer = removeUserArray.get(m);
					if(webServer.horizonPlayer.contains(removeWebServer)){
						webServer.horizonPlayer.remove(removeWebServer);
						thingOutGame(webServer,removeWebServer);
					}
				}
				sortPlayerHorizonThing(webServer,sceneData);
			}
			
			
			//增加npc，玩家的npc视野列表和npc的玩家视野列表
			int npcSize = sceneData.npcList.size();
			for(int j = 0;j < npcSize;j++){
				Monster monster = sceneData.npcList.get(j);
				for(int i = 0;i < size;i++){
					WebServer webServer = sceneData.userList.get(i);
					
					Point3D postionClone = webServer.position.clone();
					postionClone.y = 0.0;
					Point3D postionClone1 = monster.position.clone();
					postionClone1.y = 0.0;
					Point2D.Double point = IsoUtils.isoToScreen(postionClone);
					Point2D.Double point1 = IsoUtils.isoToScreen(postionClone1);
					double distance = Point.distance(point.x,point.y,point1.x,point1.y);
					//设置距离对应表
					sceneData.distanceMap.put(monster.getServerId()+"_"+webServer.getServerId(), distance);
					//可以广播
					if(distance <= viewScope || !isAreaBroadcast){
						if(!monster.isUserSkill){
							if(!webServer.horizonNpc.contains(monster)){
								webServer.horizonNpc.add(monster);
								thingEnterGame(webServer,monster);
							}
						}
						
						//npc不必做广播
						if(!monster.horizonPlayer.contains(webServer)){
							monster.horizonPlayer.add(webServer);
						}
					}else if(distance > viewScope+removeExpand){
						if(webServer.horizonNpc.contains(monster)){
							webServer.horizonNpc.remove(monster);
							thingOutGame(webServer,monster);
							
						}
						//npc不必做广播
						if(monster.horizonPlayer.contains(webServer)){
							monster.horizonPlayer.remove(webServer);
						}
					}
				}
				//如果这个删除列表里面有玩家在视野里，删除
				for(int m = 0;m < removeUserArraySize;m++){
					WebServer removeWebServer = removeUserArray.get(m);
					if(monster.horizonPlayer.contains(removeWebServer)){
						monster.horizonPlayer.remove(removeWebServer);
					}
				}
				sortPlayerHorizonThing(monster,sceneData);
			}
			for(int i = 0;i < size;i++){
				WebServer webServer = sceneData.userList.get(i);
				//如果这个删除列表里面有玩家在视野里，删除
				for(int m = 0;m < removeMonsterArraySize;m++){
					Monster removeWebServer = removeMonsterArray.get(m);
					if(webServer.horizonNpc.contains(removeWebServer)){
						webServer.horizonNpc.remove(removeWebServer);
						thingOutGame(webServer,removeWebServer);
					}
				}
				sortNpcHorizonThing(webServer,sceneData);
			}
			
			//npc视野里的npc
			for(int i = 0;i < npcSize;i++){
				Monster webServer = sceneData.npcList.get(i);
				for(int j = i+1;j<npcSize;j++){
					Monster webServer1 = sceneData.npcList.get(j);
					Point3D postionClone = webServer.position.clone();
					postionClone.y = 0.0;
					Point3D postionClone1 = webServer1.position.clone();
					postionClone1.y = 0.0;
					Point2D.Double point = IsoUtils.isoToScreen(postionClone);
					Point2D.Double point1 = IsoUtils.isoToScreen(postionClone1);
					double distance = Point.distance(point.x,point.y,point1.x,point1.y);
					//设置距离对应表
					sceneData.distanceMap.put(webServer.getServerId() < webServer1.getServerId() ? webServer.getServerId()+"_"+webServer1.getServerId() : webServer1.getServerId()+"_"+webServer.getServerId(), distance);
					//可以广播
					if(distance <= viewScope || !isAreaBroadcast){
						if(!webServer.horizonNpc.contains(webServer1)){
							webServer.horizonNpc.add(webServer1);
							
						}
						if(!webServer1.horizonNpc.contains(webServer)){
							webServer1.horizonNpc.add(webServer);
							
						}
						
					//不可以广播	
					}else if(distance > viewScope+removeExpand){
						if(webServer.horizonNpc.contains(webServer1)){
							webServer.horizonNpc.remove(webServer1);
							
							
						}
						if(webServer1.horizonNpc.contains(webServer)){
							webServer1.horizonNpc.remove(webServer);
							
							
						}
					}
					
				}
				//如果这个删除列表里面有玩家在视野里，删除
				for(int m = 0;m < removeMonsterArraySize;m++){
					Monster removeWebServer = removeMonsterArray.get(m);
					if(webServer.horizonNpc.contains(removeWebServer)){
						webServer.horizonNpc.remove(removeWebServer);
						
					}
				}
				sortNpcHorizonThing(webServer,sceneData);
			}
		}
		/**
		 * 对player视野里面的事物进行距离排序
		 * @param webServer
		 */
		public void sortNpcHorizonThing(ActivityThing player,SceneData sceneData){
			int size = player.horizonNpc.size();
			for(int i = 0;i < size;i++){
				
				for(int j = i+1;j<size;j++){
					Monster webServer = player.horizonNpc.get(i);
					double distance = sceneData.distanceMap.get(player.getServerId() < webServer.getServerId() ? player.getServerId()+"_"+webServer.getServerId() :  webServer.getServerId()+"_"+player.getServerId());
					
					Monster webServer1 = player.horizonNpc.get(j);
					double distance1 = sceneData.distanceMap.get(player.getServerId() < webServer1.getServerId() ? player.getServerId()+"_"+webServer1.getServerId() :  webServer1.getServerId()+"_"+player.getServerId());
					//交换位置
					if(distance > distance1){
						player.horizonNpc.set(i, webServer1);
						player.horizonNpc.set(j, webServer);
					}
					
				}
			}
		}
		/**
		 * 对player视野里面的事物进行距离排序
		 * @param webServer
		 */
		public void sortPlayerHorizonThing(ActivityThing player,SceneData sceneData){
			int size = player.horizonPlayer.size();
			for(int i = 0;i < size;i++){
				
				for(int j = i+1;j<size;j++){
					WebServer webServer = player.horizonPlayer.get(i);
					double distance = sceneData.distanceMap.get(player.getServerId() < webServer.getServerId() ? player.getServerId()+"_"+webServer.getServerId() :  webServer.getServerId()+"_"+player.getServerId());
					
					WebServer webServer1 = player.horizonPlayer.get(j);
					double distance1 = sceneData.distanceMap.get(player.getServerId() < webServer1.getServerId() ? player.getServerId()+"_"+webServer1.getServerId() :  webServer1.getServerId()+"_"+player.getServerId());
					//交换位置
					if(distance > distance1){
						player.horizonPlayer.set(i, webServer1);
						player.horizonPlayer.set(j, webServer);
					}
					
				}
			}
		}
		//通知玩家离开场景
		public void outScene(WebServer webServer){
			webServer.isLoadOk = false;
			TcpPacket pt = new TcpPacket(cyou.akworld.OpCodeEx.OUT_SCENE_S);
			webServer.getSession().send(pt);
			
			
		}
		//告诉玩家，这帮人离开的场景
		/**public void outGame(WebServer webServerSelf,ArrayList<WebServer> userList) {
			int userListSize = userList.size();
			for(int i = 0; i < userListSize; i++){
				WebServer webServer1 = userList.get(i);
				thingOutGame(webServerSelf,webServer1);
				
			}
		}**/
		//事物离开场景
		public void thingOutGame(WebServer webServerSelf,ActivityThing webServer1){
			TcpPacket pt = new TcpPacket(cyou.akworld.OpCodeEx.THING_OUTGAME_S);
			pt.putInt(webServer1.getServerId());
			webServerSelf.getSession().send(pt);
		}
		//事物进入场景
		public void thingEnterGame(WebServer webServerSelf,ActivityThing webServer){
			TcpPacket pt = new TcpPacket(cyou.akworld.OpCodeEx.THING_ENTERGAME_S);
			pt.putInt(webServer.getServerId());
			//如果是怪物，传一个怪物类型
			//if(webServer.getClass() == Monster.class){
			//	Monster monster = (Monster)webServer;
				pt.putInt(webServer.monster.getInt("id"));
			//}
			pt.putDouble(webServer.position.x);
			pt.putDouble(webServer.position.y);
			pt.putDouble(webServer.position.z);
			pt.put(webServer.dir);
			pt.putDouble(webServer.moveV);
			pt.putDouble(webServer.jumpVerticalV);
			
			pt.putDouble(webServer.jumpVerticalA);
			pt.putDouble(webServer.attackSpeed);
			pt.putInt(webServer.getHp());
			pt.putInt(webServer.getMaxHp());
			pt.putInt(webServer.getAtt());
			pt.putInt(webServer.getDef());
			pt.put(webServer.isDead ? 1:0);
			pt.putInt(webServer.camp == null ? 0 : webServer.camp.getInt("id"));
			//是不是敌人
			if(webServer.camp == null || webServer.camp != null && webServerSelf.camp.getInt("id") != webServer.camp.getInt("id")){
				pt.put(1);
			}else{
				pt.put(0);
			}
			webServerSelf.getSession().send(pt);
			
			//广播buff
			boardCastBuff(webServerSelf,webServer);
			
			if(webServer.isWalk){
				TcpPacket pt1 = new TcpPacket(cyou.akworld.OpCodeEx.THING_MOVE_S);
				pt1.putInt(webServer.getServerId());
				pt1.putDouble(webServer.position.x);
				pt1.putDouble(webServer.position.y);
				pt1.putDouble(webServer.position.z);
				pt1.put(webServer.dir);
				if(webServer._path != null){
					int pathSize = webServer._path.size();
					pt1.putInt(pathSize);
					for(int j = 0;j<pathSize;j++){
						Node node = webServer._path.get(j);
						pt1.putInt(node.x);
						pt1.putInt(node.y);
					}
				}else{
					pt1.putInt(0);
				}
				if(webServer.nowNode != null){
					pt1.putInt(webServer.nowNode.x);
					pt1.putInt(webServer.nowNode.y);
				}
				webServerSelf.getSession().send(pt1);
			}
			if(webServer.getClass() == MonsterNormal.class){
				//如果这个怪物，正在追踪人也广播
				if(((MonsterNormal)webServer).target != 0){
					TcpPacket pt1 = new TcpPacket(cyou.akworld.OpCodeEx.MONSTER_FOLLOW_TARGET_S);
					pt1.putInt(webServer.getServerId());
					pt1.putDouble(webServer.position.x);
					pt1.putDouble(webServer.position.y);
					pt1.putDouble(webServer.position.z);
					pt1.put(webServer.dir);
					pt1.putInt(((MonsterNormal)webServer).target);
					webServerSelf.getSession().send(pt1);
				}
				//如果这个怪，正在回到初始点也广播
				if(((MonsterNormal)webServer).isBack){
					TcpPacket pt1 = new TcpPacket(cyou.akworld.OpCodeEx.MONSTER_GO_BACK_S);
					pt1.putInt(webServer.getServerId());
					pt1.putDouble(webServer.position.x);
					pt1.putDouble(webServer.position.y);
					pt1.putDouble(webServer.position.z);
					pt1.put(webServer.dir);
					pt1.putInt(((MonsterNormal)webServer).getInitialNode().x);
					pt1.putInt(((MonsterNormal)webServer).getInitialNode().y);
					webServerSelf.getSession().send(pt1);
				}
			}
		}
		//告诉玩家 这帮人进入了场景，如果在走路，把走路也广播一下
		/**public void broadCastAllThing(WebServer webServerSelf,ArrayList<WebServer> userList){
			int userListSize = userList.size();
			for(int i = 0; i < userListSize; i++){
				WebServer webServer = userList.get(i);
				if(webServerSelf.getServerId() != webServer.getServerId()){
					thingEnterGame(webServerSelf,webServer);
				}
			}
		}**/
		
		

		public void enterScene(WebServer webServer){
			//给登录的人发送自己的方向，坐标
			TcpPacket pt = new TcpPacket(cyou.akworld.OpCodeEx.ENTER_SCENE_S);
			pt.putInt((int)Math.round(webServer.position.x/32));
			pt.putInt((int)Math.round(webServer.position.z/32));
			pt.put(webServer.dir);
			pt.putDouble(webServer.moveV);
			pt.putDouble(webServer.jumpVerticalV);
			
			pt.putDouble(webServer.jumpVerticalA);
			pt.putDouble(webServer.attackSpeed);
			pt.putInt(webServer.getHp());
			pt.putInt(webServer.getMaxHp());
			pt.putInt(webServer.getAtt());
			pt.putInt(webServer.getDef());
			pt.put(webServer.isDead ? 1:0);
			pt.putInt(webServer.getServerId());
			pt.putInt(webServer.monster.getInt("id"));
			pt.putInt(webServer.sceneData.sceneId);
			pt.putInt(webServer.camp.getInt("id"));
			webServer.getSession().send(pt);
			boardCastBuff(webServer,webServer);
		}
		public void boardCastDead(WebServer webServer){
			TcpPacket pt1 = new TcpPacket(cyou.akworld.OpCodeEx.DEAD_S);
			pt1.putInt(webServer.getServerId());
			int horizonThingSize = webServer.horizonPlayer.size();
			for(int m = 0;m < horizonThingSize;m++){
				WebServer webServer2 = webServer.horizonPlayer.get(m);
				//视野里的人，不一定在这个场景，可能已经换场景了，需要下一个tick才能移除视野
				if(webServer2.sceneData != null && webServer2.sceneData.sceneId == webServer.sceneData.sceneId){
					webServer2.getSession().send(pt1);
				}
			}
			webServer.getSession().send(pt1);
		}
		public void boardCastLife(WebServer webServer){
			TcpPacket pt1 = new TcpPacket(cyou.akworld.OpCodeEx.LIFE_S);
			pt1.putInt(webServer.getServerId());
			pt1.putInt(webServer.getHp());
			int horizonThingSize = webServer.horizonPlayer.size();
			for(int m = 0;m < horizonThingSize;m++){
				WebServer webServer2 = webServer.horizonPlayer.get(m);
				//视野里的人，不一定在这个场景，可能已经换场景了，需要下一个tick才能移除视野
				if(webServer2.sceneData != null && webServer2.sceneData.sceneId == webServer.sceneData.sceneId){
					webServer2.getSession().send(pt1);
				}
				
			}
			webServer.getSession().send(pt1);
		}
		public void boardCastBuff(WebServer webServer,ActivityThing thing){
			long time = System.currentTimeMillis();
			Object[] keyArray = thing.buffList.keySet().toArray();
			ArrayList<BuffInfo> addList = new ArrayList<BuffInfo>();
			for(int i = 0;i<keyArray.length;i++){
				int key = Integer.valueOf(String.valueOf(keyArray[i]));
				BuffArray buffArray = thing.buffList.get(key);
				if(buffArray != null){
					for(int j = 0;j < buffArray.buffArray.size();j++){
						BuffInfo buffInfo = buffArray.buffArray.get(j);
						addList.add(buffInfo);
					}
				}
			}
			if(addList.size() > 0){
				TcpPacket pt2 = new TcpPacket(cyou.akworld.OpCodeEx.ADD_BUFF_S);
				pt2.putInt(thing.getServerId());
				pt2.putInt(addList.size());
				for(int i = 0;i<addList.size();i++){
					pt2.putInt(addList.get(i).buffData.getInt("id"));
					pt2.putInt(addList.get(i).serverId);
					int duration = (int)(time-addList.get(i).startTime);
					//这里是可能的，等到之后的轮训自动清除buff就可以了
					if(duration < 0){
						duration = 0;
					}
					pt2.putInt(duration);
				}
				webServer.getSession().send(pt2);
			}
		}
	}

}
