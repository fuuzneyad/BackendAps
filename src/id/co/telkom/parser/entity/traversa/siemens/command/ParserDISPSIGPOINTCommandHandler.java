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

public class ParserDISPSIGPOINTCommandHandler extends AbstractCommandHandler implements CommandHandler {
	private ConfiguredHeader[] headers;
	private final Parser reader;
	private Map<String, Object> buffer = new LinkedHashMap<String, Object>();
	private Map<String, Object> map = null;//orig
	private DataListener listener;
	
	public ParserDISPSIGPOINTCommandHandler(Parser reader, String command,
			String params, Context ctx,  ConfiguredHeader[] headers,  DataListener listener, AbstractInitiator cynapseInit) {
		super(command, params);
		this.headers=headers;
		this.reader=reader;
		this.listener=listener;
	}

	@Override
	public void handle(Context ctx) throws IOException {
		ctx.setTableName(getCommand());
		try{
		StringBuilder sb = new StringBuilder();
	    boolean isStartExecution=false;
		
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
				@SuppressWarnings("unused")
				int get=0;
				while(!reader.isEOL()){
					map = new LinkedHashMap<String, Object>();//org					
					Parse();
					
						if (map.get(headers[0].getName())!=null )//the line, if the line cathed, it returned null
							{
								get++;
								if 	(!map.get(headers[0].getName()).equals("") && !map.get(headers[3].getName()).equals("") )								
								{
									buffer = map;
								}
								else
								if 	(map.get(headers[0].getName()).equals("") && !map.get(headers[3].getName()).equals("") ){
									buffer.put(headers[3].getName(), buffer.get(headers[3].getName())+""+map.get(headers[3].getName()));
									map=buffer;
									buffer=map;
								}									
					
							} else
//						READY, insert when done
						listener.onReadyData(ctx, buffer, reader.getLine());
						reader.skipEOL();
				}
				
				//READY,last rows
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
		}catch (Exception e){e.printStackTrace();}
	}
	
	private void Parse() throws IOException{
		try{
			StringBuilder sb = new StringBuilder();
			reader.readUntilEOL(sb);
			String value=sb.toString();
			if (!value.startsWith("---")){
				int start=0;
				for (int i = 0; i < headers.length; i++) {
					map.put(headers[i].getName(), value.substring(start, start+headers[i].getLength()).trim());
					start+=headers[i].getLength()+1;
				}
				
			}
		}catch(StringIndexOutOfBoundsException e){
			
		}
	}
	
}
