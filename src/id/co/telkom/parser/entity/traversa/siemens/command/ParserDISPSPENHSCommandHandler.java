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
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;
import id.co.telkom.parser.entity.traversa.model.SStpBuffer;

public class ParserDISPSPENHSCommandHandler extends AbstractCommandHandler implements CommandHandler {
	private ConfiguredHeader[][] headers;
	private final Parser reader;
	private Map<String, Object> map = null;//orig
	private DataListener listener;
	private boolean isStartExecution, isNumberOne;
	private Map<String, Object> buffer = new LinkedHashMap<String, Object>();
	private LinkedHashMap<String, Map<String, Object>> buffer2 = new LinkedHashMap<String, Map<String, Object>>();
	String buffersccp;
	String spenhsid;
	String spenhsname;
	String desttype;
	String SSIDOrNot; 
	int get=0;
	int WhattoParse;
	private final SStpBuffer buf;
	
	public ParserDISPSPENHSCommandHandler(Parser reader, String command,
			String params, Context ctx,  ConfiguredHeader[][] headers,  DataListener listener, AbstractInitiator cynapseInit) {
		super(command, params);
		this.headers=headers;
		this.reader=reader;
		this.listener=listener;
		this.buf = ((GlobalBuffer)cynapseInit.getMappingModel()).getSstpBuf();
	}

	@Override
	public void handle(Context ctx) throws IOException {
		StringBuilder sb = new StringBuilder();
		
		while (!isStartExecution && !reader.isEOL() && !reader.isEqual('D')){

			if(reader.isEqual(' ')){
				reader.readUntilEOL(sb);
				if(sb.toString().trim().contains("| Primary List"))
					{WhattoParse=0;} else
				if(sb.toString().trim().contains("| Backup List"))
					{WhattoParse=1;}
				String t_name = WhattoParse == 0 ? getCommand(): getCommand()+"_"+WhattoParse;
				ctx.setTableName(t_name);
			}else if(reader.isEqual('=')){
				reader.readUntilEOL(sb);
				if(sb.toString().trim().startsWith("=======")){					
					isStartExecution=true;
				}else{
					//reader.readUntilEOL(sb);
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
				while(!reader.isEOL()){
					map = new LinkedHashMap<String, Object>();//org					
					parse(map, reader, headers[WhattoParse]);	
					
						if (map.get(headers[WhattoParse][0].getName())!=null )//the line
						{
														
							if 	(!map.get(headers[WhattoParse][0].getName()).equals("") && !map.get(headers[WhattoParse][2].getName()).equals("") )								
							{	
								isNumberOne=true;
								spenhsid=map.get(headers[WhattoParse][0].getName()).toString();
								spenhsname=map.get(headers[WhattoParse][1].getName()).toString();
								buffersccp=map.get(headers[WhattoParse][2].getName()).toString();
								desttype=map.get(headers[WhattoParse][3].getName()).toString();
								if (WhattoParse==1)
									SSIDOrNot=map.get(headers[WhattoParse][8].getName()).toString();
							}else
								
								if(!map.get(headers[WhattoParse][2].getName()).equals("") && map.get(headers[WhattoParse][0].getName()).equals(""))
									buffersccp+=map.get(headers[WhattoParse][2].getName());
								
								if (WhattoParse==1)
									if(!map.get(headers[WhattoParse][8].getName()).equals("") && map.get(headers[WhattoParse][0].getName()).equals(""))
										SSIDOrNot+=map.get(headers[WhattoParse][8].getName());
									
								if (!map.get(headers[WhattoParse][4].getName()).toString().contains("------") && !map.get(headers[WhattoParse][4].getName()).equals("") && WhattoParse==0)
								{
									buffer2.put("get_"+get, map);
									get++;
								}else
								if (!map.get(headers[WhattoParse][4].getName()).toString().contains("------") && !map.get(headers[WhattoParse][4].getName()).equals("") || ( WhattoParse==1 && isNumberOne))
								{
									buffer2.put("get_"+get, map);
									get++;
								}
								isNumberOne=false;
//							}						


						} else{
							for(int i=0; i<get; i++){
								if (buffer2.get("get_"+i)!=null){
									buffer=buffer2.get("get_"+i);
									buffer.put(headers[WhattoParse][0].getName(), spenhsid);
									buffer.put(headers[WhattoParse][1].getName(), spenhsname);
									buffer.put(headers[WhattoParse][2].getName(), buffersccp);
									buffer.put(headers[WhattoParse][3].getName(), desttype);
								
								if (WhattoParse==1 && buffer.get(headers[WhattoParse][8].getName())!=null)
									buffer.put(headers[WhattoParse][8].getName(), SSIDOrNot);
								
									if (buffer.get(headers[WhattoParse][4].getName())!=null ){
										String t_name = WhattoParse == 0 ? getCommand(): getCommand()+"_"+WhattoParse;
										ctx.setTableName(t_name);
										putBuffer(buffer, ctx, WhattoParse);
										listener.onReadyData(ctx, buffer, reader.getLine());		
									}
								}
							}
							
							get=0;
						
						}

					reader.skipEOL();
				}
				
				//last rows
				if (buffer.get(headers[WhattoParse][0].getName())!=null)
					for(int i=0; i<get; i++){
						if (buffer2.get("get_"+i)!=null){
						buffer=buffer2.get("get_"+i);
						buffer.put(headers[WhattoParse][0].getName(), spenhsid);
						buffer.put(headers[WhattoParse][1].getName(), spenhsname);
						buffer.put(headers[WhattoParse][2].getName(), buffersccp);
						buffer.put(headers[WhattoParse][3].getName(), desttype);
						if (WhattoParse==1 && buffer.get(headers[WhattoParse][8].getName())!=null)
							buffer.put(headers[WhattoParse][8].getName(), SSIDOrNot);
						
							String t_name = WhattoParse == 0 ? getCommand(): getCommand()+"_"+WhattoParse;
							ctx.setTableName(t_name);
							putBuffer(buffer, ctx, WhattoParse);
							listener.onReadyData(ctx, buffer, reader.getLine());
						}
					}
				
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
			if (!(value.startsWith("---")|| value.contains("===")|| value.contains("Sccp Entity") || value.contains("Set Type"))){
				int start=0;
				for (int i = 0; i < header.length; i++) {
				
					map.put(header[i].getName(), value.substring(start, start+header[i].getLength()).trim());
					
					start+=header[i].getLength()+1;
				}
				
			}

		}catch(StringIndexOutOfBoundsException e){
			
		}
	}
	
	private void putBuffer(final Map<String, Object> mp, final Context ctx, int whatToParse){
		String spen = whatToParse ==0 ? "PRIM_LIST_NAME" : "BACKUP_LIST_NAME";
		Object sphsname = mp.get(spen);
		if(sphsname!=null){
			buf.setspenHS(ctx.ne_id, sphsname.toString());
		}
	}
}
