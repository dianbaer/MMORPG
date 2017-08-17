package cyou.mrd.io.http;


/**
 * http协议<br>
 * 协议范围<1-1000><br>
 * 
 * @note http协议管理
 * 
 * @author Administrator
 * 
 */
public class HOpCode {
	/**
	 * 定义的http协议必须小于1000
	 */
	public static final short HTTP_OPCODE_MAX_VALUE = 1000;
	
	/**
	 * server--->client 错误信息返回
	 * opcode:0
	 * data:
	 * 		error:xxx
	 * eg.{"opcode":0, "data":{"error":"请重新登录"}}
	 */
	public static final short HTTP_ERROR = 0;

	/*
	 * 玩家协议1-40
	 */ 
	
	/**
	 * client---->server 登录
	 * opcode:1
	 * data:
	 * 		loginType:xxx 		String		   	(账号类型 mid:机器码    cyou:畅游码    facebook:facebook账号  kaixin:开心网账号   renren:人人网账号)
	 * 		mid:xxx   		    String
	 * 		version:xxx			double
	 * 		acId:xxx			int				账号id(若没有记录发一个-1)
	 * 	日志统计需求
	 * 		area:xxx			String			登录地区名称
	 * 		country:xxx			String			登录国家名称
	 * 		device				String			登录设备名称
	 * 		deviceSystem		String			登录系统名称
	 * 		downloadType		String			下载类型
	 * 		networkType			String			联网类型名称
	 * 		prisonBreak			int				是否越狱(0:否   1:是)
	 * 		operator			String			运营商名称
	 * eg.{"opcode":1, "data":{"loginType":"mid","mid":"yukun","acId":58,"version":1.0,"area":"其它","country":"中国","device":"iPhone3","deviceSystem":"iOS 4.x","downloadType":"AppStore","networkType":"WIFI","prisonBreak","是","operator":"中国移动"}}
	 */
	public static final short PLAYER_LOGIN_CLIENT = 1;
	/**
	 * server--->client 登录返回
	 * opcode:2
	 * data:
	 * 		id:xxx            int		玩家id(用于通信)
	 * 		acId;xxx		  int		accountId(存储在本地   登陆时发上来)
	 * 		AD:xxx			  String	对应设备的广告id
	 * eg.{"opcode":2, "data":{"id":33,"acId":34,"AD":"efasxfasfefas"}}
	 */
	public static final short PLAYER_LOGIN_SERVER = 2;
	
	/**
	 * 玩家登出<br>
	 */
	public static final short PLAYER_LOGOUT_CLIENT = 3;
	
	/**
	 * 玩家退出
	 */
	public static final short PLAYER_LOGOUT_SERVER = 4;
	
	
	/**
	 * 玩家修改昵称
	 * client-->server
	 * opcode:5
	 * data:
	 * 		name:xxx   String   昵称
	 * eg{"opcode":5,"data":{"name":"昵称"}}
	 */
	public static final short PLAYER_NICKNAME_CLIENT = 5;
	/**
	 * 玩家修改昵称
	 * server-->client
	 * opcode:6
	 * data:
	 * 		state:xxx    String  
	 * eg{"opcode":6,"data":{"state":"OK"}}
	 */
	public static final short PLAYER_NICKNAME_SERVER = 6;
	
	/**
	 * 客户端请求恢复数据
	 * client-->server
	 * opcode:7
	 * data:
	 * 		id:xxx		int     playerId;
	 * 		acId:xxx	int		acId;
	 * 	eg.{"opcode":7,"data":{"id":2,"acId":2}}
	 */
	public static final short PLAYER_RECOVER_CLIENT = 7;
	
	/**
	 * 服务器返回人数数据
	 * server-->client
	 * opcode:8
	 * data:
	 * 		time：xxx		  String    同步成功的时间
	 * 		player:
	 * 			player的数据结构
	 */
	public static final short PLAYER_RECOVER_SERVER = 8;

	/*
	 * 管理员协议41-50
	 */ 
	
	/**
	 * 管理员统计在线玩家
	 */
	public static final short ADMIN_ONLINE_PLAYERS_CLIENT = 41;
	/**
	 * 玩家列表
	 */
	public static final short ADMIN_ONLINE_PLAYERS_SERVER = 42;

	/*
	 * 好友协议51-70
	 */ 
	
	/**
	 * client-->server 玩家好友列表
	 * opcode:51
	 * data:
	 *  	id:xxx       int   玩家id
	 *  eg.{"opcode":51,"data":{"id":12}}
	 */
	public static final short PLAYER_FRIEND_LIST_CLIENT = 51;
	/**
	 * server-->client  玩家好友列表返回
	 * opcode:52
	 * data:
	 * 		id:xxx		int
	 * 		name:xxx	String
	 * 		icon:xxx    String		
	 * 		level:xxx	int
	 * eg.{"opcode":52,"data":{"friends":[{"id":1,"name":"fds","icon":"fdsa.png","level":2},{"id":1,"name":"fds","icon":"fdsa.png"}]}}
	 */
	public static final short PLAYER_FRIEND_LIST_SERVER = 52;

	/**
	 * client-->server   添加好友
	 * opcode:53
	 * data:
	 * 		id:xxx       int  好友id
	 * eg.{"opcode":53,"data":{"id":13}}
	 */
	public static final short PLAYER_FRIEND_ADD_CLIENT = 53;
	
	/**
	 * server-->client   添加好友返回
	 * opcode:54
	 * data:
	 * 		result:xxx   int (0:申请成功      1：申请失败)
	 * eg.{"opcode":54,"data":{"result":1}}
	 */
	public static final short PLAYER_FRIEND_ADD_SERVER = 54;
	
	/**
	 * client-->server   删除好友
	 * opcode:55
	 * data:
	 * 		id:xxx       int  好友id
	 * eg.{"opcode":55,"data":{"id":13}}
	 */
	public static final short PLAYER_FRIEND_DEL_CLIENT = 55;
	
	/**
	 * server-->client   删除好友返回
	 * opcode:56
	 * data:
	 * 		result:xxx   int (0:删除失败      1：删除成功)
	 * 		id:xxx		 int  删除的好友id
	 * eg.{"opcode":56,"data":{"result":1,"id":12}}
	 */
	public static final short PLAYER_FRIEND_DEL_SERVER = 56;
	
	/**
	 * client-->server   访问好友
	 * opcode:57
	 * data:
	 * 		id:xxx       int  好友id
	 * eg.{"opcode":57,"data":{"id":13}}
	 */
	public static final short PLAYER_FRIEND_HOME_CLIENT = 57;
	

	/**
	 * client-->server   添加好友<br>
	 * opcode:58<br>
	 * data:<br>
	 * 		accountId:xxx       int  好友帐号id<br>
	 * 		accountType:		int  帐号类型 1=畅游平台 2=facebook
	 * eg.{"opcode":58,"data":{"accountId:xxx":13, accountType:1}}<br>
	 */
	public static final short PLAYER_FRIEND_ADD_BY_ACCOUNT_CLIENT = 58;
	
	/**
	 * server-->client    访问好友返回
	 * opcode:58
	 * data:
	 * 		buildList:(循环n次)
	 * 			tid:xxx			int
	 * 			x:xxx			int
	 * 			y:xxx			int
	 * 			state:xxx		int
	 * eg.{"opcode":58, "data":{"buildList":[{"tid":1,"x":200,"y":300,"state":0},{"tid":2,"x":300,"y":400,"state":1}]}}
	 */
	public static final short PLAYER_FRIEND_HOME_SERVER = 58;

	/**
	 * client-->server 搜索好友
	 * opcode:59
	 * data:
	 * 		id:xxx             String
	 * eg.{"opcode":59,"data":{"id":"xtRPs"}}
	 */
	public static final short PLAYER_FRIEND_SEARCH_CLIENT = 59;
	
	/**
	 * server-->client  返回搜索好友
	 * opcode:60
	 * data:
	 * 		id:xxx
	 * 		name:xx
	 * 		level:xxx
	 * 		icon:xxx
	 * {"opcode":60,"data"{"list":[{id:1,name:"fdsa" level:12, icon:"fds.png"},{{id:2,name:"fdsa" level:12, icon:"fds.png"}}]}}
	 */
	public static final short PLAYER_FRIEND_SEARCH_SERVER = 60;
	
	
	/**
	 * client-->server  回复加好友的申请
	 * opcode:61
	 * data:
	 * 		id:xxx			int 	申请人id
	 */
	public static final short FRIEND_ADD_REPLY_CLIENT = 61;
	
	/**
	 * server-->client	回复加好友的申请处理结果
	 * opcode:62
	 * data:
	 * 		result:xxx		int		0：处理成功    1：处理失败
	 */
	public static final short FRIEND_ADD_REPLY_SERVER = 62;
	
	
	
	
	
	
	//邮件相关协议  71-90
	
	/**
	 * client-->server 发邮件
	 * opcode:71
	 * data:
	 * 		destId:xxx           int
	 * 		content:xxx			 content
	 * eg.{"opcode":71,"data":{"destId":38,"content":"邮件内容"}}
	 */
	public static final short MAIL_SEND_CLIENT = 71;
	
	/**
	 * client-->server  发邮件返回
	 * opcode:72
	 * data:
	 * 		result:xxx       int (0:发送失败  1:发送成功)
	 * eg.{"opcode":71,"data":{"result":1}}
	 */
	public static final short MAIL_SEND_SERVER = 72;
	
	/**
	 * client-->server 邮件列表
	 * opcode:73
	 * data:
	 * 		type:xxx             int  0:玩家   1:系统  2:NPC 
	 * eg.{"opcode":73,"data":{"type":1}}
	 */
	public static final short MAIL_LIST_CLIENT = 73;
	
	/**
	 * client-->server   邮件列表返回
	 * opcode:74
	 * data:
	 * 		mailList:(循环n次)
	 * 			id:xxx			int
	 * 			useType:xxx		int		0:好友消息    1：好友申请   2：其它
	 * 			sourceId:xxx	int		
	 * 			sourceName:xxx	String
	 * 			content:xxx		String
	 * 			sourceIcon:xxx	String
	 * 			sourceLevel:xxx	int
	 * 			time:xxx		String
	 * eg.{"opcode":74,"data":{"mailList":[{"id":10,"sourceId":30,"sourceName":"pipi","content":"fdsafds","time":"2011-12-21"},{}]}}
	 */
	public static final short MAIL_LIST_SERVER = 74;
	
	/**
	 * client-->server 删除邮件
	 * opcode:75
	 * data:
	 * 		mailIds:(循环n次)             
	 * 			mailId:xxx		int		邮件id
	 * eg.{"opcode":75,"data":{"mailIds":[{"mailId":30},{"mailId":31}]}}
	 */
	public static final short MAIL_DEL_CLIENT = 75;
	
	/**
	 * client-->server   删除邮件返回
	 * opcode:76
	 * data:
	 * 		resutlt:xxx       int  好友id
	 * eg.{"opcode":76,"data":{"result":1}}
	 */
	public static final short MAIL_DEL_SERVER = 76;
	
	
	//sns相关     91-->110
	/**
	 * sns绑定
	 * opcode:91
	 * data:
	 * 		type:xxx		String (得到的平台字符串)
	 * 		id:xxx			String   sns账号
	 * 		token:xxx		String   登录令牌
	 * eg.{"opcode":91,"data":{"type":4,"id":"fdsafds@126.com","token":"fdsaevveionvljfieofdsa@fdf"}}
	 */
	public static final short SNS_BINDING_CLIENT = 91;
	
	/**
	 * sns绑定返回
	 * opcode:92
	 * data:
	 * 		result:xxx		int(0:绑定失败     1:绑定成功)
	 */
	public static final short SNS_BINDING_SERVER = 92;
	
	/**
	 * sns批量加入好友
	 * opcode:93
	 * data:
	 * 		snsType:xxx			int(1:畅游   2:FB 3:开心 4:人人    5:sina微博   6:QQ)
	 * eg.{"opcode":93,"data":{"snsType":4}}
	 */
	public static final short SNS_LOADFRIEND_CLIENT = 93;
	
	/**
	 * sns批量加入好友返回
	 * opcode:94
	 * data:
	 * 		friends:循环n次         (本次增加的好友)
	 * 			id:xxx		int
	 * 			name:xxx	String
	 * 			icon:xxx    String		
	 * 			level:xxx	int
	 * 			star
	 * 			state
	 * 			isMutualn 1 
	 * 			
	 */
	public static final short SNS_LOADFRIEND_SERVER = 94;
	
	
	/**
	 * 请求校验账单
	 * client--->server
	 * 		bid:xxx			String      应用bandle id
	 * 		pid:xxx			String		商品的id
	 * 		receipt:xxx		String		Base64编码的单据信息
	 * 		tid:xxx			String		交易号
	 * eg.{"opcode":95,"data":{"bid":"com.cyou.dracula","pid":"com.cyou.dracula.productid_0.99","receipt":"eivmxlzjieowqhfewpqtojma"}}
	 */
	public static final short BILLING_VERIFY_CLIENT = 95;
	
	/**
	 * 账单验证结果
	 * server--->client
	 * 		result:xxx	 int 	      结果（<0：失败(-1:验证失败   -2:连接超时  -3:有重复的订单)>=0  ：成功  充值的金额）
	 * 		type : 0 = 钻石， 1=金币;
	 * 		tid:xxx			String			交易号
	 * eg.{}
	 */
	public static final short BILLING_VERIFY_SERVER = 96;
	
	/**
	* 请求应用的商品列表
	* client-->server
	* 	bid:xxx              String    应用的bandle id
	* eg.{"opcode":97,"data":{ "bid":"com.cyou.test.inappdemo"}}
	*/
	public static final short PRODUCE_LIST_CLIENT = 97;


    /**
     * 请求应用的商品列表返回
     * server-->client
     * 	products:(循环n次)
     * 		id:xxx			String      商品id
     * 		name:xxx		String      商品name
     * 		des:xxx			String		商品描述
     * 		price:xxx		int		 	人民币价值
     * 		demon:xxx		int			游戏内价格
     * 		icon:xxx		String		图标
     * 		noAD:xxx		int			购买后不显示广告的时间
     * eg.{}
	 */
	public static final short PRODUCE_LIST_SERVER = 98;
	
	/**
	* 查看账号id
	* client-->server
	* eg.{"opcode":99,"data":{}}
	*/
	public static final short PLAYER_ID_CLIENT = 99;

    /**
     * 查看账号id返回
     * server-->client
     * 	id:xxx			String
     * eg.{}
	 */
	public static final short PLAYER_ID_SERVER = 100;
	
	/**
	 * 上传日志
	 * client-->server
	 * 		rmbGoods:xxx    人民币道具列表
	 * 			name:xxx		String				商品名
	 * 			usage:xxx		String				用途
	 * 			amount:xxx		String				购买数量	
	 * 		goods:xxx		商品列表 循环n次 [没有日志不发或为空]
	 * 			name:xxx		String				商品名
	 * 			usage:xxx		String				用途
	 * 			amount:xxx		String				购买数量
	 * 		game:xxx		游戏内小游戏[没有日志不发或为空]
	 * 			name:xxx		String				游戏名称
	 * eg.{"opcode":101,"data":{"rmbGoods":[{"name":"民居","usage":"增加人口","amount":1}],"goods":[{"name":"民居","usage":"增加人口","amount":1}],"game":[{"name","小游戏名称"}]}
	 */
	public static final short LOG_CLIENT = 101;
	
	/**
	 * 返回上传日志
	 * server-->client
	 * 	id:xxx			String
	 * eg{"opcode":102, "data":{"result":"OK"}}
	 */
	public static final short LOG_SERVER = 102;
	
	/**
	 * 请求刷新人名币道具
	 * client-->server
	 * actId:xxx		int		账号id
	 * imoney:xxx		int		客户端充值钻石数
	 * eg.{"opcode":103, "data":{"actId":113,"imoney":100}}
	 */
	public static final short REFRESH_IMONEY_CLIENT = 103;
	
	/**
	 * 请求刷新人名币道具
	 * server-->client
	 * imoney:xxx		int		当前账号的钻石数
	 * opType:xxx		int     opType==1 不处理    opType==2回写客户端的充值金钱数
	 * eg.{"opcode":104, "data":{"diamond":200,"opType":1}}
	 */
	public static final short REFRESH_IMONEY_SERVER = 104;
	
	/**
	 * 客户端心跳包  (每5分钟发一次)
	 * client-->server
	 * eg.{"opcode":105,"data":{}}
	 */
	public static final short HEART_CLIENT = 105;
	
	/**
	 * 客户端心跳包  (每5分钟发一次)
	 * server-->client
	 * eg.{"opcode":106,"data":{}}
	 */
	public static final short HEART_SERVER = 106;
	
	/**
	 * 客户端转换语言
	 * client-->server
	 * language:xxx    int   1 汉语  2 英语 3日语  4韩语  5法语  6俄语  7西班牙语   8德语  9意大利语  10葡萄牙语  
	 * eg.{"opcode":107,"data":{language:"1"}}
	 */
	public static final short INTERNATIONAL_CLIENT = 107;
	
	/**
	 * 客户端转换语言
	 * server-->client
	 * eg.{"opcode":108,"data":{language:"1"}}
	 */
	public static final short INTERNATIONAL_SERVER = 108;
	
	
	/**
	 * 同步美元
	 * client-->server
	 * LocalTime:xxx			int     客户端时间秒数
	 * BuyDollar:xxx			int	          充值钻石数B
	 * CompensateDollar:xxx		int		管理员赠送钻石数P
	 * RemainDollar:xxx			int		剩余钻石量R
	 * RewardDollar:xxx			int		给予钻石量X
	 * UseDollar:xxx			int		消费钻书数Y
	 * InitDollar:xxx			int		游戏初始化给的钱
	 * eg.{"opcode":107,"data":{"BuyDollar":20,"CompensateDollar":0,"LocalTime":1333251822,"RemainDollar":16,"RewardDollar":6,"UseDollar":10,"InitDollar":10}}
	 */
	public static final short PLAYER_SYNC_DOLLAR_CLIENT = 109;
	
	/**
	 * 同步美元
	 * server-->client
	 * state:xxx			String    结果（OK:成功     NO:失败）
	 * eg.{"opcode":108,"data":{"state":"OK"}}
	 */
	public static final short PLAYER_SYNC_DOLLAR_SERVER = 110;
	
	/**
	 * 请求是否显示广告
	 * client-->server
	 * eg.{"opcode":111,"data":{}}
	 */
	public static final short PLAYER_NOAD_TIME_CLIENT = 111;
	
	/**
	 * 恢复是否显示广告
	 * server-->client
	 * showAD:xxx	int  0:不显示      1:显示
	 * eg.{"opcode":112,"data":{"showAD":1}}
	 */
	public static final short PLAYER_NOAD_TIME_SERVER = 112;
	
	/**
	 * 玩家修改头像
	 * client-->server
	 * opcode:113
	 * data:
	 * 		icon:xxx   String   头像
	 * eg{"opcode":113,"data":{"icon":"headico9.png"}}
	 */
	public static final short PLAYER_ICON_CLIENT = 113;
	/**
	 * 玩家修改头像
	 * server-->client
	 * opcode:6
	 * data:
	 * 		result:xxx    int     0:修改失败   1:修改成功
	 * eg{"opcode":114,"data":{"result":1}}
	 */
	public static final short PLAYER_ICON_SERVER = 114;
	

	/**
	 * 客户端与服务器加密模式不同时  让客户端修改加密模式后 重发
	 * server-->client
	 * opcode:9998
	 * data:
	 * 		isEncode:xxx		int  0:普通   1：加密
	 */
	public static final short ENCODE_MODEL_SERVER = 9998;
	
}
