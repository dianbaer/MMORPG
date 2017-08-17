package UI.abstract.component.control.window
{
	import UI.App;
	import UI.abstract.component.control.button.BaseButton;
	import UI.abstract.component.control.container.Box;
	import UI.abstract.component.control.container.Container;
	import UI.abstract.component.control.text.ALabel;
	import UI.abstract.component.control.text.TextStyle;
	import UI.abstract.component.event.WindowEvent;
	
	import flash.display.DisplayObject;
	import flash.events.MouseEvent;

	public class AWindow extends Box
	{
		/** 关闭按钮 **/
		private var _closeBtn : BaseButton;

		/** 容器 **/
		protected var _content : Container;

		/** 容器起始位置 **/
		private var _contentX : int = 0;

		private var _contenY : int = 0;

		/** 关闭按钮边距 **/
		private var _gapClosBtnH : int = 0;

		private var _gapClosBtnV : int = 0;

		private var _titleLabel : ALabel;
		
		private var _titleAlign : String = TextStyle.CENTER;
		
		private var labelText:String;
		public function AWindow ()
		{
			super();
			drag = true;
			
		}

		override protected function draw () : void
		{
			super.draw();
			_closeBtn.x = _width - _closeBtn.width - _gapClosBtnH;
			_closeBtn.y = _gapClosBtnV;

			_content.x = _contentX;
			_content.y = _contenY;

			if ( _titleLabel )
			{
				_titleLabel.text = labelText;
				switch(_titleAlign)
				{
					case TextStyle.CENTER:
						_titleLabel.x = ( _width - _titleLabel.textFieldWidth ) >> 1;
						_titleLabel.y = 15;
						break;
					case TextStyle.LEFT:
						_titleLabel.x = 2;
						_titleLabel.y = 15;
						break;
				}
			}
			
		}

		/** 关闭按钮 **/
		public function get closeBtn () : BaseButton
		{
			return _closeBtn;
		}

		/**
		 * @private
		 */
		public function set closeBtn ( value : BaseButton ) : void
		{
			if ( _closeBtn )
			{
				_closeBtn.dispose();
				_closeBtn = null;
			}

			if(value){
				_closeBtn = value;
				addChild( _closeBtn );
				App.event.addEvent( _closeBtn , MouseEvent.CLICK , onClose );
			}
			
		}

		/** 容器 **/
		public function get content () : Container
		{
			return _content;
		}

		/**
		 * @private
		 */
		public function set content ( value : Container ) : void
		{
			if ( _content )
			{
				_content.dispose();
				_content = null;
			}
			if(value){
				_content = value;
				addChild( _content );
			}
		}

		/**
		 * 添加子对象
		 */
		public function addChildToContent ( child : DisplayObject ) : DisplayObject
		{
			return _content.addChild( child );
		}

		public function addChildAtToContent ( child : DisplayObject , index : int ) : DisplayObject
		{
			return _content.addChildAt( child , index );
		}

		/**
		 * 移出子对象
		 */
		public function removeChildToContent ( child : DisplayObject ) : DisplayObject
		{
			return _content.removeChild( child );
		}

		public function removeChildAtToContent ( index : int ) : DisplayObject
		{
			return _content.removeChildAt( index );
		}

		/**
		 * 更改层级位置
		 */
		public function setChildIndexToContent ( child : DisplayObject , index : int ) : void
		{
			_content.setChildIndex( child , index );
		}

		/** 容器边距 **/
		public function get contentX () : int
		{
			return _contentX;
		}

		/**
		 * @private
		 */
		public function set contentX ( value : int ) : void
		{
			if(_contentX == value){
				return;
			}
			_contentX = value;
			nextDraw();
		}

		public function get contentY () : int
		{
			return _contenY;
		}

		public function set contentY ( value : int ) : void
		{
			if(_contenY == value){
				return;
			}
			_contenY = value;
			nextDraw();
		}

		/** 关闭按钮边距 **/
		public function get gapClosBtnH () : int
		{
			return _gapClosBtnH;
		}

		/**
		 * @private
		 */
		public function set gapClosBtnH ( value : int ) : void
		{
			if(_gapClosBtnH == value){
				return;
			}
			_gapClosBtnH = value;
			nextDraw();
		}

		public function get gapClosBtnV () : int
		{
			return _gapClosBtnV;
		}

		public function set gapClosBtnV ( value : int ) : void
		{
			if(_gapClosBtnV == value){
				return;
			}
			_gapClosBtnV = value;
			nextDraw();
		}
		
		/**
		 * 设置窗口标题
		 */
		public function set text ( value : String ) : void
		{
			if(labelText == value){
				return;
			}
			labelText = value;
			if ( !_titleLabel )
			{
				_titleLabel = new ALabel();
				_titleLabel.size = 15;
				_titleLabel.bold = true;
				_titleLabel.color = 0xFFFFFF
				addChild( _titleLabel );
			}
			
			nextDraw();
		}

		/**
		 * 点击关闭按钮
		 */
		public function onClose ( e : MouseEvent ) : void
		{
			parent && parent.removeChild( this );
			dispatchEvent( new WindowEvent( WindowEvent.CLOSE ) );
		}
		
		override protected function onDrag ( e : MouseEvent ) : void
		{
			super.onDrag(e);
			if(parent){
				//if(parent.getChildIndex(this) == parent.numChildren){
					parent.setChildIndex(this,parent.numChildren-1);
				//}
			}
		}
		
		public function set titleAlign(value:String):void
		{
			if(_titleAlign == value){
				return;
			}
			_titleAlign = value;
			nextDraw();
		}
		
		override public function dispose () : void
		{
			
			_closeBtn = null;
			_content = null;
			_titleLabel = null;
			super.dispose();
		}
	}
}
