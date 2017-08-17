package UI.abstract.component.control.scrollBar
{
	import UI.App;
	import UI.abstract.component.control.container.Container;
	import UI.abstract.component.control.panel.Panel;
	import UI.abstract.component.event.ScrollBarEvent;
	import UI.abstract.component.event.UIEvent;
	import UI.abstract.manager.LogManager;
	
	import flash.display.DisplayObject;
	import flash.events.MouseEvent;
	import flash.utils.getQualifiedClassName;

	public class AScrollPanel extends Container
	{
		/** 容器 **/
		private var _panel : Panel;

		/** 横向滚动条 **/
		private var _hScrollbar : BaseScrollBar;

		/** 纵向滚动条 **/
		private var _vScrollbar : BaseScrollBar;

		/** 是否显示横向滚动条 **/
		private var _isShowHScrollbar : Boolean;

		/** 是否显示纵向滚动条 **/
		private var _isShowVScrollbar : Boolean;

		/** 是否一直显示滚动条 **/
		private var _isShowAlways : Boolean;

		/** 不是一直显示是否达到需要显示条件 **/
		private var _checkIsShowH : Boolean;

		private var _checkIsShowV : Boolean;

		/** 内容与滚动条距离 **/
		private var _gapToScroll : int = 0;

		/** 内容之间的距离 **/
		//private var _gapContent : int  = 0;

		/** 一次移动的距离 **/
		private var _tick : int        = 1;

		/** 是否自动置底 **/
		private var _isDown : Boolean;

		/** 是否暂停刷新一次内容位置（内容高度改变时防止抖动用） **/
		//private var isPauseOnce : Boolean;

		public function AScrollPanel ()
		{
			super();
			App.event.addEvent( this , MouseEvent.MOUSE_WHEEL , onWheel );
			
//			App.event.addEvent( this , Event.ADDED_TO_STAGE , onStage );
		}

//		private function onStage(e:Event):void
//		{
//			App.event.addEvent( this.stage , MouseEvent.MOUSE_WHEEL , onWheel ); 
//		}


		override protected function draw () : void //需要优化 太乱了
		{
			super.draw();

			//if ( _panel.width <= 0 )
			_panel.setSize( _width , _height );
			
			//重绘大小
			var i:int = 1;
			App.log.info( "scrollpanel重绘大小次数："+i, LogManager.LOG_WARN , getQualifiedClassName( this ) ,"changeChildSize");
			var tempH : Boolean = _checkIsShowH;
			var tempV : Boolean = _checkIsShowV;
			while(!changeChildSize()){
				i++;
				App.log.info( "scrollpanel重绘大小次数："+i, LogManager.LOG_WARN , getQualifiedClassName( this ) ,"changeChildSize");
			}
			
//			if ( _panel.width == 0 )
//			{
			//tempH = _checkIsShowH;
			//tempV = _checkIsShowV;
			//updateScrollShow();
			//大多数是因为第一次 draw的时候_panel大小还是0 所以可能要重新updateScrollShow
			/*if ( tempH != _checkIsShowH || tempV != _checkIsShowV )
			{
				nextDraw();
				return;
			}*/
//			}

//			_panel.update();
			updateScrollBar();
			updateBarSize();
			if ( isDown && _vScrollbar )
				positionV = _vScrollbar.max;
			updateContentPosition();
//			updateContentPosition();
			/*if ( ( _panel.content.superHeight > _panel.height && _panel.content.superHeight + _panel.content.y < _panel.height )
				|| ( _panel.content.superHeight < _panel.height && _panel.content.y != 0 ) )
				isPauseOnce = false;
			else
				isPauseOnce = true;*/

			this.graphics.clear();
			this.graphics.beginFill( 0 , 0 );
			this.graphics.drawRect( 0 , 0 , _width , _height );
			this.graphics.endFill();
			//dispatchEvent( new ScrollBarEvent( ScrollBarEvent.SCROLL_BAR_CHANGE ) );
			//if ( tempH != _checkIsShowH || tempV != _checkIsShowV )
				dispatchEvent( new ScrollBarEvent( ScrollBarEvent.SCROLL_BAR_CHANGE ) );
		}
		private function changeChildSize():Boolean{
			var tempH : Boolean = _checkIsShowH;
			var tempV : Boolean = _checkIsShowV;
			updateScrollShow();
			
			//_checkIsShowH = _isShowHScrollbar;
			//_checkIsShowV = isShowVScrollbar;
			if ( _checkIsShowH && _checkIsShowV )
			{
				//都显示
				//_panel.x = _gapContent;
				_panel.width = width - _vScrollbar.width - _gapToScroll /*- ( _gapContent << 1 )*/;
				_panel.height = height - _hScrollbar.height - _gapToScroll /*- ( _gapContent << 1 )*/;
				
				_hScrollbar.y = height - _hScrollbar.height;
				_hScrollbar.width = _panel.width;
				
				_vScrollbar.x = width - _vScrollbar.width;
				_vScrollbar.height = _panel.height;
				
			}
			else if ( _checkIsShowH )
			{
				//只显示横向
				//_panel.x = _gapContent;
				_panel.width = width;
				_panel.height = height - _hScrollbar.height - _gapToScroll /*- ( _gapContent << 1 )*/;
				_hScrollbar.y = height - _hScrollbar.height;
				_hScrollbar.width = width;
			}
			else if ( _checkIsShowV )
			{
				//只显示纵向
				//_panel.x = _gapContent;
				_panel.width = width - _vScrollbar.width - _gapToScroll /*- ( _gapContent << 1 )*/;
				_panel.height = height;
				_vScrollbar.x = width - _vScrollbar.width;
				_vScrollbar.height = height;
			}
			else
			{
				//_panel.x = _gapContent - ( _gapContent << 1 );
				_panel.width = width;
				_panel.height = height;
			}
			tempH = _checkIsShowH;
			tempV = _checkIsShowV;
			updateScrollShow();
			//大多数是因为第一次 draw的时候_panel大小还是0 所以可能要重新updateScrollShow
			if ( tempH != _checkIsShowH || tempV != _checkIsShowV )
			{
				return false
			}
			return true;
		}
		override protected function drawGraphics () : void
		{
			super.drawGraphics();
			/*if ( isPauseOnce )
			{
				isPauseOnce = false;
				return;
			}*/
			updateContentPosition();
		}

		/**
		 * 滚动条是否显示
		 */
		protected function updateScrollShow () : void
		{
			var isShow : Boolean = false;
			if ( isShowHScrollbar )
			{
				if ( isShowAlways )
					isShow = true;
				else if ( _panel.isOutH )
					isShow = true;
				else
					isShow = false;

				_hScrollbar.visible = isShow;
				_checkIsShowH = isShow;
				if ( !isShow )
					_hScrollbar.position = 0;
			}
			else
				_checkIsShowH = false;


			if ( isShowVScrollbar )
			{
				if ( isShowAlways )
					isShow = true;
				else if ( _panel.isOutV )
					isShow = true;
				else
					isShow = false;

				_vScrollbar.visible = isShow;
				_checkIsShowV = isShow;
				if ( !isShow )
					_vScrollbar.position = 0;
			}
			else
				_checkIsShowV = false;
		}

		/** 容器 **/
		protected function get panel () : Panel
		{
			return _panel;
		}

		/**
		 * @private
		 */
		protected function set panel ( value : Panel ) : void
		{
			if ( _panel )
			{
				_panel.dispose();
				_panel = null;
			}

			if ( value )
			{
				_panel = value;
				App.event.addEvent( _panel.content , UIEvent.POSITION_UI , onPosition );
				App.event.addEvent( _panel.content , UIEvent.RESIZE_UI , onContentChangeSize );
				addChild( _panel );
			}
		}
		private function onPosition(event:UIEvent):void{
			//监控panel.content里面的子对象有没有坐标的改变
			if(event.target != _panel.content){
				nextDraw();
			}
		}
		/** 横向滚动条 **/
		protected function get hScrollbar () : BaseScrollBar
		{
			return _hScrollbar;
		}

		/**
		 * @private
		 */
		protected function set hScrollbar ( value : BaseScrollBar ) : void
		{
			if ( _hScrollbar )
			{
				_hScrollbar.dispose();
				_hScrollbar = null;
			}

			if ( value )
			{
				_hScrollbar = value;
				App.event.addEvent( _hScrollbar , ScrollBarEvent.SCROLL_CHANGE , onScrollChange );
				addChild( _hScrollbar );
			}
		}

		/** 纵向滚动条 **/
		protected function get vScrollbar () : BaseScrollBar
		{
			return _vScrollbar;
		}

		/**
		 * @private
		 */
		protected function set vScrollbar ( value : BaseScrollBar ) : void
		{
			if ( _vScrollbar )
			{
				_vScrollbar.dispose();
				_vScrollbar = null;
			}

			if ( value )
			{
				_vScrollbar = value;
				App.event.addEvent( _vScrollbar , ScrollBarEvent.SCROLL_CHANGE , onScrollChange );
				addChild( _vScrollbar );
			}
		}

		/** 是否显示横向滚动条 **/
		public function get isShowHScrollbar () : Boolean
		{
			return _isShowHScrollbar;
		}

		/**
		 * @private
		 */
		public function set isShowHScrollbar ( value : Boolean ) : void
		{
			_isShowHScrollbar = value;
		}

		/** 是否显示纵向滚动条 **/
		public function get isShowVScrollbar () : Boolean
		{
			return _isShowVScrollbar;
		}

		/**
		 * @private
		 */
		public function set isShowVScrollbar ( value : Boolean ) : void
		{
			_isShowVScrollbar = value;
		}

		/** 是否一直显示滚动条 **/
		public function get isShowAlways () : Boolean
		{
			return _isShowAlways;
		}

		/**
		 * @private
		 */
		public function set isShowAlways ( value : Boolean ) : void
		{
			if(_isShowAlways == value){
				return;
			}
			_isShowAlways = value;
			nextDraw();
		}

		/**
		 * panel内容大小改变
		 */
		private function onContentChangeSize ( e : UIEvent ) : void
		{
			nextDraw();
		}

		private function onWheel ( e : MouseEvent ) : void
		{
			positionV -= e.delta;
//			if ( e.delta < 0 )
//				positionV += 1;
//			else
//				positionV -= 1;
		}

		/**
		 * 增加显示对象
		 */
		public function addChildToPanel ( child : DisplayObject ) : DisplayObject
		{
			var disObj : DisplayObject = _panel.content.addChild( child );
			nextDraw();
			return disObj;
		}

		public function addChildAtToPanel ( child : DisplayObject , index : int ) : DisplayObject
		{
			var disObj : DisplayObject = _panel.content.addChildAt( child , index );
			nextDraw();
			return disObj;
		}

		public function removeChildToPanel ( child : DisplayObject ) : DisplayObject
		{
			var disObj : DisplayObject = _panel.content.removeChild( child );
			nextDraw();
			return disObj;
		}

		public function removeChildAtToPanel ( index : int ) : DisplayObject
		{
			var disObj : DisplayObject = _panel.content.removeChildAt( index );
			nextDraw();
			return disObj;
		}

		/**
		 * 滚动条值改变
		 */
		private function onScrollChange ( e : ScrollBarEvent ) : void
		{
			switch ( e.changeType )
			{
				case ScrollBarEvent.SCROLLTYPE_H:
					if ( _panel.content.getAllChildrenSize().x <= _panel.width )
						return;
					nextDrawGraphics();
					break;
				case ScrollBarEvent.SCROLLTYPE_V:
					if ( _panel.content.getAllChildrenSize().y <= _panel.height )
					{
						/*if ( _panel.content.y != 0 )
							_panel.content.y = 0;*/
						return;
					}
					nextDrawGraphics();
					break;
			}
		}

		/**
		 * 更新内容位置
		 */
		protected function updateContentPosition () : void
		{
			if ( isShowHScrollbar )
				_panel.content.x = -( _panel.content.getAllChildrenSize().x - _panel.width ) * _hScrollbar.ratio;

			if ( isShowVScrollbar )
				_panel.content.y = -( _panel.content.getAllChildrenSize().y - _panel.height ) * _vScrollbar.ratio;
		}

		/**
		 * 内容大小改变时更新滚动条
		 */
		protected function updateScrollBar () : void
		{
			var num : Number = 0;
			if ( isShowHScrollbar )
			{
				num = Math.max( 0 , _panel.content.getAllChildrenSize().x - _panel.width );
				_hScrollbar.max = Math.ceil( num / tick );
			}

			if ( isShowVScrollbar )
			{
				num = Math.max( 0 , _panel.content.getAllChildrenSize().y - _panel.height );
				_vScrollbar.max = Math.ceil( num / tick );
			}
		}

		/**
		 * 更新滑块大小
		 */
		protected function updateBarSize () : void
		{
			if ( _checkIsShowH )
				_hScrollbar.barRatio = _panel.width / _panel.content.getAllChildrenSize().x;

			if ( _checkIsShowV )
				_vScrollbar.barRatio = _panel.height / _panel.content.getAllChildrenSize().y;
		}

//		/**
//		 * 更新滑块正确位置
//		 */
//		protected function 

		/**
		 * 检测是否达到显示条件
		 */
		/*public function checkIsShow () : void
		{
			if ( isShowHScrollbar )
			{
				if ( _panel.isOutH )
					_checkIsShowH = true;
				else
					_checkIsShowH = false;
			}
			else
				_checkIsShowH = false;

			if ( isShowVScrollbar )
			{
				if ( _panel.isOutV )
					_checkIsShowV = true;
				else
					_checkIsShowV = false;
			}
			else
				_checkIsShowV = false;
		}*/

		/**
		 * 当前值
		 */
		public function get positionH () : Number
		{
			if ( !_hScrollbar )
				return 0;
			return _hScrollbar.position;
		}

		/**
		 * @private
		 */
		public function set positionH ( value : Number ) : void
		{
			if ( !_hScrollbar || value == positionH )
				return;
			_hScrollbar.position = value;
//			nextDrawGraphics();
		}

		/**
		 * 当前值
		 */
		public function get positionV () : Number
		{
			if ( !_vScrollbar )
				return 0;
			return _vScrollbar.position;
		}

		/**
		 * @private
		 */
		public function set positionV ( value : Number ) : void
		{
			if ( !_vScrollbar || value == positionV )
				return;
			_vScrollbar.position = value;
//			nextDrawGraphics();
		}

		/** 内容距离滚动条距离 **/
		public function get gapToScroll () : int
		{
			return _gapToScroll;
		}

		/**
		 * @private
		 */
		public function set gapToScroll ( value : int ) : void
		{
			if(_gapToScroll == value){
				return;
			}
			_gapToScroll = value;
			nextDraw();
		}

		/** 一次移动的距离 **/
		public function get tick () : int
		{
			return _tick;
		}

		/**
		 * @private
		 */
		public function set tick ( value : int ) : void
		{
			if(_tick == value){
				return;
			}
			_tick = value;
//			if ( _vScrollbar )
//				_vScrollbar.tick = value; 
//			if ( _hScrollbar )
//				_hScrollbar.tick = value;
			nextDraw();
		}

		public function reset () : void
		{
			positionH = 0;
			positionV = 0;
		}

//		override public function set width ( value : Number ) : void
//		{
//			if ( _width == value )
//				return;
//			_width = value;
//			nextDraw();
//		}
//
//		override public function set height ( value : Number ) : void
//		{
//			if ( _height == value )
//				return;
//			_height = value;
//			nextDraw();
//		}

		/**
		 * 显示内容宽
		 */
		public function get contentWidth () : Number
		{
			var w : Number = 0;
			if ( _checkIsShowH && _checkIsShowV )
				w = width - _vScrollbar.width - _gapToScroll;
			else if ( _checkIsShowH )
				w = width;
			else if ( _checkIsShowV )
				w = width - _vScrollbar.width - _gapToScroll;
			else
				w = width;
			return w;
		}

		/**
		 * 显示内容高
		 */
		public function get contentHeight () : Number
		{
			var h : Number = 0;
			if ( _checkIsShowH && _checkIsShowV )
				h = height - _hScrollbar.height - _gapToScroll;
			else if ( _checkIsShowH )
				h = height - _hScrollbar.height - _gapToScroll;
			else if ( _checkIsShowV )
				h = height;
			else
				h = height;
			return h;
		}

		/**
		 * 滚动条宽
		 */
		public function get vScrollWidth () : Number
		{
			if ( vScrollbar )
				return vScrollbar.width + gapToScroll;
			return 0;
		}

		public function get vScrollVHeight () : Number
		{
			if ( hScrollbar )
				return hScrollbar.height + gapToScroll;
			return 0;
		}

		/** 是否自动置底 **/
		public function get isDown () : Boolean
		{
			return _isDown;
		}

		/**
		 * @private
		 */
		public function set isDown ( value : Boolean ) : void
		{
			if(_isDown == value){
				return;
			}
			_isDown = value;
			nextDraw();
		}

		override public function dispose () : void
		{
			
			_panel = null;
			_hScrollbar = null;
			_vScrollbar = null;
			super.dispose();
		}
	}
}
