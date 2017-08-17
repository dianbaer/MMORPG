package UI.abstract.component.control.button
{
	import flash.events.MouseEvent;

	public class ARadioButton extends ACheckBox implements ITriggerButton
	{
		protected var _group : RadioButtonGroup;

		public function ARadioButton ()
		{
			super();
		}
		override protected function onMouseClick ( event : MouseEvent ) : void{
			
			selected = true;
		}
		/**
		 * 鼠标抬起
		 */
		/*override protected function onMouseUp ( event : MouseEvent ) : void
		{
			App.event.removeEvent( App.stage , MouseEvent.MOUSE_UP , onMouseUp );
			var target : DisplayObject = App.ui.selectParent( event.target as DisplayObject , null , this );
			if ( target == this )
			{
				selected = true;
				//if ( currentState == ButtonStyle.UP )
				currentState = ButtonStyle.OVER;
			}
			else
				currentState = ButtonStyle.UP;
		}*/

		override public function get selected () : Boolean
		{
			return _selected;
		}

		override public function set selected ( value : Boolean ) : void
		{
			if ( _selected == value )
				return;

			_selected = value;

			if ( _group != null){
				if(_selected){
					_group.selected = this;
				}else{
					_group.selected = null;
				}
			}
			if(_selected){
				currentState = ButtonStyle.DOWN;
			}else{
				currentState = ButtonStyle.UP;
				nextDrawGraphics();
			}
		}



		public function get group () : TriggerButtonGroup
		{
			return _group;
		}

		public function set group ( value : TriggerButtonGroup ) : void
		{
			_group = value as RadioButtonGroup;
			if(_selected && _group){
				_group.selected = this;
			}
		}

		override public function dispose () : void
		{
			
			if ( _group )
			{
				_group.removeButton( this );
				_group = null;
			}
			super.dispose();
		}

	}
}
