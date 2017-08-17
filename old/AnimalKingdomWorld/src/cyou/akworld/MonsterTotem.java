package cyou.akworld;

import java.util.ArrayList;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import _45degrees.com.friendsofed.isometric.Point3D;

public class MonsterTotem extends Monster {
	private static Logger log = LoggerFactory.getLogger(MonsterTotem.class);
	//技能
	public ArrayList<Skill> skillArray = new ArrayList<Skill>();
	//主人
	public ActivityThing master;
	
	public long startTime = 0;
	public void action(){

	}
	
	public void update(){
		//到时间了
		if(startTime+monster.getInt("lifeTime") < System.currentTimeMillis()){
			decHp(getHp());
			return;
		}
		super.update();
		//更新攻击时间
		long nowTime = System.currentTimeMillis();
		if(nowReadSkill != null){
			time = nowTime;
			return;
		}
		//使用技能
		for(int i = 0;i<skillArray.size();i++){
			Skill skill = skillArray.get(i);
			if(skill.userTime+skill.skillData.getInt("cd") < nowTime){
				boolean bool = useSkill(skill,nowTime);
				if(bool){
					break;
				}
			}
		}
		time = nowTime;
	}

	private boolean useSkill(Skill skill,long nowTime){
		ActivityThing toTarget = null;
		Point3D toPosition = null;
		if(skill.skillData.getInt("target") == 1 || skill.skillData.getInt("point") == 1){
			toTarget = selectTarget(skill.skillData);
			if(toTarget == null){
				return false;
			}
			toPosition = toTarget.position.clone();
			toPosition.y = 0.0;
		}
		double distance = sceneData.distanceMap.get(this.getServerId() < toTarget.getServerId() ? this.getServerId()+"_"+toTarget.getServerId() : toTarget.getServerId()+"_"+this.getServerId());
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
	//打最近的，优先选人，其次选怪
	private ActivityThing selectTarget(JSONObject skill){
		ActivityThing targetData = null;
		if(skill.getInt("isEnemy") == 1){
			
			for(int i = 0;i< horizonPlayer.size();i++){
				ActivityThing webServer = horizonPlayer.get(i);
				if(webServer == master){
					continue;
				}
				//取距离
				double distance = sceneData.distanceMap.get(this.getServerId() < webServer.getServerId() ? this.getServerId()+"_"+webServer.getServerId() : webServer.getServerId()+"_"+this.getServerId());
				if(distance > skill.getInt("attackDistance")){
					break;
				}
				//判断阵营
				if(
					(master.camp == null && webServer.camp == null) ||
					((master.camp != null && webServer.camp != null) && master.camp.getInt("id") == webServer.camp.getInt("id"))
				){
					
				}else{
					if(webServer.isDead || webServer.sceneData == null || webServer.sceneData.sceneId != sceneData.sceneId){
						
					}else{
						targetData = webServer;
						break;
					}
					
				}
			}
			if(targetData == null){
				for(int i = 0;i< horizonNpc.size();i++){
					ActivityThing webServer = horizonNpc.get(i);
					if(webServer == master){
						continue;
					}
					//取距离
					double distance = sceneData.distanceMap.get(this.getServerId() < webServer.getServerId() ? this.getServerId()+"_"+webServer.getServerId() : webServer.getServerId()+"_"+this.getServerId());
					if(distance > skill.getInt("attackDistance")){
						break;
					}
					//判断阵营
					if(
						(master.camp == null && webServer.camp == null) ||
						((master.camp != null && webServer.camp != null) && master.camp.getInt("id") == webServer.camp.getInt("id"))
					){
						
					}else{
						if(webServer.isDead || webServer.sceneData == null || webServer.sceneData.sceneId != sceneData.sceneId){
						
						}else{
							targetData = webServer;
							break;
						}
					}
				}
			}
		}else if(skill.getInt("isEnemy") == 0){
			
			for(int i = 0;i< horizonPlayer.size();i++){
				ActivityThing webServer = horizonPlayer.get(i);
				//取距离
				double distance = sceneData.distanceMap.get(this.getServerId() < webServer.getServerId() ? this.getServerId()+"_"+webServer.getServerId() : webServer.getServerId()+"_"+this.getServerId());
				if(distance > skill.getInt("attackDistance")){
					break;
				}
				//判断阵营
				if(
					(master.camp != null && webServer.camp == null)||
					(master.camp == null && webServer.camp != null) ||
					((master.camp != null && webServer.camp != null) && master.camp.getInt("id") != webServer.camp.getInt("id"))
				){
					
				}else{
					if(webServer.isDead || webServer.sceneData == null || webServer.sceneData.sceneId != sceneData.sceneId){
						
					}else{
						if(webServer.getHpRatio() == 100){
							continue;
						}
						targetData = webServer;
						break;
					}
					
				}
			}
			if(targetData == null){
				for(int i = 0;i< horizonNpc.size();i++){
					ActivityThing webServer = horizonNpc.get(i);
					//图腾不能被加血
					if(webServer.getClass() == MonsterTotem.class){
						continue;
					}
					//取距离
					double distance = sceneData.distanceMap.get(this.getServerId() < webServer.getServerId() ? this.getServerId()+"_"+webServer.getServerId() : webServer.getServerId()+"_"+this.getServerId());
					if(distance > skill.getInt("attackDistance")){
						break;
					}
					//判断阵营
					if(
						(master.camp != null && webServer.camp == null)||
						(master.camp == null && webServer.camp != null) ||
						((master.camp != null && webServer.camp != null) && master.camp.getInt("id") != webServer.camp.getInt("id"))
					){
						
					}else{
						if(webServer.isDead || webServer.sceneData == null || webServer.sceneData.sceneId != sceneData.sceneId){
						
						}else{
							if(webServer.getHpRatio() == 100){
								continue;
							}
							targetData = webServer;
							break;
						}
					}
				}
			}
		}
		return targetData;
	}
	public void clear(){
		
		super.clear();
	}
}
