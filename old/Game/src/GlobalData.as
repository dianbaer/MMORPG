package
{
	import flash.display.Sprite;
	
	import _45degrees.com.friendsofed.isometric.DrawnIsoTile;
	import _45degrees.com.friendsofed.isometric.IsoWorld;
	import _45degrees.com.friendsofed.isometric.PitchOnFrame;
	import _45degrees.com.friendsofed.isometric.Point3D;
	import _45degrees.com.friendsofed.isometric.SkillEffect;
	import _45degrees.com.friendsofed.isometric.SkillScope;
	
	import _astar.Grid;
	
	import ui.Loading;
	
	public class GlobalData extends Sprite
	{
		public static var game:IGame;
		public static var map1:IsoWorld;
		
		public static var cellWidth:int = 0;
		
		public static var grid:Grid;
		
		public static var skillData:Object;
		public static var monsterData:Object;
		public static var buffData:Object;
		public static var campData:Object;
		public static var configData:Object;
		
		//正在被技能影响着
		//1.闪现是不能走路了，闪现先发过去，再发过去走路，客户端被强制拉过去后果难以想象
		//2.闪现是不能大跳，先闪现，然后服务器没反应，点击大跳。
		
		//1.闪现时，是可以闪现的
		//闪现必须以计数的方式，连续闪两下，然后服务器返回一下，如果放开了限制，后果难以想象
		//闪现的过程中，被击飞没关系，先闪现，然后被击飞，只可能击飞的方向是反方向，问题不大。
		public static var nowBeEffectOnSkill:int = 0;
		
		//距离性技能
		//1.任何状态的冲锋。2.需要服务器施放的大跳
		//等服务器施放时，已经走路了，这时客户端会冲过去，但是服务器认为这是技能施放完之后的寻路，所以会在技能施放完之后，广播出去，造成位置偏差太大
		public static var nowBeEffectOnSkillDistance:int = 0;
		//无敌斩，无敌斩锁定所有技能，寻路，攻击。
		public static var nowBeEffectOnSkillOmnislash:int = 0;
		
		public static var loading:Loading;
		public static var role:Object;
		public static var chooseId:int;
		public static var chooseCamp:Object;
		
		public static var iscdModel:Boolean = true;
		public static var nowTargetEffect:SkillEffect;
		public static var scale:Number;
		public static var pitchOnFrame:PitchOnFrame;
		public static var skillScope:SkillScope;
		public static var shadow:DrawnIsoTile;
		//在剧情里
		public static var inStory:Boolean = false;
		public static var mapStory:IsoWorld;
		/*********************追踪*******************************/
		public static var isTrace:Boolean = false;
		//追踪的类型
		public static var traceType:int = 0;
		//追踪的目标，点追踪或者目标追踪
		public static var traceTarget:*;
		public static var start:Point3D = new Point3D();
		public static var traceDistance:Number = 0;
		public static var moveD:Number = 0;
		public static var speed:Number = 0;
		/*********************追踪*******************************/
		
		public static var ip:String;
		public static var port:int;
		public static var sceneId:int;
		public function GlobalData()
		{
			super();
		}
	}
}