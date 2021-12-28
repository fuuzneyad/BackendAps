package id.co.telkom.parser.common.util;

import java.util.concurrent.atomic.AtomicInteger;

public class RowIncrementor {
	private AtomicInteger counter = new AtomicInteger();
	private int factor=1000;
	
	public RowIncrementor(int factor){
		this.factor=factor;
		this.counter = new AtomicInteger();
	}
	
	public void incrementRow(){
		counter.incrementAndGet();
	}
	
	public void rowIncrement(){
		counter.incrementAndGet();
	}
	
	public int getRow() {
		return counter.get();
	}
	
	public boolean checkFactors(){
		if(getRow()%factor==0){
			return true;
		}
			return false;
	}
}
