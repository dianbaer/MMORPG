package cyou.mrd.io.tcp;

public class OpCode {	

	
	
	/**
	 * 错误
	 * serial					int
	 * type						short
	 * message					string
	 */
	public static final short ERROR = -1;
	
	/**
	 * 断线
	 */
	public static final short DISCONNECTED = 0;
	
	/**
	 * string name
	 * string password;
	 */
	public static final short WORLD_LOGIN_CLIENT = 1;
	
	/**
	 * 
	 */
	public static final short WORLD_LOGIN_SERVER = 2;
	
	/**
	 * byte serverId
	 * int playerId
	 */
	public static final short PLAYER_LOGIN_CLIENT = 3;

	/**
	 * 积累到一定程度时,web服务器通知世界服务器更新session
	 * byte serverId
	 * int changedNmu
	 */
	public static final short SESSION_CHANGED_CLIENT = 4;
	
	/**
	 * 世界服务器通知更新session
	 */
	public static final short SESSION_CHANGED_SERVER = 5;

	/**
	 * 时间同步
	 * long time, 毫秒
	 */
	public static final short WORLD_SYNC_TIME_SERVER = 6;
	
	/**
	 * 时间同步成功
	 * byte
	 */
	public static final short WORLD_SYNC_TIME_CLIENT = 7;

	/**
	 * 通知玩家信息修改
	 * int playerId
	 */
	public static final short PLAYER_CHANGED_CLIENT = 8;
	
	
	
	
	//与付费服务器通信    21--50
	/**
	 * World 登陆
	 * client--->server
	 * 		gamecode:xxx	String   游戏码
	 * 		passward:xxx	String   密码
	 */
	public static final short BILLING_LOGING_CLIENT = 21;
	
	/**
	 *  World 登陆返回
	 *  server--->client
	 *  	worldId:xxx	int			登陆状态
	 */
	public static final short BILLING_LOGING_SERVER = 22;
	
	
	/**
	 * 请求校验账单 world 到billing
	 * client--->server
	 * 		gamecode:xxx	String      游戏码
	 * 		serverId:xxx	int			GameServerId
	 * 		accountid:xxx	int			账号id
	 * 		playerid:xxx	int			角色id
	 * 		serial:xxx		long		流水号
	 * 		bid:xxx			String      应用bandle id
	 * 		pid:xxx			String		商品的id
	 * 		receipt:xxx		String		单据信息
	 * eg.{}
	 */
	public static final short BILLING_VERIFY_CLIENT = 23;
	
	/**
	 * 账单验证结果 billing 到 world
	 * server--->client
	 * 		result:xxx			int       结果（-1：失败     1：成功）
	 * 		accountId:xxx		int		     需要充值的账号id
	 * 		playerId;xxx		int
	 * 		imoney:xxx			int		     充值金额
	 * 		type:xxx			int		     商品类型
	 * 		extraImoneyRatio:xxx int		     商品促销比例
	 * eg.{}
	 */
	public static final short BILLING_VERIFY_SERVER = 24;
	
	/**
	 * 请求校验账单 GameServer 到  world
	 * client--->server
	 * 		gamecode:xxx	String      游戏码
	 * 		serialNum:xxx	Long		流水号
	 * 		serverId:xxx	int			GameServerId
	 * 		accountId:xxx	int			账号id
	 * 		playerId:xxx	int			角色id
	 * 		bid:xxx			String      应用bandle id
	 * 		pid:xxx			String		商品的id
	 * 		receipt:xxx		String		单据信息
	 * eg.{}
	 */
	public static final short WORLD_VERIFY_CLIENT = 27;
	
	/**
	 * 账单验证结果  world 到 GameServer
	 * server--->client
	 * 		result:xxx			int       结果（<0：失败     >=0：成功   为充值的金额）
	 * 		rmb:xxx				int		     充值人民币数
	 * 		accountId:xxx		int		     需要充值的账号id
	 * 		serialNum:xxx		long	    流水号
	 * eg.{}
	 */
	
	public static final short WORLD_VERIFY_SERVER = 28;
	/**
	 * World与 billingServer之间的心跳包
	 * client--->server
	 * 		worldId:xxx		int
	 * eg.{}
	 */
	public static final short BILLING_HEART_CLIENT = 25;
	
	/**
	 * World与 billingServer之间的心跳包
	 * server--->client
	 * eg.{}
	 */
	public static final short BILLING_HEART_SERVER = 26;
	
	
	/**
	 * GameServer 向world请求商品列表(优化：不再需要playerId,bid)
	 * gameserver-->world
	 */
	public static final short SERVER_REQUEST_PRODUCT_CLIENT = 34;

	/**
	 * world 回复GameServer应用商品
	 * world --> gameserver
	 * 		size:int						数量
	 * 		(循环size次)
	 * 	 		id:xxx			String      商品id
     * 			name:xxx		String      商品name
     * 			des:xxx			String		商品描述
     * 			price:xxx		int		 	人民币价值
     * 			demon:xxx		int			游戏内价格
     * 			icon:xxx		String		图标
     * 			AD				int			广告时间
     * 			type			int 		商品类型
     * 			extraImoneyRatio		int		促销加成比例
	 */
	public static final short SERVER_REQUEST_PRODUCT_SERVER = 29;
	
	/**
	 * world 向billingServer请求商品列表
	 * world-->billingServer(serverId,playerId,bid废弃)
	 */
	public static final short WORLD_REQUEST_PRODUCT_CLIENT = 30;

	/**
	 * billingServer 回复world应用列表
	 * billingServer --> world
	 * 		size:int			数量
	 * 		(循环size次)
	 * 	 		id:xxx			String      商品id
     * 			name:xxx		String      商品name
     * 			des:xxx			String		商品描述
     * 			price:xxx		int		 	人民币价值
     * 			demon:xxx		int			游戏内价格
     * 			icon:xxx		String		图标
     * 			AD				int			广告时间
     * 			type			int 		商品类型
     * 			extraImoneyRatio		int		促销加成比例
	 */
	public static final short WORLD_REQUEST_PRODUCT_SERVER = 31;
	
	
	/**
	 * server 请求world连接状态
	 * int serverId
	 */
	public static final short SERVER_REQUEST_WORLD_CLIENT = 32;
	/**
	 * world 回复server连接状态
	 * int    billingState       0:断线     1：正常
	 * String retStr			 gameServer数，serverId.......
	 */
	public static final short SERVER_REQUEST_WORLD_SERVER = 33;
	/**
	 * world 请求billing连接状态
	 * int     serverId
	 */
	public static final short WORLD_REQUEST_BILLING_CLIENT = 35;
	
	/**
	 * billing 回复world连接状态
	 * int serverId
	 */
	public static final short WORLD_REQUEST_BILLING_SERVER = 36;
	
	
	
	
	//
	public static final short WORLD_LOGIN_C = 30000;
	public static final short WORLD_LOGIN_S = 30001;
	//再连world服务器
	public static final short WORLD_AGAIN_CONNECT_S = 30002;
	
	//用户登录
	public static final short WORLD_PLAYER_LOGIN_C = 30003;
	
	//通知用户下线
	public static final short WORLD_KICK_PLAYER_S = 30004;
	//通知用户可以正常登录了
	public static final short WORLD_PLAYER_CAN_LOGIN_S = 30005;
	//用户下线了
	public static final short WORLD_PLAYER_LOGOUT_C = 30006;
	//通知用户重新登录
	public static final short WORLD_PLAYER_RELOGIN_S = 30007;
	
	
}