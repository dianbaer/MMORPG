package ak.friend;

import java.io.Serializable;

import cyou.mrd.util.Time;

public class FriendHome implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// public static final int MAX_CLEAR_TIMES = 30;
	// public static final int MAX_TRANSPORT_TIMES = 10;
	

	private int id;
	private int playerId;
	private int friendId;
	private int clearTimes;
	private int clearDay;// Time.day
	private int transportTimes;
	private int transportDay;// Time.day

	public boolean needCreateFlag;
	public boolean needSaveFlag;
	//新加的字段
	/**
	 * 发送爱心次数
	 */
	private int sendTimes;
	/**
	 * 发送爱心日期
	 */
	private int sendDay;
	/**
	 * 田地的状态
	 */
	private int fieldState;
	/**
	 * 无状态
	 */
	public static final int STATE_NULL = 0;
	/**
	 * 田地更新日期
	 */
	private int fieldDay;
	/**
	 * 房屋状态
	 */
	private int houseState;
	/**
	 * 房屋更新日期
	 */
	private int houseDay;
	/**
	 * 刷新的npcId
	 */
	private int npcId;

	public FriendHome() {
	}

	public FriendHome(int playerId, int friendId) {
		this.playerId = playerId;
		this.friendId = friendId;
		this.clearDay = Time.day;
		this.transportDay = Time.day;
		this.needCreateFlag = true;
	}

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

	public int getFriendId() {
		return friendId;
	}

	public void setFriendId(int friendId) {
		this.friendId = friendId;
	}

	public int getTodayClearTimes() {
		if (clearDay == Time.day) {
			return clearTimes;
		} else {
			clearTimes = 0;
			return 0;
		}
	}

	public int getClearTimes() {
		return clearTimes;
	}

	public void setClearTimes(int clearTimes) {
		this.clearTimes = clearTimes;
	}

	public void addClearTimes() {
		clearTimes++;
		clearDay = Time.day;
		needSaveFlag = true;
	}

	public int getClearDay() {
		return clearDay;
	}

	public void setClearDay(int clearDay) {
		this.clearDay = clearDay;
	}

	public int getTodayTransportTimes() {
		if (transportDay == Time.day) {
			return transportTimes;
		} else {
			transportTimes = 0;
			return 0;
		}
	}

	public int getTransportTimes() {
		return transportTimes;
	}

	public void setTransportTimes(int transportTimes) {
		this.transportTimes = transportTimes;
	}

	public int getTransportDay() {
		return transportDay;
	}

	public void setTransportDay(int transportDay) {
		this.transportDay = transportDay;
	}

	public void addTransportTimes() {
		transportTimes++;
		transportDay = Time.day;
		needSaveFlag = true;
	}

	public int getState() {
		int ret = 0;
		ret |= (this.clearDay == Time.day && this.clearTimes >= FriendService.HELP_FRIEND_ONEPLAYER_NUM_EVERYDAY) ? 0 : 1;
		ret |= (this.transportDay == Time.day && this.transportTimes >= FriendService.SEND_GIFT_ONEPLAYER_NUM_EVERYDAY) ? 0 : (1 << 1);
		return ret;
	}

	public boolean isNothingSave() {
		return transportTimes == 0 && clearTimes == 0;
	}

	public int getSendTimes() {
		return sendTimes;
	}

	public void setSendTimes(int sendTimes) {
		this.sendTimes = sendTimes;
	}

	public int getSendDay() {
		return sendDay;
	}

	public void setSendDay(int sendDay) {
		this.sendDay = sendDay;
	}
	public int getTodaySendTimes() {
		if (sendDay == Time.day) {
			return sendTimes;
		} else {
			sendTimes = 0;
			return 0;
		}
	}
	public void addSendTimes() {
		sendTimes++;
		sendDay = Time.day;
		needSaveFlag = true;
	}

	public int getFieldState() {
		return fieldState;
	}

	public void setFieldState(int fieldState) {
		this.fieldState = fieldState;
		fieldDay = Time.day;
		needSaveFlag = true;
	}
	/**
	 * 获取今天的田地状态
	 * @return
	 */
	public int getTodayFieldState() {
		if (fieldDay == Time.day) {
			return fieldState;
		} else {
			fieldState = STATE_NULL;
			return fieldState;
		}
	}
	public int getFieldDay() {
		return fieldDay;
	}

	public void setFieldDay(int fieldDay) {
		this.fieldDay = fieldDay;
	}

	public int getHouseState() {
		return houseState;
	}

	public void setHouseState(int houseState) {
		this.houseState = houseState;
		houseDay = Time.day;
		needSaveFlag = true;
	}
	/**
	 * 获取今天的房屋状态
	 * @return
	 */
	public int getTodayHouseState() {
		if (houseDay == Time.day) {
			return houseState;
		} else {
			houseState = STATE_NULL;
			return houseState;
		}
	}
	public int getHouseDay() {
		return houseDay;
	}

	public void setHouseDay(int houseDay) {
		this.houseDay = houseDay;
	}

	public int getNpcId() {
		return npcId;
	}

	public void setNpcId(int npcId) {
		this.npcId = npcId;
	}
}
