package gui.mc
{
	import flash.display.Bitmap;
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.geom.Rectangle;
	import flash.media.Sound;
	
	import UI.App;
	import UI.abstract.component.event.UIEvent;
	import UI.abstract.resources.AnCategory;
	import UI.abstract.resources.ResourceUtil;
	import UI.abstract.resources.item.ImageResource;
	import UI.abstract.resources.item.JtaResource;
	
	import gui.animation.IAnimatable;
	import gui.animation.JugglerManager;
	
	
	public class Animation extends Sprite implements IAnimatable
	{
		private var mTextures:Array;
		private var mSounds:Vector.<Sound>;
		private var mDurations:Vector.<Number>;
		private var mStartTimes:Vector.<Number>;
		
		private var mDefaultFrameDuration:Number;
		//private var mTotalTime:Number;
		private var mCurrentTime:Number;
		private var mCurrentFrame:int;
		private var mLoop:Boolean;
		private var mPlaying:Boolean;
		
		public var _res : JtaResource;
		protected var _url : String;
		public var _bitmap : Bitmap;
		
		private var _dir:int = 1;
		/**
		 * 动作
		 */
		private var _action:int = 0;
		/**
		 * 类型
		 */
		private var _type:String;
		/**
		 * 名字
		 */
		private var _jtaName:String;
		//这个是总时间
		private var _fps:Number = 1.0;
		//private var initAnimation:Boolean = false;
		public var isDispose:Boolean = false;
		public var data:Object;
		public function Animation(type:String,jtaName:String,action:int,loop:Boolean,allTime:Number,dir:int)
		{
			_bitmap = new Bitmap();
			addChild( _bitmap );
			data = new Object();
			reset(type,jtaName,action,loop,allTime,dir);
		}
		 public function reset(type:String,jtaName:String,action:int,loop:Boolean,allTime:Number,dir:int):Animation{
			_type = type;
			_jtaName = jtaName;
			_action = action;
			mLoop = loop;
			_fps = allTime;
			_dir = dir;
			
			//trace(_dir);
			isDispose = false;
			JugglerManager.oneuggler.add(this);
			//TweenManager.addIAnimatableObj(this);
			play();
			url = ResourceUtil.getAnimationURL(_type,_jtaName,_action);
			return this;
		 }
		/**
		 * 方向
		 */
		public function get dir():int
		{
			return _dir;
		}

		public function set url ( str : String ) : void
		{
			
			
			// 取消上次加载
			if(_url){
				if ( ( _res && _url != _res.url ) || !_res  )
					App.loader.canelLoad( _url , onComplete );
			}
			
			
			_url = str;
			mCurrentTime = 0.0;
			currentF = 0;
			mTextures = null;
			_bitmap.bitmapData = null;
			if ( _res )
				App.loader.subtractUseNumber( _res );
			_res = null;
			App.loader.load( _url , onComplete );
			
		}
		public function get url () : String
		{
			return _url;
		}
		/**
		 * 资源加载完成
		 */
		public function onComplete ( res : JtaResource ) : void
		{
			if ( _url == res.url )
			{
				setJtaResource(res);
				
				dispatchEvent( new UIEvent( UIEvent.LOADER_COMPLETE ) );
			}
		}
		protected function get jtaResource () : JtaResource
		{
			return _res;
		}
		/**
		 * 设置资源
		 */
		public function setJtaResource ( res : JtaResource ) : void
		{
			
			//更新 新资源
			_res = res;
			//为什么要加这个？
			//_url = _res.url;
			App.loader.addUseNumber( _res );
			
			//传入的肯定是合格的资源，因为与url相匹配。如果没有就说明这个资源的动作命名不对，所以改变其动作命名（这不太好，先这样用着，没什么大问题）
			if(!_res.content[_action]){
				//动作有可能命名不都是0，所以做一个循环，取第一个
				for ( var i : * in _res.content )
				{
					if(_res.content[i] is Object && _res.content[i].hasOwnProperty("dirCount")){
						_res.content[_action] = _res.content[i];
						_res.content[i] = null;
						delete _res.content[i];
						break;
					}
				}
				
			}
			texture = _res.content[_action]["dirData"][_dir]["frames"][0];
			init(_res.content[_action]["dirData"][_dir]["frames"],_res.content[_action]["frameCount"]);
			
		}
		/**
		 * 方向
		 */
		public function set dir(dir:int):void{
			
			if(_dir == dir){
				return;
			}
			_dir = dir;
			if(_res && _res.content[_action]){
				texture = _res.content[_action]["dirData"][_dir]["frames"][mCurrentFrame];
				mTextures = _res.content[_action]["dirData"][_dir]["frames"];
				
			}
		}
		public function get action():int{
			return _action;
		}
		public function set action(action:int):void{
			if(_action == action){
				return;
			}
			_action = action;
			if(_res && _res.content[_action]){
				//如果类型是user或者mounts就说明url改变了,修改_url的值，防止加载一个旧资源完成前改变动作出现的bug
				if(_type == AnCategory.USER || _type == AnCategory.MOUNTS){
					_url = ResourceUtil.getAnimationURL(_type,_jtaName,_action);
				}
				mCurrentTime = 0.0;
				currentF = 0;
				texture = _res.content[_action]["dirData"][_dir]["frames"][0];
				init(_res.content[_action]["dirData"][_dir]["frames"],_res.content[_action]["frameCount"]);
			}else{
				url = ResourceUtil.getAnimationURL(_type,_jtaName,_action);
			}
		}
		public function setActionAndFps(action:int,fps:Number):void{
			_fps = fps;
			this.action = action;
		}
		public function set jtaName(jtaName:String):void{
			if(_jtaName == jtaName){
				return;
			}
			_jtaName = jtaName;
			url = ResourceUtil.getAnimationURL(_type,_jtaName,_action);
		}
		public function set texture(obj:Object):void{
			var imageResource:ImageResource = obj["imageResource"];
			var rect:Rectangle = obj["rect"];
			_bitmap.bitmapData = imageResource.bitmapData;
			_bitmap.width = _bitmap.bitmapData.width*GlobalData.scale;
			_bitmap.height = _bitmap.bitmapData.height*GlobalData.scale;
			if(rect){
				//有可能res不存在或者res的当前动作不存在
				if(_res && _res.content[_action] && _res.content[_action]["dirData"][_dir]["flip"]){
					_bitmap.scaleX = -1*GlobalData.scale;
					_bitmap.x = -rect.x*GlobalData.scale;
					_bitmap.y = rect.y*GlobalData.scale;
				}else{
					_bitmap.scaleX = 1*GlobalData.scale;
					_bitmap.x = rect.x*GlobalData.scale;
					_bitmap.y = rect.y*GlobalData.scale;
				}
				
			}else{
				_bitmap.x = 0;
				_bitmap.y = 0;
			}
		}
		public function changeSize():void{
			if (mTextures) texture = mTextures[mCurrentFrame];
		}
		private function init(textures:Array, frameCount:Number):void
		{
			if (frameCount <= 0) throw new ArgumentError("Invalid fps: " + frameCount);
			var numFrames:int = textures.length;
			
			mDefaultFrameDuration = _fps / frameCount;
			//mLoop = true;
			//mPlaying = true;
			//mCurrentTime = 0.0;
			//mCurrentFrame = 0;
			//mTotalTime = mDefaultFrameDuration * numFrames;
			mTextures = textures;
			
			if(mSounds){
				mSounds.length = 0;
				mDurations.length = 0;
				mStartTimes.length = 0;
			}else{
				mSounds = new Vector.<Sound>();
				mDurations = new Vector.<Number>();
				mStartTimes = new Vector.<Number>();
			}
			for (var i:int=0; i<numFrames; ++i)
			{
				
				mStartTimes[i] = i * mDefaultFrameDuration;
				if(i == numFrames-1){
					mDurations[i] = _fps - (mStartTimes[i-1]+mDurations[i-1]);
				}else{
					mDurations[i] = mDefaultFrameDuration;
				}
			}
			//play();
			//initAnimation = true;
		}
		
		
		
		public function play():void
		{
			if(!mPlaying){
				mPlaying = true;
				
			}
		}
		
		
		public function pause():void
		{
			if(mPlaying){
				mPlaying = false;
				
			}
		}
		
		
		public function stop():void
		{
			currentFrame = 0;
			if(mPlaying){
				mPlaying = false;
				
			}
		}
		
		
		private function updateStartTimes():void
		{
			var numFrames:int = this.numFrames;
			
			mStartTimes.length = 0;
			mStartTimes[0] = 0;
			
			for (var i:int=1; i<numFrames; ++i)
				mStartTimes[i] = mStartTimes[i-1] + mDurations[i-1];
			
			//最够在把最后一帧可能会延长的部分加上
			mDurations[numFrames-1] =_fps - (mStartTimes[numFrames-2]+mDurations[numFrames-2]);
		}
		
		public function advanceTime(passedTime:Number):void
        {
            if (!mPlaying || passedTime <= 0.0) return;
            
            var finalFrame:int;
            var previousFrame:int = mCurrentFrame;
            var restTime:Number = 0.0;
            var breakAfterFrame:Boolean = false;
            var hasCompleteListener:Boolean = hasEventListener(Event.COMPLETE); 
            var dispatchCompleteEvent:Boolean = false;
            var totalTime:Number = _fps;
			if(this._type == AnCategory.EFFECT && _jtaName=="550000530_mj_sishenliandao"){
				//trace(mCurrentFrame+","+passedTime+","+_fps+","+mCurrentTime+","+JugglerManager.processTime);
			}
			//没有资源的时候走这里
			if(_res == null || _res.content[_action]==null){
				//重置当前时间
				if (mLoop && mCurrentTime == totalTime)
				{ 
					mCurrentTime = 0.0; 
				}
				mCurrentTime += passedTime;
				if(mCurrentTime >= totalTime){
					restTime = mCurrentTime-totalTime;
					mCurrentTime = totalTime;
					if(hasCompleteListener)
						 dispatchEvent(new Event(Event.COMPLETE));
					
					if (mLoop && restTime > 0.0)
						advanceTime(restTime);
				}
				return;
			}
			
			
			//xp帧是否改变了
			var currentFrameIsChange:Boolean = false;
            //xp这里当前时间肯定不会大于总时间，有点误导，删除
            if (mLoop && mCurrentTime == totalTime)
            { 
                mCurrentTime = 0.0; 
               	currentF = 0; 
				currentFrameIsChange = true;
            }
            //xp有时间差，才可以播放动画（这个条件肯定是成立，其实没什么用）
            if (mCurrentTime < totalTime)
            {
                mCurrentTime += passedTime;
                finalFrame = mTextures.length - 1;
                //if(this._type == AnCategory.EFFECT && finalFrame == 5){
				//	trace(mCurrentFrame+","+passedTime);
				//}
                while (mCurrentTime > mStartTimes[mCurrentFrame] + mDurations[mCurrentFrame])
                {
                    if (mCurrentFrame == finalFrame)
                    {
						
						//xp是loop并且没有完成事件
						//xp这是继续的循环啊，是loop，如果没有完成事件的话，继续走while
						//这是有好处的，不用再调advanceTime了
                        if (mLoop && !hasCompleteListener)
                        {
                            mCurrentTime -= totalTime;
                            currentF = 0;
							currentFrameIsChange = true;
                        }
						//xp这里就是1：不是loop，没有完成事件，2：是loop，有完成事件，3：不是loop，有完成事件
						//xp：那就先发个事件再走，1，3：都不用走while了，直接完成就完事儿了
                        else
                        {
                            breakAfterFrame = true;
                            restTime = mCurrentTime - totalTime;
                            dispatchCompleteEvent = hasCompleteListener;
                            currentF = finalFrame;
							currentFrameIsChange = true;
                            mCurrentTime = totalTime;
                        }
                    }
					//xp这里可能会到最后一帧，并且时间跟最后一帧的最后时间恰巧相等，所以会下面有判断最后一帧并且时间相等
                    else
                    {
                        mCurrentFrame++;
						currentFrameIsChange = true;
                    }
                    //xp这里可能播放两次音效，到最后一帧开始播放，结束时也可能播放（？）
                    //var sound:Sound = mSounds[mCurrentFrame];
                    //if (sound) sound.play();
                    if (breakAfterFrame) break;
                }
                
                // special case when we reach *exactly* the total time.
				//xp有用，while循环是大于，才走，等于的话，直接发事件就可以了，不用做切换
				//因为这是最后一帧了，不能再往下切换了
                if (mCurrentFrame == finalFrame && mCurrentTime == totalTime)
                    dispatchCompleteEvent = hasCompleteListener;
            }
            
            if (mCurrentFrame != previousFrame){
				//if(this._type == AnCategory.EFFECT){
				//	trace(mCurrentFrame+","+previousFrame+","+finalFrame+","+totalTime+","+mCurrentTime);
				//}
                texture = mTextures[mCurrentFrame];
			}
            //xp把音效放在这里比较好,如果帧有所变化，就播放音乐
			if(currentFrameIsChange && mSounds.length > mCurrentFrame){
				var sound:Sound = mSounds[mCurrentFrame];
				if (sound) sound.play();
			}
            if (dispatchCompleteEvent)
                dispatchEvent(new Event(Event.COMPLETE));
			
			//如果是loop并且有剩余没用的时间，才有意义走动画
			//如果事件回调，改变了loop的值，也就没有必要再走这里了
            if (mLoop && restTime > 0.0)
                advanceTime(restTime);
        }
		public function get isComplete():Boolean 
		{
			return !mLoop && mCurrentTime == _fps;
		}
		
		
		//public function get totalTime():Number { return _fps; }
		
		
		public function get numFrames():int { return mTextures.length; }
		
		
		public function get loop():Boolean { return mLoop; }
		public function set loop(value:Boolean):void { mLoop = value; }
		
		
		public function get currentFrame():int { return mCurrentFrame; }
		public function getFrameDuration(frameID:int):Number
		{
			if (frameID < 0 || frameID >= numFrames) throw new ArgumentError("Invalid frame id");
			return mDurations[frameID];
		}
		public function set currentF(value:int):void{
			mCurrentFrame = value;
			//if(this._type == AnCategory.EFFECT && _jtaName=="550000530_mj_sishenliandao" && mCurrentFrame == 0 && mCurrentTime !=0 && mCurrentTime != 1){
			//	trace(mCurrentFrame+","+_fps+","+mCurrentTime+","+JugglerManager.processTime);
			//}
		}
		/**
		 * 设置当前帧数
		 */
		public function set currentFrame(value:int):void
		{
			currentF = value;
			//if(this._type == AnCategory.EFFECT && _jtaName=="550000530_mj_sishenliandao" && mCurrentFrame == 0){
			//	trace(mCurrentFrame+","+_fps+","+mCurrentTime+","+JugglerManager.processTime);
			//}
			mCurrentTime = 0.0;
			
			for (var i:int=0; i<value; ++i)
				mCurrentTime += getFrameDuration(i);
			
			if (mTextures) texture = mTextures[mCurrentFrame];
			if (mSounds && mSounds.length > mCurrentFrame && mSounds[mCurrentFrame]) mSounds[mCurrentFrame].play();
		}
		
		
		public function get fps():Number { return _fps; }
		/**
		 * 设置每秒多少帧
		 */
		public function set fps(value:Number):void
		{
			if(_fps == value){
				return;
			}
			_fps = value;
			if(_res == null || _res.content[_action]==null){
				return;
			}
			var newFrameDuration:Number = _fps / _res.content[_action]["frameCount"];
			if (value <= 0) throw new ArgumentError("Invalid fps: " + value);
			
			
			var acceleration:Number = newFrameDuration / mDefaultFrameDuration;
			mCurrentTime *= acceleration;
			mDefaultFrameDuration = newFrameDuration;
			
			for (var i:int=0; i<numFrames; ++i) 
			{
				var duration:Number = mDurations[i] * acceleration;
				//mTotalTime = mTotalTime - mDurations[i] + duration;
				mDurations[i] = duration;
			}
			
			updateStartTimes();
		}
		
		
		public function get isPlaying():Boolean 
		{
			if (mPlaying)
				return mLoop || mCurrentTime < _fps;
			else
				return false;
		}
		public function dispose():void{
			
			//清理路径
			if ( _url )
			{
				if ( ( _res && _url != _res.url ) || !_res  )
					App.loader.canelLoad( _url , onComplete );
				_url = null;
			}
			if ( _res ){
				App.loader.subtractUseNumber( _res );
				_res = null;
			}
			_bitmap.bitmapData = null;
			_bitmap.x = 0;
			_bitmap.y = 0;
			_bitmap.filters = null;
			this.rotation = 0;
			this.x = 0;
			this.y = 0;
			mTextures = null;
			if(mSounds){
				mSounds.length = 0;
				mDurations.length = 0;
				mStartTimes.length = 0;
			}
			stop();
			mDefaultFrameDuration = 0;
			mLoop = false;
			_dir = 1;
			_action = 0;
			_type = null;
			_jtaName = null;
			_fps = 1.0;
			//data = null;
			JugglerManager.oneuggler.remove(this);
			//TweenManager.removeIAnimatableObj(this);
			
			if ( this.parent )
				this.parent.removeChild( this );
			isDispose = true;
			toPool(this);
		}
		private static var sAnimationPool:Vector.<Animation> = new <Animation>[];
        
        /** @private */
        public static function fromPool(type:String,jtaName:String,action:int,loop:Boolean,allTime:Number,dir:int):Animation
        {
            if (sAnimationPool.length) return sAnimationPool.pop().reset(type, jtaName, action, loop,allTime,dir);
            else return new Animation(type, jtaName, action, loop,allTime,dir);
        }
        
        /** @private */
		public static function toPool(animation:Animation):void
        {
            sAnimationPool[sAnimationPool.length] = animation;
        }
	}
}