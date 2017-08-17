/**
 * PlayerLockService.java
 * ak.lock
 *
 *   version  date      	author
 * ──────────────────────────────────
 *    1.0	 2013年11月30日 		shiwei2006
 *
 * Copyright (c) 2013, www.cyou-inc.com All Rights Reserved.
*/

package cyou.mrd.lock;

import org.apache.log4j.Logger;

import cyou.mrd.Platform;
import cyou.mrd.service.Service;

/**
 * ClassName:PlayerLockService
 * ClassDescription:  玩家锁服务
 *
 * @author   shiwei2006
 * @Date	 2013年11月30日		下午2:25:33
 * @version  1.0
 */
public class PlayerLockService implements LockService, Service {
	
	private static final Logger log = Logger.getLogger(PlayerLockService.class);
	
	private GameLock gameLock;

	@Override
	public String getId() {
		return "PlayerLockService";

	}

	@Override
	public void startup() throws Exception {
		gameLock = new GameLock();
		gameLock.setRetryTimes(Platform.getConfiguration().getInt("lock_retry_times"));
		gameLock.setLockExpire(Platform.getConfiguration().getLong("lock_expire"));
		log.info("init game lock retryTimes:" + gameLock.getRetryTimes() + ",lockExpire:" + gameLock.getLockExpire() + " ms.");
	}

	@Override
	public void shutdown() throws Exception {
		
	}

	@Override
	public void lock(Object id) throws LockFailedException, ExceedTimesException {
		gameLock.lock(id);
	}

	@Override
	public void lock(Object id1, Object id2) throws LockFailedException, ExceedTimesException {
		gameLock.lock(id1, id2);
	}

	@Override
	public void unlock(Object id) {
		gameLock.unlock(id);
	}

	@Override
	public void unlock(Object id1, Object id2) {
		gameLock.unlock(id1, id2);
	}

}

