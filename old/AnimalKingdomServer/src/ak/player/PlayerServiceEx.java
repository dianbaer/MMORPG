package ak.player;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ak.battle.BattleSupportData;
import ak.log.AkLogService;
import ak.mail.IAkMailService;
import ak.mail.MailEx;
import ak.market.MarketService;
import ak.notice.AkNoticeService;
import ak.playerSns.PlayerSns;
import ak.playerSns.PlayerSnsService;
import ak.server.ErrorHandlerEx;
import ak.server.LogEx;
import ak.util.SRWLock;
import ak.world.WorldManagerEx;
import cyou.mrd.ObjectAccessor;
import cyou.mrd.Platform;
import cyou.mrd.account.Account;
import cyou.mrd.account.Account.ResetImoneyRet;
import cyou.mrd.account.AccountService;
import cyou.mrd.data.Data;
import cyou.mrd.data.DataKeys;
import cyou.mrd.entity.Player;
import cyou.mrd.event.Event;
import cyou.mrd.event.GameEvent;
import cyou.mrd.event.OPEvent;
import cyou.mrd.game.actor.Actor;
import cyou.mrd.game.actor.ActorCacheService;
import cyou.mrd.io.OP;
import cyou.mrd.io.OPHandler;
import cyou.mrd.io.Packet;
import cyou.mrd.io.http.HOpCode;
import cyou.mrd.io.http.HSession;
import cyou.mrd.io.http.JSONPacket;
import cyou.mrd.io.tcp.HOpCodeEx;
import cyou.mrd.lock.LoginMidLockService;
import cyou.mrd.service.HarmoniousService;
import cyou.mrd.service.PlayerService;
import cyou.mrd.service.VersionService;
import cyou.mrd.util.ErrorHandler;
import cyou.mrd.util.IdUtil;
import cyou.mrd.util.RunTimeMonitor;
import cyou.mrd.util.Time;
import cyou.mrd.util.Utils;
import cyou.mrd.util.Utils.CheckNameState;
import cyou.mrd.world.WorldManager;

@OPHandler(TYPE = OPHandler.HTTP_EVENT)
public class PlayerServiceEx extends PlayerService {
	private static final Logger log = LoggerFactory.getLogger(PlayerServiceEx.class);

	private static final int RECOMMEND_NUM_LIMIT = 12;//邀请码可用次数限制。
	

	private static final int TAPJOY = Platform.getConfiguration().getInt("tapjoy");

	//public TIntIntMap accountIdPlayerIdMap = new TIntIntHashMap();
	public Map<String, LoginCheckFromWorldLock> loginCheckFromWorldLockMap = new ConcurrentHashMap<String, LoginCheckFromWorldLock>();
	public Map<Integer, SRWLock> initPlayerFromWorldLockMap = new ConcurrentHashMap<Integer, SRWLock>();
	
	/**
	 * 爱心值初始值
	 */
	public static final int LOVE_INITIAL_VALUE = Platform.getConfiguration().getInt("love_initial_value");
	/**
	 * 是否通过world服务器验证一下
	 */
	public static final int IS_NEED_WORLD_CHECK_LOGIN = Platform.getConfiguration().getInt("is_need_world_check_login");
	/**
	 * 是否通过world初始化player的memcached
	 */
	public static final int IS_THROUGH_WORLD_INIT_PLAYER_MEMCACHED = Platform.getConfiguration().getInt("is_through_world_init_player_memcached");

	@OPEvent(eventCode = GameEvent.EVENT_PLAYER_LOGOUTED)
	protected void playerLogoutEvent(Event event) {
		super.playerLogoutEvent(event);
		final PlayerEx player = (PlayerEx) event.param1;
		//增加登出log
		AkLogService.printLogout(player.clone());
	}

	@OPEvent(eventCode = GameEvent.EVENT_PLAYER_CHANGEED)
	protected void playerChangeEvent(Event event) {
		super.playerChangeEvent(event);
	}

	@OPEvent(eventCode = GameEvent.EVENT_PLAYER_CHANGEED_FORCE)
	protected void playerChangeEventForce(Event event) {
		super.playerChangeEventForce(event);
	}

	/**
	 * 
	 * @param accountId
	 * @return
	 */
	public int getPlayerIdByAccountId(int accountId) {
		//log.info("[login] [getPlayerIdByAccountId] accountId:{}", accountId);
		//int playerId = accountIdPlayerIdMap.get(accountId);
		//if (playerId == accountIdPlayerIdMap.getNoEntryValue()) {
			//从数据库查出数据，如果没有直接返回空，如果有返回缓存里面的信息，不保存到缓存
			PlayerEx player = Platform.getEntityManager().fetch("from PlayerEx where accountId = ? and exist = 0",
					accountId);
			if (player == null) {
				return 0;
			}
			//accountIdPlayerIdMap.put(accountId, player.getInstanceId());
			//log.info("[login] [accountIdPlayerIdMap] new entry:  account={}->addPlayer={}", accountId, player.getInstanceId());
			return player.getInstanceId();
		//}
		//return playerId;
	}
	/**
	 * 需要阻塞
	 * @param id
	 * @return
	 */
	public synchronized SRWLock getInitPlayerLock(int id){
		SRWLock sRWLock = null;
		if(initPlayerFromWorldLockMap.get(id) == null){
			sRWLock = new SRWLock();
			initPlayerFromWorldLockMap.put(id, sRWLock);
		}
		sRWLock = initPlayerFromWorldLockMap.get(id);
		synchronized (sRWLock) {
			sRWLock.setBack(false);
		}
		return sRWLock;
	}
	public void callBackInitPlayerLock(int id){
		SRWLock sRWLock = getInitPlayerLock(id);
		synchronized (sRWLock) {
			sRWLock.setBack(true);
			sRWLock.notify();
		}
	}
	/**
	 * 第一步从DataCenter中读取, 没有读到测从数据库中读取
	 * 
	 * @param playerId
	 * @return player 或 null
	 */
	public PlayerEx loadPlayer(int playerId) {
		PlayerEx player = null;
		player = (PlayerEx)ObjectAccessor.getPlayer(playerId);
		if(player == null){
			Data data = Platform.dataCenter().getData(DataKeys.playerKey(playerId));
			
			if (data == null) {
				//只要保证这份数据只有该在线玩家可以进行修改，
				//这块不会出问题的，如果同时并发的查询，当在线玩家修改数据之后，该玩家的好友sendNewData，虽然memcached的数据是一个旧的数据
				//但是当该在线玩家保存数据的时候，memcached还会更新这份数据的，这种情况memached的这份数据是旧的不会影响数据安全的
				//只有可能在服务器不正常关闭的时候，那个玩家的数据没有被保存上，memcached的数据的旧数据反而成了最新的数据
				//虽然降低了第一次的效率，但是保证了数据的最新，初始化一次之后，就不会在走这里了
				if(IS_THROUGH_WORLD_INIT_PLAYER_MEMCACHED == 1){
					//进行world服务器验证操作
					SRWLock sRWLock = getInitPlayerLock(playerId);
					//去world服务器验证mid
					if(Platform.worldServer() != null){
						WorldManagerEx wmanager = (WorldManagerEx)Platform.getAppContext().get(WorldManager.class);
						wmanager.initPlayerMemcached(playerId);
					}else{
						return null;
					}
					synchronized (sRWLock) {
						try {
							if(!sRWLock.isBack()){
								sRWLock.wait(30000);
							}
						} catch (Exception e) {
							log.info("PlayerServiceEx.loadPlayer:error[through world init player memcached error]");
						}
					}
					data = Platform.dataCenter().getData(DataKeys.playerKey(playerId));
					if(data == null){
						return null;
					}else{
						return (PlayerEx) data.value;
					}
				}else{
					player = Platform.getEntityManager().find(PlayerEx.class, playerId);
					if (player != null) {
						Platform.dataCenter().sendNewData(DataKeys.playerKey(playerId), player);
					}
					return player;
				}
				
			} else {
				
				return (PlayerEx) data.value;
			}
		}
		return player;
	}
	public Object[] loadPlayer(int playerId,boolean showType) {
		Object[] jsObj = new Object[2];
		PlayerEx player = null;
		player = (PlayerEx)ObjectAccessor.getPlayer(playerId);
		if(player == null){
			Data data = Platform.dataCenter().getData(DataKeys.playerKey(playerId));
			
			if (data == null) {
				//数据库再放入缓存返回
				if(IS_THROUGH_WORLD_INIT_PLAYER_MEMCACHED == 1){
					//进行world服务器验证操作
					SRWLock sRWLock = getInitPlayerLock(playerId);
					//去world服务器验证mid
					if(Platform.worldServer() != null){
						WorldManagerEx wmanager = (WorldManagerEx)Platform.getAppContext().get(WorldManager.class);
						wmanager.initPlayerMemcached(playerId);
					}else{
						jsObj[0]= null;
						jsObj[1]= 2;
						return jsObj;
					}
					synchronized (sRWLock) {
						try {
							if(!sRWLock.isBack()){
								sRWLock.wait(30000);
							}
						} catch (Exception e) {
							log.info("PlayerServiceEx.loadPlayer:error[through world init player memcached error]");
						}
					}
					data = Platform.dataCenter().getData(DataKeys.playerKey(playerId));
					if(data == null){
						jsObj[0]= null;
						jsObj[1]= 2;
						return jsObj;
					}else{
						log.info("PlayerServiceEx.loadPlayer:info[ world init player memcached finish]");
						jsObj[0]= (PlayerEx) data.value;
						jsObj[1]= 2;
						return jsObj;
					}
				}else{
					player = Platform.getEntityManager().find(PlayerEx.class, playerId);
					if (player != null) {
						Platform.dataCenter().sendNewData(DataKeys.playerKey(playerId), player);
					}
					jsObj[0]= player;
					jsObj[1]= 3;
					return jsObj;
				}
			} else {
				jsObj[0]= data.value;
				jsObj[1]= 2;
				
				return jsObj;
			}
		}
		jsObj[0]= player;
		jsObj[1]= 1;
		return jsObj;
	}
	@OP(code = HOpCode.PLAYER_NICKNAME_CLIENT)
	public void nickname(Packet packet, HSession session) throws Exception {
		log.info("[HTTPRequest] session ={} packet:{}", session.getSessionId(), packet.toString());
		Player player = (Player) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			return;
		}
		String name = packet.getString("name");
		CheckNameState cns = Utils.checkUserName(name);
		if (cns.sucess) {
			LogEx.changeName(player.getId(), player.getName(), name);
			name = Platform.getAppContext().get(HarmoniousService.class).filterBadWords(name);
			log.info("[nickname] palyerId:{}, oldName:{}->newName{}", new Object[] { player.getId(), player.getName(), name });
			player.setName(name);
			player.notifySave();

			Packet pt = new JSONPacket(HOpCode.PLAYER_NICKNAME_SERVER);
			pt.put("state", "OK");
			session.send(pt);
		} else {
			ErrorHandler.sendErrorMessage(session, cns.errorCode, packet.getopcode());
			return;
		}
		log.info("[METHODEND] return[null]");
	}
	/**
	 * 不需要阻塞，因为mid不能并发请求
	 * @param mid
	 * @return
	 */
	public LoginCheckFromWorldLock getLoginCheckFromWorldLock(String mid){
		LoginCheckFromWorldLock loginCheckFromWorldLock = null;
		if(loginCheckFromWorldLockMap.get(mid) == null){
			loginCheckFromWorldLock = new LoginCheckFromWorldLock();
			loginCheckFromWorldLockMap.put(mid, loginCheckFromWorldLock);
		}
		loginCheckFromWorldLock = loginCheckFromWorldLockMap.get(mid);
		loginCheckFromWorldLock.setState(-1);
		//这里也不需要阻塞，上层同一个mid不能并发请求
		loginCheckFromWorldLock.setBack(false);
		return loginCheckFromWorldLock;
	}
	/**
	 * 回调
	 * @param mid
	 */
	public void callBackLoginCheckFromWorldLock(String mid, int state){
		LoginCheckFromWorldLock loginCheckFromWorldLock = getLoginCheckFromWorldLock(mid);
		loginCheckFromWorldLock.setState(state);
		synchronized (loginCheckFromWorldLock) {
			loginCheckFromWorldLock.setBack(true);
			loginCheckFromWorldLock.notify();
		}
	}
	
	
	@OP(code = HOpCode.PLAYER_LOGIN_CLIENT)
	public void login(Packet packet, HSession session){
		LoginMidLockService loginMidLock = Platform.getAppContext().get(LoginMidLockService.class);
		String lockMid = "";
		try {
			RunTimeMonitor rt = packet.getRunTimeMonitor();
			//version检测
			if (packet.containsKey("version")) {
				String version = packet.getString("version");
				VersionService versionService = Platform.getAppContext().get(VersionService.class);
				if (versionService.notAllowAbleVision(version)) {
					ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_27, packet.getopcode());
					log.info("PlayerServiceEx.login:error[version is not allow:{} or VersionService throwable]", version);
					return;
				}
			}
			String mid = "";
			//mid检测
			if(packet.containsKey("mid")){
				mid = packet.getString("mid").replaceAll(":", "");
			}
			if (mid == null || mid == "" || mid.equals("") || mid.length() == 0 || mid.equalsIgnoreCase("UnKnown") || mid.indexOf('\'') != -1 || mid.indexOf('\"') != -1 || mid.indexOf("&") != -1) {
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_24, packet.getopcode());
				log.info("PlayerServiceEx.login:error[mid is not allow:{}]",mid);
				return;
			}
			lockMid = mid;
			//id检测
			int accountId = 0;
			if (packet.containsKey("id")) {
				if (Platform.getConfiguration().getBoolean("ID_ENCODE")) {
					accountId = IdUtil.deCode(packet.getString("id"));
				} else {
					accountId = packet.getInt("id");
				}
				if (accountId < 1) {
					ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_25, packet.getopcode());
					log.info("PlayerServiceEx.login:error[id is not allow:{}]",accountId);
					return;
				}
			}
			//加锁，一个mid同时只能进行一次操作
			try {
				loginMidLock.lock(lockMid);
			} catch (Exception e) {//锁失败或者超时
				ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_111, packet.getopcode());
				return;
			}
			if(IS_NEED_WORLD_CHECK_LOGIN == 1){
				rt.knock("world check start");
				//进行world服务器验证操作
				LoginCheckFromWorldLock loginCheckFromWorldLock = getLoginCheckFromWorldLock(lockMid);
				//去world服务器验证mid
				if(Platform.worldServer() != null){
					WorldManagerEx wmanager = (WorldManagerEx)Platform.getAppContext().get(WorldManager.class);
					wmanager.checkMid(lockMid);
				}else{
					ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_127, packet.getopcode());
					log.info("PlayerServiceEx.login:error[worldServer==null]");
					return;
				}
				
				synchronized (loginCheckFromWorldLock) {
					try {
						if(!loginCheckFromWorldLock.isBack()){
							loginCheckFromWorldLock.wait(30000);
						}
					} catch (Exception e) {
						log.info("PlayerServiceEx.login:error[player login to world wait time out]");
					}
					
				}
				//world服务器无返回
				if(loginCheckFromWorldLock.getState() == -1){
					ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_126, packet.getopcode());
					log.info("PlayerServiceEx.login:error[login timeout]");
					return;
				//正在别的服务器进行登录操作
				}else if(loginCheckFromWorldLock.getState() == 0){
					ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_125, packet.getopcode());
					log.info("PlayerServiceEx.login:error[mid:{} on other server login...]",lockMid);
					return;
				}
				//验证成功，可以登录
				rt.knock("world check end");
			}
			
			
			PlayerEx player = null;
			Account account;
			if (accountId > 0) {// 登录包
				//查出account并放入缓存，此account并不一定是最新的，这份数据只有在从数据库里查出player的时候才会使用，放入player里
				//如果不使用这份数据，后续的更新account会把这份数据覆盖掉，没有问题
				account = Platform.getEntityManager().find(Account.class, accountId);
				if (account == null || account.getMid().indexOf(mid) == -1) {// 找不到account.或者mid不匹配
					ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_25, packet.getopcode());
					log.info("PlayerServiceEx.login:error[mid is not match:{} or account==null]",mid);
					return;
				}
				Object obj = checkPlayerByCachaAndDb(account,session,packet);
				
				if (obj == null) {
					ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_25, packet.getopcode());
					log.info("PlayerServiceEx.login:error[player==null]");
					return;
				}else if(obj instanceof PlayerEx){
					player = (PlayerEx)obj;
				}else{
					return;
				}
				
				
				player.setLastLoginTime(new Date());
				
				initPlayerTongji(player, packet);
				try {
					setNotify(player);
				} catch (Throwable e) {
					log.error("PlayerServiceEx.login:error[setNotify false]",e);
				}
				
				PlayerSnsService psService = Platform.getAppContext().get(PlayerSnsService.class);
				psService.initPlayerSns(player);
				
				Packet pt = new JSONPacket(HOpCode.PLAYER_LOGIN_SERVER);
				pt.put("AD", getADByPlat(player.getDevice()));
				pt.put("mail", getPlayerMailCount(player));
				if (Platform.getConfiguration().getBoolean("ID_ENCODE")) {
					pt.put("id", IdUtil.enCode(player.getAccount().getId()));
				} else {
					pt.put("id", player.getAccount().getId());
				}
				pt.put("playerId", player.getId());
				MarketService marketService = Platform.getAppContext().get(MarketService.class);
				pt.put("marketStatus", marketService.marketIsRecover(player.getId()));
				pt.put("love", player.getLove());
				fillInAppendInfoToPacket(packet, pt, player);
				session.send(pt);
			}else{
				boolean isFirst = false;
				boolean isBackProcess = false;
				boolean isNewProcess = false;
				int loginType = -1;
				if (packet.containsKey("loginType")) {
					loginType = packet.getInt("loginType");
					isFirst = loginType == 0;
					isBackProcess = loginType == 1;
					isNewProcess = loginType == 2;
				}

				if (isFirst) {
					//直接从数据库里面查个数
					int mCount = (int) Platform.getEntityManager().count("select count(*) from Account where mid like ?", mid+"%");
					if (mCount == 0) {
						// 没有存档. 直接注册并登录
						player = regAccountAndLogin(packet, session, mid);
						if (player == null) {// 没有成功的注册会加入error code; 直接返回.
							return;
						} else {
							Platform.getLog().role(player);
							Platform.getLog().register(player);
						}
					} else {
						log.error("login:error[mid:{} is have]",mid);
						ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_REG_HAS_OLD_PROCESS, packet.getopcode());
						return;
					}
				} else if (isBackProcess) {
					// 回档. 组mid. 并登录
					List<Account> accountList = Platform.getEntityManager().limitQuery("from Account where mid like ? order by id desc", 0, 1,
							mid + "%");
					if (accountList == null || accountList.isEmpty()) {
						log.error("login:error[mid:{} is not have]",mid);
						ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_25, packet.getopcode());
						return;
					}
					account = accountList.get(0);
					Object obj = checkPlayerByCachaAndDb(account,session,packet);
					
					if (obj == null) {
						ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_25, packet.getopcode());
						log.info("login:error[player==null]");
						return;
					}else if(obj instanceof PlayerEx){
						player = (PlayerEx)obj;
					}else{
						return;
					}

					player.setLastLoginTime(new Date());
					initPlayerTongji(player, packet);
					try {
						setNotify(player);
					} catch (Throwable e) {
						log.error("PlayerServiceEx.login:error[setNotify false]",e);
					}
					
					PlayerSnsService psService = Platform.getAppContext().get(PlayerSnsService.class);
					psService.initPlayerSns(player);
					
					Packet pt = new JSONPacket(HOpCode.PLAYER_LOGIN_SERVER);
					pt.put("AD", getADByPlat(player.getDevice()));
					pt.put("mail", getPlayerMailCount(player));
					
					if (Platform.getConfiguration().getBoolean("ID_ENCODE")) {
						pt.put("id", IdUtil.enCode(player.getAccount().getId()));
					} else {
						pt.put("id", player.getAccount().getId());
					}
					pt.put("playerId", player.getId());
					MarketService marketService = Platform.getAppContext().get(MarketService.class);
					pt.put("marketStatus", marketService.marketIsRecover(player.getId()));
					pt.put("love", player.getLove());
					pt.put("loginType", loginType);
					fillInAppendInfoToPacket(packet, pt, player);
					session.send(pt);
				} else if (isNewProcess) {
					// 新人. 删旧号.组mid. 注册并登录
					List<Account> accountList = Platform.getEntityManager().query("from Account where mid like ? order by id desc", mid + "%");
					mid = mid + "&" + accountList.size();
					account = accountList.get(0);
					Object obj = checkPlayerByCachaAndDb(account,session,packet);
					if (obj == null) {
						ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_25, packet.getopcode());
						log.info("login:error[player==null]");
						return;
					}else if(obj instanceof PlayerEx){
						player = (PlayerEx)obj;
					}else{
						return;
					}
					//把老用户设为删除
					player.setExist(1);
					player.notifySaveForce();
					//如果在这个服务器上并且在线，给他踢下线
					player = (PlayerEx)ObjectAccessor.getPlayer(player.getId());
					if(player != null){
						Platform.getEventManager().fireEvent(new Event(GameEvent.EVENT_PLAYER_LOGOUTED, player));
					}
					
					player = regAccountAndLogin(packet, session, mid);
					if (player == null) {// 没有成功的注册会加入error code; 直接返回.
						return;
					}else {
						Platform.getLog().role(player);
						Platform.getLog().register(player);
					}

				} else {
					ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_124, packet.getopcode());
					log.error("login:error[loginType:{}]",loginType);
					return;
				}
			}
			super.loginSucess(player, session, player.getAccount());
			if (packet.containsKey("version")) {
				String[] version = packet.getString("version").split("\\.");
				player.setClientVersionIntValue(Integer.parseInt(version[2]), Integer.parseInt(version[3]));
				player.setClientVersion(packet.getString("version"));
			}
			setGift(player);
			//if(IS_NEED_WORLD_CHECK_LOGIN == 1){
				//马上存入memcached里面
				savePlayer(player, false , false);
				//然后强制异步线程保存
			//}
			player.notifySaveForce();
			addLove(session,player);
		} catch (Throwable e) {
			log.error("login:error[Throwable]",e);
			ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_0, packet.getopcode());
		} finally {
			if(IS_NEED_WORLD_CHECK_LOGIN == 1){
				//通知world服务器该mid的登录流程已经走完，可以下一个相同的mid走这个流程了
				if(Platform.worldServer() != null){
					WorldManagerEx wmanager = (WorldManagerEx)Platform.getAppContext().get(WorldManager.class);
					wmanager.loginFinish(lockMid);
				}
			}
			if(lockMid != null && lockMid != "" && !lockMid.equals("") && lockMid.length() != 0){
				loginMidLock.unlock(lockMid);
			}
		}
		
	}
	private void setGift(PlayerEx player){
		if(player.getPool().getInt(PlayerEx.GET_GIFT_DAY) != Time.day){
			IAkMailService<MailEx> mailService = Platform.getAppContext().get(IAkMailService.class);
			String itemListStr = "197=1";
			JSONObject jsObj = new JSONObject();
			jsObj.put("award", itemListStr);
			mailService.sendSystemMailHaveGoods(player.getId(), jsObj.toString(), "每天登陆的奖励");
			player.getPool().setInt(PlayerEx.GET_GIFT_DAY, Time.day);
		}
	}
	/**
	 * loginType: 0是首次登录. 1是选择回档. 2是选择重新玩
	 */
	//@OP(code = HOpCode.PLAYER_LOGIN_CLIENT)
//	public void login(Packet packet, HSession session) throws Exception {
//		log.info("[login] ip:{} packet:{}", session.ip(), packet.toString());
//		RunTimeMonitor rt = packet.getRunTimeMonitor();
//		rt.knock("login");
//		
//		// 版本检测
//		if (packet.containsKey("version")) {
//			String version = packet.getString("version");
//			VersionService versionService = Platform.getAppContext().get(VersionService.class);
//			if (versionService.notAllowAbleVision(version)) {
//				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_27, packet.getopcode());
//				log.info("login:error[{}],version[{}]", "not support version", version);
//				return;
//			}
//		}
//		// mid 检测
//		String mid = packet.getString("mid").replaceAll(":", "");
//		if (mid == null || mid.length() == 0 || mid.equalsIgnoreCase("UnKnown") || mid.indexOf('\'') != -1 || mid.indexOf('\"') != -1) {
//			log.info("[midcheck] ip:{} login wrong.  mid:null", session.ip());
//			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_24, packet.getopcode());
//			return;
//		} else {
//			log.info("[midcheck] ip:{} login ok.  mid:{}", session.ip(), mid);
//			// int mCount = (int)
//			// Platform.getEntityManager().count("select count(*) from Account where mid = ?",
//			// mid);
//			// if(mCount == 0) {
//			// String mid2 = createMid2ByMid(mid);
//			// int mCount2 = (int)
//			// Platform.getEntityManager().count("select count(*) from Account where mid = ?",
//			// mid2);
//			// if(mCount2 > 0) {
//			// log.info("[midcheck] change mid:({}) to mid2({})", new
//			// Object[]{session.ip(), mid, mid2});
//			// mid = mid2;
//			// }
//			// }
//		}
//		rt.knock("check mid.");
//		
//		// accountId 检测
//		int accountId = -1;
//		if (packet.containsKey("id")) {
//			if (Platform.getConfiguration().getBoolean("ID_ENCODE")) {
//				accountId = IdUtil.deCode(packet.getString("id"));
//			} else {
//				accountId = packet.getInt("id");
//			}
//			if (accountId < 1) {
//				log.info("[login]ip:{} login wrong. accountId:{} -> idUtil:{}", new Object[] { session.ip(), packet.getString("id"),
//						accountId });
//				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_25, packet.getopcode());
//				return;
//			}
//		}
//		rt.knock("check ok.");
//
//		// go!
//		PlayerEx player;
//		Account account;
//		if (accountId > 0) {// 登录包
//			account = Platform.getEntityManager().findNoCache(Account.class, accountId);
//			rt.knock("find by accountId.");
//			if (account == null || account.getMid().indexOf(mid) == -1) {// 找不到account.或者mid不匹配
//				log.info("[login] ip:{} login wrong.  account == null", session.ip());
//				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_25, packet.getopcode());
//				return;
//			} else {
//				log.info("[login] ip:{} login ok. cause : getAccount By mid:{}, accountId:{}",
//						new Object[] { session.ip(), mid, account.getId() });
//			}
//			player = getPlayerByCachaAndDb(account);
//			rt.knock("getPlayerByCachaAndDb.");
//			if (player == null) {
//				log.info("[login] ip:{} login wrong. cause :not find playerId by accountId:{}", session.ip(), account.getId());
//				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_25, packet.getopcode());
//				return;
//			}
//			if (player.getLoginServerId() != -1) {
//				// 说明已经登陆过一台服务器了。 通知world， 强制让改服务器内的此玩家退出；
//			}
//			player.setAccount(account);
//			rt.knock("setAccount");
//			Date lastLoginTime = player.getLastLoginTime();
//			rt.knock("getLastLoginTime");
//			player.setLastLoginTime(new Date());
//			rt.knock("player.setLastLoginTime(new Date()) ");
//			initPlayerTongji(player, packet);
//			rt.knock("TongjiOK");
//			log.info("[login] Player Logined. ip:{}, playerId:{}, mid:{}, account({}), lastLoginTime:{}, online:{}",
//					new Object[] { session.ip(), player.getInstanceId(), mid, account, lastLoginTime, ObjectAccessor.size() });
//			Packet pt = new JSONPacket(HOpCode.PLAYER_LOGIN_SERVER);
//			pt.put("AD", getADByPlat(player.getDevice()));
//			pt.put("mail", getPlayerMailCount(player));
//			setNotify(player);
//			if (Platform.getConfiguration().getBoolean("ID_ENCODE")) {
//				pt.put("id", IdUtil.enCode(player.getAccount().getId()));
//			} else {
//				pt.put("id", player.getAccount().getId());
//			}
//			pt.put("playerId", player.getId());
//			MarketService marketService = Platform.getAppContext().get(MarketService.class);
//			pt.put("marketStatus", marketService.marketIsRecover(player.getId()));
//			pt.put("love", player.getLove());
//			fillInAppendInfoToPacket(packet, pt, player);
//			rt.knock("playerTransListLimitQuery");
//			session.send(pt);
//		} else { // 注册包
//			boolean isFirst = false;
//			boolean isBackProcess = false;
//			boolean isNewProcess = false;
//			int loginType = -1;
//			if (packet.containsKey("loginType")) {
//				loginType = packet.getInt("loginType");
//				isFirst = loginType == 0;
//				isBackProcess = loginType == 1;
//				isNewProcess = loginType == 2;
//			}
//
//			if (isFirst) {
//				// 检测是否有存档. 下发选择消息
//				int mCount = (int) Platform.getEntityManager().count("select count(*) from Account where mid = ?", mid);
//				rt.knock("select mCount:" + mCount);
//				if (mCount == 0) {
//					// 没有存档. 直接注册并登录
//					player = regAccountAndLogin(packet, session, mid);
//					rt.knock("regAccountAndLogin");
//					if (player == null) {// 没有成功的注册会加入error code; 直接返回.
//						log.info("[login] reg not Ok!");
//						return;
//					} else {
//						Platform.getLog().role(player);
//						Platform.getLog().register(player);
//						rt.knock("Log().role");
//					}
//				} else {
//					log.info("[login] ip:{} login wrong.  account == null", session.ip());
//					ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_REG_HAS_OLD_PROCESS, packet.getopcode());
//					return;
//				}
//			} else if (isBackProcess) {
//				// 回档. 组mid. 并登录
//				List<Account> accountList = Platform.getEntityManager().limitQuery("from Account where mid like ? order by id desc", 0, 1,
//						mid + "&%");
//				rt.knock("Query limit 1. accountList");
//				if (accountList == null || accountList.isEmpty()) {
//					// 没有找到相应的account记录. 回档失败.
//					accountList = Platform.getEntityManager().limitQuery("from Account where mid = ? order by id desc", 0, 1, mid);
//					if (accountList == null || accountList.isEmpty()) {
//						log.info("[login] ip:{} BackProcess wrong.  accountList == null, mid:{}", session.ip(), mid);
//						ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_25, packet.getopcode());
//						return;
//					}
//				}
//				account = accountList.get(0);
//				player = getPlayerByCachaAndDb(account);
//				rt.knock("isBackProcess getPlayerByCachaAndDb");
//				if (player == null) {
//					log.info("[login] ip:{} login wrong. cause :not find playerId by accountId:{}", session.ip(), account.getId());
//					ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_25, packet.getopcode());
//					return;
//				}
//				player.setAccount(account);
//				Date lastLoginTime = player.getLastLoginTime();
//				player.setLastLoginTime(new Date());
//				initPlayerTongji(player, packet);
//				log.info("[login] Player Logined. ip:{}, playerId:{}, mid:{}, account({}), lastLoginTime:{}, online:{}", new Object[] {
//						session.ip(), player.getInstanceId(), mid, account, lastLoginTime, ObjectAccessor.size() });
//				Packet pt = new JSONPacket(HOpCode.PLAYER_LOGIN_SERVER);
//				pt.put("AD", getADByPlat(player.getDevice()));
//				pt.put("mail", getPlayerMailCount(player));
//				setNotify(player);
//				if (Platform.getConfiguration().getBoolean("ID_ENCODE")) {
//					pt.put("id", IdUtil.enCode(player.getAccount().getId()));
//				} else {
//					pt.put("id", player.getAccount().getId());
//				}
//				pt.put("playerId", player.getId());
//				MarketService marketService = Platform.getAppContext().get(MarketService.class);
//				pt.put("marketStatus", marketService.marketIsRecover(player.getId()));
//				pt.put("love", player.getLove());
//				pt.put("loginType", loginType);
//				fillInAppendInfoToPacket(packet, pt, player);
//				rt.knock("playerTransListLimitQuery");
//				session.send(pt);
//			} else if (isNewProcess) {
//				// 新人. 删旧号.组mid. 注册并登录
//				List<Account> accountList = Platform.getEntityManager().query(Account.class, "from Account where mid like ?", mid + "&%");
//				List<Account> accountList2 = Platform.getEntityManager().query(Account.class, "from Account where mid = ?", mid);
//				accountList.addAll(accountList2);
//				rt.knock("Query limit all. accountList");
//				mid = mid + "&" + accountList.size();
//				for (Account ac : accountList) {
//					Player p = getPlayerByCachaAndDb(ac);
//					if (p != null) {
//						p.setExist(1);
//						p.notifySaveForce();
//						ObjectAccessor.removeGameObject(p);
//					}
//				}
//				rt.knock("set player exist = 1");
//				player = regAccountAndLogin(packet, session, mid);
//				if (player == null) {// 没有成功的注册会加入error code; 直接返回.
//					log.info("[login] reg not Ok!");
//					rt.knock("reg not Ok");
//					return;
//				}
//
//			} else {
//				// 旧协议
//				log.info("[login] old old old old old old old old  login opcode!");
//				//_oldLogin(packet, session, mid);
//				rt.knock("_oldLogin");
//				return;
//			}
//		}
//		super.loginSucess(player, session, player.getAccount());
//		if (packet.containsKey("version")) {
//			String[] version = packet.getString("version").split("\\.");
//			player.setClientVersionIntValue(Integer.parseInt(version[2]), Integer.parseInt(version[3]));
//			player.setClientVersion(packet.getString("version"));
//		}
//		
//		rt.knock("loginSucess");
//	}
	/**
	 * 设置通知信息
	 * @param player
	 * @throws Exception
	 */
	public void setNotify(PlayerEx player) throws Exception{
		//如果有未读的系统公告，设置为有通知
		AkNoticeService akNoticeService = Platform.getAppContext().get(AkNoticeService.class);
		if(akNoticeService.getUnReceiveSystemNotice(player).size() > 0){
			player.getPool().setInt(PlayerEx.NOTIFY_CLIENT_NOTICE_SYSTEM, PlayerEx.NOTIFY_CLIENT);
			
		}
		//获取未下载的收件箱
		IAkMailService<MailEx> service = Platform.getAppContext().get(IAkMailService.class);
		if(service.getPlayerMailListUnDownloadInbox(player).size() > 0){
			player.getPool().setInt(PlayerEx.NOTIFY_CLIENT_MAIL_INBOX, PlayerEx.NOTIFY_CLIENT);
		}
		//获取未下载的信息箱
		if(service.getPlayerMailListUnDownloadMessagebox(player).size() > 0){
			player.getPool().setInt(PlayerEx.NOTIFY_CLIENT_MAIL_MESSAGEBOX, PlayerEx.NOTIFY_CLIENT);
		}
	}

	private boolean inputRecommendCode(PlayerEx player, String recommendCode) throws Exception {
//		if(player.getRecommendCode() == -1) {
//			throw new Exception("邀请码已经使用成功");
//		}
//		if(player.getRecommendCode() != 0) {
//			throw new Exception("已经输入过邀请码了");
//		}
//		
//		int id = IdUtil.deCode(recommendCode);
//		if(id <= 0) {
//			return false;
//		}else {
//			//id -- > player is not vaild;
//			int playerId = getPlayerIdByAccountId(id);
//			if(playerId == 0) {
//				return false;
//			}else {
//				PlayerEx targetPlayer = loadPlayer(playerId);
//				if(targetPlayer == null){
//					return false;
//				}else {
//					int rNum = targetPlayer.getRecommendNum();
//					rNum++;
//					if(rNum > RECOMMEND_NUM_LIMIT) {
//						throw new Exception("邀请码失效");
//					}else {
//						targetPlayer.setRecommendNum(rNum);
//						targetPlayer.notifySave();
//						player.setRecommendCode(playerId);
//						DefaultMailService mailService = (DefaultMailService) Platform.getAppContext().get(MailService.class);
////邮件系统有改动，这个接口注掉
////						mailService.sendSystemMailNoFilter(player.getInstanceId(), targetPlayer.getInstanceId(),
////								MailTemplateStatus.MAIL_TEMPLATE_RECOMMEND_FRIEND, targetPlayer.getLang(), IdUtil.enCode(player.getAccountId()),
////								targetPlayer.getName(), MailUseType.MAIL_RECOMMEND_CODE);
//					}
//				}
//			}
//		}
//		
//		return true;
		return false;
	}

	private int getPlayerMailCount(Player player) {
//		// 显示是否有系统邮件
//		int count = (int) Platform.getEntityManager().count("select count(*) from MailEx where destId = ? and type = 1 and exist = 0",
//				player.getInstanceId());
//		// gm mail
//		if (count == 0) {
//			DefaultMailService service = (DefaultMailService) Platform.getAppContext().get(MailService.class);
//			if (service.hadNoReadSystemNotice(player)) {
//				count = 1;
//			}
//		}
//		return count;
		return 0;
	}

	/**
	 * mid是用mac地址为值. 但是客户端不保证什么时候发不带冒号的mac地址, 什么时候发带冒号的mac地址.
	 * 这个方法是为了生成一个相应的带或不带冒号的mid值
	 * 
	 * @param mid
	 * @return
	 */
	private String createMid2ByMid(String mid) {
		if (mid.length() < 3) {
			return mid;
		}
		if (mid.charAt(2) == ':') {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mid.length(); i++) {
				char ch = mid.charAt(i);
				if (ch != ':') {
					sb.append(ch);
				}
			}
			return sb.toString();
		} else {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mid.length(); i++) {
				char ch = mid.charAt(i);
				if (i != 0 && i != mid.length() - 1 && i % 2 == 0) {
					sb.append(':');
				}
				sb.append(ch);
			}
			return sb.toString();
		}
	}

	public PlayerEx getPlayerByCachaAndDb(Account account) {
		int playerId = getPlayerIdByAccountId(account.getId());
		if (playerId == 0) {
			return null;
		}
		PlayerEx player;// = (PlayerEx) ObjectAccessor.getPlayer(playerId);
//		if (player == null) {
			player = loadPlayer(playerId);
//		}
		return player;
	}
	public Object checkPlayerByCachaAndDb(Account account,HSession session,Packet packet){
		int playerId = getPlayerIdByAccountId(account.getId());
		if (playerId == 0) {
			return null;
		}
		PlayerEx player = null;
		Object[] jsObj = loadPlayer(playerId,true);
		int type = Integer.parseInt(jsObj[1].toString());
		player = (PlayerEx)jsObj[0];
		//从在线用户取到的player
		if(type == 1){
			if(player.getAccount() == null){
				player.setAccount(account);
				log.info("PlayerServiceEx.login:info[ObjectAccessor.PlayerEx.Account==null]");
			}
			return player;
		//从MemCached取到的player
		}else if(type == 2){
			if(player != null){
				if (player.getLoginServerId() != -1) {
					if(player.getLoginServerId() > 0){
						//在别的服务器登陆了，通知那个服务器把这个用户踢下线
						if(player.getLoginServerId() != Platform.getServerId()){
							ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_123, packet.getopcode());
							log.info("PlayerServiceEx.login:error[oldLoginServerId:{},nowLoginServerId:{}]",player.getLoginServerId(),Platform.getServerId());
							//通知玩家下线
							if(Platform.worldServer() != null){
								WorldManagerEx wmanager = (WorldManagerEx)Platform.getAppContext().get(WorldManager.class);
								wmanager.noticePlayerLogout(player.getId(),player.getLoginServerId());
							}else{
								log.info("PlayerServiceEx.login:error[worldServer==null]");
							}
							return 1;
						}else{
							log.info("PlayerServiceEx.login:info[MemCached.PlayerEx.getLoginServerId==Platform.getServerId()]");
						}
					}else{
						log.info("PlayerServiceEx.login:info[MemCached.PlayerEx.getLoginServerId<=0]");
					}
					
				}else {
					//可能在别的服务器已经下线但是数据并没有存储完成，稍后重试
					if(player.isDirty()){
						ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_122, packet.getopcode());
						log.info("PlayerServiceEx.login:error[now Save Player,player is dirty]");
						return 2;
					}
				}
				
				if(player.getAccount() == null){
					player.setAccount(account);
					log.info("PlayerServiceEx.login:info[MemCached.PlayerEx.Account==null]");
				}
			}
			return player;
		//从数据库取到的player
		}else{
			if(player != null){
				if(player.getAccount() == null){
					player.setAccount(account);
				}else{
					log.info("PlayerServiceEx.login:info[Mysql.PlayerEx.Account!=null]");
				}
			}
			return player;
		}
	}
	private PlayerEx regAccountAndLogin(Packet packet, HSession session, String mid) {
		
		Date now = new Date();
		AccountService accountService = Platform.getAppContext().get(AccountService.class);
		Account account = null;
		try {
			account = accountService.createAccount(mid);
		} catch (Throwable e) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_26, packet.getopcode());
			log.error("login:error[createAccount false,mid:{}]",mid);
			return null;
		}
		if (account == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_26, packet.getopcode());
			log.error("login:error[createAccount false,mid:{}]",mid);
			return null;
		}

		PlayerEx player = new PlayerEx();
		player.setAccountId(account.getId());
		player.setAccount(account);
		player.setCreateTime(now);
		player.setLevel(1);
		player.setLastLoginTime(now);
		player.setLove(LOVE_INITIAL_VALUE);//初始化爱心值为0，添加和使用由服务器控制
		player.setLastSynchInfoTime(now);
		Platform.getEntityManager().createSync(player);
		initPlayerTongji(player, packet);
		try {
			setNotify(player);
		} catch (Throwable e) {
			log.error("PlayerServiceEx.login:error[setNotify false]",e);
		}
		//设置所有推送已读
		AkNoticeService akNoticeService = Platform.getAppContext().get(AkNoticeService.class);
		akNoticeService.setInteractiveNoticeRead(player);
		
		//初始化玩家SNS互动记录表，与玩家表同时生成
		PlayerSns ps = new PlayerSns();
		ps.setPlayerId(player.getId());
		ps.setHelpCount(0);
		ps.setLastHelpDay(Time.day);
		ps.setTreeX(0);
		ps.setTreeY(0);
		ps.setTreeGrade(0);
		ps.setTreeStatus(PlayerSns.TREE_STATUS_INIT);
		ps.setVisitCount(0);
		ps.setVisitRecord("");
		ps.setLastHarvestTime((int)(now.getTime() / 1000));
		ps.setActiveCount(PlayerSnsService.ACTIVE_MAX_NUM);
		ps.setLastActiveTime((int)(now.getTime() / 1000));
		ps.setSendCount(0);
		ps.setLastSendDay(Time.day);
		Platform.getEntityManager().createSync(ps);	
		
		PlayerSnsService psService = Platform.getAppContext().get(PlayerSnsService.class);
		psService.initPlayerSns(player);
		
		//发送结果
		Packet pt = new JSONPacket(HOpCode.PLAYER_LOGIN_SERVER);
		pt.put("AD", getADByPlat(player.getDevice()));
		pt.put("mail", getPlayerMailCount(player));
		
		if (Platform.getConfiguration().getBoolean("ID_ENCODE")) {
			pt.put("id", IdUtil.enCode(player.getAccount().getId()));
		} else {
			pt.put("id", player.getAccount().getId());
		}
		pt.put("playerId", player.getId());
		MarketService marketService = Platform.getAppContext().get(MarketService.class);
		pt.put("marketStatus", marketService.marketIsRecover(player.getId()));
		pt.put("love", player.getLove());
		pt.put("transportInfo", new JSONArray());
		fillInAppendInfoToPacket(packet, pt, player);
		session.send(pt);
		return player;
	}

	/**
	 * 附加信息：1 运送小动物， 2 下发服务器时间, 3 下发推荐码返回结果, 4 下发支援的好友ID和建筑等级
	 * @param packet  
	 * @param pt
	 * @param player
	 */
	private void fillInAppendInfoToPacket(Packet packet, Packet pt, PlayerEx player) {
		fillTransInfo(pt, player);
		//客户端判定是否连续登陆， 下发服务器时间
		serverTime(pt, player);
		
		//推荐码.
		if (packet.containsKey("recommendCode")) {
			String recommendCode = packet.getString("recommendCode");
			if (recommendCode != null && recommendCode.length() > 0) {
				boolean rcRet;
				try {
					rcRet = inputRecommendCode(player, recommendCode);
					pt.put("recommendRet", rcRet);
					pt.put("hadRecommendRet", true);
				} catch (Exception e) {//已经输入过邀请码了. 对方超过12次使用。
					log.info("[recommendCode] recommend final.Cause: {}",e.getCause());
					pt.put("hadRecommendRet", false);
				}
			}else {
				pt.put("hadRecommendRet", false);
			}
		}else {
			pt.put("hadRecommendRet", false);
		}
		
		//是否有自己推荐的好友已经达到12级标准了
		if(player.getBerecommend12LvId() != 0) {
				pt.put("recommend12LvRet", player.getBerecommend12LvId());
				Player recommendPlayer = loadPlayer(player.getBerecommend12LvId());
				player.resetBerecommend12LvId();
				if(recommendPlayer == null) {
					pt.put("recommendName", "");
				}else {
					pt.put("recommendName", recommendPlayer.getName());
				}
		}
//		pt.put("recommendRet", true);
//		pt.put("recommend12LvRet",123);
		
		// 把支援的好友ID和支援建筑等级下发
		int friendID = 0;
		int buildlev = 0;
		String name = "";
		String icon = "";
		Data playerData = Platform.dataCenter().getData(DataKeys.battleSupportKey(player.getId()));
		if ( null != playerData )
		{
			BattleSupportData playerbattleSupportData = (BattleSupportData)playerData.value;
			if ( null != playerbattleSupportData )
			{
				friendID = playerbattleSupportData.getBattleSupportFriendID();
				buildlev = playerbattleSupportData.getBattleBuildingLevel();
				if ( friendID != 0 )
				{
					ActorCacheService acservice = Platform.getAppContext().get(ActorCacheService.class);
					Actor actor = acservice.findActor(friendID);
					if ( null != actor )
					{
						name = actor.getName();
						icon = actor.getIcon();
					}
					else
					{
						friendID = 0;
						buildlev = 0;
					}
				}
				else
				{
					friendID = 0;
					buildlev = 0;
				}
				playerbattleSupportData.setBattleBuildingLevel(0);
				playerbattleSupportData.setBattleSupportFriendID(0);
				playerData.value = playerbattleSupportData;
				Platform.dataCenter().sendData(DataKeys.battleSupportKey(player.getId()),playerData);
			}
		}
		pt.put("supportID", friendID);
		pt.put("supportBuildLev", buildlev);
		pt.put("supportName",name);
		pt.put("supportIcon", icon);
		
		//下发tapjoy的开关限制
		pt.put("tapjoy", TAPJOY);
	}

	private void fillTransInfo(Packet pt, Player player) {
		List<PlayerTransport> playerTransList = Platform.getEntityManager().limitQuery(
				"from PlayerTransport where playerId = ? and exist = 1", 0, 100, player.getInstanceId());
		ActorCacheService aservice = Platform.getAppContext().get(ActorCacheService.class);
		JSONArray trans = new JSONArray();
		if (playerTransList != null && playerTransList.size() > 0) {
			for (int i = playerTransList.size() - 1; i >= 0; i--) {
				PlayerTransport playerTrans = playerTransList.get(i);
				Actor actor = aservice.findActor(playerTrans.getFriendId());
				if (actor == null) {
					continue;
				}
				JSONObject temp = new JSONObject();
				temp.put("friendId", playerTrans.getFriendId());
				temp.put("friendIcon", actor.getIcon() == null ? "1" : actor.getIcon());
				temp.put("friendName", actor.getName() == null ? "" : actor.getName());
				temp.put("friendStar", actor.getStar());
				trans.add(temp);
				playerTrans.setExist(false);
				Platform.getEntityManager().updateSync(playerTrans);
			}
		}
		pt.put("transportInfo", trans);
		//log.info("[login] transportInfo playerId:{} put packet. transportInfo={}", player.getInstanceId(),  trans);
	}
	private void serverTime(Packet pt, PlayerEx player) {
		System.out.println("=================SERVER DAY=====================" + ((int)(System.currentTimeMillis()/1000L))/(24*60*60));
		pt.put("time", ((int)(System.currentTimeMillis()/1000L)));
	}
	@OP(code = HOpCodeEx.PLAYER_UPLOAD_CLIENT)
	public void uploadProcess(Packet packet, HSession session){
		try {
			PlayerEx player = (PlayerEx) session.client();
			if (player == null) {
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
				return;
			}
			if(!packet.containsKey("player")){
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_SYNC_FORMAT_ERROR, packet.getopcode());
				return;
			}
			JSONObject playerInfo = packet.getObject("player");
			int level = playerInfo.getInt("Level");
			if (level < player.getLevel() || level > PlayerEx.MaxLevel) {// 等级回退了:时间错误！
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_CLIENT_TIME_ERROR, packet.getopcode());
				return;
			}
			if (!packet.containsKey("Buildings")) {
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_SYNC_FORMAT_ERROR, packet.getopcode());
				return;
			}
			
			JSONObject syncDollar = playerInfo.getJSONObject("verify");
			// 客户端防作弊, 同步金钱
			ResetImoneyRet ret = verifyDollar(syncDollar, player);
			boolean needSyncDollar = false;
			if (ret != null) {
				if (!ret.result) {
					AkLogService.printDiamond(player.clone(), syncDollar);
					ErrorHandler.sendErrorMessage(session, ret.errorCode, packet.getopcode());
					return;
				}
				if(ret.isClientNotSyncDollar) {
					needSyncDollar = true;
				}
			}
			// 增加对多岛的支持
			int sceneId = 1;
			// 作弊检查
			if(player.getSceneOpenBlock(sceneId) != playerInfo.getInt("OpenBlock") && playerInfo.getInt("OpenBlock") == 0){
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_41, packet.getopcode());
				return;
			}
			//增加消费金币的log
			Player playerOld = player.clone();	
			
			
			
			//修改player数据
			player.getAccount().resetImoney(syncDollar.getInt("InitDollar"), syncDollar.getInt("RewardDollar"), syncDollar.getInt("UseDollar"));
			player.notifySaveForce();
			if (playerInfo.containsKey("star")) {
				player.setStar(playerInfo.getInt("star"));
			}
			if (playerInfo.containsKey("lang")) {
				player.setLang(playerInfo.getInt("lang"));
			}
			if (playerInfo.containsKey("Nickname")) {
				String name = playerInfo.getString("Nickname");
				CheckNameState cns = Utils.checkUserName(name);
				if (!cns.sucess) {
					if (player.getName() == null) {
						player.setName("");
					}
				} else {
					name = Platform.getAppContext().get(HarmoniousService.class).filterBadWords(name);
					player.setName(name);
				}
			}
			if (playerInfo.containsKey("icon")) {
				player.setIcon(playerInfo.getString("icon"));
			}

			player.setExp(playerInfo.getInt("Exp"));
			if(playerInfo.getInt("Level") >= 12) {//12级之后推荐放会获得额外奖励
				//recommend12LvReward(player);//推荐方获得二次奖励
			}
			player.setLevel(playerInfo.getInt("Level"));
			
			player.setMoney(playerInfo.getInt("Money"));
			
			
			player.setSceneOpenBlock(sceneId, playerInfo.getInt("OpenBlock"));
			player.setEnergy(playerInfo.getInt("Energy"));
			player.setBattleTimes(playerInfo.getInt("BattleTimes"));
			player.setBattleWinTimes(playerInfo.getInt("BattleWinTimes"));
			
			player.setSceneRabit0Number(sceneId, playerInfo.getInt("Rabit0Number"));
			player.setSceneRabit1Number(sceneId, playerInfo.getInt("Rabit1Number"));
			
			player.setWoods(playerInfo.getInt("Woods"));
			player.setFood(playerInfo.getInt("Food"));
			//新版需要添加的属性
			player.setRaceId(playerInfo.getInt("raceId"));
			player.setRich(playerInfo.getInt("rich"));
			player.setStone(playerInfo.getInt("Yuanshi"));
			if (playerInfo.containsKey("Cats")) {
				player.setCatsInfo(playerInfo.getJSONArray("Cats").toString());
				//存入兵力总数
				JSONArray js = playerInfo.getJSONArray("Cats");
				int count = 0;
				for(int i = 0;i<js.size();i++){
					JSONObject jso = (JSONObject)js.get(i);
					count += jso.getInt("count");
				}
				player.setAllCatCount(count);
			} else {
				player.setCatsInfo(new JSONArray().toString());
				player.setAllCatCount(0);
			}
			
			if(playerInfo.containsKey("BattleWin"))
			{
				player.setBattleWin(playerInfo.getString("BattleWin"));
			} else {
				player.setBattleWin("");
			}
			
			
			if (playerInfo.containsKey("Items")) {
				player.setItems(playerInfo.getString("Items"));
			} else {
				player.setItems("");
			}
			
			// 任务列表
			// "ID":12,"State":2,"Condition":1,"building":"1=1,2=20","item":"[2=10,2=100]","StartTime":11111,"TimeLong":120,"BattleWinTimes":12
//			try {
//				if (missions != null) {
//					QuestList questList = new QuestList(missions.size());
//					Iterator<JSONObject> missionsIterator = missions.iterator();
//					while (missionsIterator.hasNext()) {
//						JSONObject json = missionsIterator.next();
//						if(json != null){
//							int id = json.getInt("ID");
//							int state = json.getInt("State");
//							int condition = 0;
//							if (json.containsKey("Condition")) {
//								condition = json.getInt("Condition");
//							}
//							int startTime = 0;
//							if (json.containsKey("StartTime")) {
//								startTime = json.getInt("StartTime");
//							}
//							int timeLong = 0;
//							if (json.containsKey("TimeLong")) {
//								timeLong = json.getInt("TimeLong");
//							}
//							int battleWinTimes = 0;
//							if (json.containsKey("BattleWinTimes")) {
//								battleWinTimes = json.getInt("BattleWinTimes");
//							}
//							String building = "";
//							if (json.containsKey("building")) {
//								building = json.getString("building");
//							}
//							String item = "";
//							if (json.containsKey("item")) {
//								item = json.getString("item");
//							}
//
//							Quest quest = new Quest(id, state, condition, startTime, timeLong, battleWinTimes, building, item);
//
//							questList.add(quest);
//						}
//						
//					}
//					player.setQuestList(sceneId, questList);
//				}
//				
//			} catch (Throwable e) {
//				
//			}
//			BuildingList buildList = null;
//			try {
//				// 建筑列表
//				buildList = new BuildingList(builds.size());
//				Iterator<JSONObject> iterator = builds.iterator();
//				while (iterator.hasNext()) {
//					JSONObject json = iterator.next();
//					if(json != null){
//						int x = json.getInt("x");
//						int y = json.getInt("y");
//						int templateId = json.getInt("ID");
//						int flip = 0;
//						if (json.containsKey("flip")) {
//							flip = json.getInt("flip");
//						}
//						int state = 0;
//						if (json.containsKey("State")) {
//							state = json.getInt("State");
//						}
//						int stateTime = 0;
//						if (json.containsKey("StateTime")) {
//							stateTime = json.getInt("StateTime");
//						}
//						int havestTimes = 0;
//						if (json.containsKey("havestTimes")) {
//							havestTimes = json.getInt("havestTimes");
//						}
//						boolean isHarvest = false;
//						if (json.containsKey("isHarvest")) {
//							isHarvest = json.getInt("isHarvest") == 1;
//						}
//						int assembleId = 0;
//						if (json.containsKey("AssembleId")) {
//							assembleId = json.getInt("AssembleId");
//						}
//						int selfId = 0;
//						if (json.containsKey("SelfID")) {
//							selfId = json.getInt("SelfID");
//						}
//						Building building = new Building(selfId, templateId, x, y); // instanceId
//																				// = 0;
//						building.setAssembleId(assembleId);
//						building.setHavestTimes(havestTimes);
//						building.setFlip(flip);
//						building.setHarvest(isHarvest);
//						building.setState(state);
//						building.setStateTime(stateTime);
//						buildList.add(building);
//					}
//					
//				}
//			} catch (Throwable e) {
//				// TODO: handle exception
//			}
//			player.setSceneBuildingList(sceneId, buildList);
			player.setJsonData(packet.toString());
			player.setLastSynchInfoTime(new Date());
			
			//增加消费金币的log
			Player playerNew = player.clone();	
			
			AkLogService.printMoneyCost(playerOld, playerNew);
			AkLogService.printPopulation(playerOld, playerNew);
			//增加关卡日志
			AkLogService.printPVEfight(playerOld, playerNew);
			AkLogService.printGuide(playerOld, playerNew);
			AkLogService.printBuilding(playerOld, playerNew);
			//目前改成强制的方便测试人员测试
			player.notifySaveForce();
			
//			Platform.getLog().fortune(player, Words.JIN_BI, player.getMoney());
//			Platform.getLog().fortune(player, Words.YUAN_BAO, player.getAccount().getImoney());
//			if (isNeedLogLevel) {
//				Platform.getLog().role(player);
//			}
//			if (player.getClientVersionIntValue() > PlayerEx.CLIENT_VERSION_GM_LOW) {
//				// 下发信息, 成功更新.
//				Packet pt = new JSONPacket(HOpCodeEx.PLAYER_UPLOAD_SERVER);
//				// 统一补钱接口
//				int needCompensate = CatlikeCompensate.needCompensate(player);
//				if (needCompensate > 0) {
//					log.info("[needCompensate]HOpCodeEx  needCompensate[{}] player:[{}], account:[{}]",
//							new Object[] { needCompensate, player, player.getAccount() });
//					pt.put("dollar", needCompensate);
//				} else {
//					pt.put("dollar", 0);
//				}
//
//				pt.put("other", 0);
//				pt.put("result", "OK");
//				pt.put("time", Time.currTime/1000L);
//				pt.put("needSyncDollar", needSyncDollar);
//				log.info("[needCompensate] packet:[{}]" + pt.toString());
//				session.send(pt);
//			} else {
//				int needCompensate = CatlikeCompensate.needCompensate(player);
//				if (needCompensate > 0) {
//					log.info("[needCompensate] ErrorHandler needCompensate[{}] player:[{}], account:[{}]",
//							new Object[] { needCompensate, player, player.getAccount() });
//					//gm版之前的客户端需要用错误来申请回档；
//					log.info("[needCompensate] ErrorHandler:[{}]" + ErrorHandler.ERROR_CODE_IMONEY_REMAINDOLLAR_NOLINEAR);
//					ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_IMONEY_REMAINDOLLAR_NOLINEAR, HOpCodeEx.PLAYER_UPLOAD_CLIENT);
//					return;
//				} 
				Packet pt = new JSONPacket(HOpCodeEx.PLAYER_UPLOAD_SERVER);
				pt.put("result", "OK");
				pt.put("time", Time.currTime/1000L);
				pt.put("needSyncDollar", needSyncDollar);
				session.send(pt);
//			}
//			Platform.getLog().userLog(player);
//			Platform.getLog().economicLog1(player, oldMoney, oldIMoney);
		} catch (Throwable e) {
			log.error("uploadProcess error",e);
			Packet pt = new JSONPacket(HOpCodeEx.PLAYER_UPLOAD_SERVER);
			pt.put("result", "NO");
			session.send(pt);
		}
		
	}
//	private void recommend12LvReward(PlayerEx player) {
//		if(player.getRecommendCode() == 0) {
//			return;
//		}else {
//			int targetId = player.getRecommendCode();
//			PlayerEx targetPlayer = loadPlayer(targetId);
//			if(targetPlayer == null) {
//				return;
//			}else {
//				if(targetPlayer.addBerecommend12LvPlayerId(player.getInstanceId())) {
//					player.setRecommendCode(-1);
//					targetPlayer.notifySave();
//					player.notifySave();
//				}
//			}
//		}
//	}

//	@OP(code = 222)
//	public void testComn(Packet packet, HSession session) {
//		Player cPlayer = (Player) session.client();
//		if (cPlayer == null || !cPlayer.getAccount().getMid().equals("3001")) {
//			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
//			return;
//		}
//		
//		int playerId = 0;
//		if(packet.containsKey("id")) {
//			int id = 0;
//			if (Platform.getConfiguration().getBoolean("ID_ENCODE")) {
//				id = IdUtil.deCode(packet.getString("id"));
//			} else {
//				id = packet.getInt("id");
//			}
//			playerId = this.getPlayerIdByAccountId(id);
//		}else if(packet.containsKey("mid")) {
//			String mid = packet.getString("mid");
//			List<Account> accountList = Platform.getEntityManager().limitQuery("from Account where mid like ? order by id desc", 0, 1,
//					mid + "&%");
//			if (accountList == null || accountList.isEmpty()) {
//				accountList = Platform.getEntityManager().limitQuery("from Account where mid = ? order by id desc", 0, 1, mid);
//				if (accountList == null || accountList.isEmpty()) {
//					log.info("[login] ip:{} BackProcess wrong.  accountList == null, mid:{}", session.ip(), mid);
//					ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_25, packet.getopcode());
//					return;
//				}
//			}
//			Account account = accountList.get(0);
//			playerId = this.getPlayerIdByAccountId(account.getId());
//		}  else {
//			ErrorHandler.sendErrorMessage(session, 222, packet.getopcode());
//			return;
//		}
//		
//		PlayerEx player = null;
//		if (playerId != 0) {
//			player = this.loadPlayer(playerId);
//			if(player.getAccount() == null) {
//				Account accout = Platform.getEntityManager().find(Account.class, player.getAccountId());
//				player.setAccount(accout);
//			}
//			int tMoney = packet.getInt("t");
//			CatlikeCompensate.addCompensate(player, player.getAccount().getCompensateDollar() + tMoney);
//		}
//
//		Packet pt = new JSONPacket(223);
//		if (player != null) {
//			pt.put("accout", player.getAccount());
//		}else {
//			pt.put("accout", "not found.");
//		}
//		pt.put("list", CatlikeCompensate.whiteList.toString());
//		session.send(pt);
//	}
	
	@OP(code = HOpCode.PLAYER_LOGOUT_CLIENT)
	public void logout(Packet packet, HSession session) throws Exception {
		log.info("[HTTPRequest] session:{}  packet:{}]", session.getSessionId(), packet.toString());

		Player p = (Player) session.client();
		if (p == null) {
			return;
		}
		Platform.getEventManager().addEvent(new Event(GameEvent.EVENT_PLAYER_LOGOUTED, p));
		log.info("logout playerId:{},accountId:{}", p.getId(), p.getAccountId());
		Packet pt = new JSONPacket(HOpCodeEx.PLAYER_LOGOUT_SERVER);
		if (Platform.getConfiguration().getBoolean("ID_ENCODE")) {
			pt.put("accountId", IdUtil.enCode(p.getAccount().getId()));
		} else {
			pt.put("accountId", p.getAccount().getId());
		}
		
		session.send(pt);
	}

	@OP(code = HOpCodeEx.PLAYER_ICON_CLIENT)
	public void icon(Packet packet, HSession session) throws Exception {
		log.info("[HTTPRequest] session:{}  packet:{}", session.getSessionId(), packet.toString());
		Player player = (Player) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			return;
		}
		String icon = packet.getString("icon");
		if (packet.containsKey("nickname")) {
			String name = packet.getString("nickname");
			CheckNameState cns = Utils.checkUserName(name);
			if (!cns.sucess) {
				ErrorHandler.sendErrorMessage(session, cns.errorCode, packet.getopcode());
				return;
			}
			name = Platform.getAppContext().get(HarmoniousService.class).filterBadWords(name);
			log.info("[nickname] palyerId:{}, oldName:{}->newName{}", new Object[] { player.getId(), player.getName(), name });
			player.setName(name);
		}
		player.setIcon(icon);

//		Actor actor = Platform.getAppContext().get(ActorCacheService.class).findActorByCache(player.getInstanceId());
//		if (actor != null) {
//			actor.setIcon(icon);
//			actor.setName(player.getName());
//		}

		player.notifySave();
		// 下发信息, 成功更新.
		Packet pt = new JSONPacket(HOpCodeEx.PLAYER_ICON_SERVER);
		pt.put("result", "OK");
		session.send(pt);
		log.info("[METHODEND] return[null]");
	}

	@OP(code = HOpCode.SNS_BINDING_CLIENT)
	public void bindingSNS(Packet packet, HSession session) throws Exception {
		super.bindingSNS(packet, session);
	}

	@OP(code = HOpCodeEx.PLAYER_HOUSE_PASSWORD_CLIENT)
	public void housePassword(Packet packet, HSession session) throws Exception {
		log.info("[HTTPRequest] session:{}  packet:{}", session.getSessionId(), packet.toString());
		PlayerEx player = (PlayerEx) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			return;
		}
		String pwd = packet.getString("pwd");
		if (pwd == null || pwd.length() == 0 || pwd.length() > 100) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_19, packet.getopcode());
			return;
		}
		player.setHousePassword(pwd);

		player.notifySaveForce();

		// 下发信息, 成功更新.
		Packet pt = new JSONPacket(HOpCodeEx.PLAYER_HOUSE_PASSWORD_SERVER);
		pt.put("result", "OK");
		session.send(pt);
		log.info("[METHODEND] return[null]");
	}

	/**
	 * 需要搬家的内容有: 1. Account里的sns信息, 帐号信息, 元宝等; 2. Player里的进度信息, 好友等,
	 * 这里的playerId应该是还用原来的那个 3. 替换account中的指引值, mid, accountid;
	 * 将原来的mid设置为"最后登录时间I2000",
	 * 
	 * @param packet
	 * @param session
	 * @throws Exception
	 */
	@OP(code = HOpCodeEx.PLAYER_MOVE_HOUSE_CLIENT)
	public void moveHouse(Packet packet, HSession session) throws Exception {
		log.info("[HTTPRequest] session:{}  packet:{}", session.getSessionId(), packet.toString());
		PlayerEx player = (PlayerEx) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			return;
		}
		int id = 0;
		if (Platform.getConfiguration().getBoolean("ID_ENCODE")) {
			id = IdUtil.deCode(packet.getString("id"));
		} else {
			id = packet.getInt("id");
		}
		
		String pwd = packet.getString("pwd");
		if (id < 1) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_28, packet.getopcode());
			return;
		}
		if (pwd == null || pwd.length() == 0 || pwd.length() > 100) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_19, packet.getopcode());
			return;
		}

		Account nowAccount = player.getAccount();

		PlayerEx oldPlayer = Platform.getEntityManager().fetch("from PlayerEx where accountId = ? and housePassword = ?",
				id, pwd);
		if (oldPlayer == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_29, packet.getopcode());
			return;
		}

		Account oldAccount = Platform.getEntityManager().find(Account.class, oldPlayer.getAccountId());
		oldPlayer.setAccount(oldAccount);

		// log 搬家;
		String oldMid = oldAccount.getMid();
		oldAccount.setMid(nowAccount.getMid());
		nowAccount.setMid(oldMid);
		Platform.getEntityManager().updateSync(oldAccount);
		Platform.getEntityManager().updateSync(nowAccount);
		// 下发信息, 成功更新. 客户端需要发起登录协议
		Packet pt = new JSONPacket(HOpCodeEx.PLAYER_MOVE_HOUSE_SERVER);
		if (Platform.getConfiguration().getBoolean("ID_ENCODE")) {
			pt.put("ID", IdUtil.enCode(oldAccount.getId()));
		} else {
			pt.put("ID", oldAccount.getId());
		}
		
		//sendPlayerInfoBuildingInfo(packet, player, pt);
		session.send(pt);

		logout(packet, session);
		log.info("[METHODEND] return[null]");
	}

	@OP(code = HOpCodeEx.PLAYER_DOWNLOAD_CLIENT)
	public void downloadProcess(Packet packet, HSession session) throws Exception {
		
		PlayerEx player = (PlayerEx) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			return;
		}
		
		// 统一补钱接口
		//CatlikeCompensate.compensate(player);

		JSONPacket pt = new JSONPacket(HOpCodeEx.PLAYER_DOWNLOAD_SERVER);
		//int sceneId = 1;
		int nextSceneId = -1;
//		if(packet.containsKey("sceneID")) {
//			sceneId = packet.getInt("sceneID");
//		}
//		int MAX_SCENE_NUM = 4;
//		for(nextSceneId = sceneId + 1; nextSceneId <= MAX_SCENE_NUM;  nextSceneId++) {
//			if (player.getSceneBuildingList(nextSceneId) == null|| player.getSceneBuildingList(nextSceneId).size() == 0) {
//				continue;
//			}else {
//				break;
//			}
//		}
//		if(nextSceneId > MAX_SCENE_NUM) {
//			nextSceneId = -1;
//		}
		
		if (player.getJsonData() == null || player.getJsonData() == "" || player.getJsonData().equals("")) {
			//if(true) {//默认进度为空的人， 作弊了我们只能让他自己连不上网了。 否则会洗空客户端进度。
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
				return;
			//}
		} else {
			JSONObject jsonData = JSONObject.fromObject(player.getJsonData());
			if (jsonData.containsKey("Mission")) {
				pt.put("Mission", jsonData.get("Mission"));
			}
			if(jsonData.containsKey("player")){
				JSONObject jsonPlayer = (JSONObject)jsonData.get("player");
				//换成客户端接受的字段
				if(jsonPlayer.containsKey("Yuanshi")){
					jsonPlayer.put("stone", jsonPlayer.get("Yuanshi"));
				}
				//换成服务器的数值
				JSONObject verify = player.getAccount().toVerifyJson();
				jsonPlayer.put("verify", verify);
				//换成截取好的名字
				if(jsonPlayer.containsKey("Nickname")){
					jsonPlayer.put("Nickname", player.getName());
				}
				pt.put("Player", jsonPlayer);
			}
			JSONObject scene = new JSONObject();
			
			if (jsonData.containsKey("Buildings")) {
				scene.put("Buildings", jsonData.get("Buildings"));
			}
			if(jsonData.containsKey("BuildingSelfIDCounter")){
				scene.put("BuildingSelfIDCounter", jsonData.get("BuildingSelfIDCounter"));
			}
			if (jsonData.containsKey("Palace")) {
				scene.put("Palace", jsonData.get("Palace"));
			}
			if (jsonData.containsKey("Trade")) {
				scene.put("Trade", jsonData.get("Trade"));
			}
			if(jsonData.containsKey("GuideFrameData")){
				pt.put("GuideFrameData", jsonData.get("GuideFrameData"));
			}
			if(jsonData.containsKey("mCount")){
				pt.put("mCount", jsonData.get("mCount"));
			}
			if(jsonData.containsKey("totalMoney")){
				pt.put("totalMoney", jsonData.get("totalMoney"));
			}
			if(jsonData.containsKey("sceneID")){
				pt.put("sceneID", jsonData.get("sceneID"));
			}
			if(jsonData.containsKey("save_time")){
				pt.put("save_time", jsonData.get("save_time"));
			}
			if(jsonData.containsKey("LocalTime")){
				pt.put("LocalTime", jsonData.get("LocalTime"));
			}
			pt.put("ConstructionScene", scene.toString());
			pt.put("nextSceneID", nextSceneId);
			pt.put("time", System.currentTimeMillis());
			session.send(pt);
		}
		
	}

	@OP(code = HOpCode.LOG_CLIENT)
	public void clientLog(Packet packet, HSession session) {
		super.clientLog(packet, session);
	}

	@OP(code = HOpCode.REFRESH_IMONEY_CLIENT)
	public void refreshImoney(Packet packet, HSession session) {
		log.info("[refreshImoney] ip:{} packet:{}", session.ip(), packet.toString());
		Player player = (Player) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			log.info("[refreshImoney] error:player is null,ip:{} packet:{}", session.ip(), packet.toString());
			return;
		}
		Packet retPt = new JSONPacket(HOpCode.REFRESH_IMONEY_SERVER);
		int imoney = player.getAccount().getImoney();
		retPt.put("imoney", imoney);
		session.send(retPt);
		log.info("refreshImoney:palyerId:{},accountId:{},imoney:{}", new Object[] { player.getId(), player.getAccountId(), imoney });
	}
	/**
	 * 刷新爱心值
	 * @param packet
	 * @param session
	 */
	@OP(code = HOpCodeEx.CLIENT_REFRESH_LOVE)
	public void refreshlove(Packet packet, HSession session) {
		try {
			PlayerEx player = (PlayerEx) session.client();
			if (player == null) {
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
				return;
			}
			addLove(session,player);
//			Packet retPt = new JSONPacket(HOpCodeEx.SERVER_REFRESH_LOVE);
//			retPt.put("love", player.getLove());
//			retPt.put("result", 1);
//			session.send(retPt);
		} catch (Throwable e) {
			log.error("refreshlove error",e);
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
		}
		
	}
	/**
	 * 添加当前爱心值返回数据
	 * @param session
	 */
	public void addLove(HSession session,PlayerEx player){
		Packet retPt = new JSONPacket(HOpCodeEx.SERVER_REFRESH_LOVE);
		retPt.put("love", player.getLove());
		PlayerSnsService psService = Platform.getAppContext().get(PlayerSnsService.class);
		
		retPt.put("remainTime",PlayerSnsService.LOVE_MAIL_MAXNUM_EVERYDAY- player.getPlayerSns().getTodaySendCount() );
		retPt.put("result", 1);
		session.send(retPt);
	}
	/**
	 * 国际化 client-->server eg.{"opcode":108,"data":{language:English/中文}}
	 */
	@OP(code = HOpCode.INTERNATIONAL_CLIENT)
	public void languageInternation(Packet packet, HSession session) {
		log.info("[languageInternation] ip:{} packet:{}", session.ip(), packet.toString());
		PlayerEx player = (PlayerEx) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			return;
		}
		int language = packet.getInt("language");
		player.setLang(language);
		player.notifySave();
		Packet pt = new JSONPacket(HOpCode.INTERNATIONAL_SERVER);
		pt.put("language", language);
		session.send(pt);
		log.info("[languageInter()] return[null]");
	}

	/**
	 * 客户端心跳包 (每5分钟发一次) client-->server eg.{"opcode":105,"data":{}}
	 */
	@OP(code = HOpCode.HEART_CLIENT)
	public void clientHeart(Packet packet, HSession session) {
		PlayerEx player = (PlayerEx) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			log.info("clientHeart error:player is null,ip:{}", session.ip());
			return;
		}
		//增加日志
		AkLogService.printHeartBeat(player.clone());
		Packet retPt = new JSONPacket(HOpCode.HEART_SERVER);
		boolean isSave = false;
		//收件箱
		if(player.getPool().getInt(PlayerEx.NOTIFY_CLIENT_MAIL_INBOX, PlayerEx.NOT_NOTIFY_CLIENT) == PlayerEx.NOTIFY_CLIENT){
			player.getPool().setInt(PlayerEx.NOTIFY_CLIENT_MAIL_INBOX, PlayerEx.NOT_NOTIFY_CLIENT);
			isSave = true;
			retPt.put("notifyMailInBox", PlayerEx.NOTIFY_CLIENT);
		}else{
			retPt.put("notifyMailInBox", PlayerEx.NOT_NOTIFY_CLIENT);
		}
		//信息箱
		if(player.getPool().getInt(PlayerEx.NOTIFY_CLIENT_MAIL_MESSAGEBOX, PlayerEx.NOT_NOTIFY_CLIENT) == PlayerEx.NOTIFY_CLIENT){
			player.getPool().setInt(PlayerEx.NOTIFY_CLIENT_MAIL_MESSAGEBOX, PlayerEx.NOT_NOTIFY_CLIENT);
			isSave = true;
			retPt.put("notifyMailMessageBox", PlayerEx.NOTIFY_CLIENT);
		}else{
			retPt.put("notifyMailMessageBox", PlayerEx.NOT_NOTIFY_CLIENT);
		}
		//系统公告
		if(player.getPool().getInt(PlayerEx.NOTIFY_CLIENT_NOTICE_SYSTEM, PlayerEx.NOT_NOTIFY_CLIENT) == PlayerEx.NOTIFY_CLIENT){
			player.getPool().setInt(PlayerEx.NOTIFY_CLIENT_NOTICE_SYSTEM, PlayerEx.NOT_NOTIFY_CLIENT);
			isSave = true;
			retPt.put("notifyNoticeSystem", PlayerEx.NOTIFY_CLIENT);
		}else{
			retPt.put("notifyNoticeSystem", PlayerEx.NOT_NOTIFY_CLIENT);
		}
		//交互公告
		if(player.getPool().getInt(PlayerEx.NOTIFY_CLIENT_NOTICE_INTERACTIVE, PlayerEx.NOT_NOTIFY_CLIENT) == PlayerEx.NOTIFY_CLIENT){
			player.getPool().setInt(PlayerEx.NOTIFY_CLIENT_NOTICE_INTERACTIVE, PlayerEx.NOT_NOTIFY_CLIENT);
			isSave = true;
			retPt.put("notifyNoticeInteractive", PlayerEx.NOTIFY_CLIENT);
		}else{
			retPt.put("notifyNoticeInteractive", PlayerEx.NOT_NOTIFY_CLIENT);
		}
		//如果属性改变了，通知保存
		if(isSave){
			player.notifySave();
		}
		retPt.put("time", (int)(System.currentTimeMillis()/1000L));
		session.send(retPt);
		log.info("[clientHeart] playerId:{},ip:{}", player.getId(), session.ip());
	}
	
	/**
	 * 客户端行为统计
	 */
	@OP(code = HOpCodeEx.CLIENT_OP_LOG)
	public void clientDoLog(Packet packet, HSession session) {
		log.info("{}",packet);
		Player player = (Player) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			log.info("[clientDoLog] error:player is null,ip:{} packet:{}", session.ip(), packet.toString());
			return;
		}
		
		JSONArray logRoot = packet.getJSONArray("LogRoot");
		Iterator<JSONObject> iterator = logRoot.iterator();
		while (iterator.hasNext()) {
			JSONObject logItem = iterator.next();
			long startTime = logItem.getInt("StartTM");
			if(startTime == 0) {
				log.info("[clientDoLog] startTime is 0");
				startTime = (int) (Time.currTime /1000);
			}
			JSONArray logs = logItem.getJSONArray("Log");
			if(logs == null || logs.size() == 0) {
				log.info("[clientDoLog] logs is empty");
				continue;
			}
			Iterator<JSONObject> logIt = logs.iterator();
			while (logIt.hasNext()) {
				JSONObject opLog = logIt.next();
				String op = opLog.getString("do");
				op = op == "" ? "NULL" : op;
				long tm = opLog.getInt("tm");
				int ty = opLog.getInt("ty");
				//do log;
//				Platform.getLog().logPlayerOp(player, op,startTime +  tm, ty);
				Platform.getLog().behaviorLog(player, op, startTime +  tm + ":" + ty);
			}
		}
		
		Packet retPt = new JSONPacket(HOpCodeEx.SERVER_OP_LOG);
		retPt.put("ok", "1");
		session.send(retPt);
		
	}
	/**
	 * 通过如果连接world服务器，通过world服务器保存
	 */
	public void savePlayer(Player player, boolean isForce,boolean islogout) {
		// 更新远程数据.
		String key = DataKeys.playerKey(player.getInstanceId());

		Data data = Platform.dataCenter().getData(key);
		boolean ret = false;
		if (data == null) {
			if(IS_THROUGH_WORLD_INIT_PLAYER_MEMCACHED == 0){
				ret = Platform.dataCenter().sendNewData(key, player);
			}
		} else {
			data.value = player;
			ret = Platform.dataCenter().sendData(key, data);
		}
		if(!ret){
			log.info("savePlayer:error[sendMemcached]");
		}
		boolean needSave = false;
//		if (ret && Platform.worldServer() != null) {
//			if (isForce) {// 强制保存
//				needSave = true;
//			} else {// 其他时候按照上次保存时间保存
//				if (Time.currTime / 1000 - player.getSaveTime() > DB_SAVE_SPLIT_SECOND) {
//					needSave = true;
//				}
//			}
//			if (needSave) {
//				player.setSaveTime((int) (Time.currTime / 1000));
//				WorldManager wmanager = Platform.getAppContext().get(WorldManager.class);
//				wmanager.playerChanged(player);// 通知保存数据库.
//				// 清理本地缓存。
//				Platform.getEntityManager().clearFromCache(player);
//				if (player.getAccount() != null) {
//					Platform.getEntityManager().clearFromCache(player.getAccount());
//				}
//			}
//		} else {
			if (isForce) {// 强制保存
				needSave = true;
			} else {// 其他时候按照上次保存时间保存
				if (Time.currTime / 1000 - player.getSaveTime() > DB_SAVE_SPLIT_SECOND) {
					needSave = true;
				}
			}
			if (needSave) {
				player.setSaveTime((int) (Time.currTime / 1000));
				if (player.getAccount() != null) {
					Platform.getEntityManager().updateSync(player.getAccount());// 自己保存数据库
				}
				Platform.getEntityManager().updateSync(player);// 自己保存数据库
			}
		//}
		//如果是登出保存2次，第二次仅仅是把dirty保存下来，为了叫其他的服务器进行查询
		if(islogout){
			player.setDirty(false);
			data = Platform.dataCenter().getData(key);
			ret = false;
			if (data == null) {
				if(IS_THROUGH_WORLD_INIT_PLAYER_MEMCACHED == 0){
					ret = Platform.dataCenter().sendNewData(key, player);
				}
			} else {
				data.value = player;
				ret = Platform.dataCenter().sendData(key, data);
			}
			if(!ret){
				log.info("savePlayer:error[sendMemcached second]");
			}
			if(IS_NEED_WORLD_CHECK_LOGIN == 1){
				//通知world服务器该玩家登出完成
				if(Platform.worldServer() != null){
					WorldManagerEx wmanager = (WorldManagerEx)Platform.getAppContext().get(WorldManager.class);
					String mid = player.getAccount().getMid();
					if(mid.indexOf("&") != -1){
						mid = mid.substring(0, mid.indexOf("&"));
					}
					wmanager.loginFinish(mid);
				}
			}
		}
		
	}
}
