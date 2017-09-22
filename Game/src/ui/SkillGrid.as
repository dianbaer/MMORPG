package ui
{
	import flash.display.Bitmap;
	
	import UI.theme.defaulttheme.Grid;
	
	import _45degrees.com.friendsofed.isometric.ActivityThing;
	
	import gui.animation.IAnimatable;
	import gui.animation.JugglerManager;
	
	public class SkillGrid extends Grid implements IAnimatable
	{
		
		public var cdMask:Bitmap;
		//开始时间
		private var startTime:int = 0;
		//结束时间
		private var endTime:int = 0;
		//cd时间
		private var cdTime:int = 0;
		//是否在CD
		public var isInCD:Boolean;
		private var target:ActivityThing;
		public var buffData:Object;
		public var isCancelSkill:Boolean;
		public function SkillGrid()
		{
			super();
			cdMask = new Bitmap();
			cdMask.visible = false;
			addChild( cdMask );
		}
		override protected function draw () : void
		{
			super.draw();
			cdMask.x = gapImageToGrid;
			cdMask.y = gapImageToGrid;
		}
		public function setTarget(target:ActivityThing):void{
			this.target = target;
		}
		public function addToJuggler():void{
			JugglerManager.fourJuggler.add(this);
		}
		//是否是公共cd true是，false不是
		public function enterCD(bool:Boolean):void{
			if(bool){
				//没有公共cd
				if(data.data["isAllowCommonCD"] == 0){
					return;
				}
				if(isInCD){
					var time:int = JugglerManager.processTime+1000;
					if(time >= endTime){
						endTime = time;
						cdTime = 1000;
						startTime = JugglerManager.processTime;
						cdMask.bitmapData = SkillUI.arrCache[0];
						cdMask.visible = true;
					}else{
						return;
					}
				}else{
					endTime = JugglerManager.processTime+1000;
					cdTime = 1000;
					startTime = JugglerManager.processTime;
					cdMask.bitmapData = SkillUI.arrCache[0];
					cdMask.visible = true;
				}
			}else{
				//有错
				if(isInCD){
					return;
				}else{
					endTime = JugglerManager.processTime+data.data["cd"];
					cdTime = data.data["cd"];
					startTime = JugglerManager.processTime;
					cdMask.bitmapData = SkillUI.arrCache[0];
					cdMask.visible = true;
				}
			}
			isInCD = true;
		}
		//退出cd
		public function exitCD():void{
			if(isInCD){
				isInCD = false;
				cdMask.visible = false;
				cdMask.bitmapData = null;
			}
		}
		public function advanceTime(time:Number):void{
			onFrame();
		}
		public function onFrame():void{
			if(isInCD){
				if(target && target.isDispose){
					target = null;
				}
				if(target && buffData){
					var buffArray:BuffArray = target.buffDict[buffData["type"]];
					if(buffArray && buffArray.buffArray.length > 0){
						for(var i:int = 0;i<buffArray.buffArray.length;i++){
							var buffInfo:BuffInfo = buffArray.buffArray[i];
							if(buffInfo.buffData["id"] == buffData["id"] && buffInfo.masterId == target.thingId){
								if(!isCancelSkill){
									data.imageUrl = "skill/"+data.data["cancelicon"];
									data = data;
									cdMask.visible = false;
									isCancelSkill = true;
								}
								return;
							}
						}
					}
					
				}
				if(isCancelSkill){
					data.imageUrl = "skill/"+data.data["icon"];
					data = data;
					isCancelSkill = false;
				}
				
				var time:int = JugglerManager.processTime-startTime;
				if(time >= cdTime){
					isInCD = false;
					cdMask.visible = false;
					cdMask.bitmapData = null;
				}else{
					var pos:int = Math.round(SkillUI.arrCache.length*time/cdTime);
					if(pos >= SkillUI.arrCache.length){
						pos = SkillUI.arrCache.length-1;
					}
					cdMask.visible = true;
					cdMask.bitmapData = SkillUI.arrCache[pos];
				}
			}else{
				if(target && target.isDispose){
					target = null;
				}
				if(target && buffData){
					var buffArray1:BuffArray = target.buffDict[buffData["type"]];
					if(buffArray1 && buffArray1.buffArray.length > 0){
						for(var j:int = 0;j<buffArray1.buffArray.length;j++){
							var buffInfo1:BuffInfo = buffArray1.buffArray[j];
							if(buffInfo1.buffData["id"] == buffData["id"] && buffInfo1.masterId == target.thingId){
								if(!isCancelSkill){
									data.imageUrl = "skill/"+data.data["cancelicon"];
									data = data;
									cdMask.visible = false;
									isCancelSkill = true;
								}
								return;
							}
						}
					}
				}
				if(isCancelSkill){
					data.imageUrl = "skill/"+data.data["icon"];
					data = data;
					isCancelSkill = false;
				}
			}
			
			
		}
		override public function dispose () : void
		{
			cdMask = null;
			target = null;
			super.dispose();
		}
	}
}