package UI.abstract.resources.loader
{
	import flash.events.Event;
	import flash.events.IOErrorEvent;
	import flash.utils.Dictionary;
	
	import UI.App;
	import UI.abstract.manager.LogManager;
	import UI.abstract.resources.ResourceFactory;
	import UI.abstract.resources.ResourceManager;
	import UI.abstract.resources.item.LoadObj;
	import UI.abstract.resources.item.Resource;

	/**
	 * 队列加载器
	 */
	public class QueueLoader
	{
		/**缓存列表**/
		private var cache : Object    = {};

		/**最大加载数量（增加人物资源类之后，同时加载数量小，会出现问题,这个问题回来在解决，只要同时加载数量大一些就不会出现问题了）**/
		private var maxLoadNum : int  = 5;

		/** 正在加载数量 **/
		private var loadingNum : int  = 0;

		/** 等待队列 **/
		private var waitQueue : Array = [];

		private var res : ResourceManager;
		
		/** loader缓存 防止回收 **/
		private var _cacheLoader : Dictionary = new Dictionary();

		public function QueueLoader ( res : ResourceManager )
		{
			this.res = res;
		}

		public function append ( obj : Object ) : void
		{
			// 正在队列中，不在回调状态下
			if ( cache[ obj.url ] != null && !(cache[ obj.url ] as LoadObj).isComplete)
			{
				LoadObj( cache[ obj.url ] ).push( obj );
				//如果有二级制数据，肯定还有待加载的数据
				if(obj.raw){
					LoadObj( cache[ obj.url ] ).byteArray = obj.raw;
					obj.raw = null;
					delete obj.raw;
					loadInWaitQueue();
				}
				return;
			}else if(cache[ obj.url ] != null && (cache[ obj.url ] as LoadObj).isComplete){
				App.log.info( "不允许，在加载资源完成的回调函数，再次加载相同的资源" , LogManager.LOG_WARN , "QueueLoader" , "append" );
				return;
			}

			// 创建新的loadObj
			var loadObj : LoadObj = LoadObj.fromPool( res );
			loadObj.url = obj.url;
			loadObj.extension = ResourceFactory.checkType( obj.url );
			//存入二级制数据，加载二级制
			if(obj.raw){
				loadObj.byteArray = obj.raw;
				obj.raw = null;
				delete obj.raw;
			}
			
			loadObj.push( obj );
			cache[ obj.url ] = loadObj;

			waitQueue[waitQueue.length] = loadObj;
			loadInWaitQueue();
		}

		/**
		 * 从等待队列里面加载图片放入加载队列
		 */
		private function loadInWaitQueue () : void
		{
			if ( loadingNum < maxLoadNum && waitQueue.length > 0 )
			{
				
				
				
				
				//是不是已经循环一圈了，都没有合适的资源进行加载
				var isOneRound:Boolean = false;
				var loadObj : LoadObj   = waitQueue.shift();
				var loadObj1:LoadObj = loadObj;
				//如果是jtas并且有二级制数据再去加载
				while(loadObj1.extension == "jtas" && !loadObj1.byteArray){
					waitQueue[waitQueue.length] = loadObj1;
					loadObj1 = waitQueue.shift();
					//一圈了
					if(loadObj1 == loadObj){
						isOneRound = true;
						break;
					}
				}
				//一圈了就放回去，返回
				if(isOneRound){
					waitQueue[waitQueue.length] = loadObj1;
					return;
				}
				loadingNum++;
				loadObj = loadObj1;
				
				
				var loader : BaseLoader = ResourceFactory.createLoader( loadObj );
				_cacheLoader[loader] = true;
				loader.addEventListener( Event.COMPLETE , onComplete );
				loader.addEventListener( IOErrorEvent.IO_ERROR , onIOError );
				loader.load();
			}
		}

		/**
		 * 加载完成
		 */
		private function onComplete ( event : Event ) : void
		{
			loadingNum--;
			var loader : BaseLoader = event.target as BaseLoader;
			loader.removeEventListener( Event.COMPLETE , onComplete );
			loader.removeEventListener( IOErrorEvent.IO_ERROR , onIOError );
			delete _cacheLoader[loader];
			
			// 创建资源并初始化
			var resource : Resource = ResourceFactory.createResource( loader.loadObj );
			resource.initialize( loader );
			res.addResource( ResourceManager.unFormatResourceName( loader.loadObj.url ) , resource )

			App.log.info( "加载完成 : " + loader.loadObj.url , LogManager.LOG_NORMAL , "QueueLoader" , "onComplete" );

			// 回调完成函数并释放loader
			loader.loadObj.onComplete();
			var loadObj : LoadObj = cache[ loader.loadObj.url ];
//			cache[ loader.loadObj.url ] = null;
			delete cache[ loader.loadObj.url ];
			loader.dispose();
			loadObj.dispose();
			loadInWaitQueue();
		}

		/**
		 * 异常
		 */
		private function onIOError ( event : IOErrorEvent ) : void
		{
			loadingNum--;
			var loader : BaseLoader = event.target as BaseLoader;
			loader.removeEventListener( Event.COMPLETE , onComplete );
			loader.removeEventListener( IOErrorEvent.IO_ERROR , onIOError );
			delete _cacheLoader[loader];
			
			App.log.info( "加载失败 : " + loader.loadObj.url , LogManager.LOG_ERROR , "QueueLoader" , "onIOError" );

			loader.loadObj.onField();
			var loadObj : LoadObj = cache[ loader.loadObj.url ];
//			cache[ loader.loadObj.url ] = null;
			delete cache[ loader.loadObj.url ];
			loader.dispose();
			loadObj.dispose();
			loadInWaitQueue();
		}

		/**
		 * 取消加载回调
		 */
		public function canelLoad ( url : String , fun : Function ) : void
		{
			//如果是图片集，去掉后缀，才能取消回调
			if(url.indexOf("atlas?") != -1){
				url = url.substr(0,url.lastIndexOf( "?" ));
			}
			if ( cache[ url ] != null && !(cache[ url ] as LoadObj).isComplete) 
			{
				var bool : Boolean = LoadObj( cache[ url ] ).canel( fun ); 
				if ( bool )
				{
					//只有在等待列队，才可以被注销，要是在已经加载的不能注销，等着回调完成之后在注销
					var i : int = waitQueue.indexOf( cache[ url ] )
					if ( i != -1 )
					{
						var loadObj:LoadObj = waitQueue.splice( i , 1 )[0];
						delete cache[ url ];
						loadObj.dispose();
					}
				}
				return;
			}else if(cache[ url ] != null && (cache[ url ] as LoadObj).isComplete){
				App.log.info( "不允许，在加载资源完成的回调函数，取消加载此的资源" , LogManager.LOG_WARN , "QueueLoader" , "canelLoad" );
			}
		}
	}
}
