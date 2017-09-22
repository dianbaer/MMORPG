package
{
	import flash.display.Sprite;
	import flash.utils.Dictionary;
	
	public class Map extends Sprite
	{
		private static var helpArray:Array = new Array();
		private static var helpArray2:Array = new Array();
		private var mapData:Object;
		public var mapX:Number = 0;
		public var mapY:Number = 0;
		public var stageWidth:Number = 0;
		public var stageHeight:Number = 0;
		private var mapWidth:int;
		private var mapHeight:int;
		private var cutMapSize:int;
		private var sourceDict:Dictionary;
		
		private var childDict:Array;
		//上一次最大最小的索引
		private var upminX:int;
		private var upmaxX:int;
		private var upminY:int;
		private var upmaxY:int;
		//一帧加载几张图
		private static var oneFrameAddNum:int = 1;
		private static var frameOnTime:int = 5;
		private var nowFrame:int = 0;
		public function Map(mapData:Object)
		{
			super();
			
			
			childDict = new Array();
			sourceDict = new Dictionary();
			reset(mapData);
			
			
		}
		public function reset(mapData:Object):Map{
			
			this.mapData = mapData;
			mapWidth = mapData["mapWidth"]*GlobalData.scale;
			mapHeight = mapData["mapHeight"]*GlobalData.scale;
			cutMapSize = mapData["cutMapSize"]*GlobalData.scale;
			var wSize:int = Math.ceil(mapWidth/cutMapSize);
			var hSize:int = Math.ceil(mapHeight/cutMapSize);
			for(var i:int = 0;i<hSize;i++){
				for(var j:int = 0;j<wSize;j++){
					var str:String = "map/s"+mapData["id"]+"/s"+mapData["id"]+"_"+i+"_"+j+".jpg";
					sourceDict[str] = MapCellData.fromPool();
					sourceDict[str].id = str;
					
					sourceDict[str].bitmap.y = i*cutMapSize;
					sourceDict[str].bitmap.x = j*cutMapSize;
				}
				
			}
			return this;
		}
		public function changeScale():void{
			mapWidth = mapData["mapWidth"]*GlobalData.scale;
			mapHeight = mapData["mapHeight"]*GlobalData.scale;
			cutMapSize = mapData["cutMapSize"]*GlobalData.scale;
			var wSize:int = Math.ceil(mapWidth/cutMapSize);
			var hSize:int = Math.ceil(mapHeight/cutMapSize);
			for(var i:int = 0;i<hSize;i++){
				for(var j:int = 0;j<wSize;j++){
					var str:String = "map/s"+mapData["id"]+"/s"+mapData["id"]+"_"+i+"_"+j+".jpg";
					if(sourceDict[str]){
						sourceDict[str].bitmap.y = i*cutMapSize;
						sourceDict[str].bitmap.x = j*cutMapSize;
					}
				}
				
			}
		}
		public function updateScale():void{
			var minX:int = Math.floor((Math.abs(mapX)/cutMapSize))-1;
			var maxX:int = stageWidth + Math.abs(mapX) > mapWidth ? mapWidth : stageWidth + Math.abs(mapX);
			maxX = Math.ceil(maxX/cutMapSize)+1;
			
			var minY:int = Math.floor((Math.abs(mapY)/cutMapSize))-1;
			var maxY:int = stageHeight + Math.abs(mapY) > mapHeight ? mapHeight : stageHeight + Math.abs(mapY);
			maxY = Math.ceil(maxY/cutMapSize)+1;
			
			var mapCellData:MapCellData;
			for(var i:int = minY;i < maxY;i++){
				for(var j:int = minX;j < maxX;j++){
					var str:String = "map/s"+mapData["id"]+"/s"+mapData["id"]+"_"+i+"_"+j+".jpg";
					mapCellData = sourceDict[str];
					if(mapCellData){
						helpArray[helpArray.length] = mapCellData;
					}
				}
			}
			
			var length:int = helpArray.length;
			var middle:int = (length/2)+1;
			
			for(var s:int = 0;s<=middle;s++){
				if(s == 0){
					if(middle >= 0 && middle < length){
						helpArray2[helpArray2.length] = helpArray[middle];
					}
				}else{
					if(middle+s >= 0 && middle+s < length){
						helpArray2[helpArray2.length] = helpArray[middle+s];
					}
					if(middle-s >= 0 && middle-s < length){
						helpArray2[helpArray2.length] = helpArray[middle-s];
					}
					
				}
			}
			
			var mapCellData2:MapCellData = null;
			for(var m:int = 0;m<childDict.length;m++){
				mapCellData2 = childDict[m];
				var isHave:Boolean = false;
				var mapCellData1:MapCellData = null;
				for(var n:int = 0;n<helpArray2.length;n++){
					mapCellData1 = helpArray2[n];
					if(mapCellData1 == mapCellData2){
						isHave = true;
						break;
					}
				}
				if(!isHave && mapCellData2.bitmap.parent != null){
					mapCellData2.bitmap.parent.removeChild(mapCellData2.bitmap);
				}
			}
			childDict = helpArray2.concat();
			helpArray.length = 0;
			helpArray2.length = 0;
			upminX = minX;
			upminY = minY;
			upmaxX = maxX;
			upmaxY = maxY;
			
			var mapCellData3:MapCellData;
			for(var nn:int = 0;nn<childDict.length;nn++){
				mapCellData3 = childDict[nn];
				if(mapCellData3.bitmap.parent && mapCellData3.resource != null){
					mapCellData3.updateScale();
				}
			}
			update1();
		}
		public function update(isInit:Boolean = false):void{
			

			var minX:int = Math.floor((Math.abs(mapX)/cutMapSize))-1;
			var maxX:int = stageWidth + Math.abs(mapX) > mapWidth ? mapWidth : stageWidth + Math.abs(mapX);
			maxX = Math.ceil(maxX/cutMapSize)+1;
			
			var minY:int = Math.floor((Math.abs(mapY)/cutMapSize))-1;
			var maxY:int = stageHeight + Math.abs(mapY) > mapHeight ? mapHeight : stageHeight + Math.abs(mapY);
			maxY = Math.ceil(maxY/cutMapSize)+1;
			if(!isInit && minX == upminX && minY == upminY && maxX == upmaxX && maxY == upmaxY){
				update1();
				return;
			}

			var mapCellData:MapCellData;
			for(var i:int = minY;i < maxY;i++){
				for(var j:int = minX;j < maxX;j++){
					var str:String = "map/s"+mapData["id"]+"/s"+mapData["id"]+"_"+i+"_"+j+".jpg";
					mapCellData = sourceDict[str];
					if(mapCellData){
						helpArray[helpArray.length] = mapCellData;
					}
				}
			}
			var length:int = helpArray.length;
			var middle:int = (length/2)+1;
			
			for(var s:int = 0;s<=middle;s++){
				if(s == 0){
					if(middle >= 0 && middle < length){
						helpArray2[helpArray2.length] = helpArray[middle];
					}
				}else{
					if(middle+s >= 0 && middle+s < length){
						helpArray2[helpArray2.length] = helpArray[middle+s];
					}
					if(middle-s >= 0 && middle-s < length){
						helpArray2[helpArray2.length] = helpArray[middle-s];
					}
					
				}
			}
			var mapCellData2:MapCellData = null;
			for(var m:int = 0;m<childDict.length;m++){
				mapCellData2 = childDict[m];
				var isHave:Boolean = false;
				var mapCellData1:MapCellData = null;
				for(var n:int = 0;n<helpArray2.length;n++){
					mapCellData1 = helpArray2[n];
					if(mapCellData1 == mapCellData2){
						isHave = true;
						break;
					}
				}
				if(!isHave && mapCellData2.bitmap.parent != null){
					mapCellData2.bitmap.parent.removeChild(mapCellData2.bitmap);
				}
			}
			childDict = helpArray2.concat();
			helpArray.length = 0;
			helpArray2.length = 0;
			upminX = minX;
			upminY = minY;
			upmaxX = maxX;
			upmaxY = maxY;
			update1();
		}
		public function update1():void{
			//nowFrame++;
			//if(nowFrame >= frameOnTime){
			//	nowFrame = 0;
			//}else{
			//	return;
			//}
			var num:int = 0;
			var num1:int = 0;
			var mapCellData:MapCellData;
			for(var i:int = 0;i<childDict.length;i++){
				mapCellData = childDict[i];
				if(num1 < oneFrameAddNum){
					if(!mapCellData.isLoad){
						mapCellData.load();
						num1++;
					}
				}
				if(num < oneFrameAddNum){
					if(!mapCellData.bitmap.parent && mapCellData.resource != null){
						mapCellData.addToMap(this);
						num++;
					}
				}
				if(num >= oneFrameAddNum && num1 >= oneFrameAddNum){
					break;
				}
			}
		}
		public function dispose():void{
			
			if(parent){
				parent.removeChild(this);
			}
			var mapCellData : MapCellData;
			for each ( mapCellData in sourceDict )
			{
				delete sourceDict[mapCellData.id];
				mapCellData.dispose();
				//delete mapCellData;
			}
			
			mapData = null;
			mapX = 0;
			mapY = 0;
			stageWidth = 0;
			stageHeight = 0;
			mapWidth = 0;
			mapHeight = 0;
			cutMapSize = 0;
			upminX = 0;
			upmaxX = 0;
			upminY = 0;
			upmaxY = 0;
			x = 0;
			y = 0;
			childDict.length = 0;
			
		}
	}
}