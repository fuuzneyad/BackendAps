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

public class ParserB6OCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	@SuppressWarnings("unused")
	private Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	private final String T_NAME;
	private static final Logger logger = Logger.getLogger(ParserB6OCommandHandler.class);
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	@SuppressWarnings("unused")
	private String Param;
	private boolean isStartExecution;
	public ParserB6OCommandHandler(
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
			}if (sb.toString().startsWith("TAI-LAI-VLR")) {
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
			reader.readUntilEOL(sb).skipEOL();
			if(sb.toString().trim().startsWith("TAI")){
				reader.readUntilEOL(sb).skipEOL();
				String s = sb.toString().trim();
				String mcc = s.split("MCC\\: ")[1].trim().split("\\ ")[0];
				String mnc = s.split("MNC\\: ")[1].trim().split("\\ ")[0];
				String tacs = s.split("TACs ")[1].trim();
				map.put("TAI_MCC", mcc);
				map.put("TAI_MNC", mnc);
				map.put("TAI_TACS", tacs);
			}else
			if(sb.toString().trim().startsWith("LAI")){
				reader.readUntilEOL(sb).skipEOL();
				String s = sb.toString().trim();
				String mcc = s.split("MCC\\: ")[1].trim().split("\\ ")[0];
				String mnc = s.split("MNC\\: ")[1].trim().split("\\ ")[0];
				String lac = s.split("LAC ")[1].trim();
				map.put("LAI_MCC", mcc);
				map.put("LAI_MNC", mnc);
				map.put("LAI_LAC", lac);
			}				
			else
			if(sb.toString().trim().startsWith("VLRs")){
				reader.readUntilEOL(sb).skipEOL();
				map.put("VLRS", sb.toString().trim().replace(" ", ""));
				listener.onReadyData(ctx, map, reader.getLine());
				map = new LinkedHashMap<String, Object>();
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
