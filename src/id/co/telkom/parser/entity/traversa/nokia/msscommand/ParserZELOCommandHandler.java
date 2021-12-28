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


public class ParserZELOCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private static final String PROPERTIES = "PROPERTIES";
	private static final String RNCs = "RNCs";
	private String LAC;
	private final Parser reader;
	private final DataListener listener;
	private Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	private final String T_NAME;
	private static final Logger logger = Logger.getLogger(ParserZELOCommandHandler.class);
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	@SuppressWarnings("unused")
	private final GlobalBuffer gb;
	@SuppressWarnings("unused")
	private String Param;
	private Map<String, Object> buffer = new LinkedHashMap<String, Object>();
	@SuppressWarnings("unused")
	private String groupName;
	
	public ParserZELOCommandHandler(
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
				else if(reader.isEqual('L')) {
					reader.readUntilEOL(sb);
					if(sb.toString().equals("LOCATION AREA")) {
						reader.skipEOLs();
						while(!reader.isEOL()){
							parseMerged(map, reader, ctx, headersMap.get(getCommand()));
							reader.skipEOL();
						}
						ctx.setTableName(T_NAME);
						listener.onReadyData(ctx, map, reader.getLine());
						map = new LinkedHashMap<String, Object>();
					}
				}
				else if(reader.isEqual('B') || reader.isEqual('S')) {
					String t_name = reader.isEqual('B') ? "BTSS" : "SERVICE";
					reader.readUntilEOL(sb);
					
					sb.setLength(sb.length()-2);
					final String s = sb.toString();
					if(s.startsWith("BTSs LOCATED") || s.startsWith("SERVICE AREAS LOCATED"))
						buffer = new LinkedHashMap<String, Object>();
					groupName = s;
					reader.skipLines(1).skipUntilEOL().skipEOL();
					
					final ConfiguredHeader[] configuredHeaders = headersMap.get(t_name);
					while(!reader.isEOL()){
						parseTable(map, reader, ctx, configuredHeaders, t_name);
						reader.skipEOL();
					}
				}else if(reader.isEqual('R')){
					reader.skipLines(2).skipUntilEOL().skipEOL();
					final ConfiguredHeader[] configuredHeaders = headersMap.get(RNCs);
					while(!reader.isEOL()){
						parseRnc(map, reader, ctx, configuredHeaders);
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
	protected void parseTable(Map<String, Object> map, Parser reader, Context ctx, ConfiguredHeader[] header, String tName) throws IOException {
		map = new LinkedHashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
//		int lastHeader = header.length-2;
		int lastHeader = header.length;
		map.put("LAC", LAC);
		for (int i = 0; i < lastHeader; i++) {
			ConfiguredHeader configuredHeader = header[i];
			if(lastHeader==i)
				reader.readUntilEOL(sb).skipEOL();
			else
				reader.read(sb, configuredHeader.getLength());
			
			final String s = sb.toString().trim();
			if(configuredHeader.copied && s.length() > 0){
				buffer.put(configuredHeader.getName(), s);
			}
			if(configuredHeader.copied)
				map.put(configuredHeader.getName(), buffer.get(configuredHeader.getName()));
			else
				map.put(configuredHeader.getName(), s);
		}
//		map.put("GROUPNAME", groupName);
		ctx.setTableName(T_NAME+"_"+tName);
		listener.onReadyData(ctx, map, reader.getLine());//(reader.getLine(), ctx, map, parentId);
		map = new LinkedHashMap<String, Object>();
	}
	
	protected void parseRnc(Map<String, Object> map, Parser reader, Context ctx, ConfiguredHeader[] header) throws IOException {
		map = new LinkedHashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		int lastHeader = header.length-1;
		map.put("LAC", LAC);
		for (int i = 0; i <= lastHeader; i++) {
			ConfiguredHeader configuredHeader = header[i];
			if(lastHeader==i)
				reader.readUntilEOL(sb);
			else
				reader.read(sb, configuredHeader.getLength());
			
			final String s = sb.toString().trim();
			if(configuredHeader.copied && s.length() > 0){
				buffer.put(configuredHeader.getName(), s);
			}
			if(configuredHeader.copied)
				map.put(configuredHeader.getName(), buffer.get(configuredHeader.getName()));
			else
				map.put(configuredHeader.getName(), s);
		}
		ctx.setTableName(T_NAME+"_RNCS");
		listener.onReadyData(ctx, map, reader.getLine());//(reader.getLine(), ctx, map, parentId);
		map = new LinkedHashMap<String, Object>();
		
	}
	protected void parseMerged(Map<String, Object> map, Parser reader, Context ctx, ConfiguredHeader[] header) throws IOException{
		StringBuilder sb = new StringBuilder();
		while(!reader.isEOL()){
			reader.readUntilEOL(sb);
			String line = sb.toString();
			if(line.contains("LAC")){
				LAC = line.substring(line.indexOf(":")+1).split(" ")[0].trim();
				map.put("LAC", LAC);
			}else if(line.contains("(")&&line.contains(")")){
				String param = line.substring(line.indexOf("(")+1).split("\\)")[0].toUpperCase().replace("INT", "INT_");
				String values = line.substring(line.indexOf(":")+1).trim();
				map.put(param, values);
			}
			reader.skipEOL();
		}
	}
	protected void parse(Map<String, Object> map, Parser reader, Context ctx, ConfiguredHeader[] header) throws IOException{
		map = new LinkedHashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		
		while(!reader.isEOL()){
			reader.readUntil(':', sb);
			String param = sb.toString().trim().replaceAll("\\s+", "_");
			reader.readWhileAlphaNumeric(sb);
			map.put(param, sb.toString());
			reader.skipWhile(' ');
		}
		listener.onReadyData(ctx, map, reader.getLine());//(reader.getLine(), ctx, map, parentId);
//		parentId = (Long) map.get("parentId");
	}
	
	protected void parseProperty(Map<String, Object> map, Parser reader, Context ctx, ConfiguredHeader[] header) throws IOException{
		map = new LinkedHashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		
		reader.readUntil('.', sb).skipWhile('.');
		StringBuilder buffer = new StringBuilder(sb);
		reader.readUntil('.', sb).skipUntil(':');
		buffer.append(sb);
		reader.readUntilEOL(sb);
		map.put("PARAM", buffer.toString());
		map.put("VALUE", sb.toString());
		map.put("t_name", PROPERTIES);
		listener.onReadyData(ctx, map, reader.getLine());//(reader.getLine(), ctx, map, parentId);
	}
}
