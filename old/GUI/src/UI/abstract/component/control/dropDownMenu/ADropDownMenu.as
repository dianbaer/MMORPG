package UI.abstract.component.control.dropDownMenu
{
	import UI.App;
	import UI.abstract.component.control.base.UIComponent;
	import UI.abstract.component.control.container.Container;
	import UI.abstract.component.control.layout.VLayout;
	import UI.abstract.component.control.text.TextStyle;
	import UI.abstract.component.data.DataProvider;
	import UI.abstract.component.event.DropDownMenuEvent;
	
	import flash.display.DisplayObject;
	import flash.events.MouseEvent;
	import flash.geom.Point;

	public class ADropDownMenu extends Container
	{
		//private var _dataProvider : DataProvider;

		/** container列表 **/
		private var _containerList : Array           = [];

		private var _itemRenderer : Class;

		/** 每项高度 **/
		private var _itemHeight : int;

		private var _isShow : Boolean;

		/** 另一个菜单间距 **/
		private var _gapMenu : int;

		/** 文字对其类型 **/
		protected var _align : String                = TextStyle.CENTER;

		/** 是否有选中效果图标 **/
		private var _isSelectIcon : Boolean;

		/** 是否关闭时自动卸载子列表 **/
		private var _isAutoDispose : Boolean         = true;
		;

		public function ADropDownMenu ()
		{
			super();
			App.event.addEvent( this , DropDownMenuEvent.SELECT_CHANGE , onSelectChange );
			App.event.addEvent( this , DropDownMenuEvent.SELECT_COMPLETE , onSelectComplete );
		}

		override protected function draw () : void
		{
			super.draw();
		}

		override protected function drawGraphics () : void
		{
			super.drawGraphics();
		}

		/**
		 * 显示
		 */
		public function show ( bool : Boolean ) : void
		{
			if ( _isShow == bool )
				return;
			_isShow = bool;
			//用于单个的dropdownmenu
			if ( bool )
			{
				this.x = App.stage.mouseX;
				this.y = App.stage.mouseY;
				if ( x + superWidth > App.stage.stageWidth )
					x -= superWidth;
				if ( y + superHeight > App.stage.stageHeight )
					y -= superHeight;
				
			}
			
			if ( bool )
			{
				App.stage.addChild( this );
				App.event.addEvent( App.stage , MouseEvent.CLICK , onStageClick );
			}
			else
			{
				if ( _isAutoDispose )
				{
					//var obj : DropDownMenuItemContainer;
					for ( var i : int = _containerList.length - 1 ; i >= 0 ; i-- )
						UIComponent( _containerList[ i ] ).dispose();
					_containerList.length = 0;
				}
				//用于单个的dropdownmenu，第一级不隐藏
				hideLevel( 1 );
				this.parent && this.parent.removeChild( this );
				App.event.removeEvent( App.stage , MouseEvent.CLICK , onStageClick );
			}
		}

		public function isShow () : Boolean
		{
			return _isShow;
		}

		/**
		 * 数据集
		 */
		public function get dataProvider () : DataProvider
		{
			if ( _containerList.length == 0 )
				return null;
			return _containerList[ 0 ].dataProvider
		}

		public function set dataProvider ( value : DataProvider ) : void
		{
			for ( var i : int = _containerList.length - 1 ; i >= 0 ; i-- )
				UIComponent( _containerList[ i ] ).dispose();
			_containerList.length = 0;
			createContainer( 0 , value );
		}

		/**
		 * 创建container
		 * @param level : 第几级子菜单
		 */
		private function createContainer ( level : int , data : DataProvider ) : DropDownMenuItemContainer
		{
			var container : DropDownMenuItemContainer = _containerList[ level ];
			if ( container )
			{
				container.dataProvider = data;
				return container;
			}

			container = new DropDownMenuItemContainer();
			container.width = _width;
			container.itemHeight = _itemHeight;
			container.itemRenderer = _itemRenderer;
			container.layout = new VLayout( 0 , 1 , 1 );
			//必须是false
			container.isClearDataProvider = false;
			container.dataProvider = data;
			container.level = level;
			container.align = _align;
			container.isSelectIcon = _isSelectIcon;
			_containerList[ level ] = container;

			this.addChild( container );
			return container;
		}

		/**
		 * 设置显示对象容器
		 */
		public function set itemRenderer ( value : Class ) : void
		{
			_itemRenderer = value;
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
			_itemHeight = value;
		}

		/** 另一个菜单间距 **/
		public function get gapMenu () : int
		{
			return _gapMenu;
		}

		/**
		 * @private
		 */
		public function set gapMenu ( value : int ) : void
		{
			_gapMenu = value;
		}
		
		/**
		 * 设置选择
		 */
		public function selectIndex ( index : int, level : int = 0 ) : void
		{
			if ( _containerList[level] )
			{
				DropDownMenuItemContainer(_containerList[level]).selectedIndex = index;
			}
		}
		
		public function getSelectIndex ( level : int = 0 ) : int
		{
			if ( _containerList[level] )
			{
				return DropDownMenuItemContainer(_containerList[level]).selectedIndex;
			}
			return -1;
		}


		private function onSelectChange ( e : DropDownMenuEvent ) : void
		{
			if ( e.item.hasItems() )
			{
				hideLevel( e.level + 1 );
				var container : DropDownMenuItemContainer = createContainer( e.level + 1 , e.item.getItems() );
				if ( e.item is UIComponent )
				{
					var pos : Point = UIComponent( e.item ).stagePosition;
					pos = this.globalToLocal( pos );
					container.x = pos.x + _width;
					container.y = pos.y;
					container.visible = true;
				}
			}
			else
				hideLevel( e.level + 1 );
		}
		//用于单个的dropdownmenu（自己关闭自己）
		private function onSelectComplete(e : DropDownMenuEvent):void{
			show( false );
		}
		/**
		 * 隐藏index以上层级
		 */
		private function hideLevel ( index : int ) : void
		{
			var len : int = _containerList.length;
			var container : DropDownMenuItemContainer;
			for ( var i : int = len - 1 ; i >= index ; i-- )
			{
				container = _containerList[ i ] as DropDownMenuItemContainer;
				if ( !container )
					continue;
				//App.event.removeEventByObj( container.dataProvider );
				DropDownMenuItemContainer( _containerList[ i ] ).visible = false;
			}
		}


		private function onStageClick ( e : MouseEvent ) : void
		{
			var target : DisplayObject = App.ui.selectParent( e.target as DisplayObject , null,this );
			if ( target == this ){
				return;
			}
			//用于单个的dropdownmenu（自己关闭自己）
			show( false );
			dispatchEvent( new DropDownMenuEvent( DropDownMenuEvent.CLOSE , null , 0 ) );
		}

		

		/** 文字对齐方式 **/
		public function get align () : String
		{
			return _align;
		}

		/**
		 * @private
		 */
		public function set align ( value : String ) : void
		{
			_align = value;
		}

		/** 是否有选中效果图标 **/
		public function get isSelectIcon () : Boolean
		{
			return _isSelectIcon;
		}

		/**
		 * @private
		 */
		public function set isSelectIcon ( value : Boolean ) : void
		{
			_isSelectIcon = value;
		}

		/** 是否关闭时自动卸载所有 **/
		public function get isAutoDispose () : Boolean
		{
			return _isAutoDispose;
		}

		/**
		 * @private
		 */
		public function set isAutoDispose ( value : Boolean ) : void
		{
			_isAutoDispose = value;
		}

		override public function dispose () : void
		{
			var len : int = _containerList.length;
			for ( var i : int = len - 1 ; i >= 0 ; i-- )
				UIComponent( _containerList[ i ] ).dispose();
			_containerList.length = 0;
			_containerList = null;
			_itemRenderer = null;
			super.dispose();
		}
	}
}
