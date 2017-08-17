/**
 * MoneyTreeTemplate.java
 * ak.playerSns
 *
 *   version  date      	author
 * ──────────────────────────────────
 *    1.0	 2013年11月25日 		shiwei2006
 *
 * Copyright (c) 2013, www.cyou-inc.com All Rights Reserved.
*/

package ak.playerSns;

import cyou.mrd.projectdata.Template;
import cyou.mrd.projectdata.TextDataInitException;

/**
 * ClassName:MoneyTreeTemplate
 * ClassDescription:  摇钱树配置表
 *
 * @author   shiwei2006
 * @Date	 2013年11月25日		下午3:32:23
 * @version  1.0
 */
public class MoneyTreeTemplate implements Template {
	
	private int gradeId;
	
	private int friendWaterTimes;
	
	private int helpWaterTimes;
	
	private int coolTime;
	
	private int costActiveCount;
	
	private int friendAwardId;
	
	private int selfAwardId;

	@Override
	public Template initTemplateByTxtLine(String[] txtLineinfo) throws TextDataInitException {
		MoneyTreeTemplate mt = new MoneyTreeTemplate();
		mt.gradeId = Integer.parseInt(txtLineinfo[0]);
		mt.friendWaterTimes = Integer.parseInt(txtLineinfo[1]);
		mt.helpWaterTimes = Integer.parseInt(txtLineinfo[2]);
		mt.coolTime = Integer.parseInt(txtLineinfo[3]);
		mt.costActiveCount = Integer.parseInt(txtLineinfo[4]);
		mt.friendAwardId = Integer.parseInt(txtLineinfo[5]);
		mt.selfAwardId = Integer.parseInt(txtLineinfo[6]);
		return mt;
	}

	@Override
	public int getId() {
		return gradeId;
	}

	public int getGradeId() {
		return gradeId;
	}

	public void setGradeId(int gradeId) {
		this.gradeId = gradeId;
	}

	public int getFriendWaterTimes() {
		return friendWaterTimes;
	}

	public void setFriendWaterTimes(int friendWaterTimes) {
		this.friendWaterTimes = friendWaterTimes;
	}

	public int getHelpWaterTimes() {
		return helpWaterTimes;
	}

	public void setHelpWaterTimes(int helpWaterTimes) {
		this.helpWaterTimes = helpWaterTimes;
	}

	public int getCoolTime() {
		return coolTime;
	}

	public void setCoolTime(int coolTime) {
		this.coolTime = coolTime;
	}

	public int getCostActiveCount() {
		return costActiveCount;
	}

	public void setCostActiveCount(int costActiveCount) {
		this.costActiveCount = costActiveCount;
	}

	public int getFriendAwardId() {
		return friendAwardId;
	}

	public void setFriendAwardId(int friendAwardId) {
		this.friendAwardId = friendAwardId;
	}

	public int getSelfAwardId() {
		return selfAwardId;
	}

	public void setSelfAwardId(int selfAwardId) {
		this.selfAwardId = selfAwardId;
	}

}

