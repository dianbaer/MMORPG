package UI.abstract.resources.item
{
	import flash.display.BitmapData;
	import flash.geom.Rectangle;
	import flash.utils.Dictionary;
	
	import UI.abstract.component.control.image.BitmapScale9Grid;
	import UI.abstract.resources.loader.BaseLoader;

	public class ImageResource extends Resource
	{
		private var squaredArray:Dictionary;
		
		/**
		 * 父类
		 */
		public var parent:ImageAtlasResource;
		/**
		 * 图片的位置
		 */
		public var frame:Rectangle;
		public var r:String;
		public function ImageResource()
		{
			super();
		}
		public function reset():ImageResource{
			return this;
		}
		override public function initialize ( data : BaseLoader ) : void
		{
			super.initialize( data );
			_content = data.content;
		}
		public function squaredBitmapData(_scale9Grid:Rectangle,_width:Number,_height:Number):BitmapData{
			if(!squaredArray){
				squaredArray = new Dictionary();
			}
			var str:String = String(_width)+","+String(_height)+","+String(_scale9Grid.x)+","+String(_scale9Grid.y);
			if(!squaredArray[str]){
				squaredArray[str] = BitmapScale9Grid.drawBitmapData(bitmapData,_scale9Grid,_width,_height);
			}
			return squaredArray[str] as BitmapData;
		}
		/**
		 * 获取bitmapData
		 */
		public function get bitmapData () : BitmapData
		{
			return _content ? (content as BitmapData) : null;
		}
		
		/**
		 * 获取bitmapData副本
		 */
		public function get bitmapDataCopy () : BitmapData
		{
			return _content ? (content as BitmapData).clone() : null;
		}
		
		override public function dispose():void
		{
			
			if ( _content )
			{
				BitmapData(_content).dispose()
				_content = null;
			}
			if(squaredArray){
				for ( var str : String in squaredArray )
				{
					if(squaredArray[str]){
						squaredArray[str].dispose();
					}
					delete squaredArray[str];
				}
			}
			parent = null;
			frame = null;
			r = null;
			super.dispose();
			toPool(this);
		}
		private static var sImageResourcePool:Vector.<ImageResource> = new <ImageResource>[];
		
		/** @private */
		public static function fromPool():ImageResource
		{
			if (sImageResourcePool.length) return sImageResourcePool.pop().reset();
			else return new ImageResource();
		}
		
		/** @private */
		public static function toPool(imageResource:ImageResource):void
		{
			
			sImageResourcePool[sImageResourcePool.length] = imageResource;
		}
	}
}