package id.co.telkom.parser.entity.cli.ericsson.mss.nocdcommand;


import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.common.charparser.AbstractCommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;


public class DirCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private final ConfiguredHeader[] headers;
	
	public DirCommandHandler(Parser reader, DataListener listener, String command, String params, ConfiguredHeader[] headers) {
		super(command, params);
		this.reader = reader;
		this.listener=listener;
		this.headers=headers;
	}
	
	@Override
	public void handle(Context ctx)
			throws IOException {
		Map<String, Object> map= new LinkedHashMap<String, Object>();
		ctx.setTableName(getCommand());
		StringBuilder sb = new StringBuilder();
		reader.skipLines(1).skipEOLs().readUntilEOL(sb).skipEOLs();
		if (sb.toString().startsWith("The system cannot")) {
			done();
			return;
		}
		listener.onBeginTable(reader.getLastReadLine(), ctx);

		while(!isDone()&&!reader.isEqual('<') &&!reader.isEqual('>') && !reader.isEOF() && !reader.isEOL()) {
			if(reader.isNumber()){
				for(int i=0; i<headers.length;i++){
					if(i==headers.length-1){
						reader.readUntilEOL(sb).skipEOLs();
						map.put(headers[i].getName(), sb.toString().trim());
						if(!map.get("SIZE").equals("<DIR>"))
						listener.onReadyData(ctx, map, reader.getLine());
						map= new LinkedHashMap<String, Object>();
					}else{
						reader.read(sb, headers[i].length);
						map.put(headers[i].getName(), sb.toString().trim());
					}
					reader.skipEOLs();
				}
			}else{
				reader.readUntilEOL(sb).skipEOL();
//				System.out.println("skip: "+sb.toString());
			}
			reader.skipEOLs();
		}
		done();
		return;
	}
	
}
