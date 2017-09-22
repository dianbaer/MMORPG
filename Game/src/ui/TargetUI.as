package ui
{
	
	import UI.abstract.component.control.container.Box;
	import UI.abstract.utils.ColorFilters;
	import UI.theme.defaulttheme.ProgressBar;
	
	import _45degrees.com.friendsofed.isometric.ActivityThing;
	
	import gui.animation.IAnimatable;
	import gui.animation.JugglerManager;
	
	public class TargetUI extends Box implements IAnimatable
	{
		private var hp:ProgressBar;
		private var readSkill:ProgressBar;
		private var target:ActivityThing;
		public function TargetUI()
		{
			super();
			hp = new ProgressBar();
			hp.setSize(200,20);
			hp.isShowInfo = true;
			addChild(hp);
			
			readSkill = new ProgressBar();
			readSkill.setSize(200,20);
			readSkill.isShowInfo = true;
			readSkill.visible = false;
			readSkill.y = hp.height+23;
			
			readSkill.label.color = 0x00ff00;
			readSkill.label.filters = [ColorFilters.colour_Black];
			addChild(readSkill);
			visible = false;
		}
		public function setTarget(target:ActivityThing):void{
			if(
				(this.target != null && target == null) 
				||(this.target == null && target != null)
				||(this.target != null && target != null && this.target.sortId != target.sortId)){
				
				this.target = target;
				JugglerManager.fourJuggler.remove(this);
				if(this.target != null){
					JugglerManager.fourJuggler.add(this);
					
				}else{
					visible = false;
				}
			}else{
				return;
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
				return;
			}
			if(target.nowReadSkill != null){
				readSkill.visible = true;
				readSkill.text = target.nowReadSkill["name"];
				if(target.nowReadSkill["read"] != readSkill.max){
					readSkill.max = target.nowReadSkill["read"];
				}
				var value:int = JugglerManager.processTime-target.nowReadTime;
				if(readSkill.value != value){
					readSkill.value = value;
				}
			}else{
				readSkill.visible = false;
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