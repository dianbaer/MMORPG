package cyou.mrd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.entity.Player;
import cyou.mrd.event.Event;
import cyou.mrd.event.GameEvent;
import cyou.mrd.event.OPEvent;
import cyou.mrd.io.OPHandler;
import cyou.mrd.io.http.SessionManager;
import cyou.mrd.keyword.KeyWordService;
import cyou.mrd.service.Service;
import cyou.mrd.util.Time;
import cyou.mrd.util.Utils;

/**
 * 日志
 * 
 * @author pmeng
 */
@OPHandler(TYPE = OPHandler.EVENT)
public class LogService implements Service {

	private static final Logger log = LoggerFactory.getLogger(LogService.class);

	private static String logDir = "";

	private static File logFile;

	private LogQueue logqueue = new LogQueue();
	// 登录
	public static final String TYPE_LOGIN = "login.log.";
	// 付费
	public static final String TYPE_PAY = "recharge.log.";
	// 消费钻石
	public static final String TYPE_DIAMOND = "diamond.log.";
	// 角色级别统计
	public static final String TYPE_ROLEUPLEVEL = "role.log.";
	// 玩家财富
	public static final String TYPE_FORTUNE = "fortune.log.";
	// 玩家成就
	public static final String TYPE_ACHIEVE = "achieve.log.";
	// 游戏内商品销售信息
	public static final String TYPE_COMMODITY = "commodity.log.";
	// 微博转发信息统计
	public static final String TYPE_WEIBO = "weibo.log.";
	// 好友访问信息统计
	public static final String TYPE_FRIEND = "friend.log.";
	// 小游戏信息统计
	public static final String TYPE_APPLET = "applet.log.";
	// 总注册用户付费转化率
	public static final String TYPE_EVOLUTION = "evolution.log.";

	// 服务器运行日志
	public static final String TYPE_CATALINA = "catalina.log.";

	// 服务器运行日志
	public static final String TYPE_CHARGE = "sys_charge.log.";

	// 注册日志
	public static final String TYPE_REGISTER = "register.log.";

	// 同时在线用户
	public static final String TYPE_ONLINE = "online.log.";

	public static final String TYPE_ERROR = "error.log.";

	//行为统计日志
	public static final String TYPE_BEHAVIOR = "behavior.log.";
	//搜索好友日志
	public static final String TYPE_SEARCH_FRIEND = "searchfriend.log.";
	//用户详细信息日志
	public static final String TYPE_USER = "user.log.";
	//世界经济日志
	public static final String TYPE_ECONOMIC = "economic.log.";
	//sns bind日志
	public static final String TYPE_SNS = "weibo.log.";
	
	
	private ConcurrentHashMap<String, Log> logs = new ConcurrentHashMap<String, Log>();

	public static boolean isNeedLogOnline = true;

	public String getId() {
		return "logService";
	}

	public void startup() throws Exception {
		logDir = Platform.getConfiguration().getString("logdir");
		logFile = new File(logDir);
		logFile.mkdir();
		registerLoggers();
		initDayLogger();
		Platform.setLog(this);
		Platform.getScheduler().scheduleAtFixedRate(logqueue, 30, 10, TimeUnit.MILLISECONDS);
		if (isNeedLogOnline) {
			Platform.getScheduler().scheduleAtFixedRate(new OnlineLog(), 10, 10, TimeUnit.MINUTES);
		}
	}

	public void shutdown() throws Exception {

	}

	@OPEvent(eventCode = GameEvent.EVENT_CHANGE_DAY)
	public void changeDay(Event event) {
		log.info("LogService: handler change day event");
		logEvolution();
		initDayLogger();
	}

	/**
	 * 总注册用户付费转化率信息统计
	 */
	private void logEvolution() {
		long evolutionTotal = Platform.getEntityManager().count("select count(*) from Account where imoney > 0");
		long totle = Platform.getEntityManager().count("select count(*) from Account");
		evolution(evolutionTotal, totle);
	}

	// 注册所有类型的日志
	protected void registerLoggers() {
		registerLogger(TYPE_LOGIN, true, true);
		registerLogger(TYPE_PAY, true, true);
		registerLogger(TYPE_DIAMOND, true, true);
		registerLogger(TYPE_ROLEUPLEVEL, true, true);
		registerLogger(TYPE_FORTUNE, true, true);
		registerLogger(TYPE_ACHIEVE, true, true);
		registerLogger(TYPE_COMMODITY, true, true);
		registerLogger(TYPE_FRIEND, true, true);
		registerLogger(TYPE_APPLET, true, true);
		registerLogger(TYPE_EVOLUTION, true, false);
		registerLogger(TYPE_CATALINA, true, true);
		registerLogger(TYPE_CHARGE, true, true);
		registerLogger(TYPE_REGISTER, true, true);
		registerLogger(TYPE_ONLINE, true, true);
		registerLogger(TYPE_BEHAVIOR, true, true);
		registerLogger(TYPE_SEARCH_FRIEND,true,true);
		registerLogger(TYPE_USER,true,true);
		registerLogger(TYPE_ECONOMIC,true,true);
		registerLogger(TYPE_SNS,true,true);
	}

	// 初始化所有每日所有日志文件
	protected void initDayLogger() {
		for (Log log : logs.values()) {
			if (log.isNeedChangeDay) {
				log.initDayLogFile();
			}
		}
	}

	class OnlineLog implements Runnable {
		public void run() {
			online();
		}
	}

	class LogQueue implements Runnable {
		@Override
		public void run() {
			try {
				if (logFile == null)
					return;
				for (Log log : logs.values()) {
					if (log.isNeedRun)
						log.printLog();
				}
			} catch (Throwable e) {
				log.error(e.getMessage());
			}
		}
	}

	protected void registerLogger(String prefix, boolean isNeedChangeDay, boolean isNeedRun) {
		Log log = new Log(prefix, isNeedChangeDay, isNeedRun);
		logs.put(prefix, log);
	}

	public void printLog(String logType, String logmsg) {
		Log log = logs.get(logType);
		if (log != null) {
			log.addLog(logmsg);
		}
	}

	public void printLogForce(String logType, String logmsg) {
		Log log = logs.get(logType);
		if (log != null) {
			log.addLogAndPrint(logmsg);
		}
	}

	protected void printLogOnDay(String logType, String logmsg, Date date) {
		Log log = logs.get(logType);
		if (log != null) {
			log.addLog(logmsg);
			log.printLogOnDay(date);
		}
	}

	class Log {

		// 是否换天
		boolean isNeedChangeDay;

		// 是否实时打印(有些类型日志的打印是事件 或 其他方式驱动的 不需要update调用)
		boolean isNeedRun;

		// 日志消息队列
		ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();
		// 日志文件
		File logFile;
		// 日志前缀名
		String prefix;

		String createDayString;

		// 日志文件Map
		Map<String, File> logFiles = new HashMap<String, File>();

		public Log(String prefix, boolean isNeedChangeDay, boolean isNeedRun) {
			this.prefix = prefix;
			this.isNeedChangeDay = isNeedChangeDay;
			this.isNeedRun = isNeedRun;
			if (!isNeedChangeDay) {
				logFile = new File(logDir, prefix);
				try {
					logFile.createNewFile();
				} catch (IOException e) {
					log.error("create log file error:", e);
				}
				log.info("init logFile, dir:{},name:{}", logFile.getAbsolutePath(), logFile.getName());
			}
		}

		public void initDayLogFile() {
			Date date = new Date(Time.currTime);
			createDayString = Utils.getDateString(date);
			File logFile = new File(logDir, prefix + Utils.getDayString(date));
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				log.error("create log file error:", e);
			}
			logFiles.put(Utils.getDayString(date), logFile);
			log.info("init logFile, dir:{},name:{}", logFile.getAbsolutePath(), logFile.getName());
		}

		// 向日志队列中加入一条日志信息
		public void addLog(String logmsg) {
			queue.add(logmsg);
		}

		// 向日志队列中加入一条日志信息 并强制打印
		public void addLogAndPrint(String logmsg) {
			addLog(logmsg);
			printLog();
		}

		// 将日志打印到指定天的日志文件中
		public void printLogOnDay(Date date) {
			if (queue.size() <= 0)
				return;
			logFile = logFiles.get(Utils.getDayString(date));
			printLogNormal();
		}

		// 将日志打印到当前日期的文件中
		public void printLog() {
			if (queue.size() <= 0)
				return;
			logFile = logFiles.get(Utils.getDayString(new Date(Time.currTime)));
			printLogNormal();
		}

		// 打印日志
		public void printLogNormal() {
			if (logFile == null)
				return;
			FileWriter mWriter = null;
			try {
				mWriter = new FileWriter(logFile, true);
				while (!queue.isEmpty()) {
					String msg = queue.poll();
					log.info(msg);
					if (msg == null || msg == "")
						continue;
					mWriter.write(msg, 0, msg.length());
					mWriter.write("\r\n");
				}
			} catch (FileNotFoundException e) {
				// 文件没有找到, 可能是文件路径被破坏了.
			} catch (IOException e) {
				log.error("IOException", e);
			} finally {
				if (mWriter != null) {
					try {
						mWriter.flush();
						mWriter.close();
					} catch (IOException e) {
						log.error("IOException", e);
					}
				}
			}
		}

	}

	// 通过类型查找对应的id
	public int getIdfoByType(String type) {
		return Platform.getAppContext().get(KeyWordService.class).getKeyByWord(type);
	}

	// 测试用 每种日志打印1000条
	public void testLog(Player p) {
		long time = System.currentTimeMillis();
		for (int i = 0; i < 1; i++) {
			logPlayerLogin(p);
			reCharge(p, 443);
			diamond(p, 534, "购买初级魔法屋");
			role(p);
			fortune(p, "钻石", 69);
			commodity(p, "初级魔法屋", "建造", 3,2);
			// weibo(p,"推荐好友");
			friend(p, 59);
			applet(p, "吸血鬼的那点事");
			evolution(43234, 534264253);
		}
	}

	/**************************************** 日志格式 ****************************************************************************/
	/**
	 * 登录
	 */
	public void logPlayerLogin(Player p) {
		Date date = new Date();
		StringBuilder sb = new StringBuilder();
		sb.append(Utils.getDateString(date)).append(",login,").append(this.gameInfoNeedPlayer(p) + ",").append(this.clientInfo(p));
		printLog(TYPE_LOGIN, sb.toString());
	}

	/**
	 * 充值
	 * 
	 * @param sellAmount
	 *            销售金额
	 * @param rechargeAmount
	 *            充值金额
	 */
	public void reCharge(Player p, int rechargeAmount) {
		Date date = new Date();
		StringBuilder sb = new StringBuilder();
		sb.append(Utils.getDateString(date)).append(",recharge,").append(this.gameInfoNeedPlayer(p) + ",").append(rechargeAmount + ",")
				.append(this.clientInfo(p));
		//新需求    增加ip还有用户级别
		sb.append("," + p.getSession().ip() + "," + p.getLevel());
		printLog(TYPE_PAY, sb.toString());
	}

	/**
	 * 消费钻石
	 * 
	 * @param amount
	 *            消费数
	 * @param usageId
	 *            消费用途id
	 * @param usage
	 *            消费用途
	 */
	public void diamond(Player p, int amount, String usage) {
		Date date = new Date();
		StringBuilder sb = new StringBuilder();
		sb.append(Utils.getDateString(date)).append(",diamond,").append(this.gameInfoNeedPlayer(p) + ",").append(amount + ",")
				.append(getIdfoByType(usage) + ",").append(usage + ",").append(this.clientInfo(p));
		printLog(TYPE_DIAMOND, sb.toString());
	}

	/**
	 * 玩家升级
	 * 
	 * @param p
	 * @param level
	 *            角色等级
	 */
	public void role(Player p) {
		Date date = new Date();
		StringBuilder sb = new StringBuilder();
		sb.append(Utils.getDateString(date)).append(",role,").append(this.gameInfoNeedPlayer(p) + ",").append(p.getLevel() + ",")
				.append(this.clientInfo(p));
		printLog(TYPE_ROLEUPLEVEL, sb.toString());
	}

	/**
	 * 玩家财富统计
	 * 
	 * @param typeId
	 *            财富类型id
	 * @param type
	 *            财富类型
	 * @param amount
	 *            财富数
	 */
	public void fortune(Player p, String type, int amount) {
		Date date = new Date();
		StringBuilder sb = new StringBuilder();
		sb.append(Utils.getDateString(date)).append(",fortune,").append(this.gameInfoNeedPlayer(p) + ",").append(getIdfoByType(type) + ",")
				.append(type + ",").append(amount + ",").append(this.clientInfo(p));
		printLog(TYPE_FORTUNE, sb.toString());
	}

	/**
	 * 游戏内商品销售信息统计
	 * 
	 * @param name
	 *            商品名称
	 * @param usageId
	 *            商品用途id
	 * @param usage
	 *            商品用途
	 * @param amount
	 *            购买数量
	 * @param commodityType
	 * 			 1:付费商品	2：免费商品
	 */
	public void commodity(Player p, String name, String usage, int amount,int commodityType) {
		Date date = new Date();
		StringBuilder sb = new StringBuilder();
		sb.append(Utils.getDateString(date)).append(",commodity,").append(this.gameInfoNeedPlayer(p) + ",")
				.append(getIdfoByType(name) + ",").append(name + ",").append(getIdfoByType(usage) + ",").append(usage + ",")
				.append(amount + ",").append(this.clientInfo(p));
		sb.append("," + commodityType);
		printLog(TYPE_COMMODITY, sb.toString());
	}

	/**
	 * 游戏内商品销售信息统计
	 * 
	 * @param name
	 *            商品名称
	 * @param usageId
	 *            商品用途id
	 * @param usage
	 *            商品用途
	 * @param amount
	 *            购买数量
	 */
	public void commodity(long time, Player p, String name, String usage, int amount,int commodityType) {
		Date date = new Date(time);
		StringBuilder sb = new StringBuilder();
		sb.append(Utils.getDateString(date)).append(",commodity,").append(this.gameInfoNeedPlayer(p) + ",")
				.append(getIdfoByType(name) + ",").append(name + ",").append(getIdfoByType(usage) + ",").append(usage + ",")
				.append(amount + ",").append(this.clientInfo(p));
		sb.append("," + commodityType);
		printLog(TYPE_COMMODITY, sb.toString());
	}

	/**
	 * 微博转发信息统计
	 * 
	 * @param reasonId
	 *            转发原因id
	 * @param reason
	 *            转发原因
	 */
	public void weibo(Player p, String reason) {
		Date date = new Date();
		StringBuilder sb = new StringBuilder();
		sb.append(Utils.getDateString(date)).append(",weibo,").append(this.gameInfoNeedPlayer(p) + ",").append(getIdfoByType(reason) + ",")
				.append(reason + ",").append(this.clientInfo(p));
		printLog(TYPE_WEIBO, sb.toString());
	}

	/**
	 * 好友访问信息统计
	 * 
	 * @param friendId
	 *            受访者唯一码
	 */
	public void friend(Player p, int friendId) {
		Date date = new Date();
		StringBuilder sb = new StringBuilder();
		sb.append(Utils.getDateString(date)).append(",friend,").append(this.gameInfoNeedPlayer(p) + ",").append(friendId + ",")
				.append(this.clientInfo(p));
		printLog(TYPE_FRIEND, sb.toString());
	}

	/**
	 * 小游戏信息统计
	 * 
	 * @param appletId
	 *            小游戏id appletName 小游戏名称
	 */
	public void applet(Player p, String appletName) {
		Date date = new Date();
		StringBuilder sb = new StringBuilder();
		sb.append(Utils.getDateString(date)).append(",applet,").append(this.gameInfoNeedPlayer(p) + ",")
				.append(getIdfoByType(appletName) + ",").append(appletName + ",").append(this.clientInfo(p));
		printLog(TYPE_APPLET, sb.toString());
	}

	/**
	 * 小游戏信息统计
	 * 
	 * @param appletId
	 *            小游戏id appletName 小游戏名称
	 */
	public void applet(long time, Player p, String appletName) {
		Date date = new Date(time);
		StringBuilder sb = new StringBuilder();
		sb.append(Utils.getDateString(date)).append(",applet,").append(this.gameInfoNeedPlayer(p) + ",")
				.append(getIdfoByType(appletName) + ",").append(appletName + ",").append(this.clientInfo(p));
		printLog(TYPE_APPLET, sb.toString());
	}

	/**
	 * 总注册用户付费转化率信息统计
	 */
	public void evolution(long totalConsumers, long totalRegisteredUsers) {
		StringBuilder sb = new StringBuilder();
		sb.append(logs.get(TYPE_EVOLUTION).createDayString).append(",evolution,").append(Platform.getGameId() + ",")
				.append(Platform.getGameCode() + ",").append(totalConsumers + ",").append(totalRegisteredUsers);
		long yestday = new Date().getTime() - (24 * 60 * 60 * 1000L);
		printLogOnDay(TYPE_EVOLUTION, sb.toString(), new Date(yestday));
	}

	private String gameInfoNeedPlayer(Player player) {
		// gameId(游戏Id),game(游戏名称),serverId(服务器唯一码),clientId(客户端唯一码),account(帐号)
		StringBuilder sb = new StringBuilder();
		sb.append(Platform.getGameId()).append(",").append(Platform.getGameCode()).append(",").append(player.getId()).append(",").append(player.getAccount().getMid())
		.append(",").append(player.getAccountId());
		return sb.toString();
	}

	private String clientInfo(Player player) {
		// areaId(登录地区id),area(登录地区名称),countryId(登录国家id),country(登录国家名称),deviceId(登录设备id),device(登录设备名称),deviceSystemId(系统类型id),deviceSystem(登录系统名称),downloadTypeId(下载类型id),downloadType(下载类型),networkTypeId(联网类型id),networkType(联网类型名称),prisonBreak(是否越狱
		// 是=1 否=0),operatorId(运营商id),operator(运营商名称)
		return getIdfoByType(player.getArea()) + "," + player.getArea() + "," + getIdfoByType(player.getCountry()) + ","
				+ player.getCountry() + "," + getIdfoByType(player.getDevice()) + "," + player.getDevice() + ","
				+ getIdfoByType(player.getDeviceSystem()) + "," + player.getDeviceSystem() + "," + getIdfoByType(player.getDownloadType())
				+ "," + player.getDownloadType() + "," + getIdfoByType(player.getNetworkType()) + "," + player.getNetworkType() + ","
				+ player.getPrisonBreak() + "," + getIdfoByType(player.getOperator()) + "," + player.getOperator();
	}

	public void logoutLog(Player p) {
		Date date = new Date();
		StringBuilder sb = new StringBuilder();
		sb.append(Utils.getDateString(date)).append(
				",logout,loginTime(" + Utils.getDateString(p.getLastLoginTime()) + "),startTime("
						+ Utils.getDateString(p.getLastLoginTime()) + "),endTime(" + Utils.getDateString(date) + "),roleid(" + p.getId()
						+ "),roleName(" + replaceSep(p.getName()) + "),roleClass(" + p.getLevel() + "),groupId(" + Platform.getServerId() + ")");
		printLog(TYPE_LOGIN, sb.toString());
	}

	public void consumeLog(Player p, long time, int money, String name) {
		Date date = new Date();
		replaceSep(name);
		StringBuilder sb = new StringBuilder();
		sb.append(Utils.getDateString(date)).append(
				",consume,buyTime(" + Utils.getDateString(new Date(time)) + "),roleid(" + p.getId() + "),roleName(" + replaceSep(p.getName())
						+ "),roleClass(" + p.getLevel() + "),groupId(" + Platform.getServerId() + "),jewel(" + money + "),weaponName("
						+ name + ")");
		printLog(TYPE_PAY, sb.toString());
	}

	// 注册时间戳,协议号,gameId(游戏Id),game(游戏名称),userId(用户id，可以唯一标识用户),account(帐号)
	public void register(Player p) {
		Date date = new Date();
		StringBuilder sb = new StringBuilder();
		sb.append(Utils.getDateString(date)).append(",register,").append(Platform.getGameId()).append(",").append(Platform.getGameCode())
				.append(",").append(p.getInstanceId()).append(",").append(p.getAccount().getMid());
		sb.append(",").append(getIdfoByType(p.getCountry())).append(",").append(p.getCountry());//注册日志增加国家字段， 内部直接用+的方式背离使用StringBuilder的初衷
		printLog(TYPE_REGISTER, sb.toString());
	}

	public void online() {
		Date date = new Date();
		StringBuilder sb = new StringBuilder();
		int onlineSize = SessionManager.worldOnlineUser < ObjectAccessor.size() ? ObjectAccessor.size() : SessionManager.worldOnlineUser;
		sb.append(Utils.getMinuteString(date)).append(",online," + Platform.getGameId() + "," + Platform.getGameCode() + "," + onlineSize);
		printLog(TYPE_ONLINE, sb.toString());
	}

	public void logCharge(String msg, Object... value) {
		if (value != null && value.length > 0) {
			for (Object val : value) {
				msg = msg.replaceFirst("\\{\\}", val.toString());
			}
		}
		Date date = new Date();
		StringBuilder sb = new StringBuilder();
		sb.append(Utils.getDateString(date)).append(msg);
		printLog(TYPE_CHARGE, sb.toString());
	}

	public void logError(String msg) {
		Date date = new Date();
		StringBuilder sb = new StringBuilder();
		sb.append(Utils.getDateString(date)).append(msg);
		printLog(TYPE_ERROR, sb.toString());
	}

	public void logPlayerOp(Player player, String op, long tm, int ty) {
		String key = player.getAccount().getMid();
		String time = Utils.getDateString(new Date(tm * 1000));

		StringBuilder sb = new StringBuilder();
		sb.append(key);
		sb.append("\t");
		sb.append(time);
		sb.append("\t");
		sb.append(op);
		sb.append("\t");
		sb.append(ty);

		printLog(TYPE_BEHAVIOR, sb.toString());
	}

	/**
	 * 玩家行为统计日志
	 */
	public void logPlayerOpDra(Player player, long tm, String op, String re) {

		String key = player.getAccount().getMid();
		String time = Utils.getDateString(new Date(tm * 1000));
		StringBuilder sb = new StringBuilder();
		sb.append(key);
		sb.append("\t");
		sb.append(time);
		sb.append("\t");
		sb.append(op);
		sb.append("\t");
		sb.append(re);

		printLog(TYPE_BEHAVIOR, sb.toString());

	}
	
	/**
	 * 搜索好友统计日志
	 * @param player
	 * @param searchKey
	 */
	public void searchFriend(Player p,String searchKey){
		Date date = new Date();
		StringBuilder sb = new StringBuilder();
		sb.append(Utils.getDateString(date)).append(",searchfriend,").append(Platform.getGameId()).append(",").append(Platform.getGameCode())
				.append(",").append(p.getInstanceId()).append(",").append(p.getAccount().getMid()).append(",").append(searchKey);
		sb.append(",").append(getIdfoByType(p.getCountry())).append("," ).append(p.getCountry());//注册日志增加国家字段， 内部直接用+的方式背离使用StringBuilder的初衷
		printLog(TYPE_SEARCH_FRIEND, sb.toString());
	}
	
	/**
	 * 行为统计日志
	 * @param p
	 */
	public void behaviorLog(Player p,String behaviorName,String behaviorArgs){
		Date date = new Date();
		behaviorName = replaceSep(behaviorName);
		behaviorArgs = replaceSep(behaviorArgs);
		StringBuilder sb = new StringBuilder();
		sb.append(Utils.getDateString(date)).append(",behavior,").append(Platform.getGameId()).append(",").append(Platform.getGameCode())
				.append(",").append(p.getInstanceId()).append(",").append(p.getAccount().getMid()).append(",").append(p.getInstanceId());
		sb.append(",").append(behaviorName).append(",").append(behaviorArgs);
		sb.append(",").append(getIdfoByType(p.getCountry())).append(",").append(p.getCountry());
		printLog(TYPE_BEHAVIOR, sb.toString());
	}
	
	//接受返回值
	private String replaceSep(String str) {
		String tstr = str.replaceAll(",", "，");
		return tstr;
	}

	/**
	 * 用户详细信息日志
	 */
	public void userLog(Player p){
		Date date = new Date();
		StringBuilder sb = new StringBuilder();
		sb.append(Utils.getDateString(date)).append(",user,").append(Platform.getGameId()).append(",").append(Platform.getGameCode())
		.append(",").append(p.getInstanceId()).append(",").append(p.getAccount().getMid()).append(",").append(p.getInstanceId());
		sb.append(",").append(replaceSep(p.getName()));
		sb.append(",").append(p.getCountry());
		sb.append(",").append(p.getLang());
		sb.append(",").append(p.getLevel());
		sb.append(",").append(p.getMoney());
		sb.append(",").append(p.getAccount().getRemainDollar());
		sb.append(",").append(p.getExp());
		sb.append(",").append(Utils.getDateString(p.getCreateTime()));
		sb.append(",").append(Utils.getDateString(p.getLastLoginTime()));
		sb.append(",").append(Utils.getDateString(p.getLastSynchInfoTime()));
		sb.append(",").append(Utils.getDateString(p.getLastSynchInfoTime()));
		printLog(TYPE_USER, sb.toString());
	}
	
	/**
	 * 世界经济日志
	 * @param p
	 * @param addMoney		加金币数
	 * @param decMoney		减金币数
	 * @param addIMoney		加钻石数
	 * @param decIMoney		减钻石数
	 */
	private void economicLog(Player p,int addMoney,int decMoney,int addIMoney,int decIMoney){
		Date date = new Date();
		StringBuilder sb = new StringBuilder();
		sb.append(Utils.getDateString(date)).append(",economic,").append(Platform.getGameId()).append(",").append(Platform.getGameCode())
		.append(",").append(p.getInstanceId()).append(",").append(p.getAccount().getMid()).append(",").append(p.getInstanceId());
		sb.append(",").append(addMoney).append(",").append(decMoney).append(",").append(addIMoney).append(",").append(decIMoney);
		sb.append(",").append(getIdfoByType(p.getCountry())).append(",").append(p.getCountry());
		printLog(TYPE_ECONOMIC,sb.toString());
	}
	
	/**
	 * 世界经济日志     外部调用接口
	 * @param p
	 * @param oldMoney		之前金币数
	 * @param oldIMoney		之前钻石数
	 */
	public void economicLog1(Player p,int oldMoney,int oldIMoney){
		if(p.getMoney() != oldMoney || p.getAccount().getRemainDollar() != oldIMoney){
			int moneyAdd = 0;
			int moneyDec = 0;
			int imoneyAdd = 0;
			int imoneyDec = 0;
			if(p.getMoney() != oldMoney){
				if(p.getMoney() > oldMoney){
					moneyAdd = p.getMoney() - oldMoney;
				}else{
					moneyDec = oldMoney - p.getMoney();
				}
			}
			if(p.getAccount().getRemainDollar() != oldIMoney){
				if(p.getAccount().getRemainDollar() > oldIMoney){
					imoneyAdd = p.getAccount().getRemainDollar() - oldIMoney;
				}else{
					imoneyDec = oldIMoney - p.getAccount().getRemainDollar();
				}
			}
			economicLog(p,moneyAdd,moneyDec,imoneyAdd,imoneyDec);
		}
	}

	//2012-09-29 00:06:40,weibo,1, animalkingdom,15575,60FACD0F342E,666822,renren,abcd@renren.com
	public void logSNSBind(Player player, String type, String snsId) {
		Date date = new Date();
		StringBuilder sb = new StringBuilder();
		sb.append(Utils.getDateString(date))
		.append(",weibo,")
		.append(Platform.getGameId())
		.append(",")
		.append(Platform.getGameCode())
		.append(",")
		.append(player.getInstanceId())
		.append(",")
		.append(player.getAccount().getMid())
		.append(",")
		.append(player.getAccount().getId())
		.append(",")
		.append(type)
		.append(",")
		.append(snsId);
		printLog(TYPE_SNS,sb.toString());
	}

}
