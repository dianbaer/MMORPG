package cyou.akworld;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import _45degrees.com.friendsofed.isometric.AnConst;
import _45degrees.com.friendsofed.isometric.IsoUtils;
import _45degrees.com.friendsofed.isometric.Point3D;
import _astar.Node;
import cyou.mrd.io.tcp.TcpPacket;

public class ActivityThing extends Thing {
	private static Logger log = LoggerFactory.getLogger(ActivityThing.class);
	public JSONObject monster;
	public JSONObject monsterSkillData;
	private int hp;
	private int maxHp;
	private int att;
	private int def;
	//移动速度
	public double moveV;
	//攻击速度(int型就可以了)
	public int attackSpeed;
	//跳跃速度
	public double jumpVerticalV;
	
	//重力加速度
	public double jumpVerticalA;
	public int dir;
	/**************寻路*********************/
	public ArrayList<Node> _path;
	public Node nowNode;
	public Point2D.Double nowArrivePoint;
	public boolean isWalk;
	/**************寻路*********************/
	
	/****************走路**********************/
	private boolean isLine = true;
	private double walkAllDis = 0;
	private Point3D walkGoal = null;
	private Point3D walkStart = null;
	private double walkMoveD = 0;
	/****************走路**********************/
	
	public long upAttackTime = 0;
	
	
	public boolean isDead = false;
	//死亡的事情已经广播了
	public boolean deadIsBoradCast = false;
	/**
	 * 视野里的事物
	 */
	public ArrayList<WebServer> horizonPlayer = new ArrayList<WebServer>();
	public ArrayList<Monster> horizonNpc = new ArrayList<Monster>();
	//buff哈希
	public HashMap<Integer,BuffArray> buffList = new HashMap<Integer, BuffArray>();
	
	
	/*******************大跳***************/
	//正在释放技能
	public boolean isUserSkill = false;
	//正在施放的技能
	public JSONObject skillData;
	//目标点
	public Point3D goal;
	private double skillJumpH = 0;
	//如果是大跳，记录大跳的速度，如果是冲锋，记录冲锋的总距离
	private double skillJumpV = 0;
	private Point3D start;
	private double moveD = 0;
	//无敌斩的目标
	public ActivityThing toTarget;
	public int flyTime = 1;
	
	//2015-2-23xp施放技能时，保存的寻路，施放完成后，在广播出去，防止先广播寻路，在广播技能完成，被拉到不对的位置上
	public ArrayList<Node> savePath;
	public int saveDir;
	public Point3D savePoint;
	/*******************大跳***************/
	/*************读条技能***********************/
	//读条技能的一些属性
	public JSONObject nowReadSkill;
	public long nowReadTime;
	public ActivityThing readTarget;
	public int readType;
	public Point3D readToPosition;
	public int readDir;
	public double readDistance;
	/*************读条技能***********************/
	//阵营
	public JSONObject camp = null;
	
	public int getHp() {
		return hp;
	}
	public void setHp(int hp) {
		this.hp = hp;
	}
	public int decHp(int value){
	
		//计算加伤害减伤害
		if(buffList.get(BuffInfo.ADD_OR_SUBTRACT_DAMAGE) != null){
			value = (int)((1+buffList.get(BuffInfo.ADD_OR_SUBTRACT_DAMAGE).addPer/100.0)*value);
		}
		this.hp -= value;
		if(this.hp < 0){
			this.hp = 0;
		}
		//只有在没死的时候，才清理变羊的buff，如果死了，客户端会自行清理
		if(this.hp > 0){
			clearDebuffByType(BuffInfo.CAST);
		}
		if(this.hp == 0 && !isDead){
			isDead = true;
			//如果死了，直接把他放到施放技能成功的位置，因为死了，所以不会再跟其他人有交互了
			
			clearUseSkill(true);
			cannelReadSkill(false);
			setPath(null);
			buffList.clear();
			//用来刷新复活refresh()
			this.time = System.currentTimeMillis();
		}
		return value;
	}
	
	public void outScene(){
		
		clearUseSkill(true);
		cannelReadSkill(false);
		setPath(null);
		horizonPlayer.clear();
		horizonNpc.clear();
	}
	public void cannelReadSkill(boolean isSend){
		if(nowReadSkill != null){
			nowReadSkill = null;
			nowReadTime = 0;
			readTarget = null;
			readType = 0;
			readToPosition = null;
			readDir = 0;
			readDistance = 0;
			//重置时间（施法的时候不能移动，施法结束后，时间积累的很多，会直接走到玩家的身边）（已废弃，这里重置在原则上不对）
			//time = System.currentTimeMillis();
			if(isSend){
				if(this.getClass() == WebServer.class){
					TcpPacket pt2 = new TcpPacket(cyou.akworld.OpCodeEx.READ_SKILL_COMPLETE_S);
					((WebServer)this).getSession().send(pt2);
				}
			}
		}
		
	}
	public void readSkill(JSONObject nowReadSkill,ActivityThing target,int type,Point3D toPosition,int dir,double distance){
		this.nowReadSkill = nowReadSkill;
		this.nowReadTime = System.currentTimeMillis();
		readTarget = target;
		readType = type;
		readToPosition = toPosition;
		readDir = dir;
		readDistance = distance;
		setPath(null);
	}
	public int addHp(int value){
		this.hp += value;
		if(this.hp > this.maxHp){
			this.hp = maxHp;
		}
		return value;
	}
	//得到血的比例
	public int getHpRatio(){
		return (int)((this.hp/maxHp)*100);
	}
	public int getMaxHp() {
		return maxHp;
	}
	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}
	public int getAtt() {
		return att;
	}
	public void setAtt(int att) {
		this.att = att;
	}
	public int getDef() {
		return def;
	}
	public void setDef(int def) {
		this.def = def;
	}
	public void setPath(ArrayList<Node> arr)
	{
		_path = arr;
		nowNode = null;
		nowArrivePoint = null;
		if(_path != null && _path.size() > 0){
			isWalk = true;
			
			//设置直线参数
			isLine = true;
			walkGoal = new Point3D(_path.get(_path.size()-1).x*size+0.0,0.0,_path.get(_path.size()-1).y*size+0.0);
			walkStart = this.position.clone();
			Point2D.Double point = IsoUtils.isoToScreen(walkStart);
			Point2D.Double point1 = IsoUtils.isoToScreen(walkGoal);
			walkAllDis = Point.distance(point.x,point.y,point1.x,point1.y);
			walkMoveD = 0;
			calculateDir(walkStart,walkGoal,false);
			
			cannelReadSkill(true);
		}else{
			isWalk = false;
			//设置直线参数
			isLine = true;
			walkGoal = null;
			walkStart = null;
			walkAllDis = 0;
			walkMoveD = 0;
		}
	}
	public void calculateDir(Point3D position,Point3D position1,boolean walkAffect){
		if(isWalk && walkAffect){
			return;
		}
		Point3D positionClone = position.clone();
		positionClone.y = 0.0;
		Point3D positionClone1 = position1.clone();
		positionClone1.y = 0.0;
		Point2D.Double point = IsoUtils.isoToScreen(positionClone);
		Point2D.Double point1 = IsoUtils.isoToScreen(positionClone1);
		double radian = Math.atan2(point1.y-point.y,point1.x-point.x);
		double rotation = radian/Math.PI*180.0;
		if(rotation >= 157.5 || rotation < -157.5){
			dir = AnConst.LEFT;
		}else if(rotation < 157.5 && rotation >=112.5){
			dir = AnConst.LEFT_DOWN;
		}else if(rotation < 112.5 && rotation >= 67.5){
			dir = AnConst.DOWN;
		}else if(rotation < 67.5 && rotation >= 22.5){
			dir = AnConst.RIGHT_DOWN;
		}else if(rotation < 22.5 && rotation >= -22.5){
			dir = AnConst.RIGHT;
		}else if(rotation < -22.5 && rotation >= -67.5){
			dir = AnConst.RIGHT_UP;
		}else if(rotation < -67.5 && rotation >= -112.5){
			dir = AnConst.UP;
		}else if(rotation < -112.5 && rotation >= -157.5){
			dir = AnConst.LEFT_UP;
		}
	}
	public double getNowMoveV(){
		if(buffList.get(BuffInfo.SPEED_ADD) != null && buffList.get(BuffInfo.SPEED_DEL) != null){
			return moveV*(1+(buffList.get(BuffInfo.SPEED_ADD).addPer+buffList.get(BuffInfo.SPEED_DEL).addPer)/100.0);
		}else if(buffList.get(BuffInfo.SPEED_ADD) != null && buffList.get(BuffInfo.SPEED_DEL) == null){
			return moveV*(1+buffList.get(BuffInfo.SPEED_ADD).addPer/100.0);
		}else if(buffList.get(BuffInfo.SPEED_ADD) == null && buffList.get(BuffInfo.SPEED_DEL) != null){
			return moveV*(1+buffList.get(BuffInfo.SPEED_DEL).addPer/100.0);
		}else{
			return moveV;
		}
		
	}
	//处理buff
	public void handleBuff(){
		long time1 = System.currentTimeMillis();
		Object[] keyArray = buffList.keySet().toArray();
		ArrayList<BuffInfo> removeList = new ArrayList<BuffInfo>();
		for(int i = 0;i<keyArray.length;i++){
			int key = Integer.valueOf(String.valueOf(keyArray[i]));
			BuffArray buffArray = buffList.get(key);
			if(buffArray != null){
				//boolean haveDizzy = false;
				//if((key == BuffInfo.DIZZY_EFFECT || key == BuffInfo.CANNOT_MOVE) && buffArray.buffArray.size() > 0){
				//	haveDizzy = true;
				//}
				for(int j = 0;j < buffArray.buffArray.size();j++){
					BuffInfo buffInfo = buffArray.buffArray.get(j);
					//如果是掉血buff
					if(key == BuffInfo.CONTINUED_EFFECT_DAMAGE || key == BuffInfo.CONTINUED_EFFECT_TREAT){
						//时间已经超过掉血时间
						if(time1 >= buffInfo.nextInterval){
							//如果有无敌状态，则不掉血（无敌解除所有的debuff，所以这个判断其实没意义）
							//if(!isIceBox()){
								int damageValue = 0;
								if(key == BuffInfo.CONTINUED_EFFECT_DAMAGE){
									//无敌斩时掉血为0
									if(isUserSkill && skillData.getInt("type") == SkillData.OMNISLASH){
										
									}else{
										damageValue = decHp(buffInfo.buffData.getInt("damage"));
									}
								}else{
									damageValue = addHp(buffInfo.buffData.getInt("damage"));
								}
								
								//如果死了 其他的就不用计算了
								//伤害包
								TcpPacket pt1 = new TcpPacket(cyou.akworld.OpCodeEx.SKILL_DAMAGE_S);
								if(key == BuffInfo.CONTINUED_EFFECT_DAMAGE){
									//类型2是buff掉血
									pt1.putInt(2);
								}else{
									//类型4是buff涨血
									pt1.putInt(4);
								}
								
								pt1.putInt(buffInfo.buffData.getInt("id"));
								pt1.putInt(getServerId());
								pt1.putInt(damageValue);
								for(int m = 0;m < horizonPlayer.size();m++){
									WebServer webServer2 = horizonPlayer.get(m);
									if(webServer2.sceneData != null && webServer2.sceneData.sceneId == sceneData.sceneId){
										webServer2.getSession().send(pt1);
									}
								}
								if(this.getClass() == WebServer.class){
									((WebServer)this).getSession().send(pt1);
								}
								
								if(isDead){
									return;
								}
							//}
							
							//更新下一次时间
							buffInfo.nextInterval += buffInfo.buffData.getInt("interval");
						}
					}
					
					if(time1 - buffInfo.startTime >= buffInfo.buffData.getInt("duration")){
						buffArray.buffArray.remove(buffInfo);
						removeList.add(buffInfo);
						if(buffInfo.buffData.getInt("type") == BuffInfo.SPEED_ADD || buffInfo.buffData.getInt("type") == BuffInfo.SPEED_DEL){
							if(buffArray.buffArray.size() > 0 ){
								SkillData.sortBuff(buffArray);
								buffArray.addPer = buffArray.buffArray.get(0).buffData.getInt("value");
							}else{
								buffArray.addPer = 0;
							}
							
						}
						if(buffInfo.buffData.getInt("type") == BuffInfo.ADD_OR_SUBTRACT_DAMAGE){
							SkillData.calculateBuff(buffArray);
						}
						j--;
					}
				}
				//boolean clearDizzy = false;
				//if((key == BuffInfo.DIZZY_EFFECT || key == BuffInfo.CANNOT_MOVE) && buffArray.buffArray.size() == 0){
				//	clearDizzy = true;
				//}
				//如果有，并且眩晕被清除了
				//if(haveDizzy && clearDizzy){
					//眩晕解除重置时间（已废弃，这里重置在原则上不对）
					//time = time1;
					//log.info("清除眩晕buff，更新time"+time);
				//}
			}
		}
		if(removeList.size() > 0){
			TcpPacket pt2 = new TcpPacket(cyou.akworld.OpCodeEx.DEL_BUFF_S);
			pt2.putInt(serverId);
			pt2.putInt(removeList.size());
			for(int i = 0;i<removeList.size();i++){
				pt2.putInt(removeList.get(i).buffData.getInt("id"));
				pt2.putInt(removeList.get(i).serverId);
			}
			
			for(int m = 0;m < horizonPlayer.size();m++){
				WebServer webServer2 = horizonPlayer.get(m);
				if(webServer2.sceneData != null && webServer2.sceneData.sceneId == sceneData.sceneId){
					webServer2.getSession().send(pt2);
				}
			}
			if(this.getClass() == WebServer.class){
				((WebServer)this).getSession().send(pt2);
			}
		}
		
	}
	//清理debuff
	public void clearAllDebuff(){
		Object[] keyArray = buffList.keySet().toArray();
		ArrayList<BuffInfo> removeList = new ArrayList<BuffInfo>();
		for(int i = 0;i<keyArray.length;i++){
			int key = Integer.valueOf(String.valueOf(keyArray[i]));
			BuffArray buffArray = buffList.get(key);
			if(buffArray != null){
				
				for(int j = 0;j < buffArray.buffArray.size();j++){
					BuffInfo buffInfo = buffArray.buffArray.get(j);
					if(buffInfo.buffData.getInt("isEnemy") == 1){
						buffArray.buffArray.remove(buffInfo);
						removeList.add(buffInfo);
						if(buffInfo.buffData.getInt("type") == BuffInfo.SPEED_ADD || buffInfo.buffData.getInt("type") == BuffInfo.SPEED_DEL){
							if(buffArray.buffArray.size() > 0 ){
								SkillData.sortBuff(buffArray);
								buffArray.addPer = buffArray.buffArray.get(0).buffData.getInt("value");
							}else{
								buffArray.addPer = 0;
							}
							
						}
						if(buffInfo.buffData.getInt("type") == BuffInfo.ADD_OR_SUBTRACT_DAMAGE){
							SkillData.calculateBuff(buffArray);
						}
						j--;
					}
				}
				
			}
		}
		if(removeList.size() > 0){
			TcpPacket pt2 = new TcpPacket(cyou.akworld.OpCodeEx.DEL_BUFF_S);
			pt2.putInt(serverId);
			pt2.putInt(removeList.size());
			for(int i = 0;i<removeList.size();i++){
				pt2.putInt(removeList.get(i).buffData.getInt("id"));
				pt2.putInt(removeList.get(i).serverId);
			}
			
			for(int m = 0;m < horizonPlayer.size();m++){
				WebServer webServer2 = horizonPlayer.get(m);
				if(webServer2.sceneData != null && webServer2.sceneData.sceneId == sceneData.sceneId){
					webServer2.getSession().send(pt2);
				}
			}
			if(this.getClass() == WebServer.class){
				((WebServer)this).getSession().send(pt2);
			}
		}
		
	}
	//清理debuff
	public void clearDebuffByType(int type){
		ArrayList<BuffInfo> removeList = null;
		BuffArray buffArray = buffList.get(type);
		if(buffArray != null){
			removeList = new ArrayList<BuffInfo>();
			for(int j = 0;j < buffArray.buffArray.size();j++){
				BuffInfo buffInfo = buffArray.buffArray.get(j);
				buffArray.buffArray.remove(buffInfo);
				removeList.add(buffInfo);
				if(buffInfo.buffData.getInt("type") == BuffInfo.SPEED_ADD || buffInfo.buffData.getInt("type") == BuffInfo.SPEED_DEL){
					if(buffArray.buffArray.size() > 0 ){
						SkillData.sortBuff(buffArray);
						buffArray.addPer = buffArray.buffArray.get(0).buffData.getInt("value");
					}else{
						buffArray.addPer = 0;
					}
					
				}
				if(buffInfo.buffData.getInt("type") == BuffInfo.ADD_OR_SUBTRACT_DAMAGE){
					SkillData.calculateBuff(buffArray);
				}
				j--;
			}
			
		}
		if(removeList != null && removeList.size() > 0){
			TcpPacket pt2 = new TcpPacket(cyou.akworld.OpCodeEx.DEL_BUFF_S);
			pt2.putInt(serverId);
			pt2.putInt(removeList.size());
			for(int i = 0;i<removeList.size();i++){
				pt2.putInt(removeList.get(i).buffData.getInt("id"));
				pt2.putInt(removeList.get(i).serverId);
			}
			
			for(int m = 0;m < horizonPlayer.size();m++){
				WebServer webServer2 = horizonPlayer.get(m);
				if(webServer2.sceneData != null && webServer2.sceneData.sceneId == sceneData.sceneId){
					webServer2.getSession().send(pt2);
				}
			}
			if(this.getClass() == WebServer.class){
				((WebServer)this).getSession().send(pt2);
			}
		}
		
	}
	//取消自身的有益buff
	public void cancelMyBuffById(int buffId){
		JSONObject buff = GameServerService.buffData.getJSONObject(buffId+"");
		ArrayList<BuffInfo> removeList = null;
		if(buff != null){
			BuffArray buffArray = buffList.get(buff.getInt("type"));
			if(buffArray != null){
				for(int j = 0;j < buffArray.buffArray.size();j++){
					BuffInfo buffInfo = buffArray.buffArray.get(j);
					if(buffInfo.serverId == this.serverId && buffInfo.buffData.getInt("id") == buff.getInt("id")){
						if(removeList == null){
							removeList = new ArrayList<BuffInfo>();
						}
						buffArray.buffArray.remove(buffInfo);
						removeList.add(buffInfo);
						if(buffInfo.buffData.getInt("type") == BuffInfo.SPEED_ADD || buffInfo.buffData.getInt("type") == BuffInfo.SPEED_DEL){
							if(buffArray.buffArray.size() > 0 ){
								SkillData.sortBuff(buffArray);
								buffArray.addPer = buffArray.buffArray.get(0).buffData.getInt("value");
							}else{
								buffArray.addPer = 0;
							}
							
						}
						if(buffInfo.buffData.getInt("type") == BuffInfo.ADD_OR_SUBTRACT_DAMAGE){
							SkillData.calculateBuff(buffArray);
						}
						j--;
					}
				}
			}
		}
		
		
		if(removeList != null && removeList.size() > 0){
			TcpPacket pt2 = new TcpPacket(cyou.akworld.OpCodeEx.DEL_BUFF_S);
			pt2.putInt(serverId);
			pt2.putInt(removeList.size());
			for(int i = 0;i<removeList.size();i++){
				pt2.putInt(removeList.get(i).buffData.getInt("id"));
				pt2.putInt(removeList.get(i).serverId);
			}
			
			for(int m = 0;m < horizonPlayer.size();m++){
				WebServer webServer2 = horizonPlayer.get(m);
				if(webServer2.sceneData != null && webServer2.sceneData.sceneId == sceneData.sceneId){
					webServer2.getSession().send(pt2);
				}
			}
			if(this.getClass() == WebServer.class){
				((WebServer)this).getSession().send(pt2);
			}
		}
		
	}
	/**
	 * 是否眩晕
	 * @return
	 */
	public boolean isDizzy(){
		BuffArray buffArray = buffList.get(BuffInfo.DIZZY_EFFECT);
		if(buffArray != null && buffArray.buffArray.size() > 0){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 是否冰箱
	 * @return
	 */
	public boolean isIceBox(){
		BuffArray buffArray = buffList.get(BuffInfo.ICE_BOX);
		if(buffArray != null && buffArray.buffArray.size() > 0){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 是否变羊
	 * @return
	 */
	public boolean isCast(){
		BuffArray buffArray = buffList.get(BuffInfo.CAST);
		if(buffArray != null && buffArray.buffArray.size() > 0){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 是否定身
	 * @return
	 */
	public boolean isCanNotMove(){
		BuffArray buffArray = buffList.get(BuffInfo.CANNOT_MOVE);
		if(buffArray != null && buffArray.buffArray.size() > 0){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 是否有法术发射buff
	 * @return
	 */
	public boolean isHaveMagicrReflect(){
		BuffArray buffArray = buffList.get(BuffInfo.MAGIC_REFLECT);
		if(buffArray != null && buffArray.buffArray.size() > 0){
			return true;
		}else{
			return false;
		}
	}
	public void update(){
		
		handleBuff();
		long nowTime = System.currentTimeMillis();
		if(isDizzy() || isIceBox() || isCast()){
			time = nowTime;
			return;
		}
		Point2D.Double point;
		Point2D.Double point2 = null;
		double moveDistance;
		Point3D point3D;
		if(isWalk){
			if(isLine){
				walkMoveD += (nowTime-time)/1000.0*getNowMoveV();
				if(walkMoveD > walkAllDis){
					walkMoveD = walkAllDis;
				}
				Point2D.Double point11 = IsoUtils.isoToScreen(walkGoal);
				Point2D.Double point22 = IsoUtils.isoToScreen(walkStart);
				Point2D.Double point33 =interpolate(point11,point22,walkMoveD);
				
				Point3D point3D11 = IsoUtils.screenToIso(point33);
				
				
				if(walkMoveD  == walkAllDis){
					position = walkGoal.clone();
					//if(this.getClass() == WebServer.class){
					//	log.info("走完了");
					//}
					setPath(null);
				}else{
					position = point3D11;
				}
				time = nowTime;
				return;
			}
			point = IsoUtils.isoToScreen(position);
			double distance = 0;
			if(nowNode == null && nowArrivePoint == null){
				while(distance == 0 && _path.size() > 0){
					nowNode = _path.remove(0);
					nowArrivePoint = IsoUtils.isoToScreen(new Point3D(nowNode.x*size+0.0,0.0,nowNode.y*size+0.0));
					//如果是同一个格，直接连接下一个不同的格，不在走到同一个格的中心点再连接
					if(nowNode.x == Math.round(position.x/size) && nowNode.y == Math.round(position.z/size)){
						distance = Point.distance(point.x, point.y, nowArrivePoint.x, nowArrivePoint.y);
						if(distance > 0){
							
						}
						distance = 0;
					}else{
						distance = Point.distance(point.x, point.y, nowArrivePoint.x, nowArrivePoint.y);
					}
					
				}
			}else{
				distance = Point.distance(point.x, point.y, nowArrivePoint.x, nowArrivePoint.y);
				if(distance == 0){
					while(distance == 0 && _path.size() > 0){
						nowNode = _path.remove(0);
						nowArrivePoint = IsoUtils.isoToScreen(new Point3D(nowNode.x*size+0.0,0.0,nowNode.y*size+0.0));
						distance = Point.distance(point.x, point.y, nowArrivePoint.x, nowArrivePoint.y);
					}
				}
				
			}
			if(distance == 0){
				nowNode = null;
				nowArrivePoint = null;
				isWalk = false;
				//这个没什么用，只是作为一个规则
				time = nowTime;
				return;
			}
			
			
			
			moveDistance = (nowTime-time)/1000.0*getNowMoveV();
			
			

			Point upNode = new Point((int)Math.round(position.x/size),(int)Math.round(position.z/size));
			Point arrrie = new Point(nowNode.x,nowNode.y);
			if(moveDistance > distance){
				while(moveDistance > distance){
					moveDistance = moveDistance-distance;
					distance = 0;
					while(distance == 0 && _path.size() > 0){
						upNode = new Point(nowNode.x,nowNode.y);
						nowNode = _path.remove(0);
						nowArrivePoint = IsoUtils.isoToScreen(new Point3D(nowNode.x*size+0.0,0.0,nowNode.y*size+0.0));
						arrrie = new Point(nowNode.x,nowNode.y);
						point = IsoUtils.isoToScreen(new Point3D(upNode.x*size+0.0,0.0,upNode.y*size+0.0));
						distance = Point.distance(point.x, point.y, nowArrivePoint.x, nowArrivePoint.y);
					}
					if(distance == 0){
						moveDistance = 0;
						
						point2 = nowArrivePoint;
						nowNode = null;
						nowArrivePoint = null;
						isWalk = false;
					}else{
						
					}
				}
				if(point2 == null){
					
					point2 = interpolate(nowArrivePoint,point,moveDistance);
					
					
				}
				
				
				
			}else{
				
				point2 = interpolate(nowArrivePoint,point,moveDistance);
				
			}
			
			point3D = IsoUtils.screenToIso(point2);
			calculateDir(position,point3D,false);
			position = point3D;
			time = nowTime;
//			int startX = upNode.x-1;
//			int endX = upNode.x+1;
//			int startY = upNode.y-1;
//			int endY = upNode.y+1;
//			int a = 0;
//			for(int i = startX;i<=endX;i++){
//				for(int j = startY;j<=endY;j++){
//					a++;
//					if(i ==arrrie.x && j == arrrie.y){
//						if(a == 1){
//							dir = AnConst.UP;
//						}else if(a == 2){
//							dir = AnConst.LEFT_UP;
//						}else if(a == 3){
//							dir = AnConst.LEFT;
//						}else if(a == 4){
//							dir = AnConst.RIGHT_UP;
//						}else if(a == 6){
//							dir = AnConst.LEFT_DOWN;
//						}else if(a == 7){
//							dir = AnConst.RIGHT;
//						}else if(a == 8){
//							dir = AnConst.RIGHT_DOWN;
//						}else if(a == 9){
//							dir = AnConst.DOWN;
//						}
//					}
//					
//				}
//			}
		}else if(isUserSkill){
			if(skillData.getInt("type") == SkillData.BIG_JUMP || skillData.getInt("type") == SkillData.CLICK_THE_FLY){
				
				double passedTime = (nowTime-time)/1000.0;
				
				moveD += passedTime*skillJumpH;
				if(moveD != 0){
					time = nowTime;
				}
				
				
				point2 = interpolate(IsoUtils.isoToScreen(goal),IsoUtils.isoToScreen(start),moveD);
				
				point3D = IsoUtils.screenToIso(point2);
				
				point3D.y = -(skillJumpV*passedTime+jumpVerticalA*passedTime*passedTime/2)+position.y;
				skillJumpV = skillJumpV+jumpVerticalA*passedTime;
				//System.out.println(moveD);
				if(point3D.y >= 0){
					TcpPacket pt2 = new TcpPacket(cyou.akworld.OpCodeEx.SKILL_COMPLETE_S);
					pt2.putInt(serverId);
					pt2.putInt(this.skillData.getInt("id"));
					clearUseSkill(true);
					
					
					for(int m = 0;m < horizonPlayer.size();m++){
						WebServer webServer2 = horizonPlayer.get(m);
						if(webServer2.sceneData != null && webServer2.sceneData.sceneId == sceneData.sceneId){
							webServer2.getSession().send(pt2);
						}
					}
					if(this.getClass() == WebServer.class){
						((WebServer)this).getSession().send(pt2);
					}
					return;
				}
				position = point3D;
				
			}else if(skillData.getInt("type") == SkillData.RUSH){
				double passedTime = (nowTime-time)/1000.0;
				
				moveD += passedTime*skillJumpH;
				if(moveD != 0){
					time = nowTime;
				}
				
				if(moveD > skillJumpV){
					moveD = skillJumpV;
				}
				point2 = interpolate(IsoUtils.isoToScreen(goal),IsoUtils.isoToScreen(start),moveD);
				
				point3D = IsoUtils.screenToIso(point2);
				
				if(moveD == skillJumpV){
					TcpPacket pt2 = new TcpPacket(cyou.akworld.OpCodeEx.SKILL_COMPLETE_S);
					pt2.putInt(serverId);
					pt2.putInt(this.skillData.getInt("id"));
					clearUseSkill(true);
					
					
					for(int m = 0;m < horizonPlayer.size();m++){
						WebServer webServer2 = horizonPlayer.get(m);
						if(webServer2.sceneData != null && webServer2.sceneData.sceneId == sceneData.sceneId){
							webServer2.getSession().send(pt2);
						}
					}
					if(this.getClass() == WebServer.class){
						((WebServer)this).getSession().send(pt2);
					}
					return;
				}
				position = point3D;
			}else if(skillData.getInt("type") == SkillData.OMNISLASH){
				double passedTime = (nowTime-time)/1000.0;
				
				moveD += passedTime*skillJumpH;
				if(moveD != 0){
					time = nowTime;
				}
				
				if(moveD > skillJumpV){
					moveD = skillJumpV;
				}
				point2 = interpolate(IsoUtils.isoToScreen(goal),IsoUtils.isoToScreen(start),moveD);
				
				point3D = IsoUtils.screenToIso(point2);
				
				if(moveD == skillJumpV){
					TcpPacket pt2 = null;
					//看目标
					if(toTarget.sceneData == null || toTarget.sceneData.sceneId != sceneData.sceneId || toTarget.isDead ){
						//直接完成无敌斩
						pt2 = new TcpPacket(cyou.akworld.OpCodeEx.SKILL_OMNISLASH_COMPLETE_S);
						pt2.putInt(serverId);
						pt2.putInt(this.skillData.getInt("id"));
						clearUseSkill(true);
						for(int m = 0;m < horizonPlayer.size();m++){
							WebServer webServer2 = horizonPlayer.get(m);
							if(webServer2.sceneData != null && webServer2.sceneData.sceneId == sceneData.sceneId){
								webServer2.getSession().send(pt2);
							}
						}
						if(this.getClass() == WebServer.class){
							((WebServer)this).getSession().send(pt2);
						}
						
						return;
					}
					flyTime++;
					if(skillData.getInt("flyTime") == flyTime){
						//伤害结算
						SkillData.selectTarget(skillData, this, toTarget,null,sceneData,null);
						//结束无敌斩
						pt2 = new TcpPacket(cyou.akworld.OpCodeEx.SKILL_OMNISLASH_COMPLETE_S);
						pt2.putInt(serverId);
						pt2.putInt(this.skillData.getInt("id"));
						clearUseSkill(true);
						for(int m = 0;m < horizonPlayer.size();m++){
							WebServer webServer2 = horizonPlayer.get(m);
							if(webServer2.sceneData != null && webServer2.sceneData.sceneId == sceneData.sceneId){
								webServer2.getSession().send(pt2);
							}
						}
						if(this.getClass() == WebServer.class){
							((WebServer)this).getSession().send(pt2);
						}
						return;
					}
					
					//伤害结算
					SkillData.selectTarget(skillData, this, toTarget,null,sceneData,null);
					//选目标
					ActivityThing newToTarget = selectTarget();
					if(newToTarget == null){
						//结束无敌斩
						pt2 = new TcpPacket(cyou.akworld.OpCodeEx.SKILL_OMNISLASH_COMPLETE_S);
						pt2.putInt(serverId);
						pt2.putInt(this.skillData.getInt("id"));
						clearUseSkill(true);
						for(int m = 0;m < horizonPlayer.size();m++){
							WebServer webServer2 = horizonPlayer.get(m);
							if(webServer2.sceneData != null && webServer2.sceneData.sceneId == sceneData.sceneId){
								webServer2.getSession().send(pt2);
							}
						}
						if(this.getClass() == WebServer.class){
							((WebServer)this).getSession().send(pt2);
						}
						return;
					}
					//做转向广播
					changeTarget(newToTarget);
					return;
				}
				position = point3D;
			}
		}else if(nowReadSkill != null){
			
			if(nowReadSkill.getInt("target") == 1 && (readTarget.sceneData == null || readTarget.sceneData.sceneId != sceneData.sceneId || readTarget.isDead)){
				cannelReadSkill(true);
				//log.info("施法的目标消失，取消施法");
			}
			if(nowReadSkill != null && (nowTime-nowReadTime>= nowReadSkill.getInt("read"))){
				if(nowReadSkill.getInt("target") == 1){
					GameServerService.attack(this,nowReadSkill,readTarget,1,null,readDir,readDistance,true,0);
				}else if(nowReadSkill.getInt("point") == 1){
					GameServerService.attack(this,nowReadSkill,null,2,readToPosition,readDir,readDistance,true,0);
				}else{
					GameServerService.attack(this,nowReadSkill,null,0,null,readDir,readDistance,true,0);
				}
				cannelReadSkill(true);
			}
			//这里可能已经完成读条的了
			time = nowTime;
			
		}
	}
	//无敌斩改变目标
	public void changeTarget(ActivityThing toTarget){
		//技能移动
		this.goal = toTarget.position.clone();
		this.goal.y = 0.0;
		start = this.position.clone();
		start.y = 0.0;
		if(skillData.getInt("type") == SkillData.OMNISLASH){
			//速度翻倍
			skillJumpH = monster.getInt("rushSpeed")*2;
			Point2D.Double point = IsoUtils.isoToScreen(start);
			Point2D.Double point1 = IsoUtils.isoToScreen(this.goal);
			//距离翻倍
			skillJumpV = Point.distance(point.x,point.y,point1.x,point1.y)*2;
			//目标点改变
			Point2D.Double point2 = interpolateWithArrvie(point1, point, skillJumpV/2);
			this.goal = IsoUtils.screenToIso(point2);
			
			this.toTarget = toTarget;
		}
		moveD = 0;
		//计算方向
		if(skillData.getInt("type") != SkillData.CLICK_THE_FLY){
			calculateDir(start,this.goal,false);
		}
		//广播出去
		TcpPacket pt2 = new TcpPacket(cyou.akworld.OpCodeEx.SKILL_OMNISLASH_CHANGE_S);
		pt2.putInt(serverId);
		pt2.putInt(this.skillData.getInt("id"));
		pt2.putInt(toTarget.getServerId());
		pt2.putDouble(toTarget.position.x);
		pt2.putDouble(toTarget.position.y);
		pt2.putDouble(toTarget.position.z);
		for(int m = 0;m < horizonPlayer.size();m++){
			WebServer webServer2 = horizonPlayer.get(m);
			if(webServer2.sceneData != null && webServer2.sceneData.sceneId == sceneData.sceneId){
				webServer2.getSession().send(pt2);
			}
		}
		if(this.getClass() == WebServer.class){
			((WebServer)this).getSession().send(pt2);
		}
	}
	//找无敌斩的目标,随机的
	public ActivityThing selectTarget(){
		ArrayList<WebServer> horizonThing = horizonPlayer;
		int size = horizonThing.size();
		ArrayList<ActivityThing> beAttackList = new ArrayList<ActivityThing>();
		//ActivityThing newToTarget = null;
		for(int i = 0;i < size;i++){
			ActivityThing webServer = horizonThing.get(i);
			//可能玩家视野里的这些人物，有一些已经去别的场景，并且没有及时的切换
			if(webServer.sceneData == null || webServer.sceneData.sceneId != sceneData.sceneId){
				continue;
			}
			//如果是死了
			if(webServer.isDead){
				continue;
			}
			if(skillData.getInt("isEnemy") == 1){
				
				if((camp == null && webServer.camp == null) ||
						((camp != null && webServer.camp != null) && camp.getInt("id") == webServer.camp.getInt("id"))){
					continue;
				}
			}else{
				if((camp != null && webServer.camp == null)||
						(camp == null && webServer.camp != null) ||
						((camp != null && webServer.camp != null) && camp.getInt("id") != webServer.camp.getInt("id"))){
					continue;
				}
			}
			double distance = sceneData.distanceMap.get(getServerId() < webServer.getServerId() ? getServerId()+"_"+webServer.getServerId() :  webServer.getServerId()+"_"+getServerId());
			if(distance > skillData.getInt("attackDistance")/2){
				break;
			}
			//newToTarget = webServer;
			//break;
			beAttackList.add(webServer);
		}
		//if(newToTarget == null){
			ArrayList<Monster> horizonThing1 =  horizonNpc;
			int size1 = horizonThing1.size();
			for(int i = 0;i < size1;i++){
				ActivityThing webServer = horizonThing1.get(i);
				//如果是死了
				if(webServer.isDead){
					continue;
				}
				if(skillData.getInt("isEnemy") == 1){
					if((camp == null && webServer.camp == null) ||
							((camp != null && webServer.camp != null) && camp.getInt("id") == webServer.camp.getInt("id"))){
						continue;
					}
				}else{
					if((camp != null && webServer.camp == null)||
							(camp == null && webServer.camp != null) ||
							((camp != null && webServer.camp != null) && camp.getInt("id") != webServer.camp.getInt("id"))){
						continue;
					}
				}
				double distance = sceneData.distanceMap.get(getServerId() < webServer.getServerId() ? getServerId()+"_"+webServer.getServerId() :  webServer.getServerId()+"_"+getServerId());
				if(distance > skillData.getInt("attackDistance")/2){
					break;
				}
				//newToTarget = webServer;
				//break;
				beAttackList.add(webServer);
			}
		//}
		if(beAttackList.size() == 0){
			return null;
		}else{
			int index = (int)(Math.random()*beAttackList.size());
			return beAttackList.get(index);
		}
		
	}
	public void userSkill(JSONObject skillData,Point3D goal,double distance,ActivityThing toTarget){
		//技能移动
		time = System.currentTimeMillis();
		this.skillData = skillData;
		this.goal = goal.clone();
		this.goal.y = 0.0;
		isUserSkill = true;
		//清空保留的值
		savePath = null;
		saveDir = 0;
		savePoint = null;
		//终止寻路
		setPath(null);
		start = this.position.clone();
		start.y = 0.0;
		this.position.y = 0.0;
		if(skillData.getInt("type") == SkillData.BIG_JUMP){
			skillJumpH = distance*Math.abs(((jumpVerticalA/jumpVerticalV)/2));
			skillJumpV = jumpVerticalV;
		}else if(skillData.getInt("type") == SkillData.RUSH){
			skillJumpH = monster.getInt("rushSpeed");
			Point2D.Double point = IsoUtils.isoToScreen(start);
			Point2D.Double point1 = IsoUtils.isoToScreen(this.goal);
			skillJumpV = Point.distance(point.x,point.y,point1.x,point1.y);
		}else if(skillData.getInt("type") == SkillData.CLICK_THE_FLY){
			skillJumpH = distance*Math.abs(((jumpVerticalA/jumpVerticalV)/2));
			skillJumpV = jumpVerticalV;
			//被影响，取消技能。
			cannelReadSkill(true);
		//无敌斩
		}else if(skillData.getInt("type") == SkillData.OMNISLASH){
			//速度翻倍
			skillJumpH = monster.getInt("rushSpeed")*2;
			Point2D.Double point = IsoUtils.isoToScreen(start);
			Point2D.Double point1 = IsoUtils.isoToScreen(this.goal);
			//距离翻倍
			skillJumpV = Point.distance(point.x,point.y,point1.x,point1.y)*2;
			//目标点改变
			Point2D.Double point2 = interpolateWithArrvie(point1, point, skillJumpV/2);
			this.goal = IsoUtils.screenToIso(point2);
			
			this.toTarget = toTarget;
		}
		
		
		
		//System.out.println(distance);
		moveD = 0;
		//计算方向
		if(skillData.getInt("type") != SkillData.CLICK_THE_FLY){
			calculateDir(start,this.goal,false);
		}
	}
	public void beAttack(ActivityThing attacker){
		
	}
	public void clearUseSkill(boolean isSetPos){
		if(isUserSkill){
			//是否设置位置的终点
			if(isSetPos){
				position = goal.clone();
			}
			isUserSkill = false;
			this.skillData = null;
			skillJumpH = 0;
			skillJumpV = 0;
			goal = null;
			start = null;
			moveD = 0;
			this.toTarget = null;
			flyTime = 1;
		}
	}
	//2015-2-23xp处理保留的未广播的动作（技能施放成功后，不能立即广播玩家保留的行为，因为
	//可能这个施放技能的玩家并没有被完全的广播出去，导致就算广播玩家的行为，该看到的人也看不到，所以等区域广播后，收到消息包之前把这个行为广播出去）
	public void action(){
		if(!isUserSkill && savePath != null){
			this.dir = saveDir;
			position = savePoint;
			GameServerService.move1(this, savePath);
			savePath = null;
			saveDir = 0;
			savePoint = null;
			log.info("原来保留的路径广播出去了");
		}
	}
	public void clear(){
		
		setPath(null);
		horizonPlayer.clear();
		horizonPlayer = null;
		horizonNpc.clear();
		horizonNpc = null;
		buffList.clear();
		clearUseSkill(true);
		cannelReadSkill(false);
		super.clear();
	}
}
