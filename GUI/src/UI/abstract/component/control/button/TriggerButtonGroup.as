package UI.abstract.component.control.button
{
	import UI.App;
	import UI.abstract.component.control.base.UIComponent;
	
	import flash.events.EventDispatcher;

	public class TriggerButtonGroup extends EventDispatcher
	{
		protected var _buttonsList : Array;

		public function TriggerButtonGroup ()
		{
			_buttonsList = [];
		}

		/**
		 * 增加一个按钮
		 */
		public function addButton ( button : ITriggerButton ) : void
		{
			_buttonsList.push( button );
			button.group = this;
		}
		/**
		 * 移出一个按钮
		 */
		public function removeButton ( button : ITriggerButton ) : void
		{
			button.group = null;
			_buttonsList.splice( _buttonsList.indexOf( button ) , 1 );
		}

		/**
		 *
		 */
		public function click () : void
		{
		}

		/**
		 *
		 */
		public function doubleClick () : void
		{
		}

		/**
		 *
		 */
		public function get over () : Boolean
		{
			return false;
		}

		public function set over ( value : Boolean ) : void
		{
		}

		/**
		 *
		 */
		public function get pressed () : Boolean
		{
			return false;
		}

		public function set pressed ( value : Boolean ) : void
		{
		}

		/**
		 *
		 */
		public function get locked () : Boolean
		{
			return false;
		}

		public function set locked ( value : Boolean ) : void
		{
		}

		public function dispose () : void
		{
			/*for each ( var item : Object in _buttonsList )
			{
				if ( item is UIComponent )
				{
					UIComponent( item ).dispose();
				}
			}*/
			App.event.removeEventByObj( this );
			_buttonsList.length = 0;
			_buttonsList = null
		}
	}
}
