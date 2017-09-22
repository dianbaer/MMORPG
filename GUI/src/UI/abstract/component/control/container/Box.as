package UI.abstract.component.control.container
{
	import UI.abstract.component.control.image.Image;

	/**
	 * 有背景的容器
	 */
	public class Box extends Container
	{
		/** ui背景 **/
		protected var _bg : Image;
		protected var _bgUrl : String;
		public function Box()
		{
			super();
		}
		/**
		 * 设置背景url
		 */
		public function set bgUrl ( url : String ) : void
		{
			if(_bgUrl == url){
				return;
			}
			if ( !_bg )
			{
				_bg = new Image();
				addChildAt( _bg , 0 );
			}
			_bgUrl = url;
			_bg.url = _bgUrl;
			nextDraw();
			
		}
		override protected function draw () : void
		{
			super.draw();
			if ( _bg )
			{
				//get大小，有可能用到重写的宽度
				_bg.width = _width;
				_bg.height = _height;
				if ( _scale9GapW > 0 && _scale9GapH > 0 )
				{
					_bg.set9Gap( _scale9GapW , _scale9GapH );
				}
				else if ( _scale9Grid )
				{
					_bg.scale9Grid = _scale9Grid;
				}
				else
				{
					_bg.set9Gap( 0 , 0 );
				}
				_bg.url = _bgUrl;
				
			}
		}
		override public function dispose () : void
		{
			_bg = null;
			super.dispose();
		}
	}
}	