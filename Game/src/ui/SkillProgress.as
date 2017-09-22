package ui
{
	
	import UI.abstract.component.control.container.Container;
	import UI.abstract.utils.ColorFilters;
	import UI.theme.defaulttheme.ProgressBar;
	
	import gui.animation.IAnimatable;
	import gui.animation.JugglerManager;
	
	public class SkillProgress extends Container implements IAnimatable
	{
		private var progressBar:ProgressBar;
		private var nowTime:Number;
		private var skillData:Object;
		public function SkillProgress()
		{
			super();
			progressBar = new ProgressBar();
			progressBar.setSize(200,20);
			progressBar.isShowInfo = true;
			progressBar.label.color = 0x00ff00;
			progressBar.label.filters = [ColorFilters.colour_Black];
			addChild(progressBar);
			visible = false;
		}
		public function start(skillData:Object):void{
			
			JugglerManager.fourJuggler.remove(this);
			visible = true;
			nowTime = JugglerManager.processTime;
			this.skillData = skillData;
			
			progressBar.max = skillData["read"];
			
			progressBar.text = skillData["name"];
			progressBar.value = 0;
			
			JugglerManager.fourJuggler.add(this);
		}
		public function advanceTime(time:Number):void{
			onFrame();
		}
		private function onFrame():void{
			var value:Number = JugglerManager.processTime-nowTime;
			progressBar.value = value;
			if(progressBar.value == progressBar.max){
				cannel();
			}
		}
		public function cannel():void{
			
			JugglerManager.fourJuggler.remove(this);
			visible = false;
			skillData = null;
		}
		override public function get width():Number{
			return progressBar.width;
		}
		override public function get height():Number{
			return progressBar.height;
		}
	}
}