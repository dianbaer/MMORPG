package UI.theme.defaulttheme.dropDownList
{
	import UI.abstract.component.control.dropDownList.ADropDownList;
	import UI.theme.defaulttheme.button.Button;
	import UI.theme.defaulttheme.list.List;
	import UI.theme.defaulttheme.text.TextInput;

	public class DropDownList extends ADropDownList
	{
		public function DropDownList ()
		{
			super();
			gapBtnToRight = 5;
			textHeight = 25;
			listHeight = 100;

			textInput = new TextInput();

			button = new Button();
			button.setSize( 20 , 20 );

			list = new List();
			list.scrollToTop = 0;
		}
	}
}
