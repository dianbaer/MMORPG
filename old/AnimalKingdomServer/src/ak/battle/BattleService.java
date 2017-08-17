package ak.battle;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.procedure.TObjectProcedure;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ak.friend.FriendService;
import ak.player.PlayerEx;
import ak.player.PlayerServiceEx;
import cyou.mrd.Platform;
import cyou.mrd.data.Data;
import cyou.mrd.data.DataKeys;
import cyou.mrd.game.actor.Actor;
import cyou.mrd.game.actor.ActorCacheService;
import cyou.mrd.game.relation.PlayerRelation;
import cyou.mrd.game.relation.RelationService;
import cyou.mrd.io.OP;
import cyou.mrd.io.OPHandler;
import cyou.mrd.io.Packet;
import cyou.mrd.io.http.HSession;
import cyou.mrd.io.http.JSONPacket;
import cyou.mrd.io.tcp.HOpCodeEx;
import cyou.mrd.service.PlayerService;
import cyou.mrd.service.Service;
import cyou.mrd.util.ErrorHandler;
import cyou.mrd.util.Time;

@OPHandler(TYPE = OPHandler.HTTP_EVENT)
public class BattleService  implements Service {
	
	private static final Logger log = LoggerFactory.getLogger(BattleService.class);
	
	private static final int HOUSRBYSECONDS = 3600;
	
	private static final int MAXHOUR = 8;

	@Override
	public String getId() {
		return "BattleService";
	}

	@Override
	public void startup() throws Exception {
		
	}

	@Override
	public void shutdown() throws Exception {
		
	}
	
	protected PlayerEx playerExist(Packet packet, HSession session)
	{
		PlayerEx player = (PlayerEx) session.client();
		// 没有登录，需要重新登录;
		if (player == null) 
		{
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			return null;
		}
		return player;
	}
	
	protected PlayerEx friendPlayerExist(int friendID,int playerID,Packet packet, HSession session)
	{
		// 检查该好友是否存在;
		
		// 获取该玩家的好友列表（FriendService），然后再判断该好友是否在好友列表里;
		FriendService friendService = (FriendService) Platform.getAppContext().get(RelationService.class);
		PlayerRelation playerRelation = friendService.findRelation(playerID);
		if ( playerRelation == null
				|| playerRelation.getFriends() == null 
				|| playerRelation.getFriends().findPlayer(friendID) == -1 )
		{
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_10, packet.getopcode());
			return null;
		}
		
		// 获取player service
		PlayerServiceEx playerService = (PlayerServiceEx) Platform.getAppContext().get(PlayerService.class);
		// 根据好友ID，获取好友的player
		PlayerEx friendPlayer = (PlayerEx)playerService.loadPlayer(friendID);
		
		if ( friendPlayer == null )
		{
			// 好友player不存在，好友删号了？
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_10, packet.getopcode());
			return null;
		}
		// 好友存在
		return friendPlayer;
	}
	
	
	@OP(code = HOpCodeEx.CLIENT_REQUST_SUPPORT)
	public void reqSupport(Packet packet, HSession session) throws Exception {
		
		log.info("[HTTPRequest] session ={} packet:{}", session.getSessionId(), packet.toString());
		
		PlayerEx player = playerExist(packet,session);
		if ( player == null )
		{
			// 没有登录
			return;
		}
		
		// 读取请求支援的好友ID
		int friendID = packet.getInt("id");
		
		log.info("[Battle Support] request support,player({}) friendId:{}", player, friendID);
		
		// 检查该好友是否存在;	
		PlayerEx friendPlayer = friendPlayerExist(friendID,player.getId(),packet,session);
		if ( friendPlayer == null )
		{
			// 好友不存在，异常已发，直接返回
			return;
		}
		
		log.info("[Battle Support] request support,friendPlayer({}) friendId:{}", friendPlayer, friendID);
		
		// 存在，检查是否可以请求，已请求判断
		int playerID = player.getInstanceId();
		
		// 从data center中获取战斗支援数据
		Data data = Platform.dataCenter().getData(DataKeys.battleSupportKey(friendID));
		BattleSupportData battleSupportData;
		boolean needSendNewData = false;
		if (data == null) // 这个玩家的这个好友还没有战斗支援数据
		{
			// 在对应好友的player身上建立数据
			needSendNewData = true;
			battleSupportData = new BattleSupportData();
			log.info("[reqSupport] sendNewData ", friendID);
		}
		else
		{
			log.info("[reqSupport] battle support data : ", data.value);
			battleSupportData = (BattleSupportData) data.value;
		}
		if ( battleSupportData == null )
		{
			// exception
			return;
		}
		TIntObjectMap<BattleSupport> battleSupportMap = battleSupportData.getBattleSupportMap();
		boolean hadSended = battleSupportMap.containsKey(playerID);
		int curTime = (int)(Time.currTime/1000L);
		if ( hadSended )
		{
			// 已发过请求
			// 也许这里需要加上时间判断
			// 重设时间
			BattleSupport battleSupport = battleSupportMap.get(playerID);
			battleSupport.setTime(curTime);
		}
		else
		{
			// 没有请求过，第一次请求
			BattleSupport battleSupport = new BattleSupport( playerID,curTime );
			battleSupportMap.put( playerID , battleSupport );
		}
		if ( data != null && needSendNewData == false )
		{
			data.value = battleSupportData;	
		}
		boolean saveSuccess = true;
		if ( needSendNewData )
		{
			saveSuccess = Platform.dataCenter().sendNewData(DataKeys.battleSupportKey(friendID), battleSupportData);
		}
		else
		{
			saveSuccess = Platform.dataCenter().sendData(DataKeys.battleSupportKey(friendID),data);	
		}
		int sendValue = 1;
		if ( !saveSuccess )
		{
			sendValue = 0;
		}
		Packet pt = new JSONPacket(HOpCodeEx.SERVER_REQUST_SUPPORT);
		// 保存数据失败，通过发送0值告诉客户端sendValue
		pt.put("result", sendValue);
		// 把时间发下去，防止时间作弊
		pt.put("time",curTime);
		session.send(pt);
		log.info("[METHODEND] return[null]");
		
	}
	
	protected void sendEmptyRequestListToClient(Packet packet,HSession session)
	{
		JSONArray friendsJson = new JSONArray();
		log.info("[Battle Support] request list, list is empty.");
		packet.put("reqList", friendsJson.toString());
		session.send(packet);
	}
	
	@OP(code = HOpCodeEx.CLIENT_SUPPORT_REQUESTLIST)
	public void requestList(Packet packet, HSession session) throws Exception 
	{
		log.info("[HTTPRequest] session ={} packet:{}", session.getSessionId(), packet.toString());
		
		final PlayerEx player = playerExist(packet,session);
		if( null == player )
		{
			// 没有登录
			return;
		}
		log.info("[Battle Support] request list,player({})", player);
		
		Packet pt = new JSONPacket(HOpCodeEx.SERVER_SUPPORT_REQUESTLIST);
		
		final int playerID = player.getInstanceId();
		// 从data center 中获取好友支援的请求列表
		final Data data = Platform.dataCenter().getData(DataKeys.battleSupportKey(playerID));
		if ( null == data )
		{
			// 发送异常给客户端，没有数据表示没有请求列表
			sendEmptyRequestListToClient(pt,session);
			return;
		}
		final BattleSupportData battleSupportData = (BattleSupportData)data.value;
		if ( null == battleSupportData )
		{
			// 发送异常给客户端，没有数据表示没有请求列表
			sendEmptyRequestListToClient(pt,session);
			return;
		}
		final TIntObjectMap<BattleSupport> battleSupportMap = battleSupportData.getBattleSupportMap();
		
		if ( battleSupportMap.isEmpty() )
		{
			sendEmptyRequestListToClient(pt,session);
		}
		else
		{
			final ActorCacheService acservice = Platform.getAppContext().get(ActorCacheService.class);
			final JSONArray friendsJson = new JSONArray();
			//遍历
			battleSupportMap.forEachValue(new TObjectProcedure<BattleSupport>() {
				
				@Override
				public boolean execute(BattleSupport battleSupport) {
					// 这里要把时间超过八小时的过滤掉
					int curTime = (int)(Time.currTime/1000L);
					int saveTime = battleSupport.getTime();
					if ( saveTime <= 0 )
					{
						// exception
					}
					int deltaTime = curTime - saveTime;
					int egihtHousrs = HOUSRBYSECONDS * MAXHOUR;
					if ( deltaTime >= egihtHousrs )
					{
						int friendID = battleSupport.getFriendID();
						battleSupportMap.remove(friendID);
						data.value = battleSupportData;
						Platform.dataCenter().sendData(DataKeys.battleSupportKey(playerID),data);
						log.info("[Battle Support] request list, time > 8 hour, player[] : ,friendID : ",player,friendID);
						return true;	
					}
					int friendId = battleSupport.getFriendID();
					// 获取actor service，从actor中获取好友的名字等，比较快
					String friendName = "";
					String friendIcon = "";
					Actor actor = acservice.findActor(friendId);
					if ( null == actor )
					{
						// 好友不存在了，不发送，并从列表中删除
						battleSupportMap.remove(friendId);
						data.value = battleSupportData;
						Platform.dataCenter().sendData(DataKeys.battleSupportKey(playerID),data);
						log.info("[Battle Support] request list, friend hadn't existed, player[] : ,friendID : ",player,friendId);
						return true;
					}
					else
					{
						friendName = actor.getName();
						friendIcon = actor.getIcon();
					}
					int intTime = battleSupport.getTime();
					JSONObject friend =  new JSONObject();
					friend.put("id", friendId);
					friend.put("time",intTime);
					friend.put("name", friendName);
					friend.put("icon", friendIcon);
					friendsJson.add(friend);
					return true;
				}
			});
			log.info("[Battle Support] request list, list is not empty.");
			pt.put("reqList", friendsJson.toString());
			session.send(pt);
		}
		log.info("[METHODEND] return[null]");
	}
	
	@OP(code = HOpCodeEx.CLIENT_ANSWER_SUPPORT)
	public void answerSupport(Packet packet, HSession session) throws Exception 
	{
		log.info("[HTTPRequest] session ={} packet:{}", session.getSessionId(), packet.toString());
		
		PlayerEx player = playerExist(packet,session);
		if ( null == player )
		{
			// 没有登录
			return;
		}
		
		// 读取好友ID，建筑等级
		// 根据好友ID，获取好友player
		// 将好友等级写到好友player身上
		// 需要支持同意所有的
		
		// 遍历接受的list，取出每一个的level，设置到对应好友的player身上
		
		int buildingLev = packet.getInt("lev");
		JSONArray answerListJson = packet.getJSONArray("list");
		int size = answerListJson.size();
		for ( int i = 0; i < size; ++ i )
		{
			JSONObject infoObject = answerListJson.getJSONObject(i);
			int friendID = infoObject.getInt("id");
			final Data data = Platform.dataCenter().getData(DataKeys.battleSupportKey(friendID));
			// 这里不考虑对应好友不存在的情况，即使不存在，默认支援成功
			if( null != data )
			{
				BattleSupportData battleSupportData = (BattleSupportData)data.value;
				battleSupportData.setBattleSupportFriendID(player.getId());
				battleSupportData.setBattleBuildingLevel(buildingLev);
				data.value = battleSupportData;
				Platform.dataCenter().sendData(DataKeys.battleSupportKey(friendID),data);
			}
			else
			{
				// 这里是好友不存在，然后创建data赋值在好友身上
				BattleSupportData battleSupportData = new BattleSupportData();
				log.info("[answerSupport] new data ", friendID);
				battleSupportData.setBattleSupportFriendID(player.getId());
				battleSupportData.setBattleBuildingLevel(buildingLev);
				Platform.dataCenter().sendNewData(DataKeys.battleSupportKey(friendID), battleSupportData);
			}
			// 若这里存储失败了，则表示支援失败了， 如何处理？
			// 当玩家同意支援后，把ID和建筑等级保存到对应好友的player身上
			Data playerData = Platform.dataCenter().getData(DataKeys.battleSupportKey(player.getId()));
			if ( null != playerData )
			{
				BattleSupportData playerbattleSupportData = (BattleSupportData)playerData.value;
				if ( null != playerbattleSupportData )
				{
					TIntObjectMap<BattleSupport> battleSupportMap = playerbattleSupportData.getBattleSupportMap();
					battleSupportMap.remove(friendID);
					playerData.value = playerbattleSupportData;
					Platform.dataCenter().sendData(DataKeys.battleSupportKey(player.getId()),playerData);
				}
			}
		}
		
		Packet pt = new JSONPacket(HOpCodeEx.SERVER_ANSWER_SUPPORT);
		pt.put("result", 1);
		session.send(pt);
		log.info("[METHODEND] return[null]");
	}
	
	protected void sendSupportDataToClient(HSession session,int level,int friendID,String name,String icon)
	{
		Packet pt = new JSONPacket(HOpCodeEx.SERVER_HAD_SUPPORT);
		pt.put("level", level);
		pt.put("id", friendID);
		pt.put("name", name);
		pt.put("icon", icon);
		session.send(pt);
	}
	
	@OP(code = HOpCodeEx.CLIENT_HAD_SUPPORT)
	public void hadSupport(Packet packet, HSession session) throws Exception
	{
		log.info("[HTTPRequest] session ={} packet:{}", session.getSessionId(), packet.toString());
		
		PlayerEx player = playerExist(packet,session);
		if ( null == player )
		{
			// 没有登录
			return;
		}
		
		Data playerData = Platform.dataCenter().getData(DataKeys.battleSupportKey(player.getId()));
		if ( null == playerData )
		{
			// send empty json
			sendSupportDataToClient(session,0,0,"","");
			log.info("[hadSupport] playerData,send empty json and return directly.");
			return;
		}
		BattleSupportData playerbattleSupportData = (BattleSupportData)playerData.value;
		if ( null == playerbattleSupportData )
		{
			// send empty json
			sendSupportDataToClient(session,0,0,"","");
			log.info("[hadSupport] playerbattleSupportData,send empty json and return directly.");
			return;
		}
		
		
		// 当player身上的BuildingLevel值大于0，表示有支援，否则没有
		int level = playerbattleSupportData.getBattleBuildingLevel();
		int friendID = playerbattleSupportData.getBattleSupportFriendID();
		ActorCacheService acservice = Platform.getAppContext().get(ActorCacheService.class);
		Actor actor = acservice.findActor(friendID);
		String name = "";
		String icon = "";
		if ( null == actor )
		{
			friendID = 0;
			level = 0;
			playerbattleSupportData.setBattleBuildingLevel(level);
			playerbattleSupportData.setBattleSupportFriendID(friendID);
		}
		else
		{
			name = actor.getName();
			icon = actor.getIcon();	
		}
		sendSupportDataToClient(session,level,friendID,name,icon);
		
		//这里暂时在发送之后把这个值设为0，正确的办法是再写一个协议
		playerbattleSupportData.setBattleBuildingLevel(0);
		playerbattleSupportData.setBattleSupportFriendID(0);
		playerData.value = playerbattleSupportData;
		Platform.dataCenter().sendData(DataKeys.battleSupportKey(player.getId()),playerData);
		
		log.info("[METHODEND] return[null]");
	}
}
