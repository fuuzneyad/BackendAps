package id.co.telkom.parser.entity.traversa.nokia.msscommand;


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


public class ParserZWVJCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private final Map<String, ConfiguredHeader[]> headersMap;
	private final String T_NAME;
	private static final Logger logger = Logger.getLogger(ParserZWVJCommandHandler.class);
	@SuppressWarnings("unused")
	private final GlobalBuffer gb;
	
	public ParserZWVJCommandHandler(
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
	public void handle(Context ctx)
			throws IOException {
		StringBuilder sb = new StringBuilder();
		boolean isStartExecution = false;
		while (!isStartExecution && !reader.isEqual('<') && !reader.isEOL() ){
			reader.skipEOLs();
			
			if(reader.isEqual('M')){
				reader.readUntil(' ', sb);
				if(sb.toString().equals("MSCi")){
					reader.skipWhile(' ').readUntil(' ', sb).skipUntilEOL();
					ctx.setNe_id(sb.toString());
					isStartExecution = true;
				} else {
					reader.readUntilEOL(sb);
					logger.error("Skiping : "+sb);
				}
			}else if(reader.isEqual('/')){
				reader.readUntilEOL(sb);
				if (sb.toString().startsWith("/*** COMMAND NOT FOUND ***/")) {
					done();
					listener.onError(reader.getLine(), ctx, reader.getColumn(), sb.toString());
					return;
				}else
					System.err.println("Skiping : "+sb);
			}else{
				reader.readUntilEOL(sb);
				System.err.println("Skiping : "+sb);
			}
			reader.skipEOLs();
		}
		if(!isStartExecution){
			done();
			return;
		}else{
			reader.readUntilEOL(sb);
			String s = sb.toString().trim();
			Map<String, Object> map = null;
			while(!s.equals("COMMAND EXECUTED")){
				reader.skipLines(1);//skip header
				
				listener.onBeginTable(reader.getLine(), ctx);
				while(!reader.isEOL() && !reader.isEqual(' ')){
					map = new LinkedHashMap<String, Object>();
					ctx.setTableName(T_NAME+"_"+sb.toString().split(" ")[0]);
					parse(map, reader, headersMap.get(s));
					listener.onReadyData(ctx, map, reader.getLine());
					reader.skipEOL();
				}
				reader.skipLines(2).readUntilEOL(sb);
				s = sb.toString().trim();
			}
			done();
		}
	}
	
	protected void parse(Map<String, Object> map, Parser reader, ConfiguredHeader[] header) throws IOException{
		StringBuilder sb = new StringBuilder();
		int lastHeader = header.length-1;
		for (int i = 0; i < header.length; i++) {
			if(lastHeader==i)
				reader.readUntilEOL(sb);
			else
				reader.read(sb, header[i].getLength());
			map.put(header[i].getName(), sb.toString().trim());
		}
	}
}
