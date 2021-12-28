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

public class ParserC7LDPCommandHandler extends AbstractCommandHandler implements MscCommandHandler {

	private final Parser reader;
	private final DataListener listener;
	private final Map<String, ConfiguredHeader[]> headersMap;
	private final String T_NAME;
	private static final Logger logger = Logger.getLogger(ParserC7LDPCommandHandler.class);
	private Map<String, Object> buffer = new LinkedHashMap<String, Object>();

	
	public ParserC7LDPCommandHandler(
			Parser reader, 
			DataListener listener,
			String command,
			String params, 
			Map<String, ConfiguredHeader[]> headersMap,
			AbstractInitiator cynapseInit) {
		super(command,params);
		this.reader=reader;
		this.listener=listener;
		this.headersMap=headersMap;
		this.T_NAME=command;
	}

	@Override
	public void handle(Context ctx) throws IOException {

		ctx.setTableName(T_NAME);
		StringBuilder sb = new StringBuilder();
		boolean isStartExecution = false;
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Map<String, Object> mapFirst = new LinkedHashMap<String, Object>();
		ConfiguredHeader[] header1 = null;
		ConfiguredHeader[] header2 = null;
		boolean check = false;
		
		while(!isStartExecution && !reader.isEOF() && !reader.isEqual('<')){
			if(reader.isEqual('/')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("/*** COMMAND NOT FOUND ***/")){
					listener.onError(reader.getLine(), ctx, reader.getColumn(), sb.toString());
					done();
					return;
				}else{
					logger.error("Skip : "+sb);
				}
			}else if(reader.isEqual('C')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("CCITT7 LINK SET DATA")){
					isStartExecution = true;
				}
			}else{
				reader.readUntilEOL(sb);
				//System.err.println("Skip : "+sb);
				
			}
			reader.skipEOLs();
		}
		if(!isStartExecution){
			done();
			return;
		}
		
		while(!isDone() && !reader.isEOF() && !reader.isEqual('<')){
			if(reader.isEqual('L')){
				reader.readUntilEOL(sb).skipEOL();
				if(sb.indexOf("LS             SPID") >-1){
					buffer.clear();
					header1 = headersMap.get("FIRST");
					
					parse(mapFirst, reader, header1, check);
					map.putAll(mapFirst);
					buffer.putAll(mapFirst);
					mapFirst=new LinkedHashMap<String, Object>();
				}
			}else
			if(reader.isEqual('S')){
				reader.skipEOL().readUntilEOL(sb);
				if(sb.toString().contains("SLC")){
				reader.skipEOL();	
				header2 = headersMap.get("SECOND");
				parse(map, reader, header2, check);
				map.putAll(buffer);
				listener.onReadyData(ctx, map, reader.getLine());
				map=new LinkedHashMap<String, Object>();
				}
			}else
			if(reader.isEqual(' ')){
				
					header2 = headersMap.get("SECOND");
					parse(map, reader, header2, check);
					map.putAll(buffer);
					listener.onReadyData(ctx, map, reader.getLine());
					map=new LinkedHashMap<String, Object>();
			}else{
				reader.readUntilEOL(sb);
				//System.err.println("Skip : "+sb);
			}
			reader.skipEOLs();
			
		}
	}
	
	public void parse(Map<String, Object> map, Parser reader, ConfiguredHeader[] header, boolean check)throws IOException{

		StringBuilder sb = new StringBuilder();
		final int lastIdx = header.length-1;
		for (int i = 0; i <= lastIdx; i++) {
			ConfiguredHeader configuredHeader = header[i];
			if(lastIdx==i)
				reader.readUntilEOL(sb);
			else
				reader.read(sb, configuredHeader.getLength());
			
			String s = sb.toString().trim();
			map.put(header[i].getName(), s);
		}
	}
}
