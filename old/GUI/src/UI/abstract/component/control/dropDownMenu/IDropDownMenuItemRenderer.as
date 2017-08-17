package UI.abstract.component.control.dropDownMenu
{
	import UI.abstract.component.control.list.IItemRenderer;
	import UI.abstract.component.data.DataProvider;

	public interface IDropDownMenuItemRenderer extends IItemRenderer
	{
		/**
		 * 是否有子项
		 */
		function hasItems () : Boolean

		/**
		 * 获得子项
		 */
		function getItems () : DataProvider

		/**
		 * 是否有选中效果图标
		 */
		function get isSelectIcon () : Boolean
		function set isSelectIcon ( value : Boolean ) : void
	}
}
