package id.co.telkom.parser.entity.traversa.siemens.command;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.common.charparser.AbstractCommandHandler;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;

public class InitiatorDispsigpointCommandHandler extends AbstractCommandHandler implements CommandHandler {
	private ConfiguredHeader[] headers;
	private final Parser reader;
	private Map<String, Object> buffer = new LinkedHashMap<String, Object>();
	Map<String, Object> map = null;//orig
	int WhattoParse;
	private GlobalBuffer gb;
	private Context ctx;
	
	public InitiatorDispsigpointCommandHandler(Parser reader, String command,
			String params, Context ctx, GlobalBuffer buf, ConfiguredHeader[] headers) {
		super(command, params);
		this.headers=headers;
		this.reader=reader;
		this.gb=buf;
		this.ctx=ctx;
	}

	@Override
	public void handle(Context ctx) throws IOException {
		try{
		StringBuilder sb = new StringBuilder();
	    boolean isStartExecution=false;
		
		while (!isStartExecution && !reader.isEOL() && !reader.isEqual('D')){

			if(reader.isEqual('=')){
				reader.readUntilEOL(sb);
				if(sb.toString().trim().contains("==================")){
					isStartExecution = true;
				}else{
//					System.err.println("Skip : "+sb);
				}
			} 
			else if(reader.isEqual('E')){
				reader.readUntilEOL(sb);
				if(sb.toString().trim().startsWith("ENDJOB")){					
					done();
					return;
				}else{
					reader.readUntilEOL(sb);
//					System.err.println("Skip : "+sb);
				}		
			}
			else{
				reader.readUntilEOL(sb);
//				System.err.println("Skip : "+sb);
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
						readyBuffer(buffer);
						reader.skipEOL();
				}
				
				//READY,last rows
				readyBuffer(buffer);
				
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
		}catch (Exception e){e.printStackTrace();}
	}
	
	private void Parse() throws IOException{
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
	}
	
	private void readyBuffer(Map<String, Object> buffer){
		Object sp = buffer.get("SPC");
		Object state = buffer.get("OP_STATE");
		if(state!=null && sp!=null && state.toString().equals("Enabled")){
			gb.setSPToVertex(ctx.ne_id, toInteger(sp.toString()));
		}
	}
	
	private static Integer toInteger(String s){
		try{
			return Integer.parseInt(s);
		}catch(NumberFormatException e){
			return 0;
		}
	}
}
