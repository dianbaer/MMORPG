package net
{
	import flash.events.Event;
	import flash.utils.ByteArray;
	
	public class CodeEvent extends Event
	{
		//连接上
		public static const CONNECT:String = "connect";
		//断开
		public static const DISCONNECT:String = "disconnect";
		
		//进入场景
		public static const CODE2:String = "10000";
		//发送移动(走场景)
		public static const CODE3:String = "10001";
		//玩家添加到场景
		public static const CODE4:String = "10002";
		//玩家移动
		public static const CODE5:String = "10003";
		//玩家离开场景
		public static const CODE6:String = "10004";
		//玩家攻击(走场景)
		public static const CODE7:String = "10005";
		//玩家攻击返回
		public static const CODE8:String = "10006";
		//离开场景
		public static const CODE9:String = "10007";
		//技能施放不正确，取消技能
		public static const CODE11:String = "10008";
		//技能造成的伤害
		public static const CODE12:String = "10009";
		//怪物追踪人物
		public static const CODE13:String = "10010";
		//怪物回到初始点
		public static const CODE14:String = "10011";
		//增加buff
		public static const CODE15:String = "10012";
		//减少buff
		public static const CODE16:String = "10013";
		//刷新buff
		public static const CODE17:String = "10014";
		//闪现
		public static const CODE18:String = "10015";
		//死了
		public static const CODE19:String = "10016";
		//复活
		public static const CODE20:String = "10017";
		//施放技能成功
		public static const CODE21:String = "10018";//施放技能成功
		//读条技能完成或者取消
		public static const CODE22:String = "10019";
		//取消buff
		public static const CODE25:String = "10020";
		//被技能影响
		public static const CODE26:String = "10021";
		//飞行技能离开场景
		public static const CODE27:String = "10022";
		//飞行技能改变目标
		public static const CODE28:String = "10023";
		//无敌斩完成
		public static const CODE29:String = "10024";
		//无敌斩换目标
		public static const CODE30:String = "10025";
		
		//玩家攻击(走场景)
		//public static const CODE9:String = "10008";
		//登录发送
		public static const CODE1:String = "20000";
		//更换场景
		public static const CODE10:String = "20001";
		//加载资源
		public static const CODE23:String = "20002";
		//加载资源完成
		public static const CODE24:String = "20003";
		//更换服务器
		public static const CODE31:String = "20004";
		//收到更换服务器
		public static const CODE32:String = "20005";
		//再次连接服务器
		public static const CODE33:String = "20006";
		
		
		public var data:ByteArray;
		public function CodeEvent(type:String,data:ByteArray, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
			this.data = data;
		}
		public function dispose():void{
			ByteArrayBuffer.toPool(this.data);
			this.data = null;
		}
	}
}