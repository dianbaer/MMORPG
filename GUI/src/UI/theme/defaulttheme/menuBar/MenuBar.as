package UI.theme.defaulttheme.menuBar
{
	import UI.abstract.component.control.button.RadioButtonGroup;
	import UI.abstract.component.control.menuBar.AMenuBar;
	import UI.abstract.component.control.text.TextStyle;
	import UI.abstract.component.data.DataProvider;
	import UI.theme.defaulttheme.dropDownMenu.DropDownMenu;

	public class MenuBar extends AMenuBar
	{
		public function MenuBar ()
		{
			super();
			group = new RadioButtonGroup();
			menu = new DropDownMenu();
			menu.align = TextStyle.LEFT;
			menu.width = 100;
			bgUrl = "ui/bg2.png";
		}

		public function addItem ( text : String , data : DataProvider ) : void
		{
			var btn : MenuButton = new MenuButton();
			btn.text = text;
			btn.setSize( 45, 20 );
			//btn.update();
			addMenuItem( btn , data );
		}

		public function removeItem ( text : String ) : void
		{
			for ( var i : int = _listButton.length ; i >= 0 ; --i )
			{
				if ( _listButton[ i ].text == text )
					removeMenuItem( _listButton[ i ] );
			}
		}
	}
}
