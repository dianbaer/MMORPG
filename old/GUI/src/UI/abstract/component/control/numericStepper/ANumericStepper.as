package UI.abstract.component.control.numericStepper
{
	import UI.App;
	import UI.abstract.component.control.base.UIComponent;
	import UI.abstract.component.control.button.BaseButton;
	import UI.abstract.component.control.text.ATextInput;
	import UI.abstract.component.event.NumericStepperEvent;

	import flash.events.Event;
	import flash.events.MouseEvent;

	public class ANumericStepper extends UIComponent
	{
		private var _textInput : ATextInput;

		/** 当前值 **/
		private var _value : int;

		/** 最小值 **/
		private var _min : int;

		/** 最大值 **/
		private var _max : int;

		/** 一次增加刻度值 **/
		private var _step : int = 1;

		/** 增加按钮 **/
		private var _increaseBtn : BaseButton;

		/** 减少按钮 **/
		private var _decreaseBtn : BaseButton;

		/** 按钮间距 **/
		private var _gapBtn : int;

		/** 按钮与文本间距 **/
		private var _gapTextToBtn : int;

		public function ANumericStepper ()
		{

		}

		override protected function draw () : void
		{
			super.draw();

			if ( !_textInput || !_increaseBtn || !_decreaseBtn )
				return;

			_textInput.width = width - _increaseBtn.width - gapTextToBtn;
			_textInput.height = height;

			_increaseBtn.height = ( height - _gapBtn ) / 2;
			_decreaseBtn.height = _increaseBtn.height;
			_increaseBtn.x = _textInput.width + _gapTextToBtn;
			_decreaseBtn.x = _increaseBtn.x;
			_decreaseBtn.y = _increaseBtn.height + _gapBtn;
		}

		override protected function drawGraphics () : void
		{
			super.drawGraphics();
			_textInput.text = value.toString();
			dispatchEvent( new NumericStepperEvent( NumericStepperEvent.NUMER_CHANGE , value ) );
		}

		public function get textInput () : ATextInput
		{
			return _textInput;
		}

		public function set textInput ( value : ATextInput ) : void
		{
			if ( _textInput )
				_textInput.dispose();

			_textInput = value;
			_textInput.isNum = true;
			this.addChild( _textInput );
			App.event.addEvent( _textInput , Event.CHANGE , onTextChange );
			nextDraw();
		}

		/** 增加按钮 **/
		public function get increaseBtn () : BaseButton
		{
			return _increaseBtn;
		}

		/**
		 * @private
		 */
		public function set increaseBtn ( value : BaseButton ) : void
		{
			if ( _increaseBtn )
				_increaseBtn.dispose();

			_increaseBtn = value;
			this.addChild( _increaseBtn );
			App.event.addEvent( _increaseBtn , MouseEvent.CLICK , onClickBtn );
		}

		/** 减少按钮 **/
		public function get decreaseBtn () : BaseButton
		{
			return _decreaseBtn;
		}

		/**
		 * @private
		 */
		public function set decreaseBtn ( value : BaseButton ) : void
		{
			if ( _decreaseBtn )
				_decreaseBtn.dispose();

			_decreaseBtn = value;
			this.addChild( _decreaseBtn );
			App.event.addEvent( _decreaseBtn , MouseEvent.CLICK , onClickBtn );
		}

		/** 当前值 **/
		public function get value () : int
		{
			return _value;
		}

		/**
		 * @private
		 */
		public function set value ( value : int ) : void
		{
			if ( value < min )
				value = min;
			if ( value > max )
				value = max;
			if ( _value == value )
				return;
			;
			_value = value;
			nextDrawGraphics();
		}

		/** 最小值 **/
		public function get min () : int
		{
			return _min;
		}

		/**
		 * @private
		 */
		public function set min ( value : int ) : void
		{
			_min = value;
			nextDrawGraphics();
		}

		/** 最大值 **/
		public function get max () : int
		{
			return _max;
		}

		/**
		 * @private
		 */
		public function set max ( value : int ) : void
		{
			_max = value;
			nextDrawGraphics();
		}

		/** 范围 **/
		public function setRange ( min : int , max : int ) : void
		{
			_min = min;
			_max = max;
			nextDrawGraphics();
		}

		/** 一次增加刻度值 **/
		public function get step () : int
		{
			return _step;
		}

		/**
		 * @private
		 */
		public function set step ( value : int ) : void
		{
			_step = value;
		}

		/** 按钮间距 **/
		public function get gapBtn () : int
		{
			return _gapBtn;
		}

		/**
		 * @private
		 */
		public function set gapBtn ( value : int ) : void
		{
			_gapBtn = value;
			nextDraw();
		}

		/** 按钮与文本间距 **/
		public function get gapTextToBtn () : int
		{
			return _gapTextToBtn;
		}

		/**
		 * @private
		 */
		public function set gapTextToBtn ( value : int ) : void
		{
			_gapTextToBtn = value;
			nextDraw();
		}

		/**
		 * 点击按钮
		 */
		private function onClickBtn ( e : MouseEvent ) : void
		{
			if ( e.target == _increaseBtn )
				value += step;
			else if ( e.target == _decreaseBtn )
				value -= step;
		}

		/**
		 * 文本内容改变
		 */
		public function onTextChange ( e : Event ) : void
		{
			var num : int = int( _textInput.text );
			value = num;
			if ( num != value )
				textInput.text = value.toString();
		}

		/**
		 * 是否可输入
		 */
		public function set input ( value : Boolean ) : void
		{
			_textInput.input = value;
		}

		override public function dispose () : void
		{
			if ( _textInput )
			{
				_textInput.dispose();
				_textInput = null;
			}

			if ( _increaseBtn )
			{
				_increaseBtn.dispose();
				_increaseBtn = null;
			}

			if ( _decreaseBtn )
			{
				_decreaseBtn.dispose();
				_decreaseBtn = null;
			}

			super.dispose();
		}
	}
}
