package ak.optLog;

import java.io.Serializable;
import java.util.Date;
/**
 * 用户操作日志
 * @author xuepeng
 *
 */
public class UserOptLog implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/****************对应type字段****************************/
	/**
	 * 系统奖励
	 */
	public static final int TYPE_SYSTEM_AWARD = 1;
	/**
	 * 爱心值道具
	 */
	public static final int TYPE_LOVE_SHOP_ITEM = 2;
	/**
	 * 点击随机npc获取的道具
	 */
	public static final int RANDOM_NPC = 3;
	/**
	 * 帮助好友打扫房间获得道具
	 */
	public static final int HELP_FRIEND = 4;
	/**
	 * 送礼物获得道具
	 */
	public static final int SEND_GIFT = 5;
	
	
	
	/*******************对应status字段*************************/
	/**
	 * 服务器产生
	 */
	public static final int STATUS_SERVER_ADD = 1;
	/**
	 * 客户端相应并返回
	 */
	public static final int STATUS_CLIENT_BACK = 2;
	
	
	/*******************对应content字段*************************/
	/**
	 * 给用户发放了奖励
	 */
	public static final String CONTENT_1 = "给用户发放了奖励";
	/**
	 * 给用户返回了爱心值奖励
	 */
	public static final String CONTENT_2 = "给用户返回了爱心值奖励";
	
	/**
	 * 日志id
	 */
	private int id;
	/**
	 * 玩家id
	 */
	private int playerId;
	/**
	 * 类型 1. 系统奖励
	 */
	private int type;
	/**
	 * 系统奖励对应的mail表的id
	 */
	private int typeId;
	/**
	 * 日志状态 1. 服务端产生 2. 客户端成功响应并处理
	 */
	private int status;
	/**
	 * 内容
	 */
	private String content;
	/**
	 * 添加时间
	 */
	private Date addTime;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
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
	public void setStatus(int status) {
		this.status = status;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getAddTime() {
		return addTime;
	}
	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}
	public int getTypeId() {
		return typeId;
	}
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
}
