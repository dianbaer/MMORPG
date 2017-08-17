package ui
{
	import flash.events.MouseEvent;
	
	import UI.abstract.component.control.container.Box;
	import UI.theme.defaulttheme.button.Button;
	import UI.theme.defaulttheme.text.TextInput;
	
	public class Chat extends Box
	{
		public var text:TextInput;
		public var button:Button;
		public function Chat()
		{
			super();
			text = new TextInput();
			text.width = 100;
			text.height = 20;
			text.y = 10;
			addChild(text);
			button = new Button();
			button.width = 50;
			button.height = 20;
			button.x = text.x+text.width;
			button.y = text.y;
			button.addEventListener(MouseEvent.CLICK,onClick);
			addChild(button);
			
		}
		private function onClick(event:MouseEvent):void{
			if(GlobalData.inStory){
				return;
			}
			ChangeScene.changeScene(int(text.text));
			
		}
	}
}