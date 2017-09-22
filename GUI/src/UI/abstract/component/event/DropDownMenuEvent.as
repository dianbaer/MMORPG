package UI.abstract.component.event
{
	import UI.abstract.component.control.dropDownMenu.IDropDownMenuItemRenderer;

	public class DropDownMenuEvent extends UIEvent
	{
		/** 菜单选择项改变 **/
		public static const SELECT_CHANGE : String   = "select_change";

		/** 菜单选择项改变 **/
		public static const SELECT_COMPLETE : String = "select_complete";

		/** 菜单关闭 **/
		public static const CLOSE : String           = "dropDownMenu_close";

		/** 所选item **/
		public var item : IDropDownMenuItemRenderer;

		/** 层级 **/
		public var level : int;
		
		public var data:Object;

		public function DropDownMenuEvent ( type : String , item : IDropDownMenuItemRenderer = null , level : int = 0 , data : Object = null, bubbles : Boolean = true , cancelable : Boolean =
											false )
		{
			this.item = item;
			this.level = level;
			this.data = data;
			super( type , bubbles , cancelable );
		}
	}
}
