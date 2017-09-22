package cyou.akworld;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import _45degrees.com.friendsofed.isometric.IsoUtils;
import _45degrees.com.friendsofed.isometric.Point3D;
import _astar.Node;
import cyou.mrd.io.tcp.TcpPacket;

public class MonsterNormal extends Monster {
	private static Logger log = LoggerFactory.getLogger(MonsterNormal.class);
	public Point3D initialPosition;
	private Node initialNode;
	private int startX;
	private int endX;
	private int startY;
	private int endY;
	
	private long nextActionTime = 0;
	//目标是0，就是没有
	public int target = 0;
	public boolean isBack = false;
	public ArrayList<Skill> skillArray = new ArrayList<Skill>();
	
	private boolean isArrive = false;
	public Node getInitialNode() {
		return initialNode;
	}
	public void setInitialNode(Node initialNode) {
		this.initialNode = initialNode;
		//初始化自己可以走的点
		startX = initialNode.x-monster.getInt("moveArea");
		endX = initialNode.x+monster.getInt("moveArea");
		startY = initialNode.y-monster.getInt("moveArea");
		endY = initialNode.y+monster.getInt("moveArea");
		getNextActionTime();
	}
	
	public void getNextActionTime(){
		nextActionTime = ((long)(Math.random()*6)+10)*1000+System.currentTimeMillis();
	}
	public void action(){
		if(isDead || isDizzy() || isIceBox() || isCast() || isCanNotMove()){
			return;
		}
		if(System.currentTimeMillis() < nextActionTime){
			return;
		}
		if(!isWalk && target == 0 && isBack == false){
			ArrayList<Node> arrayList = new ArrayList<Node>();
			int x = (int)Math.round(position.x/size);
			int y = (int)Math.round(position.z/size);
			Node node = AIWalk.getNextNode(x,y,startX,startY,endX,endY);
			
			arrayList.add(node);
			GameServerService.move1(this, arrayList);
			getNextActionTime();
		}
	}
	public void beAttack(ActivityThing attacker){
		if(isDead || target != 0){
			return;
		}
		target = attacker.getServerId();
		setPath(null);
		isBack = false;
		//用来追踪目标update
		time = System.currentTimeMillis();
		//只要怪物开始攻击，就重置技能cd
		for(int i = 0;i < skillArray.size();i++){
			skillArray.get(i).userTime = time;
		}
		
		//追踪包
		TcpPacket pt1 = new TcpPacket(cyou.akworld.OpCodeEx.MONSTER_FOLLOW_TARGET_S);
		pt1.putInt(getServerId());
		pt1.putDouble(position.x);
		pt1.putDouble(position.y);
		pt1.putDouble(position.z);
		pt1.put(dir);
		pt1.putInt(attacker.getServerId());
		
		//广播给怪物的能看到的人（当看到这个怪移动的人，看不见那个人，并且这个人后来又被这个人看见了，怪物就卡住了，得等怪物回到起始点的时候才能被这个人重新看见）
		//不能想着，我能看见这个只怪能看见的人，然后我看见的人就能看见能看见的人，这样罗勒冈
		int horizonThingSize = horizonPlayer.size();
		for(int m = 0;m < horizonThingSize;m++){
			WebServer webServer2 = horizonPlayer.get(m);
			//可能已经有一部分玩家已经不在这个怪的视野里，因为后面才重新计算视野的人和怪
			if(webServer2.sceneData != null && webServer2.sceneData.sceneId == sceneData.sceneId){
				webServer2.getSession().send(pt1);
			}
		}
	}
	public void update(){
		
		super.update();
		
		//更新攻击时间
		long nowTime = System.currentTimeMillis();
		//正在使用无敌斩技能也不能有其他的动作
		if(isDizzy() || isIceBox() || isCast() || nowReadSkill != null ||(isUserSkill && skillData.getInt("type") == SkillData.OMNISLASH)){
			//log.info("已经眩晕"+time);
			time = nowTime;
			return;
		}
		double moveDistance;
		
		if(target != 0){
			ActivityThing targetData = null;
			for(int i = 0;i< horizonPlayer.size();i++){
				ActivityThing webServer = horizonPlayer.get(i);
				if(webServer.getServerId() == target){
					targetData = webServer;
					break;
				}
			}
			if(targetData == null){
				for(int i = 0;i< horizonNpc.size();i++){
					ActivityThing webServer = horizonNpc.get(i);
					if(webServer.getServerId() == target){
						targetData = webServer;
						break;
					}
				}
			}
			//怪物回到出生点(怪物在攻击时，人可能已经传送出场景了，这是因为，怪物的攻击在区域广播之前，人走了，怪物还不知道，还有这个已经走的人物在自己的视野列表)
			//这个是可以的，因为怪物在上一次轮训已经可以打找这个玩家了，这个不用再重新刷新距离了
			if(targetData == null || targetData.isDead || targetData.sceneData == null || targetData.sceneData.sceneId != sceneData.sceneId){
				target = 0;
				isBack = true;
				//用于怪物回到出生点
				time = nowTime;
				
				//回到初始点的包
				TcpPacket pt1 = new TcpPacket(cyou.akworld.OpCodeEx.MONSTER_GO_BACK_S);
				pt1.putInt(getServerId());
				pt1.putDouble(position.x);
				pt1.putDouble(position.y);
				pt1.putDouble(position.z);
				pt1.put(dir);
				pt1.putInt(initialNode.x);
				pt1.putInt(initialNode.y);
				
				//广播给怪物的能看到的人
				int horizonThingSize = horizonPlayer.size();
				for(int m = 0;m < horizonThingSize;m++){
					WebServer webServer2 = horizonPlayer.get(m);
					//可能已经有一部分玩家已经不在这个怪的视野里，因为后面才重新计算视野的人和怪
					if(webServer2.sceneData != null && webServer2.sceneData.sceneId == sceneData.sceneId){
						webServer2.getSession().send(pt1);
					}
				}
				return;
			}else{
				
				
				//这个距离是上一帧的距离，这是对的，因为怪物在上一帧已经到达攻击距离了，这次要攻击了
				double distance = sceneData.distanceMap.get(this.getServerId() < targetData.getServerId() ? this.getServerId()+"_"+targetData.getServerId() : targetData.getServerId()+"_"+this.getServerId());
				
				if(distance <= monsterSkillData.getInt("attackDistance")){
					//就算攻击，也要重置时间，防止怪物跑得太快
					//time = nowTime;
					//calculateDir(position,targetData.position,false);
					//攻击
					attack(nowTime,targetData);
				}
				//使用技能
				for(int i = 0;i<skillArray.size();i++){
					Skill skill = skillArray.get(i);
					if(skill.userTime+skill.skillData.getInt("cd") < nowTime){
						boolean bool = useSkill(skill,targetData,nowTime,distance);
						if(bool){
							break;
						}
					}
				}	
				if(!isUserSkill){
					
				
					if(distance <= monsterSkillData.getInt("attackDistance")*0.8){
						calculateDir(position,targetData.position,false);
						isArrive = true;
					}else if(!isArrive || distance > monsterSkillData.getInt("attackDistance")){
						if(!isCanNotMove()){
							moveDistance = (nowTime-time)/1000.0*getNowMoveV();
							
							//如果总距离减去移动距离已经小于攻击距离了,移动距离变小
							if(distance - moveDistance < monsterSkillData.getInt("attackDistance")){
								//+1防止有偏差,四舍五入（这里不会有什么问题的，因为下一次攻击的时候位置还没有刷新，所以肯定攻击的上）
								moveDistance = (distance - moveDistance) < monsterSkillData.getInt("attackDistance")*0.8 ? distance-(monsterSkillData.getInt("attackDistance")*0.8)+1 : moveDistance;
							}
							Point3D positionClone = position.clone();
							positionClone.y = 0.0;
							Point3D positionClone1 = targetData.position.clone();
							positionClone1.y = 0.0;
							Point2D.Double point5 = IsoUtils.isoToScreen(positionClone);
							Point2D.Double point6 = IsoUtils.isoToScreen(positionClone1);
							
							Point2D.Double point7 = interpolate(point6,point5,moveDistance);
							Point3D point3D1 = IsoUtils.screenToIso(point7);
							calculateDir(position,point3D1,false);
							position = point3D1;
							isArrive = false;
						}else{
							calculateDir(position,targetData.position,false);
						}
						
					}
				}
				time = nowTime;
			}
		}
		if(isBack){
			
			if(!isCanNotMove() && !isUserSkill){
				
				moveDistance = (nowTime-time)/1000.0*getNowMoveV();
				
				//这里不用清除y为0，因为怪物回去的时候，不会跳跃
				Point2D.Double point5 = IsoUtils.isoToScreen(position);
				Point2D.Double point6 = IsoUtils.isoToScreen(initialPosition);
				double distance = Point.distance(point5.x,point5.y,point6.x,point6.y);
				if(moveDistance >= distance){
					calculateDir(position,initialPosition,false);
					position = initialPosition.clone();
					isBack = false;
					getNextActionTime();
				}else{
					Point2D.Double point7 = interpolate(point6,point5,moveDistance);
					Point3D point3D1 = IsoUtils.screenToIso(point7);
					calculateDir(position,point3D1,false);
					position = point3D1;
				}
			}else{
				if(!isUserSkill){
					calculateDir(position,initialPosition,false);
				}
			}
			time = nowTime;
		}
	}
	private boolean useSkill(Skill skill,ActivityThing target,long nowTime,double distance){
		Point3D toPosition = target.position.clone();
		toPosition.y = 0.0;
		ActivityThing toTarget =  target;
		if((skill.skillData.getInt("target") == 1 || skill.skillData.getInt("point") == 1)/* && skill.skillData.getInt("type") != SkillData.FLASH*/){
			if(distance > skill.skillData.getInt("attackDistance")){
				
				return false;
			}
		}
		if(skill.skillData.getInt("target") == 1){
			GameServerService.attack(this,skill.skillData,toTarget,1,null,dir,distance,false,0);
		}else if(skill.skillData.getInt("point") == 1){
			GameServerService.attack(this,skill.skillData,null,2,toPosition,dir,distance,false,0);
		}else{
			GameServerService.attack(this,skill.skillData,null,0,null,dir,distance,false,0);
		}
		skill.userTime = nowTime;
		return true;
	}
	private void attack(long time,ActivityThing target){
		upAttackTime = (upAttackTime+attackSpeed)<=time?time-attackSpeed:upAttackTime;
		//普通攻击冷却中
		if(upAttackTime != 0 && upAttackTime+attackSpeed>time){
			return;
		}
		upAttackTime = upAttackTime+attackSpeed;
		GameServerService.attack(this, monsterSkillData,target,1,null,dir,0,false,0);
	}
	public boolean refresh(){
		long time1 = System.currentTimeMillis();
		if((time1 - this.time) > monster.getInt("refresh")){
			dir = 5;
			//这个也没什么用
			this.time = time1;
			_path = null;
			nowNode = null;
			nowArrivePoint = null;
			isWalk = false;
			isDead = false;
			
			target = 0;
			isBack = false;
			isArrive = false;
			setHp(getMaxHp());
			//防止id重了，-10000肯定没有1万只怪
			if(getServerId() < -10000){
				setServerId(getServerId()+10000);
			}else{
				setServerId(getServerId()-10000);
			}
			position = initialPosition;
			getNextActionTime();
			return true;
		}
		return false;
	}
	public void clear(){
		monster = null;
		initialPosition = null;
		initialNode = null;
		skillArray.clear();
		skillArray = null;
		super.clear();
	}
}
