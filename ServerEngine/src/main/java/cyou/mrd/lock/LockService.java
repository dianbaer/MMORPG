/**
 * LockService.java
 * ak.lock
 *
 *   version  date      	author
 * ──────────────────────────────────
 *    1.0	 2013年11月30日 		shiwei2006
 *
 * Copyright (c) 2013, www.cyou-inc.com All Rights Reserved.
*/

package cyou.mrd.lock;
/**
 * ClassName:LockService
 * ClassDescription:  程序当中公共的锁服务，用于锁用户，联盟，英雄，其他 每种情况提供一个Service进行处理
 *
 * @author   shiwei2006
 * @Date	 2013年11月30日		下午2:22:37
 * @version  1.0
 */
public interface LockService {
	
	/**
	 * MAP_LOCK 针对地图的全局锁对象
	 */
	public static final byte[] MAP_LOCK = new byte[0];
	
	/**
	 * PK_LOCK 针对用户竞技的全局锁对象
	 */
	public static final byte[] PK_LOCK = new byte[0];

	/**
	 * lock: 锁定单个对象
	 * 
	 * @param id
	 * @throws LockFailedException
	 * @throws ExceedTimesException
	 * @return void
	 */
	public void lock(Object id) throws LockFailedException, ExceedTimesException;

	/**
	 * lock: 同时锁定两个对象
	 * 
	 * @param id1
	 * @param id2
	 * @throws LockFailedException
	 * @throws ExceedTimesException
	 * @return void
	 */
	public void lock(Object id1, Object id2) throws LockFailedException, ExceedTimesException;

	/**
	 * unlock: 解除锁定对象
	 * 
	 * @param id
	 * @return void
	 */
	public void unlock(Object id);

	/**
	 * unlock: 同时解除两个对象的锁定
	 * 
	 * @param id1
	 * @param id2
	 * @return void
	 */
	public void unlock(Object id1, Object id2);
}

