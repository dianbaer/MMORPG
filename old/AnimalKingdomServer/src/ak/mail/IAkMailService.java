package ak.mail;

import java.util.List;

import ak.player.PlayerEx;
import cyou.mrd.game.mail.Mail;
import cyou.mrd.game.mail.MailService;
import cyou.mrd.io.Packet;
import cyou.mrd.io.http.HSession;
/**
 * 邮件服务接口扩展
 * @author xuepeng
 *
 * @param <MailEx>
 */
public interface IAkMailService<MailEx extends Mail> extends MailService<Mail> {
	/**
	 * 添加好友后，系统发送信件
	 * @param sourceId
	 * @param destId
	 * @param sourceName
	 * @param mailTemplateId
	 * @param sourceIcon
	 * @param sourceLevel
	 * @param rich
	 * @param raceId
	 * @param param
	 */
	public void sendSystemMailAddFriend(int sourceId, int destId,
			String sourceName, int mailTemplateId,
			String sourceIcon, int sourceLevel,
			int rich, int raceId,String param);
	/**
	 * 发送系统邮件，带附件的
	 * @param destId
	 * @param awardId
	 * @param content
	 * @param mailTemplateId
	 */
	public void sendSystemMailHaveAward(int destId, int awardId, String content, int mailTemplateId);
	/**
	 * 玩家送的奖励
	 * @param sourceId
	 * @param destId
	 * @param sourceName
	 * @param mailTemplateId
	 * @param sourceIcon
	 * @param sourceLevel
	 * @param rich
	 * @param raceId
	 * @param param
	 * @param awardId
	 */
	public void sendSystemMailHaveAwardUser(int sourceId, int destId,
			String sourceName, int mailTemplateId,
			String sourceIcon, int sourceLevel,
			int rich, int raceId,String param,int awardId);
	public void sendSystemMailHaveGoods(int destId, String param,
			String content);
	/**
	 * 发送系统邮件，不带附件的，不可交互只供查看的
	 * @param sourceId
	 * @param destId
	 * @param sourceName
	 * @param mailTemplateId
	 * @param sourceIcon
	 * @param sourceLevel
	 * @param rich
	 * @param raceId
	 * @param param
	 */
	public void sendSystemMailUnInteractive(int sourceId, int destId,
			String sourceName, int mailTemplateId,
			String sourceIcon, int sourceLevel,
			int rich, int raceId, String param);
	/**
	 * 获取未下载的收件箱的信息一条
	 * @param player
	 * @return
	 */
	public List<MailEx> getPlayerMailListUnDownloadInbox(PlayerEx player);
	/**
	 * 获取未下载的信息箱的信息一条
	 * @param player
	 * @return
	 */
	public List<MailEx> getPlayerMailListUnDownloadMessagebox(PlayerEx player);
	public void SendSystemNoticeToAll(String content,int awardId,int mailTemplateId);
}
