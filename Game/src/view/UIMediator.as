package view
{
	import _45degrees.com.friendsofed.isometric.ActivityThing;
	
	import org.puremvc.as3.interfaces.INotification;
	import org.puremvc.as3.patterns.mediator.Mediator;
	
	public class UIMediator extends Mediator
	{
		public static const NAME:String = "UIMediator";
		public function UIMediator(viewComponent:Object=null)
		{
			super(NAME, viewComponent);
		}
		private function get view1():UISprite{
			return viewComponent as UISprite;
		}
		
		override public function handleNotification(notification:INotification):void
		{
			switch(notification.getName()){
				case NotiConst.ADD_ERROR_MESSAGE:
					view1.errorAlert.addErrorMessage(notification.getBody()["message"] as String);
					
					break;
				case NotiConst.SHOW_SKILL_PROGRESS:
					view1.skillProgress.start(notification.getBody()["skillData"] as Object);
					break;
				case NotiConst.CANNEL_SKILL_PROGRESS:
					view1.skillProgress.cannel();
					
					break;
				case NotiConst.SET_TARGET:
					view1.targetUI.setTarget(notification.getBody()["target"] as ActivityThing);
					
					break;
				case NotiConst.SET_MYSELF:
					view1.myUI.setTarget(notification.getBody()["player"] as ActivityThing);
					if(GlobalData.configData["device"] == 1){
						view1.skillUiPhone.setTarget(notification.getBody()["player"] as ActivityThing);
					}else{
						view1.skillUi.setTarget(notification.getBody()["player"] as ActivityThing);
					}
					
					break;
				case NotiConst.SET_SKILL:
					if(GlobalData.configData["device"] == 1){
						view1.skillUiPhone.setSkill(notification.getBody()["skillArray"] as Array);
					}else{
						view1.skillUi.setSkill(notification.getBody()["skillArray"] as Array);
					}
					
					
					break;
				case NotiConst.ENTER_CD:
					if(GlobalData.configData["device"] == 1){
						view1.skillUiPhone.enterCD(notification.getBody()["skillData"]);
					}else{
						view1.skillUi.enterCD(notification.getBody()["skillData"]);
					}
					
					
					break;
				case NotiConst.DEBUG_MESSAGE:
					if(view1.debug){
						view1.debug.addMessage(notification.getBody()["message"],notification.getBody()["dir"]);
					}
					
					
					break;
				case NotiConst.CLEAR_MESSAGE:
					if(view1.debug){
						view1.debug.clearMessage();
					}
					break;
				case NotiConst.EXIT_CD:
					if(GlobalData.configData["device"] == 1){
						view1.skillUiPhone.exitCD();
					}else{
						view1.skillUi.exitCD();
					}
					
					break;
				case NotiConst.CHANGE_SIZE:
					view1.sceneButton.ChangeSize();
					break;
				default:
					trace("注册了消息，未使用");
					break;
			}
		}
		
		override public function listNotificationInterests():Array
		{
			
			return [NotiConst.ADD_ERROR_MESSAGE,
				NotiConst.SHOW_SKILL_PROGRESS,
				NotiConst.CANNEL_SKILL_PROGRESS,
				NotiConst.SET_TARGET,
				NotiConst.SET_MYSELF,
				NotiConst.SET_SKILL,
				NotiConst.ENTER_CD,
				NotiConst.DEBUG_MESSAGE,
				NotiConst.CLEAR_MESSAGE,
				NotiConst.EXIT_CD,
				NotiConst.CHANGE_SIZE
			];
		}
		
		
	}
}