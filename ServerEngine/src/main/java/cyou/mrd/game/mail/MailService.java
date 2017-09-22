package cyou.mrd.game.mail;

import java.util.Date;
import java.util.List;

import cyou.mrd.service.Service;

public interface MailService<T extends Mail> extends Service{
	
	public void deleteMail(T mail) throws MailException;
	
	public void sendMail(T mail) throws MailException;
	
	public void updateMail(T mail) throws MailException;
	
	public List<T> list(int playerId,int begin,int count,Date validTime);
	
	public void sendSystemMail(int destId,int mailTemplateId,int language, String sourceName, String destName, int useType);
	
	public void sendNpcMail(int destId,int mailTemplateId,int language, String sourceName, String destName, int useType);
	
	public void sendSystemMailNoFilter(int sourceId,int destId, int mailTemplateId, int language, String sourceName, String destName, int useType);
	
	public String getMailContent(int language,String sourceName,String destName ,int mailTemplateId);
	
	/**
	 * 发送系统公告
	 * content[13] = [汉语	英语		外语2	外语3	外语4	外语5	外语6	外语7	外语8	外语9	外语10	外语11	外语12	]
	 */
	public boolean sendSystemNoticeMail(String[] content);
	/**
	 * 获取当前邮件系统支持语言
	 * 约定的语言顺序为：
	 * int 13[汉语	英语		外语2	外语3	外语4	外语5	外语6	外语7	外语8	外语9	外语10	外语11	外语12	]
	 * 0为不支持，1为支持
	 */
	public int[] getServerLangInfo();

	public boolean delSystemNoticeMail(String name);
}

