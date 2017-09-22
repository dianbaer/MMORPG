package cyou.mrd.account;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.persist.EntityManager;

/**
 * 已废弃
 * @author Administrator
 * @deprecated
 */
public class AccountDBUtil {
	private static EntityManager accountEntityManager;
//	private static Logger log = LoggerFactory.getLogger(AccountDBUtil.class);

//	static {
//		try {
//			Configuration conf = new Configuration();
//			conf.configure("account.cfg.xml");
//			log.debug("Initializing Account db");
//			accountEntityManager = new EntityManagerImpl(conf);
//			log.debug(" Account db initialized, call AccountDBUtil.getSessionFactory()");
//		} catch (Throwable ex) {
//			log.error("Building accountEntityManager failed.", ex);
//			throw new ExceptionInInitializerError(ex);
//		}
//	}

	/**
	 * Account db
	 * 
	 * @param entityClass
	 * @param key
	 * @return
	 * @deprecated
	 */
	public static <T> T find(Class<T> entityClass, Serializable key) {
		return accountEntityManager.find(entityClass, key);
	}

	/**
	 * Account db
	 * @deprecated
	 */
	public static void createSync(Object entity) {
		accountEntityManager.createSync(entity);
	}

	/**
	 * @deprecated
	 * @param entity
	 */
	public static void updateSync(Object entity) {
		accountEntityManager.updateSync(entity);
	}

	/**
	 * @deprecated
	 * @param klass
	 * @param hql
	 * @param values
	 * @return
	 */
	public static <T> T fetch(Class<T> klass, String hql, Object... values) {
		return accountEntityManager.fetch(hql, values);
	}
	
	/**
	 * @deprecated
	 * @param klass
	 * @param hql
	 * @param values
	 * @return
	 */
	public static <T> List<T>  query(Class<T> klass, String hql, Object... values){
		return accountEntityManager.query(hql, values);
	}

}
