package
{
	public class NotiConst
	{
		public function NotiConst()
		{
		}
		//内部消息，客户端之间的消息
		/**
		 * 进入游戏
		 */
		public static const ENTER_GAME:String = "enterGame";
		public static const ADD_THING:String = "addThing";
		public static const THING_MOVE:String = "thingMove";
		public static const REMOVE_THING:String = "removeThing";
		public static const ATTACK:String = "attack";
		public static const OUT_SCENE:String = "outScene";
		public static const SKILL_RESULT:String = "skillResult";
		public static const SKILL_DAMAGE:String = "skillDamage";
		public static const MONSTER_FOLLOW_USER:String = "monsterFollowUser";
		public static const MONSTER_GO_BACK:String = "monsterGoBack";
		public static const ADD_BUFF:String = "addBuff";
		public static const DEL_BUFF:String = "delBuff";
		public static const REFRESH_BUFF:String = "refreshBuff";
		public static const FLASH:String = "flash";
		public static const DEAD:String = "dead";
		public static const LIFE:String = "life";
		public static const SKILL_COMPLETE:String = "skillComplete";
		public static const READ_SKILL_COMPLETE:String = "readSkillComplete";
		public static const START_LOADING:String = "startLoading";
		public static const AFFECT_BY_SKILL:String = "affectBySkill";
		public static const REMOVE_FLY_THING:String = "removeFlyThing";
		public static const FLY_THING_CHANGE_TARGET:String = "flyThingChangeTarget";
		public static const OMNISLASH_COMPLETE:String = "omnislashComplete";
		public static const OMNISLASH_CHANGE:String = "omnislashChange";
		
		
		public static const ADD_ERROR_MESSAGE:String = "addErrorMessage";
		public static const SHOW_SKILL_PROGRESS:String = "showSkillProgress";
		public static const CANNEL_SKILL_PROGRESS:String = "cannelSkillProgress";
		public static const SET_TARGET:String = "setTarget";
		public static const SET_MYSELF:String = "setMyself";
		public static const SET_SKILL:String = "setSkill";
		public static const ENTER_CD:String = "enterCD";
		public static const USE_SKILL:String = "useSkill";
		public static const DEBUG_MESSAGE:String = "debugMessage";
		public static const CLEAR_MESSAGE:String = "clearMessage";
		public static const EXIT_CD:String = "exitCD";
		public static const CHANGE_SIZE:String = "changeSize";
		//外部消息，客户端请求，服务器响应
		/**
		 * 登录请求
		 */
		public static const LOGIN_S:String = "login_s";
		/**
		 * 登录返回
		 */
		public static const LOGIN_R:String = "login_r";
		/**
		 * 连接socket
		 */
		public static const CONNECT_SOCKET_S:String = "connectSocket_s";
		/**
		 * 连接socket返回
		 */
		public static const CONNECT_SOCKET_R:String = "connectSocket_r";
		/**
		 * 断开连接socket返回
		 */
		public static const DISCONNECT_SOCKET:String = "disConnectSocket";
	}
}