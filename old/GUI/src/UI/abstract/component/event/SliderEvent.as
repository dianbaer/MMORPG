package UI.abstract.component.event
{

	public class SliderEvent extends UIEvent
	{
		/** 滑块改变的时候 **/
		public static const SLIDER_CHANGE : String = "slider_Change";

		/** 刻度值 **/
		public var position : Number               = 0;

		public function SliderEvent ( type : String , position : Number , bubbles : Boolean = true , cancelable : Boolean = false )
		{
			super( type , bubbles , cancelable );
			this.position = position;
		}
	}
}
