package id.co.telkom.parser.entity.traversa.nokia.msscommand;


import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.AbstractCommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;


public class ParserZNBICommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private ConfiguredHeader[] headers;
	private final String T_NAME;
	private static final Logger logger = Logger.getLogger(ParserZNBICommandHandler.class);
	private boolean isStartExecution;
	@SuppressWarnings("unused")
	private final GlobalBuffer gb;
	
	public ParserZNBICommandHandler(
			Parser reader, 
			DataListener listener, 
			String command, 
			String params, 
			ConfiguredHeader[] headers,
			AbstractInitiator cynapseInit) {
		super(command, params);
		this.reader = reader;
		this.listener=listener;
		this.headers=headers;
		this.T_NAME=command;
		this.gb=(GlobalBuffer)cynapseInit.getMappingModel();
	}
	@Override
	public void handle(Context ctx)
			throws IOException {
		ctx.setTableName(T_NAME);
		StringBuilder sb = new StringBuilder();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		
		while (!isStartExecution && !reader.isEOL() && !reader.isEqual('<')){
			if(reader.isEqual('/')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("/*** COMMAND NOT FOUND ***/")){
					listener.onError(reader.getLine(), ctx, reader.getColumn(), sb.toString());
					done();
					return;
				}else{
					System.err.println("Skip : "+sb);
				}
			}else if(reader.isEqual('M')){
				reader.readUntil(' ', sb);
				if(sb.toString().equals("MSCi")){
					reader.skipWhile(' ').readUntil(' ', sb).skipUntilEOL();
					ctx.setNe_id(sb.toString());
				}else{
					reader.readUntilEOL(sb);
					System.err.println("Skip : "+sb);
				}
			}else if(reader.isEqual('S')){
				reader.readUntilEOL(sb);
				if(sb.toString().trim().startsWith("SS7  GTI")){
					isStartExecution = true;
				}else{
					//System.err.println("Skip : "+sb);
					logger.error("Skip : "+sb);
				}
			} 
			else if(reader.isEqual('C')){
				reader.readUntilEOL(sb);
				if(sb.toString().trim().equals("COMMAND EXECUTED")){
					done();
					return;
				}else{
					reader.readUntilEOL(sb);
					System.err.println("Skip : "+sb);
				}		
			}
			else{
				reader.readUntilEOL(sb);
//				if(sb.toString().contains("=")&& !sb.toString().contains("NUMBER OF DISPLAYED ROOTS")){
//					String[] splitted =  sb.toString().split("=");
//					String k = splitted[0].trim();
//					String v = splitted.length>1?splitted[1].trim():"-";
//						   v = v.contains(" ..")?v.substring(0,v.indexOf(" ..")):v;
//					head.put(k, v);
//				}
			}

			reader.skipEOLs();
		}
		
		if(!isStartExecution){
			done();
			return;
		} 
			while(!reader.isEOF() && !reader.isEqual('C') && !reader.isEqual('<') && !reader.isEqual('-') && isStartExecution){					
					listener.onBeginTable(reader.getLine(), ctx);
				while(!reader.isEOL() && !reader.isEqual(' ')){
					map = new LinkedHashMap<String, Object>();
					parse(map, reader, headers);
					listener.onReadyData(ctx, map, reader.getLine());
					reader.skipEOL();
				}
				if (isStartExecution) {isStartExecution = false;}
				while(reader.isEOL() || reader.isEqual(' '))
					reader.read();
				//break;
			}
		
		reader.readUntilEOL(sb);
		if(sb.toString().trim().equals("COMMAND EXECUTED")){
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
			map.put(header[i].getName(), sb.toString().trim());
		}
	}
}
