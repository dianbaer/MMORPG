package example.ui
{
	import UI.abstract.component.control.dropDownMenu.MenuData;
	import UI.abstract.component.control.text.TextStyle;
	import UI.abstract.component.data.DataProvider;
	import UI.theme.defaulttheme.dropDownMenu.DropDownMenu;
	
	import flash.display.Sprite;
	
	public class DropDownMenuE extends Sprite
	{
		public function DropDownMenuE()
		{
			super();
			
			var arrData : Array = [];
			var arr : Array     = [ "常规模式" , "设置不可达点" , "设置可达点" , "设置遮挡区域" , "设置孤岛区域" ];
			var data : MenuData;
			for ( var i : int = 0 ; i < arr.length ; i++ )
			{
				data = new MenuData();
				data.text = arr[ i ];
				arrData.push( data );
			}
			var _rightMenuData:DataProvider = new DataProvider( arrData );
			//右键菜单
			var _rightMenu:DropDownMenu = new DropDownMenu();
			_rightMenu.setPosition(50,50);
			_rightMenu.width = 110;
			_rightMenu.align = TextStyle.LEFT;
			_rightMenu.isSelectIcon = true;
			_rightMenu.isAutoDispose = false;
			_rightMenu.dataProvider = _rightMenuData;
			_rightMenu.selectIndex( 0 );
			addChild(_rightMenu);
		}
	}
}