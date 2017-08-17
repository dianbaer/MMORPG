/**
 * LockFailedException.java
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
 * ClassName:LockFailedException
 * ClassDescription:  锁失败异常处理
 *
 * @author   shiwei2006
 * @Date	 2013年11月30日		下午1:45:26
 * @version  1.0
 */
public class LockFailedException extends Exception {

	static final long serialVersionUID = -3387516993124229948L;

	public LockFailedException() {
		super();
	}

	public LockFailedException(String message) {
		super(message);
	}

	public LockFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public LockFailedException(Throwable cause) {
		super(cause);
	}
}
