package UI.abstract.tween
{
	import flash.events.EventDispatcher;

	public class Tween extends EventDispatcher implements IAnimatable
	{
		/** tween中需要保留属性 **/
		private static var _reservedProps : Object      =
			{
				ease: 1 , 		 	  //缓动函数
				delay: 1 , 		 	  //延迟时间
				repeat : 1,		 	  //重复次数
				repeatDelay : 1, 	  // 重复调用延迟
				onComplete: 1 , 	  //完成回调
				onCompleteParams: 1 , //完成回调参数
				onUpdate: 1 , 		  //更新属性回调
				onUpdateParams: 1 ,   //更新回调参数
				onStart: 1 , 		  //开始时回调
				onStartParams: 1 , 	  //开始回调参数
				onRepeat: 1 , 		  //重复执行回调
				onRepeatParams: 1 ,   //重复执行回调
				round: 1 ,			  //四舍五入
				yoyo: 1 ,			  //悠悠球模式
				dispose: 1 , 		  //自动清理
				nextTween: 1,		  //下一个执行的tween
				useFrames: 1 		  // 是否每帧刷新
			};
		private var _vars : Object;

		/** 目标对象 **/
		private var _target : Object;

		/** 渲染总时间 **/
		private var _totalTime : Number                 = 0;

		/** 当前渲染时间 **/
		private var _currentTime : Number               = 0;

		/** 延迟时间 **/
		private var _delay : Number                     = 0;

		/** 重复次数 **/
		private var _repeat : int                       = 0;

		/** 重复延迟时间 **/
		private var _repeatDelay : Number               = 0;

		/** 当前重复次数 **/
		private var _currentRepeat : int                = -1;

		/** 对象属性信息 **/
		private var _properties : Vector.<String>;

		private var _startsValues : Vector.<Number>;

		private var _endValues : Vector.<Number>;

		/** 缓动函数 **/
		private var _easeFunction : Function;

		private var _yoyo : Boolean;

		private var _nextTween : Tween;
		
		public function Tween ( target : Object , duration : Number , vars : Object )
		{
			init( target , duration , vars );
		}

		public function init ( target : Object , duration : Number , vars : Object ) : void
		{
			if ( _properties )
				_properties.length = 0;
			else
				_properties = new Vector.<String>();
			if ( _startsValues )
				_startsValues.length = 0;
			else
				_startsValues = new Vector.<Number>();
			if ( _endValues )
				_endValues.length = 0;
			else
				_endValues = new Vector.<Number>();

			_vars = {};
			_target = target;
			for ( var key : String in vars )
			{
				_vars[ key ] = vars[ key ];
				if ( !_reservedProps[ key ] )
				{
					_properties.push( key );
					_startsValues.push( Number.NaN );
					//if ( typeof( vars[ key ] ) == "number" )
						_endValues.push( vars[ key ] )
					//else
					//	_endValues.push( _target[ key ] + Number( vars[ key ] ) );
				}
			}
			_totalTime = Math.max( 0.0001 , duration );
			_delay = _vars.delay ? Number( _vars.delay ) : 0;
			_currentTime = -_delay;
			var easeFun : Function = EaseManager.getEase( _vars.ease );
			_easeFunction = easeFun != null ? easeFun : EaseManager.getEase( EaseManager.LINEAR );
			_repeat = _vars.repeat ? int( _vars.repeat ) : 0;
			_repeatDelay = _vars.repeatDelay ? Number( _vars.repeatDelay ) : 0;
			_currentRepeat = -1; 
			_yoyo = _vars.yoyo == 1 ? true : false;
			/*if ( _yoyo )
				_repeat = uint.MAX_VALUE;*/
			_nextTween = _vars.nextTween;
		}

		public function advanceTime ( time : Number ) : void
		{
			if ( time == 0 || ( _repeat == 0 && _currentTime == _totalTime ) )
				return;
			var preTime : Number       = _currentTime;
			var remainTime : Number    = _totalTime - _currentTime;
			var carryOverTime : Number = time > remainTime ? time - remainTime : 0.0;

			_currentTime = Math.min( _totalTime , _currentTime + time );

			if ( _currentTime <= 0 )
				return;

			if ( _currentRepeat < 0 && preTime <= 0 && _currentTime > 0 )
			{
				_currentRepeat++;
				if ( _vars.onStart )
					_vars.onStart.apply( null , _vars.onStartParams );
			}

			if ( !(_target is Function ) )
			{
				var ratio : Number        = _currentTime / _totalTime;
				var reversed : Boolean    = _yoyo && ( _currentRepeat % 2 == 1 );
				var numPros : int         = _startsValues.length;
	
				var startValue : Number   = 0;
				var endValue : Number     = 0;
				var delta : Number        = 0;
				var transValue : Number   = 0;
				var currentValue : Number = 0;
				for ( var i : int = 0 ; i < numPros ; ++i )
				{
					if ( isNaN( _startsValues[ i ] ) )
						_startsValues[ i ] = _target[ _properties[ i ] ] as Number;
	
					startValue = _startsValues[ i ];
					endValue = _endValues[ i ];
					delta = endValue - startValue;
					transValue = reversed ? _easeFunction( 1.0 - ratio ) : _easeFunction( ratio );
					currentValue = startValue + transValue * delta;
					if ( _vars.round )
						currentValue = Math.round( currentValue );
					_target[ _properties[i] ] = currentValue;
				}
			}

			if ( _vars.onUpdate )
				_vars.onUpdate.apply( null , _vars.onUpdateParams );

			if ( preTime < _totalTime && _currentTime >= _totalTime )
			{
				if ( _repeat == -1 || _repeat > 0)
				{
					_currentTime = -_repeatDelay;
					_currentRepeat++;
					if (_repeat > 0) _repeat--;
					if ( _vars.onRepeat )
						_vars.onRepeat.apply( null , _vars.onRepeatParams );
				}
				else
				{
					dispatchEvent( new TimeLineEvent( TimeLineEvent.REMOVE_FROM_TIMELINE ) );
					if ( _vars.onComplete )
						_vars.onComplete.apply( null , _vars.onCompleteParams );
					//放入池
					if ( isDispose )
						Tween.toPool( this );
					return;
				}
			}

			if ( carryOverTime )
				advanceTime( carryOverTime );
		}


		/**
		 * 是否完成
		 */
		public function get isComplete () : Boolean
		{
			if ( _repeat == 0 && _currentTime >= _totalTime )
				return true;
			if ( _repeat > 0 && _currentRepeat >= _repeat  )
				return true;
			return false;
		}

		/**
		 * 目标对象
		 */
		public function get target () : Object
		{
			return _target;
		}
		
		/**
		 * 是否帧刷新
		 */
		public function get useFrames () : Boolean
		{
			return _vars.useFrames;
		}

		/**
		 * 是否自动删除
		 */
		public function get isDispose () : Boolean
		{
			return _vars.dispose;
		}
		
		/**
		 * 下一个tween
		 */
		public function get nextTween () : Tween
		{
			return _nextTween;
		}

		/**
		 * 清理
		 */
		public function dispose () : void
		{
			_target = null;
			_vars = null;
			_totalTime = _currentTime = _delay = _repeat = _repeatDelay = 0;
			_currentRepeat = -1;
			_easeFunction = null;
			_startsValues.length = 0;
			_endValues.length = 0;
			_properties.length = 0;
			_yoyo = false;
			_nextTween = null;
		}

		/************** tween 池 *****************/
		private static var _tweensPool : Vector.<Tween> = new Vector.<Tween>();

		/** 从池中取出tween **/
		internal static function fromPool ( target : Object , duration : Number , vars : Object ) : Tween
		{
			var tween : Tween;
			if ( _tweensPool.length )
			{
				tween = _tweensPool.pop();
				tween.init( target , duration , vars );
			}
			else
				tween = new Tween( target , duration , vars );
			return tween;
		}

		/** 从池中取出tween **/
		internal static function toPool ( tween : Tween ) : void
		{
			tween.dispose();
			_tweensPool.push( tween );
		}
		
		/** 清理池 **/
		internal static function clearPool () : void
		{
			_tweensPool.length = 0;
		}
	}
}
