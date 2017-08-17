/**
 * PlayerSnsService.java
 * ak.playerSns
 *
 *   version  date      	author
 * ──────────────────────────────────
 *    1.0	 2013年11月23日 		shiwei2006
 *
 * Copyright (c) 2013, www.cyou-inc.com All Rights Reserved.
*/

package ak.playerSns;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ak.ex.DataKeysEx;
import ak.friend.FriendHome;
import ak.gameAward.GameAwardDAO;
import ak.gameAward.GameAwardTemplate;
import ak.optLog.IUserOptLogService;
import ak.optLog.UserOptLog;
import ak.player.PlayerEx;
import ak.server.ErrorHandlerEx;
import ak.util.SRWLock;
import ak.world.WorldManagerEx;
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
import cyou.mrd.service.Service;
import cyou.mrd.util.ErrorHandler;
import cyou.mrd.util.Time;
import cyou.mrd.util.Utils;
import cyou.mrd.world.WorldManager;

/**
 * ClassName:PlayerSnsService
 * ClassDescription:  玩家互动相关服务，新版的交互操作可在此处理。
 *
 * @author   shiwei2006
 * @Date	 2013年11月23日		下午6:42:48
 * @version  1.0
 */
@OPHandler(TYPE = OPHandler.HTTP_EVENT)
public class PlayerSnsService implements Service {
	
	private static final Logger log = LoggerFactory.getLogger(PlayerSnsService.class);//日志记录
	
	public static int ACTIVE_MAX_NUM = Platform.getConfiguration().getInt("active_maxnum");//体力值上限
	
	private static int ACTIVE_UNIT = Platform.getConfiguration().getInt("active_unit");//恢复单位，默认6分钟

	private static final int TREE_MAX_GRADE = 40;//暂定摇钱树最大等级40级
	/**
	 * 每天邮件获得爱心值最大次数
	 */
	public static final int LOVE_MAIL_MAXNUM_EVERYDAY = Platform.getConfiguration().getInt("love_mail_maxnum_everyday");
	/**
	 * 爱心值最大值
	 */
	public static final int LOVE_MAX_VALUE = Platform.getConfiguration().getInt("love_max_value");
	/**
	 * 邮件每日每人一次 爱心值
	 */
	public static final int LOVE_MAIL_ONEPLAYER_NUM_EVERYDAY = Platform.getConfiguration().getInt("love_mail_oneplayer_num_everyday");
	/**
	 * 送礼每日每人一次 爱心值
	 */
	public static final int LOVE_SEND_GIFT_ONEPLAYER_NUM_EVERYDAY = Platform.getConfiguration().getInt("love_send_gift_oneplayer_num_everyday");
	/**
	 * 从world初始化playersns到memcached的锁的map
	 */
	public Map<Integer, SRWLock> initPlayerSnsFromWorldLockMap = new ConcurrentHashMap<Integer, SRWLock>();
	/**
	 * 重新从world加载playersns的锁的map
	 */
	public Map<Integer, SRWLock> reloadPlayerSnsFromWorldLockMap = new ConcurrentHashMap<Integer, SRWLock>();
	@Override
	public String getId() {
		return "PlayerSnsService";
	}

	@Override
	public void startup() throws Exception {

	}

	@Override
	public void shutdown() throws Exception {

	}
	public synchronized SRWLock getInitPlayerSnsLock(int id){
		SRWLock sRWLock = null;
		if(initPlayerSnsFromWorldLockMap.get(id) == null){
			sRWLock = new SRWLock();
			initPlayerSnsFromWorldLockMap.put(id, sRWLock);
		}
		sRWLock = initPlayerSnsFromWorldLockMap.get(id);
		synchronized (sRWLock) {
			sRWLock.setBack(false);
		}
		return sRWLock;
	}
	public void callBackInitPlayerSnsLock(int id){
		SRWLock sRWLock = getInitPlayerSnsLock(id);
		synchronized (sRWLock) {
			sRWLock.setBack(true);
			sRWLock.notify();
		}
	}
	/**
	 * 获得重新加载自己的摇钱树数据的锁
	 * @param id
	 * @return
	 */
	public synchronized SRWLock getReloadPlayerSnsLock(int id){
		SRWLock sRWLock = null;
		if(reloadPlayerSnsFromWorldLockMap.get(id) == null){
			sRWLock = new SRWLock();
			reloadPlayerSnsFromWorldLockMap.put(id, sRWLock);
		}
		sRWLock = reloadPlayerSnsFromWorldLockMap.get(id);
		synchronized (sRWLock) {
			sRWLock.setBack(false);
		}
		return sRWLock;
	}
	/**
	 * 从新加载自己的摇钱树数据的返回
	 * @param id
	 * @param visitCount
	 * @param visitRecord
	 * @param treeStatus
	 */
	public void callBackReloadPlayerSnsLock(int id,int visitCount,String visitRecord,int treeStatus){
		SRWLock sRWLock = getReloadPlayerSnsLock(id);
		PlayerEx player = (PlayerEx)ObjectAccessor.getPlayer(id);
		player.getPlayerSns().setVisitCount(visitCount);
		player.getPlayerSns().setVisitRecord(visitRecord);
		player.getPlayerSns().setTreeStatus(treeStatus);
		log.info("PlayerSnsService.callBackReloadPlayerSnsLock:info[ world reload playersns finish]");
		synchronized (sRWLock) {
			sRWLock.setBack(true);
			sRWLock.notify();
		}
	}
	/**
	 * 初始化自己的playersns数据
	 * @param player
	 */
	public void initPlayerSns(PlayerEx player){
		
		Data data = Platform.dataCenter().getData(DataKeysEx.playerSnsKey(player.getId()));
		PlayerSns playerSns = null;
		if (data == null) {
			
			//进行world服务器验证操作
			SRWLock sRWLock = getInitPlayerSnsLock(player.getId());
			//去world服务器验证mid
			if(Platform.worldServer() != null){
				WorldManagerEx wmanager = (WorldManagerEx)Platform.getAppContext().get(WorldManager.class);
				wmanager.initPlayerSnsMemcached(player.getId());
			}else{
				return;
			}
			synchronized (sRWLock) {
				try {
					if(!sRWLock.isBack()){
						sRWLock.wait(30000);
					}
				} catch (Exception e) {
					log.info("PlayerSnsService.initPlayerSns:error[through world init playerSns memcached error]");
				}
			}
			data = Platform.dataCenter().getData(DataKeysEx.playerSnsKey(player.getId()));
			if(data == null){
				return;
			}else{
				log.info("PlayerSnsService.initPlayerSns:info[ world init playersns memcached finish]");
				playerSns = (PlayerSns) data.value;
			}
			
		} else {
			playerSns = (PlayerSns) data.value;
		}
		//把别人能修改的数据重置一下
		if(player.getPlayerSns() != null){
			
		}else{
			player.setPlayerSns(playerSns);
		}
	}
	/**
	 * 重新加载该player的playersns的摇钱树的最新数据
	 * @param player
	 */
	public void loadPlayerSns(PlayerEx player){
		
		SRWLock sRWLock = getReloadPlayerSnsLock(player.getId());
		
		if(Platform.worldServer() != null){
			WorldManagerEx wmanager = (WorldManagerEx)Platform.getAppContext().get(WorldManager.class);
			wmanager.loadPlayerSnsSelf(player.getPlayerSns());
		}else{
			return;
		}
		synchronized (sRWLock) {
			try {
				if(!sRWLock.isBack()){
					sRWLock.wait(30000);
				}
			} catch (Exception e) {
				log.info("PlayerSnsService.loadPlayerSns:error[through world reload playerSns error]");
			}
		}
		
	}
	/**
	 * 获得别人的playersns数据
	 * @param playerId
	 * @return
	 */
	public PlayerSns loadPlayerSns(int playerId){
		Data data = Platform.dataCenter().getData(DataKeysEx.playerSnsKey(playerId));
		PlayerSns playerSns = null;
		if (data == null) {
			
			//进行world服务器验证操作
			SRWLock sRWLock = getInitPlayerSnsLock(playerId);
			//去world服务器验证mid
			if(Platform.worldServer() != null){
				WorldManagerEx wmanager = (WorldManagerEx)Platform.getAppContext().get(WorldManager.class);
				wmanager.initPlayerSnsMemcached(playerId);
			}else{
				return null;
			}
			synchronized (sRWLock) {
				try {
					if(!sRWLock.isBack()){
						sRWLock.wait(30000);
					}
				} catch (Exception e) {
					log.info("PlayerSnsService.loadPlayerSns:error[through world init playerSns memcached error]");
				}
			}
			data = Platform.dataCenter().getData(DataKeysEx.playerSnsKey(playerId));
			if(data == null){
				return null;
			}else{
				log.info("PlayerSnsService.loadPlayerSns:info[ world init playersns memcached finish]");
				playerSns = (PlayerSns) data.value;
			}
			
			
		} else {
			playerSns = (PlayerSns) data.value;
			
		}
		return playerSns;
	}
	public void savePlayerSns(PlayerEx player){
		if(Platform.worldServer() != null){
			WorldManagerEx wmanager = (WorldManagerEx)Platform.getAppContext().get(WorldManager.class);
			wmanager.savePlayerSnsSelf(player.getPlayerSns());
		}
	}
	/**
	 * updatePlayerSns: 保存玩家互动记录，并更新对应缓存，注：ps为完整对象结构！！！
	 * @param ps 
	 * @throws Exception 
	 * @return void
	*/
	public void savePlayerSns(PlayerSns playerSns,int waterPlayerId){
		if(Platform.worldServer() != null){
			WorldManagerEx wmanager = (WorldManagerEx)Platform.getAppContext().get(WorldManager.class);
			wmanager.savePlayerSnsOther(playerSns, waterPlayerId);
		}
	}
	
	/**
	 * recountPlayerActivePoints: 重新计算玩家的体力值，系统自动恢复。注：在每次消耗时需要先调用此方法重新进行计算！
	 * 此方法不会更新写入最新体力值，只是返回当前最新。
	 * @param playerId
	 * @param now 系统当前时间，单位：秒。
	 * @throws Exception 
	 * @return PlayerSns 此对象只包含3个字段，即玩家ID，最新体力值，最后恢复时间。具体业务逻辑根据这3项数值进行计算并更新即可！
	*/
	public void recountPlayerActivePoints(PlayerEx player, int now) throws Exception{
		if(player.getPlayerSns().getActiveCount() >= ACTIVE_MAX_NUM){//已到上限
			player.getPlayerSns().setActiveCount(ACTIVE_MAX_NUM);
			player.getPlayerSns().setLastActiveTime(now);
		}
		int lastTime = player.getPlayerSns().getLastActiveTime();//最后一次恢复时间
		if(lastTime <= 0){//兼容错误数据
			lastTime = now;
		}
		int points = (now - lastTime) / ACTIVE_UNIT;//可以恢复的点数
		if(points < 0){//兼容错误数据
			points = 0;
		}
		//发送结果
		int activeCount = player.getPlayerSns().getActiveCount() + points;
		if(activeCount >= ACTIVE_MAX_NUM){
			activeCount = ACTIVE_MAX_NUM;
			player.getPlayerSns().setActiveCount(activeCount);
			player.getPlayerSns().setLastActiveTime(now);
		}
		else{
			player.getPlayerSns().setActiveCount(activeCount);
			player.getPlayerSns().setLastActiveTime(lastTime + points * ACTIVE_UNIT);//对玩家有利
		}
	}
	/**
	 * 获取达到体力最大值需要的时间
	 * @param ps
	 * @return
	 */
	public int getActiveRecoverTime(PlayerSns ps){
		int needActiveCount = ACTIVE_MAX_NUM - ps.getActiveCount();
		return needActiveCount * ACTIVE_UNIT;
	}
	
	/**
	 * updateMoneyTree: 更新玩家自己的摇钱树，包括树等级和x,y信息，由于此更新为后台操作，所以检测条件不符合时不发送结果。
	 * @param packet
	 * @param session
	 * @throws Exception 
	 * @return void
	*/
	@OP(code = HOpCodeEx.CLIENT_UPDATE_MONEY_TREE)
	public void updateMoneyTree(Packet packet, HSession session) throws Exception {
		
		PlayerEx player = (PlayerEx) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			return;
		}
		//RunTimeMonitor rt = packet.getRunTimeMonitor();
		//检测参数
		int gradeId = 0, x = -1, y = -1;
		if(packet.containsKey("gradeId")){
			gradeId = packet.getInt("gradeId");
		}
		if(packet.containsKey("x")){
			x = packet.getInt("x");
		}
		if(packet.containsKey("y")){
			y = packet.getInt("y");
		}
		if(gradeId < 1 || gradeId > TREE_MAX_GRADE || x < 0 || y < 0){
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_15, packet.getopcode());
			return;
		}
		//正式处理
		loadPlayerSns(player);
		if(player.getPlayerSns() == null){
			ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_100, packet.getopcode());
			return;
		}
		if(player.getPlayerSns().getTreeStatus() == PlayerSns.TREE_STATUS_INIT){//初始化状态直接更新
			player.getPlayerSns().setTreeGrade(gradeId);
			player.getPlayerSns().setTreeX(x);
			player.getPlayerSns().setTreeY(y);
			player.getPlayerSns().setTreeStatus(PlayerSns.TREE_STATUS_WATER);
			savePlayerSns(player);
			//返回结果
			Packet pt = new JSONPacket(HOpCodeEx.SERVER_UPDATE_MONEY_TREE);
			pt.put("result", 1);
			pt.put("status", PlayerSns.TREE_STATUS_WATER);
			session.send(pt);
			return;
		}
		//处于冷却或收获状态时，不能更新等级
		MoneyTreeTemplate template = PlayerSnsDAO.getMoneyTreeTemplate(player.getPlayerSns().getTreeGrade());//当前等级的配置
		if(template == null){
			ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_100, packet.getopcode());
			return;
		}
		
		if(player.getPlayerSns().getTreeStatus() == PlayerSns.TREE_STATUS_HARVEST || player.getPlayerSns().getTreeStatus() == PlayerSns.TREE_STATUS_COOLING){
			//返回结果
			Packet pt = new JSONPacket(HOpCodeEx.SERVER_UPDATE_MONEY_TREE);
			pt.put("result", 0);
			pt.put("status", player.getPlayerSns().getTreeStatus());
			session.send(pt);
			return;
		}
		//更新等级
		player.getPlayerSns().setTreeGrade(gradeId);
		player.getPlayerSns().setTreeX(x);
		player.getPlayerSns().setTreeY(y);
		savePlayerSns(player);
		//返回结果
		Packet pt = new JSONPacket(HOpCodeEx.SERVER_UPDATE_MONEY_TREE);
		pt.put("result", 1);
		pt.put("status", player.getPlayerSns().getTreeStatus());
		session.send(pt);
	}
	/**
	 * showMoneyTree: 摇钱树显示面板(附加同步等级)
	 * @param packet
	 * @param session
	 * @throws Exception 
	 * @return void
	*/
	@OP(code = HOpCodeEx.CLIENT_SHOW_MONEY_TREE)
	public void showMoneyTree(Packet packet, HSession session) throws Exception {
		log.info("[HTTPRequest] session:{}  packet[showMoneyTree]]", session.getSessionId());
		PlayerEx player = (PlayerEx) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			return;
		}
		//RunTimeMonitor rt = packet.getRunTimeMonitor();
		//先同步等级，再后续显示
		//updateMoneyTree(packet, session);
		
		//正式处理
		loadPlayerSns(player);
		if(player.getPlayerSns() == null){
			ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_100, packet.getopcode());
			return;
		}
		MoneyTreeTemplate template = PlayerSnsDAO.getMoneyTreeTemplate(player.getPlayerSns().getTreeGrade());//当前等级的配置
		if(template == null){
			ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_100, packet.getopcode());
			return;
		}
		//发送结果
		Packet pt = new JSONPacket(HOpCodeEx.SERVER_SHOW_MONEY_TREE);
		pt.put("gradeId", player.getPlayerSns().getTreeGrade());//树的等级
		pt.put("status", player.getPlayerSns().getTreeStatus());
		if(player.getPlayerSns().getTreeStatus() == PlayerSns.TREE_STATUS_COOLING){
			int coolTime = template.getCoolTime() - (getCurrentTime() - player.getPlayerSns().getLastHarvestTime());
			pt.put("coolTime", coolTime);//冷却倒计时
		}
		pt.put("visitCount", player.getPlayerSns().getVisitCount());
		pt.put("helpCount", getCurrentHelpCount(player));
		session.send(pt);
		savePlayerSns(player);
	}
	
	/**
	 * getCurrentTime: 获得当前时间，单位：秒
	 * @return int
	*/
	private int getCurrentTime(){
		return (int)(System.currentTimeMillis() / 1000);
	}
	
	/**
	 * getCurrentHelpCount: 获取当前帮助好友总次数，对变天进行了重置更新！
	 * @param sns
	 * @throws Exception 
	 * @return int 返回为当前最新的总次数
	*/
	private int getCurrentHelpCount(PlayerEx player) throws Exception{
		if(player.getPlayerSns().getLastHelpDay() == Time.day){//今天
			return player.getPlayerSns().getHelpCount();
		}
		//变天重置为0，并更新日期
		player.getPlayerSns().setHelpCount(0);
		player.getPlayerSns().setLastHelpDay(Time.day);
		//savePlayerSns(player);
		return 0;
	}

	/**
	 * waterMoneyTree: 好友浇水操作
	 * @param packet
	 * @param session
	 * @throws Exception 
	 * @return void
	*/
	@OP(code = HOpCodeEx.CLIENT_WATER_MONEY_TREE)
	public void waterMoneyTree(Packet packet, HSession session) throws Exception {
		log.info("[HTTPRequest] session:{}  packet[waterMoneyTree]]", session.getSessionId());
		PlayerEx player = (PlayerEx) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			return;
		}
		//检测参数
		int friendId = -1;
		if(packet.containsKey("friendId")){
			friendId = packet.getInt("friendId");
		}
		if(friendId < 1){
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_15, packet.getopcode());
			return;
		}
		//正式处理
		//是否为自己好友
		RelationService rsService = Platform.getAppContext().get(RelationService.class);
		PlayerRelation pr = rsService.findRelation(player.getId());
		if(pr == null || pr.getFriends().findPlayer(friendId) == -1){
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_12, packet.getopcode());
			return;
		}
		//注：单向好友也可以进行浇水操作，在此不作树主人好友判断！
		//由于浇水操作只涉及到playerSns对象，所以不对playerEx玩家数据进行访问。
		PlayerSns targetSns = loadPlayerSns(friendId);
		if(targetSns == null){//好友的树数据异常，不能访问！
			ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_101, packet.getopcode());
			return;
		}
		//好友是否有摇钱树
		if(targetSns.getTreeStatus() == PlayerSns.TREE_STATUS_INIT || targetSns.getTreeGrade() < 1){
			ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_102, packet.getopcode());
			return;
		}
		//此树是否已浇过水
		String viRecord = targetSns.getVisitRecord();
		if(viRecord != null && !"".equals(viRecord)){
			boolean exist = false;
			String[] vis = viRecord.split(",");
			for(String v : vis){
				if(!Utils.isNumber(v)){
					continue;
				}
				int id = Integer.parseInt(v);
				if(id == player.getId()){
					exist = true;
					break;
				}
			}
			if(exist){
				ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_107, packet.getopcode());
				return;
			}
		}
		//此树是否可浇水
		MoneyTreeTemplate template = PlayerSnsDAO.getMoneyTreeTemplate(targetSns.getTreeGrade());//当前等级的配置
		if(template == null){
			ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_100, packet.getopcode());
			return;
		}
		int now = getCurrentTime();
		
		if(targetSns.getTreeStatus() != PlayerSns.TREE_STATUS_WATER){
			ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_103, packet.getopcode());
			return;
		}
		//检测自身
		loadPlayerSns(player);
		if(player.getPlayerSns() == null){
			ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_101, packet.getopcode());
			return;
		}
		//自己必须先造此树才能与好友进行交互。
		if(player.getPlayerSns().getTreeGrade() < 1 || player.getPlayerSns().getTreeStatus() == PlayerSns.TREE_STATUS_INIT){
			ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_104, packet.getopcode());
			return;
		}
		template = PlayerSnsDAO.getMoneyTreeTemplate(player.getPlayerSns().getTreeGrade());//当前等级的配置
		if(template == null){
			ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_100, packet.getopcode());
			return;
		}
		//自己帮助次数上限
		int helpCount = getCurrentHelpCount(player);
		if(helpCount >= template.getHelpWaterTimes()){
			ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_105, packet.getopcode());
			return;
		}
		//自身体力是否符合
		recountPlayerActivePoints(player, now);
		if(player.getPlayerSns().getActiveCount() < template.getCostActiveCount()){
			ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_106, packet.getopcode());
			return;
		}
		//正式浇水
		//1.扣除自身体力
		//2.更新自身帮助总次数
		player.getPlayerSns().setHelpCount(helpCount + 1);
		player.getPlayerSns().setActiveCount(player.getPlayerSns().getActiveCount() - template.getCostActiveCount());
		
		savePlayerSns(player);
		
		//4.领取相应的奖励(并记录下奖励日志)
		int awardGrade = targetSns.getTreeGrade();//奖励等级
		//奖励日志记录
		IUserOptLogService userOptLogService = Platform.getAppContext().get(IUserOptLogService.class);
		userOptLogService.addUserOptLog(session, player.getId(), UserOptLog.TYPE_SYSTEM_AWARD, template.getFriendAwardId(), UserOptLog.CONTENT_1);
		savePlayerSns(targetSns,player.getId());
		//5.发送结果
		Packet pt = new JSONPacket(HOpCodeEx.SERVER_WATER_MONEY_TREE);
		pt.put("awardGrade", awardGrade);
		pt.put("visitCount", targetSns.getVisitCount());
		pt.put("status", targetSns.getTreeStatus());
		
		pt.put("activePoints", player.getPlayerSns().getActiveCount());
		pt.put("activeMaxPoints", PlayerSnsService.ACTIVE_MAX_NUM);
		pt.put("recoverTime", getActiveRecoverTime(player.getPlayerSns()));
		pt.put("isCanWater", isCanWater(player,friendId) == 0 ? 1 : 0);
		session.send(pt);
		//6.向树主人发信
		//todo...
	}
	public int isCanWater(PlayerEx player, int friendId) throws Exception{
		return 1;
	}
	/**
	 * harvestMoneyTree: 摇钱树收获
	 * @param packet
	 * @param session
	 * @throws Exception 
	 * @return void
	*/
	@OP(code = HOpCodeEx.CLIENT_HARVEST_MONEY_TREE)
	public void harvestMoneyTree(Packet packet, HSession session) throws Exception {
		log.info("[HTTPRequest] session:{}  packet[harvestMoneyTree]]", session.getSessionId());
		PlayerEx player = (PlayerEx) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			return;
		}
		//1.处理收获
		loadPlayerSns(player);
		if(player.getPlayerSns() == null){
			ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_100, packet.getopcode());
			return;
		}
		MoneyTreeTemplate template = PlayerSnsDAO.getMoneyTreeTemplate(player.getPlayerSns().getTreeGrade());//当前等级的配置
		if(template == null){
			ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_100, packet.getopcode());
			return;
		}
		int now = getCurrentTime();
		if(player.getPlayerSns().getTreeStatus() != PlayerSns.TREE_STATUS_HARVEST){
//			ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_108, packet.getopcode());
//			return;
			Packet pt = new JSONPacket(HOpCodeEx.SERVER_HARVEST_MONEY_TREE);
			pt.put("result", 0);
			pt.put("awardGrade", player.getPlayerSns().getTreeGrade());
			session.send(pt);
			return;
		}
		//更新为冷却中
		player.getPlayerSns().setTreeStatus(PlayerSns.TREE_STATUS_COOLING);
		player.getPlayerSns().setLastHarvestTime(now);
		savePlayerSns(player);
		//发放奖励结果
		//奖励日志记录
		IUserOptLogService userOptLogService = Platform.getAppContext().get(IUserOptLogService.class);
		userOptLogService.addUserOptLog(session, player.getId(), UserOptLog.TYPE_SYSTEM_AWARD, template.getSelfAwardId(),UserOptLog.CONTENT_1);
		
		Packet pt = new JSONPacket(HOpCodeEx.SERVER_HARVEST_MONEY_TREE);
		pt.put("awardGrade", player.getPlayerSns().getTreeGrade());
		pt.put("result", 1);
		session.send(pt);
	}
	
	/**
	 * visitMoneyTree: 此方法由好友访问时添加进去，记录当前好友的摇钱树的状态信息
	 * @param friendId
	 * @throws Exception 
	 * @return JSONObject
	*/
	public JSONObject visitMoneyTree(int friendId) throws Exception{
		JSONObject data = new JSONObject();
		if(friendId < 1){
			data.put("exist", 0);
			return data;
		}
		PlayerSns ps = loadPlayerSns(friendId);
		if(ps == null || ps.getTreeGrade() < 1 || ps.getTreeStatus() == PlayerSns.TREE_STATUS_INIT){
			data.put("exist", 0);
			return data;
		}
		int now = getCurrentTime();
		MoneyTreeTemplate template = PlayerSnsDAO.getMoneyTreeTemplate(ps.getTreeGrade());//当前等级的配置
		if(template == null){
			data.put("exist", 0);
			return data;
		}
		data.put("grade", ps.getTreeGrade());
		data.put("status", ps.getTreeStatus());
		if(ps.getTreeStatus() == PlayerSns.TREE_STATUS_COOLING){
			int coolTime = template.getCoolTime() - (now - ps.getLastHarvestTime());
			data.put("coolTime", coolTime);//冷却倒计时
		}
		data.put("x", ps.getTreeX());
		data.put("y", ps.getTreeY());
		data.put("visitCount", ps.getVisitCount());
		data.put("exist", 1);
		return data;
	}
	/**
	 * 增加爱心值
	 * @param gameAwardId
	 * @param player
	 * @param friendId
	 * @return
	 */
	public int addLove(int gameAwardId,PlayerEx player, int friendId){
		try {
			
			GameAwardTemplate gameAwardTemplate = GameAwardDAO.getGameAward(gameAwardId);
			FriendHome friendHome = player.getFriendHome(friendId);
			if(gameAwardTemplate == null){
				return 0;
			}
			//发邮件获得爱心值
			if(gameAwardId == GameAwardTemplate.ID_SEND_MAIL_REWARD_LOVE){
				
				//每天发邮件获得爱心值不得超过30次，每人每天只能获取一次
				if(player.getPlayerSns().getTodaySendCount() >= LOVE_MAIL_MAXNUM_EVERYDAY || friendHome.getTodaySendTimes() >= LOVE_MAIL_ONEPLAYER_NUM_EVERYDAY){
					return 0;
				}else{
					player.getPlayerSns().addSendCount();
					savePlayerSns(player);
					friendHome.addSendTimes();
				}
			//每人每天送礼物获得爱心值一次
			}else if((int)(gameAwardId/100) == GameAwardTemplate.IDS_SEND_GIFT){
				if(friendHome.getTodayTransportTimes() >= LOVE_SEND_GIFT_ONEPLAYER_NUM_EVERYDAY){
					return 0;
				}else{
					
				}
				
			}
			player.setLove(player.getLove()+gameAwardTemplate.getLove());
			//不能大于最大值
			if(player.getLove() > LOVE_MAX_VALUE){
				player.setLove(LOVE_MAX_VALUE);
			}
			//上线时修改
			//通知保存数据(先强制保存，好查数据库验证)
			//player.notifySave();
			
			return gameAwardTemplate.getLove();
		} catch (Throwable e) {
			return 0;
		}
		
	}
}










