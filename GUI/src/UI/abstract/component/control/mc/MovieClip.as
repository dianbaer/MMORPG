package UI.abstract.component.control.mc
{
	import UI.App;
	import UI.abstract.component.control.container.Container;
	import UI.abstract.component.event.UIEvent;
	import UI.abstract.resources.item.ImageArrayResource;
	import UI.abstract.resources.item.ImageResource;
	import UI.abstract.tween.IAnimatable;
	import UI.abstract.tween.TweenManager;
	
	import flash.display.Bitmap;
	import flash.events.Event;
	import flash.media.Sound;
	
	
	public class MovieClip extends Container implements IAnimatable
	{
		private var mTextures:Vector.<ImageResource>;
		private var mSounds:Vector.<Sound>;
		private var mDurations:Vector.<Number>;
		private var mStartTimes:Vector.<Number>;
		
		private var mDefaultFrameDuration:Number;
		private var mTotalTime:Number;
		private var mCurrentTime:Number;
		private var mCurrentFrame:int;
		private var mLoop:Boolean;
		private var mPlaying:Boolean = false;
		
		protected var _res : ImageArrayResource;
		protected var _url : String;
		public var _bitmap : Bitmap;
		private var _fps:Number;
		
		private var _widthPercent:Number = 1;
		private var _heightPercent:Number = 1;
		
		public function MovieClip(str:String = null, fps:Number=15)
		{
			_bitmap = new Bitmap();
			addChild( _bitmap );
			_fps = fps;
			url = str;
		}

		/**
		 * 高度百分比
		 */
		public function get heightPercent():Number
		{
			return _heightPercent;
		}

		/**
		 * @private
		 */
		public function set heightPercent(value:Number):void
		{
			if(_heightPercent == value){
				return;
			}
			_heightPercent = value;
			if(mTextures && mTextures[mCurrentFrame]){
				_bitmap.x = 0;
				_bitmap.y = 0;
				if(mTextures[mCurrentFrame].frame){
					_bitmap.x = mTextures[mCurrentFrame].frame.x * widthPercent;
					_bitmap.y = mTextures[mCurrentFrame].frame.y * heightPercent;
				}
				if(mTextures[mCurrentFrame].r == "y"){
					_bitmap.rotation = -90;
					if(mTextures[mCurrentFrame].frame){
						_bitmap.y = (mTextures[mCurrentFrame].frame.y+mTextures[mCurrentFrame].bitmapData.width) * widthPercent;
					}else{
						_bitmap.y = mTextures[mCurrentFrame].bitmapData.width * widthPercent;
					}
					_bitmap.width = _bitmap.bitmapData.height * widthPercent;
					_bitmap.height = _bitmap.bitmapData.width * heightPercent;
					
				}else{
					_bitmap.rotation = 0;
					_bitmap.width = _bitmap.bitmapData.width * widthPercent;
					_bitmap.height = _bitmap.bitmapData.height * heightPercent;
					
				}
			}else{
				_bitmap.x = 0;
				_bitmap.y = 0;
				_bitmap.rotation = 0;
			}
		}

		/**
		 * 宽度百分比
		 */
		public function get widthPercent():Number
		{
			return _widthPercent;
		}

		/**
		 * @private
		 */
		public function set widthPercent(value:Number):void
		{
			if(_widthPercent == value){
				return;
			}
			_widthPercent = value;
			if(mTextures && mTextures[mCurrentFrame]){
				_bitmap.x = 0;
				_bitmap.y = 0;
				if(mTextures[mCurrentFrame].frame){
					_bitmap.x = mTextures[mCurrentFrame].frame.x * widthPercent;
					_bitmap.y = mTextures[mCurrentFrame].frame.y * heightPercent;
				}
				if(mTextures[mCurrentFrame].r == "y"){
					_bitmap.rotation = -90;
					if(mTextures[mCurrentFrame].frame){
						_bitmap.y = (mTextures[mCurrentFrame].frame.y+mTextures[mCurrentFrame].bitmapData.width) * widthPercent;
					}else{
						_bitmap.y = mTextures[mCurrentFrame].bitmapData.width * widthPercent;
					}
					_bitmap.width = _bitmap.bitmapData.height * widthPercent;
					_bitmap.height = _bitmap.bitmapData.width * heightPercent;
					
				}else{
					_bitmap.rotation = 0;
					_bitmap.width = _bitmap.bitmapData.width * widthPercent;
					_bitmap.height = _bitmap.bitmapData.height * heightPercent;
					
				}
			}else{
				_bitmap.x = 0;
				_bitmap.y = 0;
				_bitmap.rotation = 0;
			}
		}

		public function set url ( str : String ) : void
		{
			if ( _url == str )
				return;
			
			// 取消上次加载
			if ( ( _res && _url && _url != _res.url ) || ( !_res && _url ) )
				App.loader.canelLoad( _url , onComplete );
			
			//地址为空，图像存在 说明是外部直接设置的bitmapdata，先清理
			if ( !_url && _bitmap.bitmapData )
			{
				_bitmap.bitmapData = null;
				
			}
			
			_url = str;
			
			stop();
			if ( _url )
			{
				App.loader.load( _url , onComplete );
			}
			else
			{
				if ( _res )
				{
					App.loader.subtractUseNumber( _res );
					_res = null;
					_bitmap.bitmapData = null;
					
				}
			}
		}
		public function get url () : String
		{
			return _url;
		}
		/**
		 * 资源加载完成
		 */
		public function onComplete ( res : ImageArrayResource ) : void
		{
			if ( _url == res.url )
			{
				this.imageArrayResource = res;
				dispatchEvent( new UIEvent( UIEvent.LOADER_COMPLETE ) );
			}
		}
		protected function get imageArrayResource () : ImageArrayResource
		{
			return _res;
		}
		/**
		 * 设置资源
		 */
		protected function set imageArrayResource ( res : ImageArrayResource ) : void
		{
			// 清理现有资源和外部穿的bitmapdata
			if ( res == null )
			{
				// 加载中
				if ( _url )
				{
					if ( ( _res && _url != _res.url ) || ( !_res && _url ) )
						App.loader.canelLoad( _url , onComplete );
					if ( _res )
						App.loader.subtractUseNumber( _res );
				}
				_url = null;
				_res = null;
				_bitmap.bitmapData = null;
				stop();
				return;
			}
			
			//更新 新资源
			var oldRes : ImageArrayResource = _res;
			_res = res;
			_url = _res.url;
			
			if ( oldRes )
				App.loader.subtractUseNumber( oldRes );
			App.loader.addUseNumber( _res );
			
			texture = _res._imageArray[0];
			init(_res._imageArray,_fps);
			
		}
		override public function get width():Number{
			if(mTextures && mTextures[mCurrentFrame]){
				if(mTextures[mCurrentFrame].frame){
					return mTextures[mCurrentFrame].frame.width;
				}else{
					if(mTextures[mCurrentFrame].r == "y"){
						return mTextures[mCurrentFrame].bitmapData.height;
					}else{
						return mTextures[mCurrentFrame].bitmapData.width;
					}
					
				}
			}else{
				return 0;
			}
		}
		override public function get height():Number{
			if(mTextures && mTextures[mCurrentFrame]){
				if(mTextures[mCurrentFrame].frame){
					return mTextures[mCurrentFrame].frame.height;
				}else{
					if(mTextures[mCurrentFrame].r == "y"){
						return mTextures[mCurrentFrame].bitmapData.width;
					}else{
						return mTextures[mCurrentFrame].bitmapData.height;
					}
				}
			}else{
				return 0;
			}
			
		}
		public function set texture(imageResource:ImageResource):void{
			_bitmap.bitmapData = imageResource.bitmapData;
			
			if(imageResource && imageResource.bitmapData){
				_bitmap.x = 0;
				_bitmap.y = 0;
				if(imageResource.frame){
					_bitmap.x = imageResource.frame.x * widthPercent;
					_bitmap.y = imageResource.frame.y * heightPercent;
				}
				if(imageResource.r == "y"){
					_bitmap.rotation = -90;
					if(imageResource.frame){
						_bitmap.y = (imageResource.frame.y+imageResource.bitmapData.width) * widthPercent;
					}else{
						_bitmap.y = imageResource.bitmapData.width * widthPercent;
					}
					_bitmap.width = _bitmap.bitmapData.height * widthPercent;
					_bitmap.height = _bitmap.bitmapData.width * heightPercent;
					
				}else{
					_bitmap.rotation = 0;
					_bitmap.width = _bitmap.bitmapData.width * widthPercent;
					_bitmap.height = _bitmap.bitmapData.height * heightPercent;
					
				}
			}else{
				_bitmap.x = 0;
				_bitmap.y = 0;
				_bitmap.rotation = 0;
			}
		}
		private function init(textures:Vector.<ImageResource>, fps:Number):void
		{
			if (fps <= 0) throw new ArgumentError("Invalid fps: " + fps);
			var numFrames:int = textures.length;
			
			mDefaultFrameDuration = 1.0 / fps;
			mLoop = true;
			//mPlaying = true;
			mCurrentTime = 0.0;
			mCurrentFrame = 0;
			mTotalTime = mDefaultFrameDuration * numFrames;
			mTextures = textures.concat();
			mSounds = new Vector.<Sound>(numFrames);
			mDurations = new Vector.<Number>(numFrames);
			mStartTimes = new Vector.<Number>(numFrames);
			
			for (var i:int=0; i<numFrames; ++i)
			{
				mDurations[i] = mDefaultFrameDuration;
				mStartTimes[i] = i * mDefaultFrameDuration;
			}
			play();
		}
		
		
		/*public function addFrame(texture:Texture, sound:Sound=null, duration:Number=-1):void
		{
		addFrameAt(numFrames, texture, sound, duration);
		}
		
		
		public function addFrameAt(frameID:int, texture:Texture, sound:Sound=null, 
		duration:Number=-1):void
		{
		if (frameID < 0 || frameID > numFrames) throw new ArgumentError("Invalid frame id");
		if (duration < 0) duration = mDefaultFrameDuration;
		
		mTextures.splice(frameID, 0, texture);
		mSounds.splice(frameID, 0, sound);
		mDurations.splice(frameID, 0, duration);
		mTotalTime += duration;
		
		if (frameID > 0 && frameID == numFrames) 
		mStartTimes[frameID] = mStartTimes[frameID-1] + mDurations[frameID-1];
		else
		updateStartTimes();
		}
		
		
		public function removeFrameAt(frameID:int):void
		{
		if (frameID < 0 || frameID >= numFrames) throw new ArgumentError("Invalid frame id");
		if (numFrames == 1) throw new IllegalOperationError("Movie clip must not be empty");
		
		mTotalTime -= getFrameDuration(frameID);
		mTextures.splice(frameID, 1);
		mSounds.splice(frameID, 1);
		mDurations.splice(frameID, 1);
		
		updateStartTimes();
		}*/
		
		
		/*public function getFrameTexture(frameID:int):Texture
		{
		if (frameID < 0 || frameID >= numFrames) throw new ArgumentError("Invalid frame id");
		return mTextures[frameID];
		}
		
		
		public function setFrameTexture(frameID:int, texture:Texture):void
		{
		if (frameID < 0 || frameID >= numFrames) throw new ArgumentError("Invalid frame id");
		mTextures[frameID] = texture;
		}
		
		
		public function getFrameSound(frameID:int):Sound
		{
		if (frameID < 0 || frameID >= numFrames) throw new ArgumentError("Invalid frame id");
		return mSounds[frameID];
		}
		
		
		public function setFrameSound(frameID:int, sound:Sound):void
		{
		if (frameID < 0 || frameID >= numFrames) throw new ArgumentError("Invalid frame id");
		mSounds[frameID] = sound;
		}*/
		
		
		public function getFrameDuration(frameID:int):Number
		{
			if (frameID < 0 || frameID >= numFrames) throw new ArgumentError("Invalid frame id");
			return mDurations[frameID];
		}
		
		
		public function setFrameDuration(frameID:int, duration:Number):void
		{
			if (frameID < 0 || frameID >= numFrames) throw new ArgumentError("Invalid frame id");
			mTotalTime -= getFrameDuration(frameID);
			mTotalTime += duration;
			mDurations[frameID] = duration;
			updateStartTimes();
		}
		
		
		public function play():void
		{
			if(!mPlaying){
				mPlaying = true;
				TweenManager.addIAnimatableObj(this);
			}
		}
		
		
		public function pause():void
		{
			if(mPlaying){
				mPlaying = false;
				TweenManager.removeIAnimatableObj(this);
			}
		}
		
		
		public function stop():void
		{
			currentFrame = 0;
			if(mPlaying){
				mPlaying = false;
				TweenManager.removeIAnimatableObj(this);
			}
		}
		
		
		private function updateStartTimes():void
		{
			var numFrames:int = this.numFrames;
			
			mStartTimes.length = 0;
			mStartTimes[0] = 0;
			
			for (var i:int=1; i<numFrames; ++i)
				mStartTimes[i] = mStartTimes[i-1] + mDurations[i-1];
		}
		
		/**动画类写的非常之完美*/
		public function advanceTime(passedTime:Number):void
		{
			var finalFrame:int;
			var previousFrame:int = mCurrentFrame;
			var restTime:Number = 0.0;
			var breakAfterFrame:Boolean = false;
			
			if (mLoop && mCurrentTime == mTotalTime) 
			{ 
				mCurrentTime = 0.0; 
				mCurrentFrame = 0; 
			}
			
			if (mPlaying && passedTime > 0.0 && mCurrentTime < mTotalTime) 
			{				
				mCurrentTime += passedTime;
				finalFrame = mTextures.length - 1;
				
				while (mCurrentTime >= mStartTimes[mCurrentFrame] + mDurations[mCurrentFrame])
				{
					if (mCurrentFrame == finalFrame)
					{
						//如果拥有完成事件，跳出
						if (hasEventListener(Event.COMPLETE))
						{
							if (mCurrentFrame != previousFrame)
								texture = mTextures[mCurrentFrame];
							
							restTime = mCurrentTime - mTotalTime;
							mCurrentTime = mTotalTime;
							dispatchEvent(new Event(Event.COMPLETE))
							breakAfterFrame = true;
						}
						//在有完成事件的情况下，其实这两个值都是0
						//没有完成事件的情况下，继续
						if (mLoop)
						{
							mCurrentTime -= mTotalTime;
							mCurrentFrame = 0;
						}
							//不循环，如果没有完成事件，直接跳出了，不做后续处理
							//如果有完成事件这个就是完成事件里面的值（没什么用）
						else
						{
							mCurrentTime = mTotalTime;
							breakAfterFrame = true;
						}
					}
					else
					{
						mCurrentFrame++;
					}
					
					var sound:Sound = mSounds[mCurrentFrame];
					if (sound) sound.play();
					if (breakAfterFrame) break;
				}
			}
			
			if (mCurrentFrame != previousFrame)
				texture = mTextures[mCurrentFrame];
			
			if (restTime)
				advanceTime(restTime);
		}
		
		public function get isComplete():Boolean 
		{
			return !mLoop && mCurrentTime >= mTotalTime;
		}
		
		
		public function get totalTime():Number { return mTotalTime; }
		
		
		public function get numFrames():int { return mTextures.length; }
		
		
		public function get loop():Boolean { return mLoop; }
		public function set loop(value:Boolean):void { mLoop = value; }
		
		
		public function get currentFrame():int { return mCurrentFrame; }
		/**
		 * 设置当前帧数
		 */
		public function set currentFrame(value:int):void
		{
			mCurrentFrame = value;
			mCurrentTime = 0.0;
			
			for (var i:int=0; i<value; ++i)
				mCurrentTime += getFrameDuration(i);
			
			if (mTextures && mTextures[mCurrentFrame]) texture = mTextures[mCurrentFrame];
			if (mSounds && mSounds[mCurrentFrame]) mSounds[mCurrentFrame].play();
		}
		
		
		public function get fps():Number { return 1.0 / mDefaultFrameDuration; }
		/**
		 * 设置每秒多少帧
		 */
		public function set fps(value:Number):void
		{
			if (value <= 0) throw new ArgumentError("Invalid fps: " + value);
			
			var newFrameDuration:Number = 1.0 / value;
			var acceleration:Number = newFrameDuration / mDefaultFrameDuration;
			mCurrentTime *= acceleration;
			mDefaultFrameDuration = newFrameDuration;
			
			for (var i:int=0; i<numFrames; ++i) 
			{
				var duration:Number = mDurations[i] * acceleration;
				mTotalTime = mTotalTime - mDurations[i] + duration;
				mDurations[i] = duration;
			}
			
			updateStartTimes();
		}
		
		
		public function get isPlaying():Boolean 
		{
			if (mPlaying)
				return mLoop || mCurrentTime < mTotalTime;
			else
				return false;
		}
		override public function dispose():void{
			
			imageArrayResource = null;
			_bitmap = null;
			mTextures = null;
			mSounds = null;
			mDurations = null;
			mStartTimes = null;
			stop();
			super.dispose();
		}
	}
}