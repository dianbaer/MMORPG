package
{
	public class BuffArray
	{
		public var buffArray:Array = new Array();
		//增加值
		public var addValue:int = 0;
		//增加百分比
		public var addPer:int = 0;
		public var buffType:int = 0;
		public function BuffArray()
		{
			reset();
		}
		public function reset():BuffArray{
			return this;
		}
		public function dispose():void{
			if(buffArray && buffArray.length > 0){
				for(var i:int = 0;i<buffArray.length;i++){
					buffArray[i].dispose();
				}
			}
			buffArray.length = 0;
			addValue = 0;
			addPer = 0;
			buffType = 0;
			toPool(this);
		}
		private static var sBuffArrayPool:Vector.<BuffArray> = new <BuffArray>[];
		
		/** @private */
		public static function fromPool():BuffArray
		{
			if (sBuffArrayPool.length) return sBuffArrayPool.pop().reset();
			else return new BuffArray();
		}
		
		/** @private */
		public static function toPool(buffArray:BuffArray):void
		{
			sBuffArrayPool[sBuffArrayPool.length] = buffArray;
			
		}
	}
}