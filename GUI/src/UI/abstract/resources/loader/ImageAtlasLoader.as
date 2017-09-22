package UI.abstract.resources.loader
{
	import flash.events.Event;
	import flash.events.IOErrorEvent;
	
	import UI.App;
	import UI.abstract.resources.ResourceManager;
	import UI.abstract.resources.item.ImageResource;
	import UI.abstract.resources.item.LoadObj;
	import UI.abstract.resources.item.XmlResource;
	import UI.abstract.utils.CommonPool;
	
	public class ImageAtlasLoader extends BaseLoader
	{
		
		public function ImageAtlasLoader(loadObj:LoadObj)
		{
			super(loadObj);
			_content = new Object();
			reset(loadObj);
			
		}
		public function reset(loadObj:LoadObj):ImageAtlasLoader{
			this.loadObj = loadObj;
			return this;
		}
		override public function load () : void
		{
			var str : String = ResourceManager.unFormatResourceName(loadObj.url.substr(0, loadObj.url.lastIndexOf( "." ) ));
			var helpObj:Object = CommonPool.fromPoolObject();
			helpObj.onField = onIOErrorXML;
			App.loader.load(str+".xml",onCompleteXML,helpObj,2);
			
		}
		
		protected function onCompleteXML ( res : XmlResource ) : void
		{
			_content.xmlResource = res;
			var str:String = res.url.slice(0,res.url.lastIndexOf("/")+1);
			var helpObj:Object = CommonPool.fromPoolObject();
			helpObj.onField = onIOErrorImage;
			App.loader.load(str+res.xml.attribute("imagePath")[0],onCompleteImage,helpObj,2);
		}
		
		protected function onIOErrorXML ( ) : void
		{
			dispatchEvent( new IOErrorEvent( IOErrorEvent.IO_ERROR ) );
		}
		protected function onCompleteImage ( res : ImageResource ) : void
		{
			_content.imageResource = res;
			dispatchEvent( new Event( Event.COMPLETE ) );
		}
		
		protected function onIOErrorImage ( ) : void
		{
			dispatchEvent( new IOErrorEvent( IOErrorEvent.IO_ERROR ) );
		}
		
		override public function dispose () : void
		{
			_content.imageResource = null;
			delete _content.imageResource;
			_content.xmlResource = null;
			delete _content.xmlResource;
			super.dispose();
			toPool(this);
		}
		private static var sImageAtlasLoaderPool:Vector.<ImageAtlasLoader> = new <ImageAtlasLoader>[];
		
		/** @private */
		public static function fromPool(loadObj : LoadObj):ImageAtlasLoader
		{
			if (sImageAtlasLoaderPool.length) return sImageAtlasLoaderPool.pop().reset(loadObj);
			else return new ImageAtlasLoader(loadObj);
		}
		
		/** @private */
		public static function toPool(imageAtlasLoader:ImageAtlasLoader):void
		{
			
			sImageAtlasLoaderPool[sImageAtlasLoaderPool.length] = imageAtlasLoader;
		}
	}
}