package _45degrees.com.friendsofed.isometric
{
	public class DrawnIsoTile extends IsoObject
	{
		protected var _color:uint;
		public function DrawnIsoTile(size:Number, color:uint)
		{
			super(size);
			this.color = color;
			
		}
		public function draw():void
		{
			graphics.clear();
			graphics.beginFill(color);
			graphics.lineStyle(0, 0, .5);
			graphics.moveTo(-size*GlobalData.scale, 0);
			graphics.lineTo(0, -size*GlobalData.scale * .5);
			graphics.lineTo(size*GlobalData.scale, 0);
			graphics.lineTo(0, size*GlobalData.scale * .5);
			graphics.lineTo(-size*GlobalData.scale, 0);
			
		}
		public function set color(value:uint):void
		{
			_color = value;
			draw();
		}
		public function get color():uint
		{
			return _color;
		}
	}
}