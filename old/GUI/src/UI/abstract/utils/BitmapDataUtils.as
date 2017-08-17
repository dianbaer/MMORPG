package UI.abstract.utils
{
	import UI.abstract.manager.UIManager;
	
	import flash.display.BitmapData;
	import flash.display.DisplayObject;
	import flash.display.MovieClip;
	import flash.geom.Point;
	import flash.geom.Rectangle;

	public class BitmapDataUtils
	{
		private static var pot:Point = new Point();
		/**
		 * 根据显示对象copy出位图
		 */
		public static function cacheBitmap(source:DisplayObject,isdelete:Boolean = false):BitmapDataInfo
		{
			
			var rect:Rectangle = source.getBounds(source);
			//var x:int = Math.round(rect.x);
			//var y:int = Math.round(rect.y);
			var x:int = 0;
			var y:int = 0;
			
			if (rect.isEmpty())
			{
				return null;
			}
			
			var bitData:BitmapData = new BitmapData(rect.width, rect.height, true, 0x00000000);
			bitData.draw(source);
			
			if(isdelete){
				var bitInfo:BitmapDataInfo = delete11(bitData);
				if(bitInfo){
					return bitInfo;
				}
			}
			
			var bitInfo1:BitmapDataInfo = new BitmapDataInfo();
			bitInfo1.x = x;
			bitInfo1.y = y;
			bitInfo1.bitmapData = bitData;
			
			return bitInfo1;
		}
		public static function delete11(bitData:BitmapData):BitmapDataInfo{
			//剔除边缘空白像素
			var realRect:Rectangle = bitData.getColorBoundsRect(0xFF000000, 0x00000000, false);
			if (!realRect.isEmpty() && (bitData.width != realRect.width || bitData.height != realRect.height))
			{
				
				var realBitData:BitmapData = new BitmapData(realRect.width, realRect.height, true, 0x00000000);
				realBitData.copyPixels(bitData, realRect, pot);
				
				bitData.dispose();
				bitData = null;
				
				var bitInfo:BitmapDataInfo = new BitmapDataInfo();
				bitInfo.x = realRect.x;
				bitInfo.y = realRect.y;
				bitInfo.bitmapData = realBitData;
				return bitInfo;
				
				
			}
			return null;
		}
		/**
		 * copy mc成位图数组
		 */
		public static function cacheBitmapMovie(mc:MovieClip):Vector.<BitmapDataInfo>
		{
			
			var v_bitInfo:Vector.<BitmapDataInfo>;
			
			if (mc == null)
			{
				
				//v_bitInfo = new Vector.<BitmapDataInfo>(1, true);
				
				//v_bitInfo[0] = cacheBitmap(source, transparent, fillColor, scale);
				
			}
			else
			{
				v_bitInfo = new Vector.<BitmapDataInfo>();
				
				var array:Array = UIManager.searchChild(mc,MovieClip);
				array.push(mc);
				var totalFrame:int = 0;
				
				for(var i:int = 0;i<array.length;i++){
					if((array[i] as MovieClip).totalFrames == 1){
						array.splice(i,1);
						i--;
					}else{
						(array[i] as MovieClip).gotoAndStop(1);
						if((array[i] as MovieClip).totalFrames>totalFrame){
							totalFrame = (array[i] as MovieClip).totalFrames;
						}
					}
					
				}
				var bitmapDataInfo1:BitmapDataInfo = BitmapDataUtils.cacheBitmap(mc);
				v_bitInfo.push(bitmapDataInfo1);
				
				for(var j:int = 2;j<=totalFrame;j++){
					
					for(var m:int = 0;m<array.length;m++){

						if((array[m] as MovieClip).totalFrames>=j){
							(array[m] as MovieClip).gotoAndStop(j);
						}else{
							var nowFrame:int = j%(array[m] as MovieClip).totalFrames;
							if(nowFrame == 0){
								nowFrame = totalFrame;
							}
							(array[m] as MovieClip).gotoAndStop(nowFrame);
						}
						
					}
					var bitmapDataInfo:BitmapDataInfo = BitmapDataUtils.cacheBitmap(mc);
					v_bitInfo.push(bitmapDataInfo);
				}
			}
			return v_bitInfo;
		}
	}
}