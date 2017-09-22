package
{
	import UI.App;
	import UI.abstract.component.control.button.RadioButtonGroup;
	import UI.abstract.component.control.dropDownMenu.MenuData;
	import UI.abstract.component.data.DataProvider;
	import UI.abstract.component.event.MenuBarEvent;
	import UI.theme.defaulttheme.button.Button;
	import UI.theme.defaulttheme.button.RadioButton;
	import UI.theme.defaulttheme.dropDownList.DropDownList;
	import UI.theme.defaulttheme.menuBar.MenuBar;
	import UI.theme.defaulttheme.text.Label;
	import UI.theme.defaulttheme.text.TextInput;
	import UI.theme.defaulttheme.window.Window;
	
	import flash.display.Sprite;
	import flash.events.MouseEvent;
	
	public class UISprite1 extends Sprite
	{
		private var menuBar : MenuBar;
		private var win : Window;
		private var textInput:TextInput;
		private var textInput1:TextInput;
		private var textInput2:TextInput;
		private var textInput3:TextInput;
		private var textInput4:TextInput;
		private var dropList : DropDownList;
		private var data2 : DataProvider;
		private var radioGroup : RadioButtonGroup;
		public function UISprite1()
		{
			super();
			createUI();
		}
		public function createUI():void{
			
			var menu : MenuData;
			
			var menuArr : Array = [];
			
			/** MenuBar **/
			menuBar = new MenuBar();
			//menuBar.x = 10;
			addChild(menuBar);
			menuBar.setSize( 500, 25 );
			
			menu = new MenuData();
			menu.text = "新建地图";
			menuArr.push( menu );
			menu = new MenuData();
			menu.text = "加载地图";
			menuArr.push( menu );
			menu = new MenuData();
			menu.text = "保存";
			menuArr.push( menu );
			
			
			
			var dataprovider : DataProvider = new DataProvider( menuArr );
			menuBar.addItem("功能", dataprovider ); 
			
			/*menuArr = [];
			
			menu = new MenuData();
			menu.text = "上传";
			menuArr.push(menu);
			menu = new MenuData();
			menu.text = "下载";
			menuArr.push(menu);
			
			dataprovider = new DataProvider( menuArr );
			menuBar.addItem("上传下载", dataprovider ); */
			
			
			App.event.addEvent( menuBar , MenuBarEvent.SELECT_MENUITEM , onSelectMenuItem );
			
		}
		private function onSelectMenuItem(event:MenuBarEvent):void{
			switch(event.text){
				case "新建地图":
					createSetParameterUI();
					break;
				case "加载地图":
					
					break;
				case "保存":
					
					break;
			}
		}
		private function createSetParameterUI():void{
			/** Window **/
			if(win == null){
				win = new Window();
				win.drag = false;
				win.setSize( 400 , 250 );
				win.text = "设置地图参数";
				var btn2 : Button = new Button();
				btn2.text = "完成设置";
				btn2.setSize(60,25);
				btn2.setPosition((win.width-win.contentX*2-btn2.width)/2,win.height-win.contentY-btn2.height*2);
				win.addChildToContent( btn2 );
				App.event.addEvent( btn2 , MouseEvent.CLICK , onClose );
				
				var label:Label = new Label();
				label.text = "地图ID：";
				win.addChildToContent( label );
				var label1:Label = new Label();
				label1.text = "地图宽度：";
				label1.y = label.textFieldHeight;
				win.addChildToContent( label1 );
				var label2:Label = new Label();
				label2.text = "地图高度：";
				label2.y = label1.textFieldHeight+label1.y;
				win.addChildToContent( label2 );
				var label3:Label = new Label();
				label3.text = "格子宽度：";
				label3.y = label2.textFieldHeight+label2.y;
				win.addChildToContent( label3 );
				var label4:Label = new Label();
				label4.text = "切割大小：";
				label4.y = label3.textFieldHeight+label3.y;
				win.addChildToContent( label4 );
				var label5:Label = new Label();
				label5.text = "使用线程：";
				label5.y = label4.textFieldHeight+label4.y;
				win.addChildToContent( label5 );
				var label6:Label = new Label();
				label6.text = "区域广播：";
				label6.y = label5.textFieldHeight+label5.y;
				win.addChildToContent( label6 );
				
				
				
				textInput = new TextInput();
				textInput.x = label1.textFieldWidth;
				textInput.setSize(100,20);
				textInput.text = "10000";
				win.addChildToContent(textInput);
				textInput1 = new TextInput();
				textInput1.x = label1.textFieldWidth;
				textInput1.y = label1.y;
				textInput1.text = "3000";
				textInput1.setSize(100,20);
				win.addChildToContent(textInput1);
				
				textInput2 = new TextInput();
				textInput2.x = label2.textFieldWidth;
				textInput2.y = label2.y;
				textInput2.text = "3000";
				textInput2.setSize(100,20);
				win.addChildToContent(textInput2);
				
				textInput3 = new TextInput();
				textInput3.x = label3.textFieldWidth;
				textInput3.y = label3.y;
				textInput3.text = "32";
				textInput3.setSize(100,20);
				win.addChildToContent(textInput3);
				
				textInput4 = new TextInput();
				textInput4.x = label4.textFieldWidth;
				textInput4.y = label4.y;
				textInput4.text = "300";
				textInput4.setSize(100,20);
				win.addChildToContent(textInput4);
				
				/** RadioButton RadioButtonGroup **/
				var radio : RadioButton = new RadioButton();
				radio.text = "是";
				radio.selected = true;
				radio.x = label6.textFieldWidth;
				radio.y = label6.y;
				win.addChildToContent( radio );
				
				var radio1 : RadioButton = new RadioButton();
				radio1.text = "否";
				radio1.x = label6.textFieldWidth+50;
				radio1.y = label6.y;
				win.addChildToContent( radio1 );
				
				
				
				radioGroup = new RadioButtonGroup();
				radioGroup.addButton( radio );
				radioGroup.addButton( radio1 );
				radioGroup.selected = radio;
				
				dropList = new DropDownList();
				var arr2 : Array            = [ "1" , "2" , "3" , "4" , "5" , "6" , "7" , "8" , "9" , "10" ];
				data2    = new DataProvider( arr2 );
				dropList.setSize( 100 , 200 );
				dropList.textHeight = 20;
				dropList.dataProvider = data2;
				dropList.x = label5.textFieldWidth;
				dropList.y = label5.y;
				dropList.selectedIndex = 0;
				dropList.isClickText = true;
				win.addChildToContent( dropList );
				
				
			}
			win.setPosition((stage.stageWidth-win.width)/2,(stage.stageHeight-win.height)/2);
			addChild( win );
		}
		private function onClose(event:MouseEvent):void{
			win.parent.removeChild(win);
			var threadId:int = int(data2.getItemAt(dropList.selectedIndex));
			var type:int = 0;
			var radio1 : RadioButton = radioGroup.selected as RadioButton;
			if(radio1.text == "是"){
				type = 1;
			}else{
				type = 2;
			}
			
			(this.parent as MapEditor).createMap(
				int(textInput.text),
				int(textInput1.text),
				int(textInput2.text),
				int(textInput3.text),
				int(textInput4.text),
				threadId,
				type
			);
		}
	}
}