package cyou.mrd.service;

/**
 *
 * @author
 * 
 *  在整个进程周期内都能使用的功能接口（服务）
 *   一般是单例或者静态对象
 */
public interface Service {
	public String getId();
	/**
	 * 启动服务，一般做一些初始化工作
	 * @throws Exception
	 */
	public void startup() throws Exception;
	/**
	 * 关闭服务，做一些清理工作
	 * @throws Exception
	 */
	public void shutdown() throws Exception;
}
