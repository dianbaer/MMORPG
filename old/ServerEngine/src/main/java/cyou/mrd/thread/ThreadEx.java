package cyou.mrd.thread;

import cyou.mrd.util.DefaultThreadPool;

public class ThreadEx extends Thread{
	
	private ThreadMonitor threadMonitor;
	
	public ThreadEx(String threadName,ThreadMonitor tm){
		super(threadName);
		this.threadMonitor = tm; 
		this.threadMonitor.registerThread(this);
	}
	
	public ThreadEx(String threadName,Runnable runnable,ThreadMonitor tm){
		super(runnable,threadName);
		this.threadMonitor = tm; 
		this.threadMonitor.registerThread(this);
		this.setPriority(DefaultThreadPool.DEFAULT_THREAD_PRIORITY);
	}
	
	public void threadCount(){
		this.threadMonitor.threadCount(this);
	}
	
}
