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

public class ParserDISPSPCLGPACommandHandler extends AbstractCommandHandler implements CommandHandler {
	private ConfiguredHeader[][] headers;
	private final Parser reader;
	private Map<String, Object> map = null;//orig
	private DataListener listener;
	private boolean isStartExecution;
	private Map<String, Object> buffer = new LinkedHashMap<String, Object>();
	int whattoParse;
	int insertTo;
	
	public ParserDISPSPCLGPACommandHandler(Parser reader, String command,
			String params, Context ctx,  ConfiguredHeader[][] headers,  DataListener listener, AbstractInitiator cynapseInit) {
		super(command, params);
		this.headers=headers;
		this.reader=reader;
		this.listener=listener;
	}

	@Override
	public void handle(Context ctx) throws IOException {
		StringBuilder sb = new StringBuilder();
		
		while (!isStartExecution && !reader.isEOF() && !reader.isEqual('D')){

			if(reader.isEqual('I')){
				reader.readUntilEOL(sb);
				if(sb.toString().trim().startsWith("Ident")){
					if (sb.toString().contains("| Ident. SPCLGPA      | Include       | Global Title"))
						{whattoParse=0; insertTo=0;}else//0
					if (sb.toString().contains("SPLNK Pointer"))
						{whattoParse=1; insertTo=1;}else//1
					if (sb.toString().trim().contains("Ident. SPCLGPA      | Global Title"))
						{whattoParse=2; insertTo=0;}else//2							
					if (sb.toString().contains("Ident. SPCLGPA      | Include                          "))
						{whattoParse=3; insertTo=2;}else//3
					if (sb.toString().contains("Address Type | SPLNK ID"))
						{whattoParse=1; insertTo=1;}//4
					
					String t_name = insertTo == 0 ? getCommand() : getCommand()+"_"+insertTo;
					ctx.setTableName(t_name);
				}else{
					//System.err.println("Skip : "+sb);
				}
			} else if(reader.isEqual('=')){
				reader.readUntilEOL(sb);
				if(sb.toString().trim().startsWith("======")){					
					isStartExecution = true;
				}else{
					reader.readUntilEOL(sb);
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
			while(!reader.isEOF() && !reader.isEqual('E') && !reader.isEqual('D') && !reader.isEqual('-') && !reader.isEqual('=') && isStartExecution){					
					listener.onBeginTable(reader.getLine(), ctx);
					
				while(!reader.isEOL()  && !reader.isEOF() ){					
					if(reader.isEqual('E')){
						reader.readUntilEOL(sb);
						if(sb.toString().trim().startsWith("ENDJOB")){					
							done();
							return;
						}
					} 				
						
					map = new LinkedHashMap<String, Object>();//org					
					parse(map, reader, headers[whattoParse]);	
					if (whattoParse==0){
						if (map.get(headers[whattoParse][0].getName())!=null )//the line
							{
								if 	(!map.get(headers[whattoParse][0].getName()).equals("") && !map.get(headers[whattoParse][6].getName()).equals("") )								
								{
									buffer = map;
								}else
								if 	(map.get(headers[whattoParse][0].getName()).equals("") && !map.get(headers[whattoParse][6].getName()).equals("") ){
									buffer.put(headers[whattoParse][6].getName(), buffer.get(headers[whattoParse][6].getName())+""+map.get(headers[whattoParse][6].getName()));
									buffer.put(headers[whattoParse][8].getName(), buffer.get(headers[whattoParse][8].getName())+""+map.get(headers[whattoParse][8].getName()));
									map=buffer;
									buffer=map;
								}									

							} else
								if(buffer.get(headers[whattoParse][0].getName())!=null)
									listener.onReadyData(ctx, buffer, reader.getLine());//.onTreeEntry(reader.getLine(), context, buffer);//insert when done	
								
								
					}else {
						if (map.get(headers[whattoParse][0].getName())!=null )	
							listener.onReadyData(ctx, map, reader.getLine());//(reader.getLine(), context, map, (long)insertTo);
					}
					
					reader.skipEOL();
					
				}
				
				//last rows
				if (whattoParse==0)
					listener.onReadyData(ctx, buffer, reader.getLine());
				
				if (isStartExecution) {isStartExecution = false;}
				while(reader.isEOL() || reader.isEqual(' '))
					reader.read();
				//break;
				
			}
		
		
	}
	
	private void parse(Map<String, Object> map, Parser reader, ConfiguredHeader[] header) throws IOException {		
		try{
			StringBuilder sb = new StringBuilder();
			reader.readUntilEOL(sb);
			String value=sb.toString();
			String value2=value;
			if (value2.trim().length()>0){
				if (!( value.startsWith("---") || value.contains("===") || value.contains("SPC for SSN") || value.contains("TTID") || value.contains("Address Information"))){
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
