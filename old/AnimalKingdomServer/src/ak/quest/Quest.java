package ak.quest;

import java.io.Serializable;

import net.sf.json.JSONObject;

/**
 * 
 * 任务类
 * 
 * @author miaoshengli
 */
public class Quest implements Cloneable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	/**
	 * 第一版
	 */
	public static final byte Quest_VERSION_0 = 0;
	
	private int id;
	private int state;
	private int condition;
	private int startTime;
	private int timeLong;
	private int battleWinTimes;
	private String building;
	private String item;

	private int version;

	public Quest(int id, int state, int condition, int startTime, int timeLong, int battleWinTimes, String building, String item) {
		this.version = Quest_VERSION_0;
		this.id = id;
		this.state = state;
		this.condition = condition;
		this.startTime = startTime;
		this.timeLong = timeLong;
		this.battleWinTimes = battleWinTimes;
		this.building = building;
		this.item = item;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getCondition() {
		return condition;
	}

	public void setCondition(int condition) {
		this.condition = condition;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getTimeLong() {
		return timeLong;
	}

	public void setTimeLong(int timeLong) {
		this.timeLong = timeLong;
	}

	public int getBattleWinTimes() {
		return battleWinTimes;
	}

	public void setBattleWinTimes(int battleWinTimes) {
		this.battleWinTimes = battleWinTimes;
	}

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public int getVersion() {
		return version;
	}

	@Override
	protected Quest clone() throws CloneNotSupportedException {
		return (Quest) super.clone();
	}

	public JSONObject toJson() {
		JSONObject json = new JSONObject();
		json.put("ID", this.id);
		json.put("State", this.state);
		json.put("Condition", this.condition);
		json.put("StartTime", this.startTime);
		json.put("TimeLong", this.timeLong);
		json.put("BattleWinTimes", this.battleWinTimes);
		json.put("building", this.building);
		json.put("item", this.item);
		return json;
	}
}
