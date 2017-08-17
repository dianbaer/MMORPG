package cyou.mrd.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;
import com.schooner.MemCached.MemcachedItem;

import cyou.mrd.Platform;

public class MemCachedDataCenter implements DataCenter {
	private final static Logger log = LoggerFactory.getLogger(MemCachedDataCenter.class);
	private final static MemCachedClient memcachedClient = new MemCachedClient();
	private static final int STORE_DATA_TRY_TIMES = Platform.getConfiguration().getInt("store_data_try_times");

	@Override
	public String getId() {
		return "MemCachedDataCenter";
	}

	public void startup() throws Exception {
//		MemCachedClient.log.setLevel(0);
		String[] servers = Platform.getConfiguration().getString("memcached.server.ip").split("-");
		Integer[] weights = null;
		String[] weightsCfg = Platform.getConfiguration().getString("memcached.server.weights").split("-");
		if (weightsCfg != null) {
			weights = new Integer[weightsCfg.length];
			for (int i = 0; i < weights.length; i++) {
				weights[i] = Integer.valueOf(weightsCfg[i]);
			}
		} else {
			weights = new Integer[servers.length];
			for (int i = 0; i < weights.length; i++) {
				weights[i] = 1;
			}
		}

		SockIOPool pool = SockIOPool.getInstance();

		pool.setServers(servers);
		pool.setWeights(weights);
		pool.setHashingAlg(SockIOPool.NEW_COMPAT_HASH);

		pool.setInitConn(Platform.getConfiguration().getInt("initConn", 5));
		pool.setMinConn(Platform.getConfiguration().getInt("minConn", 5));
		pool.setMaxConn(Platform.getConfiguration().getInt("maxConn", 5));
		pool.setMaxIdle(1000 * 60 * 60 * 6);

		pool.setMaintSleep(30);

		pool.setNagle(false);
		pool.setSocketTO(3000);
		pool.setSocketConnectTO(0);

		pool.initialize();
		
		
	}

	@Override
	public void shutdown() throws Exception {
		// 将数据保存到数据库？
	}

	@Override
	public Data getData(String key) {
		// log.info("[Memcached] get data key:{}", key);
		long t1, t2;

		String tKey = DataCenter.KEY_GAME_BASE + key;
		t1 = System.nanoTime();
		MemcachedItem mItem = memcachedClient.gets(tKey);
		t2 = System.nanoTime();
		long t = (t2 - t1) / 1000000L;
		if (t > 100) {
			log.info("[Memcached] gets data too long; key:{}, time:{}ms", tKey, t);
		}
		if (mItem == null) {
			return null;
		}
		Data ret = new Data();
		ret.value = mItem.value;
		ret.casUnique = mItem.casUnique;
		// log.info("[Memcached] get data key:{} value:{}", key, ret);
		return ret;
	}

	@Override
	public boolean sendNewData(String key, Object obj) {
		long t1, t2;
		String tKey = DataCenter.KEY_GAME_BASE + key;
		t1 = System.nanoTime();
		for (int times = 0; times < STORE_DATA_TRY_TIMES; times++) {
			if (memcachedClient.add(tKey, obj)) {
				// log.info("[Memcached] [sendNewData] SUCESS! key:{}, trytimes:{}",
				// new Object[]{tKey, times});
				return true;
			} else {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {// do nothing
				}
			}
		}
		t2 = System.nanoTime();
		long t = (t2 - t1) / 1000000L;
		log.info("[Memcached] [sendNewData] FAIL! key:{}, time:{}ms", new Object[] { tKey, t });
		return false;
	}

	@Override
	public boolean sendData(String key, Data data) {
		if (data.casUnique == 0) {
			return this.sendNewData(key, data.value);
		} else {
			boolean ret = memcachedClient.cas(DataCenter.KEY_GAME_BASE + key, data.value, data.casUnique);
			if (!ret) {
				log.info("[Memcached] sendData[cas]! key:{}, isOk:{}", new Object[] { key, ret });
			}
			return ret;
		}
	}

	public static void main(String[] args) {
		long t1, t2;
		t1 = System.nanoTime();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {// do nothing
		}
		t2 = System.nanoTime();
		long t = (t2 - t1) / 1000000000L;
		log.info("", t);
	}

}
