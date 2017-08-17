package cyou.mrd.data;


public class DataKeys {
	/**
	 * 玩家key = key + player.name
	 * value = player
	 */
	private static final String KEY_PLAYER = "player_";
	/**
	 * 玩家key = key + player.name
	 * value = player
	 */
	private static final String KEY_BUILDING = "building_";
	/**
	 * key = session列表
	 * value = TObjectIntMap<String> tSessionTable
	 * 后面需要跟<0-9>
	 */
	private static final String SESSION_TABLE_KEY = "session_table_key_";
	
	/**
	 * key = key + keyWord
	 * value = int
	 */
	private static final String KEY_KEYWORD = "keyworld_";
	
	/**
	 * 玩家好友关系key = key + relation.id
	 * value = relation
	 */
	private static final String KEY_RELATION = "relation_";
	
	/**
	 * 系统公告邮件id列表
	 * value = []
	 */
	private static final String KEY_SYSTEM_NOTICE = "mail_notice";
	
	/**
	 * battle Support key = key + player.name
	 * value = player
	 */
	private static final String KEY_BATTLESUPPORT = "BATTLESUPPORT_";
	
//	/**
//	 * 系统公告邮件id列表
//	 * value = []
//	 */
//	private static final String KEY_SYSTEM_NOTICE_NUM = "mail_notice_ids";
//	
//	/**
//	 * 系统公告邮件内容
//	 * value = relation
//	 */
//	private static final String KEY_SYSTEM_NOTICE_CONTENT = "_notice_";

	/**
	 * 系统补钱key。
	 * value = []
	 */
	private static final String KEY_SYSTEM_COMPENSATE = "gm_compensate";
	
	public static String playerKey(int playerId) {
		return KEY_PLAYER + playerId;
	}

	public static String playerBuildingKey(int playerId) {
		return KEY_BUILDING + playerId;
	}
	
	public static String sessionTableKey(int index) {
		return SESSION_TABLE_KEY + index;
	}
	
	public static String keyWordKey(String keyWord){
		return KEY_KEYWORD + keyWord;
	}

	public static String relationKey(int relationId) {
		return KEY_RELATION + relationId;
	}
	
	public static String battleSupportKey(int playerID) {
		return KEY_BATTLESUPPORT + playerID;
	}

//	public static String systemNoticeIds() {
//		return KEY_SYSTEM_NOTICE_NUM;
//	}
//
//	public static String systemNoticeContent(int noticeNum) {
//		return KEY_SYSTEM_NOTICE_CONTENT + noticeNum;
//	}
	
	public static String compensateKey() {
		return KEY_SYSTEM_COMPENSATE;
	}
	
	public static String systemNotices() {
		return KEY_SYSTEM_NOTICE;
	}

}
