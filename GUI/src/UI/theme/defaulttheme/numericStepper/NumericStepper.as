package UI.theme.defaulttheme.numericStepper
{
	import UI.abstract.component.control.numericStepper.ANumericStepper;
	import UI.abstract.component.control.text.TextStyle;
	import UI.theme.defaulttheme.Skin;
	import UI.theme.defaulttheme.button.Button;
	import UI.theme.defaulttheme.text.TextInput;

	public class NumericStepper extends ANumericStepper
	{
		public function NumericStepper ()
		{
			textInput = new TextInput();
			textInput.align = TextStyle.CENTER;

			increaseBtn = new Button( Skin.BUTTON , 20 );

			decreaseBtn = new Button( Skin.BUTTON , 20 );

//			input = false;
			gapBtn = 1;
			gapTextToBtn = 0;

			setRange( 0 , 100 );

			step = 1;

			value = 0;
		}
	}
}
