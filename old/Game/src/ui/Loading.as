package ui
{
	import flash.events.Event;
	
	import UI.App;
	import UI.abstract.component.control.container.Box;
	import UI.abstract.component.control.image.Image;
	import UI.abstract.resources.item.ImageResource;
	import UI.abstract.resources.loader.MultiLoader;
	import UI.abstract.utils.ColorFilters;
	import UI.theme.defaulttheme.ProgressBar;
	import UI.theme.defaulttheme.Skin;
	import UI.theme.defaulttheme.text.Label;
	
	import gui.animation.IAnimatable;
	import gui.animation.JugglerManager;
	
	public class Loading extends Box implements IAnimatable
	{
		private var img:Image;
		public var progressBar:ProgressBar;
		private var multiLoader:MultiLoader;
		public var label1:Label;
		public function Loading()
		{
			super();
			addEventListener(Event.ADDED_TO_STAGE,onAddedToStage);
		}
		private function onAddedToStage(event:Event):void{
			removeEventListener(Event.ADDED_TO_STAGE,onAddedToStage);
			stage.addEventListener(Event.RESIZE,onResize);
			var arr : Array = Skin.getList(Skin.PROGRESSBAR );
			arr.push("back.jpg");
			App.loader.loadList(arr,loadComplete);
		}
		private function loadComplete():void{
			var imgResource:ImageResource = App.loader.getResource("back.jpg") as ImageResource;
			img = new Image();
			img.url = "back.jpg";
			img.width = imgResource.bitmapData.width;
			img.height = imgResource.bitmapData.height;
			addChild(img);
			progressBar = new ProgressBar();
			progressBar.setSize(200,20);
			progressBar.isShowInfo = true;
			progressBar.label.color = 0x00ff00;
			progressBar.label.filters = [ColorFilters.colour_Black];
			addChild(progressBar);
			label1 = new Label();
			label1.color = 0xff0000;
			label1.text = "服务器只有1兆带宽，请耐心等待，1分钟左右";
			label1.setSize(200,20);
			addChild(label1);
			onResize();
			dispatchEvent(new Event(Event.COMPLETE));
		}
		public function onResize(event:Event = null):void{
			//可能不在舞台上
			
			if(stage && img){
				setSize(stage.stageWidth,stage.stageHeight);
				img.x = (stage.stageWidth-img.width)/2;
				img.y = (stage.stageHeight-img.height)/2;
				progressBar.x = (stage.stageWidth-progressBar.width)/2;
				progressBar.y = (stage.stageHeight-progressBar.height)/2;
				label1.y = progressBar.y+progressBar.height;
				label1.x = progressBar.x-(label1.textFieldWidth-progressBar.width);
			}else{
				addEventListener(Event.ADDED_TO_STAGE,toResize);
			}
		}
		private function toResize(event:Event):void{
			removeEventListener(Event.ADDED_TO_STAGE,toResize);
			onResize();
		}
		public function setProcess(multiLoader:MultiLoader,str:String):void{
			this.multiLoader = multiLoader;
			progressBar.max = multiLoader.currNum;
			progressBar.text = str;
			progressBar.value = 0;
			JugglerManager.fourJuggler.add(this);
		}
		public function advanceTime(time:Number):void{
			onFrame();
		}
		private function onFrame():void{
			if(multiLoader.currNum == 0){
				JugglerManager.fourJuggler.remove(this);
				this.multiLoader = null;
				return;
			}
			progressBar.value = progressBar.max-multiLoader.currNum;
		}
		override public function dispose():void{
			img = null;
			progressBar = null;
			stage.removeEventListener(Event.RESIZE,onResize);
			super.dispose();
		}
	}
}