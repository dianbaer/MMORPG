package UI.abstract.component.control.text
{
	import UI.abstract.component.control.container.Box;
	
	import flash.events.Event;
	import flash.text.TextField;
	import flash.text.TextFieldAutoSize;
	import flash.text.TextFieldType;
	import flash.text.TextFormat;
	import flash.text.TextFormatAlign;

	public class BaseTextField extends Box
	{
		/** 文本 **/
		protected var _textField : TextField      = new TextField();

		/** 格式化 **/
		protected var _defaultFormat : TextFormat = new TextFormat( "宋体" , 12 );

		/** 对其类型 **/
		protected var _align : String             = TextStyle.LEFT;

		/** 是否自动调节大小 **/
		protected var _autoSize : Boolean;

		/** 是否可以输入 **/
		private var _input : Boolean;

		/** 是否可以选择 **/
		private var _select : Boolean;

		protected var _color : int                = 0;

		protected var _bold : Boolean             = false;

		protected var _size : int;

		protected var _wordWarp : Boolean;

		public function BaseTextField ()
		{
			super();
			textField.mouseWheelEnabled = false;
			this.addChild( _textField );
			updateFormat();
//			textField.border = true;
//			textField.borderColor = 0xff0000;
		}

		protected function updateFormat () : void
		{
			_textField.defaultTextFormat = _defaultFormat;
			textField.setTextFormat( _defaultFormat );
		}

		/**
		 * 对齐方式
		 */
		public function set align ( type : String ) : void
		{
			if ( _align == type )
				return;
			_align = type;
			if ( autoSize )
				autoSize = true;
			else
			{
				switch ( _align )
				{
					case TextStyle.LEFT:
						_defaultFormat.align = TextFormatAlign.LEFT;
						break;
					case TextStyle.RIGHT:
						_defaultFormat.align = TextFormatAlign.RIGHT;
						break;
					case TextStyle.CENTER:
						_defaultFormat.align = TextFormatAlign.CENTER;
						break;
				}
				updateFormat();
			}
		}

		public function get align () : String
		{
			return _align;
		}

		/**
		 * 设置文本内容
		 */
		public function set text ( value : String ) : void
		{
			textField.text = value;
			dispatchEvent(new Event(Event.CHANGE));
		}

		public function get text () : String
		{
			return textField.text;
		}

		/**
		 * 设置html文本内容
		 */
		public function set htmlText ( value : String ) : void
		{
			textField.htmlText = value;
		}

		public function get htmlText () : String
		{
			return textField.htmlText;
		}

		/**
		 * 增加文本内容
		 */
		public function appendText ( value : String ) : void
		{
			textField.appendText( value );
		}

		/**
		 * 自动适应大小
		 */
		public function set autoSize ( value : Boolean ) : void
		{
			if ( _autoSize == value )
				return;
			_autoSize = value;
			if ( _autoSize )
			{
				switch ( _align )
				{
					case TextStyle.LEFT:
						textField.autoSize = TextFieldAutoSize.LEFT;
						break;
					case TextStyle.RIGHT:
						textField.autoSize = TextFieldAutoSize.RIGHT;
						break;
					case TextStyle.CENTER:
						textField.autoSize = TextFieldAutoSize.CENTER;
						break;
				}
			}
			else
				textField.autoSize = TextFieldAutoSize.NONE;
		}

		public function get autoSize () : Boolean
		{
			return _autoSize;
		}


		/**
		 * 格式化字符串
		 */
		public static function formatText ( text : String ) : void
		{

		}

		/** 文本 **/
		protected function get textField () : TextField
		{
			return _textField;
		}

		/** 文本鼠标事件 **/
		public function set textMouse ( value : Boolean ) : void
		{
			_textField.mouseEnabled = value;
		}

//		override public function set width ( value : Number ) : void
//		{
//			if ( width == value )
//				return;
//			super.width = value;
//			textField.width = value;
//		}
//
//		override public function set height ( value : Number ) : void
//		{
//			if ( height == value )
//				return;
//			super.height = value;
//			textField.height = value;
//		}


		/**
		 * 设置是否可以输入
		 */
		public function get input () : Boolean
		{
			return _input;
		}

		public function set input ( value : Boolean ) : void
		{
			_input = value;
			if ( value )
				textField.type = TextFieldType.INPUT;
			else
				textField.type = TextFieldType.DYNAMIC;
		}

		/**
		 * 设置是否可以选择
		 */
		public function get select () : Boolean
		{
			return _select;
		}

		public function set select ( value : Boolean ) : void
		{
			_select = value;
			if ( value )
				textField.selectable = true;
			else
				textField.selectable = false;
		}

		/**
		 * 默认格式化
		 */
		public function get defaultFormat () : TextFormat
		{
			return _defaultFormat;
		}

		public function set defaultFormat ( format : TextFormat ) : void
		{
			_defaultFormat = format;
			updateFormat();
		}

		public function get color () : uint
		{
			return _color;
		}

		public function set color ( value : uint ) : void
		{
			if ( _color == value )
				return;
			_color = value;
			textField.textColor = value;
		}

		public function get bold () : Boolean
		{
			return _bold;
		}

		public function set bold ( value : Boolean ) : void
		{
			if ( _bold == value )
				return;
			_defaultFormat.bold = true;
			updateFormat();
		}

		public function get size () : int
		{
			return _size;
		}

		public function set size ( value : int ) : void
		{
			if ( _size == value )
				return;
			_defaultFormat.size = value;
			updateFormat();
		}

		/**
		 * 允许输入字符集
		 */
		public function set restrict ( value : String ) : void
		{
			_textField.restrict = value;
		}

		/**
		 * 是否只允许输入数字
		 */
		public function set isNum ( value : Boolean ) : void
		{
			_textField.restrict = "0-9";
		}

		/**
		 * 是否只允许输入字母
		 */
		public function set isString ( value : Boolean ) : void
		{
			_textField.restrict = "a-z A-Z";
		}

		/**
		 * 是否支持换行
		 */
		public function set wordWrap ( value : Boolean ) : void
		{
			_textField.multiline = value;
			_textField.wordWrap = value;
			_wordWarp = value;
		}

		public function get textWidth () : Number
		{
			return _textField.textWidth;
		}

		public function get textHeight () : Number
		{
			return _textField.textHeight;
		}

		public function set textFieldHeight ( value : Number ) : void
		{
			_textField.height = value;
		}
		public function set textFieldWidth ( value : Number ) : void
		{
			_textField.width = value;
		}
		public function get textFieldWidth () : Number
		{
			return textField.width;
		}
		
		public function get textFieldHeight () : Number
		{
			return textField.height;
		}
		override public function dispose () : void
		{
			
			_textField = null;
			_defaultFormat = null;
			super.dispose();
			
		}
	}
}
