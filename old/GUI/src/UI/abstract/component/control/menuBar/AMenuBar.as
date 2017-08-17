package UI.abstract.component.control.menuBar
{
	import UI.App;
	import UI.abstract.component.control.base.UIComponent;
	import UI.abstract.component.control.button.BaseButton;
	import UI.abstract.component.control.button.ITriggerButton;
	import UI.abstract.component.control.button.RadioButtonGroup;
	import UI.abstract.component.control.container.Box;
	import UI.abstract.component.control.dropDownMenu.ADropDownMenu;
	import UI.abstract.component.control.layout.HLayout;
	import UI.abstract.component.data.DataProvider;
	import UI.abstract.component.event.DropDownMenuEvent;
	import UI.abstract.component.event.MenuBarEvent;
	import UI.abstract.component.event.RadioButtonGroupEvent;
	
	import flash.display.DisplayObject;
	import flash.events.MouseEvent;
	import flash.geom.Point;

	public class AMenuBar extends Box
	{
		protected var _listButton : Array;

		protected var _listData : Array;

		protected var _menu : ADropDownMenu;

		/** 按钮组 **/
		protected var _group : RadioButtonGroup;

		/** 按钮间距 **/
		private var _gapToButton : int = 4;

		/** 按钮上下间距 **/
		private var _gapH : int        = 2;

		private var _layout : HLayout;
		
		/** 是否自动清除数据集 **/
		protected var _isClearDataProvider : Boolean = true;

		public function AMenuBar ()
		{
			super();
			_listButton = [];
			_listData = [];
			_layout = new HLayout();
			App.event.addEvent( this , MouseEvent.CLICK , onClick );

		}

		override protected function draw () : void
		{
			super.draw();
			if ( _listButton.length == 0 )
				return;
			for ( var i : int = _listButton.length - 1 ; i >= 0 ; --i )
				_listButton[ i ].height = _height - 2 * _gapH;
			_layout.startX = 5;
			_layout.startY = _gapH;
			_layout.horizontalGap = _gapToButton;
			_layout.itemArray = _listButton;
			_layout.updateDisplayList();
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
		/**
		 * 设置下拉菜单
		 */
		protected function get menu () : ADropDownMenu
		{
			return _menu;
		}

		protected function set menu ( value : ADropDownMenu ) : void
		{
			if ( _menu )
				_menu.dispose();
			
			if(value){
				_menu = value;
				
				App.event.addEvent( _menu , DropDownMenuEvent.SELECT_COMPLETE , onSelectComplete );
				App.event.addEvent( _menu , DropDownMenuEvent.CLOSE , onMenuClose );
			}
		}

		/** 按钮组 **/
		public function get group () : RadioButtonGroup
		{
			return _group;
		}

		/**
		 * @private
		 */
		public function set group ( value : RadioButtonGroup ) : void
		{
			if(_group){
				_group.dispose();
			}
			if(value){
				_group = value;
				App.event.addEvent( _group , RadioButtonGroupEvent.SELECTED , onSelectButton );
			}
			
		}

		/** 按钮间距 **/
		public function get gapToButton () : int
		{
			return _gapToButton;
		}

		/**
		 * @private
		 */
		public function set gapToButton ( value : int ) : void
		{
			if(_gapToButton == value){
				return;
			}
			_gapToButton = value;
			nextDraw();
		}

		/**
		 * 增加一个菜单按钮
		 */
		protected function addMenuItem ( menuButton : ITriggerButton , data : DataProvider ) : void
		{
			if ( !menuButton || !( menuButton is UIComponent ) )
				return;
			
			var i : int = _listButton.indexOf( menuButton );
			if ( i != -1 )
			{
				var obj : DataProvider = _listData[ i ];
				if(isClearDataProvider){
					obj.dispose();
				}
				_listData[ i ] = data
			}
			else
			{
				this.addChild( menuButton as DisplayObject );
				menuButton.group = _group;
				_listButton.push( menuButton );
				_listData.push( data );
				
			}
			nextDraw();
		}

		/**
		 * 移出一个菜单按钮
		 */
		protected function removeMenuItem ( menuButton : ITriggerButton ) : void
		{
			if ( !menuButton || !( menuButton is UIComponent ) )
				return;
			//this.removeChild( menuButton as DisplayObject );
			var i : int = _listButton.indexOf( menuButton );
			if ( i == -1 )
				return;
			var btn : BaseButton    = _listButton[ i ];
			var data : DataProvider = _listData[ i ];
			btn.dispose();
			if(isClearDataProvider){
				data.dispose();
			}
			_listButton.splice( i , 1 );
			_listData.splice( i , 1 );
			nextDraw();
		}

		private function onSelectButton ( e : RadioButtonGroupEvent ) : void
		{
			var i : int = _listButton.indexOf( e.button );
			if ( i == -1 ){
				//_group.reset();
				if ( _menu.isShow() )
					_menu.show( false );
				return;
			}
				
			var btn : BaseButton    = _listButton[ i ];
			var data : DataProvider = _listData[ i ];
			
			_menu.dataProvider = data;
			if ( !_menu.isShow() )
				_menu.show( true );
			//show之后设置位置，因为show的时候会设置位置
			var p : Point = btn.localToGlobal( new Point() );
			_menu.setPosition( p.x , p.y + btn.height );
			
		}

		/**
		 * 选择菜单某一项
		 */
		private function onSelectComplete ( e : DropDownMenuEvent ) : void
		{
			_group.reset();
			dispatchEvent( new MenuBarEvent( MenuBarEvent.SELECT_MENUITEM , e.data.text ) );
		}

		/**
		 * 菜单关闭
		 */
		private function onMenuClose ( e : DropDownMenuEvent ) : void
		{
			_group.reset();
		}


		private function onClick ( e : MouseEvent ) : void
		{
			//e.stopPropagation();
			e.stopImmediatePropagation();
		}

		override public function dispose () : void
		{
			for ( var i : int = _listButton.length - 1 ; i >= 0 ; --i )
			{
				_listButton[ i ].dispose();
				if(isClearDataProvider){
					_listData[ i ].dispose();
				}
			}
			_listButton.length = 0;
			_listButton = null;
			_listData.length = 0;
			_listData = null;
			_layout.dispose();
			_layout = null;
			if ( _menu )
			{
				_menu.dispose();
				_menu = null;
			}

			if ( _group )
			{
				_group.dispose();
				_group = null;
			}

			super.dispose();
		}
	}
}
