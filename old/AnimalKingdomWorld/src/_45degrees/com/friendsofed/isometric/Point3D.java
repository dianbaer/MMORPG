package _45degrees.com.friendsofed.isometric;

public class Point3D
{
	public Double x;
	public Double y;
	public Double z;
	
	public Point3D(Double x, Double y, Double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public Point3D clone(){
		Point3D point3D = new Point3D(this.x,this.y,this.z);
		return point3D;
	}
}
