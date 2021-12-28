package id.co.telkom.parser.entity.traversa.nokia.sgsncommand;


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

public class ParserE61CommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	private final String T_NAME;
	private static final Logger logger = Logger.getLogger(ParserE61CommandHandler.class);
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	private Map<String, Object> head = new LinkedHashMap<String, Object>();
	@SuppressWarnings("unused")
	private String Param;
	private boolean isStartExecution;
	private String lastParam="";
	public ParserE61CommandHandler(
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
		this.Param=params;
	}
	@Override
	public void handle(Context ctx)
			throws IOException {
		ctx.setTableName(T_NAME);
		StringBuilder sb = new StringBuilder();
		while (!isStartExecution && !reader.isEOL() && !reader.isEqual('<')){
			reader.skipEOLs().readUntilEOL(sb);
			
			if (sb.toString().startsWith("LOADING PROGRAM")) {
				reader.skipEOLs().readUntil(' ', sb);
				if(sb.toString().equals("SGSN")){
					reader.skipWhile(' ').readUntil(' ', sb).skipUntilEOL();
					ctx.setNe_id(sb.toString());
				}
			} else if(sb.toString().startsWith("/*** COMMAND NOT FOUND ***/")){
				listener.onError(reader.getLine(), ctx, reader.getColumn(), sb.toString());
				reader.skipEOLs();
				done();
				return;
			}if (sb.toString().startsWith("RNC IN RADIO NETWORK")) {
				isStartExecution=true;
			}
			else{
				logger.error("Skip : "+sb);
			}
			reader.skipEOLs();
		}
		
		if(!isStartExecution){
			done();
			return;
		}
		
		while (!reader.isEOF() && !reader.isEqual('<')) {
			if(reader.isAlphabet()){
				reader.readUntilEOL(sb).skipEOL();
				if(sb.toString().contains("(") 
						&& sb.toString().contains(")") 
						&& sb.toString().contains(":") ){
					String param = sb.toString().split("\\(")[1].split("\\)")[0].trim();
					String val = sb.toString().split("\\:")[1].trim();
					head.put(param, val);
					lastParam = param;
				}else
				if(sb.toString().startsWith("ROUTING AREA IDENTITY LIST")){
					reader.skipLines(2).skipEOL();
					while(!reader.isEOL() && !reader.isEOF() && !reader.isEqual('<')){
						parse(map, reader, ctx, headersMap.get("MASTER"));
						reader.skipEOL();
						head.putAll(map);
						Map<String, Object> temp = new LinkedHashMap<String, Object>();
						temp.putAll(head);
						temp.putAll(map);
						listener.onReadyData(ctx, temp, reader.getLine());
						temp = new LinkedHashMap<String, Object>();
						map = new LinkedHashMap<String, Object>();
					}
					head = new LinkedHashMap<String, Object>();
				}
			}else if(reader.isEqual(' ')&&lastParam.equals("PAPU")){
				reader.readUntilEOL(sb).skipEOL();
				head.put("PAPU", head.get("PAPU")+sb.toString().trim());
			}else{
				reader.readUntilEOL(sb).skipEOL();
			}
			reader.skipEOLs();
		}
		done();
		return;
	}
	
	protected void parse(Map<String, Object> map, Parser reader, Context context, ConfiguredHeader[] header) throws IOException{
		StringBuilder sb = new StringBuilder();
		int lastHeader = header.length-1;
		for (int i = 0; i < header.length && !reader.isEOL(); i++) {
			if(i==lastHeader)
				reader.readUntilEOL(sb);
			else
				reader.read(sb, header[i].getLength());
			map.put(header[i].getName(), sb.toString().trim());
		}
	}
}
