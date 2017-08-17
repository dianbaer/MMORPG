package example
{
	import UI.App;
	import UI.abstract.component.control.base.UIComponent;
	import UI.abstract.component.control.mc.MovieClip;
	import UI.abstract.resources.ResourceUtil;
	import UI.abstract.tween.TweenManager;
	import UI.abstract.utils.Stats;
	
	import flash.display.Bitmap;
	import flash.display.BitmapData;
	import flash.display.Sprite;
	import flash.display.StageAlign;
	import flash.display.StageScaleMode;
	import flash.events.Event;
	import flash.geom.Matrix;
	import flash.geom.Point;
	
	[SWF(width="1000", height="580")]
	public class BitmapDataCopyPixels extends Sprite
	{
		protected var canvas:BitmapData;
		private var array:Array = new Array();
		private var bmt:BitmapData = new BitmapData(1, 1, true, 0x00000000);
		public function BitmapDataCopyPixels()
		{
			super();
			stage.align = StageAlign.TOP_LEFT;
			stage.scaleMode = StageScaleMode.NO_SCALE;
			
			App.init( this );
			TweenManager.initClass();
			createUi();
		}
		private function createUi() : void
		{
			canvas = new BitmapData(stage.stageWidth, stage.stageHeight, true, 0x0);
			addChildAt(new Bitmap(canvas), 0);
			
			for(var i:int =0;i<1000;i++){
				var mc:MovieClip = new MovieClip(ResourceUtil.getResourcePathByXML("ui/preOpenSWF.xml",ResourceUtil.MC,"image "),24);
				mc.x = int(Math.random()*(stage.stageWidth-100));
				mc.y = int(Math.random()*(stage.stageHeight-100));
				array.push(mc);
				//addChild(mc);
			}
			var star:Stats = new Stats();
			star.x = 600;
			addChild(star);
			
			addEventListener(Event.ENTER_FRAME,onEnterFrame);
			stage.addEventListener(Event.RESIZE,onResize);
		}
		private function onEnterFrame(event:Event = null):void{
			var num:int = 0;
			for(var i:int = array.length-1;i>=0;i--){
				(array[i] as MovieClip)._bitmap.filters = [];
				if( 
					num == 0
					&& array[i]._bitmap.bitmapData
					&& array[i].x+array[i]._bitmap.x<=stage.mouseX
					&& array[i].x+array[i]._bitmap.x+array[i]._bitmap.bitmapData.width>stage.mouseX
					&& array[i].y+array[i]._bitmap.y<=stage.mouseY
					&& array[i].y+array[i]._bitmap.y+array[i]._bitmap.bitmapData.height>stage.mouseY
				){
					bmt.setPixel32(0, 0, 0x00FFFFFF);
					bmt.draw(array[i], new Matrix(1,0,0,1,-array[i].mouseX,-array[i].mouseY));
					if (bmt.getPixel32(0, 0) > 0x00FFFFFF){
						(array[i] as MovieClip)._bitmap.filters = [UIComponent.grayFilter];
						num++;
					}
					//
					/*if((array[i] as MovieClip)._bitmap.bitmapData.hitTest(new Point(array[i].x+array[i]._bitmap.x,array[i].y+array[i]._bitmap.y),255,new Point(stage.mouseX,stage.mouseY))){
					
					
					}*/
					
					
					
				}
			}
			
			canvas.fillRect(canvas.rect, 0x0);
			for(var i:int = 0;i<array.length;i++){
				if(array[i]._bitmap.bitmapData){
					if((array[i] as MovieClip)._bitmap.filters.length>0){
						canvas.draw((array[i] as MovieClip)._bitmap,new Matrix(1,0,0,1,array[i].x+array[i]._bitmap.x,array[i].y+array[i]._bitmap.y));
					}else{
						canvas.copyPixels(array[i]._bitmap.bitmapData, array[i]._bitmap.bitmapData.rect, new Point(array[i].x+array[i]._bitmap.x, array[i].y+array[i]._bitmap.y), null, null, true);
					}
					
				}
			}
		}
		private function onResize(event:Event):void{
			canvas = new BitmapData(stage.stageWidth, stage.stageHeight, true, 0x0);
			(getChildAt(0) as Bitmap).bitmapData = canvas;
			onEnterFrame()
			
		}
	}
}