package _45degrees.com.friendsofed.isometric
{
	public class PitchOnFrame extends IsoObject
	{
		private var _target:ActivityThing;
		private var type:int = 0;
		public function PitchOnFrame(size:Number)
		{
			super(size);
		}

		public function set target(value:ActivityThing):void
		{
			if(value == _target){
				return;
			}
			_target = value;
			type = 0;
		}
		public function select():void{
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
				this.graphics.drawEllipse(-_target.standWidth*GlobalData.scale/2,-_target.standWidth*GlobalData.scale/4,_target.standWidth*GlobalData.scale,_target.standWidth*GlobalData.scale/2);
				this.graphics.endFill();
			}else if(type == 2){
				this.graphics.clear();
				this.graphics.lineStyle(1,0xff0000);
				this.graphics.beginFill(0xff0000,0.3);
				this.graphics.drawEllipse(-_target.standWidth*GlobalData.scale/2,-_target.standWidth*GlobalData.scale/4,_target.standWidth*GlobalData.scale,_target.standWidth*GlobalData.scale/2);
				this.graphics.endFill();
			}
		}
		public function attack():void{
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
			_target = null;
			this.graphics.clear();
			type = 0;
		}
	}
}