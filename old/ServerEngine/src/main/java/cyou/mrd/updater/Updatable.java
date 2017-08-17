package cyou.mrd.updater;

/**
 * 更新器接口,可放入{@link Platform}的Updater中进行异步更新（调度）
 * @author mengpeng
 */
public interface Updatable {
	public boolean update();
}
