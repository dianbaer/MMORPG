package UI.abstract.component.control.list
{
	import UI.App;
	import UI.abstract.component.control.button.ButtonStyle;
	import UI.abstract.component.control.container.Container;
	import UI.abstract.component.control.layout.Layout;
	import UI.abstract.component.control.text.TextStyle;
	import UI.abstract.component.data.DataProvider;
	import UI.abstract.component.event.DataChangeEvent;
	import UI.abstract.component.event.ListEvent;
	import UI.abstract.component.interfaces.IDispose;
	
	import flash.display.DisplayObject;
	import flash.events.MouseEvent;

	public class ListItemContainer extends Container implements IItemContainer
	{
		/** 所有渲染renderer **/
		protected var _itemsArray : Array;

		protected var _dataProvider : DataProvider;

		/** renderer类 **/
		protected var ItemClass : Class;

		/** renderer之间间隙 **/
		protected var _space : int                   = 0;

		/** 每项高度 **/
		protected var _itemHeight : int              = 0;

		protected var _selectedIndex : int           = -1;

		protected var _cacheSelectedIndex : int      = -1;

		protected var _selectedItem : IItemRenderer;

		protected var _layout : Layout;

		protected var _isSelectDispatch : Boolean;

		/** 对其类型 **/
		protected var _align : String                = TextStyle.CENTER;

		/** 是否自动清除数据集 **/
		protected var _isClearDataProvider : Boolean = true;

		public function ListItemContainer ()
		{
			//this.mouseEnabled = true;
			super();
			_itemsArray = [];
			App.event.addEvent( this , MouseEvent.CLICK , onClickItem );
			App.event.addEvent( this , MouseEvent.MOUSE_OVER , onMouseOver );
			App.event.addEvent( this , MouseEvent.MOUSE_OUT , onMouseOut );
		}

		override protected function draw () : void
		{
			super.draw();
			var len : int = _itemsArray.length;
			for ( var i : int = 0; i < len ; i++ )
			{
				IItemRenderer( _itemsArray[ i ] ).width = _width;
			}
		}

		override protected function drawGraphics () : void
		{
			super.drawGraphics();
			if ( _dataProvider != null )
			{
				var i : int = 0;
				var item : IItemRenderer;
				var dataProviderLength : int = _dataProvider.length;
				var itemsArrayLength : int = _itemsArray.length;

				// 刷新数据集
				if ( dataProviderLength > itemsArrayLength )
				{
					for ( i ; i < itemsArrayLength ; i++ )
					{
						item = _itemsArray[ i ];
						// 必须先设置高度宽度（后面的参数会改变这个初始值）
						item.height = _itemHeight;
						item.width = _width;
						item.align = _align;
						
						if ( _itemHeight > 0 )
							item.isFix = true;
						else
							item.isFix = false;
						
						
						item.data = _dataProvider.getItemAt( i );
						item.itemIndex = i;
						drawItem( item );
					}
					for ( i ; i < dataProviderLength ; i++ )
					{
						item = new ItemClass() as IItemRenderer;
						// 必须先设置高度宽度（后面的参数会改变这个初始值）
						item.height = _itemHeight;
						item.width = _width;
						item.align = _align;
						
						if ( _itemHeight > 0 )
							item.isFix = true;
						else
							item.isFix = false;
						
						
						item.data = _dataProvider.getItemAt( i );
						item.itemIndex = i;
						this.addChild( DisplayObject( item ) );
						_itemsArray.push( item );
						drawItem( item );
					}
				}
				else if ( dataProviderLength <= itemsArrayLength )
				{
					for ( i ; i < dataProviderLength ; i++ )
					{
						item = _itemsArray[ i ];
						// 必须先设置高度宽度（后面的参数会改变这个初始值）
						item.height = _itemHeight;
						item.width = _width;
						item.align = _align;
						
						if ( _itemHeight > 0 )
							item.isFix = true;
						else
							item.isFix = false;
						
						
						item.data = _dataProvider.getItemAt( i );
						item.itemIndex = i;
						drawItem( item );
					}
					var j : int = i;
					for ( i ; i < itemsArrayLength ; i++ )
					{
						this.removeChild( _itemsArray[ i ] );
						if ( _itemsArray[ i ] is IDispose )
							IDispose( _itemsArray[ i ] ).dispose();
					}
					_itemsArray.splice( j );
				}
			}

			//更新上次选择的
			if ( selectedItem )
			{
				if ( _itemsArray.indexOf( selectedItem ) != -1 )
					_selectedIndex = selectedItem.itemIndex;
			}

			//缓存
			if (_cacheSelectedIndex != -1 )
			{
				selectedIndex = _cacheSelectedIndex;
				_cacheSelectedIndex = -1;
			}
			toLayout();
			
			/*dispatchEvent( new UIEvent( UIEvent.RESIZE_UI ) );
			dispatchEvent( new UIEvent( UIEvent.DRAW_UI ) );*/
			
		}
		public function toLayout():void{
			//如果isnext是true的时候，layout可能没有报错
			if(_layout){
				_layout.itemArray = _itemsArray;
				_layout.updateDisplayList();
				//宽度高度，会因为子类的宽度高度而变化，这个需要注意，很有可能会回归成0，导致没办法draw
				width = getAllChildrenSize().x;
				height = getAllChildrenSize().y;
			}
		}
		/**
		 * 渲染IitemRenderer (供子类使用)
		 */
		protected function drawItem ( item : IItemRenderer ) : void
		{
			item.forceUpdate();
		}

		/**
		 * 数据集
		 */
		public function get dataProvider () : DataProvider
		{
			return _dataProvider;
		}

		/**
		 * @private
		 */
		public function set dataProvider ( value : DataProvider ) : void
		{
			if ( !value )
				value = new DataProvider();

			if ( isClearDataProvider && _dataProvider )
			{
				_dataProvider.dispose();
				_dataProvider = null;
			}

			_dataProvider = value;
			if ( App.event.hasEvent( _dataProvider , DataChangeEvent.DATA_CHANGE ) )
				App.event.removeEventByObj( _dataProvider );

			App.event.addEvent( _dataProvider , DataChangeEvent.DATA_CHANGE , onDataChange );
			nextDrawGraphics();
		}

		public function set itemRenderer ( value : Class ) : void
		{
			ItemClass = value;
		}

		/**
		 * 当前选择索引
		 */
		public function get selectedIndex () : int
		{
			return _selectedIndex;
		}

		/**
		 * @private
		 */
		public function set selectedIndex ( value : int ) : void
		{
			if ( value < 0 || value >= _itemsArray.length )
			{
				_cacheSelectedIndex = value;
				return;
			}

			_selectedIndex = value;
			if ( _selectedItem )
				_selectedItem.selected = false;
			_selectedItem = _itemsArray[ value ];
			_selectedItem.selected = true;
			setState( value , ListStyle.SELECTED );
			dispatchEvent( new ListEvent( ListEvent.CLICK_ITEM , selectedItem ) );
		}

		/**
		 * 当前选择对象
		 */
		public function get selectedItem () : IItemRenderer
		{
			return _selectedItem;
		}

//		/**
//		 * 选中的数据源
//		 */
//		public function get selectedItem () : Object
//		{
//			return _selectedItem.data;
//		}

		/**
		 * 排序方式
		 */
		public function get layout () : Layout
		{
			return _layout;
		}

		public function set layout ( value : Layout ) : void
		{
			if(_layout == value){
				return;
			}
			_layout = value;
			if(_itemsArray.length>0){
				toLayout();
			}
		}

		/**
		 * 数据集改变
		 */
		protected function onDataChange ( e : DataChangeEvent ) : void
		{
			nextDrawGraphics();
		}

		/**
		 * 鼠标离开
		 */
		protected function onMouseOut ( event : MouseEvent ) : void
		{
			var target : DisplayObject = App.ui.selectParent( event.target as DisplayObject , ItemClass );

			if ( target is ItemClass )
			{
				var index : int = _itemsArray.indexOf( target );
				if ( index != selectedIndex )
					setState( index , ListStyle.UNSELECTED );
			}
		}

		/**
		 * 鼠标进入
		 */
		protected function onMouseOver ( event : MouseEvent ) : void
		{
			var target : DisplayObject = App.ui.selectParent( event.target as DisplayObject , ItemClass );

			if ( target is ItemClass )
			{
				var index : int = _itemsArray.indexOf( target );
				if ( index != selectedIndex )
					setState( index , ListStyle.OVER );
			}
		}

		/**
		 * 选中
		 */
		protected function onClickItem ( e : MouseEvent ) : void
		{
			var target : DisplayObject = App.ui.selectParent( e.target as DisplayObject , ItemClass );
			if ( target is ItemClass )
			{
				if ( selectedItem == target && !_isSelectDispatch )
					return;
				selectedIndex = IItemRenderer( target ).itemIndex;
			}
		}

		protected function setState ( index : int , state : String ) : void
		{
			if ( _itemsArray[ index ] == null )
				return;

			switch ( state )
			{
				case ListStyle.OVER:
					IItemRenderer( _itemsArray[ index ] ).currentState = ButtonStyle.OVER;
					break;
				case ListStyle.SELECTED:
					IItemRenderer( _itemsArray[ index ] ).currentState = ButtonStyle.DOWN;
					break;
				case ListStyle.UNSELECTED:
					IItemRenderer( _itemsArray[ index ] ).currentState = ButtonStyle.UP;
					break;
			}
		}

		/** renderer之间间隙 **/
		public function get space () : int
		{
			return _space;
		}

		/**
		 * @private
		 */
		public function set space ( value : int ) : void
		{
			_space = value;
		}

		/** 每项高度 **/
		public function get itemHeight () : int
		{
			return _itemHeight;
		}

		/**
		 * @private
		 */
		public function set itemHeight ( value : int ) : void
		{
			if(_itemHeight == value){
				return;
			}
			_itemHeight = value;
			nextDrawGraphics();
		}

		/** 是否选中也发送事件 **/
		public function get isSelectDispatch () : Boolean
		{
			return _isSelectDispatch;
		}

		/**
		 * @private
		 */
		public function set isSelectDispatch ( value : Boolean ) : void
		{
			_isSelectDispatch = value;
		}

		/** 对其类型 **/
		public function get align () : String
		{
			return _align;
		}

		/**
		 * @private
		 */
		public function set align ( value : String ) : void
		{
			if(_align == value){
				return;
			}
			_align = value;
			nextDrawGraphics();
		}

		/** 是否自动清除数据集 **/
		public function get isClearDataProvider () : Boolean
		{
			return _isClearDataProvider;
		}

		/**
		 * @private
		 */
		public function set isClearDataProvider ( value : Boolean ) : void
		{
			_isClearDataProvider = value;
		}

		override public function dispose () : void
		{
			_selectedItem = null;
			_layout.dispose();
			_layout = null;
			var len : int = _itemsArray.length;
			for ( var i : int ; i < len ; i++ )
				IItemRenderer( _itemsArray[ i ] ).dispose();
			_itemsArray = null;
			if ( isClearDataProvider )
				_dataProvider.dispose();
			_dataProvider = null;
			super.dispose();
		}
	}
}
