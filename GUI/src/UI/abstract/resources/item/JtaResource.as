package UI.abstract.resources.item
{
	import UI.App;
	import UI.abstract.resources.loader.BaseLoader;

	public class JtaResource extends Resource
	{
		public function JtaResource()
		{
			super();
		}
		
		override public function initialize ( data : BaseLoader ) : void
		{
			super.initialize( data );
			
		}
		public function reset():JtaResource{
			return this;
		}
		override public function dispose():void
		{
			for each ( var res : Object in _content )
			{
				if(res is Object && res.hasOwnProperty("dirCount")){
					for(var i:int = 1;i<=res["dirCount"];i++){
						var obj:Object = res["dirData"][i];
						for(var j:int = 0;j<obj["frames"].length;j++){
							App.loader.subtractUseNumber(obj["frames"][j]["imageResource"]);
							obj["frames"][j]["imageResource"] = null;
							
						}
						
					}
				}
				
			}
			
			super.dispose();
			toPool(this);
		}
		private static var sJtaResourcePool:Vector.<JtaResource> = new <JtaResource>[];
		
		/** @private */
		public static function fromPool():JtaResource
		{
			if (sJtaResourcePool.length) return sJtaResourcePool.pop().reset();
			else return new JtaResource();
		}
		
		/** @private */
		public static function toPool(jtaResource:JtaResource):void
		{
			
			sJtaResourcePool[sJtaResourcePool.length] = jtaResource;
		}
	}
}