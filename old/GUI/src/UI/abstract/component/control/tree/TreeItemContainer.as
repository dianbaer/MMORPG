package UI.abstract.component.control.tree
{
	import UI.App;
	import UI.abstract.component.control.list.IItemRenderer;
	import UI.abstract.component.control.list.ListItemContainer;
	import UI.abstract.component.data.DataProvider;
	import UI.abstract.component.event.DataChangeEvent;
	
	import flash.display.DisplayObject;
	import flash.events.MouseEvent;

	/**
	 * 树容器 数据集得循序必须按全部打开的顺序添加
	 */
	public class TreeItemContainer extends ListItemContainer
	{
		/** 原始数据集 **/
		private var _origDataProvider : DataProvider;

		public function TreeItemContainer ()
		{
			super();
		}

		override public function set dataProvider ( value : DataProvider ) : void
		{
			if ( !value )
				value = new DataProvider();

			if (isClearDataProvider && _origDataProvider )
			{
				_origDataProvider.dispose();
				_origDataProvider = null;
			}

			_origDataProvider = value;
			
			if ( App.event.hasEvent( _origDataProvider , DataChangeEvent.DATA_CHANGE ) )
				App.event.removeEventByObj( _origDataProvider );
			
			App.event.addEvent( _origDataProvider , DataChangeEvent.DATA_CHANGE , onDataChange );

			updateDataProvider();
		}

		/**
		 * 更新数据集 取出可显示对象
		 */
		protected function updateDataProvider () : void
		{
			var item : TreeData;
			var levelsArray : Array    = [];
			var showList : Array       = [];
			var len : int              = _origDataProvider.length;
			var parentOpened : Boolean = true;

			for ( var i : int = 0 ; i < len ; i++ )
			{
				item = _origDataProvider.getItemAt( i ) as TreeData;
				levelsArray[ item.level ] = item;

				if ( item.parent == null )
				{
					// root节点加入显示列表
					showList.push( item );
				}
				else
				{
					// 查找父项是否有未打开
					for ( var j : int = item.level - 1 ; j >= 0 ; j-- )
					{
						parentOpened = TreeData( levelsArray[ j ] ).opened;
						if ( parentOpened == false ){
							break;
						}else{
							//trace(j);
						}
							
					}

					if ( parentOpened )
					{
						showList.push( item );
					}
				}

			}

			if ( !_dataProvider )
				_dataProvider = new DataProvider();
			_dataProvider.removeAll();
			_dataProvider.addItems( showList );

			nextDrawGraphics();
		}

		/**
		 * 选中
		 */
		override protected function onClickItem ( e : MouseEvent ) : void
		{
			super.onClickItem( e );

			var target : DisplayObject = App.ui.selectParent( e.target as DisplayObject , ItemClass );
			if ( target is IItemRenderer )
			{
				var obj : TreeData = _dataProvider.getItemAt( IItemRenderer( target ).itemIndex ) as TreeData;

				if ( obj.canExpand )
				{
					obj.opened = !obj.opened
					updateDataProvider();
				}
			}
		}

		/**
		 * 数据集改变
		 */
		override protected function onDataChange ( e : DataChangeEvent ) : void
		{
			updateDataProvider();
		}

		override public function dispose () : void
		{
			if (isClearDataProvider && _origDataProvider )
			{
				_origDataProvider.dispose();
				_origDataProvider = null;
			}
			//_origDataProvider.dispose();
			super.dispose();
		}
	}
}
