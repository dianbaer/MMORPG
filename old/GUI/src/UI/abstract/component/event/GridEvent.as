package UI.abstract.component.event
{

	public class GridEvent extends UIEvent
	{
		/** 拖拽完成 **/
		//public static const DRAG_COMPLETE : String = "drag_complete";

		public function GridEvent ( type : String , bubbles : Boolean = true , cancelable : Boolean = false )
		{
			super( type , bubbles , cancelable );
		}
	}
}
