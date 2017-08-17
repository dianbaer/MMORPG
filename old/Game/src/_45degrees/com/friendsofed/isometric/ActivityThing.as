package _45degrees.com.friendsofed.isometric
{
	import flash.display.DisplayObject;
	import flash.events.Event;
	import flash.filters.BlurFilter;
	import flash.geom.Point;
	import flash.text.TextField;
	import flash.utils.Dictionary;
	
	import UI.App;
	import UI.abstract.resources.AnCategory;
	import UI.abstract.resources.AnConst;
	import UI.abstract.resources.ResourceUtil;
	import UI.abstract.resources.item.ImageResource;
	import UI.abstract.resources.item.JtaResource;
	import UI.abstract.utils.ColorFilters;
	import UI.abstract.utils.CommonPool;
	
	import _astar.Node;
	
	import gui.animation.IAnimatable;
	import gui.animation.JugglerManager;
	import gui.mc.Animation;
	import gui.mc.AnimationEquip;
	
	import proxy.PlayerProxy;
	
	/**
	 * 
	 * 1.因为可能攻击动画的播的时间长短的问题
	 * 如果长，攻击动画还没播完，攻击就应该完成了
	 * 如果断，攻击动画播完，攻击还没有完成
	 * 所以有攻击完成和攻击动画完成的的区分isAttack attackAnimationFinish
	 * 2.普通攻击和技能，普通攻击之后接技能技能时，普通攻击的靠前，所以普通攻击完成之后不能终止动画，需要等到技能动画播完之后，才可以终止isAttackAnimation
	 * 
	 * */
	public class ActivityThing extends IsoObject implements IAnimatable
	{
		
		private static var helpPos:Point3D = new Point3D();
		private static var helpPos1:Point3D = new Point3D();
		private static var helpPos2:Point3D = new Point3D();
		private static var helpPoint:Point = new Point();
		private static var helpPoint1:Point = new Point();
		private static var helpPoint2:Point = new Point();
		
		protected var _path:Array;
		//走路的目标点，或跳的方向点
		private var nowNode:Node;
		//走路的目标点，或跳的方向点(屏幕像素)
		private var nowArrivePoint:Point;
		public var mc:Object;
		//是否在走路
		public var isWalk:Boolean;
		
		/****************走路**********************/
		private var isLine:Boolean = true;
		private var walkAllDis:Number = 0;
		private var walkGoal:Point3D = null;
		private var walkStart:Point3D = null;
		private var walkMoveD:Number = 0;
		/****************走路**********************/
		
		/****************大跳**********************/
		public var isUserSkill:Boolean = false;
		public var skillData:Object = null;
		private var skillJumpH:Number = 0;
		//如果是大跳，记录大跳的速度，如果是冲锋，记录冲锋的总距离
		private var skillJumpV:Number = 0;
		private var goal:Point3D = null;
		private var start:Point3D = null;
		private var moveD:Number = 0;
		/****************大跳**********************/
		
		
		//影子
		public var shadow:DrawnIsoTile;
		//影子滤镜
		private var filter:BlurFilter;
		private var _speedData:Data = new Data();
		
		
		//上一次攻击时间
		private var upAttackTime:Number = 0;
		//是否正在攻击（用来判断时候在普通攻击状态）
		public var isAttack:Boolean;
		//攻击动画是否完成（用来判断攻击那个动画是否播完）
		private var attackAnimationFinish:Boolean = true;
		//攻击前的动作（就是一些普通动作，用来还原用，只有走路和站立）
		private var _attackFrontAction:int;
		//是否自动攻击
		public var isAutoAttack:Boolean = true;
		//目标
		public var target:ActivityThing = null;
		//是不是别人
		public var isOther:Boolean = false;
		//
		
		
		//目前的攻击动画是否是普通攻击产生的，
		private var isAttackAnimation:Boolean = false;
		/**
		 * 第多少次攻击
		 */
		private var attackNum:int = 0;
		private var skillNum:int = 0;
		private var attackAndSkillNum:int = 0;
		private var dict:Dictionary = new Dictionary();
		
		//文字的位置
		public var textPosition:Boolean = false;
		
		//三个挂点头，身子，脚
		public var headPoint:Point = new Point(0,0);
		public var bodyPoint:Point = new Point(0,0);
		public var footPoint:Point = new Point(0,0);
		public var standWidth:Number = 100;
		
		//buff字典
		public var buffDict:Dictionary = new Dictionary();
		//buff是否改变了，主要用于buffUI是否重新读取buff
		public var buffIsChange:Boolean = true;
		private var buffEffectDict:Dictionary = new Dictionary();
		private var skillHitEffectDict:Dictionary = new Dictionary();
		public var isDead:Boolean = false;
		
		//怪物数据
		public var monster:Object;
		public var monsterSkillData:Object;
		
		
		/*****************施法********************/
		public var nowReadSkill:Object;
		public var nowReadSkillEffect:Animation;
		public var nowReadTime:int;
		//这个作用是记录一下目标的状况，没有记点，是因为点不会有状况
		public var nowReadTarget:ActivityThing;
		public var nextSkill:int;
		public var nextTarget:ActivityThing;
		public var nextPosition:Point3D;
		/*****************施法********************/
		
		protected var nameTxt:TextField = new TextField();
		
		//阵营
		public var camp:Object;
		//是否是敌人
		public var isEnemy:Boolean;
		
		
		private var standUrl:String;
		
		private var numTime:int = 0;
		public function ActivityThing(size:Number,monster:Object, monsterSkillData:Object)
		{
			super(size);
			interactive = true;
			resetActivityThing(size,monster,monsterSkillData,false);
			
		}
		public function resetActivityThing(size:Number,monster:Object, monsterSkillData:Object,isReset:Boolean = true):ActivityThing{
			if(isReset){
				reset(size);
			}
			this.monster = monster;
			this.monsterSkillData = monsterSkillData;
			if(int(monster["id"]/100000) == 2 || int(monster["id"]/100000) == 3){
				mc = Animation.fromPool(AnCategory.NPC,monster["src"],AnConst.STAND,true,1.0,AnConst.RIGHT_DOWN);
				//加载站立的
				standUrl = ResourceUtil.getAnimationURL(AnCategory.NPC,monster["src"],AnConst.STAND);
				App.loader.load(standUrl,onLoadComplete);
			}else{
				mc = AnimationEquip.fromPool(AnCategory.USER,monster["src"],AnConst.STAND,true,1.0,AnConst.RIGHT_DOWN,monster["srcWeapon"],monster["srcHat"]);
				standUrl = ResourceUtil.getAnimationURL(AnCategory.USER,monster["src"],AnConst.STAND);
				App.loader.load(standUrl,onLoadComplete);
			}
			addChild(mc as DisplayObject);
			nameTxt.mouseEnabled = false;
			nameTxt.mouseWheelEnabled = false;
			nameTxt.tabEnabled = false;
			nameTxt.filters = [ColorFilters.colour_Black];
			addChild(nameTxt);
			JugglerManager.twoJuggler.add(this);
			return this;
		}
		override public function dispose():void{
			if(_path){
				CommonPool.toPoolArray(_path);
				_path = null;
			}
			
			nowNode = null;
			if(nowArrivePoint){
				nowArrivePoint.x = 0;
				nowArrivePoint.y = 0;
			}
			//设置直线参数
			isLine = true;
			if(walkGoal){
				walkGoal.setValue(0,0,0);
				walkStart.setValue(0,0,0);
			}
			walkAllDis = 0;
			walkMoveD = 0;
			
			
			mc = null;
			isWalk = false;
			isUserSkill = false;
			skillData = null;
			skillJumpH = 0;
			skillJumpV = 0;
			if(goal){
				goal.setValue(0,0,0);
				start.setValue(0,0,0);
			}
			moveD = 0;
			shadow = null;
			filter = null;
			_speedData;
			upAttackTime = 0;
			isAttack = false;
			attackAnimationFinish = true;
			_attackFrontAction = 0;
			isAutoAttack = true;
			target = null;
			if(!isOther){
				AppFacade.getInstance().sendNotification(NotiConst.CANNEL_SKILL_PROGRESS);
			}
			isOther = false;
			isAttackAnimation = false;
			attackNum = 0;
			skillNum = 0;
			attackAndSkillNum = 0;
			//这个得清理，有可能离开场景之时，客户端再次攻击，往服务器发，但是服务器不响应了，客户端还有这个字典缓存，应该被回收到池里，增加效率，降低垃圾回收
			for each ( var attackRestore : AttackRestore in dict )
			{
				delete dict[attackRestore.attackAndSkillNum];
				attackRestore.dispose();
				
			}
			//这里不用重新创建了
			//dict = new Dictionary();
			textPosition = false;
			headPoint.x = 0;
			headPoint.y = 0;
			bodyPoint.x = 0;
			bodyPoint.y = 0;
			footPoint.x = 0;
			footPoint.y = 0;
			standWidth = 100;
			//buff数值清理，直接清理
			for each ( var buffArray : BuffArray in buffDict )
			{
				delete buffDict[buffArray.buffType];
				buffArray.dispose();
			}
			buffIsChange = true;
			//不用new了
			//buffDict = new Dictionary();
			for each ( var mc2 : Animation in buffEffectDict )
			{
				delete buffEffectDict[mc2.data["buff"]["id"]];
				mc2.data["buff"] = null;
				delete mc2.data["buff"];
				mc2.dispose();
				
			}
			//这不用new了
			//buffEffectDict = new Dictionary();
			
			//被击中特效清理，要移除监听
			for each ( var mc1 : Animation in skillHitEffectDict )
			{
				//这个应该是以前没删除这个map导致这个列表里存在dispose为true的动画，现在已经修复了，所以这个判断应该没用了
				//if(!mc1.isDispose){
					delete skillHitEffectDict[mc1.data["skill"]["id"]];
					mc1.data["skill"] = null;
					mc1.data["isHitEffect"] = null;
					delete mc1.data["skill"];
					delete mc1.data["isHitEffect"];
					mc1.removeEventListener(Event.COMPLETE,onSkillComplete);
					mc1.dispose();
				//}
				
			}
			//不需要new了
			//skillHitEffectDict = new Dictionary();
			
			isDead = false;
			monster = null;
			monsterSkillData = null;
			nowReadSkill = null;
			nowReadSkillEffect = null;
			nowReadTime = 0;
			nowReadTarget = null;
			nextSkill = 0;
			nextTarget = null;
			nextPosition = null;
			nameTxt.text = "";
			camp = null;
			isEnemy = false;
			//可能这个怪，没有加载完，就被注销了，所以得取消回调
			if(standUrl){
				App.loader.canelLoad( standUrl , onLoadComplete );
				standUrl = null;
			}
			
			if(parent){
				(parent.parent as IsoWorld).removeChildToWorld(this);
			}
			
			JugglerManager.twoJuggler.remove(this);
			
			for (var i:int=numChildren-1; i>=0; --i){
				var obj : DisplayObject = removeChildAt(i);
				if(obj is Animation){
					
					(obj as Animation).removeEventListener(Event.COMPLETE,onSkillComplete);
					(obj as Animation).removeEventListener(Event.COMPLETE,onAttackComplete);
					//(obj as Animation).removeEventListener(Event.COMPLETE,onJumpComplete);
					(obj as Animation).dispose();
				}
				if(obj is AnimationEquip){
					
					(obj as AnimationEquip).removeEventListener(Event.COMPLETE,onSkillComplete);
					(obj as AnimationEquip).removeEventListener(Event.COMPLETE,onAttackComplete);
					//(obj as AnimationEquip).removeEventListener(Event.COMPLETE,onJumpComplete);
					(obj as AnimationEquip).dispose();
				}
				
			}
			visible = true;
			super.dispose();
		}
		public function dead(isClear:Boolean):void{
			//如果直接死了，直接返回
			if(isDead){
				trace("已经死了，服务器出问题了");
				return;
			}
			isDead = true;
			visible = false;
			if(!isClear){
				return;
			}
			
			//被击中特效清理，要移除监听
			for each ( var mc1 : Animation in skillHitEffectDict )
			{
				//这个应该是以前没删除这个map导致这个列表里存在dispose为true的动画，现在已经修复了，所以这个判断应该没用了
				//if(!mc1.isDispose){
					delete skillHitEffectDict[mc1.data["skill"]["id"]];
					mc1.data["skill"] = null;
					mc1.data["isHitEffect"] = null;
					delete mc1.data["skill"];
					delete mc1.data["isHitEffect"];
					mc1.removeEventListener(Event.COMPLETE,onSkillComplete);
					mc1.dispose();
				//}
				
			}
			//不需要new了
			//skillHitEffectDict = new Dictionary();
			
			//buff数值清理，直接清理
			for each ( var buffArray : BuffArray in buffDict )
			{
				delete buffDict[buffArray.buffType];
				buffArray.dispose();
			}
			buffIsChange = true;
			//不用new了
			//buffDict = new Dictionary();
			
			for each ( var mc2 : Animation in buffEffectDict )
			{
				delete buffEffectDict[mc2.data["buff"]["id"]];
				mc2.data["buff"] = null;
				delete mc2.data["buff"];
				mc2.dispose();
				
			}
			//这不用new了
			//buffEffectDict = new Dictionary();
			
			
			
			//取消寻路
			path = null;
			
			//如果死的时候正在施放技能，直接把他放到施放技能成功的位置
			clearSkill();
			
			upAttackTime = 0;
			//
			
			
			
			isAttack = false;
			if(isAttackAnimation){
				if(!attackAnimationFinish){
					attackAnimationFinish = true;
					action = AnConst.STAND;
					mc.removeEventListener(Event.COMPLETE,onAttackComplete);
				}
			}else{
				if(!attackAnimationFinish){
					attackAnimationFinish = true;
					action = AnConst.STAND;
					mc.removeEventListener(Event.COMPLETE,onAttackComplete);
				}
			}
			//这个也要重置，因为玩家死之前放了个技能，拨了动画，然后玩家死了，服务器返回技能施放错误，把动作重置为攻击之前的动作，攻击之前的工作可能不是站立
			attackFrontAction = AnConst.STAND;
			
			
			
			
			
			//清理正在读条的技能
			cannelReadSkill();
			if(!isOther){
				AppFacade.getInstance().sendNotification(NotiConst.CANNEL_SKILL_PROGRESS);
			}
			
			nextSkill = 0;
			nextTarget = null;
			nextPosition = null;
			
			//解除技能锁定，因为这个技能不会返回施放成功了
			if(!isOther){
				GlobalData.nowBeEffectOnSkill = 0;
				GlobalData.nowBeEffectOnSkillDistance = 0;
				GlobalData.nowBeEffectOnSkillOmnislash = 0;
			}
		}
		//加载最初站立的资源，回调，设置身体和头顶的挂点
		//有这么一种情况，动画和这张图同时加载，然后正好，动画切动作了，所以取消加载，但是这里没有解析资源，所以会出问题
		protected function onLoadComplete(_res:JtaResource):void{
			if(!_res.content[AnConst.STAND]){
				//动作有可能命名不都是0，所以做一个循环，取第一个
				for ( var i : * in _res.content )
				{
					if(_res.content[i] is Object && _res.content[i].hasOwnProperty("dirCount")){
						_res.content[AnConst.STAND] = _res.content[i];
						_res.content[i] = null;
						delete _res.content[i];
						break;
					}
				}
				
			}
			//if(_res != null && _res.content[AnConst.STAND] != null){
				var imageResource:ImageResource = _res.content[AnConst.STAND]["dirData"][AnConst.DOWN]["frames"][0]["imageResource"] as ImageResource;
				bodyPoint.y = int(-imageResource.bitmapData.height/2);
				headPoint.y = -imageResource.bitmapData.height;
				standWidth = imageResource.bitmapData.width;
			//}
			standUrl = null;
		}
		//被击中的特效
		public function hitEffect(attackSkill:Object,isStory:Boolean = false):void{
			if(attackSkill["hitEffect"]){
				if(attackSkill["hitIsfollow"]){
					if(skillHitEffectDict[attackSkill["id"]] && skillHitEffectDict[attackSkill["id"]].parent){
						//重置为第0帧
						(skillHitEffectDict[attackSkill["id"]] as Animation).currentFrame = 0;
					}else{
						var skillMc:Animation = Animation.fromPool(AnCategory.EFFECT,attackSkill["hitEffect"],AnConst.STAND,false,attackSkill["hitFps"]/1000.0,AnConst.DOWN);
						//skillMc.dir = mc.dir;
						//skillMc.fps = attackSkill["hitFps"]/1000.0;
						//设置这个被攻击特效的属性
						skillMc.data["skill"] = attackSkill;
						skillMc.data["isHitEffect"] = true;
						addChild(skillMc);
						skillHitEffectDict[attackSkill["id"]] = skillMc;
						skillMc.addEventListener(Event.COMPLETE,onSkillComplete);
					}
				}else{
					var skillMc1:Animation = Animation.fromPool(AnCategory.EFFECT,attackSkill["hitEffect"],AnConst.STAND,false,attackSkill["hitFps"]/1000.0,AnConst.DOWN);
					//skillMc1.dir = mc.dir;
					//skillMc1.fps = attackSkill["hitFps"]/1000.0;
					
					var skillEffect:SkillEffect = SkillEffect.fromPool(this.size);
					
					this.position.clone(helpPos);
					helpPos.y = 0;
					skillEffect.position = helpPos;
					
					skillEffect.addChild(skillMc1);
					skillEffect.addEvent();
					if(isStory){
						GlobalData.mapStory.addChildToWorld(skillEffect);
					}else{
						GlobalData.map1.addChildToWorld(skillEffect);
					}
				}
				
				
			}
		}
		public function get nowMoveV():Number{
			if(buffDict[BuffInfo.SPEED_ADD] && buffDict[BuffInfo.SPEED_DEL]){
				return speedData.moveV*(1+(buffDict[BuffInfo.SPEED_ADD].addPer+buffDict[BuffInfo.SPEED_DEL].addPer)/100.0);
			}else if(buffDict[BuffInfo.SPEED_ADD] && !buffDict[BuffInfo.SPEED_DEL]){
				return speedData.moveV*(1+buffDict[BuffInfo.SPEED_ADD].addPer/100.0);
			}else if(!buffDict[BuffInfo.SPEED_ADD] && buffDict[BuffInfo.SPEED_DEL]){
				return speedData.moveV*(1+buffDict[BuffInfo.SPEED_DEL].addPer/100.0);
			}else{
				return speedData.moveV;
			}
		}
		public function addBuff(addBuffArray:Array,length:int):void{
			for(var i:int = 0;i<length;i++){
				var buffId:int = addBuffArray[i]["id"];
				var attackId:int = addBuffArray[i]["attackId"];
				var duration:int = addBuffArray[i]["duration"];
				var buff:Object = GlobalData.buffData[buffId];
				if(buff){
					var buffArray:BuffArray = buffDict[buff["type"]];
					if(!buffArray){
						buffArray = BuffArray.fromPool();
						buffDict[buff["type"]] = buffArray;
						buffArray.buffType = buff["type"];
					}
					var haveBuff:BuffInfo = null;
					for(var j:int = 0;j<buffArray.buffArray.length;j++){
						var buffInfo:BuffInfo = buffArray.buffArray[j];
						if(buffInfo.buffData["id"] == buff["id"] && buffInfo.masterId == attackId){
							haveBuff = buffInfo;
							break;
						}
					}
					if(haveBuff){
						haveBuff.surplusTime = duration;
					}else{
						haveBuff = BuffInfo.fromPool();
						haveBuff.buffData = buff;
						haveBuff.masterId = attackId;
						haveBuff.surplusTime = duration;
						
						buffArray.buffArray[buffArray.buffArray.length] = haveBuff;
						sortBuff(buffArray);
						buffArray.addPer = buffArray.buffArray[0].buffData["value"];
						//添加特效(相同类型的buff，只显示一个，没有必要显示多个)
						if(!buffEffectDict[haveBuff.buffData["id"]]){
							var skillMc:Animation = Animation.fromPool(AnCategory.EFFECT,haveBuff.buffData["src"],AnConst.STAND,true,haveBuff.buffData["fps"]/1000.0,AnConst.DOWN);
							//skillMc.dir = mc.dir;
							//skillMc.fps = haveBuff.buffData["fps"]/1000.0;
							skillMc.data["buff"] = haveBuff.buffData;
							switch(haveBuff.buffData["position"]){
								case 0:
									skillMc.x = footPoint.x;
									skillMc.y = footPoint.y+haveBuff.buffData["yoffset"];
									break;
								case 1:
									skillMc.x = bodyPoint.x*GlobalData.scale;
									skillMc.y = bodyPoint.y*GlobalData.scale+haveBuff.buffData["yoffset"];
									break;
								case 2:
									skillMc.x = headPoint.x*GlobalData.scale;
									skillMc.y = headPoint.y*GlobalData.scale+haveBuff.buffData["yoffset"];
									break;
							}
							addChild(skillMc);
							buffEffectDict[haveBuff.buffData["id"]] = skillMc;
						}else{
							//(buffEffectDict[haveBuff.buffData["id"]] as Animation).currentFrame = 0;
						}
						switch(haveBuff.buffData["type"]){
							case BuffInfo.SPEED_ADD:
							case BuffInfo.SPEED_DEL:
								if(mc.action == AnConst.WALK){
									mc.fps = (monster["moveFps"]*(monster["moveV"]/nowMoveV))/1000.0;
								}
								break;
							case BuffInfo.DIZZY_EFFECT:
								path = null;
								//dizzyFrontAction = mc.action;
								if(isOther){
									cannelReadSkill();
								}else{
									cannelNextSkill();
								}
								
								break;
							case BuffInfo.ICE_BOX:
								path = null;
								if(isOther){
									cannelReadSkill();
								}else{
									cannelNextSkill();
								}
								mc.pause();
								break;
							case BuffInfo.CAST:
								path = null;
								if(isOther){
									cannelReadSkill();
								}else{
									cannelNextSkill();
								}
								//替换模型
								break;
							case BuffInfo.CANNOT_MOVE:
								path = null;
								
								break;
							
						}
						//如果有特殊效果的buff，例如变身之类的，可以在这里加逻辑
					}
				}
			}
			buffIsChange = true;
			
		}
		public function delBuff(delbuffArray:Array,length:int):void{
			for(var i:int = 0;i<length;i++){
				var buffId:int = delbuffArray[i]["id"];
				var attackId:int = delbuffArray[i]["attackId"];
				var buff:Object = GlobalData.buffData[buffId];
				if(buff){
					var buffArray:BuffArray = buffDict[buff["type"]];
					if(buffArray){
						for(var j:int = 0;j<buffArray.buffArray.length;j++){
							var buffInfo:BuffInfo = buffArray.buffArray[j];
							if(buffInfo.buffData["id"] == buff["id"] && buffInfo.masterId == attackId){
								buffArray.buffArray.splice(j,1);
								if(buffArray.buffArray.length> 0){
									sortBuff(buffArray);
									buffArray.addPer = buffArray.buffArray[0].buffData["value"];
								}else{
									buffArray.addPer = 0;
								}
								
								//删除特效
								if(buffEffectDict[buffInfo.buffData["id"]]){
									buffEffectDict[buffInfo.buffData["id"]].data["buff"] = null;
									delete buffEffectDict[buffInfo.buffData["id"]].data["buff"];
									buffEffectDict[buffInfo.buffData["id"]].dispose();
									delete buffEffectDict[buffInfo.buffData["id"]];
								}
								switch(buffInfo.buffData["type"]){
									case BuffInfo.SPEED_ADD:
									case BuffInfo.SPEED_DEL:
										if(mc.action == AnConst.WALK){
											mc.fps = (monster["moveFps"]*(monster["moveV"]/nowMoveV))/1000.0;
										}
										break;
									case BuffInfo.DIZZY_EFFECT:
										//action = dizzyFrontAction;
										
										break;
									case BuffInfo.ICE_BOX:
										mc.play();
										
										break;
									case BuffInfo.CAST:
										//切回模型
										
										break;
									
								}
								break;
								buffInfo.dispose();
							}
						}
					}
				}
				
				
			}
			buffIsChange = true;
		}
		public function set dir(dir:int):void{
			if(mc.dir == dir){
				return;
			}
			mc.dir = dir;
		}
		public function set attackFrontAction(value:int):void
		{
			if(value == AnConst.ATTACK || value == AnConst.ATTACK2){
				return;
			}
			_attackFrontAction = value;
		}
		
		public function set attackSpeed(attackSpeed:Number):void{
			if(speedData.attackSpeed == attackSpeed){
				return;
			}
			if(attackSpeed <= 0){
				return;
			}
			speedData.attackSpeed = attackSpeed;
			if(mc.action == AnConst.ATTACK || mc.action == AnConst.ATTACK2){
				mc.fps = (monsterSkillData["fps"]*(speedData.attackSpeed/monster["attackSpeed"]))/1000.0;
				
			}
		}
		public function get attackSpeed():Number{
			return speedData.attackSpeed;
		}
		public function set moveV(moveV:Number):void{
			if(speedData.moveV == moveV){
				return;
			}
			if(moveV <= 0){
				return;
			}
			speedData.moveV = moveV;
			if(mc.action == AnConst.WALK){
				mc.fps = (monster["moveFps"]*(monster["moveV"]/nowMoveV))/1000.0;
			}
		}
		public function get moveV():Number{
			return speedData.moveV;
		}
		public function set jumpVerticalV(jumpVerticalV:Number):void{
			if(speedData.jumpVerticalV == jumpVerticalV){
				return;
			}
			if(jumpVerticalV <= 0){
				return;
			}
			speedData.jumpVerticalV = jumpVerticalV;
		}
		public function get jumpVerticalV():Number{
			return speedData.jumpVerticalV;
		}
		public function set speedData(speedData:Data):void{
			_speedData = speedData;
			if(mc.action == AnConst.ATTACK || mc.action == AnConst.ATTACK2){
				//mc.fps = (AnConst.ATTACK_ALLFRAME_SECOND*(speedData.attackSpeed/Data.initAttackSpeed))/1000.0;
				mc.fps = (monsterSkillData["fps"]*(speedData.attackSpeed/monster["attackSpeed"]))/1000.0;
				
			}
			if(mc.action == AnConst.WALK){
				mc.fps = (monster["moveFps"]*(monster["moveV"]/nowMoveV))/1000.0;
			}
		}
		public function get speedData():Data{
			return _speedData;
		}
		/**
		 * 这里不仅仅有设置动作，还有设置帧频，这个很重要，不能轻易返回
		 */
		public function set action(action:int):void{
			//攻击动画没播完，继续播，其余动作做个记录，等攻击动作播完了再播
			if(!attackAnimationFinish && (action != AnConst.ATTACK || action != AnConst.ATTACK2)){
				attackFrontAction = action;
				return;
			}
			//攻击动作播完了，但是还在攻击状态，并且过来的动作不是攻击，记录一下，等攻击完成之后，还要切这个动作
			/*if(isAttack && action != AnConst.ATTACK){
			attackFrontAction = action;
			}*/
			if(mc.action == action){
				return;
			}
			var fps:Number = 0;
			
			switch(action){
				case AnConst.STAND:
					fps = monster["standFps"]/1000.0;
					break;
				case AnConst.WALK:
					//如果是使用冲锋技能，则会播放的比较快，可能切换动作的时候，还在攻击，那就把这个动作保存了起来，攻击完成后
					//切换冲锋动作时，会拨快。冲锋完成后，会切成站立。
					if(isUserSkill && this.skillData["type"] == Skill.RUSH){
						fps = (monster["moveFps"]*(monster["moveV"]/this.monster["rushSpeed"]))/1000.0;
					}else{
						fps = (monster["moveFps"]*(monster["moveV"]/nowMoveV))/1000.0;
					}
					
					break;
				case AnConst.ATTACK:
					//fps = (AnConst.ATTACK_ALLFRAME_SECOND*(speedData.attackSpeed/Data.initAttackSpeed))/1000.0;
					fps = (monsterSkillData["fps"]*(speedData.attackSpeed/monster["attackSpeed"]))/1000.0;
					break;
				//这个是跳跃
				case AnConst.ATTACK2:
					//fps = (AnConst.ATTACK_ALLFRAME_SECOND*(speedData.attackSpeed/Data.initAttackSpeed))/1000.0;
					fps = (monsterSkillData["fps"]*(speedData.attackSpeed/monster["attackSpeed"]))/1000.0;
					break;
			}
			//mc.action = action;
			mc.setActionAndFps(action,fps);
		}
		public function get path():Array
		{
			return _path;
		}
		
		public function set path(value:Array):void
		{
			
			
			if(_path){
				CommonPool.toPoolArray(_path);
			}
			_path = value;
			nowNode = null;
			if(nowArrivePoint){
				nowArrivePoint.x = 0;
				nowArrivePoint.y = 0;
			}
			
			if(_path && _path.length > 0){
				action = AnConst.WALK;
				isWalk = true;
				
				//设置直线参数
				isLine = true;
				if(walkGoal){
					walkGoal.setValue(_path[_path.length-1].x*size,0,_path[_path.length-1].y*size);
				}else{
					walkGoal = new Point3D(_path[_path.length-1].x*size,0,_path[_path.length-1].y*size);
				}
				walkStart = this.position.clone(walkStart);
				IsoUtils.isoToScreen(walkStart,helpPoint);
				IsoUtils.isoToScreen(walkGoal,helpPoint1);
				walkAllDis = Point.distance(helpPoint,helpPoint1)/GlobalData.scale;
				walkMoveD = 0;
				calculateDir(walkStart,walkGoal,false);
				if(isOther){
					cannelReadSkill();
				}else{
					cannelNextSkill();
				}
				//if(this is Player){
				//	numTime = 0;
				//	AppFacade.getInstance().sendNotification(NotiConst.CLEAR_MESSAGE);
				//}
				
				
			}else{
				isWalk = false;
				action = AnConst.STAND;
				
				//设置直线参数
				isLine = true;
				if(walkGoal){
					walkGoal.setValue(0,0,0);
					walkStart.setValue(0,0,0);
				}
				walkAllDis = 0;
				walkMoveD = 0;
				
				//跟随者isWalk
				if(!isOther && GlobalData.nowTargetEffect && !GlobalData.nowTargetEffect.isDispose){
					GlobalData.nowTargetEffect.dispose();
					GlobalData.nowTargetEffect = null;
				}
			}
		}
		public function setNowNode(nowNode:Node):void{
			if(nowNode == null){
				return;
			}
			
			this.nowNode = nowNode;
			helpPos.setValue(nowNode.x*size,0,nowNode.y*size);
			nowArrivePoint = IsoUtils.isoToScreen(helpPos,nowArrivePoint);
			action = AnConst.WALK;
			isWalk = true;
			//走直线不可能有这个未走完点，所以不用加代码了
			//怪物广播的时候会有这个
			//就剩最后一个点的时候才有意义
			//设置直线参数
			if(!_path || _path.length == 0){
				isLine = true;
				if(walkGoal){
					walkGoal.setValue(nowNode.x*size,0,nowNode.y*size);
				}else{
					walkGoal = new Point3D(nowNode.x*size,0,nowNode.y*size);
				}
				walkStart = this.position.clone(walkStart);
				IsoUtils.isoToScreen(walkStart,helpPoint);
				IsoUtils.isoToScreen(walkGoal,helpPoint1);
				walkAllDis = Point.distance(helpPoint,helpPoint1)/GlobalData.scale;
				walkMoveD = 0;
				calculateDir(walkStart,walkGoal,false);
			}
			
		}
		
		public function isDizzy():Boolean{
			var buffArray:BuffArray = buffDict[BuffInfo.DIZZY_EFFECT];
			if(buffArray && buffArray.buffArray.length > 0){
				return true;
			}else{
				return false;
			}
		}
		public function isIceBox():Boolean{
			var buffArray:BuffArray = buffDict[BuffInfo.ICE_BOX];
			if(buffArray && buffArray.buffArray.length > 0){
				return true;
			}else{
				return false;
			}
		}
		public function isCast():Boolean{
			var buffArray:BuffArray = buffDict[BuffInfo.CAST];
			if(buffArray && buffArray.buffArray.length > 0){
				return true;
			}else{
				return false;
			}
		}
		/**
		 * 定身
		 */
		public function isCanNotMove():Boolean{
			var buffArray:BuffArray = buffDict[BuffInfo.CANNOT_MOVE];
			if(buffArray && buffArray.buffArray.length > 0){
				return true;
			}else{
				return false;
			}
		}
		public function cannelReadSkill():void{
			if(nowReadSkill){
				nowReadSkill = null;
				//移除上一次读条的特效
				if(nowReadSkillEffect && nowReadSkillEffect.parent){
					nowReadSkillEffect.dispose();
					nowReadSkillEffect = null;
				}
				nowReadTarget = null;
				
			}
		}
		//取消下一个技能的目的，是服务器发送技能完成之后，自动释放下一个技能，如果走路了或者被晕了，就不能放下一个技能所以取消
		public function cannelNextSkill():void{
			nextSkill = 0;
			nextTarget = null;
			nextPosition = null;
		}
		public function userSkillServer(skillId:int,target:ActivityThing=null,dir:int = 0,toPosition:Point3D=null,isComplete:int = 0,flyThingId:int = 0,isStory:Boolean = false):void{
			var toTarget:ActivityThing = target;
			
			var skillData:Object = GlobalData.skillData[skillId];
			if((skillData["target"] == 1 && toTarget == null) || (skillData["point"] == 1 && toPosition == null)){
				//trace("服务器返回错误没有目标或者点");
				var helpObj:Object = CommonPool.fromPoolObject();
				helpObj.message = "服务器返回错误没有目标或者点";
				AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj);
				return;
			}
			
			//判断距离(闪现不用判断距离)
			var distance:Number = 0;
			if(skillData["type"] == Skill.BIG_JUMP){
				this.position.clone(helpPos);
				helpPos.y = 0;
				IsoUtils.isoToScreen(helpPos,helpPoint);
				
				IsoUtils.isoToScreen(toPosition,helpPoint1);
					
				
				distance = Point.distance(helpPoint,helpPoint1)/GlobalData.scale;
				
			}
			
			//正在被闪现影响着
			if(skillData["type"] == Skill.FLASH || skillData["type"] == Skill.BIG_JUMP || skillData["type"] == Skill.RUSH || skillData["type"] == Skill.OMNISLASH){
				//清空寻路
				path = null;
				
			}
			//如果在施放大跳类型的移动技能，并且不是服务器发来的消息，，并且正在施放的是读条技能
			/*if(isUserSkill && !isServer && skillData["read"]>0){
			AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,"现在我不能施放读条技能");
			return;
			}*/
			
			if(nowReadSkill){
				if(isOther){
					cannelReadSkill();
				}
				
			}
			
			var skillMc:Animation;
			if(isComplete == 0 && skillData["read"] > 0){
				nowReadSkill = skillData;
				nowReadTime = JugglerManager.processTime;
				//清空寻路
				path = null;
				
				if(skillData["readEffect"]){
					skillMc = Animation.fromPool(AnCategory.EFFECT,skillData["readEffect"],AnConst.STAND,true,skillData["fps"]/1000.0,AnConst.DOWN);
					//skillMc.dir = mc.dir;
					//skillMc.fps = skillData["fps"]/1000.0;
					addChild(skillMc);
					
					nowReadSkillEffect = skillMc;
				}
				nowReadTarget = toTarget;
				
			}else{
				
				if(skillData["src"]){
					skillMc = Animation.fromPool(AnCategory.EFFECT,skillData["src"],AnConst.STAND,false,skillData["fps"]/1000.0,AnConst.DOWN);
					//skillMc.dir = mc.dir;
					//skillMc.fps = skillData["fps"]/1000.0;
				}
				
				if(skillData["isfly"]){
					var flySkillEffect:FlySkillEffect = FlySkillEffect.fromPool(this.size);
					this.position.clone(helpPos);
					helpPos.y = 0;
					flySkillEffect.position = helpPos;
					flySkillEffect.thingId = flyThingId;
					flySkillEffect.yValue = skillData["y"];
					if(skillMc){
						flySkillEffect.addChild(skillMc);
					}
					flySkillEffect.skillData = skillData;
					if(skillData["point"] == 1){
						flySkillEffect.toPosition = toPosition;
					}else if(skillData["target"] == 1){
						flySkillEffect.target = toTarget;
					}
					if(isStory){
						GlobalData.mapStory.addChildToWorld(flySkillEffect);
					}else{
						GlobalData.map1.addChildToWorld(flySkillEffect);
					}
					
					
					
					
				}else{
					if(skillMc){
						//如果是跟随玩家的
						if(skillData["isfollow"]){
							if(skillData["isonplayerup"]){
								addChild(skillMc);
							}else{
								addChildAt(skillMc,this.getChildIndex(mc as DisplayObject));
							}
							skillMc.addEventListener(Event.COMPLETE,onSkillComplete);
							
						}else{
							
							var skillEffect:SkillEffect = SkillEffect.fromPool(this.size);
							if(skillData["point"] == 1 && skillData["pointType"] == 0){
								skillEffect.position = toPosition;
							}else if(skillData["target"] == 1 && skillData["pointType"] == 0){
								toTarget.position.clone(helpPos);
								helpPos.y = 0;
								skillEffect.position = helpPos;
							}else if(skillData["pointType"] == 1){
								this.position.clone(helpPos);
								helpPos.y = 0;
								skillEffect.position = helpPos;
							}
							skillEffect.addChild(skillMc);
							skillEffect.addEvent();
							if(isStory){
								GlobalData.mapStory.addChildToWorld(skillEffect);
							}else{
								GlobalData.map1.addChildToWorld(skillEffect);
							}
							
							
							
						}
					}
				}
			}
			
			//如果是大跳，保存原来的坐标点，如果失败了，直接回到原来的位置
			if(skillData["type"] == Skill.BIG_JUMP || skillData["type"] == Skill.RUSH || skillData["type"] == Skill.OMNISLASH){
				if(skillData["type"] == Skill.BIG_JUMP)
				{
					isUserSkill = true;
					this.skillData = skillData;
					skillJumpH = distance*Math.abs(((speedData.jumpVerticalA/speedData.jumpVerticalV)/2));
					skillJumpV = speedData.jumpVerticalV;
					goal = toPosition.clone(goal);
					goal.y = 0;
					start = this.position.clone(start);
					start.y = 0;
					this.position.y = 0;
					//trace(distance);
					moveD = 0;
				}else if(skillData["type"] == Skill.RUSH){
					isUserSkill = true;
					this.skillData = skillData;
					skillJumpH = this.monster["rushSpeed"];
					//skillJumpV = 0;
					goal = toPosition.clone(goal);
					goal.y = 0;
					start = this.position.clone(start);
					start.y = 0;
					this.position.y = 0;
					IsoUtils.isoToScreen(start,helpPoint);
					IsoUtils.isoToScreen(goal,helpPoint1);
					skillJumpV = Point.distance(helpPoint,helpPoint1)/GlobalData.scale;
					//trace(distance);
					moveD = 0;
				}else if(skillData["type"] == Skill.OMNISLASH){
					isUserSkill = true;
					this.skillData = skillData;
					skillJumpH = this.monster["rushSpeed"]*2;
					
					goal = toPosition.clone(goal);
					goal.y = 0;
					start = this.position.clone(start);
					start.y = 0;
					this.position.y = 0;
					IsoUtils.isoToScreen(start,helpPoint);
					IsoUtils.isoToScreen(goal,helpPoint1);
					skillJumpV = Point.distance(helpPoint,helpPoint1)/GlobalData.scale*2;
					
					interpolateWithArrvie(helpPoint1, helpPoint, skillJumpV/2,helpPoint2);
					this.goal = IsoUtils.screenToIso(helpPoint2,goal);
					
					moveD = 0;
				}
				
			}
			
			if(dir != 0){
				mc.dir = dir;
			}else{
				if(skillData["target"] == 1){
					calculateDir(this.position,toTarget.position);
				}else if(skillData["point"] == 1){
					calculateDir(this.position,toPosition);
				}
			}
			if(skillData["read"] > 0 && isComplete == 0){
				return;
			}
			//如果是冲锋，则只是改变为走路
			if(skillData["type"] == Skill.RUSH){
				action = AnConst.WALK;
			}else if(skillData["type"] == Skill.OMNISLASH){
				action = AnConst.STAND;
			}else{
				if(!attackAnimationFinish){
					attackFrontAction = _attackFrontAction;
					
				}else{
					attackFrontAction = mc.action;
				}
				if(skillData["action"] == 1 || this is Monster){
					action = AnConst.ATTACK;
				}else{
					action = AnConst.ATTACK2;
				}
				
				//重置动作，播完没播完 都直接重置
				mc.currentFrame = 0;
				isAttackAnimation = false;
				attackAnimationFinish = false;
				mc.addEventListener(Event.COMPLETE,onAttackComplete);
			}
			
			
			
		}
		public function userSkillClient(skillId:int,target:ActivityThing=null,toPosition:Point3D=null):void{
			var toTarget:ActivityThing;
			
			toTarget = this.target;
			if(toTarget && /*toTarget.position == null*/toTarget.isDispose){
				this.target = null;
				toTarget = null;
			}
			
			var skillData:Object = GlobalData.skillData[skillId];
			if(skillData["read"] > 0 && isUserSkill){
				var helpObj4:Object = CommonPool.fromPoolObject();
				helpObj4.message = "现在不能施放读条技能";
				AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj4);
				return;
			}
			//治疗类型（是不是选的自己）
			var isMySelf:Boolean = false;
			if((toTarget == null || toTarget.isEnemy) && skillData["target"] == 1 && (skillData["type"] == Skill.TREAT || (skillData["type"] == Skill.BUFF && skillData["isEnemy"] == 0))){
				toTarget = this;
				isMySelf = true;
			}
			if((skillData["target"] == 1 && toTarget == null)|| (skillData["point"] == 1 && toPosition == null)){
				trace("请选择一个目标");
				var helpObj:Object = CommonPool.fromPoolObject();
				helpObj.message = "请选择一个目标";
				AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj);
				return;
			}
			
			//判断距离(闪现不用判断距离)
			var distance:Number = 0;
			if((skillData["target"] == 1 || skillData["point"] == 1)/* && skillData["type"] != Skill.FLASH*/ && !isMySelf){
				this.position.clone(helpPos);
				helpPos.y = 0;
				IsoUtils.isoToScreen(helpPos,helpPoint);
				
				if(skillData["target"] == 1){
					toTarget.position.clone(helpPos);
					helpPos.y = 0;
					IsoUtils.isoToScreen(helpPos,helpPoint1);
					
				}else{
					IsoUtils.isoToScreen(toPosition,helpPoint1);
					
				}
				distance = Point.distance(helpPoint,helpPoint1)/GlobalData.scale;
				if(distance > skillData["attackDistance"]){
					trace("太远了，打不着");
					var helpObj1:Object = CommonPool.fromPoolObject();
					helpObj1.message = "太远了，打不着";
					AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj1);
					return;
				}
			}
			if(nowReadSkill){
				/*if(nowReadSkill["id"] == skillData["id"]){
				return;
				}else{
				cannelReadSkill();
				}*/
				if(JugglerManager.processTime-nowReadTime >= nowReadSkill["read"]-200){
					nextSkill = skillId;
					nextTarget = toTarget;
					if(toPosition){
						nextPosition = toPosition.clone(nextPosition);
					}else{
						nextPosition = null;
					}
					
				}
				
				return;
			}
			//正在被闪现影响着
			if(skillData["type"] == Skill.FLASH || skillData["type"] == Skill.BIG_JUMP || skillData["type"] == Skill.RUSH|| skillData["type"] == Skill.OMNISLASH){
				//清空寻路
				path = null;
				if(skillData["type"] == Skill.FLASH){
					//如果是自己，才锁定其他操作（例如自动攻击，施放技能，寻路等）
					GlobalData.nowBeEffectOnSkill++;
				}
				if(skillData["type"] == Skill.OMNISLASH){
					//如果是自己，才锁定其他操作（例如自动攻击，施放技能，寻路等）
					GlobalData.nowBeEffectOnSkillOmnislash++;
				}
				
			}
			
			
			
			var attackRestore:AttackRestore = AttackRestore.fromPool();
			var skillMc:Animation;
			if(skillData["read"] > 0){
				nowReadSkill = skillData;
				nowReadTime = JugglerManager.processTime;
				//清空寻路
				path = null;
				var helpObj2:Object = CommonPool.fromPoolObject();
				helpObj2.skillData = skillData;
				AppFacade.getInstance().sendNotification(NotiConst.SHOW_SKILL_PROGRESS,helpObj2);
				
				if(skillData["readEffect"]){
					skillMc = Animation.fromPool(AnCategory.EFFECT,skillData["readEffect"],AnConst.STAND,true,skillData["fps"]/1000.0,AnConst.DOWN);
					//skillMc.dir = mc.dir;
					//skillMc.fps = skillData["fps"]/1000.0;
					addChild(skillMc);
					attackRestore.readMc = skillMc;
					
					nowReadSkillEffect = skillMc;
				}
				nowReadTarget = toTarget;
			}else{
				//如果是正在施放技能，并且这个技能也是时间性技能，则需要等到确定能施放，在触发
				if((isUserSkill && skillData["type"] == Skill.BIG_JUMP) || skillData["type"] == Skill.RUSH || skillData["isfly"] || skillData["type"] == Skill.OMNISLASH){
					
				}else{
					if(skillData["src"]){
						skillMc = Animation.fromPool(AnCategory.EFFECT,skillData["src"],AnConst.STAND,false,skillData["fps"]/1000.0,AnConst.DOWN);
						//skillMc.dir = mc.dir;
						//skillMc.fps = skillData["fps"]/1000.0;
					}
					//这个飞行技能已无用
					if(skillData["isfly"]){
						var flySkillEffect:FlySkillEffect = FlySkillEffect.fromPool(this.size);
						this.position.clone(helpPos);
						helpPos.y = 0;
						flySkillEffect.position = helpPos;
						flySkillEffect.yValue = skillData["y"];
						if(skillMc){
							flySkillEffect.addChild(skillMc);
						}
						flySkillEffect.skillData = skillData;
						if(skillData["point"] == 1){
							flySkillEffect.toPosition = toPosition;
						}else if(skillData["target"] == 1){
							flySkillEffect.target = toTarget;
						}
						GlobalData.map1.addChildToWorld(flySkillEffect);
						
						
						attackRestore.flySkillEffect = flySkillEffect;
					}else{
						if(skillMc){
							//如果是跟随玩家的
							if(skillData["isfollow"]){
								if(skillData["isonplayerup"]){
									addChild(skillMc);
								}else{
									addChildAt(skillMc,this.getChildIndex(mc as DisplayObject));
								}
								skillMc.addEventListener(Event.COMPLETE,onSkillComplete);
								attackRestore.skillMc = skillMc;
							}else{
								
								var skillEffect:SkillEffect = SkillEffect.fromPool(this.size);
								if(skillData["point"] == 1 && skillData["pointType"] == 0){
									skillEffect.position = toPosition;
								}else if(skillData["target"] == 1 && skillData["pointType"] == 0){
									toTarget.position.clone(helpPos);
									helpPos.y = 0;
									skillEffect.position = helpPos;
								}else if(skillData["pointType"] == 1){
									this.position.clone(helpPos);
									helpPos.y = 0;
									skillEffect.position = helpPos;
								}
								skillEffect.addChild(skillMc);
								skillEffect.addEvent();
								GlobalData.map1.addChildToWorld(skillEffect);
								
								attackRestore.skillEffect = skillEffect;
							}
						}
					}
				}
				
			}
			//如果是大跳，保存原来的坐标点，如果失败了，直接回到原来的位置
			//如果是正在施放技能，并且这个技能也是时间性技能，则需要等到确定能施放，在触发
			if((isUserSkill && skillData["type"] == Skill.BIG_JUMP) || skillData["type"] == Skill.RUSH || (skillData["isfly"] && skillData["read"] <= 0) || skillData["type"] == Skill.OMNISLASH){
				attackRestore.isUseSkill = true;
				//做计数用
				if(skillData["type"] == Skill.BIG_JUMP || skillData["type"] == Skill.RUSH){
					GlobalData.nowBeEffectOnSkillDistance++;
				}
			}else{
				if(skillData["type"] == Skill.BIG_JUMP){
					attackRestore.position = this.position.clone(attackRestore.position);
					attackRestore.position.y = 0;
					isUserSkill = true;
					this.skillData = skillData;
					skillJumpH = distance*Math.abs(((speedData.jumpVerticalA/speedData.jumpVerticalV)/2));
					skillJumpV = speedData.jumpVerticalV;
					goal = toPosition.clone(goal);
					goal.y = 0;
					start = this.position.clone(start);
					start.y = 0;
					this.position.y = 0;
					//trace(distance);
					moveD = 0;
				//这块废弃了，不会再被调用了
				}else if(skillData["type"] == Skill.RUSH){
					attackRestore.position = this.position.clone(attackRestore.position);
					attackRestore.position.y = 0;
					isUserSkill = true;
					this.skillData = skillData;
					skillJumpH = this.monster["rushSpeed"];
					//skillJumpV = 0;
					goal = toTarget.position.clone(goal);
					goal.y = 0;
					start = this.position.clone(start);
					start.y = 0;
					this.position.y = 0;
					IsoUtils.isoToScreen(start,helpPoint);
					IsoUtils.isoToScreen(goal,helpPoint1);
					skillJumpV = Point.distance(helpPoint,helpPoint1)/GlobalData.scale;
					//trace(distance);
					moveD = 0;
				}
				
			}
			
				
			//做一个施放技能失败还原的记录
			skillNum++;
			attackAndSkillNum++;
			attackRestore.attackAndSkillNum = attackAndSkillNum;
			dict[attackAndSkillNum] = attackRestore;
			
			if(GlobalData.iscdModel){
				var helpObj3:Object = CommonPool.fromPoolObject();
				helpObj3.skillData = skillData;
				AppFacade.getInstance().sendNotification(NotiConst.ENTER_CD,helpObj3);
			}
			
			
			var proxy1:PlayerProxy = AppFacade.getInstance().retrieveProxy(PlayerProxy.NAME) as PlayerProxy;
			if(skillData["target"] == 1){
				//如果是正在施放技能，并且这个技能也是时间性技能，则需要等到确定能施放，在触发
				if(!attackRestore.isUseSkill){
					calculateDir(this.position,toTarget.position);
				}
				
				proxy1.attack(skillId,1,toTarget.thingId,mc.dir,skillNum,attackAndSkillNum);
			}else if(skillData["point"] == 1){
				//如果是正在施放技能，并且这个技能也是时间性技能，则需要等到确定能施放，在触发
				if(!attackRestore.isUseSkill){
					calculateDir(this.position,toPosition);
				}
				proxy1.attack(skillId,2,toPosition,mc.dir,skillNum,attackAndSkillNum);
			}else{
				proxy1.attack(skillId,0,0,0,skillNum,attackAndSkillNum);
			}
				
				
			
			if(skillData["read"] > 0){
				return;
			}
			//如果是正在施放技能，并且这个技能也是时间性技能，则需要等到确定能施放，在触发
			if(attackRestore.isUseSkill){
				return;
			}
			//如果是冲锋，则只是改变为走路
			//这块也废弃了
			if(skillData["type"] == Skill.RUSH){
				action = AnConst.WALK;
			}else{
				if(!attackAnimationFinish){
					attackFrontAction = _attackFrontAction;
					
				}else{
					attackFrontAction = mc.action;
				}
				if(skillData["action"] == 1 || this is Monster){
					action = AnConst.ATTACK;
				}else{
					action = AnConst.ATTACK2;
				}
				
				//重置动作，播完没播完 都直接重置
				mc.currentFrame = 0;
				isAttackAnimation = false;
				attackAnimationFinish = false;
				mc.addEventListener(Event.COMPLETE,onAttackComplete);
			}
			
		}
		private function onSkillComplete(event:*):void{
			var skillMc:Animation;
			if(event is Event){
				skillMc = event.target as Animation;
			}else{
				skillMc = event as Animation;
			}
			if(skillMc.data["skill"] && skillMc.data["isHitEffect"] == true){
				delete skillHitEffectDict[skillMc.data["skill"]["id"]];
				skillMc.data["skill"] = null;
				skillMc.data["isHitEffect"] = null;
				delete skillMc.data["skill"];
				delete skillMc.data["isHitEffect"];
			}
			skillMc.removeEventListener(Event.COMPLETE,onSkillComplete);
			skillMc.dispose();
			
		}
		public function calculateDir(position:Point3D,position1:Point3D,walkAffect:Boolean = true,show:Boolean = false,dis:Number = 0,time:Number = 0):void{
			if(isWalk && walkAffect){
				return;
			}
			position.clone(helpPos);
			helpPos.y = 0;
			IsoUtils.isoToScreen(helpPos,helpPoint);
			position1.clone(helpPos);
			helpPos.y = 0;
			
			IsoUtils.isoToScreen(helpPos,helpPoint1);
			var radian:Number = Math.atan2(helpPoint1.y-helpPoint.y,helpPoint1.x-helpPoint.x);
			var rotation:Number = radian/Math.PI*180.0;
			if(rotation >= 157.5 || rotation < -157.5){
				mc.dir = AnConst.LEFT;
			}else if(rotation < 157.5 && rotation >=112.5){
				mc.dir = AnConst.LEFT_DOWN;
			}else if(rotation < 112.5 && rotation >= 67.5){
				mc.dir = AnConst.DOWN;
			}else if(rotation < 67.5 && rotation >= 22.5){
				mc.dir = AnConst.RIGHT_DOWN;
			}else if(rotation < 22.5 && rotation >= -22.5){
				mc.dir = AnConst.RIGHT;
			}else if(rotation < -22.5 && rotation >= -67.5){
				mc.dir = AnConst.RIGHT_UP;
			}else if(rotation < -67.5 && rotation >= -112.5){
				mc.dir = AnConst.UP;
			}else if(rotation < -112.5 && rotation >= -157.5){
				mc.dir = AnConst.LEFT_UP;
			}
			//if(this is Player){
			//	if(show){
			//		numTime++;
			//	}
			//	var helpObj:Object = CommonPool.fromPoolObject();
			//	helpObj.dir = mc.dir;
			//	helpObj.message = "<"+mc.dir+","+rotation+","+show+","+numTime+","+dis+","+time+">"+position.toString()+"..."+position1.toString();
			//	AppFacade.getInstance().sendNotification(NotiConst.DEBUG_MESSAGE,helpObj);
				
			//}
		}
		public function attackServer(isAuto:Boolean,target:IsoObject=null,dir:int = 0):void{
			//trace("攻击");
			var toTarget:IsoObject;
			
			toTarget = target;
			
			if(toTarget == null){
				trace("服务器返回的攻击目标为空");
				var helpObj:Object = CommonPool.fromPoolObject();
				helpObj.message = "服务器返回的攻击目标为空";
				AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj);
				return;
			}
			if(dir != 0){
				mc.dir = dir;
			}
			
			
			//如果是自动攻击，才有可能没播完，手动攻击完成，肯定动画也就完成了。
			//如果是自动攻击，并且攻击动画没播完，说明这个动作目前还是攻击工作，不改变记录
			//手动攻击也有可能没完成
			if(!attackAnimationFinish){
				attackFrontAction = _attackFrontAction;
			}else{
				attackFrontAction = mc.action;
			}
			action = AnConst.ATTACK;
			//重置动作，播完没播完 都直接重置
			mc.currentFrame = 0;
			isAttackAnimation = true;
			attackAnimationFinish = false;
			mc.addEventListener(Event.COMPLETE,onAttackComplete);
			
			
		}
		public function attackClient(isAuto:Boolean):void{
			//trace("攻击");
			var toTarget:IsoObject;
			
			toTarget = this.target;
			if(toTarget && /*toTarget.position == null*/toTarget.isDispose){
				this.target = null;
				toTarget = null;
			}
			
			if(toTarget == null){
				trace("请选择一个目标");
				var helpObj:Object = CommonPool.fromPoolObject();
				helpObj.message = "请选择一个目标";
				AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj);
				return;
			}
			
			
				
			//判断距离
			position.clone(helpPos);
			helpPos.y = 0;
			IsoUtils.isoToScreen(helpPos,helpPoint);
			toTarget.position.clone(helpPos);
			helpPos.y = 0;
			IsoUtils.isoToScreen(helpPos,helpPoint1);
			
			var distance:Number = Point.distance(helpPoint,helpPoint1)/GlobalData.scale;
			if(distance > monsterSkillData["attackDistance"]){
				//trace("太远了，打不着");
				isAttack = true;
				var helpObj1:Object = CommonPool.fromPoolObject();
				helpObj1.message = "太远了";
				AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj1);
				return;
			}
			
			//如果在攻击，并且不是自动攻击返回
			if(isAttack && !isAuto){
				trace("冷却中");
				return;
			}
			if(upAttackTime != 0 && upAttackTime+speedData.attackSpeed>JugglerManager.processTime){
				trace("冷却中");
				isAttack = true;
				return;
			}
			calculateDir(this.position,toTarget.position);
			
			
			//做一个施放技能失败还原的记录
			attackNum++;
			attackAndSkillNum++;
			var attackRestore:AttackRestore = AttackRestore.fromPool();
			attackRestore.upAttackTime = upAttackTime;
			attackRestore.attackAndSkillNum = attackAndSkillNum;
			dict[attackAndSkillNum] = attackRestore;
			
			var proxy1:PlayerProxy = AppFacade.getInstance().retrieveProxy(PlayerProxy.NAME) as PlayerProxy;
			proxy1.attack(monsterSkillData["id"],1,toTarget.thingId,mc.dir,attackNum,attackAndSkillNum);
				
			
			
			//不如是自动攻击，用最短的间隔，这样更公平
			if(isAuto){
				upAttackTime = upAttackTime+speedData.attackSpeed;
			}else{
				upAttackTime = JugglerManager.processTime;
			}
			
			//如果是自动攻击，才有可能没播完，手动攻击完成，肯定动画也就完成了。
			//如果是自动攻击，并且攻击动画没播完，说明这个动作目前还是攻击工作，不改变记录
			//手动攻击也有可能没完成
			if(!attackAnimationFinish){
				attackFrontAction = _attackFrontAction;
			}else{
				attackFrontAction = mc.action;
			}
			action = AnConst.ATTACK;
			//重置动作，播完没播完 都直接重置
			mc.currentFrame = 0;
			isAttack = true;
			isAttackAnimation = true;
			attackAnimationFinish = false;
			mc.addEventListener(Event.COMPLETE,onAttackComplete);
			
			
		}
		private function onAttackComplete(event:Event = null):void{
			//如果攻击动画完成了，就不用做这个操作了
			if(attackAnimationFinish){
				return;
			}
			attackAnimationFinish = true;
			action = _attackFrontAction;
			mc.removeEventListener(Event.COMPLETE,onAttackComplete);
		}
		public function omnislashChange(skill:Object,toPosition:Point3D):void{
			if(skill["type"] == Skill.OMNISLASH){
				isUserSkill = true;
				this.skillData = skill;
				skillJumpH = this.monster["rushSpeed"]*2;
				start = this.position.clone(start);
				start.y = 0;
				//被技能影响清空寻路很重要
				//这里没必要清空寻路，因为无敌斩的过程中是不能走路的
				//path = null;
				this.position.y = 0;
				goal = toPosition.clone(goal);
				goal.y = 0;
				IsoUtils.isoToScreen(start,helpPoint);
				IsoUtils.isoToScreen(goal,helpPoint1);
				skillJumpV = Point.distance(helpPoint,helpPoint1)/GlobalData.scale*2;
				interpolateWithArrvie(helpPoint1, helpPoint, skillJumpV/2,helpPoint2);
				this.goal = IsoUtils.screenToIso(helpPoint2,goal);
				moveD = 0;
				calculateDir(this.position,toPosition);
				action = AnConst.STAND;
			}
		}
		public function affectBySkill(skill:Object,toPosition:Point3D):void{
			if(skill["type"] == Skill.CLICK_THE_FLY){
				isUserSkill = true;
				this.skillData = skill;
				start = this.position.clone(start);
				start.y = 0;
				//被技能影响清空寻路很重要
				path = null;
				this.position.y = 0;
				//trace("被击飞的位置"+this.position.x+"+"+this.position.z);
				goal = toPosition.clone(goal);
				goal.y = 0;
				IsoUtils.isoToScreen(goal,helpPoint);
				IsoUtils.isoToScreen(start,helpPoint1);
				var distance:Number = 0;
				distance = Point.distance(helpPoint,helpPoint1)/GlobalData.scale;
				skillJumpH = distance*Math.abs(((speedData.jumpVerticalA/speedData.jumpVerticalV)/2));
				skillJumpV = speedData.jumpVerticalV;
				
				
				//trace(distance);
				moveD = 0;
				//如果不是本人，就直接取消读条
				if(isOther){
					cannelReadSkill();
				}
			}
		}
		public function skillResult(result:int,skillId:int,attackNum:int,attackAndSkillNum:int,toTarget:ActivityThing,toPosition:Point3D,type:int,flyThingId:int):void{
			
			var attackRestore:AttackRestore;
			attackRestore = dict[attackAndSkillNum];
			if(result == 0){
				if(int(skillId/10000) == 1){
					//都相等，就说明现在播的还是这次普通攻击
					if(this.attackNum == attackNum && this.attackAndSkillNum == attackAndSkillNum){
						//加一个200毫秒的延迟攻击，这样不至于发好多次卡顿，减少服务器压力
						this.upAttackTime = attackRestore.upAttackTime+200;
						onAttackComplete();
						trace("取消了攻击")
						//这次普通攻击之后，又进行了普通攻击，这很有可能是网络延迟产生的问题，不作处理，处理后续的普通攻击回来的包
					}else if(attackNum < this.attackNum){
						trace("网络延迟太大，这次返回的攻击已经不是上一次攻击了")
						//如果没有再增加普通攻击，而进行放技能了，只需要还原普通攻击的时间，不还原动作，因为已经放技能了
					}else if(this.attackNum == attackNum && this.attackAndSkillNum > attackAndSkillNum){
						//加一个200毫秒的延迟攻击，这样不至于发好多次卡顿，减少服务器压力
						this.upAttackTime = attackRestore.upAttackTime+200;
					}
				}else{
					if(this.skillNum == attackNum && this.attackAndSkillNum == attackAndSkillNum){
						if(!attackRestore.isUseSkill){
							onAttackComplete();
						}
						//如果是不叫用户操作的技能，技能施放错误时，恢复用户操作
						var skill:Object = GlobalData.skillData[skillId];
						if(skill["type"] == Skill.FLASH){
							GlobalData.nowBeEffectOnSkill--;
						}else if(skill["type"] == Skill.BIG_JUMP){
							if(!attackRestore.isUseSkill){
								//还原位置
								position = attackRestore.position;
								//取消大跳
								isUserSkill = false;
								this.skillData = null;
								skillJumpH = 0;
								skillJumpV = 0;
								if(goal){
									goal.setValue(0,0,0);
									start.setValue(0,0,0);
								}
								moveD = 0;
							}else{
								GlobalData.nowBeEffectOnSkillDistance--;
							}
							
						}else if(skill["type"] == Skill.RUSH){
							if(!attackRestore.isUseSkill){
								//还原位置
								position = attackRestore.position;
								//取消大跳
								isUserSkill = false;
								this.skillData = null;
								skillJumpH = 0;
								skillJumpV = 0;
								if(goal){
									goal.setValue(0,0,0);
									start.setValue(0,0,0);
								}
								moveD = 0;
							}else{
								GlobalData.nowBeEffectOnSkillDistance--;
							}
						}else if(skill["type"] == Skill.OMNISLASH){
							//不用判断是否用技能了，无敌斩肯定是
							GlobalData.nowBeEffectOnSkillOmnislash--;
						}else if(skill["read"] > 0){
							cannelReadSkill();
							AppFacade.getInstance().sendNotification(NotiConst.CANNEL_SKILL_PROGRESS);
						}
						
					}
					//如果是人物身上的技能特效，并且特效还没有删除，直接调特效完成
					if(!attackRestore.isUseSkill){
						if(attackRestore.skillMc && attackRestore.skillMc.parent){
							onSkillComplete(attackRestore.skillMc);
							//如果是飞行道具，并且没有移除，直接删除
						}else if(attackRestore.flySkillEffect && !attackRestore.flySkillEffect.isDispose){
							attackRestore.flySkillEffect.dispose();
							//如果是场景里的面技能效果，直接删除
						}else if(attackRestore.skillEffect && !attackRestore.skillEffect.isDispose){
							attackRestore.skillEffect.dispose();
						}else if(attackRestore.readMc && attackRestore.readMc.parent){
							attackRestore.readMc.dispose();
						}
					}
				}
				//trace("取消了技能："+skillId);
				var helpObj:Object = CommonPool.fromPoolObject();
				helpObj.message = "取消了技能："+skillId;
				AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj);
			}else{
				
				if(attackRestore.isUseSkill){
					var skillData:Object = GlobalData.skillData[skillId];
					var skillMc:Animation;
					if(skillData["src"]){
						skillMc = Animation.fromPool(AnCategory.EFFECT,skillData["src"],AnConst.STAND,false,skillData["fps"]/1000.0,AnConst.DOWN);
						//skillMc.dir = mc.dir;
						//skillMc.fps = skillData["fps"]/1000.0;
					}
					
					if(skillData["isfly"]){
						var flySkillEffect:FlySkillEffect = FlySkillEffect.fromPool(this.size);
						this.position.clone(helpPos);
						helpPos.y = 0;
						flySkillEffect.position = helpPos;
						flySkillEffect.yValue = skillData["y"];
						flySkillEffect.thingId = flyThingId;
						if(skillMc){
							flySkillEffect.addChild(skillMc);
						}
						flySkillEffect.skillData = skillData;
						if(skillData["point"] == 1){
							flySkillEffect.toPosition = toPosition;
						}else if(skillData["target"] == 1){
							flySkillEffect.target = toTarget;
						}
						GlobalData.map1.addChildToWorld(flySkillEffect);
						
						
						
					}else{
						if(skillMc){
							//如果是跟随玩家的
							if(skillData["isfollow"]){
								if(skillData["isonplayerup"]){
									addChild(skillMc);
								}else{
									addChildAt(skillMc,this.getChildIndex(mc as DisplayObject));
								}
								skillMc.addEventListener(Event.COMPLETE,onSkillComplete);
								
							}else{
								
								var skillEffect:SkillEffect = SkillEffect.fromPool(this.size);
								if(skillData["point"] == 1 && skillData["pointType"] == 0){
									skillEffect.position = toPosition;
								}else if(skillData["target"] == 1 && skillData["pointType"] == 0){
									toTarget.position.clone(helpPos);
									helpPos.y = 0;
									skillEffect.position = helpPos;
								}else if(skillData["pointType"] == 1){
									this.position.clone(helpPos);
									helpPos.y = 0;
									skillEffect.position = helpPos;
								}
								skillEffect.addChild(skillMc);
								skillEffect.addEvent();
								GlobalData.map1.addChildToWorld(skillEffect);
								
							}
						}
					}
					
					
					if(skillData["type"] == Skill.BIG_JUMP){
						
						isUserSkill = true;
						this.skillData = skillData;
						start = this.position.clone(start);
						start.y = 0;
						this.position.y = 0;
						goal = toPosition.clone(goal);
						goal.y = 0;
						IsoUtils.isoToScreen(goal,helpPoint);
						IsoUtils.isoToScreen(start,helpPoint1);
						var distance:Number = 0;
						distance = Point.distance(helpPoint,helpPoint1)/GlobalData.scale;
						skillJumpH = distance*Math.abs(((speedData.jumpVerticalA/speedData.jumpVerticalV)/2));
						skillJumpV = speedData.jumpVerticalV;
						
						
						//trace(distance);
						moveD = 0;
						GlobalData.nowBeEffectOnSkillDistance--;
					}else if(skillData["type"] == Skill.RUSH){
						
						isUserSkill = true;
						this.skillData = skillData;
						skillJumpH = this.monster["rushSpeed"];
						//skillJumpV = 0;
						goal = toPosition.clone(goal);
						goal.y = 0;
						start = this.position.clone(start);
						start.y = 0;
						this.position.y = 0;
						IsoUtils.isoToScreen(start,helpPoint);
						IsoUtils.isoToScreen(goal,helpPoint1);
						skillJumpV = Point.distance(helpPoint,helpPoint1)/GlobalData.scale;
						//trace(distance);
						moveD = 0;
						GlobalData.nowBeEffectOnSkillDistance--;
						trace(GlobalData.nowBeEffectOnSkillDistance);
					//无敌斩在这块不能取消限制，因为后续服务器还会有很多操作
					}else if(skillData["type"] == Skill.OMNISLASH){
						isUserSkill = true;
						this.skillData = skillData;
						skillJumpH = this.monster["rushSpeed"]*2;
						goal = toPosition.clone(goal);
						goal.y = 0;
						start = this.position.clone(start);
						start.y = 0;
						this.position.y = 0;
						IsoUtils.isoToScreen(start,helpPoint);
						IsoUtils.isoToScreen(goal,helpPoint1);
						skillJumpV = Point.distance(helpPoint,helpPoint1)/GlobalData.scale*2;
						
						
						interpolateWithArrvie(helpPoint1, helpPoint, skillJumpV/2,helpPoint2);
						this.goal = IsoUtils.screenToIso(helpPoint2,goal);
						
						
						moveD = 0;
					}
					
					if(skillData["target"] == 1 && skillData["type"] != Skill.RUSH){
						calculateDir(this.position,toTarget.position);
					}else if(skillData["point"] == 1 || skillData["type"] == Skill.RUSH){
						calculateDir(this.position,toPosition);
					}
					
					
					//如果是冲锋，则只是改变为走路
					if(skillData["type"] == Skill.RUSH){
						action = AnConst.WALK;
					}else if(skillData["type"] == Skill.OMNISLASH){
						action = AnConst.STAND;
					}else{
						if(!attackAnimationFinish){
							attackFrontAction = _attackFrontAction;
							
						}else{
							attackFrontAction = mc.action;
						}
						if(skillData["action"] == 1 || this is Monster){
							action = AnConst.ATTACK;
						}else{
							action = AnConst.ATTACK2;
						}
						
						//重置动作，播完没播完 都直接重置
						mc.currentFrame = 0;
						isAttackAnimation = false;
						attackAnimationFinish = false;
						mc.addEventListener(Event.COMPLETE,onAttackComplete);
					}
					
				}
				
				
				
			}
			attackRestore.dispose();
			delete dict[attackAndSkillNum];
		}
		/*
		private function onJumpComplete(event:Event):void{
			mc.removeEventListener(Event.COMPLETE,onJumpComplete);
			mc.stop();
		}
		*/
		public function setShadow(shadow:DrawnIsoTile,filter:BlurFilter):void{
			this.shadow = shadow;
			this.filter = filter;
			shadow.x = this.x;
			shadow.z = this.z;
		}
		public function life(hp:int):void{
			if(!isDead){
				trace("还么死呢，服务器出问题了");
				return;
			}
			speedData.hp = hp;
			isDead = false;
			visible = true;
		}
		
		public function clearSkill(isSetPos:Boolean = true,skillId:int = 0):void{
			if(isUserSkill){
				if(isSetPos){
					position = goal;
				}
				isUserSkill = false;
				this.skillData = null;
				skillJumpH = 0;
				skillJumpV = 0;
				if(goal){
					goal.setValue(0,0,0);
					start.setValue(0,0,0);
				}
				moveD = 0;
				
				if(shadow){
					shadow.x = this.x;
					shadow.z = this.z;
					filter.blurX = filter.blurY = -y * .25;
					shadow.filters = [filter];
				}
			}
			
		}
		public function advanceTime(passedTime:Number):void
		{
			/*if(target){
			trace(target.toString());
			}*/
			//及时清除不存在的目标
			if(target && (/*target.position == null*/target.isDispose || target.isDead)){
				target = null;
			}
			if(isDizzy() || isIceBox() || isCast()){
				return;
			}
			upAttackTime = (upAttackTime+speedData.attackSpeed)<=JugglerManager.processTime?JugglerManager.processTime-speedData.attackSpeed:upAttackTime;
			
			var moveDistance:Number;
			
			if(isAttack){
				if(upAttackTime+speedData.attackSpeed <=JugglerManager.processTime){
					//不被技能影响着才能发起自动攻击，不然闪现时，马上攻击可能会出问题，屏蔽这个问题
					if(isAutoAttack && target){
						//无敌斩自动攻击，可能会影响动作，所以暂时不能攻击
						if(GlobalData.nowBeEffectOnSkillOmnislash == 0 && (!isUserSkill || (isUserSkill  && skillData["type"] != Skill.OMNISLASH)) && nowReadSkill == null){
							attackClient(true);
							//trace("自动攻击了");
						}else{
							
						}
						
					}else{
						isAttack = false;
						
						//如果是普通攻击的动画，就设置攻击完成，换动作，移除监听(因为在普通攻击之后接技能时，普通攻击完成会提前，不能终止技能的动作)
						//如果是普通攻击的动画，并且动画没完成在进入这里，
						//1.如果不是普通攻击的动画，不能在这里作控制，
						//2.如果是普通攻击的动画，并且完成了，这里的代码也没有意义的了
						if(isAttackAnimation && !attackAnimationFinish){
							attackAnimationFinish = true;
							action = _attackFrontAction;
							mc.removeEventListener(Event.COMPLETE,onAttackComplete);
						}
					}
					
				}
			}
			if(isWalk && passedTime > 0.0){
				if(isLine){
					walkMoveD += passedTime*nowMoveV;
					if(walkMoveD > walkAllDis){
						walkMoveD = walkAllDis;
					}
					IsoUtils.isoToScreen(walkGoal,helpPoint);
					IsoUtils.isoToScreen(walkStart,helpPoint1)
					interpolate(helpPoint,helpPoint1,walkMoveD*GlobalData.scale,helpPoint2);
					
					IsoUtils.screenToIso(helpPoint2,helpPos);
					
					
					if(walkMoveD  == walkAllDis){
						position = walkGoal;
						path = null;
					}else{
						position = helpPos;
					}
					//if(!isOther){
					//	trace("当前位置"+position.x+","+position.z);
					//}
					if(shadow){
						shadow.x = this.x;
						shadow.z = this.z;
						filter.blurX = filter.blurY = -y * .25;
						shadow.filters = [filter];
					}
					return;
				}
				IsoUtils.isoToScreen(position,helpPoint);
				var distance:Number = 0;
				if(nowNode == null){
					while(distance == 0 && _path.length > 0){
						nowNode = _path.shift();
						helpPos.setValue(nowNode.x*size,0,nowNode.y*size);
						nowArrivePoint = IsoUtils.isoToScreen(helpPos,nowArrivePoint);
						//如果是同一个格，直接连接下一个不同的格，不在走到同一个格的中心点再连接
						if(nowNode.x == Math.round(position.x/size) && nowNode.y == Math.round(position.z/size)){
							distance = Point.distance(helpPoint,nowArrivePoint);
							if(distance > 0){
								//trace(distance);
							}
							distance = 0;
						}else{
							distance = Point.distance(helpPoint,nowArrivePoint);
						}
						
					}
				}else{
					distance = Point.distance(helpPoint,nowArrivePoint);
					if(distance == 0){
						while(distance == 0 && _path.length > 0){
							nowNode = _path.shift();
							helpPos.setValue(nowNode.x*size,0,nowNode.y*size);
							nowArrivePoint = IsoUtils.isoToScreen(helpPos,nowArrivePoint);
							distance = Point.distance(helpPoint,nowArrivePoint);
						}
					}
					
				}
				if(distance == 0){
					nowNode = null;
					
					if(nowArrivePoint){
						nowArrivePoint.x = 0;
						nowArrivePoint.y = 0;
					}
					action = AnConst.STAND;
					isWalk = false;
					//跟随者isWalk
					if(!isOther && GlobalData.nowTargetEffect && !GlobalData.nowTargetEffect.isDispose){
						GlobalData.nowTargetEffect.dispose();
						GlobalData.nowTargetEffect = null;
					}
					return;
				}
				
				
				
				moveDistance = passedTime*nowMoveV;
				var dis:Number = moveDistance;
				
				
				
				if(moveDistance > distance){
					var bool:Boolean = true;
					while(moveDistance > distance){
						moveDistance = moveDistance-distance;
						distance = 0;
						while(distance == 0 && _path.length > 0){
							helpPos.setValue(nowNode.x*size,0,nowNode.y*size);
							IsoUtils.isoToScreen(helpPos,helpPoint);
							
							nowNode = _path.shift();
							helpPos.setValue(nowNode.x*size,0,nowNode.y*size);
							nowArrivePoint = IsoUtils.isoToScreen(helpPos,nowArrivePoint);
							
							
							distance = Point.distance(helpPoint,nowArrivePoint);
						}
						if(distance == 0){
							moveDistance = 0;
							helpPoint1.x = nowArrivePoint.x;
							helpPoint1.y = nowArrivePoint.y;
							nowNode = null;
							if(nowArrivePoint){
								nowArrivePoint.x = 0;
								nowArrivePoint.y = 0;
							}
							action = AnConst.STAND;
							isWalk = false;
							//跟随者isWalk
							if(!isOther && GlobalData.nowTargetEffect && !GlobalData.nowTargetEffect.isDispose){
								GlobalData.nowTargetEffect.dispose();
								GlobalData.nowTargetEffect = null;
							}
							bool = false;
						}else{
							
						}
					}
					if(bool){
						
						interpolate(nowArrivePoint,helpPoint,moveDistance,helpPoint1);
						
						
					}
					
					
					
				}else{
					
					interpolate(nowArrivePoint,helpPoint,moveDistance,helpPoint1);
					
				}
				
				IsoUtils.screenToIso(helpPoint1,helpPos1);
				IsoUtils.screenToIso(helpPoint,helpPos2);
				calculateDir(helpPos2,helpPos1,false,true,dis,passedTime);
				position = helpPos1;
				
				
				if(shadow){
					shadow.x = this.x;
					shadow.z = this.z;
					filter.blurX = filter.blurY = -y * .25;
					shadow.filters = [filter];
				}
				
			}else if(isUserSkill && passedTime > 0.0){
				if(isWalk){
					trace("警告”“”“”“”，被技能影响还在寻路");
				}
				if(skillData["type"] == Skill.BIG_JUMP || skillData["type"] == Skill.CLICK_THE_FLY){
					moveD += passedTime*skillJumpH;
					IsoUtils.isoToScreen(goal,helpPoint);
					IsoUtils.isoToScreen(start,helpPoint1)
					interpolate(helpPoint,helpPoint1,moveD*GlobalData.scale,helpPoint2);
					
					IsoUtils.screenToIso(helpPoint2,helpPos);
					/*****这快不用计算缩放比，这个改变的是一个3D坐标系，转换成2D坐标系时，自动会扩大*/
					helpPos.y = -(skillJumpV*passedTime+speedData.jumpVerticalA*passedTime*passedTime/2)+position.y;
					skillJumpV = skillJumpV+speedData.jumpVerticalA*passedTime;
					
					if(helpPos.y >= 0){
						position = goal;
						isUserSkill = false;
						this.skillData = null;
						skillJumpH = 0;
						skillJumpV = 0;
						if(goal){
							goal.setValue(0,0,0);
							start.setValue(0,0,0);
						}
						moveD = 0;
					}else{
						position = helpPos;
					}
					
					
				}else if(skillData["type"] == Skill.RUSH){
					moveD += passedTime*skillJumpH;
					if(moveD > skillJumpV){
						moveD = skillJumpV;
					}
					IsoUtils.isoToScreen(goal,helpPoint);
					IsoUtils.isoToScreen(start,helpPoint1)
					interpolate(helpPoint,helpPoint1,moveD*GlobalData.scale,helpPoint2);
					
					IsoUtils.screenToIso(helpPoint2,helpPos);
					
					
					if(moveD  == skillJumpV){
						position = goal;
						isUserSkill = false;
						this.skillData = null;
						skillJumpH = 0;
						skillJumpV = 0;
						if(goal){
							goal.setValue(0,0,0);
							start.setValue(0,0,0);
						}
						moveD = 0;
						action = AnConst.STAND;
					}else{
						position = helpPos;
					}
				}else if(skillData["type"] == Skill.OMNISLASH){
					moveD += passedTime*skillJumpH;
					if(moveD > skillJumpV){
						moveD = skillJumpV;
					}
					IsoUtils.isoToScreen(goal,helpPoint);
					IsoUtils.isoToScreen(start,helpPoint1)
					interpolate(helpPoint,helpPoint1,moveD*GlobalData.scale,helpPoint2);
					
					IsoUtils.screenToIso(helpPoint2,helpPos);
					
					helpPos.y = -60;
					if(moveD  == skillJumpV){
						position = goal;
						isUserSkill = false;
						this.skillData = null;
						skillJumpH = 0;
						skillJumpV = 0;
						if(goal){
							goal.setValue(0,0,0);
							start.setValue(0,0,0);
						}
						moveD = 0;
						action = AnConst.STAND;
						if(GlobalData.nowBeEffectOnSkillOmnislash <= 0 && !isOther){
							GlobalData.game.startTrace(2,this,200);
							trace("客户端后置了");
						}
					}else{
						position = helpPos;
					}
					
				}
				if(shadow){
					shadow.x = this.x;
					shadow.z = this.z;
					filter.blurX = filter.blurY = -y * .25;
					shadow.filters = [filter];
				}
			}else if(nowReadSkill){
				if(isOther){
					if(nowReadSkill["target"] == 1 && (nowReadTarget && (/*nowReadTarget.position == null*/nowReadTarget.isDispose || nowReadTarget.isDead))){
						cannelReadSkill();
					}
					if(nowReadSkill != null && JugglerManager.processTime-nowReadTime >= nowReadSkill["read"]){
						cannelReadSkill();			
					}
				}
			}
			
		}
		
		
		public static function sortBuff(buffList:BuffArray):void{
			var size:int = buffList.buffArray.length;
			//只取出一个最大的就行了
			for(var i:int = 0;i < 1;i++){
				for(var j:int = i+1;j<size;j++){
					var  buffInfo:BuffInfo = buffList.buffArray[i];
					var  buffInfo1:BuffInfo = buffList.buffArray[j];
					//交换位置
					if(Math.abs(buffInfo1.buffData["value"]) > Math.abs(buffInfo.buffData["value"])){
						buffList.buffArray[i] = buffInfo1;
						buffList.buffArray[j] = buffInfo;
					}
					
				}
			}
		}
		
	}
}