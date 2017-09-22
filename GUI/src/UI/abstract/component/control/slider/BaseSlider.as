package UI.abstract.component.control.slider
{
	import UI.App;
	import UI.abstract.component.control.base.UIActiveCompent;
	import UI.abstract.component.control.base.UIComponent;
	import UI.abstract.component.control.button.AImageButton;
	import UI.abstract.component.control.button.BaseButton;
	import UI.abstract.component.control.container.Container;
	import UI.abstract.component.control.image.Image;
	import UI.abstract.component.control.text.ALabel;
	import UI.abstract.component.event.SliderEvent;
	
	import flash.events.MouseEvent;
	import flash.geom.Rectangle;

	public class BaseSlider extends Container

	{
		/**水平移动*/
		public static const HORIZONTAL : String = "horizontal";

		/**垂直移动*/
		public static const VERTICAL : String   = "vertical";

		protected var _direction : String;

		/** 是否支点击背景 **/
		private var _allowBackClick : Boolean   = false;

		/** 最大有效值 **/
		protected var _max : Number             = 100;

		/** 最小有效值 **/
		protected var _min : Number             = 0;

		/** 一次移动刻度值 目前最小为1 **/
		protected var _tick : int               = 1;

		/** 点击背景移动距离 0为直接移动鼠标点**/
		protected var _trackTick : int          = 0;

		/** 当前值 **/
		protected var _value : Number           = 0;

		protected var _lastValue : Number       = 0;

		protected var _mouseValue : Number      = 0;

		protected var _track : BaseButton;

		protected var _bar : BaseButton;

		protected var _label : ALabel;

		protected var _showLabel : Boolean      = false;

		/** 点击背景不显示文字 **/
		protected var _isClickBack : Boolean    = true;

		/**是否强制更新value**/
		private var _isEnforceUpdate:Boolean    =  false;
		public function BaseSlider ( direction : String )
		{
			super();
			_direction = direction;
		}

		public function set track ( value : BaseButton ) : void
		{
			if ( _track )
			{
				_track.dispose();
				_track = null;
			}

			if ( value )
			{
				_track = value;
				this.addChildAt( _track , 0 );
			}
		}

		public function get track () : BaseButton
		{
			return _track;
		}

		public function set bar ( value : BaseButton ) : void
		{
			if ( _bar )
			{
				_bar.dispose();
				_bar = null;
			}

			if ( value )
			{
				_bar = value;
				this.addChild( _bar );
				App.event.addEvent( _bar , MouseEvent.MOUSE_DOWN , onButtonMouseDown );
			}
		}

		public function get bar () : BaseButton
		{
			return _bar;
		}

		override protected function draw () : void
		{
			super.draw();
			_track.width = _width;
			_track.height = _height;
			// 滑块居中
			if ( _direction == HORIZONTAL )
				_bar.y = ( height - _bar.height ) >> 1;
			else
				_bar.x = ( width - _bar.width ) >> 1;
			nextDrawGraphics();
			//updateValue();
		}

		override protected function drawGraphics () : void
		{
			super.drawGraphics();
//			updateTick();
			//更新滑块
			if ( _direction == HORIZONTAL )
				_bar.x = ( value - _min ) / ( _max - _min ) * ( width - _bar.width );
			else
			{
				_bar.y = ( value - _min ) / ( _max - _min ) * ( height - _bar.height );
				//超出范围
				/*if ( _bar.height + _bar.y >= height )
				{
					_bar.y = height - _bar.height;
					_value = max;
				}*/
			}
			
			
			//if (/* _isInitDrawGraphics && */!_isClickBack )
				showValueText();
			//_isClickBack = false;
			
//			trace( value );
		}

		/**
		 * 点击滑块
		 */
		protected function onButtonMouseDown ( e : MouseEvent ) : void
		{
			App.event.addEvent( App.stage , MouseEvent.MOUSE_UP , onMouseUp );
			App.event.addEvent( App.stage , MouseEvent.MOUSE_MOVE , onMouseMove );

			if ( _direction == HORIZONTAL )
			{
				_lastValue = value;
				_mouseValue = e.stageX;
			}
			else
			{
				_lastValue = value;
				_mouseValue = e.stageY;
			}
		}


		/**
		 * 点击背景条
		 */
		protected function onBackMouseClick ( e : MouseEvent ) : void
		{
			if ( e.target != track )
				return;
			_isClickBack = true;
			var num : Number = 0;
			if ( _direction == HORIZONTAL )
			{
				if ( _trackTick == 0 )
					value = _min+e.localX * ( _max - _min ) / width;
				else
					value += e.localX > _bar.x ? _trackTick : -_trackTick;
			}
			else
			{
				if ( _trackTick == 0 )
					value = _min+e.localY * ( _max - _min ) / height;
				else
					value += e.localY > _bar.y ? _trackTick : -_trackTick;
			}
		}

		protected function onMouseUp ( e : MouseEvent ) : void
		{
			App.event.removeEvent( App.stage , MouseEvent.MOUSE_UP , onMouseUp );
			App.event.removeEvent( App.stage , MouseEvent.MOUSE_MOVE , onMouseMove );

			hideValueText();
		}

		//private var isMoveToBar : Boolean       = true;

		protected function onMouseMove ( e : MouseEvent ) : void
		{
			var num : int = 0;
			// 通过鼠标平移距离 换算value值改变多少
			if ( _direction == HORIZONTAL )
			{
				num = e.stageX - _mouseValue;
				value = _lastValue + num * ( _max - _min ) / ( width - bar.width );
			}
			else
			{
				num = e.stageY - _mouseValue;

				value = _lastValue + num * ( _max - _min ) / ( height - bar.height );
			}

		}

		/**
		 * 显示当前值
		 */
		protected function showValueText () : void
		{
			if ( _showLabel )
			{
				_label.text = _value + "";
				if ( _direction == HORIZONTAL )
				{
					_label.y = _bar.y - _label.textFieldHeight;
					_label.x = ( ( _bar.width - _label.textFieldWidth ) >> 1 ) + _bar.x;
				}
				else
				{
					_label.x = _bar.x + _bar.width;
					_label.y = ( ( _bar.height - _label.textFieldHeight ) >> 1 ) + _bar.y;
				}
				if(!_isClickBack){
					_label.visible = true;
				}
				_isClickBack = false;
				
			}
		}

		protected function hideValueText () : void
		{
			if ( _showLabel )
				_label.visible = false;
		}

		/**
		 * 设置滑动条取值范围
		 **/
		public function setRange ( min : Number , max : Number ) : void
		{
			_min = min;
			_max = max > min ? max : min;
			//nextDraw();
			updateValue();
		}
		/**
		 * 强制更新value
		 */
		private function updateValue():void{
			_isEnforceUpdate = true;
			value = value;
		}
		/**
		 * 当前值
		 */
		public function get value () : Number
		{
			return _value;
		}

		/**
		 * @private
		 */
		public function set value ( value : Number ) : void
		{
			if ( _value == value && !_isEnforceUpdate)
				return;
			var last : Number = _value;
			_value = value;
			updateTick();
			if ( _value == last && !_isEnforceUpdate)
				return;
			if(!_isEnforceUpdate){
				dispatchEvent( new SliderEvent( SliderEvent.SLIDER_CHANGE , value ) );
			}
			_isEnforceUpdate = false;
//			trace( _value )
			nextDrawGraphics();
		}

		/**
		 * 根据当前刻度值，做更新
		 */
		protected function updateTick () : void
		{
			if ( _value >= _max )
			{
				_value = _max;
				return;
			}
			else if ( _value <= _min )
			{
				_value = _min;
				return;
			}

			//当刻度大于1时
			if ( _tick >= 1 )
			{
				var n : int = ( _value - _min ) / _tick + 0.5;
				_value = n * _tick + _min;
				if ( _value > _max )
					_value = _max;
			}
		}

		public function get showLabel () : Boolean
		{
			return _showLabel;
		}

		/**
		 * 是否显示刻度条值
		 */
		public function set showLabel ( value : Boolean ) : void
		{
			if(_showLabel == value){
				return;
			}
			_showLabel = value;
			if ( value )
			{
				if ( !_label )
				{
					_label = new ALabel();
					_label.visible = false;
					this.addChild( _label );
				}
			}
		}

		/**
		 * 是否支点击背景
		 */
		public function get allowBackClick () : Boolean
		{
			return _allowBackClick;
		}

		public function set allowBackClick ( value : Boolean ) : void
		{
			if(_allowBackClick == value){
				return;
			}
			_allowBackClick = value;
			if ( _allowBackClick )
				App.event.addEvent( track , MouseEvent.CLICK , onBackMouseClick );
			else
				App.event.removeEvent( track , MouseEvent.CLICK , onBackMouseClick );
		}

		/** 一次移动刻度值 目前最小为1 **/
		public function get tick () : int
		{
			return _tick;
		}

		/**
		 * @private
		 */
		public function set tick ( value : int ) : void
		{
			if(_tick == value){
				return;
			}
			_tick = value;
			updateValue();
		}

		/** 点击背景移动距离 0为直接移动鼠标点**/
		public function get trackTick () : int
		{
			return _trackTick;
		}

		/**
		 * @private
		 */
		public function set trackTick ( value : int ) : void
		{
			if(_trackTick == value){
				return;
			}
			_trackTick = value;
		}

		/** 最大有效值 **/
		public function get max () : Number
		{
			return _max;
		}

		/**
		 * @private
		 */
		public function set max ( value : Number ) : void
		{
			if ( _max == value )
				return;
			setRange( _min , value );
		}

		/** 最小有效值 **/
		public function get min () : Number
		{
			return _min;
		}

		/**
		 * @private
		 */
		public function set min ( value : Number ) : void
		{
			if ( _min == value )
				return;
			setRange( value , _max );
		}

		/**
		 * 当前比例值
		 */
		public function get ratio () : Number
		{
			return _value / _max;
		}

		override public function dispose () : void
		{
			
			if ( App.event.hasEventFun( App.stage , MouseEvent.MOUSE_UP , onMouseUp ) )
				App.event.removeEvent( App.stage , MouseEvent.MOUSE_UP , onMouseUp );
			if ( App.event.hasEventFun( App.stage , MouseEvent.MOUSE_MOVE , onMouseMove ) )
				App.event.removeEvent( App.stage , MouseEvent.MOUSE_MOVE , onMouseMove );
			_track = null;
			_bar = null;
			_label = null;
			super.dispose();
		}
	}
}
