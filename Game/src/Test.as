package
{
	import flash.display.Sprite;
	import flash.utils.getTimer;
	
	public class Test extends Sprite
	{
		public function Test()
		{
			super();
			var obj:Object = {aaa:"111111",bbb:"cccccc"};
			for(var str:String in obj){
				trace(str);
			}
			return;
			var i:int;
			var num:int = 1000000;
			var time:int = getTimer();
			for(i = 0;i<num;i++){
				var array:Array = new Array();
			}
			var time1:int = getTimer();
			trace(time1-time);
			var list:Vector.<Array> = new Vector.<Array>();
			for(i = 0;i<num;i++){
				var array1:Array = new Array();
				list.push(array1);
			}
			var time2:int = getTimer();
			trace(time2-time1);
			
			var list1:Vector.<Array> = new Vector.<Array>();
			for(i = 0;i<num;i++){
				var array2:Array = new Array();
				list1[list1.length] = array2;
				//list1.push(array2);
			}
			var time3:int = getTimer();
			trace(time3-time2);
			for(i = 0;i<num;i++){
				var array3:Array = list.pop();
			}
			var time4:int = getTimer();
			trace(time4-time3);
		}
	}
}