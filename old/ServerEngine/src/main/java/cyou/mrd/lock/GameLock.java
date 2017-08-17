/**
 * GameLock.java
 * ak.lock
 *
 *   version  date      	author
 * ──────────────────────────────────
 *    1.0	 2013年11月30日 		shiwei2006
 *
 * Copyright (c) 2013, www.cyou-inc.com All Rights Reserved.
*/

package cyou.mrd.lock;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

/**
 * ClassName:GameLock
 * 自定义锁实现，单独需要加锁处理的一类数据可以创建一个锁对象，例如：用户，副本，公会等。
 * @author   shiwei2006
 * @version  
 * @Date	 2013 2013年11月30日 下午1:50:14
 */
public class GameLock {

	private final Map<Object, Long> lockMap = new ConcurrentHashMap<Object, Long>();

	private final Lock lock = new ReentrantLock();

	private final Condition con = lock.newCondition();

	private int retryTimes = 300;

	private long lockExpire = 120000; // 2分钟

	private final Logger logger = Logger.getLogger(GameLock.class);

	private final String ERROR_MSG = "Lock object failed!";

	/**
	 * lock: 锁定当前对象
	 * @param key
	 * @throws LockFailedException
	 * @throws ExceedTimesException 
	 * @return void
	*/
	public void lock(Object key) throws LockFailedException, ExceedTimesException {
		if (key == null) {
			LockFailedException le = new LockFailedException("Can't lock a object whose id is null.");
			logger.error(ERROR_MSG, le);
			throw le;
		}
		lock.lock();
		try {
			Long time = lockMap.get(key);
			int count = 0;
			while (time != null && System.currentTimeMillis() - time.longValue() < lockExpire) {
				if (count >= retryTimes) {
					StringBuffer msg = new StringBuffer();
					msg.append("Lock object failed after retried ").append(count).append(" times,")
					   .append("current key:").append(key);
					ExceedTimesException ee = new ExceedTimesException(msg.toString());
					logger.error(ERROR_MSG, ee);
					throw ee;
				}
				con.await(30, TimeUnit.MILLISECONDS);
				time = lockMap.get(key);
				count++;
			}
			lockMap.put(key, new Long(System.currentTimeMillis()));
		}
		catch (InterruptedException ie) {
			logger.error(ERROR_MSG, ie);
			throw new LockFailedException(ie);
		}
		finally {
			con.signal();
			lock.unlock();
		}
	}


	/**
	 * lock: 同时锁定两个对象，适用于两个用户，英雄或者其他情况
	 * @param key1
	 * @param key2
	 * @throws LockFailedException
	 * @throws ExceedTimesException 
	 * @return void
	*/
	public void lock(Object key1, Object key2) throws LockFailedException, ExceedTimesException {
		if (key1 == null || key2 == null || key1.hashCode() < 1 || key2.hashCode() < 1) {
			LockFailedException le = new LockFailedException("Can't lock a object whose id is null.");
			logger.error(ERROR_MSG, le);
			throw le;
		}
		//如果key1和key2相同，则只锁一个
		if(key1.equals(key2)){
			lock(key1);
			return;
		} else if(key1.hashCode() > key2.hashCode()){//key1和key2始终保持相同顺序进行加锁，防止同时调用时出现互相等待出现死锁以及后续锁失效
			Object temp = key1;
			key1 = key2;
			key2 = temp;
		}
		lock(key1);
		try {
			lock(key2);
		}
		catch (ExceedTimesException ee) {
			unlock(key1);
			throw ee;
		}
		catch (LockFailedException ue) {
			unlock(key1);
			throw ue;
		}
	}

	/**
	 * unlock: 解除锁定对象
	 * @param key 
	 * @return void
	*/
	public void unlock(Object key) {
		if (key != null)
			lockMap.remove(key);
	}

	/**
	 * unlock: 同时解除两个对象的锁定
	 * @param key1
	 * @param key2 
	 * @return void
	*/
	public void unlock(Object key1, Object key2) {
		unlock(key1);
		unlock(key2);
	}

	public int getRetryTimes() {
		return retryTimes;
	}

	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}

	public long getLockExpire() {
		return lockExpire;
	}

	public void setLockExpire(long lockExpire) {
		this.lockExpire = lockExpire;
	}

}
