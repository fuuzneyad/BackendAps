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

public class ZtutCommandHandlerTRF  extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final String tableMaster;
	private final DataListener listener;
	private final Map<String, ConfiguredHeader[]> headersMap;
	private boolean isStartExecution;
	
	public ZtutCommandHandlerTRF(Parser reader,String command, String params, DataListener listener, String tableMaster, Map<String, ConfiguredHeader[]> headersMap) {
		super(command, params);
		this.reader=reader;
		this.tableMaster=tableMaster;
		this.listener=listener;
		this.headersMap=headersMap;
	}

	@Override
	public void handle(Context ctx) throws IOException {
		ctx.setTableName(tableMaster);
		StringBuilder sb = new StringBuilder();
		Map<String, Object> map = null;
		ConfiguredHeader[] header = null;
			
		while (!isStartExecution && !reader.isEOL() && !reader.isEqual('<')){
			reader.skipEOLs().readUntilEOL(sb);
			
			if (sb.toString().startsWith("LOADING PROGRAM")) {				
				while(reader.isEOL() || reader.isEqual(' '))
					reader.read();
				reader.readUntil(' ', sb);
				if(sb.toString().equals("MSCi")){
					reader.skipWhile(' ').readUntil(' ', sb).skipUntilEOL();
					if(sb!=null && !sb.toString().trim().equals(""))
						ctx.setNe_id(sb.toString());					
				}else{
					reader.readUntilEOL(sb);
					System.err.println("Skip : "+sb);
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
			}else if(sb.toString().contains("TOTAL:")){
				header=headersMap.get("TUT");
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

		while(!reader.isEOL()){
				if (header!=null){
					map = new LinkedHashMap<String, Object>();
					parse(map, reader, header);
					listener.onReadyData(ctx, map, reader.getLine());
					reader.skipEOL();
				}else{
					reader.readUntilEOL(sb).skipEOL();
					if(sb.toString().trim().equals("END OF REPORT")||sb.toString().trim().equals("COMMAND EXECUTED")){
						reader.skipEOLs();
						done();
						return;
					}
					System.err.println("Skip : "+sb);
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
			map.put(header[i].getName(), sb.toString().replace(":", "").trim());
		}
	}
}
