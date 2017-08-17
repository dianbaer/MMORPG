package ak.friend;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.procedure.TObjectProcedure;

import java.util.List;
import java.util.concurrent.TimeUnit;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ak.mail.IAkMailService;
import ak.mail.MailEx;
import ak.market.MarketService;
import ak.optLog.IUserOptLogService;
import ak.optLog.UserOptLog;
import ak.player.PlayerEx;
import ak.player.PlayerServiceEx;
import ak.player.PlayerTransport;
import ak.playerSns.PlayerSns;
import ak.playerSns.PlayerSnsService;
import ak.server.ErrorHandlerEx;
import ak.trade.TradeService;
import ak.util.IRandom;
import ak.util.RandomUtil;
import cyou.mrd.ObjectAccessor;
import cyou.mrd.Platform;
import cyou.mrd.entity.Player;
import cyou.mrd.event.Event;
import cyou.mrd.event.GameEvent;
import cyou.mrd.event.OPEvent;
import cyou.mrd.game.actor.Actor;
import cyou.mrd.game.actor.ActorCacheService;
import cyou.mrd.game.relation.PlayerRelation;
import cyou.mrd.game.relation.RelationList;
import cyou.mrd.game.relation.RelationService;
import cyou.mrd.io.AsyncCall;
import cyou.mrd.io.OP;
import cyou.mrd.io.OPHandler;
import cyou.mrd.io.Packet;
import cyou.mrd.io.http.HOpCode;
import cyou.mrd.io.http.HSession;
import cyou.mrd.io.http.JSONPacket;
import cyou.mrd.io.tcp.HOpCodeEx;
import cyou.mrd.projectdata.TextDataService;
import cyou.mrd.service.PlayerService;
import cyou.mrd.util.ConfigKeys;
import cyou.mrd.util.ErrorHandler;
import cyou.mrd.util.IdUtil;
import cyou.mrd.util.Utils;

@OPHandler(TYPE = OPHandler.HTTP_EVENT)
public class FriendService extends RelationService {
	private static final Logger log = LoggerFactory.getLogger(FriendService.class);
	private static final int FRIEND_MAX_LIMIT = Platform.getConfiguration().getInt(ConfigKeys.FRIEND_MAX_LIMIT);

	private RandomFriendPool randomFriendPool_lang0 = new RandomFriendPool(0);
	private RandomFriendPool randomFriendPool_lang1 = new RandomFriendPool(1);
	private RandomFriendPool randomFriendPool_lang2 = new RandomFriendPool(2);
	/**
	 * 送礼每人每天的次数
	 */
	public static int SEND_GIFT_ONEPLAYER_NUM_EVERYDAY = Platform.getConfiguration().getInt("send_gift_oneplayer_num_everyday");
	/**
	 * 帮助每人每天次数
	 */
	public static int HELP_FRIEND_ONEPLAYER_NUM_EVERYDAY = Platform.getConfiguration().getInt("help_friend_oneplayer_num_everyday");
	/**
	 * 送多个礼物的最大个数
	 */
	public static int SEND_MULTI_GIFT_NUM = Platform.getConfiguration().getInt("send_multi_gift_num");
	/**
	 * 田地
	 */
	public static final int FIELD = 1;
	/**
	 * 房屋
	 */
	public static final int HOUSE = 2;
	@Override
	public String getId() {
		return "FriendService";
	}

	@Override
	public void startup() throws Exception {
		Platform.getScheduler().scheduleAtFixedRate(new updateRandomFriendList(), 1, 1, TimeUnit.HOURS);
		randomFriendPool_lang0.initRandomFriendPool();
		randomFriendPool_lang1.initRandomFriendPool();
		randomFriendPool_lang2.initRandomFriendPool();
	}
	/**
	 * 获得随机操作
	 * @param friendHome
	 * @param type
	 * @param player
	 * @return
	 */
	public int getState(FriendHome friendHome, int type ,PlayerEx player,boolean firstVisit){
		int state = 0;
		if(type == FIELD){
			state = friendHome.getTodayFieldState();
			//状态是0
			if(state == FriendHome.STATE_NULL){
				//没有达到上限刷新
				if(friendHome.getTodayClearTimes() < HELP_FRIEND_ONEPLAYER_NUM_EVERYDAY){
					IRandom random = RandomUtil.getRandomGift(player,RandomUtil.FIELD , type,firstVisit);
					if(random == null){
						
					}else{
						HelpFriendTemplate helpFriendTemplate = (HelpFriendTemplate)random;
						state = helpFriendTemplate.getId();
						friendHome.setFieldState(state);
					}
					
				}
			}
		}else if(type == HOUSE){
			state = friendHome.getTodayHouseState();
			
			if(state == FriendHome.STATE_NULL){
				if(friendHome.getTodayClearTimes() < HELP_FRIEND_ONEPLAYER_NUM_EVERYDAY){
					IRandom random = RandomUtil.getRandomGift(player,RandomUtil.HOUSE , type,firstVisit);
					if(random == null){
						
					}else{
						HelpFriendTemplate helpFriendTemplate = (HelpFriendTemplate)random;
						state = helpFriendTemplate.getId();
						friendHome.setHouseState(state);
					}
				}
			}
		}
		return state;
	}
	/**
	 * 获得随机npc
	 * @param friendHome
	 * @param player
	 * @return
	 */
	public int getNpc(FriendHome friendHome, PlayerEx player){
		int npcId = 0;
		npcId = friendHome.getNpcId();
		if(npcId == 0){
			IRandom random = RandomUtil.getRandomGift(player,RandomUtil.NPC , 0,false);
			if(random == null){
				
			}else{
				RandomNpcTemplate randomNpcTemplate = (RandomNpcTemplate)random;
				npcId = randomNpcTemplate.getId();
				friendHome.setNpcId(npcId);
			}
			
		}
		return npcId;
	}
	@Override
	public void shutdown() throws Exception {
	}

	@OPEvent(eventCode = GameEvent.EVENT_PLAYER_LOGOUTED)
	protected void playerLogoutEvent(Event event) {
		final PlayerEx player = (PlayerEx) event.param1;
		Platform.getThreadPool().execute(new AsyncCall() {
			@Override
			public void run() {
				playerLogoutedEx(player);
			}

			@Override
			public void callFinish() throws Exception {
			}
		});
	}

	@OPEvent(eventCode = GameEvent.EVENT_CHANGE_DAY)
	protected void refreshFriendPool(Event event) {
		Platform.getThreadPool().execute(new AsyncCall() {
			@Override
			public void run() {
				randomFriendPool_lang0.initRandomFriendPool();
				randomFriendPool_lang1.initRandomFriendPool();
				randomFriendPool_lang2.initRandomFriendPool();
			}

			@Override
			public void callFinish() throws Exception {
			}
		});
	}

	@OPEvent(eventCode = GameEvent.EVENT_RELATION_ADD)
	protected void relationAdd(Event event) {
		super.relationAdd(event);
	}

	private void playerLogoutedEx(final PlayerEx player) {
		long t, t1, t2;
		t1 = System.nanoTime();
		TIntObjectMap<FriendHome> friendHomeMap = player.getFriendHomeMap();
		if (friendHomeMap != null) {
			friendHomeMap.forEachValue(new TObjectProcedure<FriendHome>() {
				@Override
				public boolean execute(FriendHome friendHome) {
					if (friendHome.isNothingSave()) {
						return true;// 忽略新的, 没有操作过的FriendHome对象
					}
					if (friendHome.needCreateFlag) {
						log.info("[playerLogoutedEx] playerId:{} friendHome Create. clearTimes:{}", player.getInstanceId(),
								friendHome.getTodayClearTimes());
						Platform.getEntityManager().createSync(friendHome);
					} else if (friendHome.needSaveFlag) {
						log.info("[playerLogoutedEx] playerId:{} friendHome updateSync. clearTimes:{}", player.getInstanceId(),
								friendHome.getTodayClearTimes());
						Platform.getEntityManager().updateSync(friendHome);
					}
					return true;
				}
			});
		}
		t2 = System.nanoTime();
		t = (t2 - t1) / 1000000L;
		log.info("[playerLogoutedEx] PlayerEx:{} use:{}ms", player.getInstanceId(), t);
	}

	/**
	 * 好友访问
	 * @param packet
	 * @param session
	 * @throws CloneNotSupportedException
	 * @throws Exception
	 */
	@OP(code = HOpCode.PLAYER_FRIEND_HOME_CLIENT)
	protected void friendHome(Packet packet, HSession session) throws CloneNotSupportedException, Exception {
		try {
			PlayerEx player = (PlayerEx) session.client();
			if (player == null) {
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
				return;
			}
			//好友id
			int friendId = packet.getInt("friendId");
			//场景id默认是1
			int sceneId = 1;
//			if(packet.containsKey("sceneID")) {
//				sceneId = packet.getInt("sceneID");
//			}
			PlayerServiceEx playerService = (PlayerServiceEx) Platform.getAppContext().get(PlayerService.class);
			//没有检测好友有效性
			//PlayerEx friend = (PlayerEx) ObjectAccessor.getPlayer(friendId);
			//if (friend == null) {
				
				PlayerEx friend = playerService.loadPlayer(friendId);
				if(friend == null){
					ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_11, packet.getopcode());
					return;
				}
			//}
			if(friend.getExist() == 0){
				Packet pt = new JSONPacket(HOpCode.PLAYER_FRIEND_HOME_SERVER);
//				for(int sceneIndex = 1; sceneIndex <= 4; sceneIndex++){
//					boolean sceneNothingState = (friend.getSceneBuildingList(sceneIndex) == null || friend.getSceneBuildingList(sceneIndex).size() == 0);
//					pt.put("sceneID_" + sceneIndex, sceneNothingState ? 0 : 1);
//				}
				
				//根据场景id获取场景里兔子和猫的数量
				JSONObject scene = friend.toFriendViewClientData(sceneId);
				
				scene.put("sceneID", sceneId);
				
				//根据场景id获取建筑列表
				JSONObject jsonData = JSONObject.fromObject(friend.getJsonData());
				//BuildingList buildingList = friend.getSceneBuildingList(sceneId);
				if (jsonData.containsKey("Buildings")) {
					scene.put("Buildings", jsonData.get("Buildings"));
					pt.put("sceneID_" +sceneId, 1);
				} else {
					scene.put("Buildings", "[]");
					pt.put("sceneID_" +sceneId, 0);
				}
				//添加摇钱树的信息
				PlayerSnsService psService = Platform.getAppContext().get(PlayerSnsService.class);
				//if(sceneId == 1){
					scene.put("moneyTree", psService.visitMoneyTree(friend.getId()));
				//}else{
				//	scene.put("moneyTree", 0);
				//}
				//场景里的所有信息
				pt.put("nowScene", scene.toString());
				
				//好友相关信息
				JSONObject friendData = new JSONObject();
				PlayerRelation targetRelation = findRelation(player.getId());
				//如果是好友
				if (targetRelation != null && targetRelation.getFriends() != null && targetRelation.getFriends().findPlayer(friendId) != -1) {
					FriendHome friendHome = player.getFriendHome(friendId);
					friendData.put("clearTimes", (HELP_FRIEND_ONEPLAYER_NUM_EVERYDAY - friendHome.getTodayClearTimes()));
					friendData.put("transTimes", (SEND_GIFT_ONEPLAYER_NUM_EVERYDAY - friendHome.getTodayTransportTimes()));
					friendData.put("isMyFriend", 1);
					
					
					//田地的状态
					int field = getState(friendHome,FIELD, player,player.getPlayerSns().getFirstVisit() == 0);
					if(field == 0){
						friendData.put("fieldType",0);
					}else{
						HelpFriendTemplate helpFriendTemplate = (HelpFriendTemplate)Platform.getAppContext().get(TextDataService.class).getTemplates(HelpFriendTemplate.class).get(field);
						friendData.put("fieldType",helpFriendTemplate.getOperateType());
					}
					friendData.put("field",field);
					//房屋的状态
					
					int house = getState(friendHome,HOUSE, player,player.getPlayerSns().getFirstVisit() == 0);
					if(house == 0){
						friendData.put("houseType",0);
					}else{
						HelpFriendTemplate helpFriendTemplate = (HelpFriendTemplate)Platform.getAppContext().get(TextDataService.class).getTemplates(HelpFriendTemplate.class).get(house);
						friendData.put("houseType",helpFriendTemplate.getOperateType());
					}
					if(player.getPlayerSns().getFirstVisit() == 0){
						player.getPlayerSns().setFirstVisit(1);
						psService.savePlayerSns(player);
					}
					friendData.put("house", house);
					friendData.put("npcId",getNpc(friendHome, player));
					TradeService tradeService = Platform.getAppContext().get(TradeService.class);
					JSONArray tradeArray = tradeService.getTradeListByPlayerId(friendId);
					if(tradeArray.size() == 0){
						friendData.put("tradeState", 0);
					}else{
						friendData.put("tradeState", 1);
					}
					friendData.put("tradeList", tradeArray.toString());
					
					MarketService marketService = Platform.getAppContext().get(MarketService.class);
					friendData.put("marketStatus", marketService.marketIsUpload(friendId));
					if(jsonData.containsKey("player")){
						friendData.put("achieve", JSONObject.fromObject(jsonData.get("player")).get("achieve"));
					}
					
				} else {
					friendData.put("clearTimes", 0);
					friendData.put("transTimes", 0);
					friendData.put("isMyFriend", 0);
					friendData.put("fieldType",0);
					friendData.put("field",0);
					friendData.put("houseType",0);
					friendData.put("house",0);
					friendData.put("npcId",0);
					friendData.put("tradeState", 0);
					friendData.put("tradeList", "[]");
					friendData.put("marketStatus",0);
					friendData.put("achieve", new JSONArray());
				}
				friendData.put("id", friend.getId());
				friendData.put("nickname", friend.getName() == null ? "" : friend.getName());
				friendData.put("icon", friend.getIcon() == null ? "" : friend.getIcon());
				friendData.put("Exp", friend.getExp());
				friendData.put("Level", friend.getLevel());
				friendData.put("rich", friend.getRich());
				friendData.put("raceId", friend.getRaceId());
				friendData.put("isSendGift",isTransport(friend.getId(),player));
				friendData.put("isSendMutliGift",isMutliTransport(player));
				friendData.put("isCanWater", psService.isCanWater(player,friendId) == 0 ? 1 : 0);
				
				
				pt.put("friend", friendData.toString());
				
				//自己的相关信息
				JSONObject myData = new JSONObject();
				myData.put("love", player.getLove());
				int now = (int)(System.currentTimeMillis() / 1000);
				psService.recountPlayerActivePoints(player, now);
				myData.put("activePoints", player.getPlayerSns().getActiveCount());
				myData.put("activeMaxPoints", PlayerSnsService.ACTIVE_MAX_NUM);
				myData.put("recoverTime", psService.getActiveRecoverTime(player.getPlayerSns()));
				pt.put("myData", myData.toString());
				
				session.send(pt);
				playerService.addLove(session,player);
				Platform.getLog().friend(player, friendId);
			}else{
				//好友已经删除，清理自己的好友列表
				PlayerRelation relation = this.findRelation(player.getInstanceId());
				relation.getFriends().removePlayer(friendId);
				this.putRelationToDataCenter(relation);
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_12, packet.getopcode());
				return;
			}
		} catch (Throwable e) {
			log.error("friendHome error",e);
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_0, packet.getopcode());
		}
	}

	/**
	 * 好友搜索
	 * 
	 * @param session
	 */
	@OP(code = HOpCode.PLAYER_FRIEND_SEARCH_CLIENT)
	protected void friendSearch(Packet packet, HSession session) {
		log.info("[HTTPRequest] session:{}  packet:{}]", session.getSessionId(), packet.toString());
		Player player = (Player) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			return;
		}
		String name = packet.getString("name");
		if (name == null || name.length() == 0 || name.length() > 100 || name.indexOf('\'') != -1 || name.indexOf('\"') != -1) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_15, packet.getopcode());
			return;
		}
		log.info("[Friend] friendSearch[try]  player:{} searchword:{}", player.getInstanceId(), name);
		Platform.getLog().searchFriend(player, name);
		JSONArray array = new JSONArray();

		packet.getRunTimeMonitor().knock("friendSearch");
		int id = -1;
		if (Platform.getConfiguration().getBoolean("ID_ENCODE")) {
			id = IdUtil.deCode(name, false);
		} else if (Utils.isNumber(name)) {
			id = Integer.parseInt(name);
		}
		ActorCacheService serivce = Platform.getAppContext().get(ActorCacheService.class);
		// if (id > 0 && id != player.getAccountId()) {
		if (id > 0) {
			PlayerEx firend = Platform.getEntityManager().fetch("from PlayerEx where accountId = ? and exist = 0", id);
			packet.getRunTimeMonitor().knock("db.fetch.accountId");
			if (firend != null) {
				Actor actor = serivce.findActorByCache(firend.getInstanceId());
				if (actor == null) {
					actor = new Actor(firend);
				}
				JSONObject temp = new JSONObject();
				temp.put("id", actor.getId());
				temp.put("level", actor.getLevel());
				temp.put("star", actor.getStar());
				temp.put("name", actor.getName() == null ? "" : firend.getName());
				temp.put("icon", actor.getIcon() == null ? "" : firend.getIcon());
				temp.put("raceId", actor.getRaceId());
				temp.put("rich", actor.getRich());
				if (id == player.getAccountId()) {
					temp.put("me", 1);
				}
				array.add(temp);
			}
			log.info("[Friend] friendSearch[SearchID:{}]  player:{} firend:{}", new Object[] { id, player.getInstanceId(),
					firend == null ? "none " + name : firend.getId() });
		}

		packet.getRunTimeMonitor().knock("ActorCacheService");
		List<Actor> friends = serivce.findActorsByName(name);
		packet.getRunTimeMonitor().knock("findActorsByName");
		Packet pt = new JSONPacket(HOpCode.PLAYER_FRIEND_SEARCH_SERVER);
		if (friends == null || friends.size() == 0) {
			if (array.size() == 0) {
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_50, packet.getopcode());
				return;
			} else {
				pt.put("friends", array);
			}
		} else {
			JSONObject temp;
			for (Actor actor : friends) {
				// if (actor.getId() != player.getInstanceId()) {
				temp = new JSONObject();
				temp.put("id", actor.getId());
				temp.put("level", actor.getLevel());
				temp.put("star", actor.getStar());
				temp.put("name", actor.getName());
				temp.put("icon", actor.getIcon() == null ? "" : actor.getIcon());
				temp.put("raceId", actor.getRaceId());
				temp.put("rich", actor.getRich());
				temp.put("me", actor.getId() == player.getId() ? 1 : 0);
				//是否在线，读取缓存中的数据
				Actor newActor = serivce.findActorByCache(actor.getId());
				if(newActor != null){
					temp.put("online", newActor.isOnline() ? 1 : 0);
				}
				else{
					temp.put("online", actor.isOnline() ? 1 : 0);
				}
				array.add(temp);
				// }
			}
			if (array.size() == 0) {
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_50, packet.getopcode());
				return;
			}
			pt.put("friends", array.toString());
			log.info("[Friend] friendSearch[OK]  player:{} listsize:{}", player.getInstanceId(), array.size());
		}
		packet.getRunTimeMonitor().knock("JSONPacket");
		session.send(pt);
	}
	/**
	 * 点击出现的npc获取奖励
	 * @param packet
	 * @param session
	 */
	@OP(code = HOpCodeEx.CLICK_NPC_CLIENT)
	protected void clickNpc(Packet packet, HSession session) {
		try {
			PlayerEx player = (PlayerEx) session.client();
			if (player == null) {
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
				return;
			}
			int npcId = packet.getInt("npcId");
			int friendId = packet.getInt("friendId");
			
			RandomNpcTemplate randomNpcTemplate = (RandomNpcTemplate)Platform.getAppContext().get(TextDataService.class).getTemplates(RandomNpcTemplate.class).get(npcId);
			if(randomNpcTemplate == null){
				ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_113, packet.getopcode());
				return;
			}
			
			PlayerRelation relation = this.findRelation(player.getInstanceId());
			//没有这个好友
			if (relation == null || relation.getFriends() == null || relation.getFriends().findPlayer(friendId) == -1) {
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_10, packet.getopcode());
				return;
			}
			
			//该用户不存在
			ActorCacheService actorCacheService = Platform.getAppContext().get(ActorCacheService.class);
			Actor actor = actorCacheService.findActor(friendId);
			if(actor == null) {
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_13, packet.getopcode());
				return;
			}
			
			PlayerSnsService psService = Platform.getAppContext().get(PlayerSnsService.class);
			
			//体力值不足
			int now = (int)(System.currentTimeMillis() / 1000);
			
			psService.recountPlayerActivePoints(player, now);
			if(player.getPlayerSns().getActiveCount() < randomNpcTemplate.getConsumePower()){
				ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_114, packet.getopcode());
				return;
			}
			
			//有没有这个操作
			FriendHome friendHome = player.getFriendHome(friendId);
			if(friendHome.getNpcId() != npcId){
				ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_113, packet.getopcode());
				return;
			}
			
			//减少体力值
			
			player.getPlayerSns().setActiveCount(player.getPlayerSns().getActiveCount() - randomNpcTemplate.getConsumePower());
			psService.savePlayerSns(player);
			
			//增加爱心值
			int love = psService.addLove(randomNpcTemplate.getGameAwardId(), player, friendId);
			
			//通知保存
			player.notifySave();
			
			//增加日志用来记录
			IUserOptLogService userOptLogService = Platform.getAppContext().get(IUserOptLogService.class);
			userOptLogService.addUserOptLog(session, player.getId(), UserOptLog.RANDOM_NPC, npcId,UserOptLog.CONTENT_1);
			//清除npcId;
			friendHome.setNpcId(0);
			
			Packet pt = new JSONPacket(HOpCodeEx.CLICK_NPC_SERVER);
			pt.put("love", love);
			pt.put("result", 1);
			pt.put("friendId", friendId);
			pt.put("npcId", npcId);
			pt.put("gameAwardId", randomNpcTemplate.getGameAwardId());
			pt.put("activePoints", player.getPlayerSns().getActiveCount());
			pt.put("activeMaxPoints", PlayerSnsService.ACTIVE_MAX_NUM);
			pt.put("recoverTime", psService.getActiveRecoverTime(player.getPlayerSns()));
			session.send(pt);
			PlayerServiceEx playerService = (PlayerServiceEx) Platform.getAppContext().get(PlayerService.class);
			playerService.addLove(session,player);
		} catch (Throwable e) {
			log.error("clickNpc error",e);
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_0, packet.getopcode());
		}
	}
	/**
	 * 清理好友
	 * @param packet
	 * @param session
	 */
	@OP(code = HOpCodeEx.FRIEND_CLEAR_HOME_CLIENT)
	protected void clearFriendHome(Packet packet, HSession session) {
		try {
			PlayerEx player = (PlayerEx) session.client();
			if (player == null) {
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
				return;
			}
			
			int friendId = packet.getInt("friendId");
			int operate = packet.getInt("operate");
			
			HelpFriendTemplate helpFriendTemplate = (HelpFriendTemplate)Platform.getAppContext().get(TextDataService.class).getTemplates(HelpFriendTemplate.class).get(operate);
			if(helpFriendTemplate == null){
				ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_115, packet.getopcode());
				return;
			}
			
			PlayerRelation relation = this.findRelation(player.getInstanceId());
			//没有这个好友
			if (relation == null || relation.getFriends() == null || relation.getFriends().findPlayer(friendId) == -1) {
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_10, packet.getopcode());
				return;
			}
			
			//该用户不存在
			ActorCacheService actorCacheService = Platform.getAppContext().get(ActorCacheService.class);
			Actor actor = actorCacheService.findActor(friendId);
			if(actor == null) {
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_13, packet.getopcode());
				return;
			}
			
			//不能大于最大次数
			FriendHome friendHome = player.getFriendHome(friendId);
			if (friendHome.getTodayClearTimes() >= HELP_FRIEND_ONEPLAYER_NUM_EVERYDAY) {
				ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_116, packet.getopcode());
				return;
			}
			
			PlayerSnsService psService = Platform.getAppContext().get(PlayerSnsService.class);
			
			//体力值不足
			int now = (int)(System.currentTimeMillis() / 1000);
			
			psService.recountPlayerActivePoints(player, now);
			if(player.getPlayerSns().getActiveCount() < helpFriendTemplate.getConsumePower()){
				ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_114, packet.getopcode());
				return;
			}
			
			//有没有这个操作
			if(friendHome.getTodayFieldState() != operate && friendHome.getTodayHouseState() != operate){
				ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_115, packet.getopcode());
				return;
			}
			
			
			//减少体力值
			player.getPlayerSns().setActiveCount(player.getPlayerSns().getActiveCount() - helpFriendTemplate.getConsumePower());
			psService.savePlayerSns(player);
			
			//增加爱心值
			int love = psService.addLove(helpFriendTemplate.getGameAwardId(), player, friendId);
			
			//增加次数
			friendHome.addClearTimes();
			
			//通知保存
			player.notifySave();
			
			//发送信件
			IAkMailService<MailEx> service = Platform.getAppContext().get(IAkMailService.class);
			JSONObject jsObj = new JSONObject();
			jsObj.put("name", (player.getName() == null || player.getName() == "") ? "" : player.getName());
			if(helpFriendTemplate.getType() == 1){
				jsObj.put("operate", helpFriendTemplate.getOperateType());
			}else{
				jsObj.put("operate", helpFriendTemplate.getOperateType()+helpFriendTemplate.getType());
			}
			jsObj.put("love", love);
			service.sendSystemMailUnInteractive(player.getInstanceId(), friendId, player.getName(), MailEx.TEMPLATEID_3226, player.getIcon(), player.getLevel(), player.getRich(), player.getRaceId(),jsObj.toString());
			
			
			//增加日志用来记录
			IUserOptLogService userOptLogService = Platform.getAppContext().get(IUserOptLogService.class);
			userOptLogService.addUserOptLog(session, player.getId(), UserOptLog.HELP_FRIEND, operate,UserOptLog.CONTENT_1);
			
			Packet pt = new JSONPacket(HOpCodeEx.FRIEND_CLEAR_HOME_SERVER);
			pt.put("love", love);
			pt.put("result", 1);
			pt.put("operate", operate);
			pt.put("friendId", friendId);
			pt.put("gameAwardId", helpFriendTemplate.getGameAwardId());
			
			//清除好友的这个状态
			if(friendHome.getTodayFieldState() == operate){
				friendHome.setFieldState(FriendHome.STATE_NULL);
			}else if(friendHome.getTodayHouseState() == operate){
				friendHome.setHouseState(FriendHome.STATE_NULL);
			}
			
			//田地的状态
			
			int field = friendHome.getTodayFieldState();
			if(field == 0){
				pt.put("fieldType",0);
			}else{
				helpFriendTemplate = (HelpFriendTemplate)Platform.getAppContext().get(TextDataService.class).getTemplates(HelpFriendTemplate.class).get(field);
				pt.put("fieldType",helpFriendTemplate.getOperateType());
			}
			pt.put("field",field);
			//房屋的状态
			int house = friendHome.getTodayHouseState();
			if(house == 0){
				pt.put("houseType",0);
			}else{
				helpFriendTemplate = (HelpFriendTemplate)Platform.getAppContext().get(TextDataService.class).getTemplates(HelpFriendTemplate.class).get(house);
				pt.put("houseType",helpFriendTemplate.getOperateType());
			}
			pt.put("house",house);
			
			
			pt.put("activePoints", player.getPlayerSns().getActiveCount());
			pt.put("activeMaxPoints", PlayerSnsService.ACTIVE_MAX_NUM);
			pt.put("recoverTime", psService.getActiveRecoverTime(player.getPlayerSns()));
			
			session.send(pt);
			PlayerServiceEx playerService = (PlayerServiceEx) Platform.getAppContext().get(PlayerService.class);
			playerService.addLove(session,player);
		} catch (Throwable e) {
			log.error("clearFriendHome",e);
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_0, packet.getopcode());
		}
	}

	/**
	 * 好友访问, 运送小动物
	 * 
	 * @param session
	 */
	@OP(code = HOpCodeEx.FRIEND_TRANSPORT_CLIENT)
	protected void transportFriendHome(Packet packet, HSession session) {
		try {
			//是否登录
			PlayerEx player = (PlayerEx) session.client();
			if (player == null) {
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
				return;
			}
			
			int friendId = packet.getInt("friendId");
			
			PlayerRelation relation = this.findRelation(player.getInstanceId());
			//没有这个好友
			if (relation == null || relation.getFriends() == null || relation.getFriends().findPlayer(friendId) == -1) {
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_10, packet.getopcode());
				return;
			}
			//该用户不存在
			ActorCacheService actorCacheService = Platform.getAppContext().get(ActorCacheService.class);
			Actor actor = actorCacheService.findActor(friendId);
			if(actor == null) {
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_13, packet.getopcode());
				return;
			}
			
			JSONObject js = transportToFriendHome(session,actor,player,false);
			if(js.getInt("result") == -1){
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_13, packet.getopcode());
				return;
			}else if(js.getInt("result") == -2){
				ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_117, packet.getopcode());
				return;
			}else if(js.getInt("result") == -3){
				ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_114, packet.getopcode());
				return;
			}else if(js.getInt("result") == -4){
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_0, packet.getopcode());
				return;
			}
			Packet pt = new JSONPacket(HOpCodeEx.FRIEND_TRANSPORT_SERVER);
			pt.put("love", js.getInt("love"));
			pt.put("gameAwardId", js.getInt("gameAwardId"));
			pt.put("result", 1);
			pt.put("friendId", friendId);
			pt.put("isSendGift",isTransport(actor.getId(),player));
			pt.put("isSendMutliGift",isMutliTransport(player));
			PlayerSnsService psService = Platform.getAppContext().get(PlayerSnsService.class);
			int now = (int)(System.currentTimeMillis() / 1000);
			psService.recountPlayerActivePoints(player, now);
			pt.put("activePoints", player.getPlayerSns().getActiveCount());
			pt.put("activeMaxPoints", PlayerSnsService.ACTIVE_MAX_NUM);
			pt.put("recoverTime", psService.getActiveRecoverTime(player.getPlayerSns()));
			session.send(pt);
			PlayerServiceEx playerService = (PlayerServiceEx) Platform.getAppContext().get(PlayerService.class);
			playerService.addLove(session,player);
		} catch (Throwable e) {
			log.error("transportFriendHome",e);
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_0, packet.getopcode());
		}
	}
	/**
	 * 给多个好友发送礼物
	 * @param packet
	 * @param session
	 */
	@OP(code = HOpCodeEx.MULTI_FRIEND_TRANSPORT_CLIENT)
	protected void transportTenFriendHome(Packet packet, HSession session) {
		try {
			//是否登录
			PlayerEx player = (PlayerEx) session.client();
			if (player == null) {
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
				return;
			}
			
			int friendId = packet.getInt("friendId");
			
			PlayerRelation relation = this.findRelation(player.getInstanceId());
			int count = 0;
			int allLove = 0;
			for(int i = 0;i < relation.getFriends().actors.size();i++){
				Actor actor = relation.getFriends().actors.get(i);
				JSONObject js = transportToFriendHome(session,actor,player,true);
				if(js.getInt("result") == 1){
					count++;
					allLove += js.getInt("love");
				}
				if(count >= SEND_MULTI_GIFT_NUM){
					break;
				}
			}
			Packet pt = new JSONPacket(HOpCodeEx.MULTI_FRIEND_TRANSPORT_SERVER);
			pt.put("isSendGift",isTransport(friendId,player));
			pt.put("isSendMutliGift",isMutliTransport(player));
			pt.put("result", 1);
			pt.put("friendId", friendId);
			pt.put("love", allLove);
			PlayerSnsService psService = Platform.getAppContext().get(PlayerSnsService.class);
			int now = (int)(System.currentTimeMillis() / 1000);
			psService.recountPlayerActivePoints(player, now);
			pt.put("activePoints", player.getPlayerSns().getActiveCount());
			pt.put("activeMaxPoints", PlayerSnsService.ACTIVE_MAX_NUM);
			pt.put("recoverTime", psService.getActiveRecoverTime(player.getPlayerSns()));
			session.send(pt);
			PlayerServiceEx playerService = (PlayerServiceEx) Platform.getAppContext().get(PlayerService.class);
			playerService.addLove(session,player);
		} catch (Throwable e) {
			log.error("transportTenFriendHome",e);
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_0, packet.getopcode());
		}
	}
	/**
	 * 今天是否还可以送礼物
	 * @param friendId
	 * @param player
	 * @return
	 */
	public int isTransport(int friendId, PlayerEx player){
		PlayerRelation relation = this.findRelation(player.getInstanceId());
		//没有这个好友
		if (relation == null || relation.getFriends() == null || relation.getFriends().findPlayer(friendId) == -1) {
			return 0;
		}
		//不能大于最大次数
		FriendHome friendHome = player.getFriendHome(friendId);
		if (friendHome.getTodayTransportTimes() >= SEND_GIFT_ONEPLAYER_NUM_EVERYDAY) {
			return 0;
		}
		return 1;
	}
	/**
	 * 今天是否还可以一次性送多个礼物
	 * @param player
	 * @return
	 */
	public int isMutliTransport(PlayerEx player){
		PlayerRelation relation = this.findRelation(player.getInstanceId());
		if(relation != null){
			for(int i = 0;i < relation.getFriends().actors.size();i++){
				Actor actor = relation.getFriends().actors.get(i);
				int num = isTransport(actor.getId(),player);
				if(num == 1){
					return 1;
				}
			}
		}
		return 0;
	}
	/**
	 * 送礼物
	 * @param friend
	 * @param player
	 * @param isMutliSend true多人送礼 false单人送礼
	 * @return -1用户不存在；-2不能大于最大次数；-3体力值不足；4服务器错误；大于等于0正确，增加爱心值
	 */
	public JSONObject transportToFriendHome(HSession session, Actor friend, PlayerEx player,boolean isMutliSend){
		JSONObject js = new JSONObject();
		//该用户不存在
		ActorCacheService actorCacheService = Platform.getAppContext().get(ActorCacheService.class);
		Actor actor = actorCacheService.findActor(friend.getId());
		if(actor == null) {
			js.put("result", -1);
			return js;
		}
		//不能大于最大次数
		FriendHome friendHome = player.getFriendHome(friend.getId());
		if (friendHome.getTodayTransportTimes() >= SEND_GIFT_ONEPLAYER_NUM_EVERYDAY) {
			js.put("result", -2);
			return js;
		}
		try {
			//如果没随机到,填表正确的话，不会触发这个错误
			IRandom random = RandomUtil.getRandomGift(player,RandomUtil.GIFT , 0,false);
			if(random == null){
				js.put("result", -4);
				return js;
			}
			SendGiftTemplate sendGiftTemplate = (SendGiftTemplate)random;
			
			PlayerSnsService psService = Platform.getAppContext().get(PlayerSnsService.class);
			
			//体力值不足
			int now = (int)(System.currentTimeMillis() / 1000);
			
			psService.recountPlayerActivePoints(player, now);
			if(player.getPlayerSns().getActiveCount() < sendGiftTemplate.getConsumePower()){
				js.put("result", -3);
				return js;
			}
			//减少体力值
			player.getPlayerSns().setActiveCount(player.getPlayerSns().getActiveCount() - sendGiftTemplate.getConsumePower());
			psService.savePlayerSns(player);
			
			//增加爱心值
			int love = psService.addLove(sendGiftTemplate.getGameAwardId(), player, friend.getId());
			
			//增加次数
			friendHome.addTransportTimes();
			
			//通知保存
			player.notifySave();
			
			//发送信件
			IAkMailService<MailEx> service = Platform.getAppContext().get(IAkMailService.class);
			JSONObject jsObj = new JSONObject();
			jsObj.put("name", (player.getName() == null || player.getName() == "") ? "" : player.getName());
			service.sendSystemMailHaveAwardUser(player.getInstanceId(), friend.getId(), player.getName(), MailEx.TEMPLATEID_3225, player.getIcon(), player.getLevel(), player.getRich(), player.getRaceId(),jsObj.toString(),sendGiftTemplate.getGameAwardId());
			//如果不是送多个好友礼物，添加玩家获得物品日志
			if(!isMutliSend){
				//增加日志用来记录
				IUserOptLogService userOptLogService = Platform.getAppContext().get(IUserOptLogService.class);
				userOptLogService.addUserOptLog(session, player.getId(), UserOptLog.SEND_GIFT, sendGiftTemplate.getId(),UserOptLog.CONTENT_1);
			}
			
			//添加小动物
			Platform.getEntityManager().createSync(new PlayerTransport(friend.getId(), player));
			js.put("result", 1);
			js.put("gameAwardId", sendGiftTemplate.getGameAwardId());
			js.put("love", love);
			return js;
		} catch (Throwable e) {
			e.printStackTrace();
			js.put("result", -4);
			return js;
		}
		
		
	}
	/**
	 * 获取好友关系列表
	 * 
	 * @param session
	 */
	@OP(code = HOpCode.PLAYER_FRIEND_LIST_CLIENT)
	protected void list(Packet packet, HSession session) {
		log.info("[HTTPRequest] session:{}  packet:{}", session.getSessionId(), packet.toString());
		PlayerEx player = (PlayerEx) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			return;
		}
		log.info("[Friend] try get Friend list  playerId:{}", player.getInstanceId());
		PlayerRelation playerRelation = findRelation(player.getId());
		packet.getRunTimeMonitor().knock("findRelation");
		Packet pt = new JSONPacket(HOpCode.PLAYER_FRIEND_LIST_SERVER);
		if (playerRelation == null || playerRelation.getFriends() == null || playerRelation.getFriends().actors.size() == 0) {
			pt.put("friends", "[]");
		} else {
			ActorCacheService acservice = Platform.getAppContext().get(ActorCacheService.class);
			List<Actor> actors = playerRelation.getFriends().actors;
			JSONArray temp = new JSONArray();
			JSONObject act;
			// for (Actor actor : actors) {
			// for(int i = 0; i < actors.size(); i++) {
			for (int i = actors.size() - 1; i >= 0; i--) {
				Actor actor = actors.get(i);
				Actor newActor = acservice.findActorByCache(actor.getId());
				if (newActor != null) {
					actor = newActor;
				}
				act = new JSONObject();
				act.put("id", actor.getId());
				act.put("num", i);
				act.put("name", actor.getName() == null ? "" : actor.getName());
				act.put("icon", actor.getIcon() == null ? "" : actor.getIcon());
				act.put("level", actor.getLevel());
				act.put("star", actor.getStar());
				act.put("state", player.getFriendHome(actor.getId()).getState());
				act.put("isMutualn", isFriend(actor.getId(), player.getInstanceId()) ? 1 : 0);
				act.put("raceId", actor.getRaceId());
				act.put("rich", actor.getRich());
				act.put("online", actor.isOnline() ? 1 : 0);
				temp.add(act);
			}
			pt.put("friends", temp.toString());
			log.info("[Friend] list  playerId:{} listSize:{}", player.getInstanceId(), actors.size());
		}
		session.send(pt);
		log.info("[list] return[null]");
	}

	private boolean isFriend(int myId, int targetId) {
		PlayerRelation playerRelation = findRelation(myId);
		if (playerRelation == null) {
			return false;
		} else if (playerRelation.getFriends() == null) {
			return false;
		} else if (playerRelation.getFriends().findPlayer(targetId) == -1) {
			return false;
		}
		return true;
	}

	/**
	 * 新增好友
	 * 
	 * @param session
	 */
	@OP(code = HOpCode.PLAYER_FRIEND_ADD_CLIENT)
	protected void add(Packet packet, HSession session) {
		log.info("[HTTPRequest] session:{}  packet:{}]", session.getSessionId(), packet.toString());
		PlayerEx player = (PlayerEx) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			return;
		}
		int friendId = packet.getInt("id");
		if (player.getInstanceId() == friendId) {
			log.info("[Friend] add[Fail] playerId:{}, target:{}, case: playerId() == friendId.", player.getInstanceId(), friendId);
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_43, packet.getopcode());
			return;
		}
		log.info("[Friend] add Friend[try] playerId:{}, target:{}", player.getInstanceId(), friendId);
		ActorCacheService actorCacheService = Platform.getAppContext().get(ActorCacheService.class);
		Actor actor = actorCacheService.findActor(friendId);
		packet.getRunTimeMonitor().knock("findActor:friendId");
		if (actor == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_5, packet.getopcode());
			return;
		}
		PlayerRelation playerRelation = findRelation(player.getId());
		packet.getRunTimeMonitor().knock("findRelation");
		if (playerRelation == null) {
			playerRelation = new PlayerRelation();
			playerRelation.setFriends(new RelationList());
			playerRelation.setId(player.getId());
			log.info("[Relation] relationAdd:save new PlayerRelation to DB playerId:{}", playerRelation.getId());
			Platform.getEntityManager().createSync(playerRelation);
			log.info("[Relation] relationAdd:success playerId:{}", playerRelation.getId());
		} else if (playerRelation.getFriends() == null) {
			playerRelation.setFriends(new RelationList());
		}

		if (playerRelation.getFriends().actors.size() > FRIEND_MAX_LIMIT) {
			log.info("[Friend] add[Fail] playerId:{}, target:{}, case: playerId() == friendId.", player.getInstanceId(), friendId);
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_FIREND_MAX_LIMIT, packet.getopcode());
			return;
		}

		boolean isMutualn = false;// 是否互为好友
		PlayerRelation targetRelation = findRelation(actor.getId());
		packet.getRunTimeMonitor().knock("findtargetRelation");
		if (targetRelation == null) {
			targetRelation = new PlayerRelation();
			targetRelation.setFriends(new RelationList());
			targetRelation.setId(actor.getId());
			Platform.getEntityManager().createSync(targetRelation);
		} else if (targetRelation.getFriends() == null) {
			targetRelation.setFriends(new RelationList());
		}
		if (playerRelation.getFriends().findPlayer(actor.getId()) != -1) {
			log.info("[Friend] add[Fail] playerId:{}, target:{} already is Friend yet.", player.getInstanceId(), friendId);
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_6, packet.getopcode());
			return;
		} else {
			playerRelation.getFriends().addPlayer(actor);
			Platform.getEntityManager().updateSync(playerRelation);
			putRelationToDataCenter(playerRelation);
			log.info("[Friend] add[OK] playerId:{}, target:{}", player.getInstanceId(), friendId);
			//发送信件
			IAkMailService<MailEx> service = Platform.getAppContext().get(IAkMailService.class);
			JSONObject jsObj = new JSONObject();
			jsObj.put("name", (player.getName() == null || player.getName() == "") ? "" : player.getName());
			service.sendSystemMailAddFriend(player.getId(), friendId, player.getName(), MailEx.TEMPLATEID_3221, player.getIcon(), player.getLevel(), player.getRich(), player.getRaceId(),jsObj.toString());
			packet.getRunTimeMonitor().knock("findtargetRelation");
		}
		Packet pt = new JSONPacket(HOpCode.PLAYER_FRIEND_ADD_SERVER);
		pt.put("id", actor.getId());
		pt.put("name", actor.getName() == null ? "" : actor.getName());
		pt.put("icon", actor.getIcon() == null ? "" : actor.getIcon());
		pt.put("level", actor.getLevel());
		pt.put("star", actor.getStar());
		pt.put("state", player.getFriendHome(actor.getId()).getState());
		pt.put("isMutualn", isMutualn ? 1 : 0);
		pt.put("raceId", actor.getRaceId());
		pt.put("rich", actor.getRich());
		pt.put("online", actor.isOnline() ? 1 : 0);
		session.send(pt);
		log.info("[add] return[null]");
	}

	/**
	 * loadInSnsFriend 导入sns好友
	 */
	public JSONArray addSNSFriend(Player p, List<Actor> friends) throws Exception {
		PlayerEx player = (PlayerEx) p;
		log.info("[RelationService] addFriend(Player [id={}], List<Actor>[size={})]", player.getInstanceId(), friends.size());
		JSONArray ja = new JSONArray();
		PlayerRelation playerRelation = findRelation(player.getId());
		if (playerRelation == null) {
			playerRelation = new PlayerRelation();
			playerRelation.setFriends(new RelationList());
			playerRelation.setId(player.getId());
			putRelationToDataCenter(playerRelation);
			log.info("[Relation] relationAdd:save new PlayerRelation to DB playerId:{}", playerRelation.getId());
			Platform.getEntityManager().createSync(playerRelation);
			log.info("[Relation] relationAdd:success playerId:{}", playerRelation.getId());
		}
		boolean isChange = false;
		Actor playerActor = Platform.getAppContext().get(ActorCacheService.class).findActor(player.getId());
		for (Actor actor : friends) {
			log.info("[RelationService] addFriend loop: find actor: {}", actor.getId());
			if (playerRelation.getFriends().findPlayer(actor.getId()) != -1) {
				log.info("[RelationService] addFriend loop: continue actor: {}", actor.getId());
				continue;
			} else {
				log.info("[RelationService] addFriend loop: addPlayer actor: {}", actor.getId());

				// 将sns好友加到自己的好友中
				playerRelation.getFriends().addPlayer(actor);
				Platform.getEntityManager().updateSync(playerRelation);
				this.putRelationToDataCenter(playerRelation);
				JSONObject jo = new JSONObject();
				jo.put("id", actor.getId());
				jo.put("name", actor.getName());
				jo.put("icon", actor.getIcon());
				jo.put("level", actor.getLevel());
				jo.put("star", actor.getStar());
				jo.put("state", player.getFriendHome(actor.getId()).getState());
				jo.put("isMutualn", 1);
				jo.put("raceId", actor.getRaceId());
				jo.put("rich", actor.getRich());
				ja.add(jo);
				// 将自己加到sns好友的游戏内好友中
				PlayerRelation friendRelation = findRelation(actor.getId());
				log.info("[RelationService] addFriend loop: friendRelation add player: {}", friendRelation == null ? "[null]"
						: "[friendRelation]");
				if (friendRelation == null) {
					friendRelation = new PlayerRelation();
					friendRelation.setFriends(new RelationList());
					friendRelation.setId(actor.getId());
					this.putRelationToDataCenter(friendRelation);
					log.info("[Relation] relationAdd:save new friendRelation to DB playerId:{}", friendRelation.getId());
					Platform.getEntityManager().createSync(friendRelation);
					log.info("[Relation] relationAdd:success playerId:{}", friendRelation.getId());
				}
				if (friendRelation.getFriends().findPlayer(player.getId()) == -1) {
					friendRelation.getFriends().addPlayer(playerActor);
					Platform.getEntityManager().updateSync(friendRelation);
					this.putRelationToDataCenter(friendRelation);
				}
				isChange = true;
			}
		}
		if (isChange) {
			return ja;
		}
		log.info("[RelationService] addFriend return [null]");
		return null;
	}

	/**
	 * 删除好友
	 * 
	 * @param session
	 */
	@OP(code = HOpCode.PLAYER_FRIEND_DEL_CLIENT)
	protected void delete(Packet packet, HSession session) {
		log.info("[HTTPRequest] packet:{} session :{}", packet.toString(), session.getSessionId());
		Player player = (Player) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			return;
		}
		int friendId = packet.getInt("id");
		if (player.getInstanceId() == friendId) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_4, packet.getopcode());
			return;
		}

		PlayerRelation playerRelation = findRelation(player.getId());
		if (playerRelation == null || playerRelation.getFriends() == null || playerRelation.getFriends().findPlayer(friendId) == -1) {
//			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_7, packet.getopcode());
//			return;
		} else {
			playerRelation.getFriends().removePlayer(friendId);
			Platform.getEntityManager().updateSync(playerRelation);
			this.putRelationToDataCenter(playerRelation);
//			Platform.getEventManager().addEvent(new Event(GameEvent.EVENT_RELATION_CHANGE, playerRelation));
//			ActorCacheService actorCacheService = Platform.getAppContext().get(ActorCacheService.class);
//			Actor actor = actorCacheService.findActor(friendId);
//			if (actor != null && isFriend(actor.getId(), player.getId())) {
//				MailService<Mail> mailService = Platform.getAppContext().get(MailService.class);
//邮件系统有改动，这个接口注掉
//				mailService.sendSystemMailNoFilter(player.getInstanceId(), friendId, MailTemplateStatus.MAIL_TEMPLATE_DELETE_FRIEND,
//						player.getLang(), player.getName(), actor.getName(), MailUseType.MAIL_FRIEND_REQUEST_DELETE_FRIEND);
//			}
		}
		Packet pt = new JSONPacket(HOpCode.PLAYER_FRIEND_DEL_SERVER);
		pt.put("result", 1);
		pt.put("id", friendId);
		session.send(pt);
		log.info("[METHODEND] return[null]");
	}

	// TODO 优化特殊好友, 从内存中读取. 弱引用, 等级为key, 容量,遍历

	// SoftReference cache = new SoftReference();

	// TIntObjectMap<Actor> recommendFriends = new TIntObjectHashMap<Actor>();

	/**
	 * 推荐好友 1、非自己当前的好友 2、等级相等3、等级相差最少中，星座相等。
	 * 以上为旧版规则，新版如下：
	 * 根据玩家当前等级的正负10级内，登录时间降序排列，取10条记录。
	 * 如有其他规则需要添加则统一在此方法处理
	 * 新版使用规则
	 * @param session
	 */
	@OP(code = HOpCodeEx.PLAYER_RANDOM_FRIEND_CLIENT)
	protected void recommendFriend(Packet packet, HSession session) {
		log.info("[HTTPRequest] session:{}  packet:{}", session.getSessionId(), packet.toString());
		PlayerEx player = (PlayerEx) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			return;
		}
		int curLevel = player.getLevel();//当前等级
		int diff = 10;//正负10级差
		int start = 0;//起始位置
		int count = 20;//预读条数
		List<Actor> actors = Platform.getEntityManager().limitQuery("from Actor where level >= ? and level <= ? and exist = 0 order by lastLoginTime desc", start,
					count, curLevel - diff, curLevel + diff);

		Packet pt = new JSONPacket(HOpCodeEx.PLAYER_RANDOM_FRIEND_SERVER);
		ActorCacheService acservice = Platform.getAppContext().get(ActorCacheService.class);
		JSONArray temp = new JSONArray();
		JSONObject act;
		PlayerRelation relation = findRelation(player.getInstanceId());
		int num = 0, totalNum = 10;//总条数为10条
		for (Actor actor : actors) {
			if(actor.getId() == player.getId()){//自己略过
				continue;
			}
			if (relation == null || relation.getFriends() == null || relation.getFriends().findPlayer(actor.getId()) == -1) {
				num++;
				Actor newActor = acservice.findActorByCache(actor.getId());
				if (newActor != null) {
					actor = newActor;
				}
				act = new JSONObject();
				act.put("id", actor.getId());
				act.put("name", actor.getName() == null ? "" : actor.getName());
				act.put("icon", actor.getIcon() == null ? "" : actor.getIcon());
				act.put("level", actor.getLevel());
				act.put("star", actor.getStar());
				act.put("raceId", actor.getRaceId());
				act.put("rich", actor.getRich());
				act.put("online", actor.isOnline() ? 1 : 0);
				temp.add(act);
				if(num == totalNum){
					break;
				}
			}
		}
		pt.put("friends", temp.toString());
		session.send(pt);
		log.info("[recommendFriend] return[null]");
	}

	/**
	 * 随机好友 - 等级接近的玩家 - 星座相同的玩家 - 等级排行靠前的玩家 将以上三类混合乱序推荐给玩家,每次推荐随机推荐20人
	 * 新版已不用
	 * @param session
	 */
	
	protected void randomFriend(Packet packet, HSession session) {
//		log.info("[HTTPRequest] [randomFriend] session:{}  packet:{}", session.getSessionId(), packet.toString());
//		PlayerEx player = (PlayerEx) session.client();
//		if (player == null) {
//			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
//			return;
//		}
//		int level = packet.getInt("level");
//		if (level < 0 || level > PlayerEx.MaxLevel) {
//			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_31, packet.getopcode());
//			return;
//		}
//		
//		Actor[] actors;
//		if(player.getLang() == 0) {//0=自动， 默认为英文。
//			actors = this.randomFriendPool_lang0.getRandomFriend(level);
//		}else if(player.getLang() == 1) {
//			actors = this.randomFriendPool_lang1.getRandomFriend(level);
//		}else {
//			actors = this.randomFriendPool_lang2.getRandomFriend(level);
//		}
//		Packet pt = new JSONPacket(HOpCodeEx.PLAYER_RANDOM_FRIEND_SERVER);
//
//		JSONArray temp = new JSONArray();
//		JSONObject act;
//		// boolean fiter = true;
//		// PlayerRelation relation = findRelation(player.getInstanceId());
//		// if (relation == null || relation.getFriends() == null) {
//		// fiter = false;
//		// }
//		for (Actor actor : actors) {
//			// if (fiter && relation.getFriends().findPlayer(actor.getId()) ==
//			// -1) {
//			if (actor.getId() != player.getInstanceId()) {
//				act = new JSONObject();
//				act.put("id", actor.getId());
//				act.put("name", actor.getName() == null ? "" : actor.getName());
//				act.put("icon", actor.getIcon() == null ? "" : actor.getIcon());
//				act.put("level", actor.getLevel());
//				act.put("star", actor.getStar());
//				act.put("raceId", actor.getRaceId());
//				act.put("rich", actor.getRich());
//				temp.add(act);
//			}
//			// }
//		}
//		pt.put("friends", temp.toString());
//		session.send(pt);
//		log.info("[randomFriend] return[null]");
//			
	}

	/**
	 * 特殊好友
	 * 
	 * @param session
	 */
	@OP(code = HOpCodeEx.PLAYER_SPECIAL_FRIEND_CLIENT)
	protected void specialFriend(Packet packet, HSession session) {
		log.info("[HTTPRequest] session:{}  packet:{}", session.getSessionId(), packet.toString());
		// PlayerEx player = (PlayerEx) session.client();
		// if (player == null) {
		// ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1,
		// packet.getopcode());
		// return;
		// }
		//
		// Packet pt = new JSONPacket(HOpCodeEx.PLAYER_SPECIAL_FRIEND_SERVER);
		//
		// pt.put("friends", specialFriendListJsonStr);
		// session.send(pt);
		// log.info("[specialFriend] return[null]");
	}

	class updateRandomFriendList implements Runnable {
		public void run() {
			try {
				log.info("[SpecialFriend] update[start]");
				randomFriendPool_lang0.initRandomFriendPool();
				randomFriendPool_lang1.initRandomFriendPool();
				randomFriendPool_lang2.initRandomFriendPool();
				log.info("[SpecialFriend] update[OK]");
			} catch (Throwable t) {
				log.error("[SpecialFriend] update[Fail]", t);
			}
		}
	}

}
