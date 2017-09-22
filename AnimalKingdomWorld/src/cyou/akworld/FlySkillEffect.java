package cyou.akworld;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.io.tcp.TcpPacket;

import _45degrees.com.friendsofed.isometric.IsoUtils;
import _45degrees.com.friendsofed.isometric.Point3D;

public class FlySkillEffect extends Thing{
	private static Logger log = LoggerFactory.getLogger(WebServer.class);
	
	public JSONObject skillData;
	public ActivityThing toTarget;
	public Point3D toPosition;
	public Point2D.Double toPoint;
	public ActivityThing master;
	private double _yValue;
	private boolean canDispose = false;
	public ArrayList<WebServer> horizonPlayer;
	public int flyTime = 1;
	public double get_yValue() {
		return _yValue;
	}
	public void set_yValue(double _yValue) {
		this._yValue = _yValue;
		position.y = _yValue;
	}
	public void update(){
		if(skillData.getInt("point") == 1){
			if(canDispose){
				//计算伤害值
				
				SkillData.selectTarget(skillData, master, null,toPosition,sceneData,null);
				clear();
				return;
			}
			double moveDistance;
			long nowTime = System.currentTimeMillis();
			moveDistance = (nowTime-time)/1000.0*skillData.getInt("flyspeed");
			if(moveDistance != 0){
				time = nowTime;
			}
			Point2D.Double point = IsoUtils.isoToScreen(new Point3D(position.x,0.0,position.z));
			
			
			double distance = Point.distance(point.x,point.y,toPoint.x,toPoint.y);
			
			if(moveDistance >= distance){
				position = new Point3D(toPosition.x,_yValue,toPosition.z);
				canDispose = true;
			}else{
				
				Point2D.Double point2 = interpolate(toPoint,point,moveDistance);
				Point3D point3D = IsoUtils.screenToIso(point2);
				point3D.y = _yValue;
				position = point3D;
			}
		}else if(skillData.getInt("target") == 1){
			//可能这个玩家在道具飞行的过程中，消失了，下线或者去别的场景了
			if(toTarget.sceneData == null || toTarget.sceneData.sceneId != sceneData.sceneId || toTarget.isDead){
				clear();
				return;
			}
			if(canDispose){
				//计算伤害值
				
				//是否反射
				boolean isReflect = SkillData.selectTarget(skillData, master, toTarget,null,sceneData,this);
				if(!isReflect){
					canDispose = false;
					
					//广播法术发射
					int userListSize = horizonPlayer.size();
					TcpPacket pt = new TcpPacket(cyou.akworld.OpCodeEx.FLYTHING_CHANGE_TARGET_S);
					pt.putInt(getServerId());
					pt.putInt(toTarget.getServerId());
					
					for(int i = 0; i < userListSize; i++){
						WebServer webServer1 = horizonPlayer.get(i);
						if(webServer1.sceneData != null && webServer1.sceneData.sceneId == sceneData.sceneId){
							webServer1.getSession().send(pt);
						}
					}
					
					return;
				}
				clear();
				return;
			}
			
			double moveDistance;
			long nowTime = System.currentTimeMillis();
			moveDistance = (nowTime-time)/1000.0*skillData.getInt("flyspeed");
			if(moveDistance != 0){
				time = nowTime;
			}
			Point2D.Double point = IsoUtils.isoToScreen(new Point3D(position.x,0.0,position.z));
			Point3D positionClone = toTarget.position.clone();
			positionClone.y = 0.0;
			Point2D.Double point1 = IsoUtils.isoToScreen(positionClone);
			
			double distance = Point.distance(point.x,point.y,point1.x,point1.y);
			
			if(moveDistance >= distance){
				position = new Point3D(toTarget.position.x,_yValue,toTarget.position.z);
				canDispose = true;
			}else{
				
				Point2D.Double point2 = interpolate(point1,point,moveDistance);
				Point3D point3D = IsoUtils.screenToIso(point2);
				point3D.y = _yValue;
				position = point3D;
			}
		}
		
	}
	public void clear(){
		int userListSize = horizonPlayer.size();
		TcpPacket pt = new TcpPacket(cyou.akworld.OpCodeEx.FLYTHING_OUTGAME_S);
		pt.putInt(getServerId());
		
		for(int i = 0; i < userListSize; i++){
			WebServer webServer1 = horizonPlayer.get(i);
			if(webServer1.sceneData != null && webServer1.sceneData.sceneId == sceneData.sceneId){
				webServer1.getSession().send(pt);
			}
		}
		horizonPlayer.clear();
		horizonPlayer = null;
		toPosition = null;
		toPoint = null;
		skillData = null;
		toTarget = null;
		master = null;
		super.clear();
	}
}
