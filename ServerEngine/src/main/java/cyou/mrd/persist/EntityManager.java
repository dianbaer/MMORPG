package cyou.mrd.persist;

import java.io.Serializable;
import java.util.List;

/**
 * @note 数据持久化管理器,通过此接口来进行数据的持久化工作(包括存储数据到数据库，从数据库取出数据，删除数据等等)
 * @author mengpeng
 *
 */
public interface EntityManager {
	
	/**
	 * @note 获取指定类型指定key的对象
	 * @param entityClass 指定类型
	 * @param key 指定的主键
	 * @return 返回指定数据，如果没找到返回null
	 */
	public <T> T find(Class<T> entityClass, Serializable key);
	
	/**
	 * @note 获取指定类型指定key的对象
	 * @param entityClass 指定类型
	 * @param key 指定的主键
	 * @return 返回指定数据，如果没找到返回null
	 */
	//public <T> T findNoCache(Class<T> entityClass, Serializable key);
	
	/**
	 * @note 同步的创建对象
	 * @param entity 需要被创建的对象
	 */
	public void createSync(Object entity);
	
	
	/**
	 * @note 同步删除对象
	 * @param entity 被删除的对象
	 */
	public void deleteSync(Object entity);
	
	/**
	 * @note 在缓存中删除对象
	 * @param entity 被删除的对象
	 */
	//public void clearFromCache(Object entity);
	
	
	/**
	 * @note 异步更新对象。调用此方法后，对象被更新到本地，如果需要将对象更新到远程数据库，还需要调用sync方法
	 * @param entity 需要被更新的对象
	 */
	//public void update(Object entity);
	
	/**
	 * @note 同步更新对象。调用此方法以后，对象先被更新到远程的数据库，如果更新成功以后，将删除本地数据库中的此对象
	 * @param entity
	 */
	public void updateSync(Object entity);
	
	/**
	 * @note 根据指定条件查询指定的对象，注意此方法只返回单个对象，如果查询条件返回多个对象，此方法将抛出异常
	 * @param klass 指定的类型
	 * @param hql 查询的hql语句
	 * @param values 查询需要的参数值，如果没有参数可以为null
	 */
	public <T> T fetch(String hql, Object... values);
	
	/**
	 * @note 根据指定条件查询对象列表
	 * @param klass 指定的类型
	 * @param hql 查询的hql语句
	 * @values 查询需要的参数值，如果没有参数可以为null
	 */
	public <T> List<T> query(String hql, Object... values);
	//public <T> List<T> query(String hql, Object... values);
	
	/**
	 * @note 根据指定条件查询指定数量的对象列表
	 * @param klass 指定的类型
	 * @param hql 查询的hql语句
	 * @param start 开始位置
	 * @param count 数量
	 * @param values 查询需要的参数值，如果没有参数可以为null
	 */
	public <T> List<T> limitQuery(String hql, int start, int count, Object... values);
	
	/**
	 * @note 根据指定条件查询指定数量的对象列表
	 * @param 没有指定的类型, 直接使用hql查询
	 * @param hql 查询的hql语句
	 * @param start 开始位置
	 * @param count 数量
	 * @param values 查询需要的参数值，如果没有参数可以为null
	 */
	//public <T> List<T> limitQuery(String hql, int start, int count, Object... values);
	
	
	/**
	 * @note 根据条件，返回数据库中符合条件的对象数量
	 * @param hql 查询的hql语句，语句中必须要 有"select count(*)"类似的语句
	 * @param values 查询需要的参数，如果没有参数可以为null
	 * @return
	 */
	public long count(String hql, Object... values);

	/**
	 * @note 将本地库中的数据跟远程的数据库进行同步，每同步一条，将会从本地库中删除一条
	 */
	//public void sync();
	
	//public void clearLocal(Class<?> klass);
	
}
