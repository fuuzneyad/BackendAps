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



public class DfCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	@SuppressWarnings("unused")
	private final ConfiguredHeader[] headers;
	private Map<String, Object> map= new LinkedHashMap<String, Object>();
	private Map<String, String> header = new LinkedHashMap<String, String>();
	private String[] head;
	private final String TNAME="DF";
	
	public DfCommandHandler(Parser reader, DataListener listener, String command, String params, ConfiguredHeader[] headers) {
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
			if(sb.toString().toUpperCase().contains("FILESYSTEM")){
				head = sb.toString().toUpperCase().replace("MOUNTED ON", "MOUNTED_ON").split("\\s+");
				for(int i=0;i<head.length;i++)
					header.put("H"+i, head[i].replace("1024", "_1024").replace("-", "").replace(" ", "_").replace("%", "PERCENT_").toUpperCase());
				isStartExecution = true;
				reader.skipEOL();
			}else{
				System.out.println("skip:"+sb);
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
			if(head!=null && head.length==data.length){
				for(int i=0;i<head.length;i++)
					map.put(header.get("H"+i), data[i]);
				ctx.setTableName(TNAME);
				listener.onReadyData(ctx, map, reader.getLine());
				map = new LinkedHashMap<String, Object>();
			}
			reader.skipEOL();
		}
	}
	
}
