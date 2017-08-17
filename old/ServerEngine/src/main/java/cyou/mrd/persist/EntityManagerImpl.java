package cyou.mrd.persist;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.persister.entity.EntityPersister;



/**
 * 数据持久化管理器缺省的具体实现类。这个实现中借助了Hibernate，JBossCache以及BDB三个工具。用Hibernate实现对远程数据库的操作，用JBossCache做本地的数据缓存提高速度，
 * 用BDB来作为本地的存储库。此实现类使用Hibernate的配置信息，在初始化的时候将会借助Hibernate的配置信息来初始化JBossCache以及BDB。
 * @author mengpeng
 */
public class EntityManagerImpl implements EntityManager{
	
	protected Configuration conf;
	
	protected SessionFactory sessionFactory;
	
	protected HashMap<String,EntityPersister> entityPersisters = null;
	
	//protected Cache cache;
	
	//protected HashMap<String,Lock> locks = null;
	
	public EntityManagerImpl() throws Exception {
		this(null);
	}
	
	public EntityManagerImpl(Configuration conf) throws Exception{
		this.conf = conf;
		initHibernate();
		//initLocks();
		//initCache();
	}
	
	protected void initHibernate() {
		if(this.conf == null) {
			this.conf = new Configuration();
			this.conf.configure();
		}
		this.sessionFactory = this.conf.buildSessionFactory();
		entityPersisters = new HashMap<String,EntityPersister>();
		//获取所有Hibernate配置中的映射元信息
		Iterator ite =  this.sessionFactory.getAllClassMetadata().entrySet().iterator(); 
		while(ite.hasNext()) {
			Map.Entry entry = (Map.Entry)ite.next();
			String entityName = (String)entry.getKey();
			EntityPersister entityPersister = (EntityPersister)entry.getValue();
			entityPersisters.put(entityName, entityPersister);
		}
	}
	
//	protected void initCache() {
//		this.cache = new EhCacheCache();
//		//根据Hibernate配置中的元信息初始化JBossCache，每一个映射都有单独的Cache节点
//		Iterator<String> ite = entityPersisters.keySet().iterator();
//		while(ite.hasNext()) {
//			String entityName = (String)ite.next();
//			this.cache.createGroup(entityName);
//		}
//	}
	
//	protected void initLocks() {
//		locks = new HashMap<String,Lock>();
//		Iterator<String> ite = entityPersisters.keySet().iterator();
//		while(ite.hasNext()) {
//			String entityName = ite.next();
//			locks.put(entityName, new ReentrantLock());
//		}
//	}
	
//	protected Lock getLock(String name) {
//		return locks.get(name);
//	}
	
	/**
	 * @note 获取指定类型指定主键的对象。具体的实现思路是先根据类型和key在缓存中进行查找，如果没有找到就到BDB中查找，如果再没有找到就去远程数据库查找
	 */
	public <T> T find(Class<T> entityClass, Serializable key) {
		//String entityName = entityClass.getName();
		//Lock lock = getLock(entityName);
		//lock.lock();
		//try{
			//T result = (T)cache.get(entityName, key);
			//if(result == null) {
		Session session = getSession();
		Transaction tx = session.beginTransaction();
		try{
			T o = (T)session.get(entityClass, key);
			session.clear();
			tx.commit();
			return o; 
		} catch(Exception ex) {
			tx.rollback();
			throw new DataAccessException(ex);
		}
				//if(result != null) {
				//	cache.put(entityName, key, result);
				//}
			//}
		//	return result;
		//}finally{
			//lock.unlock();
		//}
	}
	
	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
//	protected <T> T findInDB(Class<T> entityClass, Serializable key) {
//		Session session = getSession();
//		Transaction tx = session.beginTransaction();
//		try{
//			T o = (T)session.get(entityClass, key);
//			session.clear();
//			tx.commit();
//			return o; 
//		} catch(Exception ex) {
//			tx.rollback();
//			throw new DataAccessException(ex);
//		}
//	}

	/**
	 * @note 同步创建一个对象，此对象将现在远程数据库中创建，如果创建成功，那么将会放到本地的缓存中
	 */
	public void createSync(Object entity) {
		//checkCreateObject(entity);
		//String entityName = entity.getClass().getName();
		//Lock lock = getLock(entityName);
		//lock.lock();
		//try{
			Session session = getSession();
			Transaction tx = session.beginTransaction();
			try {
				session.save(entity);
				//session.clear();
				tx.commit();
				//putToCache(entityName, entity, EntityMode.POJO);
			} catch (Exception e) {
				tx.rollback();
				throw new DataAccessException(e);
			}
		//} catch(Exception ex) {
		//	throw new DataAccessException(ex);
		//} finally{
			//lock.unlock();
		//}
	}
	
//	protected void putToCache(String entityName, Object entity, EntityMode entityMode) {
//		EntityPersister eper = entityPersisters.get(entityName);
//		Serializable id = eper.getIdentifier(entity, entityMode);
//		cache.put(entityName, id, entity);
//	}
	
	

	/**
	 * 同步删除指定对象。此对象将先在远程数据库中进行删除，如果删除成功将会从本地的缓存以及本地的BDB数据库中删除
	 */
	public void deleteSync(Object entity){
		//checkRemoveObject(entity);
		//String entityName = entity.getClass().getName();
		//Lock lock = getLock(entityName);
		//lock.lock();
		//try {
			Session session = getSession();
			Transaction tx = session.beginTransaction();
			try {
				session.delete(entity);
				//session.clear();
				tx.commit();
			} catch (Exception e) {
				tx.rollback();
				throw new DataAccessException(e);
			}
//			Serializable id = getEntityIdentifier(entityName, entity,
//					EntityMode.POJO);
			//removeFromCache(entityName, id);
		//} catch (Exception ex) {
		//	throw new DataAccessException(ex);
		//} finally{
			//lock.unlock();
		//}
	}
	
//	public void clearFromCache(Object entity) throws DataAccessException {
//		checkRemoveObject(entity);
//		String entityName = entity.getClass().getName();
//		Lock lock = getLock(entityName);
//		lock.lock();
//		try{
//			Serializable id = getEntityIdentifier(entityName, entity,
//					EntityMode.POJO);
//			removeFromCache(entityName, id);
//		} catch (Exception ex) {
//			throw new DataAccessException(ex);
//		} finally{
//			lock.unlock();
//		}
//	}
	
//	protected Serializable getEntityIdentifier(String entityName, Object entity, EntityMode entityMode) {
//		EntityPersister eper = entityPersisters.get(entityName);
//		return eper.getIdentifier(entity, entityMode);
//	}
	
//	protected void removeFromCache(String entityName, Serializable id) {
//		cache.remove(entityName, id);
//	}
//	
//	protected void removeFromCache(String entityName, Object entity, EntityMode entityMode) {
//		Serializable id = getEntityIdentifier(entityName, entity, entityMode);
//		cache.remove(entityName, id);
//	}

	/**
	 * 异步的更新一个对象，对象将会更新到本地的缓存以及本地的BDB库中，只有当调用了sync方法以后才会从本地更新到远程数据库
	 */
//	public void update(Object entity){
//		checkUpdateObject(entity);
//		String entityName = entity.getClass().getName();
//		Lock lock = getLock(entityName);
//		lock.lock();
//		try{
//			Serializable id = getEntityIdentifier(entityName, entity, EntityMode.POJO);
//			cache.put(entityName, id, entity);
//		} catch(Exception ex) {
//			throw new DataAccessException(ex);
//		} finally{
//			lock.unlock();
//		}
//	}
	
	/**
	 * 同步的更新指定对象。对象将会先更新到远程服务器，更新成功以后将会从本地的BDB库中删除，并且更新缓存
	 */
	public void updateSync(Object entity){
		//checkUpdateObject(entity);
		//String entityName = entity.getClass().getName();
		//Lock lock = getLock(entityName);
		//lock.lock();
		//try{
			//Serializable id = getEntityIdentifier(entityName, entity, EntityMode.POJO);
			Session session = getSession();
			Transaction tx = session.beginTransaction();
			try{
				session.update(entity);
				//session.clear();
				tx.commit();
				//cache.put(entityName, id, entity);
			} catch (Exception e) {
				tx.rollback();
				throw new DataAccessException(e);
			}
		//} catch(DataAccessException ex) {
		//	throw ex;
		//} catch(Exception ex1) {
		//	throw new DataAccessException(ex1);
		//} finally{
			//lock.unlock();
		//}
	}

	/**
	 * 根据条件返回指定的对象，在根据查询条件查询远程数据库返回指定对象以后将会在本地缓存一个DBD库中查找是否有此对象的更新的版本，如果有，那么将会放回更新的版本
	 */
	public <T> T fetch(String hql, Object... values){
		//String entityName = klass.getName();
		//Lock lock = getLock(entityName);
		//lock.lock();
		//try{
			Session session = getSession();
			Transaction tx = session.beginTransaction();
			try {
				Query query = getSession().createQuery(hql);
				if (values != null) {
					for (int i = 0; i < values.length; i++) {
						query.setParameter(i, values[i]);
					}
				}
				Object entity = query.uniqueResult();
				session.clear();
				tx.commit();
				//if(entity == null)
					return (T)entity;
				//Serializable id = getEntityIdentifier(entityName, entity, EntityMode.POJO);
				//return (T)getLocalEntity(entityName, entity, id);
			} catch (Exception e) {
				tx.rollback();
				throw new DataAccessException(e);
			}
		//} catch(DataAccessException ex){
		//	throw ex;
		//} catch(Exception ex1) {
		//	throw new DataAccessException(ex1); 
		//} finally{
			//lock.unlock();
		//}
	}
	
//	protected Object getLocalEntity(String entityName, Object entity, Serializable id) {
//		Object o = entity;
//		o = cache.get(entityName, id);
//		if(o == null)
//			return entity;
//		return o;
//	}
	
	/**
	 * 根据查询条件返回对象列表。先在远程库中进行查询，返回的对象列表将会被遍历 ，查找本地是否有最新的版本，如果有，将会被本地对象替换
	 */
	public <T> List<T> query(String hql, Object... values){
		//String entityName = klass.getName();
		//Lock lock = getLock(entityName);
		//lock.lock();
		//try{
			Session session = getSession();
			Transaction tx = session.beginTransaction();
			try {
				Query query = getSession().createQuery(hql);
				if (values != null) {
					for (int i = 0; i < values.length; i++) {
						query.setParameter(i, values[i]);
					}
				}
				List list = query.list();
				session.clear();
				tx.commit();
//				for(int i = list.size() - 1;i > 0;i--) {
//					Object entity = list.get(i);
//					Serializable id = getEntityIdentifier(entityName, entity, EntityMode.POJO);
//					Object newEntity = getLocalEntity(entityName, entity, id);
//					if(newEntity == entity)
//						continue;
//					list.set(i, newEntity);
//				}
				return list;
			} catch (Exception e) {
				tx.rollback();
				throw new DataAccessException(e);
			}
		//} catch(DataAccessException ex){
		//	throw ex;
		//} catch(Exception ex1) {
		//	throw new DataAccessException(ex1); 
		//} finally{
			//lock.unlock();
		//}
	}
	/**
	 * 根据查询条件返回指定个数的对象列表。先在远程库中进行查询，返回的对象列表将会被遍历 ，查找本地是否有最新的版本，如果有，将会被本地对象替换
	 */
	public <T> List<T> limitQuery(String hql, int start,
			int count, Object... values) throws DataAccessException{
		//String entityName = klass.getName();
		//Lock lock = getLock(entityName);
		//lock.lock();
		//try{
			Session session = getSession();
			Transaction tx = session.beginTransaction();
			try {
				Query query = getSession().createQuery(hql);
				if (values != null) {
					for (int i = 0; i < values.length; i++) {
						query.setParameter(i, values[i]);
					}
				}
		        query.setFirstResult(start);
		        query.setMaxResults(count);
		        List list = query.list();
		        session.clear();
				tx.commit();
//				for(int i = list.size() - 1;i > 0;i--) {
//					Object entity = list.get(i);
//					Serializable id = getEntityIdentifier(entityName, entity, EntityMode.POJO);
//					Object newEntity = getLocalEntity(entityName, entity, id);
//					if(newEntity == entity)
//						continue;
//					if(newEntity == null) {
//						list.remove(i);
//					} else{
//						list.set(i, newEntity);
//					}
//				}
				return list;
			} catch (Exception e) {
				tx.rollback();
				throw new DataAccessException(e);
			}
		//} catch(DataAccessException ex){
		//	throw ex;
		//} catch(Exception ex1) {
		//	throw new DataAccessException(ex1); 
		//} finally{
			//lock.unlock();
		//}
	}
	public long count(String hql, Object... values){
		Session session = getSession();
		Transaction tx = session.beginTransaction();
		try{
			Query query = getSession().createQuery(hql);
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					query.setParameter(i, values[i]);
				}
			}
			long ret = (Long)query.uniqueResult();
			session.clear();
			tx.commit();
			return ret;
		} catch(Exception e) {
			tx.rollback();
			throw new DataAccessException(e);
		}
	}
	
//	protected void checkUpdateObject(Object o) {
//	}
//	
//	protected void checkRemoveObject(Object o) {
//	}
	
//	protected Class getEntityClass(Object o) {
//		if(o instanceof HibernateProxy) {
//			return o.getClass().getSuperclass();
//		}
//		return o.getClass();
//	}
	
//	protected void checkCreateObject(Object o) {
//		if(o instanceof HibernateProxy) {
//			throw new IllegalArgumentException();
//		}
//	}
	
	/**
	 * 进行本地库跟远程库的同步，本地库中的对象将会被同步到远程库中，并且清空本地库
	 */
//	public void sync() {
//		for(SleepyCatDB db : dbs.values()) {
//			try {
//				Cursor cursor = db.database.openCursor(null, null);
//				DatabaseEntry key = new DatabaseEntry();
//				DatabaseEntry data = new DatabaseEntry();
//				EntityPersister ep = entityPersisters.get(db.database.getDatabaseName());
//				while(cursor.getPrev(key, data, LockMode.DEFAULT)==OperationStatus.SUCCESS){
//					try {
//						Object o = scp.getObject(data.getData(), ep);
//						Session session = getSession();
//						Transaction tx = session.beginTransaction();
//						try{
//							session.update(o);
//							tx.commit();
//							cursor.delete();
//						} catch(Exception ex) {
//							tx.rollback();
//							log.error("Exception", ex);
//						}
//					} catch (Exception e) {
//						log.error("Exception", e);
//					}
//				}
//				cursor.close();
//			} catch (DatabaseException e) {
//				log.error("DatabaseException", e);
//			}
//		}
//	}
	
	/**
	 * 根据查询条件返回指定个数的对象列表。先在远程库中进行查询，返回的对象列表将会被遍历 
	 */
//	public <T> List<T> limitQuery(String hql, int start,
//			int count, Object... values) throws DataAccessException{
//		try{
//			Session session = getSession();
//			Transaction tx = session.beginTransaction();
//			try {
//				Query query = getSession().createQuery(hql);
//				if (values != null) {
//					for (int i = 0; i < values.length; i++) {
//						query.setParameter(i, values[i]);
//					}
//				}
//		        query.setFirstResult(start);
//		        query.setMaxResults(count);
//		        List list = query.list();
//		        session.clear();
//				tx.commit();
//				return list;
//			} catch (Exception e) {
//				tx.rollback();
//				throw new DataAccessException(e);
//			}
//		} catch(DataAccessException ex){
//			throw ex;
//		} catch(Exception ex1) {
//			throw new DataAccessException(ex1); 
//		} 
//	}
	
	/**
	 * @note 获取指定类型指定主键的对象。具体的实现思路是先根据类型和key在缓存中进行查找，如果没有找到就到BDB中查找，如果再没有找到就去远程数据库查找
	 */
//	@Override
//	public <T> T findNoCache(Class<T> entityClass, Serializable key) {
//		String entityName = entityClass.getName();
//		Lock lock = getLock(entityName);
//		lock.lock();
//		try{
//				T result = findInDB(entityClass, key);
//				if(result != null) {
//					cache.put(entityName, key, result);
//				}
//			return result;
//		}finally{
//			lock.unlock();
//		}
//	}

//	@Override
//	public void clearLocal(Class<?> klass) {
//		// TODO Auto-generated method stub
//		
//	}
}

