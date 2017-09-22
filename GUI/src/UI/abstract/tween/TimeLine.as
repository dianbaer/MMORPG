package UI.abstract.tween
{
	import flash.events.EventDispatcher;

	public class TimeLine
	{
		/** 所有IAnimatable对象 **/
		private var _objects : Vector.<IAnimatable>;

		/** 流失时间 **/
		private var _elapsedTime : Number;

		public function TimeLine ()
		{
			_elapsedTime = 0;
			_objects = new Vector.<IAnimatable>();
		}


		/**
		 * 向TimeLine添加一个对象.
		 */
		public function add ( object : IAnimatable ) : void
		{
			if ( object && _objects.indexOf( object ) == -1 )
			{
				_objects.push( object );

				var dispatcher : EventDispatcher = object as EventDispatcher;
				if ( dispatcher )
					dispatcher.addEventListener( TimeLineEvent.REMOVE_FROM_TIMELINE , onRemove );
			}
		}

		/**
		 * 从TimeLine移出一个对象.
		 */
		public function remove ( object : IAnimatable ) : void
		{
			if ( object == null || !contains( object ) )
				return;

			var dispatcher : EventDispatcher = object as EventDispatcher;
			if ( dispatcher )
				dispatcher.removeEventListener( TimeLineEvent.REMOVE_FROM_TIMELINE , onRemove );

			var index : int = _objects.indexOf( object );
			if ( index != -1 )
				_objects[ index ] = null;
		}

		/**
		 * 判断TimeLine是否持有对这个对象的引用
		 */
		public function contains ( object : IAnimatable ) : Boolean
		{
			return _objects.indexOf( object ) != -1;
		}

		public function advanceTime ( time : Number ) : void
		{
			var len : int       = _objects.length;
			var currIndex : int = 0;

			_elapsedTime += time;
			if ( len == 0 )
				return;

			var obj : IAnimatable;
			for ( var i : int = 0 ; i < len ; ++i )
			{
				obj = _objects[ i ];
				if ( obj )
				{
					if ( currIndex != i )
					{
						_objects[ currIndex ] = obj;
						_objects[ i ] = null;
					}

					obj.advanceTime( time );
					++currIndex;
				}
			}

			if ( currIndex != i )
			{
				len = _objects.length;

				// 防止删除新增项
				while ( i < len )
					_objects[ currIndex++ ] = _objects[ i++ ];

				_objects.length = currIndex;
			}
		}

		/**
		 * 删除一个对象上应用的所有的tween对象
		 */
		public function removeTweensToTarget ( target : Object ) : void
		{
			if ( target == null )
				return;
			for ( var i : int = _objects.length - 1 ; i >= 0 ; --i )
			{
				var tween : Tween = _objects[ i ] as Tween;
				if ( tween && tween.target == target )
				{
					tween.removeEventListener(TimeLineEvent.REMOVE_FROM_TIMELINE, onRemove);
					_objects[i] = null;
					//放入池
					tween.isDispose && Tween.toPool( tween );
				}
			}
		}

		/**
		 * 判断一个对象是否正在被tween激活中
		 */
		public function isTweeningToTarget ( target : Object ) : Boolean
		{
			if ( target == null )
				return false;
			for ( var i : int = _objects.length - 1 ; i >= 0 ; --i )
			{
				var tween : Tween = _objects[ i ] as Tween;
				if ( tween && tween.target == target )
					return true;
			}
			return false;
		}

		/**
		 * 一次性删除所有的对象
		 */
		public function purge () : void
		{
			for ( var i : int = _objects.length - 1 ; i >= 0 ; --i )
			{
				var dispatcher : EventDispatcher = _objects[ i ] as EventDispatcher;
				if ( dispatcher )
					dispatcher.removeEventListener( TimeLineEvent.REMOVE_FROM_TIMELINE , onRemove );
				
				//放入池
				var tween : Tween = _objects[ i ] as Tween;
				tween.isDispose && Tween.toPool( tween );
				
				_objects[ i ] = null;
			}
			_objects.length = 0;
		}

		private function onRemove ( event : TimeLineEvent ) : void
		{
			remove( event.target as IAnimatable )

			var tween : Tween = event.target as Tween;
			
			if ( tween && tween.isComplete && tween.nextTween )
				add( tween.nextTween );
		}
	}
}
