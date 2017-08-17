package cyou.mrd.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.io.AsyncCall;

public class DefaultThreadPool implements ThreadPool {

	private static final Logger log = LoggerFactory.getLogger(DefaultThreadPool.class);
	
	protected ExecutorService executor;
	
	public final static int DEFAULT_THREAD_PRIORITY = Thread.NORM_PRIORITY + 3;
	
	
	public DefaultThreadPool(int min,int max){
		executor = new ThreadPoolExecutor(min,max,60L,TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(),new MeasureThreadFactory());
	}
	
	@Override
	public void execute(AsyncCall call) {
		executor.execute(call);
	}
	
	/**
	 * 这个ThreadFactory产生的Thread会测量线程的运行时间，如果超过10s，就会记录日志
	 *
	 */
	static class MeasureThreadFactory implements ThreadFactory {

		static final AtomicInteger poolNumber = new AtomicInteger(1);
		final ThreadGroup group;
		final AtomicInteger threadNumber = new AtomicInteger(1);
		final String namePrefix;

		MeasureThreadFactory() {
			SecurityManager s = System.getSecurityManager();
			group = (s != null) ? s.getThreadGroup() : Thread.currentThread()
					.getThreadGroup();
			namePrefix = "pool-" + poolNumber.getAndIncrement() + "-measurethread-";
		}

		public Thread newThread(final Runnable r) {
			Thread t = new Thread(group, r, namePrefix
					+ threadNumber.getAndIncrement(), 0){
				@Override
				public void run() {
					long t1 = System.nanoTime();
					super.run();
					long t2 = System.nanoTime();
					long interval = (t2 - t1) / 1000000L;
					if(interval > 1000) {
//						int playerID = -1;
//						if (r instanceof ClientSessionAsyncCall) {
//							ClientSessionAsyncCall call = (ClientSessionAsyncCall)r;
//							if (call.getSession() != null && call.getSession().getClient() instanceof Player) {
//								playerID = ((Player)call.getSession().getClient()).getInstanceId();
//							}
//						}
//						log.info("[CALLTOOLONG]ID[" + playerID + "]CLASS[{}]TIME[{}]", r.getClass().getName(), interval);
						log.info("[CALLTOOLONG]CLASS[{}]TIME[{}]", r.getClass().getName(), interval);
					}
					log.info("[call run]CLASS[{}]TIME[{}]", r.getClass().getName(), interval);
				}
			};
			if (t.isDaemon())
				t.setDaemon(false);
			if (t.getPriority() != DEFAULT_THREAD_PRIORITY) {
				t.setPriority(DEFAULT_THREAD_PRIORITY);
			}
			log.info("[METHODEND] return[Thread({})]",t.getName());
			return t;
		}

	}
}
