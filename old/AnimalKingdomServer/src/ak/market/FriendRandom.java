package ak.market;

import ak.util.IRandom;

public class FriendRandom implements IRandom {

	private int playerId;
	private String name;
	private String icon;
	private int rich;
	private int raceId;
	private int level;
	private int probability;
	
	@Override
	public int getOpenLvl() {
		
		return 0;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getRich() {
		return rich;
	}

	public void setRich(int rich) {
		this.rich = rich;
	}

	public int getRaceId() {
		return raceId;
	}

	public void setRaceId(int raceId) {
		this.raceId = raceId;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getProbability() {
		return probability;
	}

	public void setProbability(int probability) {
		this.probability = probability;
	}

	@Override
	public int getType() {
		
		return 0;
	}

}
