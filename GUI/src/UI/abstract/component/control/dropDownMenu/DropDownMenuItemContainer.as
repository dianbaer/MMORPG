package UI.abstract.component.control.dropDownMenu
{
	import UI.App;
	import UI.abstract.component.control.list.IItemRenderer;
	import UI.abstract.component.control.list.ListItemContainer;
	import UI.abstract.component.event.DropDownMenuEvent;
	
	import flash.display.DisplayObject;
	import flash.events.MouseEvent;

	public class DropDownMenuItemContainer extends ListItemContainer
	{
		/** 层级 **/
		private var _level : int;

		/** 是否有选中效果图标 **/
		private var _isSelectIcon : Boolean;

		public function DropDownMenuItemContainer ()
		{
			super();
		}

		override protected function drawItem ( item : IItemRenderer ) : void
		{
			IDropDownMenuItemRenderer( item ).isSelectIcon = _isSelectIcon;
			item.forceUpdate();
		}

		/*override protected function setState ( index : int , state : String ) : void
		{
			if ( _itemsArray[ index ] == null )
				return;
			super.setState( index , state );

			var item : IDropDownMenuItemRenderer = _itemsArray[ index ];

			if ( item is IDropDownMenuItemRenderer )
			{
				if ( state == ListStyle.OVER )
					dispatchEvent( new DropDownMenuEvent( DropDownMenuEvent.SELECT_CHANGE , item , _level ) );
				else if ( state == ListStyle.SELECTED )
				{
					if ( !item.hasItems() )
						dispatchEvent( new DropDownMenuEvent( DropDownMenuEvent.SELECT_COMPLETE , item , _level ) );
				}
			}
		}*/
		/**
		 * 鼠标离开
		 */
		/*override protected function onMouseOut ( event : MouseEvent ) : void
		{
			super.onMouseOut(event);
			var target : DisplayObject = App.ui.selectParent( event.target as DisplayObject , ItemClass );
			
			if ( target is ItemClass )
			{
				
			}
			
		}*/
		
		/**
		 * 鼠标进入
		 */
		override protected function onMouseOver ( event : MouseEvent ) : void
		{
			super.onMouseOver(event);
			var target : DisplayObject = App.ui.selectParent( event.target as DisplayObject , ItemClass );
			
			if ( target is ItemClass )
			{
				dispatchEvent( new DropDownMenuEvent( DropDownMenuEvent.SELECT_CHANGE , target as IDropDownMenuItemRenderer , _level ) );
			}
		}
		
		/**
		 * 选中
		 */
		override protected function onClickItem ( e : MouseEvent ) : void
		{
			//super.onClickItem(e);
			var target : DisplayObject = App.ui.selectParent( e.target as DisplayObject , ItemClass );
			
			if ( target is ItemClass )
			{
				if ( !(target as IDropDownMenuItemRenderer).hasItems() ){
					if ( selectedItem == target && !_isSelectDispatch ){
						
					}else{
						selectedIndex = IItemRenderer( target ).itemIndex;
					}
					dispatchEvent( new DropDownMenuEvent( DropDownMenuEvent.SELECT_COMPLETE , target as IDropDownMenuItemRenderer , _level ,(target as IDropDownMenuItemRenderer).data) );
				}
					
			}
		}
		/** 层级 **/
		public function get level () : int
		{
			return _level;
		}

		/**
		 * @private
		 */
		public function set level ( value : int ) : void
		{
			_level = value;
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
			if(_isSelectIcon == value){
				return;
			}
			_isSelectIcon = value;
			nextDrawGraphics();
		}

	}
}
