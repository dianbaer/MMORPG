package UI.abstract.resources.item
{
	import UI.abstract.resources.loader.BaseLoader;

	public class XmlResource extends Resource
	{
		public function XmlResource()
		{
			super();
		}
		override public function initialize ( data : BaseLoader ) : void
		{
			super.initialize( data );
			_content = XML(data.content);
		}
		public function reset():XmlResource{
			return this;
		}
		
		public function get xml () : XML
		{
			return _content ? (content as XML) : null;
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
		private static var sXmlResourcePool:Vector.<XmlResource> = new <XmlResource>[];
		
		/** @private */
		public static function fromPool():XmlResource
		{
			if (sXmlResourcePool.length) return sXmlResourcePool.pop().reset();
			else return new XmlResource();
		}
		
		/** @private */
		public static function toPool(xmlResource:XmlResource):void
		{
			
			sXmlResourcePool[sXmlResourcePool.length] = xmlResource;
		}
	}
}