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



public class DspChgfileCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private final ConfiguredHeader[] headers;
	private Map<String, Object> map= new LinkedHashMap<String, Object>();
	
	public DspChgfileCommandHandler(Parser reader, DataListener listener, String command, String params, ConfiguredHeader[] headers) {
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
				reader.skipEOL().skipLines(1);
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
			reader.skipEOL();
		}
		if(!map.isEmpty())
			listener.onReadyData(ctx, map, reader.getLine());
		map = new LinkedHashMap<String, Object>();
		
		done();
		return;
	}
	private void parse(Map<String, Object> map, Parser reader, ConfiguredHeader[] header) throws IOException {		
		StringBuilder sb = new StringBuilder();
		reader.readUntilEOL(sb);
		if(sb.toString().contains("=")){
			String[] spt = sb.toString().split("=");
			map.put(spt[0].trim().toUpperCase()
					.replace(" ", "_")
					.replace("(MB)", "")
					.replace("(%)", ""), spt[1].trim());
		}
	}
}
