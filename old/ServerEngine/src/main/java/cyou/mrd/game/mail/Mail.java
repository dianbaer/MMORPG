package cyou.mrd.game.mail;

import java.io.Serializable;
import java.util.Date;

import cyou.mrd.Platform;

public class Mail implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;

	private int sourceId;

	private int destId;

	private String sourceName;

	private String sourceIcon;
	
	private int sourceLevel;

	private Date postTime; // 发送时间

	private Date expirationTime; // 过期时间

	private String content;

	private int status;// 状态 0:未阅读 1:已阅读

	private int type;// 类型 0:玩家 1：系统 2:npc
	
	private int exist;//0:未删除       1:已删除

	/**
	 *  用途类型 0 = 好友消息; 1 = 好友申请消息; 2 = 其他;...
	 */
	private int useType;

	public Mail() {
	}

	public Mail(int sourceId, int destId, String sourceName, String sourceIcon,int sourceLevel, String content, int useType) {
		this.sourceId = sourceId;
		this.destId = destId;
		this.sourceName = sourceName;
		this.sourceIcon = sourceIcon;
		this.content = content;
		this.useType = useType;
		this.sourceLevel = sourceLevel;
		this.exist = 0;
	}
	
	public Mail(int sourceId, int destId, String destName, String sourceName, String sourceIcon,int sourceLevel ,int lang, int mailTemplateId, int useType) {
		this.sourceId = sourceId;
		this.destId = destId;
		this.sourceName = sourceName;
		this.sourceIcon = sourceIcon;
		MailService<?> service = Platform.getAppContext().get(MailService.class);
		this.content = service.getMailContent(lang, sourceName, destName, mailTemplateId);//获取邮件合适的内容
		this.useType = useType;
		this.sourceLevel = sourceLevel;
		this.exist = 0;
	}
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getStatus() {
		return status;
	}

	public int getUseType() {
		return useType;
	}

	public void setUseType(int useType) {
		this.useType = useType;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public int getSourceId() {
		return sourceId;
	}

	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	public int getDestId() {
		return destId;
	}

	public void setDestId(int destId) {
		this.destId = destId;
	}

	public Date getPostTime() {
		return postTime;
	}

	public void setPostTime(Date postTime) {
		this.postTime = postTime;
	}

	public Date getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(Date expirationTime) {
		this.expirationTime = expirationTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		content = content.replaceAll("\"", "“");
		content = content.replaceAll(":", " ");
		this.content = content;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public String getSourceIcon() {
		return sourceIcon;
	}

	public void setSourceIcon(String sourceIcon) {
		this.sourceIcon = sourceIcon;
	}

	public int getSourceLevel() {
		return sourceLevel;
	}

	public void setSourceLevel(int sourceLevel) {
		this.sourceLevel = sourceLevel;
	}

	public int getExist() {
		return exist;
	}

	public void setExist(int exist) {
		this.exist = exist;
	}
	
}
