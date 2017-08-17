package
{
	import flash.display.Sprite;
	import flash.events.Event;
	
	import ui.Chat;
	import ui.Debug;
	import ui.ErrorAlert;
	import ui.MyUI;
	import ui.SceneButton;
	import ui.SkillProgress;
	import ui.SkillUI;
	import ui.TargetUI;
	import ui.phone.SkillUIPhone;
	
	public class UISprite extends Sprite
	{
		public var errorAlert:ErrorAlert;
		public var chat:Chat;
		public var skillProgress:SkillProgress;
		public var targetUI:TargetUI;
		public var myUI:MyUI;
		public var skillUi:SkillUI;
		public var skillUiPhone:SkillUIPhone;
		public var debug:Debug;
		public var sceneButton:SceneButton;
		public function UISprite()
		{
			super();
			addEventListener(Event.ADDED_TO_STAGE,onAddedToStage);
		}
		private function onAddedToStage(event:Event):void{
			removeEventListener(Event.ADDED_TO_STAGE,onAddedToStage);
			errorAlert = new ErrorAlert();
			addChild(errorAlert);
			chat = new Chat();
			chat.bgUrl = "ui/bg2.png";
			chat.width = 150;
			chat.height = 40;
			addChild(chat);
			skillProgress = new SkillProgress();
			addChild(skillProgress);
			targetUI = new TargetUI();
			addChild(targetUI);
			myUI = new MyUI();
			addChild(myUI);
			if(GlobalData.configData["device"] == 1){
				skillUiPhone = new SkillUIPhone();
				addChild(skillUiPhone);
			}else{
				skillUi = new SkillUI();
				addChild(skillUi);
			}
			
			//debug = new Debug();
			//addChild(debug);
			sceneButton = new SceneButton();
			addChild(sceneButton);
			stage.addEventListener(Event.RESIZE,onResize);
			onResize();
		}
		
		public function onResize(event:Event = null):void{
			errorAlert.x = (stage.stageWidth-errorAlert.width)/2;
			errorAlert.y = stage.stageHeight/5;
			if(GlobalData.configData["device"] == 1){
				chat.x = 0;
				chat.y = stage.stageHeight-chat.height;
			}else{
				chat.x = 100;
				chat.y = stage.stageHeight*4/5;
			}
			
			skillProgress.x = (stage.stageWidth-skillProgress.width)/2;
			skillProgress.y = stage.stageHeight/5*3;
			
			targetUI.x = (stage.stageWidth-targetUI.width)/4*3;
			targetUI.y = stage.stageHeight/5*3.5;
			myUI.x = (stage.stageWidth-myUI.width)/4*1;
			myUI.y = stage.stageHeight/5*3.5;
			if(GlobalData.configData["device"] == 1){
				skillUiPhone.x = stage.stageWidth-skillUiPhone.width;
				skillUiPhone.y = stage.stageHeight-skillUiPhone.height;
			}else{
				skillUi.x = (stage.stageWidth-skillUi.width)/2;
				skillUi.y = stage.stageHeight/5*4;
			}
			
			//debug.x = stage.stageWidth-debug.width;
			//debug.y = (stage.stageHeight-debug.height)/2;
			if(GlobalData.configData["device"] == 1){
				sceneButton.x = stage.stageWidth-sceneButton.width;
				sceneButton.y = 0;
			}else{
				sceneButton.x = (stage.stageWidth-sceneButton.width)/20;
				sceneButton.y = (stage.stageHeight-sceneButton.height)/10;
			}
			
		}
		
	}
}