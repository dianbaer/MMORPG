package cyou.mrd.persist;

public class DataAccessException extends RuntimeException {
	public DataAccessException(Throwable cause) {
		super(cause);
	}
	
	public DataAccessException(String msg) {
		super(msg);
	}
}
