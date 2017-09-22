package cyou.akworld;

import java.awt.Point;
import java.awt.geom.Point2D;

import tcp.SceneData;
import _45degrees.com.friendsofed.isometric.Point3D;

public class Thing {
	public Point3D position;
	public boolean isDispose = false;
	public SceneData sceneData;
	//对于人包括移动时间，施放大跳时间，和死亡刷新时间，这几个时间都是相互冲突的所以没问题
	//对于怪包括移动时间，施放大跳时间，死亡时间，追踪时间，回去时间
	//对于飞行道具飞行时间
	public long time;
	public int size;
	//clear，不要清除这个，在场景线程设置state=2时，可能主线程就同时会调用clear()导致，场景线程无法广播从该场景删除此玩家
	protected int serverId;
	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	public void update(){
		
	}
	public static Point2D.Double interpolate(Point.Double arrivePoint,Point.Double startPoint, double moveDistance){
		double radian = Math.atan2(arrivePoint.y-startPoint.y,arrivePoint.x-startPoint.x);
		
		Point.Double point = new Point.Double();
		point.x = Math.cos(radian)*moveDistance+startPoint.x;
		point.y = Math.sin(radian)*moveDistance+startPoint.y;
		return point;
	}
	public static Point2D.Double interpolateWithArrvie(Point.Double arrivePoint,Point.Double startPoint, double moveDistance){
		double radian = Math.atan2(arrivePoint.y-startPoint.y,arrivePoint.x-startPoint.x);
		
		Point.Double point = new Point.Double();
		point.x = Math.cos(radian)*moveDistance+arrivePoint.x;
		point.y = Math.sin(radian)*moveDistance+arrivePoint.y;
		return point;
	}
	public void clear(){
		position = null;
		sceneData = null;
		isDispose = true;
	}
}
