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

public class ZtutCommandHandlerCLR  extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final String tableMaster;
	private final DataListener listener;
	private final Map<String, ConfiguredHeader[]> headersMap;
	private boolean isStartExecution;
	
	public ZtutCommandHandlerCLR(Parser reader,String command, String params, DataListener listener, String tableMaster, Map<String, ConfiguredHeader[]> headersMap) {
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
			}else if(sb.toString().startsWith("OUTPUT INTERVAL")){
				header=headersMap.get("TUT_CLR");
				isStartExecution = true;
			}
			else{
				System.err.println("Skips : "+sb);
			}
			reader.skipEOLs();
		}
		
		if(!isStartExecution||header==null){
			done();
			return;
		}else{
			while(!reader.isEOF() && !reader.isEqual('<')){
				boolean isCandidateExit = reader.isEqual('C');
				reader.readUntilEOL(sb).skipEOLs();
				if(isCandidateExit && sb.toString().trim().equals("COMMAND EXECUTED")){
					done();
					return;
				}else if (sb.toString().startsWith("SIGNALLING  ")){
					reader.skipEOL().read();
					while(!reader.isEOL()){
						
						if(reader.isEqual('E')){
							reader.readUntilEOL(sb);
							if(sb.toString().startsWith("END OF REPORT")){
								reader.skipEOL();
								break;
							}
						}else{
							map = new LinkedHashMap<String, Object>();
							parse(map, reader, header);
							if(!map.isEmpty()){
								map.put("t_name", "CLR");
								listener.onReadyData(ctx, map, reader.getLine());
								map = new LinkedHashMap<String, Object>();
							}
						}
						reader.skipEOL();
					}
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
