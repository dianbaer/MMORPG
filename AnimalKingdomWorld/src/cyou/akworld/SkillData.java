package cyou.akworld;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import cyou.mrd.io.tcp.TcpPacket;

import net.sf.json.JSONObject;
import tcp.SceneData;
import _45degrees.com.friendsofed.isometric.IsoUtils;
import _45degrees.com.friendsofed.isometric.Point3D;

public class SkillData {
	//伤害技能
	public static int ATTACK = 1;
	//buff技能
	public static int BUFF = 2;
	//闪现
	public static int FLASH = 3;
	//大跳
	public static int BIG_JUMP = 4;
	//治疗技能
	public static int TREAT = 5;
	//冲锋
	public static int RUSH = 6;
	//召唤图腾
	public static int TOTEM = 7;
	//被技能影响(击飞效果)
	public static int CLICK_THE_FLY = 8;
	//无敌斩
	public static int OMNISLASH = 9;
	public static boolean selectTarget(JSONObject skillData,ActivityThing attacker,ActivityThing beAttacker,Point3D toPosition,SceneData sceneData,FlySkillEffect flySkillEffect){
		if(beAttacker != null){
			//被攻击者可能不已经不在这个场景里了
			if(beAttacker.sceneData == null || beAttacker.sceneData.sceneId != sceneData.sceneId){
				return true;
			}
			//如果是范围技能
			if(skillData.getInt("scope") == 1){
				int scopeValue = skillData.getInt("scopeValue");
				ArrayList<WebServer> horizonThing =  beAttacker.horizonPlayer;
				int size = horizonThing.size();
				ArrayList<ActivityThing> beAttackList = new ArrayList<ActivityThing>();
				
				//先判断下被攻击者
				if(beAttacker.isDead){
					
				}else if(skillData.getInt("isEnemy") == 1){
					if((attacker.camp == null && beAttacker.camp == null) ||
							((attacker.camp != null && beAttacker.camp != null) && attacker.camp.getInt("id") == beAttacker.camp.getInt("id"))){
						
					}else{
						//如果有法术发射buff，则设置发射，并且这次攻击取消
						if(beAttacker.isHaveMagicrReflect()){
							//清理法术发射buff，防止两个人无限反，服务器down
							beAttacker.clearDebuffByType(BuffInfo.MAGIC_REFLECT);
							//看攻击者是否在这个场景，是否死亡
							if(attacker.sceneData == null || attacker.sceneData.sceneId != sceneData.sceneId || attacker.isDead){
								return true;
							}
							if(flySkillEffect != null){
								flySkillEffect.master = beAttacker;
								flySkillEffect.toTarget = attacker;
								return false;
							}else{
								selectTarget(skillData,beAttacker,attacker,null,sceneData,null);
								return true;
							}
						}
						beAttackList.add(beAttacker);
					}
				}else if(skillData.getInt("isEnemy") == 0){
					if((attacker.camp != null && beAttacker.camp == null)||
							(attacker.camp == null && beAttacker.camp != null) ||
							((attacker.camp != null && beAttacker.camp != null) && attacker.camp.getInt("id") != beAttacker.camp.getInt("id"))){
						
					}else{
						beAttackList.add(beAttacker);
					}
				}
				
				for(int i = 0;i < size;i++){
					ActivityThing webServer = horizonThing.get(i);
					if(webServer.sceneData == null || webServer.sceneData.sceneId != sceneData.sceneId){
						continue;
					}
					//如果是自己，下一轮
					if(webServer.isDead){
						continue;
					}
					if(skillData.getInt("isEnemy") == 1){
						if(webServer.getServerId() == attacker.getServerId()){
							continue;
						}
						if((attacker.camp == null && webServer.camp == null) ||
								((attacker.camp != null && webServer.camp != null) && attacker.camp.getInt("id") == webServer.camp.getInt("id"))){
							continue;
						}
					}else{
						if((attacker.camp != null && webServer.camp == null)||
								(attacker.camp == null && webServer.camp != null) ||
								((attacker.camp != null && webServer.camp != null) && attacker.camp.getInt("id") != webServer.camp.getInt("id"))){
							continue;
						}
					}
					
					double distance = sceneData.distanceMap.get(beAttacker.getServerId() < webServer.getServerId() ? beAttacker.getServerId()+"_"+webServer.getServerId() :  webServer.getServerId()+"_"+beAttacker.getServerId());
					if(distance > scopeValue){
						break;
					}
					beAttackList.add(webServer);
				}
				
				ArrayList<Monster> horizonThing1 =  beAttacker.horizonNpc;
				int size1 = horizonThing1.size();
				
				
				for(int i = 0;i < size1;i++){
					ActivityThing webServer = horizonThing1.get(i);
					//如果是自己，下一轮
					if(webServer.isDead){
						continue;
					}
					if(skillData.getInt("isEnemy") == 1){
						if(webServer.getServerId() == attacker.getServerId()){
							continue;
						}
						if((attacker.camp == null && webServer.camp == null) ||
								((attacker.camp != null && webServer.camp != null) && attacker.camp.getInt("id") == webServer.camp.getInt("id"))){
							continue;
						}
					}else{
						if((attacker.camp != null && webServer.camp == null)||
								(attacker.camp == null && webServer.camp != null) ||
								((attacker.camp != null && webServer.camp != null) && attacker.camp.getInt("id") != webServer.camp.getInt("id"))){
							continue;
						}
					}
					
					double distance = sceneData.distanceMap.get(beAttacker.getServerId() < webServer.getServerId() ? beAttacker.getServerId()+"_"+webServer.getServerId() :  webServer.getServerId()+"_"+beAttacker.getServerId());
					if(distance > scopeValue){
						break;
					}
					beAttackList.add(webServer);
				}
				
				
				
				
				int beAttackListSize = beAttackList.size();
				for(int j = 0;j < beAttackListSize;j++){
					ActivityThing webServer1 = beAttackList.get(j);
					calculateDamage(skillData,attacker,webServer1,sceneData);
				}
			}else{
				//先判断下被攻击者
				if(beAttacker.isDead){
					
				}else if(skillData.getInt("isEnemy") == 1){
					if((attacker.camp == null && beAttacker.camp == null) ||
							((attacker.camp != null && beAttacker.camp != null) && attacker.camp.getInt("id") == beAttacker.camp.getInt("id"))){
						
					}else{
						//如果有法术发射buff，则设置发射，并且这次攻击取消
						if(beAttacker.isHaveMagicrReflect()){
							//清理法术发射buff，防止两个人无限反，服务器down
							beAttacker.clearDebuffByType(BuffInfo.MAGIC_REFLECT);
							//看攻击者是否在这个场景，是否死亡
							if(attacker.sceneData == null || attacker.sceneData.sceneId != sceneData.sceneId || attacker.isDead){
								return true;
							}
							if(flySkillEffect != null){
								flySkillEffect.master = beAttacker;
								flySkillEffect.toTarget = attacker;
								return false;
							}else{
								selectTarget(skillData,beAttacker,attacker,null,sceneData,null);
								return true;
							}
						}
						calculateDamage(skillData,attacker,beAttacker,sceneData);
					}
				}else if(skillData.getInt("isEnemy") == 0){
					if((attacker.camp != null && beAttacker.camp == null)||
							(attacker.camp == null && beAttacker.camp != null) ||
							((attacker.camp != null && beAttacker.camp != null) && attacker.camp.getInt("id") != beAttacker.camp.getInt("id"))){
						
					}else{
						calculateDamage(skillData,attacker,beAttacker,sceneData);
					}
				}
				
			}
			//做闪电链的功能
			if(flySkillEffect != null){
				//判断次数
				if(skillData.getInt("flyTime") > 0 && flySkillEffect.flyTime < skillData.getInt("flyTime")){
					flySkillEffect.flyTime++;
					//选目标攻击者，被攻击者
					ArrayList<WebServer> horizonThing =  sceneData.userList;
					int size = horizonThing.size();
					boolean isChangeTarget = false;
					for(int i = 0;i < size;i++){
						ActivityThing webServer = horizonThing.get(i);
						//不能是死的
						if(webServer.isDead){
							continue;
						}
						if(skillData.getInt("isEnemy") == 1){
							//排除自己
							if(webServer.getServerId() == attacker.getServerId()){
								continue;
							}
							//排除刚攻击的人
							if(webServer.getServerId() == beAttacker.getServerId()){
								continue;
							}
							if((attacker.camp == null && webServer.camp == null) ||
									((attacker.camp != null && webServer.camp != null) && attacker.camp.getInt("id") == webServer.camp.getInt("id"))){
								continue;
							}
						}else{
							//排除刚攻击的人
							if(webServer.getServerId() == beAttacker.getServerId()){
								continue;
							}
							if((attacker.camp != null && webServer.camp == null)||
									(attacker.camp == null && webServer.camp != null) ||
									((attacker.camp != null && webServer.camp != null) && attacker.camp.getInt("id") != webServer.camp.getInt("id"))){
								continue;
							}
						}
						Point3D positionClone = webServer.position.clone();
						positionClone.y = 0.0;
						Point2D.Double point = IsoUtils.isoToScreen(positionClone);
						Point2D.Double point1 = IsoUtils.isoToScreen(flySkillEffect.position);
						double distance = Point.distance(point.x,point.y,point1.x,point1.y);
						if(distance > skillData.getInt("attackDistance")/3){
							continue;
						}
						//攻击他转变方向
						isChangeTarget = true;
						flySkillEffect.toTarget = webServer;
						break;
					}
					
					if(!isChangeTarget){
						ArrayList<Monster> horizonThing1 =  sceneData.npcList;
						int size1 = horizonThing1.size();
						
						for(int i = 0;i < size1;i++){
							ActivityThing webServer = horizonThing1.get(i);
							//不能是死的
							if(webServer.isDead){
								continue;
							}
							if(skillData.getInt("isEnemy") == 1){
								if(webServer.getServerId() == attacker.getServerId()){
									continue;
								}
								//排除刚攻击的人
								if(webServer.getServerId() == beAttacker.getServerId()){
									continue;
								}
								if((attacker.camp == null && webServer.camp == null) ||
										((attacker.camp != null && webServer.camp != null) && attacker.camp.getInt("id") == webServer.camp.getInt("id"))){
									continue;
								}
							}else{
								//排除刚攻击的人
								if(webServer.getServerId() == beAttacker.getServerId()){
									continue;
								}
								if((attacker.camp != null && webServer.camp == null)||
										(attacker.camp == null && webServer.camp != null) ||
										((attacker.camp != null && webServer.camp != null) && attacker.camp.getInt("id") != webServer.camp.getInt("id"))){
									continue;
								}
							}
							Point3D positionClone = webServer.position.clone();
							positionClone.y = 0.0;
							Point2D.Double point = IsoUtils.isoToScreen(positionClone);
							Point2D.Double point1 = IsoUtils.isoToScreen(flySkillEffect.position);
							double distance = Point.distance(point.x,point.y,point1.x,point1.y);
							if(distance > skillData.getInt("attackDistance")/3){
								continue;
							}
							isChangeTarget = true;
							flySkillEffect.toTarget = webServer;
							break;
						}
					}
					if(isChangeTarget){
						return false;
					}else{
						return true;
					}
				}else{
					return true;
				}
			}
		//这种技能最好cd长一点，因为距离都是需要计算的，消耗效率，具体消耗多少以后再测
		}else if(toPosition != null){
			if(skillData.getInt("scope") == 1){
				int scopeValue = skillData.getInt("scopeValue");
				ArrayList<WebServer> horizonThing =  sceneData.userList;
				int size = horizonThing.size();
				ArrayList<ActivityThing> beAttackList = new ArrayList<ActivityThing>();
				for(int i = 0;i < size;i++){
					ActivityThing webServer = horizonThing.get(i);
					//如果是自己，下一轮
					if(webServer.isDead){
						continue;
					}
					if(skillData.getInt("isEnemy") == 1){
						if(webServer.getServerId() == attacker.getServerId()){
							continue;
						}
						if((attacker.camp == null && webServer.camp == null) ||
								((attacker.camp != null && webServer.camp != null) && attacker.camp.getInt("id") == webServer.camp.getInt("id"))){
							continue;
						}
					}else{
						if((attacker.camp != null && webServer.camp == null)||
								(attacker.camp == null && webServer.camp != null) ||
								((attacker.camp != null && webServer.camp != null) && attacker.camp.getInt("id") != webServer.camp.getInt("id"))){
							continue;
						}
					}
					Point3D positionClone = webServer.position.clone();
					positionClone.y = 0.0;
					Point2D.Double point = IsoUtils.isoToScreen(positionClone);
					Point2D.Double point1 = IsoUtils.isoToScreen(toPosition);
					double distance = Point.distance(point.x,point.y,point1.x,point1.y);
					if(distance > scopeValue){
						continue;
					}
					beAttackList.add(webServer);
				}
				
				ArrayList<Monster> horizonThing1 =  sceneData.npcList;
				int size1 = horizonThing1.size();
				
				for(int i = 0;i < size1;i++){
					ActivityThing webServer = horizonThing1.get(i);
					//如果是自己，下一轮
					if(webServer.isDead){
						continue;
					}
					if(skillData.getInt("isEnemy") == 1){
						if(webServer.getServerId() == attacker.getServerId()){
							continue;
						}
						if((attacker.camp == null && webServer.camp == null) ||
								((attacker.camp != null && webServer.camp != null) && attacker.camp.getInt("id") == webServer.camp.getInt("id"))){
							continue;
						}
					}else{
						if((attacker.camp != null && webServer.camp == null)||
								(attacker.camp == null && webServer.camp != null) ||
								((attacker.camp != null && webServer.camp != null) && attacker.camp.getInt("id") != webServer.camp.getInt("id"))){
							continue;
						}
					}
					Point3D positionClone = webServer.position.clone();
					positionClone.y = 0.0;
					Point2D.Double point = IsoUtils.isoToScreen(positionClone);
					Point2D.Double point1 = IsoUtils.isoToScreen(toPosition);
					double distance = Point.distance(point.x,point.y,point1.x,point1.y);
					if(distance > scopeValue){
						continue;
					}
					beAttackList.add(webServer);
				}
				
				
				
				
				int beAttackListSize = beAttackList.size();
				for(int j = 0;j < beAttackListSize;j++){
					ActivityThing webServer1 = beAttackList.get(j);
					calculateDamage(skillData,attacker,webServer1,sceneData);
				}
			}else{
				//不可能出现
			}
		}else{
			if(skillData.getInt("scope") == 1){
				int scopeValue = skillData.getInt("scopeValue");
				ArrayList<WebServer> horizonThing = attacker.horizonPlayer;
				int size = horizonThing.size();
				ArrayList<ActivityThing> beAttackList = new ArrayList<ActivityThing>();
				
				//如果是友方技能则影响攻击者
				if(skillData.getInt("isEnemy") == 0){
					beAttackList.add(attacker);
				}
				for(int i = 0;i < size;i++){
					ActivityThing webServer = horizonThing.get(i);
					//可能玩家视野里的这些人物，有一些已经去别的场景，并且没有及时的切换
					if(webServer.sceneData == null || webServer.sceneData.sceneId != sceneData.sceneId){
						continue;
					}
					//如果是自己，下一轮
					if(webServer.isDead){
						continue;
					}
					if(skillData.getInt("isEnemy") == 1){
						/*视野里不可能有自己的
						if(webServer.getServerId() == attacker.getServerId()){
							continue;
						}*/
						if((attacker.camp == null && webServer.camp == null) ||
								((attacker.camp != null && webServer.camp != null) && attacker.camp.getInt("id") == webServer.camp.getInt("id"))){
							continue;
						}
					}else{
						if((attacker.camp != null && webServer.camp == null)||
								(attacker.camp == null && webServer.camp != null) ||
								((attacker.camp != null && webServer.camp != null) && attacker.camp.getInt("id") != webServer.camp.getInt("id"))){
							continue;
						}
					}
					double distance = sceneData.distanceMap.get(attacker.getServerId() < webServer.getServerId() ? attacker.getServerId()+"_"+webServer.getServerId() :  webServer.getServerId()+"_"+attacker.getServerId());
					if(distance > scopeValue){
						break;
					}
					beAttackList.add(webServer);
				}
				
				ArrayList<Monster> horizonThing1 =  attacker.horizonNpc;
				int size1 = horizonThing1.size();
				
				
				for(int i = 0;i < size1;i++){
					ActivityThing webServer = horizonThing1.get(i);
					//如果是自己，下一轮
					if(webServer.isDead){
						continue;
					}
					if(skillData.getInt("isEnemy") == 1){
						/*视野里不可能有自己的
						if(webServer.getServerId() == attacker.getServerId()){
							continue;
						}*/
						if((attacker.camp == null && webServer.camp == null) ||
								((attacker.camp != null && webServer.camp != null) && attacker.camp.getInt("id") == webServer.camp.getInt("id"))){
							continue;
						}
					}else{
						if((attacker.camp != null && webServer.camp == null)||
								(attacker.camp == null && webServer.camp != null) ||
								((attacker.camp != null && webServer.camp != null) && attacker.camp.getInt("id") != webServer.camp.getInt("id"))){
							continue;
						}
					}
					double distance = sceneData.distanceMap.get(attacker.getServerId() < webServer.getServerId() ? attacker.getServerId()+"_"+webServer.getServerId() :  webServer.getServerId()+"_"+attacker.getServerId());
					if(distance > scopeValue){
						break;
					}
					beAttackList.add(webServer);
				}
				
				
				
				int beAttackListSize = beAttackList.size();
				for(int j = 0;j < beAttackListSize;j++){
					ActivityThing webServer1 = beAttackList.get(j);
					calculateDamage(skillData,attacker,webServer1,sceneData);
				}
			}else{
				//先判断下被攻击者
				if(attacker.isDead){
					
				}else if(skillData.getInt("isEnemy") == 1){
					
				}else if(skillData.getInt("isEnemy") == 0){
					//单体的给自己释放
					calculateDamage(skillData,attacker,attacker,sceneData);
					
				}
				
				
			}
		}
		return true;
	}
	public static void calculateDamage(JSONObject skillData,ActivityThing attacker,ActivityThing beAttacker,SceneData sceneData){
		//如果被攻击者死了，直接返回
		if(beAttacker.isDead){
			return;
		}
		//图腾是无敌的
		if(beAttacker.getClass() == MonsterTotem.class){
			return;
		}
		//如果被攻击者是无敌状态，并且这个技能是攻击性技能，直接返回
		//无敌斩也是无敌的
		if((beAttacker.isIceBox() || (beAttacker.isUserSkill && beAttacker.skillData.getInt("type") == SkillData.OMNISLASH)) && skillData.getInt("isEnemy") == 1){
			return;
		}
		int skillType = skillData.getInt("type");
		int horizonThingSize = beAttacker.horizonPlayer.size();
		//如果是1类型的伤害技能，掉血，
		int damageValue = 0;
		if(skillType == ATTACK || skillType == OMNISLASH){
			//暂时只有怪掉血，比较好测试
			//if(beAttacker.getClass() == Monster.class){
				if(Math.floor(skillData.getInt("id")/10000) == 1){
					damageValue = beAttacker.decHp(skillData.getInt("damage"));
				}else{
					damageValue = beAttacker.decHp(skillData.getInt("damage"));
				}
				
			//}
			
		}else if(skillType == TREAT){
			damageValue = beAttacker.addHp(skillData.getInt("damage"));
		}
		TcpPacket pt1 = null;
		if(skillType == ATTACK || skillType == OMNISLASH || skillType == TREAT){
			//伤害包
			pt1 = new TcpPacket(cyou.akworld.OpCodeEx.SKILL_DAMAGE_S);
			//类型，1是技能掉血
			if(skillType == ATTACK || skillType == OMNISLASH){
				pt1.putInt(1);
			}else{
				//3是技能加血
				pt1.putInt(3);
			}
			
			pt1.putInt(skillData.getInt("id"));
			pt1.putInt(beAttacker.getServerId());
			if(skillType == ATTACK || skillType == OMNISLASH){
				if(Math.floor(skillData.getInt("id")/10000) == 1){
					pt1.putInt(damageValue);
				}else{
					pt1.putInt(damageValue);
				}
			}else{
				pt1.putInt(damageValue);
			}
			
			
		}
		
		//这里已经死了，不用做后续操作了
		if(beAttacker.isDead){
			//如果已经死了，就不用处理buff了，直接广播掉血
			for(int m = 0;m < horizonThingSize;m++){
				WebServer webServer2 = beAttacker.horizonPlayer.get(m);
				if(webServer2.sceneData != null && webServer2.sceneData.sceneId == sceneData.sceneId){
					if(pt1 != null){
						webServer2.getSession().send(pt1);
					}
				}
			}
			if(beAttacker.getClass() == WebServer.class){
				if(pt1 != null){
					((WebServer)beAttacker).getSession().send(pt1);
				}
			}
			return;
		}
		//可能施放攻击的这个人已经离开场景或者他去了别的场景了（远程飞行技能通常会这样）
		if(attacker.sceneData == null || attacker.sceneData.sceneId != beAttacker.sceneData.sceneId){
			
		}else{
			if(skillType == TREAT || (skillType == BUFF && skillData.getInt("isEnemy") == 0)){
				
			}else{
				beAttacker.beAttack(attacker);
			}
		}
		
		
		
		//如果这个技能附带buff
		BuffInfo haveBuff = null;
		int way = 0;
		if(skillData.containsKey("buffId") && skillData.getInt("buffId") > 0){
			JSONObject buff = GameServerService.buffData.getJSONObject(skillData.getString("buffId"));
			if(buff != null){
				int type = buff.getInt("type");
				BuffArray buffList = beAttacker.buffList.get(type);
				
				if(buffList != null){
					for(int i = 0;i<buffList.buffArray.size();i++){
						BuffInfo buffInfo = buffList.buffArray.get(i);
						if(buffInfo.serverId == attacker.getServerId() && buffInfo.buffData.getInt("id") == buff.getInt("id")){
							haveBuff = buffInfo;
						}
					}
				}else{
					buffList = new BuffArray();
					beAttacker.buffList.put(type, buffList);
				}
				
				if(haveBuff != null){
					haveBuff.startTime = System.currentTimeMillis();
					
					way = 1;
				}else{
					haveBuff = new BuffInfo();
					haveBuff.serverId = attacker.getServerId();
					haveBuff.buffData = buff;
					haveBuff.startTime = System.currentTimeMillis();
					haveBuff.nextInterval = haveBuff.startTime+haveBuff.buffData.getInt("interval");
					buffList.buffArray.add(haveBuff);
					if(type == BuffInfo.SPEED_ADD || type == BuffInfo.SPEED_DEL){
						sortBuff(buffList);
						buffList.addPer = buffList.buffArray.get(0).buffData.getInt("value");
					}
					//如果是加伤，减伤技能计算百分比
					if(type == BuffInfo.ADD_OR_SUBTRACT_DAMAGE){
						calculateBuff(buffList);
					}
					way = 0;
					//如果是眩晕，清空这个被攻击者的走路
					if(type == BuffInfo.DIZZY_EFFECT || type == BuffInfo.ICE_BOX || type == BuffInfo.CAST){
						beAttacker.setPath(null);
						beAttacker.cannelReadSkill(true);
						//如果是冰箱则接触所有debuff
						if(type == BuffInfo.ICE_BOX){
							beAttacker.clearAllDebuff();
						}
					}
					//定身，清除寻路
					if(type == BuffInfo.CANNOT_MOVE){
						beAttacker.setPath(null);
					}
				}
			}
		}
		
		
		
		//buff包
		TcpPacket pt2 = null;
		if(haveBuff != null){
			if(way == 0){
				pt2 = new TcpPacket(cyou.akworld.OpCodeEx.ADD_BUFF_S);
			}else{
				pt2 = new TcpPacket(cyou.akworld.OpCodeEx.REFRESH_BUFF_S);
			}
			pt2.putInt(beAttacker.getServerId());
			pt2.putInt(1);
			pt2.putInt(haveBuff.buffData.getInt("id"));
			//谁给的buff
			pt2.putInt(haveBuff.serverId);
			//剩余时间，刚给的buff没问题
			pt2.putInt(haveBuff.buffData.getInt("duration"));
		}
		TcpPacket pt3 = null;
		if(skillType == CLICK_THE_FLY){
			//这个人被技能影响着
			Point3D positionClone = attacker.position.clone();
			positionClone.y = 0.0;
			Point2D.Double point = IsoUtils.isoToScreen(positionClone);
			Point3D positionClone1 = beAttacker.position.clone();
			positionClone1.y = 0.0;
			Point2D.Double point1 = IsoUtils.isoToScreen(positionClone1);
			Point2D.Double point2 = Thing.interpolateWithArrvie(point1, point, skillData.getInt("affectDistance"));
			Point3D toPosition = IsoUtils.screenToIso(point2);
			beAttacker.userSkill(skillData,toPosition,skillData.getInt("affectDistance"),null);
			//被技能影响包，谁被影响，技能是哪个，影响到的目的地
			pt3 = new TcpPacket(cyou.akworld.OpCodeEx.AFFECT_BY_SKILL_S);
			pt3.putInt(beAttacker.getServerId());
			pt3.putInt(skillData.getInt("id"));
			pt3.putDouble(toPosition.x);
			pt3.putDouble(toPosition.y);
			pt3.putDouble(toPosition.z);
		}
		for(int m = 0;m < horizonThingSize;m++){
			WebServer webServer2 = beAttacker.horizonPlayer.get(m);
			if(webServer2.sceneData != null && webServer2.sceneData.sceneId == sceneData.sceneId){
				if(pt1 != null){
					webServer2.getSession().send(pt1);
				}
				
				if(pt2 != null){
					
					webServer2.getSession().send(pt2);
				}
				if(pt3 != null){
					
					webServer2.getSession().send(pt3);
				}
			}
		}
		if(beAttacker.getClass() == WebServer.class){
			
			if(pt1 != null){
				((WebServer)beAttacker).getSession().send(pt1);
			}
			if(pt2 != null){
				
				((WebServer)beAttacker).getSession().send(pt2);
			}
			if(pt3 != null){
				
				((WebServer)beAttacker).getSession().send(pt3);
			}
		}
		
	}
	public static void sortBuff(BuffArray buffList){
		int size = buffList.buffArray.size();
		//只取出一个最大的就行了
		for(int i = 0;i < 1;i++){
			for(int j = i+1;j<size;j++){
				BuffInfo buffInfo = buffList.buffArray.get(i);
				BuffInfo buffInfo1 = buffList.buffArray.get(j);
				//交换位置
				if(Math.abs(buffInfo1.buffData.getInt("value")) > Math.abs(buffInfo.buffData.getInt("value"))){
					buffList.buffArray.set(i, buffInfo1);
					buffList.buffArray.set(j, buffInfo);
				}
				
			}
		}
	}
	//计算最后的百分比
	public static void calculateBuff(BuffArray buffList){
		int size = buffList.buffArray.size();
		int value = 0;
		for(int i = 0;i < size;i++){
			BuffInfo buffInfo = buffList.buffArray.get(i);
			value = value+buffInfo.buffData.getInt("value");
		}
		
		if(value < -100){
			value = -100;
		}
		buffList.addPer = value;
	}

}
