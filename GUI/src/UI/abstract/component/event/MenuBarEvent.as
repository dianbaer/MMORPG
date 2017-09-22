package UI.abstract.component.event
{

	public class MenuBarEvent extends UIEvent
	{
		/** 菜单选择 **/
		public static const SELECT_MENUITEM : String = "select_menuItem";

		public var text : String                     = "";

		public function MenuBarEvent ( type : String , text : String , bubbles : Boolean = true , cancelable : Boolean = false )
		{
			super( type , bubbles , cancelable );
			this.text = text;
		}
	}
}
