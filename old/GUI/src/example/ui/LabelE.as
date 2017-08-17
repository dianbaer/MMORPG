package example.ui
{
	import UI.theme.defaulttheme.text.Label;
	import UI.theme.defaulttheme.text.TextInput;
	
	import flash.display.Sprite;
	import flash.events.FocusEvent;
	
	public class LabelE extends Sprite
	{
		private var ui:Label
		public function LabelE()
		{
			super();
			ui = new Label();
			ui.text = "abc"
			addChild(ui);
			
			
		}
		private function onFocusIn(event:FocusEvent):void{
			
		}
		private function onFocusOut(event:FocusEvent):void{
			if(event.currentTarget.name == "gapH" || event.currentTarget.name == "gapW"){
				ui.set9Gap(int((getChildByName("gapW") as TextInput).text),int((getChildByName("gapH") as TextInput).text));
			}
			else{
				
				ui[event.currentTarget.name] = (getChildByName(event.currentTarget.name) as TextInput).text;
			}
			
		}
	}
}