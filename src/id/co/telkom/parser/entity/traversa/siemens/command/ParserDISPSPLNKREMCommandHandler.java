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

public class ParserDISPSPLNKREMCommandHandler extends AbstractCommandHandler implements CommandHandler {
	private ConfiguredHeader[][] headers;
	private final Parser reader;
	private Map<String, Object> map = null;//orig
	private DataListener listener;
	private boolean isStartExecution;
	private int whattoParse;
	private final SStpBuffer buf;
	
	public ParserDISPSPLNKREMCommandHandler(Parser reader, String command,
			String params, Context ctx,  ConfiguredHeader[][] headers,  DataListener listener, AbstractInitiator cynapseInit) {
		super(command, params);
		this.headers=headers;
		this.reader=reader;
		this.listener=listener;
		this.buf = ((GlobalBuffer)cynapseInit.getMappingModel()).getSstpBuf();
	}

	@Override
	public void handle(Context ctx) throws IOException {
		ctx.setTableName(getCommand());
		StringBuilder sb = new StringBuilder();
		
		while (!isStartExecution && !reader.isEOL() && !reader.isEqual('D')){

			if(reader.isEqual('S')){
				reader.readUntilEOL(sb);
				if(sb.toString().trim().startsWith("SPLNKREM")){
					if(sb.toString().trim().contains("Broadcast | SPCAREA"))
						whattoParse=0; else
					if(sb.toString().trim().contains("SCTP Association"))
						whattoParse=1; else
					if(sb.toString().trim().contains("| SPENHS"))
						whattoParse=2; else
					if(sb.toString().trim().contains("Ass.Conn.SectionFl")){
						whattoParse=3;
					}
					isStartExecution = true;
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
			else{
				reader.readUntilEOL(sb);
				if(sb.toString().trim().startsWith("SPLNKREM")){
					if(sb.toString().trim().contains("Broadcast | SPCAREA"))
						whattoParse=0; else
					if(sb.toString().trim().contains("SCTP Association"))
						whattoParse=1; else
					if(sb.toString().trim().contains("| SPENHS"))
						whattoParse=2; else
					if(sb.toString().trim().contains("Ass.Conn.SectionFl")){
						whattoParse=3;
					}
					isStartExecution = true;
				}
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
					
					String t_name = (whattoParse==0||whattoParse==3) ? getCommand() : getCommand()+"_"+whattoParse;
					ctx.setTableName(t_name);
					if(whattoParse==0)
					{
						if (map.get(headers[0][0].getName())!=null ){
							putBuffer(map, ctx);
							listener.onReadyData(ctx, map, reader.getLine());					
						}
					}else
					{
						if (map.get(headers[whattoParse][0].getName())!=null )	
							listener.onReadyData(ctx, map, reader.getLine());
						if(whattoParse==3){
							putBuffer(map, ctx);
						}
					}

					reader.skipEOL();
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
		StringBuilder sb = new StringBuilder();

		reader.readUntilEOL(sb);
		String value=sb.toString();
		if (!(value.startsWith("---")|| value.contains("===")|| value.contains("Net ID | Net name") || value.contains("ASP Init") || value.contains("Routing Context"))){
			int start=0;
			for (int i = 0; i < header.length; i++) {
			
				map.put(header[i].getName(), value.substring(start, start+header[i].getLength()).trim());
				
				start+=header[i].getLength()+1;
			}
		}

	}
	
	private void putBuffer(final Map<String, Object> mp, final Context ctx){
		Object splnkName = mp.get("SPLNKREM_IDENT_NAME");
		Object dpc = mp.get("SPLNKREM_IDENT_DPC");
		if(splnkName!=null && dpc!=null){
			buf.setSpLnkRem(ctx.ne_id, splnkName.toString(), dpc.toString());
		}
	}
}
