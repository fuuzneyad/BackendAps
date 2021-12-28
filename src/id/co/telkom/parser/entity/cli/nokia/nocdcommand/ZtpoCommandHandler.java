package id.co.telkom.parser.entity.cli.nokia.nocdcommand;


import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.common.charparser.AbstractCommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;


public class ZtpoCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	@SuppressWarnings("unused")
	private String commandParams;
	private final Map<String, ConfiguredHeader[]> headersMap;
	private final String TABLENAME;
	private final DataListener listener;
	private boolean isStartExecution;
	
	public ZtpoCommandHandler(Parser reader, DataListener listener, String command, String params, Map<String, ConfiguredHeader[]> headersMap, String tableName) {
		super(command, params);
		this.TABLENAME=tableName;
		this.reader = reader;
		this.listener = listener;
		this.headersMap = headersMap;
		this.commandParams=params;
	}
	
	@Override
	public void handle(Context ctx)
			throws IOException {
		ctx.setTableName(TABLENAME);
		StringBuilder sb = new StringBuilder();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		ConfiguredHeader[] header = null;
		String CounterFlag =null;
		String Papu=null;
		String PapuGroup=null;
		@SuppressWarnings("unused")
		String headerCounter;	
		while (!isStartExecution && !reader.isEOL() && !reader.isEqual('<')){
			reader.skipEOLs().readUntilEOL(sb);
			
			if (sb.toString().startsWith("LOADING PROGRAM")) {				
				while(reader.isEOL() || reader.isEqual(' '))
					reader.read();
				reader.readUntil(' ', sb);
				if(sb.toString().equals("SGSN")){
					reader.skipWhile(' ').readUntil(' ', sb).skipUntilEOL();
					if(sb!=null && !sb.toString().trim().equals(""))
						ctx.setNe_id(sb.toString());					
				}else{
					reader.readUntilEOL(sb);
					System.err.println("Skipw : "+sb);
				}
			} else if(sb.toString().startsWith("/*** COMMAND NOT FOUND ***/")){
				listener.onError(reader.getLine(), ctx, reader.getColumn(), sb.toString());
				reader.skipEOLs();
				done();
				return;
			}else if(sb.toString().trim().equals("COMMAND EXECUTION FAILED")){
				listener.onError(reader.getLine(), ctx, reader.getColumn(), sb.toString());
				reader.skipEOLs();
				done();
				return;
			}else if(sb.toString().startsWith("HEALTH VIEW COUNTERS")){
				header=headersMap.get("CTR_GLOBAL");
				isStartExecution = true;
			}
			else{
				System.err.println("Skips : "+sb);
			}
			reader.skipEOLs();
		}
		
		if(!isStartExecution){
			done();
			return;
		}
		header=headersMap.get("CTR_GLOBAL");
		while(!reader.isEOL() ){
			if(reader.isNumber()){
				map.put("COUNTER_FLAG", CounterFlag);
				map.put("PAPU", Papu);
				map.put("PAPUGROUP", PapuGroup);
				parse(map, reader, header);
				ctx.setMo_id("PAPU="+Papu+"/PAPUGROUP="+PapuGroup);
				if(header[0].getName().equals("MEAS_SERIES"))
					map.put("t_name", "MEAS_SERIES");
				listener.onReadyData(ctx, map, reader.getLine());
				map = new LinkedHashMap<String, Object>();
				reader.skipEOLs();
			}else{
					reader.readUntilEOL(sb).skipEOLs();
					if(sb.toString().trim().startsWith("END OF REPORT")||sb.toString().trim().startsWith("COMMAND EXECUTED")){
						done();
						return;
					}else if(sb.toString().startsWith("PAPU ")){
						if(sb.toString().contains("GROUP"))
							PapuGroup=sb.toString().trim();else
								Papu=sb.toString().trim();
					}else if(sb.toString().startsWith("MM")){
						CounterFlag="MM_COUNTER";
					}else if(sb.toString().startsWith("SM")){
						CounterFlag="SM_COUNTER";
					}else if(sb.toString().startsWith("DATA")){
						CounterFlag="DATA_COUNTER";
					}else if(sb.toString().startsWith("SGSN USER")){
						CounterFlag="SGSN_USER_COUNTER";
					}else if(sb.toString().startsWith("COUNTERS ")){
						headerCounter=sb.toString();
					}else if(sb.toString().startsWith("MEASUREMENT PERIOD")){
						CounterFlag="MEAS_SERIES";
						header=headersMap.get("MEAS_SERIES");
					}else{
//						System.err.println("Skipee : "+sb);
					}
				}
			}
			
		if(sb.toString().trim().equals("COMMAND EXECUTED")){
			done();
			return;
		}
		
	}
	private void parse(Map<String, Object> map, Parser reader, ConfiguredHeader[] header) throws IOException {		
		StringBuilder sb = new StringBuilder();
		int lastHeader = header.length-1;
		for (int i = 0; i < header.length; i++) {
			if(lastHeader==i){
				reader.readUntilEOL(sb);
			}else
				reader.read(sb, header[i].getLength());
			map.put(header[i].getName(), sb.toString().trim());
		}
	}

}
