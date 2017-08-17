/**
 * MarketPosTemplate.java
 * ak.market
 *
 *   version  date      	author
 * ──────────────────────────────────
 *    1.0	 2013年12月2日 		shiwei2006
 *
 * Copyright (c) 2013, www.cyou-inc.com All Rights Reserved.
 */

package ak.market;

import cyou.mrd.projectdata.Template;
import cyou.mrd.projectdata.TextDataInitException;

/**
 * ClassName:MarketPosTemplate ClassDescription: 市场货架格子配置表
 * 
 * @author shiwei2006
 * @Date 2013年12月2日 下午4:12:36
 * @version 1.0
 */
public class MarketPosTemplate implements Template {

	private int id;// 格子号从1开始

	private int openType;

	private int friendNum;

	private int diamondNum;

	private int itemId;

	private int itemNum;
	
	/**
	 * 开启条件：0.初始默认开启
	 */
	public static final int OPEN_TYPE_INIT = 0;
	
	/**
	 * 开启条件：1.好友数量开启
	 */
	public static final int OPEN_TYPE_FRIEND = 1;
	
	/**
	 * 开启条件：2.灯笼开启
	 */
	public static final int OPEN_TYPE_DIAMOND = 2;
	
	/**
	 * 开启条件：3.道具开启
	 */
	public static final int OPEN_TYPE_ITEM = 3;
	

	@Override
	public Template initTemplateByTxtLine(String[] txtLineinfo) throws TextDataInitException {
		MarketPosTemplate mt = new MarketPosTemplate();
		mt.id = Integer.parseInt(txtLineinfo[0]);
		mt.openType = Integer.parseInt(txtLineinfo[1]);
		mt.friendNum = Integer.parseInt(txtLineinfo[2]);
		mt.diamondNum = Integer.parseInt(txtLineinfo[3]);
		mt.itemId = Integer.parseInt(txtLineinfo[4]);
		mt.itemNum = Integer.parseInt(txtLineinfo[5]);
		return mt;
	}

	@Override
	public int getId() {
		return id;
	}

	public int getOpenType() {
		return openType;
	}

	public void setOpenType(int openType) {
		this.openType = openType;
	}

	public int getFriendNum() {
		return friendNum;
	}

	public void setFriendNum(int friendNum) {
		this.friendNum = friendNum;
	}

	public int getDiamondNum() {
		return diamondNum;
	}

	public void setDiamondNum(int diamondNum) {
		this.diamondNum = diamondNum;
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

	public void setId(int id) {
		this.id = id;
	}

}
