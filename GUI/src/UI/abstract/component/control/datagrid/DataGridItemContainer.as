package UI.abstract.component.control.datagrid
{
	import UI.abstract.component.control.list.IItemRenderer;
	import UI.abstract.component.control.list.ListItemContainer;

	public class DataGridItemContainer extends ListItemContainer
	{
		private var _widthList : Array = [];

		public function DataGridItemContainer ()
		{
			super();
		}

		override protected function drawItem ( item : IItemRenderer ) : void
		{
			if ( item is IItemRendererGrid )
			{
				IItemRendererGrid( item ).widthList = _widthList;
			}
			item.forceUpdate();
		}

		/** 设置index列的宽度 **/
		public function set widthList ( value : Array ) : void
		{
			_widthList = value;
			nextDrawGraphics();
		}
		override public function dispose () : void
		{
			_widthList = null;
			super.dispose();
		}
	}
}
