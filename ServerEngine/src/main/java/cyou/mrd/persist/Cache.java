package cyou.mrd.persist;

public interface Cache {
	
	public void createGroup(String group);
	
	public void put(String group, Object key, Object entity);
	
	public Object get(String group, Object key);
	
	public boolean remove(String group, Object key);
}
