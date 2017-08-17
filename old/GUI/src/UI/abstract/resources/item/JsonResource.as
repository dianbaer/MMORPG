package UI.abstract.resources.item
{
	import UI.abstract.resources.loader.BaseLoader;

	public class JsonResource extends Resource
	{
		public function JsonResource()
		{
			super();
		}
		override public function initialize ( data : BaseLoader ) : void
		{
			super.initialize( data );
			
			_content = JSON.parse(data.content);
		}
		public function reset():JsonResource{
			return this;
		}
		/**
		 * 获取bitmapData
		 */
		public function get object () : Object
		{
			return _content ? (content as Object) : null;
		}
		override public function dispose():void
		{
			
			if ( _content )
			{
				_content = null;
			}
			super.dispose();
			toPool(this);
		}
		private static var sJsonResourcePool:Vector.<JsonResource> = new <JsonResource>[];
		
		/** @private */
		public static function fromPool():JsonResource
		{
			if (sJsonResourcePool.length) return sJsonResourcePool.pop().reset();
			else return new JsonResource();
		}
		
		/** @private */
		public static function toPool(jsonResource:JsonResource):void
		{
			
			sJsonResourcePool[sJsonResourcePool.length] = jsonResource;
		}
	}
}