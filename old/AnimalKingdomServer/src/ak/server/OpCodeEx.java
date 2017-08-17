package ak.server;

import cyou.mrd.io.tcp.OpCode;

public class OpCodeEx extends OpCode{
	
	/**
	 * 通知玩家下线
	 */
	public static final short AK_NOTICE_PLAYER_LOGOUT_CLIENT = 1000;
	/**
	 * 通知玩家下线world下发
	 */
	public static final short AK_NOTICE_PLAYER_LOGOUT_SERVER = 1001;
	/**
	 * 发送系统公告
	 */
	public static final short AK_SEND_SYSTEM_NOTICE_CLIENT = 1002;
	/**
	 * 通知所有在线用户，有新的公告
	 */
	public static final short AK_NOTIFY_ONLINE_USER_NEW_NOTICE_SERVER = 1003;
	/**
	 * 玩家登录验证
	 */
	public static final short AK_PLAYER_LOGIN_CHECK_CLIENT = 1004;
	/**
	 * 玩家登录验证返回
	 */
	public static final short AK_PLAYER_LOGIN_CHECK_SERVER = 1005;
	/**
	 * 玩家登录完成
	 */
	public static final short AK_PLAYER_LOGIN_FINISH_CLIENT = 1006;
	/**
	 * 玩家登出完成
	 */
	public static final short AK_PLAYER_LOGOUT_FINISH_CLIENT = 1007;
	/**
	 * 初始化player的memcached
	 */
	public static final short AK_INIT_PLAYER_MEMCACHED_CLIENT = 1008;
	/**
	 * 初始化player的memcached返回
	 */
	public static final short AK_INIT_PLAYER_MEMCACHED_SERVER = 1009;
	/**
	 * 初始化playerSns的memcached
	 */
	public static final short AK_INIT_PLAYERSNS_MEMCACHED_CLIENT = 1010;
	/**
	 * 初始化playerSns的memcached返回
	 */
	public static final short AK_INIT_PLAYERSNS_MEMCACHED_SERVER = 1011;
	/**
	 * 保存playersns(玩家自己的)
	 */
	public static final short AK_SAVE_PLAYERSNS_SELF_CLIENT = 1012;
	/**
	 * 保存playersns返回(玩家自己的)
	 */
	public static final short AK_SAVE_PLAYERSNS_SELF_SERVER = 1013;
	/**
	 * 保存playersns(好友的数据)
	 */
	public static final short AK_SAVE_PLAYERSNS_OTHER_CLIENT = 1014;
	/**
	 * 保存playersns返回(好友的数据)
	 */
	public static final short AK_SAVE_PLAYERSNS_OTHER_SERVER = 1015;
	/**
	 * 加载player的当前摇钱树状态
	 */
	public static final short AK_LOAD_PLAYERSNS_SELF_CLIENT = 1016;
	/**
	 * 加载player的当前摇钱树状态（返回）
	 */
	public static final short AK_LOAD_PLAYERSNS_SELF_SERVER = 1017;
}
