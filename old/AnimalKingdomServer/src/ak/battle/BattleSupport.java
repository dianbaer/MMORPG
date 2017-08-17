package ak.battle;

import java.io.Serializable;

public class BattleSupport implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int mFriendID; // 好友ID;
	private int mTimeCount; // 时间计数;

	BattleSupport() {

	}

	BattleSupport(int friendID, int time) {
		mFriendID = friendID;
		mTimeCount = time;
	}

	public int getFriendID() {
		return mFriendID;
	}

	public void setTime(int time) {
		mTimeCount = time;
	}

	public int getTime() {
		return mTimeCount;
	}

}
