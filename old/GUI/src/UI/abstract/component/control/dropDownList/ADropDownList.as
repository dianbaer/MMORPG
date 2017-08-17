package UI.abstract.component.control.dropDownList
{
	import UI.App;
	import UI.abstract.component.control.button.BaseButton;
	import UI.abstract.component.control.container.Container;
	import UI.abstract.component.control.list.AList;
	import UI.abstract.component.control.text.ATextInput;
	import UI.abstract.component.data.DataProvider;
	import UI.abstract.component.event.ListEvent;
	
	import flash.display.DisplayObject;
	import flash.events.MouseEvent;

	public class ADropDownList extends Container
	{
		/** 文本内容 **/
		protected var _textInput : ATextInput;

		/** 下拉按钮 **/
		protected var _button : BaseButton;

		/** 列表 **/
		protected var _list : AList;

		/** 列表是否显示 **/
		private var _isShowList : Boolean;

		/** 是否点击文本也打开下拉列表 **/
		protected var _isClickText : Boolean;

		/** 文本和列表的高度 **/
		protected var _gapTextToList : int;

		protected var _gapBtnToRight : int = 0;

		/** 列表高度 **/
		protected var _listHeight : int    = 0;

		protected var _textHeight : int    = 0;

		public function ADropDownList ()
		{
			super();
			App.event.addEvent( this , MouseEvent.CLICK , onClick );
		}

		/** 文本高度 **/
		public function get textHeight():int
		{
			return _textHeight;
		}

		/**
		 * @private
		 */
		public function set textHeight(value:int):void
		{
			if(_textHeight == value){
				return;
			}
			_textHeight = value;
			nextDraw();
		}

		/** 按钮距离最右边边距离 **/
		public function get gapBtnToRight():int
		{
			return _gapBtnToRight;
		}

		/**
		 * @private
		 */
		public function set gapBtnToRight(value:int):void
		{
			if(_gapBtnToRight == value){
				return;
			}
			_gapBtnToRight = value;
			nextDraw();
		}

		override protected function draw () : void
		{
			super.draw();

			if ( !_textInput || !_button || !_list )
				return;

			_textInput.setSize( _width , _textHeight );
			_textInput.extraWidth = _button.width + _gapBtnToRight;

			_button.x = _width - _button.width - _gapBtnToRight;
			_button.y = ( _textInput.height - _button.height ) >> 1;

			_list.y = _textInput.height + _gapTextToList;
			_list.setSize( _width , _listHeight );
		}

		override protected function drawGraphics () : void
		{
			super.drawGraphics();
		}

		/**
		 * 数据集
		 */
		public function get dataProvider () : DataProvider
		{
			return _list.dataProvider;
		}

		public function set dataProvider ( value : DataProvider ) : void
		{
			_list.dataProvider = value;
		}

		/** 文本内容 **/
		protected function get textInput () : ATextInput
		{
			return _textInput;
		}

		/**
		 * @private
		 */
		protected function set textInput ( value : ATextInput ) : void
		{
			if ( _textInput )
				_textInput.dispose();

			_textInput = value;
			_textInput.select = false;
			_textInput.textMouse = false;
			this.addChild( _textInput );
			nextDraw();
		}

		/** 下拉按钮 **/
		protected function get button () : BaseButton
		{
			return _button;
		}

		/**
		 * @private
		 */
		protected function set button ( value : BaseButton ) : void
		{
			if ( _button )
				_button.dispose();

			_button = value;
			this.addChild( _button );
			nextDraw();
		}

		/** 列表 **/
		protected function get list () : AList
		{
			return _list;
		}

		/**
		 * @private
		 */
		protected function set list ( value : AList ) : void
		{
			if ( _list )
				_list.dispose();

			_list = value;
			_list.visible = false;
			this.addChild( _list );
			App.event.addEvent( list , ListEvent.CLICK_ITEM , onSelectItem );
			nextDraw();
		}

		/** 是否点击文本也打开下拉列表 **/
		public function get isClickText () : Boolean
		{
			return _isClickText;
		}

		/**
		 * @private
		 */
		public function set isClickText ( value : Boolean ) : void
		{
			_isClickText = value;
		}

		/**
		 * 设置选择index
		 */
		public function set selectedIndex ( value : int ) : void
		{
			if ( value == _list.selectedIndex )
				return;
			list.selectedIndex = value;
		}

		public function get selectedIndex () : int
		{
			return list.selectedIndex;
		}

		private function onClick ( e : MouseEvent ) : void
		{
			e.stopImmediatePropagation();
			var target : DisplayObject = App.ui.selectParent( e.target as DisplayObject , null, _button);
			var target1 : DisplayObject = App.ui.selectParent( e.target as DisplayObject , null, _textInput);
			if ( target == _button || ( _isClickText && target1 == _textInput ) )
			{
				showList( true );
			}
			
		}

		/**
		 * 显示隐藏列表
		 */
		private function showList ( value : Boolean ) : void
		{
			_isShowList = value;

			if ( _isShowList )
			{
				_list.visible = true;
				App.event.addEvent( this.stage , MouseEvent.CLICK , onStageClick ); //需要优化 (遇到组织冒泡的东西会接收不到)
				App.event.removeEvent( this , MouseEvent.CLICK , onClick );
			}
			else
			{
				_list.visible = false;
				if ( App.event.hasEventFun( this.stage , MouseEvent.CLICK , onStageClick ) )
					App.event.removeEvent( this.stage , MouseEvent.CLICK , onStageClick );
				
				if(!App.event.hasEventFun( this , MouseEvent.CLICK , onClick )){
					App.event.addEvent( this , MouseEvent.CLICK , onClick );
				}
			}
		}

		private function onStageClick ( e : MouseEvent ) : void
		{
			var target : DisplayObject = App.ui.selectParent( e.target as DisplayObject , null, list);
			
			if ( target == list )
			{
				return;
			}
			showList( false );
		}

		/**
		 * 点击列表选项
		 */
		private function onSelectItem ( e : ListEvent ) : void
		{
			showList( false );
			_textInput.text = e.item.data as String;
		}

		/** 文本和列表的高度 **/
		public function get gapTextToList () : int
		{
			return _gapTextToList;
		}

		/**
		 * @private
		 */
		public function set gapTextToList ( value : int ) : void
		{
			if(_gapTextToList == value){
				return;
			}
			_gapTextToList = value;
			nextDraw();
		}

		/** 列表高度 **/
		public function get listHeight () : int
		{
			return _listHeight;
		}

		/**
		 * @private
		 */
		public function set listHeight ( value : int ) : void
		{
			if(_listHeight == value){
				return;
			}
			_listHeight = value;
			nextDraw();
		}

		override public function dispose () : void
		{
			
			_textInput = null;
			_button = null;
			_list = null;
			
			super.dispose();
		}

	}
}
