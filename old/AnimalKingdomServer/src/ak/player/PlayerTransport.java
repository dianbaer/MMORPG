package ak.player;

import java.util.Date;

public class PlayerTransport{
	private int id;
	private int playerId;
	private int friendId;
	private String friendName;
	private String icon;
	private Date transTime;
	private boolean exist;

	public PlayerTransport() {
	}

	public PlayerTransport(int playerId, PlayerEx friend) {
		this.playerId = playerId;
		this.friendId = friend.getInstanceId();
		this.friendName = friend.getName() == null ? "" : friend.getName();
		transTime = new Date();
		exist = true;
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

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public void setFriendId(int friendId) {
		this.friendId = friendId;
	}

	public String getFriendName() {
		return friendName;
	}

	public void setFriendName(String friendName) {
		this.friendName = friendName;
	}

	public Date getTransTime() {
		return transTime;
	}

	public void setTransTime(Date transTime) {
		this.transTime = transTime;
	}

	public boolean isExist() {
		return exist;
	}

	public void setExist(boolean exist) {
		this.exist = exist;
	}

}
