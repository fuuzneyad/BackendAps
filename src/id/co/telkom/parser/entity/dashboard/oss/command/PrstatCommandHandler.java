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



public class PrstatCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	@SuppressWarnings("unused")
	private final ConfiguredHeader[] headers;
	private Map<String, Object> map= new LinkedHashMap<String, Object>();
	private Map<String, String> header = new LinkedHashMap<String, String>();
	private String[] head;
	private final String TNAME="PRSTAT";
	
	public PrstatCommandHandler(Parser reader, DataListener listener, String command, String params, ConfiguredHeader[] headers) {
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
		while(!isStartExecution && !reader.isEOF() && !reader.isEqual('#')){
			reader.readUntilEOL(sb);
			String line = sb.toString().toUpperCase();
			if(line.contains("PID")){
				head = line.split("\\s+");
				ctx.setTableName(TNAME+"_"+head[1]);
				header = new LinkedHashMap<String, String>();
				for(int i=0;i<head.length;i++)
					header.put("H"+i, head[i].replace("/", "_").replace("-", "_").replace(" ", "_").toUpperCase());
				isStartExecution = true;
				reader.skipEOL();
			}else{
				//System.out.println("skip:"+sb);
			}
			reader.skipEOLs();
		}
		
		if(!isStartExecution){
			done();
			return;
		}
		
		listener.onBeginTable(reader.getLastReadLine(), ctx);

		while(!isDone() &&!reader.isEqual('#') && !reader.isEOF() ) {
			reader.readUntilEOL(sb);
			String[] data = sb.toString().split("\\s+");
			if(!isInteger(data[1])){
				header = new LinkedHashMap<String, String>();
				String line = sb.toString().toUpperCase();
				head = line.split("\\s+");
				ctx.setTableName(TNAME+"_"+head[1]);
				for(int i=0;i<head.length;i++)
					header.put("H"+i, head[i].replace("/", "_").replace("-", "_").replace(" ", "_").toUpperCase());
			}else
			if(head.length==data.length)
				for(int i=0;i<head.length;i++)
					if(!header.get("H"+i).equals(""))
						map.put(header.get("H"+i), data[i]);
			if(!map.isEmpty())
				listener.onReadyData(ctx, map, reader.getLine());
			map = new LinkedHashMap<String, Object>();
			reader.skipEOL();
		}
	}
	
	private boolean isInteger(String s){
		try{
			Integer.parseInt(s);
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}
	
}
