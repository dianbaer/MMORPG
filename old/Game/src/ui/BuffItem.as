package ui
{
	
	
	public class BuffItem extends Box implements IAnimatable
	{
		private var buffInfo:BuffInfo;
		public function BuffItem()
		{
			super();
			
			
		}
		public function setBuffInfo(buffInfo:BuffInfo):void{
			this.buffInfo = buffInfo;
			JugglerManager.fourJuggler.add(this);
		}
		public function advanceTime(time:Number):void{
			onFrame();
		}
		private function onFrame():void{
			
		}
		override public function get width():Number{
			return getAllChildrenSize().x;
		}
		override public function get height():Number{
			return getAllChildrenSize().y;
		}
	}
}