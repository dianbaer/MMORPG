package UI.abstract.component.control.text
{
	import UI.App;
	import UI.abstract.component.control.container.Box;
	import UI.abstract.component.control.scrollBar.AScrollPanel;
	import UI.abstract.component.event.ScrollBarEvent;
	
	import flash.events.Event;
	
	public class ATextArea extends Box
	{
		/** 文本 **/
		protected var _textInput : ATextInput;
		
		/** 滚动条 **/
		protected var _scrollBar : AScrollPanel;
		
		/** 边距 **/
		protected var _gap : int = 2;
		
		public function ATextArea()
		{
			super();
		}
		
		override protected function draw () : void
		{
			super.draw();
			
			if ( _textInput )
			{
				var num : Number = Math.max( _textInput.textFieldHeight, _height-_gap*2 );
				_textInput.height = num;
				_textInput.width = _width-_gap*2;
				//_textInput.setPosition(_gap,_gap);
			}
			
			if ( _scrollBar )
			{
				
				_scrollBar.setSize( _width-_gap*2, _height-_gap*2 );
				_scrollBar.setPosition(_gap,_gap);
				
			}
			
			
		}
		
		/** 文本 **/
		protected function get textInput():ATextInput
		{
			return _textInput;
		}

		/**
		 * @private
		 */
		protected function set textInput(value:ATextInput):void
		{
			if ( _textInput )
			{
				_textInput.dispose();
				_textInput = null;
			}
			if(value){
				_textInput = value;
				_scrollBar.addChildToPanel(_textInput);
				App.event.addEvent( _textInput, Event.CHANGE , onChange );
			}
			nextDraw();
		}

		/** 滚动条 **/
		protected function get scrollBar():AScrollPanel
		{
			return _scrollBar;
		}

		/**
		 * @private
		 */
		protected function set scrollBar(value:AScrollPanel):void
		{
			if ( _scrollBar )
			{
				_scrollBar.dispose();
				_scrollBar = null;
			}
			if(value){
				_scrollBar = value;
				this.addChild(_scrollBar);
				App.event.addEvent(_scrollBar, ScrollBarEvent.SCROLL_BAR_CHANGE, onScrollBarChange );
			}
			
			nextDraw();
		}

		private function onChange ( e : Event ) : void
		{
			var num : Number = Math.max( _textInput.textFieldHeight, _height-_gap*2 );
			_textInput.height = num;
		}
		
		private function onScrollBarChange ( e : Event ) : void
		{
			_textInput.width = _scrollBar.contentWidth;
		}
		
		/**
		 * 设置文本内容
		 */
		public function set text ( value : String ) : void
		{
			_textInput.text = value;
		}
		
		public function get text () : String
		{
			return _textInput.text;
		}
		
		/**
		 * 设置html文本内容
		 */
		public function set htmlText ( value : String ) : void
		{
			_textInput.htmlText = value;
		}
		
		public function get htmlText () : String
		{
			return _textInput.htmlText;
		}
		
		/**
		 * 增加文本内容
		 */
		public function appendText ( value : String ) : void
		{
			_textInput.appendText( value );
		}
		
		override public function dispose():void
		{
			
			
			
			_textInput = null;
			_scrollBar = null;
			super.dispose();
		}

	}
}