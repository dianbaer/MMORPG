package UI.abstract.component.event
{
	import flash.events.Event;

	public class UIEvent extends Event
	{
		/** ui资源加载完成 **/
		public static const LOADER_COMPLETE : String = "loader_complete";

		/** ui组件大小变化 **/
		public static const RESIZE_UI : String       = "resize_ui";
		
		/** ui组件位置变化 **/
		public static const POSITION_UI : String       = "position_ui";

		/** 渲染完成 **/
		//public static const DRAW_UI : String         = "draw_ui";

		public function UIEvent ( type : String , bubbles : Boolean = true , cancelable : Boolean = false )
		{
			super( type , bubbles , cancelable );
		}
	}
}
