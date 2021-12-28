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

public class ParserANESPCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private ConfiguredHeader[] headers;
	private final String T_NAME;
	private static final Logger logger = Logger.getLogger(ParserANESPCommandHandler.class);
	private Map<String, Object> buffer = new LinkedHashMap<String, Object>();
	private String ES;
	
	public ParserANESPCommandHandler(
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
					listener.onError(reader.getLine(), ctx, reader.getColumn(), sb.toString());
					done();
					return;
				}else{
					System.err.println("Skip : "+sb);
					logger.error("Skip : "+sb);
				}
			}else if(reader.isEqual('O')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("OPERATING AREA")){
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
				logger.info("Skip : "+sb);
			}
			reader.skipEOLs();
		}
		if(!isStartExecution){
			done();
			return;
		}
		while(!isDone() && !reader.isEOF() && !reader.isEqual('<') ){
			if(reader.isNumber()){
				Parse(headers);
			}else if(reader.isWhiteSpace()){
				Parse(headers);
				if(buffer.get("EOSRES")!=null){
					buffer.put("ES", ES);
					String EOSRES=buffer.get("EOSRES").toString();
					for(String s1 : EOSRES.split(",")){
						String s2[] = s1.split("=");
						if(s2.length==2)
							buffer.put(s2[0], s2[1]);
						
					}
					listener.onReadyData(ctx, buffer, reader.getLine());
					buffer=new LinkedHashMap<String, Object>();
				}
			}else if(reader.isEqual('E')){
				reader.readUntilEOL(sb);
				if(sb.toString().equals("END")){
					done();
					return;
				}
			}
			else{
				reader.readUntilEOL(sb);
			}
			reader.skipEOLs();
		}
	}
	
	private void Parse(ConfiguredHeader[] header) throws IOException{
		StringBuilder sb = new StringBuilder();
		final int lastIdx = header.length-1;
		for (int i = 0; !isDone() && i <= lastIdx; i++) {
			ConfiguredHeader configuredHeader = header[i];
			if(lastIdx==i)
				reader.readUntilEOL(sb);
			else
				reader.read(sb, configuredHeader.getLength());
			
			String s = sb.toString().trim();
			ES = i==0 && !s.equals("") ? s : ES;
			
			if(buffer.get(configuredHeader.getName())==null || buffer.get(configuredHeader.getName()).equals(""))//check buffer
				buffer.put(configuredHeader.getName(), s);
		}
	}
}
