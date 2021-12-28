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

public class ParserM3RSPCommandHandler extends AbstractCommandHandler implements MscCommandHandler	{
	
	
	private final Parser reader;
	private final DataListener listener;
	private final String T_NAME;
	@SuppressWarnings("unused")
	private final GlobalBuffer gb;
	private static final Logger logger = Logger.getLogger(ParserM3RSPCommandHandler.class);
	private Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();

	
	public ParserM3RSPCommandHandler(
			Parser reader, 
			DataListener listener, 
			String command, 
			String params, 
			Map<String, ConfiguredHeader[]> headersMap,
			AbstractInitiator cynapseInit) {
		super(command, params);
		this.reader = reader;
		this.listener=listener;
		this.headersMap=headersMap;
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
		String key="";

		while(!isStartExecution && !reader.isEOF() && !reader.isEqual('<')){
			if(reader.isEqual('/')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("/*** COMMAND NOT FOUND ***/")){
					done();
					return;
				}else{
					System.err.println("Skip : "+sb);
				}
			}else if(reader.isEqual('M')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("M3UA ROUTING DATA")){
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
			if(reader.isEqual('D')){
				reader.skipLines(1);
			}
			else	
			if(reader.isAlphaNumeric()){//NAME
				ctx.setTableName(T_NAME);
		
					ConfiguredHeader[] configuredHeaders = headersMap.get(getCommand());

									
					parse(map, reader, ctx, configuredHeaders);
					key=(String)map.get("DEST");
					listener.onReadyData(ctx, map, reader.getLine());
					map=new LinkedHashMap<String, Object>();

			}else 
			if(reader.isEqual(' ')){
				reader.readUntilEOL(sb).skipEOL();
				if(sb.toString().contains("SAID             PRIO  ")){
				    ctx.setTableName(T_NAME+"_SAID");

					while(!reader.isEOL()){
						ConfiguredHeader[] configuredHeaders = headersMap.get(getCommand()+"_DEST");
						
						reader.skipUntilAlphabet();
						parseChild(map, reader, ctx, configuredHeaders,key);

						reader.skipEOL();
					
						listener.onReadyData(ctx, map, reader.getLastReadLine());	
						map=new LinkedHashMap<String, Object>();
						
					}
					
				}
			
			}else 
			if(reader.isEqual('E')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("END")){
					done();
					return;
				}
				else
					System.err.println("Unexpected character found : "+sb);
			}else{
				reader.readUntilEOL(sb);
				System.err.println("Skip : "+sb);
			}
			reader.skipEOLs();
		}
	}
	
	protected void parseChild(Map<String, Object> map, Parser reader, Context ctx, ConfiguredHeader[] header,String key) throws IOException{
		map.clear();
		StringBuilder sb = new StringBuilder();
		final int lastIdx = header.length-1;
		map.put("KEY_DEST", key);
		for (int i = 0; i <= lastIdx; i++) {
			ConfiguredHeader configuredHeader = header[i];
			if(lastIdx==i){
				reader.readUntilEOL(sb);
			}else{
				reader.read(sb, configuredHeader.getLength());
			}
			
			String s = sb.toString().trim();
			map.put(configuredHeader.getName(), s);
		}

	}
	
	protected void parse(Map<String, Object> map, Parser reader, Context ctx, ConfiguredHeader[] header) throws IOException{
		StringBuilder sb = new StringBuilder();
		final int lastIdx = header.length-1;
		for (int i = 0; i <= lastIdx; i++) {
			ConfiguredHeader configuredHeader = header[i];
			if(lastIdx==i){
				reader.readUntilEOL(sb);
			}else{
				reader.read(sb, configuredHeader.getLength());
				if( i==0){
					if(sb.toString().startsWith("END")) {
						done();	
						return;
					}
				}
			}
			
			String s = sb.toString().trim();
			map.put(configuredHeader.getName(), s);
		}

	}
}
