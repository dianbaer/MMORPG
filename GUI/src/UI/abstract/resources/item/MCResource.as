package UI.abstract.resources.item
{
	import flash.display.Loader;
	import flash.display.MovieClip;
	
	import UI.abstract.manager.UIManager;
	import UI.abstract.resources.loader.BaseLoader;
	import UI.abstract.utils.BitmapDataInfo;
	import UI.abstract.utils.BitmapDataUtils;

	public class MCResource extends Resource
	{
		private var _v_bitInfo:Vector.<BitmapDataInfo>;
		private var _movieClipArray:Array;
		/**
		 * 总帧数
		 */
		public var totalFrame:int = 1;
		
		private var loader:Loader;
		
		private var className:Class;
		public function MCResource()
		{
			super();
		}

		public function get movieClipArray():Array
		{
			if(!_movieClipArray){
				_movieClipArray = UIManager.searchChild(mc,MovieClip);
				_movieClipArray.push(mc);
				for(var i:int = 0;i<_movieClipArray.length;i++){
					if((_movieClipArray[i] as MovieClip).totalFrames == 1){
						_movieClipArray.splice(i,1);
						i--;
					}else{
						(_movieClipArray[i] as MovieClip).gotoAndStop(1);
						if((_movieClipArray[i] as MovieClip).totalFrames>totalFrame){
							totalFrame = (_movieClipArray[i] as MovieClip).totalFrames;
						}
					}
					
				}
			}
			return _movieClipArray;
		}
		public function goMc(frame:int = 1):void{
			if(frame<1){
				frame = 1;
			}
			if(frame>totalFrame){
				frame = totalFrame;
			}
			for(var j:int = 0;j<movieClipArray.length;j++){
				if((movieClipArray[j] as MovieClip).totalFrames>=frame){
					(movieClipArray[j] as MovieClip).gotoAndStop(frame);
				}else{
					var nowFrame:int = frame%(movieClipArray[j] as MovieClip).totalFrames;
					if(nowFrame == 0){
						nowFrame = totalFrame;
					}
					(movieClipArray[j] as MovieClip).gotoAndStop(nowFrame);
				}
				
			}
		}
		public function get v_bitInfo():Vector.<BitmapDataInfo>
		{
			if(!_v_bitInfo){
				_v_bitInfo = BitmapDataUtils.cacheBitmapMovie(_content as MovieClip);
				totalFrame = _v_bitInfo.length;
			}
			return _v_bitInfo;
		}

		override public function initialize ( data : BaseLoader) : void
		{
			super.initialize( data );
			_content = Loader(data.content).content;
			loader = Loader(data.content);
		}
		public function mcClass(name:String):Class{
			if(!className){
				className = loader.contentLoaderInfo.applicationDomain.getDefinition(name) as Class;
			}
			return className;
		}
		/**
		 * 获取MC
		 */
		public function get mc () : MovieClip
		{
			return _content ? (content as MovieClip) : null;
		}
		override public function dispose():void
		{
			
			if ( _content )
			{
				MovieClip(_content).stop();
				_content = null;
			}
			if(_v_bitInfo && _v_bitInfo.length>0){
				for(var i:int = 0;i<_v_bitInfo.length;i++){
					_v_bitInfo[i].bitmapData.dispose();
				}
				_v_bitInfo.length = 0;
				_v_bitInfo = null;
			}
			if(_movieClipArray && _movieClipArray.length>0){
				_movieClipArray.length = 0;
				_movieClipArray = null;
			}
			loader.unloadAndStop(false);
			loader.unload();
			loader = null;
			className = null;
			super.dispose();
		}
	}
}