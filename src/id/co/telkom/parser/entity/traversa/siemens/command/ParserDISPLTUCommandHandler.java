package id.co.telkom.parser.entity.traversa.siemens.command;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.AbstractCommandHandler;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserDISPLTUCommandHandler extends AbstractCommandHandler implements CommandHandler {
	private ConfiguredHeader[] headers;
	private final Parser reader;
	private Map<String, Object> map = null;//orig
	private DataListener listener;
	private boolean isStartExecution;
	
	public ParserDISPLTUCommandHandler(Parser reader, String command,
			String params, Context ctx,  ConfiguredHeader[] headers,  DataListener listener, AbstractInitiator cynapseInit) {
		super(command, params);
		this.headers=headers;
		this.reader=reader;
		this.listener=listener;
	}

	@Override
	public void handle(Context ctx) throws IOException {
		ctx.setTableName(getCommand());
		StringBuilder sb = new StringBuilder();
		
		while (!isStartExecution && !reader.isEOL() && !reader.isEqual('D')){

			if(reader.isEqual('L')){
				reader.readUntilEOL(sb);
				if(sb.toString().trim().startsWith("LTG")){
					isStartExecution = true;
				}else{
					//System.err.println("Skip : "+sb);
				}
			} 
			else if(reader.isEqual('E')){
				reader.readUntilEOL(sb);
				if(sb.toString().trim().startsWith("END JOB")){					
					done();
					return;
				}else{
					reader.readUntilEOL(sb);
					//System.err.println("Skip : "+sb);
				}		
			}
			else{
				reader.readUntilEOL(sb);
				//System.err.println("Skip : "+sb);
			}

			reader.skipEOLs();
		}
		
		if(!isStartExecution){
			done();
			return;
		} 
			while(!reader.isEOF() && !reader.isEqual('E') && !reader.isEqual('D') && !reader.isEqual('-') && isStartExecution){					
					listener.onBeginTable(reader.getLine(), ctx);
				while(!reader.isEOL()){
					map = new LinkedHashMap<String, Object>();//org
					parse(map, reader, headers);					
					listener.onReadyData(ctx, map, reader.getLine());
					reader.skipEOL();
				}
				if (isStartExecution) {isStartExecution = false;}
				while(reader.isEOL() || reader.isEqual(' '))
					reader.read();
			}
		
		reader.readUntilEOL(sb);
		if(sb.toString().trim().startsWith("END JOB")){
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


			map.put(header[i].getName(), sb.toString().trim());//the org
	
		}

	}
	
}
