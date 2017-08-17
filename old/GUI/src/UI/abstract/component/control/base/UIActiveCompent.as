package UI.abstract.component.control.base
{
	import UI.App;
	
	import flash.display.DisplayObject;
	import flash.events.MouseEvent;

	/**
	 * 基本交互对象基类
	 */
	public class UIActiveCompent extends UIComponent
	{
		protected var _enabled : Boolean;

		public function UIActiveCompent ()
		{
			super();
			//enabled = true;
		}

		/**
		 * 销毁
		 */
		override public function dispose () : void
		{
			
			tooltip();
			cursor();
			super.dispose();
		}

		/**
		 * 是否可交互
		 */
		public function get enabled () : Boolean
		{
			return _enabled;
		}

		public function set enabled ( value : Boolean ) : void
		{
			if ( _enabled == value )
				return;
			_enabled = value;
			this.mouseChildren = value;
			this.mouseEnabled = value;
			this.tabChildren = value;
			this.tabEnabled = value;
		}

		public function tooltip ( value : Object = null , TooltipClass : Class = null ) : void
		{

			if ( value == null )
			{
				App.tip.destroyToolTip( this );
			}
			else
			{
				App.tip.registerToolTip( this , value , TooltipClass );
			}
		}

		public function cursor ( name : String = null ) : void
		{
			if ( name == null )
			{
				App.cursor.destroyCursor( this );
			}
			else
			{
				App.cursor.registerCursor( this );
			}
		}

		/**
		 * 是否允许拖拽
		 */
		public function set drag ( value : Boolean ) : void
		{
			if ( this.mouseEnabled == false )
				return;

			if ( value )
				App.event.addEvent( this , MouseEvent.MOUSE_DOWN , onDrag );
			else
				App.event.removeEvent( this , MouseEvent.MOUSE_DOWN , onDrag );
		}

		protected function onDrag ( e : MouseEvent ) : void
		{
			var target : DisplayObject = App.ui.selectParent( e.target as DisplayObject , null,this );
			if ( target != this )
				return;
			App.drag.doDrag( this );
		}

	}
}
