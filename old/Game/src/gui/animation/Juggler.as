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
    import flash.events.Event;
    import flash.events.EventDispatcher;

    /** xp已看完
	 *  The Juggler takes objects that implement IAnimatable (like Tweens) and executes them.
     * 
     *  <p>A juggler is a simple object. It does no more than saving a list of objects implementing 
     *  "IAnimatable" and advancing their time if it is told to do so (by calling its own 
     *  "advanceTime"-method). When an animation is completed, it throws it away.</p>
     *  
     *  <p>There is a default juggler available at the Starling class:</p>
     *  
     *  <pre>
     *  var juggler:Juggler = Starling.juggler;
     *  </pre>
     *  
     *  <p>You can create juggler objects yourself, just as well. That way, you can group 
     *  your game into logical components that handle their animations independently. All you have
     *  to do is call the "advanceTime" method on your custom juggler once per frame.</p>
     *  
     *  <p>Another handy feature of the juggler is the "delayCall"-method. Use it to 
     *  execute a function at a later time. Different to conventional approaches, the method
     *  will only be called when the juggler is advanced, giving you perfect control over the 
     *  call.</p>
     *  
     *  <pre>
     *  juggler.delayCall(object.removeFromParent, 1.0);
     *  juggler.delayCall(object.addChild, 2.0, theChild);
     *  juggler.delayCall(function():void { doSomethingFunny(); }, 3.0);
     *  </pre>
     * 
     *  @see Tween
     *  @see DelayedCall 
     */
    public class Juggler implements IAnimatable
    {
        private var mObjects:Vector.<IAnimatable>;
        private var mElapsedTime:Number;
        
        /** Create an empty juggler. */
        public function Juggler()
        {
            mElapsedTime = 0;
            mObjects = new <IAnimatable>[];
        }

        /** Adds an object to the juggler. */
		/** xp放入的这个对象必须是不在这个数组里，如果在了，就不再继续增加了，防止多次添加的错误
		    如果这个对象是事件对象，监听一个REMOVE_FROM_JUGGLER的事件（无回调）**/
        public function add(object:IAnimatable):void
        {
            if (object && mObjects.indexOf(object) == -1) 
            {
				mObjects[mObjects.length] = object;
            
                var dispatcher:EventDispatcher = object as EventDispatcher;
                if (dispatcher) dispatcher.addEventListener(AnEvent.REMOVE_FROM_JUGGLER, onRemove);
            }
        }
        
        /** Determines if an object has been added to the juggler. */
		/** xp判断是否这个对象（无回调）**/
        public function contains(object:IAnimatable):Boolean
        {
            return mObjects.indexOf(object) != -1;
        }
        
        /** Removes an object from the juggler. */
		/** xp移除一个对象，如果这个对象是事件对象就移除REMOVE_FROM_JUGGLER事件监听，然后把这个数组的这个对象位置置空,
			置空是有意义的，因为在调用动画时，可能会动态的增加动画，置空了，可以控制新加入的动画，在下一帧在做操作（无回调）
			如果是tween池里出来的，并且是主动调用，而不是事件调用，就把他回放到tween池**/
        public function remove(object:IAnimatable,event:Event = null):void
        {
            if (object == null) return;
            
            var dispatcher:EventDispatcher = object as EventDispatcher;
            if (dispatcher) dispatcher.removeEventListener(AnEvent.REMOVE_FROM_JUGGLER, onRemove);
			
			var tween:Tween = object as Tween;
			if(tween && event == null && tween.isPoolTween) onPooledTweenComplete(tween);
			var delayedCall:DelayedCall = object as DelayedCall;
			if(delayedCall && event == null && delayedCall.isPool) onPooledDelayedCallComplete(delayedCall);

            var index:int = mObjects.indexOf(object);
            if (index != -1) mObjects[index] = null;
        }
        
        /** Removes all tweens with a certain target. */
		/** xp移除这个对象的所有tween，tween肯定是事件类，所以移除REMOVE_FROM_JUGGLER事件（无回调）
			这个方法总觉得有问题，移除的时候，如果tween不是pool出来的，必须在外面手动施放，一定得注意**/
        public function removeTweens(target:Object):void
        {
            if (target == null) return;
            
            for (var i:int=mObjects.length-1; i>=0; --i)
            {
                var tween:Tween = mObjects[i] as Tween;
                if (tween && tween.target == target)
                {
                    tween.removeEventListener(AnEvent.REMOVE_FROM_JUGGLER, onRemove);
					//xp这个很安全的，如果是移除事件监听，也不会移除当前这次事件的监听
					if (tween && tween.isPoolTween) onPooledTweenComplete(tween);
                    mObjects[i] = null;
                }
            }
        }
        /** Figures out if the juggler contains one or more tweens with a certain target. */
        public function containsTweens(target:Object):Boolean
        {
            if (target == null) return false;
            
            for (var i:int=mObjects.length-1; i>=0; --i)
            {
                var tween:Tween = mObjects[i] as Tween;
                if (tween && tween.target == target) return true;
            }
            
            return false;
        }
        /** Removes all objects at once. */
		/** xp移除所有的动画，如果是事件类则删除REMOVE_FROM_JUGGLER事件
			这个方法压根就不用能用把，不然好多对象都不能被回收（X）**/
		/**
        public function purge():void
        {
            // the object vector is not purged right away, because if this method is called 
            // from an 'advanceTime' call, this would make the loop crash. Instead, the
            // vector is filled with 'null' values. They will be cleaned up on the next callalong the way
            // to 'advanceTime'.
            
            for (var i:int=mObjects.length-1; i>=0; --i)
            {
                var dispatcher:EventDispatcher = mObjects[i] as EventDispatcher;
                if (dispatcher) dispatcher.removeEventListener(Event.REMOVE_FROM_JUGGLER, onRemove);
				//xp这个很安全的，如果是移除事件监听，也不会移除当前这次事件的监听
				var tween:Tween = mObjects[i] as Tween;
				if (tween && tween.isPoolTween) onPooledTweenComplete(tween);
                mObjects[i] = null;
            }
        }
        **/
        /** Delays the execution of a function until a certain time has passed. Creates an
         *  object of type 'DelayedCall' internally and returns it. Remove that object
         *  from the juggler to cancel the function call. */
		/** xp delaycall 没问题 返回的这个DelayedCall，需要自己手动施法，调用DelayedCall的dispose就可以了
			这个delaycall 必须外面主动调用施放**/
		/*
        public function delayCall(call:Function, delay:Number, ...args):DelayedCall
        {
            if (call == null) return null;
            
            var delayedCall:DelayedCall = new DelayedCall(call, delay, args);
            add(delayedCall);
            return delayedCall;
        }
		*/
		 /** Delays the execution of a function until <code>delay</code> seconds have passed.
         *  This method provides a convenient alternative for creating and adding a DelayedCall
         *  manually.
         *
         *  <p>To cancel the call, pass the returned 'IAnimatable' instance to 'Juggler.remove()'.
         *  Do not use the returned IAnimatable otherwise; it is taken from a pool and will be
         *  reused.</p> */
        public function delayCall(call:Function, delay:Number, ...args):IAnimatable
        {
            if (call == null) return null;
            
            var delayedCall:DelayedCall = DelayedCall.fromPool(call, delay, args);
            //delayedCall.addEventListener(Event.REMOVE_FROM_JUGGLER, onPooledDelayedCallComplete);
            add(delayedCall);

            return delayedCall; 
        }

        /** Runs a function at a specified interval (in seconds). A 'repeatCount' of zero
         *  means that it runs indefinitely.
         *
         *  <p>To cancel the call, pass the returned 'IAnimatable' instance to 'Juggler.remove()'.
         *  Do not use the returned IAnimatable otherwise; it is taken from a pool and will be
         *  reused.</p> */
        public function repeatCall(call:Function, interval:Number, repeatCount:int=0, ...args):IAnimatable
        {
            if (call == null) return null;
            
            var delayedCall:DelayedCall = DelayedCall.fromPool(call, interval, args);
            delayedCall.repeatCount = repeatCount;
            //delayedCall.addEventListener(Event.REMOVE_FROM_JUGGLER, onPooledDelayedCallComplete);
            add(delayedCall);
            
            return delayedCall;
        }
        private function onPooledDelayedCallComplete(delayedCall:DelayedCall):void
        {
            DelayedCall.toPool(delayedCall);
        }
        /** Utilizes a tween to animate the target object over a certain time. Internally, this
         *  method uses a tween instance (taken from an object pool) that is added to the
         *  juggler right away. This method provides a convenient alternative for creating 
         *  and adding a tween manually.
         *  
         *  <p>Fill 'properties' with key-value pairs that describe both the 
         *  tween and the animation target. Here is an example:</p>
         *  
         *  <pre>
         *  juggler.tween(object, 2.0, {
         *      transition: Transitions.EASE_IN_OUT,
         *      delay: 20, // -> tween.delay = 20
         *      x: 50      // -> tween.animate("x", 50)
         *  });
         *  </pre> 
         */
		 /** xp使用tween对象池，创建tween，优先把所有属性先判断tween有没有，如果有算tween的，如果没有判断tween驱动的对象有没有，有则设置驱动对象往这个值改变，
			 然后监听REMOVE_FROM_JUGGLER用来回收tween，最后再加入动画列表（无回调）**/
        
		public function tween(target:Object, time:Number, properties:Object):IAnimatable
        {
            var tween:Tween = Tween.fromPool(target, time);
            
            for (var property:String in properties)
            {
                var value:Object = properties[property];
                
                if (tween.hasOwnProperty(property))
                    tween[property] = value;
                else if (target.hasOwnProperty(property))
                    tween.animate(property, value as Number);
                else
                    throw new ArgumentError("Invalid property: " + property);
            }
            
            //tween.addEventListener(Event.REMOVE_FROM_JUGGLER, onPooledTweenComplete);
            add(tween);
			return tween;
        }
        /** xp回收tween（无回调）**/
        private function onPooledTweenComplete(tween:Tween):void
        {
			Tween.toPool(tween);
        }
        
        /** Advances all objects by a certain time (in seconds). */
		/** xp 回调的时候添加没问题，在下一帧执行，移除的话也处理好了，只是置空，保证后续添加的在下一帧执行，如果在回调的时候，移除了这个列表里面的一个，如果他在
		    还没有执行的那段，就不会在执行了，这会不会有问题（？）**/
        public function advanceTime(time:Number):void
        {   
            var numObjects:int = mObjects.length;
            var currentIndex:int = 0;
            var i:int;
            
            mElapsedTime += time;
            if (numObjects == 0) return;
            
            // there is a high probability that the "advanceTime" function modifies the list 
            // of animatables. we must not process new objects right now (they will be processed
            // in the next frame), and we need to clean up any empty slots in the list.
            
			//xp把固定数量的动画，放到数组的前端，并执行，是空的全部放入后端
            for (i=0; i<numObjects; ++i)
            {
                var object:IAnimatable = mObjects[i];
                if (object)
                {
                    // shift objects into empty slots along the way
                    if (currentIndex != i) 
                    {
                        mObjects[currentIndex] = object;
                        mObjects[i] = null;
                    }
                    
                    object.advanceTime(time);
                    ++currentIndex;
                }
            }
            
            if (currentIndex != i)
            {
                numObjects = mObjects.length; // count might have changed!
                
                while (i < numObjects)
                    mObjects[int(currentIndex++)] = mObjects[int(i++)];
                
                mObjects.length = currentIndex;
            }
        }
        /** xp移除，如果是tween，并且这个tween完成了，执行下一个tween，如果发布REMOVE_FROM_JUGGLER事件，就会调用这个方法，并且执行下一个tween**/
        private function onRemove(event:Event):void
        {
            remove(event.target as IAnimatable,event);
            
            var tween:Tween = event.target as Tween;
			var delayedCall:DelayedCall = event.target as DelayedCall;
            if (tween && tween.isComplete)
                add(tween.nextTween);
			//xp等tween的nextTween放入，在回收tween
			if (tween && tween.isPoolTween) onPooledTweenComplete(tween);
			if (delayedCall && delayedCall.isPool) onPooledDelayedCallComplete(delayedCall);
			
        }
        
        /** The total life time of the juggler. */
        public function get elapsedTime():Number { return mElapsedTime; }
		/** The actual vector that contains all objects that are currently being animated. */
        protected function get objects():Vector.<IAnimatable> { return mObjects; }        
    }
}