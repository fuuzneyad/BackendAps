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



public class GbfrStatusCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private final ConfiguredHeader[] headers;
	private Map<String, Object> map= new LinkedHashMap<String, Object>();
	private Map<String, Object> buffer= new LinkedHashMap<String, Object>();
	public GbfrStatusCommandHandler(Parser reader, DataListener listener, String command, String params, ConfiguredHeader[] headers) {
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
			reader.readUntilEOL(sb);
			if(sb.toString().contains("Bsc") && sb.toString().contains("Nsei")){
				isStartExecution = true;
				reader.skipEOL().skipLines(1);
			}else{
//				reader.readUntilEOL(sb);
				System.out.println("skip:"+sb);
			}
			reader.skipEOLs();
		}
		
		listener.onBeginTable(reader.getLastReadLine(), ctx);

		while(!isDone()&&!reader.isEqual('<') &&!reader.isEqual('>') && !reader.isEOF() && !reader.isEOL()) {
			reader.readUntilEOL(sb).skipEOL();
			if(sb.toString().startsWith("-----") || sb.toString().startsWith("END")){
				done();
				return;
			}
			else{
				Parse(reader,headers);
				if(!map.isEmpty())
				listener.onReadyData(ctx, map, reader.getLine());
				map = new LinkedHashMap<String, Object>();
			}
			reader.skipEOL();
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
				if(!s.trim().equals("")){
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
}
