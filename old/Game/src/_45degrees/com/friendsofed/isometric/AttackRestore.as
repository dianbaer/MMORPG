package _45degrees.com.friendsofed.isometric
{
	import gui.mc.Animation;

	public class AttackRestore
	{
		public var upAttackTime:int;
		public var skillMc:Animation;
		public var flySkillEffect:FlySkillEffect;
		public var skillEffect:SkillEffect;
		public var readMc:Animation;
		public var position:Point3D;
		public var attackAndSkillNum:int = 0;
		public var isUseSkill:Boolean = false;
		public function AttackRestore()
		{
			reset();
		}
		public function reset():AttackRestore{
			return this;
		}
		public function dispose():void{
			upAttackTime = 0;
			skillMc = null;
			flySkillEffect = null;
			skillEffect = null;
			readMc = null;
			if(position){
				position.setValue(0,0,0);
			}
			attackAndSkillNum = 0;
			isUseSkill = false;
			toPool(this);
		}
		private static var sAttackRestorePool:Vector.<AttackRestore> = new <AttackRestore>[];
		
		/** @private */
		public static function fromPool():AttackRestore
		{
			if (sAttackRestorePool.length) return sAttackRestorePool.pop().reset();
			else return new AttackRestore();
		}
		
		/** @private */
		public static function toPool(attackRestore:AttackRestore):void
		{
			sAttackRestorePool[sAttackRestorePool.length] = attackRestore;
		}
	}
}