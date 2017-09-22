package UI.abstract.component.control.text
{
	import flash.text.TextField;
	import flash.text.TextFieldAutoSize;

	public class ATextInput extends BaseTextField
	{
		/** 文字和背景边距 **/
		protected var _gapH : int       = 0;

		/** 内部textField额外减少宽度 **/
		protected var _extraWidth : int = 0;

		private static var tmpTextField : TextField;

		private static var mulitTextField : TextField;

		public function ATextInput ()
		{
			super();
			enabled = true;
			textMouse = true;
			input = true;
			align = TextStyle.LEFT;
			tmpTextField = new TextField();
			tmpTextField.mouseEnabled = false;
			tmpTextField.tabEnabled = false;
			tmpTextField.autoSize = TextFieldAutoSize.LEFT;
//			textField.border = true

		}

		override protected function draw () : void
		{
			super.draw();
			var h : Number;
			tmpTextField.defaultTextFormat = defaultFormat;
			tmpTextField.text = "A";
			h = tmpTextField.height + 1;

			textFieldWidth = _width - ( _gapH << 1 ) - _extraWidth;
			_textField.x = _gapH;
			if ( !_wordWarp )
			{
				textFieldHeight = h;
				_textField.y = ( _height - h ) >> 1;
			}else{
				textFieldHeight = _height;
				if(textFieldHeight > _height){
					height = textFieldHeight;
				}
			}
//			_bg.graphics.beginFill(0xff0000);
//			_bg.graphics.drawRect(0,0,width,height);
		}

		override public function get textFieldHeight () : Number
		{
			if ( !mulitTextField )
			{
				mulitTextField = new TextField();
				mulitTextField.autoSize = TextFieldAutoSize.LEFT;
				mulitTextField.wordWrap = true;
				mulitTextField.multiline = true;
			}
			mulitTextField.defaultTextFormat = defaultFormat;
			mulitTextField.width = width;
			mulitTextField.text = text;
			return mulitTextField.height;
		}

//		/**
//		 * 是否可以选择输入
//		 */
//		public function set select ( value : Boolean ) : void
//		{
//			input = value;
//		}

		override public function dispose () : void
		{
			
			
			tmpTextField = null;
			mulitTextField = null;
			super.dispose();
		}

		/** 内部textField额外减少宽度 **/
		public function get extraWidth () : int
		{
			return _extraWidth;
		}

		/**
		 * @private
		 */
		public function set extraWidth ( value : int ) : void
		{
			if(_extraWidth == value){
				return;
			}
			_extraWidth = value;
			
			if ( _extraWidth < 0 )
				_extraWidth = 0;
			
			nextDraw();
		}

		/** 文字和背景边距 **/
		public function get gapH () : int
		{
			return _gapH;
		}

		/**
		 * @private
		 */
		public function set gapH ( value : int ) : void
		{
			if(_gapH == value){
				return;
			}
			_gapH = value;
			nextDraw();
		}


	}
}
