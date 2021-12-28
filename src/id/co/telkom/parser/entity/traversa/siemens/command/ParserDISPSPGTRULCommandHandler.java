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

public class ParserDISPSPGTRULCommandHandler extends AbstractCommandHandler implements CommandHandler {
	private ConfiguredHeader[][] headers;
	private final Parser reader;
	private Map<String, Object> map = null;//orig
	private DataListener listener;
	private boolean isStartExecution;
	int whattoParse;
	
	public ParserDISPSPGTRULCommandHandler(Parser reader, String command,
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
//		Map<String, Object> map = null;//orig
		//long parentId = -1;
		
		while (!isStartExecution && !reader.isEOL() && !reader.isEqual('D')){

			if(reader.isEqual('S')){
				reader.readUntilEOL(sb);
				if(sb.toString().trim().startsWith("SPGTTRL")){
					if(sb.toString().trim().contains("Global Title"))
						{whattoParse=0;} else
					if(sb.toString().trim().contains("Pointer"))
						{whattoParse=1;} else
						whattoParse=0;
					isStartExecution = true;
				}else{
					////System.err.println("Skip : "+sb);
				}
			} 
			else if(reader.isEqual('E')){
				reader.readUntilEOL(sb);
				if(sb.toString().trim().startsWith("ENDJOB")){					
					done();
					return;
				}else{
					reader.readUntilEOL(sb);
					////System.err.println("Skip : "+sb);
				}		
			}
			else{
				reader.readUntilEOL(sb);
				////System.err.println("Skip : "+sb);
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
					parse(map, reader, headers[whattoParse]);	
//					parentId = (Long) map.get("parentId");
					
					if(whattoParse==1)
					{
						if (map.get(headers[1][0].getName())!=null ){
							ctx.setTableName(getCommand()+"_"+whattoParse);
							listener.onReadyData(ctx, map, reader.getLine());
						}
					}else
					{
						if (map.get(headers[0][0].getName())!=null ){
							ctx.setTableName(getCommand());
							listener.onReadyData(ctx, map, reader.getLine());
						}	
					}


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
			StringBuilder sb = new StringBuilder();
			reader.readUntilEOL(sb);
			String value=sb.toString();
			if (!(value.contains("---")|| value.contains("===")|| value.contains("Address Information (Range)") || value.contains("Ported Number"))){
				int start=0;
				for (int i = 0; i < header.length; i++) {
				
					map.put(header[i].getName(), value.substring(start, start+header[i].getLength()).trim());
					
					start+=header[i].getLength()+1;
				}
			}
	}
	
}
