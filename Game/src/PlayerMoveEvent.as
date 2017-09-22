package
{
	import flash.events.Event;
	import flash.geom.Point;
	
	public class PlayerMoveEvent extends Event
	{
		public static const MOVE:String = "move";
		public static const JUMPOVER:String = "jumpOver";
		public var point1:Point;
		public var point2:Point;
		public function PlayerMoveEvent(type:String,point1:Point,point2:Point ,bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
			this.point1 = point1;
			this.point2 = point2;
		}
	}
}