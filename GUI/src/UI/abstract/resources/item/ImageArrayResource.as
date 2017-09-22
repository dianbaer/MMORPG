package UI.abstract.resources.item
{
	import UI.App;

	public class ImageArrayResource extends Resource
	{
		public var _imageArray:Vector.<ImageResource> = new Vector.<ImageResource>();
		public function ImageArrayResource()
		{
			super();
		}
		public function reset():ImageArrayResource{
			return this;
		}
		override public function set content (data:*):void{
			_content = data;
			_imageArray = _content;
			for(var i:int = 0;i<_imageArray.length;i++){
				App.loader.addUseNumber( _imageArray[i] );
			}
			_content = null;
		}
		override public function dispose():void
		{
			
			for(var i:int = 0;i<_imageArray.length;i++){
				App.loader.subtractUseNumber( _imageArray[i] );
			}
			_imageArray.length = 0;
			
			super.dispose();
			toPool(this);
		}
		private static var sImageArrayResourcePool:Vector.<ImageArrayResource> = new <ImageArrayResource>[];
		
		/** @private */
		public static function fromPool():ImageArrayResource
		{
			if (sImageArrayResourcePool.length) return sImageArrayResourcePool.pop().reset();
			else return new ImageArrayResource();
		}
		
		/** @private */
		public static function toPool(imageArrayResource:ImageArrayResource):void
		{
			
			sImageArrayResourcePool[sImageArrayResourcePool.length] = imageArrayResource;
		}
	}
}