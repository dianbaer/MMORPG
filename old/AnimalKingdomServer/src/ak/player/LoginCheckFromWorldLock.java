package ak.player;

import ak.util.SRWLock;

public class LoginCheckFromWorldLock extends SRWLock{
	/**
	 * 返回的状态
	 */
	private int state;

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
}
