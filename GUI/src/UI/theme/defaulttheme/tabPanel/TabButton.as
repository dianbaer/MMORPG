package UI.theme.defaulttheme.tabPanel
{
	import UI.theme.defaulttheme.Skin;
	import UI.theme.defaulttheme.button.ToggleButton;

	public class TabButton extends ToggleButton
	{
		public function TabButton ( skin : String = Skin.TABPANEL_BUTTON )
		{
			super( skin );
			setSize( 60 , 30 );
			text = "";
			color = 0xffffff;
			_isUp = false;
		}
	}
}
