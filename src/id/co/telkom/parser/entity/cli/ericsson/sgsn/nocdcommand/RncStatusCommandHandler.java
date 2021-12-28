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


public class RncStatusCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private final ConfiguredHeader[] headers;
	private Map<String, Object> map= new LinkedHashMap<String, Object>();
	public RncStatusCommandHandler(Parser reader, DataListener listener, String command, String params, ConfiguredHeader[] headers) {
		super(command, params);
		this.reader = reader;
		this.listener=listener;
		this.headers=headers;
	}
	
	@Override
	public void handle(Context ctx)
			throws IOException {
		ctx.setTableName(getCommand());
		StringBuilder sb = new StringBuilder();
		boolean isStartExecution = false;
		while(!isStartExecution && !reader.isEOF() && !reader.isEqual('<')){
			 if(reader.isEqual('r')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("rn     ri")){
					isStartExecution = true;
					reader.skipEOL().skipLines(1);
				}
			}else{
				reader.readUntilEOL(sb);
			}
			reader.skipEOLs();
		}
		listener.onBeginTable(reader.getLastReadLine(), ctx);

		while(!isDone()&&!reader.isEqual('<') &&!reader.isEqual('>') && !reader.isEOF() && !reader.isEOL()) {
			reader.readUntilEOL(sb).skipEOL();
			if(sb.toString().startsWith("-----")){
				done();
				return;
			}else{
				ProcessLine(sb.toString());
				listener.onReadyData(ctx, map, reader.getLine());
				map = new LinkedHashMap<String, Object>();
			}
			reader.skipEOL();
		}
		done();
		return;
	}
	
	private void ProcessLine(String s){
		String[] splitted = s.split("\\s+");
		if(splitted.length==headers.length){
			for(int i=0;i<headers.length;i++){
				map.put(headers[i].getName(), splitted[i].trim());
			}
		}
	}
}
