package cyou.mrd;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.data.DataCenter;
import cyou.mrd.event.EventManager;
import cyou.mrd.game.Server;
import cyou.mrd.io.tcp.connector.single.SingleClient;
import cyou.mrd.persist.EntityManager;
import cyou.mrd.thread.ThreadMonitor;
import cyou.mrd.updater.Updater;
import cyou.mrd.util.ThreadPool;

/**
 * 平台类, 根.
 * 
 * @author miaoshengli
 */
public class Platform {
	/**
	 * 服务器负载人数限度
	 * 值应该以性能为参考.不应该配置化
	 */
	protected static final Logger logger = LoggerFactory.getLogger(Platform.class);
	
	private static final int SERVER_OBJECT_ACCESSOR_SIZE = 300000000;
	
	static AppContext appContext;
	// 持久层
	static EntityManager entityManager;

	// 事件管理
	static EventManager eventManager;

	// 更新器
	static Updater updater;

	// 服务器配置
	static XMLConfiguration configuration;

	static DataCenter dataCenter;

	//作为gameserer时需要连world
	private static SingleClient worldServer;
	
	//作为world时 需要连billing
	private static SingleClient billingServer;

	static int serverId;
	
	static String gameId;
	
	static String gameCode;
	
	static int defaultLanguage;

	static ThreadPool threadPool;

	static LogService log;
	
	static  ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
	
	static ThreadMonitor threadMonitor;
	
	public static LogService getLog() {
		return log;
	}

	public static void setLog(LogService log) {
		Platform.log = log;
	}

	public static EntityManager getEntityManager() {
		return entityManager;
	}

	public static void setEntityManager(EntityManager entityManager) {
		Platform.entityManager = entityManager;
	}

	public static EventManager getEventManager() {
		return eventManager;
	}

	public static void setEventManager(EventManager eventManager) {
		Platform.eventManager = eventManager;
	}

	public static Updater getUpdater() {
		return updater;
	}

	public static void setUpdater(Updater updater) {
		Platform.updater = updater;
	}

	public static AppContext getAppContext() {
		return appContext;
	}

	public static void setAppContext(AppContext context) {
		appContext = context;
	}

	public static void setConfiguration(XMLConfiguration configuration) {
		Platform.configuration = configuration;
	}

	public static XMLConfiguration getConfiguration() {
		return Platform.configuration;
	}

	public static void shutdown() {
		appContext.shutdown();
		Server.running = false; 
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			logger.error("InterruptedException",e);
		}
	}

	public static DataCenter dataCenter() {
		return dataCenter;
	}

	public static void setDataCenter(DataCenter dc) {
		dataCenter = dc;
	}

	public static SingleClient worldServer() {
		return worldServer;
	}

	public static void setWorldServer(SingleClient ws) {
		worldServer = ws;
	}

	public static int getServerId() {
		return serverId;
	}

	public static void setServerId(int sid) {
		serverId = sid;
	}

	public static String getGameId() {
		return gameId;
	}

	public static void setGameId(String gameId) {
		Platform.gameId = gameId;
	}

	static Server server;

	private static String webRootPath;

	public static Server getServer() {
		return server;
	}

	public static void setServer(Server gameServer) {
		server = gameServer;
	}

	public static ScheduledExecutorService getScheduler(){
		return scheduler;
	}

	public static ThreadPool getThreadPool() {
		return threadPool;
	}

	public static void setThreadPool(ThreadPool threadPool) {
		Platform.threadPool = threadPool;
	}

	public static boolean willBomb() {
		return ObjectAccessor.size() > SERVER_OBJECT_ACCESSOR_SIZE;
	}

	public static String getGameCode() {
		return Platform.gameCode;
	}
	public static void setGameCode(String code) {
		Platform.gameCode = code;
	}

	public static ThreadMonitor getThreadMonitor() {
		return threadMonitor;
	}

	public static void setThreadMonitor(ThreadMonitor threadMonitor) {
		Platform.threadMonitor = threadMonitor;
	}

	public static void setWebRootPath(String realPath) {
		Platform.webRootPath = realPath;
	}
	
	public static String getWebRootPath() {
		return Platform.webRootPath;
	}

	public static int getDefaultLanguage() {
		return defaultLanguage;
	}

	public static void setDefaultLanguage(int defaultLanguage) {
		Platform.defaultLanguage = defaultLanguage;
	}

	public static SingleClient getBillingServer() {
		return billingServer;
	}

	public static void setBillingServer(SingleClient billingServer) {
		Platform.billingServer = billingServer;
	}

		
}
