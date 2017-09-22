package
{
	import flash.display.Bitmap;
	
	import UI.App;
	import UI.abstract.resources.item.ImageResource;

	public class MapCellData
	{
		public var id:String;
		//是否正在加载
		public var isLoad:Boolean = false;
		//资源
		public var resource:ImageResource;
		public var bitmap:Bitmap;
		private var isSetBitmapData:Boolean = false;
		public function MapCellData()
		{
			bitmap = new Bitmap();
			reset();
		}
		
		public function dispose():void{
			if ( id && isLoad)
			{
				if ( !resource  )
					App.loader.canelLoad( id , onComplete );
				id = null;
			}
			if ( resource ){
				App.loader.subtractUseNumber( resource );
				resource = null;
			}
			isLoad = false;
			bitmap.bitmapData = null;
			isSetBitmapData = false;
			bitmap.x = 0;
			bitmap.y = 0;
			if(bitmap.parent) bitmap.parent.removeChild(bitmap);
			toPool(this);
		}
		public function load():void{
			isLoad = true;
			App.loader.load(id,onComplete);
		}
		public function cannelLoad():void{
			
		}
		private function onComplete(resource:ImageResource):void{
			
			this.resource = resource;
			App.loader.addUseNumber(this.resource);
			
		}
		public function addToMap(map:Map):void{
			if(!isSetBitmapData){
				bitmap.bitmapData = this.resource.bitmapData;
			}
			updateScale();
			map.addChild(bitmap);
			isSetBitmapData = true;
		}
		public function updateScale():void{
			bitmap.width = bitmap.bitmapData.width*GlobalData.scale;
			bitmap.height = bitmap.bitmapData.height*GlobalData.scale;
		}
		public function reset():MapCellData{
			return this;
		}
		private static var sMapCellDataPool:Vector.<MapCellData> = new <MapCellData>[];
		
		/** @private */
		public static function fromPool():MapCellData
		{
			if (sMapCellDataPool.length) return sMapCellDataPool.pop().reset();
			else return new MapCellData();
		}
		
		/** @private */
		public static function toPool(mapCellData:MapCellData):void
		{
			
			sMapCellDataPool[sMapCellDataPool.length] = mapCellData;
		}
	}
}