package UI.theme.defaulttheme.list
{
	import UI.App;
	import UI.abstract.component.control.base.UIComponent;
	import UI.abstract.component.control.container.Container;
	import UI.abstract.component.control.list.IItemRenderer;
	import UI.abstract.component.control.panel.Panel;
	import UI.abstract.component.control.text.TextStyle;
	import UI.theme.defaulttheme.Skin;
	import UI.theme.defaulttheme.button.ToggleButton;
	
	import flash.display.DisplayObject;

	public class ListObject extends ToggleButton implements IItemRenderer
	{
		protected var _data : Object;

		protected var _itemIndex : int = 0;

		protected var _minHeight : int = 25;

		protected var _isFix : Boolean = false;

		/**容器**/
		private var container:Panel;
		/**传进来子对象的原来大小**/
		//private var childWidth:Number;
		/**传进来子对象的原来大小**/
		//private var childHeight:Number;
		public function ListObject ( skin : String = Skin.BUTTON )
		{
			super(  skin );
			set9Gap( 8 , 8 );
			//_labelMouse = true;
			App.event.removeEventByObj( this );
			//bgUrl = Skin.getList( Skin.LIST_ITEM_BG )[ 0 ]; //"ui/ListItem_Bg.png";
			container = new Panel();
			addChild(container);

		}
		override protected function draw():void{
			super.draw();
			if ( data is DisplayObject )
			{
				/*if ( isFix )
					data.height = _height;*/
				//这是宽度不可能小于等于0，因为没有显示区域，不会触发的
				/*if ( data.width > _width )
					data.width = _width;*/
				container.width = _width;
				container.height = _height;
				switch ( _align )
				{
					case TextStyle.CENTER:
						data.x = ( _width - data.width ) >> 1;
						data.y = ( _height - data.height ) >> 1;
						break;
					case TextStyle.LEFT:
						data.x = 0;
						data.y = ( _height - data.height ) >> 1;
						break;
					case TextStyle.RIGHT:
						data.x = _width - data.width;
						data.y = ( _height - data.height ) >> 1;
						break;
				}
			}
			
		}
		public function forceUpdate():void{
			text = "";
			//var isSame:Boolean = false;
			container.content.removeChildren(0,container.content.numChildren-1)
			/*if(container.numChildren == 1){
				if(container.getChildAt(0) != data){
					container.removeChildAt(0);
				}else{
					isSame = true;
				}
			}*/
			if ( data is String || data is int )
			{
				text = String( data );
			}
			else if ( data is DisplayObject )
			{
				var disObj : DisplayObject = data as DisplayObject;
				//if(!isSame){
				container.content.addChild( disObj );
				/*	childWidth = disObj.width;
					childHeight = disObj.height;
				}
				disObj.width = childWidth;
				disObj.height = childHeight;*/
				//高度
				/*if ( isFix )
					data.height = _height;
				else if ( data.height > _height )
					height = data.height;*/
				
				//宽度
				/*if ( disObj.width > _width && _width > 0)
					disObj.width = _width;*/
			}
			//不能小于最小高度
			if(_height<_minHeight){
				height = _minHeight;
			}
			nextDraw();
		}
		public function get itemIndex () : int
		{
			return _itemIndex;
		}

		public function set itemIndex ( value : int ) : void
		{
			_itemIndex = value;
		}

		public function get data () : Object
		{
			return _data;
		}

		public function set data ( value : Object ) : void
		{
			if ( _data === value )
				return;

			_data = value;
			
			//forceUpdate();
			nextDraw();
			

			
			
		}
		public function set isFix ( b : Boolean ) : void
		{
			if(_isFix == b){
				return;
			}
			_isFix = b;
			//forceUpdate();
			//nextDraw();
		}
		/*override public function set align ( value : String ) : void
		{
			if(_align == value){
				return;
			}
			_align = value;
			forceUpdate();
			nextDraw();
		}*/
		public function get isFix () : Boolean
		{
			return _isFix;
		}
		override public function dispose () : void
		{
			container.content.removeChildren(0,container.content.numChildren-1)
			container = null;
			_data = null;
			super.dispose();
		}
	}
}
