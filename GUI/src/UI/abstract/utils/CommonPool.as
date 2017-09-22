package UI.abstract.utils
{
	import flash.text.TextField;

	public class CommonPool
	{
		
		public function CommonPool()
		{
		}
		
		//文本
		private static var sTextFieldPool:Vector.<TextField> = new <TextField>[];
		
		/** @private */
		public static function fromPoolText():TextField
		{
			if (sTextFieldPool.length) return sTextFieldPool.pop();
			else return new TextField();
		}
		
		/** @private */
		public static function toPoolText(textField:TextField):void
		{
			
			
			sTextFieldPool[sTextFieldPool.length] = textField;
		}
		
		
		//数组
		private static var sArrayPool:Vector.<Array> = new <Array>[];
		
		/** @private */
		public static function fromPoolArray():Array
		{
			if (sArrayPool.length) return sArrayPool.pop();
			else return new Array();
		}
		
		/** @private */
		public static function toPoolArray(array:Array):void
		{
			array.length = 0;
			sArrayPool[sArrayPool.length] = array;
		}
		
		//对象
		private static var sObjectPool:Vector.<Object> = new <Object>[];
		
		/** @private */
		public static function fromPoolObject():Object
		{
			if (sObjectPool.length) return sObjectPool.pop();
			else return new Object();
		}
		
		/** @private */
		public static function toPoolObject(object:Object):void
		{
			for(var key:String in object){
				object[key] = null;
				delete object[key];
			}
			sObjectPool[sObjectPool.length] = object;
		}
	}
}