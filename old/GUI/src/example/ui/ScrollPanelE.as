package example.ui
{
	import UI.abstract.component.control.container.Box;
	import UI.abstract.component.control.container.Container;
	import UI.abstract.component.control.image.Image;
	import UI.theme.defaulttheme.button.Button;
	import UI.theme.defaulttheme.scrollBar.ScrollPanel;
	import UI.theme.defaulttheme.tabPanel.TabPanel;
	import UI.theme.defaulttheme.text.TextInput;
	
	import flash.display.Sprite;
	import flash.events.FocusEvent;
	import flash.events.MouseEvent;
	
	public class ScrollPanelE extends Sprite
	{
		private var ui:ScrollPanel;
		private var ui1:TabPanel;
		private var im : Image
		public function ScrollPanelE()
		{
			super();
			ui = new ScrollPanel();
			ui.isShowHScrollbar = true;
			//ui.isShowAlways = true;
			ui.setSize( 300 ,300 );
			//ui.setSize(100,100);
			ui.setPosition(100,100);
			im   = new Image();
			im.isResizeDispatchEvent = true;
			im.setSize(300,300);
			im.url = "image.png";
			ui.addChildToPanel( im );
			
			addChild(ui);
			//addEventListener(MouseEvent.CLICK,onClick);
			
			ui1 = new TabPanel();
			ui1.isResizeDispatchEvent = true;
			ui1.setSize(300,300);
			
			var box1 : Container = new Container();
			ui1.addTab( box1 , "Text" );
			
			var btn : Button = new Button();
			btn.text = "Button";
			box1.addChild( btn );
			
			var box2 : Container = new Container();
			ui1.addTab( box2 , "Image" );
			//ui1.y = 500;
			
			var btn : Button = new Button();
			btn.text = "Button1";
			box2.addChild( btn );
			ui.addChildToPanel(ui1);
			
			
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
			textInput.name = "positionV";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 60;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
			
			var textInput:TextInput = new TextInput();
			textInput.name = "gapToScroll";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 90;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
			
			var textInput:TextInput = new TextInput();
			textInput.name = "tick";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 120;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
			
			var textInput:TextInput = new TextInput();
			textInput.name = "isDown";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 150;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
			
			var textInput:TextInput = new TextInput();
			textInput.name = "x";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 180;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
			var textInput:TextInput = new TextInput();
			textInput.name = "y";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 210;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
		}
		private function onFocusIn(event:FocusEvent):void{
			
		}
		private function onFocusOut(event:FocusEvent):void{
			if(event.currentTarget.name == "gapH" || event.currentTarget.name == "gapW"){
				ui.set9Gap(int((getChildByName("gapW") as TextInput).text),int((getChildByName("gapH") as TextInput).text));
			}else if(event.currentTarget.name == "x" || event.currentTarget.name == "y"){
				ui1[event.currentTarget.name] = (getChildByName(event.currentTarget.name) as TextInput).text;
			}
			else{
				
				ui[event.currentTarget.name] = (getChildByName(event.currentTarget.name) as TextInput).text;
			}
			
		}
		/*private function onClick(event:MouseEvent):void{
			removeEventListener(MouseEvent.CLICK,onClick);
			ui.removeChild(im);
			var ui1:TabPanel = new TabPanel();
			ui1.isResizeDispatchEvent = true;
			ui1.setSize(300,300);
			addChild(ui);
			
			var box1 : Box = new Box();
			ui1.addTab( box1 , "Text" );
			
			var btn : Button = new Button();
			btn.text = "Button";
			box1.addChild( btn );
			
			var box2 : Box = new Box();
			ui1.addTab( box2 , "Image" );
			
			var btn : Button = new Button();
			btn.text = "Button1";
			box2.addChild( btn );
			ui.addChild(ui1);
		}*/
	}
}