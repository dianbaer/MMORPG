package cyou.mrd.updater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 更新器管理实现
 * @author mengpeng
 */
public class SimpleUpdater implements Updater{
	
	protected static final Logger log = LoggerFactory.getLogger(SimpleUpdater.class);
	
	protected List<Updatable> asyncUpdatables = new ArrayList<Updatable>();
	protected List<Updatable> syncUpdatables = new ArrayList<Updatable>();
	protected ArrayBlockingQueue<Runnable> syncRunnables = new ArrayBlockingQueue<Runnable>(2048);
	
	//protected int lastAsyncSize = 0;
	
	protected SimpleCountDownLatch latch = new SimpleCountDownLatch();
	
	/**
	 * 输出日志
	 */
	public static boolean sysoutLogFlag= true;
	/**
	 * 主线程调用，异步线程执行
	 */
	public void addAsyncUpdatable(Updatable updatable) {
		AsyncRunner au = new AsyncRunner(updatable);
		Thread t = new Thread(au,"Updater-Async-"+updatable.getClass().getSimpleName() + "-" + asyncUpdatables.size());
		t.setPriority(Thread.NORM_PRIORITY + 3);
		t.start();
		asyncUpdatables.add(au);
		latch.asyncRunnerMap.put(au, false);
	}
	
	/**
	 * 主线程调用，主线程执行
	 */
	public void addSyncUpdatable(Updatable updatable) {
		syncUpdatables.add(updatable);
	}
	
	/**
	 * 任何线程都可以调用，只执行一次
	 */
	public void addTempSyncRunnable(Runnable runnable){
		syncRunnables.offer(runnable);
	}

	public void update() {
//		log.info("====================update start==========================");
		long t1 = System.nanoTime();
		int size = asyncUpdatables.size();
		if(size > 0){
//			latch = new SimpleCountDownLatch(size);
//			for(int i=0;i<size;i++){
//				try {
//					asyncUpdatables.get(i).update();
//				} catch (Throwable e) {
//					log.error(e.toString(),e);
//				}
//			}
			try {
				latch.await(10000);
			} catch (Throwable e1) {
				log.error(e1.toString(),e1);
				return;
			}
		}
		long t2 =  System.nanoTime();
		long el1 = (t2 - t1) / 1000000;
		if( el1 > 100){
			log.info("==============================================ConCycle["+el1+"]");
		}

//		log.info("====================update asyncUpdatables over ==========================");
		for(Updatable u:syncUpdatables){
			long logTime = System.nanoTime();
			try {
				u.update();
			} catch (Throwable e) {
				log.error(e.toString(),e);
			}
			if(sysoutLogFlag){
				long logTime1 = System.nanoTime();
				long logTime2 = (logTime1 - logTime) / 1000000;
				if( logTime2 > 30){
					log.info("SynCycleTooLong["+u.getClass().getName() + " Time " + logTime2 +"]");
				}
			}
		}
		long t3 = System.nanoTime();
		long el2 = (t3 - t2) / 1000000;
		if( el2 > 100){
			log.info("SynCycle["+el2+"]");
		}
		Runnable runnable = null;
		while((runnable=syncRunnables.poll())!=null){
			try {
				runnable.run();
			} catch (Throwable e) {
				log.error(e.toString(),e);
			}
		}
		long t4 = System.nanoTime();
		long el3 = (t4 - t3) / 1000000;
		if( el3 > 100){
			log.info("SynCycle1["+el3+"]");
		}
//		log.info("====================update syncRunnables over ==========================");
	}
	
	class AsyncRunner implements Runnable,Updatable{
		
		protected Updatable updatable;
		private boolean isNotice = false;
		public AsyncRunner(Updatable updatable){
			this.updatable = updatable;

		}
		
		public boolean update(){
			synchronized(this){
				notify();
				isNotice = true;
				return true;
			}
		}
		
		public void run(){
			while(true){
//				log.info("AsyncRunner start *************** {}",updatable.getClass().getSimpleName());
				synchronized(this){
					try {
						if(!isNotice){
							wait();
						}else{
							//wait();
							//log.info("主线程已经通知了");
						}
						isNotice = false;
					} catch (InterruptedException e) {
						log.error(e.toString(),e);
						break;
					}
				}
				
//				log.info("AsyncRunner run *************** {}",updatable.getClass().getSimpleName());
				try {
					long t, t1, t2;
					t1 = System.nanoTime();
					updatable.update();
					t2 = System.nanoTime();
					t = (t2 - t1)/1000000;
					if(t > 10) {
						//log.info("AsyncRunner end ***************{}={}",updatable.getClass().getSimpleName(), t);
					}
				} catch (Throwable e) {
					log.error(e.toString(),e);
				}finally{
					latch.countDown(this);
//					log.info("AsyncRunner finally **************{} latch.countDown",updatable.getClass().getSimpleName());
				}
			}
		}
	}
	
	/**
	 * 替代J2SE的CountDownLatch实现，避免出现扣不正确的情况。
	 */
	class SimpleCountDownLatch {
		
		public Map<Updatable, Boolean> asyncRunnerMap;
		public SimpleCountDownLatch() {
			asyncRunnerMap = new HashMap<Updatable, Boolean>();
		}
		public synchronized void await(long timeout) throws InterruptedException {
			Object[] asyncRunnerMapKey = asyncRunnerMap.keySet().toArray();
			for(int i = 0;i<asyncRunnerMap.size();i++){
				Updatable updatable = (Updatable)asyncRunnerMapKey[i];
				asyncRunnerMap.put(updatable, false);
				updatable.update();
			}
			if (asyncRunnerMap.size() > 0) {
//				log.info("latch await remain: {}", remain);
				wait(timeout);
			}
		}
		
		public synchronized void countDown(Updatable updatable) {
			asyncRunnerMap.put(updatable, true);
			Boolean isNoticeAll = true;
			Object[] asyncRunnerMapKey = asyncRunnerMap.keySet().toArray();
			for(int i = 0;i<asyncRunnerMap.size();i++){
				Boolean asyncRunnerMapValue = asyncRunnerMap.get(asyncRunnerMapKey[i]);
				if(!asyncRunnerMapValue){
					isNoticeAll = false;
				}
			}
			if (isNoticeAll) {
//				log.info("countDown notifyAll");
				notifyAll();
			}
		}
	}
}
