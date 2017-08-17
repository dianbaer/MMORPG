package ak.log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ak.building.Building;
import ak.building.BuildingList;
import ak.player.PlayerEx;

import cyou.mrd.Platform;
import cyou.mrd.entity.Player;
import cyou.mrd.sdk.log.ILogger;
import cyou.mrd.sdk.log.LogSdk;
import cyou.mrd.service.Service;

public class AkLogService implements Service {

	private static ILogger logger;
	protected static BlockingQueue<Map<String, Object>> pendingLogs = null;
	private static final Logger log = LoggerFactory.getLogger(AkLogService.class);
	/**
	 * 心跳
	 */
	public static String HEARTBEAT = "heartbeat";
	/**
	 * 登出
	 */
	public static String LOGOUT = "logout";
	/**
	 * 金钱的花费
	 */
	public static String MONEYCOST = "moneycost";
	/**
	 * PVE战斗
	 */
	public static String PVEFIGHT = "PVEfight";
	/**
	 * 人口
	 */
	public static String POPULATION = "population";
	/**
	 * 建筑物
	 */
	public static String BUILDING = "building";
	/**
	 * 新手引导
	 */
	public static String GUIDE = "guide";
	/**
	 * 钻石异常
	 */
	public static String DIAMOND = "diamond";
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return "AkLogService";
	}

	@Override
	public void startup() throws Exception {
		//初始化logSdk
		LogSdk.startup();
		//获取logger
		logger = LogSdk.getLogger();
		pendingLogs = new LinkedBlockingQueue<Map<String,Object>>();
		Thread logThread = new Thread(new LogWork(), "logThread[AkLogService]");
		logThread.start();
	}
	class LogWork implements Runnable {
		public void run() {
			while (true) {
				try {
					Map<String, Object> map = pendingLogs.take();
					String type = (String)map.get("type");
					if(type == HEARTBEAT){
						PlayerEx player = (PlayerEx)map.get("player");
						String timestamp = (String)map.get("timestamp");
						Map<String, String> msg = getNormal(player,timestamp);
						logger.addLog(type, msg);
					}else if(type == LOGOUT){
						PlayerEx player = (PlayerEx)map.get("player");
						String timestamp = (String)map.get("timestamp");
						Map<String, String> msg = getNormal(player,timestamp);
						logger.addLog(type, msg);
					}else if(type == MONEYCOST){
						PlayerEx playerOld = (PlayerEx)map.get("playerOld");
						PlayerEx playerNow = (PlayerEx)map.get("playerNow");
						String timestamp = (String)map.get("timestamp");
						Map<String, String> msg = getNormal(playerNow,timestamp);
						//金币的变化比率
						msg.put("oldValue", String.valueOf(playerOld.getMoney()));
						msg.put("nowValue", String.valueOf(playerNow.getMoney()));
						msg.put("type", "2");
						logger.addLog(type, msg);
						//元宝的变化比率
						msg.put("oldValue", String.valueOf(playerOld.getAccount().getRemainDollar()));
						msg.put("nowValue", String.valueOf(playerNow.getAccount().getRemainDollar()));
						msg.put("type", "1");
						logger.addLog(type, msg);
					}else if(type == PVEFIGHT){
						PlayerEx playerOld = (PlayerEx)map.get("playerOld");
						PlayerEx playerNow = (PlayerEx)map.get("playerNow");
						String timestamp = (String)map.get("timestamp");
						String[] readedsOld = null;
						String[] readedsNow = null;
						if(playerOld.getBattleWin() != null && playerOld.getBattleWin().trim() != ""){
							readedsOld = playerOld.getBattleWin().split(",");
						}
						if(playerNow.getBattleWin() != null && playerNow.getBattleWin().trim() != ""){
							readedsNow = playerNow.getBattleWin().split(",");
						}
						if(readedsNow != null){
							for (String ridNow : readedsNow) {
								String[] valuesNow = ridNow.split("=");
								if(valuesNow.length < 2){
									continue;
								}
								int idNow = Integer.parseInt(valuesNow[0]);
								int starNow = Integer.parseInt(valuesNow[1]);
								boolean isHave = false;
								if(readedsOld != null){
									for (String ridOld : readedsOld) {
										String[] valuesOld = ridOld.split("=");
										if(valuesOld.length < 2){
											continue;
										}
										int idOld = Integer.parseInt(valuesOld[0]);
										int starOld = Integer.parseInt(valuesOld[1]);
										if(idOld == idNow && starNow == starOld){
											isHave = true;
											break;
										}
									}
								}
								
								if(!isHave){
									Map<String, String> msg = getNormal(playerNow,timestamp);
									msg.put("guanQiaId", String.valueOf(idNow));
									msg.put("star", String.valueOf(starNow));
									msg.put("type", "1");
									logger.addLog(type, msg);
								}
							}
						}
						if(readedsOld != null){
							for (String ridOld : readedsOld) {
								String[] valuesOld = ridOld.split("=");
								if(valuesOld.length < 2){
									continue;
								}
								int idOld = Integer.parseInt(valuesOld[0]);
								int starOld = Integer.parseInt(valuesOld[1]);
								boolean isHave = false;
								if(readedsNow != null){
									for (String ridNow : readedsNow) {
										String[] valuesNow = ridNow.split("=");
										if(valuesNow.length < 2){
											continue;
										}
										int idNow = Integer.parseInt(valuesNow[0]);
										int starNow = Integer.parseInt(valuesNow[1]);
										if(idNow == idOld && starOld == starNow){
											isHave = true;
											break;
										}
									}
								}
								
								if(!isHave){
									Map<String, String> msg = getNormal(playerNow,timestamp);
									msg.put("guanQiaId", String.valueOf(idOld));
									msg.put("star", String.valueOf(starOld));
									msg.put("type", "2");
									logger.addLog(type, msg);
								}
							}
						}
						
						
					}else if(type == POPULATION){
						PlayerEx playerOld = (PlayerEx)map.get("playerOld");
						PlayerEx playerNow = (PlayerEx)map.get("playerNow");
						String timestamp = (String)map.get("timestamp");
						Map<String, String> msg = getNormal(playerNow,timestamp);
						msg.put("oldValue", String.valueOf(playerOld.getSceneRabit0Number(1)+playerOld.getSceneRabit1Number(1)));
						msg.put("nowValue", String.valueOf(playerNow.getSceneRabit0Number(1)+playerNow.getSceneRabit1Number(1)));
						logger.addLog(type, msg);
					}else if(type == BUILDING){
						PlayerEx playerOld = (PlayerEx)map.get("playerOld");
						PlayerEx playerNow = (PlayerEx)map.get("playerNow");
						String timestamp = (String)map.get("timestamp");
						JSONObject jsonDataNow = JSONObject.fromObject(playerNow.getJsonData());
						JSONObject jsonDataOld = null;
						if(playerOld.getJsonData() != null){
							jsonDataOld = JSONObject.fromObject(playerOld.getJsonData());
						}
						JSONArray buildingListNow = null;
						if(jsonDataNow.containsKey("Buildings")){
							buildingListNow = jsonDataNow.getJSONArray("Buildings");
						}
						JSONArray buildingListOld = null;
						if(jsonDataOld != null && jsonDataOld.containsKey("Buildings")){
							buildingListOld = jsonDataOld.getJSONArray("Buildings");
						}
						for(int i = 0;i < buildingListNow.size();i++){
							JSONObject buildingNow = buildingListNow.getJSONObject(i);
							if(buildingNow != null){
								boolean isHave = false;
								if(buildingListOld != null && buildingListOld.size()>0){
									for(int j = 0;j < buildingListOld.size();j++){
										JSONObject buildingOld = buildingListOld.getJSONObject(j);
										//装饰性建筑
										if(buildingNow.getInt("ID") < 1000){
											if(buildingNow.getInt("ID") == buildingOld.getInt("ID") && buildingNow.getInt("havestTimes") == buildingOld.getInt("havestTimes")){
												isHave = true;
												break;
											}
										//非装饰性建筑
										}else{
											if(buildingNow.getInt("ID")/1000 == buildingOld.getInt("ID")/1000 && buildingNow.getInt("havestTimes") == buildingOld.getInt("havestTimes")){
												isHave = true;
												break;
											}
										}
									}
								}
								if(!isHave){
									Map<String, String> msg = getNormal(playerNow,timestamp);
									msg.put("buildingId", String.valueOf(buildingNow.getInt("ID")));
									msg.put("buildLevel", String.valueOf(buildingNow.getInt("havestTimes")));
									logger.addLog(type, msg);
								}
							}
						}
					}else if(type == GUIDE){
						PlayerEx playerOld = (PlayerEx)map.get("playerOld");
						PlayerEx playerNow = (PlayerEx)map.get("playerNow");
						String timestamp = (String)map.get("timestamp");
						JSONObject guideNow = JSONObject.fromObject(JSONObject.fromObject(playerNow.getJsonData()).get("GuideFrameData"));
						
						JSONObject guideOld = null;
						if(playerOld.getJsonData() != null && playerOld.getJsonData() != ""){
							guideOld = JSONObject.fromObject(JSONObject.fromObject(playerOld.getJsonData()).get("GuideFrameData"));
						}
						//新手引导101-105目前只有这些（如果客户端新加内容需要在这添加）
						for(int i = 101;i <= 105;i++){
							Object guideStrNow = guideNow.get(String.valueOf(i));
							Object guideStrOld = null;
							if(guideOld != null){
								guideStrOld = guideOld.get(String.valueOf(i));
							}
							if((guideStrNow == null && guideStrOld == null) || guideStrNow == null){
								
							}else{
								JSONObject guideObjNow = (JSONObject)guideStrNow;
								if(guideStrOld != null){
									JSONObject guideObjOld = (JSONObject)guideStrOld;
									if(guideObjNow.getInt("bGuideStep") <= guideObjOld.getInt("bGuideStep")){
										continue;
									}
								}
								Map<String, String> msg = getNormal(playerNow,timestamp);
								msg.put("step", String.valueOf(guideObjNow.getInt("bGuideStep")));
								msg.put("type", "2");
								msg.put("id", String.valueOf(i));
								logger.addLog(type, msg);
							}
						}
						//剧情引导
						JSONArray storyNow = guideNow.getJSONArray("StoryFrame");
						JSONArray storyOld = null;
						if(guideOld != null && guideOld.containsKey("StoryFrame")){
							storyOld = guideOld.getJSONArray("StoryFrame");
						}
						for(int i = 0;i < storyNow.size();i++){
							JSONObject jsObjNow = (JSONObject)storyNow.get(i);
							boolean isHave = false;
							if(storyOld != null){
								for(int j = 0;j <storyOld.size();j++){
									JSONObject jsObjOld = (JSONObject)storyOld.get(j);
									if(jsObjNow.getInt("ID") == jsObjOld.getInt("ID") && jsObjNow.getInt("bGuideStep") == jsObjOld.getInt("bGuideStep")){
										isHave = true;
										break;
									}
								}
							}
							if(!isHave){
								Map<String, String> msg = getNormal(playerNow,timestamp);
								msg.put("step", String.valueOf(jsObjNow.getInt("bGuideStep")));
								msg.put("type", "1");
								msg.put("id", String.valueOf(jsObjNow.getInt("ID")));
								logger.addLog(type, msg);
							}
						}
					}else if(type == DIAMOND){
						PlayerEx playerOld = (PlayerEx)map.get("playerOld");
						JSONObject syncDollar = (JSONObject)map.get("syncDollar");
						String timestamp = (String)map.get("timestamp");
						Map<String, String> msg = getNormal(playerOld,timestamp);
						msg.put("imoney0", String.valueOf(playerOld.getAccount().getImoney()));
						msg.put("imoney1", String.valueOf(syncDollar.getInt("BuyDollar")));
						msg.put("rewardDollar0", String.valueOf(playerOld.getAccount().getRewardDollar()));
						msg.put("rewardDollar1", String.valueOf(syncDollar.getInt("RewardDollar")));
						msg.put("usedDollar0", String.valueOf(playerOld.getAccount().getUsedDollar()));
						msg.put("usedDollar1", String.valueOf(syncDollar.getInt("UseDollar")));
						msg.put("compensateDollar0", String.valueOf(playerOld.getAccount().getCompensateDollar()));
						msg.put("compensateDollar1", String.valueOf(syncDollar.getInt("CompensateDollar")));
						msg.put("initDollar0", String.valueOf(playerOld.getAccount().getInitDollar()));
						msg.put("initDollar1", String.valueOf(syncDollar.getInt("InitDollar")));
						logger.addLog(type, msg);
					}
					
				} catch (Exception e) {
					log.error(e.toString(), e);
				}
			}
		}
	}
	@Override
	public void shutdown() throws Exception {
		// TODO Auto-generated method stub

	}
	public static Map<String, String> getNormal(PlayerEx player,String timestamp){
		Map<String, String> msg = new HashMap<String, String>();
		msg.put("timestamp", timestamp);
		msg.put("accountId", String.valueOf(player.getAccount().getId()));
		msg.put("roleId", String.valueOf(player.getId()));
		msg.put("roleName", player.getName());
		msg.put("level", String.valueOf(player.getLevel()));
		return msg;
	}
	public static void printHeartBeat(Player player) {
		Map<String, Object> msg = new HashMap<String, Object>();
		LocalDateTime now = new LocalDateTime();
		msg.put("type", HEARTBEAT);
		msg.put("player", ((PlayerEx)player).clone());
		msg.put("timestamp", now.toString());
		pendingLogs.add(msg);
	}
	public static void printLogout(Player player){
		Map<String, Object> msg = new HashMap<String, Object>();
		LocalDateTime now = new LocalDateTime();
		msg.put("type", LOGOUT);
		msg.put("player", ((PlayerEx)player).clone());
		msg.put("timestamp", now.toString());
		pendingLogs.add(msg);
	}
	public static void printMoneyCost(Player playerOld,Player playerNow){
		Map<String, Object> msg = new HashMap<String, Object>();
		LocalDateTime now = new LocalDateTime();
		msg.put("type", MONEYCOST);
		msg.put("playerOld", playerOld);
		msg.put("playerNow", playerNow);
		msg.put("timestamp", now.toString());
		pendingLogs.add(msg);
	}
	public static void printPVEfight(Player playerOld,Player playerNow){
		Map<String, Object> msg = new HashMap<String, Object>();
		LocalDateTime now = new LocalDateTime();
		msg.put("type", PVEFIGHT);
		msg.put("playerOld", playerOld);
		msg.put("playerNow", playerNow);
		msg.put("timestamp", now.toString());
		pendingLogs.add(msg);
	}
	public static void printPopulation(Player playerOld,Player playerNow){
		Map<String, Object> msg = new HashMap<String, Object>();
		LocalDateTime now = new LocalDateTime();
		msg.put("type", POPULATION);
		msg.put("playerOld", playerOld);
		msg.put("playerNow", playerNow);
		msg.put("timestamp", now.toString());
		pendingLogs.add(msg);
	}
	public static void printBuilding(Player playerOld,Player playerNow){
		Map<String, Object> msg = new HashMap<String, Object>();
		LocalDateTime now = new LocalDateTime();
		msg.put("type", BUILDING);
		msg.put("playerOld", playerOld);
		msg.put("playerNow", playerNow);
		msg.put("timestamp", now.toString());
		pendingLogs.add(msg);
	}
	public static void printGuide(Player playerOld,Player playerNow){
		Map<String, Object> msg = new HashMap<String, Object>();
		LocalDateTime now = new LocalDateTime();
		msg.put("type", GUIDE);
		msg.put("playerOld", playerOld);
		msg.put("playerNow", playerNow);
		msg.put("timestamp", now.toString());
		pendingLogs.add(msg);
	}
	public static void printDiamond(Player playerOld,JSONObject syncDollar){
		Map<String, Object> msg = new HashMap<String, Object>();
		LocalDateTime now = new LocalDateTime();
		msg.put("type", DIAMOND);
		msg.put("playerOld", playerOld);
		msg.put("syncDollar", syncDollar);
		msg.put("timestamp", now.toString());
		pendingLogs.add(msg);
	}
}
