package UI.abstract.component.control.button
{
	import UI.abstract.component.control.text.ALabel;
	import UI.abstract.component.control.text.TextStyle;

	public class ALabelImageButton extends AImageButton
	{
		protected var _label : ALabel;

		protected var _align : String    = "";
		
		protected var _color : uint;

		//protected var _labelMouse : Boolean;

		/** 文字水平边距 **/
		//private var _gapToLabelH : int   = 6;

		/** 文字垂直边距 **/
		//private var _gapToLabelV : int   = 2;

		/** 文字在TextStyle.LEFT和 TextStyle.RIGHT 类型时 最小边距 **/
		private var _gapToLabelMin : int = 2;

		private var labelText:String;
		public function ALabelImageButton ()
		{
			super();
			_align = TextStyle.CENTER
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

		public function get text () : String
		{
			if ( _label )
				return _label.text;
			return "";
		}

		override protected function draw () : void
		{
			super.draw();
			if ( _label )
			{
				_label.text = labelText;
				/*if ( width < _label.width + _gapToLabelH * 2 ){
					width = _label.width + _gapToLabelH * 2;
				}
				if ( height < _label.height + _gapToLabelV * 2 ){
					height = _label.height + _gapToLabelV * 2;
					
				}*/
				_label.align = _align;
				//_label.enabled = _labelMouse;
				_label.color = _color;
				if ( _align == TextStyle.CENTER )
					_label.x = ( width - _label.textFieldWidth ) >> 1;
				else if ( _align == TextStyle.LEFT )
					_label.x = _gapToLabelMin;
				else if ( _align == TextStyle.RIGHT )
					_label.x = ( width - _label.textFieldWidth ) - _gapToLabelMin;
				_label.y = ( height - _label.textFieldHeight ) >> 1;
			}
			
		}
		
		/** 文字对齐方式 **/
		public function get align () : String
		{
			return _align;
		}

		/**
		 * @private
		 */
		public function set align ( value : String ) : void
		{
			if(_align == value){
				return;
			}
			_align = value;
			nextDraw();
		}

		/*public function get labelMouse () : Boolean
		{
			return _labelMouse;
		}

		public function set labelMouse ( value : Boolean ) : void
		{
			if(_labelMouse == value){
				return;
			}
			_labelMouse = value;
			nextDraw();
		}*/

		public function get color () : uint
		{
			return _color;
		}
		
		public function set color ( value : uint ) : void
		{
			if ( _color == value )
				return;
			_color = value;
			nextDraw();
		}
		/** 文字垂直边距 **/
		/*public function get gapToLabelV () : int
		{
			return _gapToLabelV;
		}*/

		/**
		 * @private
		 */
		/*public function set gapToLabelV ( value : int ) : void
		{
			if(_gapToLabelV == value){
				return;
			}
			_gapToLabelV = value;
			nextDraw();
			
		}*/

		/** 文字水平边距 **/
		/*public function get gapToLabelH () : int
		{
			return _gapToLabelH;
		}*/

		/**
		 * @private
		 */
		/*public function set gapToLabelH ( value : int ) : void
		{
			if(_gapToLabelH == value){
				return;
			}
			_gapToLabelH = value;
			nextDraw();
		}*/

		/** 文字在TextStyle.LEFT和 TextStyle.RIGHT 类型时 最小边距 **/
		public function get gapToLabelMin () : int
		{
			return _gapToLabelMin;
		}

		/**
		 * @private
		 */
		public function set gapToLabelMin ( value : int ) : void
		{
			if(_gapToLabelMin == value){
				return;
			}
			_gapToLabelMin = value;
			nextDraw();
		}


		override public function dispose () : void
		{
			
			_label = null;
			super.dispose();
			
		}


//		override public function set width ( value : Number ) : void
//		{
//			if ( _width == value )
//				return;
//			super.width = value;
//		}
//
//		override public function set height ( value : Number ) : void
//		{
//			if ( _height == value )
//				return;
//			super.width = height;
//		}
	}
}
