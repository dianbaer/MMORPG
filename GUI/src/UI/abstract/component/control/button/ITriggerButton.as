package UI.abstract.component.control.button
{
	

	public interface ITriggerButton
	{
		function get selected () : Boolean;
		function set selected ( value : Boolean ) : void;

		function get group () : TriggerButtonGroup;
		function set group ( value : TriggerButtonGroup ) : void;
	}
}
