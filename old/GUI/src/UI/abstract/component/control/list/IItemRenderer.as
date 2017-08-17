package UI.abstract.component.control.list
{
	import UI.abstract.component.interfaces.IDispose;

	public interface IItemRenderer extends IDispose
	{
		/**
		 * 所在位置
		 */
		function get itemIndex () : int;
		function set itemIndex ( value : int ) : void;

		/**
		 * 数据内容
		 */
		function get data () : Object;
		function set data ( value : Object ) : void;

		/**
		 * 是否选中
		 */
		function get selected () : Boolean;
		function set selected ( value : Boolean ) : void;

		/**
		 * 大小
		 */
		function get width () : Number;
		function set width ( value : Number ) : void;
		function get height () : Number;
		function set height ( value : Number ) : void;

		/**
		 * 设置状态
		 */
		function get currentState () : String
		function set currentState ( value : String ) : void

		/**
		 * 固定大小
		 */
		function get isFix () : Boolean
		function set isFix ( value : Boolean ) : void

		/**
		 * 对其类型
		 */
		function get align () : String
		function set align ( value : String ) : void
		/**更新**/
		function forceUpdate():void
	}
}
