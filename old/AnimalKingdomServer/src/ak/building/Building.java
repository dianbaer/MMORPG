package ak.building;

import java.io.Serializable;

import net.sf.json.JSONObject;

public class Building implements Cloneable, Serializable {
	
	/*
	 * 版本1 增加树的支持
	 */
	public static final byte BUILDING_VERSION_TREE = 1;
	
	/*
	 * 版本2 压缩building DB容量 去掉instanceId，压缩havestTimes为byte
	 */
	public static final byte BUILDING_VERSION_NO_INSTANCEID = 2;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;

	private byte version;

	private int instanceId;
	private int templateId;
	private int x;
	private int y;
	private int state;
	private int StateTime;

	private int havestTimes;//byte
	private boolean isHarvest;
	private int Flip;
	private int AssembleId;
	private int techHarvst;
	private double techTime;
	private int clickFarmID;
	private int mFarmSelfId;
	private int clickChopWoodID;
	private int clickOreID;
	private int startTimer;
	private int techId;
	private int complete;
	private int mChopWoodId;
	private double mProduceWoodTime;
	private int mOreId;
	private double mProduceOreTime;

	public Building(int instanceId, int templateId, int x, int y) {
		this.instanceId = instanceId;
		this.templateId = templateId;
		this.x = x;
		this.y = y;
		this.version = BUILDING_VERSION_NO_INSTANCEID;
	}
	
	public Building(int templateId, int x, int y) {
		this(0,templateId, x, y);
	}

	public int getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(int instanceId) {
		this.instanceId = instanceId;
	}

	public int getFlip() {
		return Flip;
	}

	public void setFlip(int flip) {
		this.Flip = flip;
	}

	public int getAssembleId() {
		return AssembleId;
	}

	public void setAssembleId(int assembleId) {
		AssembleId = assembleId;
	}

	public int getTemplateId() {
		return templateId;
	}

	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getStateTime() {
		return StateTime;
	}

	public void setStateTime(int stateTime) {
		StateTime = stateTime;
	}

	public int getHavestTimes() {
		return havestTimes;
	}

	public void setHavestTimes(int havestTimes) {
		this.havestTimes = havestTimes;
	}

	public boolean isHarvest() {
		return isHarvest;
	}

	public void setHarvest(boolean isHarvest) {
		this.isHarvest = isHarvest;
	}

	public int getTechHarvst() {
		return techHarvst;
	}

	public void setTechHarvst(int techHarvst) {
		this.techHarvst = techHarvst;
	}

	public double getTechTime() {
		return techTime;
	}

	public void setTechTime(double techTime) {
		this.techTime = techTime;
	}

	public int getClickFarmID() {
		return clickFarmID;
	}

	public void setClickFarmID(int clickFarmID) {
		this.clickFarmID = clickFarmID;
	}

	public int getmFarmSelfId() {
		return mFarmSelfId;
	}

	public void setmFarmSelfId(int mFarmSelfId) {
		this.mFarmSelfId = mFarmSelfId;
	}

	public int getClickChopWoodID() {
		return clickChopWoodID;
	}

	public void setClickChopWoodID(int clickChopWoodID) {
		this.clickChopWoodID = clickChopWoodID;
	}

	public int getClickOreID() {
		return clickOreID;
	}

	public void setClickOreID(int clickOreID) {
		this.clickOreID = clickOreID;
	}

	public int getStartTimer() {
		return startTimer;
	}

	public void setStartTimer(int startTimer) {
		this.startTimer = startTimer;
	}

	public int getTechId() {
		return techId;
	}

	public void setTechId(int techId) {
		this.techId = techId;
	}

	public int getComplete() {
		return complete;
	}

	public void setComplete(int complete) {
		this.complete = complete;
	}

	public byte getVersion() {
		return version;
	}

	public void setVersion(byte version) {
		this.version = version;
	}

	public JSONObject toJSONObject() {
		JSONObject temp = new JSONObject();
		temp.put("x", this.getX());
		temp.put("y", this.getY());
		temp.put("ID", this.getTemplateId());
		temp.put("State", this.getState());
		if(this.getStateTime() != 0) {
			temp.put("StateTime", this.getStateTime());
		}
		if(this.getHavestTimes() != 0) {
			temp.put("havestTimes", this.getHavestTimes());
		}
		if(this.isHarvest()) {
			temp.put("isHarvest", 1);
		}else{
			temp.put("isHarvest", 0);
		}
		if(this.getFlip() != 0) {
			temp.put("flip", this.getFlip());
		}
		if(this.getAssembleId() != 0) {
			temp.put("AssembleId", this.getAssembleId());
		}
		if(this.getTechHarvst() != 0) {
			temp.put("TechHarvst", this.getTechHarvst());
		}
		if(this.getTechTime() != 0.0) {
			temp.put("TechTime", this.getTechTime());
		}
		if(this.getClickFarmID() != 0) {
			temp.put("ClickFarmID", this.getClickFarmID());
		}
		if(this.getmFarmSelfId() != 0) {
			temp.put("mFarmSelfId", this.getmFarmSelfId());
		}
		if(this.getClickChopWoodID() != 0) {
			temp.put("ClickChopWoodID", this.getClickChopWoodID());
		}
		if(this.getClickOreID() != 0) {
			temp.put("ClickOreID", this.getClickOreID());
		}
		if(this.getStartTimer() != 0) {
			temp.put("StartTimer", this.getStartTimer());
		}
		if(this.getTechId() != 0) {
			temp.put("TechId", this.getTechId());
		}
		if(this.getComplete() != 0) {
			temp.put("complete", this.getComplete());
		}
		if(this.getmChopWoodId() != 0) {
			temp.put("mChopWoodId", this.getmChopWoodId());
		}
		if(this.getmProduceWoodTime() != 0.0) {
			temp.put("mProduceWoodTime", this.getmProduceWoodTime());
		}
		if(this.getmOreId() != 0) {
			temp.put("mOreId", this.getmOreId());
		}
		if(this.getmProduceOreTime() != 0.0) {
			temp.put("mProduceOreTime", this.getmProduceOreTime());
		}
		
		temp.put("SelfID", instanceId);
//		temp.put("StateTime", this.getStateTime());
//		temp.put("havestTimes", this.getHavestTimes());
//		temp.put("isHarvest", this.isHarvest());
//		temp.put("flip", this.getFlip());
//		temp.put("AssembleId", this.getAssembleId());
		return temp;
	}

	@Override
	protected Building clone() throws CloneNotSupportedException {
		return (Building) super.clone();
	}

	public int getmChopWoodId() {
		return mChopWoodId;
	}

	public void setmChopWoodId(int mChopWoodId) {
		this.mChopWoodId = mChopWoodId;
	}

	public double getmProduceWoodTime() {
		return mProduceWoodTime;
	}

	public void setmProduceWoodTime(double mProduceWoodTime) {
		this.mProduceWoodTime = mProduceWoodTime;
	}

	public int getmOreId() {
		return mOreId;
	}

	public void setmOreId(int mOreId) {
		this.mOreId = mOreId;
	}

	public double getmProduceOreTime() {
		return mProduceOreTime;
	}

	public void setmProduceOreTime(double mProduceOreTime) {
		this.mProduceOreTime = mProduceOreTime;
	}

}
