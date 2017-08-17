package ak.rank;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ak.ex.DataKeysEx;
import ak.player.PlayerEx;
import ak.player.PlayerServiceEx;
import ak.playerSns.PlayerSns;
import ak.playerSns.PlayerSnsService;
import ak.server.ErrorHandlerEx;
import cyou.mrd.ObjectAccessor;
import cyou.mrd.Platform;
import cyou.mrd.data.Data;
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
/**
 * 排行榜
 * @author xuepeng
 *
 */
@OPHandler(TYPE = OPHandler.HTTP)
public class RankService implements Service {

	private static final Logger log = LoggerFactory.getLogger(RankService.class);
	@Override
	public String getId() {
		return "RankService";
	}

	@Override
	public void startup() throws Exception {
		
	}
	@Override
	public void shutdown() throws Exception {

	}
	
	/**
	 * 获取所有玩家的排行榜前100名（只访问memcache）
	 * @param packet
	 * @param session
	 */
	@OP(code = HOpCodeEx.RANK_CLIENT)
	public void getAllPlayerRank(Packet packet, HSession session){
		try {
			PlayerEx player = (PlayerEx) session.client();
			//发送人没有登陆
			if (player == null) {
				ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_1, packet.getopcode());
				return;
			}
			int type = packet.getInt("type");
			List<Rank> list = null;
			Data data = Platform.dataCenter().getData(DataKeysEx.rankKey(type));
			if(data == null){
//				list = RankDAO.getRankListByTypeNotNull(type);
//				if(list == null || list.size() == 0){
//					//ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_0, packet.getopcode());
//					//return;
//				}else{
//					Platform.dataCenter().sendNewData(DataKeysEx.rankKey(type), list);
//				}
			}else{
				list = (List<Rank>)data.value;
			}
			//发包
			Packet pt = new JSONPacket(HOpCodeEx.RANK_SERVER);
			JSONArray ja = new JSONArray();
			//自己的排名
			Rank selfRank = null;
			if (list != null && list.size() > 0) {
				for (Rank rank : list) {
					JSONObject jo = new JSONObject();
					jo.put("nowRank", rank.getNowRank());
					jo.put("lastRank", rank.getLastRank());
					jo.put("value", rank.getValue());
					if(rank.getPlayerId() == player.getInstanceId()){
						jo.put("self", 1);
						selfRank = rank;
					}else{
						jo.put("self", 0);
					}
					jo.put("playerId", rank.getPlayerId());
					jo.put("playerName", rank.getPlayerName());
					jo.put("playerLvl", rank.getPlayerLvl());
					jo.put("type", rank.getType());
					ja.add(jo);
				}
				pt.put("rankList", ja.toString());
			}else{
				pt.put("rankList", "[]");
			}
			//自己的排名
			JSONObject self = new JSONObject();
			if(selfRank != null){
				self.put("value", selfRank.getValue());
				self.put("type", selfRank.getType());
				self.put("playerName", selfRank.getPlayerName());
				self.put("playerLvl", selfRank.getPlayerLvl());
				self.put("playerId", selfRank.getPlayerId());
				self.put("lastRank", selfRank.getLastRank());
				self.put("nowRank", selfRank.getNowRank());
			}else{
				if(type== Rank.TYPE_RICH){
					self.put("value", player.getRich());
				}else if(type== Rank.TYPE_LOVE){
					self.put("value", player.getLove());
				}else if(type== Rank.TYPE_GOLD){
					self.put("value", player.getMoney());
				}else if(type== Rank.TYPE_FOOD){
					self.put("value", player.getFood());
				}else if(type== Rank.TYPE_WOOD){
					self.put("value", player.getWoods());
				}else if(type== Rank.TYPE_STONE){
					self.put("value", player.getStone());
				}else if(type== Rank.TYPE_MONEY_TREE){
					self.put("value", player.getPlayerSns().getTreeGrade());
				}else if(type== Rank.TYPE_PASS){
					self.put("value", player.getAllBattleStarCount());
				}else if(type== Rank.TYPE_FORCE){
					self.put("value", player.getAllCatCount());
				}
				self.put("type", type);
				self.put("playerName", player.getName());
				self.put("playerLvl", player.getLevel());
				self.put("playerId", player.getInstanceId());
				self.put("lastRank", 0);
				self.put("nowRank", 0);
			}
			
			pt.put("self", self);
			//大类型
			pt.put("type", type);
			session.send(pt);
		} catch (Throwable e) {
			log.error("getAllPlayerRank error");
			ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_0, packet.getopcode());
		}
	}
	/**
	 * 获取好友排行(实时数据，先访问memcache)
	 * @param packet
	 * @param session
	 */
	@OP(code = HOpCodeEx.FRIEND_RANK_CLIENT)
	public void getFriendRank(Packet packet, HSession session) {
		try {
			PlayerEx player = (PlayerEx) session.client();
			//发送人没有登陆
			if (player == null) {
				ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_1, packet.getopcode());
				return;
			}
			//查询所有好友的数据
			RelationService relationService = Platform.getAppContext().get(RelationService.class);
			PlayerSnsService psService = Platform.getAppContext().get(PlayerSnsService.class);
			PlayerRelation targetRelation = relationService.findRelation(player.getInstanceId());
			List<Object[]> list = new ArrayList<Object[]>();
			if(targetRelation != null && targetRelation.getFriends() != null && targetRelation.getFriends().actors.size() >0){
				for(int i = 0; i < targetRelation.getFriends().actors.size(); i++){
					//PlayerEx friend = (PlayerEx) ObjectAccessor.getPlayer(targetRelation.getFriends().actors.get(i).getId());
					//if (friend == null) {
						PlayerServiceEx playerService = (PlayerServiceEx) Platform.getAppContext().get(PlayerService.class);
						PlayerEx friend = playerService.loadPlayer(targetRelation.getFriends().actors.get(i).getId());
						if (friend == null) {
							continue;
						}
					//}
					
					PlayerSns sns = psService.loadPlayerSns(friend.getId());
					Object[] obj = {friend,sns};
					list.add(obj);
				}
			}
			Object[] selfObj = {player,player.getPlayerSns()};
			//加入自己
			list.add(selfObj);
			//获取排行榜
			int type = packet.getInt("type");
			List<Rank> listRank = null;
			
			listRank = getFriendRankList(list,type);
			
			//发包
			Packet pt = new JSONPacket(HOpCodeEx.FRIEND_RANK_SERVER);
			JSONArray ja = new JSONArray();
			Rank selfRank = null;
			if (listRank != null && listRank.size() > 0) {
				for (Rank rank : listRank) {
					JSONObject jo = new JSONObject();
					jo.put("nowRank", rank.getNowRank());
					jo.put("value", rank.getValue());
					if(rank.getPlayerId() == player.getInstanceId()){
						jo.put("self", 1);
						selfRank = rank;
					}else{
						jo.put("self", 0);
					}
					jo.put("playerId", rank.getPlayerId());
					jo.put("playerName", rank.getPlayerName());
					jo.put("playerLvl", rank.getPlayerLvl());
					jo.put("type", rank.getType());
					ja.add(jo);
				}
				pt.put("rankList", ja.toString());
			}else{
				pt.put("rankList", "[]");
			}
			
			//自己的排名
			JSONObject self = new JSONObject();
			self.put("value", selfRank.getValue());
			self.put("type", selfRank.getType());
			self.put("playerName", selfRank.getPlayerName());
			self.put("playerLvl", selfRank.getPlayerLvl());
			self.put("playerId", selfRank.getPlayerId());
			self.put("nowRank", selfRank.getNowRank());
			pt.put("self", self);
			
			pt.put("type", type);
			session.send(pt);
		} catch (Throwable e) {
			log.error("getFriendRank error");
			ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_0, packet.getopcode());
		}
	}
	/**
	 * 获取好友排行榜
	 * @param list
	 * @param type 
	 * @return
	 */
	public List<Rank> getFriendRankList(List<Object[]> list,int type){
		//排序
		//相同名次的个数
		int sameRankCount = 1;
		//当前名次
		int nowRank = 0;
		//上个名次的玩家
		PlayerEx upPlayerEx = null;
		PlayerSns upPlayerSns = null;
		List<Rank> listRank = new ArrayList<Rank>();
		for(int i = 0;i<list.size();i++){
			PlayerEx playerex = null;
			PlayerSns playerSns = null;
			playerex = (PlayerEx)(list.get(i)[0]);
			if(type== Rank.TYPE_MONEY_TREE){
				playerSns = (PlayerSns)(list.get(i)[1]);
			}
			for(int j = i+1;j<list.size();j++){
				PlayerEx playerex1 = null;
				PlayerSns playerSns1 = null;
				playerex1 = (PlayerEx)(list.get(j)[0]);
				if(type== Rank.TYPE_MONEY_TREE){
					playerSns1 = (PlayerSns)(list.get(j)[1]);
				}
				if(
						(type== Rank.TYPE_RICH && (
								playerex.getRich() < playerex1.getRich() || (playerex.getRich() == playerex1.getRich() && playerex.getLastSynchInfoTime() != null && playerex1.getLastSynchInfoTime()!= null && playerex.getLastSynchInfoTime().getTime() > playerex1.getLastSynchInfoTime().getTime())
								)) ||
						(type== Rank.TYPE_LOVE && (
								playerex.getLove() < playerex1.getLove() || (playerex.getLove() == playerex1.getLove() && playerex.getLastSynchInfoTime() != null && playerex1.getLastSynchInfoTime()!= null && playerex.getLastSynchInfoTime().getTime() > playerex1.getLastSynchInfoTime().getTime())
								)) ||
						(type== Rank.TYPE_GOLD && (
								playerex.getMoney() < playerex1.getMoney() || (playerex.getMoney() == playerex1.getMoney() && playerex.getLastSynchInfoTime() != null && playerex1.getLastSynchInfoTime()!= null && playerex.getLastSynchInfoTime().getTime() > playerex1.getLastSynchInfoTime().getTime())
								)) ||
						(type== Rank.TYPE_FOOD && (
								playerex.getFood() < playerex1.getFood() || (playerex.getFood() == playerex1.getFood() && playerex.getLastSynchInfoTime() != null && playerex1.getLastSynchInfoTime()!= null && playerex.getLastSynchInfoTime().getTime() > playerex1.getLastSynchInfoTime().getTime())
								)) ||
						(type== Rank.TYPE_WOOD && (
								playerex.getWoods() < playerex1.getWoods() || (playerex.getWoods() == playerex1.getWoods() && playerex.getLastSynchInfoTime() != null && playerex1.getLastSynchInfoTime()!= null && playerex.getLastSynchInfoTime().getTime() > playerex1.getLastSynchInfoTime().getTime())
								)) ||
						(type== Rank.TYPE_STONE && (
								playerex.getStone() < playerex1.getStone() || (playerex.getStone() == playerex1.getStone() && playerex.getLastSynchInfoTime() != null && playerex1.getLastSynchInfoTime()!= null && playerex.getLastSynchInfoTime().getTime() > playerex1.getLastSynchInfoTime().getTime())
								)) ||
						(type== Rank.TYPE_MONEY_TREE && (
								playerSns.getTreeGrade() < playerSns1.getTreeGrade() || (playerSns.getTreeGrade() == playerSns1.getTreeGrade() && playerex.getLastSynchInfoTime() != null && playerex1.getLastSynchInfoTime()!= null && playerex.getLastSynchInfoTime().getTime() > playerex1.getLastSynchInfoTime().getTime())
								)) ||
						(type== Rank.TYPE_PASS && (
								playerex.getAllBattleStarCount() < playerex1.getAllBattleStarCount() || (playerex.getAllBattleStarCount() == playerex1.getAllBattleStarCount() && playerex.getLastSynchInfoTime() != null && playerex1.getLastSynchInfoTime()!= null && playerex.getLastSynchInfoTime().getTime() > playerex1.getLastSynchInfoTime().getTime())
								)) ||
						(type== Rank.TYPE_FORCE && (
								playerex.getAllCatCount() < playerex1.getAllCatCount() || (playerex.getAllCatCount() == playerex1.getAllCatCount() && playerex.getLastSynchInfoTime() != null && playerex1.getLastSynchInfoTime()!= null && playerex.getLastSynchInfoTime().getTime() > playerex1.getLastSynchInfoTime().getTime())
								))
				){
					Object[] obj = {playerex,playerSns};
					Object[] obj1 = {playerex1,playerSns1};
					list.set(j, obj);
					list.set(i, obj1);
					playerex = (PlayerEx)(list.get(i)[0]);
					if(type== Rank.TYPE_MONEY_TREE){
						playerSns = (PlayerSns)(list.get(i)[1]);
					}
				}
			}
			
			if(		
				(upPlayerEx != null && 
					(
						(type== Rank.TYPE_RICH && upPlayerEx.getRich() == playerex.getRich()) ||
						(type== Rank.TYPE_LOVE && upPlayerEx.getLove() == playerex.getLove()) ||
						(type== Rank.TYPE_GOLD && upPlayerEx.getMoney() == playerex.getMoney()) ||
						(type== Rank.TYPE_FOOD && upPlayerEx.getFood() == playerex.getFood()) ||
						(type== Rank.TYPE_WOOD && upPlayerEx.getWoods() == playerex.getWoods()) ||
						(type== Rank.TYPE_STONE && upPlayerEx.getStone() == playerex.getStone()) ||
						(type== Rank.TYPE_PASS && upPlayerEx.getAllBattleStarCount() == playerex.getAllBattleStarCount()) ||
						(type== Rank.TYPE_FORCE && upPlayerEx.getAllCatCount() == playerex.getAllCatCount())
					)
				) 
				|| 
				(upPlayerSns != null &&
					(
						type== Rank.TYPE_MONEY_TREE && upPlayerSns.getTreeGrade() == playerSns.getTreeGrade()
					)
				)
			){
				//不需要相同点名次，如果需要再打开此功能
				//sameRankCount++;
				nowRank += sameRankCount;
				sameRankCount = 1;
			}else{
				nowRank += sameRankCount;
				sameRankCount = 1;
			}
			upPlayerEx = playerex;
			upPlayerSns = playerSns;
			
			Rank rank = new Rank();
			rank.setType(type);
			rank.setNowRank(nowRank);
			if(type== Rank.TYPE_RICH){
				rank.setValue(upPlayerEx.getRich());
			}else if(type== Rank.TYPE_LOVE){
				rank.setValue(upPlayerEx.getLove());
			}else if(type== Rank.TYPE_GOLD){
				rank.setValue(upPlayerEx.getMoney());
			}else if(type== Rank.TYPE_FOOD){
				rank.setValue(upPlayerEx.getFood());
			}else if(type== Rank.TYPE_WOOD){
				rank.setValue(upPlayerEx.getWoods());
			}else if(type== Rank.TYPE_STONE){
				rank.setValue(upPlayerEx.getStone());
			}else if(type== Rank.TYPE_MONEY_TREE){
				rank.setValue(upPlayerSns.getTreeGrade());
			}else if(type== Rank.TYPE_PASS){
				rank.setValue(upPlayerEx.getAllBattleStarCount());
			}else if(type== Rank.TYPE_FORCE){
				rank.setValue(upPlayerEx.getAllCatCount());
			}
			rank.setPlayerId(upPlayerEx.getInstanceId());
			rank.setPlayerName(upPlayerEx.getName());
			rank.setPlayerLvl(upPlayerEx.getLevel());
			listRank.add(rank);
		}
		return listRank;
	}

}
