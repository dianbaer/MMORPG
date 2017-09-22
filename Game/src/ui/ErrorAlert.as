package ui
{
	
	import UI.abstract.component.control.container.Container;
	import UI.theme.defaulttheme.text.Label;
	
	import gui.animation.JugglerManager;
	
	public class ErrorAlert extends Container
	{
		private var text:Label;
		public function ErrorAlert()
		{
			super();
			text = new Label();
			text.size = 16;
			text.color = 0xff0000;
			text.enabled = false;
			addChild(text);
		}
		public function addErrorMessage(str:String):void{
			text.text = str;
			text.alpha = 1;
			JugglerManager.fourJuggler.removeTweens(text);
			JugglerManager.fourJuggler.tween(text,0.5,{alpha:0});
		}
		override public function get width():Number{
			return text.textFieldWidth;
		}
		override public function get height():Number{
			return text.textFieldHeight;
		}
	}
}