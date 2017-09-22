package _45degrees.com.friendsofed.isometric;

import java.awt.geom.Point2D;




public class IsoUtils
{
	
	public static Double Y_CORRECT = Math.cos(-Math.PI / 6) * Math.sqrt(2);
	
	
	public static Point2D.Double isoToScreen(Point3D pos)
	{
		
		double screenX = pos.x - pos.z;
		double screenY = pos.y * Y_CORRECT + (pos.x + pos.z) * .5;
		
		return new Point2D.Double(screenX, screenY);
		
	}
	
	
	public static Point3D screenToIso(Point2D.Double point)
	{
		double xpos = point.y + point.x * .5;
		double ypos = 0;
		double zpos = point.y - point.x * .5;
		return new Point3D(xpos, ypos, zpos);
	}
	
}