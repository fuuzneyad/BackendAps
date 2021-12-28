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


public class ParserZMXOCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	private final String T_NAME;
	private static final Logger logger = Logger.getLogger(ParserZMXOCommandHandler.class);
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	@SuppressWarnings("unused")
	private final GlobalBuffer gb;
	@SuppressWarnings("unused")
	private String Param;
	
	public ParserZMXOCommandHandler(
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
		this.Param=params;
	}
	@Override
	public void handle(Context ctx)
			throws IOException {
		ctx.setTableName(T_NAME);
		StringBuilder sb = new StringBuilder();
		boolean isStartExecution = false;
		
		while(!isStartExecution && !reader.isEOL() && !reader.isEqual('<')){
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
				reader.readUntil(' ', sb);
				if(sb.toString().equals("MSCi")){
					reader.skipWhile(' ').readUntil(' ', sb).skipUntilEOL();
					ctx.setNe_id(sb.toString());
					isStartExecution = true;
				}else{
					reader.readUntilEOL(sb);
					System.err.println("Skip : "+sb);
					logger.error("Skip : "+sb);
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
				if(reader.isEqual('C')) {
					reader.readUntilEOL(sb);
					if(!sb.toString().equals("COMMAND EXECUTED")){
						System.err.println("Unexpected character in line "+reader.getLine()+" : "+sb);
					}
					done();
					return;
				}
				else if(reader.isEqual('T')) {
					reader.readUntilEOL(sb).skipEOL();
					if(sb.toString().startsWith("TMSI")) {
						while(!(reader.isEOF()||reader.isEqual('<'))){
							parseProperty(map, reader, ctx, headersMap.get(getCommand()));
							reader.skipEOL();
						}
					}
				}
				else {
					reader.readUntilEOL(sb).skipEOL();
					System.err.println("Skip : " + sb + ", " + reader.getLine());
				}
				reader.skipEOLs();
			}
		}
	}
	protected void parseProperty(Map<String, Object> map, Parser reader, Context ctx, ConfiguredHeader[] header) throws IOException{
		map = new LinkedHashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		reader.readUntilEOL(sb);
		String line = sb.toString();
		ctx.setTableName(T_NAME);
		if(line.contains(":")){
			map.put("PARAM", line.split(":")[0].trim());
			map.put("VALUE", line.split(":")[1].trim());
			listener.onReadyData(ctx, map, reader.getLine());
		}
	}
}
