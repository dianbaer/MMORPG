/**
 * ExceedTimesException.java
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
 * ClassName:ExceedTimesException
 * ClassDescription:  锁超时异常处理
 *
 * @author   shiwei2006
 * @Date	 2013年11月30日		下午1:44:26
 * @version  1.0
 */
public class ExceedTimesException extends Exception {

	static final long serialVersionUID = -3387516993124229948L;

	public ExceedTimesException() {
		super();
	}

	public ExceedTimesException(String message) {
		super(message);
	}

	public ExceedTimesException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExceedTimesException(Throwable cause) {
		super(cause);
	}

}
