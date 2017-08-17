package UI.abstract.component.control.list
{
	import UI.App;
	import UI.abstract.component.control.container.Box;
	import UI.abstract.component.control.layout.Layout;
	import UI.abstract.component.control.scrollBar.AScrollPanel;
	import UI.abstract.component.control.text.TextStyle;
	import UI.abstract.component.data.DataProvider;
	import UI.abstract.component.event.ScrollBarEvent;
	
	import flash.display.DisplayObject;
	import flash.events.Event;

	public class AList extends Box
	{
		/** 容器 **/
		protected var _container : IItemContainer;

		/** 滚动条 **/
		protected var _scrollBar : AScrollPanel;

		protected var _scrollToTop : int = 10;

		/** 对其类型 **/
		protected var _align : String    = TextStyle.CENTER;

		public function AList ()
		{
			super();
		}

		/** 滚动条距顶端距离 **/
		public function get scrollToTop():int
		{
			return _scrollToTop;
		}

		/**
		 * @private
		 */
		public function set scrollToTop(value:int):void
		{
			if(_scrollToTop == value){
				return
			}
			_scrollToTop = value;
			nextDraw();
		}

		override protected function draw () : void //有问题 需要优化
		{
			super.draw();
			_scrollBar.y = _scrollToTop;
			_scrollBar.x = _scrollToTop;
			_scrollBar.setSize( _width - _scrollToTop*2, _height - _scrollToTop*2 );
			_container.width = _width - _scrollToTop*2;
			///_scrollBar.update();
			//_container.width = _scrollBar.contentWidth;
//			_container.height = _scrollBar.contentHeight;
			
		}

		/**
		 * 数据集
		 */
		public function get dataProvider () : DataProvider
		{
			return _container.dataProvider;
		}

		public function set dataProvider ( value : DataProvider ) : void
		{
			if ( _scrollBar != null )
				_scrollBar.reset();

			_container.dataProvider = value;
			//nextDraw();
		}

		/**
		 * 设置显示对象容器
		 */
		public function set itemRenderer ( value : Class ) : void
		{
			_container.itemRenderer = value;
		}

		/**
		 * 选择的索引
		 */
		public function get selectedIndex () : int
		{
			return _container.selectedIndex;
		}

		public function set selectedIndex ( value : int ) : void
		{
			_container.selectedIndex = value;
		}

		/**
		 * 选择的项
		 */
		public function get selectedItem () : IItemRenderer
		{
			return _container.selectedItem;
		}

//		/**
//		 * 背景图
//		 */
//		public function get background () : Image
//		{
//			return _background;
//		}
//
//		public function set background ( value : Image ) : void
//		{
//			if ( _background )
//				_background.dispose();
//
//			if ( value )
//			{
//				_background = value;
//				addChildAt( _background , 0 );
//				_background.setSize( width , height );
//			}
//		}

		/**
		 * 设置容器对象
		 */
		public function get container () : IItemContainer
		{
			return _container;
		}

		/**
		 * @private
		 */
		public function set container ( value : IItemContainer ) : void
		{
			if ( _container )
			{
				_container.dispose();
				_container = null;
			}
			if(value){
				_container = value;
				_scrollBar.addChildToPanel( _container as DisplayObject );
				//App.event.addEvent( ( _container as IEventDispatcher ) , UIEvent.DRAW_UI , nextDraw , null , false ); //resize_ui不管用 不知道为什么
			}
			nextDraw();
		}

		/** 滚动条 **/
		public function get scrollBar () : AScrollPanel
		{
			return _scrollBar;
		}

		/**
		 * @private
		 */
		public function set scrollBar ( value : AScrollPanel ) : void
		{
			if ( _scrollBar )
			{
				_scrollBar.dispose();
				_scrollBar = null;
			}

			if(value){
				_scrollBar = value;
				this.addChild(_scrollBar);
				App.event.addEvent(_scrollBar, ScrollBarEvent.SCROLL_BAR_CHANGE, onScrollBarChange );
			}
			nextDraw();
		}
		
		protected function onScrollBarChange ( e : Event ) : void
		{
			_container.width = _scrollBar.contentWidth;
		}

		/** 每项高度 **/
		public function get itemHeight () : int
		{
			return _container.itemHeight;
		}

		/**
		 * @private
		 */
		public function set itemHeight ( value : int ) : void
		{
			_container.itemHeight = value;
		}

		/**
		 * 对其类型
		 */
		public function set align ( value : String ) : void
		{
			_container.align = value;
		}
		public function set layout ( value : Layout ) : void
		{
			(_container as ListItemContainer).layout = value;
			
		}
		/**
		 * 清理所有item
		 */
		public function clear () : void
		{
			dataProvider = null;
		}

		override public function dispose () : void
		{
			_container = null;
			_scrollBar = null;
			super.dispose();
		}
	}
}
