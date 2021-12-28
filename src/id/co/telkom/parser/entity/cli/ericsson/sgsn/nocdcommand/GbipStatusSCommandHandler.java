package id.co.telkom.parser.entity.cli.ericsson.sgsn.nocdcommand;


import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.common.charparser.AbstractCommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;


public class GbipStatusSCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private Map<String, ConfiguredHeader[]> headersMap;
	public  final String T_NAME=getCommand();
	private Map<String, Object> map= new LinkedHashMap<String, Object>();
	private Map<String, Object> buffer = new LinkedHashMap<String, Object>();
	public GbipStatusSCommandHandler(Parser reader, DataListener listener, String command, String params, Map<String, ConfiguredHeader[]> headersMap) {
		super(command, params);
		this.reader = reader;
		this.listener=listener;
		this.headersMap=headersMap;
	}
	
	@Override
	public void handle(Context ctx)
			throws IOException {
		StringBuilder sb = new StringBuilder();
		boolean isStartExecution = false;
		while(!isStartExecution && !reader.isEOF() && !reader.isEqual('<')){
			 if(reader.isEqual('T')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("The following remote IP-end-points")){
					isStartExecution = true;
					reader.skipEOL().skipLines(1);
				}
			}else{
				reader.readUntilEOL(sb);
			}
			reader.skipEOLs();
		}
		listener.onBeginTable(reader.getLastReadLine(), ctx);

		while(!isDone()&&!reader.isEqual('<') &&!reader.isEqual('>') && !reader.isEOF() ) {
			if(reader.isEqual('R')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("Remote IP-end-point")){
					reader.skipEOL();
					ConfiguredHeader[] header =headersMap.get("CONFIGURED");
					ctx.setTableName(T_NAME+"_CONFIGURED");
					while (reader.isNumber()){
						Parse(reader,header);
						listener.onReadyData(ctx, map, reader.getLine());
						map = new LinkedHashMap<String, Object>();
						reader.skipEOL();
					}
				}else
					System.out.println("skip:"+sb);
			}else if(reader.isEqual('E')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("END")){
					done();
					return;
				}
			}else if(reader.isEqual('P')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("PTP BVC [NSEI-BVCI]")){
					reader.skipEOL();
					ConfiguredHeader[] header =headersMap.get("CONNECTED");
					ctx.setTableName(T_NAME+"_CONNECTED");
					while (reader.isNumber()){
						Parse(reader,header);
						listener.onReadyData(ctx, map, reader.getLine());
						map = new LinkedHashMap<String, Object>();
						reader.skipEOLs();
					}
				}else
					System.out.println("skip:"+sb);
			}else{
				reader.readUntilEOL(sb);
				//System.out.println("skip:"+sb);
			}
			reader.skipEOLs();
		}
		done();
		return;
	}
	
	private void Parse(final Parser reader, ConfiguredHeader[]  header) throws IOException{
		StringBuilder sb = new StringBuilder();
		final int lastIdx = header.length-1;
		for (int i = 0; !isDone() && i <= lastIdx; i++) {
			ConfiguredHeader configuredHeader = header[i];
			if(lastIdx==i)
				reader.readUntilEOL(sb);
			else{
				reader.read(sb, configuredHeader.getLength());
				if(i==0){
					if(sb.toString().startsWith("END")){ 
						done();						
					}
				}
			}
			if(!isDone()){
				String s = sb.toString().trim();
				
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
	
}
