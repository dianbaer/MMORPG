package example.ui
{
	import UI.abstract.component.control.image.Image;
	import UI.abstract.component.control.layout.VLayout;
	import UI.abstract.component.data.DataProvider;
	import UI.theme.defaulttheme.list.List;
	import UI.theme.defaulttheme.text.TextInput;
	
	import flash.display.Sprite;
	import flash.events.FocusEvent;
	import flash.events.KeyboardEvent;
	import flash.ui.Keyboard;
	
	public class ListE extends Sprite
	{
		private var list : List;
		private var imm : Image
		public function ListE()
		{
			super();
			imm = new Image();
			imm.setSize( 50 , 100 );
			imm.url = "image.png";
			var arr1 : Array         = [ imm,"0" , "1" , "2" , "3"  , "4" , "5" , "6" , "7" , "8" , "9" ];
			var data1 : DataProvider = new DataProvider( arr1 );
			list          = new List();
			//			UIComponent(list.container).removeEvent();
			list.scrollBar.isShowHScrollbar = false;
			list.setSize( 200 , 200 );
			list.dataProvider = data1;
			list.selectedIndex = 1;
			list.setPosition(100,100);
			addChild( list );
			
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
			textInput.name = "scrollToTop";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 60;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
			
			var textInput:TextInput = new TextInput();
			textInput.name = "selectedIndex";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 90;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
			
			var textInput:TextInput = new TextInput();
			textInput.name = "itemHeight";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 120;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
			
			var textInput:TextInput = new TextInput();
			textInput.name = "align";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 150;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
			
			var textInput:TextInput = new TextInput();
			textInput.name = "imageWidth";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 180;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
			
			var textInput:TextInput = new TextInput();
			textInput.name = "imageHeight";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 210;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
			
			addEventListener(KeyboardEvent.KEY_DOWN,onKeyDown);
		}
		private function onFocusIn(event:FocusEvent):void{
			
		}
		private function onFocusOut(event:FocusEvent):void{
			if(event.currentTarget.name == "gapH" || event.currentTarget.name == "gapW"){
				list.set9Gap(int((getChildByName("gapW") as TextInput).text),int((getChildByName("gapH") as TextInput).text));
			}else if(event.currentTarget.name == "imageWidth"){
				imm.width = int((getChildByName(event.currentTarget.name) as TextInput).text);
			}else if(event.currentTarget.name == "imageHeight"){
				imm.height = int((getChildByName(event.currentTarget.name) as TextInput).text);
			}
			else{
				
				list[event.currentTarget.name] = (getChildByName(event.currentTarget.name) as TextInput).text;
			}
			
		}
		private function onKeyDown(event:KeyboardEvent):void{
			if(event.keyCode == Keyboard.A){
				var arr1 : Array         = [ "0" , "1" , "2" , "3"  , "4" ];
				var data1 : DataProvider = new DataProvider( arr1 );
				list.dataProvider = data1;
			}else if(event.keyCode == Keyboard.B){
				list.layout = new VLayout( 0 , 20 , 1 );
			}
		}
	}
}