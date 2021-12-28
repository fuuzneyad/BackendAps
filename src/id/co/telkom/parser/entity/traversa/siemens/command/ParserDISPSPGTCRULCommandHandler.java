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

public class ParserDISPSPGTCRULCommandHandler extends AbstractCommandHandler implements CommandHandler {
	private ConfiguredHeader[] headers;
	private final Parser reader;
	private Map<String, Object> map = null;//orig
	private Map<String, Object> buffer = new LinkedHashMap<String, Object>();
	private DataListener listener;
	private boolean isStartExecution;
	
	public ParserDISPSPGTCRULCommandHandler(Parser reader, String command,
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

			if(reader.isEqual('=')){
				reader.readUntilEOL(sb);
				if(sb.toString().trim().contains("==================")){
					isStartExecution = true;
				}else{
				}
			} 
			else if(reader.isEqual('E')){
				reader.readUntilEOL(sb);
				if(sb.toString().trim().startsWith("ENDJOB")){					
					done();
					return;
				}else{
					reader.readUntilEOL(sb);
				}		
			}
			else{
				reader.readUntilEOL(sb);
			}

			reader.skipEOLs();
		}
		
		if(!isStartExecution){
			done();
			return;
		} 
			while(!reader.isEOF() && !reader.isEqual('E') && !reader.isEqual('D') && !reader.isEqual('-') && !reader.isEqual('=') && isStartExecution){					
					listener.onBeginTable(reader.getLine(), ctx);
				while(!reader.isEOL()){
					map = new LinkedHashMap<String, Object>();//org					
					parse(map, reader, headers);	
						if (map.get(headers[0].getName())!=null )//the line
							{
								if 	(!map.get(headers[0].getName()).equals("") && !map.get(headers[3].getName()).equals("") )								
								{
									buffer = map;
								}else
								if 	(map.get(headers[0].getName()).equals("") && !map.get(headers[3].getName()).equals("") ){
									buffer.put(headers[3].getName(), buffer.get(headers[3].getName())+""+map.get(headers[3].getName()));
									map=buffer;
									buffer=map;
								}									

							} else
							//insert when done		
							listener.onReadyData(ctx, buffer, reader.getLine());	

					reader.skipEOL();
				}
				
				//last rows
				listener.onReadyData(ctx, buffer, reader.getLine());
				
				if (isStartExecution) {isStartExecution = false;}
				while(reader.isEOL() || reader.isEqual(' '))
					reader.read();
			}
		
		reader.readUntilEOL(sb);
		if(sb.toString().trim().startsWith("ENDJOB")){
			done();
			return;
		}
		
	}
	
	private void parse(Map<String, Object> map, Parser reader, ConfiguredHeader[] header) throws IOException {		
		try{
			StringBuilder sb = new StringBuilder();
			reader.readUntilEOL(sb);
			String value=sb.toString();
			if (!(value.startsWith("---"))){
				int start=0;
				for (int i = 0; i < header.length; i++) {
				
					map.put(header[i].getName(), value.substring(start, start+header[i].getLength()).trim());
					start+=header[i].getLength()+1;
				}
				
			}
		}catch(StringIndexOutOfBoundsException e){}
	}
	
}
