package UI.abstract.component.control.datagrid
{
	import UI.abstract.component.control.list.IItemRenderer;

	public interface IItemRendererGrid extends IItemRenderer
	{
		/**
		 * 列数
		 */
//		function get column () : int;
//		function set column ( value : int ) : void;

		/**
		 * 列表宽度
		 */
		//function get widthList () : Array;
		function set widthList ( value : Array ) : void;
	}
}
