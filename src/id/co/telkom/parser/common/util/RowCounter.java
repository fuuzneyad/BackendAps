package id.co.telkom.parser.common.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RowCounter {
	private int factor=50000;
	private Map<String, AtomicInteger> ctr = new LinkedHashMap<String, AtomicInteger>();
	
	public  RowCounter (int factor){
		this.factor=factor;
	}

	public synchronized int GetPage(String table){
		AtomicInteger a =ctr.get(table);
		if(a==null)
			a=new AtomicInteger();
		int get=a.incrementAndGet();
		ctr.put(table, a);
		return ((get-1)/factor);
	}
	
	public synchronized int getCounter(String table){
			AtomicInteger a =ctr.get(table);
			if(a==null)
				a=new AtomicInteger();
			int get=a.incrementAndGet();
			ctr.put(table, a);
			return get;
	}
}
