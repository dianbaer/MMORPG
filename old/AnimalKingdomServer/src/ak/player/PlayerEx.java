package ak.player;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import ak.building.BuildingList;
import ak.friend.FriendHome;
import ak.playerSns.PlayerSns;
import ak.quest.QuestList;
import cyou.mrd.Platform;
import cyou.mrd.account.Account;
import cyou.mrd.entity.Player;

public class PlayerEx extends Player implements Serializable {

	//private static final String POOL_KEY_PROCESS_SAVE_TIME_SED = "pool_process_save_sed";
	//private static final String POOL_KEY_PROCESS_MCOUNT = "pool_process_mid_count";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 玩家限定最高等级
	 */
	public static final int MaxLevel = 1000;
	//private static final String POOL_KEY_PROCESS_TOTAL_MONEY = "pool_process_total_money";

	/**
	 * 经验
	 */
	private int exp;
	
	
	private String catsInfo; 
	
	//private static final String POOL_KEY_TRAINCATS = "pool_key_traincats";
	private static final String POOL_KEY_BATTLEWIN = "pool_key_battlewin";
	//private static final String POOL_KEY_GUIDE_DATA = "pool_key_guide_data";
	
	private String items;
	/**
	 * 木材
	 */
	private int woods;
	/**
	 * 总战斗次数
	 */
	private int battleTimes;
	/**
	 * 胜利次数
	 */
	private int battleWinTimes;
	/**
	 * 食物
	 */
	private int food;
	/**
	 * 能量
	 */
	private int energy;
	
	private int rabit0Number;
	private int rabit1Number;
	private int rabit0Number2;
	private int rabit1Number2;
	private int rabit0Number3;
	private int rabit1Number3;
	private int rabit0Number4;
	private int rabit1Number4;
	
	
	private int openBlock;
	private int openBlock2;
	private int openBlock3;
	private int openBlock4;


	/**
	 * 用户语言
	 */
	private int lang;

	private BuildingList buildingList;//默认为1
	private BuildingList buildingList2;//新开的岛屿2
	private BuildingList buildingList3;
	private BuildingList buildingList4;

	private TIntObjectMap<FriendHome> friendHomeMap;

	private String housePassword;
	private Date housePasswordCreateTime;

	/**
	 * 任务列表
	 */
	private QuestList questList;
	private QuestList questList2;
	private QuestList questList3;
	private QuestList questList4;
	/**
	 * hv*100000 + lv
	 */
	private int clientVersionIntValue;
	
	public final static int CLIENT_VERSION_GM_LOW = 99 * 100000 + 99;
	private static final String POOL_KEY_LOCALTIME_DIFF = "pool_key_localtime_diff";
	private static final String POOL_KEY_LOCALTIME_DIFF_SET_DAY = "pool_key_localtime_diff_set_day";
	private static final String POOL_KEY_LOCALTIME_DIFF_SET_TIMES = "pool_key_localtime_diff_set_times";
	private static final String POOL_KEY_RECOMMENDCODE = "pool_key_recommendcode";
	private static final String POOL_KEY_RECOMMEND_NUM = "pool_key_recommend_num";
	private static final String POOL_KEY_RECOMMEND_12LV = "pool_key_recommend_12lv";
	private static int LOCAL_DIFF_MAX_TIMES = 2;
	
	//以下为新版需求添加的属性
	
	private int stone;//石头
	
	private int love;//爱心值
	/**
	 * 所有兵的总数
	 */
	private int allCatCount;
	/**
	 * 所有战斗星级的总数
	 */
	private int allBattleStarCount;
	
	private String jsonData;
	private PlayerSns playerSns;
	/**
	 * 有，需要通知(对应于下面的那些字符串定义)
	 */
	public static final int NOTIFY_CLIENT = 1;
	/**
	 * 没有，不用通知(对应于下面的那些字符串定义)
	 */
	public static final int NOT_NOTIFY_CLIENT = 0;
	/**
	 * 通知用户收件箱有新邮件
	 */
	public static final String NOTIFY_CLIENT_MAIL_INBOX = "notifyClientMailInBox";
	/**
	 * 通知用户信息箱有新邮件
	 */
	public static final String NOTIFY_CLIENT_MAIL_MESSAGEBOX = "notifyClientMailMessageBox";
	/**
	 * 通知用户有系统公告
	 */
	public static final String NOTIFY_CLIENT_NOTICE_SYSTEM = "notifyClientNoticeSystem";
	/**
	 * 通知用户有交互公告
	 */
	public static final String NOTIFY_CLIENT_NOTICE_INTERACTIVE = "notifyClientNoticeInteractive";
	
	public static final String GET_GIFT_DAY = "getGiftDay";

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getBattleTimes() {
		return battleTimes;
	}

	public int getFood() {
		return food;
	}

	public int getLang() {
		return lang;
	}

	public void setLang(int lang) {
		this.lang = lang;
	}

	

	public String getItems() {
		return items;
	}

	public void setItems(String items) {
		this.items = items;
	}

	public void setFood(int food) {
		this.food = food;
	}

	public void setBattleTimes(int battleTimes) {
		this.battleTimes = battleTimes;
	}

	public int getBattleWinTimes() {
		return battleWinTimes;
	}

	public void setBattleWinTimes(int battleWinTimes) {
		this.battleWinTimes = battleWinTimes;
	}

	public int getWoods() {
		return woods;
	}

	public void setWoods(int woods) {
		this.woods = woods;
	}
	
	
	public int getSceneOpenBlock(int sceneId) {
		switch (sceneId) {
		case 1:
			return openBlock;
		case 2:
			return openBlock2;
		case 3:
			return openBlock3;
		case 4:
			return openBlock4;
		default:
			return openBlock;
		}
	}

	public void setSceneOpenBlock(int sceneId, int openBlock) {
		switch (sceneId) {
		case 1:
			this.openBlock = openBlock;
			break;
		case 2:
			this.openBlock2 = openBlock;
			break;
		case 3:
			this.openBlock3 = openBlock;
			break;
		case 4:
			this.openBlock4 = openBlock;
			break;
		default:
			
			break;
		}
	}
	public int getSceneRabit0Number(int sceneId) {
		switch (sceneId) {
		case 1:
			return rabit0Number;
		case 2:
			return rabit0Number2;
		case 3:
			return rabit0Number3;
		case 4:
			return rabit0Number4;
		default:
			return rabit0Number;
		}
	}

	public void setSceneRabit0Number(int sceneId, int rabit0Number) {
		switch (sceneId) {
		case 1:
			this.rabit0Number = rabit0Number;
			break;
		case 2:
			this.rabit0Number2 = rabit0Number;
			break;
		case 3:
			this.rabit0Number3 = rabit0Number;
			break;
		case 4:
			this.rabit0Number4 = rabit0Number;
			break;
		default:
			
			break;
		}
	}

	public int getSceneRabit1Number(int sceneId) {
		switch (sceneId) {
		case 1:
			return rabit1Number;
		case 2:
			return rabit1Number2;
		case 3:
			return rabit1Number3;
		case 4:
			return rabit1Number4;
		default:
			return rabit1Number;
		}
	}

	public void setSceneRabit1Number(int sceneId, int rabit1Number) {
		switch (sceneId) {
		case 1:
			this.rabit1Number = rabit1Number;
			break;
		case 2:
			this.rabit1Number2 = rabit1Number;
			break;
		case 3:
			this.rabit1Number3 = rabit1Number;
			break;
		case 4:
			this.rabit1Number4 = rabit1Number;
			break;
		default:
			
			break;
		}
	}

	public int getRabit0Number() {
		return rabit0Number;
	}

	public void setRabit0Number(int rabit0Number) {
		this.rabit0Number = rabit0Number;
	}

	public int getRabit1Number() {
		return rabit1Number;
	}

	public void setRabit1Number(int rabit1Number) {
		this.rabit1Number = rabit1Number;
	}

	public int getRabit0Number2() {
		return rabit0Number2;
	}

	public void setRabit0Number2(int rabit0Number2) {
		this.rabit0Number2 = rabit0Number2;
	}

	public int getRabit1Number2() {
		return rabit1Number2;
	}

	public void setRabit1Number2(int rabit1Number2) {
		this.rabit1Number2 = rabit1Number2;
	}

	public int getRabit0Number3() {
		return rabit0Number3;
	}

	public void setRabit0Number3(int rabit0Number3) {
		this.rabit0Number3 = rabit0Number3;
	}

	public int getRabit1Number3() {
		return rabit1Number3;
	}

	public void setRabit1Number3(int rabit1Number3) {
		this.rabit1Number3 = rabit1Number3;
	}

	public int getRabit0Number4() {
		return rabit0Number4;
	}

	public void setRabit0Number4(int rabit0Number4) {
		this.rabit0Number4 = rabit0Number4;
	}

	public int getRabit1Number4() {
		return rabit1Number4;
	}

	public void setRabit1Number4(int rabit1Number4) {
		this.rabit1Number4 = rabit1Number4;
	}

	public int getOpenBlock() {
		return openBlock;
	}

	public void setOpenBlock(int openBlock) {
		this.openBlock = openBlock;
	}

	public int getOpenBlock2() {
		return openBlock2;
	}

	public void setOpenBlock2(int openBlock2) {
		this.openBlock2 = openBlock2;
	}

	public int getOpenBlock3() {
		return openBlock3;
	}

	public void setOpenBlock3(int openBlock3) {
		this.openBlock3 = openBlock3;
	}

	public int getOpenBlock4() {
		return openBlock4;
	}

	public void setOpenBlock4(int openBlock4) {
		this.openBlock4 = openBlock4;
	}
	
	public BuildingList getBuildingList() {
		return buildingList;
	}

	public void setBuildingList(BuildingList buildingList) {
		this.buildingList = buildingList;
	}

	public BuildingList getBuildingList2() {
		return buildingList2;
	}

	public void setBuildingList2(BuildingList buildingList2) {
		this.buildingList2 = buildingList2;
	}

	public BuildingList getBuildingList3() {
		return buildingList3;
	}

	public void setBuildingList3(BuildingList buildingList3) {
		this.buildingList3 = buildingList3;
	}

	public BuildingList getBuildingList4() {
		return buildingList4;
	}

	public void setBuildingList4(BuildingList buildingList4) {
		this.buildingList4 = buildingList4;
	}
	
	public QuestList getQuestList() {
		return questList;
	}

	public void setQuestList(QuestList questList) {
		this.questList = questList;
	}

	public QuestList getQuestList2() {
		return questList2;
	}

	public void setQuestList2(QuestList questList2) {
		this.questList2 = questList2;
	}

	public QuestList getQuestList3() {
		return questList3;
	}

	public void setQuestList3(QuestList questList3) {
		this.questList3 = questList3;
	}

	public QuestList getQuestList4() {
		return questList4;
	}

	public void setQuestList4(QuestList questList4) {
		this.questList4 = questList4;
	}

	public BuildingList getSceneBuildingList(int sceneId) {
		switch (sceneId) {
		case 1:
			return buildingList;
		case 2:
			return buildingList2;
		case 3:
			return buildingList3;
		case 4:
			return buildingList4;
		default:
			return null;
		}
		
	}

	public void setSceneBuildingList(int sceneId, BuildingList buildingList) {
		switch (sceneId) {
		case 1:
			this.buildingList = buildingList;
			break;
		case 2:
			this.buildingList2 = buildingList;
			break;
		case 3:
			this.buildingList3 = buildingList;
			break;
		case 4:
			this.buildingList4 = buildingList;
			break;
		default:
			
			break;
		}
		
	}
	
	public QuestList getQuestList(int sceneId) {
		switch (sceneId) {
		case 1:
			return questList;
		case 2:
			return questList2;
		case 3:
			return questList3;
		case 4:
			return questList4;
		default:
			return null;
		}
	}

	public void setQuestList(int sceneId, QuestList questList) {
		switch (sceneId) {
		case 1:
			this.questList = questList;
			break;
		case 2:
			this.questList2 = questList;
			break;
		case 3:
			this.questList3 = questList;
			break;
		case 4:
			this.questList4 = questList;
			break;
		default:
			
			break;
		}
	}


	public int getEnergy() {
		return energy;
	}

	public void setEnergy(int energy) {
		this.energy = energy;
	}

	public String getCatsInfo() {
		return catsInfo;
	}

	public void setCatsInfo(String catsInfo) {
		this.catsInfo = catsInfo;
	}
 
 
	public TIntObjectMap<FriendHome> getFriendHomeMap() {
		return friendHomeMap;
	}

	public String getHousePassword() {
		return housePassword;
	}

	public void setHousePassword(String housePassword) {
		this.housePassword = housePassword;
		this.housePasswordCreateTime = new Date();
	}

	public Date getHousePasswordCreateTime() {
		return housePasswordCreateTime;
	}

	public void setHousePasswordCreateTime(Date housePasswordCreateTime) {
		this.housePasswordCreateTime = housePasswordCreateTime;
	}

	/**
	 * 注意: 建筑数据, 需要额外调用<br>
	 * <blockquote>
	 * 
	 * <pre>
	 * scene.put(&quot;player&quot;, player.toClientData());
	 * if (buildingList != null) {
	 * 	scene.put(&quot;Buildings&quot;, buildingList.toJsonString());
	 * } else {
	 * 	scene.put(&quot;Buildings&quot;, &quot;[]&quot;);
	 * }
	 * </pre>
	 * 
	 * "Energy":5,"Exp":16,"Food":37,
	 * 
	 * "Items":"8=32,18=10,19=14,21=5,38=6,49=4,62=9,79=1,83=24,",
	 * 
	 * "Level":7,"Money":710501,
	 * 
	 * "Nickname":"123123","OpenBlock":1,"Rabit0Number":5,"Rabit1Number":3,
	 * "Woods":670,"icon":1,"lang":1,"star":1, </blockquote>
	 */
//	public JSONObject toClientDataInfo(int sceneId) {
//		JSONObject temp = new JSONObject();
//		temp.put("BattleTimes", this.getBattleTimes());
//		temp.put("BattleWinTimes", this.getBattleWinTimes());
//		temp.put("Energy", this.getEnergy());
//		temp.put("Exp", this.getExp());
//		temp.put("Food", this.getFood());
//		temp.put("Items", this.getItems());
//		temp.put("Level", this.getLevel());
//		temp.put("Money", this.getMoney());
//		temp.put("Nickname", this.getName());
//		temp.put("OpenBlock", this.getSceneOpenBlock(sceneId));
//		temp.put("Rabit0Number", this.getSceneRabit0Number(sceneId));
//		temp.put("Rabit1Number", this.getSceneRabit1Number(sceneId));
//		temp.put("Woods", this.getWoods());
//		temp.put("raceId", this.getRaceId());
//		temp.put("rich", this.getRich());
//		temp.put("stone", this.getStone());
//		temp.put("love", this.getLove());
//		int iconNum = 1;
//		try {
//			iconNum = Integer.parseInt(this.getIcon());
//		} catch (NumberFormatException e) {
//		}
//		temp.put("icon", iconNum);
//		temp.put("lang", this.getLang());
//		temp.put("star", this.getStar());
//
//		JSONArray catsInfo = JSONArray.fromObject(this.getCatsInfo() == null ? "[]" : this.getCatsInfo());
//		temp.put("Cats", catsInfo);
//		
//		temp.put("TrainCats", this.getTrainCats());
//		temp.put("BattleWin", this.getBattleWin());
//
//		JSONObject verify = this.getAccount().toVerifyJson();
//		temp.put("verify", verify);
//		return temp;
//	}

	public FriendHome getFriendHome(int friendId) {
		if (friendHomeMap == null) {
			friendHomeMap = new TIntObjectHashMap<FriendHome>();
			List<FriendHome> friendHomeList = Platform.getEntityManager().query("from FriendHome where playerId = ?",
					this.getInstanceId());
			if (friendHomeList != null) {
				for (FriendHome friendHome : friendHomeList) {
					friendHomeMap.put(friendHome.getFriendId(), friendHome);
				}
			}
		}
		if (friendHomeMap.containsKey(friendId)) {
			return friendHomeMap.get(friendId);
		} else {
			FriendHome friendHome = new FriendHome(this.getInstanceId(), friendId);
			friendHomeMap.put(friendId, friendHome);
			return friendHome;
		}
	}

	public JSONObject toFriendViewClientData(int sceneId) {
		JSONObject temp = new JSONObject();
		// temp.put("id", this.getId());
		// temp.put("nickname", this.getName() == null ? "" : this.getName());
		// temp.put("icon", this.getIcon() == null ? "" : this.getIcon());
		// temp.put("Exp", this.getExp());
		// temp.put("Level", this.getLevel());
		// temp.put("Money", this.getMoney());
		temp.put("RabitNumber", this.getSceneRabit0Number(sceneId) + this.getSceneRabit1Number(sceneId));
		if(sceneId == 1) {
			JSONArray catsInfo = JSONArray.fromObject(this.getCatsInfo());
			temp.put("Cats", catsInfo);
		}else {
			temp.put("Cats", new JSONArray());
		}
		// temp.put("Woods", this.getWoods());
		return temp;
	}

	public void setDefaultPlayerInfo() {
		this.setExp(0);
		this.setLevel(1);
		this.setMoney(50000);
		this.setBattleTimes(0);
		this.setBattleWinTimes(0);
		this.setSceneRabit0Number(1, 6);
		this.setCatsInfo("[{\"count\":0,\"id\":12}]");
		this.setWoods(100);
		this.setFood(100);
		this.getAccount().setImoney(20);
		// this.getAccount().setTotalImoney(200);
	}

//	public int getProcessSaveTime() {
//		return this.getPool().getInt(POOL_KEY_PROCESS_SAVE_TIME_SED, 0);
//	}
//
//	public int setProcessSaveTime(int saveTime) {
//		return this.getPool().setInt(POOL_KEY_PROCESS_SAVE_TIME_SED, saveTime);
//	}

//	public void setMCount(int mCount) {
//		this.getPool().setInt(POOL_KEY_PROCESS_MCOUNT, mCount);
//	}
//
//	public int getMCount() {
//		return this.getPool().getInt(POOL_KEY_PROCESS_MCOUNT, 0);
//	}

//	public void setTotalMoney(long totalMoney) {
//		this.getPool().setLong(POOL_KEY_PROCESS_TOTAL_MONEY, totalMoney);
//	}
//	
//	public long getTotalMoney() {
//		return this.getPool().getLong(POOL_KEY_PROCESS_TOTAL_MONEY, 0L);
//	}

	/**
	 * 1.0.73.72.x
	 * =73*100000|72
	 * @return
	 */
	public int getClientVersionIntValue() {
		return this.clientVersionIntValue;
	}
	public void setClientVersionIntValue(int hv, int lv) {
		this.clientVersionIntValue = hv * 100000 + lv;
	}
	
	public String getclientVersionStrIntValue() {
		return this.clientVersionIntValue /100000 + "." + (short)this.clientVersionIntValue;
	}

	public int getLocalTimeDiff() {
		return this.getPool().getInt(POOL_KEY_LOCALTIME_DIFF, 0);
	}
	
	public void setLocalTimeDiff(int diff) {
		if (getLocalTimeDiff() != diff) {
			this.getPool().setInt(POOL_KEY_LOCALTIME_DIFF, diff);
			Calendar calendar = Calendar.getInstance();
			this.getPool().setInt(POOL_KEY_LOCALTIME_DIFF_SET_DAY, calendar.get(Calendar.DAY_OF_YEAR));
			int times = this.getPool().getInt(POOL_KEY_LOCALTIME_DIFF_SET_TIMES, 0);
			this.getPool().setInt(POOL_KEY_LOCALTIME_DIFF_SET_TIMES, ++times);
		}
	}

	public boolean canResetLocalDiffTime() {
		int day = this.getPool().getInt(POOL_KEY_LOCALTIME_DIFF_SET_DAY, 0);
		Calendar calendar = Calendar.getInstance();
		if(calendar.get(Calendar.DAY_OF_YEAR) != day) {
			return true;
		}else {
			int times = this.getPool().getInt(POOL_KEY_LOCALTIME_DIFF_SET_TIMES, 0);
			if(times > LOCAL_DIFF_MAX_TIMES) {
				return false;
			}
		}
		return true;
	}

	public int getToDayChangeTimes() {
		int day = this.getPool().getInt(POOL_KEY_LOCALTIME_DIFF_SET_DAY, 0);
		Calendar calendar = Calendar.getInstance();
		if(calendar.get(Calendar.DAY_OF_YEAR) != day) {
			return 0;
		}else {
			int times = this.getPool().getInt(POOL_KEY_LOCALTIME_DIFF_SET_TIMES, 0);
				return times;
		}
	}

	public int getRecommendCode() {
		return this.getPool().getInt(POOL_KEY_RECOMMENDCODE);
	}

	public int getRecommendNum() {
		return this.getPool().getInt(POOL_KEY_RECOMMEND_NUM, 0);
	}

	public void setRecommendNum(int num) {
		this.getPool().setInt(POOL_KEY_RECOMMEND_NUM, num);
	}

	public void setRecommendCode(int recommendPlayerId) {
		this.getPool().setInt(POOL_KEY_RECOMMENDCODE, recommendPlayerId);
	}

	public int getBerecommend12LvId() {
		return this.getPool().getInt(POOL_KEY_RECOMMEND_12LV);
	}

	public boolean addBerecommend12LvPlayerId(int recommendPlayerId) {
		int src = this.getPool().getInt(POOL_KEY_RECOMMEND_12LV);
		if(src == 0) {
			this.getPool().setInt(POOL_KEY_RECOMMEND_12LV, recommendPlayerId);
			return true;
		}else {
			return false;
		}
		
	}

	public void resetBerecommend12LvId() {
		this.getPool().setInt(POOL_KEY_RECOMMEND_12LV, 0);
	}
	
//	public void setTrainCats(String trainCats){
//		this.getPool().setString(POOL_KEY_TRAINCATS, trainCats);
//	}
//	
//	public String getTrainCats(){
//		return this.getPool().getString(POOL_KEY_TRAINCATS);
//	}
	
	public void setBattleWin(String battleWin){
		this.getPool().setString(POOL_KEY_BATTLEWIN, battleWin);
		if(battleWin == null || battleWin == "" || battleWin.equals("")){
			setAllBattleStarCount(0);
			return;
		}
		int count = 0;
		String[] readeds = battleWin.split(",");
		for (String rid : readeds) {
			String[] values = rid.split("=");
			count += Integer.parseInt(values[1]);
		}
		setAllBattleStarCount(count);
	}
	
	public String getBattleWin(){
		return this.getPool().getString(POOL_KEY_BATTLEWIN);
	}
//	public void setStepData(String stepData){
//		this.getPool().setString(POOL_KEY_GUIDE_DATA, stepData);
//	}
//	public String getStepData(){
//		return this.getPool().getString(POOL_KEY_GUIDE_DATA);
//	}
	public int getStone() {
		return stone;
	}

	public void setStone(int stone) {
		this.stone = stone;
	}

	public int getLove() {
		return love;
	}

	public void setLove(int love) {
		this.love = love;
	}
	/**
	 * 设置公告未读
	 * @param id
	 */
	public void setNoticeUnReaded(int id) {
		String readedSrc = this.getPool().getString(Player.POOL_KEY_SYSTEM_NOTICE_READED);
		String[] readeds;
		if (readedSrc.equals("")) {
			readeds = new String[0];
		} else {
			readeds = readedSrc.split(",");
		}
		int i = 0;
		for (String rid : readeds) {
			if (rid.equals(String.valueOf(id))) {
				readeds[i] = "";
				break;
			}
			i++;
		}
		readedSrc = "";
		i = 0;
		for (String rid : readeds) {
			if(rid == ""){
				continue;
			}
			if(i == 0){
				readedSrc += rid;
			}else{
				readedSrc += "," + rid;
			}
			i++;
		}
		this.getPool().setString(Player.POOL_KEY_SYSTEM_NOTICE_READED, readedSrc);
		this.notifySave();
	}

	public int getAllCatCount() {
		return allCatCount;
	}

	public void setAllCatCount(int allCatCount) {
		this.allCatCount = allCatCount;
	}

	public int getAllBattleStarCount() {
		return allBattleStarCount;
	}

	public void setAllBattleStarCount(int allBattleStarCount) {
		this.allBattleStarCount = allBattleStarCount;
	}
	/**
	 * 用于打日志用的，保证player当时的数据不会被改变
	 */
	public PlayerEx clone(){
		PlayerEx player = new PlayerEx();
		Account account = new Account();
		account.setId(this.getAccount().getId());
		//account.setMid(this.getAccount().getMid());
		account.setImoney(this.getAccount().getImoney());
		account.setInitDollar(this.getAccount().getInitDollar());
		account.setRewardDollar(this.getAccount().getRewardDollar());
		account.setUsedDollar(this.getAccount().getUsedDollar());
		account.setCompensateDollar(this.getAccount().getCompensateDollar());
		player.setAccount(account);
		player.setId(this.getId());
		player.setName(this.getName());
		player.setLevel(this.getLevel());
		player.setMoney(this.getMoney());
		player.setBattleWin(this.getBattleWin());
		player.setSceneRabit0Number(1,this.getSceneRabit0Number(1));
		player.setSceneRabit1Number(1, this.getSceneRabit1Number(1));
		//player.setSceneBuildingList(1, this.getSceneBuildingList(1));
		player.setJsonData(this.getJsonData());
		return player;
	}
	
	public String getJsonData() {
		return jsonData;
	}

	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}

	public PlayerSns getPlayerSns() {
		return playerSns;
	}

	public void setPlayerSns(PlayerSns playerSns) {
		this.playerSns = playerSns;
	}
}
