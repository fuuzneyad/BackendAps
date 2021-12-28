package id.co.telkom.parser.entity.dashboard.oss.command;


import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.common.charparser.AbstractCommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;



public class IpscanCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	@SuppressWarnings("unused")
	private final ConfiguredHeader[] headers;
	private Map<String, Object> map= new LinkedHashMap<String, Object>();
	private final String TNAME="IPSCAN";
	
	public IpscanCommandHandler(Parser reader, DataListener listener, String command, String params, ConfiguredHeader[] headers) {
		super(command, params);
		this.reader = reader;
		this.listener=listener;
		this.headers=headers;
	}
	
	@Override
	public void handle(Context ctx)
			throws IOException {
		StringBuilder sb = new StringBuilder();
		boolean isStartExecution = false;
		ctx.setTableName(TNAME);
		while(!isStartExecution && !reader.isEOF() && !reader.isEqual('#')){
			reader.readUntilEOL(sb);
			String line = sb.toString();
			if(line.contains("inet")){
				String[] in = line.split("\\s+"); 
				for (int i=0;i<in.length;i++){
					if(in[i].equals("inet"))
						map.put("INET", in[1+i]);
					else if(in[i].equals("netmask"))
						map.put("NETMASK", in[1+i]);
					else if(in[i].equals("broadcast"))
						map.put("BROADCAST", in[1+i]);
				}
			}
			if(!map.isEmpty())
				listener.onReadyData(ctx, map, reader.getLine());
			map = new LinkedHashMap<String, Object>();
			reader.skipEOLs();
		}
		
		if(!isStartExecution){
			done();
			return;
		}
		
		listener.onBeginTable(reader.getLastReadLine(), ctx);

	}
	
}
