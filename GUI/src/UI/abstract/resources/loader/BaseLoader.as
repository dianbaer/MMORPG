package UI.abstract.resources.loader
{
	import flash.events.Event;
	import flash.events.EventDispatcher;
	import flash.events.IOErrorEvent;
	
	import UI.abstract.resources.item.LoadObj;

	public class BaseLoader extends EventDispatcher
	{
		public var loadObj : LoadObj;
		
		/** 加载内容 **/
		protected var _content : *;
		
		public function BaseLoader ( loadObj : LoadObj )
		{
			super();
			this.loadObj = loadObj;
		}

		public function load () : void
		{

		}

		protected function onComplete ( event : Event ) : void
		{

		}

		protected function onIOError ( event : IOErrorEvent ) : void
		{

		}
		
		public function get content () : *
		{
			return _content;
		}

		public function dispose () : void
		{
			loadObj = null;
			_content = null;
		}
	}
}
