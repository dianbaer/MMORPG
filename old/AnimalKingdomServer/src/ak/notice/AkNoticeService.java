package ak.notice;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ak.ex.DataKeysEx;
import ak.player.PlayerEx;
import ak.server.ErrorHandlerEx;
import ak.world.WorldManagerEx;
import cyou.mrd.ObjectAccessor;
import cyou.mrd.Platform;
import cyou.mrd.data.Data;
import cyou.mrd.entity.Player;
import cyou.mrd.io.OP;
import cyou.mrd.io.OPHandler;
import cyou.mrd.io.Packet;
import cyou.mrd.io.http.HSession;
import cyou.mrd.io.http.JSONPacket;
import cyou.mrd.io.tcp.HOpCodeEx;
import cyou.mrd.service.Service;
import cyou.mrd.world.WorldManager;

/**
 * 公告系统
 * @author xuepeng
 *
 */
@OPHandler(TYPE = OPHandler.HTTP)
public class AkNoticeService implements Service {

	private static final Logger log = LoggerFactory.getLogger(AkNoticeService.class);
	
	/**
	 * 交互公告
	 */
	public static TIntObjectMap<Integer>[] interactiveNotice = new TIntObjectMap[1];
	/**
	 * 摇钱树收益后
	 */
	public static final int TEMPLATEID_3201 = 3201;
	/**
	 * 每日0点摇钱树帮助好友浇水次数刷新
	 */
	public static final int TEMPLATEID_3202 = 3202;
	/**
	 * 用户收到好友发来新邮件后
	 */
	public static final int TEMPLATEID_3203 = 3203;
	/**
	 * 用户消耗的体力值恢复已满
	 */
	public static final int TEMPLATEID_3204 = 3204;
	/**
	 * 用户好友将市场商品进行更新或有新上架的商品后
	 */
	public static final int TEMPLATEID_3205 = 3205;
	
	
	//对应于上面的模版，交互公告的id
	public static final int INTERACTIVE_1 = -1;
	public static final int INTERACTIVE_2 = -2;
	public static final int INTERACTIVE_3 = -3;
	public static final int INTERACTIVE_4 = -4;
	public static final int INTERACTIVE_5 = -5;
	@Override
	public String getId() {
		return "AkNoticeService";
	}

	@Override
	public void startup() throws Exception {
		//载入交互公告
		interactiveNotice[0] = new TIntObjectHashMap<Integer>();
		interactiveNotice[0].put(INTERACTIVE_1, TEMPLATEID_3201);
		interactiveNotice[0].put(INTERACTIVE_2, TEMPLATEID_3202);
		interactiveNotice[0].put(INTERACTIVE_3, TEMPLATEID_3203);
		interactiveNotice[0].put(INTERACTIVE_4, TEMPLATEID_3204);
		interactiveNotice[0].put(INTERACTIVE_5, TEMPLATEID_3205);
	}

	@Override
	public void shutdown() throws Exception {

	}
	/**
	 * 测试gm用来发送系统公告
	 * @param packet
	 * @param session
	 */
	@OP(code = 2300)
	public void testGmSendSystemNotice(Packet packet, HSession session) {
		try {
			String content = packet.getString("content");
			if(content != null && content != ""){
				sendSystemNotice(content);
				
			}
		} catch (Throwable e) {
			log.error("testGmSendSystemNotice error",e);
		}
		
	}
	/**
	 * 发送系统公告
	 * @param content
	 * @return
	 */
	public boolean sendSystemNotice(String content){
		if(Platform.worldServer() != null){
			WorldManagerEx wmanager = (WorldManagerEx)Platform.getAppContext().get(WorldManager.class);
			wmanager.sendSystemNotice(content);
			return true;
		}
		return false;
	}
	/**
	 * 测试gm发送交互公告
	 * @param packet
	 * @param session
	 */
	@OP(code = 2301)
	public void testGmSendInteractiveNotice(Packet packet, HSession session) {
		try {
			//交互公告id
			int id = packet.getInt("id");
			PlayerEx[] playerArray = new PlayerEx[1];
			playerArray[0] = (PlayerEx)session.client();
			this.sendInteractiveNotice(id,playerArray);
		} catch (Throwable e) {
			log.error("testGmSendNotice error",e);
		}
		
	}
	@OP(code = 2302)
	public void testGmSendInteractiveNoticeToAll(Packet packet, HSession session){
		//交互公告id
		int id = packet.getInt("id");
		PlayerEx[] playerArray = new PlayerEx[ObjectAccessor.players.size()];
		int i = 0;
		for (Player player : ObjectAccessor.players.values()) {
			playerArray[i] = (PlayerEx)player;
			i++;
		}
		this.sendInteractiveNotice(id,playerArray);
	}
	/**
	 * 发送交互公告
	 * @param interactiveNoticeID 交互公告id
	 * @param player player数组
	 * @return
	 */
	public void sendInteractiveNotice(int interactiveNoticeID, PlayerEx[] playerArray){
		for(int i = 0; i < playerArray.length;i++){
			if(playerArray[i] != null){
				//清除已读
				playerArray[i].setNoticeUnReaded(interactiveNoticeID);
				//通知该玩家有新的交互信息
				playerArray[i].getPool().setInt(PlayerEx.NOTIFY_CLIENT_NOTICE_INTERACTIVE, PlayerEx.NOTIFY_CLIENT);
				playerArray[i].notifySave();
			}
		}
	}
	
	/**
	 * 获取交互公告列表
	 * @param packet
	 * @param session
	 */
	@OP(code = HOpCodeEx.INTERACTIVE_NOTICE_LIST_CLIENT)
	public void playerInteractiveListNotice(Packet packet, HSession session) {
		try {
			PlayerEx player = (PlayerEx) session.client();
			//发送人没有登陆
			if (player == null) {
				ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_1, packet.getopcode());
				return;
			}
			Map<Integer,Integer> interActiveNoticeList = getUnReceiveInteractiveNotice(player);
			
			Packet pt = new JSONPacket(HOpCodeEx.INTERACTIVE_NOTICE_LIST_SERVER);
			JSONArray ja = new JSONArray();
			
			if (interActiveNoticeList != null && !interActiveNoticeList.isEmpty()) {
				Object s[] = interActiveNoticeList.keySet().toArray();
				for (int i = 0; i < interActiveNoticeList.size(); i++) {
					JSONObject jo = new JSONObject();
					jo.put("noticeId", s[i]);
					jo.put("templateId", interActiveNoticeList.get(s[i]));
					ja.add(jo);
				}
			}
			pt.put("noticeList", ja.toString());
			session.send(pt);
		} catch (Throwable e) {
			log.error("playerInteractiveListNotice error",e);
			ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_0, packet.getopcode());
		}
	}
	/**
	 * 获取未收到的交互公告列表
	 * @param player
	 * @return
	 * @throws Exception
	 */
	public Map<Integer,Integer> getUnReceiveInteractiveNotice(Player player) throws Exception {
		Map<Integer,Integer> unReceiveNoticeList = new HashMap<Integer,Integer>();
		int[] hadNotices = player.getReadedNoticeIds();
		int indexId = 0;
		int id = 0;
		boolean isHave;
		int s[] = interactiveNotice[0].keySet().toArray();
		for(int i = 0; i < s.length; i++){
			isHave = false;
			indexId = s[i];
			for(int j = 0; j < hadNotices.length; j++){
				id = hadNotices[j];
				if(id == indexId){
					isHave = true;
					break;
				}
			}
			if(!isHave){
				unReceiveNoticeList.put(s[i], interactiveNotice[0].get(s[i]));
			}
		}
		return unReceiveNoticeList;
	}
	/**
	 * 获取系统公告列表
	 * @param packet
	 * @param session
	 */
	@OP(code = HOpCodeEx.NOTICE_LIST_CLIENT)
	public void playerListNotice(Packet packet, HSession session) {
		try {
			PlayerEx player = (PlayerEx) session.client();
			//发送人没有登陆
			if (player == null) {
				ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_1, packet.getopcode());
				return;
			}
			
			List<Notice> systemNoticeList = getUnReceiveSystemNotice(player);
			
			Packet pt = new JSONPacket(HOpCodeEx.NOTICE_LIST_SERVER);
			JSONArray ja = new JSONArray();
			
			if (systemNoticeList != null && !systemNoticeList.isEmpty()) {
				for (int i = systemNoticeList.size() - 1; i >= 0; i--) {
					Notice notice = systemNoticeList.get(i);
					JSONObject jo = new JSONObject();
					jo.put("noticeId", notice.getNoticeId());
					jo.put("content", notice.getContent());
					jo.put("addTime", notice.getAddTime().getTime() / 1000);
					ja.add(jo);
				}
			}
			pt.put("noticeList", ja.toString());
			session.send(pt);
		} catch (Throwable e) {
			log.error("playerListNotice error",e);
			ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_0, packet.getopcode());
		}
	}
	/**
	 * 删除公告
	 * @param packet
	 * @param session
	 */
	@OP(code = HOpCodeEx.NOTICE_DEL_CLIENT)
	public void playerDelNotice(Packet packet, HSession session) {
		try {
			PlayerEx player = (PlayerEx) session.client();
			//发送人没有登陆
			if (player == null) {
				ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_1, packet.getopcode());
				return;
			}
			
			JSONArray ja = JSONArray.fromObject(packet.getString("noticeIds"));
			//删除邮件
			for (int i = 0; i < ja.size(); i++) {
				int noticeId = ja.getInt(i);
				player.setNoticeReaded(noticeId);
			}
			Packet pt = new JSONPacket(HOpCodeEx.NOTICE_DEL_SERVER);
			pt.put("result", 1);
			pt.put("noticeIds", ja);
			session.send(pt);
		} catch (Throwable e) {
			log.error("playerDelNotice error",e);
			ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_0, packet.getopcode());
		}
	}
	/**
	 * 获取未收到的系统公告
	 * @param player
	 * @return
	 * @throws Exception 
	 */
	public List<Notice> getUnReceiveSystemNotice(Player player) throws Exception {
		List<Notice> unReceiveNoticeList = new ArrayList<Notice>();
		int[] hadNotices = player.getReadedNoticeIds();
		TIntObjectMap<Notice>[] noticeList = loadSystemNoticeList();
		Notice notice = null;
		int id = 0;
		boolean isHave;
		int[] s = noticeList[0].keySet().toArray();
		for(int i = 0; i < noticeList[0].size(); i++){
			isHave = false;
			notice = noticeList[0].get(s[i]);
			for(int j = 0; j < hadNotices.length; j++){
				id = hadNotices[j];
				if(id == notice.getNoticeId()){
					isHave = true;
					break;
				}
			}
			if(!isHave){
				unReceiveNoticeList.add(notice);
			}
		}
		return unReceiveNoticeList;
	}
	/**
	 * loadSystemNoticeList: 获取所有系统公告
	 * @throws Exception 
	 * @return TIntObjectMap<Notice>[] 未取到返回NULL值
	*/
	public TIntObjectMap<Notice>[] loadSystemNoticeList() throws Exception{
		Data data = Platform.dataCenter().getData(DataKeysEx.systemNotices());
		if(data == null){
			//从数据库中读取
			List<Notice> noticeList = Platform.getEntityManager().query("from Notice order by addTime desc");
			TIntObjectMap<Notice>[] systemNotice = new TIntObjectMap[1];
			systemNotice[0] = new TIntObjectHashMap<Notice>();
			for(int i = 0;i< noticeList.size();i++){
				systemNotice[0].put(noticeList.get(i).getNoticeId(), noticeList.get(i));
			}
			if(systemNotice != null){
				Platform.dataCenter().sendNewData(DataKeysEx.systemNotices(), systemNotice);
			}
			return systemNotice;
		}
		
		return (TIntObjectMap<Notice>[])data.value;
	}
	/**
	 * 设置所有交互公告已读
	 */
	public void setInteractiveNoticeRead(PlayerEx player){
		int s[] = interactiveNotice[0].keySet().toArray();
		for(int i = 0; i < s.length; i++){
			player.setNoticeReaded(s[i]);
		}
	}
}
