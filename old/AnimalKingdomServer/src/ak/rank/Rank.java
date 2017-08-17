package ak.rank;

import java.io.Serializable;

import net.sf.json.JSONObject;

public class Rank implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 排行榜id
	 */
	private int rankId;
	/**
	 * 类型
	 */
	private int type;
	/**
	 * 1.	繁荣榜
	 */
	public static final int TYPE_RICH = 1;
	/**
	 * 2.	闯关榜
	 */
	public static final int TYPE_PASS = 2;
	/**
	 * 3.	爱心榜
	 */
	public static final int TYPE_LOVE = 3;
	/**
	 * 4.	金币榜
	 */
	public static final int TYPE_GOLD = 4;
	/**
	 * 5.	食物榜
	 */
	public static final int TYPE_FOOD = 5;
	/**
	 * 6.	木材榜
	 */
	public static final int TYPE_WOOD = 6;
	/**
	 * 7.	石头榜
	 */
	public static final int TYPE_STONE = 7;
	/**
	 * 8.	摇钱榜
	 */
	public static final int TYPE_MONEY_TREE = 8;
	/**
	 * 9.	兵力榜
	 */
	public static final int TYPE_FORCE = 9;
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
	 * 玩家id
	 */
	private int playerId;
	/**
	 * 排行的日期
	 */
	private int rankDay;
	/**
	 * 玩家昵称
	 */
	private String playerName;
	/**
	 * 玩家等级
	 */
	private int playerLvl;
	public int getRankId() {
		return rankId;
	}
	public void setRankId(int rankId) {
		this.rankId = rankId;
	}
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
	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	public JSONObject toJSONObject() {
		JSONObject temp = new JSONObject();
		temp.put("rankId", rankId);
		temp.put("type", type);
		temp.put("lastRank", lastRank);
		temp.put("nowRank", nowRank);
		temp.put("value", value);
		temp.put("playerId", playerId);
		return temp;
	}
	public int getRankDay() {
		return rankDay;
	}
	public void setRankDay(int rankDay) {
		this.rankDay = rankDay;
	}
	/**
	 * copy 复制
	 */
	public Rank clone(){
		Rank rank = new Rank();
		rank.lastRank = lastRank;
		rank.nowRank = nowRank;
		rank.playerId = playerId;
		rank.rankDay = rankDay;
		rank.rankId = rankId;
		rank.type = type;
		rank.value = value;
		return rank;
	}
	public String getPlayerName() {
		return playerName;
	}
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	public int getPlayerLvl() {
		return playerLvl;
	}
	public void setPlayerLvl(int playerLvl) {
		this.playerLvl = playerLvl;
	}
	
}
