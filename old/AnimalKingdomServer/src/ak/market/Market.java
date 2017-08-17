/**
 * Market.java
 * ak.market
 *
 *   version  date      	author
 * ──────────────────────────────────
 *    1.0	 2013年11月30日 		shiwei2006
 *
 * Copyright (c) 2013, www.cyou-inc.com All Rights Reserved.
 */

package ak.market;

import java.io.Serializable;

/**
 * ClassName:Market ClassDescription: 市场交易类
 * 
 * @author shiwei2006
 * @Date 2013年11月30日 下午7:48:48
 * @version 1.0
 */
public class Market implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int playerId;

	private int pos;

	private int status;

	private int itemId;

	private int itemNum;

	private int price;

	private int addTime;

	private int expireTime;// 有效时间（6小时挂单时间，到时后被系统标记为过期，市场上不可见）

	private int recycleTime;// 回收时间（过期3小时后被系统自动回收）expire_time + 3小时

	private int buyPlayerId;

	private String buyPlayerName;

	private String buyPlayerIcon;

	private int buyPlayerRich;
	
	private String sellPlayerName;
	private String sellPlayerIcon;
	private int sellPlayerRich;
	private int sellPlayerLvl;
	private int sellPlayerRaceId;
	
	//private int operate = UPDATE;
	/**
	 * 增
	 */
	//public static final int ADD = 1;
	/**
	 * 改
	 */
	//public static final int UPDATE = 2;
	/**
	 * 格子状态：空闲中
	 */
	public static final int STATUS_IDLE = 1;

	/**
	 * 格子状态：上架出售中
	 */
	public static final int STATUS_SELLING = 2;

	/**
	 * 格子状态：玩家已购买
	 */
	public static final int STATUS_BUYED = 3;

	/**
	 * 格子状态：格子物品已过期
	 */
	public static final int STATUS_EXPIRE = 4;

	/**
	 * 格子状态：被系统回收
	 */
	public static final int STATUS_RECYCLE = 5;

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(int expireTime) {
		this.expireTime = expireTime;
	}

	public int getRecycleTime() {
		return recycleTime;
	}

	public void setRecycleTime(int recycleTime) {
		this.recycleTime = recycleTime;
	}

	public int getBuyPlayerId() {
		return buyPlayerId;
	}

	public void setBuyPlayerId(int buyPlayerId) {
		this.buyPlayerId = buyPlayerId;
	}

	public String getBuyPlayerName() {
		return buyPlayerName;
	}

	public void setBuyPlayerName(String buyPlayerName) {
		this.buyPlayerName = buyPlayerName;
	}

	public String getBuyPlayerIcon() {
		return buyPlayerIcon;
	}

	public void setBuyPlayerIcon(String buyPlayerIcon) {
		this.buyPlayerIcon = buyPlayerIcon;
	}

	public int getBuyPlayerRich() {
		return buyPlayerRich;
	}

	public void setBuyPlayerRich(int buyPlayerRich) {
		this.buyPlayerRich = buyPlayerRich;
	}

	public int getAddTime() {
		return addTime;
	}

	public void setAddTime(int addTime) {
		this.addTime = addTime;
	}

//	public int getOperate() {
//		return operate;
//	}
//
//	public void setOperate(int operate) {
//		this.operate = operate;
//	}

	public int getSellPlayerRich() {
		return sellPlayerRich;
	}

	public void setSellPlayerRich(int sellPlayerRich) {
		this.sellPlayerRich = sellPlayerRich;
	}

	public int getSellPlayerLvl() {
		return sellPlayerLvl;
	}

	public void setSellPlayerLvl(int sellPlayerLvl) {
		this.sellPlayerLvl = sellPlayerLvl;
	}

	public int getSellPlayerRaceId() {
		return sellPlayerRaceId;
	}

	public void setSellPlayerRaceId(int sellPlayerRaceId) {
		this.sellPlayerRaceId = sellPlayerRaceId;
	}

	public String getSellPlayerName() {
		return sellPlayerName;
	}

	public void setSellPlayerName(String sellPlayerName) {
		this.sellPlayerName = sellPlayerName;
	}

	public String getSellPlayerIcon() {
		return sellPlayerIcon;
	}

	public void setSellPlayerIcon(String sellPlayerIcon) {
		this.sellPlayerIcon = sellPlayerIcon;
	}

}
