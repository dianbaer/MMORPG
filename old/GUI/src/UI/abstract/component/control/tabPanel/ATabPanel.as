package UI.abstract.component.control.tabPanel
{
	import UI.App;
	import UI.abstract.component.control.base.UIComponent;
	import UI.abstract.component.control.button.ITriggerButton;
	import UI.abstract.component.control.button.RadioButtonGroup;
	import UI.abstract.component.control.container.Box;
	import UI.abstract.component.control.layout.HLayout;
	import UI.abstract.component.control.layout.Layout;
	import UI.abstract.component.control.layout.VLayout;
	import UI.abstract.component.control.panel.Panel;
	import UI.abstract.component.event.RadioButtonGroupEvent;
	import UI.abstract.manager.LogManager;

	public class ATabPanel extends Box
	{
		/** 按钮和内容数据列表 **/
		protected var _tabData : Array     = [];

		/** 容器 **/
		protected var _content : Panel;

		/** 按钮组 **/
		protected var _radioGroup : RadioButtonGroup;

		protected var _selectTab : int     = -1;

		protected var _selectData : TabData;

		/** 按钮所在位置方向 **/
		private var _direction : int       = -1;

		/** 按钮间距 **/
		private var _gapBtn : int          = 2;

		/** 第一个按钮填充距离 **/
		private var _padding : int         = 10;

		/** 按钮距离容器背景距离 **/
		private var _gapBtnToContent : int = 0;

		/** 容器背景和容器内容间距 **/
		private var _gapContent : int      = 10;

		/** 布局 **/
		protected var _layout : Layout;

		public function ATabPanel ()
		{
			super();
			_content = new Panel();
			this.addChild( _content );
			_radioGroup = new RadioButtonGroup();
			App.event.addEvent( _radioGroup , RadioButtonGroupEvent.SELECTED , onButtonSelect );
			

		}

		override protected function draw () : void
		{
			super.draw();
			var arr : Array = [];
			for ( var i : int = 0 ; i < _tabData.length ; i++ )
				arr.push( _tabData[ i ].button );

			if(_layout){
				_layout.itemArray = arr;
				_layout.updateDisplayList();
			}

			var a : int = _tabData.length == 0 ? 0 : ( _tabData[ 0 ].button.y + _tabData[ 0 ].button.height + _gapBtnToContent );
			_bg.y = a;
			_bg.width = width;
			_bg.height = height - a;

			_content.x = _bg.x + _gapContent;
			_content.y = _bg.y + _gapContent;
			_content.width = _bg.width - ( _gapContent << 1 );
			_content.height = _bg.height - ( _gapContent << 1 );
			
		}

		override protected function drawGraphics () : void
		{
			super.drawGraphics();
			_content.removeContentAll();
			ITriggerButton( _tabData[ selectTab ].button ).selected = true;
			_content.content.addChild( _tabData[ selectTab ].content );
			
		}

		protected function onButtonSelect ( event : RadioButtonGroupEvent ) : void
		{
			for ( var i : int = 0 ; i < _tabData.length ; i++ )
			{
				if ( _tabData[ i ].button == event.button )
				{
					selectTab = i;
					break;
				}
			}
		}

		/**
		 * 增加一组tab
		 */
		public function addTabData ( data : TabData ) : void
		{
			if ( !data.button || !data.content )
			{
				App.log.info( "添加了一个不存在的tab" , LogManager.LOG_ERROR , "ATabPanel" , "addTab" );
				return;
			}

			if ( data.button is UIComponent )
				this.addChild( UIComponent( data.button ) );

			_tabData.push( data );
			_radioGroup.addButton( data.button );
			if ( _tabData.length == 1 )
				selectTab = 0;
			nextDraw();
		}

		/**
		 * 增加一组tab到某个位置
		 */
		public function addTabDataAt ( data : TabData , index : int ) : void
		{
			if ( !data.button || !data.content )
			{
				App.log.info( "添加了一个不存在的tab" , LogManager.LOG_ERROR , "ATabPanel" , "addTab" );
				return;
			}

			if ( _tabData.length == 0 || index > _tabData.length )
			{
				addTabData( data );
				return;
			}

			if ( data.button is UIComponent )
				this.addChild( UIComponent( data.button ) );

			var temp : TabData = _tabData[ selectTab ];
			_tabData.splice( index , 0 , data );
			_radioGroup.addButton( data.button );
			selectTab = _tabData.indexOf( temp );
			nextDraw();
		}

		/**
		 * 移出一组位置为index的tab
		 */
		public function removeTabAt ( index : int ) : void
		{
			var temp : TabData = _tabData[ index ];
			if ( !temp )
				return;
			_radioGroup.removeButton( temp.button );
			_tabData.splice( index , 1 );
			temp.dispose()
			if ( index == selectTab )
			{
				_selectTab = -1;
				selectTab = 0;
			}
			else if ( index < selectTab )
			{

				_selectTab -= 1;
				if ( _selectTab < 0 )
					_selectTab = 0;
			}
			nextDraw();
		}

		/**
		 * 选择tab页数 从0开始
		 */
		public function get selectTab () : int
		{
			return _selectTab;
		}

		/**
		 * @private
		 */
		public function set selectTab ( value : int ) : void
		{
			if ( _selectTab == value )
				return;
			_selectTab = value;
			nextDrawGraphics();
		}

		/** 按钮所在位置方向 **/
		public function get direction () : int
		{
			return _direction;
		}

		/**
		 * 必须先设置 按钮间距等 再设置方向
		 */
		public function set direction ( value : int ) : void
		{
			if ( _direction == value )
				return;
			_direction = value;
			if(_layout){
				_layout.dispose();
				_layout = null;
			}
			switch ( value )
			{
				case TabPanelStyle.UP:
				case TabPanelStyle.DOWN:
					_layout = new HLayout( _gapBtn , _gapBtn );
					_layout.startX = padding;
					break;
				case TabPanelStyle.LEFT:
				case TabPanelStyle.RIGHT:
					_layout = new VLayout( _gapBtn , _gapBtn );
					_layout.startY = padding;
					break;
			}
			nextDraw();
		}


		/** 按钮间距 **/
		public function get gapBtn () : int
		{
			return _gapBtn;
		}

		/**
		 * @private
		 */
		public function set gapBtn ( value : int ) : void
		{
			if ( _gapBtn == value )
				return;
			_gapBtn = value;
			if(_layout){
				_layout.horizontalGap = _gapBtn;
				_layout.verticalGap = _gapBtn;
			}
			nextDraw();
		}

		/** 第一个按钮填充距离 **/
		public function get padding () : int
		{
			return _padding;
		}

		/**
		 * @private
		 */
		public function set padding ( value : int ) : void
		{
			if ( _padding == value )
				return;
			_padding = value;
			if(_layout){
				switch ( _direction )
				{
					case TabPanelStyle.UP:
					case TabPanelStyle.DOWN:
						_layout.startX = padding;
						break;
					case TabPanelStyle.LEFT:
					case TabPanelStyle.RIGHT:
						_layout.startY = padding;
						break;
				}
			}
			
			nextDraw();
		}

		/** 按钮距离容器距离 **/
		public function get gapBtnToContent () : int
		{
			return _gapBtnToContent;
		}

		/**
		 * @private
		 */
		public function set gapBtnToContent ( value : int ) : void
		{
			if ( _gapBtnToContent == value )
				return;
			_gapBtnToContent = value;
			nextDraw();
		}

		/** 容器和容器内容间距 **/
		public function get gapContent () : int
		{
			return _gapContent;
		}

		/**
		 * @private
		 */
		public function set gapContent ( value : int ) : void
		{
			if ( _gapContent == value )
				return;
			_gapContent = value;
			nextDraw();
		}

		/**
		 * 获得内容宽
		 */
		public function get contentWidth () : Number
		{
			update();
			return _content.width;
		}

		/**
		 * 获得内容高
		 */
		public function get contentHeight () : Number
		{
			update();
			return _content.height;
		}

		/**
		 * 销毁
		 */
		override public function dispose () : void
		{
			
			_selectData = null;
			_layout.dispose();
			_layout = null;
			for each ( var item : TabData in _tabData )
				item.dispose();

			_tabData.length = 0;
			_tabData = null;
			
			
			_content = null;
			

			if ( _radioGroup )
			{
				//App.event.removeEvent( _radioGroup , RadioButtonGroupEvent.SELECTED , onButtonSelect );
				_radioGroup.dispose();
				_radioGroup = null;
			}
			super.dispose();
			
		}

	}
}
