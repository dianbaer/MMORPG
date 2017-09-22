package UI.abstract.component.event
{
	import UI.abstract.component.control.button.ITriggerButton;

	import flash.events.Event;

	public class RadioButtonGroupEvent extends UIEvent
	{
		public static const SELECTED : String = "RadioButtonGroupEventSelected";

		public var button : ITriggerButton;

		public function RadioButtonGroupEvent ( type : String , button : ITriggerButton , bubbles : Boolean = false , cancelable : Boolean = false )
		{
			super( type , bubbles , cancelable );
			this.button = button;
		}

	}
}
