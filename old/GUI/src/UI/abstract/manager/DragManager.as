package UI.abstract.manager
{
	import UI.App;
	import UI.abstract.component.control.IDrop;
	import UI.abstract.component.control.base.UIComponent;
	import UI.abstract.component.event.DragEvent;

	import flash.display.Bitmap;
	import flash.display.BitmapData;
	import flash.display.DisplayObject;
	import flash.display.Sprite;
	import flash.events.MouseEvent;
	import flash.geom.Point;
	import flash.geom.Rectangle;

	public class DragManager
	{
		private var root : Sprite;

		private var _dragObj : UIComponent;

		private var _drawObj : DisplayObject;

		private var _data : Object;

		private var _bitmap : Bitmap;

		/** 改变原始图透明度 **/
		private var _changeAlpha : Number      = 0.5;

		private var _dragAlpha : Number        = 1;

		private var _dragGray : Boolean        = false;

		private var _isSetPos : Boolean;

		private var _isDragObj : Boolean;

		private var _isAlpha : Boolean;

		private var _isGray : Boolean;

		private var _dragStartPos : Point;

		/** 拖动对象原始坐标 **/
		private var _dragSrcPos : Point;

		private var _mouseStartPos : Point;

		private var _initDrag : Boolean;

		public function DragManager ()
		{
			if ( _instance != null )
				throw Error( SINGLETON_MSG );
			_instance = this;
			this.root = App.ui.root;
			_bitmap = new Bitmap();
		}

		/**
		 * 当鼠标按下选择某个对象时，添加需要拖拽对象，只有鼠标开始移动时才会触发拖拽
		 * @param drawObj 绘制鼠标跟随图像，为null则绘制拖动对象
		 * @param data 携带数据
		 * @param isSetPos 是否设置坐标
		 * @param isDragObj 是否直接拖动对象
		 * @param isAlpha 是否半透明原始图
		 * @param isGray 是否置灰原始图
		 */
		public function doDrag ( dragObj : UIComponent , drawObj : DisplayObject = null , data : Object = null , isSetPos : Boolean = true , isDragObj : Boolean =
								 true , isAlpha : Boolean = false , isGray : Boolean = false ) : void
		{
			if ( !dragObj )
				return;

			_drawObj = drawObj ? drawObj : dragObj;
			_data = data;
			_isSetPos = isSetPos;
			_isAlpha = isAlpha;
			_isGray = isGray;
			_dragObj = dragObj;
			_isDragObj = isDragObj;
			_dragAlpha = dragObj.alpha;
			_dragGray = dragObj.gray;
			_dragStartPos = _dragObj.localToGlobal( new Point() ).clone();
			_dragSrcPos = new Point( _dragObj.x , _dragObj.y );
			_mouseStartPos = new Point( root.stage.mouseX , root.stage.mouseY );
			_initDrag = false;
			if ( _bitmap.bitmapData )
			{
				_bitmap.bitmapData.dispose();
				_bitmap.bitmapData = null;
			}

			App.event.addEvent( this.root.stage , MouseEvent.MOUSE_MOVE , onMouseMove );
			App.event.addEvent( this.root.stage , MouseEvent.MOUSE_UP , onMouseUp );
		}

		private function onMouseMove ( event : MouseEvent ) : void
		{
			if ( !_initDrag )
			{
				if ( !_isDragObj && !_bitmap.bitmapData )
				{
					var bitmapData : BitmapData = new BitmapData( _drawObj.width , _drawObj.height, true, 0 );
					bitmapData.draw( _drawObj );
					_bitmap.bitmapData = bitmapData;
					_bitmap.width = bitmapData.width;
					_bitmap.height = bitmapData.height;
					this.root.addChild( _bitmap );
				}

				if ( _isAlpha )
					_dragObj.alpha = _changeAlpha;
				if ( _isGray )
					_dragObj.gray = true;

				_initDrag = true;
			}


			if ( !_isDragObj )
			{
				_bitmap.x = _dragStartPos.x + ( event.stageX - _mouseStartPos.x );
				_bitmap.y = _dragStartPos.y + ( event.stageY - _mouseStartPos.y );
			}
			else
			{
				_dragObj.x = _dragSrcPos.x + ( event.stageX - _mouseStartPos.x );
				_dragObj.y = _dragSrcPos.y + ( event.stageY - _mouseStartPos.y );
			}
		}

		private function onMouseUp ( event : MouseEvent ) : void
		{
			if ( !_initDrag )
			{
				App.event.removeEvent( this.root.stage , MouseEvent.MOUSE_MOVE , onMouseMove );
				App.event.removeEvent( this.root.stage , MouseEvent.MOUSE_UP , onMouseUp );
				_dragObj = null;
				_drawObj = null;
				_data = null;
				return;
			}
			var target : DisplayObject = event.target as DisplayObject;
			var drop : IDrop;
			while ( target != this.root.stage )
			{
				drop = target as IDrop;
				if ( drop && drop.canDrop( _dragObj as UIComponent ) )
				{
					drop.drop( _dragObj );
					break;
				}
				target = target.parent;
			}

			if ( _isSetPos )
			{
				_dragObj.x = _dragSrcPos.x + ( event.stageX - _mouseStartPos.x );
				_dragObj.y = _dragSrcPos.y + ( event.stageY - _mouseStartPos.y );
			}
			else
			{
				_dragObj.x = _dragSrcPos.x;
				_dragObj.y = _dragSrcPos.y;
			}

			if ( _isAlpha )
				_dragObj.alpha = _dragAlpha;
			if ( _isGray )
				_dragObj.gray = _dragGray;
			_dragObj.dispatchEvent( new DragEvent( DragEvent.DRAG_COMPLETE , _dragObj , drop as UIComponent ) );

			if ( !_isDragObj )
			{
				if ( _bitmap.bitmapData )
				{
					_bitmap.bitmapData.dispose();
					_bitmap.bitmapData = null;
				}
				//if ( root.contains(_bitmap) )
				root.removeChild( _bitmap );
			}

			App.event.removeEvent( this.root.stage , MouseEvent.MOUSE_MOVE , onMouseMove );
			App.event.removeEvent( this.root.stage , MouseEvent.MOUSE_UP , onMouseUp );

			_dragObj = null;
			_drawObj = null;
			_data = null;
		}

		public static function get instance () : DragManager
		{
			if ( _instance == null )
				_instance = new DragManager();
			return _instance;
		}

		protected static var _instance : DragManager;

		protected const SINGLETON_MSG : String = "DragManager Singleton already constructed!";
	}
}
