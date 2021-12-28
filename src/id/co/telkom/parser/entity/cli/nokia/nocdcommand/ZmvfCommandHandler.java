package id.co.telkom.parser.entity.cli.nokia.nocdcommand;


import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.common.charparser.AbstractCommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;


public class ZmvfCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private String commandParams;
	private final Map<String, ConfiguredHeader[]> headersMap;
	private final String TABLENAME;
	private final DataListener listener;
	private boolean isStartExecution;
	
	public ZmvfCommandHandler(Parser reader, DataListener listener, String command, String params, Map<String, ConfiguredHeader[]> headersMap, String tableName) {
		super(command, params);
		this.TABLENAME=tableName;
		this.reader = reader;
		this.listener = listener;
		this.headersMap = headersMap;
		this.commandParams=params;
	}
	
	@Override
	public void handle(Context ctx)
			throws IOException {
		StringBuilder sb = new StringBuilder();
		Map<String, Object> map = null;
		ConfiguredHeader[] header = null;
		String t_name = null;		
			
		while (!isStartExecution && !reader.isEOL() && !reader.isEqual('<')){
			reader.skipEOLs().readUntilEOL(sb);
			
			if (sb.toString().startsWith("LOADING PROGRAM")) {				
				while(reader.isEOL() || reader.isEqual(' '))
					reader.read();
				reader.readUntil(' ', sb);
				if(sb.toString().equals("MSCi")){
					reader.skipWhile(' ').readUntil(' ', sb).skipUntilEOL();
					if(sb!=null && !sb.toString().trim().equals(""))
						ctx.setNe_id(sb.toString());					
				}else{
					reader.readUntilEOL(sb);
					System.err.println("Skip : "+sb);
				}
			} else if(sb.toString().startsWith("/*** COMMAND NOT FOUND ***/")){
				listener.onError(reader.getLine(), ctx, reader.getColumn(), sb.toString());
				reader.skipEOLs();
				done();
				return;
			}else if(sb.toString().trim().equals("COMMAND EXECUTION FAILED")){
				listener.onError(reader.getLine(), ctx, reader.getColumn(), sb.toString());
				reader.skipEOLs();
				done();
				return;
			}else if(sb.toString().contains("HLR ADDRESS")){
				t_name="HLR_ADDR";
				header=headersMap.get("HLR_ADDR");
				isStartExecution = true;
			}else if(sb.toString().contains("LAC")){
				t_name="LAC";
				header=headersMap.get("LAC");
				isStartExecution = true;
			}
			else{
				System.err.println("Skips : "+sb);
			}
			reader.skipEOLs();
		}
		
		if(!isStartExecution){
			done();
			return;
		}

		while(!reader.isEOL() && !reader.isEqual('C') && !reader.isEqual('-')){
				if (header!=null){
					map = new LinkedHashMap<String, Object>();
					parse(map, reader, header);
					if (commandParams.toUpperCase().contains("DETACHED"))
						map.put("DETACHED", "1");
					ctx.setTableName(TABLENAME+"_"+t_name);
					listener.onReadyData(ctx, map, reader.getLine());
					reader.skipEOL();
				}else{
					reader.readUntilEOL(sb).skipEOL();
					System.err.println("Skip : "+sb);
				}	
			}
			
			if (reader.isEqual('-'))
				reader.readUntilEOL(sb).skipEOL();
			if (reader.isEqual('T')){
				map = new LinkedHashMap<String, Object>();
				reader.readUntilEOL(sb).skipEOL();
				if (sb.toString().contains("Total")){
					int pos = sb.indexOf(":");
					String tot= pos !=-1 ? sb.toString().substring(pos+1).trim() : sb.toString().trim();
					
					if (commandParams.toUpperCase().contains("DETACHED"))
						{
							String tmp=tot;
							tot=tmp.substring(0,tmp.indexOf("(")).trim();
							String Percent=tmp.substring(tmp.indexOf("(")+1,tmp.indexOf(")")).trim();
							map.put("PERCENT", Percent);
							map.put("DETACHED", "1");
						}
					map.put("TOTAL", tot);
					ctx.setTableName(TABLENAME+"_"+t_name+"_SUM");
					listener.onReadyData(ctx, map, reader.getLine());
				done();
				return;
				}else{
					reader.readUntilEOL(sb).skipEOL();
					System.err.println("Skip : "+sb);
				}
					
			}
				reader.readUntilEOL(sb).skipEOL();	
	
		reader.readUntilEOL(sb);
		if(sb.toString().trim().equals("COMMAND EXECUTED")){
			done();
			return;
		}
	}
	private void parse(Map<String, Object> map, Parser reader, ConfiguredHeader[] header) throws IOException {		
		StringBuilder sb = new StringBuilder();
		int lastHeader = header.length-1;
		for (int i = 0; i < header.length; i++) {
			if(lastHeader==i){
				reader.readUntilEOL(sb);
			}else
				reader.read(sb, header[i].getLength());
			map.put(header[i].getName(), sb.toString().replace(":", "").trim());
		}
	}


}
