package UI.abstract.component.control.button
{
	import UI.App;
	import UI.abstract.component.control.image.Image;
	import UI.abstract.component.control.text.ALabel;
	
	import flash.events.MouseEvent;

	public class ACheckBox extends AImageButton
	{
		/** 选择与否 **/
		protected var _selected : Boolean;

		protected var _imageSelectUp : Image;

		protected var _imageSelectOver : Image;

		protected var _imageSelectDown : Image;

		protected var _label : ALabel;

		protected var _space : int    = 0;

		protected var _minSpace : int = 0;

		private var labelText:String;
		public function ACheckBox ()
		{
			super();
			_imageSelectUp = new Image();
			_imageSelectUp.mouseEnabled = false;
			_imageSelectUp.mouseChildren = false;
			this.addChild( _imageSelectUp );
			_imageSelectOver = new Image();
			_imageSelectOver.mouseEnabled = false;
			_imageSelectOver.mouseChildren = false;
			this.addChild( _imageSelectOver );
			_imageSelectDown = new Image();
			_imageSelectDown.mouseEnabled = false;
			_imageSelectDown.mouseChildren = false;
			this.addChild( _imageSelectDown );
//			isOverKonck = false;
			App.event.addEvent( this , MouseEvent.CLICK , onMouseClick );
		}
		protected function onMouseClick ( event : MouseEvent ) : void{
			selected = !selected;
			
		}
		/**
		 * 鼠标按下
		 */
//		override protected function onMouseDown ( event : MouseEvent ) : void
//		{
//			App.event.addEvent( stage , MouseEvent.MOUSE_UP , onMouseUp , null , true );
//		}

		/**
		 * 鼠标抬起
		 */
		/*override protected function onMouseUp ( event : MouseEvent ) : void
		{
			App.event.removeEvent( stage , MouseEvent.MOUSE_UP , onMouseUp );
			var target : DisplayObject = App.ui.selectParent( event.target as DisplayObject , null , this );
			if ( target == this )
			{
				selected = !selected;
				currentState = ButtonStyle.OVER;
			}
			else
				currentState = ButtonStyle.UP;
		}*/

		override protected function draw () : void
		{
			super.draw();
			_imageSelectUp.width = _width;
			_imageSelectUp.height = _height;
			_imageSelectOver.width = _width;
			_imageSelectOver.height = _height;
			_imageSelectDown.width = _width;
			_imageSelectDown.height = _height;
			
			if ( _scale9GapW > 0 && _scale9GapH > 0 )
			{
				_imageSelectUp.set9Gap( _scale9GapW , _scale9GapH );
				_imageSelectOver.set9Gap( _scale9GapW , _scale9GapH );
				_imageSelectDown.set9Gap( _scale9GapW , _scale9GapH );
			}
			else if ( _scale9Grid )
			{
				_imageSelectUp.scale9Grid = _scale9Grid;
				_imageSelectOver.scale9Grid = _scale9Grid;
				_imageSelectDown.scale9Grid = _scale9Grid;
			}
			else
			{
				_imageSelectUp.set9Gap( 0 , 0 );
				_imageSelectOver.set9Gap( 0 , 0 );
				_imageSelectDown.set9Gap( 0 , 0 );
			}
			if ( _label )
			{
				_label.text = labelText;
				_label.x = _imageSelectUp.width + _space;
				_label.y = ( _imageSelectUp.height - _label.textFieldHeight ) >> 1;
			}
			
		}

		

		override protected function up () : void
		{
			_imageUp.visible = false;
			_imageOver.visible = false;
			_imageDown.visible = false;
			_imageSelectUp.visible = false;
			_imageSelectOver.visible = false;
			_imageSelectDown.visible = false;
			if ( _selected ){
				_imageSelectUp.visible = true
			}else{
				_imageUp.visible = true;
				
			}
		}

		override protected function over () : void
		{
			_imageUp.visible = false;
			_imageOver.visible = false;
			_imageDown.visible = false;
			_imageSelectUp.visible = false;
			_imageSelectOver.visible = false;
			_imageSelectDown.visible = false;
			if ( _selected ){
				_imageSelectOver.visible = true;
			}else{
				_imageOver.visible = true;
			}
		}

		override protected function down () : void
		{
			_imageUp.visible = false;
			_imageOver.visible = false;
			_imageDown.visible = false;
			_imageSelectUp.visible = false;
			_imageSelectOver.visible = false;
			_imageSelectDown.visible = false;
			if ( _selected ){
				_imageSelectDown.visible = true;
			}else{
				_imageDown.visible = true;
			}
		}

		public function get selected () : Boolean
		{
			return _selected;
		}

		public function set selected ( value : Boolean ) : void
		{
			if ( _selected == value )
				return;
			_selected = value;

			nextDrawGraphics();
		}

		public function set text ( str : String ) : void
		{
			if(labelText == str){
				return;
			}
			if ( !_label )
			{
				_label = new ALabel();
				_label.mouseEnabled = false;
				_label.mouseChildren = false;
				this.addChild( _label );
			}
			labelText = str;
			nextDraw();
		}
		public function get text():String{
			return labelText;
		}
		public function get space () : int
		{
			return _space;
		}

		public function set space ( value : int ) : void
		{
			if(_space == value){
				return;
			}
			_space = value;
			nextDraw();
		}

		override public function dispose () : void
		{
			
			_imageSelectUp = null;
			_imageSelectOver = null;
			_imageSelectDown = null;
			_label = null
			super.dispose();
			
		}
	}
}
