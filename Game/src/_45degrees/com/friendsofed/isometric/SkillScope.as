package _45degrees.com.friendsofed.isometric
{
	public class SkillScope extends IsoObject
	{
		private var _skillData:Object;
		private var type:int = 0;
		public function SkillScope(size:Number)
		{
			super(size);
		}
		public function set skillData(skillData:Object):void{
			if(this._skillData != null && this._skillData["id"] == skillData["id"]){
				return;
			}
			this._skillData = skillData;
			type = 0;
			this.graphics.clear();
		}
		public function canUse():void{
			if(type == 1){
				return;
			}
			type = 1;
			changeScale();
		}
		public function changeScale():void{
			if(type == 1){
				this.graphics.clear();
				this.graphics.lineStyle(1,0x00ff00);
				this.graphics.beginFill(0x00ff00,0.3);
				//this.graphics.drawCircle(0,0,_skillData["scopeValue"]);
				this.graphics.drawEllipse(-_skillData["scopeValue"]*GlobalData.scale,-_skillData["scopeValue"]*GlobalData.scale/2,_skillData["scopeValue"]*GlobalData.scale*2,_skillData["scopeValue"]*GlobalData.scale);
				this.graphics.endFill();
			}else if(type == 2){
				this.graphics.clear();
				this.graphics.lineStyle(1,0xff0000);
				this.graphics.beginFill(0xff0000,0.3);
				this.graphics.drawEllipse(-_skillData["scopeValue"]*GlobalData.scale,-_skillData["scopeValue"]*GlobalData.scale/2,_skillData["scopeValue"]*GlobalData.scale*2,_skillData["scopeValue"]*GlobalData.scale);
				this.graphics.endFill();
			}
		}
		public function nowCanUse():void{
			if(type == 2){
				return;
			}
			type = 2;
			changeScale();
		}
		
		override public function dispose():void{
			clear();
			if(parent){
				parent.removeChild(this);
			}
			super.dispose();
		}
		public function clear():void{
			this.graphics.clear();
			this._skillData = null;
			type = 0;
		}
	}
}