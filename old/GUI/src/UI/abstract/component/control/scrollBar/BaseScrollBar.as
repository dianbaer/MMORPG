package UI.abstract.component.control.scrollBar
{
	import UI.App;
	import UI.abstract.component.control.base.UIComponent;
	import UI.abstract.component.control.button.BaseButton;
	import UI.abstract.component.control.container.Container;
	import UI.abstract.component.control.image.Image;
	import UI.abstract.component.control.slider.BaseSlider;
	import UI.abstract.component.event.ScrollBarEvent;
	import UI.abstract.component.event.SliderEvent;
	
	import flash.events.MouseEvent;
	import flash.geom.Point;
	import flash.geom.Rectangle;

	public class BaseScrollBar extends Container
	{
		/** 水平滚动 **/
		public static const HORIZONTAL : String = "horizontal";

		/** 垂直垂直 **/
		public static const VERTICAL : String   = "vertical";

		protected var _direction : String;

		protected var _slider : BaseSlider;

		protected var _addBtn : BaseButton;

		protected var _reduceBtn : BaseButton;

		/** 内容距离背景框边距 **/
		protected var _gapBack : int            = 0;

		/** 按钮距离滚动条距离 **/
		protected var _gapTrack : int           = 0;

		protected var _barRatio : Number        = -1;

		/** 滑块最小长度 **/
		protected var _minLen : int             = 20;

		public function BaseScrollBar ( direction : String )
		{
			_direction = direction;
		}

		override protected function draw () : void
		{
			super.draw();
			var ratio : Number = 0;
			if ( _direction == HORIZONTAL )
			{
				ratio = ( height - _gapBack * 2 ) / _addBtn.height;
				_addBtn.x = _gapBack;
				_addBtn.y = _gapBack;
//				_addBtn.height = height -  _gapBack * 2;
				_addBtn.height = ratio * _addBtn.height;
				_addBtn.width = ratio * _addBtn.width;
				ratio = ( height - _gapBack * 2 ) / _reduceBtn.height;
				_reduceBtn.height = ratio * _reduceBtn.height;
				_reduceBtn.width = ratio * _reduceBtn.width;
				_reduceBtn.x = width - _gapBack - _reduceBtn.width;
				_reduceBtn.y = _gapBack;

				slider.x = _addBtn.x + _addBtn.width + _gapTrack;
				slider.y = _gapBack;
				slider.setSize( width - _addBtn.width * 2 - _gapBack * 2 - _gapTrack * 2 , height - _gapBack * 2 );
				slider.bar.setSize( slider.width , slider.height );
			}
			else
			{
				ratio = ( width - _gapBack * 2 ) / _addBtn.width;
				_addBtn.x = _gapBack;
				_addBtn.y = _gapBack;
				_addBtn.height = ratio * _addBtn.height;
				_addBtn.width = ratio * _addBtn.width;
				ratio = ( width - _gapBack * 2 ) / _reduceBtn.width;
				_reduceBtn.height = ratio * _reduceBtn.height;
				_reduceBtn.width = ratio * _reduceBtn.width;
				_reduceBtn.x = _gapBack;
				_reduceBtn.y = height - _gapBack - _reduceBtn.height;


				slider.x = _gapBack;
				slider.y = _addBtn.y + _addBtn.height + _gapTrack;
				slider.setSize( width - _gapBack * 2 , height - _addBtn.height * 2 - _gapBack * 2 - _gapTrack * 2 );
				slider.bar.setSize( slider.width , slider.height );
			}

			// 更新滑块大小
			barRatio = _barRatio;
		}

		override protected function drawGraphics () : void
		{
			super.drawGraphics();
			if ( _direction == HORIZONTAL )
			{

			}
			else
			{

			}
		}

//		/**
//		 * 背景
//		 */
//		public function get back () : Image
//		{
//			return _back;
//		}
//
//		public function set back ( value : Image ) : void
//		{
//			if ( _back )
//			{
//				_back.dispose();
//				_back = null;
//			}
//
//			if ( value )
//			{
//				_back = value;
//				this.addChildAt( _back , 0 );
//			}
//		}

		/**
		 * 滑动器
		 */
		public function get slider () : BaseSlider
		{
			return _slider;
		}

		public function set slider ( value : BaseSlider ) : void
		{
			if ( _slider )
			{
				_slider.dispose();
				_slider = null;
			}

			if ( value )
			{
				_slider = value;
				this.addChild( _slider );
				App.event.addEvent( _slider , SliderEvent.SLIDER_CHANGE , onSliderChange );
			}
		}

		/**
		 * 刻度增加按钮
		 */
		public function get addBtn () : BaseButton
		{
			return _addBtn;
		}

		public function set addBtn ( value : BaseButton ) : void
		{
			if ( _addBtn )
			{
				_addBtn.dispose();
				_addBtn = null;
			}

			if ( value )
			{
				_addBtn = value;
				this.addChild( _addBtn );
				App.event.addEvent( _addBtn , MouseEvent.MOUSE_DOWN , onButtonDown );
			}
		}

		/**
		 * 刻度减少按钮
		 */
		public function get reduceBtn () : BaseButton
		{
			return _reduceBtn;
		}

		public function set reduceBtn ( value : BaseButton ) : void
		{
			if ( _reduceBtn )
			{
				_reduceBtn.dispose();
				_reduceBtn = null;
			}

			if ( value )
			{
				_reduceBtn = value;
				this.addChild( _reduceBtn );
				App.event.addEvent( _reduceBtn , MouseEvent.MOUSE_DOWN , onButtonDown );
			}
		}

		/** 按钮大小 **/
		public function get btnWidth () : int
		{
			return _addBtn.width;
		}

		public function get btnHeight () : int
		{
			return _addBtn.height;
		}

		private function onButtonDown ( e : MouseEvent ) : void
		{
			var value : int;
			switch ( e.currentTarget )
			{
				case _addBtn:
					_slider.value -= tick;
					break;
				case _reduceBtn:
					_slider.value += tick;
					break;
			}
		}

		/**
		 * 滑块值改变
		 */
		private function onSliderChange ( e : SliderEvent ) : void
		{
			if ( _direction == HORIZONTAL )
				dispatchEvent( new ScrollBarEvent( ScrollBarEvent.SCROLL_CHANGE , e.position , ScrollBarEvent.SCROLLTYPE_H ) );
			else
				dispatchEvent( new ScrollBarEvent( ScrollBarEvent.SCROLL_CHANGE , e.position , ScrollBarEvent.SCROLLTYPE_V ) );
		}

		/** 一次移动刻度值 目前最小为1 **/
		public function get tick () : int
		{
			return _slider.tick;
		}

		/**
		 * @private
		 */
		public function set tick ( value : int ) : void
		{
			_slider.tick = value;
			//trackTick = 5 * tick;
		}

		/**
		 * 设置滑动条取值范围
		 **/
		public function setRange ( min : Number , max : Number ) : void
		{
			_slider.setRange( min , max );
		}

		/** 最大有效值 **/
		public function get max () : Number
		{
			return _slider.max;
		}

		/**
		 * @private
		 */
		public function set max ( value : Number ) : void
		{
			if ( _slider.max == value )
				return;
			setRange( min , value );
		}

		/** 最小有效值 **/
		public function get min () : Number
		{
			return _slider.min;
		}

		/**
		 * @private
		 */
		public function set min ( value : Number ) : void
		{
			if ( _slider.min == value )
				return;
			setRange( value , max );
		}

		/**
		 * 当前值
		 */
		public function get position () : Number
		{
			return _slider.value;
		}

		/**
		 * @private
		 */
		public function set position ( value : Number ) : void
		{
			_slider.value = value;
		}

		/** 点击背景移动距离 0为直接移动鼠标点**/
		public function get trackTick () : int
		{
			return _slider.trackTick;
		}

		/**
		 * @private
		 */
		public function set trackTick ( value : int ) : void
		{
			_slider.trackTick = value;
		}

		/**
		 * 当前比例值
		 */
		public function get ratio () : Number
		{
			return _slider.ratio;
		}

		/** 设置滑块占轨道比例 **/
		public function get barRatio () : Number
		{
			return _barRatio;
		}

		/**
		 * @private
		 */
		public function set barRatio ( value : Number ) : void
		{
			value = Math.min( 1 , value );
			value = Math.max( 0 , value );
			_barRatio = value;
			if ( _direction == HORIZONTAL )
				_slider.bar.width = Math.max( value * _slider.width , _minLen );
			else
			{
				_slider.bar.height = Math.max( value * _slider.height , _minLen );
			}
		}

		/** 滑块最小长度 **/
		public function get minLen () : int
		{
			return _minLen;
		}

		/**
		 * @private
		 */
		public function set minLen ( value : int ) : void
		{
			if(_minLen == value){
				return;
			}
			_minLen = value;
			// 更新滑块大小
			barRatio = _barRatio;
		}

		override public function dispose () : void
		{
			
			_slider = null;
			_addBtn = null;
			_reduceBtn = null;
			super.dispose();
		}

	}
}
