/**
 * 1.还可以进一步优化的tween类，把tween类拆分成游戏内的tween和ui的tween，减少循环次数
 * 2.自动攻击，是服务器自己自动攻击，客户端不发送自动攻击的消息，减少消息包
 * 
 */
package
{
	import flash.display.BitmapData;
	import flash.display.DisplayObject;
	import flash.display.Sprite;
	import flash.display.StageAlign;
	import flash.display.StageScaleMode;
	import flash.events.Event;
	import flash.events.KeyboardEvent;
	import flash.events.MouseEvent;
	import flash.filters.BlurFilter;
	import flash.geom.Matrix;
	import flash.geom.Point;
	import flash.text.TextField;
	import flash.text.TextFormat;
	import flash.ui.Keyboard;
	
	import UI.App;
	import UI.abstract.component.control.base.UIComponent;
	import UI.abstract.resources.AnCategory;
	import UI.abstract.resources.AnConst;
	import UI.abstract.resources.ResourceUtil;
	import UI.abstract.resources.item.JsonResource;
	import UI.abstract.resources.loader.MultiLoader;
	import UI.abstract.tween.TweenManager;
	import UI.abstract.utils.ColorFilters;
	import UI.abstract.utils.CommonPool;
	import UI.abstract.utils.Stats;
	import UI.theme.defaulttheme.Skin;
	
	import _45degrees.com.friendsofed.isometric.ActivityThing;
	import _45degrees.com.friendsofed.isometric.Data;
	import _45degrees.com.friendsofed.isometric.DrawnIsoTile;
	import _45degrees.com.friendsofed.isometric.FlySkillEffect;
	import _45degrees.com.friendsofed.isometric.IsoObject;
	import _45degrees.com.friendsofed.isometric.IsoUtils;
	import _45degrees.com.friendsofed.isometric.IsoWorld;
	import _45degrees.com.friendsofed.isometric.Monster;
	import _45degrees.com.friendsofed.isometric.PitchOnFrame;
	import _45degrees.com.friendsofed.isometric.Player;
	import _45degrees.com.friendsofed.isometric.Point3D;
	import _45degrees.com.friendsofed.isometric.SkillEffect;
	import _45degrees.com.friendsofed.isometric.SkillScope;
	
	import _astar.AStar;
	import _astar.Grid;
	import _astar.Node;
	
	import gui.animation.IAnimatable;
	import gui.animation.JugglerManager;
	import gui.mc.Animation;
	
	import proxy.PlayerProxy;
	
	import ui.ChooseRole;
	import ui.Loading;
	import ui.Speak;
	
	import view.UIMediator;
	
	[SWF(height="720",width="1280",frameRate="60")]
	public class GameM extends GlobalData implements IAnimatable,IGame
	{
		private static var helpPoint:Point = new Point();
		private static var helpPoint1:Point = new Point();
		private static var helpPoint2:Point = new Point();
		private static var helpPos:Point3D = new Point3D();
		private static var helpPos1:Point3D = new Point3D();
		private static var helpMatrix:Matrix = new Matrix();
		/** 格式化 **/
		private static var _defaultFormat : TextFormat = new TextFormat( "宋体" , 20,null,true );

		private var mapWidth:int = 0;
		private var mapHeight:int = 0;

		
		private var mouseDownX:Number;
		private var mouseDownY:Number;
		private var isDown:Boolean;
		private var minX:int = 0;
		private var minY:int = 0;
		private var maxX:int = 0;
		private var maxY:int = 0;

		private var astar:AStar = new AStar();
		private var player:Player;
		
		private var bmt:BitmapData = new BitmapData(1, 1, true, 0x00000000);
		private var pointUser:ActivityThing;
		private var camera:DisplayObject;
		
		

		private var star:Stats;
		//上一次照相机的位置
		private var upPoint3d:Point3D;
		//上一帧场景的宽度和高度
		private var upStageWidth:Number = 0;
		private var upStageHeight:Number = 0;
		//private static var sortFrame:int = 5;
		private var nowFrame:int = 5;
		
		
		
		//记录进入场景时的数据
		private var sceneId:int;
		private var serverId:int;
		private var monsterId:int;
		private var data:Data;
		private var dir:int;
		private var playerPoint:Point;
		private var isDead:Boolean;
		private var campId:int;
		private var mapData:Object;
		
		private var mapBG:Map;
		
		private var uiSprite:UISprite;
		private var mapContiner:Sprite;
		
		//当前正要施放的技能
		private var skill:Object = null;
		
		

		
		//每个键位对应的模块
		private var keyMap:KeyMap = new KeyMap();
		
		//选择人物界面
		private var chooseRole:ChooseRole;

		
		

		
		
		private var filter:BlurFilter;
		private var storyManager:StoryManager = new StoryManager();
		private var speakUi:Speak = new Speak();
		public function GameM()
		{
			super();
			stage.align = StageAlign.TOP_LEFT;
			stage.scaleMode = StageScaleMode.NO_SCALE;
			//stage.displayState = StageDisplayState.FULL_SCREEN_INTERACTIVE;
			addEventListener(Event.ADDED_TO_STAGE,onAddedToStage);
		}
		private function countMaxAndMin(point:Point):void{
			if(minX > point.x){
				minX = point.x;
			}
			if(minY > point.y){
				minY = point.y;
			}
			if(maxX < point.x){
				maxX = point.x;
			}
			if(maxY < point.y){
				maxY = point.y;
			}
		}
		private function onAddedToStage(event:Event):void{
			removeEventListener(Event.ADDED_TO_STAGE,onAddedToStage);
			App.init( this );
			JugglerManager.init(this.stage);
			GlobalData.game = this;
			TweenManager.initClass();
			stage.addEventListener(MouseEvent.RIGHT_MOUSE_DOWN,onRightDown);
			loading = new Loading();
			loading.addEventListener(Event.COMPLETE,onloadLoadingComplete);
			addChild(loading);
			
			
			
			
		}
		private function onloadLoadingComplete(event:Event):void{
			loading.removeEventListener(Event.COMPLETE,onloadLoadingComplete);
			var multiLoader:MultiLoader = App.loader.loadList(["skill.json","monster.json","buff.json","camp.json","config.json"],loadJsonComplete,null,false);
			loading.setProcess(multiLoader,"加载配置文件");
			multiLoader.load();
		}
		private function loadJsonComplete():void{
			skillData = (App.loader.getResource("skill.json") as JsonResource).object;
			monsterData = (App.loader.getResource("monster.json") as JsonResource).object;
			buffData = (App.loader.getResource("buff.json") as JsonResource).object;
			campData = (App.loader.getResource("camp.json") as JsonResource).object;
			configData = (App.loader.getResource("config.json") as JsonResource).object;
			GlobalData.ip = GlobalData.configData["ip"];
			GlobalData.port = GlobalData.configData["port"];
			GlobalData.sceneId = GlobalData.configData["sceneId"];
			GlobalData.scale = configData["scale"]/100;
			var roleArray:Array = new Array();
			var url:String;
			for each (var role:Object in GlobalData.monsterData) 
			{
				if(int(role["id"]/100000) == 1){
					url = ResourceUtil.getAnimationURL(AnCategory.USER,role["src"],AnConst.BIGBODY);
					if(roleArray.indexOf(url) == -1){
						roleArray.push(url);
					}
					url = ResourceUtil.getAnimationURL(AnCategory.USER,role["srcWeapon"],AnConst.BIGBODY);
					if(roleArray.indexOf(url) == -1){
						roleArray.push(url);
					}
				}else{
					break;
				}
			}
			var skinList:Array = Skin.getList( Skin.BUTTON );
			roleArray = roleArray.concat(skinList);
			var skinList1:Array = Skin.getList( Skin.TEXTINPUT );
			roleArray = roleArray.concat(skinList1);
			var multiLoader:MultiLoader = App.loader.loadList(roleArray,loadResourceComplete,null,false);
			loading.setProcess(multiLoader,"加载登陆资源");
			multiLoader.load();
			
		}
		private function loadResourceComplete():void{
			chooseRole = new ChooseRole();
			chooseRole.addEventListener(Event.COMPLETE,onChooseComplete);
			addChild(chooseRole);
			removeChild(loading);
		}
		private function onChooseComplete(event:Event):void{
			role = chooseRole.nowRole;
			chooseId = int(chooseRole.input.text);
			chooseCamp = chooseRole.nowCamp;
			if(chooseId <= 0){
				return;
			}else{
				chooseRole.removeEventListener(Event.COMPLETE,onChooseComplete);
				chooseRole.dispose();
				chooseRole = null;
				addChild(loading);
				AppFacade.getInstance().startUp(this);
			}
			
		}
		public function disConnectSocket():void{
			outScene();
			loading.progressBar.text = "与服务器断开连接";
		}
		public function outScene():void{
			//结束剧情要放到最高的位置，因为他会有很多操作，影响到加载界面，ui界面
			storyManager.endStory(true);
			addChild(loading);
			if(mapBG){
				mapBG.dispose();
			}
			if(map1){
				map1.dispose();
				
				//map1 = null;
			}
			
			
			removeChild(uiSprite);
			//清空追踪
			clearTrace();
			
			if(player){
				
				player = null;
			}
			
			
			skill = null;
			if(skillScope){
				skillScope.dispose();
				//skillScope = null;
			}
			if(pitchOnFrame){
				pitchOnFrame.dispose();
				//pitchOnFrame = null;
			}
			
			nowBeEffectOnSkill = 0;
			nowBeEffectOnSkillDistance = 0;
			nowBeEffectOnSkillOmnislash = 0;
			camera = null;
			if(grid){
				grid.clear();
			}
			
			
			//if(mapContiner){
			//	mapContiner.removeEventListener(MouseEvent.MOUSE_MOVE,onMouseMove);
			//	
			//}
			stage.removeEventListener(MouseEvent.MOUSE_DOWN,onClick);
			stage.removeEventListener(MouseEvent.RIGHT_MOUSE_DOWN,onRightDown1);
			//stage.addEventListener(MouseEvent.RIGHT_MOUSE_DOWN,onRightDown1);
			//removeEventListener(MouseEvent.MOUSE_UP,onMouseUp);
			//removeEventListener(MouseEvent.CLICK,onClick);
			stage.removeEventListener(KeyboardEvent.KEY_DOWN,onKeyDown);
			stage.removeEventListener(KeyboardEvent.KEY_UP,onKeyUp);
			if(star && star.parent){
				removeChild(star);
				//star = null;
			}
			
			
			pointUser = null;
			
			JugglerManager.threeJuggler.remove(this);
			upPoint3d = null;
			upStageHeight = 0;
			upStageWidth = 0;
			sceneId = 0;
			serverId = 0;
			monsterId = 0;
			//data = null;
			dir = 0;
			//playerPoint = null;
			isDead = false;
			campId = 0;
			mapData = null;
			nowTargetEffect = null;
			//clear();
		}
		//清空追踪
		public function clearTrace():void{
			isTrace = false;
			traceType = 0;
			traceTarget = null;
			
		}
		//开始追踪
		public function startTrace(type:int,targets:*,time:Number):void{
			isTrace = true;
			traceType = type;
			traceTarget = targets;
			
			if(type == 1){
				var targetPoint:Point3D = targets as Point3D;
				start.setValue(upPoint3d.x,0,upPoint3d.z);
				IsoUtils.isoToScreen(start,helpPoint);
				IsoUtils.isoToScreen(targetPoint,helpPoint1);
				traceDistance = Point.distance(helpPoint,helpPoint1)/GlobalData.scale;
				speed = traceDistance/time;
				if(speed < 1){
					speed = 1;
				}
				moveD = 0;
			}else if(type == 2){
				var targets:ActivityThing = targets as ActivityThing;
				start.setValue(upPoint3d.x,0,upPoint3d.z);
				targets.position.clone(helpPos);
				helpPos.y = 0;
				IsoUtils.isoToScreen(start,helpPoint);
				IsoUtils.isoToScreen(helpPos,helpPoint1);
				traceDistance = Point.distance(helpPoint,helpPoint1)/GlobalData.scale;
				speed = traceDistance/time;
				if(speed < 1){
					speed = 1;
				}
				moveD = 0;
			}
		}
		public function startLoading(sceneId:int,monsterId:int):void{
			addChild(loading);
			ChangeScene.loadingScene(sceneId,monsterId);
		}
		//被技能影响
		public function affectBySkill(targetId:int,skillId:int,toPosition:Point3D):void{
			var drawnIsoBox:ActivityThing = map1.thingDict[targetId];
			if(drawnIsoBox){
				drawnIsoBox.affectBySkill(skillData[skillId],toPosition);
			}
		}
		/**
		 * 技能施放返回
		 */
		public function skillResult(result:int,skillId:int,attackNum:int,attackAndSkillNum:int,targetId:int,position:Point3D,type:int,flyThingId:int):void{
			//if(map1 == null){
			//	return;
			//}
			var target:ActivityThing = null;
			if(targetId == 0){
				
			}else{
				target = map1.thingDict[targetId];
			}
			player.skillResult(result,skillId,attackNum,attackAndSkillNum,target,position,type,flyThingId);
		}
		public static function bombEffect(attackSkill:Object,position:Point3D):void{
			if(attackSkill["bombEffect"]){
				var skillMc1:Animation = Animation.fromPool(AnCategory.EFFECT,attackSkill["bombEffect"],AnConst.STAND,false,attackSkill["bombFps"]/1000.0,AnConst.DOWN);
				var skillEffect:SkillEffect = SkillEffect.fromPool(GlobalData.cellWidth);
				position.y = 0;
				skillEffect.position = position;
				
				skillEffect.addChild(skillMc1);
				skillEffect.addEvent();
				map1.addChildToWorld(skillEffect);
			}
		}
		public function skillDamage(type:int,skillId:int,serverId:int,damage:int):void{
			//if(map1 == null){
			//	return;
			//}
			var target:ActivityThing = map1.thingDict[serverId];
			/*for(var i:int = map1._playerObjects.length-1;i>=0;i--){
				var drawnIsoBox : ActivityThing = map1._playerObjects[i] as ActivityThing;
				if(drawnIsoBox.thingId == serverId){
					target = drawnIsoBox;
					break;
				}
			}*/
			if(target != null){
				//return;
				var text:TextField = CommonPool.fromPoolText();
				if(type == 3 || type == 4){
					text.text = "+"+damage.toString();
				}else{
					text.text = "-"+damage.toString();
				}
				
				
				text.mouseEnabled = false;
				text.mouseWheelEnabled = false;
				text.tabEnabled = false;
				text.defaultTextFormat = _defaultFormat;
				text.setTextFormat(_defaultFormat);
				text.filters = [ColorFilters.colour_Black];
				if(type == 3 || type == 4){
					text.textColor = 0x00ff00;
				}else{
					text.textColor = 0xff0000;
				}
				target.position.clone(helpPos);
				helpPos.y = 0;
				IsoUtils.isoToScreen(helpPos,helpPoint);
				text.x = helpPoint.x;
				text.y = helpPoint.y+target.bodyPoint.y*GlobalData.scale;
				
				var toX:int = 0;
				if(target.textPosition){
					toX = 50;
					target.textPosition = false;
				}else{
					toX = -50;
					target.textPosition = true;
				}
				if(type == 1 || type == 3){
					target.hitEffect(skillData[skillId]);
				}else{
					
				}
				if(type == 3 || type == 4){
					target.speedData.addHP(damage);
				}else{
					target.speedData.decHP(damage);
				}
				
				map1.addChildToText(text);
				
				JugglerManager.threeJuggler.tween(text,1.0,{onComplete:onComplete,onCompleteArgs:[text],y:text.y-125/*,x:text.x+toX*/});
				
			}
		}
		private function onComplete(text:TextField):void{
			if(text.parent){
				text.parent.removeChild(text);
				CommonPool.toPoolText(text);
			}
			
		}
		public function monsterFollowUser(id:int,position:Point3D,dir:int,toTargetId:int):void{
			//if(map1 == null){
			//	return;
			//}
			var drawnIsoBox:Monster = map1.thingDict[id];
			var target:ActivityThing = map1.thingDict[toTargetId];
			/*for(var i:int = map1._playerObjects.length-1;i>=0;i--){
				if((map1._playerObjects[i] as ActivityThing).thingId == id){
					drawnIsoBox = map1._playerObjects[i] as Monster;
					if(target){
						break;
					}
					
				}
				
				if((map1._playerObjects[i] as ActivityThing).thingId == toTargetId){
					target = map1._playerObjects[i] as ActivityThing;
					if(drawnIsoBox){
						break;
					}
				}
			}*/
			if(drawnIsoBox){
				drawnIsoBox.position = position;
				drawnIsoBox.dir = dir;
				//清空其他行动
				drawnIsoBox.path = null;
				drawnIsoBox.isBack = false;
				drawnIsoBox.backNode = null;
				//如果目标存在的话，设置最总目标
				if(target){
					drawnIsoBox.followTarget = target;
					drawnIsoBox.action = AnConst.WALK;
				}
			}
			
		}
		public function monsterGoBack(id:int,position:Point3D,dir:int,node:Node):void{
			//if(map1 == null){
			//	return;
			//}
			var drawnIsoBox:Monster = map1.thingDict[id];
			/*for(var i:int = map1._playerObjects.length-1;i>=0;i--){
				if((map1._playerObjects[i] as ActivityThing).thingId == id){
					drawnIsoBox = map1._playerObjects[i] as Monster;
					break;
				}
			}*/
			if(drawnIsoBox){
				drawnIsoBox.position = position;
				drawnIsoBox.dir = dir;
				//清空其他行动
				drawnIsoBox.path = null;
				drawnIsoBox.followTarget = null;
				drawnIsoBox.isBack = true;
				
				drawnIsoBox.backNode = node;
				drawnIsoBox.action = AnConst.WALK;
				
			}
		}
		public function addBuff(targetId:int,buffArray:Array,length:int,isStory:Boolean = false):void{
			var drawnIsoBox:ActivityThing;
			if(isStory){
				drawnIsoBox = mapStory.thingDict[targetId];
			}else{
				drawnIsoBox = map1.thingDict[targetId];
			}
			if(drawnIsoBox){
				
				drawnIsoBox.addBuff(buffArray,length);
			}
		}
		public function delBuff(targetId:int,buffArray:Array,length:int,isStory:Boolean = false):void{
			var drawnIsoBox:ActivityThing;
			if(isStory){
				drawnIsoBox = mapStory.thingDict[targetId];
			}else{
				drawnIsoBox = map1.thingDict[targetId];
			}
			if(drawnIsoBox){
				drawnIsoBox.delBuff(buffArray,length);
			}
		}
		public function flashTo(targetId:int,position:Point3D,dir:int,skillId:int,isClearUseSkill:int,isStory:Boolean = false):void{
			var drawnIsoBox:ActivityThing;
			if(isStory){
				drawnIsoBox = mapStory.thingDict[targetId];
			}else{
				drawnIsoBox = map1.thingDict[targetId];
			}
			
			
			if(drawnIsoBox){
				//如果需要清理正在施放的技能，则清理
				if(isClearUseSkill == 1){
					drawnIsoBox.clearSkill(false);
				}
				drawnIsoBox.position = position;
				drawnIsoBox.dir = dir;
				drawnIsoBox.hitEffect(skillData[skillId],isStory);
			}
			//如果这个人是player，传送完毕，关闭限制
			if(drawnIsoBox == player){
				drawnIsoBox.shadow.x = drawnIsoBox.x;
				drawnIsoBox.shadow.z = drawnIsoBox.z;
				nowBeEffectOnSkill--;
				startTrace(2,drawnIsoBox,400);
			}
		}
		//无敌斩完成
		public function omnislashComplete(targetId:int,skillId:int):void{
			
			var drawnIsoBox:ActivityThing = map1.thingDict[targetId];
			
			if(drawnIsoBox){
				bombEffect(skillData[skillId],drawnIsoBox.position.clone());
			}
			//如果这个人是player，关闭无敌斩限制
			if(drawnIsoBox == player){
				nowBeEffectOnSkillOmnislash--;
				if(!(player.isUserSkill && player.skillData["type"] == Skill.OMNISLASH)){
					startTrace(2,drawnIsoBox,400);
					trace("服务器后置了");
				}
			}
		}
		//无敌斩改变目标
		public function omnislashChange(targetId:int,skillId:int,toTargetId:int,toPosition:Point3D):void{
			var drawnIsoBox:ActivityThing = map1.thingDict[targetId];
			var toTarget:ActivityThing = map1.thingDict[toTargetId];
			
			if(drawnIsoBox){
				drawnIsoBox.omnislashChange(skillData[skillId],toPosition);
			}else{
				trace("无敌斩时未找到攻击玩家");
			}
			if(toTarget == null){
				trace("无敌斩时未找到被攻击玩家");
			}
		}
		public function dead(targetId:int):void{
			//if(map1 == null){
			//	return;
			//}
			var drawnIsoBox:ActivityThing = map1.thingDict[targetId];
			/*for(var i:int = map1._playerObjects.length-1;i>=0;i--){
				if((map1._playerObjects[i] as ActivityThing).thingId == targetId){
					drawnIsoBox = map1._playerObjects[i] as ActivityThing;
					break;
				}
			}*/
			//如果是自己
			if(drawnIsoBox == player){
				player.dead(true);
				
			}else{
				drawnIsoBox.dead(true);
			}
		}
		public function life(targetId:int,hp:int):void{
			//if(map1 == null){
			//	return;
			//}
			var drawnIsoBox:ActivityThing = map1.thingDict[targetId];
			/*for(var i:int = map1._playerObjects.length-1;i>=0;i--){
				if((map1._playerObjects[i] as ActivityThing).thingId == targetId){
					drawnIsoBox = map1._playerObjects[i] as ActivityThing;
					break;
				}
			}*/
			//如果是自己
			if(drawnIsoBox == player){
				
				player.life(hp);
				
			}else{
				drawnIsoBox.life(hp);
			}
		}
		public function skillComplete(targetId:int,skillId:int):void{
			//if(map1 == null){
			//	return;
			//}
			var drawnIsoBox:ActivityThing = map1.thingDict[targetId];
			/*for(var i:int = map1._playerObjects.length-1;i>=0;i--){
				if((map1._playerObjects[i] as ActivityThing).thingId == targetId){
					drawnIsoBox = map1._playerObjects[i] as ActivityThing;
					break;
				}
			}*/
			//if(drawnIsoBox.isUserSkill && drawnIsoBox.skillData["id"] != skillId){
			//	trace("回来的技能不是现在这个技能");
			//	return;
			//}
			if(drawnIsoBox != null){
				/****如果服务器提前完成的话，客户端也不要完成，不然有一种拉着过去的感觉****/
				//drawnIsoBox.clearSkill(true,skillId);
				/****如果服务器提前完成的话，客户端也不要完成，不然有一种拉着过去的感觉****/
				drawnIsoBox.hitEffect(skillData[skillId]);
			}
			
			
		}
		public function readSkillComplete():void{
			//if(map1 == null){
			//	return;
			//}
			player.cannelReadSkill();
			AppFacade.getInstance().sendNotification(NotiConst.CANNEL_SKILL_PROGRESS);
			if(player.nextSkill > 0){
				player.userSkillClient(player.nextSkill,player.nextTarget,player.nextPosition);
				player.cannelNextSkill();
			}
			//trace("取消了施法");
			
		}
		
		
		
		public function enterGame(sceneId:int,serverId:int,monsterId:int,data:Data,dir:int,playerPoint:Point,dead:int,campId:int):void{
			this.sceneId = sceneId;
			this.serverId = serverId;
			this.monsterId = monsterId;
			this.data = data;
			this.dir = dir;
			this.playerPoint = playerPoint;
			this.isDead = dead == 1 ? true:false;
			this.campId = campId;
			if(uiSprite == null){
				uiSprite = new UISprite();
				addChildAt(uiSprite,0);
				AppFacade.getInstance().registerMediator(new UIMediator(uiSprite));
			}else{
				addChildAt(uiSprite,0);
			}
			if(mapContiner == null){
				mapContiner = new Sprite();
				addChild(mapContiner);
				//再进入一次场景
				//App.loader.load("map/"+sceneId+".json",loadFirst);
				loadComplete(App.loader.getResource("map/"+sceneId+".json") as JsonResource);
			}else{
				//这句话的意思就是，直接进入loadComplete
				//App.loader.load("map/"+sceneId+".json",loadComplete);
				loadComplete(App.loader.getResource("map/"+sceneId+".json") as JsonResource);
			}
			
			
		}
		//第一次进入游戏的时候，在传送一次场景，服务器懒得做处理了，这样就得了
		//private function loadFirst(jsonResource:JsonResource):void{
			
		//	ChangeScene.changeScene(jsonResource.object["id"]);
		//}
		//进入剧情
		public function enterStory(storyId:int):void{
			if(inStory){
				trace("在剧情中");
				return;
			}
			inStory = true;
			mapContiner.removeChild(map1);
			
			mapContiner.addChild(mapStory);
			removeChild(uiSprite);
			
			
			//加载剧情资源
			storyManager.enterStory(storyId);
		}
		public function endStory(isDispose:Boolean = false):void{
			inStory = false;
			
			
			if(mapStory){
				mapStory.dispose();
			}
			closeSpeak();
			if(!isDispose){
				mapContiner.addChild(map1);
				
			}
			if(uiSprite){
				addChild(uiSprite);
				setChildIndex(uiSprite,numChildren-1);
			}
			if(camera){
				startTrace(2,camera,400);
			}
			
		}
		public function setCamera(drawnIsoBox:ActivityThing):void{
			
		}
		public function clearCamera():void{
			
		}
		public function addLoading():void{
			addChild(loading);
		}
		public function removeLoading():void{
			removeChild(loading);
		}
		private function loadComplete(jsonResource:JsonResource):void{
			mapData = jsonResource.object;
			cellWidth = mapData["cellWidth"];
			mapWidth = mapData["mapWidth"];
			mapHeight = mapData["mapHeight"];
			if(!mapBG){
				mapBG = new Map(mapData);
			}else{
				mapBG.reset(mapData);
			}
			
			mapContiner.addChild(mapBG);
			
			if(!map1){
				map1 = new IsoWorld();
			}
			if(!mapStory){
				mapStory = new IsoWorld();
			}
			
			
			mapContiner.addChild(map1);
			
			helpPoint.x = 0;
			helpPoint.y = 0;
			/*var point0:Point3D = */IsoUtils.screenToIso(helpPoint,helpPos);
			helpPoint.x = Math.round(helpPos.x/cellWidth);
			helpPoint.y = Math.round(helpPos.z/cellWidth);
			//var point00:Point = new Point(Math.round(helpPos.x/cellWidth),Math.round(helpPos.z/cellWidth));
			countMaxAndMin(helpPoint);
			helpPoint.x = mapWidth;
			helpPoint.y = 0;
			/*var point1:Point3D = */IsoUtils.screenToIso(helpPoint,helpPos);
			helpPoint.x = Math.round(helpPos.x/cellWidth);
			helpPoint.y = Math.round(helpPos.z/cellWidth);
			//var point11:Point = new Point(Math.round(helpPos.x/cellWidth),Math.round(helpPos.z/cellWidth));
			countMaxAndMin(helpPoint);
			helpPoint.x = 0;
			helpPoint.y = mapHeight;
			/*var point2:Point3D = */IsoUtils.screenToIso(helpPoint,helpPos);
			helpPoint.x = Math.round(helpPos.x/cellWidth);
			helpPoint.y = Math.round(helpPos.z/cellWidth);
			//var point22:Point = new Point(Math.round(helpPos.x/cellWidth),Math.round(helpPos.z/cellWidth));
			countMaxAndMin(helpPoint);
			helpPoint.x = mapWidth;
			helpPoint.y = mapHeight;
			/*var point3:Point3D = */IsoUtils.screenToIso(helpPoint,helpPos);
			helpPoint.x = Math.round(helpPos.x/cellWidth);
			helpPoint.y = Math.round(helpPos.z/cellWidth);
			//var point33:Point = new Point(Math.round(helpPos.x/cellWidth),Math.round(helpPos.z/cellWidth));
			countMaxAndMin(helpPoint);
			
			//显示网格的
			/*for(var i:int = minX; i <= maxX; i++)
			{
			array[i] = new Array();
			for(var j:int = minY; j <= maxY; j++)
			{
			var tile:DrawnIsoTile = new DrawnIsoTile(cellWidth, 0xcccccc);
			tile.position = new Point3D(i * cellWidth, 0, j * cellWidth);
			map1.addChildToFloor(tile);
			array[i][j] = tile;
			}
			}*/
			
			
			player = Player.fromPool(cellWidth,monsterData[monsterId],skillData[monsterData[monsterId]["skillId"]]);
			player.dir = dir;
			//设置位置
			player.position.setValue(playerPoint.x * cellWidth,0,playerPoint.y* cellWidth);
			player.position = player.position;
			//这个点是PlayerProxy的帮助点，删除
			playerPoint = null;
			player.camp = GlobalData.campData[campId];
			player.speedData = data.clone(player.speedData);
			//这个data也是PlayerProxy的帮助类
			data = null;
			player.thingId = serverId;
			
			map1.addChildToWorld(player);
			if(!shadow){
				shadow = new DrawnIsoTile(cellWidth, 0x000000);
				shadow.alpha = .5;
			}
			map1.addChildToFloor(shadow);
			if(!filter){
				filter = new BlurFilter();
			}
			player.setShadow(shadow,filter);
			camera = player;
			//如果玩家死了，直接做死的效果
			if(isDead){
				player.dead(false);
			}
			
			/*for(var m:int = 0;m < 1 ; m++){
			for(var n:int = 0;n< 1;n++){
			var drawnIsoBox:DrawnIsoBox = new DrawnIsoBox(32,0x00ff00,32);
			drawnIsoBox.position = new Point3D((18+m) * cellWidth,0,(5+n)* cellWidth);
			map1.addChildToWorld(drawnIsoBox);
			}
			
			}*/
			
			if(!skillScope){
				skillScope = new SkillScope(cellWidth);
				//设置技能(第一次，发送技能)
				var skillArray:Array = monsterData[monsterId]["skill"];
				var helpObj1:Object = CommonPool.fromPoolObject();
				helpObj1.skillArray = skillArray;
				AppFacade.getInstance().sendNotification(NotiConst.SET_SKILL,helpObj1);
			}else{
				skillScope.reset(cellWidth);
			}
			//设置目标置后
			var helpObj:Object = CommonPool.fromPoolObject();
			helpObj.player = player;
			AppFacade.getInstance().sendNotification(NotiConst.SET_MYSELF,helpObj);
			
			map1.addChildToFloor(skillScope);
			if(!pitchOnFrame){
				pitchOnFrame = new PitchOnFrame(cellWidth);
			}else{
				pitchOnFrame.reset(cellWidth);
			}
			
			map1.addChildToFloor(pitchOnFrame);
			
			if(!grid){
				grid = new Grid(minX, maxX, minY, maxY);
			}else{
				grid.reset(minX, maxX, minY, maxY);
			}
			
			
			//mapContiner.addEventListener(MouseEvent.MOUSE_MOVE,onMouseMove);
			stage.addEventListener(MouseEvent.MOUSE_DOWN,onClick);
			stage.addEventListener(MouseEvent.RIGHT_MOUSE_DOWN,onRightDown1);
			//stage.removeEventListener(MouseEvent.RIGHT_MOUSE_DOWN,onRightDown1);
			
			//mapContiner.addEventListener(MouseEvent.MOUSE_UP,onMouseUp);
			//mapContiner.addEventListener(MouseEvent.CLICK,onClick);
			stage.addEventListener(KeyboardEvent.KEY_DOWN,onKeyDown);
			stage.addEventListener(KeyboardEvent.KEY_UP,onKeyUp);
			
			
			setChildIndex(uiSprite,numChildren-1);
			if(!star){
				star = new Stats();
				star.x = 600;
				star.visible = false;
			}
			addChild(star);
			JugglerManager.threeJuggler.add(this);
			//初始化调一次
			onEnterFrame(true);
			removeChild(loading);
		}
		
		public function addThing(thingId:int,point3d:Point3D,speedData:Data,dir:int,monsterId:int,dead:int,campId:int,enemy:int,isStory:Boolean = false):void{
			if(!isStory){
				if(map1.thingDict[thingId]){
					trace("已经有这个玩家了,无法添加");
					return;
				}
			}else{
				if(mapStory.thingDict[thingId]){
					trace("已经有这个玩家了,无法添加,剧情表填错了");
					return;
				}
			}
			var user:ActivityThing;
			if(thingId > 0){
				user = Player.fromPool(cellWidth,monsterData[monsterId],skillData[monsterData[monsterId]["skillId"]]);
			}else{
				user = Monster.fromPool(cellWidth,monsterData[monsterId],skillData[monsterData[monsterId]["skillId"]]);
			}
			user.position = point3d;
			if(campId != 0){
				user.camp = campData[campId];
			}
			user.isEnemy = enemy == 1 ? true : false;
			user.thingId = thingId;
			user.speedData = speedData.clone(user.speedData);
			user.dir = dir;
			user.isOther = true;
			user.isAutoAttack = false;
			if(isStory){
				mapStory.addChildToWorld(user);
			}else{
				map1.addChildToWorld(user);
			}
			if(dead == 1){
				user.dead(false);
			}
		}
		public function removeThing(thingId:int):void{
			//if(map1 == null){
			//	return;
			//}
			//var isFind:Boolean = false;
			var drawnIsoBox:ActivityThing = map1.thingDict[thingId];
			if(drawnIsoBox){
				drawnIsoBox.dispose();
			}else{
				trace("未找到这个玩家，无法移除");
			}
			/*for(var i:int = map1._playerObjects.length-1;i>=0;i--){
				
				var drawnIsoBox:ActivityThing = map1._playerObjects[i] as ActivityThing;
				if(drawnIsoBox.thingId == thingId){
					drawnIsoBox.dispose();
					isFind = true;
					break;
				}
				
			}*/
			/*if(!isFind){
				trace("未找到这个玩家，无法移除");
			}*/
		}
		public function removeFlyThing(thingId:int):void{
			
			var drawnIsoBox:FlySkillEffect = map1.thingDict[thingId];
			if(drawnIsoBox){
				drawnIsoBox.dispose();
			}else{
				trace("未找到这个飞行道具，无法移除");
			}
			
		}
		public function flyThingChangeTarget(thingId:int,targetId:int):void{
			
			var drawnIsoBox:FlySkillEffect = map1.thingDict[thingId];
			var target:ActivityThing = map1.thingDict[targetId];
			if(drawnIsoBox && target){
				drawnIsoBox.target = target;
			}else{
				trace("未找到飞行道具或转变的目标");
			}
			
		}
		public function speak(drawnIsoBox:ActivityThing,speakContent:String):void{
			addChild(speakUi);
			speakUi.setText(speakContent,drawnIsoBox.monster["npcHalf"]);
		}
		public function closeSpeak():void{
			if(speakUi.parent){
				removeChild(speakUi);
			}
		}
		public function thingMove(thingId:int,point3d:Point3D,path:Array,dir:int,nowNode:Node,isStory:Boolean = false):void{
			var drawnIsoBox:ActivityThing;
			if(isStory){
				drawnIsoBox = mapStory.thingDict[thingId];
			}else{
				drawnIsoBox = map1.thingDict[thingId];
			}
			if(drawnIsoBox){
				drawnIsoBox.position = point3d;
				drawnIsoBox.dir = dir;
				drawnIsoBox.path = path;
				drawnIsoBox.setNowNode(nowNode);
				
			}else{
				trace("未找到这个玩家，无法移动");
			}
		}
		public function attack(thingId:int,skillId:int,targetId:int,point:Point3D,dir:int,complete:int,flyThingId:int,isStory:Boolean = false):void{
			var drawnIsoBox:ActivityThing;
			if(isStory){
				drawnIsoBox = mapStory.thingDict[thingId];
			}else{
				drawnIsoBox = map1.thingDict[thingId];
			}
			var target:ActivityThing = null;
			if(targetId != 0){
				if(isStory){
					target = mapStory.thingDict[targetId];
				}else{
					target = map1.thingDict[targetId];
				}
			}
			
			if(drawnIsoBox && (target || targetId == 0)){
				if(int(skillId/10000) == 1){
					drawnIsoBox.attackServer(false,target,dir);
				}else{
					drawnIsoBox.userSkillServer(skillId,target,dir,point,complete,flyThingId,isStory);
				}
			}else{
				trace("未找到攻击者或者被攻击者，无法攻击");
			}
		}
		public function advanceTime(time:Number):void{
			onEnterFrame(false,time);
		}
		private function onEnterFrame(isInit:Boolean = false,time:Number = 0):void{
			//每5帧排一次序
			//if(nowFrame == sortFrame){
				map1.sort();
			//	nowFrame = 1;
			//}else{
			//	nowFrame++;
			//}
			
			
			if(player.target != null){
				pitchOnFrame.target = player.target;
				pitchOnFrame.position = player.target.position/*.clone()*/;//不需要克隆了
				if(player.isAttack){
					pitchOnFrame.attack();
				}else{
					pitchOnFrame.select();
				}
				
			}else{
				pitchOnFrame.clear();
			}
			//删除鼠标上人物的滤镜
			if(pointUser){
				if(pointUser.mc){
					pointUser.mc._bitmap.filters = null;
				}else{
					trace("pointUser.mc=null");
				}
			}
			pointUser = null;
			//如果面积技能存在的话，就不需要显示鼠标选择的目标了
			if(skill){
				
				//var pos1:Point3D;
				helpPoint.x = stage.mouseX-map1.x;
				helpPoint.y = stage.mouseY-map1.y;
				/*pos1 = */IsoUtils.screenToIso(/*new Point(stage.mouseX-map1.x, stage.mouseY-map1.y)*/helpPoint,helpPos1);
				//var point2:Point;
				/*point2 = */IsoUtils.isoToScreen(helpPos1,helpPoint);
				/*var positionClone:Point3D =*/this.player.position.clone(helpPos);
				helpPos.y = 0;
				/*var point1:Point = */IsoUtils.isoToScreen(helpPos,helpPoint1);
				
					
				
				var distance:Number = Point.distance(helpPoint1,helpPoint)/GlobalData.scale;
				if(distance > skill["attackDistance"]){
					skillScope.nowCanUse();
				}else{
					skillScope.canUse();
				}
				//这里有问题
				skillScope.position = helpPos1;
			}else{
				//删除所有滤镜，如果有人在鼠标上，设置鼠标上的人物
				for(var i:int = map1._playerObjects.length-1;i>=0;i--){
					if(map1._playerObjects[i] is ActivityThing){
						var drawnIsoBox:ActivityThing = map1._playerObjects[i] as ActivityThing;
						if(drawnIsoBox.mc._bitmap.filters.length != 0){
							drawnIsoBox.mc._bitmap.filters = null;
						}
						if(drawnIsoBox == player){
							//trace(i);
						}
						if(drawnIsoBox.isDead){
							continue;
						}
						if(pointUser == null){
							bmt.setPixel32(0, 0, 0x00FFFFFF);
							//helpMatrix.identity();
							helpMatrix.setTo(1,0,0,1,-drawnIsoBox.mouseX,-drawnIsoBox.mouseY);
							bmt.draw(drawnIsoBox.mc as DisplayObject, helpMatrix);
							if (bmt.getPixel32(0, 0) > 0x00FFFFFF){
								pointUser = drawnIsoBox;
								//加了break,好像没事儿，有事儿再把break;删了
								break;
							}
						}
					}
				}
				//设置鼠标上人物的滤镜
				if(pointUser){
					pointUser.mc._bitmap.filters = [UIComponent.grayFilter];
				}
			}
			if(isTrace){
				if(traceType == 1){
					moveD += time*speed*1000;
					if(moveD > traceDistance){
						moveD = traceDistance;
					}
					IsoUtils.isoToScreen(traceTarget as Point3D,helpPoint);
					IsoUtils.isoToScreen(start,helpPoint1)
					IsoObject.interpolate(helpPoint,helpPoint1,moveD*GlobalData.scale,helpPoint2);
					IsoUtils.screenToIso(helpPoint2,helpPos);
					
					if(upPoint3d 
						&& upPoint3d.x == helpPos.x 
						//&& upPoint3d.y == helpPos.y 
						&& upPoint3d.z == helpPos.z 
						&& upStageHeight == stage.stageHeight 
						&& upStageWidth == stage.stageWidth){
						mapBG.update1();
						if(moveD  == traceDistance){
							clearTrace();
						}else{
							
						}
						return;
					}
					var isupdate2:Boolean = false;
					upPoint3d = helpPos.clone(upPoint3d);
					upStageHeight = stage.stageHeight;
					upStageWidth = stage.stageWidth;
					
					helpPoint1.x = helpPoint2.x - stage.stageWidth/2.0;
					helpPoint1.y = helpPoint2.y - stage.stageHeight/2.0;
					var mapX2:Number = -helpPoint1.x;
					var mapY2:Number = -helpPoint1.y;
					if(mapX2 > 0){
						mapX2 = 0;
					}
					if(mapX2 < stage.stageWidth-mapWidth*GlobalData.scale){
						mapX2 = stage.stageWidth-mapWidth*GlobalData.scale;
					}
					if(mapY2 > 0){
						mapY2 = 0;
					}
					if(mapY2 < stage.stageHeight-mapHeight*GlobalData.scale){
						mapY2 = stage.stageHeight-mapHeight*GlobalData.scale;
					}
					if(map1.x != mapX2){
						map1.x = mapX2;
						mapStory.x = mapX2;
						mapBG.mapX = mapX2;
						mapBG.x = mapX2;
						isupdate2 = true;
					}
					if(map1.y != mapY2){
						map1.y = mapY2;
						mapStory.y = mapY2;
						mapBG.mapY = mapY2;
						mapBG.y = mapY2;
						isupdate2 = true;
					}
					if(mapBG.stageWidth != stage.stageWidth || mapBG.stageHeight != stage.stageHeight){
						mapBG.stageWidth = stage.stageWidth;
						mapBG.stageHeight = stage.stageHeight;
						isupdate2 = true;
					}
					if(isupdate2 || isInit){
						
						mapBG.update(isInit);
					}else{
						mapBG.update1();
					}
					if(moveD  == traceDistance){
						clearTrace();
					}else{
						
					}
				}else if(traceType = 2){
					moveD = time*speed*1000;
					start.setValue(upPoint3d.x,0,upPoint3d.z);
					(traceTarget as ActivityThing).position.clone(helpPos);
					helpPos.y = 0;
					IsoUtils.isoToScreen(helpPos,helpPoint);
					//trace(helpPoint.x+","+helpPoint.y);
					IsoUtils.isoToScreen(start,helpPoint1);
					var distance1:Number = Point.distance(helpPoint1,helpPoint)/GlobalData.scale;
					if(moveD > distance1){
						moveD = distance1;
					}
					IsoObject.interpolate(helpPoint,helpPoint1,moveD*GlobalData.scale,helpPoint2);
					IsoUtils.screenToIso(helpPoint2,helpPos);
					
					
					if(upPoint3d 
						&& upPoint3d.x == helpPos.x 
						//&& upPoint3d.y == helpPos.y 
						&& upPoint3d.z == helpPos.z 
						&& upStageHeight == stage.stageHeight 
						&& upStageWidth == stage.stageWidth){
						mapBG.update1();
						if(moveD  == distance1){
							clearTrace();
						}else{
							
						}
						return;
					}
					var isupdate1:Boolean = false;
					upPoint3d = helpPos.clone(upPoint3d);
					upStageHeight = stage.stageHeight;
					upStageWidth = stage.stageWidth;
					
					helpPoint1.x = helpPoint2.x - stage.stageWidth/2.0;
					helpPoint1.y = helpPoint2.y - stage.stageHeight/2.0;
					var mapX1:Number = -helpPoint1.x;
					var mapY1:Number = -helpPoint1.y;
					if(mapX1 > 0){
						mapX1 = 0;
					}
					if(mapX1 < stage.stageWidth-mapWidth*GlobalData.scale){
						mapX1 = stage.stageWidth-mapWidth*GlobalData.scale;
					}
					if(mapY1 > 0){
						mapY1 = 0;
					}
					if(mapY1 < stage.stageHeight-mapHeight*GlobalData.scale){
						mapY1 = stage.stageHeight-mapHeight*GlobalData.scale;
					}
					if(map1.x != mapX1){
						map1.x = mapX1;
						mapStory.x = mapX1;
						mapBG.mapX = mapX1;
						mapBG.x = mapX1;
						isupdate1 = true;
					}
					if(map1.y != mapY1){
						map1.y = mapY1;
						mapStory.y = mapY1;
						mapBG.mapY = mapY1;
						mapBG.y = mapY1;
						isupdate1 = true;
					}
					if(mapBG.stageWidth != stage.stageWidth || mapBG.stageHeight != stage.stageHeight){
						mapBG.stageWidth = stage.stageWidth;
						mapBG.stageHeight = stage.stageHeight;
						isupdate1 = true;
					}
					if(isupdate1 || isInit){
						
						mapBG.update(isInit);
					}else{
						mapBG.update1();
					}
					if(moveD  == distance1){
						clearTrace();
					}else{
						
					}
				}
				return;
			}
			//地图移动
			if(camera){
					
					//如果照相机的位置没变，就不移动地图的位置了
					var point3d:Point3D = (camera as ActivityThing).position;
					//如果是无敌斩，视角不移动
					if((player.isUserSkill && player.skillData["type"] == Skill.OMNISLASH) || GlobalData.nowBeEffectOnSkillOmnislash > 0){
						point3d = upPoint3d;
					}
					if(inStory){
						point3d = upPoint3d;
					}
					if(upPoint3d 
						&& upPoint3d.x == point3d.x 
						//&& upPoint3d.y == point3d.y 
						&& upPoint3d.z == point3d.z 
						&& upStageHeight == stage.stageHeight 
						&& upStageWidth == stage.stageWidth){
						mapBG.update1();
						return;
					}
					var isupdate:Boolean = false;
					upPoint3d = point3d.clone(upPoint3d);
					upStageHeight = stage.stageHeight;
					upStageWidth = stage.stageWidth;
					helpPos.setValue(point3d.x,0,point3d.z);
					IsoUtils.isoToScreen(helpPos,helpPoint);
					helpPoint1.x = helpPoint.x - stage.stageWidth/2.0;
					helpPoint1.y = helpPoint.y - stage.stageHeight/2.0;
					var mapX:Number = -helpPoint1.x;
					var mapY:Number = -helpPoint1.y;
					if(mapX > 0){
						mapX = 0;
					}
					if(mapX < stage.stageWidth-mapWidth*GlobalData.scale){
						mapX = stage.stageWidth-mapWidth*GlobalData.scale;
					}
					if(mapY > 0){
						mapY = 0;
					}
					if(mapY < stage.stageHeight-mapHeight*GlobalData.scale){
						mapY = stage.stageHeight-mapHeight*GlobalData.scale;
					}
					if(map1.x != mapX){
						map1.x = mapX;
						mapStory.x = mapX;
						mapBG.mapX = mapX;
						mapBG.x = mapX;
						isupdate = true;
					}
					if(map1.y != mapY){
						map1.y = mapY;
						mapStory.y = mapY;
						mapBG.mapY = mapY;
						mapBG.y = mapY;
						isupdate = true;
					}
					if(mapBG.stageWidth != stage.stageWidth || mapBG.stageHeight != stage.stageHeight){
						mapBG.stageWidth = stage.stageWidth;
						mapBG.stageHeight = stage.stageHeight;
						isupdate = true;
					}
					if(isupdate || isInit){
						
						mapBG.update(isInit);
					}else{
						mapBG.update1();
					}
				
			}
		}
		public function changeScale():void{
			if(mapBG.parent == null){
				return;
			}
			mapBG.changeScale();
			//如果照相机的位置没变，就不移动地图的位置了
			var point3d:Point3D = (camera as ActivityThing).position;
			if((player.isUserSkill && player.skillData["type"] == Skill.OMNISLASH) || GlobalData.nowBeEffectOnSkillOmnislash > 0){
				point3d = upPoint3d;
			}
			if(inStory){
				point3d = upPoint3d;
			}
			upPoint3d = point3d.clone(upPoint3d);
			upStageHeight = stage.stageHeight;
			upStageWidth = stage.stageWidth;
			helpPos.setValue(point3d.x,0,point3d.z);
			IsoUtils.isoToScreen(helpPos,helpPoint);
			helpPoint1.x = helpPoint.x - stage.stageWidth/2.0;
			helpPoint1.y = helpPoint.y - stage.stageHeight/2.0;
			var mapX:Number = -helpPoint1.x;
			var mapY:Number = -helpPoint1.y;
			if(mapX > 0){
				mapX = 0;
			}
			if(mapX < stage.stageWidth-mapWidth*GlobalData.scale){
				mapX = stage.stageWidth-mapWidth*GlobalData.scale;
			}
			if(mapY > 0){
				mapY = 0;
			}
			if(mapY < stage.stageHeight-mapHeight*GlobalData.scale){
				mapY = stage.stageHeight-mapHeight*GlobalData.scale;
			}
			if(map1.x != mapX){
				map1.x = mapX;
				mapStory.x = mapX;
				mapBG.mapX = mapX;
				mapBG.x = mapX;
			}
			if(map1.y != mapY){
				map1.y = mapY;
				mapStory.y = mapY;
				mapBG.mapY = mapY;
				mapBG.y = mapY;
			}
			if(mapBG.stageWidth != stage.stageWidth || mapBG.stageHeight != stage.stageHeight){
				mapBG.stageWidth = stage.stageWidth;
				mapBG.stageHeight = stage.stageHeight;
			}
			mapBG.updateScale();
		}
		public function useSkill(skillId:int):void{
			if(player.isDead){
				return;
			}
			var skillData:Object = GlobalData.skillData[skillId];
			if(skillData == null){
				return;
			}
			if(skillData["point"] == 1){
				
				if(skillData["type"] == Skill.FLASH){
					if(player.isIceBox() || player.isCast()){
						return;
					}
					//helpPoint.x = stage.mouseX-map1.x;
					//helpPoint.y = stage.mouseY-map1.y;
					//IsoUtils.screenToIso(helpPoint,helpPos);
					//player.userSkillClient(skillId,null,helpPos);
					skill = skillData;
					skillScope.skillData = skillData;
				}else{
					if(player.isDizzy() || player.isIceBox() || player.isCast()){
						return;
					}
					//如果闪现时不能放大跳
					
					skill = skillData;
					skillScope.skillData = skillData;
				}
				
			}else{
				if(player.isDizzy() || player.isIceBox() || player.isCast()){
					return;
				}
				if(player.isCanNotMove() && skillData["type"] == Skill.RUSH){
					var helpObj5:Object = CommonPool.fromPoolObject();
					helpObj5.message = "定身时不能施放冲锋";
					AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj5);
					return;
				}
				//如果闪现时不能放大跳(但是可以放冲锋了，因为冲锋已经是服务器行为了)
				if((skillData["type"] == Skill.BIG_JUMP /*|| skillData["type"] == Skill.RUSH*/) && nowBeEffectOnSkill > 0){
					var helpObj4:Object = CommonPool.fromPoolObject();
					helpObj4.message = "闪现时，不能施放大跳或冲锋";
					AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj4);
					return;
				}
				//第一种状况是，无敌斩服务器放完了，第二种状况是，无敌斩客户端放完了。都不可以放技能，走路
				if(nowBeEffectOnSkillOmnislash > 0 || (player.isUserSkill && skillData["type"] == Skill.OMNISLASH)){
					var helpObj6:Object = CommonPool.fromPoolObject();
					helpObj6.message = "无敌斩时，不能施放任何技能";
					AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj6);
					return;
				}
				if(skillData["target"] == 1 && player.target){
					if(skillData["isEnemy"] == 1 && player.target.isEnemy){
						player.userSkillClient(skillId);
					}else if(skillData["isEnemy"] == 0/* && !player.target.isEnemy*/){
						player.userSkillClient(skillId);
					}else{
						var helpObj:Object = CommonPool.fromPoolObject();
						helpObj.message = "不能对那个目标施放这个技能";
						AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj);
					}
					return;
				}
				player.userSkillClient(skillId);
			}
		}
		private function onKeyDown(event:KeyboardEvent):void{
			if(inStory && event.keyCode != 32){
				return;
			}
			if(keyMap.keyIsSkill(event.keyCode)){
				if(event.target is TextField){
					return;
				}
				var skillId:int;
				if(configData["device"] == 1){
					skillId = uiSprite.skillUiPhone.useSkill(event.keyCode);
				}else{
					skillId = uiSprite.skillUi.useSkill(event.keyCode);
				}
				
				useSkill(skillId);
			}else{
				if(event.keyCode == Keyboard.NUMBER_1){
					if(event.target is TextField){
						return;
					}
					//ChangeScene.changeScene(10100);
				}else if(event.keyCode == Keyboard.NUMBER_2){
					if(event.target is TextField){
						return;
					}
					//ChangeScene.changeScene(10200);
				}else if(event.keyCode == Keyboard.NUMBER_3){
					if(event.target is TextField){
						return;
					}
					//ChangeScene.changeScene(10300);
				}else if(event.keyCode == Keyboard.NUMBER_4){
					if(event.target is TextField){
						return;
					}
					//ChangeScene.changeScene(10400);
				}else if(event.keyCode == Keyboard.ESCAPE){
					player.target = null;
					var helpObj:Object = CommonPool.fromPoolObject();
					helpObj.target = player.target;
					AppFacade.getInstance().sendNotification(NotiConst.SET_TARGET,helpObj);
				}else if(event.keyCode == 32){
					AppFacade.getInstance().sendNotification(NotiConst.CHANGE_SIZE);
				}
			}
		}
		public function onKeyUp(event:KeyboardEvent):void{
			
			
		}
		private function onRightDown(event:MouseEvent):void{
		}
		private function onRightDown1(event:MouseEvent):void{
			if(inStory){
				return;
			}
			var findUi:DisplayObject = App.ui.selectParent(event.target as DisplayObject,UISprite);
			if(findUi !=null && findUi is UISprite){
				return;
			}
			helpPoint.x = stage.mouseX-map1.x;
			helpPoint.y = stage.mouseY-map1.y;
			IsoUtils.screenToIso(helpPoint,helpPos);
			var nodeX:int = Math.round(helpPos.x / cellWidth);
			var nodeY:int = Math.round(helpPos.z / cellWidth);
			//说明这次点击是施放技能，不往下走
			if(skill != null){
				skill = null;
				skillScope.clear();
				return;
			}
			
			if(pointUser && pointUser != player){
				//眩晕，冰箱，死亡不能攻击
				if(player.isDizzy() || player.isIceBox() || player.isCast() || player.isDead){
					var helpObj6:Object = CommonPool.fromPoolObject();
					helpObj6.message = "当前状态无法攻击目标";
					AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj6);
					return;
				}
				if(pointUser.isEnemy){
					player.target = pointUser;
					var helpObj1:Object = CommonPool.fromPoolObject();
					helpObj1.target = player.target;
					AppFacade.getInstance().sendNotification(NotiConst.SET_TARGET,helpObj1);
					
					if(nowBeEffectOnSkillOmnislash > 0 || (player.isUserSkill && skillData["type"] == Skill.OMNISLASH)){
						var helpObj11:Object = CommonPool.fromPoolObject();
						helpObj11.message = "无敌斩时不能攻击";
						AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj11);
						return;
					}
					player.attackClient(false)
				}else{
					//死亡，冰箱，眩晕，可以选择目标
					player.target = pointUser;
					player.isAttack = false;
					var helpObj8:Object = CommonPool.fromPoolObject();
					helpObj8.target = player.target;
					AppFacade.getInstance().sendNotification(NotiConst.SET_TARGET,helpObj8);
					//var helpObj:Object = CommonPool.fromPoolObject();
					//helpObj.message = "不能攻击友方单位";
					//AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj);
				}
				return;
			}
			
			//眩晕，冰箱，死亡不能寻路
			if(player.isDizzy() || player.isIceBox() || player.isCast() || player.isDead){
				var helpObj7:Object = CommonPool.fromPoolObject();
				helpObj7.message = "当前状态不能走路";
				AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj7);
				addMouseDownEffect(helpPos,true);
				return;
			}
			if(player.isUserSkill || nowBeEffectOnSkill > 0 || nowBeEffectOnSkillDistance > 0 || nowBeEffectOnSkillOmnislash > 0 || (player.isUserSkill && skillData["type"] == Skill.OMNISLASH)){
				var helpObj2:Object = CommonPool.fromPoolObject();
				helpObj2.message = "大跳或被技能影响时，不能寻路";
				AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj2);
				addMouseDownEffect(helpPos,true);
				return;
			}
			
			
			
			if(grid.getNode(nodeX,nodeY) != null && !player.isCanNotMove()){
				
				grid.setStartNode(Math.round(player.position.x/ cellWidth),Math.round(player.position.z/ cellWidth));
				grid.setEndNode(nodeX,nodeY);
				//当前人物并没有走路，并且在同一个格时，直接返回
				if(!player.isWalk && grid.startNode.x == grid.endNode.x && grid.startNode.y == grid.endNode.y){
					trace(nodeX+","+nodeY+"111");
					addMouseDownEffect(helpPos,true);
					return;
				}
				
				if(astar.findPath(grid,cellWidth))
				{
					var proxy1:PlayerProxy = AppFacade.getInstance().retrieveProxy(PlayerProxy.NAME) as PlayerProxy;
					proxy1.move(player.position.x,player.position.y,player.position.z,player.mc.dir,astar.path);
					player.path = astar.path;
					//drawPath(astar.path);
					astar.clear();
					addMouseDownEffect(helpPos,false,true);
					//显示路径的
					/*for(var i:int = 0;i<astar.path.length;i++){
					var node:Node = astar.path[i];
					(array[node.x][node.y] as DrawnIsoTile).color = 0x00ff00;
					}*/
					
				}else{
					var helpObj9:Object = CommonPool.fromPoolObject();
					helpObj9.message = "不能走到那个位置";
					AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj9);
					addMouseDownEffect(helpPos,true);
				}
				
			}else{
				var helpObj10:Object = CommonPool.fromPoolObject();
				helpObj10.message = "当前状态不能走路";
				AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj10);
				addMouseDownEffect(helpPos,true);
			}
			
		}
		private function onClick(event:MouseEvent):void{
			if(speakUi.parent){
				storyManager.jump();
				return;
			}
			if(inStory){
				return;
			}
			var findUi:DisplayObject = App.ui.selectParent(event.target as DisplayObject,UISprite);
			if(findUi !=null && findUi is UISprite){
				return;
			}
			helpPoint.x = stage.mouseX-map1.x;
			helpPoint.y = stage.mouseY-map1.y;
			IsoUtils.screenToIso(helpPoint,helpPos);
			var nodeX:int = Math.round(helpPos.x / cellWidth);
			var nodeY:int = Math.round(helpPos.z / cellWidth);
			//说明这次点击是施放技能，不往下走
			if(skill != null){
				//眩晕，冰箱，死亡不能释放技能
				if(skill["type"] == Skill.FLASH){
					//眩晕时可以闪现
					if(player.isIceBox() || player.isCast() || player.isDead){
						var helpObj4:Object = CommonPool.fromPoolObject();
						helpObj4.message = "现在不能释放技能";
						AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj4);
						return;
					}
				}else{
					if(player.isDizzy() || player.isIceBox() || player.isCast() || player.isDead){
						var helpObj5:Object = CommonPool.fromPoolObject();
						helpObj5.message = "现在不能释放技能";
						AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj5);
						return;
					}
				}
				
				//闪现是不能大跳(但是可以放冲锋了，因为冲锋已经是服务器行为了)
				if((skill["type"] == Skill.BIG_JUMP/* || skill["type"] == Skill.RUSH*/) && nowBeEffectOnSkill > 0){
					var helpObj3:Object = CommonPool.fromPoolObject();
					helpObj3.message = "闪现时，不能施放大跳或冲锋";
					AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj3);
					return;
				}
				if(nowBeEffectOnSkillOmnislash > 0 || (player.isUserSkill && skillData["type"] == Skill.OMNISLASH)){
					var helpObj11:Object = CommonPool.fromPoolObject();
					helpObj11.message = "无敌斩时，不能施放技能";
					AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj11);
					return;
				}
				player.userSkillClient(skill["id"],null,helpPos);
				skill = null;
				skillScope.clear();
				addMouseDownEffect(helpPos);
				return;
			}
			
			if(configData["device"] == 1){
				
			
			/********************************这里也要算一遍****************************/
			
			//删除鼠标上人物的滤镜
			if(pointUser){
				if(pointUser.mc){
					pointUser.mc._bitmap.filters = null;
				}else{
					trace("pointUser.mc=null");
				}
			}else{
				AppFacade.getInstance().sendNotification(NotiConst.CLEAR_MESSAGE);
				var helpObj10:Object = CommonPool.fromPoolObject();
				helpObj10.dir = 0;
				helpObj10.message = "point user =null";
				AppFacade.getInstance().sendNotification(NotiConst.DEBUG_MESSAGE,helpObj10);
			}
			pointUser = null;
			//删除所有滤镜，如果有人在鼠标上，设置鼠标上的人物
			for(var i:int = map1._playerObjects.length-1;i>=0;i--){
				if(map1._playerObjects[i] is ActivityThing){
					var drawnIsoBox:ActivityThing = map1._playerObjects[i] as ActivityThing;
					if(drawnIsoBox.mc._bitmap.filters.length != 0){
						drawnIsoBox.mc._bitmap.filters = null;
					}
					if(drawnIsoBox == player){
						//trace(i);
					}
					if(drawnIsoBox.isDead){
						continue;
					}
					if(pointUser == null){
						bmt.setPixel32(0, 0, 0x00FFFFFF);
						//helpMatrix.identity();
						helpMatrix.setTo(1,0,0,1,-drawnIsoBox.mouseX,-drawnIsoBox.mouseY);
						bmt.draw(drawnIsoBox.mc as DisplayObject, helpMatrix);
						if (bmt.getPixel32(0, 0) > 0x00FFFFFF){
							pointUser = drawnIsoBox;
							//加了break,好像没事儿，有事儿再把break;删了
							break;
						}
					}
				}
			}
			//设置鼠标上人物的滤镜
			if(pointUser){
				pointUser.mc._bitmap.filters = [UIComponent.grayFilter];
			}
			
			/********************************这里也要算一遍****************************/
			}
			if(pointUser && pointUser != player){
				if(player.target == pointUser){
					//眩晕，冰箱，死亡不能攻击
					if(configData["device"] == 1){
					
						if(player.isDizzy() || player.isIceBox() || player.isCast() || player.isDead){
							var helpObj6:Object = CommonPool.fromPoolObject();
							helpObj6.message = "当前状态无法攻击目标";
							AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj6);
							return;
						}
						if(nowBeEffectOnSkillOmnislash > 0 || (player.isUserSkill && skillData["type"] == Skill.OMNISLASH)){
							var helpObj12:Object = CommonPool.fromPoolObject();
							helpObj12.message = "无敌斩时，不能攻击";
							AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj12);
							return;
						}
						if(pointUser.isEnemy && player.isAttack == false){
							player.attackClient(false)
						}else{
							if(!pointUser.isEnemy){
								var helpObj:Object = CommonPool.fromPoolObject();
								helpObj.message = "不能攻击友方单位";
								AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj);
							}
						}
					
					}
				}else{
					//死亡，冰箱，眩晕，可以选择目标
					player.target = pointUser;
					player.isAttack = false;
					var helpObj1:Object = CommonPool.fromPoolObject();
					helpObj1.target = player.target;
					AppFacade.getInstance().sendNotification(NotiConst.SET_TARGET,helpObj1);
				}
				return;
			}
			if(configData["device"] == 0){
				return;
			}
			//眩晕，冰箱，死亡不能寻路
			if(player.isDizzy() || player.isIceBox() || player.isCast() || player.isDead){
				var helpObj7:Object = CommonPool.fromPoolObject();
				helpObj7.message = "当前状态不能走路";
				AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj7);
				addMouseDownEffect(helpPos,true);
				return;
			}
			if(player.isUserSkill || nowBeEffectOnSkill > 0 || nowBeEffectOnSkillDistance > 0){
				var helpObj2:Object = CommonPool.fromPoolObject();
				helpObj2.message = "大跳或被技能影响时，不能寻路";
				AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj2);
				addMouseDownEffect(helpPos,true);
				return;
			}
			if(nowBeEffectOnSkillOmnislash > 0 || (player.isUserSkill && skillData["type"] == Skill.OMNISLASH)){
				var helpObj13:Object = CommonPool.fromPoolObject();
				helpObj13.message = "无敌斩时，不能走路";
				AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj13);
				return;
			}
			
			
			if(grid.getNode(nodeX,nodeY) != null && !player.isCanNotMove()){
				
					grid.setStartNode(Math.round(player.position.x/ cellWidth),Math.round(player.position.z/ cellWidth));
					grid.setEndNode(nodeX,nodeY);
					//当前人物并没有走路，并且在同一个格时，直接返回
					if(!player.isWalk && grid.startNode.x == grid.endNode.x && grid.startNode.y == grid.endNode.y){
						trace(nodeX+","+nodeY+"111");
						addMouseDownEffect(helpPos,true);
						return;
					}
					
					if(astar.findPath(grid,cellWidth))
					{
						var proxy1:PlayerProxy = AppFacade.getInstance().retrieveProxy(PlayerProxy.NAME) as PlayerProxy;
						proxy1.move(player.position.x,player.position.y,player.position.z,player.mc.dir,astar.path);
						player.path = astar.path;
						//drawPath(astar.path);
						astar.clear();
						addMouseDownEffect(helpPos,false,true);
						//显示路径的
						/*for(var i:int = 0;i<astar.path.length;i++){
							var node:Node = astar.path[i];
							(array[node.x][node.y] as DrawnIsoTile).color = 0x00ff00;
						}*/
						
					}else{
						var helpObj9:Object = CommonPool.fromPoolObject();
						helpObj9.message = "不能走到那个位置";
						AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj9);
						addMouseDownEffect(helpPos,true);
					}
				
			}else{
				var helpObj8:Object = CommonPool.fromPoolObject();
				helpObj8.message = "当前状态不能走路";
				AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj8);
				addMouseDownEffect(helpPos,true);
			}
		}
		private function addMouseDownEffect(pos:Point3D,isAddFilter:Boolean = false,isTarget:Boolean = false):void{
			var skillMc1:Animation = Animation.fromPool(AnCategory.EFFECT,"810002310_zs_buff_zengyi",AnConst.STAND,true,0.5,AnConst.DOWN);
			var skillEffect:SkillEffect = SkillEffect.fromPool(cellWidth);
			skillEffect.position = pos;
			skillEffect.addChild(skillMc1);
			if(!isTarget){
				skillEffect.addEvent();
			}else{
				if(nowTargetEffect){
					nowTargetEffect.addEvent();
				}
				nowTargetEffect = skillEffect;
			}
			
			if(isAddFilter){
				skillMc1._bitmap.filters = [UIComponent.grayFilter];
			}
			map1.addChildToWorld(skillEffect);
		}
		private function isLine (start : Node , end : Node) : Boolean
		{
			var startScene : Point = IsoUtils.isoToScreen( new Point3D( start.x,0,start.y) );
			var endScene : Point   = IsoUtils.isoToScreen( new Point3D( end.x,0,end.y) );
			var pScene : Point = endScene.subtract(startScene);
			var pLen : int = pScene.length;
			var tempP : Point3D;
			var tempP1 : Point = startScene.clone();
			
			while ( pLen > 0 )
			{
				pLen--;
				pScene.normalize(pLen);
				tempP1.x = startScene.x + pScene.x;
				tempP1.y = startScene.y + pScene.y;
				tempP = IsoUtils.screenToIso( tempP1 );
				if ( !grid.getNode(tempP.x, tempP.z).walkable )
					return false;
			}
			
			return true;
		}
		
		private function drawPath(path:Array):void{
			map1._path.graphics.clear();
			//map1._path.graphics.beginFill(0x000000);
			map1._path.graphics.lineStyle(0, 0, 1.0);
			for(var i:int = 0;i<path.length;i++){
				var node:Node = path[i] as Node;
				helpPos.setValue(node.x*cellWidth,0,node.y*cellWidth);
				IsoUtils.isoToScreen(helpPos,helpPoint);
				if(i == 0){
					map1._path.graphics.moveTo(helpPoint.x, helpPoint.y);
				}else{
					map1._path.graphics.lineTo(helpPoint.x, helpPoint.y);
				}
			}
			
			
			
		}
		private function onMouseMove(event:MouseEvent):void{
			
			/*if(isDown){
				map1.x = map1.x+(stage.mouseX - mouseDownX);
				map1.y = map1.y+(stage.mouseY - mouseDownY);
				mouseDownX = stage.mouseX;
				mouseDownY = stage.mouseY;
			}*/
			//定位鼠标在哪个格子的
			/*var pos:Point3D = IsoUtils.screenToIso(new Point(stage.mouseX-map.x, stage.mouseY-map.y));
			pos.x = Math.round(pos.x / cellWidth);
			pos.y = Math.round(pos.y / cellWidth);
			pos.z = Math.round(pos.z / cellWidth);
			if(array[pos.x]){
				if(array[pos.x][pos.z]){
					(array[pos.x][pos.z] as DrawnIsoTile).color = 0x00ff00;
				}
			}*/
			
			
		}
		private function onMouseDown(event:MouseEvent):void{
			isDown = true;
			mouseDownX = stage.mouseX;
			mouseDownY = stage.mouseY;
			trace("mouse down");
		}
		private function onMouseUp(event:MouseEvent):void{
			isDown = false;
		}
	}
}