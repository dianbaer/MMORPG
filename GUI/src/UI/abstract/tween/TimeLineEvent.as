package UI.abstract.tween
{
	import UI.abstract.component.event.UIEvent;

	public class TimeLineEvent extends UIEvent
	{
		public static const REMOVE_FROM_TIMELINE : String = "removeFromTimeLine"

		public function TimeLineEvent ( type : String , bubbles : Boolean = true , cancelable : Boolean = false )
		{
			super( type , bubbles , cancelable );
		}
	}
}
