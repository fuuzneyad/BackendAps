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

public class ParserZNETCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private final String T_NAME;
	private static final Logger logger = Logger.getLogger(ParserZNETCommandHandler.class);
	
	private Map<String, ConfiguredHeader[]> headersMap;
	@SuppressWarnings("unused")
	private final GlobalBuffer gb;
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	private Map<String, Object> buffer = new LinkedHashMap<String, Object>();
	private final String[] tables = new String[]{"SIGNALLING ROUTE SETS", "SIGNALLING LINK SETS", "SIGNALLING LINKS", "M3UA BASED LINKS"};
	
	public ParserZNETCommandHandler(
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
		logger.info(command+" Started..");
	}
	@Override
	public void handle(Context ctx)
			throws IOException {
		StringBuilder sb = new StringBuilder();
		boolean isStartExecution = false;
		String headerKey = "";
		
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
			ConfiguredHeader[] configuredHeaders = headersMap.get("SIGNALLING ROUTE SETS");
			String netType = "NA0";
			while(!reader.isEOF() && !reader.isEqual('<')){
				reader.readUntilEOL(sb).skipEOLs();
				String s = sb.toString().trim();
				if(s.equals("COMMAND EXECUTED")){
					done();
					return;
				} else if(headersMap.containsKey(s)) {
						configuredHeaders = headersMap.get(s);
						headerKey = s;
				} else if(headerKey.equals(tables[0])) {//SIGNALLING ROUTE SETS
					if(s.startsWith("NETWORK:")) {
						netType = s.substring(s.indexOf(':')+1).trim();
					} else if(s.startsWith("SIGNALLING ROUTES")) {
						//handlers.onBeginTable(reader.getLine(), context);
						reader.readUntilEOL(sb).skipEOLs();
						s = sb.toString();
						//handlers.onTableHeader(reader.getLine(), context, configuredHeaders);
						int t1 = s.indexOf("NAME");
						int t2 = s.indexOf("STATE", t1);
						if(t1>0) {
							configuredHeaders[0].setLength(t1);
							if(t2>t1) {
								configuredHeaders[1].setLength(t2-t1);
								t1 = s.indexOf("IN ROUTE SET", t2);
								if(t1>t2) {
									configuredHeaders[2].setLength(t1-t2);
									t2 = s.indexOf("STATE", t1);
									if(t2>t1) {
										configuredHeaders[3].setLength(t2-t1);
										t1 = s.indexOf("INFO", t2);
										if(t1>t2)
											configuredHeaders[4].setLength(t1-t2);
									}
								}
							}
						}
						reader.skipLines(1);
						while(!reader.isEOL()) {
							map = new LinkedHashMap<String, Object>();
							map.put("NETWORK", netType);
							parseRoute(map, reader, ctx, configuredHeaders);
						}
						//handlers.onEndTable(reader.getLine(), context);
					} else
						System.err.println("Skip : "+s);
				} else if(headerKey.equals(tables[1])) {//SIGNALLING LINK SETS
					if(s.startsWith("NET")) {
						int t1 = s.indexOf("SP CODE H/D");
						int t2 = s.indexOf("LINK SET", t1)-1;
						if(t1>0) {
							configuredHeaders[0].setLength(t1);
							if(t2>t1) {
								configuredHeaders[1].setLength(t2-t1);
								t1 = s.indexOf("STATE", t2);
								if(t1>t2) {
									configuredHeaders[2].setLength(t1-t2);
									t2 = s.indexOf("SIGNALLING LINKS IN LINK SET", t1);
									if(t2>t1)
										configuredHeaders[3].setLength(t2-t1);
								}
							}
						}
						reader.skipLines(1);
						while(!reader.isEOL()) {
							parseLink(map, reader, ctx, configuredHeaders);
						}
					} else
						System.err.println("Skip : "+s);
				} else if(headerKey.equals(tables[2])) {//SIGNALLING LINKS
					if(s.contains("EXTERN")) {
						reader.readUntilEOL(sb).skipEOLs();
						s = sb.toString();
						int t1 = s.indexOf("LINK SET");
						int t2 = s.indexOf("STATE", t1);
						if(t1>0) {
							configuredHeaders[0].setLength(t1);
							if(t2>t1) {
								configuredHeaders[1].setLength(t2-t1);
								t1 = s.indexOf("UNIT", t2);
								if(t1>t2) {
									configuredHeaders[2].setLength(t1-t2);
									t2 = s.indexOf("TERM", t1);
									if(t2>t1) {
										configuredHeaders[3].setLength(t2-t1);
										t1 = s.indexOf("FUNCT", t2);
										if(t1>t2) {
											configuredHeaders[4].setLength(t1-t2);
											t2 = s.indexOf("TERM", t1);
											if(t2>t1) {
												configuredHeaders[5].setLength(t2-t1);
												t1 = s.indexOf("PCM-TSL", t2);
												if(t1>t2) {
													configuredHeaders[6].setLength(t1-t2);
													t2 = s.indexOf("PCM-TSL", t1+1);
													if(t2>t1) {
														configuredHeaders[7].setLength(t2-t1);
														t1 = s.indexOf("RATE", t2);
														if(t1>t2)
															configuredHeaders[8].setLength(t1-t2);
													}
												}
											}
										}
									}
								}
							}
						}
						reader.skipLines(1);
						while(!reader.isEOL())
							parseLinks(map, reader, ctx, configuredHeaders);
					} else
						System.err.println("Skip : "+s);
				} else if(headerKey.equals(tables[3])) {//M3UA BASED LINKS
					if(s.startsWith("M3UA")) {
						reader.readUntilEOL(sb).skipEOLs();
						s = sb.toString();
						int t1 = s.indexOf("LINK SET");
						int t2 = s.indexOf("SET", t1+8);
						if(t1>0) {
							configuredHeaders[0].setLength(t1);
							if(t2>t1) {
								configuredHeaders[1].setLength(t2-t1);
								t1 = s.indexOf("STATE", t2);
								if(t1>t2)
									configuredHeaders[2].setLength(t1-t2);
							}
						}
						reader.skipLines(1);
						while(!reader.isEOL())
							parseM3ua(map, reader, ctx, configuredHeaders);
					} else
						System.err.println("Skip : "+s);
				}
				reader.skipEOLs();
			}
		}
	}
	protected void parseM3ua(Map<String, Object> map, Parser reader, Context context, ConfiguredHeader[] header) throws IOException{
		map = new LinkedHashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		int lastHeader = header.length-1;
		int line = reader.getLine();
		for (int i = 0; i < header.length; i++) {
			ConfiguredHeader configuredHeader = header[i];
			if(lastHeader==i)
				reader.readUntilEOL(sb).skipEOL();
			else
				reader.read(sb, configuredHeader.getLength());
			
			final String s = sb.toString().trim();
			map.put(configuredHeader.getName(), s);
		}
		context.setTableName(T_NAME+"_"+tables[3].replace(" ", "_"));
		listener.onReadyData(context, map, line);
		
	}

	protected void parseLinks(Map<String, Object> map, Parser reader, Context context, ConfiguredHeader[] header) throws IOException{
		map = new LinkedHashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		int lastHeader = header.length-1;
		int line = reader.getLine();
		for (int i = 0; i < header.length; i++) {
			ConfiguredHeader configuredHeader = header[i];
			if(lastHeader==i)
				reader.readUntilEOL(sb).skipEOL();
			else
				reader.read(sb, configuredHeader.getLength());
			
			final String s = sb.toString().trim();
			map.put(configuredHeader.getName(), s);
		}
		context.setTableName(T_NAME+"_"+tables[2].replace(" ", "_"));
		listener.onReadyData(context, map, line);

	}

	protected void parseLink(Map<String, Object> map, Parser reader, Context context, ConfiguredHeader[] header) throws IOException{
		map = new LinkedHashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		int lastHeader = header.length-1;
		int line = reader.getLine();
		for (int i = 0; i < header.length; i++) {
			ConfiguredHeader configuredHeader = header[i];
			if(lastHeader==i)
				reader.readUntilEOL(sb).skipEOL();
			else
				reader.read(sb, configuredHeader.getLength());
			
			final String s = lastHeader==i ? sb.toString().trim().replaceAll("\\s+", ",") : sb.toString().trim();
			if(configuredHeader.copied && s.length() > 0){
				buffer.put(configuredHeader.getName(), s);
			}
			if(configuredHeader.copied)
				map.put(configuredHeader.getName(), buffer.get(configuredHeader.getName()));
			else
				map.put(configuredHeader.getName(), s);
		}
		
		if(!reader.isEOL()){
			line = reader.getLine();
			reader.readUntilEOL(sb).skipEOL();
			if(sb.toString().contains("LINK TEST")){
				map.put("LINK_TEST", sb.toString().trim());
			}else{
				context.setTableName(T_NAME+"_"+tables[1].replace(" ", "_"));
				listener.onReadyData(context, map, line);
				map = new LinkedHashMap<String, Object>();
				parse0(map, sb, header);
			}
			reader.skipEOL();
		}else
			reader.skipEOL();
		context.setTableName(T_NAME+"_"+tables[1].replace(" ", "_"));
		listener.onReadyData(context, map, line);
	}

	protected void parseRoute(Map<String, Object> map, Parser reader, Context context, ConfiguredHeader[] header) throws IOException{
		StringBuilder sb = new StringBuilder();
		int lastHeader = header.length-2;
		boolean loop = true;
		int line = reader.getLine();
		for (int i = 0; i < header.length-1 && loop; i++) {
			ConfiguredHeader configuredHeader = header[i];
			loop = true;
			if(lastHeader==i)
				reader.readUntilEOL(sb).skipEOL();
			else if(2==i && reader.isEqual('O')){
				reader.readUntilEOL(sb).skipEOL();
				loop = false;
			}else
				reader.read(sb, configuredHeader.getLength());
			
			final String s = sb.toString().trim();
			if(configuredHeader.copied && s.length() > 0){
				buffer.put(configuredHeader.getName(), s);
			}
			if(configuredHeader.copied)
				map.put(configuredHeader.getName(), buffer.get(configuredHeader.getName()));
			else
				map.put(configuredHeader.getName(), sb.toString().trim());
		}
		
		context.setTableName(T_NAME+"_"+tables[0].replace(" ", "_"));
		listener.onReadyData(context, map, line);

	}

	protected void parse0(Map<String, Object> map, StringBuilder sb, ConfiguredHeader[] header) throws IOException{
		int lastHeader = header.length-1;
		int pos = 0;
		String s = "";
		for (int i = 0; i < header.length; i++) {
			ConfiguredHeader configuredHeader = header[i];
			if(lastHeader==i){
				s = sb.substring(pos).trim().replaceAll("\\s+", ",");
			}else{
				s = sb.substring(pos, pos+configuredHeader.getLength()).trim();
				pos += configuredHeader.getLength();
			}
			
			if(configuredHeader.copied && s.length() > 0){
				buffer.put(configuredHeader.getName(), s);
			}
			if(configuredHeader.copied)
				map.put(configuredHeader.getName(), buffer.get(configuredHeader.getName()));
			else
				map.put(configuredHeader.getName(), s);
		}
	}
}
