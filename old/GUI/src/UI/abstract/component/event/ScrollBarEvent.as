package UI.abstract.component.event
{

	public class ScrollBarEvent extends UIEvent
	{
		/** 滚动条类型 **/
		public static const SCROLLTYPE_H : int         = 0;

		public static const SCROLLTYPE_V : int         = 1;

		/** 滑块改变的时候 **/
		public static const SCROLL_CHANGE : String     = "scroll_Change";

		/** 滚动条出现或消失 **/
		public static const SCROLL_BAR_CHANGE : String = "scroll_Bar_Change";

		/** 刻度值 **/
		public var position : int;

		/** 横向还是纵向值改变 **/
		public var changeType : int;

		public function ScrollBarEvent ( type : String , position : int = 0 , changeType : int = 0 , bubbles : Boolean = true , cancelable : Boolean =
										 false )
		{
			this.position = position;
			this.changeType = changeType;
			super( type , bubbles , cancelable );
		}
	}
}
