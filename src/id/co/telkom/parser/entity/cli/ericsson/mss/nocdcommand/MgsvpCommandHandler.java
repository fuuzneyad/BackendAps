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


public class MgsvpCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private final Map<String, ConfiguredHeader[]> headersMap;
	private Map<String, Object> buffer = new LinkedHashMap<String, Object>();
	private Map<String,Object> sum = new LinkedHashMap<String, Object>();
	
	public MgsvpCommandHandler(Parser reader, DataListener listener, String command, String params, Map<String, ConfiguredHeader[]> headersMap) {
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
		String t_name = null;
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
			}else if(reader.isEqual('M')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("MT MOBILE")){
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
			if(reader.isEqual('H')){
				reader.readUntilEOL(sb).skipEOL();
				if(sb.indexOf("HLRADDR") >-1){
					{
						header = headersMap.get(getCommand());
						t_name = null;
					}
				}else
				{
					header = headersMap.get(getCommand());
					t_name = null;
				}
				while(!reader.isEOL() && !reader.isEqual('<')){
					map = new LinkedHashMap<String, Object>();
					check = reader.isEqual('E');//yyn
					parse(map, reader, header, check);
//					map.put("t_name", t_name);
					ctx.setTableName(getCommand());
					listener.onReadyData(ctx, map, reader.getLine());
					reader.skipEOL();
				}
			}else{
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("END")){//yyn					
					done();
					return;
				}else 
				if(sb.toString().trim().equals("TOTNSUB"))	{
					reader.skipEOL().readUntilEOL(sb);
					sum.put("TOTNSUB", sb.toString());
				}else
				if(sb.toString().trim().equals("TOTNSUBA"))	{
					reader.skipEOL().readUntilEOL(sb);
					sum.put("TOTNSUBA", sb.toString());
				}else
				if(sb.toString().startsWith("NSUBPR"))	{
					reader.skipEOL().readUntilEOL(sb);
					sum.put("NSUBPR", sb.toString());
				}else
				if(sb.toString().startsWith("NSUBXP"))	{
					reader.skipEOL().readUntilEOL(sb);
					sum.put("NSUBXP", sb.toString());
				}else	
				if(sb.toString().startsWith("NSUBGS"))	{
					reader.skipEOL().readUntilEOL(sb);
					sum.put("NSUBGS", sb.toString());
					t_name="SUMMARY";
//					sum.put("t_name", t_name);
					ctx.setTableName(getCommand()+"_"+t_name);
					listener.onReadyData(ctx, sum, reader.getLine());
				}else					
				System.err.println("Skipx : "+sb);
			}
			reader.skipEOLs();
		}
	}
	protected void parse(Map<String, Object> map, Parser reader, ConfiguredHeader[] header, boolean check) throws IOException{
		StringBuilder sb = new StringBuilder();
		final int lastIdx = header.length-1;
		for (int i = 0; !isDone() && i <= lastIdx; i++) {
			ConfiguredHeader configuredHeader = header[i];
			if(lastIdx==i)
				reader.readUntilEOL(sb);
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
