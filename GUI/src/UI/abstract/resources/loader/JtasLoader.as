package UI.abstract.resources.loader
{
	import flash.display.Bitmap;
	import flash.display.Loader;
	import flash.events.Event;
	import flash.events.IOErrorEvent;
	
	import UI.abstract.resources.item.LoadObj;
	
	public class JtasLoader extends BaseLoader
	{
		private var loader : Loader;
		public function JtasLoader(loadObj:LoadObj)
		{
			super(loadObj);
			reset(loadObj);
			
		}
		public function reset(loadObj:LoadObj):JtasLoader{
			this.loadObj = loadObj;
			if(!loader){
				loader = new Loader();
			}
			loader.contentLoaderInfo.addEventListener( Event.COMPLETE , onComplete );
			loader.contentLoaderInfo.addEventListener( IOErrorEvent.IO_ERROR , onIOError );
			return this;
		}
		override public function load () : void
		{
			loader.loadBytes(  loadObj.byteArray  );
		}
		
		override protected function onComplete ( event : Event ) : void
		{
			_content = Bitmap(loader.content).bitmapData;
			dispatchEvent( new Event( Event.COMPLETE ) );
		}
		
		override protected function onIOError ( event : IOErrorEvent ) : void
		{
			dispatchEvent( new IOErrorEvent( IOErrorEvent.IO_ERROR ) );
		}
		
		override public function dispose () : void
		{
			
			loader.contentLoaderInfo.removeEventListener( Event.COMPLETE , onComplete );
			loader.contentLoaderInfo.removeEventListener( IOErrorEvent.IO_ERROR , onIOError );
			loader.unloadAndStop(false);
			loader.unload();
			//loader = null;
			super.dispose();
			toPool(this);
		}
		private static var sJtasLoaderPool:Vector.<JtasLoader> = new <JtasLoader>[];
		
		/** @private */
		public static function fromPool(loadObj : LoadObj):JtasLoader
		{
			if (sJtasLoaderPool.length) return sJtasLoaderPool.pop().reset(loadObj);
			else return new JtasLoader(loadObj);
		}
		
		/** @private */
		public static function toPool(jtasLoader:JtasLoader):void
		{
			
			sJtasLoaderPool[sJtasLoaderPool.length] = jtasLoader;
		}
	}
}