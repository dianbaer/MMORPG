package
{
	import UI.App;
	import UI.abstract.resources.AnCategory;
	import UI.abstract.resources.AnConst;
	import UI.abstract.resources.ResourceUtil;
	import UI.abstract.resources.item.JsonResource;
	import UI.abstract.resources.loader.MultiLoader;
	import UI.abstract.utils.CommonPool;
	
	import proxy.PlayerProxy;

	public class ChangeScene
	{
		public static var nowLoadScene:int = 0;
		public function ChangeScene()
		{
		}
		public static function changeScene(sceneId:int):void{
			var proxy1:PlayerProxy = AppFacade.getInstance().retrieveProxy(PlayerProxy.NAME) as PlayerProxy;
			proxy1.changeScene(sceneId);
			//发过来的场景不对
			/*
			if(sceneId <= 0){
				return;
			}
			//当前正在加载别的场景
			if(nowLoadScene > 0){
				var helpObj:Object = CommonPool.fromPoolObject();
				helpObj.message = "正在进入场景";
				AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj);
				return;
			}
			nowLoadScene = sceneId;
			var helpObj1:Object = CommonPool.fromPoolObject();
			helpObj1.onField = onField;
			App.loader.load("map/"+sceneId+".json",loadComplete,helpObj1);
			*/
		}
		public static function loadingScene(sceneId:int,monsterId:int):void{
			nowLoadScene = sceneId;
			var monster:Object = GlobalData.monsterData[monsterId];
			var skillArray:Array = monster["skill"];
			var loadArray:Array = new Array();
			var url:String;
			var skill:Object;
			var buff:Object;
			for(var i:int = 0;i<skillArray.length;i++){
				skill = GlobalData.skillData[skillArray[i]];
				if(skill == null){
					continue;
				}
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
				if(skill["icon"] != ""){
					url = "skill/"+skill["icon"];
					if(loadArray.indexOf(url) == -1){
						loadArray[loadArray.length] = url;
					}
				}
				if(skill["buffId"] != 0){
					buff = GlobalData.buffData[skill["buffId"]];
					if(buff["src"] != ""){
						url = ResourceUtil.getAnimationURL(AnCategory.EFFECT,buff["src"]);
						if(loadArray.indexOf(url) == -1){
							loadArray[loadArray.length] = url;
						}
					}
				}
			}
			loadArray[loadArray.length] = "map/"+sceneId+".json";
			loadArray[loadArray.length] = ResourceUtil.getAnimationURL(AnCategory.USER,monster["src"],AnConst.STAND);
			loadArray[loadArray.length] = ResourceUtil.getAnimationURL(AnCategory.USER,monster["src"],AnConst.WALK);
			loadArray[loadArray.length] = ResourceUtil.getAnimationURL(AnCategory.USER,monster["src"],AnConst.ATTACK);
			loadArray[loadArray.length] = ResourceUtil.getAnimationURL(AnCategory.USER,monster["src"],AnConst.ATTACK2);
			loadArray[loadArray.length] = ResourceUtil.getAnimationURL(AnCategory.USER,monster["srcWeapon"],AnConst.STAND);
			loadArray[loadArray.length] = ResourceUtil.getAnimationURL(AnCategory.USER,monster["srcWeapon"],AnConst.WALK);
			loadArray[loadArray.length] = ResourceUtil.getAnimationURL(AnCategory.USER,monster["srcWeapon"],AnConst.ATTACK);
			loadArray[loadArray.length] = ResourceUtil.getAnimationURL(AnCategory.USER,monster["srcWeapon"],AnConst.ATTACK2);
			var helpObj1:Object = CommonPool.fromPoolObject();
			helpObj1.onField = onField;
			var multiLoader:MultiLoader = App.loader.loadList(loadArray,loadComplete,helpObj1,false);
			GlobalData.loading.setProcess(multiLoader,"加载人物和技能资源");
			multiLoader.load();
		}
		public static function loadComplete():void{
			var resource:JsonResource = App.loader.getResource("map/"+nowLoadScene+".json") as JsonResource;
			var mapData:Object = resource.object;
			var wSize:int = Math.ceil(mapData["mapWidth"]/mapData["cutMapSize"]);
			var hSize:int = Math.ceil(mapData["mapHeight"]/mapData["cutMapSize"]);
			var loadArray:Array = new Array();
			for(var i:int = 0;i<hSize;i++){
				for(var j:int = 0;j<wSize;j++){
					var str:String = "map/s"+mapData["id"]+"/s"+mapData["id"]+"_"+i+"_"+j+".jpg";
					loadArray[loadArray.length] = str;
				}
			}
			var helpObj1:Object = CommonPool.fromPoolObject();
			helpObj1.onField = onField;
			var multiLoader:MultiLoader = App.loader.loadList(loadArray,loadComplete1,helpObj1,false);
			GlobalData.loading.setProcess(multiLoader,"加载地图资源");
			multiLoader.load();
			
		}
		public static function loadComplete1():void{
			var proxy1:PlayerProxy = AppFacade.getInstance().retrieveProxy(PlayerProxy.NAME) as PlayerProxy;
			proxy1.loadingOk();
		}
		public static function onField():void{
			
		}
		//public static function loadComplete(jsonResource:JsonResource):void{
		//	var proxy1:PlayerProxy = AppFacade.getInstance().retrieveProxy(PlayerProxy.NAME) as PlayerProxy;
		//	proxy1.changeScene(nowLoadScene);
		//	nowLoadScene = 0;
		//}
		//public static function onField():void{
		//	nowLoadScene = 0;
		//	var helpObj:Object = CommonPool.fromPoolObject();
		//	helpObj.message = "没有此场景";
		//	AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj);
		//}
	}
}