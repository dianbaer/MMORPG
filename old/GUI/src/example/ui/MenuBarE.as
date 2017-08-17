package example.ui
{
	import UI.abstract.component.control.dropDownMenu.MenuData;
	import UI.abstract.component.data.DataProvider;
	import UI.theme.defaulttheme.menuBar.MenuBar;
	
	import flash.display.Sprite;
	
	public class MenuBarE extends Sprite
	{
		public function MenuBarE()
		{
			super();
			var i : int;
			var j : int;
			var n : int;
			var menu : MenuData;
			var menu1 : MenuData;
			var menu2 : MenuData;
			var menuArr : Array = [];
			var menuArr1 : Array = [];
			var menuArr2 : Array = [];
			/** MenuBar **/
			var menuBar : MenuBar = new MenuBar();
			addChild(menuBar);
			menuBar.setSize( 200, 25 );
			
			for ( i = 0 ; i < 5 ; i++ )
			{
				menu = new MenuData();
				menu.text = "菜单1";
				menuArr.push( menu );
				if ( i > 1 )
				{
					menuArr1 = [];
					for ( j = 0 ; j < 4 ; j++ )
					{
						menu1 = new MenuData();
						menu1.text =  "菜单子2";
						menuArr1.push( menu1 );
						if ( j == 0 ) 
						{
							menuArr2 = [];
							for ( n = 0 ; n < 3 ; n++ )
							{
								menu2 = new MenuData();
								menu2.text = "菜单子3";
								menuArr2.push( menu2 );
							}
							menu1.items = new DataProvider( menuArr2 );
						}
					}
					menu.items = new DataProvider( menuArr1 );
				}
			}
			var dataprovider : DataProvider = new DataProvider( menuArr );
			menuBar.addItem("文件", dataprovider ); 
			
			menuArr = [];
			for ( i = 0 ; i < 5 ; i++ )
			{
				menu = new MenuData();
				menu.text = "菜单2";
				menuArr.push(menu);
			} 
			dataprovider = new DataProvider( menuArr );
			menuBar.addItem("编辑", dataprovider ); 
			
			menuArr = [];
			for ( i = 0 ; i < 5 ; i++ )
			{
				menu = new MenuData();
				menu.text = "菜单3";
				menuArr.push(menu);
			}
			dataprovider = new DataProvider( menuArr );
			menuBar.addItem("工具", dataprovider ); 
			
			menuArr = [];
			for ( i = 0 ; i < 5 ; i++ )
			{
				menu = new MenuData();
				menu.text = "菜单4";
				menuArr.push(menu);
			}
			dataprovider = new DataProvider( menuArr );
			menuBar.addItem("帮助", dataprovider ); 
		}
	}
}