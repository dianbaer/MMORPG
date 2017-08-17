package
{
	import flash.ui.Keyboard;
	import flash.utils.Dictionary;

	public class KeyMap
	{
		public static const SKILL:String = "skill";
		private var keyDict:Dictionary = new Dictionary();
		public function KeyMap()
		{
			keyDict[Keyboard.NUMBER_1] = SKILL;
			keyDict[Keyboard.NUMBER_2] = SKILL;
			keyDict[Keyboard.NUMBER_3] = SKILL;
			keyDict[Keyboard.NUMBER_4] = SKILL;
			
			keyDict[Keyboard.Q] = SKILL;
			keyDict[Keyboard.E] = SKILL;
			keyDict[Keyboard.R] = SKILL;
			keyDict[Keyboard.F] = SKILL;
			keyDict[Keyboard.Z] = SKILL;
		}
		/**
		 * 这个键是技能键
		 */
		public function keyIsSkill(num:int):Boolean{
			if(keyDict[num] != null && keyDict[num] == SKILL){
				return true;
			}else {
				return false;
			}
		}
	}
}