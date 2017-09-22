package cyou.mrd.game.actor;

import java.io.Serializable;
import java.util.Date;

import cyou.mrd.entity.Player;

public class Actor implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	private int accountId;
	private int level;
	private int star;
	private String name;
	private String icon;
	//新版兔村需要添加属性
	private int raceId;//种族ID
	private int rich;//繁荣度
	protected boolean online; // 是否在线
	private Date lastLoginTime;//仅用于查询使用

	public Actor(Player player) {
		this.id = player.getInstanceId();
		this.accountId = player.getAccountId();
		this.level = player.getLevel();
		this.star = player.getStar();
		this.name = player.getName();
		this.icon = player.getIcon();
		this.raceId = player.getRaceId();
		this.rich = player.getRich();
	}

	public Actor() {
	}

	public Actor(int id, String name, String icon, int level, int star, int raceId, int rich) {
		this.id = id;
		this.level = level;
		this.star = star;
		this.name = name;
		this.icon = icon;
		this.raceId = raceId;
		this.rich = rich;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
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

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getStar() {
		return star;
	}

	public void setStar(int star) {
		this.star = star;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("playerId:").append(this.id);
		sb.append(", accountId:").append(this.accountId);
		sb.append(", level:").append(this.level);
		sb.append(", name:").append(this.name);
		sb.append(", icon:").append(this.icon);
		sb.append(", star:").append(this.star);
		sb.append(", raceId:").append(this.raceId);
		sb.append(", rich:").append(this.rich);
		return sb.toString();
	}

	
	public int getRaceId() {
		return raceId;
	}

	
	public void setRaceId(int raceId) {
		this.raceId = raceId;
	}

	
	public int getRich() {
		return rich;
	}

	
	public void setRich(int rich) {
		this.rich = rich;
	}

	
	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	
	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
}
