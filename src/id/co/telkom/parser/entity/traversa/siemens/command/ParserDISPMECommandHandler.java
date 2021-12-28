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

public class ParserDISPMECommandHandler extends AbstractCommandHandler implements CommandHandler {
	private ConfiguredHeader[] headers;
	private final Parser reader;
	private Map<String, Object> map = null;//orig
	private DataListener listener;
	private boolean isStartExecution;
	
	public ParserDISPMECommandHandler(Parser reader, String command,
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

			if(reader.isEqual('M')){
				reader.readUntilEOL(sb);
				String value=sb.toString();
				if(value.startsWith("Managed")){
					listener.onBeginTable(reader.getLine(), ctx);
					map = new LinkedHashMap<String, Object>();//org
					map.put("PARAM", headers[0].getName());
					map.put("VALUE", value.substring(headers[0].getLength()).trim());
					listener.onReadyData(ctx, map, reader.getLine());
				}else{
					//System.err.println("Skip : "+sb);
				}
			} 
			if(reader.isEqual('V')){
				reader.readUntilEOL(sb);
				String value=sb.toString();
				if(value.startsWith("Vendor")){
					listener.onBeginTable(reader.getLine(), ctx);
					map = new LinkedHashMap<String, Object>();//org
					map.put("PARAM", headers[1].getName());
					map.put("VALUE", value.substring(headers[1].getLength()).trim());
					listener.onReadyData(ctx, map, reader.getLine());
				}else{
					//System.err.println("Skip : "+sb);
				}
			}
			if(reader.isEqual('S')){
				reader.readUntilEOL(sb);
				String value=sb.toString();
				if(value.startsWith("System")){
					listener.onBeginTable(reader.getLine(), ctx);
					map = new LinkedHashMap<String, Object>();//org
					map.put("PARAM", headers[2].getName());
					map.put("VALUE", value.substring(headers[2].getLength()).trim());
					listener.onReadyData(ctx, map, reader.getLine());
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
			}

			reader.skipEOLs();
		}
		
		if(!isStartExecution){
			done();
			return;
		} 

		reader.readUntilEOL(sb);
		if(sb.toString().trim().startsWith("END JOB")){
			done();
			return;
		}
		
	}
	
}
