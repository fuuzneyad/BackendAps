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


public class ParserZOCICommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	private final String T_NAME;
	private static final Logger logger = Logger.getLogger(ParserZOCICommandHandler.class);
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	@SuppressWarnings("unused")
	private final GlobalBuffer gb;
	@SuppressWarnings("unused")
	private String Param;
	private Map<String, Object> buffer = new LinkedHashMap<String, Object>();
	private String SET_NUMBER, SET_NAME;
	
	public ParserZOCICommandHandler(
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
	}
	@Override
	public void handle(Context ctx)
			throws IOException {
		ctx.setTableName(T_NAME);
		StringBuilder sb = new StringBuilder();
		boolean isStartExecution = false;
		
		while(!isStartExecution && !reader.isEOL() && !reader.isEqual('<')){
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
					isStartExecution = true;
				}else{
					reader.readUntilEOL(sb);
					System.err.println("Skip : "+sb);
					logger.error("Skip : "+sb);
				}
			}else{
				reader.readUntilEOL(sb);
				System.err.println("Skip : "+sb);
			}
			reader.skipEOLs();
		}
		if(!isStartExecution){
			done();
			return;
		}else{
			while(!reader.isEOL() && !reader.isEqual('<')){
				if(reader.isEqual('C')) {
					reader.readUntilEOL(sb);
					if(!sb.toString().equals("COMMAND EXECUTED")){
						System.err.println("Unexpected character in line "+reader.getLine()+" : "+sb);
					}
					done();
					return;
				}
				else if(reader.isEqual('S')) {
					reader.readUntilEOL(sb).skipLines(1);
					if(sb.toString().startsWith("SET NUMBER")) {
						reader.skipEOL();
						String line = sb.toString();
//						System.out.println("sss "+sb);
						SET_NUMBER = line.replace("SET NUMBER: ", "").split(" ")[0];
						SET_NAME = line.contains("SET NAME:")?line.substring(line.indexOf("SET NAME:")+"SET NAME:".length()).trim():null;
					}
				}
				else if(reader.isEqual('N')) {
					reader.readUntilEOL(sb).skipEOL();
					
					if(sb.toString().startsWith("NO:  NAME")){
						reader.skipLines(1);
					}
					while(!reader.isEOL()){
						parseTable(map, reader, ctx, headersMap.get(getCommand()));
						reader.skipEOL();
					}
					
				}
				else {
					reader.readUntilEOL(sb).skipEOL();
					System.err.println("Skip : " + sb + ", " + reader.getLine());
				}
				reader.skipEOLs();
			}
		}
	}
	protected void parseTable(Map<String, Object> map, Parser reader, Context ctx, ConfiguredHeader[] header) throws IOException {
		map = new LinkedHashMap<String, Object>();
		map.put("SET_NUMBER", SET_NUMBER);
		map.put("SET_NAME", SET_NAME);
		StringBuilder sb = new StringBuilder();
		int lastHeader = header.length;
		for (int i = 0; i < lastHeader; i++) {
			ConfiguredHeader configuredHeader = header[i];
			if(lastHeader==i)
				reader.readUntilEOL(sb).skipEOL();
			else
				reader.read(sb, configuredHeader.getLength());
			
			final String s = sb.toString().trim();
			if(configuredHeader.copied && s.length() > 0){
				buffer.put(configuredHeader.getName(), s.trim());
			}
			if(configuredHeader.copied)
				map.put(configuredHeader.getName(), buffer.get(configuredHeader.getName()));
			else
				map.put(configuredHeader.getName(), s.trim());
		}
		ctx.setTableName(T_NAME);
		listener.onReadyData(ctx, map, reader.getLine());
		map = new LinkedHashMap<String, Object>();
	}
	
}
