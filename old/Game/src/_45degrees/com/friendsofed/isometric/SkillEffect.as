package _45degrees.com.friendsofed.isometric
{
	import flash.events.Event;
	
	import gui.mc.Animation;

	public class SkillEffect extends IsoObject
	{
		public function SkillEffect(size:Number)
		{
			super(size);
			interactive = false;
		}
		public function addEvent():void{
			(getChildAt(0) as Animation).addEventListener(Event.COMPLETE,onSkillComplete);
		}
		public function onSkillComplete(event:Event):void{
			dispose();
		}
		override public function dispose():void{
			var animation:Animation = getChildAt(0) as Animation;
			(getChildAt(0) as Animation).removeEventListener(Event.COMPLETE,onSkillComplete);
			animation.dispose();
			if(parent){
				(parent.parent as IsoWorld).removeChildToWorld(this);
			}
			super.dispose();
			toPool(this);
		}
		/*
		override public function reset(size:Number):IsoObject{
			super.reset(size);
			return this;
		}
		*/
		private static var sSkillEffectPool:Vector.<SkillEffect> = new <SkillEffect>[];
		
		/** @private */
		public static function fromPool(size:Number):SkillEffect
		{
			if (sSkillEffectPool.length) return sSkillEffectPool.pop().reset(size) as SkillEffect;
			else return new SkillEffect(size);
		}
		
		/** @private */
		public static function toPool(skillEffect:SkillEffect):void
		{
			sSkillEffectPool[sSkillEffectPool.length] = skillEffect;
		}
	}
}