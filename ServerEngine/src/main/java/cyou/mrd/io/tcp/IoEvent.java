package cyou.mrd.io.tcp;

public class IoEvent {
	/**
	 * 连接建立事件。参数：ClientSession
	 */
	public static final int EVENT_SESSION_ADDED = 1;
	/**
	 * 连接关闭事件。参数：ClientSession
	 */
	public static final int EVENT_SESSION_REMOVEING = 2;
	/**
	 * 连接关闭事件。参数：ClientSession
	 */
	public static final int EVENT_SESSION_REMOVED = 3;
	/**
	 * 连接建立事件。参数：ClientSession
	 */
	public static final int EVENT_SESSION_ADDED_SERVER = 4;
	
	/**
	 * 连接关闭事件。参数：ClientSession
	 */
	public static final int EVENT_SESSION_REMOVED_SERVER = 5;
	
	public static final int EVENT_KICK_PLAYER = 6;
	
	public static final int EVENT_PLAYER_CAN_LOGIN = 7;
	public static final int EVENT_PLAYER_RE_LOGIN = 8;
}
