// =================================================================================================
//
//	Starling Framework
//	Copyright 2011 Gamua OG. All Rights Reserved.
//
//	This program is free software. You can redistribute and/or modify it
//	in accordance with the terms of the accompanying license agreement.
//
// =================================================================================================

package gui.animation
{
	import flash.events.EventDispatcher;

    /** xp已看完
	 *   主要注意的是，这个对象并不会被注销，需要保留这个对象的引用，然后注销了，在删除引用
	 *	A DelayedCall allows you to execute a method after a certain time has passed. Since it 
     *  implements the IAnimatable interface, it can be added to a juggler. In most cases, you 
     *  do not have to use this class directly; the juggler class contains a method to delay
     *  calls directly. 
     * 
     *  <p>DelayedCall dispatches an Event of type 'Event.REMOVE_FROM_JUGGLER' when it is finished,
     *  so that the juggler automatically removes it when its no longer needed.</p>
     * 
     *  @see Juggler
     */ 
    public class DelayedCall extends EventDispatcher implements IAnimatable
    {
        private var mCurrentTime:Number;
        private var mTotalTime:Number;
        private var mCall:Function;
        private var mArgs:Array;
        private var mRepeatCount:int;
		//xp增加的
		public var isPool:Boolean = false;
        
        /** Creates a delayed call. */
        public function DelayedCall(call:Function, delay:Number, args:Array=null, isPool:Boolean = false)
        {
            reset(call, delay, args, isPool);
        }
        
        /** Resets the delayed call to its default values, which is useful for pooling. */
		/** xp默认是一次，如果不用默认值，需要new对象然后修改次数，如果是0次，就说明无限回调，0.0001的限制比这个小的话就用0.00001不能等于0
			如果传0，那就等于下一帧调用**/
        public function reset(call:Function, delay:Number, args:Array=null, isPool:Boolean = false):DelayedCall
        {
			this.isPool = isPool;
            mCurrentTime = 0;
            mTotalTime = Math.max(delay, 0.0001);
            mCall = call;
            mArgs = args;
            mRepeatCount = 1;
            
            return this;
        }
        
        /** @inheritDoc */
        public function advanceTime(time:Number):void
        {
            var previousTime:Number = mCurrentTime;
            mCurrentTime = Math.min(mTotalTime, mCurrentTime + time);
            
			//xp上一次时间小于总时间，这个是为了确实有过时间差，第二个条件，如果正确肯定是等于，不可能大于
			//不可能有大于的情况，所以把大于去了
			//上一次时间必须小于总时间，并且这一次时间必须大于等于总时间，这个是动画的必须条件，概念上这才叫有时间的动画
            if (previousTime < mTotalTime && mCurrentTime == mTotalTime)
            {                
                
                
                if (mRepeatCount == 0 || mRepeatCount > 1)
                {
					
                    if (mRepeatCount > 0) mRepeatCount -= 1;
                    mCurrentTime = 0;
					//xp回调放下面点比较好吧，这样如果修改repeatCount的时候，不至于还被这里置回去
					mCall.apply(null, mArgs);
					//xp精确一点时间都不浪费
                    advanceTime((previousTime + time) - mTotalTime);
                }
                else
                {	
					// save call & args: they might be changed through an event listener
					//xp保存回调的函数和参数
					var call:Function = mCall;
                    var args:Array = mArgs;
					// in the callback, people might want to call "reset" and re-add it to the
                    // juggler; so this event has to be dispatched *before* executing 'call'.
					//先发事件，再回调，不然juggler可能无法清除这个delayedcall
                    dispatchEvent(new AnEvent(AnEvent.REMOVE_FROM_JUGGLER));
					
					call.apply(null, args);
                }
            }
        }
		//xp注销，传进来的，不能这里修改需要在外面清空
        public function dispose():void{
			mCall = null;
			mArgs = null;
			//removeEventListeners();
		}
        /** Indicates if enough time has passed, and the call has already been executed. */
		/** xp没问题，因为如果是1的话，就不会再减1了，并且mCurrentTime也不会再改变了**/
        public function get isComplete():Boolean 
        { 
			//不可能有大于的情况，所以把大于去了
            return mRepeatCount == 1 && mCurrentTime == mTotalTime; 
        }
        
        /** The time for which calls will be delayed (in seconds). */
        public function get totalTime():Number { return mTotalTime; }
        
        /** The time that has already passed (in seconds). */
        public function get currentTime():Number { return mCurrentTime; }
        
        /** The number of times the call will be repeated. 
         *  Set to '0' to repeat indefinitely. @default 1 */
        public function get repeatCount():int { return mRepeatCount; }
        public function set repeatCount(value:int):void { mRepeatCount = value; }
		// delayed call pooling
        
        private static var sPool:Vector.<DelayedCall> = new <DelayedCall>[];
        
        /** @private */
        public static function fromPool(call:Function, delay:Number, 
                                                   args:Array=null):DelayedCall
        {
            if (sPool.length) return sPool.pop().reset(call, delay, args, true);
            else return new DelayedCall(call, delay, args, true);
        }
        
        /** @private */
		public static function toPool(delayedCall:DelayedCall):void
        {
            // reset any object-references, to make sure we don't prevent any garbage collection
            delayedCall.mCall = null;
            delayedCall.mArgs = null;
            //delayedCall.removeEventListeners();
			sPool[sPool.length] = delayedCall;
        }
    }
}