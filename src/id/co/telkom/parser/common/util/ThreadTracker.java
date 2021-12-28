package id.co.telkom.parser.common.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

public class ThreadTracker {
	private AtomicInteger counter = new AtomicInteger(0);
	private ExecutorService pool;
	private int maxThread ;
	private static final Logger logger = Logger.getLogger(ThreadTracker.class);
	private boolean threadFinished=false;
	int taskCompleted=0;
	
	public  synchronized void  incrementCounter(){
		int incrementAndGet = counter.incrementAndGet();
		System.out.println("Thread counter : "+incrementAndGet);
		logger.info("Thread counter : "+incrementAndGet);
	}
	public  synchronized void  decrementCounter(){
		if(counter.get()<0)
			throw new ArithmeticException("counter is < 0");	
		int decrementAndGet = counter.decrementAndGet();
		System.out.println("Thread counter : "+decrementAndGet);
		logger.info("Thread counter : "+decrementAndGet);
	}
	
	public synchronized void decreaseCounter(){
		
		if(counter.get()<0)
			throw new ArithmeticException("counter is < 0");	
		int decrementAndGet=1;
		if(maxThread!=1 || (maxThread==1 && getCounter()==1) ){
			decrementAndGet = counter.decrementAndGet();
			System.out.println("Thread counter : "+decrementAndGet);
			logger.info("Thread counter : "+decrementAndGet);
			checkIfThreadDead();
		}
	}
	

	protected synchronized int getCounter() {
		return counter.get();
	}
	
	protected synchronized void checkIfThreadDead(){
//		if((getCounter()==0 && maxThread!=1) || (maxThread!=1 || (maxThread==1 && ((ThreadPoolExecutor)getPool()).getActiveCount()==taskCompleted) && taskCompleted!=1 )){
		if(getCounter()==0){
			threadFinished=true;
			allThreadsHaveFinished();
		}
	}
	
	public synchronized void allThreadsHaveFinished(){
		taskCompleted++;
		if(((ThreadPoolExecutor)getPool()).getActiveCount()==taskCompleted){
			allThreadsHaveFinished();
		}
	}
	
	public  synchronized void checkThreadFininshed(int task){
		
	}
	
	public synchronized boolean isThreadFinished() {
		return threadFinished;
	}

	public void setPool(ExecutorService pool) {
		this.pool = pool;
	}

	public ExecutorService getPool() {
		return pool;
	}
	
	public void SetMaxThread(int maxThread){
		this.maxThread=maxThread;
	}
	
	
}
