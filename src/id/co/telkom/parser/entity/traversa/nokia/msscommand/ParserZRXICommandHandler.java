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


public class ParserZRXICommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	@SuppressWarnings("unused")
	private Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	private final String T_NAME;
	private static final Logger logger = Logger.getLogger(ParserZRXICommandHandler.class);
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	private Map<String, String> mappingHeader = new LinkedHashMap<String, String>();
	@SuppressWarnings("unused")
	private final GlobalBuffer gb;
	@SuppressWarnings("unused")
	private String Param;
	private boolean isStartExecution;
	
	public ParserZRXICommandHandler(
			Parser reader, 
			DataListener listener, 
			String command, 
			String params, 
			Map<String, ConfiguredHeader[]> headersMap,
			AbstractInitiator cynapseInit) {
		super(command, params);
		this.reader = reader;
		this.listener=listener;
		this.headersMap=headersMap;
		this.T_NAME=command;
		this.gb=(GlobalBuffer)cynapseInit.getMappingModel();
		this.Param=params;
		initMapping();
	}
	private void initMapping(){
		mappingHeader.put("NODE INFO", "NODE_INFO");
		mappingHeader.put("MGW OVERLOAD CONGESTION", "MGW_OVERLD_CONGEST");
		mappingHeader.put("FORWARD RELEASE INFO", "FWD_RELEASE_INFO");
		mappingHeader.put("MGW OVERLOAD CONGESTION", "MGW_OVERLD_CONGEST");
	}
	@Override
	public void handle(Context ctx)
			throws IOException {
		ctx.setTableName(T_NAME);
		StringBuilder sb = new StringBuilder();
		while (!isStartExecution && !reader.isEOL() && !reader.isEqual('<')){
			reader.skipEOLs().readUntilEOL(sb);
			
			if (sb.toString().startsWith("LOADING PROGRAM")) {
				isStartExecution = true;
				reader.skipEOLs().readUntil(' ', sb);
				if(sb.toString().equals("MSCi")){
					reader.skipWhile(' ').readUntil(' ', sb).skipUntilEOL();
					ctx.setNe_id(sb.toString());
				}
			} else if(sb.toString().startsWith("/*** COMMAND NOT FOUND ***/")){
				listener.onError(reader.getLine(), ctx, reader.getColumn(), sb.toString());
				reader.skipEOLs();
				done();
				return;
			} else{
				System.err.println("Skip : "+sb);
				logger.error("Skip : "+sb);
			}
			reader.skipEOLs();
		}
		
		while (!reader.isEOL() && !reader.isEqual('<')) {
			reader.readUntilEOL(sb);
			String line = sb.toString();
			if(line.startsWith("RESGR")){
				parseLine1(line.trim().replace("NOT SPECIFIED", "NOT_SPECIFIED")
								.replace("NODE INFO", "NODE_INFO")
						);
			}else
			if(line.contains(":")){
				parseLine2(line.trim());
				if(line.startsWith("MGW OVERLOAD CONGESTION")){
					listener.onReadyData(ctx, map, reader.getLine());
					map = new LinkedHashMap<String, Object>();
				}
			}
			
			reader.skipEOLs();
		}
		done();
		return;
	}
	
	private void parseLine1(String line){
		String[] splitted  = line.split("\\s+");
		for (String s:splitted){
			if(s.contains("=")){
				String k = s.split("=")[0];
				String v = s.split("=")[1];
				String field = mappingHeader.get(k);
				if(field!=null)
					map.put(field, v);
				else
					map.put(k.replace(" ", ""), v);
			}
		}
	}
	private void parseLine2(String line){
		String[] splitted  = line.split(":");
		if(splitted.length>1){
			String k = splitted[0].trim();
			String v = splitted[1].trim();
			String field = mappingHeader.get(k);
			if(field!=null)
				map.put(field, v);
			else
				map.put(k.replace(" ", ""), v);
		}
	}
}
