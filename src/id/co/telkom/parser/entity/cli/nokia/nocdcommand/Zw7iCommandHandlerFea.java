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

public class Zw7iCommandHandlerFea  extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final String tableMaster;
	private final DataListener listener;
	@SuppressWarnings("unused")
	private final Map<String, ConfiguredHeader[]> headersMap;
	private boolean isStartExecution;
	Map<String, Object> map = new LinkedHashMap<String, Object>();
	
	public Zw7iCommandHandlerFea(Parser reader,String command, String params, DataListener listener, String tableMaster, Map<String, ConfiguredHeader[]> headersMap) {
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
					if(sb.toString().contains("INFORMATION:")){
						isStartExecution = true;
					}else
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
			}else if(sb.toString().contains("INFORMATION:")){
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
		
		reader.skipLines(1);
		while( !reader.isEOF() && !reader.isEqual('<')){
			reader.readUntilEOL(sb);
			if(sb.toString().contains(":")){
				splitData(sb.toString());
				if(map.get("FEATURE_CAPACITY")!=null){
					listener.onReadyData(ctx, map, reader.getLine());
					map = new LinkedHashMap<String, Object>();
				}
			}else
			if(sb.toString().trim().startsWith("COMMAND EXECUTED")){
				done();
				return;
			}
			reader.skipEOLs();
		}
		done();
		return;
	}
	
	private  void splitData(String s){
		if(s.contains(":")){
			String[]ss=s.split(":");
			map.put(ss[0].trim().replace(" ", "_"), ss[1].trim().replace("=", "").replace(".", ""));
		}
	}
}
