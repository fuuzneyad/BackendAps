package id.co.telkom.parser.common.loader;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientFactory;
import org.apache.flume.event.EventBuilder;
import org.apache.log4j.Logger;


public class CynapseInterfaceExtended{
	private Map<String, String> heads = new LinkedHashMap<String, String>();
	private Event event;
	private RpcClient client;
	private static final Logger logger = Logger.getLogger(CynapseInterfaceExtended.class);
	
	public CynapseInterfaceExtended(String host, int port){
		this.client = RpcClientFactory.getDefaultInstance(host, Integer.valueOf(port));
	}

	
	public boolean createActivePartInfo(Map<String, String>datetimes, String source) {
		heads.put("type", "PARTITIONS");
		heads.put("path", "active_partition/pm/" + source + "/new");
		StringBuilder sb = new StringBuilder();
		int mapLength = datetimes.size();
		if(mapLength>0){
			for (Map.Entry<String, String> mp : datetimes.entrySet()){
				sb.append(mp.getKey()+"\n");
			}
			if(sb.length()>2)
				sb.setLength(sb.length()-1);
			this.event = EventBuilder.withBody(sb.toString().getBytes(), this.heads);
			try {
				this.client.append(this.event);
				return true;
			} catch (EventDeliveryException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
		return false;
	}

	public RpcClient getRpcClient(){
		return this.client;
	}
	
	public void close(){
		this.client.close();
	}
	
}
