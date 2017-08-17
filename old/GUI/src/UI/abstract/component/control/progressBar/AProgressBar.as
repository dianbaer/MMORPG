package UI.abstract.component.control.progressBar
{
	import UI.abstract.component.control.container.Box;
	import UI.abstract.component.control.image.Image;
	import UI.abstract.component.control.panel.Panel;
	import UI.abstract.component.control.text.ALabel;
	import UI.abstract.component.control.text.TextStyle;
	
	import flash.text.TextFormat;

	public class AProgressBar extends Box
	{
		/** 进度条 **/
		private var _bar : Image;

		/** 进度信息 **/
		private var _label : ALabel;

		/** 当前值 **/
		private var _value : Number = 0;

		/** 是否显示进度信息 **/
		private var _isShowInfo : Boolean;

		/** 最小值 **/
		private var _min : Number   = 0;

		/** 最大值 **/
		private var _max : Number   = 1;

		private var _panel : Panel;

		private var _infoLb : ALabel;
		
		public function AProgressBar ()
		{
			super();
			isNext = false;
			_panel = new Panel;
			_panel.isNext = false;
			this.addChild( _panel );
		}

		override protected function draw () : void
		{
			super.draw();
			_bar.setSize( _width , _height );
			_panel.setSize( _width , _height );
			nextDrawGraphics()
			//drawGraphics();
		}

		override protected function drawGraphics () : void
		{
			super.drawGraphics();
			var radio : Number = _value / (max+min);
			_panel.width = radio * _width;
			if ( _label )
			{
				_label.text = int( radio * 100 ) + "%";
				_label.x = ( _width - _label.textFieldWidth ) >> 1;
				_label.y = ( _height - _label.textFieldHeight ) >> 1;
			}
			
			if ( _infoLb )
			{
				_infoLb.x = (_width - _infoLb.textFieldWidth) >> 1;
				_infoLb.y = -_infoLb.textFieldHeight - 4;
			}
			
		}

		/** 进度条 **/
		public function get bar () : Image
		{
			return _bar;
		}

		/**
		 * @private
		 */
		public function set bar ( value : Image ) : void
		{
			if ( _bar )
			{
				_bar.dispose();
				_bar = null;
			}
			if(value){
				_bar = value;
				_panel.content.addChild( _bar );
			}
		}

		/** 当前值 **/
		public function get value () : Number
		{
			return _value;
		}

		/**
		 * @private
		 */
		public function set value ( value : Number ) : void
		{
			if(_value == value){
				return;
			}
			_value = value;
			if ( _value < min )
				_value = min;
			if ( _value > max )
				_value = max;
			nextDrawGraphics();
		}

		/** 是否显示进度信息 **/
		public function get isShowInfo () : Boolean
		{
			return _isShowInfo;
		}

		/**
		 * @private
		 */
		public function set isShowInfo ( value : Boolean ) : void
		{
			if(_isShowInfo == value){
				return;
			}
			_isShowInfo = value;

			if ( value )
			{
				if ( !_label )
				{
					_label = new ALabel();
					_label.align = TextStyle.CENTER;
					_label.color = 0xFFFFFF;
					this.addChild( _label );
				}
			}
			else
			{
				_label.dispose();
				_label = null;
			}
		}

		/** 最小值 **/
		public function get min () : Number
		{
			return _min;
		}

		/**
		 * @private
		 */
		public function set min ( value : Number ) : void
		{
			if(_min == value){
				return;
			}
			_min = value;
			if ( min > max )
				min = max;
			nextDrawGraphics();
		}

		/** 最大值 **/
		public function get max () : Number
		{
			return _max;
		}

		/**
		 * @private
		 */
		public function set max ( value : Number ) : void
		{
			if(_max == value){
				return;
			}
			_max = value;
			if ( max < min )
				max = min;
			nextDrawGraphics();
		}

		public function setRange ( min : Number , max : Number ) : void
		{
			this.min = min;
			this.max = max;
		}
		
		/**
		 * 设置提示信息
		 */
		public function set text ( value : String ) : void
		{
			if ( !_infoLb )
			{
				_infoLb = new ALabel();
				_infoLb.color = 0x000000;
				this.addChild( _infoLb );
			}
			if(_infoLb.text == value){
				return;
			}
			_infoLb.text = value;
		}
		public function get label():ALabel{
			if ( !_infoLb )
			{
				_infoLb = new ALabel();
				_infoLb.color = 0x000000;
				this.addChild( _infoLb );
			}
			return _infoLb;
		}
		public function set textformat ( value : TextFormat ) : void
		{
			if ( _infoLb )
				_infoLb.defaultFormat = value;
		}

		override public function dispose () : void
		{
			
			_bar = null;
			_label = null;
			_panel = null;
			_infoLb = null;
			super.dispose();
		}

	}
}
