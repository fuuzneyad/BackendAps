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

public class ParserC7NCPCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private Map <String, ConfiguredHeader[]> mapHeaders;
	private final String T_NAME;
	private static final Logger logger = Logger.getLogger(ParserC7NCPCommandHandler.class);
	
	public ParserC7NCPCommandHandler(
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
		ctx.setTableName(T_NAME);
	    Map<String, Object> mapFirst = new LinkedHashMap<String, Object>();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		ctx.setTableName(T_NAME);
		boolean isStartExecution = false;
		StringBuilder sb = new StringBuilder();
		ConfiguredHeader[] header;
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
			}else if(reader.isEqual('C')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("CCITT7 SCCP NETWORK ")){
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
				System.err.println("Skip : "+sb);
			}
			reader.skipEOLs();
		}
		if(!isStartExecution){
			done();
			return;
		}
		while(!isDone() && !reader.isEOF() && !reader.isEqual('<') ){
			 if(reader.isEqual('S')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("SP      ")){
					reader.skipEOL();
					mapFirst= new LinkedHashMap<String, Object>();
					header = mapHeaders.get("SP");
					Parse(header, mapFirst);
					reader.skipEOLs().skipUntilAlphabet();
				}else if(sb.toString().startsWith("SSN    ")){
					reader.skipEOL();
					while(!isDone() && !reader.isEOL() && !reader.isEqual('<')){
						header = mapHeaders.get("SSN");
						map = new LinkedHashMap<String, Object>(mapFirst);
						Parse(header, map);
						listener.onReadyData(ctx, map, reader.getLine());
						reader.skipEOL();
					}
				}
				else{
					reader.readUntilEOL(sb);
					System.out.println("skips: "+sb);
				}
			}else{
				reader.readUntilEOL(sb);
			}
			reader.skipEOL();
		}
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
