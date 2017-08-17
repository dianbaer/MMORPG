package cyou.mrd.util;

import cyou.mrd.io.AsyncCall;


public interface ThreadPool {
	
	public void execute(AsyncCall call);
}
