package
{
	import flash.geom.Point;
	
	import _45degrees.com.friendsofed.isometric.ActivityThing;
	import _45degrees.com.friendsofed.isometric.Data;
	import _45degrees.com.friendsofed.isometric.Point3D;
	
	import _astar.Node;

	public interface IGame
	{
		function disConnectSocket():void;
		function enterGame(sceneId:int,serverId:int,monsterId:int,data:Data,dir:int,playerPoint:Point,dead:int,campId:int):void;
		function addThing(thingId:int,point3d:Point3D,speedData:Data,dir:int,monsterId:int,dead:int,campId:int,enemy:int,isStory:Boolean = false):void;
		function thingMove(thingId:int,point3d:Point3D,path:Array,dir:int,nowNode:Node,isStory:Boolean = false):void;
		function removeThing(thingId:int):void;
		function attack(thingId:int,skillId:int,targetId:int,point:Point3D,dir:int,complete:int,flyThingId:int,isStory:Boolean = false):void;
		function outScene():void;
		function skillResult(result:int,skillId:int,attackNum:int,attackAndSkillNum:int,targetId:int,position:Point3D,type:int,flyThingId:int):void;
		function skillDamage(type:int,skillId:int,serverId:int,damage:int):void;
		function monsterFollowUser(id:int,position:Point3D,dir:int,toTargetId:int):void;
		function monsterGoBack(id:int,position:Point3D,dir:int,node:Node):void;
		function addBuff(targetId:int,buffArray:Array,length:int,isStory:Boolean = false):void;
		function delBuff(targetId:int,buffArray:Array,length:int,isStory:Boolean = false):void;
		function flashTo(targetId:int,position:Point3D,dir:int,skillId:int,isClearUseSkill:int,isStory:Boolean = false):void;
		function dead(targetId:int):void;
		function life(targetId:int,hp:int):void;
		function skillComplete(targetId:int,skillId:int):void;
		function readSkillComplete():void;
		function useSkill(skillId:int):void;
		function startLoading(sceneId:int,monsterId:int):void;
		function affectBySkill(targetId:int,skillId:int,toPosition:Point3D):void;
		function removeFlyThing(thingId:int):void;
		function flyThingChangeTarget(thingId:int,targetId:int):void;
		function changeScale():void;
		function omnislashComplete(targetId:int,skillId:int):void;
		function omnislashChange(targetId:int,skillId:int,toTargetId:int,toPosition:Point3D):void;
		function endStory(isDispose:Boolean = false):void;
		function enterStory(storyId:int):void;
		function speak(drawnIsoBox:ActivityThing,speak:String):void;
		function closeSpeak():void;
		function startTrace(type:int,target:*,time:Number):void;
		function addLoading():void;
		function removeLoading():void;
		function setCamera(drawnIsoBox:ActivityThing):void;
		function clearCamera():void;
		
	}
}