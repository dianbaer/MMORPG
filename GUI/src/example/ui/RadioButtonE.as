package example.ui
{
	import UI.abstract.component.control.button.RadioButtonGroup;
	import UI.theme.defaulttheme.button.RadioButton;
	import UI.theme.defaulttheme.text.TextInput;
	
	import flash.display.Sprite;
	import flash.events.FocusEvent;
	
	public class RadioButtonE extends Sprite
	{
		private var radio : RadioButton
		private var radio1 : RadioButton
		private var radio2: RadioButton
		public function RadioButtonE()
		{
			super();
			/** RadioButton RadioButtonGroup **/
			radio = new RadioButton();
			radio.text = "RadioButton1";
			radio.selected = true;
			radio.setSize(30,30);
			radio.x = 150;
			addChild( radio );
			
			radio1 = new RadioButton();
			radio1.text = "RadioButton2";
			radio1.y = radio.height + 5;
			radio1.setSize(30,30);
			radio1.x = 150;
			addChild( radio1 );
			
			radio2 = new RadioButton();
			radio2.text = "RadioButton3";
			radio2.y = radio1.y + radio1.height + 5;
			radio2.setSize(30,30);
			radio2.x = 150;
			addChild( radio2 );
			
			var radioGroup : RadioButtonGroup = new RadioButtonGroup();
			radioGroup.addButton( radio );
			radioGroup.addButton( radio1 );
			radioGroup.addButton( radio2 );
			radioGroup.selected = radio;
			
			var textInput:TextInput = new TextInput();
			textInput.name = "setSkin";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 120;
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
				radio.set9Gap(int((getChildByName("gapW") as TextInput).text),int((getChildByName("gapH") as TextInput).text));
			}else if(event.currentTarget.name == "setSkin"){
				radio.setSkin((getChildByName("setSkin") as TextInput).text);
				radio1.setSkin((getChildByName("setSkin") as TextInput).text);
				radio2.setSkin((getChildByName("setSkin") as TextInput).text);
			}
			else{
				
				radio[event.currentTarget.name] = (getChildByName(event.currentTarget.name) as TextInput).text;
			}
			
		}
	}
}