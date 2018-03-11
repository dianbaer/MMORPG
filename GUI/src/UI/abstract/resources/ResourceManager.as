package UI.abstract.resources
{
	import flash.display.Shape;
	import flash.events.Event;
	import flash.utils.Dictionary;
	import flash.utils.getTimer;
	
	import UI.App;
	import UI.abstract.manager.LogManager;
	import UI.abstract.resources.item.ImageAtlasResource;
	import UI.abstract.resources.item.ImageResource;
	import UI.abstract.resources.item.Resource;
	import UI.abstract.resources.loader.MultiLoader;
	import UI.abstract.resources.loader.QueueLoader;
	import UI.abstract.utils.CommonPool;
	import UI.abstract.utils.GameUtils;

	public class ResourceManager
	{
		/** 资源URL前缀 **/
		public static var resourcePrefixURL : String = "asset/";

		/** 队列加载器 **/
		private var loaderQueue : QueueLoader;
		
		private var loaderQueueTwo : QueueLoader;

		/**资源字典**/
		private var resDict : Dictionary             = new Dictionary();

		/** 资源管理时间 **/
		public static var currentTime : uint         = 0;

		/** 一次回收资源最大数目 **/
		public var resourceRecycleMax : int          = 10;

		/** 回收资源执行时间间隔 帧数 **/
		public var resourceRecycleInterva : int      = 30;

		/** 上次资源未回收完全 再次回收间隔 帧数 **/
		public var resourceAgainRecycleInterva : int = 5;

		/** 资源缺省生命周期 **/
		public var resourceRecycleLife : int         = 500;

		/** 最小GC时间 **/
		public var minGCTimeInterval : int           = 300;

		/** 最小GC内存 **/
		public var minGCMem : int                    = 800;

		/** 跳帧数 **/
		private var _elapse : int                    = 0;

		/** 上一次GC时间 **/
		private var _prevGC : int                    = 0;

		/** 添加帧事件 **/
		private static var _shape : Shape  = new Shape();

		/** 多文件加载器 缓存 **/
		private var _dictLoadList : Dictionary       = new Dictionary();
		
		/***暂时存储当前的多文件加载**/
		private var multiLoader:MultiLoader
		public function ResourceManager ()
		{
//			if (_instance != null) throw Error(SINGLETON_MSG);
//			_instance = this;
			init();
		}

		private function init () : void
		{
			loaderQueue = new QueueLoader( this );
			loaderQueueTwo = new QueueLoader( this );
			_elapse = resourceRecycleInterva;
			_shape.addEventListener( Event.ENTER_FRAME , recycleResources );
		}

		/**
		 * 自动回收资源
		 */
		public function recycleResources ( e : Event ) : void
		{
			currentTime = getTimer();
			_elapse--;
			if ( _elapse >= 0 )
				return;
//			App.log.info( "开始自动回收资源。。。" , LogManager.LOG_NORMAL , "ResourceManager" , "recycleResources" );
			_elapse = resourceRecycleInterva;
			var processNum : int = resourceRecycleMax;
			var num : int        = 0;
			// 回收超时资源
			for each ( var res : Resource in resDict )
			{
				if ( processNum < 0 )
				{
					_elapse = resourceAgainRecycleInterva;
					return;
				}
				if ( res.isLocked )
					continue;
				if ( res.useNumber > 0 )
					continue;
				if ( ( currentTime - res.accessTime ) * 0.001 > resourceRecycleLife )
				{
					processNum--;
					removeResource( res.url );
					App.log.info( "移出资源：" + res.url , LogManager.LOG_NORMAL , "ResourceManager" , "recycleResources" );
					num++;
				}
			}
			if ( num )
				App.log.info( "自动回收资源卸载数量：" + num , LogManager.LOG_NORMAL , "ResourceManager" , "recycleResources" );

			// 内存过大判断是否GC
			var mem : Number = GameUtils.currMem;
			if ( mem > minGCMem && ( currentTime - _prevGC ) * 0.001 > minGCTimeInterval )
			{
				GameUtils.GC();
				_prevGC = currentTime;
			}
		}

		/**
		 * 加载
		 * @param obj 参数定义：
		 * @param onComplete ： 加载完成 ( 参数为resource ）
		 * @param onField : 加载失败 （ 无参数 ）
		 * 如果有atlas?后缀加参数就是图片集，后面的参数会转换成propName与prop 属性名与属性值
		 * 如果加载jta的某一张图片jtas，后面的类型type需要传2，因为jta内部加载jtas就是走的queue2的队列，如果传1应该第一次得不到这个资源，回来测试一下,测完了必须传2
		 */
		public function load ( url : String , onComplete : Function , obj : Object = null ,type:int = 1) : void
		{
			if ( !obj ){
				obj = CommonPool.fromPoolObject();
			}
			if ( onComplete != null )
				obj.onComplete = onComplete;

			var resourceName : String = formatResourceName( url );
			var res : Resource        = resDict[ resourceName ];

			// 资源已加载完成
			if ( res )
			{
				if ( obj.onComplete )
				{
					obj.onComplete.apply( null , [ res ] );
				}
				else
				{
					trace( "无onComplete" );
				}
				return;
			}
			else if(resourceName.indexOf("atlas?") != -1)
			{
				//路径
				var newResourceName:String = resourceName.substr(0,resourceName.lastIndexOf( "?" ));
				//参数
				var props:String = resourceName.substr(resourceName.lastIndexOf( "?" )+1);
				//分解参数名与参数值
				var array:Array = props.split("=");
				//获取图片集资源
				var parentRes:ImageAtlasResource = resDict[newResourceName] as ImageAtlasResource;
				//存在
				if(parentRes){
					if ( obj.onComplete )
					{
						switch(array[0]){
							case ResourceUtil.IMAGE:
								obj.onComplete.apply( null , [ parentRes.getTexture(array[1]) ] );
								break;
							case ResourceUtil.MC:
								obj.onComplete.apply( null , [ parentRes.getTextures(array[1]) ] );
								break;
							default:
								obj.onComplete.apply( null , [ null ] );
								break;
						}
					}
					else
					{
						trace( "无onComplete" );
					}
					return;
				}else{
					resourceName = newResourceName;
					//参数名与参数值存入回调对象
					obj.propName = array[0];
					obj.prop = array[1];
				}
				
			//这块会创建多个加载回调，虽然没有回调函数，但是还是消耗了资源（已解决，没有回调函数，不放入回调函数数组）
			}else if(resourceName.indexOf("jtas?") != -1){
				var newResourceName1:String = resourceName.substr(0,resourceName.lastIndexOf( "jtas?" ));
				//加载jtas肯定需要先加载jta，回调设为空
				load(unFormatResourceName(newResourceName1+"jta"),null);
			}

			// 添加到加载队列
			obj.url = resourceName;
			if(type == 1){
				loaderQueue.append( obj );
			}else{
				loaderQueueTwo.append( obj );
			}
			
		}

		/**
		 * 加载数组
		 * onCompleteParam
		 */
		public function loadList ( urlList : Array , onComplete : Function , obj : Object = null,isAutoLoad:Boolean = true ) : MultiLoader
		{
			if ( _dictLoadList[ onComplete ] ) 
			{
				App.log.info("多文件加载器回调函数重复",LogManager.LOG_ERROR, "ResourceManager", "loadList" );
				return null;
			}
			if ( !obj )
				obj = {};
			if ( onComplete != null )
				obj.onComplete = onComplete;
			multiLoader = MultiLoader.fromPool( this , urlList , obj );
			_dictLoadList[ onComplete ] = multiLoader;
			//是否自动加载，如果不是就是在外部调用
			if(isAutoLoad){
				multiLoader.load();
			}
			multiLoader = null;
			return _dictLoadList[ onComplete ];
		}

		/**
		 * 增加一个资源
		 */
		public function addResource ( url : String , resource : Resource ) : void
		{
			var resourceName : String = formatResourceName( url );
			if ( !resourceName || !resource || resDict[ resourceName ] ){
				App.log.info("重复增加资源",LogManager.LOG_ERROR, "ResourceManager", "addResource" );
				return;
			}
			resDict[ resourceName ] = resource;
		}

		/**
		 * 取得资源
		 */
		public function getResource ( url : String ) : Resource
		{
			var resourceName : String = formatResourceName( url );
			return resDict[ resourceName ];
		}

		/**
		 * 移除资源
		 */
		public function removeResource ( url : String ) : void
		{
			var resourceName : String = formatResourceName( url );
			if ( resDict[ resourceName ] is Resource )
				Resource( resDict[ resourceName ] ).dispose();
			delete resDict[ resourceName ];
		}

		/**
		 * 取消加载回调(目前图片集不能取消，之后修改)
		 */
		public function canelLoad ( url : String , fun : Function ) : void
		{
			var resourceName : String = formatResourceName( url );
			loaderQueue.canelLoad( resourceName , fun );
		}

		/**
		 * 对文件加载完成回调
		 */
		public function loadListOnComplete ( fun : Function ) : void
		{
			if ( _dictLoadList[ fun ] )
				delete _dictLoadList[ fun ];
		}

		/**
		 * 取消对文件加载回调
		 */
		public function canelLoadList ( fun : Function ) : void
		{
			var load : MultiLoader = _dictLoadList[ fun ];
			if ( load && !load.isComplete)
			{
				load.canel();
				delete _dictLoadList[ fun ];
			}else if(load && load.isComplete){
				App.log.info( "不允许，在加载资源完成的回调函数，取消加载此的资源" , LogManager.LOG_WARN , "ResourceManager" , "canelLoadList" );
			}
		}

		/**
		 * 格式化资源地址
		 */
		public static function formatResourceName ( url : String ) : String
		{
			return resourcePrefixURL + url;
		}

		/**
		 * 反格式化资源地址
		 */
		public static function unFormatResourceName ( url : String ) : String
		{
			return url.replace( resourcePrefixURL , "" );
		}

		public function addUseNumber ( res : Resource ) : void
		{
			res.useNumber++;
			if(res is ImageResource){
				if((res as ImageResource).parent){
					(res as ImageResource).parent.useNumber++;
				}
				
			}
		}

		public function subtractUseNumber ( res : Resource ) : void
		{
			res.useNumber--;
			if ( res.useNumber == 0 )
				res.updateTime();
			if(res is ImageResource){
				if((res as ImageResource).parent){
					(res as ImageResource).parent.useNumber--;
					if((res as ImageResource).parent.useNumber == 0){
						(res as ImageResource).parent.updateTime();
					}
				}
			}
		}

		//不看
//		public static function get instance () : ResourceManager
//		{
//			if ( _instance == null )
//				_instance = new ResourceManager();
//			return _instance;
//		}
//
//		protected static var _instance : ResourceManager;
//
//		protected const SINGLETON_MSG : String       = "ResourceManager Singleton already constructed!";
	}
}
