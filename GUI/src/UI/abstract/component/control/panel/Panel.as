package UI.abstract.component.control.panel
{
	import UI.abstract.component.control.container.Box;
	import UI.abstract.component.control.container.Container;
	
	import flash.display.Shape;

	public class Panel extends Box
	{
		private var _content : Container;

		/** 遮罩 **/
		protected var _mask : Shape;

		public function Panel ()
		{
			super();
			_content = new Container();
			addChild( _content );

			_mask = new Shape();
			addChild( _mask );

			_content.mask = _mask;
			noneHaveVisibleAreaDraw = true;
		}

		public function removeContentAll () : void
		{
			while ( _content.numChildren )
			{
				_content.removeChildAt( _content.numChildren - 1 );
			}
		}

		override protected function draw () : void
		{
			super.draw();
			_mask.graphics.clear();
			_mask.graphics.beginFill( 0 );
			_mask.graphics.drawRect( 0 , 0 , _width , _height );
			_mask.graphics.endFill();
			
		}

		/** 容器 **/
		public function get content () : Container
		{
			return _content;
		}

		override public function dispose () : void
		{
			
			_mask.graphics.clear();
			_mask = null;
			_content.mask = null;
			_content = null;
			super.dispose();
			

		}

		/**
		 * 水平内容是否超出范围
		 */
		public function get isOutH () : Boolean
		{
			return _content.getAllChildrenSize().x - _width > 0;
		}

		/**
		 * 垂直内容是否超出范围
		 */
		public function get isOutV () : Boolean
		{
			return _content.getAllChildrenSize().y - _height > 0;
		}

	}
}
