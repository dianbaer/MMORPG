package _45degrees.com.friendsofed.isometric
{
	import flash.geom.Point;
	
	public class IsoUtils
	{
		// a more accurate version of 1.2247...
		public static const Y_CORRECT:Number = Math.cos(-Math.PI / 6) * Math.sqrt(2);
		
		/**
		 * Converts a 3D point in isometric space to a 2D screen position.
		 * @arg pos the 3D point.
		 */
		public static function isoToScreen(pos:Point3D,point:Point = null):Point
		{
			if(point == null) point = new Point();
			point.x = (pos.x - pos.z)*GlobalData.scale;
			point.y = (pos.y * Y_CORRECT + (pos.x + pos.z) * .5)*GlobalData.scale;
			return point;
		}
		
		/**
		 * Converts a 2D screen position to a 3D point in isometric space, assuming y = 0.
		 * @arg point the 2D point.
		 */
		public static function screenToIso(point:Point,pos:Point3D = null):Point3D
		{
			if(pos == null) pos = new Point3D();
			pos.x = (point.y/GlobalData.scale) + (point.x/GlobalData.scale) * .5;
			pos.y = 0;
			pos.z = (point.y/GlobalData.scale) - (point.x/GlobalData.scale) * .5;
			return pos;
		}
		
	}
}