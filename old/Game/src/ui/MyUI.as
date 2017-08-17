package ui
{
	
	import UI.abstract.component.control.container.Box;
	import UI.theme.defaulttheme.ProgressBar;
	
	import _45degrees.com.friendsofed.isometric.ActivityThing;
	
	import gui.animation.IAnimatable;
	import gui.animation.JugglerManager;
	
	public class MyUI extends Box implements IAnimatable
	{
		private var hp:ProgressBar;
		private var target:ActivityThing;
		public function MyUI()
		{
			super();
			hp = new ProgressBar();
			hp.setSize(200,20);
			hp.isShowInfo = true;
			addChild(hp);
		}
		public function setTarget(target:ActivityThing):void{
			this.target = target;
			JugglerManager.fourJuggler.remove(this);
			if(this.target != null){
				JugglerManager.fourJuggler.add(this);
			}else{
				visible = false;
			}
			
		}
		public function advanceTime(time:Number):void{
			onFrame();
		}
		private function onFrame():void{
			if(target && !target.isDispose){
				if(hp.max != target.speedData.maxHp){
					hp.max = target.speedData.maxHp;
				}
				if(hp.value != target.speedData.hp){
					hp.value = target.speedData.hp;
				}
				visible = true;
				
			}else{
				target = null;
				visible = false;
				JugglerManager.fourJuggler.remove(this);
				
			}
		}
		override public function get width():Number{
			return getAllChildrenSize().x;
		}
		override public function get height():Number{
			return getAllChildrenSize().y;
		}
	}
}