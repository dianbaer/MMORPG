package UI.abstract.component.event
{

	public class NumericStepperEvent extends UIEvent
	{
		/** 值改变 **/
		public static const NUMER_CHANGE : String = "numer_change";

		/** 当前值 **/
		public var value : int;

		public function NumericStepperEvent ( type : String , value : int , bubbles : Boolean = true , cancelable : Boolean = false )
		{
			this.value = value;
			super( type , bubbles , cancelable );
		}
	}
}
