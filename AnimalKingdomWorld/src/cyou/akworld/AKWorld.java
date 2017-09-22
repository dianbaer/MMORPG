package cyou.akworld;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tcp.ThreadService;
import cyou.mrd.DefaultAppContext;
import cyou.mrd.Platform;
import cyou.mrd.ServerStartedException;
import cyou.mrd.data.DataCenter;
import cyou.mrd.data.MemCachedDataCenter;
import cyou.mrd.event.EventManager;
import cyou.mrd.game.Server;
import cyou.mrd.io.tcp.ClientSessionUpdater;
import cyou.mrd.io.tcp.ServerClientSessionUpdater;
import cyou.mrd.packethandler.HttpHandlerDispatch;
import cyou.mrd.packethandler.HttpHandlerDispatchManager;
import cyou.mrd.packethandler.TcpHandlerDispatch;
import cyou.mrd.packethandler.TcpHandlerDispatchManager;
import cyou.mrd.updater.SimpleUpdater;
import cyou.mrd.util.Time;
import cyou.mrd.util.Utils;
import cyou.mrd.world.WorldManager;

public class AKWorld implements Runnable {
	private static Logger log = LoggerFactory.getLogger(AKWorld.class);
	private static AKWorld world = null;

	int CYCLE_TIME = 80;
	long preTime;
	long currentTime;
	public boolean running = true;
	final String resource = "custom.properties";
	public static void main(String[] args) {
		try {
			init();
			log.info("=======================               AKWorld START OK!               =======================");
		}catch(Throwable e) {
			e.printStackTrace();
			log.error("=======================               AKWorld START ERROR!               =======================",e);
			System.exit(0);
		}
	}

	private void loadCustomConfig() throws Exception {
		Properties pro = null;
		InputStream stream = null;
		
		File res = new File(resource);
		if (res.exists()) {
			stream = new FileInputStream(res);
			if (stream != null) {
				log.info("{} read from : {}", resource, res.getAbsolutePath());
			}
		}
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		if (stream == null) {
			stream = classLoader.getResourceAsStream(resource);
			if (stream != null) {
				log.info("{} read from : {}", resource, classLoader.getResource(resource).getPath());
			}
		}
		if (stream != null) {
			pro = new Properties();
			pro.load(stream);
			pro.putAll(System.getProperties());
			System.setProperties(pro);
			log.info("init custom.properties to System properties!");
		} else {
			log.info("not found custom.properties!");
			throw new Exception("custom.properties not found!");
		}
		log.info("[loadCustomConfig] return[null]");
	}

	public void baseInit() throws Throwable {
		loadCustomConfig();
		XMLConfiguration conf = new XMLConfiguration("config.xml");
		Utils.resolvePlaceHolders(conf);
		Platform.setConfiguration(conf);

		//Platform.setEntityManager(new EntityManagerImpl());
		Platform.setUpdater(new SimpleUpdater());
		Platform.setEventManager(new EventManager());

		// 注册更新器
		Platform.getUpdater().addSyncUpdatable(Platform.getEventManager());
		Platform.setAppContext(new DefaultAppContext());

		HttpHandlerDispatchManager.add(new HttpHandlerDispatch(HttpHandlerDispatch.PLAYER));
		TcpHandlerDispatchManager.add(new TcpHandlerDispatch(TcpHandlerDispatch.PLAYER));

		// Platform.getAppContext().create(SessionManager.class,
		// SessionManager.class);

		// 使用引擎的绑定ip端口功能
		Platform.getAppContext().create(ClientSessionUpdater.class,ClientSessionUpdater.class);
		Platform.getAppContext().create(ServerClientSessionUpdater.class,ServerClientSessionUpdater.class);
		//监听
		Server.buildConnections(Platform.getConfiguration().configurationsAt("connections.connection"));
		//连接world服务器
		Server.buildConnections(null);
		
		//Platform.getAppContext().create(LogService.class, LogService.class);
		//Platform.setLog(Platform.getAppContext().get(LogService.class));
		Platform.getAppContext().create(MemCachedDataCenter.class, DataCenter.class);
		Platform.setDataCenter(Platform.getAppContext().get(DataCenter.class));

		Platform.getAppContext().create(GameServerService.class, GameServerService.class);
		//Platform.getAppContext().create(TimeService.class, TimeService.class);
		//Platform.getAppContext().create(WorldSessionService.class, WorldSessionService.class);
		//Platform.getAppContext().create(ChargeService.class, ChargeService.class);
		//Platform.getAppContext().create(SyncPersistService.class, SyncPersistService.class);
		
		//Platform.getAppContext().create(ProductListService.class, ProductListService.class);
		//Platform.getAppContext().create(NoticeService.class, NoticeService.class);
		//Platform.getAppContext().create(RankService.class, RankService.class);
		//Platform.getAppContext().create(LoginCheckService.class, LoginCheckService.class);
		//Platform.getAppContext().create(InitMemcachedService.class, InitMemcachedService.class);
		//Platform.getAppContext().create(TextDataService.class, TextDataService.class);
		//Platform.getAppContext().create(PlayerSnsService.class, PlayerSnsService.class);
		Platform.getAppContext().create(ThreadService.class, ThreadService.class);
		Platform.getAppContext().create(WorldManager.class, WorldManager.class);
		log.info("[baseInit] return[null]");

	}

	public static void init() throws Throwable {
		if (world == null) {
			world = new AKWorld();
			world.baseInit();
			Thread mainCycle = new Thread(world);
			mainCycle.setName("MainCycle");
			mainCycle.start();
		} else {
			throw new ServerStartedException("服务器重复初始化");
		}
		log.info("[init] return[null]");
	}

	// 所有载入 监听工作完成以后 才能运行run
	public void run() {
		while (running) {
			preTime = Time.currTime;

			// 更新器更新
			Platform.getUpdater().update();
			Time.update_0(System.currentTimeMillis());

			// 维持主循环速度
			currentTime = Time.currTime;
			//主轮训不睡眠
			if (currentTime - preTime < CYCLE_TIME) {
				try {
					Thread.sleep(CYCLE_TIME - (currentTime - preTime));
				} catch (InterruptedException e) {
					log.error("InterruptedException", e);
				}
			}
		}
	}
}
