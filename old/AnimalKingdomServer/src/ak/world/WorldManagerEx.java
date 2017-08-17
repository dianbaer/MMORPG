package ak.world;

import ak.market.Market;
import ak.player.PlayerEx;
import ak.player.LoginCheckFromWorldLock;
import ak.player.PlayerServiceEx;
import ak.playerSns.PlayerSns;
import ak.playerSns.PlayerSnsService;
import ak.server.OpCodeEx;
import cyou.mrd.ObjectAccessor;
import cyou.mrd.Platform;
import cyou.mrd.entity.Player;
import cyou.mrd.event.Event;
import cyou.mrd.event.GameEvent;
import cyou.mrd.io.OP;
import cyou.mrd.io.OPHandler;
import cyou.mrd.io.tcp.ClientSession;
import cyou.mrd.io.tcp.OpCode;
import cyou.mrd.io.tcp.TcpPacket;
import cyou.mrd.service.PlayerService;
import cyou.mrd.world.WorldManager;

@OPHandler(TYPE = OPHandler.TCP)
public class WorldManagerEx extends WorldManager {
	public void marketChanged(Market market) {
		
	}
	/**
	 * playerSns改变
	 * @param playerSns
	 */
//	public void playerSnsChanged(PlayerSns playerSns){
//		TcpPacket pt = new TcpPacket(OpCodeEx.AK_PLAYERSNS_CHANGE_CLIENT);
//		pt.putInt(playerSns.getPlayerId());
//		Platform.worldServer().send(pt);
//	}
//	public void tradeChanged(int playerId){
//		TcpPacket pt = new TcpPacket(OpCodeEx.AK_TRADE_CHANGE_CLIENT);
//		pt.putInt(playerId);
//		Platform.worldServer().send(pt);
//	}
//	public void marketChanged(int playerId){
//		TcpPacket pt = new TcpPacket(OpCodeEx.AK_MARKET_CHANGE_CLIENT);
//		pt.putInt(playerId);
//		Platform.worldServer().send(pt);
//	}
	public void noticePlayerLogout(int playerId,int serverId){
		TcpPacket pt = new TcpPacket(OpCodeEx.AK_NOTICE_PLAYER_LOGOUT_CLIENT);
		pt.putInt(playerId);
		pt.putInt(serverId);
		Platform.worldServer().send(pt);
	}
	
	@OP(code = OpCodeEx.AK_NOTICE_PLAYER_LOGOUT_SERVER)
	public void receivePlayerLogout(TcpPacket packet, ClientSession session) {
		int playerId = packet.getInt();
		Player player = ObjectAccessor.getPlayer(playerId);
		if(player != null){
			Platform.getEventManager().putEvent(new Event(GameEvent.EVENT_PLAYER_LOGOUTED, player));
		}
		
	}
	public void sendSystemNotice(String content){
		TcpPacket pt = new TcpPacket(OpCodeEx.AK_SEND_SYSTEM_NOTICE_CLIENT);
		pt.putString(content);
		Platform.worldServer().send(pt);
	}
	@OP(code = OpCodeEx.AK_NOTIFY_ONLINE_USER_NEW_NOTICE_SERVER)
	public void notifyOnlineUserNewNotice(TcpPacket packet, ClientSession session) {
		for (Player player : ObjectAccessor.players.values()) {
			player.getPool().setInt(PlayerEx.NOTIFY_CLIENT_NOTICE_SYSTEM, PlayerEx.NOTIFY_CLIENT);
			player.notifySave();
		}
		
	}
	public void checkMid(String mid){
		TcpPacket pt = new TcpPacket(OpCodeEx.AK_PLAYER_LOGIN_CHECK_CLIENT);
		pt.putString(mid);
		Platform.worldServer().send(pt);
	}
	public void initPlayerMemcached(int playerId){
		TcpPacket pt = new TcpPacket(OpCodeEx.AK_INIT_PLAYER_MEMCACHED_CLIENT);
		pt.putInt(playerId);
		Platform.worldServer().send(pt);
	}
	public void initPlayerSnsMemcached(int playerId){
		TcpPacket pt = new TcpPacket(OpCodeEx.AK_INIT_PLAYERSNS_MEMCACHED_CLIENT);
		pt.putInt(playerId);
		Platform.worldServer().send(pt);
	}
	public void loadPlayerSnsSelf(PlayerSns playerSns){
		TcpPacket pt = new TcpPacket(OpCodeEx.AK_LOAD_PLAYERSNS_SELF_CLIENT);
		pt.putInt(playerSns.getPlayerId());
		pt.putInt(playerSns.getActiveCount());
		pt.putInt(playerSns.getFirstVisit());
		pt.putInt(playerSns.getHelpCount());
		pt.putInt(playerSns.getLastActiveTime());
		pt.putInt(playerSns.getLastHarvestTime());
		pt.putInt(playerSns.getLastHelpDay());
		pt.putInt(playerSns.getLastSendDay());
		pt.putInt(playerSns.getSendCount());
		pt.putInt(playerSns.getTreeGrade());
		pt.putInt(playerSns.getTreeStatus());
		pt.putInt(playerSns.getTreeX());
		pt.putInt(playerSns.getTreeY());
		Platform.worldServer().send(pt);
	}
	public void savePlayerSnsSelf(PlayerSns playerSns){
		TcpPacket pt = new TcpPacket(OpCodeEx.AK_SAVE_PLAYERSNS_SELF_CLIENT);
		pt.putInt(playerSns.getPlayerId());
		pt.putInt(playerSns.getActiveCount());
		pt.putInt(playerSns.getFirstVisit());
		pt.putInt(playerSns.getHelpCount());
		pt.putInt(playerSns.getLastActiveTime());
		pt.putInt(playerSns.getLastHarvestTime());
		pt.putInt(playerSns.getLastHelpDay());
		pt.putInt(playerSns.getLastSendDay());
		pt.putInt(playerSns.getSendCount());
		pt.putInt(playerSns.getTreeGrade());
		pt.putInt(playerSns.getTreeStatus());
		pt.putInt(playerSns.getTreeX());
		pt.putInt(playerSns.getTreeY());
		Platform.worldServer().send(pt);
	}
	public void savePlayerSnsOther(PlayerSns playerSns,int waterPlayerId){
		TcpPacket pt = new TcpPacket(OpCodeEx.AK_SAVE_PLAYERSNS_OTHER_CLIENT);
		pt.putInt(playerSns.getPlayerId());
		pt.putInt(waterPlayerId);
		Platform.worldServer().send(pt);
	}
	public void loginFinish(String mid){
		TcpPacket pt = new TcpPacket(OpCodeEx.AK_PLAYER_LOGIN_FINISH_CLIENT);
		pt.putString(mid);
		Platform.worldServer().send(pt);
	}
	public void logoutFinish(String mid){
		TcpPacket pt = new TcpPacket(OpCodeEx.AK_PLAYER_LOGOUT_FINISH_CLIENT);
		pt.putString(mid);
		Platform.worldServer().send(pt);
	}
	@OP(code = OpCodeEx.AK_PLAYER_LOGIN_CHECK_SERVER)
	public void loginMidReturn(TcpPacket packet, ClientSession session) {
		int state = packet.getShort();
		String mid = packet.getString();
		
		PlayerServiceEx playerService = (PlayerServiceEx) Platform.getAppContext().get(PlayerService.class);
		playerService.callBackLoginCheckFromWorldLock(mid,state);
	}
	@OP(code = OpCodeEx.AK_INIT_PLAYER_MEMCACHED_SERVER)
	public void initPlayerMemcachedReturn(TcpPacket packet, ClientSession session) {
		int playerId = packet.getInt();
		PlayerServiceEx playerService = (PlayerServiceEx) Platform.getAppContext().get(PlayerService.class);
		playerService.callBackInitPlayerLock(playerId);
	}
	@OP(code = OpCodeEx.AK_INIT_PLAYERSNS_MEMCACHED_SERVER)
	public void initPlayerSnsMemcachedReturn(TcpPacket packet, ClientSession session) {
		int playerId = packet.getInt();
		PlayerSnsService playerSnsService =  Platform.getAppContext().get(PlayerSnsService.class);
		playerSnsService.callBackInitPlayerSnsLock(playerId);
	}
	@OP(code = OpCodeEx.AK_LOAD_PLAYERSNS_SELF_SERVER)
	public void loadPlayerSnsSelfReturn(TcpPacket packet, ClientSession session) {
		int playerId = packet.getInt();
		int visitCount = packet.getInt();
		String visitRecord = packet.getString();
		int treeStatus = packet.getInt();
		PlayerSnsService playerSnsService =  Platform.getAppContext().get(PlayerSnsService.class);
		playerSnsService.callBackReloadPlayerSnsLock(playerId,visitCount,visitRecord,treeStatus);
	}
	@OP(code = OpCode.WORLD_LOGIN_SERVER)
	public void loginSucess(TcpPacket packet, ClientSession session) {
		super.loginSucess(packet,session);
	}
	@OP(code = OpCode.WORLD_SYNC_TIME_SERVER)
	public void syncTime(TcpPacket packet, ClientSession session) {
		super.syncTime(packet,session);
	}

	@OP(code = OpCode.SESSION_CHANGED_SERVER)
	public void syncSessionTable(TcpPacket packet, ClientSession session) {
		super.syncSessionTable(packet,session);
	}
	@OP(code = OpCode.WORLD_VERIFY_SERVER)
	public void billingResponseVerify(TcpPacket packet, ClientSession session) {
		super.billingResponseVerify(packet,session);
	}
	

	@OP(code = OpCode.SERVER_REQUEST_WORLD_SERVER)
	public void worldResponseConnectState(TcpPacket packet, ClientSession session){
		super.worldResponseConnectState(packet,session);
	}
}
