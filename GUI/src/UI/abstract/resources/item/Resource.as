package UI.abstract.resources.item
{
	import flash.utils.getTimer;
	
	import UI.abstract.resources.ResourceManager;
	import UI.abstract.resources.loader.BaseLoader;

	public class Resource
	{
		/** 访问时间 **/
		private var _accessTime : int;

		/** 资源内容 **/
		protected var _content : *      = null;

		public var url : String;

		private var _isLocked : Boolean = false;

		public var useNumber : int      = 0;

		public function Resource ()
		{
		}

		/**
		 * 资源加载完成初始化资源数据
		 */
		public function initialize ( data : BaseLoader ) : void
		{
			_content = data.content;
			url = ResourceManager.unFormatResourceName(data.loadObj.url);;
		}

		/** 资源内容 **/
		public function get content () : *
		{
			updateTime();
			return _content;
		}
		public function set content (data:*):void{
			_content = data;
		}
		/**
		 * 卸载资源
		 */
		public function dispose () : void
		{
			useNumber = 0;
			_isLocked = false;
			url = null;
			_content = null;
			_accessTime = 0;
		}

		/** 资源上次访问的时间 **/
		public function get accessTime () : uint
		{
			return _accessTime;
		}

		public function set accessTime ( value : uint ) : void
		{
			_accessTime = value;
		}

		public function get isLocked () : Boolean
		{
			return _isLocked;
		}

		public function set isLocked ( value : Boolean ) : void
		{
			_isLocked = value;
		}
		
		/** 更新时间 **/
		public function updateTime () : void
		{
			accessTime = getTimer();
		}

	}
}
