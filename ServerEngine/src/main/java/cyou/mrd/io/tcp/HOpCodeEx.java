package cyou.mrd.io.tcp;

import java.util.HashMap;
import java.util.Map;

import cyou.mrd.io.http.HOpCode;
/**
 * 项目协议号<1001-n>
 * 
 * @author Administrator
 *
 */
public class HOpCodeEx extends HOpCode {
	/**
	 * client-->server 客户端回复收到邮件
	 * opcode:77
	 * data:
	 * 		mailIds:
	 * 			ID:xxxxx int           
	 * eg.{"opcode":77,"data":{"mailIds":[{ID:30},{ID:31}]}}
	 */
	public static final short MAIL_REPLY_LIST_CLIENT = 77;
	
	/**
	 * server-->client   客户端回复收到邮件返回
	 * opcode:78
	 * data:
	 * 		result:xxx       int  （1成功，0失败）
	 * eg.{"opcode":78,"data":{"result":1}}
	 */
	public static final short MAIL_REPLY_LIST_SERVER = 78;
	/**
	 * client-->server 获取邮件的附件奖励
	 * opcode:79
	 * data:  
	 * 		mailId:xxx		int		邮件id
	 * eg.{"opcode":79,"data":{"mailId":30}}
	 */
	public static final short MAIL_GET_GOODS_CLIENT = 79;
	
	/**
	 * server-->client   获取邮件的附件奖励返回
	 * opcode:80
	 * data:
	 * 		result:xxx       int  （1成功，0失败）
	 * 		mailId:xxx     int 邮件id
	 * 		gameAwardId:xxx     int 奖励id
	 * eg.{"opcode":80,"data":{"result":1,"mailId":1,"gameAwardId":1}}
	 */
	public static final short MAIL_GET_GOODS_SERVER = 80;
	
	/*******************日志回馈的统一入口*************************/
	
	/**
	 * client-->server 日志的回馈
	 * opcode:81
	 * data:  
	 * 		optLogId:xxx		int		日志id
	 * eg.{"opcode":81,"data":{"optLogId":30}}
	 */
	public static final short USER_OPT_LOG_BACK_CLIENT = 81;
	
	/**
	 * server-->client   日志回馈的返回
	 * opcode:82
	 * data:
	 * 		result:xxx       int  （1成功，0失败）
	 * eg.{"opcode":82,"data":{"result":1}}
	 */
	public static final short USER_OPT_LOG_BACK_SERVER = 82;
	/**
	 * server-->client 发送日志
	 * opcode:83
	 * data:  
	 * 		optLogId:xxx		int		日志id
	 * eg.{"opcode":83,"data":{"optLogId":30}}
	 */
	public static final short USER_OPT_LOG_CLIENT = 83;
	/**
	 * client-->server 公告列表
	 * opcode:85
	 * eg.{"opcode":85}
	 */
	public static final short NOTICE_LIST_CLIENT = 85;
	/**
	 * server-->client 公告列表返回
	 * opcode:86
	 * data:
	 * 		noticeList是一个数组
	 * 			noticeId:xxx		int		公告id
	 * 			content:xxxx		String  内容
	 * 			addTime:xxxx		int 时间
	 * eg.{"opcode":86,"data":{"noticeList":[{"noticeId":1,"content","xxxxx","addTime",11111},{}]}}
	 */
	public static final short NOTICE_LIST_SERVER = 86;
	/**
	 * client-->server 删除公告
	 * opcode:87
	 * data:
	 * 		noticeIds:[1,2,3,4]
	 * eg.{"opcode":87,"data":{"noticeIds":[1,2,3,4]}}
	 */
	public static final short NOTICE_DEL_CLIENT = 87;
	/**
	 * server-->client 删除公告返回
	 * opcode:88
	 * data:
	 * 		result:xxx    int 返回结果(1成功，0失败)
	 * eg.{"opcode":88,"data":{"result":1}}
	 */
	public static final short NOTICE_DEL_SERVER = 88;
	/**
	 * client-->server 获取交互公告列表
	 * opcode:89
	 * eg.{"opcode":89}
	 */
	public static final short INTERACTIVE_NOTICE_LIST_CLIENT = 89;
	/**
	 * server-->client 获取交互公告列表返回
	 * opcode:90
	 * data:
	 * 		noticeList是一个数组
	 * 			noticeId:xxx		int		公告id
	 * 			templateId:xxxx		int  模版id 对应于字典表
	 * eg.{"opcode":89,"data":{"noticeList":[{"noticeId":1,"templateId",3201},{}]}}
	 */
	public static final short INTERACTIVE_NOTICE_LIST_SERVER = 90;
	
	
	/**
	 * 定义的http协议必须大于1001
	 */
	public static final short HTTP_OPCODE_MIN_VALUE = 1001;
	
	/**
	 * 清理好友房间<br>
	 * client-->server<br>
	 * opcode:1002<br>
	 * data:<br>
	 * 		friendId:xxx   int   好友id<br>
	 * eg{"opcode":1002,"data":{"friendId":123}}<br>
	 */
	public static final short FRIEND_CLEAR_HOME_CLIENT = 1002;
	
	/**
	 * 返回清理好友房间<br>
	 *  server-->client 清理成功奖励<br>
	 * opcode:1003<br>
	 * data:<br>
	 * 		money:xxx   int   奖励金钱<br>
	 * eg{"opcode":1003,"data":{"money":123}}<br>
	 */
	public static final short FRIEND_CLEAR_HOME_SERVER = 1003;
	
	/**
	 * TODO
	 * 挖宝好友房间<br>
	 * client-->server<br>
	 * opcode:1002<br>
	 * data:<br>
	 * 		friendId:xxx   int   好友id<br>
	 * eg{"opcode":1004,"data":{"friendId":123}}<br>
	 */
	public static final short FRIEND_DIG_HOME_CLIENT = 1004;
	
	/**
	 * TODO
	 * 返回挖宝好友房间<br>
	 *  server-->client 挖宝成功<br>
	 * opcode:1003<br>
	 * data:<br>
	 * eg{"opcode":1003,"data":{}}<br>
	 */
	public static final short FRIEND_DIG_HOME_SERVER = 1005;
	
	/**
	 * TODO
	 * 挖宝<br>
	 * client-->server<br>
	 * opcode:1002<br>
	 * data:<br>
	 * eg{"opcode":1006,"data":{}}<br>
	 */
	public static final short PLAYER_DIG_CLIENT = 1006;
	/**
	 * TODO
	 * 挖宝<br>
	 * client-->server<br>
	 * opcode:1002<br>
	 * data:<br>
	 * eg{"opcode":1006,"data":{}}<br>
	 */
	public static final short PLAYER_DIG_SERVER = 1007;
	
	/**
	 * 运送<br>
	 * client-->server<br>
	 * opcode:1008<br>
	 * data:<br>
	 * eg{"opcode":1008,"data":{"friendId":123}}<br>
	 */
	public static final short FRIEND_TRANSPORT_CLIENT = 1008;
	/**
	 * 返回运送<br>
	 * client-->server<br>
	 * opcode:1009<br>
	 * data:<br>
	 * eg{"opcode":1009,"data":{}}<br>
	 */
	public static final short FRIEND_TRANSPORT_SERVER = 1009;
	
	/**
	 * 验证元宝<br>
	 * client-->server<br>
	 * opcode:1010<br>
	 * data:<br>
	 * eg{"opcode":1010,"data":{"Dollar":10, quickBuilding:12, "items":[{"id"=1}, {"id"=2}]}}<br>
	 */
	public static final short PLAYER_SYNC_DOLLAR_CLIENT = 1010;
	/**
	 * 返回验证元宝<br>
	 * client-->server<br>
	 * opcode:1011<br>
	 * data:<br>
	 * eg{"opcode":1011,"data":{dollar:10}}<br>
	 */
	public static final short PLAYER_SYNC_DOLLAR_SERVER = 1011;

	/**
	 * 设置头像<br>
	 * client-->server<br>
	 * opcode:1012<br>
	 * data:<br>
	 * eg{"opcode":1012,"data":{"icon":"12_9_3"}}<br>
	 */
	public static final short PLAYER_ICON_CLIENT = 1012;
	/**
	 * 设置头像<br>
	 * client-->server<br>
	 * opcode:1013<br>
	 * data:<br>
	 * eg{"opcode":1013,"data":{"result":"ok"}}<br>
	 */
	public static final short PLAYER_ICON_SERVER = 1013;
	
	/**
	 * 设置搬家密码<br>
	 * client-->server<br>
	 * opcode:1014<br>
	 * data:<br>
	 * eg{"opcode":1014,"data":{"pwd":"abcd1234"}}<br>
	 */
	public static final short PLAYER_HOUSE_PASSWORD_CLIENT = 1014;
	/**
	 * 设置搬家密码<br>
	 * client-->server<br>
	 * opcode:1015<br>
	 * data:<br>
	 * eg{"opcode":1015,"data":{"result":"ok"}}<br>
	 */
	public static final short PLAYER_HOUSE_PASSWORD_SERVER = 1015;
	
	/**
	 * 搬家<br>
	 * client-->server<br>
	 * opcode:1016<br>
	 * data:<br>
	 * eg{"opcode":1016,"data":{id:123, "pwd":"abcd1234"}}<br>
	 */
	public static final short PLAYER_MOVE_HOUSE_CLIENT = 1016;
	/**
	 *  搬家<br>
	 * server-->client<br>
	 * opcode:1017<br>
	 * data:<br>
	 * eg{"opcode":1017,"data":{"result":"ok"}}<br>
	 */
	public static final short PLAYER_MOVE_HOUSE_SERVER = 1017;
	
	/**
	 * 上传进度<br>
	 * client-->server<br>
	 * opcode:1020<br>
	 * data:<br>
	 * eg{{"opcode":1020,"data":{<br>
	 * <br>
	 * "save_time": 8978278178<br>
	 * <br>
	 * "LocalTime": 1333251822,<br>
	 * <br>
	 * "verify":
	 * {"BuyDollar":20,"CompensateDollar":0,"RemainDollar":16,"RewardDollar"
	 * :6,"UseDollar":10},<br>
	 * <br>
	 * <br>
	 * "player":<br>
	 * {<br>
	 * "star":1,"lang":1, "Nickname":"aaa", "icon":"2", <br>
	 * "Dollar":200,<br>
	 * "Exp":0,"Level":1,"Money":50000,"Energy":100,
	 * "Rabit0Number":6,"Rabit1Number":6,<br>
	 * "Cats":[{"count":0,"id":12}],<br>
	 * "BattleTimes":0,"BattleWinTimes":0,<br>
	 * "Woods":100,<br>
	 * "OpenBlock":0, <br>
	 * "Items":"7=10,8=20"<br>
	 * }, <br>
	 * <br>
	 * "Buildings":<br>
	 * [<br>
	 * {"x":7,"y":9,"ID":48,"flip":0,
	 * "State":0,"StateTime":0,"CutTimes":0,"GrowTimes"
	 * :0},{"x":12,"y":37,"ID":58,"flip":0,
	 * "State":0,"StateTime":0,"CutTimes":0,"GrowTimes":0},<br>
	 * {"x":20,"y":33,"ID":83,"flip":0,
	 * "State":0,"StateTime":0,"CutTimes":0,"GrowTimes"
	 * :0},{"x":20,"y":32,"ID":83,"flip":0,
	 * "State":0,"StateTime":0,"CutTimes":0,"GrowTimes":0}<br>
	 * ]<br>
	 * <br>
	 * "Mission":[<br>
	 * {"ID":2,"State":99},<br>
	 * {"ID":3,"State":99},<br>
	 * {"ID":4,"State":99},<br>
	 * {"ID":5,"State":99},<br>
	 * {"ID":8,"State":99},<br>
	 * {"ID":9,"State":99},<br>
	 * {"ID":10,"State":99},<br>
	 * {"ID":11,"State":99},<br>
	 * {"ID":12,"State":2,"Condition":1,"building":"1=1,2=20","item":
	 * "[2=10,2=100]","StartTime":11111,"TimeLong":120,"BattleWinTimes":12}<br>
	 * ]<br>
	 * <br>
	 * }}}<br>
	 */
	public static final short PLAYER_UPLOAD_CLIENT = 1020;
	/**
	 *  返回上传进度<br>
	 *server-->client<br>
	 * opcode:1021<br>
	 * data:<br>
	 * eg{"opcode":1021, "data"{"dollar":"1234/0", "others":"1/0", }}
	 *<br>
	 */
	public static final short PLAYER_UPLOAD_SERVER = 1021;
	
	/**
	 * 特殊好友<br>
	 * client-->server<br>
	 * opcode:1020<br>
	 * data:<br>
	 * eg{"opcode":1020,"data":{type:"level"}}<br>
	 */
	public static final short PLAYER_SPECIAL_FRIEND_CLIENT = 1022;
	/**
	 *  返回上传进度<br>
	 * server-->client<br>
	 * opcode:1021<br>
	 * data:<br>
	 * eg{"opcode":1021, "data"{"result":"OK"}}
	 *<br>
	 */
	public static final short PLAYER_SPECIAL_FRIEND_SERVER = 1023;
	 
	/**
	 * 恢复进度
	 * client-->server<br>
	 * opcode:1024<br>
	 * data:<br>
	 * eg{"opcode":1024,"data":{}}<br>
	 */
	public static final short PLAYER_DOWNLOAD_CLIENT = 1024;
	/**
	 * 恢复进度
	 * client-->server<br>
	 * opcode:1025<br>
	 * data:<br>
	 * eg{"opcode":1025,"data":{AKGameSave.sav}}<br>
	 */
	public static final short PLAYER_DOWNLOAD_SERVER = 1025;
	
	/**
	 * 随机好友<br>
	 * client-->server<br>
	 * opcode:1026<br>
	 * data:<br>
	 * eg{"opcode":1026,"data":{"level":20}}<br>
	 */
	public static final short PLAYER_RANDOM_FRIEND_CLIENT = 1026;
	/**
	 *  随机好友<br>
	 *server-->client<br>
	 * opcode:1027<br>
	 * data:<br>
	 * eg{"opcode":1027, "data"{"list":[{id:1,name:"fdsa" level:12, icon:"fds.png"},{{id:2,name:"fdsa" level:12, icon:"fds.png"}}]}}
	 *<br>
	 */
	public static final short PLAYER_RANDOM_FRIEND_SERVER = 1027;

	/**
	 * client-->server<br>
	 * opcode:1028<br>
	 * data:<br>
	 * eg{"opcode":1028,"data":{{"LogRoot":[{"Log":[{"do":"EnterGame","tm":0,"ty":15},
	 * {"do":"main_UI_003#Button_connet","tm":15,"ty":2},{"Log":[{"do":"EnterGame","tm":0,"ty":15}],"StartTM":1343790622}]}}<br> 
	 */
	public static final short CLIENT_OP_LOG = 1028;
	/**
	 * client-->server<br>
	 * opcode:1028<br>
	 * data:<br>
	 * eg{"opcode":1028,"data":{"ok":1}}<br> 
	 */
	public static final short SERVER_OP_LOG = 1029;
	
	/**
	 * 战斗，请求好友支援<br>
	 * client-->server<br>
	 * opcode:1030<br>
	 * data:<br>
	 * eg{"opcode":1030,"data":{"id":1,"time":0}}
	 */
	public static final short CLIENT_REQUST_SUPPORT = 1030;
	/**
	 * 战斗，回复好友支援<可以或者不可以><br>
	 * server-->client<br>
	 * opcode:1031<br>
	 * data:<br>
	 * eg{"opcode":1031,"data":{}}
	 */
	public static final short SERVER_REQUST_SUPPORT = 1031;
	
	/**
	 * 战斗，来自好友请求的列表<br>
	 * client-->server<br>
	 * opcode:1032<br>
	 * data:<br>
	 * eg{"opcode":1032,"data":{""}}
	 */
	public static final short CLIENT_SUPPORT_REQUESTLIST = 1032;
	/**
	 * 战斗，好友请求列表发给客户端<br>
	 * server-->client<br>
	 * opcode:1033<br>
	 * data:<br>
	 * eg{"opcode":1033,"data":{}}
	 */
	public static final short SERVER_SUPPORT_REQUESTLIST = 1033;
	
	/**
	 * 战斗，同意请求<br>
	 * client-->server<br>
	 * opcode:1034<br>
	 * data:<br>
	 * eg{"opcode":1034,"data":{""}}
	 */
	public static final short CLIENT_ANSWER_SUPPORT = 1034;
	/**
	 * 战斗，同意请求回报<br>
	 * server-->client<br>
	 * opcode:1035<br>
	 * data:<br>
	 * eg{"opcode":1035,"data":{}}
	 */
	public static final short SERVER_ANSWER_SUPPORT = 1035;
	
	/**
	 * 战斗，请求是否有战斗支援<br>
	 * client-->server<br>
	 * opcode:1035<br>
	 * data:<br>
	 * eg{"opcode":1035,"data":{""}}
	 */
	public static final short CLIENT_HAD_SUPPORT = 1036;
	/**
	 * 战斗，<br>
	 * server-->client<br>
	 * opcode:1037<br>
	 * data:<br>
	 * eg{"opcode":1037,"data":{}}
	 */
	public static final short SERVER_HAD_SUPPORT = 1037;
	
	//以下为摇钱树接口，在1050-1060之间
	/**
	 * 更新摇钱树等级，坐标信息<br/>
	 * client-->server<br>
	 * opcode:1050<br>
	 * data:<br>
	 * eg{"opcode":1050,"data":{"gradeId":11,"x":40,"y":40}}
	 */
	public static final short CLIENT_UPDATE_MONEY_TREE = 1050;
	
	/**
	 * 更新摇钱树等级，坐标信息 服务器返回
	 * server-->client<br>
	 * opcode:1051<br>
	 * data:<br>
	 * eg{"opcode":1051,"data":{"result":1,0}}
	 */
	public static final short SERVER_UPDATE_MONEY_TREE = 1051;
	
	/**
	 * 请求显示摇钱树面板，详细说明见协议文档，下同
	 */
	public static final short CLIENT_SHOW_MONEY_TREE = 1052;
	
	public static final short SERVER_SHOW_MONEY_TREE = 1053;
	
	/**
	 * 好友浇水
	 */
	public static final short CLIENT_WATER_MONEY_TREE = 1054;
	
	public static final short SERVER_WATER_MONEY_TREE = 1055;
	
	/**
	 * 主人收获
	 */
	public static final short CLIENT_HARVEST_MONEY_TREE = 1056;
	
	public static final short SERVER_HARVEST_MONEY_TREE = 1057;
	
	//市场模块1060开始
	/**
	 * 同步格子数据
	 */
	public static final short CLIENT_SYNC_POS_DATA = 1060;
	
	public static final short SERVER_SYNC_POS_DATA = 1061;
	
	/**
	 * 显示自己的市场列表
	 */
	public static final short CLIENT_SHOW_MARKET = 1062;
	
	public static final short SERVER_SHOW_MARKET = 1063;
	
	/**
	 * 搜索显示的市场列表(按好友ID，按物品ID)
	 */
	public static final short CLIENT_QUERY_MARKET = 1064;
	
	public static final short SERVER_QUERY_MARKET = 1065;
	
	/**
	 * 上架操作
	 */
	public static final short CLIENT_UPLOAD_MARKET = 1066;
	
	public static final short SERVER_UPLOAD_MARKET = 1067;
	
	/**
	 * 下架操作
	 */
	public static final short CLIENT_DOWN_MARKET = 1068;
	
	public static final short SERVER_DOWN_MARKET = 1069;
	
	/**
	 * 收取金币（卖方获得）
	 */
	public static final short CLIENT_COLLECT_MARKET = 1070;
	
	public static final short SERVER_COLLECT_MARKET = 1071;
	
	/**
	 * 购买操作
	 */
	public static final short CLIENT_BUY_MARKET = 1072;
	
	public static final short SERVER_BUY_MARKET = 1073;
	
	/**
	 * 好友开启新格子(已不用，改为系统自动开启)
	 */
	public static final short CLIENT_FRIEND_OPEN_MARKET = 1074;
	
	public static final short SERVER_FRIEND_OPEN_MARKET = 1075;
	
	/**
	 * 获取回收金币
	 */
	public static final short CLIENT_RECYCLE_MARKET = 1076;
	
	public static final short SERVER_RECYCLE_MARKET = 1077;
	
	
	/**
	 * client-->server   刷新爱心值
	 * opcode:1100
	 * eg.{"opcode":1100}
	 */
	public static final short CLIENT_REFRESH_LOVE = 1100;
	/**
	 * server-->client   刷新爱心值返回
	 * opcode:1101
	 * data:
	 * 		result:xxx       int  （1成功，0失败）
	 * 		love:xxx       int  爱心值
	 * eg.{"opcode":1101,"data":{"result":1,"love":100}}
	 */
	public static final short SERVER_REFRESH_LOVE = 1101;
	/**
	 * client-->server   用爱心值购买商品
	 * opcode:1102
	 * data:  
	 * 	  shopId:xxx		int		商品id
	 * eg.{"opcode":1102,"data":{"shopId":1}}
	 */
	public static final short CLIENT_BUY_SHOPITEM_ONLOVE = 1102;
	/**
	 * server-->client   用爱心值购买返回
	 * opcode:1103
	 * data:
	 * 		result:xxx       int  （1购买成功，0购买失败）
	 * 		love:xxx       int  （当前爱心值）
	 * eg.{"opcode":1103,"data":{"result":1,"love":1000}}
	 */
	public static final short SERVER_BUY_SHOPITEM_ONLOVE = 1103;
	/**
	 * 给多个好友送礼物
	 */
	public static final short MULTI_FRIEND_TRANSPORT_CLIENT = 1104;
	/**
	 * 给多个好友送礼物返回
	 */
	public static final short MULTI_FRIEND_TRANSPORT_SERVER = 1105;
	/**
	 * 点击出现的npc
	 */
	public static final short CLICK_NPC_CLIENT = 1106;
	/**
	 * 点击出现的npc返回
	 */
	public static final short CLICK_NPC_SERVER = 1107;
	/**
	 * 获取玩家好友的排行
	 */
	public static final short FRIEND_RANK_CLIENT = 1108;
	/**
	 * 获取玩家好友的排行返回
	 */
	public static final short FRIEND_RANK_SERVER = 1109;
	/**
	 * 获取总的排行
	 */
	public static final short RANK_CLIENT = 1110;
	/**
	 * 获取总的排行返回
	 */
	public static final short RANK_SERVER = 1111;
	/**
	 * 贸易，请求帮助
	 */
	public static final short TRADE_REQUEST_HELP_CLIENT = 1112;
	/**
	 * 贸易，请求帮助返回
	 */
	public static final short TRADE_REQUEST_HELP_SERVER = 1113;
	/**
	 * 贸易，帮助
	 */
	public static final short TRADE_HELP_CLIENT = 1114;
	/**
	 * 贸易，帮助返回
	 */
	public static final short TRADE_HELP_SERVER = 1115;
	/**
	 * 贸易，设置空闲
	 */
	public static final short TRADE_SET_FREE_CLIENT = 1116;
	/**
	 * 贸易，设置空闲返回
	 */
	public static final short TRADE_SET_FREE_SERVER = 1117;
	/**
	 * 贸易，获取自己的贸易列表
	 */
	public static final short TRADE_MY_LIST_CLIENT = 1118;
	/**
	 * 贸易，获取自己的贸易列表返回
	 */
	public static final short TRADE_MY_LIST_SERVER = 1119;
	/**
	 * 贸易，获取好友的贸易列表
	 */
	public static final short TRADE_FRIEND_LIST_CLIENT = 1120;
	/**
	 * 贸易，获取好友的贸易列表返回
	 */
	public static final short TRADE_FRIEND_LIST_SERVER = 1121;
	/**
	 * 不锁定的操作
	 */
	private static Map<Short, Boolean> unLockOpCodeArray = new HashMap<Short, Boolean>();
	static {
		unLockOpCodeArray.put(HEART_CLIENT, true);
	}
	/**
	 * 是否是锁定用户的操作
	 * @param opCode
	 * @return
	 */
	public static boolean isLockPlayerOpCode(Short opCode){
		if(unLockOpCodeArray.get(opCode) != null && unLockOpCodeArray.get(opCode) == true){
			return false;
		}
		return true;
	}
}
