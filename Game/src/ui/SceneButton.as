package ui
{
	import flash.display.DisplayObject;
	import flash.events.MouseEvent;
	
	import UI.abstract.component.control.container.Box;
	import UI.theme.defaulttheme.button.Button;
	
	import _45degrees.com.friendsofed.isometric.ActivityThing;
	import _45degrees.com.friendsofed.isometric.IsoObject;
	import _45degrees.com.friendsofed.isometric.Monster;
	
	import gui.animation.IAnimatable;
	import gui.animation.JugglerManager;
	import gui.mc.Animation;
	import gui.mc.AnimationEquip;
	
	public class SceneButton extends Box implements IAnimatable
	{
		private var button1:Button;
		private var button2:Button;
		private var button3:Button;
		private var button4:Button;
		private var button6:Button;
		private var button7:Button;
		private var button5:Button;
		private var isStart:Boolean = false;
		private var isStartTwo:Boolean = false;
		public function SceneButton()
		{
			super();
			button1 = new Button();
			button1.text = "去场景1";
			button1.setSize(50,30);
			addChild(button1);
			button2 = new Button();
			button2.text = "去场景2";
			button2.setSize(50,30);
			button2.setPosition(0,30);
			addChild(button2);
			button3 = new Button();
			button3.text = "去场景3";
			button3.setSize(50,30);
			button3.setPosition(0,60);
			addChild(button3);
			button4 = new Button();
			button4.text = "去场景4";
			button4.setSize(50,30);
			button4.setPosition(0,90);
			addChild(button4);
			
			button6 = new Button();
			button6.text = "扩大模型";
			button6.setSize(50,30);
			button6.setPosition(0,160);
			addChild(button6);
			
			button7 = new Button();
			button7.text = "进入剧情";
			button7.setSize(50,30);
			button7.setPosition(0,190);
			addChild(button7);
			
			button5 = new Button();
			if(GlobalData.iscdModel == true){
				button5.text = "切无CD模式";
			}else{
				button5.text = "切CD模式";
			}
			button5.setSize(50,30);
			button5.setPosition(0,120);
			addChild(button5);
			
			
			addEventListener(MouseEvent.CLICK,onClick);
		}
		private function onClick(event:MouseEvent):void{
			if(GlobalData.inStory){
				return;
			}
			switch(event.target){
				case button1:
					ChangeScene.changeScene(10100);
					break;
				case button2:
					ChangeScene.changeScene(10200);
					break;
				case button3:
					ChangeScene.changeScene(10300);
					break;
				case button4:
					ChangeScene.changeScene(10400);
					break;
				case button6:
					ChangeSize();
					break;
				case button7:
					GlobalData.game.enterStory(1);
					break;
				case button5:
					if(GlobalData.iscdModel == true){
						GlobalData.iscdModel = false;
						button5.text = "切CD模式";
						AppFacade.getInstance().sendNotification(NotiConst.EXIT_CD);
					}else{
						GlobalData.iscdModel = true;
						button5.text = "切无CD模式";
					}
					break;
			}
		}
		public function ChangeSize():void{
			if(isStart || isStartTwo){
				return;
			}
			if(GlobalData.configData["scale"] == 100){
				GlobalData.configData["scale"] = 100;
				GlobalData.scale = GlobalData.configData["scale"]/100;
				isStart = true;
				JugglerManager.fourJuggler.add(this);
			}else if(GlobalData.configData["scale"] == 200){
				GlobalData.configData["scale"] = 200;
				GlobalData.scale = GlobalData.configData["scale"]/100;
				isStartTwo = true;
				JugglerManager.fourJuggler.add(this);
			}
			
		}
		public function advanceTime(time:Number):void{
			onEnterFrame();
		}
		private function onEnterFrame():void{
			if(isStart){
				GlobalData.configData["scale"] += 5;
				GlobalData.scale = GlobalData.configData["scale"]/100;
				for(var i:int =0;i<GlobalData.map1._objects.length;i++){
					if(GlobalData.map1._objects[i] is IsoObject){
						var obj:IsoObject = GlobalData.map1._objects[i];
						if(obj is ActivityThing){
							//所有子类都要改变大小
							for (var m:int=obj.numChildren-1; m>=0; --m){
								var child : DisplayObject = obj.getChildAt(m);
								if(child is Animation){
									(child as Animation).changeSize();
								}
								if(child is AnimationEquip){
									(child as AnimationEquip).changeSize();
								}

							}
							//if((obj as ActivityThing).mc is Animation){
							//	(obj as ActivityThing).mc.changeSize();
							//}else{
							//	(obj as ActivityThing).mc.changeSize();
							//}
						}
						if(obj is Monster){
							(obj as Monster).changeNamePos();
						}
						obj.position = obj.position;
					}
					
				}
				for(var ii:int =0;ii<GlobalData.map1._floor.numChildren;ii++){
					if(GlobalData.map1._floor.getChildAt(ii) is IsoObject){
						(GlobalData.map1._floor.getChildAt(ii) as IsoObject).position = (GlobalData.map1._floor.getChildAt(ii) as IsoObject).position;
					}
				}
				changeStory();
				GlobalData.game.changeScale();
				GlobalData.pitchOnFrame.changeScale();
				GlobalData.skillScope.changeScale();
				GlobalData.shadow.draw();
				if(GlobalData.configData["scale"] == 200){
					isStart = false;
					JugglerManager.fourJuggler.remove(this);
				}
			}else if(isStartTwo){
				GlobalData.configData["scale"] -= 5;
				GlobalData.scale = GlobalData.configData["scale"]/100;
				for(var j:int =0;j<GlobalData.map1._objects.length;j++){
					if(GlobalData.map1._objects[j] is IsoObject){
						var obj1:IsoObject = GlobalData.map1._objects[j];
						
						if(obj1 is ActivityThing){
							//所有子类都要改变大小
							for (var n:int=obj1.numChildren-1; n>=0; --n){
								var child1 : DisplayObject = obj1.getChildAt(n);
								if(child1 is Animation){
									(child1 as Animation).changeSize();
								}
								if(child1 is AnimationEquip){
									(child1 as AnimationEquip).changeSize();
								}
							}
							//if((obj1 as ActivityThing).mc is Animation){
							//	(obj1 as ActivityThing).mc.changeSize();
							//}else{
							//	(obj1 as ActivityThing).mc.changeSize();
							//}
						}
						if(obj1 is Monster){
							(obj1 as Monster).changeNamePos();
						}
						obj1.position = obj1.position;
					}
					
				}
				for(var jj:int =0;jj<GlobalData.map1._floor.numChildren;jj++){
					if(GlobalData.map1._floor.getChildAt(jj) is IsoObject){
						(GlobalData.map1._floor.getChildAt(jj) as IsoObject).position = (GlobalData.map1._floor.getChildAt(jj) as IsoObject).position;
					}
				}
				changeStory();
				GlobalData.game.changeScale();
				GlobalData.pitchOnFrame.changeScale();
				GlobalData.skillScope.changeScale();
				GlobalData.shadow.draw();
				if(GlobalData.configData["scale"] == 100){
					isStartTwo = false;
					JugglerManager.fourJuggler.remove(this);
				}
			}
		}
		public function changeStory():void{
			for(var i:int =0;i<GlobalData.mapStory._objects.length;i++){
				if(GlobalData.mapStory._objects[i] is IsoObject){
					var obj:IsoObject = GlobalData.mapStory._objects[i];
					if(obj is ActivityThing){
						//所有子类都要改变大小
						for (var m:int=obj.numChildren-1; m>=0; --m){
							var child : DisplayObject = obj.getChildAt(m);
							if(child is Animation){
								(child as Animation).changeSize();
							}
							if(child is AnimationEquip){
								(child as AnimationEquip).changeSize();
							}
							
						}
						//if((obj as ActivityThing).mc is Animation){
						//	(obj as ActivityThing).mc.changeSize();
						//}else{
						//	(obj as ActivityThing).mc.changeSize();
						//}
					}
					if(obj is Monster){
						(obj as Monster).changeNamePos();
					}
					obj.position = obj.position;
				}
				
			}
			for(var ii:int =0;ii<GlobalData.mapStory._floor.numChildren;ii++){
				if(GlobalData.mapStory._floor.getChildAt(ii) is IsoObject){
					(GlobalData.mapStory._floor.getChildAt(ii) as IsoObject).position = (GlobalData.mapStory._floor.getChildAt(ii) as IsoObject).position;
				}
			}
		}
		override public function get width():Number{
			return getAllChildrenSize().x;
		}
		override public function get height():Number{
			return getAllChildrenSize().y;
		}
	}
}