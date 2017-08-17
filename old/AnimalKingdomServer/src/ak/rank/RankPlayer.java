package ak.rank;

import net.sf.json.JSONObject;

public class RankPlayer {
	/**
	 * 类型
	 */
	private int type;
	/**
	 * 上一次排名
	 */
	private int lastRank;
	/**
	 * 当前排名
	 */
	private int nowRank;
	/**
	 * 值
	 */
	private int value;
	/**
	 * 上一次更新的日子
	 */
	private int lastUpdateDay;
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getLastRank() {
		return lastRank;
	}
	public void setLastRank(int lastRank) {
		this.lastRank = lastRank;
	}
	public int getNowRank() {
		return nowRank;
	}
	public void setNowRank(int nowRank) {
		this.nowRank = nowRank;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public JSONObject toJSONObject() {
		JSONObject temp = new JSONObject();
		temp.put("type", type);
		temp.put("lastRank", lastRank);
		temp.put("nowRank", nowRank);
		temp.put("value", value);
		temp.put("lastUpdateDay", lastUpdateDay);
		return temp;
	}
	public int getLastUpdateDay() {
		return lastUpdateDay;
	}
	public void setLastUpdateDay(int lastUpdateDay) {
		this.lastUpdateDay = lastUpdateDay;
	}
	
}
