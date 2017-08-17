package example.ui
{
	import UI.abstract.component.control.base.UIComponent;
	import UI.abstract.component.control.container.Box;
	import UI.abstract.component.control.container.Container;
	import UI.theme.defaulttheme.text.TextInput;
	
	import flash.display.Sprite;
	import flash.events.FocusEvent;
	
	public class BoxE extends Sprite
	{
		private var ui:Box
		public function BoxE()
		{
			super();
			ui = new Box();
			addChild(ui);
			
			var textInput:TextInput = new TextInput();
			textInput.name = "width";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 0;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
			
			var textInput:TextInput = new TextInput();
			textInput.name = "height";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 30;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
			
			var textInput:TextInput = new TextInput();
			textInput.name = "gapH";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 60;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
			
			var textInput:TextInput = new TextInput();
			textInput.name = "gapW";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 90;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
			
			var textInput:TextInput = new TextInput();
			textInput.name = "bgUrl";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 120;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
		}
		private function onFocusIn(event:FocusEvent):void{
			
		}
		private function onFocusOut(event:FocusEvent):void{
			if(event.currentTarget.name == "gapH" || event.currentTarget.name == "gapW"){
				ui.set9Gap(int((getChildByName("gapW") as TextInput).text),int((getChildByName("gapH") as TextInput).text));
			}else{
				ui[event.currentTarget.name] = (getChildByName(event.currentTarget.name) as TextInput).text;
			}
			
		}
	}
}