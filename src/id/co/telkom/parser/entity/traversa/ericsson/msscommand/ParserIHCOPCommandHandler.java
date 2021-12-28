package id.co.telkom.parser.entity.traversa.ericsson.msscommand;


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

public class ParserIHCOPCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private Map <String, ConfiguredHeader[]> mapHeaders;
	private final String T_NAME;
	private static final Logger logger = Logger.getLogger(ParserIHCOPCommandHandler.class);
	
	public ParserIHCOPCommandHandler(
			Parser reader, 
			DataListener listener, 
			String command, 
			String params, 
			Map <String, ConfiguredHeader[]> mapHeaders,
			AbstractInitiator cynapseInit) {
		super(command, params);
		this.reader = reader;
		this.listener=listener;
		this.mapHeaders=mapHeaders;
		this.T_NAME=command;
	}
	
	@Override
	public void handle(Context ctx)
			throws IOException {
		logger.info(T_NAME);
		ctx.setTableName(T_NAME);
		boolean isFirst = true;
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		ctx.setTableName(T_NAME);
		boolean isStartExecution = false;
		StringBuilder sb = new StringBuilder();
		while(!isStartExecution && !reader.isEOF() && !reader.isEqual('<')){
			if(reader.isEqual('/')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("/*** COMMAND NOT FOUND ***/")){
					listener.onError(reader.getLine(), ctx, reader.getColumn(), sb.toString());
					done();
					return;
				}else{
					//logger.error("Skip : "+sb);
				}
			}else if(reader.isEqual('I')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("IP PORT CONNECTION DATA")){
					isStartExecution = true;
				}
			}else{
				reader.readUntilEOL(sb);
				if (sb.toString().equals("NOT ACCEPTED")) {
					reader.skipEOL().readUntilEOL(sb);
					listener.onError(reader.getLine(), ctx, reader.getColumn(), sb.toString());
					reader.skipEOLs();
					done();
					return;
				}else
				if (sb.toString().equals("END")) {
					reader.skipEOL().readUntilEOL(sb);
					listener.onError(reader.getLine(), ctx, reader.getColumn(), sb.toString());
					reader.skipEOLs();
					done();
					return;
				}
				//System.err.println("Skip : "+sb);
			}
			reader.skipEOLs();
		}
		if(!isStartExecution){
			done();
			return;
		}
		while(!isDone() && !reader.isEOF() && !reader.isEqual('<') ){
			 if(reader.isAlphaNumeric()){
				 reader.readUntilEOL(sb);
				 String prefix = sb.toString().split(" ")[0].trim();
				 ConfiguredHeader[] hdr = mapHeaders.get(prefix);
				 if(hdr!=null){
					 if(sb.toString().startsWith("IPPORT  MHROLE")){
						 if(!isFirst){
							 listener.onReadyData(ctx, map, reader.getLine());
							 map = new LinkedHashMap<String, Object>();
						 }
						 isFirst=false;
					 }
					 reader.skipEOL();
					 Parse(hdr, map);
				 }
			 }else{
					reader.readUntilEOL(sb);
			 }
			reader.skipEOL();
		}
		listener.onReadyData(ctx, map, reader.getLine());
	}
	
	private void Parse(ConfiguredHeader[] header, Map<String, Object> map) throws IOException{
		StringBuilder sb = new StringBuilder();
		final int lastIdx = header.length-1;
		for (int i = 0; !isDone() && i <= lastIdx; i++) {
			ConfiguredHeader configuredHeader = header[i];
			if(lastIdx==i)
				reader.readUntilEOL(sb);
			else{
				reader.read(sb, configuredHeader.getLength());
				if( i==0){
					if(sb.toString().startsWith("END")) {
						done();	
					}
				}
			}
			if(!isDone()){
				String s = sb.toString().trim();
				map.put(configuredHeader.getName(), s);
			}
		}
	}
	
}
