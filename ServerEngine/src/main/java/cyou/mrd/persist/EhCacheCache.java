package cyou.mrd.persist;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

public class EhCacheCache implements Cache {
	
	protected CacheManager manager;
	
	public EhCacheCache() {
		manager = CacheManager.create();
	}

	public void createGroup(String group) {
		CacheConfiguration conf = new CacheConfiguration(group, 1000)
				.eternal(true).overflowToDisk(false).diskPersistent(false)
				.memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU);
		net.sf.ehcache.Cache cache = new net.sf.ehcache.Cache(conf);
		manager.addCache(cache);
	}

	public void put(String group, Object key, Object entity) {
		getCache(group).put(new Element(key,entity));
	}

	public Object get(String group, Object key) {
		Element element = getCache(group).get(key);
		if(element == null)
			return null;
		return element.getObjectValue();
	}

	public boolean remove(String group, Object key) {
		return getCache(group).remove(key);
	}
	
	protected net.sf.ehcache.Cache getCache(String group) {
		return manager.getCache(group);
	}
}
