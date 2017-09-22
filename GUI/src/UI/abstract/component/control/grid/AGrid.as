package UI.abstract.component.control.grid
{
	import UI.App;
	import UI.abstract.component.control.base.UIComponent;
	import UI.abstract.component.control.container.Container;
	import UI.abstract.component.control.image.Image;
	import UI.abstract.component.control.text.ALabel;
	import UI.abstract.utils.ColorFilters;
	
	import flash.display.DisplayObject;
	import flash.events.MouseEvent;

	public class AGrid extends BaseGrid
	{
		/** 容器 **/
		protected var _container : Container;

		/** 格子图像 **/
		protected var _gridImage : Image;

		/** 基础图像距离格子内间距 **/
		private var _gapImageToGrid : int;

		/** 数量文字 **/
		private var _textNum : ALabel;

		/** 数量为空是否显示 **/
		private var _isShowNull : Boolean = false;

		public function AGrid ()
		{
			super();
			isNext = false;
			_container = new Container();
			this.addChild( _container );
		}

		override protected function draw () : void
		{
			super.draw();

			_container.setPosition( _gapImageToGrid , _gapImageToGrid );
			_container.setSize( _width - _gapImageToGrid * 2 , _height - _gapImageToGrid * 2 );
			if ( _gridImage ){
				_gridImage.url = _data.imageUrl;
				_gridImage.setSize( _width - _gapImageToGrid * 2 , _height - _gapImageToGrid * 2 );
			}
				
			if ( _textNum )
			{
				_textNum.text = _data.num.toString();
				_textNum.x = _container.width - _textNum.textFieldWidth;
				_textNum.y = _container.height - _textNum.textFieldHeight;
				if ( _data.num == null && !isShowNull )
					_textNum.visible = false;
				else
				{
					_textNum.visible = true;
				}
			}
		}
		/**
		 * 是否能可以接受
		 */
		//override public function canDrop ( dragObject : UIComponent ) : Boolean
		//{
		//	return (dragObject is BaseGrid) && dragObject != this;
		//}

		/**
		 * 接受拖动对象
		 */
		//override public function drop ( dragObject : UIComponent ) : void
		//{
//			var grid : BaseGrid = dragObject as BaseGrid;
//			var obj : GridData = grid.data;
//			grid.data = _data;
//			this.data = obj;
		//}
		override public function set data ( value : GridData ) : void
		{
			//if(_data == value){
			//	return;
			//}
			_data = value;
			if ( !_gridImage )
			{
				_gridImage = new Image();
				_container.addChild( _gridImage );
			}
			if ( !_textNum )
			{
				_textNum = new ALabel();
				_textNum.bold = true;
				_textNum.color = 0x00ff00;
				_textNum.filters = [ColorFilters.colour_Black];
				_container.addChild( _textNum );
			}
			nextDraw();
			
		}
		/** 格子基本图像地址 **/
		public function get imageUrl () : String
		{
			return _data.imageUrl;
		}

		/**
		 * @private
		 */
		public function set imageUrl ( value : String ) : void
		{
			if(_data.imageUrl == value){
				return;
			}
			_data.imageUrl = value;
			if ( !_gridImage )
			{
				_gridImage = new Image();
				_container.addChild( _gridImage );
			}
			nextDraw();
		}

		/** 基础图像距离格子内间距 **/
		public function get gapImageToGrid () : int
		{
			return _gapImageToGrid;
		}

		/**
		 * @private
		 */
		public function set gapImageToGrid ( value : int ) : void
		{
			if(_gapImageToGrid == value){
				return;
			}
			_gapImageToGrid = value;
			nextDraw();
		}

		//public function clear () : void
		//{
		//	_data.clear();
		//	nextDraw();
		//}

		/** 数量 **/
		public function get num () : String
		{
			return _data.num;
		}

		/**
		 * @private
		 */
		public function set num ( value : String ) : void
		{
			if(_data.num == value){
				return;
			}
			_data.num = value;
			if ( !_textNum )
			{
				_textNum = new ALabel();
				_textNum.bold = true;
				_textNum.color = 0x00ff00;
				_textNum.filters = [ColorFilters.colour_Black];
				_container.addChild( _textNum );
			}
			
			nextDraw();
		}

		/** 数量为0是否显示 **/
		public function get isShowNull () : Boolean
		{
			return _isShowNull;
		}

		/**
		 * @private
		 */
		public function set isShowNull ( value : Boolean ) : void
		{
			if(_isShowNull == value){
				return;
			}
			_isShowNull = value;
			nextDraw();
		}

		override protected function onDrag ( e : MouseEvent ) : void
		{
			var target : DisplayObject = App.ui.selectParent( e.target as DisplayObject , null,this );
			if ( !_gridImage || !_data.isDrog() || target != this)
				return;
			App.drag.doDrag( this , _gridImage , _data , false , false , true , true );
		}

		override public function dispose () : void
		{
			_container = null;
			_gridImage = null;
			_textNum = null;

			super.dispose();
		}
	}
}
