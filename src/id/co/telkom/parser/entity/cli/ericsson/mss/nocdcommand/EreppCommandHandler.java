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


public class EreppCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private final Map<String, ConfiguredHeader[]> headersMap;
	private Map<String, Object> buffer = new LinkedHashMap<String, Object>();
	private String[] bufferHeader;
	private boolean isHeader=true;
	private int counter=0;
	public EreppCommandHandler(Parser reader, DataListener listener, String command, String params, Map<String, ConfiguredHeader[]> headersMap) {
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
		Map<String, Object> mapFirst = new LinkedHashMap<String, Object>();
		ConfiguredHeader[] header = null;
		boolean check = false;
		boolean isFirst =true;
		while(!isStartExecution && !reader.isEOF() && !reader.isEqual('<')){
			if(reader.isEqual('/')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("/*** COMMAND NOT FOUND ***/")){
					listener.onError(reader.getLine(), ctx, reader.getColumn(), sb.toString());
					done();
					return;
				}else{
					System.err.println("Skip : "+sb);
				}
			}else if(reader.isEqual('E')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("EVENT REPORTING RESULT")){
					isStartExecution = true;
				}
			}else{
				reader.readUntilEOL(sb);
				System.err.println("Skip : "+sb);
			}
			reader.skipEOLs();
		}
		if(!isStartExecution){
			done();
			return;
		}
		
		while(!isDone() && !reader.isEOF() && !reader.isEqual('<')){
			if(reader.isEqual('E')){
//				t_name = null;
				reader.readUntilEOL(sb).skipEOL();
				if(sb.indexOf("ENUM HEADER") >-1){
					header = headersMap.get("FIRST");
					parse(mapFirst, reader, header, check);
					ctx.setTableName(getCommand());
					if(!isFirst&&!mapFirst.isEmpty()){
						counter++;
						mapFirst.put("PARAM_ID", ((long)ctx.date.getTime())+counter);
						listener.onReadyData(ctx, mapFirst, reader.getLine());
						mapFirst = new LinkedHashMap<String, Object>();
					}else
					if(isFirst)
						 isFirst=false;
					reader.skipEOLs();
				}else{
//					System.err.println("Skip : "+sb);
					parseChild(sb.toString(),ctx,listener);
				}
			}
			else{
				reader.readUntilEOL(sb);
//				System.err.println("Skip : "+sb);
				parseChild(sb.toString(),ctx,listener);
			}
			reader.skipEOLs();
			
		}
	}
	
	private void parseChild(String s, Context ctx, DataListener listener){
		if(s.startsWith("END"))
			done();
		if(!s.trim().equals("")){
		String[] splitted =s.split("\\s+");
		
		if(isHeader){
			bufferHeader=splitted;
		}else{
			if(bufferHeader.length==splitted.length){
				Map<String, Object> child = new LinkedHashMap<String, Object>();
				ctx.setTableName(getCommand()+"_PARAM");
				for (int i=0;i<bufferHeader.length;i++){
//					child.put(bufferHeader[i], splitted[i]);
					child.put("PARAM_KEY", bufferHeader[i]);
					child.put("PARAM_VALUE", splitted[i]);
					child.put("PARAM_ID", ((long)ctx.date.getTime())+counter);
					listener.onReadyData(ctx, child, reader.getLine());
					child = new LinkedHashMap<String, Object>();
				}
				
				
				
			}
		}
			
		isHeader=!isHeader;
		}
	}
	
	protected void parse(Map<String, Object> map, Parser reader, ConfiguredHeader[] header, boolean check) throws IOException{
		StringBuilder sb = new StringBuilder();
		final int lastIdx = header.length-1;
		for (int i = 0; !isDone() && i <= lastIdx; i++) {
			ConfiguredHeader configuredHeader = header[i];
			if(lastIdx==i){
				reader.readUntilEOL(sb);
			}
			else{
				reader.read(sb, configuredHeader.getLength());
				if(check && i==0){
					if(sb.toString().startsWith("END")){
						done();
					}
				}
			}
			if(!isDone()){
				String s = sb.toString().trim();
				
				if(s.length()>0){
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
