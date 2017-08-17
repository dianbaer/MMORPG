/**
 * MarketService.java
 * ak.market
 *
 *   version  date      	author
 * ──────────────────────────────────
 *    1.0	 2013年11月30日 		shiwei2006
 *
 * Copyright (c) 2013, www.cyou-inc.com All Rights Reserved.
 */

package ak.market;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ak.ex.DataKeysEx;
import ak.friend.FriendService;
import ak.mail.IAkMailService;
import ak.mail.MailEx;
import ak.notice.AkNoticeService;
import ak.player.PlayerEx;
import ak.player.PlayerServiceEx;
import ak.server.ErrorHandlerEx;
import ak.util.IRandom;
import ak.util.RandomUtil;
import cyou.mrd.ObjectAccessor;
import cyou.mrd.Platform;
import cyou.mrd.data.Data;
import cyou.mrd.game.actor.Actor;
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

/**
 * ClassName:MarketService ClassDescription: 市场交易相关服务
 * 
 * @author shiwei2006
 * @Date 2013年11月30日 下午8:33:16
 * @version 1.0
 */
@OPHandler(TYPE = OPHandler.HTTP)
public class MarketService implements Service {

	private static final Logger log = LoggerFactory.getLogger(MarketService.class);

	/**
	 * 按物品ID搜索显示的条数，可配置
	 */
	private static final int QUERY_COUNT = Platform.getConfiguration().getInt("market_query_count");
	
	/**
	 * 挂单超时时间，默认6小时，单位：秒
	 */
	private static final int EXPIRE_TIME = Platform.getConfiguration().getInt("market_expire_time");
	
	/**
	 * 系统回收时间，默认9小时，单位：秒
	 */
	private static final int RECYCLE_TIME = Platform.getConfiguration().getInt("market_recycle_time");
	
	@Override
	public String getId() {
		return "MarketService";
	}

	@Override
	public void startup() throws Exception {

	}

	@Override
	public void shutdown() throws Exception {

	}
	/**
	 * 根据列表获取一个对应的市场信息
	 * @param pos
	 * @param list
	 * @return
	 */
	public Market loadMarket(int pos,List<Market> list){
		if(list != null && list.size() > 0){
			Market market = null;
			for(int i = 0;i < list.size();i++){
				market = list.get(i);
				if(market.getPos() == pos){
					return market;
				}
			}
		}
		
		return null;
	}
	/**
	 * 加载市场列表
	 * @param playerId
	 * @return
	 */
	public List<Market> loadMarketList(int playerId){
		Data data = Platform.dataCenter().getData(DataKeysEx.marketKey(playerId));
		List<Market> list = null;
		if (data == null) {
			list = MarketDAO.getMarketList(playerId);
			if (list != null) {
				Platform.dataCenter().sendNewData(DataKeysEx.marketKey(playerId), list);
				
			}
			
		} else {
			list = (List<Market>)data.value;
			
		}
		return list;
	}
	/**
	 * 保存市场列表
	 * @param list
	 * @param playerId
	 */
	public void saveMarketList(List<Market> list,int playerId){
		// 更新远程数据.
		String key = DataKeysEx.marketKey(playerId);
		Data data = Platform.dataCenter().getData(key);
		boolean ret = false;
		if (data == null) {
			ret = Platform.dataCenter().sendNewData(key, list);
		} else {
			data.value = list;
			ret = Platform.dataCenter().sendData(key, data);
		}
//		if (ret && Platform.worldServer() != null) {
//			WorldManagerEx wmanager = (WorldManagerEx)Platform.getAppContext().get(WorldManager.class);
//			wmanager.marketChanged(playerId);// 通知保存数据库.
//			// 清理本地缓存。
//			for(int i = 0;i<list.size();i++){
//				Platform.getEntityManager().clearFromCache(list.get(i));
//			}
//		} else {
			for(int i = 0;i<list.size();i++){
				Market market = list.get(i);
//				if(market.getOperate() == Trade.ADD){
//					Platform.getEntityManager().createSync(market);
//					market.setOperate(Trade.UPDATE);
//				}else{
					Platform.getEntityManager().updateSync(market);// 自己保存数据库
//				}
			}
			//data = Platform.dataCenter().getData(key);
			//data.value = list;
			//Platform.dataCenter().sendData(key, data);
//		}
	}
	/**
	 * syncMarketPos: 同步市场格子数据(只同步写入新格子)
	 * 
	 * @param packet
	 * @param session
	 * @throws Exception
	 * @return void
	 */
	@OP(code = HOpCodeEx.CLIENT_SYNC_POS_DATA)
	public void syncMarketPos(Packet packet, HSession session) throws Exception {
		try {
			
			PlayerEx player = (PlayerEx) session.client();
			if (player == null) {
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
				return;
			}
			// 检测参数
			JSONArray client = null, remote = null;
			if (packet.containsKey("client")) {
				client = packet.getJSONArray("client");
			}
			if (client == null || client.isEmpty()) {
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_15, packet.getopcode());
				return;
			}
			// 正式处理
			remote = new JSONArray();
			List<Market> list = loadMarketList(player.getId());
			if(list == null || list.isEmpty()){//服务器现有格子
				list = new ArrayList<Market>();
				createInitMarketBox(list,player);
			}
			for(Market market : list){
				remote.add(market.getPos());
			}
			for (int k = 0; k < client.size(); k++) {
				int pos = client.getInt(k);//格子号
				boolean exist = false;
				if (list != null && !list.isEmpty()) {
					for (Market market : list) {
						if (market.getPos() == pos) {
							exist = true;
							break;
						}
					}
				}
				if (!exist) {// 不存在此格子写入一条
					Market domain = new Market();
					domain.setPlayerId(player.getId());
					domain.setPos(pos);
					domain.setStatus(Market.STATUS_IDLE);
					domain.setItemId(0);
					domain.setItemNum(0);
					domain.setPrice(0);
					domain.setAddTime(0);
					domain.setExpireTime(0);
					domain.setRecycleTime(0);
					domain.setBuyPlayerId(0);
					domain.setBuyPlayerName("");
					domain.setBuyPlayerIcon("");
					domain.setBuyPlayerRich(0);
					domain.setSellPlayerName(player.getName());
					domain.setSellPlayerIcon(player.getIcon());
					domain.setSellPlayerRich(player.getRich());
					domain.setSellPlayerRaceId(player.getRaceId());
					domain.setSellPlayerLvl(player.getLevel());
//					domain.setOperate(Market.ADD);
					Platform.getEntityManager().createSync(domain);
					list.add(domain);
//					try {
//						Platform.getEntityManager().createSync(domain);
//					} catch (Exception e) {// 并发主键，自动忽略
//						log.error("createSync Market at playerId:" + player.getId() + ",pos:" + pos);
//					}
					remote.add(pos);//增加的新格子
				}
			}
			// 发送结果
			Packet pt = new JSONPacket(HOpCodeEx.SERVER_SYNC_POS_DATA);
			pt.put("remote", remote);
			session.send(pt);
			saveMarketList(list,player.getId());
		} catch (Throwable e) {
			log.error("syncMarketPos error",e);
		}
		
	}
	public void createInitMarketBox(List<Market> list,PlayerEx player){
		//默认创建6个格子
		for(int i = 1;i <= 6 ;i++){
			Market domain = new Market();
			domain.setPlayerId(player.getId());
			domain.setPos(i);
			domain.setStatus(Market.STATUS_IDLE);
			domain.setItemId(0);
			domain.setItemNum(0);
			domain.setPrice(0);
			domain.setAddTime(0);
			domain.setExpireTime(0);
			domain.setRecycleTime(0);
			domain.setBuyPlayerId(0);
			domain.setBuyPlayerName("");
			domain.setBuyPlayerIcon("");
			domain.setBuyPlayerRich(0);
			domain.setSellPlayerName(player.getName());
			domain.setSellPlayerIcon(player.getIcon());
			domain.setSellPlayerRich(player.getRich());
			domain.setSellPlayerRaceId(player.getRaceId());
			domain.setSellPlayerLvl(player.getLevel());
//			domain.setOperate(Market.ADD);
			Platform.getEntityManager().createSync(domain);
			list.add(domain);
		}
	}
	/**
	 * showMarketList: 显示自己的市场货物列表
	 * 
	 * @param packet
	 * @param session
	 * @throws Exception
	 * @return void
	 */
	@OP(code = HOpCodeEx.CLIENT_SHOW_MARKET)
	public void showMarketList(Packet packet, HSession session) throws Exception {
		try {
			
			PlayerEx player = (PlayerEx) session.client();
			if (player == null) {
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
				return;
			}
			//直接处理(并对每条记录进行单独处理不更新数据库)不加锁
			List<Market> list = loadMarketList(player.getId());
			if(list == null || list.isEmpty()){
				list = new ArrayList<Market>();
				createInitMarketBox(list,player);
				saveMarketList(list,player.getId());
			}
			//检查当前好友数量
			int friendNum = 0;
			//当前好友数量检测
			RelationService rsService = Platform.getAppContext().get(RelationService.class);
			PlayerRelation pr = rsService.findRelation(player.getId());
			if(pr != null && pr.getFriends() != null && !pr.getFriends().actors.isEmpty()){
				friendNum = pr.getFriends().actors.size();
			}
			Packet pt = new JSONPacket(HOpCodeEx.SERVER_SHOW_MARKET);
			pt.put("marketList", executeMarketList(list));
			pt.put("friendNum", friendNum);
			
			//随机好友
			List<Market> randomList = getFriendMarketNewUpload(pr,0);
			if(randomList == null || randomList.size() == 0){
				pt.put("friendList", "[]");
			}else{
				List<FriendRandom> friendRandomList = new ArrayList<FriendRandom>();
				FriendRandom friendRandomL = null;
				FriendRandom friendRandomR = null;
				friendRandomL = randomFriend(randomList,0);
				
				if(randomList.size() == 1){
					friendRandomR = randomFriend(randomList,0);
				}else{
					friendRandomR = randomFriend(randomList,friendRandomL.getPlayerId());
				}
				friendRandomList.add(friendRandomL);
				friendRandomList.add(friendRandomR);
				pt.put("friendList", executeRandomFriend(friendRandomList));
			}
			//自动检测好友开启
			//autoCheckFriendMarketPos(list, player, friendNum);
			//发送结果
			session.send(pt);
		} catch (Throwable e) {
			log.error("showMarketList error",e);
		}
		
	}
	public String executeRandomFriend(List<FriendRandom> friendRandomList){
		JSONArray array = new JSONArray();
		for(int i = 0; i < friendRandomList.size(); i++){
			FriendRandom friendRandom = friendRandomList.get(i);
			JSONObject data = new JSONObject();
			data.put("friendId", friendRandom.getPlayerId());
			data.put("friendName", friendRandom.getName());
			data.put("friendIcon", friendRandom.getIcon());
			data.put("friendLvl", friendRandom.getLevel());
			data.put("friendRaceId", friendRandom.getRaceId());
			data.put("friendRich", friendRandom.getRich());
			array.add(data);
		}
		return array.toString();
	}
	/**
	 * 取上架时间最新的100个用户
	 * @param pr
	 * @return
	 */
	public List<Market> getFriendMarketNewUpload(PlayerRelation pr,int remove){
		List<Market> list = null;
		if(pr != null && pr.getFriends() != null && pr.getFriends().actors.size() > 0){
			String inStr = "(";
			for(int i = 0;i < pr.getFriends().actors.size();i++){
				Actor actor = pr.getFriends().actors.get(i);
				if(i == pr.getFriends().actors.size()-1){
					//好友個數大於1再排除
					if(remove == actor.getId() && pr.getFriends().actors.size() > 1){
						inStr = inStr.substring(0,inStr.length()-1);
						inStr += ")";
					}else{
						inStr += actor.getId()+")";
					}
				}else{
					//好友個數大於1再排除
					if(remove == actor.getId() && pr.getFriends().actors.size() > 1){
						continue;
					}
					inStr += actor.getId()+",";
				}
			}
			if(inStr != null && inStr.equals("()"))
				return null;
			String sql = "select playerId,sellPlayerName,sellPlayerIcon,sellPlayerRich,sellPlayerRaceId,sellPlayerLvl,count(*) from Market where playerId in "+inStr+" group by playerId order by addTime desc";
			list = Platform.getEntityManager().limitQuery(sql,0,100);
		}
		
		return list;
	}
	/**
	 * 随机好友，
	 * @param list
	 * @param remove 不能被随机的
	 * @return
	 */
	public FriendRandom randomFriend(List<Market> list,int remove){
		List<IRandom> randomFriendList = new ArrayList<IRandom>();
		//总百分比
		int count = 0;
		//不被随机的是不是最后一个
		boolean removeIsLast = false;
		//随机的个数如果remove大于0 就说明有一个不被随机
		int size = list.size();
		if(remove > 0){
			size = list.size()-1;
		}
		//每个好友的概率
		int probability = 100/size;
		FriendRandom friendRandom = null;
		for(int j = 0;j < list.size();j++){
			Object obj = (Object)list.get(j);
			JSONArray js = JSONArray.fromObject(obj);
			if(Integer.parseInt(js.get(0).toString()) == remove){
				if(j == list.size()-1){
					removeIsLast = true;
				}
				continue;
			}
			friendRandom = new FriendRandom();
			friendRandom.setPlayerId(Integer.parseInt(js.get(0).toString()));
			friendRandom.setName(js.get(1).toString());
			friendRandom.setIcon(js.get(2).toString());
			friendRandom.setRich(Integer.parseInt(js.get(3).toString()));
			friendRandom.setRaceId(Integer.parseInt(js.get(4).toString()));
			friendRandom.setLevel(Integer.parseInt(js.get(5).toString()));
			
			if(j == list.size()-1){
				probability = 100-count;
			}
			friendRandom.setProbability(probability);
			randomFriendList.add(friendRandom);
			count += probability;
		}
		//不被随机的是最后一个，把剩余的概率都给他
		if(removeIsLast){
			friendRandom = (FriendRandom)randomFriendList.get(randomFriendList.size()-1);
			friendRandom.setProbability(100-count+friendRandom.getProbability());
		}
		IRandom random = RandomUtil.getRandom(randomFriendList);
		return (FriendRandom)random;
	}
	/**
	 * autoCheckFriendMarketPos: 自动检测当前好友数量是否可开启新格子。如果开启了新格子追加到现有列表之后。
	 * @param list
	 * @param player
	 * @param friendNum
	 * @throws Exception 
	 * @return void
	*/
	private void autoCheckFriendMarketPos(List<Market> list, PlayerEx player, int friendNum) throws Exception{
		//好友数量配置检测
		List<MarketPosTemplate> mtList = MarketDAO.findFriendMarketPosList();
		if(mtList == null || mtList.isEmpty()){
			return;
		}
		for(MarketPosTemplate mt : mtList){
			if(friendNum >= mt.getFriendNum()){
				//此格子是否已存在
				boolean exist = false;
				for(Market mm : list){
					if(mm.getPos() == mt.getId()){
						exist = true;
						break;
					}
				}
				if(exist){//已存在，略过
					continue;
				}
				//好友数量满足，添加新格子，并追回到现有列表中。
				Market domain = new Market();
				domain.setPlayerId(player.getId());
				domain.setPos(mt.getId());
				domain.setStatus(Market.STATUS_IDLE);
				domain.setItemId(0);
				domain.setItemNum(0);
				domain.setPrice(0);
				domain.setAddTime(0);
				domain.setExpireTime(0);
				domain.setRecycleTime(0);
				domain.setBuyPlayerId(0);
				domain.setBuyPlayerName("");
				domain.setBuyPlayerIcon("");
				domain.setBuyPlayerRich(0);
				try {
					Platform.getEntityManager().createSync(domain);
				} catch (Exception e) {// 并发主键，自动忽略
					log.error("Friend Add CreateSync Market at playerId:" + player.getId() + ",pos:" + mt.getId());
				}
				list.add(domain);
			}
		}
	}
	/**
	 * 市场是否有物品可以上架
	 * @param playerId
	 * @return
	 */
	public int marketIsUpload(int playerId){
		int isUpload = 0;
		int now = getCurrentTime();
		List<Market> list = loadMarketList(playerId);
		if(list != null && list.size() > 0){
			for(int i = 0;i < list.size();i++){
				Market market = list.get(i);
				int status = recountMarketStatus(market,now);
				if(status == Market.STATUS_SELLING){
					isUpload = 1;
					break;
				}
			}
		}
		
		return isUpload;
	}
	/**
	 * 市场有物品可以被回收
	 * @param playerId
	 * @return
	 */
	public int marketIsRecover(int playerId){
		int isRecover = 0;
		int now = getCurrentTime();
		List<Market> list = loadMarketList(playerId);
		if(list != null && list.size() > 0){
			for(int i = 0;i < list.size();i++){
				Market market = list.get(i);
				int status = recountMarketStatus(market,now);
				if(status == Market.STATUS_BUYED || status == Market.STATUS_EXPIRE || status == Market.STATUS_RECYCLE){
					isRecover = 1;
					break;
				}
			}
		}
		
		return isRecover;
	}
	/**
	 * queryMarketList: 搜索显示的市场列表(按好友ID，按物品ID)
	 * @param packet
	 * @param session
	 * @throws Exception 
	 * @return void
	*/
	@OP(code = HOpCodeEx.CLIENT_QUERY_MARKET)
	public void queryMarketList(Packet packet, HSession session) throws Exception {
		log.info("[HTTPRequest] session:{}  packet[showMarketList]]", session.getSessionId());
		PlayerEx player = (PlayerEx) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			return;
		}
		//检测参数
		int type = 0, id = 0;
		if(packet.containsKey("type")){
			type = packet.getInt("type");
		}
		if(packet.containsKey("id")){
			id = packet.getInt("id");
		}
		if(type < 1 || type > 2 || id < 1){
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_15, packet.getopcode());
			return;
		}
		if(type == 1 && id == player.getId()){//过滤自己
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_15, packet.getopcode());
			return;
		}
		//正式处理
		List<Market> list = null;
		if(type == 1){//按好友ID搜索
			list = loadMarketList(id);
			PlayerServiceEx playerService = (PlayerServiceEx) Platform.getAppContext().get(PlayerService.class);
			PlayerEx friend = playerService.loadPlayer(id);
			if(friend == null){
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_11, packet.getopcode());
				return;
			}
			if(list == null || list.isEmpty()){
				list = new ArrayList<Market>();
				createInitMarketBox(list,friend);
				saveMarketList(list,friend.getId());
			}
		}
		else if(type == 2){//按物品ID搜索，过滤自己
			list = MarketDAO.queryMarketList(player.getId(), id, QUERY_COUNT, getCurrentTime());
		}
		//直接处理(并对每条记录进行单独处理不更新数据库)不加锁
//		if(list == null || list.isEmpty()){
//			ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_110, packet.getopcode());
//			return;
//		}
		
		//发送结果
		Packet pt = new JSONPacket(HOpCodeEx.SERVER_QUERY_MARKET);
		pt.put("marketList", executeMarketList(list));
		pt.put("type", type);
		pt.put("id", id);
		
		//随机好友
		RelationService rsService = Platform.getAppContext().get(RelationService.class);
		PlayerRelation pr = rsService.findRelation(player.getId());
		List<Market> randomList = null;
		if(type == 1){
			randomList = getFriendMarketNewUpload(pr,id);
		}else{
			randomList = getFriendMarketNewUpload(pr,0);
		}
		if(randomList == null || randomList.size() == 0){
			pt.put("friendList", "[]");
		}else{
			List<FriendRandom> friendRandomList = new ArrayList<FriendRandom>();
			FriendRandom friendRandomL = null;
			FriendRandom friendRandomR = null;
			friendRandomL = randomFriend(randomList,0);
			
			if(randomList.size() == 1){
				friendRandomR = randomFriend(randomList,0);
			}else{
				friendRandomR = randomFriend(randomList,friendRandomL.getPlayerId());
			}
			friendRandomList.add(friendRandomL);
			friendRandomList.add(friendRandomR);
			pt.put("friendList", executeRandomFriend(friendRandomList));
		}
		session.send(pt);
	}
	
	/**
	 * uploadMarket: 上架操作
	 * 注：由于网络原因和游戏本身性质，采用上传数据列表方法进行上架操作，服务端只是添加记录，不对其进行有效性处理！因为数据在客户端。
	 * 此处需要加锁进行处理，由于涉及到货价格子状态改变
	 * @param packet
	 * @param session
	 * @throws Exception 
	 * @return void
	*/
	@OP(code = HOpCodeEx.CLIENT_UPLOAD_MARKET)
	public void uploadMarket(Packet packet, HSession session) throws Exception {
		log.info("[HTTPRequest] session:{}  packet[uploadMarket]]", session.getSessionId());
		PlayerEx player = (PlayerEx) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			return;
		}
		//检测参数
		JSONArray marketList = null;
		if(packet.containsKey("marketList")){
			marketList = packet.getJSONArray("marketList");
		}
		if(marketList == null || marketList.isEmpty()){
			//暂时返回错误
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_15, packet.getopcode());
			return;
		}
		//加锁正式处理
		//PlayerLockService lockService = Platform.getAppContext().get(PlayerLockService.class);
		int playerId = player.getId();
//		try {
//			lockService.lock(playerId);
//		} catch (Exception e) {//锁失败或者超时
//			ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_111, packet.getopcode());
//			return;
//		}
		List<Market> list = loadMarketList(playerId);
		try{
			int now = getCurrentTime();
			JSONArray resultList = new JSONArray();
			for(int i = 0; i < marketList.size(); i++){
				JSONObject obj = marketList.getJSONObject(i);
				JSONObject data = new JSONObject();
				int pos = obj.getInt("pos");//格子号
				int itemId = obj.getInt("itemId");
				int itemNum = obj.getInt("itemNum");
				int price = obj.getInt("price");
				data.put("pos", pos);
				//只检测参数大概范围
				if(itemId < 1 || itemNum < 1 || price < 1){
					data.put("result", 0);//0表示参数不正常
					resultList.add(data);
					continue;
				}
				Market mm = loadMarket(pos,list);
				if(mm == null){
					data.put("result", -1);//-1表示没有此格子
					resultList.add(data);
					continue;
				}
				int status = recountMarketStatus(mm, now);//得到最新状态
				if(status != Market.STATUS_IDLE){//状态不是空闲中
					data.put("result", -2);//-2表示格子不是空闲状态
					resultList.add(data);
					continue;
				}
				//正式写入更新
				mm.setStatus(Market.STATUS_SELLING);
				mm.setItemId(itemId);
				mm.setItemNum(itemNum);
				mm.setPrice(price);
				mm.setAddTime(now);
				mm.setExpireTime(now + EXPIRE_TIME);
				mm.setRecycleTime(now + RECYCLE_TIME);
				mm.setBuyPlayerId(0);
				mm.setBuyPlayerName("");
				mm.setBuyPlayerIcon("");
				mm.setBuyPlayerRich(0);
				mm.setSellPlayerName(player.getName());
				mm.setSellPlayerIcon(player.getIcon());
				mm.setSellPlayerRich(player.getRich());
				mm.setSellPlayerRaceId(player.getRaceId());
				mm.setSellPlayerLvl(player.getLevel());
				//Platform.getEntityManager().updateSync(mm);
				data.put("result", 1);//1表示添加成功
				resultList.add(data);
			}
			//发送结果
			Packet pt = new JSONPacket(HOpCodeEx.SERVER_UPLOAD_MARKET);
			pt.put("resultList", resultList);
			session.send(pt);
			saveMarketList(list,player.getId());
			//发交互公告，并且发系统信息
			FriendService friendService = (FriendService)Platform.getAppContext().get(RelationService.class);
			PlayerRelation targetRelation = friendService.findRelation(player.getInstanceId());
			if(targetRelation != null){
				PlayerEx[] playerArray = new PlayerEx[targetRelation.getFriends().actors.size()];
				for(int i = 0; i < targetRelation.getFriends().actors.size(); i++){
					PlayerEx friend = (PlayerEx) ObjectAccessor.getPlayer(targetRelation.getFriends().actors.get(i).getId());
					if (friend == null) {
						PlayerServiceEx playerService = (PlayerServiceEx) Platform.getAppContext().get(PlayerService.class);
						friend = playerService.loadPlayer(targetRelation.getFriends().actors.get(i).getId());
						if (friend == null) {
							continue;
						}
					}else{
						//如果不在这个服上，暂时先不做通知了,防止数据出现问题
						playerArray[i] = friend;
						log.info("MarketService.uploadMarket:error[player:{} is not in this server]",targetRelation.getFriends().actors.get(i).getId());
					}
					if(friend != null){
						//发系统信息
						IAkMailService<MailEx> service = Platform.getAppContext().get(IAkMailService.class);
						JSONObject jsObj = new JSONObject();
						jsObj.put("name", (player.getName() == null || player.getName() == "") ? "" : player.getName());
						service.sendSystemMailUnInteractive(player.getId(), friend.getId(), player.getName(), MailEx.TEMPLATEID_3224, player.getIcon(), player.getLevel(), player.getRich(), player.getRaceId(), jsObj.toString());
					}
				}
				AkNoticeService akNoticeService = Platform.getAppContext().get(AkNoticeService.class);
				akNoticeService.sendInteractiveNotice(AkNoticeService.INTERACTIVE_5, playerArray);
			}
		}finally{
			//lockService.unlock(playerId);
		}
	}
	
	/**
	 * downMarket: 下架操作
	 * 1.正常物品未达到6小时下架 2.已过期但是未回收的物品下架
	 * @param packet
	 * @param session
	 * @throws Exception 
	 * @return void
	*/
	@OP(code = HOpCodeEx.CLIENT_DOWN_MARKET)
	public void downMarket(Packet packet, HSession session) throws Exception {
		log.info("[HTTPRequest] session:{}  packet[downMarket]]", session.getSessionId());
		PlayerEx player = (PlayerEx) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			return;
		}
		//检测参数
		int pos = 0;
		if(packet.containsKey("pos")){
			pos = packet.getInt("pos");
		}
		if(pos < 1){
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_15, packet.getopcode());
			return;
		}
		//加锁处理下架
		//PlayerLockService lockService = Platform.getAppContext().get(PlayerLockService.class);
		int playerId = player.getId();
//		try {
//			lockService.lock(playerId);
//		} catch (Exception e) {//锁失败或者超时
//			ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_111, packet.getopcode());
//			return;
//		}
		List<Market> list = loadMarketList(playerId);
		try{
			Market market = loadMarket(pos,list);
			if(market == null){
				ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_112, packet.getopcode());
				return;
			}
			Packet pt = new JSONPacket(HOpCodeEx.SERVER_DOWN_MARKET);
			int result = 0;//0表示未知错误，前端可不处理此状态
			int now = getCurrentTime();
			int status = recountMarketStatus(market, now);//取最新状态
			if(status == Market.STATUS_IDLE){
				result = -1; //-1表示该格子为空格子不能下架
			}
			else if(status == Market.STATUS_BUYED){
				result = -2; //-2表示该格子货物已被玩家购买，不能下架操作，只能收取
			}
			else if(status == Market.STATUS_RECYCLE){
				result = -3; //-3表示该格子货物已被系统回收，不能下架操作，只能收取
			}
			else if(status == Market.STATUS_SELLING || status == Market.STATUS_EXPIRE){
				//对上架中和已过期进行正式下架处理
				if(status == Market.STATUS_SELLING){
					result = 1;//1表示正常物品未达到6小时下架，可扣除灯笼
				}
				else{
					result = 2;//2表示已过期但是未回收的物品下架，免费下架
				}
				int itemId = market.getItemId();
				int itemNum = market.getItemNum();
				int price = market.getPrice();
				pt.put("itemId", itemId);
				pt.put("itemNum", itemNum);
				//更新格子为空闲状态
				market.setStatus(Market.STATUS_IDLE);
				market.setItemId(0);
				market.setItemNum(0);
				market.setPrice(0);
				market.setAddTime(0);
				market.setExpireTime(0);
				market.setRecycleTime(0);
				market.setBuyPlayerId(0);
				market.setBuyPlayerName("");
				market.setBuyPlayerIcon("");
				market.setBuyPlayerRich(0);
				market.setSellPlayerName(player.getName());
				market.setSellPlayerIcon(player.getIcon());
				market.setSellPlayerRich(player.getRich());
				market.setSellPlayerRaceId(player.getRaceId());
				market.setSellPlayerLvl(player.getLevel());
				saveMarketList(list, playerId);
				//Platform.getEntityManager().updateSync(market);
				//写入下架日志
				MarketLog log = new MarketLog();
				log.setType(MarketLog.TYPE_DOWN);
				log.setPos(pos);
				log.setItemId(itemId);
				log.setItemNum(itemNum);
				log.setPrice(price);
				log.setSellPlayerId(playerId);
				log.setBuyPlayerId(0);
				log.setAddTime(new Date());
				Platform.getEntityManager().createSync(log);
				pt.put("market", showSimpleMarket(market, now));
			}
			pt.put("result", result);
			session.send(pt);
		}finally{
			//lockService.unlock(playerId);
		}
	}
	
	/**
	 * collectMarket: 收取玩家购买操作，收取金币（卖方获得）
	 * @param packet
	 * @param session
	 * @throws Exception 
	 * @return void
	*/
	@OP(code = HOpCodeEx.CLIENT_COLLECT_MARKET)
	public void collectMarket(Packet packet, HSession session) throws Exception {
		log.info("[HTTPRequest] session:{}  packet[collectMarket]]", session.getSessionId());
		PlayerEx player = (PlayerEx) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			return;
		}
		//检测参数
		int pos = 0;
		if(packet.containsKey("pos")){
			pos = packet.getInt("pos");
		}
		if(pos < 1){
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_15, packet.getopcode());
			return;
		}
		//加锁处理收取金币（卖方获得）
		//PlayerLockService lockService = Platform.getAppContext().get(PlayerLockService.class);
		int playerId = player.getId();
//		try {
//			lockService.lock(playerId);
//		} catch (Exception e) {//锁失败或者超时
//			ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_111, packet.getopcode());
//			return;
//		}
		List<Market> list = loadMarketList(playerId);
		try{
			Market market = loadMarket(pos,list);
			if(market == null){
				ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_112, packet.getopcode());
				return;
			}
			Packet pt = new JSONPacket(HOpCodeEx.SERVER_COLLECT_MARKET);
			int now = getCurrentTime();
			int status = recountMarketStatus(market, now);//取最新状态
			if(status != Market.STATUS_BUYED){
				pt.put("result", 0);//状态不正常，不能领取金币
			}
			else{
				int itemId = market.getItemId();
				int itemNum = market.getItemNum();
				int price = market.getPrice();
				int buyPlayerId = market.getBuyPlayerId();//购买方
				//更新格子为空闲状态
				market.setStatus(Market.STATUS_IDLE);
				market.setItemId(0);
				market.setItemNum(0);
				market.setPrice(0);
				market.setAddTime(0);
				market.setExpireTime(0);
				market.setRecycleTime(0);
				market.setBuyPlayerId(0);
				market.setBuyPlayerName("");
				market.setBuyPlayerIcon("");
				market.setBuyPlayerRich(0);
				market.setSellPlayerName(player.getName());
				market.setSellPlayerIcon(player.getIcon());
				market.setSellPlayerRich(player.getRich());
				market.setSellPlayerRaceId(player.getRaceId());
				market.setSellPlayerLvl(player.getLevel());
				saveMarketList(list, playerId);
				//Platform.getEntityManager().updateSync(market);
				//写入收取志
				MarketLog log = new MarketLog();
				log.setType(MarketLog.TYPE_COLLECT);
				log.setPos(pos);
				log.setItemId(itemId);
				log.setItemNum(itemNum);
				log.setPrice(price);
				log.setSellPlayerId(playerId);
				log.setBuyPlayerId(buyPlayerId);
				log.setAddTime(new Date());
				Platform.getEntityManager().createSync(log);
				pt.put("market", showSimpleMarket(market, now));
				pt.put("price", price);
				pt.put("result", 1);//下发成功
			}
			session.send(pt);
		}finally{
			//lockService.unlock(playerId);
		}
	}
	
	/**
	 * recycleMarket: 获取回收金币
	 * @param packet
	 * @param session
	 * @throws Exception 
	 * @return void
	*/
	@OP(code = HOpCodeEx.CLIENT_RECYCLE_MARKET)
	public void recycleMarket(Packet packet, HSession session) throws Exception {
		log.info("[HTTPRequest] session:{}  packet[recycleMarket]]", session.getSessionId());
		PlayerEx player = (PlayerEx) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			return;
		}
		//检测参数
		int pos = 0;
		if(packet.containsKey("pos")){
			pos = packet.getInt("pos");
		}
		if(pos < 1){
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_15, packet.getopcode());
			return;
		}
		//加锁处理回收
		//PlayerLockService lockService = Platform.getAppContext().get(PlayerLockService.class);
		int playerId = player.getId();
//		try {
//			lockService.lock(playerId);
//		} catch (Exception e) {//锁失败或者超时
//			ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_111, packet.getopcode());
//			return;
//		}
		List<Market> list = loadMarketList(playerId);
		try{
			Market market = loadMarket(pos,list);
			if(market == null){
				ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_112, packet.getopcode());
				return;
			}
			Packet pt = new JSONPacket(HOpCodeEx.SERVER_RECYCLE_MARKET);
			int now = getCurrentTime();
			int status = recountMarketStatus(market, now);//取最新状态
			if(status != Market.STATUS_RECYCLE){
				pt.put("result", 0);//状态不正常，不能回收系统金币
			}
			else{
				int itemId = market.getItemId();
				int itemNum = market.getItemNum();
				int price = market.getPrice();
				//更新格子为空闲状态
				market.setStatus(Market.STATUS_IDLE);
				market.setItemId(0);
				market.setItemNum(0);
				market.setPrice(0);
				market.setAddTime(0);
				market.setExpireTime(0);
				market.setRecycleTime(0);
				market.setBuyPlayerId(0);
				market.setBuyPlayerName("");
				market.setBuyPlayerIcon("");
				market.setBuyPlayerRich(0);
				market.setSellPlayerName(player.getName());
				market.setSellPlayerIcon(player.getIcon());
				market.setSellPlayerRich(player.getRich());
				market.setSellPlayerRaceId(player.getRaceId());
				market.setSellPlayerLvl(player.getLevel());
				saveMarketList(list, playerId);
				//Platform.getEntityManager().updateSync(market);
				//写入回收日志
				MarketLog log = new MarketLog();
				log.setType(MarketLog.TYPE_RECYCLE);
				log.setPos(pos);
				log.setItemId(itemId);
				log.setItemNum(itemNum);
				log.setPrice(price);
				log.setSellPlayerId(playerId);
				log.setBuyPlayerId(0);
				log.setAddTime(new Date());
				Platform.getEntityManager().createSync(log);
				pt.put("market", showSimpleMarket(market, now));
				pt.put("itemId", itemId);
				pt.put("itemNum", itemNum);
				pt.put("result", 1);//下发成功，客户端可以根据物品数量计算出具体的回收金币即可。
			}
			session.send(pt);
		}finally{
			//lockService.unlock(playerId);
		}
	}
	
	/**
	 * buyMarket: 购买操作(买方购买)
	 * @param packet
	 * @param session
	 * @throws Exception 
	 * @return void
	*/
	@OP(code = HOpCodeEx.CLIENT_BUY_MARKET)
	public void buyMarket(Packet packet, HSession session) throws Exception {
		log.info("[HTTPRequest] session:{}  packet[buyMarket]]", session.getSessionId());
		PlayerEx player = (PlayerEx) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			return;
		}
		//检测参数
		int pos = 0, step = 0, sellPlayerId = 0;
		if(packet.containsKey("sellPlayerId")){
			sellPlayerId = packet.getInt("sellPlayerId");
		}
		if(packet.containsKey("pos")){
			pos = packet.getInt("pos");
		}
		if(packet.containsKey("step")){
			step = packet.getInt("step");
		}
		if(sellPlayerId < 1 || pos < 1 || step < 1 || step > 2){
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_15, packet.getopcode());
			return;
		}
		if(step == 1){//检查下该货物是否可以购买
			buy1(player, sellPlayerId, pos, packet, session);
		}
		else if(step == 2){//本地购买成功后向服务器发送购买成功信息并让服务器去更新对方货物状态
			buy2(player, sellPlayerId, pos, packet, session);
		}
	}
	
	/**
	 * buy1: 购买第一步：检查卖方货物是否可以购买
	 * @param player
	 * @param sellPlayerId
	 * @param pos
	 * @param packet
	 * @param session
	 * @throws Exception 
	 * @return void
	*/
	private void buy1(PlayerEx player, int sellPlayerId, int pos, Packet packet, HSession session) throws Exception {
		List<Market> list = loadMarketList(sellPlayerId);
		Market market = loadMarket(pos,list);
		if(market == null){
			ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_112, packet.getopcode());
			return;
		}
		Packet pt = new JSONPacket(HOpCodeEx.SERVER_BUY_MARKET);
		pt.put("step", 1);
		int now = getCurrentTime();
		int status = recountMarketStatus(market, now);//获取该格子最新状态
		if(status != Market.STATUS_SELLING){
			pt.put("result", 0);//状态不是出售中，不能购买！
		}
		else{
			pt.put("result", 1);
			pt.put("market", showSimpleMarket(market, now));
		}
		session.send(pt);
	}
	
	/**
	 * buy2: 购买第二步：买方本地购买成功后去更新卖方货物状态
	 * @param player
	 * @param sellPlayerId
	 * @param pos
	 * @param packet
	 * @param session
	 * @throws Exception 
	 * @return void
	*/
	private void buy2(PlayerEx player, int sellPlayerId, int pos, Packet packet, HSession session) throws Exception {
		//加锁更新卖方货物状态
		//PlayerLockService lockService = Platform.getAppContext().get(PlayerLockService.class);
//		try {
//			lockService.lock(sellPlayerId);
//		} catch (Exception e) {//锁失败或者超时
//			ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_111, packet.getopcode());
//			return;
//		}
		List<Market> list = loadMarketList(sellPlayerId);
		try{
			Market market = loadMarket(pos,list);
			if(market == null){
				ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_112, packet.getopcode());
				return;
			}
			Packet pt = new JSONPacket(HOpCodeEx.SERVER_BUY_MARKET);
			pt.put("step", 2);
			int now = getCurrentTime();
			int status = recountMarketStatus(market, now);//获取该格子最新状态
			if(status == Market.STATUS_SELLING){//出售中更新状态
				//更新货物状态
				market.setStatus(Market.STATUS_BUYED);
				market.setBuyPlayerId(player.getId());
				market.setBuyPlayerName(player.getName());
				market.setBuyPlayerIcon(player.getIcon());
				market.setBuyPlayerRich(player.getRich());
				saveMarketList(list, sellPlayerId);
				//Platform.getEntityManager().updateSync(market);
				//写入购买日志
				MarketLog log = new MarketLog();
				log.setType(MarketLog.TYPE_BUY);
				log.setPos(pos);
				log.setItemId(market.getItemId());
				log.setItemNum(market.getItemNum());
				log.setPrice(market.getPrice());
				log.setSellPlayerId(sellPlayerId);
				log.setBuyPlayerId(player.getId());
				log.setAddTime(new Date());
				Platform.getEntityManager().createSync(log);
			}
			pt.put("market", showSimpleMarket(market, now));//固定返回最新结果信息
			session.send(pt);
			//发系统信息
			IAkMailService<MailEx> service = Platform.getAppContext().get(IAkMailService.class);
			JSONObject jsObj = new JSONObject();
			jsObj.put("name", (player.getName() == null || player.getName() == "") ? "" : player.getName());
			jsObj.put("itemId", market.getItemId());
			jsObj.put("itemNum", market.getItemNum());
			service.sendSystemMailUnInteractive(player.getId(), sellPlayerId, player.getName(), MailEx.TEMPLATEID_3227, player.getIcon(), player.getLevel(), player.getRich(), player.getRaceId(), jsObj.toString());
		}finally{
			//lockService.unlock(sellPlayerId);
		}
	}
	
	
	/**
	 * executeMarketList: 处理并返回最新货架数据列表，可在查看自己，查看好友，搜索物品统一调用此方法进行最终结果输出
	 * @param list
	 * @throws Exception 
	 * @return JSONArray
	*/
	private JSONArray executeMarketList(List<Market> list) throws Exception{
		JSONArray array = new JSONArray();
		if(list == null || list.isEmpty()){
			return array;
		}
		int now = getCurrentTime();
		for(int i = 0; i < list.size(); i++){
			Market market = list.get(i);
			JSONObject data = showSimpleMarket(market, now);
			array.add(data);
		}
		return array;
	}
	
	/**
	 * showSimpleMarket: 显示单个货架信息(已是最新状态)
	 * @param market
	 * @param now
	 * @return JSONObject
	*/
	private JSONObject showSimpleMarket(Market market, int now){
		JSONObject data = new JSONObject();
		data.put("playerId", market.getPlayerId());
		data.put("pos", market.getPos());
		int status = recountMarketStatus(market, now);
		data.put("status", status);
		//以下数据直接给出，前端根据状态对应使用。
		data.put("itemId", market.getItemId());
		data.put("itemNum", market.getItemNum());
		data.put("price", market.getPrice());
		if(status == Market.STATUS_SELLING){
			data.put("cdTime", market.getExpireTime() - now);//将要过期倒计时，单位：秒
		}
		else if(status == Market.STATUS_EXPIRE){
			data.put("cdTime", market.getRecycleTime() - now);//将要被回收倒计时，单位：秒
		}
		data.put("buyPlayerId", market.getBuyPlayerId());
		data.put("buyPlayerName", market.getBuyPlayerName() == null ? "" : market.getBuyPlayerName());
		data.put("buyPlayerIcon", market.getBuyPlayerIcon() == null ? "" : market.getBuyPlayerIcon());
		data.put("buyPlayerRich", market.getBuyPlayerRich());
		
		data.put("sellPlayerRaceId", market.getSellPlayerRaceId());
		data.put("sellPlayerName", market.getSellPlayerName() == null ? "" : market.getSellPlayerName());
		data.put("sellPlayerIcon", market.getSellPlayerIcon() == null ? "" : market.getSellPlayerIcon());
		data.put("sellPlayerRich", market.getSellPlayerRich());
		data.put("sellPlayerLvl", market.getSellPlayerLvl());
		return data;
	}
	
	
	/**
	 * getCurrentTime: 获得当前时间，单位：秒
	 * @return int
	*/
	private int getCurrentTime(){
		return (int)(System.currentTimeMillis() / 1000);
	}
	
	/**
	 * recountMarketStatus: 重新计算当前货架的状态
	 * @param market
	 * @param now
	 * @throws Exception 
	 * @return int
	*/
	private int recountMarketStatus(Market market, int now){
		int status = market.getStatus();
		//1.空闲 3.玩家已购买 5系统回收 三种状态不处理直接返回
		if(status == Market.STATUS_IDLE || status == Market.STATUS_BUYED || status == Market.STATUS_RECYCLE){
			return status;
		}
		//对2.上架出售中的状态处理
		if(status == Market.STATUS_SELLING){
			if(now > market.getExpireTime()){
				status = Market.STATUS_EXPIRE;
			}
			if(now > market.getRecycleTime()){
				status = Market.STATUS_RECYCLE;
			}
		}
		//对4.已过期的状态处理
		if(status == Market.STATUS_EXPIRE){
			if(now > market.getRecycleTime()){
				status = Market.STATUS_RECYCLE;
			}
		}
		return status;
	}
	

}