package
{
	import UI.App;
	import UI.abstract.component.control.dropDownMenu.MenuData;
	import UI.abstract.component.data.DataProvider;
	import UI.abstract.component.event.MenuBarEvent;
	import UI.abstract.resources.ResourceManager;
	import UI.abstract.resources.item.JsonResource;
	import UI.abstract.tween.TweenManager;
	import UI.theme.defaulttheme.button.Button;
	import UI.theme.defaulttheme.menuBar.MenuBar;
	import UI.theme.defaulttheme.text.Label;
	import UI.theme.defaulttheme.text.TextInput;
	import UI.theme.defaulttheme.window.Window;
	
	import _45degrees.com.friendsofed.isometric.DrawnIsoTile;
	import _45degrees.com.friendsofed.isometric.IsoUtils;
	import _45degrees.com.friendsofed.isometric.IsoWorld;
	import _45degrees.com.friendsofed.isometric.Point3D;
	
	import _astar.Grid;
	import _astar.Node;
	
	import flash.display.Sprite;
	import flash.display.StageAlign;
	import flash.display.StageScaleMode;
	import flash.events.Event;
	import flash.events.IOErrorEvent;
	import flash.events.KeyboardEvent;
	import flash.events.MouseEvent;
	import flash.filesystem.File;
	import flash.filesystem.FileMode;
	import flash.filesystem.FileStream;
	import flash.geom.Point;
	import flash.ui.Keyboard;
	import flash.utils.ByteArray;

	[SWF(height="768",width="1024",frameRate="60")]
	public class MapEditor extends Sprite
	{
		
		
		//属性
		private var mapWidth:int = 3000;
		private var mapHeight:int = 3000;
		private var cellWidth:int = 32;
		private var id:int = 30000;
		private var threadId:int = 1;
		private var type:int = 1;
		private var cutMapSize:int = 300;
		
		
		public static var map1:IsoWorld;
		private var mouseDownX:Number;
		private var mouseDownY:Number;
		private var isDown:Boolean;
		private var minX:int = 0;
		private var minY:int = 0;
		private var maxX:int = 0;
		private var maxY:int = 0;
		private var grid:Grid;
		private var array:Array;
		
		
		private var oneColor:uint = 0x00ff00;
		private var twoColor:uint = 0xff0000;
		private var nowColor:uint = oneColor;
		
		//数据
		public static var sceneData:Object;
		public static var monsterData:Object;
		private var ui:UISprite1;
		public function MapEditor()
		{
			stage.align = StageAlign.TOP_LEFT;
			stage.scaleMode = StageScaleMode.NO_SCALE;
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
			ResourceManager.resourcePrefixURL = "C:/Users/xuepeng/Desktop/asset/";
			TweenManager.initClass();
			
			App.loader.loadList(["scene.json","monster.json"],loadJsonComplete);
			
		}
		private function loadJsonComplete():void{
			sceneData = (App.loader.getResource("scene.json") as JsonResource).object;
			monsterData = (App.loader.getResource("monster.json") as JsonResource).object;
			
			ui = new UISprite1();
			addChild(ui);
		}
		public function outScene():void{
			
			if(map1 && grid){
				map1.dispose();
				
				map1 = null;
				array.length = 0;
				array = null;
				grid.clear();
				grid = null;
				
				removeEventListener(MouseEvent.MOUSE_MOVE,onMouseMove);
				removeEventListener(MouseEvent.MOUSE_DOWN,onMouseDown);
				removeEventListener(MouseEvent.MOUSE_UP,onMouseUp);
				removeEventListener(MouseEvent.DOUBLE_CLICK,onClick);
				stage.removeEventListener(KeyboardEvent.KEY_DOWN,onKeyDown);
				stage.removeEventListener(KeyboardEvent.KEY_UP,onKeyUp);
			}
			
			
			
			
			
			
			
		}
		public function createMap(sceneId:int,mapWidth:int,mapHeight:int,cellWidth:int,cutMapSize:int,threadId:int,type:int):void{
			outScene();
			this.id = sceneId;
			this.mapWidth = mapWidth;
			this.mapHeight = mapHeight;
			this.cellWidth = cellWidth;
			this.cutMapSize = cutMapSize;
			this.threadId = threadId;
			this.type = type;
			enterGame();
		}
		public function enterGame():void{
			map1 = new IsoWorld();
			
			map1.graphics.beginFill(0x00ff00,0.1);
			map1.graphics.drawRect(0,0,mapWidth,mapHeight);
			map1.graphics.endFill();
			map1.doubleClickEnabled = true;
			addChild(map1);
			var point0:Point3D = IsoUtils.screenToIso(new Point(0,0));
			var point00:Point = new Point(Math.round(point0.x/cellWidth),Math.round(point0.z/cellWidth));
			countMaxAndMin(point00);
			var point1:Point3D = IsoUtils.screenToIso(new Point(mapWidth,0));
			var point11:Point = new Point(Math.round(point1.x/cellWidth),Math.round(point1.z/cellWidth));
			countMaxAndMin(point11);
			var point2:Point3D = IsoUtils.screenToIso(new Point(0,mapHeight));
			var point22:Point = new Point(Math.round(point2.x/cellWidth),Math.round(point2.z/cellWidth));
			countMaxAndMin(point22);
			var point3:Point3D = IsoUtils.screenToIso(new Point(mapWidth,mapHeight));
			var point33:Point = new Point(Math.round(point3.x/cellWidth),Math.round(point3.z/cellWidth));
			countMaxAndMin(point33);
			
			array = new Array();
			for(var i:int = minX; i <= maxX; i++)
			{
				array[i] = new Array();
				for(var j:int = minY; j <= maxY; j++)
				{
					var tile:DrawnIsoTile = new DrawnIsoTile(cellWidth, 0xcccccc);
					tile.position = new Point3D(i * cellWidth, 0, j * cellWidth);
					tile.doubleClickEnabled = true;
					map1.addChildToFloor(tile);
					array[i][j] = tile;
				}
			}
			
			grid = new Grid(minX, maxX, minY, maxY);
			addEventListener(MouseEvent.MOUSE_MOVE,onMouseMove);
			addEventListener(MouseEvent.MOUSE_DOWN,onMouseDown);
			addEventListener(MouseEvent.MOUSE_UP,onMouseUp);
			addEventListener(MouseEvent.DOUBLE_CLICK,onClick);
			stage.addEventListener(KeyboardEvent.KEY_DOWN,onKeyDown);
			stage.addEventListener(KeyboardEvent.KEY_UP,onKeyUp);
		}
		private function onKeyDown(event:KeyboardEvent):void{
			
		}
		public function onKeyUp(event:KeyboardEvent):void{
			if(event.keyCode == Keyboard.W){
				var file:File = new File("C:/Users/Administrator/Desktop/"+id+".json");    
				var stream:FileStream = new FileStream();
				stream.open(file,FileMode.WRITE);
				stream.addEventListener(IOErrorEvent.IO_ERROR,writeIOErrorHandler);
				stream.addEventListener(Event.COMPLETE, writeCompleteHandler);
				var str:String = "{\"id\":"+id+",\"cellWidth\":"+cellWidth+",\"threadId\":"+threadId+",\"mapWidth\":"+mapWidth+",\"mapHeight\":"+mapHeight+",\"cutMapSize\":300,\"type\":1,\"monster\":[";
				for(var i:int = 0;i<array.length;i++){
					var arr:Array = array[i];
					for(var j:int = 0;j<arr.length;j++){
						var  drawnIsoTile:DrawnIsoTile = arr[j];
						var node:Node = new Node(Math.round(drawnIsoTile.position.x/cellWidth),Math.round(drawnIsoTile.position.z/cellWidth));
						if(drawnIsoTile.color == oneColor){
							
							
							//if(i == array.length -1 && j == arr.length - 1){
								//str += "200000_"+node.x+"_"+node.y;
							//}else{
								str += "\"200000_"+node.x+"_"+node.y+"\",";
							//}
							
							
						}else if(drawnIsoTile.color == twoColor){
							str += "\"200001_"+node.x+"_"+node.y+"\",";
						}
					}
				}
				str = str.substr(0,str.length-1);
				str += "]}";
				stream.writeUTF(str);
				stream.close();
			}else if(event.keyCode == Keyboard.NUMBER_1){
				nowColor = oneColor;
			}else if(event.keyCode == Keyboard.NUMBER_2){
				nowColor = twoColor;
			}
		}
		private function writeIOErrorHandler(event:IOErrorEvent):void{
			
		}
		private function writeCompleteHandler(event:Event):void{
			
		}
		private function onClick(event:MouseEvent):void{
			//定位鼠标在哪个格子的
			var pos:Point3D = IsoUtils.screenToIso(new Point(stage.mouseX-map1.x, stage.mouseY-map1.y));
			pos.x = Math.round(pos.x / cellWidth);
			pos.y = Math.round(pos.y / cellWidth);
			pos.z = Math.round(pos.z / cellWidth);
			if(array[pos.x]){
				if(array[pos.x][pos.z]){
					(array[pos.x][pos.z] as DrawnIsoTile).color = nowColor;
				}
			}
		}
		private function onMouseMove(event:MouseEvent):void{
			
			if(isDown){
				var mapX:Number = 0;
				var mapY:Number = 0;
				mapX = map1.x+(stage.mouseX - mouseDownX);
				mapY = map1.y+(stage.mouseY - mouseDownY);
				
				if(mapX > 0){
					mapX = 0;
				}
				if(mapX < stage.stageWidth-mapWidth){
					mapX = stage.stageWidth-mapWidth;
				}
				if(mapY > 0){
					mapY = 0;
				}
				if(mapY < stage.stageHeight-mapHeight){
					mapY = stage.stageHeight-mapHeight;
				}
				if(map1.x != mapX){
					map1.x = mapX;
				}
				if(map1.y != mapY){
					map1.y = mapY;
				}
				mouseDownX = stage.mouseX;
				mouseDownY = stage.mouseY;
			}
			
			
			
		}
		private function onMouseDown(event:MouseEvent):void{
			isDown = true;
			mouseDownX = stage.mouseX;
			mouseDownY = stage.mouseY;
		}
		private function onMouseUp(event:MouseEvent):void{
			isDown = false;
		}
	}
}