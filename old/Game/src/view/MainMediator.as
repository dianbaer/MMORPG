package view
{
	import org.puremvc.as3.interfaces.INotification;
	import org.puremvc.as3.patterns.mediator.Mediator;
	
	public class MainMediator extends Mediator
	{
		public static const NAME:String = "MainMediator";
		public function MainMediator(viewComponent:Object=null)
		{
			super(NAME, viewComponent);
		}
		private function get view1():IGame{
			return viewComponent as IGame;
		}
		
		override public function handleNotification(notification:INotification):void
		{
			switch(notification.getName()){
				case NotiConst.DISCONNECT_SOCKET:
					view1.disConnectSocket();
					break;
				case NotiConst.ENTER_GAME:
					view1.enterGame(notification.getBody()["sceneId"],
						notification.getBody()["serverId"],
						notification.getBody()["monsterId"],
						notification.getBody()["data"],
						notification.getBody()["dir"],
						notification.getBody()["playerPoint"],
						notification.getBody()["dead"],
						notification.getBody()["campId"]
					);
					break;
				case NotiConst.ADD_THING:
					view1.addThing(notification.getBody()["id"],notification.getBody()["position"],notification.getBody()["data"],notification.getBody()["dir"],notification.getBody()["monsterId"],notification.getBody()["dead"],notification.getBody()["campId"],notification.getBody()["enemy"]);
					break;
				case NotiConst.THING_MOVE:
					view1.thingMove(notification.getBody()["id"],notification.getBody()["position"],notification.getBody()["path"],notification.getBody()["dir"],notification.getBody()["nowNode"]);
					break;
				case NotiConst.REMOVE_THING:
					view1.removeThing(notification.getBody()["id"]);
					break;
				case NotiConst.ATTACK:
					view1.attack(notification.getBody()["id"],notification.getBody()["skillId"],notification.getBody()["targetId"],notification.getBody()["position"],notification.getBody()["dir"],notification.getBody()["complete"],notification.getBody()["flyThingId"]);
					break;
				case NotiConst.OUT_SCENE:
					view1.outScene();
					break;
				case NotiConst.SKILL_RESULT:
					view1.skillResult(notification.getBody()["result"],notification.getBody()["skillId"],notification.getBody()["attackNum"],notification.getBody()["attackAndSkillNum"],notification.getBody()["targetId"],notification.getBody()["position"],notification.getBody()["type"],notification.getBody()["flyThingId"]);
					break;
				case NotiConst.SKILL_DAMAGE:
					view1.skillDamage(notification.getBody()["type"],notification.getBody()["skillId"],notification.getBody()["serverId"],notification.getBody()["damage"]);
					break;
				case NotiConst.MONSTER_FOLLOW_USER:
					view1.monsterFollowUser(notification.getBody()["id"],notification.getBody()["position"],notification.getBody()["dir"],notification.getBody()["toTargetId"]);
					break;
				case NotiConst.MONSTER_GO_BACK:
					view1.monsterGoBack(notification.getBody()["id"],notification.getBody()["position"],notification.getBody()["dir"],notification.getBody()["node"]);
					break;
				case NotiConst.ADD_BUFF:
					view1.addBuff(notification.getBody()["targetId"],notification.getBody()["buffArray"],notification.getBody()["length"]);
					break;
				case NotiConst.DEL_BUFF:
					view1.delBuff(notification.getBody()["targetId"],notification.getBody()["buffArray"],notification.getBody()["length"]);
					break;
				case NotiConst.REFRESH_BUFF:
					view1.addBuff(notification.getBody()["targetId"],notification.getBody()["buffArray"],notification.getBody()["length"]);
					break;
				case NotiConst.FLASH:
					view1.flashTo(notification.getBody()["targetId"],notification.getBody()["position"],notification.getBody()["dir"],notification.getBody()["skillId"],notification.getBody()["isClearUseSkill"]);
					break;
				case NotiConst.DEAD:
					view1.dead(notification.getBody()["targetId"]);
					break;
				case NotiConst.LIFE:
					view1.life(notification.getBody()["targetId"],notification.getBody()["hp"]);
					break;
				case NotiConst.SKILL_COMPLETE:
					view1.skillComplete(notification.getBody()["targetId"],notification.getBody()["skillId"]);
					break;
				case NotiConst.READ_SKILL_COMPLETE:
					view1.readSkillComplete();
					break;
				case NotiConst.USE_SKILL:
					view1.useSkill(notification.getBody()["skillId"] as int);
					break;
				case NotiConst.START_LOADING:
					view1.startLoading(notification.getBody()["sceneId"],notification.getBody()["monsterId"]);
					break;
				case NotiConst.AFFECT_BY_SKILL:
					view1.affectBySkill(notification.getBody()["targetId"],notification.getBody()["skillId"],notification.getBody()["toPosition"]);
					break;
				case NotiConst.REMOVE_FLY_THING:
					view1.removeFlyThing(notification.getBody()["id"]);
					break;
				case NotiConst.FLY_THING_CHANGE_TARGET:
					view1.flyThingChangeTarget(notification.getBody()["id"],notification.getBody()["targetId"]);
					break;
				case NotiConst.OMNISLASH_COMPLETE:
					view1.omnislashComplete(notification.getBody()["id"],notification.getBody()["skillId"]);
					break;
				case NotiConst.OMNISLASH_CHANGE:
					view1.omnislashChange(notification.getBody()["id"],notification.getBody()["skillId"],notification.getBody()["targetId"],notification.getBody()["toPosition"]);
					break;
				default:
					trace("注册了消息，未使用");
					break;
			}
		}
		
		override public function listNotificationInterests():Array
		{
			
			return [NotiConst.DISCONNECT_SOCKET,
				NotiConst.ENTER_GAME,
				NotiConst.ADD_THING,
				NotiConst.THING_MOVE,
				NotiConst.REMOVE_THING,
				NotiConst.ATTACK,
				NotiConst.OUT_SCENE,
				NotiConst.SKILL_RESULT,
				NotiConst.SKILL_DAMAGE,
				NotiConst.MONSTER_FOLLOW_USER,
				NotiConst.MONSTER_GO_BACK,
				NotiConst.ADD_BUFF,
				NotiConst.DEL_BUFF,
				NotiConst.REFRESH_BUFF,
				NotiConst.FLASH,
				NotiConst.DEAD,
				NotiConst.LIFE,
				NotiConst.SKILL_COMPLETE,
				NotiConst.READ_SKILL_COMPLETE,
				NotiConst.USE_SKILL,
				NotiConst.START_LOADING,
				NotiConst.AFFECT_BY_SKILL,
				NotiConst.REMOVE_FLY_THING,
				NotiConst.FLY_THING_CHANGE_TARGET,
				NotiConst.OMNISLASH_COMPLETE,
				NotiConst.OMNISLASH_CHANGE
			];
		}
		
		
	}
}