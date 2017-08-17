package UI.abstract.resources.item
{
	import flash.display.BitmapData;
	import flash.geom.Point;
	import flash.geom.Rectangle;
	import flash.utils.Dictionary;
	
	import UI.App;
	import UI.abstract.resources.ResourceManager;
	import UI.abstract.resources.ResourceUtil;
	import UI.abstract.resources.loader.BaseLoader;

	public class ImageAtlasResource extends Resource
	{
		private var imageResource:ImageResource;
		private var mTextureRegions:Dictionary;
		private var mTextureFrames:Dictionary;
		private var rDict:Dictionary;
		
		private static var sNames:Array = new Array();
		public function ImageAtlasResource()
		{
			super();
			mTextureRegions = new Dictionary();
			mTextureFrames  = new Dictionary();
			rDict = new Dictionary();
		}
		override public function initialize ( data : BaseLoader ) : void
		{
			super.initialize( data );
			imageResource = data.content.imageResource;
			App.loader.addUseNumber( imageResource );
			parseAtlasXml(data.content.xmlResource.xml);
			//_content.imageResource = null;
			//delete _content.imageResource;
			//_content.xmlResource = null;
			//delete _content.xmlResource;
			_content = null;
			
			
		}
		protected function parseAtlasXml(atlasXml:XML):void
		{
 			
			for each (var subTexture:XML in atlasXml.sprite)
			{
				var name:String        = subTexture.attribute("n");
				var x:Number           = parseFloat(subTexture.attribute("x"));
				var y:Number           = parseFloat(subTexture.attribute("y"));
				var width:Number       = parseFloat(subTexture.attribute("w"));
				var height:Number      = parseFloat(subTexture.attribute("h"));
				var frameX:Number      = parseFloat(subTexture.attribute("oX"));
				var frameY:Number      = parseFloat(subTexture.attribute("oY"));
				var frameWidth:Number  = parseFloat(subTexture.attribute("oW"));
				var frameHeight:Number = parseFloat(subTexture.attribute("oH"));
				var r:String = subTexture.attribute("r");
				if(!r){
					r = "x";
				}
				
				var region:Rectangle = new Rectangle(x, y, width, height);
				var frame:Rectangle  = frameWidth > 0 && frameHeight > 0 ?
					new Rectangle(frameX, frameY, frameWidth, frameHeight) : null;
				
				addRegion(name, region, frame, r);
			}
		}
		public function addRegion(name:String, region:Rectangle, frame:Rectangle=null,r:String = "x"):void
		{
			mTextureRegions[name] = region;
			mTextureFrames[name]  = frame;
			rDict[name] = r;
		}
		public function getTexture(name:String):ImageResource
		{
			//有可能图片集被清除了，图片并没有被清除，在获取图片的事件，直接返回，增加重复资源
			if(App.loader.getResource( ResourceManager.unFormatResourceName( url+"?"+ResourceUtil.IMAGE+"="+name ))){
				return App.loader.getResource( ResourceManager.unFormatResourceName( url+"?"+ResourceUtil.IMAGE+"="+name )) as ImageResource;
			}
			var region:Rectangle = mTextureRegions[name];
			var bmd : BitmapData = new BitmapData( region.width , region.height , true , 0 );
			bmd.copyPixels( imageResource.bitmapData , region , new Point( 0 , 0 ) );
			
			var newImageResource:ImageResource = ImageResource.fromPool();
			newImageResource.content = bmd;
			newImageResource.parent = this;
			newImageResource.frame = mTextureFrames[name];
			newImageResource.r = rDict[name];
			newImageResource.url = url+"?"+ResourceUtil.IMAGE+"="+name;
			App.loader.addResource(ResourceManager.unFormatResourceName( newImageResource.url ) , newImageResource);
			return newImageResource;
		}
		public function getTextures(prefix:String):ImageArrayResource
		{
			var imageArrayResource:ImageArrayResource = ImageArrayResource.fromPool();
			var result:Vector.<ImageResource> = imageArrayResource._imageArray;
			
			for each (var name:String in getNames(prefix, sNames))
				result[result.length] = getTexture(name); 
			
			sNames.length = 0;
			
			
			imageArrayResource.content = result;
			imageArrayResource.url = url+"?"+ResourceUtil.MC+"="+prefix;
			App.loader.addResource(ResourceManager.unFormatResourceName( imageArrayResource.url ) , imageArrayResource);
			return imageArrayResource;
		}
		public function getNames(prefix:String="", result:Array=null):Array
		{
			if (result == null) result = new Array();
			
			for (var name:String in mTextureRegions)
				if (name.indexOf(prefix) == 0)
					result[result.length] = name.substr(prefix.length,name.lastIndexOf(".")-prefix.length);
			
			result.sort(Array.NUMERIC);
			
			for (var i:int = 0;i<result.length;i++) {
				result[i] = prefix+result[i]+".png";
			}
			return result;
		}
		override public function dispose():void
		{
			
			App.loader.subtractUseNumber( imageResource );
			imageResource = null;
			mTextureRegions = null;
			mTextureFrames = null;
			rDict = null;
			super.dispose();
		}
	}
}