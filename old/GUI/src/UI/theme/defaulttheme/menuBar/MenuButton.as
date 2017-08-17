package UI.theme.defaulttheme.menuBar
{
	import UI.abstract.component.control.button.RadioButtonGroup;
	import UI.theme.defaulttheme.Skin;
	import UI.theme.defaulttheme.button.ToggleButton;
	
	import flash.events.MouseEvent;

	public class MenuButton extends ToggleButton
	{
		private var skinList : Array = []

		public function MenuButton ( skin : String = Skin.BUTTON )
		{
			super( skin );
			isUp = true;
		}

		/*override protected function over () : void
		{
			super.over();
			if ( RadioButtonGroup( group ).selected && RadioButtonGroup( group ).selected != this )
			{
				selected = true;
				currentState = ButtonStyle.DOWN;
			}
		}*/
		override protected function onMouseOver ( event : MouseEvent ) : void
		{
			super.onMouseOver(event);
			if ( RadioButtonGroup( group ).selected && RadioButtonGroup( group ).selected != this )
			{
				selected = true;
				//currentState = ButtonStyle.DOWN;
			}
		}
	}
}
