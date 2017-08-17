package ak.mail;

import cyou.mrd.game.mail.Mail;
/**
 * 邮件的扩展，增加繁荣度和种族和是否下载到客户端的标识
 * @author xuepeng
 *
 */
public class MailEx extends Mail {
	/**
	 * userType属性的值--玩家消息(玩家)
	 */
	public static final int USERTYPE_FRIEND_MESSAGE = 0;
	/**
	 * userType属性的值--添加好友 (系统)
	 */
	public static final int USERTYPE_ADD_FRIEND = 1;
	/**
	 * userType属性的值--附件奖励(系统)
	 */
	public static final int USERTYPE_AWARD = 2;
	/**
	 * userType属性的值--系统提示(系统)
	 */
	public static final int USERTYPE_SYSTEM_MESSAGE = 3;
	/**
	 * userType属性的值--道具奖励(系统)
	 */
	public static final int USERTYPE_GOODS = 4;
	
	
	/**
	 * type字段的值（玩家）
	 */
	public static final int TYPE_PLAYER = 0;
	/**
	 * type字段的值（系统）
	 */
	public static final int TYPE_SYSTEM = 1;
	/**
	 * type字段的值（npc）
	 */
	public static final int TYPE_NPC = 2;
	
	
	/**
	 * 未下载到客户端(download属性的值)
	 */
	public static final int DOWNLOAD_0 = 0;
	/**
	 * 下载到客户端 (download属性的值)
	 */
	public static final int DOWNLOAD_1 = 1;
	/**
	 * 不确定，客户端没有返回(download属性的值)
	 */
	public static final int DOWNLOAD_2 = 2;
	
	/**
	 * 未删除（exist字段的值）
	 */
	public static final int EXIST = 0;
	/**
	 * 已删除（exist字段的值）
	 */
	public static final int UNEXIST = 1;
	
	/**
	 * 未读（status字段的值）
	 */
	public static final int STATUS_0 = 0;
	/**
	 * 已读（status字段的值）
	 */
	public static final int STATUS_1 = 1;
	/**
	 * 已领取附件奖励（status字段的值）
	 */
	public static final int STATUS_2 = 2;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 繁荣度
	 */
	private int rich;
	/**
	 * 种族
	 */
	private int raceId;
	/**
	 * 是否下载到客户端
	 */
	private int download;
	/**
	 * 模板id
	 */
	private int templateId;
	/**
	 * 参数
	 */
	private String param;
	/**
	 * 奖励id
	 */
	private int awardId;
	
	//以下标识系统消息模板ID，即对应的词典ID。
	
	/**
	 * 添加好友
	 */
	public static final int TEMPLATEID_3221 = 3221;
	/**
	 * 王宫升级
	 */
	public static final int TEMPLATEID_3222 = 3222;
	/**
	 * 摇钱树cd结束可以浇水
	 */
	public static final int TEMPLATEID_3223 = 3223;
	/**
	 * 市场更新
	 */
	public static final int TEMPLATEID_3224 = 3224;
	/**
	 * 送小动物
	 */
	public static final int TEMPLATEID_3225 = 3225;
	/**
	 * 帮助
	 */
	public static final int TEMPLATEID_3226 = 3226;
	/**
	 * 其他用户通过市场购买物品
	 */
	public static final int TEMPLATEID_3227 = 3227;
	
	public MailEx() {
		super();
	}
	public MailEx(int sourceId, int destId, String sourceName, String sourceIcon,int sourceLevel, String content, int useType) {
		super(sourceId, destId, sourceName, sourceIcon, sourceLevel, content, useType);
		
	}
	
	public MailEx(int sourceId, int destId, String destName, String sourceName, String sourceIcon,int sourceLevel ,int lang, int mailTemplateId, int useType) {
		super(sourceId, destId, destName, sourceName, sourceIcon, sourceLevel ,lang, mailTemplateId, useType);
		
	}

	public int getRich() {
		return rich;
	}

	public void setRich(int rich) {
		this.rich = rich;
	}

	public int getRaceId() {
		return raceId;
	}

	public void setRaceId(int raceId) {
		this.raceId = raceId;
	}
	public int getDownload() {
		return download;
	}
	public void setDownload(int download) {
		this.download = download;
	}
	public int getTemplateId() {
		return templateId;
	}
	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
	public int getAwardId() {
		return awardId;
	}
	public void setAwardId(int awardId) {
		this.awardId = awardId;
	}
	
	
}
