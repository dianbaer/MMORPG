package cyou.mrd.game;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mrd.encode.HTTPEncodeUtil;

import cyou.mrd.DefaultAppContext;
import cyou.mrd.LogService;
import cyou.mrd.Platform;
import cyou.mrd.ServerStartedException;
import cyou.mrd.account.AccountService;
import cyou.mrd.charge.ChargeClientService;
import cyou.mrd.charge.ProductListService;
import cyou.mrd.data.DataCenter;
import cyou.mrd.data.MemCachedDataCenter;
import cyou.mrd.event.EventManager;
import cyou.mrd.game.actor.ActorCacheService;
import cyou.mrd.game.mail.DefaultMailService;
import cyou.mrd.game.mail.MailService;
import cyou.mrd.io.http.SessionManager;
import cyou.mrd.io.tcp.ClientSessionUpdater;
import cyou.mrd.io.tcp.DirectClientSessionService;
import cyou.mrd.io.tcp.ServerClientSessionService;
import cyou.mrd.keyword.KeyWordService;
import cyou.mrd.packethandler.HttpHandlerDispatch;
import cyou.mrd.packethandler.HttpHandlerDispatchManager;
import cyou.mrd.packethandler.TcpHandlerDispatch;
import cyou.mrd.packethandler.TcpHandlerDispatchManager;
import cyou.mrd.persist.EntityManagerImpl;
import cyou.mrd.projectdata.TextDataService;
import cyou.mrd.service.HarmoniousService;
import cyou.mrd.sns.SnsService;
import cyou.mrd.updater.SimpleUpdater;
import cyou.mrd.util.ConfigKeys;
import cyou.mrd.util.DefaultThreadPool;
import cyou.mrd.util.RunTimeMonitor;
import cyou.mrd.util.Time;
import cyou.mrd.util.Utils;
import cyou.mrd.world.WorldManager;

/**
 * 具体游戏实现GameServer接口, 配置cofing.xml的游戏类。
 * 
 * @author miaoshengli
 * 
 */
public class Server implements Runnable {
	private static Logger log = LoggerFactory.getLogger(Server.class);

	private static Server server = null;

	private static final String ADDRESS = "address";
	private static final String PORT = "port";
	
	/**
	 * 主循环间隔时间, 单位毫秒
	 */
	int CYCLE_TIME = 60;
	long preTime;
	long currentTime;
	public static boolean running = true;

	private static final String ENCODE_LIB_NAME = "EncodeLib";
	private static final String LIB_BIN = "/lib-bin/";//临时文件夹
	private static GameServer gameServer;

	public static void init() throws Throwable {
		if (server == null) {
			log.info("Engine init start..");
			server = new Server();
			server.baseInit();
			log.info("Engine init complet!");
			
			log.info("GameServer init start..");
			String gameServerClassName = Platform.getConfiguration().getString(ConfigKeys.GAME_SERVER_CLASS_NAME);
			if(gameServerClassName == null) {
				log.error("[config][error]gameServerClassName is null check config.xml!");
				throw new ServerStartedException("[config][error]gameServerClassName is null.");
			}
			gameServer = (GameServer) Class.forName(gameServerClassName).newInstance();
			gameServer.startup();
			log.info("GameServer init complate!");
			
			running = true;
			Thread mainCycle = new Thread(server);
			mainCycle.setPriority(Thread.MAX_PRIORITY);
			mainCycle.setName("MainCycle");
			mainCycle.start();
			log.info("mainCycle runing.");

			log.info("[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[GAMESERVER START OK!]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
		} else {
			log.error("[config][error]server reduplicate init!");
			throw new ServerStartedException("[config][error]server reduplicate init!");
		}
	}

	public static void buildConnections(List<SubnodeConfiguration> l) throws Throwable {
		if (l == null || l.size() == 0) {// 默认为直连, 不监听任何ip,端口.
			ServerClientSessionService dcservice = new ServerClientSessionService("", 0,
					TcpHandlerDispatchManager.get(TcpHandlerDispatch.PLAYER));
			dcservice.startup();
			Platform.getAppContext().add(dcservice, ServerClientSessionService.class);
			log.info("[Connections][bind] no bind!");
		} else {
			for (Configuration cfg : l) {
				try {
					if ("direct".equals(cfg.getString("type"))) {
						String url = cfg.getString(ADDRESS);
						int port = cfg.getInt(PORT);
						DirectClientSessionService dcservice = new DirectClientSessionService(url, port,
								TcpHandlerDispatchManager.get(TcpHandlerDispatch.PLAYER));
						dcservice.startup();
						Platform.getAppContext().add(dcservice, DirectClientSessionService.class);
						if (cfg.getInt("port") != 0) {
							dcservice.bind();// 监听端口;
						} else {
							log.info("[bind] connections.connection port = 0!");
						}
					} else {
						log.info("[bind] connections.connection type is not find!");
					}
				} catch (Throwable e) {
					log.error(e.toString(), e);
					throw e;
				}
			}
		}
	}
	
	public void loadLibrary() throws Throwable {
		// 加载动态库
		try {
			if (Platform.getConfiguration().getBoolean(ConfigKeys.SERVER_HTTP_ENCODE_MODE)) {
				log.info("loadLibrary try load : LibraryName = {}", ENCODE_LIB_NAME);
				log.info("loadLibrary try load : file = {}", System.mapLibraryName(ENCODE_LIB_NAME));
				log.info("loadLibrary try load : Resource = {}", HTTPEncodeUtil.class.getResource(System.mapLibraryName(ENCODE_LIB_NAME)));
				InputStream in = HTTPEncodeUtil.class.getResourceAsStream(System.mapLibraryName(ENCODE_LIB_NAME));

				File fileOut = new File(System.getProperty("java.io.tmpdir") + "/" + LIB_BIN + System.mapLibraryName(ENCODE_LIB_NAME));
				fileOut.getParentFile().mkdirs();
				log.info("Writing ENCODE_LIB_NAME to: " + fileOut.getAbsolutePath());
				OutputStream out = new FileOutputStream(fileOut);
				byte[] buf = new byte[1024];
				for (int i = in.read(buf); i != -1; i = in.read(buf)) {
					out.write(buf, 0, i);
				}
				in.close();
				out.close();
				System.load(fileOut.toString());
				log.info("EncodeLib loaded : PATH = {}", fileOut.toString());
			}
		} catch (Throwable e) {
			log.error(e.toString(), e);
			throw e;
		}

	}

	private static void loadCustomConfig() throws Throwable {
        //加载配置文件
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		Properties pro = null;
		InputStream stream = null;
		final String resource = "custom.properties";
		if (classLoader != null) {
			stream = classLoader.getResourceAsStream(resource);
			if(classLoader.getResource(resource) != null) {
				log.info("FILE: PATH = " + classLoader.getResource(resource).getPath());
			}else {
				log.info("FILE: Resource load by classLoader = " + classLoader.getClass().getName());
			}
		}
		if (stream == null) {
			stream = Server.class.getResourceAsStream(resource);
			if(Server.class.getResource(resource) != null) {
				log.info("FILE: PATH = " + Server.class.getResource(resource).getPath());
			}else {
				log.info("FILE: Resource load by classLoader = " + Server.class.getName());
			}
		}
		if (stream == null) {
			stream = Server.class.getClassLoader().getResourceAsStream(resource);
			if(Server.class.getClassLoader().getResource(resource) != null) {
				log.info("FILE: PATH = " + Server.class.getClassLoader().getResource(resource).getPath());
			}else {
				log.info("FILE: Resource load by classLoader = " + Server.class.getName());
			}
		}
		if (stream == null) {
			File res = new File(resource);
			if (res.exists()) {
				log.info("FILE: PATH = " + res.getAbsolutePath());
				stream = new FileInputStream(res);
			}else {
				log.info("FILE: NO FIND IN PATH = " + res.getAbsolutePath());
			}
		}
		if (stream == null) {
			File res = new File("../" + resource);
			if (res.exists()) {
				log.info("FILE: PATH = " + res.getAbsolutePath());
				stream = new FileInputStream(res);
			} else {
				res = new File(System.getProperty("catalina.base") + resource);
				if (res.exists()) {
					log.info("FILE: PATH = " + res.getAbsolutePath());
					stream = new FileInputStream(res);
				} else {
					log.info("FILE: NO FIND IN PATH = " + res.getAbsolutePath());
				}
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
		log.info("loadCustomConfig success");
	}

	@SuppressWarnings("unchecked")
	public void baseInit() throws Throwable {
		loadCustomConfig();
		XMLConfiguration conf = new XMLConfiguration("config.xml");
		Utils.resolvePlaceHolders(conf);
		Platform.setConfiguration(conf);
		Platform.setServerId(Platform.getConfiguration().getInt(ConfigKeys.SERVER_ID));
		Platform.setGameId(Platform.getConfiguration().getString(ConfigKeys.GAME_ID));
		Platform.setGameCode(Platform.getConfiguration().getString(ConfigKeys.GAME_CODE));
		Platform.setDefaultLanguage(Platform.getConfiguration().getInt(ConfigKeys.DEFAUTL_LANGUAGE));
		
		//加载JNI
		loadLibrary();
	 
		//初始化线程监听器
//		ThreadMonitor tm = new ThreadMonitor("Thread-ThreadMonitor");
//		Platform.setThreadMonitor(tm);
//		tm.start();
		
        //初始化线程池
        DefaultThreadPool pool = new DefaultThreadPool(1,20);
        Platform.setThreadPool(pool);
		
		Platform.setEntityManager(new EntityManagerImpl());
		Platform.setUpdater(new SimpleUpdater());
		Platform.setEventManager(new EventManager());
		// 注册更新器
		Platform.getUpdater().addSyncUpdatable(Platform.getEventManager());
		Platform.setAppContext(new DefaultAppContext());

		HttpHandlerDispatchManager.add(new HttpHandlerDispatch(HttpHandlerDispatch.PLAYER));
		TcpHandlerDispatchManager.add(new TcpHandlerDispatch(TcpHandlerDispatch.PLAYER));

		Platform.getAppContext().create(SessionManager.class, SessionManager.class);
		Platform.getAppContext().create(TextDataService.class, TextDataService.class);
		//Platform.getAppContext().create(WorldManager.class, WorldManager.class);
		Platform.getAppContext().create(MemCachedDataCenter.class, DataCenter.class);
		Platform.setDataCenter(Platform.getAppContext().get(DataCenter.class));
		Platform.getAppContext().create(ActorCacheService.class, ActorCacheService.class);
		Platform.getAppContext().create(LogService.class, LogService.class);
		//Platform.getAppContext().create(DefaultMailService.class, MailService.class);
		Platform.getAppContext().create(SnsService.class, SnsService.class);
		Platform.getAppContext().create(ChargeClientService.class, ChargeClientService.class);
		Platform.getAppContext().create(KeyWordService.class,KeyWordService.class);
		Platform.getAppContext().create(HarmoniousService.class,HarmoniousService.class);
		Platform.getAppContext().create(ClientSessionUpdater.class,ClientSessionUpdater.class);
		
		Platform.getAppContext().create(ProductListService.class, ProductListService.class);

		buildConnections(Platform.getConfiguration().configurationsAt("connections.connection"));

		
		Platform.getAppContext().create(AccountService.class, AccountService.class);
	}


	// 所有载入 监听工作完成以后 才能运行run
	public void run() {
		log.info("[MainCycle] Thread start!");
		while (running) {
			preTime = Time.currTime;
			// 更新器更新
			Platform.getUpdater().update();
			Time.update(System.currentTimeMillis());
			// 维持主循环速度
			currentTime = Time.currTime;
			long useTime = currentTime - preTime;
			if (useTime < CYCLE_TIME) {
				try {
					Thread.sleep(CYCLE_TIME - useTime);
				} catch (InterruptedException e) {
					log.error("MainCycle Error",e);
				}
			}
			Time.tick++;
//			if(useTime > 100){
//				log.info("main cycle time = {}， tick = {}",currentTime - preTime, Time.tick);
//			}
		}
		log.info("[MainCycle] Thread stop!");
	}

	public static void main(String[] args) throws Exception {
//		init();
	}
}
