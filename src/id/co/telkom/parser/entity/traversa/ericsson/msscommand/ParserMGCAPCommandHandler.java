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


public class ParserMGCAPCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private ConfiguredHeader[] headers;
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	private final String T_NAME;
	private static final Logger logger = Logger.getLogger(ParserMGCAPCommandHandler.class);
	
	public ParserMGCAPCommandHandler(
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
	}
	
	@Override
	public void handle(Context ctx)
			throws IOException {
		ctx.setTableName(T_NAME);
		boolean isStartExecution = false;
		StringBuilder sb = new StringBuilder();
		while(!isStartExecution && !reader.isEOF() && !reader.isEqual('<')){
			if(reader.isEqual('/')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("/*** COMMAND NOT FOUND ***/")){
					done();
					return;
				}else{
					System.err.println("Skip : "+sb);
				}
			}else if(reader.isEqual('M')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("MT OWN CALLING ADDRESS DATA")){
					isStartExecution = true;
				}
			}else if(reader.isEqual('E')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("END")){
					done();
					return;
				}
			}else{
				reader.readUntilEOL(sb);
				System.err.println("Skip : "+sb);
				logger.error("Skip : "+sb);
			}
			reader.skipEOLs();
		}
		if(!isStartExecution){
			done();
			return;
		}
		
		if(isStartExecution){
			reader.skipLines(1).skipEOL();
			while(reader.isNumber() && !reader.isEOF() && !reader.isEqual('<')){
				Parse();
				listener.onReadyData(ctx, map, reader.getLine());
				map = new LinkedHashMap<String, Object>();
				reader.skipEOL();
			}
		}
		done();
		return;
		
	}
	private void Parse() throws IOException{
		StringBuilder sb = new StringBuilder();
		final int lastIdx = headers.length-1;
		for (int i = 0; !isDone() && i <= lastIdx; i++) {
			ConfiguredHeader configuredHeader = headers[i];
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
