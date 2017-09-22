package UI.abstract.resources.loader
{
	import UI.abstract.resources.ResourceManager;
	import UI.abstract.resources.item.Resource;
	import UI.abstract.utils.CommonPool;

	/**
	 * 多文件加载器
	 */
	public class MultiLoader
	{
		private var obj : Object;

		/** 当前未加载数量 **/
		public var currNum : int;

		private var _res : ResourceManager;

		/** 是否取消加载 **/
		private var isCanel : Boolean;
		
		private var _isComplete:Boolean = false;
		
		private var list:Array;

		public function MultiLoader ( res : ResourceManager , list : Array , obj : Object )
		{
			
			reset(res,list,obj);

		}
		public function reset(res : ResourceManager , list : Array , obj : Object):MultiLoader{
			_res = res;
			currNum = list.length;
			this.obj = obj;
			this.list = list;
			
			return this;
		}
		public function load():void{
			//如果这个资源是已经加载完成的，只会会回调然后，注销，list在最后一次循环会被注销的，所以得取list的长度
			var length:int = list.length;
			for ( var i : int = 0 ; i < length ; i++ ){
				var helpObj:Object = CommonPool.fromPoolObject();
				helpObj.onField = onField;
				_res.load( list[ i ] , onComplete , helpObj );
			}
		}
		private function onComplete ( res : Resource ) : void
		{
			
			currNum--;
			if ( currNum == 0 )
			{
				_isComplete = true;
				if ( isCanel )
				{
					dispose();
					return;
				}
				//选回调，再移除
				if (obj && obj.onComplete )
				{
					obj.onComplete.apply( null , obj.onCompleteParam );
					_res.loadListOnComplete(obj.onComplete);
				}
				dispose();
			}

		}

		private function onField () : void
		{
			_isComplete = true;
			currNum--;
			
			if ( isCanel )
			{
				dispose();
				return;
			}
			//选回调，再移除
			if (obj && obj.onField )
			{
				obj.onField.apply(null , obj.onFieldParam);
			}
			if (obj && obj.onComplete )
			{
				_res.loadListOnComplete(obj.onComplete);
			}
			dispose();
			
		}

		public function canel () : void
		{
			isCanel = true;
		}

		public function dispose () : void
		{
			obj = null;
			currNum = 0;
			isCanel = false;
			_isComplete = false;
			_res = null;
			list.length = 0;
			list = null;
			toPool(this);
		}
		/**
		 * 是否处于完成回调状态，如果是外部就不调用这个类的方法
		 */
		public function get isComplete():Boolean
		{
			return _isComplete;
		}
		private static var sMultiLoaderPool:Vector.<MultiLoader> = new <MultiLoader>[];
		
		/** @private */
		public static function fromPool(res : ResourceManager , list : Array , obj : Object):MultiLoader
		{
			if (sMultiLoaderPool.length) return sMultiLoaderPool.pop().reset(res,list,obj);
			else return new MultiLoader(res,list,obj);
		}
		
		/** @private */
		public static function toPool(multiLoader:MultiLoader):void
		{
			
			sMultiLoaderPool[sMultiLoaderPool.length] = multiLoader;
		}
	}
}
