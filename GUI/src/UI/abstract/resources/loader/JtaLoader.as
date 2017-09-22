package UI.abstract.resources.loader
{
	import flash.display.BitmapData;
	import flash.events.Event;
	import flash.events.IOErrorEvent;
	import flash.geom.Point;
	import flash.geom.Rectangle;
	import flash.net.URLLoader;
	import flash.net.URLLoaderDataFormat;
	import flash.net.URLRequest;
	import flash.utils.ByteArray;
	
	import UI.App;
	import UI.abstract.resources.ResourceManager;
	import UI.abstract.resources.ResourceUtil;
	import UI.abstract.resources.item.ImageResource;
	import UI.abstract.resources.item.LoadObj;
	import UI.abstract.utils.CommonPool;
	
	public class JtaLoader extends BaseLoader
	{
		private var loader : URLLoader;
		private var isEnd:Boolean = false;
		public function JtaLoader(loadObj:LoadObj)
		{
			super(loadObj);
			reset(loadObj);
			
		}
		public function reset(loadObj:LoadObj):JtaLoader{
			this.loadObj = loadObj;
			if(!loader){
				loader = new URLLoader();
			}
			loader.dataFormat = URLLoaderDataFormat.BINARY;
			loader.addEventListener( Event.COMPLETE , onComplete );
			loader.addEventListener( IOErrorEvent.IO_ERROR , onIOError );
			return this;
		}
		override public function load () : void
		{
			loader.load( new URLRequest( loadObj.url ) );
		}
		override protected function onComplete ( event : Event ) : void
		{
			_content = loader.data;
			//dispatchEvent( new Event( Event.COMPLETE ) );
			var ba:ByteArray = _content as ByteArray;
			/*try{
				ba.uncompress();
			}
			catch(err:*){
				trace(1111);
			};*/
			_content = new Object();
			_content.loadings = 0;
			var headFlag:String = ba.readUTF();	//标识
			var kind:int = ba.readInt();	//种类
			while(ba.bytesAvailable){
				var type:int = ba.readByte();	//动作,值:0[站立],1[移动],2[攻击],3[受伤],4[死亡],5[休息]
				var len:int = ba.readInt();	//该动作的总字节长
				var animation:Object = {loadings:0, ready:false};
				_content[type] = animation;
				var position:int = ba.position;
				animation.frameCount = ba.readByte();	//该动作的某一方向帧数(所有方向的帧数统一)
				animation.boundsRect = new Rectangle();	//状态矩阵,Animal.heartPos属性通过此矩阵获取
				animation.boundsRect.x = ba.readShort();	//动作矩阵相对注册点的X坐标
				animation.boundsRect.y = ba.readShort();	//动作矩阵相对注册点的Y坐标
				animation.boundsRect.width = ba.readShort();	//动作矩阵宽度
				animation.boundsRect.height = ba.readShort();	//动作矩阵高度
				animation.speed = ba.readByte();	//动作下帧间隔
				if(type == 3){
					animation.speed = 10;
				}
				if(kind >= 5){
					animation.fireFrame = [ba.readByte(), ba.readByte(), ba.readByte()];	//三个射击点[第几帧].目前只用第一个
					animation.secondFireEndFrame = ba.readByte();	//第二次攻击的帧数,目前这个参数没用
				}else{
					animation.fireFrame = [ba.readByte()];	//一个射击点
					animation.secondFireEndFrame = 0;
				}
				animation.repeatDelay = ba.readByte();	//重复间隔
				if(kind == 7 || kind == 3){
					ba.readByte();	//保留参数
				}
				if(kind >= 6){
					animation.swing = ba.readByte() == 1;	//swing:是不是来回播放(即从后面往前面播),1:是.
				}else{
					animation.swing = false;
				}
				animation.dirData = new Object;
				var dirCount:int;
				animation.dirCount = 0;
				while(ba.position < len + position){
					var dir:int = ba.readByte();	//方向,值:1[上],2[右上],3[右],4[右下],5[下],6[左下],7[左],8[左上]
					var dirObj:Object = {frames:[], rect:new Rectangle(), firePoint:new Point(), flip:false};
					animation.dirData[dir] = dirObj;
					if(dirCount == 0){
						animation.defaultDir = dir;
					}
					dirCount ++;
					animation.dirCount++;
					//方向矩阵
					dirObj.rect.x = ba.readShort();	//方向矩阵相对注册点的X坐标
					dirObj.rect.y = ba.readShort();	//方向矩阵相对注册点的Y坐标
					dirObj.rect.width = ba.readShort();	//方向矩阵宽度
					dirObj.rect.height = ba.readShort();	//方向矩阵高度
					//Animal.firePoint通过此点获取,即发射箭等的参考点
					dirObj.firePoint.x = ba.readShort();
					dirObj.firePoint.y = ba.readShort();
					for(var i:int=0;i<animation.frameCount;i++){
						var item:Object = new Object();
						item.rect = new Rectangle();	//帧的矩阵,主要用来定位图像在容器中的位置
						item.rect.x = ba.readShort();	//帧图片离注册点的x坐标
						item.rect.y = ba.readShort();	//帧图片离注册点的y坐标
						item.rect.width = ba.readShort();	//帧图片宽度
						item.rect.height = ba.readShort();	//帧图片高度
						var loaderLen:int = ba.readInt();	//图片的长度
						if(loaderLen == int.MIN_VALUE){
							loaderLen = ba.readInt();
							if(loaderLen > 0){
								item.raw = new ByteArray();
								ba.readBytes(item.raw, 0, loaderLen);	//读取帧图片的二进制图像数据	
								var alphaLen:int = ba.readInt();
								item.alpha = new ByteArray();
								ba.readBytes(item.alpha, 0, alphaLen);
							}
						}else if (loaderLen > 0)
						{
							item.raw = new ByteArray();
							ba.readBytes(item.raw, 0, loaderLen);	//读取帧图片的二进制图像数据	
						}
						dirObj.frames[i] = item;
						if (item.raw){
							animation.loadings ++;
							//var loader:FrameLoader = new FrameLoader();
							//loader.frame = item;
							//item.loader = loader;
							//item.currAction = currAction;
							//loader.contentLoaderInfo.addEventListener(Event.COMPLETE, onFrameLoaded);
							
							item.prior = 1;
							_content.loadings ++;
							
							
							//如果指定了优先级,也先加载
							//loader.loadBytes(item.raw);
							var helpObj:Object = CommonPool.fromPoolObject();
							helpObj.raw = item.raw;
							App.loader.load(ResourceUtil.getAnimationBitmapData(ResourceManager.unFormatResourceName(loadObj.url),type,dir,i),onFrameLoaded,helpObj,2);
							//item.raw.clear();
							item.raw = null;
							delete item.raw;
							
							/////////////////////////////////////////////////
						}else{
							item.bitmapData = new BitmapData(1, 1, true, 0);
						}
					}
				}
				var index:int = 1;
				var indexTmp:int;
				var indexTmp2:int;
				var dirTmp:Object;
				//把它的反方向补齐上去
				while (index <= 8)
				{
					dirTmp = animation.dirData[index];
					if (!dirTmp)
					{
						dirTmp = animation.dirData[10 - index];
						if (dirTmp)
						{
							var rect:Rectangle = dirTmp.rect.clone();
							rect.x = -rect.right;
							var point:Point = dirTmp.firePoint.clone();
							point.x = - point.x;
							animation.dirData[index] = {flip:true, frames:dirTmp.frames, rect:rect, firePoint:point};
							if (index < 5)
							{
								indexTmp = index;
							}
							else
							{
								indexTmp2 = index;
							}
						}
					}
					else if (index < 5)
					{
						indexTmp = index;
					}
					else
					{
						indexTmp2 = index;
					}
					index++;
				}
				if (indexTmp == 0)
				{
					indexTmp = indexTmp2;
				}
				if (indexTmp2 == 0)
				{
					indexTmp2 = indexTmp;
				}
				index = 1;
				//把没有方向的用其它方向替低,1,2,3,4为一边,4,5,6,7,8为另一边
				while (index <= 8)
				{
					dirTmp = animation.dirData[index];
					if (!dirTmp)
					{
						if (index < 5)
						{
							animation.dirData[index] = animation.dirData[indexTmp];
						}
						else
						{     
							animation.dirData[index] = animation.dirData[indexTmp2];
						}
					}
					index++;
				}
			}
			ba.clear();
			//等执行到头，有可能会直接对调，所以这里如果是直接回调的资源，肯定就完成了，直接发完成事件
			isEnd = true;
			if(_content.loadings == 0){
				dispatchEvent( new Event( Event.COMPLETE ) );
			}
		}
		private function onFrameLoaded(res:ImageResource):void
		{
			var str:String = res.url.substr(res.url.indexOf("jtas?")+5,res.url.length-1);
			var array:Array = str.split("&");
			var obj:Object = new Object();
			for(var i:int = 0;i<array.length;i++){
				var str1:Array = array[i].split("=");
				obj["str"+i] = str1;
			}
			var obj1:Object = content[obj["str0"][1]]["dirData"][obj["str1"][1]]["frames"][obj["str2"][1]];
			/*if(itemObj.alpha){
				var bitmap:BitmapData = Bitmap(frameLoader.content).bitmapData;
				var tp:Point = new Point();
				var talpha:BitmapData = new BitmapData(bitmap.width, bitmap.height);
				itemObj.alpha.position = 0;
				talpha.setPixels(bitmap.rect, itemObj.alpha);
				
				//创建透明图片
				itemObj.bitmapData = new BitmapData(bitmap.width, bitmap.height);
				itemObj.bitmapData.copyPixels(bitmap, bitmap.rect, tp);
				itemObj.bitmapData.copyChannel(talpha, bitmap.rect, tp, BitmapDataChannel.RED, BitmapDataChannel.ALPHA);
				
				//清理
				bitmap.dispose();
				talpha.dispose();
				itemObj.alpha.clear();
				itemObj.alpha = null;
				delete itemObj.alpha;
				frameLoader.unload();
			}else{*/
			obj1.imageResource = res;
			App.loader.addUseNumber(res);
			//}
			/*var animation:Object = itemObj.owner;
			animation.loadings --;
			var anData:AnLoadData
			if (animation.loadings == 0)
			{
				animation.ready = true;
				
				anData = animation.owner;
				if(anData){
					anData.readyMap[itemObj.currAction]=true
					
				}
			}
			if (itemObj.prior)
			{
				anData = animation.owner;
				anData.loadings --;
				if (anData.loadings == 0)
				{
					anData.ready = true;
					anData.readyMap[itemObj.currAction]=true
					
				}
			}*/
			_content.loadings--;
			//有可能里面的图片资源没删除，直接回调，导致没有执行到头
			if(_content.loadings == 0 && isEnd){
				dispatchEvent( new Event( Event.COMPLETE ) );
			}
		}
		override protected function onIOError ( event : IOErrorEvent ) : void
		{
			dispatchEvent( new IOErrorEvent( IOErrorEvent.IO_ERROR ) );
		}
		
		override public function dispose () : void
		{
			
			loader.removeEventListener( Event.COMPLETE , onComplete );
			loader.removeEventListener( IOErrorEvent.IO_ERROR , onIOError );
			//loader = null;
			isEnd = false;
			super.dispose();
			toPool(this);
		}
		private static var sJtaLoaderPool:Vector.<JtaLoader> = new <JtaLoader>[];
		
		/** @private */
		public static function fromPool(loadObj : LoadObj):JtaLoader
		{
			if (sJtaLoaderPool.length) return sJtaLoaderPool.pop().reset(loadObj);
			else return new JtaLoader(loadObj);
		}
		
		/** @private */
		public static function toPool(jtaLoader:JtaLoader):void
		{
			
			sJtaLoaderPool[sJtaLoaderPool.length] = jtaLoader;
		}
	}
}