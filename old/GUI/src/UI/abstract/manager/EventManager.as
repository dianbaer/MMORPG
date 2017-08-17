package UI.abstract.manager
{
	import UI.App;
	
	import flash.events.Event;
	import flash.events.IEventDispatcher;
	import flash.utils.Dictionary;
	import flash.utils.getQualifiedClassName;

	/**
	 * 事件管理器
	 */
	public class EventManager
	{
		/** 所有对象注册事件缓存列表 **/
		private var Dict : Dictionary = new Dictionary( true );

		/**
		 * 添加事件
		 * @param obj : 添加对象
		 * @param type : 事件类型
		 * @param callBackFun : 回调函数
		 * @param callBackFun : 回调函数参数(参数以数组的形式，数组这个容器返回时，不一定是原来的那个索引，里面的数值是正确的)
		 * @param eventParam : 是否返回事件参数，以第一个参数返回 默认true
		 * @param useCapture : 是否捕获 默认false
		 * @param priority : 使用优先级 默认0
		 * @param useWeakReference ： 弱引用 默认false
		 */
		public function addEvent ( obj : IEventDispatcher , type : String , callBackFun : Function , callBackParam : Array = null , eventParam : Boolean =
								   true , useCapture : Boolean = false , priority : int = 0 , useWeakReference : Boolean = false ) : void
		{
			// 如果该obj从未注册过
			if ( Dict[ obj ] == null )
			{
				Dict[ obj ] = {};
			}
			// 如果没注册过该事件
			if ( Dict[ obj ][ type ] == null )
			{
				Dict[ obj ][ type ] = [];
				obj.addEventListener( type , onEventCatch , useCapture , priority , useWeakReference );
			}
			var eventObj : EventObj = new EventObj( callBackFun , callBackParam , eventParam );

			// 同一事件注册了同一函数 覆盖原先的
			for ( var i : int = 0 ; i < Dict[ obj ][ type ].length ; i++ )
			{
				if ( EventObj( Dict[ obj ][ type ][ i ] ).fun == callBackFun )
				{
					EventObj( Dict[ obj ][ type ][ i ] ).dispose();
					Dict[ obj ][ type ][ i ] = eventObj;
//					trace( "同一事件相同函数被覆盖， 对象 -> " + obj + "  类型 -> " + type );
					App.log.info( "同一事件相同函数被覆盖 ==> 类型 ==> " + type , LogManager.LOG_WARN , getQualifiedClassName( obj ) , "addEvent" );
					return;
				}
			}

			Dict[ obj ][ type ].push( eventObj );
		}

		/**
		 * 移出事件
		 */
		public function removeEvent ( obj : IEventDispatcher , type : String , callBack : Function ) : void
		{
			if ( !Dict[ obj ] || !Dict[ obj ][ type ] )
			{
				App.log.info( "移出了一个不存在的事件 ==> 类型 ==> " + type , LogManager.LOG_WARN , getQualifiedClassName( obj ) , "removeEvent" );
				return;
			}
			// 取出 此侦听器 所有的侦听函数
			var arr : Array = Dict[ obj ][ type ];

			for each ( var item : EventObj in arr )
			{
				if ( item.fun == callBack )
				{
					Dict[ obj ][ type ].splice( Dict[ obj ][ type ].indexOf( item ) , 1 );
					item.dispose();
					break;
				}
			}
			
			// 如果obj的这种侦听已经没有，则删除
			if ( Dict[ obj ][ type ].length == 0 )
			{
				obj.removeEventListener( type , onEventCatch );
				delete Dict[ obj ][ type ];

				// 检测是否还有侦听器
				var n : int = 0;
				for each ( var str : String in Dict[ obj ] )
					n++;
				if ( n == 0 )
				{
					Dict[ obj ] = null;
					delete Dict[ obj ];
				}
			}


		}

		/**
		 * 删除一个obj的所有type指定类型的事件
		 */
		public function removeEventByType ( obj : IEventDispatcher , type : String ) : void
		{
			obj.removeEventListener( type , onEventCatch );
			for each ( var item : EventObj in Dict[ obj ][ type ] )
				item.dispose();
			Dict[ obj ][ type ] = null;
			delete Dict[ obj ][ type ];

			// 检测是否还有侦听器
			var n : int = 0;
			for each ( var str : String in Dict[ obj ] )
				n++;
			if ( n == 0 )
			{
				Dict[ obj ] = null;
				delete Dict[ obj ];
			}
		}

		/**
		 *  删除一个obj所有的事件
		 */
		public function removeEventByObj ( obj : IEventDispatcher ) : void
		{
			var c : int = 0;
			for ( var type : String in Dict[ obj ] )
			{
				obj.removeEventListener( type , onEventCatch );
				for each ( var item : EventObj in Dict[ obj ][ type ] )
					item.dispose();
				Dict[ obj ][ type ] = null;
				c++;
//				App.log.info( "清理事件类型 ==> " + type , LogManager.LOG_NORMAL , getQualifiedClassName( obj ) , "removeEventByObj" );
//				trace( "清理对象 -> " + obj + " -> 事件类型:", type );
			}
			if ( c )
				App.log.info( "清理该对象所有事件侦听器数目 ==> " + c , LogManager.LOG_NORMAL , getQualifiedClassName( obj ) , "removeEventByObj" , 1 );
			Dict[ obj ] = null;
			delete Dict[ obj ];
		}

		/**
		 *  执行事件
		 */
		private function onEventCatch ( e : Event ) : void
		{
			if ( !Dict[ e.currentTarget ] || !Dict[ e.currentTarget ][ e.type ] )
			{
				return;
			}
			var arr : Array = Dict[ e.currentTarget ][ e.type ].concat();

			for each ( var item : EventObj in arr )
			{
				if ( item.eventParam )
				{
					//(参数以数组的形式，数组这个容器返回时，不一定是原来的那个索引，里面的数值是正确的)
					var param : Array = item.param ? item.param.concat() : [];
					param.unshift( e );
					item.fun.apply( null , param );
				}
				else
					item.fun.apply( null , item.param );
			}
		}

		/**
		 *  判断一个侦听存在与否
		 */
		public function hasEvent ( obj : IEventDispatcher , type : String ) : Boolean
		{
			if ( !Dict[ obj ] || !Dict[ obj ][ type ] )
				return false;
//			for each (var item:EventObj in Dict[ obj ][ type ]) 
//			{
//				if ( item.fun == fn )
//					return true;
//			}
			return true;
		}

		public function hasEventFun ( obj : IEventDispatcher , type : String , fun : Function ) : Boolean
		{
			if ( !Dict[ obj ] || !Dict[ obj ][ type ] )
				return false;
			var arr : Array = Dict[ obj ][ type ];
			for each ( var item : EventObj in arr )
			{
				if ( item.fun == fun )
					return true;
			}
			return false;
		}



		/**
		 * 调试器 查看事件数量
		 */
		public function traceDebugInfo ( all : Boolean = false ) : void
		{
			var fnCount : int    = 0;
			var eventCount : int = 0;
			var objCount : int   = 0;
			var num : int = 0;
			for ( var obj : Object in Dict )
			{
				num = 0;
				if ( all )
					trace( "\n***** 事件对象：" , obj , " *****" );
				objCount++;
				for ( var type : String in Dict[ obj ] )
				{
					if ( all )
						trace( "事件类型：" + type , "  事件数量：" , Dict[ obj ][ type ].length );
					eventCount++;
					fnCount += Dict[ obj ][ type ].length;
					num+=Dict[ obj ][ type ].length;
				}
				trace("该对象事件数 ==> " + num)
			}
			trace( "\nEventManager -> 统计 -> 侦听目标：" + objCount + " -> 侦听器：" + eventCount + " -> 事件数：" + fnCount );
		}

	}
}

class EventObj
{
	public var fun : Function;

	public var param : Array;

	public var eventParam : Boolean;

	public function EventObj ( fun : Function , param : Array , eventParam : Boolean )
	{
		this.fun = fun;
		this.param = param;
		this.eventParam = eventParam;
	}

	public function dispose () : void
	{
		this.fun = null;
		this.param = null;
	}
}
