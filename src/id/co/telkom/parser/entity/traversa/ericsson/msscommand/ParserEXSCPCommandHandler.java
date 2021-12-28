package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.AbstractCommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;

public class ParserEXSCPCommandHandler extends AbstractCommandHandler implements MscCommandHandler{
	private int side = -1;
	private final Parser reader;
	private final DataListener listener;
	private final Map<String, ConfiguredHeader[]> headers;
	private final String T_NAME;
	@SuppressWarnings("unused")
	private final GlobalBuffer gb;
	private static final Logger logger = Logger.getLogger(ParserDBTSPCommandHandler.class);
	private Map<String, Object> buffer = new LinkedHashMap<String, Object>();
	
	public ParserEXSCPCommandHandler(
			Parser reader, 
			DataListener listener, 
			String command, 
			String params, 
			Map<String, ConfiguredHeader[]> headers,
			AbstractInitiator cynapseInit) {
		super(command, params);
		this.reader = reader;
		this.listener=listener;
		this.headers=headers;
		this.T_NAME=command;
		this.gb=(GlobalBuffer)cynapseInit.getMappingModel();
	}
	
	@Override
	public void handle(Context ctx) throws IOException {
		logger.info(T_NAME);
		ctx.setTableName(T_NAME);
		StringBuilder sb = new StringBuilder();
		boolean isStartExecution = false;
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Map<String,Object> mapFirst=new LinkedHashMap<String, Object>();
		ConfiguredHeader[] configuredHeaders;

		while(!isStartExecution && !reader.isEOF() && !reader.isEqual('<')){
			if(reader.isEqual('/')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("/*** COMMAND NOT FOUND ***/")){
					listener.onError(reader.getLine(), ctx, reader.getColumn(), sb.toString());
					done();
					return;
				}else{
					//System.err.println("Skip : "+sb);
				}
			}else if(reader.isEqual('S')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("SEMIPERMANENT CONNECTION DATA")){
					isStartExecution = true;
				}
			}else{
				reader.readUntilEOL(sb);
				//System.err.println("Skip : "+sb);
			}
			reader.skipEOLs();
		}
		if(!isStartExecution){
			done();
			return;
		}
		
		while(!isDone() && !reader.isEOF() && !reader.isEqual('<')){
			if(reader.isEqual('N')){//NAME
				reader.readUntilEOL(sb).skipEOL();
				if(sb.toString().equals("NONE"))
					done();
				else
				if(sb.toString().contains("NAME")){
					buffer.clear();
					configuredHeaders= headers.get("FIRST");
					parse(mapFirst, reader, ctx, configuredHeaders);
					map.putAll(mapFirst);
					buffer.putAll(mapFirst);
					mapFirst=new LinkedHashMap<String, Object>();
				}
			
			}else
			if(reader.isEqual(' ')){
				reader.skipUntil(' ').readUntil(' ', sb).skipUntilAlphabet();
				if(sb.toString().contains("SIDE")){
					String sideString=sb.toString().substring("SIDE".length());
					side = Integer.parseInt(sideString);
				}else
				if(sb.toString().contains("="))	{
					map.putAll(buffer);
					String param1=sb.toString().substring("DEV=".length());
					map.put("SIDE",side);
					map.put("DEV", param1);
					reader.readUntilEOL(sb);
					map.put("SSTATE", sb.toString().trim());
				
					listener.onReadyData(ctx, map, reader.getLine());
					map=new LinkedHashMap<String, Object>();
					
					
				}else 
				if(sb.toString().contains("CH")){
				final String param1 = sb.toString().trim();
				reader.skipEOL().readUntilEOL(sb);
				final String param2 = sb.toString().trim();
	            final String[] params={param1,param2};
	            
				reader.skipEOL().readUntilEOL(sb);
				final String[] values=sb.toString().trim().split("\\s+");
				for (int i = 0; i < 2; i++) {
					map.putAll(buffer);
					map.put("PARAM", params[i]);
					map.put("VALUE", values[i]);
					map.put("SIDE", side);
					
					ctx.setTableName(T_NAME);
					listener.onReadyData(ctx, map, reader.getLine());
				    map=new LinkedHashMap<String, Object>();
					
					}
				}
					reader.skipEOL();
					
			}else if(reader.isEqual('E')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("END"))
					done();
				else
					System.err.println("Unexpected character found : "+sb);
			}else{
				reader.readUntilEOL(sb);
				//System.err.println("Skip : "+sb);
			}
			reader.skipEOLs();
		}
		
	}
	

	
	protected void parse(Map<String, Object> map, Parser reader, Context ctx, ConfiguredHeader[] header) throws IOException{
		map.clear();
		StringBuilder sb = new StringBuilder();
		int lastIdx = header.length-1;
		for (int i = 0; i <= lastIdx; i++) {
			ConfiguredHeader configuredHeader = header[i];
			if(lastIdx==i)
				reader.readUntilEOL(sb);
			else{
				reader.read(sb, configuredHeader.getLength());
			}
			
			String s = sb.toString().trim();
			map.put(configuredHeader.getName(), s);
		}

	}

}
