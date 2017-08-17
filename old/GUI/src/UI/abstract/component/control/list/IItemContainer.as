package UI.abstract.component.control.list
{
	import UI.abstract.component.data.DataProvider;
	import UI.abstract.component.interfaces.IDispose;

	public interface IItemContainer extends IDispose
	{
		/**
		 * 渲染可视元素
		 */
		function set itemRenderer ( value : Class ) : void;

		/**
		 * 数据集
		 */
		function get dataProvider () : DataProvider;
		function set dataProvider ( value : DataProvider ) : void;


		/**
		 * 当前选择对象
		 */
		function get selectedItem () : IItemRenderer;

		/**
		 * 当前选择索引
		 */
		function get selectedIndex () : int;
		function set selectedIndex ( value : int ) : void;

		function get itemHeight () : int;
		function set itemHeight ( value : int ) : void;

		/**
		 * 大小
		 */
		function get width () : Number;
		function set width ( value : Number ) : void;
		function get height () : Number;
		function set height ( value : Number ) : void;

		/**
		 * 对其类型
		 */
		function get align () : String
		function set align ( value : String ) : void

	}
}
