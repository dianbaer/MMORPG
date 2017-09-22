package ui
{
	import flash.events.Event;
	
	import UI.abstract.component.control.container.Box;
	import UI.abstract.component.control.image.Image;
	import UI.theme.defaulttheme.text.Label;
	
	public class Speak extends Box
	{
		public var label1:Label;
		private var img:Image;
		public function Speak()
		{
			super();
			addEventListener(Event.ADDED_TO_STAGE,onAddedToStage);
		}
		private function onAddedToStage(event:Event):void{
			removeEventListener(Event.ADDED_TO_STAGE,onAddedToStage);
			stage.addEventListener(Event.RESIZE,onResize);
			
		
			
			label1 = new Label();
			label1.wordWrap = true;
			label1.color = 0xff0000;
			//label1.text = "服务器只有1兆带宽，请耐心等待，1分钟左右";
			label1.setSize(200,20);
			addChild(label1);
			
			img = new Image();
			addChild(img);
			onResize();
		}
		public function setText(text:String,npcHalf:int):void{
			label1.text = text;
			img.url = "npcHalf/"+npcHalf+".png";
		}
		public function onResize(event:Event = null):void{
			//可能不在舞台上
			
			if(stage){
				graphics.clear();
				graphics.beginFill(0x000000,0.5);
				graphics.drawRect(0,stage.stageHeight/4*3,stage.stageWidth,stage.stageHeight/4);
				graphics.endFill();
				
				label1.y = stage.stageHeight/4*3;
				label1.x = 0;
				img.x = 0;
				img.y = label1.y-280;
			}else{
				addEventListener(Event.ADDED_TO_STAGE,toResize);
			}
		}
		private function toResize(event:Event):void{
			removeEventListener(Event.ADDED_TO_STAGE,toResize);
			onResize();
		}
		
		override public function dispose():void{
			
			stage.removeEventListener(Event.RESIZE,onResize);
			removeEventListener(Event.ADDED_TO_STAGE,toResize);
			super.dispose();
		}
	}
}