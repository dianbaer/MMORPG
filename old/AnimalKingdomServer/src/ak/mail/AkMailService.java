package ak.mail;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ak.gameAward.GameAwardDAO;
import ak.gameAward.GameAwardTemplate;
import ak.notice.AkNoticeService;
import ak.optLog.IUserOptLogService;
import ak.optLog.UserOptLog;
import ak.player.PlayerEx;
import ak.player.PlayerServiceEx;
import ak.playerSns.PlayerSnsService;
import ak.server.ErrorHandlerEx;
import cyou.mrd.ObjectAccessor;
import cyou.mrd.Platform;
import cyou.mrd.entity.Player;
import cyou.mrd.event.Event;
import cyou.mrd.event.GameEvent;
import cyou.mrd.event.OPEvent;
import cyou.mrd.game.actor.Actor;
import cyou.mrd.game.actor.ActorCacheService;
import cyou.mrd.game.mail.Mail;
import cyou.mrd.game.mail.MailException;
import cyou.mrd.io.OP;
import cyou.mrd.io.OPHandler;
import cyou.mrd.io.Packet;
import cyou.mrd.io.http.HSession;
import cyou.mrd.io.http.JSONPacket;
import cyou.mrd.io.tcp.HOpCodeEx;
import cyou.mrd.service.HarmoniousService;
import cyou.mrd.service.PlayerService;
import cyou.mrd.util.DefaultThreadPool;
/**
 *  兔村的邮件系统
 * @author xuepeng
 *
 */
@OPHandler(TYPE = OPHandler.HTTP_EVENT)
public class AkMailService implements IAkMailService<MailEx> {

	private static final Logger log = LoggerFactory.getLogger(AkMailService.class);
	
	
	protected BlockingQueue<Mail> pendingMails = null;
	
	private final long MAIL_VALID_TIME = 30 * 24 * 60 * 60 * 1000L;// 过期时间
	
	public static final String PROPERTY_MAILLNUM_DAY = "property_mailnum_day";//每天发送的邮件数量属性名
	
	public static int PLAYER_MAXMAILNUM_DAY = Platform.getConfiguration().getInt("player_maxmailnum_day");//玩家每日可发邮件数
	
	
	/**
	 * 获取邮件列表type字段的值（收件箱）
	 */
	public static final int INBOX = 1;
	/**
	 * 获取邮件列表type字段的值（发件箱）
	 */
	public static final int OUTBOX = 3;
	/**
	 * 获取邮件列表type字段的值（信息箱）
	 */
	public static final int MESSAGEBOX = 2;
	
	
	/**
	 * 客户端邮件列表最大邮件数
	 */
	public static final int CLIENT_MAIL_LIST_MAXSIZE = Platform.getConfiguration().getInt("client_mail_list_maxsize");
	
	@Override
	public void deleteMail(Mail mail) throws MailException {
		// TODO Auto-generated method stub

	}
	/**
	 * 系统自动发送邮件，记录到player属性里，等发来心跳的时候通知给客户端
	 */
	@Override
	public void sendMail(Mail mail) throws MailException {
		//如果玩家消息过滤一下
		if (mail.getUseType() == MailEx.USERTYPE_FRIEND_MESSAGE) {
			String content = Platform.getAppContext().get(HarmoniousService.class).filterBadWords(mail.getContent());
			mail.setContent(content);
		}
		Platform.getEntityManager().createSync((MailEx)mail);
		
		//发送交互公告
		if(mail.getUseType() == MailEx.USERTYPE_FRIEND_MESSAGE){
			PlayerEx friend = (PlayerEx) ObjectAccessor.getPlayer(mail.getDestId());
			//如果不在这个服上，暂时先不做通知了,防止数据出现问题
			if(friend != null){
				PlayerEx[] playerArray = new PlayerEx[1];
				playerArray[0] = friend;
				AkNoticeService akNoticeService = Platform.getAppContext().get(AkNoticeService.class);
				akNoticeService.sendInteractiveNotice(AkNoticeService.INTERACTIVE_3, playerArray);
			}else{
				log.info("AkMailService.sendMail:error[player:{} is not in this server]",mail.getDestId());
			}
		}
		
		//通知收到信邮件，等发心跳的时候会告诉客户端有新的消息
		//如果不在这个服上，暂时先不做通知了,防止数据出现问题
		if (ObjectAccessor.players.get(mail.getDestId()) != null) {
			Player p = ObjectAccessor.getPlayer(mail.getDestId());
			//信息箱
			if(mail.getUseType() == MailEx.USERTYPE_SYSTEM_MESSAGE){
				p.getPool().setInt(PlayerEx.NOTIFY_CLIENT_MAIL_MESSAGEBOX, PlayerEx.NOTIFY_CLIENT);
			//收件箱
			}else{
				p.getPool().setInt(PlayerEx.NOTIFY_CLIENT_MAIL_INBOX, PlayerEx.NOTIFY_CLIENT);
			}
			p.notifySave();
		}else{
			
		}
	}

	@Override
	public void updateMail(Mail mail) throws MailException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Mail> list(int playerId, int begin, int count, Date validTime) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendSystemMail(int destId, int mailTemplateId, int language,
			String sourceName, String destName, int useType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendNpcMail(int destId, int mailTemplateId, int language,
			String sourceName, String destName, int useType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendSystemMailNoFilter(int sourceId, int destId,
			int mailTemplateId, int language, String sourceName,
			String destName, int useType) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getMailContent(int language, String sourceName,
			String destName, int mailTemplateId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean sendSystemNoticeMail(String[] content) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int[] getServerLangInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean delSystemNoticeMail(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getId() {
		return "AkMailService";
	}

	@Override
	public void startup() throws Exception {
		pendingMails = new LinkedBlockingQueue<Mail>();
		//开启一个自动发送邮件的线程
		Thread daemonMail = new Thread(new DaemonSendMail(), "Daemon-Send-Mail");
		daemonMail.setPriority(DefaultThreadPool.DEFAULT_THREAD_PRIORITY);
		daemonMail.start();
	} 
	/**
	 * 自动发送邮件的线程
	 * @author xuepeng
	 *
	 */
	class DaemonSendMail implements Runnable {
		public void run() {
			while (true) {
				try {
					Mail mail = pendingMails.take();
					sendMail(mail);
				} catch (Exception e) {
					log.error(e.toString(), e);
				}
			}
		}
	}
	@Override
	public void shutdown() throws Exception {
		// TODO Auto-generated method stub

	}
	/**
	 * 系统发送添加好友
	 */
	@Override
	public void sendSystemMailAddFriend(int sourceId, int destId,
			String sourceName, int mailTemplateId,
			String sourceIcon, int sourceLevel,
			int rich, int raceId,String param) {
		MailEx mail = new MailEx();
		mail.setSourceId(sourceId);
		mail.setDestId(destId);
		mail.setSourceName(sourceName == null ? "null" : sourceName);
		mail.setPostTime(new Date());
		mail.setExpirationTime(new Date(System.currentTimeMillis() + MAIL_VALID_TIME));
		//MailTemplateStatus值或者奖励表的值
		mail.setContent("null");
		mail.setParam(param == null ? "null" : param);
		mail.setStatus(MailEx.STATUS_0);
		mail.setType(MailEx.TYPE_SYSTEM);
		mail.setUseType(MailEx.USERTYPE_ADD_FRIEND);
		mail.setSourceIcon(sourceIcon == null ? "1" : sourceIcon);
		mail.setSourceLevel(sourceLevel);
		mail.setExist(MailEx.EXIST);
		mail.setRich(rich == 0 ? 1 : rich);
		mail.setRaceId(raceId == 0 ? 1 : raceId);
		mail.setDownload(MailEx.DOWNLOAD_0);
		mail.setTemplateId(mailTemplateId);
		pendingMails.add(mail);
		log.info("AkMailService sendSystemMailAddFriend success");

	}
	/**
	 * 发送系统邮件，带物品的
	 */
	public void sendSystemMailHaveGoods(int destId, String param,
			String content) {
		MailEx mail = new MailEx();
		mail.setSourceId(-1);
		mail.setDestId(destId);
		mail.setSourceName("system");
		mail.setPostTime(new Date());
		mail.setExpirationTime(new Date(System.currentTimeMillis() + MAIL_VALID_TIME));
		//MailTemplateStatus值或者奖励表的值
		mail.setContent(content);
		mail.setParam(param);
		mail.setStatus(MailEx.STATUS_0);
		mail.setType(MailEx.TYPE_SYSTEM);
		mail.setUseType(MailEx.USERTYPE_GOODS);
		mail.setSourceIcon("0");
		mail.setSourceLevel(0);
		mail.setExist(MailEx.EXIST);
		mail.setRich(0);
		mail.setRaceId(0);
		mail.setDownload(MailEx.DOWNLOAD_0);
		mail.setTemplateId(0);
		pendingMails.add(mail);
		log.info("AkMailService sendSystemMailHaveGoods success");
	}
	/**
	 * 发送系统邮件，带奖励的
	 */
	public void sendSystemMailHaveAward(int destId, int awardId,
			String content,int mailTemplateId) {
		MailEx mail = new MailEx();
		mail.setSourceId(-1);
		mail.setDestId(destId);
		mail.setSourceName("system");
		mail.setPostTime(new Date());
		mail.setExpirationTime(new Date(System.currentTimeMillis() + MAIL_VALID_TIME));
		//MailTemplateStatus值或者奖励表的值
		mail.setContent(content);
		mail.setStatus(MailEx.STATUS_0);
		mail.setType(MailEx.TYPE_SYSTEM);
		mail.setUseType(MailEx.USERTYPE_AWARD);
		mail.setSourceIcon("0");
		mail.setSourceLevel(0);
		mail.setExist(MailEx.EXIST);
		mail.setRich(0);
		mail.setRaceId(0);
		mail.setDownload(MailEx.DOWNLOAD_0);
		mail.setTemplateId(mailTemplateId);
		mail.setAwardId(awardId);
		pendingMails.add(mail);
		log.info("AkMailService sendSystemMailHaveAward success");
	}
	public void sendSystemMailHaveAwardUser(int sourceId, int destId,
			String sourceName, int mailTemplateId,
			String sourceIcon, int sourceLevel,
			int rich, int raceId,String param,int awardId){
		MailEx mail = new MailEx();
		mail.setSourceId(sourceId);
		mail.setDestId(destId);
		mail.setSourceName(sourceName == null ? "null" : sourceName);
		mail.setPostTime(new Date());
		mail.setExpirationTime(new Date(System.currentTimeMillis() + MAIL_VALID_TIME));
		//MailTemplateStatus值或者奖励表的值
		mail.setContent("null");
		mail.setParam(param == null ? "null" : param);
		mail.setStatus(MailEx.STATUS_0);
		mail.setType(MailEx.TYPE_SYSTEM);
		mail.setUseType(MailEx.USERTYPE_AWARD);
		mail.setSourceIcon(sourceIcon == null ? "1" : sourceIcon);
		mail.setSourceLevel(sourceLevel);
		mail.setExist(MailEx.EXIST);
		mail.setRich(rich == 0 ? 1 : rich);
		mail.setRaceId(raceId == 0 ? 1 : raceId);
		mail.setDownload(MailEx.DOWNLOAD_0);
		mail.setTemplateId(mailTemplateId);
		mail.setAwardId(awardId);
		pendingMails.add(mail);
	}
	@OP(code = 2303)
	public void testGmSendSystemNoticeToAllOnline(Packet packet, HSession session) {
		
		String content = packet.getString("content");
		int awardId = packet.getInt("awardId");
		//int mailTemplateId = packet.getInt("mailTemplateId");
		for (Player player : ObjectAccessor.players.values()) {
			sendSystemMailHaveAward(player.getId(), awardId, content, 0);
		}
		
		
	}
	public void SendSystemNoticeToAll(String content,int awardId,int mailTemplateId) {
		for (Player player : ObjectAccessor.players.values()) {
			sendSystemMailHaveAward(player.getId(), awardId, content, mailTemplateId);
		}
		
		
	}
	@OP(code = 2304)
	public void testGmSendSystemNotice(Packet packet, HSession session) {
		try {
			String content = packet.getString("content");
			int awardId = packet.getInt("awardId");
			//int mailTemplateId = packet.getInt("mailTemplateId");
			int playerId = packet.getInt("playerId");
			sendSystemMailHaveAward(playerId, awardId, content, 0);
		} catch (Throwable e) {
			log.error("testGmSendNotice error",e);
		}
		
	}
	@OP(code = 2305)
	public void testGmSendSystemGoodsToAllOnline(Packet packet, HSession session) {
		
		String content = packet.getString("content");
		String param = packet.getString("param");
		for (Player player : ObjectAccessor.players.values()) {
			sendSystemMailHaveGoods(player.getId(), param, content);
		}
		
		
	}
	@OP(code = 2306)
	public void testGmSendSystemGoods(Packet packet, HSession session) {
		try {
			String content = packet.getString("content");
			JSONObject param = (JSONObject)packet.getObject("param");
			int playerId = packet.getInt("playerId");
			sendSystemMailHaveGoods(playerId, param.toString(), content);
		} catch (Throwable e) {
			log.error("testGmSendNotice error",e);
		}
		
	}
	/**
	 * 发送系统邮件，不带附件的，不可交互只供查看的
	 */
	@Override
	public void sendSystemMailUnInteractive(int sourceId, int destId,
			String sourceName, int mailTemplateId,
			String sourceIcon, int sourceLevel,
			int rich, int raceId, String param) {
		MailEx mail = new MailEx();
		mail.setSourceId(sourceId);
		mail.setDestId(destId);
		mail.setSourceName(sourceName == null ? "null" : sourceName);
		mail.setPostTime(new Date());
		mail.setExpirationTime(new Date(System.currentTimeMillis() + MAIL_VALID_TIME));
		//MailTemplateStatus值或者奖励表的值
		mail.setContent("null");
		mail.setParam(param == null ? "null" : param);
		mail.setStatus(MailEx.STATUS_0);
		mail.setType(MailEx.TYPE_SYSTEM);
		mail.setUseType(MailEx.USERTYPE_SYSTEM_MESSAGE);
		mail.setSourceIcon(sourceIcon == null ? "1" : sourceIcon);
		mail.setSourceLevel(sourceLevel);
		mail.setExist(MailEx.EXIST);
		mail.setRich(rich == 0 ? 1 : rich);
		mail.setRaceId(raceId == 0 ? 1 : raceId);
		mail.setDownload(MailEx.DOWNLOAD_0);
		mail.setTemplateId(mailTemplateId);
		pendingMails.add(mail);
		log.info("AkMailService sendSystemMailUnInteractive success");
	}
	/**
	 * 接受玩家发送的普通信件
	 * @param packet
	 * @param session
	 */
	@OP(code = HOpCodeEx.MAIL_SEND_CLIENT)
	public void playerSendMail(Packet packet, HSession session){
		try {
			PlayerEx player = (PlayerEx) session.client();
			//发送人没有登陆
			if (player == null) {
				ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_1, packet.getopcode());
				return;
			}
			
			int num = player.getPool().getInt(PROPERTY_MAILLNUM_DAY, 0);
			//不能超过每日发送最大邮件数量
			if (num >= PLAYER_MAXMAILNUM_DAY) {
				ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_44, packet.getopcode());
				return;
			}
			
			int destId = packet.getInt("destId");
			String content = packet.getString("content");
			Actor actor = Platform.getAppContext().get(ActorCacheService.class).findActor(destId);
			//被发送人不存在
			if (actor == null) {
				ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_5, packet.getopcode());
				return;
			}
			
			MailEx mail = new MailEx();
			mail.setSourceId(player.getId());
			mail.setDestId(actor.getId());
			mail.setSourceName(player.getName() == null ? "null": player.getName());
			mail.setPostTime(new Date());
			mail.setExpirationTime(new Date(System.currentTimeMillis() + MAIL_VALID_TIME));
			//MailTemplateStatus值或者奖励表的值
			mail.setContent(content == null ? "null" : content);
			mail.setStatus(MailEx.STATUS_0);
			mail.setType(MailEx.TYPE_PLAYER);
			mail.setUseType(MailEx.USERTYPE_FRIEND_MESSAGE);
			mail.setSourceIcon(player.getIcon() == null ? "1" : player.getIcon());
			mail.setSourceLevel(player.getLevel());
			mail.setExist(MailEx.EXIST);
			mail.setRich(player.getRich() == 0 ? 1 : player.getRich());
			mail.setRaceId(player.getRaceId() == 0 ? 1 : player.getRaceId());
			mail.setDownload(MailEx.DOWNLOAD_0);
			pendingMails.add(mail);
			
			player.getPool().setInt(PROPERTY_MAILLNUM_DAY, ++num);
			
			//增加爱心值
			PlayerSnsService playerSnsService = Platform.getAppContext().get(PlayerSnsService.class);
			playerSnsService.addLove(GameAwardTemplate.ID_SEND_MAIL_REWARD_LOVE, player, destId);
			player.notifySave();
			Packet pt = new JSONPacket(HOpCodeEx.MAIL_SEND_SERVER);
			pt.put("result", 1);
			session.send(pt);
			PlayerServiceEx playerService = (PlayerServiceEx) Platform.getAppContext().get(PlayerService.class);
			playerService.addLove(session,player);
		} catch (Throwable e) {
			log.error("playerSendMail error",e);
			ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_0, packet.getopcode());
		}
	}
	/**
	 * 查询邮件列表
	 * @param packet
	 * @param session
	 */
	@OP(code = HOpCodeEx.MAIL_LIST_CLIENT)
	public void playerListMail(Packet packet, HSession session) {
		try {
			PlayerEx player = (PlayerEx) session.client();
			//发送人没有登陆
			if (player == null) {
				ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_1, packet.getopcode());
				return;
			}
			
			int type = packet.getInt("type");
			
			//已经下载到客户端，没有删除的邮件个数
			//int count = AkMailDAO.getPlayerMailDownloadCount(player);
			int selectCount = CLIENT_MAIL_LIST_MAXSIZE;//固定每次最多100条新邮件
			List<MailEx> mailList = null;
			//收件箱
			if(type == INBOX && selectCount > 0){
				mailList = AkMailDAO.getPlayerMailListUnDownloadInbox(player,selectCount);
			//信息箱
			}else if(type == MESSAGEBOX && selectCount > 0){
				mailList = AkMailDAO.getPlayerMailListUnDownloadMessagebox(player,selectCount);
			//type类型不正确（不查询）
			}else{
				
			}
			
			Packet pt = new JSONPacket(HOpCodeEx.MAIL_LIST_SERVER);
			
			JSONArray ja = new JSONArray();
			if (mailList != null && mailList.size() > 0) {
				for (MailEx mail : mailList) {
					JSONObject jo = new JSONObject();
					jo.put("id", mail.getId());
					jo.put("useType", mail.getUseType());
					jo.put("sourceId", mail.getSourceId());
					jo.put("sourceName", mail.getSourceName());
					jo.put("content", mail.getContent());
					jo.put("param", mail.getParam());
					jo.put("sourceIcon", mail.getSourceIcon());
					jo.put("sourceLevel", mail.getSourceLevel());
					jo.put("time", mail.getPostTime().getTime() / 1000);//转成秒数
					jo.put("status", mail.getStatus());
					jo.put("rich", mail.getRich());
					jo.put("raceId", mail.getRaceId());
					jo.put("download", mail.getDownload());
					jo.put("templateId", mail.getTemplateId());
					jo.put("awardId", mail.getAwardId());
					ja.add(jo);
				}
				pt.put("mailList", ja.toString());
			}else{
				pt.put("mailList", "[]");
			}
			log.info("邮件列表：长度"+ja.size()+":"+ja.toString());
			pt.put("type", type);
			session.send(pt);
			//更新邮件为未确定客户端是否下载成功
			if (mailList != null && mailList.size() > 0) {
				for (MailEx mail : mailList) {
					if(mail.getDownload() == MailEx.DOWNLOAD_0){
						mail.setDownload(MailEx.DOWNLOAD_2);
						Platform.getEntityManager().updateSync(mail);
					}
					
				}
			}
		} catch (Throwable e) {
			log.error("playerListMail error",e);
			ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_0, packet.getopcode());
		}
	}
	/**
	 * 删除邮件
	 * @param packet
	 * @param session
	 */
	@OP(code = HOpCodeEx.MAIL_DEL_CLIENT)
	public void playerDelMail(Packet packet, HSession session) {
		try {
			PlayerEx player = (PlayerEx) session.client();
			//发送人没有登陆
			if (player == null) {
				ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_1, packet.getopcode());
				return;
			}
			
			JSONArray ja = JSONArray.fromObject(packet.getString("mailIds"));
			//删除邮件
			for (int i = 0; i < ja.size(); i++) {
				int mailId = ja.getInt(i);
				AkMailDAO.deleteMailEx(player, mailId);
			}
			Packet pt = new JSONPacket(HOpCodeEx.MAIL_DEL_SERVER);
			pt.put("result", 1);
			pt.put("mailIds", ja);
			session.send(pt);
		} catch (Throwable e) {
			log.error("playerDelMail error",e);
			ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_0, packet.getopcode());
		}
	}
	/**
	 * 回复邮件列表到达客户端本地
	 * @param packet
	 * @param session
	 */
	@OP(code = HOpCodeEx.MAIL_REPLY_LIST_CLIENT)
	public void playerReplyMailList(Packet packet, HSession session){
		try {
			PlayerEx player = (PlayerEx) session.client();
			//发送人没有登陆
			if (player == null) {
				ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_1, packet.getopcode());
				return;
			}
			JSONArray ja = JSONArray.fromObject(packet.getString("mailIds"));
			//更新download 用户下载状态字段
			for (int i = 0; i < ja.size(); i++) {
				int mailId = ja.getJSONObject(i).getInt("ID");
				AkMailDAO.updateMailDownload(player, mailId);
			}
			log.info("返回邮件列表：长度"+ja.size()+":"+ja.toString());
			Packet pt = new JSONPacket(HOpCodeEx.MAIL_REPLY_LIST_SERVER);
			pt.put("result", 1);
			session.send(pt);
		} catch (Throwable e) {
			log.error("playerReplyMailList error",e);
			ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_0, packet.getopcode());
		}
	}
	/**
	 * 获取邮件的附件奖励
	 * @param packet
	 * @param session
	 */
	@OP(code = HOpCodeEx.MAIL_GET_GOODS_CLIENT)
	public void playerGetGoodsByMail(Packet packet, HSession session){
		try {
			PlayerEx player = (PlayerEx) session.client();
			//发送人没有登陆
			if (player == null) {
				ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_1, packet.getopcode());
				return;
			}
			int mailId = packet.getInt("mailId");
			MailEx mail = AkMailDAO.getMailByIdEx(mailId);
			log.info("获取奖励："+mail.toString());
			//不存在此邮件 或者 邮件已经删除 或者 该角色不是此邮件的所有人
			if(mail == null || mail.getExist() == MailEx.UNEXIST || mail.getDestId() != player.getId()){
				ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_64, packet.getopcode());
				return;
			}
			//该邮件没有奖励附件
			if(mail.getUseType() != MailEx.USERTYPE_AWARD){
				ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_65, packet.getopcode());
				return;
			}
			//已经领取奖励，无法重复领取
			if(mail.getStatus() == MailEx.STATUS_2){
				ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_63, packet.getopcode());
				return;
			}
			//根据templateId查询奖励表
			GameAwardTemplate gameAwardTemplate = GameAwardDAO.getGameAward(mail.getAwardId());
			if(gameAwardTemplate == null){
				ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_65, packet.getopcode());
				return;
			}
			if(gameAwardTemplate.getLove() > 0){
				player.setLove(player.getLove()+gameAwardTemplate.getLove());
			}
			//更新为领取完奖励
			mail.setStatus(MailEx.STATUS_2);
			Platform.getEntityManager().updateSync(mail);
			//增加日志用来记录
			IUserOptLogService userOptLogService = Platform.getAppContext().get(IUserOptLogService.class);
			userOptLogService.addUserOptLog(session, player.getId(), UserOptLog.TYPE_SYSTEM_AWARD, mail.getId(),UserOptLog.CONTENT_1);
			
			//发送领取成功
			Packet pt = new JSONPacket(HOpCodeEx.MAIL_GET_GOODS_SERVER);
			pt.put("result", 1);
			pt.put("mailId", mailId);
			pt.put("gameAwardId", gameAwardTemplate.getId());
			log.info(pt.toString());
			session.send(pt);
			log.info(pt.toString());
			PlayerServiceEx playerService = (PlayerServiceEx) Platform.getAppContext().get(PlayerService.class);
			playerService.addLove(session,player);
		} catch (Throwable e) {
			log.error("playerGetGoodsByMail error",e);
			ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_0, packet.getopcode());
		}
	}

	/**
	 * 玩家登录，如果跟上次登录不是同一天，则重置每天发送的邮件个数
	 * @param event
	 */
	@OPEvent(eventCode = GameEvent.EVENT_PLAYER_LOGINED)
	public void resetPlayerMailNum(Event event) {
		Player player = (Player) event.param1;
		if (player.getPool().getInt(PROPERTY_MAILLNUM_DAY, 0) != 0) {
			Calendar ca1 = Calendar.getInstance();
			Calendar ca2 = Calendar.getInstance();
			if (player.getLastLogoutTime() == null) {
				player.setLastLogoutTime(new Date());
			}
			ca2.setTime(player.getLastLogoutTime());
			if (ca1.get(Calendar.DAY_OF_YEAR) != ca2.get(Calendar.DAY_OF_YEAR)) {
				player.getPool().setInt(PROPERTY_MAILLNUM_DAY, 0);
				player.notifySave();
			}
		}
	}
	/**
	 * 如果换天了，重置所有用户的每日发送邮件个数
	 * @param event
	 */
	@OPEvent(eventCode = GameEvent.EVENT_CHANGE_DAY)
	public void resetAllOnlinePlayerMailNum(Event event){
		for (Player player : ObjectAccessor.players.values()) {
			player.getPool().setInt(PROPERTY_MAILLNUM_DAY, 0);
			player.notifySave();
		}
	}
	/**
	 * 获取未下载的收件箱信息1条
	 * @param player
	 * @return
	 */
	public List<MailEx> getPlayerMailListUnDownloadInbox(PlayerEx player){
		List<MailEx> mailList = null;
		mailList = AkMailDAO.getPlayerMailListUnDownloadInbox(player,1);
		return mailList;
	}
	/**
	 * 获取未下载的信息箱信息1条
	 * @param player
	 * @return
	 */
	public List<MailEx> getPlayerMailListUnDownloadMessagebox(PlayerEx player){
		List<MailEx> mailList = null;
		mailList = AkMailDAO.getPlayerMailListUnDownloadMessagebox(player,1);
		return mailList;
	}
}
