package cyou.mrd.game.mail;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.Platform;
import cyou.mrd.entity.Player;

public class MailDAO {
	

	public static final Logger log = LoggerFactory.getLogger(MailDAO.class);
	
	/**
	 * 获取指定页的邮件列表
	 */
	public static List<Mail> getPlayerMail(Player player,int pageCount,int num){
		List<Mail> mails = null;
		mails = Platform.getEntityManager().limitQuery( 
				"from Mail where destId = ? and exist = 0  order by postTime desc"
				, pageCount * num, pageCount, player.getId());
		log.info("[MailDAO] [getPlayerMail] playerId:{}, mailSize:{}",player.getId(),mails==null?0:mails.size());
		return mails;
		
	}
	
	public static List<Mail> getPlayerMailNotRead(Player player){
		List<Mail> mails = null;
		mails = Platform.getEntityManager().query( 
				"from Mail where destId = ? and status = ? and exist = 0  order by postTime desc"
				,player.getId(),DefaultMailService.MAIL_STATE_NOTREAD);
		log.info("[MailDAO] [getPlayerMailNotRead] playerId:{}, mailSize:{}",player.getId(),mails==null?0:mails.size());
		return mails;
	}
	
	public static List<Mail> getPlayerMailByType(Player player,int type){
		List<Mail> mails = null;
		mails = Platform.getEntityManager().limitQuery( 
				"from Mail where destId = ? and type = ? and exist = 0 order by postTime desc"
				,0,DefaultMailService.MAIL_LIST_MAXSIZE,player.getId(),type);
		log.info("[MailDAO] [getPlayerMailByType]  playerId:{}, mailSize:{}",player.getId(),mails==null?0:mails.size());
		return mails;
	}
	
	/**
	 * 获取邮件总数 
	 */
	public static int getPlayerMailCount(Player player){
		int num = (int)Platform.getEntityManager().count("select count(*) from Mail where destId = ? and exist = 0 ", player.getId());
		log.info("[MailDAO] [getPlayerMailCount] playerId:{}, count:{}",player.getId(),num);
		return num;
	}
	
	/**
	 * 取得制定id号的邮件
	 */
	public static Mail getMailById(int mailId){
		Mail mail = Platform.getEntityManager().find(Mail.class, mailId);
		log.info("[MailDAO] [getMailById] id:{}",mail==null?"null":mail.getId());
		return mail;
	}
	
	/**
	 * 删除邮件  将exist设为1
	 */
	public static void deleteMail(int mailId){
		Mail mail = getMailById(mailId);
		if(mail!=null && mail.getExist() == 0){
			mail.setExist(1);
			Platform.getEntityManager().updateSync(mail);
			log.info("[MailDAO] [deleteMail] mailId:{}",mailId);
		}
	}
	
}
