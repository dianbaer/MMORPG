package
{
	public class BuffInfo
	{
		//加速
		public static const SPEED_ADD:int = 1;
		//减速
		public static const SPEED_DEL:int = 2;
		//眩晕
		public static const DIZZY_EFFECT:int = 3;
		//持续类型的
		public static const CONTINUED_EFFECT_DAMAGE:int = 4;
		//定身
		public static const CANNOT_MOVE:int = 5;
		//冰箱
		public static const ICE_BOX:int = 6;
		//变羊
		public static const CAST:int = 7;
		//持续类型治疗
		public static const CONTINUED_EFFECT_TREAT:int = 8;
		//减伤,加伤技能
		public static const ADD_OR_SUBTRACT_DAMAGE:int = 9;
		//法术反射
		public static const MAGIC_REFLECT:int = 10;
		
		public var buffData:Object;
		public var masterId:int;
		//毫秒级的1000就等于1秒
		public var surplusTime:int;
		public function BuffInfo()
		{
			reset();
		}
		public function reset():BuffInfo{
			return this;
		}
		public function dispose():void
		{
			buffData = null;
			masterId = 0;
			surplusTime = 0;
			toPool(this);
		}
		public function clone():BuffInfo{
			var buffInfo:BuffInfo = BuffInfo.fromPool();
			buffInfo.buffData = buffData;
			buffInfo.masterId = masterId;
			buffInfo.surplusTime = surplusTime;
			return buffInfo;
		}
		private static var sBuffInfoPool:Vector.<BuffInfo> = new <BuffInfo>[];
		
		/** @private */
		public static function fromPool():BuffInfo
		{
			if (sBuffInfoPool.length) return sBuffInfoPool.pop().reset();
			else return new BuffInfo();
		}
		
		/** @private */
		public static function toPool(buffInfo:BuffInfo):void
		{
			
			sBuffInfoPool[sBuffInfoPool.length] = buffInfo;
		}
	}
}