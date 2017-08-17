package ak.playerSns;

import java.io.Serializable;

import cyou.mrd.util.Time;


public class PlayerSns implements Serializable {
	/**
	 * 玩家的ID
	 */
	private int playerId;
	/**
	 * 帮助好友浇水次数，用户第一次初始化此记录，初始为0，以后为更新操作，每天有上限限制。
	 */
	private int helpCount;
	/**
	 * 最后一次帮助好友浇水更新日期。
	 */
	private int lastHelpDay;

	private int treeX;

	private int treeY;

	/**
	 * 当前摇钱树等级，由用户上传获取，可能不实时，计算时使用。需检查合理等级区间。
	 */
	private int treeGrade;
	
	private int treeStatus;//摇钱树状态
	
	/**
	 * 好友浇水次数，初始为0或冷却CD结束后清0，达到条件即为可收获状态
	 */
	private int visitCount;

	/**
	 * 记录当前已浇水的玩家ID列表，由于只存储玩家ID，采用字符串直接存储，逗号隔开的列表。初始固定为空字符串。
	 */
	private String visitRecord;

	/**
	 * 最后一次收获时间，玩家成功收获后更新的时间戮，初始为0表示
	 */
	private int lastHarvestTime;
	/**
	 * 每天的爱心发信次数，初始为0，每天重置为0
	 */
	private int sendCount;
	/**
	 * 当天爱心发信日期，年月日格式
	 */
	private int lastSendDay;
	/**
	 * 该玩家的体力值，即行动力。固定上限，每单位时间恢复1点，初始为上限最大值。
	 */
	private int activeCount;
	/**
	 * 最后一次计算更新体力值时间戮，初始为当前时间。
	 */
	private int lastActiveTime;
	/**
	 * 是否是第一次访问好友
	 */
	private int firstVisit;
	
	/**
	 * 摇钱树状态：0.系统初始化
	 */
	public static final int TREE_STATUS_INIT = 0;
	
	/**
	 * 摇钱树状态：1.可浇水
	 */
	public static final int TREE_STATUS_WATER = 1;
	
	/**
	 * 摇钱树状态：2.等待收获
	 */
	public static final int TREE_STATUS_HARVEST = 2;
	
	/**
	 * 摇钱树状态：3.冷却中
	 */
	public static final int TREE_STATUS_COOLING = 3;

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getHelpCount() {
		return helpCount;
	}

	public void setHelpCount(int helpCount) {
		this.helpCount = helpCount;
	}

	public int getLastHelpDay() {
		return lastHelpDay;
	}

	public void setLastHelpDay(int lastHelpDay) {
		this.lastHelpDay = lastHelpDay;
	}

	public int getTreeGrade() {
		return treeGrade;
	}

	public void setTreeGrade(int treeGrade) {
		this.treeGrade = treeGrade;
	}

	public int getVisitCount() {
		return visitCount;
	}

	public void setVisitCount(int visitCount) {
		this.visitCount = visitCount;
	}

	public int getLastHarvestTime() {
		return lastHarvestTime;
	}

	public void setLastHarvestTime(int lastHarvestTime) {
		this.lastHarvestTime = lastHarvestTime;
	}

	public int getSendCount() {
		return sendCount;
	}

	public void setSendCount(int sendCount) {
		this.sendCount = sendCount;
	}

	public int getLastSendDay() {
		return lastSendDay;
	}

	public void setLastSendDay(int lastSendDay) {
		this.lastSendDay = lastSendDay;
	}

	public int getActiveCount() {
		return activeCount;
	}

	public void setActiveCount(int activeCount) {
		this.activeCount = activeCount;
	}

	public int getLastActiveTime() {
		return lastActiveTime;
	}

	public void setLastActiveTime(int lastActiveTime) {
		this.lastActiveTime = lastActiveTime;
	}

	public int getTreeX() {
		return treeX;
	}

	public void setTreeX(int treeX) {
		this.treeX = treeX;
	}

	public int getTreeY() {
		return treeY;
	}

	public void setTreeY(int treeY) {
		this.treeY = treeY;
	}

	public String getVisitRecord() {
		return visitRecord;
	}

	public void setVisitRecord(String visitRecord) {
		this.visitRecord = visitRecord;
	}

	public int getTreeStatus() {
		return treeStatus;
	}

	public void setTreeStatus(int treeStatus) {
		this.treeStatus = treeStatus;
	}

	public static int getTreeStatusCooling() {
		return TREE_STATUS_COOLING;
	}
	/**
	 * 获取今天的发送爱心值次数，如果换天，则重置次数
	 * @return
	 */
	public int getTodaySendCount() {
		if (lastSendDay == Time.day) {
			return sendCount;
		} else {
			sendCount = 0;
			return sendCount;
		}
	}
	/**
	 * 增加发信增加爱心值次数
	 */
	public void addSendCount() {
		sendCount++;
		lastSendDay = Time.day;
	}

	public int getFirstVisit() {
		return firstVisit;
	}

	public void setFirstVisit(int firstVisit) {
		this.firstVisit = firstVisit;
	}
}
