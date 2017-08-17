package UI.abstract.component.control.button
{
	import UI.App;
	import UI.abstract.component.control.container.Container;
	
	import flash.display.DisplayObject;
	import flash.events.MouseEvent;

	public class BaseButton extends Container
	{
		private var _currentState : String;

		/** 是否只有鼠标抬起才有 over事件 **/
		protected var isOverKonck : Boolean = true;

		public function BaseButton ()
		{
			super();
			App.event.addEvent( this , MouseEvent.MOUSE_OVER , onMouseOver );
			App.event.addEvent( this , MouseEvent.MOUSE_OUT , onMouseOut );
			App.event.addEvent( this , MouseEvent.MOUSE_DOWN , onMouseDown );
			this.buttonMode = true;
		}

		override protected function drawGraphics () : void
		{
			super.drawGraphics();
			switch ( currentState )
			{
				case ButtonStyle.UP:
					up();
					break;
				case ButtonStyle.OVER:
					over();
					break;
				case ButtonStyle.DOWN:
					down();
					break;
			}
		}

		/**
		 * 鼠标不同状态时触发 子类重写
		 */
		protected function up () : void
		{

		}

		protected function over () : void
		{

		}

		protected function down () : void
		{

		}

		/**
		 * 状态
		 */
		public function get currentState () : String
		{
			return _currentState;
		}

		public function set currentState ( value : String ) : void
		{
			if ( _currentState == value )
				return;
			_currentState = value;
			nextDrawGraphics();
		}

		/**
		 * 鼠标在上
		 */
		protected function onMouseOver ( event : MouseEvent ) : void
		{
			if ( currentState == ButtonStyle.UP || !isOverKonck )
				currentState = ButtonStyle.OVER;
		}

		/**
		 * 鼠标离开
		 */
		protected function onMouseOut ( event : MouseEvent ) : void
		{
			if ( currentState == ButtonStyle.OVER )
				currentState = ButtonStyle.UP;
		}

		/**
		 * 鼠标按下
		 */
		protected function onMouseDown ( event : MouseEvent ) : void
		{
			App.event.addEvent( App.stage , MouseEvent.MOUSE_UP , onMouseUp );
			currentState = ButtonStyle.DOWN;
		}

		/**
		 * 鼠标抬起
		 */
		protected function onMouseUp ( event : MouseEvent ) : void
		{
			App.event.removeEvent( App.stage , MouseEvent.MOUSE_UP , onMouseUp );
			var target : DisplayObject = App.ui.selectParent( event.target as DisplayObject , null , this );
			if ( target == this )
				currentState = ButtonStyle.OVER;
			else
				currentState = ButtonStyle.UP;
		}
		override public function dispose () : void
		{
			if(App.event.hasEventFun(App.stage,MouseEvent.MOUSE_UP,onMouseUp)){
				App.event.removeEvent( App.stage , MouseEvent.MOUSE_UP , onMouseUp );
			}
			super.dispose();
		}

	}
}
