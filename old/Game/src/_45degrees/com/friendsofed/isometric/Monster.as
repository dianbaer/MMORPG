package _45degrees.com.friendsofed.isometric
{
	import flash.geom.Point;
	
	import UI.abstract.resources.AnConst;
	import UI.abstract.resources.item.JtaResource;
	
	import _astar.Node;

	public class Monster extends ActivityThing
	{
		private static var helpPos:Point3D = new Point3D();
		private static var helpPoint:Point = new Point();
		private static var helpPoint1:Point = new Point();
		private static var helpPoint2:Point = new Point();
		//怪物攻击的目标
		public var followTarget:IsoObject;
		//是否正在回到初始位置
		public var isBack:Boolean;
		public var backNode:Node;
		
		private var isArrive:Boolean = false;
		public function Monster(size:Number, monster:Object, monsterSkillData:Object)
		{
			super(size, monster,monsterSkillData);
			resetMonster(size, monster,monsterSkillData,false);
		}
		public function resetMonster(size:Number, monster:Object, monsterSkillData:Object,isResetParent:Boolean = true):Monster{
			if(isResetParent){
				resetActivityThing(size,monster,monsterSkillData);
			}
			nameTxt.text = monster["name"];
			
			nameTxt.textColor = 0x00ff00;
			nameTxt.x = -nameTxt.textWidth/2;
			
			changeNamePos();
			return this;
		}
		public function changeNamePos():void{
			//如果已经加载完成，则这个有用,如果没有加载完成，则onLoadComplete里的有用
			nameTxt.y = headPoint.y == 0 ? -100:(headPoint.y*GlobalData.scale-nameTxt.textHeight);
		}
		override public function dispose():void{
			followTarget = null;
			isBack = false;
			backNode = null;
			isArrive = false;
			super.dispose();
			toPool(this);
		}
		override public function set path(value:Array):void
		{
			super.path = value;
			if(_path && _path.length > 0){
				//清空其他行动
				followTarget = null;
				isBack = false;
				backNode = null;
			}
		}
		override protected function onLoadComplete(_res:JtaResource):void{
			super.onLoadComplete(_res);
			changeNamePos();
		}
		override public function advanceTime(passedTime:Number):void
		{
			
			super.advanceTime(passedTime);
			if(isDizzy() || isIceBox() || isCast() || nowReadSkill != null){
				return;
			}
			var moveDistance:Number;
			//怪物的逻辑
			if(followTarget && passedTime > 0.0){
				if(/*followTarget.position == null*/followTarget.isDispose){
					//清除自己
					followTarget = null;
					action = AnConst.STAND;
					return;
				}else if(!isUserSkill){
					position.clone(helpPos);
					helpPos.y = 0;
					IsoUtils.isoToScreen(helpPos,helpPoint);
					followTarget.position.clone(helpPos);
					helpPos.y = 0;
					
					IsoUtils.isoToScreen(helpPos,helpPoint1);
					var distance:Number = Point.distance(helpPoint,helpPoint1);
					
					if(distance <= monsterSkillData["attackDistance"] * 0.8*GlobalData.scale){
						calculateDir(this.position,followTarget.position);
						action = AnConst.STAND;
						//trace("站立"+AnConst.STAND+"，距离"+distance);
						isArrive = true;
					}else if(!isArrive || distance > monsterSkillData["attackDistance"]*GlobalData.scale){
						if(!isCanNotMove()){
							moveDistance = passedTime*nowMoveV*GlobalData.scale;
							//如果总距离减去移动距离已经小于攻击距离了,移动距离变小
							if(distance - moveDistance < monsterSkillData["attackDistance"]*GlobalData.scale){
								//+1防止有偏差,四舍五入
								moveDistance = (distance - moveDistance) < monsterSkillData["attackDistance"]*0.8*GlobalData.scale ? distance-(monsterSkillData["attackDistance"]*0.8*GlobalData.scale)+1 : moveDistance;
								//trace((distance - moveDistance)+","+moveDistance);
							}
							interpolate(helpPoint1,helpPoint,moveDistance,helpPoint2);
							IsoUtils.screenToIso(helpPoint2,helpPos);
							calculateDir(this.position,helpPos);
							position = helpPos;
							action = AnConst.WALK;
							//trace("走路"+AnConst.WALK+"，距离"+distance);
							isArrive = false;
						}else{
							calculateDir(this.position,followTarget.position);
							action = AnConst.STAND;
						}
						
					}
						
					
				}
			}else if(isBack && passedTime > 0.0){
				if(!isCanNotMove() && !isUserSkill){
					moveDistance = passedTime*nowMoveV*GlobalData.scale;
					IsoUtils.isoToScreen(position,helpPoint);
					helpPos.setValue(backNode.x*size,0,backNode.y*size);
					IsoUtils.isoToScreen(helpPos,helpPoint1);
					var distance1:Number = Point.distance(helpPoint,helpPoint1);
					if(moveDistance >= distance1){
						calculateDir(this.position,helpPos);
						position = helpPos;
						//清除自己
						isBack = false;
						backNode = null;
						action = AnConst.STAND;
					}else{
						interpolate(helpPoint1,helpPoint,moveDistance,helpPoint2);
						IsoUtils.screenToIso(helpPoint2,helpPos);
						calculateDir(this.position,helpPos);
						position = helpPos;
						action = AnConst.WALK;
					}
				}else{
					action = AnConst.STAND;
					helpPos.setValue(backNode.x*size,0,backNode.y*size);
					calculateDir(this.position,helpPos);
				}
				
			}
		}
		private static var sMonsterPool:Vector.<Monster> = new <Monster>[];
		
		/** @private */
		public static function fromPool(size:Number, monster:Object, monsterSkillData:Object):Monster
		{
			if (sMonsterPool.length) return sMonsterPool.pop().resetMonster(size, monster,monsterSkillData) as Monster;
			else return new Monster(size, monster,monsterSkillData);
		}
		
		/** @private */
		public static function toPool(monster:Monster):void
		{
			sMonsterPool[sMonsterPool.length] = monster;
		}
	}
}