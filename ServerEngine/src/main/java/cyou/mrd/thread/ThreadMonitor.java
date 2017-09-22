package cyou.mrd.thread;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 线程监听器
 * @author Administrator
 */
public class ThreadMonitor extends Thread{

	private static final Logger log = LoggerFactory.getLogger(ThreadMonitor.class);
	
	private ConcurrentHashMap<String, Integer> thread2Count = new ConcurrentHashMap<String,Integer>();
	
	private ConcurrentHashMap<String, Long> thread2Time = new ConcurrentHashMap<String,Long>();
	
	private static final int CYCLE_TIME = 1000;
	
	private static final int INT_NUM = 100000;
	
	public ThreadMonitor(String threadName) {
		super(threadName);
		registerThread(this);
	}
	
	
	/**
	 * 注册监听一个线程
	 */
	public void registerThread(Thread thread){
		thread2Count.put(thread.getName(), 0);
		thread2Time.put(thread.getName(), System.currentTimeMillis());
		log.info("ThreadMonitor : registerThread({})",thread.getName());
	}
	
	/**
	 * 取消监听一个线程
	 */
	public void unRegisterThread(Thread thread){
		if(thread2Count.contains(thread.getName())){
			thread2Count.remove(thread.getName());
			thread2Time.remove(thread.getName());
			log.info("ThreadMonitor : unRegisterThread({})",thread.getName());
		}else{
			log.info("ThreadMonitor : unRegisterThread({}) error[thread is null]",thread.getName());
		}
	}
	
	/**
	 * 线程计数
	 */
	public void threadCount(Thread thread){
		if(thread2Count.containsKey(thread.getName())){
			int num = thread2Count.get(thread.getName());
			num += 1;
			if(num > INT_NUM)
				num = 0;
			thread2Count.put(thread.getName(), num);
			thread2Time.put(thread.getName(), System.currentTimeMillis());
//			log.info("threadCount({})  count:[{}]",thread.getName(),num);
		}else{
			log.info("threadCount({}) error[thread is null]");
		}
	}
	
	public void run(){
		long preTime;
		long currentTime;
		while(true){
			preTime = System.currentTimeMillis();
			
			log.info("Thread Size[{}]",thread2Count.size());
			for(String threadName:thread2Count.keySet()){
				log.info("ThreadName[{}] ,count[{}], lastRunTime[{}]",new Object[]{threadName,thread2Count.get(threadName),thread2Time.get(threadName)});
			}
			
			currentTime = System.currentTimeMillis();
			if (currentTime - preTime < CYCLE_TIME) {
				try {
					Thread.sleep(CYCLE_TIME - (currentTime - preTime));
				} catch (InterruptedException e) {
					log.error(e.getMessage());
				}
			}
			threadCount(this);
		}
	}
	
	public static void main(String[] arges){
		ThreadMonitor tm = new ThreadMonitor("Thread-ThreadMonitor");
		tm.start();
	}
	
}
