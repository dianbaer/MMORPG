package UI.abstract.component.event
{
	import UI.abstract.component.control.list.IItemRenderer;

	public class ListEvent extends UIEvent
	{
		private var _item : IItemRenderer;

		/** 数据已经改变 **/
		public static const CLICK_ITEM : String = "click_item";

		public function ListEvent ( type : String , item : IItemRenderer , bubbles : Boolean = true , cancelable : Boolean = false )
		{
			super( type , bubbles , cancelable );
			_item = item;
		}

		/** 触发项 **/
		public function get item () : IItemRenderer
		{
			return _item;
		}

	}
}
