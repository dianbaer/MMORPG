package cyou.mrd.updater;


/**
 * 更新器管理器接口
 * @author mengpeng
 */
public interface Updater {
	
	/**
	 * 添加一个同步可更新对象,每次调用将产生一个线程周期性的调用{@link #Updateble}的{@link #cyou.mrd.updater.Updatable.update()}方法
	 * @param updatable
	 */
	public void addSyncUpdatable(Updatable updatable);
	/**
	 * 添加一个临时可更新对象,和同步可更新对象串行的被更新，只会被执行一次！
	 * @param runnable
	 */
	public void addTempSyncRunnable(Runnable runnable);
	/**
	 * 添加一个异步可更新对象,每次调用将产生一个线程周期性的调用updateble的update方法
	 * @param updatable
	 */
	public void addAsyncUpdatable(Updatable updatable);
	/**
	 * 更新更新器中所有的同步可更新对象
	 */
	public void update();
}
