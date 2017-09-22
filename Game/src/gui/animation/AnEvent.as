package gui.animation
{
	import flash.events.Event;
	
	public class AnEvent extends Event
	{
		public static const REMOVE_FROM_JUGGLER:String = "removeFromJuggler";
		
		public function AnEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
		}
	}
}