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


public class ZedoCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	@SuppressWarnings("unused")
	private String commandParams;
	private static final String PROPERTIES = "PROPERTIES";
	private static final String CIRCUIT_POOLS = "CIRCUIT_POOLS";
	private static final String BTSS = "BTSS";
	private final Map<String, ConfiguredHeader[]> headersMap;
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	private final String TABLENAME;
	private final DataListener listener;
		
	
	public ZedoCommandHandler(Parser reader, DataListener listener, String command, String params, Map<String, ConfiguredHeader[]> headersMap, String tableName) {
		super(command, params);
		this.TABLENAME=tableName;
		this.reader = reader;
		this.listener = listener;
		this.headersMap = headersMap;
		this.commandParams=params;
	}
	
	@Override
	public void handle(Context ctx)
			throws IOException {
		StringBuilder sb = new StringBuilder();
		boolean isStartExecution = false;
		boolean isParent = true;
		
		while(!isStartExecution && !reader.isEOL() && !reader.isEqual('<')){
			if(reader.isEqual('/')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("/*** COMMAND NOT FOUND ***/")){
					done();
					return;
				}else{
					System.err.println("Skip : "+sb);
				}
			}else if(reader.isEqual('M')){
				reader.readUntil(' ', sb);
				if(sb.toString().equals("MSCi")){
					reader.skipWhile(' ').readUntil(' ', sb).skipUntilEOL();
					ctx.setNe_id(sb.toString());
					isStartExecution = true;
					reader.skipLines(1);//skip OUTPUTTING BASE
				}else{
					reader.readUntilEOL(sb);
					System.err.println("Skip : "+sb);
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
		}else{
			while(!reader.isEOL() && !reader.isEqual('<')){
				if(reader.isEqual('C'))
				{
					reader.readUntilEOL(sb);
					if(sb.toString().equals("COMMAND EXECUTED")){
						done();
						return;
					}else if(sb.toString().startsWith("CIRCUIT POOLS")){
						reader.skipEOL();
						final ConfiguredHeader[] configuredHeaders = headersMap.get(CIRCUIT_POOLS);
						while(!reader.isEOL()){
							parseCircuit(map, reader, ctx, configuredHeaders);
							reader.skipEOL();
						}
					}else {
						System.err.println("Unexpected character in line "+reader.getLine()+" : "+sb);
					}
					
				}else if(reader.isEqual('B')){
					int pos = 0;
					if(isParent){
						final ConfiguredHeader[] configuredHeaders = headersMap.get(PROPERTIES);
						while(!reader.isEOL()){
							if(pos<1)
								parseParent(map, reader, ctx, headersMap.get(getCommand()));
							else
								parseProperty(map, reader, ctx, configuredHeaders);
							reader.skipEOL();
							pos++;
						}
						
					}else{
						final ConfiguredHeader[] configuredHeaders = headersMap.get(BTSS);
						reader.skipLines(3);
						while(!reader.isEOL()){
							parse(map, reader, ctx, configuredHeaders);
							listener.onReadyData(ctx, map, reader.getLine() );
							map = new LinkedHashMap<String, Object>();
							reader.skipEOL();
						}
					}
					isParent = !isParent;
				}else{
					reader.readUntilEOL(sb);
					System.err.println("skip : "+sb);
				}
				reader.skipEOLs();
			}
		}
	}
	
	protected void parse(Map<String, Object> map, Parser reader, Context context, ConfiguredHeader[] header) throws IOException{
		StringBuilder sb = new StringBuilder();
		int lastHeader = header.length-1;
		for (int i = 0; i < header.length && !reader.isEOL(); i++) {
			if(i==lastHeader)
				reader.readUntilEOL(sb);
			else
				reader.read(sb, header[i].getLength());
			map.put(header[i].getName(), sb.toString().trim());
		}
		map.put("t_name", BTSS);
		context.setTableName(this.TABLENAME+"_"+BTSS);
	}

	private void parseCircuit(Map<String, Object> map, Parser reader, Context context, ConfiguredHeader[] header) throws IOException {
		StringBuilder sb = new StringBuilder();
		map = new LinkedHashMap<String, Object>();
		reader.skipUntil(':').readUntil(':', sb);
		String param = sb.toString().trim().replaceAll("\\.\\.+", "");
		reader.readUntilEOL(sb);
		map.put("PARAM", param.trim());
		map.put("VALUE", sb.toString().trim().replaceAll("\\s\\s+", ","));
		
		//System.out.println(map);
		context.setTableName(this.TABLENAME+"_"+CIRCUIT_POOLS);
		listener.onReadyData(context, map, reader.getLine() );
		map = new LinkedHashMap<String, Object>();
	}
//
	protected void parseParent(Map<String, Object> map, Parser reader, Context context, ConfiguredHeader[] header) throws IOException{
		StringBuilder sb = new StringBuilder();
		map = new LinkedHashMap<String, Object>();
		for (int i = 0; i < 2; i++) {
			reader.readUntil('.', sb);
			String param = sb.toString().trim().replaceAll("\\s+", "_");
			reader.skipUntil(':').readUntilEOL(sb);
			map.put(param, sb.toString());
			reader.skipEOL();
		}
		//System.out.println(map);
		context.setTableName(TABLENAME);
		listener.onReadyData(context, map, reader.getLine() );
		map = new LinkedHashMap<String, Object>();
	}
	
	private void parseProperty(Map<String, Object> map, Parser reader, Context context, ConfiguredHeader[] header) throws IOException{
		map = new LinkedHashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		
		reader.readUntil('.', sb);
		String param = sb.toString().trim().replaceAll("\\s+", "_");
		reader.skipUntil(':').readUntilEOL(sb);
		map.put("PARAM", param);
		map.put("VALUE", sb.toString().trim());
		//System.out.println(map);
		context.setTableName(this.TABLENAME+"_"+PROPERTIES);
		listener.onReadyData(context, map, reader.getLine() );
		//yyn, convert SPC to dec
		if (param.charAt(0)=='S' && param.equals("SIGNALLING_POINT_CODE"))
		{
			map.put("PARAM", "SIGNALLING_POINT_CODE_DEC");
			String Val =sb.toString().trim();
			int tmpt = Val.indexOf(" ") > -1 ? Val.indexOf(" ")+1 : 0; 
			map.put("VALUE", Val.contains("-") ? Val :  Integer.parseInt(Val.substring(0, tmpt).trim(), 16));
			listener.onReadyData(context, map , reader.getLine());
			map = new LinkedHashMap<String, Object>();
		}
	}


}