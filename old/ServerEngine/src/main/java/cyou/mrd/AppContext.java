package cyou.mrd;

public interface AppContext {
	
	public <T> T get(Class<T> clazz);
	
	public <X,Y extends X> void create(Class<Y> clazz,Class<X> inter) throws Exception;
	
	public <T> void add(Object service,Class<T> inter);
	
	public void shutdown();
	
}
