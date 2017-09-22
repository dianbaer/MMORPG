package UI.theme.defaulttheme.button
{
	import UI.App;
	import UI.abstract.component.control.button.ButtonStyle;
	import UI.abstract.component.control.button.ITriggerButton;
	import UI.abstract.component.control.button.RadioButtonGroup;
	import UI.abstract.component.control.button.TriggerButtonGroup;
	import UI.theme.defaulttheme.Skin;
	
	import flash.events.MouseEvent;

	public class ToggleButton extends Button implements ITriggerButton
	{
		protected var _selected : Boolean = false;

		/** 组 **/
		protected var _group : RadioButtonGroup;

		/** 按下后是否再次点击抬起 **/
		protected var _isUp : Boolean     = true;

		public function ToggleButton ( skin : String = Skin.BUTTON , w : int = 0 , h : int = 0 )
		{
			super( skin , w , h );
			App.event.addEvent( this , MouseEvent.CLICK , onMouseClick );
			//App.event.addEvent( stage , MouseEvent.CLICK , onMouseUp , null , true );
			//isOverKonck = false;
		}

		/*override protected function onMouseDown ( event : MouseEvent ) : void
		{
			App.event.addEvent( stage , MouseEvent.MOUSE_UP , onMouseUp , null , true );
		}*/
		protected function onMouseClick ( event : MouseEvent ) : void{
			if(_isUp){
				selected = !selected;
				if(selected == false){
					currentState = ButtonStyle.OVER;
				}
			}else{
				selected = true;
			}
			
		}
		/*override protected function onMouseUp ( event : MouseEvent ) : void
		{
			App.event.removeEvent( App.stage , MouseEvent.MOUSE_UP , onMouseUp );
			var target : DisplayObject = App.ui.selectParent( event.target as DisplayObject , null , this );
			if ( target == this )
			{
				if ( _isUp )
				{
					selected = !selected;
					if ( selected == true )
						currentState = ButtonStyle.DOWN;
					else
						currentState = ButtonStyle.OVER;
				}
				else
				{
					selected = true;
					currentState = ButtonStyle.DOWN;
				}
			}
		}*/

		override protected function up () : void
		{
			_imageUp.visible = false;
			_imageOver.visible = false;
			_imageDown.visible = false;
			if ( _selected )
				_imageDown.visible = true;
			else
				_imageUp.visible = true;
		}

		override protected function over () : void
		{
			_imageUp.visible = false;
			_imageOver.visible = false;
			_imageDown.visible = false;
			if ( !_selected )
				_imageOver.visible = true;
			else 
				_imageDown.visible = true;
		}

		override protected function down () : void
		{
			_imageUp.visible = false;
			_imageOver.visible = false;
			_imageDown.visible = false;
			if ( _selected )
				_imageDown.visible = true;
			else
				_imageDown.visible = true;
		}

		public function set selected ( value : Boolean ) : void
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

		public function get selected () : Boolean
		{
			return _selected;
		}


		public function set group ( value : TriggerButtonGroup ) : void
		{
			_group = value as RadioButtonGroup;
			if(_selected && _group){
				_group.selected = this;
			}
			
		}

		public function get group () : TriggerButtonGroup
		{
			return _group;
		}

		/** 按下后是否再次点击抬起 **/
		public function get isUp () : Boolean
		{
			return _isUp;
		}

		/**
		 * @private
		 */
		public function set isUp ( value : Boolean ) : void
		{
			_isUp = value;
		}

		override public function dispose () : void
		{
			if ( _group )
				_group.removeButton( this );
			_group = null;
			super.dispose();
			
			
		}

	}
}
