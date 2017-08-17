package example
{
	import flash.display.Sprite;
	import flash.display.StageAlign;
	import flash.display.StageScaleMode;
	
	import UI.App;
	import UI.abstract.component.control.image.Image;
	import UI.abstract.component.control.mc.Animation;
	import UI.abstract.resources.AnCategory;
	import UI.abstract.resources.AnConst;
	import UI.abstract.resources.ResourceUtil;
	import UI.abstract.resources.item.JtaResource;
	import UI.abstract.tween.TweenManager;
	
	import example.ui.AnimationE;
	
	import map.MapContainer;
	
	import things.AnimationData;
	import things.User;
	import things.data.UsData;

	[SWF(width="1000", height="580",backgroundColor="#222222")]
	public class UIEditor extends Sprite
	{
		
		public function UIEditor()
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
			var ui1:Animation = new Animation(AnCategory.EFFECT,"440005110",0,true,1.0,AnConst.DOWN);
			ui1.x = 300;
			ui1.dir = 4;
			ui1.y = 300;
			addChild(ui1);
			return;
			/*var usData:UsData = new UsData();
			var user:User = new User();
			user.create(usData);
			addChild(user);
			user.x = 300;
			user.y = 300;
			return;*/
			/*var ui1:AnimationData = new AnimationData(AnCategory.MOUNTS,"601200001",21);
			ui1.x = 300;
			ui1.dir = 4;
			ui1.y = 300;
			addChild(ui1);
			return;*/
			var ui:AnimationE = new AnimationE();
			addChild(ui);
			return;
			//var com:MovieClipE = new MovieClipE();
			//addChild(com);
			//return;
			/*
			ui/atlas.atlas?names=flight_
			ui/mySpritesheet.atlas?names=fly_
			*/
			
			/*var image:Image = new Image();
			image.url = ResourceUtil.getAnimationBitmapData5(ResourceUtil.getAnimationURL(AnCategory.NPC,"500000025",0),2,3,2);
			addChild(image);*/
			//App.loader.load(ResourceUtil.getAnimationURL(AnCategory.EFFECT,"400000060",0),onComplete);
		}
		private function onComplete(res:JtaResource):void{
			//App.loader.addUseNumber(res);
			
		}
	}
}