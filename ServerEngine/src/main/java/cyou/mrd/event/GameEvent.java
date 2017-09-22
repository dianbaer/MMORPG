package cyou.mrd.event;

/**
 * 事件
 * 
 * @author mengpeng
 */
public class GameEvent {

	//1---1000   引擎保留
	
	/**
	 * 测试事件。参数：无
	 */
	public static final int EVENT_TEST = 101;

	/**
	 * 角色登录事件。参数：Player
	 */
	public static final int EVENT_PLAYER_LOGINED = 102;
	/**
	 * 角色登出事件。参数：Player
	 */
	public static final int EVENT_PLAYER_LOGOUTED = 103;
	/**
	 * 角色注册事件。参数：Player
	 */
	public static final int EVENT_PLAYER_CREATED = 104;
	
	/**
	 * session更新事件。参数：int 修改数量
	 */
	public static final int EVENT_SESSION_TABLE_UPDATE_CREATED = 105;
	
	/**
	 * 角色数据修改事件。参数：Player
	 */
	public static final int EVENT_PLAYER_CHANGEED = 106;

	/**
	 * 日期, 换天;
	 */
	public static final int EVENT_CHANGE_DAY = 107;
	
	/**
	 * 新增人物关系。参数：PlayerRelation
	 */
	public static final int EVENT_RELATION_ADD = 108;
	
	/**
	 * 人物关系表修改事件。参数：PlayerRelation
	 */
	public static final int EVENT_RELATION_CHANGE = 109;
	
//	/**
//	 * session过期事件。参数：Player
//	 */
//	public static final int EVENT_SESSION_TIME_OUT = 110;
	
	/**
	 * 玩家充值成功事件。参数accountId   imoney
	 */
	public static final int EVENT_ADD_IMONEY = 111;
	
	/**
	 * 角色数据强制修改事件。参数：Player
	 */
	public static final int EVENT_PLAYER_CHANGEED_FORCE = 112;
	
	
	/**
	 *	给客户端通知事件
	 *	playerId
	 */
	public static final int EVENT_NOTIFY_CLIENT = 113;
	

}
