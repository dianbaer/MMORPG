package ui
{
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.utils.getTimer;
	
	import UI.abstract.component.control.button.RadioButtonGroup;
	import UI.abstract.component.control.container.Box;
	import UI.abstract.component.control.container.Container;
	import UI.abstract.component.data.DataProvider;
	import UI.abstract.component.event.ListEvent;
	import UI.abstract.component.event.RadioButtonGroupEvent;
	import UI.abstract.resources.AnCategory;
	import UI.abstract.resources.AnConst;
	import UI.theme.defaulttheme.button.Button;
	import UI.theme.defaulttheme.button.ToggleButton;
	import UI.theme.defaulttheme.dropDownList.DropDownList;
	import UI.theme.defaulttheme.text.Label;
	import UI.theme.defaulttheme.text.TextInput;
	
	import gui.mc.Animation;
	
	public class ChooseRole extends Box
	{
		private var toggleArray:Array;
		private var roleArray:Array;
		private var animation:Animation;
		private var animation1:Animation;
		private var submit:Button;
		private var container:Container;
		public var label:Label;
		public var label1:Label;
		public var label3:Label;
		public var label4:Label;
		public var label2:Label;
		public var label5:Label;
		public var input:TextInput;
		
		public var nowRole:Object;
		
		private var toggleGroup : RadioButtonGroup;
		
		private var campArray:Array;
		private var dropList : DropDownList;
		public var nowCamp:Object;
		public function ChooseRole()
		{
			super();
			addEventListener(Event.ADDED_TO_STAGE,onAddedToStage);
		}
		private function onAddedToStage(event:Event):void{
			removeEventListener(Event.ADDED_TO_STAGE,onAddedToStage);
			stage.addEventListener(Event.RESIZE,onResize);
			
			//bgUrl = "1.png";
			roleArray = new Array();
			for each (var role:Object in GlobalData.monsterData) 
			{
				if(int(role["id"]/100000) == 1){
					roleArray[roleArray.length] = role;
				}else{
					break;
				}
			}
			toggleArray = new Array();
			container = new Container();
			addChild(container);
			toggleGroup = new RadioButtonGroup();
			
			for(var i:int = 0;i<roleArray.length;i++){
				var toggle : ToggleButton = new ToggleButton();
				toggle.text = roleArray[i]["name"];
				toggle.setSize( 200, 30 );
				toggle.x = 0;
				toggle.y = i*30;
				container.addChild( toggle );
				toggleGroup.addButton(toggle);
				toggleArray[toggleArray.length] = toggle;
				if(i == 0){
					toggleGroup.selected = toggle;
				}
			}
			
			
			toggleGroup.addEventListener(RadioButtonGroupEvent.SELECTED,onSelect);
			nowRole = roleArray[0];
			animation = Animation.fromPool(AnCategory.USER,nowRole["src"],AnConst.BIGBODY,true,1.0,AnConst.DOWN);
			//animation.dir = AnConst.DOWN;
			addChild(animation);
			animation1 = Animation.fromPool(AnCategory.USER,nowRole["srcWeapon"],AnConst.BIGBODY,true,1.0,AnConst.DOWN);
			//animation.dir = AnConst.DOWN;
			addChild(animation1);
			
			label = new Label();
			label.text = "ID:";
			label.setSize(60,20);
			addChild(label);
			input = new TextInput();
			var num:Number = Math.random();
			input.text = getTimer()+int(num*100000000)+"";
			input.setSize(80,20);
			addChild(input);
			label1 = new Label();
			label1.color = 0xff0000;
			label1.text = "ID必须保证不同，同一个id会把在线的和登陆的全部踢下线的！！";
			label1.setSize(200,20);
			addChild(label1);
			
			label3 = new Label();
			label3.color = 0x000000;
			label3.text = "战士：偏向近身攻击以及拉近距离的技能";
			label3.setSize(200,20);
			addChild(label3);
			
			label4 = new Label();
			label4.color = 0x000000;
			label4.text = "法师：偏向远程攻击以及拉远距离的技能";
			label4.setSize(200,20);
			addChild(label4);
			
			submit = new Button();
			submit.setSize(80,20);
			submit.text = "登录";
			addChild(submit);
			submit.addEventListener(MouseEvent.CLICK,onClick);
			
			campArray = new Array();
			var arr2 : Array = new Array();
			for each (var camp:Object in GlobalData.campData) 
			{
				if(int(camp["id"]/1000) == 1){
					campArray[campArray.length] = camp;
					arr2[arr2.length] = camp["name"];
				}else{
					break;
				}
			}
			dropList = new DropDownList();
			dropList.addEventListener(ListEvent.CLICK_ITEM,onChange);
			
			var data2 : DataProvider    = new DataProvider( arr2 );
			dropList.setSize( 100 , 100 );
			dropList.dataProvider = data2;
			dropList.selectedIndex = 0;
			dropList.isClickText = true;
			addChild( dropList );
			
			label2 = new Label();
			label2.color = 0xff0000;
			label2.text = "不同的阵营可以测试PK！！";
			label2.setSize(200,20);
			addChild(label2);
			
			label5 = new Label();
			label5.color = 0xff0000;
			label5.text = "此项目只是技术展示版。用2011年《永恒之站》的美术资源作为目前的展示资源，并不是最终游戏效果。";
			label5.setSize(400,20);
			label5.textFieldWidth = 400;
			label5.wordWrap = true;
			addChild(label5);
			
			nowCamp = campArray[0];
			onResize();
		}
		private function onChange(event:ListEvent):void{
			nowCamp = campArray[dropList.selectedIndex];
			//trace(nowCamp["name"]);
		}
		
		private function onSelect(event:RadioButtonGroupEvent = null):void{
			
			var toggle : ToggleButton = event.button as ToggleButton;
			var index:int = toggleArray.indexOf(toggle);
			nowRole = roleArray[index];
			animation.jtaName = nowRole["src"];
			animation1.jtaName = nowRole["srcWeapon"];
		}
		private function onClick(event:MouseEvent):void{
			dispatchEvent(new Event(Event.COMPLETE));
		}
		public function onResize(event:Event = null):void{
			setSize(stage.stageWidth,stage.stageHeight);
			animation.x = stage.stageWidth/2;
			animation.y = stage.stageHeight/2;
			animation1.x = stage.stageWidth/2;
			animation1.y = stage.stageHeight/2;
			container.x = 0;
			container.y = 0;
			label.x = 0;
			label.y = container.getAllChildrenSize().y+container.y+20;
			input.x = label.width+label.x;
			input.y = label.y;
			label1.x = input.width+input.x;
			label1.y = input.y;
			label3.x = 200;
			label3.y = (30-label3.textFieldHeight)/2;
			label4.x = 200;
			label4.y = 30+(30-label3.textFieldHeight)/2;
			dropList.x = (container.getAllChildrenSize().x-dropList.width)/2;
			dropList.y = input.height+input.y+20;
			label2.x = dropList.width+dropList.x;
			label2.y = dropList.y;
			submit.x = (container.getAllChildrenSize().x-submit.width)/2;
			submit.y = dropList.y+dropList.height+20;
			label5.x = submit.x;
			label5.y = submit.y+submit.height+20;
		}
		override public function dispose():void{
			toggleArray.length = 0;
			toggleArray = null;
			roleArray.length = 0;
			roleArray = null;
			animation.x = 0;
			animation.y = 0;
			animation.dispose();
			animation = null;
			animation1.x = 0;
			animation1.y = 0;
			animation1.dispose();
			animation1 = null;
			submit.removeEventListener(MouseEvent.CLICK,onClick);
			submit = null;
			container = null;
			label = null;
			input = null;
			nowRole = null;
			toggleGroup.removeEventListener(RadioButtonGroupEvent.SELECTED,onSelect);
			toggleGroup = null;
			stage.removeEventListener(Event.RESIZE,onResize);
			campArray.length = 0;
			campArray = null;
			dropList.removeEventListener(ListEvent.CLICK_ITEM,onChange);
			dropList = null;
			nowCamp = null;
			super.dispose();
		}
	}
}