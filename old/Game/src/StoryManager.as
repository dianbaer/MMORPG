package
{
	import UI.App;
	import UI.abstract.resources.AnCategory;
	import UI.abstract.resources.AnConst;
	import UI.abstract.resources.ResourceUtil;
	import UI.abstract.resources.item.JsonResource;
	import UI.abstract.resources.loader.MultiLoader;
	import UI.abstract.utils.CommonPool;
	
	import _45degrees.com.friendsofed.isometric.ActivityThing;
	import _45degrees.com.friendsofed.isometric.Data;
	import _45degrees.com.friendsofed.isometric.Point3D;
	
	import _astar.Node;
	
	import gui.animation.IAnimatable;
	import gui.animation.JugglerManager;
	

	public class StoryManager implements IAnimatable
	{
		//npc出现
		public static const NPC_APPEAR:int = 1;
		//npc走路
		public static const NPC_WALK:int = 2;
		//npc使用技能
		public static const NPC_USESKILL:int = 3;
		//npc获得buff
		public static const NPC_ADDBUFF:int = 4;
		//npc移除buff
		public static const NPC_DELBUFF:int = 5;
		//闪现到目的地
		public static const NPC_FLASHTO:int = 6;
		//npc说话
		public static const NPC_SPEAK:int = 7;
		//npc说话关闭
		public static const NPC_CLOSESPEAK:int = 8;
		//追踪目标
		public static const NPC_TRACE:int = 9;
		//设置相机
		public static const NPC_SET_CAMERA:int = 10;
		//设置相机
		public static const NPC_CLEAR_CAMERA:int = 11;
		//追踪点
		public static const NPC_TRACE_POINT:int = 12;
		private static var story:Object;
		private static var nextTime:Number;
		private static var step:int = 1;
		private static var storyId:int = 0;
		
		private static var helpPos:Point3D = new Point3D();
		private static var data1:Data = new Data();
		private static var helpArray:Array = new Array();
		public function StoryManager()
		{
			
		}
		public function enterStory(storyId:int):void{
			StoryManager.storyId = storyId;
			story = null;
			//加载剧情资源
			var loadArray:Array = new Array();
			loadArray[0] = "story/"+storyId+".json";
			var helpObj1:Object = CommonPool.fromPoolObject();
			helpObj1.onField = onField;
			//第一种可能加载完成肯定story不为空
			//第二种可能是没加载完成
			var multiLoader:MultiLoader = App.loader.loadList(loadArray,startStory,helpObj1,false);
			GlobalData.loading.setProcess(multiLoader,"加载剧情配置");
			GlobalData.game.addLoading();
			multiLoader.load();
		}
		public function onField():void{
			endStory();
		}
		public function startStory():void{
			
			var story:Object = (App.loader.getResource("story/"+storyId+".json") as JsonResource).object;
			
			
			var loadArray:Array = new Array();
			var url:String;
			var skill:Object;
			var buff:Object;
			var monster:Object;
			var storyStep:Object;
			for(var key:String in story){
				storyStep = story[key];
				switch(storyStep["type"]){
					case NPC_APPEAR:
						monster = GlobalData.monsterData[storyStep["monsterId"]];
						//加载人物资源
						url = ResourceUtil.getAnimationURL(AnCategory.USER,monster["src"],AnConst.STAND);
						if(loadArray.indexOf(url) == -1){
							loadArray[loadArray.length] = url;
						}
						url = ResourceUtil.getAnimationURL(AnCategory.USER,monster["src"],AnConst.WALK);
						if(loadArray.indexOf(url) == -1){
							loadArray[loadArray.length] = url;
						}
						url = ResourceUtil.getAnimationURL(AnCategory.USER,monster["src"],AnConst.ATTACK);
						if(loadArray.indexOf(url) == -1){
							loadArray[loadArray.length] = url;
						}
						url = ResourceUtil.getAnimationURL(AnCategory.USER,monster["src"],AnConst.ATTACK2);
						if(loadArray.indexOf(url) == -1){
							loadArray[loadArray.length] = url;
						}
						url = ResourceUtil.getAnimationURL(AnCategory.USER,monster["srcWeapon"],AnConst.STAND);
						if(loadArray.indexOf(url) == -1){
							loadArray[loadArray.length] = url;
						}
						url = ResourceUtil.getAnimationURL(AnCategory.USER,monster["srcWeapon"],AnConst.WALK);
						if(loadArray.indexOf(url) == -1){
							loadArray[loadArray.length] = url;
						}
						url = ResourceUtil.getAnimationURL(AnCategory.USER,monster["srcWeapon"],AnConst.ATTACK);
						if(loadArray.indexOf(url) == -1){
							loadArray[loadArray.length] = url;
						}
						url = ResourceUtil.getAnimationURL(AnCategory.USER,monster["srcWeapon"],AnConst.ATTACK2);
						if(loadArray.indexOf(url) == -1){
							loadArray[loadArray.length] = url;
						}
						//加载半身像
						if(monster["npcHalf"] != 0){
							url = "npcHalf/"+monster["npcHalf"]+".png";
							if(loadArray.indexOf(url) == -1){
								loadArray[loadArray.length] = url;
							}
						}
						break;
					case NPC_USESKILL:
						skill = GlobalData.skillData[storyStep["skillId"]];
						if(skill["src"] != ""){
							url = ResourceUtil.getAnimationURL(AnCategory.EFFECT,skill["src"]);
							if(loadArray.indexOf(url) == -1){
								loadArray[loadArray.length] = url;
							}
						}
						if(skill["hitEffect"] != ""){
							url = ResourceUtil.getAnimationURL(AnCategory.EFFECT,skill["hitEffect"]);
							if(loadArray.indexOf(url) == -1){
								loadArray[loadArray.length] = url;
							}
						}
						if(skill["readEffect"] != ""){
							url = ResourceUtil.getAnimationURL(AnCategory.EFFECT,skill["readEffect"]);
							if(loadArray.indexOf(url) == -1){
								loadArray[loadArray.length] = url;
							}
						}
						if(skill["bombEffect"] != ""){
							url = ResourceUtil.getAnimationURL(AnCategory.EFFECT,skill["bombEffect"]);
							if(loadArray.indexOf(url) == -1){
								loadArray[loadArray.length] = url;
							}
						}
						break;
					case NPC_ADDBUFF:
						buff = GlobalData.buffData[storyStep["buffId"]];
						if(buff["src"] != ""){
							url = ResourceUtil.getAnimationURL(AnCategory.EFFECT,buff["src"]);
							if(loadArray.indexOf(url) == -1){
								loadArray[loadArray.length] = url;
							}
						}
						break;
				}
			}
			var helpObj1:Object = CommonPool.fromPoolObject();
			helpObj1.onField = onField;
			var multiLoader:MultiLoader = App.loader.loadList(loadArray,startStoryTwo,helpObj1,false);
			GlobalData.loading.setProcess(multiLoader,"加载剧情资源");
			multiLoader.load();
		}
		public function startStoryTwo():void{
			
			story = (App.loader.getResource("story/"+storyId+".json") as JsonResource).object;
			GlobalData.game.removeLoading();
			step = 1;
			nextTime = 0;
			storyStep();
			JugglerManager.fourJuggler.add(this);
		}
		//跳过
		public function jump():void{
			//没有上一步
			var oldStep:int = step-1;
			if(oldStep <= 0 ){
				return;
			}
			//上一步不存在，或者上一步类型不是说话
			var obj:Object = story[oldStep];
			if(!obj || obj["type"] != NPC_SPEAK){
				return;
			}
			//上一步类型是说话了
			nextTime = 0;
			advanceTime(0);
		}
		public function storyStep():void{
			var obj:Object = story[step];
			if(obj){
				switch(obj["type"]){
					case NPC_APPEAR:
						var monster:Object = GlobalData.monsterData[obj["monsterId"]];
						data1.moveV = monster["moveV"];
						data1.jumpVerticalV = monster["jumpVerticalV"];
						data1.jumpVerticalA = monster["jumpVerticalA"];
						data1.attackSpeed = monster["attackSpeed"];
						data1.hp = monster["hp"];
						data1.maxHp = monster["maxHp"];
						data1.att = monster["att"];
						data1.def = monster["def"];
						helpPos.setValue(obj["x"] * GlobalData.cellWidth,0,obj["y"]* GlobalData.cellWidth);
						GlobalData.game.addThing(obj["thingId"],helpPos,data1,obj["dir"],obj["monsterId"],0,obj["camp"],obj["enemy"],true);
						break;
					case NPC_WALK:
						var path:Array = CommonPool.fromPoolArray();
						var array:Array = obj["path"];
						for(var i:int = 0;i < array.length;i++){
							var str:String = array[i];
							var array1:Array = str.split("_");
							var node:Node = GlobalData.grid.getNode(array1[0],array1[1]);
							path[path.length] = node;
						}
						helpPos.setValue(path[0].x * GlobalData.cellWidth,0,path[0].y* GlobalData.cellWidth);
						GlobalData.game.thingMove(obj["thingId"],helpPos,path,5,null,true)
						break;
					case NPC_USESKILL:
						helpPos.setValue(obj["x"] * GlobalData.cellWidth,0,obj["y"]* GlobalData.cellWidth);
						GlobalData.game.attack(obj["thingId"],obj["skillId"],obj["targetId"],helpPos,0,0,0,true);
						break;
					case NPC_ADDBUFF:
						if(!helpArray[0]){
							helpArray[0] = new Object();
						}
						helpArray[0]["id"] = obj["buffId"];
						helpArray[0]["attackId"] = obj["attackId"];
						helpArray[0]["duration"] = obj["duration"];
						GlobalData.game.addBuff(obj["thingId"],helpArray,1,true);
						break;
					case NPC_DELBUFF:
						if(!helpArray[0]){
							helpArray[0] = new Object();
						}
						helpArray[0]["id"] = obj["buffId"];
						helpArray[0]["attackId"] = obj["attackId"];
						GlobalData.game.delBuff(obj["thingId"],helpArray,1,true);
						break;
					case NPC_FLASHTO:
						helpPos.setValue(obj["x"] * GlobalData.cellWidth,0,obj["y"]* GlobalData.cellWidth);
						GlobalData.game.flashTo(obj["thingId"],helpPos,5,obj["skillId"],0,true);
						break;
					case NPC_SPEAK:
						var drawnIsoBox:ActivityThing = GlobalData.mapStory.thingDict[obj["thingId"]];
						GlobalData.game.speak(drawnIsoBox,obj["speak"]);
						break;
					case NPC_CLOSESPEAK:
						GlobalData.game.closeSpeak();
						break;
					case NPC_TRACE:
						var drawnIsoBox1:ActivityThing = GlobalData.mapStory.thingDict[obj["thingId"]];
						GlobalData.game.startTrace(2,drawnIsoBox1,obj["traceTime"]);
						break;
					case NPC_SET_CAMERA:
						var drawnIsoBox2:ActivityThing = GlobalData.mapStory.thingDict[obj["thingId"]];
						GlobalData.game.setCamera(drawnIsoBox);
						break;
					case NPC_CLEAR_CAMERA:
						GlobalData.game.clearCamera();
						break;
					case NPC_TRACE_POINT:
						helpPos.setValue(obj["x"] * GlobalData.cellWidth,0,obj["y"]* GlobalData.cellWidth);
						GlobalData.game.startTrace(1,helpPos,obj["traceTime"]);
						break;
				}
				nextTime = obj["time"];
				step++;
				if(nextTime == 0){
					storyStep();
				}
			}else{
				endStory();
			}
		}
		public function advanceTime(time:Number):void{
			nextTime = nextTime-time*1000;
			if(nextTime <= 0){
				storyStep();
			}
		}
		
		public function endStory(isDispose:Boolean = false):void{
			if(story == null && StoryManager.storyId != 0){
				App.loader.canelLoadList(startStory);
				App.loader.canelLoadList(startStoryTwo);
				GlobalData.game.removeLoading();
			}
			StoryManager.storyId = 0;
			story = null;
			JugglerManager.fourJuggler.remove(this);
			GlobalData.game.endStory(isDispose);
		}
		
	}
}