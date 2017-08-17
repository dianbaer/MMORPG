package UI.abstract.tween
{
	import flash.display.Shape;
	import flash.events.Event;
	import flash.utils.getTimer;

	/**
	 * tween 时间管理器
	 * 所有时间都用秒
	 *  参数设置
		<li> ease 				缓动函数 </li>
		<li> delay 			 	延迟时间 </li>
		<li> repeat 			重复次数 </li>
		<li> repeatDelay 		重复调用延迟 </li>
		<li> onComplete:		完成回调 </li>
		<li> onCompleteParams:	完成回调参数 </li>
		<li> onUpdate: 			更新属性回调 </li>
		<li> onUpdateParams: 	更新回调参数 </li>
		<li> onStart:			开始时回调 </li>
		<li> onStartParams: 	开始回调参数 </li>
		<li> onRepeat:			重复执行回调 </li>
		<li> onRepeatParams: 	重复执行回调 </li>
		<li> round: 			四舍五入 </li>
		<li> yoyo: 				悠悠球模式 </li>
		<li> dispose: 			自动清理 </li>
		<li> nextTween: 		下一个执行的tween </li>
		<li> useFrames: 		是否每帧刷新 </li>
	 */
	public class TweenManager
	{
		private static var _shape : Shape      = new Shape();

		private static var _isInit : Boolean;

		/** 当前时间 **/
		public static var processTime : uint;

		/** 当前帧 **/
		public static var rootFrames : uint;

		/** 按时间变化 **/
		private static var _rootTimeLine : TimeLine;

		/** 按帧变化 **/
		private static var _rootFramesTimeLine : TimeLine;

		/** 定时清理缓存时间 (秒) **/
		private static const _clearTime : uint = 600;

		public function TweenManager ()
		{
		}

		/**
		 * 静态初始化
		 */
		public static function initClass () : void
		{
			if ( _isInit )
				return;
			_isInit = true;
			processTime = getTimer();
			rootFrames = 0;
			_rootTimeLine = new TimeLine();
			_rootFramesTimeLine = new TimeLine();
			_shape.addEventListener( Event.ENTER_FRAME , updateAll , false , 0 , false );
		}

		/**
		 * 增加一个tween
		 */
		public static function add ( tween : Tween ) : void
		{
			if ( tween.useFrames )
				_rootFramesTimeLine.add( tween );
			else
				_rootTimeLine.add( tween );
		}

		/**
		 * 移出一个tween
		 */
		public static function remove ( tween : Tween ) : void
		{
			_rootTimeLine.remove( tween );
			_rootFramesTimeLine.remove( tween );
		}

		/**
		 * 将一个非tween的对象加入时间线
		 */
		public static function addIAnimatableObj(object : IAnimatable):void{
			_rootTimeLine.add( object );
		}
		/**
		 * 将一个非tween的对象移除时间线
		 */
		public static function removeIAnimatableObj(object : IAnimatable):void{
			_rootTimeLine.remove( object );
		}
		/**
		 * 将一个目标从现有状态缓动到指定状态
		 *
		 * @param target 指定目标对象
		 * @param duration 变化时间长度
		 * @param vars 变化参数
		 */
		public static function to ( target : Object , duration : Number , vars : Object ) : void
		{
			vars.dispose = true;
			var tween : Tween = Tween.fromPool( target , duration , vars );
			add( tween );

		}

		/**
		 * 延迟调用
		 *
		 * @param delay 需要延迟的时间/帧数
		 * @param onComplete 回调函数
		 * @param onCompleteParams 回调函数参数
		 * @param useFrames 是否使用帧数
		 */
		public static function delayedCall ( delay : Number , onComplete : Function , onCompleteParams : Array = null , useFrames : Boolean = false ) : void
		{
			var tween : Tween = Tween.fromPool( onComplete , 0 , { delay: delay , onComplete: onComplete , onCompleteParams: onCompleteParams , useFrames: useFrames ,
													dispose: true } );
			add( tween );
		}

		/**
		 * 定时调用
		 *
		 * @param interval 定时的时间/帧数
		 * @param onInterval 调用函数
		 * @param onIntervalParams 调用函数参数
		 * @param useFrames 是否使用帧数
		 */
		public static function intervalCall ( interval : Number , onInterval : Function , onIntervalParams : Array = null , useFrames : Boolean = false ) : void
		{
			var tween : Tween = Tween.fromPool( onInterval , interval , { repeat: -1 , onComplete: onInterval , onCompleteParams: onInterval , onRepeat: onInterval ,
													onRepeatParams: onIntervalParams ,
													useFrames: useFrames , dispose: true } );
			add( tween )
		}

		/**
		 * 每帧调用
		 * @param onFrame 调用函数
		 * @param onFrameParams 调用函数参数
		 */
		public static function frameCall ( onFrame : Function , onFrameParams : Array = null ) : void
		{
			intervalCall( 1 , onFrame , onFrameParams , true );
		}

		/**
		 * 判断一个对象是否正在被tween激活中
		 */
		public static function isTweening ( target : Object ) : Boolean
		{
			if ( _rootTimeLine.isTweeningToTarget( target ) )
				return true;
			if ( _rootFramesTimeLine.isTweeningToTarget( target ) )
				return true;
			return false;
		}

		/**
		 * 更新所有Tween
		 */
		public static function updateAll ( event : Event ) : void
		{
			var lastValue : uint = processTime;
			processTime = getTimer();
			_rootTimeLine.advanceTime( ( processTime - lastValue ) / 1000.0 );
			rootFrames += 1;
			_rootFramesTimeLine.advanceTime( 1 );

			/*if ( !( processTime % ( _clearTime * 1000 ) ) )
				Tween.clearPool();*/
		}
		public static function clearTween():void{
			Tween.clearPool();
		}
		/**
		 * 清楚目标所有tween
		 */
		public static function killTweensOf ( target : Object ) : void
		{
			_rootTimeLine.removeTweensToTarget( target );
			_rootFramesTimeLine.removeTweensToTarget( target );
		}

		/**
		 * 清楚所有tween
		 */
		public static function killAll () : void
		{
			_rootTimeLine.purge();
			_rootFramesTimeLine.purge();
		}

	}
}
