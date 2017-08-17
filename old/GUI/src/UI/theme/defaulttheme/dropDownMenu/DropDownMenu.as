package UI.theme.defaulttheme.dropDownMenu
{
	import UI.abstract.component.control.dropDownMenu.ADropDownMenu;

	public class DropDownMenu extends ADropDownMenu
	{
		public function DropDownMenu ()
		{
			super();
			itemHeight = 25;
			itemRenderer = DropDownMenuObject;
			gapMenu = 2;
		}
	}
}
