/**
 * MarketLog.java
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
import java.util.Date;

/**
 * ClassName:MarketLog ClassDescription: 市场交易日志
 * 
 * @author shiwei2006
 * @Date 2013年11月30日 下午8:02:11
 * @version 1.0
 */
public class MarketLog implements Serializable {

	private int logId;

	private int type;

	private int pos;

	private int itemId;

	private int itemNum;

	private int price;

	private int sellPlayerId;

	private int buyPlayerId;

	private Date addTime;
	
	/**
	 * 1.玩家下架
	 */
	public static final int TYPE_DOWN = 1;
	
	/**
	 * 2.玩家购买
	 */
	public static final int TYPE_BUY = 2;
	
	/**
	 * 3.系统回收
	 */
	public static final int TYPE_RECYCLE = 3;
	
	/**
	 * 4.收取金币（卖方获得）
	 */
	public static final int TYPE_COLLECT = 4;

	public int getLogId() {
		return logId;
	}

	public void setLogId(int logId) {
		this.logId = logId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
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

	public int getSellPlayerId() {
		return sellPlayerId;
	}

	public void setSellPlayerId(int sellPlayerId) {
		this.sellPlayerId = sellPlayerId;
	}

	public int getBuyPlayerId() {
		return buyPlayerId;
	}

	public void setBuyPlayerId(int buyPlayerId) {
		this.buyPlayerId = buyPlayerId;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

}
