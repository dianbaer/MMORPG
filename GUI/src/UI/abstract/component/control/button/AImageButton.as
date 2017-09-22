package UI.abstract.component.control.button
{
	import UI.abstract.component.control.image.Image;

	public class AImageButton extends BaseButton
	{

		protected var _imageUp : Image;

		protected var _imageOver : Image;

		protected var _imageDown : Image;

		public function AImageButton ()
		{
			super();
			_imageUp = new Image();
			_imageUp.mouseEnabled = false;
			_imageUp.mouseChildren = false;
			this.addChild( _imageUp );
			_imageOver = new Image();
			_imageOver.mouseEnabled = false;
			_imageOver.mouseChildren = false;
			this.addChild( _imageOver );
			_imageDown = new Image();
			_imageDown.mouseEnabled = false;
			_imageDown.mouseChildren = false;
			this.addChild( _imageDown );
		}

		override protected function draw () : void
		{
			super.draw();
			_imageUp.width = _width;
			_imageUp.height = _height;
			_imageOver.width = _width;
			_imageOver.height = _height;
			_imageDown.width = _width;
			_imageDown.height = _height;
			
			if ( _scale9GapW > 0 && _scale9GapH > 0 )
			{
				_imageUp.set9Gap( _scale9GapW , _scale9GapH );
				_imageOver.set9Gap( _scale9GapW , _scale9GapH );
				_imageDown.set9Gap( _scale9GapW , _scale9GapH );
			}
			else if ( _scale9Grid )
			{
				_imageUp.scale9Grid = _scale9Grid;
				_imageOver.scale9Grid = _scale9Grid;
				_imageDown.scale9Grid = _scale9Grid;
			}
			else
			{
				_imageUp.set9Gap( 0 , 0 );
				_imageOver.set9Gap( 0 , 0 );
				_imageDown.set9Gap( 0 , 0 );
			}
			

		}

		override protected function up () : void
		{
			_imageUp.visible = true;
			_imageOver.visible = false;
			_imageDown.visible = false;
		}

		override protected function over () : void
		{
			_imageUp.visible = false;
			_imageOver.visible = true;
			_imageDown.visible = false;
		}

		override protected function down () : void
		{
			_imageUp.visible = false;
			_imageOver.visible = false;
			_imageDown.visible = true;
		}
		override public function dispose () : void
		{
			
			_imageUp = null;
			_imageDown = null;
			_imageOver = null;
			super.dispose();
		}
	}
}
