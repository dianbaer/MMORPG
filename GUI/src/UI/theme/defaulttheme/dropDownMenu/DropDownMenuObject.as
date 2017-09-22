package UI.theme.defaulttheme.dropDownMenu
{
	import UI.abstract.component.control.dropDownMenu.IDropDownMenuItemRenderer;
	import UI.abstract.component.control.dropDownMenu.MenuData;
	import UI.abstract.component.control.image.Image;
	import UI.abstract.component.data.DataProvider;
	import UI.theme.defaulttheme.list.ListObject;

	public class DropDownMenuObject extends ListObject implements IDropDownMenuItemRenderer
	{
		private var _iconArrow : Image;

		private var _iconPrefix : Image;

		private var _iconSelect : Image;

		/** 是否有选中效果图标 **/
		private var _isSelectIcon : Boolean; // 注意：选中效果图标 和前缀图标只能同时选中一个

		private const _gapPre : int = 20

		public function DropDownMenuObject ()
		{
			super();
			//labelMouse = false;
			gapToLabelMin = 6;
		}

		/*override protected function down () : void
		{
//			if ( _selected )
//				_image.bitmapData = _imageDown;
//			else
			_imageUp.visible = true;
			_imageOver.visible = false;
			_imageDown.visible = false;
			//_image.bitmapData = _imageUp;
		}*/
		override protected function draw():void{
			
			super.draw();
			if(_iconArrow){
				_iconArrow.x = _width - _iconArrow.width;
				_iconArrow.y = ( _height - _iconArrow.height ) >> 1;
			}
			if(_iconPrefix){
				_iconPrefix.x = 5;
				_iconPrefix.y = ( _height - _iconPrefix.height ) >> 1;
			}
			if(_iconSelect){
				_iconSelect.x = 5;
				_iconSelect.y = ( height - _iconSelect.height ) >> 1;
			}
			
			
		}
		override public function forceUpdate():void{
			var menuData : MenuData = _data as MenuData;
			text = menuData.text as String;
			
			if ( menuData.iconPrefix != null )
			{
				if ( !_iconPrefix )
				{
					_iconPrefix = new Image();
					//App.event.addEvent( _iconPrefix , UIEvent.RESIZE_UI , onIconResize );
					this.addChild( _iconPrefix );
				}
				//if ( menuData.iconPrefix != "" )
				_iconPrefix.url = menuData.iconPrefix;
				gapToLabelMin = _gapPre;
				_iconPrefix.visible = true;
			}
			else
			{
				if ( _iconPrefix )
				{
					_iconPrefix.visible = false;
					gapToLabelMin = 2;
				}
			}
			if ( _isSelectIcon )
				gapToLabelMin = _gapPre;
			
			if ( menuData.items && menuData.items.length > 0 )
			{
				if ( !_iconArrow )
				{
					_iconArrow = new Image();
					_iconArrow.width = 9;
					_iconArrow.height = 17;
					//App.event.addEvent( _iconArrow , UIEvent.RESIZE_UI , onIconResize );
					_iconArrow.url = "arrow.png";
					this.addChild( _iconArrow );
				}
				_iconArrow.visible = true;
			}
			else
			{
				if ( _iconArrow )
					_iconArrow.visible = false;
			}
			//不能小于最小高度
			if(_height<_minHeight){
				height = _minHeight;
			}
			nextDraw();
		}
		
		override public function set selected ( value : Boolean ) : void
		{
			if ( _selected == value )
				return;

			super.selected = value;

			if ( _isSelectIcon )
			{
				if ( !_iconSelect )
				{
					_iconSelect = new Image();
					_iconSelect.width = 7;
					_iconSelect.height = 7;
					//App.event.addEvent( _iconSelect , UIEvent.RESIZE_UI , onIconResize );
					_iconSelect.url = "ui/DropDownMenu_ItemIconPrefix.png";
					this.addChild( _iconSelect );
				}

				if ( _selected )
				{
					_iconSelect.visible = true;
					gapToLabelMin = _gapPre;
				}
				else
				{
					_iconSelect.visible = false;
					gapToLabelMin = _gapPre;
				}
			}
			else
			{
				if ( _iconSelect )
				{
					_iconSelect.visible = false;
					gapToLabelMin = 2;
				}
			}
			nextDraw();
		}

		/*private function onIconResize ( e : UIEvent ) : void
		{
			if ( e.target == _iconArrow )
			{
				App.event.removeEventByType( _iconArrow , UIEvent.RESIZE_UI );
				_iconArrow.x = width - _iconArrow.width;
				_iconArrow.y = ( height - _iconArrow.height ) >> 1;
			}

			if ( e.target == _iconPrefix )
			{
				App.event.removeEventByType( _iconPrefix , UIEvent.RESIZE_UI );
				_iconPrefix.x = 5;
				_iconPrefix.y = ( height - _iconPrefix.height ) >> 1;
			}

			if ( e.target == _iconSelect )
			{
				App.event.removeEventByType( _iconSelect , UIEvent.RESIZE_UI );
				_iconSelect.x = 5;
				_iconSelect.y = ( height - _iconSelect.height ) >> 1;
			}
		}*/

		/**
		 * 是否有子项
		 */
		public function hasItems () : Boolean
		{
			if ( data is MenuData )
			{
				if ( MenuData( data ).items && MenuData( data ).items.length > 0 )
					return true;
			}

			return false;
		}

		/**
		 * 获得子项
		 */
		public function getItems () : DataProvider
		{
			if ( hasItems() )
				return MenuData( data ).items;
			return null;
		}

		/**
		 * 是否有选中效果图标
		 */
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

		override public function dispose () : void
		{
			if ( _iconArrow )
			{
				_iconArrow.dispose();
				_iconArrow = null;
			}

			if ( _iconPrefix )
			{
				_iconPrefix.dispose();
				_iconPrefix = null;
			}
			if ( _iconSelect )
			{
				_iconSelect.dispose();
				_iconSelect = null;
			}

//			if ( data is MenuData )
//				MenuData(data).dispose();
			super.dispose();
		}

	}
}
