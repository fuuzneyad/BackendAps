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



public class PerformanceCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private final ConfiguredHeader[] headers;
	private Map<String, Object> map= new LinkedHashMap<String, Object>();
	private final String TNAME="PERFORMANCE";
	
	public PerformanceCommandHandler(Parser reader, DataListener listener, String command, String params, ConfiguredHeader[] headers) {
		super(command, params);
		this.reader = reader;
		this.listener=listener;
		this.headers=headers;
	}
	
	@Override
	public void handle(Context ctx)
			throws IOException {
		ctx.setTableName(TNAME);
		StringBuilder sb = new StringBuilder();
		boolean isStartExecution = false;
		while(!isStartExecution && !reader.isEOF() && !reader.isEqual('#')){
			reader.readUntilEOL(sb);
			String line = sb.toString();
			String[] s = line.split("\\s+"); 
			for(int i=0;i<headers.length;i++){
				if(headers.length<=s.length){
					map.put(headers[i].getName(), s[i]);
					if(s[i].contains(":")){
						map.put("DATETIME", s[i-2]+" "+s[i-1]+" "+s[i]);
					}
				}
			}
			listener.onReadyData(ctx, map, reader.getLine());
			map=new LinkedHashMap<String, Object>();
			reader.skipEOLs();
		}
		
		if(!isStartExecution){
			done();
			return;
		}
		
		listener.onBeginTable(reader.getLastReadLine(), ctx);
	}
	
}
