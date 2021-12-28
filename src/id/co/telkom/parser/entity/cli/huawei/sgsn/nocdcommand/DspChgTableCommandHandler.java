package id.co.telkom.parser.entity.cli.huawei.sgsn.nocdcommand;


import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.common.charparser.AbstractCommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;



public class DspChgTableCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private final ConfiguredHeader[] headers;
	private Map<String, Object> map= new LinkedHashMap<String, Object>();
	public DspChgTableCommandHandler(Parser reader, DataListener listener, String command, String params, ConfiguredHeader[] headers) {
		super(command, params);
		this.reader = reader;
		this.listener=listener;
		this.headers=headers;
	}
	
	@Override
	public void handle(Context ctx)
			throws IOException {
		StringBuilder sb = new StringBuilder();
		boolean isStartExecution = false;
		while(!isStartExecution && !reader.isEOF() && !reader.isEqual('[')){
			reader.readUntilEOL(sb);
			if(sb.toString().contains("The result is as follows:")){
				isStartExecution = true;
				reader.skipEOL().skipLines(2);
			}else{
				//System.out.println("skip:"+sb);
			}
			reader.skipEOLs();
		}
		
		if(!isStartExecution){
			done();
			return;
		}
		
		listener.onBeginTable(reader.getLastReadLine(), ctx);

		while(!isDone()&&!reader.isEqual('[') &&!reader.isEqual('(')  && !reader.isEOF() && !reader.isEOL()) {
			parse(map,reader,headers);
			listener.onReadyData(ctx, map, reader.getLine());
			map = new LinkedHashMap<String, Object>();
			reader.skipEOL();
		}
		done();
		return;
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
