package UI.abstract.resources
{
	import UI.abstract.resources.item.BinaryResource;
	import UI.abstract.resources.item.ImageAtlasResource;
	import UI.abstract.resources.item.ImageResource;
	import UI.abstract.resources.item.JsonResource;
	import UI.abstract.resources.item.JtaResource;
	import UI.abstract.resources.item.LoadObj;
	import UI.abstract.resources.item.MCResource;
	import UI.abstract.resources.item.Resource;
	import UI.abstract.resources.item.XmlResource;
	import UI.abstract.resources.loader.BaseLoader;
	import UI.abstract.resources.loader.BinaryLoader;
	import UI.abstract.resources.loader.ImageAtlasLoader;
	import UI.abstract.resources.loader.ImageLoader;
	import UI.abstract.resources.loader.JsonLoader;
	import UI.abstract.resources.loader.JtaLoader;
	import UI.abstract.resources.loader.JtasLoader;
	import UI.abstract.resources.loader.MCLoader;
	import UI.abstract.resources.loader.XmlLoader;
	
	public class ResourceFactory
	{
		/** 加载器类映射 **/
		private static var loaderClass : Object       = {
			png: "image" ,
			jpg: "image" ,
			gif: "image" ,
			jpeg: "image" ,
			swf: "mc" ,
			xml: "xml" ,
			atlas:"imageAtlas",
			jta:"jta",
			jtas:"jtas",
			json:"json"};
		
		
		
		/**
		 * 检测类型
		 */
		public static function checkType ( url : String ) : String
		{
			var str : String = url.substr( url.lastIndexOf( "." ) + 1 );
			if(str.indexOf("?") != -1){
				str = str.substring(0,str.indexOf("?"));
			}
			return str.toLowerCase();
		}
		
		/**
		 * 创建加载器类
		 */
		public static function createLoader ( loadObj : LoadObj ) : BaseLoader
		{
			if ( loaderClass[ loadObj.extension ] == null )
				return new BinaryLoader( loadObj );
			if(loaderClass[ loadObj.extension ] == "image")
				return ImageLoader.fromPool(loadObj);
			if(loaderClass[ loadObj.extension ] == "json")
				return JsonLoader.fromPool(loadObj);
			if(loaderClass[ loadObj.extension ] == "jtas")
				return JtasLoader.fromPool(loadObj);
			if(loaderClass[ loadObj.extension ] == "xml")
				return XmlLoader.fromPool(loadObj);
			if(loaderClass[ loadObj.extension ] == "imageAtlas")
				return ImageAtlasLoader.fromPool(loadObj);
			if(loaderClass[ loadObj.extension ] == "jta")
				return JtaLoader.fromPool(loadObj);
			return new loaderClass[ loadObj.extension ]( loadObj );
		}
		
		/**
		 * 创建资源类
		 */
		public static function createResource ( loadObj : LoadObj ) : Resource
		{
			if ( loaderClass[ loadObj.extension ] == null )
				return new BinaryResource();
			if(loaderClass[ loadObj.extension ] == "image")
				return ImageResource.fromPool();
			if(loaderClass[ loadObj.extension ] == "jta")
				return JtaResource.fromPool();
			if(loaderClass[ loadObj.extension ] == "json")
				return JsonResource.fromPool();
			if(loaderClass[ loadObj.extension ] == "xml")
				return XmlResource.fromPool();
			if(loaderClass[ loadObj.extension ] == "jtas")
				return ImageResource.fromPool();
			return new loaderClass[ loadObj.extension ]();
		}
	}
}