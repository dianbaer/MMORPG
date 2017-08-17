package UI.abstract.resources.loader
{
	import flash.events.Event;
	import flash.events.IOErrorEvent;
	import flash.net.URLLoader;
	import flash.net.URLRequest;
	
	import UI.abstract.resources.item.LoadObj;
	
	public class JsonLoader extends BaseLoader
	{
		private var loader : URLLoader;
		public function JsonLoader(loadObj:LoadObj)
		{
			super(loadObj);
			reset(loadObj);
			
		}
		public function reset(loadObj:LoadObj):JsonLoader{
			this.loadObj = loadObj;
			if(!loader){
				loader = new URLLoader();
			}
			loader.addEventListener( Event.COMPLETE , onComplete );
			loader.addEventListener( IOErrorEvent.IO_ERROR , onIOError );
			return this;
		}
		override public function load () : void
		{
			loader.load( new URLRequest( loadObj.url ) );
		}
		
		override protected function onComplete ( event : Event ) : void
		{
			_content = loader.data;
			dispatchEvent( new Event( Event.COMPLETE ) );
		}
		
		override protected function onIOError ( event : IOErrorEvent ) : void
		{
			dispatchEvent( new IOErrorEvent( IOErrorEvent.IO_ERROR ) );
		}
		
		override public function dispose () : void
		{
			
			loader.removeEventListener( Event.COMPLETE , onComplete );
			loader.removeEventListener( IOErrorEvent.IO_ERROR , onIOError );
			//loader = null;
			super.dispose();
			toPool(this);
		}
		private static var sJsonLoaderPool:Vector.<JsonLoader> = new <JsonLoader>[];
		
		/** @private */
		public static function fromPool(loadObj : LoadObj):JsonLoader
		{
			if (sJsonLoaderPool.length) return sJsonLoaderPool.pop().reset(loadObj);
			else return new JsonLoader(loadObj);
		}
		
		/** @private */
		public static function toPool(jsonLoader:JsonLoader):void
		{
			
			sJsonLoaderPool[sJsonLoaderPool.length] = jsonLoader;
		}
	}
}