package cyou.mrd.io.tcp;

import java.util.concurrent.atomic.AtomicInteger;

public class ClientSessionUtil {
	
	protected static final AtomicInteger id_gen = new AtomicInteger(0);
	
	public static int getSessionId(){
		return id_gen.incrementAndGet();
	}
}
