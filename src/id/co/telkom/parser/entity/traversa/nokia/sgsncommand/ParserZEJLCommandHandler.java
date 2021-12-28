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

public class ParserZEJLCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	@SuppressWarnings("unused")
	private Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	private final String T_NAME;
	private static final Logger logger = Logger.getLogger(ParserZEJLCommandHandler.class);
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	private Map<String, Object> header = new LinkedHashMap<String, Object>();
	@SuppressWarnings("unused")
	private String Param;
	private boolean isStartExecution;
	private String NSEI, PAPU, RAC;
	public ParserZEJLCommandHandler(
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
			}if (sb.toString().startsWith("NETWORK CONFIGURATION DATA OUTPUT")) {
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
			if(reader.isEqual('N')){
				reader.readUntilEOL(sb).skipEOL();
				if(sb.toString().startsWith("NSEI")){
					header = new LinkedHashMap<String, Object>();
					if(sb.toString().contains("-"))
						NSEI = sb.toString().trim().split("-")[1].trim();
					reader.readUntilEOL(sb).skipEOL();
					if(sb.toString().trim().startsWith("PAPU") & sb.toString().contains("-"))
						PAPU = sb.toString().trim().split("-")[1].trim();
					header.put("NSEI", NSEI);
					header.put("PAPU", PAPU);
					if(reader.isEOL()){
						//ready 1 here child kosong
						header.put("MCC", "-");
						header.put("MNC", "-");
						header.put("LAC", "-");
						header.put("RAC", "-");
						header.put("CI", "-");
						header.put("BVCI", "-");
						header.put("STATE", "-");
//						System.out.println(header);
						listener.onReadyData(ctx, header, reader.getLine());
						header = new LinkedHashMap<String, Object>();
					}
				}
			}else if(reader.isEqual('M')){
				reader.readUntilEOL(sb).skipEOL();
				if(sb.toString().startsWith("MCC")){
					for(String s : sb.toString().trim().split(" ")){
						if(s.contains("-"))
							header.put(s.split("-")[0], s.split("-")[1]);
					}
					reader.readUntilEOL(sb).skipEOL();
					if(sb.toString().trim().startsWith("RAC") & sb.toString().contains("-")){
						RAC = sb.toString().trim().split("-")[1].trim();
						header.put("RAC", RAC);
					}
				}
			}else if(reader.isEqual(' ')){
				reader.readUntilEOL(sb).skipEOL();
				if(sb.toString().trim().startsWith("CI")){
					map = new LinkedHashMap<String, Object>();
					for(String s : sb.toString().trim().split(" ")){
						if(s.contains("-"))
							map.put(s.split("-")[0], s.split("-")[1]);
					}
					//ready oke
					map.putAll(header);
					listener.onReadyData(ctx, map, reader.getLine());
					map = new LinkedHashMap<String, Object>();
				}
			}else{
				reader.readUntilEOL(sb).skipEOL();
			}
			reader.skipEOLs();
		}
		done();
		return;
	}
	
	
}
