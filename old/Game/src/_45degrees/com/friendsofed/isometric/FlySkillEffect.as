package _45degrees.com.friendsofed.isometric
{
	import flash.geom.Point;
	
	import gui.animation.IAnimatable;
	import gui.animation.JugglerManager;
	import gui.mc.Animation;

	public class FlySkillEffect extends IsoObject implements IAnimatable
	{
		//目标
		private var _target:IsoObject;
		
		private var _toPosition:Point3D = new Point3D();
		private var _toPoint:Point = new Point();
		public var skillData:Object;
		private var canDispose:Boolean = false;
		private var _yValue:Number;
		
		private static var helpPoint3D:Point3D = new Point3D();
		private static var helpPoint:Point = new Point();
		private static var helpPoint1:Point = new Point();
		private static var helpPoint2:Point = new Point();
		private static var helpPoint3:Point = new Point();
		public function FlySkillEffect(size:Number)
		{
			super(size);
			
			interactive = false;
		}

		public function get yValue():Number
		{
			return _yValue;
		}

		public function set yValue(value:Number):void
		{
			_yValue = value;
			position.y = _yValue;
		}

		public function get target():IsoObject
		{
			return _target;
		}

		public function set target(value:IsoObject):void
		{
			if(_target == value){
				return;
			}
			_target = value;
			
			calculateDir();
			
		}
		public function get toPosition():Point3D
		{
			return _toPosition;
		}

		public function set toPosition(toPosition:Point3D):void
		{
			
			
			toPosition.clone(_toPosition);
			IsoUtils.isoToScreen(toPosition,_toPoint);
			calculateDir();
			
		}
		private function calculateDir():void{
			helpPoint3D.setValue(position.x,0,position.z);
			IsoUtils.isoToScreen(helpPoint3D,helpPoint);
			var radian:Number;
			if(skillData["target"] == 1){
				target.position.clone(helpPoint3D);
				helpPoint3D.y = 0;
				IsoUtils.isoToScreen(helpPoint3D,helpPoint1);
				radian = Math.atan2(helpPoint1.y-helpPoint.y,helpPoint1.x-helpPoint.x);
			}else if(skillData["point"] == 1){
				//重新算一下
				IsoUtils.isoToScreen(toPosition,_toPoint);
				radian = Math.atan2(_toPoint.y-helpPoint.y,_toPoint.x-helpPoint.x);
			}
			var animation:Animation = getChildAt(0) as Animation;
			//飞行时是否旋转方向
			if(skillData["flyIsRotate"] == 1){
				animation.rotation = radian/Math.PI*180.0;
			}
		}
		public function advanceTime(passedTime:Number):void
		{
			//可能这个玩家在道具飞行的过程中，消失了，下线或者去别的场景了
			if(skillData["target"] == 1){
				if(target.isDispose){
					//dispose();
					return;
				}
				//if(canDispose){
				//	dispose();
				//	return;
				//}
				var moveDistance:Number;
				moveDistance = passedTime*skillData["flyspeed"]*GlobalData.scale;
				helpPoint3D.setValue(position.x,0,position.z);
				IsoUtils.isoToScreen(helpPoint3D,helpPoint2);
				target.position.clone(helpPoint3D);
				helpPoint3D.y = 0;
				IsoUtils.isoToScreen(helpPoint3D,helpPoint3);
				
				var distance:Number = Point.distance(helpPoint2,helpPoint3);
				
				if(moveDistance >= distance){
					helpPoint3D.setValue(target.position.x,_yValue,target.position.z);
					position = helpPoint3D;
					//canDispose = true;
				}else{
					calculateDir();
					interpolate(helpPoint3,helpPoint2,moveDistance,helpPoint);
					IsoUtils.screenToIso(helpPoint,helpPoint3D);
					helpPoint3D.y = _yValue;
					position = helpPoint3D;
				}
			}else if(skillData["point"] == 1){
				if(canDispose){
					//dispose();
					return;
				}
				var moveDistance1:Number;
				moveDistance1 = passedTime*skillData["flyspeed"]*GlobalData.scale;
				helpPoint3D.setValue(position.x,0,position.z);
				IsoUtils.isoToScreen(helpPoint3D,helpPoint2);
				//重算一下
				IsoUtils.isoToScreen(toPosition,_toPoint);
				var distance1:Number = Point.distance(helpPoint2,_toPoint);
				if(moveDistance1 >= distance1){
					helpPoint3D.setValue(_toPosition.x,_yValue,_toPosition.z);
					position = helpPoint3D;
					canDispose = true;
				}else{
					calculateDir();
					interpolate(_toPoint,helpPoint2,moveDistance1,helpPoint);
					IsoUtils.screenToIso(helpPoint,helpPoint3D);
					helpPoint3D.y = _yValue;
					position = helpPoint3D;
				}
			}
			
			
			//var animation:Animation = getChildAt(0) as Animation;
			//animation.rotation++;
		}
		override public function dispose():void{
			JugglerManager.twoJuggler.remove(this);
			Game1.bombEffect(skillData,position.clone());
			_target = null;
			_toPoint.x = 0;
			_toPoint.y = 0;
			_toPosition.x = 0;
			_toPosition.y = 0;
			_toPosition.z = 0;
			skillData = null;
			canDispose = false;
			_yValue = 0;
			var animation:Animation = getChildAt(0) as Animation;
			//特别特别注意的地方，设完了，注销时，一定要还原，因为动画类是缓存池
			animation.rotation = 0;
			animation.dispose();
			if(parent){
				(parent.parent as IsoWorld).removeChildToWorld(this);
			}
			super.dispose();
			toPool(this);
		}
		override public function reset(size:Number):IsoObject{
			super.reset(size);
			JugglerManager.twoJuggler.add(this);
			return this;
		}
		private static var sFlySkillEffectPool:Vector.<FlySkillEffect> = new <FlySkillEffect>[];
		
		/** @private */
		public static function fromPool(size:Number):FlySkillEffect
		{
			if (sFlySkillEffectPool.length) return sFlySkillEffectPool.pop().reset(size) as FlySkillEffect;
			else return new FlySkillEffect(size);
		}
		
		/** @private */
		public static function toPool(flySkillEffect:FlySkillEffect):void
		{
			sFlySkillEffectPool[sFlySkillEffectPool.length] = flySkillEffect;
		}
	}
}