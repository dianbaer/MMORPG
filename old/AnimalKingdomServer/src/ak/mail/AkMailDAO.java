package ak.mail;

import java.util.List;

import ak.player.PlayerEx;
import cyou.mrd.Platform;
import cyou.mrd.game.mail.MailDAO;
/**
 * 邮件系统dao扩展
 * @author xuepeng
 *
 */
public class AkMailDAO extends MailDAO {
	/**
	 * 获取用户下载到客户端的邮件数量
	 * @param player
	 * @return
	 */
	public static int getPlayerMailDownloadCount(PlayerEx player){
		int mCount = (int) Platform.getEntityManager().count("select count(*) from MailEx where destId = ? and exist = ? and download = ?", player.getId(),MailEx.EXIST,MailEx.DOWNLOAD_1);
		return mCount;
	}
	/**
	 * 根据传来的个数
	 * 获取玩家未下载到客户端的邮件列表（包括不确定是否下载到客户端的）(收件箱)
	 * @param player
	 * @param selectCount
	 * @return
	 */
	public static List<MailEx> getPlayerMailListUnDownloadInbox(PlayerEx player,int selectCount){
		List<MailEx> mailList = Platform.getEntityManager().limitQuery( 
				"from MailEx where destId = ? and exist = ? and download in(?,?) and useType in(?,?,?,?) order by postTime desc"
				,0,selectCount,player.getId(),MailEx.EXIST,MailEx.DOWNLOAD_0,MailEx.DOWNLOAD_2,MailEx.USERTYPE_FRIEND_MESSAGE,MailEx.USERTYPE_ADD_FRIEND,MailEx.USERTYPE_AWARD,MailEx.USERTYPE_GOODS);
		return mailList;
	}
	/**
	 * 根据传来的个数
	 * 获取玩家未下载到客户端的邮件列表（包括不确定是否下载到客户端的）(消息箱)
	 * @param player
	 * @param selectCount
	 * @return
	 */
	public static List<MailEx> getPlayerMailListUnDownloadMessagebox(PlayerEx player,int selectCount){
		List<MailEx> mailList = Platform.getEntityManager().limitQuery( 
				"from MailEx where destId = ? and exist = ? and download in(?,?) and useType = ? order by postTime desc"
				,0,selectCount,player.getId(),MailEx.EXIST,MailEx.DOWNLOAD_0,MailEx.DOWNLOAD_2,MailEx.USERTYPE_SYSTEM_MESSAGE);
		return mailList;
	}
	/**
	 * 根据id获取邮件
	 * @param mailId
	 * @return
	 */
	public static MailEx getMailByIdEx(int mailId){
		MailEx mail = Platform.getEntityManager().find(MailEx.class, mailId);
		
		return mail;
	}
	/**
	 * 删除邮件
	 * @param player
	 * @param mailId
	 */
	public static void deleteMailEx(PlayerEx player,int mailId){
		MailEx mail = getMailByIdEx(mailId);
		//邮件是这个用户的并且没有删除
		if(mail!=null && mail.getExist() == MailEx.EXIST && mail.getDestId() == player.getId()){
			mail.setExist(MailEx.UNEXIST);
			Platform.getEntityManager().updateSync(mail);
			
		}
	}
	/**
	 * 更新邮件邮件是否下载到本地的状态的字段
	 * @param player
	 * @param mailId
	 */
	public static void updateMailDownload(PlayerEx player,int mailId){
		MailEx mail = getMailByIdEx(mailId);
		//邮件是这个用户的并且download状态时2 未确定用户是否接到
		if(mail!=null && mail.getDownload() == MailEx.DOWNLOAD_2 && mail.getDestId() == player.getId()){
			mail.setDownload(MailEx.DOWNLOAD_1);
			Platform.getEntityManager().updateSync(mail);
			
		}
	}
}
