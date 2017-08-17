package ak.trade;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ak.ex.DataKeysEx;
import ak.gameAward.GameAwardTemplate;
import ak.player.PlayerEx;
import ak.player.PlayerServiceEx;
import ak.playerSns.PlayerSns;
import ak.playerSns.PlayerSnsService;
import ak.server.ErrorHandlerEx;
import cyou.mrd.Platform;
import cyou.mrd.data.Data;
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
 * 贸易服务
 * @author xuepeng
 *
 */
@OPHandler(TYPE = OPHandler.HTTP)
public class TradeService implements Service {

	private static final Logger log = LoggerFactory.getLogger(TradeService.class);
	/**
	 * 贸易最大请求帮助数
	 */
	public static int TRADE_MAX_REQUEST_HELP = Platform.getConfiguration().getInt("trade_max_request_help");
	/**
	 * 帮助好友贸易消耗体力值 
	 */
	public static int TRADE_CONSUME_POWER = Platform.getConfiguration().getInt("trade_consume_power");
	@Override
	public String getId() {
		return "TradeService";
	}

	@Override
	public void startup() throws Exception {
		

	}

	@Override
	public void shutdown() throws Exception {
		

	}
	/**
	 * 根据列表取出对应的一个贸易
	 * @param tradeId
	 * @param boxId
	 * @param list
	 * @return
	 */
	public Trade loadTrade(int tradeId, int boxId,List<Trade> list){
		if(list != null && list.size() > 0){
			Trade trade = null;
			for(int i = 0;i < list.size();i++){
				trade = list.get(i);
				if(trade.getTradeId() == tradeId && trade.getBoxId() == boxId && trade.getStatus() != Trade.STATUS_NULL){
					return trade;
				}
			}
		}
		
		return null;
	}
	/**
	 * 加载贸易列表
	 * @param playerId
	 * @return
	 */
	public List<Trade> loadTradeList(int playerId){
		Data data = Platform.dataCenter().getData(DataKeysEx.tradeKey(playerId));
		List<Trade> list = null;
		if (data == null) {
			list = TradeDAO.getTradeById(playerId);
			if (list != null) {
				Platform.dataCenter().sendNewData(DataKeysEx.tradeKey(playerId), list);
				
			}
			
		} else {
			list = (List<Trade>)data.value;
			
		}
		return list;
	}
	/**
	 * 删除此列表中，不是tradeId的贸易
	 * @param tradeId
	 * @param list
	 */
	public void deleteTrade(int tradeId, List<Trade> list){
		if(list != null && list.size() > 0){
			Trade trade = null;
			for(int i = 0;i < list.size();i++){
				trade = list.get(i);
				if(trade.getTradeId() != tradeId){
					trade.setStatus(Trade.STATUS_NULL);
				}
			}
		}
	}
	/**
	 * 保存贸易列表
	 * @param list
	 * @param playerId
	 */
	public void saveTradeList(List<Trade> list,int playerId){
		// 更新远程数据.
		String key = DataKeysEx.tradeKey(playerId);
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
//			wmanager.tradeChanged(playerId);// 通知保存数据库.
//			// 清理本地缓存。
//			for(int i = 0;i<list.size();i++){
//				Platform.getEntityManager().clearFromCache(list.get(i));
//			}
//		} else {
			for(int i = 0;i<list.size();i++){
				Trade trade = list.get(i);
//				if(trade.getOperate() == Trade.ADD){
//					Platform.getEntityManager().createSync(trade);
//					trade.setOperate(Trade.UPDATE);
//				}else{
					Platform.getEntityManager().updateSync(trade);// 自己保存数据库
//				}
			}
//			data = Platform.dataCenter().getData(key);
//			data.value = list;
//			Platform.dataCenter().sendData(key, data);
//		}
	}
	/**
	 * 获取列表里未删除的个数
	 * @param list
	 * @return
	 */
	public int getListSizeUnDelete(List<Trade> list){
		int count = 0;
		for(int i = 0;i<list.size();i++){
			Trade trade = list.get(i);
			if(trade.getStatus() != Trade.STATUS_NULL){
				count++;
			}
		}
		return count;
	}
	public void createInitBox(int playerId,List<Trade> list){
		int size = list.size();
		for(int i = 1;i <= TRADE_MAX_REQUEST_HELP-size;i++){
			Trade trade = new Trade();
			trade.setBoxId(0);
			trade.setTradeId(0);
			trade.setItemId(0);
			trade.setItemNum(0);
			trade.setStatus(Trade.STATUS_NULL);
			trade.setHelpPlayerIcon("");
			trade.setHelpPlayerId(0);
			trade.setHelpPlayerName("");
			trade.setHelpPlayerRich(0);
			trade.setPlayerId(playerId);
			trade.setPos(i+size);
			//trade.setOperate(Trade.ADD);
			Platform.getEntityManager().createSync(trade);
			list.add(trade);
		}
	}
	public Trade getOneNullTrade(List<Trade> list){
		for(int i = 0;i<list.size();i++){
			Trade trade = list.get(i);
			if(trade.getStatus() == Trade.STATUS_NULL){
				return trade;
			}
		}
		return null;
	}
	/**
	 * 请求帮助
	 * @param packet
	 * @param session
	 */
	@OP(code = HOpCodeEx.TRADE_REQUEST_HELP_CLIENT)
	public void requestHelp(Packet packet, HSession session){
		//加锁处理回收
		//PlayerLockService lockService = Platform.getAppContext().get(PlayerLockService.class);
		int operatePlayerId = 0;
		try {
			PlayerEx player = (PlayerEx) session.client();
			//发送人没有登陆
			if (player == null) {
				ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_1, packet.getopcode());
				return;
			}
			int tradeId = packet.getInt("tradeId");
			int boxId = packet.getInt("boxId");
			int itemId = packet.getInt("itemId");
			int itemNum = packet.getInt("itemNum");
			operatePlayerId = player.getId();
			
			Trade trade = null;
			List<Trade> list = loadTradeList(operatePlayerId);
			if(list == null){
				list = new ArrayList<Trade>();
			}
			if(list.size() == 0){
				createInitBox(operatePlayerId,list);
			}else{
				//删除不是此次贸易的贸易
				deleteTrade(tradeId,list);
			}
			
//			try {
//				lockService.lock(operatePlayerId);
//			} catch (Exception e) {//锁失败或者超时
//				ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_111, packet.getopcode());
//				return;
//			}
			Packet pt = new JSONPacket(HOpCodeEx.TRADE_REQUEST_HELP_SERVER);
			
			trade = loadTrade(tradeId,boxId,list);
			//不存在这个
			if(trade == null){
				if(getListSizeUnDelete(list) >= TRADE_MAX_REQUEST_HELP){
					ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_121, packet.getopcode());
					return;
				}
				trade = getOneNullTrade(list);
				trade.setBoxId(boxId);
				trade.setTradeId(tradeId);
				trade.setItemId(itemId);
				trade.setItemNum(itemNum);
				trade.setStatus(Trade.STATUS_REQUEST_HELP);
				trade.setHelpPlayerIcon("");
				trade.setHelpPlayerId(0);
				trade.setHelpPlayerName("");
				trade.setHelpPlayerRich(0);
				pt.put("result", 1);
			}else{
				pt.put("result", 0);
			}
			
			if (list != null && list.size() > 0) {
				pt.put("tradeList", executeTradeList(list).toString());
			}else{
				pt.put("tradeList", "[]");
			}
			pt.put("currentBoxId", boxId);
			session.send(pt);
			saveTradeList(list,operatePlayerId);
		} catch (Throwable e) {
			log.error("requestHelp error");
			ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_0, packet.getopcode());
		} finally {
//			if(operatePlayerId > 0){
//				lockService.unlock(operatePlayerId);
//			}
		}
	}
	/**
	 * 设置所有帮助格为空闲
	 * @param packet
	 * @param session
	 */
	@OP(code = HOpCodeEx.TRADE_SET_FREE_CLIENT)
	public void setFree(Packet packet, HSession session){
		//加锁处理回收
		//PlayerLockService lockService = Platform.getAppContext().get(PlayerLockService.class);
		int operatePlayerId = 0;
		try {
			PlayerEx player = (PlayerEx) session.client();
			//发送人没有登陆
			if (player == null) {
				ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_1, packet.getopcode());
				return;
			}
			operatePlayerId = player.getId();
			
			List<Trade> list = loadTradeList(operatePlayerId);
			//删除所有贸易
			deleteTrade(-1,list);
			
//			try {
//				lockService.lock(operatePlayerId);
//			} catch (Exception e) {//锁失败或者超时
//				ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_111, packet.getopcode());
//				return;
//			}
			
			//如果设置空闲状态，就把所有的都设为空闲状态
			saveTradeList(list,operatePlayerId);
			Packet pt = new JSONPacket(HOpCodeEx.TRADE_SET_FREE_SERVER);
			pt.put("result", 1);
			session.send(pt);
		} catch (Throwable e) {
			log.error("setFree error");
			ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_0, packet.getopcode());
		} finally {
//			if(operatePlayerId > 0){
//				lockService.unlock(operatePlayerId);
//			}
		}
	}
	/**
	 * 帮助好友或者帮助自己
	 * @param packet
	 * @param session
	 */
	@OP(code = HOpCodeEx.TRADE_HELP_CLIENT)
	public void help(Packet packet, HSession session){
		//加锁处理回收
		//PlayerLockService lockService = Platform.getAppContext().get(PlayerLockService.class);
		int operatePlayerId = 0;
		try {
			PlayerEx player = (PlayerEx) session.client();
			//发送人没有登陆
			if (player == null) {
				ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_1, packet.getopcode());
				return;
			}
			
			
			int boxId = packet.getInt("boxId");
			int tradeId = packet.getInt("tradeId");
			int status = 0;
			operatePlayerId = packet.getInt("helpPlayerId");
			if(operatePlayerId == 0 || operatePlayerId == player.getId()){
				status = Trade.STATUS_SELF_COMPLETE;
				operatePlayerId = player.getId();
			}else{
				status = Trade.STATUS_FRIEND_COMPLETE;
			}
			List<Trade> list = loadTradeList(operatePlayerId);
			Trade trade = null;
			
//			try {
//				lockService.lock(operatePlayerId);
//			} catch (Exception e) {//锁失败或者超时
//				ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_111, packet.getopcode());
//				return;
//			}
			PlayerSnsService psService = Platform.getAppContext().get(PlayerSnsService.class);
			int now = (int)(System.currentTimeMillis() / 1000);
			trade = loadTrade(tradeId,boxId,list);
			Packet pt = new JSONPacket(HOpCodeEx.TRADE_HELP_SERVER);
			int love = 0;
			if(trade == null){
				//不存在的贸易信息
				//ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_118, packet.getopcode());
				//return;
				pt.put("result", 0);
			}else{
				if(trade.getStatus() == Trade.STATUS_REQUEST_HELP){
					
					//体力值不足
					
					
					psService.recountPlayerActivePoints(player, now);
					if(status == Trade.STATUS_FRIEND_COMPLETE){
						if(player.getPlayerSns().getActiveCount() < TRADE_CONSUME_POWER){
							ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_114, packet.getopcode());
							return;
						}
					}
					trade.setStatus(status);
					trade.setHelpPlayerIcon(player.getIcon() == null ? "" : player.getIcon());
					trade.setHelpPlayerId(player.getId());
					trade.setHelpPlayerName(player.getName() == null ? "" : player.getName());
					trade.setHelpPlayerRich(player.getRich());
					//增加爱心值
					if(status == Trade.STATUS_FRIEND_COMPLETE){
						
						love = psService.addLove(GameAwardTemplate.ID_HELP_TRADE_REWARD_LOVE, player, operatePlayerId);
						
						
						//减少体力值
						
						player.getPlayerSns().setActiveCount(player.getPlayerSns().getActiveCount() - TRADE_CONSUME_POWER);
						psService.savePlayerSns(player);
						player.notifySave();
						
					}
					pt.put("result", 1);
				}else{
					//该贸易已经完成
					//ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_119, packet.getopcode());
					//return;
					pt.put("result", 0);
				}
			}
			
			pt.put("activePoints", player.getPlayerSns().getActiveCount());
			pt.put("activeMaxPoints", PlayerSnsService.ACTIVE_MAX_NUM);
			pt.put("recoverTime", psService.getActiveRecoverTime(player.getPlayerSns()));
			pt.put("love", player.getLove());
			pt.put("currentBoxId", boxId);
			if (list != null && list.size() > 0) {
				pt.put("tradeList", executeTradeList(list).toString());
			}else{
				pt.put("tradeList", "[]");
			}
			session.send(pt);
			PlayerServiceEx playerService = (PlayerServiceEx) Platform.getAppContext().get(PlayerService.class);
			playerService.addLove(session,player);
			saveTradeList(list,operatePlayerId);
		} catch (Throwable e) {
			log.error("help error");
			ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_0, packet.getopcode());
		} finally {
//			if(operatePlayerId > 0){
//				lockService.unlock(operatePlayerId);
//			}
		}
	}
	@OP(code = HOpCodeEx.TRADE_MY_LIST_CLIENT)
	public void getMyList(Packet packet, HSession session){
		try {
			PlayerEx player = (PlayerEx) session.client();
			//发送人没有登陆
			if (player == null) {
				ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_1, packet.getopcode());
				return;
			}
			Packet pt = new JSONPacket(HOpCodeEx.TRADE_MY_LIST_SERVER);
			pt.put("result", 1);
			pt.put("tradeList", getTradeListByPlayerId(player.getId()).toString());
			session.send(pt);
		} catch (Throwable e) {
			log.error("getMyList error");
			ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_0, packet.getopcode());
		} 
	}
	@OP(code = HOpCodeEx.TRADE_FRIEND_LIST_CLIENT)
	public void getFriendList(Packet packet, HSession session){
		try {
			PlayerEx player = (PlayerEx) session.client();
			//发送人没有登陆
			if (player == null) {
				ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_1, packet.getopcode());
				return;
			}
			int friendId = packet.getInt("friendId");
			Packet pt = new JSONPacket(HOpCodeEx.TRADE_FRIEND_LIST_SERVER);
			pt.put("result", 1);
			pt.put("tradeList", getTradeListByPlayerId(friendId).toString());
			session.send(pt);
		} catch (Throwable e) {
			log.error("getFriendList error");
			ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_0, packet.getopcode());
		} 
	}
	public JSONArray getTradeListByPlayerId(int playerId){
		List<Trade> list = loadTradeList(playerId);
		return executeTradeList(list);
		
	}
	private JSONArray executeTradeList(List<Trade> list){
		JSONArray array = new JSONArray();
		if(list == null || list.isEmpty()){
			return array;
		}
		for(int i = 0; i < list.size(); i++){
			Trade trade = list.get(i);
			if(trade.getStatus() == Trade.STATUS_NULL){
				continue;
			}
			JSONObject data = showSimpleTrade(trade);
			array.add(data);
		}
		return array;
	}
	private JSONObject showSimpleTrade(Trade trade){
		JSONObject data = new JSONObject();
		data.put("boxId", trade.getBoxId());
		data.put("tradeId", trade.getTradeId());
		data.put("itemId", trade.getItemId());
		data.put("itemNum", trade.getItemNum());
		data.put("helpPlayerIcon", trade.getHelpPlayerIcon());
		data.put("helpPlayerId", trade.getHelpPlayerId());
		data.put("helpPlayerName", trade.getHelpPlayerName());
		data.put("helpPlayerRich", trade.getHelpPlayerRich());
		data.put("playerId", trade.getPlayerId());
		data.put("status", trade.getStatus());
		return data;
	}
}
