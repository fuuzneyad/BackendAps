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


public class ZjgiCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	@SuppressWarnings("unused")
	private String commandParams;
	private final String TABLENAME;
	private final DataListener listener;
	private final ConfiguredHeader[] headers;
	private boolean isStartExecution;
	
	public ZjgiCommandHandler(String tableName,Parser reader, DataListener listener, String command, String params,ConfiguredHeader[] header) {
		super(command, params);
		this.TABLENAME=tableName;
		this.reader = reader;
		this.listener = listener;
		this.headers = header;
		this.commandParams=params;
	}
	
	@Override
	public void handle(Context ctx)
			throws IOException {
		ctx.setTableName(TABLENAME);
		StringBuilder sb = new StringBuilder();
		Map<String, Object> map = null;
		
		while (!isStartExecution && !reader.isEOF() && !reader.isEqual('<')){
			reader.skipEOLs().readUntilEOL(sb);
			
			if (sb.toString().startsWith("LOADING PROGRAM")) {
				while(reader.isEOL() || reader.isEqual(' '))
					reader.read();
				reader.readUntil(' ', sb);
				if(sb.toString().equals("MSCi")){
					isStartExecution = true;
					reader.skipWhile(' ').readUntil(' ', sb).skipUntilEOL().skipEOLs();
					ctx.setNe_id(sb.toString());
					for(int i=0; i<5; i++) {//skip MGW DATA: and headers
						reader.readUntilEOL(sb).skipEOLs();
						if(sb.toString().startsWith("NBCRCT"))
							break;
					}
				}
			} else if(sb.toString().startsWith("/*** COMMAND NOT FOUND ***/")){
				listener.onError(reader.getLine(), ctx, reader.getColumn(), sb.toString());
				reader.skipEOLs();
				done();
				return;
			} else{
				System.err.println("Skip : "+sb);
			}
			reader.skipEOLs();
		}

		if(!isStartExecution){
			done();
			return;
		}
		while(!reader.isEOF() && !reader.isEqual('C') && !reader.isEqual('<')){
			
			while(!reader.isEOL() && !reader.isEqual(' ')){
				map = new LinkedHashMap<String, Object>();
				parse(map, reader, headers);
				listener.onReadyData( ctx, map, reader.getLine());
				reader.skipEOL();
			}
			while(reader.isEOL() || reader.isEqual(' '))
				reader.read();
		}
		
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
			if(i==5){
				reader.readUntilEOL(sb).skipEOL();
			}else if(lastHeader==i){
				reader.readUntilEOL(sb);
			}else
				reader.read(sb, header[i].getLength());
			map.put(header[i].getName(), sb.toString().trim());
		}
	}
}
