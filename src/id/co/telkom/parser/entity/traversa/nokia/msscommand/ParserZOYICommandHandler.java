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

public class ParserZOYICommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private ConfiguredHeader[][] headers;
	private final String T_NAME;
	private static final Logger logger = Logger.getLogger(ParserZOYICommandHandler.class);
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	@SuppressWarnings("unused")
	private final GlobalBuffer gb;
	@SuppressWarnings("unused")
	private String Param;
	private boolean isStartExecution;
	
	public ParserZOYICommandHandler(
			Parser reader, 
			DataListener listener, 
			String command, 
			String params, 
			ConfiguredHeader[][] headers,
			AbstractInitiator cynapseInit) {
		super(command, params);
		this.reader = reader;
		this.listener=listener;
		this.headers=headers;
		this.T_NAME=command;
		this.gb=(GlobalBuffer)cynapseInit.getMappingModel();
		this.Param=params;
	}
	@Override
	public void handle(Context ctx)
			throws IOException {
		logger.info(T_NAME);
		ctx.setTableName(T_NAME);
		StringBuilder sb = new StringBuilder();
		
		while (!isStartExecution && !reader.isEOL() && !reader.isEOF() && !reader.isEqual('<')){
			reader.skipEOLs().readUntilEOL(sb);
			
			if (sb.toString().startsWith("LOADING PROGRAM")) {				
				while(reader.isEOL() || reader.isEqual(' '))
					reader.read();
				reader.readUntil(' ', sb);
				if(sb.toString().equals("MSCi")){
					reader.skipWhile(' ').readUntil(' ', sb).skipUntilEOL();
					ctx.setNe_id(sb.toString());					
				}else{
					reader.readUntilEOL(sb);
				}
			} else 
			if (sb.toString().startsWith("INTERROGATING")) {
				isStartExecution = true;
			} else if(sb.toString().startsWith("/*** UNKNOWN COMMAND ***/")){
				listener.onError(reader.getLine(), ctx, reader.getColumn(), sb.toString());
				reader.skipEOLs();
				done();
				return;
			} else{
				//System.err.println("Skip : "+sb);
			}
			reader.skipEOLs();
		}
		if(!isStartExecution){
			done();
			return;
		}
			
		while (!reader.isEOF() && !reader.isEOL() && !reader.isEqual('<')) {
			boolean isSpace = reader.isWhiteSpace();
			reader.readUntilEOL(sb);
			if(sb.toString().startsWith("ASSOCIATION SET NAME")){
				reader.skipLines(1);
				parse(headers[0],reader, map);
//				map = new LinkedHashMap<String, Object>(mapHeader);
			}else
			if(sb.toString().startsWith("    ASSOC.         ASSOC ID")){
				reader.skipLines(2);
				parse(headers[1],reader, map);
			}else if(isSpace && sb.toString().contains(":")){
				String param = sb.toString().trim().split("\\. .")[0].trim();
				map.put(replaceParam(param), sb.toString().split(":")[1].trim());
				
				if(sb.toString().contains("DATA STREAM COUNT")){
//					System.out.println(map);
					listener.onReadyData(ctx, map, reader.getLine());
//					map = new LinkedHashMap<String, Object>(mapHeader);
				}
			}
			reader.skipEOLs();
		}
		
		done();
		return;
	}
	
	private static String replaceParam(String s){
		return s.replace(" ", "_").
			   replace("ADDRESS", "ADDR").
			   replace("SOURCE", "SRC").
			   replace("PRIMARY", "PRIM").
			   replace("SECONDARY", "SEC").
			   replace("DEST.", "DEST").
			   replace("DESTINATION", "DEST");
	}
	
	private void parse(ConfiguredHeader[] headers, Parser reader, Map<String, Object> map) throws IOException {
		final int lastColsIndex = headers.length-1;
		StringBuilder sb = new StringBuilder();
		for (int colIdx = 0; colIdx < headers.length; colIdx++) {
			ConfiguredHeader header = headers[colIdx];
			
			if(colIdx == lastColsIndex){
				reader.readUntilEOL(sb);
			}else{
				reader.read(sb, header.length);
			}
			final String s = sb.toString().trim();
			map.put(header.getName(), s);
		}
	}
	
//	public static void main (String[] args){
//		String s ="DATA STREAM COUNT  . . . . : 16 ";
//		System.out.println(s.split("  .")[0]);
//	}
}
