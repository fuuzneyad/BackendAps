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

public class ParserDISPSIGLSETCommandHandler extends AbstractCommandHandler implements CommandHandler {
	private ConfiguredHeader[][] headers;
	private final Parser reader;
	private Map<String, Object> map = null;//orig
	private DataListener listener;
	private boolean isStartExecution;
	private int whattoParse;
	
	public ParserDISPSIGLSETCommandHandler(Parser reader, String command,
			String params, Context ctx,  ConfiguredHeader[][] headers,  DataListener listener, AbstractInitiator cynapseInit) {
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

			if(reader.isEqual(' ')){
				reader.readUntilEOL(sb);
				if (sb.toString().trim().contains("Link set  | Maximum")){
					whattoParse=1;
				}else
				if (sb.toString().trim().contains("Load share algorithm        |")){
					whattoParse=0;
				}else{
					//System.err.println("Skip : "+sb);
				}
			} 
			else if(reader.isEqual('E')){
				reader.readUntilEOL(sb);
				if(sb.toString().trim().startsWith("ENDJOB")){					
					done();
					return;
				}else{
					reader.readUntilEOL(sb);
					//System.err.println("Skip : "+sb);
				}		
			}
			else if(reader.isEqual('=')){
				reader.readUntilEOL(sb);
				if(sb.toString().trim().startsWith("======")){					
					isStartExecution = true;
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
			while(!reader.isEOF() && !reader.isEqual('E') && !reader.isEqual('D') && isStartExecution){					
					listener.onBeginTable(reader.getLine(), ctx);
				while(!reader.isEOL() && !reader.isEOF() ){
					//reader.read();
					if(reader.isEqual('S')){
						reader.readUntilEOL(sb);
						if(sb.toString().trim().startsWith("S010")){					
							done();
							return;
						}
					}
					
					map = new LinkedHashMap<String, Object>();
					parse(map, reader, headers[whattoParse]);
					if (map.get(headers[whattoParse][0].getName())!=null )	
						listener.onReadyData(ctx, map, reader.getLine());//.onTreeEntry(reader.getLine(), context, map);
					
					reader.skipEOL();
				}				
				if (isStartExecution) {isStartExecution = false;}
				while(reader.isEOL() || reader.isEqual(' '))
					reader.read();
				//break;
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
			String value2=value;
			if (value2.trim().length()>0){
				if (!(value.contains("---")|| value.contains("===")|| value.contains("Adjacent DPC") ||value.contains("link set") || value.contains("| test      |"))){
					int start=0;
					for (int i = 0; i < header.length; i++) {
					
						map.put(header[i].getName(), value.substring(start, start+header[i].getLength()).trim());
						
						start+=header[i].getLength()+1;
					}
					
				}
					
			}
		}catch(StringIndexOutOfBoundsException e){
			
		}
	}
	
}
