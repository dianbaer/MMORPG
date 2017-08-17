package example.ui
{
	import UI.abstract.component.control.mc.Animation;
	import UI.abstract.resources.AnCategory;
	import UI.abstract.resources.ResourceUtil;
	import UI.theme.defaulttheme.text.TextInput;
	
	import flash.display.Sprite;
	import flash.events.FocusEvent;
	
	public class AnimationE extends Sprite
	{
		private var ui:Animation;
		private var ui1:Animation;
		private var ui2:Animation;
		public function AnimationE()
		{
			super();
			ui1 = new Animation(AnCategory.MOUNTS,"601200001",21,true,1.0,5);
			ui1.x = 300;
			ui1.dir = 4;
			ui1.y = 300;
			addChild(ui1);
			ui = new Animation(AnCategory.USER,"100012003",21,true,1.0,5);
			ui.x = 300;
			ui.dir = 4;
			ui.y = 300;
			addChild(ui);
			ui2 = new Animation(AnCategory.USER,"100022002_12",21,true,1.0,5);
			ui2.x = 300;
			ui2.dir = 4;
			ui2.y = 300;
			addChild(ui2);
			
			
			var textInput:TextInput = new TextInput();
			textInput.name = "url";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 0;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
			
			var textInput:TextInput = new TextInput();
			textInput.name = "currentFrame";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 30;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
			
			var textInput:TextInput = new TextInput();
			textInput.name = "dir";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 60;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
			
			var textInput:TextInput = new TextInput();
			textInput.name = "action";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 90;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
			
			
		}
		private function onFocusIn(event:FocusEvent):void{
			
			if(event.currentTarget.name == "setSkin"){
				event.currentTarget.text = "";
			}
		}
		private function onFocusOut(event:FocusEvent):void{
			if(event.currentTarget.name == "gapH" || event.currentTarget.name == "gapW"){
				//ui.set9Gap(int((getChildByName("gapW") as TextInput).text),int((getChildByName("gapH") as TextInput).text));
			}else if(event.currentTarget.name == "setSkin"){
				//ui.setSkin((getChildByName("setSkin") as TextInput).text);
			}
			else{
				
				ui[event.currentTarget.name] = (getChildByName(event.currentTarget.name) as TextInput).text;
				ui1[event.currentTarget.name] = (getChildByName(event.currentTarget.name) as TextInput).text;
				ui2[event.currentTarget.name] = (getChildByName(event.currentTarget.name) as TextInput).text;
			}
			
		}
	}
}