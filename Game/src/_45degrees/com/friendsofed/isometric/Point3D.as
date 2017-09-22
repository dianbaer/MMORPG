package _45degrees.com.friendsofed.isometric
{
	public class Point3D
	{
		public var x:Number;
		public var y:Number;
		public var z:Number;
		
		public function Point3D(x:Number = 0, y:Number = 0, z:Number = 0)
		{
			this.x = x;
			this.y = y;
			this.z = z;
		}
		public function setValue(x:Number = 0, y:Number = 0, z:Number = 0):void{
			this.x = x;
			this.y = y;
			this.z = z;
		}
		public function clone(pos:Point3D = null):Point3D{
			if(pos == null) pos = new Point3D();
			pos.setValue(this.x,this.y,this.z);
			
			return pos;
		}
		public function toString():String{
			return x+","+y+","+z;
		}
	}
}