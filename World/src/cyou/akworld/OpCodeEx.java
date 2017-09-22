package cyou.akworld;

public class OpCodeEx {
	
	
	//场景内的消息
	public static final short ENTER_SCENE_S = 10000;
	public static final short MOVE_C = 10001;
	public static final short THING_ENTERGAME_S = 10002;
	public static final short THING_MOVE_S = 10003;
	public static final short THING_OUTGAME_S = 10004;
	public static final short ATTACK_C = 10005;
	public static final short ATTACK_S = 10006;
	public static final short OUT_SCENE_S = 10007;
	public static final short SKILL_RESULT_S = 10008;
	public static final short SKILL_DAMAGE_S = 10009;
	public static final short MONSTER_FOLLOW_TARGET_S = 10010;
	public static final short MONSTER_GO_BACK_S = 10011;
	//增加buff
	public static final short ADD_BUFF_S = 10012;
	//减少buff
	public static final short DEL_BUFF_S = 10013;
	//刷新buff
	public static final short REFRESH_BUFF_S = 10014;
	//传送
	public static final short FLASH_S = 10015;
	//死了
	public static final short DEAD_S = 10016;
	//复活
	public static final short LIFE_S = 10017;
	//某些需要时间的技能施放完成
	public static final short SKILL_COMPLETE_S = 10018;
	//读条技能完成
	public static final short READ_SKILL_COMPLETE_S = 10019;
	//取消buff
	public static final short CANCEL_BUFF_C = 10020;
	//被技能影响
	public static final short AFFECT_BY_SKILL_S = 10021;
	//飞行技能离开场景
	public static final short FLYTHING_OUTGAME_S = 10022;
	//飞行技能改变目标
	public static final short FLYTHING_CHANGE_TARGET_S = 10023;
	//无敌斩施放完成
	public static final short SKILL_OMNISLASH_COMPLETE_S = 10024;
	//无敌斩该表目标
	public static final short SKILL_OMNISLASH_CHANGE_S = 10025;
	
	//场景外的消息
	public static final short LOGIN_C = 20000;
	public static final short CHANGE_SCENE_C = 20001;
	public static final short START_LOADING_S = 20002;
	public static final short LOADING_OK_C = 20003;
	//更换服务器
	public static final short CHANGE_SERVER_S = 20004;
	//收到更换服务器
	public static final short RECEIVE_CHANGE_SERVER_C = 20005;
	//再次连接服务器
	public static final short AGAIN_CONNECT_SERVER_S = 20006;
	
	
	
}
