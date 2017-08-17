package cyou.akworld;

import net.sf.json.JSONObject;

public class BuffInfo {
	//对速度的影响
	public static int SPEED_ADD = 1;
	//对速度的影响
	public static int SPEED_DEL = 2;
	//眩晕
	public static int DIZZY_EFFECT = 3;
	//持续类型伤害
	public static int CONTINUED_EFFECT_DAMAGE = 4;
	//不能移动
	public static int CANNOT_MOVE = 5;
	//冰箱
	public static int ICE_BOX = 6;
	//变羊
	public static int CAST = 7;
	//持续类型治疗
	public static int CONTINUED_EFFECT_TREAT = 8;
	//减伤,加伤技能
	public static int ADD_OR_SUBTRACT_DAMAGE = 9;
	//法术反射
	public static int MAGIC_REFLECT = 10;
	
	//buff数据
	public JSONObject buffData;
	//谁给的buff
	public int serverId;
	//buff开始时间
	public long startTime;
	//下一次触发掉血时间
	public long nextInterval;
}
