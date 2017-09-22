package example.ui
{
	import UI.abstract.component.control.button.RadioButtonGroup;
	import UI.theme.defaulttheme.button.ToggleButton;
	
	import flash.display.Sprite;
	
	public class ToggleButtonE extends Sprite
	{
		private var toggle : ToggleButton
		private var toggle1 : ToggleButton
		private var toggle2 : ToggleButton
		public function ToggleButtonE()
		{
			super();
			/** ToggleButton RadioButtonGroup **/
			toggle = new ToggleButton();
			toggle.text = "ToggleButton1";
			toggle.setSize(100,30)
			toggle.selected = true;
			toggle.x = 350;
			addChild( toggle );
			
			toggle1 = new ToggleButton();
			toggle1.text = "ToggleButton2";
			toggle1.y = toggle.height + 35;
			toggle1.setSize(100,30)
			toggle1.x = 350;
			addChild( toggle1 );
			
			toggle2= new ToggleButton();
			toggle2.text = "ToggleButton3";
			toggle2.y = toggle1.y + toggle1.height + 35;
			toggle2.setSize(100,30)
			toggle2.x = 350;
			addChild( toggle2 );
			
			var toggleGroup : RadioButtonGroup = new RadioButtonGroup();
			toggleGroup.addButton( toggle );
			toggleGroup.addButton( toggle1 );
			toggleGroup.addButton( toggle2 );
			toggleGroup.selected = toggle;
		}
	}
}