package ak.trade;

import java.io.Serializable;

public class Trade implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 玩家id
	 */
	private int playerId;
	private int pos;
	/**
	 * 物品id
	 */
	private int itemId;
	/**
	 * 物品数量
	 */
	private int itemNum;
	/**
	 * 贸易id
	 */
	private int tradeId;
	/**
	 * 状态2. 请求帮助中	3. 好友完成		4. 自己完成
	 */
	private int status;
	/**
	 * 清空状态
	 */
	public static final int STATUS_NULL = 1;
	/**
	 * 请求帮助
	 */
	public static final int STATUS_REQUEST_HELP = 2;
	/**
	 * 好友完成
	 */
	public static final int STATUS_FRIEND_COMPLETE = 3;
	/**
	 * 自己完成
	 */
	public static final int STATUS_SELF_COMPLETE = 4;
	/**
	 * 箱子id对应前段表
	 */
	private int boxId;
	/**
	 * 帮助玩家的id
	 */
	private int helpPlayerId;
	/**
	 * 帮助玩家名
	 */
	private String helpPlayerName;
	/**
	 * 帮助玩家icon
	 */
	private String helpPlayerIcon;
	/**
	 * 帮助玩家繁荣度
	 */
	private int helpPlayerRich;
	/**
	 * 操作（默认是改）
	 */
	//private int operate = UPDATE;
	/**
	 * 增
	 */
	//public static final int ADD = 1;
	/**
	 * 改
	 */
	//public static final int UPDATE = 2;
	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getBoxId() {
		return boxId;
	}
	public void setBoxId(int boxId) {
		this.boxId = boxId;
	}
	public int getHelpPlayerId() {
		return helpPlayerId;
	}
	public void setHelpPlayerId(int helpPlayerId) {
		this.helpPlayerId = helpPlayerId;
	}
	public String getHelpPlayerName() {
		return helpPlayerName;
	}
	public void setHelpPlayerName(String helpPlayerName) {
		this.helpPlayerName = helpPlayerName;
	}
	public String getHelpPlayerIcon() {
		return helpPlayerIcon;
	}
	public void setHelpPlayerIcon(String helpPlayerIcon) {
		this.helpPlayerIcon = helpPlayerIcon;
	}
	public int getHelpPlayerRich() {
		return helpPlayerRich;
	}
	public void setHelpPlayerRich(int helpPlayerRich) {
		this.helpPlayerRich = helpPlayerRich;
	}
	public int getItemId() {
		return itemId;
	}
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	public int getItemNum() {
		return itemNum;
	}
	public void setItemNum(int itemNum) {
		this.itemNum = itemNum;
	}
	public int getTradeId() {
		return tradeId;
	}
	public void setTradeId(int tradeId) {
		this.tradeId = tradeId;
	}
//	public int getOperate() {
//		return operate;
//	}
//	public void setOperate(int operate) {
//		this.operate = operate;
//	}
	public int getPos() {
		return pos;
	}
	public void setPos(int pos) {
		this.pos = pos;
	}
	
}
