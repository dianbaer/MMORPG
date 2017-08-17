package example.ui
{
	import UI.theme.defaulttheme.text.TextArea;
	import UI.theme.defaulttheme.text.TextInput;
	
	import flash.display.Sprite;
	import flash.events.FocusEvent;
	
	public class TextAreaE extends Sprite
	{
		private var textArea : TextArea;
		public function TextAreaE()
		{
			super();
			textArea = new TextArea();
			textArea.text = "TextArea";
			textArea.setSize( 150 , 150 );
			textArea.x = 150;
			addChild( textArea );
			
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
		}
		private function onFocusIn(event:FocusEvent):void{
			
		}
		private function onFocusOut(event:FocusEvent):void{
			if(event.currentTarget.name == "gapH" || event.currentTarget.name == "gapW"){
				textArea.set9Gap(int((getChildByName("gapW") as TextInput).text),int((getChildByName("gapH") as TextInput).text));
			}else{
				textArea[event.currentTarget.name] = (getChildByName(event.currentTarget.name) as TextInput).text;
			}
			
		}
	}
}