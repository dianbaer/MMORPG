package proxy
{
	import UI.abstract.utils.CommonPool;
	
	import _45degrees.com.friendsofed.isometric.Data;
	import _45degrees.com.friendsofed.isometric.Point3D;
	
	import _astar.Node;
	
	import flash.geom.Point;
	
	import net.CodeEvent;
	import net.TcpPacket;
	
	import org.puremvc.as3.patterns.proxy.Proxy;
	
	public class PlayerProxy extends Proxy
	{
		public static const NAME:String = "PlayerProxy";
		private var socketProxy:SocketProxy;
		//这几个输出传到游戏里，只能用作赋值，不能被保存使用
		private static var helpPos:Point3D = new Point3D();
		private static var data1:Data = new Data();
		private static var helpPoint:Point = new Point();
		private static var helpArray:Array = new Array();
		public function PlayerProxy()
		{
			super(NAME);
			socketProxy = facade.retrieveProxy(SocketProxy.NAME) as SocketProxy;
			socketProxy.socketClient.addEventListener(CodeEvent.CODE2,loginReturn);
			socketProxy.socketClient.addEventListener(CodeEvent.CODE4,thingEnterGame);
			socketProxy.socketClient.addEventListener(CodeEvent.CODE5,thingMove);
			socketProxy.socketClient.addEventListener(CodeEvent.CODE6,thingOutGame);
			socketProxy.socketClient.addEventListener(CodeEvent.CODE8,attackReturn);
			socketProxy.socketClient.addEventListener(CodeEvent.CODE9,outScene);
			socketProxy.socketClient.addEventListener(CodeEvent.CODE11,skillResult);
			socketProxy.socketClient.addEventListener(CodeEvent.CODE12,skillDamage);
			socketProxy.socketClient.addEventListener(CodeEvent.CODE13,monsterFollowUser);
			socketProxy.socketClient.addEventListener(CodeEvent.CODE14,monsterGoBack);
			socketProxy.socketClient.addEventListener(CodeEvent.CODE15,addBuff);
			socketProxy.socketClient.addEventListener(CodeEvent.CODE16,delBuff);
			socketProxy.socketClient.addEventListener(CodeEvent.CODE17,refreshBuff);
			socketProxy.socketClient.addEventListener(CodeEvent.CODE18,flashTo);
			socketProxy.socketClient.addEventListener(CodeEvent.CODE19,dead);
			socketProxy.socketClient.addEventListener(CodeEvent.CODE20,life);
			socketProxy.socketClient.addEventListener(CodeEvent.CODE21,skillComplete);
			socketProxy.socketClient.addEventListener(CodeEvent.CODE22,readSkillComplete);
			socketProxy.socketClient.addEventListener(CodeEvent.CODE23,startLoading);
			socketProxy.socketClient.addEventListener(CodeEvent.CODE26,affectBySkill);
			socketProxy.socketClient.addEventListener(CodeEvent.CODE27,flyThingOutGame);
			socketProxy.socketClient.addEventListener(CodeEvent.CODE28,flyThingChangeTarget);
			socketProxy.socketClient.addEventListener(CodeEvent.CODE29,omnislashComplete);
			socketProxy.socketClient.addEventListener(CodeEvent.CODE30,omnislashChange);
		}
		/**
		 * 移动
		 */
		public function move(x:Number,y:Number,z:Number,dir:int,path:Array):void{
			var packet:TcpPacket = TcpPacket.fromPool(CodeEvent.CODE3);
			packet.writeDouble(x);
			//trace(x+","+z);
			packet.writeDouble(y);
			packet.writeDouble(z);
			packet.writeByte(dir);
			//这个path的数组，不能被清理，因为移动的玩家还需要这个数组
			var length:int = path.length;
			packet.writeInt(length);
			for(var i:int = 0;i<length;i++){
				var node:Node = path[i];
				packet.writeInt(node.x);
				packet.writeInt(node.y);
			}
			//var point:Point = IsoUtils.isoToScreen(new Point3D(x,y,z));
			//trace(point.x+":"+point.y);
			//trace(Point.distance(point,new Point(path[1].x,path[1].y)));
			socketProxy.socketClient.send(packet);
		}
		/**
		 * 攻击
		 */
		public function attack(skillId:int,type:int,target:*,dir:int = 0,attackNum:int = 0,attackAndSkillNum:int = 0):void{
			var packet:TcpPacket = TcpPacket.fromPool(CodeEvent.CODE7);
			packet.writeInt(skillId);
			packet.writeInt(type);
			if(type == 2){
				//这个暂时先不清理，game->player->这里的，应该game里面清理
				var point:Point3D = target as Point3D;
				packet.writeDouble(point.x);
				packet.writeDouble(point.y);
				packet.writeDouble(point.z);
			}else{
				packet.writeInt(target);
			}
			packet.writeByte(dir);
			packet.writeInt(attackNum);
			packet.writeInt(attackAndSkillNum);
			socketProxy.socketClient.send(packet);
		}
		/**
		 * 切换场景
		 */
		public function changeScene(sceneId:int):void{
			var packet:TcpPacket = TcpPacket.fromPool(CodeEvent.CODE10);
			packet.writeInt(sceneId);
			socketProxy.socketClient.send(packet);
		}
		/**
		 * 自己进入场景
		 */
		private function loginReturn(event:CodeEvent):void{
			
			var x:int = event.data.readInt();
			var z:int = event.data.readInt();
			var dir:int = event.data.readByte();
			//var data1:Data = new Data();
			data1.moveV = event.data.readDouble();
			data1.jumpVerticalV = event.data.readDouble();
			
			data1.jumpVerticalA = event.data.readDouble();
			data1.attackSpeed = event.data.readDouble();
			data1.hp = event.data.readInt();
			data1.maxHp = event.data.readInt();
			data1.att = event.data.readInt();
			data1.def = event.data.readInt();
			var dead:int = event.data.readByte();
			var serverId:int = event.data.readInt();
			var monsterId:int = event.data.readInt();
			var sceneId:int = event.data.readInt();
			var campId:int = event.data.readInt();
			//var playerPoint:Point = new Point();
			helpPoint.x = x;
			helpPoint.y = z;
			var helpObj:Object = CommonPool.fromPoolObject();
			helpObj.sceneId = sceneId;
			helpObj.serverId = serverId;
			helpObj.monsterId = monsterId;
			helpObj.data = data1;
			helpObj.dir = dir;
			helpObj.playerPoint = helpPoint;
			helpObj.dead = dead;
			helpObj.campId = campId;
			sendNotification(NotiConst.ENTER_GAME,helpObj);
			
		}
		/**
		 * 玩家攻击返回
		 */
		private function attackReturn(event:CodeEvent):void{
			var id:int = event.data.readInt();
			var skillId:int = event.data.readInt();
			var type:int = event.data.readInt();
			var targetId:int = 0;
			if(type == 2){
				helpPos.x = event.data.readDouble();
				helpPos.y = event.data.readDouble();
				helpPos.z = event.data.readDouble();
			}else{
				targetId = event.data.readInt();
				if(targetId != 0){
					helpPos.x = event.data.readDouble();
					helpPos.y = event.data.readDouble();
					helpPos.z = event.data.readDouble();
				}
			}
			var dir:int = event.data.readByte();
			var complete:int = event.data.readByte();
			var flyThingId:int = event.data.readInt();
			var helpObj:Object = CommonPool.fromPoolObject();
			helpObj.id = id;
			helpObj.skillId = skillId;
			helpObj.position = helpPos;
			helpObj.targetId = targetId;
			helpObj.dir = dir;
			helpObj.complete = complete;
			helpObj.flyThingId = flyThingId;
			sendNotification(NotiConst.ATTACK,helpObj);
		}
		/**
		 * 其他玩家进入
		 */
		private function thingEnterGame(event:CodeEvent):void{
			var id:int = event.data.readInt();
			var monsterId:int = event.data.readInt();
			var x:Number = event.data.readDouble();
			var y:Number = event.data.readDouble();
			var z:Number = event.data.readDouble();
			var dir:int = event.data.readByte();
			//var data1:Data = new Data();
			data1.moveV = event.data.readDouble();
			data1.jumpVerticalV = event.data.readDouble();
			
			data1.jumpVerticalA = event.data.readDouble();
			data1.attackSpeed = event.data.readDouble();
			data1.hp = event.data.readInt();
			data1.maxHp = event.data.readInt();
			data1.att = event.data.readInt();
			data1.def = event.data.readInt();
			var dead:int = event.data.readByte();
			var campId:int = event.data.readInt();
			var enemy:int = event.data.readByte();
			//var point:Point3D = new Point3D(x,y,z);
			helpPos.setValue(x,y,z);
			var helpObj:Object = CommonPool.fromPoolObject();
			helpObj.id = id;
			helpObj.position = helpPos;
			helpObj.data = data1;
			helpObj.dir = dir;
			helpObj.monsterId = monsterId;
			helpObj.dead = dead;
			helpObj.campId = campId;
			helpObj.enemy = enemy;
			sendNotification(NotiConst.ADD_THING,helpObj);
			
		}
		/**
		 * 其他玩家移动
		 */
		private function thingMove(event:CodeEvent):void{
			//if(!Game1.grid){
			//	return;
			//}
			var id:int = event.data.readInt();
			var x:Number = event.data.readDouble();
			var y:Number = event.data.readDouble();
			var z:Number = event.data.readDouble();
			var dir:int = event.data.readByte();
			var length:int = event.data.readInt();
			//这个最后会传到玩家那，被使用
			var path:Array = CommonPool.fromPoolArray();
			for(var i:int = 0;i<length;i++){
				var nodeX:int = event.data.readInt();
				var nodeY:int = event.data.readInt();
				
				var node:Node = GlobalData.grid.getNode(nodeX,nodeY);
				
				//node.x = event.data.readInt();
				//node.y = event.data.readInt();
				path[path.length] = node;
			}
			var nowNode:Node;
			if(event.data.bytesAvailable > 0){
				var nodeXX:int = event.data.readInt();
				var nodeYY:int = event.data.readInt();
				
				nowNode = GlobalData.grid.getNode(nodeXX,nodeYY);
				
				//nowNode.x = event.data.readInt();
				//nowNode.y = event.data.readInt();
			}
			//var point:Point3D = new Point3D(x,y,z);
			helpPos.setValue(x,y,z);
			var helpObj:Object = CommonPool.fromPoolObject();
			helpObj.id = id;
			helpObj.position = helpPos;
			helpObj.path = path;
			helpObj.dir = dir;
			helpObj.nowNode = nowNode;
			sendNotification(NotiConst.THING_MOVE,helpObj);
			
		}
		/**
		 * 其他玩家离开
		 */
		private function thingOutGame(event:CodeEvent):void{
			var id:int = event.data.readInt();
			var helpObj:Object = CommonPool.fromPoolObject();
			helpObj.id = id;
			sendNotification(NotiConst.REMOVE_THING,helpObj);
		}
		/**
		 * 自己离开场景
		 */
		private function outScene(event:CodeEvent):void{
			sendNotification(NotiConst.OUT_SCENE);
		}
		private function skillResult(event:CodeEvent):void{
			var result:int = event.data.readInt();
			var skillId:int = event.data.readInt();
			
			var attackNum:int = event.data.readInt();
			var attackAndSkillNum:int = event.data.readInt();
			var helpObj:Object = CommonPool.fromPoolObject();
			helpObj.result = result;
			helpObj.skillId = skillId;
			helpObj.attackNum = attackNum;
			helpObj.attackAndSkillNum = attackAndSkillNum;
			var type:int = event.data.readInt();
			helpObj.type = type;
			var x:Number;
			var y:Number;
			var z:Number;
			if(type == 2){
				x = event.data.readDouble();
				y = event.data.readDouble();
				z = event.data.readDouble();
				helpPos.setValue(x,y,z);
				helpObj.position = helpPos;
			}else{
				var targetId:int = event.data.readInt();
				helpObj.targetId = targetId;
				//是玩家或者怪物
				if(helpObj.result == 1 && helpObj.targetId != 0){
					x = event.data.readDouble();
					y = event.data.readDouble();
					z = event.data.readDouble();
					helpPos.setValue(x,y,z);
					helpObj.position = helpPos;
				}
			}
			if(helpObj.result == 1){
				var flyThingId:int = event.data.readInt();
				helpObj.flyThingId = flyThingId;
			}
			sendNotification(NotiConst.SKILL_RESULT,helpObj);
		}
		private function skillDamage(event:CodeEvent):void{
			var type:int = event.data.readInt();
			var skillId:int = event.data.readInt();
			var serverId:int = event.data.readInt();
			var damage:int = event.data.readInt();
			var helpObj:Object = CommonPool.fromPoolObject();
			helpObj.type = type;
			helpObj.skillId = skillId;
			helpObj.serverId = serverId;
			helpObj.damage = damage;
			sendNotification(NotiConst.SKILL_DAMAGE,helpObj);
		}
		private function monsterFollowUser(event:CodeEvent):void{
			var id:int = event.data.readInt();
			var x:Number = event.data.readDouble();
			var y:Number = event.data.readDouble();
			var z:Number = event.data.readDouble();
			var dir:int = event.data.readByte();
			var toId:int = event.data.readInt();
			//var point:Point3D = new Point3D(x,y,z);
			helpPos.setValue(x,y,z);
			var helpObj:Object = CommonPool.fromPoolObject();
			helpObj.id = id;
			helpObj.position = helpPos;
			helpObj.dir = dir;
			helpObj.toTargetId = toId;
			sendNotification(NotiConst.MONSTER_FOLLOW_USER,helpObj);
		}
		private function monsterGoBack(event:CodeEvent):void{
			//if(!Game1.grid){
			//	return;
			//}
			var id:int = event.data.readInt();
			var x:Number = event.data.readDouble();
			var y:Number = event.data.readDouble();
			var z:Number = event.data.readDouble();
			var dir:int = event.data.readByte();
			var nodeX:int = event.data.readInt();
			var nodeY:int = event.data.readInt();
			var node:Node = GlobalData.grid.getNode(nodeX,nodeY);
			
			//var point:Point3D = new Point3D(x,y,z);
			helpPos.setValue(x,y,z);
			var helpObj:Object = CommonPool.fromPoolObject();
			helpObj.id = id;
			helpObj.position = helpPos;
			helpObj.dir = dir;
			helpObj.node = node;
			sendNotification(NotiConst.MONSTER_GO_BACK,helpObj);
		}
		//这里可以传到加buff那一个长度，这样所有的buff对象就可以被充分利用了
		private function addBuff(event:CodeEvent):void{
			var targetId:int = event.data.readInt();
			var length:int = event.data.readInt();
			
			for(var i:int = 0;i<length;i++){
				if(!helpArray[i]){
					helpArray[i] = new Object();
				}
				
				helpArray[i]["id"] = event.data.readInt();
				helpArray[i]["attackId"] = event.data.readInt();
				helpArray[i]["duration"] = event.data.readInt();
			}
			var helpObj:Object = CommonPool.fromPoolObject();
			helpObj.targetId = targetId;
			helpObj.buffArray = helpArray;
			helpObj.length = length;
			sendNotification(NotiConst.ADD_BUFF,helpObj);
			
		}
		private function delBuff(event:CodeEvent):void{
			var targetId:int = event.data.readInt();
			var length:int = event.data.readInt();
			
			for(var i:int = 0;i<length;i++){
				if(!helpArray[i]){
					helpArray[i] = new Object();
				}
				helpArray[i]["id"] = event.data.readInt();
				helpArray[i]["attackId"] = event.data.readInt();
				
			}
			var helpObj:Object = CommonPool.fromPoolObject();
			helpObj.targetId = targetId;
			helpObj.buffArray = helpArray;
			helpObj.length = length;
			sendNotification(NotiConst.DEL_BUFF,helpObj);
		}
		private function refreshBuff(event:CodeEvent):void{
			var targetId:int = event.data.readInt();
			var length:int = event.data.readInt();
			
			for(var i:int = 0;i<length;i++){
				if(!helpArray[i]){
					helpArray[i] = new Object();
				}
				helpArray[i]["id"] = event.data.readInt();
				helpArray[i]["attackId"] = event.data.readInt();
				helpArray[i]["duration"] = event.data.readInt();
				
			}
			var helpObj:Object = CommonPool.fromPoolObject();
			helpObj.targetId = targetId;
			helpObj.buffArray = helpArray;
			helpObj.length = length;
			sendNotification(NotiConst.REFRESH_BUFF,helpObj);
		}
		private function flashTo(event:CodeEvent):void{
			var targetId:int = event.data.readInt();
			var x:Number = event.data.readDouble();
			var y:Number = event.data.readDouble();
			var z:Number = event.data.readDouble();
			var dir:int = event.data.readByte();
			var skillId:int = event.data.readInt();
			var isClearUseSkill:int = event.data.readInt();
			//var point:Point3D = new Point3D(x,y,z);
			helpPos.setValue(x,y,z);
			var helpObj:Object = CommonPool.fromPoolObject();
			helpObj.targetId = targetId;
			helpObj.position = helpPos;
			helpObj.dir = dir;
			helpObj.skillId = skillId;
			helpObj.isClearUseSkill = isClearUseSkill;
			sendNotification(NotiConst.FLASH,helpObj);
		}
		private function dead(event:CodeEvent):void{
			var targetId:int = event.data.readInt();
			var helpObj:Object = CommonPool.fromPoolObject();
			helpObj.targetId = targetId;
			sendNotification(NotiConst.DEAD,helpObj);
		}
		private function life(event:CodeEvent):void{
			var targetId:int = event.data.readInt();
			var hp:int = event.data.readInt();
			var helpObj:Object = CommonPool.fromPoolObject();
			helpObj.targetId = targetId;
			helpObj.hp = hp;
			sendNotification(NotiConst.LIFE,helpObj);
		}
		private function skillComplete(event:CodeEvent):void{
			var targetId:int = event.data.readInt();
			var skillId:int = event.data.readInt();
			var helpObj:Object = CommonPool.fromPoolObject();
			helpObj.targetId = targetId;
			helpObj.skillId = skillId;
			sendNotification(NotiConst.SKILL_COMPLETE,helpObj);
		}
		private function readSkillComplete(event:CodeEvent):void{
			
			sendNotification(NotiConst.READ_SKILL_COMPLETE);
		}
		private function startLoading(event:CodeEvent):void{
			var monsterId:int = event.data.readInt();
			var sceneId:int = event.data.readInt();
			var helpObj:Object = CommonPool.fromPoolObject();
			helpObj.sceneId = sceneId;
			helpObj.monsterId = monsterId;
			sendNotification(NotiConst.START_LOADING,helpObj);
		}
		/**
		 * 切换场景
		 */
		public function loadingOk():void{
			var packet:TcpPacket = TcpPacket.fromPool(CodeEvent.CODE24);
			socketProxy.socketClient.send(packet);
		}
		public function cancelBuff(buffId:int):void{
			var packet:TcpPacket = TcpPacket.fromPool(CodeEvent.CODE25);
			packet.writeInt(buffId);
			socketProxy.socketClient.send(packet);
		}
		private function affectBySkill(event:CodeEvent):void{
			var targetId:int = event.data.readInt();
			var skillId:int = event.data.readInt();
			var x:Number = event.data.readDouble();
			var y:Number = event.data.readDouble();
			var z:Number = event.data.readDouble();
			helpPos.setValue(x,y,z);
			var helpObj:Object = CommonPool.fromPoolObject();
			helpObj.targetId = targetId;
			helpObj.skillId = skillId;
			helpObj.toPosition = helpPos;
			sendNotification(NotiConst.AFFECT_BY_SKILL,helpObj);
		}
		/**
		 * 飞行道具离开场景
		 */
		private function flyThingOutGame(event:CodeEvent):void{
			var id:int = event.data.readInt();
			var helpObj:Object = CommonPool.fromPoolObject();
			helpObj.id = id;
			sendNotification(NotiConst.REMOVE_FLY_THING,helpObj);
		}
		/**
		 * 飞行道具改变目标
		 */
		private function flyThingChangeTarget(event:CodeEvent):void{
			var id:int = event.data.readInt();
			var targetId:int = event.data.readInt();
			var helpObj:Object = CommonPool.fromPoolObject();
			helpObj.id = id;
			helpObj.targetId = targetId;
			sendNotification(NotiConst.FLY_THING_CHANGE_TARGET,helpObj);
		}
		private function omnislashComplete(event:CodeEvent):void{
			var id:int = event.data.readInt();
			var skillId:int = event.data.readInt();
			var helpObj:Object = CommonPool.fromPoolObject();
			helpObj.id = id;
			helpObj.skillId = skillId;
			sendNotification(NotiConst.OMNISLASH_COMPLETE,helpObj);
		}
		private function omnislashChange(event:CodeEvent):void{
			var id:int = event.data.readInt();
			var skillId:int = event.data.readInt();
			var targetId:int = event.data.readInt();
			var x:Number = event.data.readDouble();
			var y:Number = event.data.readDouble();
			var z:Number = event.data.readDouble();
			helpPos.setValue(x,y,z);
			var helpObj:Object = CommonPool.fromPoolObject();
			helpObj.id = id;
			helpObj.skillId = skillId;
			helpObj.targetId = targetId;
			helpObj.toPosition = helpPos;
			sendNotification(NotiConst.OMNISLASH_CHANGE,helpObj);
		}
	}
}