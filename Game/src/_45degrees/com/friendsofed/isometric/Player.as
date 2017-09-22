package _45degrees.com.friendsofed.isometric
{
	

	public class Player extends ActivityThing
	{
		
		public function Player(size:Number, monster:Object, monsterSkillData:Object)
		{
			super(size, monster,monsterSkillData);
		}
		override public function dispose():void{
			super.dispose();
			toPool(this);
		}
		private static var sPlayerPool:Vector.<Player> = new <Player>[];
		
		/** @private */
		public static function fromPool(size:Number, monster:Object, monsterSkillData:Object):Player
		{
			if (sPlayerPool.length) return sPlayerPool.pop().resetActivityThing(size, monster,monsterSkillData) as Player;
			else return new Player(size, monster,monsterSkillData);
		}
		
		/** @private */
		public static function toPool(player:Player):void
		{
			sPlayerPool[sPlayerPool.length] = player;
		}
	}
}