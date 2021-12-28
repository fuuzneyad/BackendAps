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


public class PlldpCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private final Map<String, ConfiguredHeader[]> headersMap;
	private Map<String, Object> buffer = new LinkedHashMap<String, Object>();
	
	public PlldpCommandHandler(Parser reader, DataListener listener, String command, String params, Map<String, ConfiguredHeader[]> headersMap) {
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
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		ConfiguredHeader[] header = null;
		boolean check = false;
		
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
			}else if(reader.isEqual('P')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("P")){
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
			if(reader.isEqual('I')){
				reader.readUntilEOL(sb).skipEOL();
				if(sb.indexOf("INT PLOAD") >-1){
					header = headersMap.get("FIRST");
					while(!isDone() && !reader.isEOF() && !reader.isEqual('<') && !reader.isEqual('I') ){
						check=reader.isEqual('E');
						parse(map, reader, header, check);
						ctx.setTableName(getCommand()+"_1");
						if(map.get(header[0].getName())!=null)
							listener.onReadyData(ctx, map, reader.getLine());						
						map = new LinkedHashMap<String, Object>();
						reader.skipEOLs();
					}
				}else
				if(sb.indexOf("INT OFFTCAP") >-1){
					header = headersMap.get("SECOND");
					while(!isDone() && !reader.isEOF() && !reader.isEqual('<') && !reader.isEqual('I')){
						check=reader.isEqual('E');
						parse(map, reader, header, check);
						ctx.setTableName(getCommand()+"_2");
						if(map.get(header[0].getName())!=null)
							listener.onReadyData(ctx, map, reader.getLine());
						map = new LinkedHashMap<String, Object>();
						reader.skipEOLs();
					}
				}else{
					System.err.println("Skip : "+sb);
				}
			}
			else if(reader.isEqual('E')){
				reader.readUntilEOL(sb).skipEOL();
				if(sb.toString().startsWith("END")){									
					done();
					return;
				}else{
					System.err.println("Skip : "+sb);
				}
			}
			else{
				reader.readUntilEOL(sb);
				System.err.println("Skip : "+sb);
			}
			reader.skipEOLs();
			
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
