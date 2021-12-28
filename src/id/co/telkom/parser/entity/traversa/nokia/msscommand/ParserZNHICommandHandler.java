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


public class ParserZNHICommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private ConfiguredHeader[] headers;
	private final String T_NAME;
	private static final Logger logger = Logger.getLogger(ParserZNHICommandHandler.class);
	private boolean isStartExecution;
	@SuppressWarnings("unused")
	private final GlobalBuffer gb;
	
	public ParserZNHICommandHandler(
			Parser reader, 
			DataListener listener, 
			String command, 
			String params, 
			ConfiguredHeader[] headers,
			AbstractInitiator cynapseInit) {
		super(command, params);
		this.reader = reader;
		this.listener=listener;
		this.headers=headers;
		this.T_NAME=command;
		this.gb=(GlobalBuffer)cynapseInit.getMappingModel();
	}
	@Override
	public void handle(Context ctx)
			throws IOException {
		ctx.setTableName(T_NAME);
		StringBuilder sb = new StringBuilder();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Map<String, Object> mapFirst = new LinkedHashMap<String, Object>();
		
		while (!isStartExecution && !reader.isEOL() && !reader.isEqual('<')){
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
				}else{
					reader.readUntilEOL(sb);
					System.err.println("Skip : "+sb);
				}
			}else if(reader.isEqual('S')){
				reader.readUntilEOL(sb);
				if(sb.toString().trim().startsWith("SUBSYSTEM STATES")){
					isStartExecution = true;
				}else{
					logger.error("Skip : "+sb);
				}
			} 
			else if(reader.isEqual('C')){
				reader.readUntilEOL(sb);
				if(sb.toString().trim().equals("COMMAND EXECUTED")){
					done();
					return;
				}else{
					reader.readUntilEOL(sb);
					System.err.println("Skip : "+sb);
				}		
			}
			else{
				reader.readUntilEOL(sb);
			}

			reader.skipEOLs();
		}
		
		if(!isStartExecution){
			done();
			return;
		} 
			while(!reader.isEOF() && !reader.isEqual('<')  && isStartExecution){
				if(reader.isEqual('N')){
					reader.readUntilEOL(sb);
					if(sb.toString().startsWith("NETWORK:")){
						mapFirst = new LinkedHashMap<String, Object>();
						String ln = sb.toString().trim();
						mapFirst.put("NETWORK", ln.substring(ln.indexOf("NETWORK:")+"NETWORK:".length()).trim().split(" ")[0]);
						mapFirst.put("POINT_HD", ln.substring(ln.indexOf("POINT H/D:")+"POINT H/D:".length()).trim().split(" ")[0]);
						mapFirst.put("SP_NAME", ln.substring(ln.indexOf("SP NAME:")+"SP NAME:".length()).trim().split(" ")[0]);
					}else if(sb.toString().startsWith("NO H/D")){
						reader.skipLines(1);
						while( !reader.isEqual('N') && !reader.isEqual('<')){
							map = new LinkedHashMap<String, Object>(mapFirst);
							if(!parse(map, reader, headers)){
								reader.skipEOLs();
								listener.onReadyData(ctx, map, reader.getLine());
							}else{
								done();
								return;
							}
								
							
						}
					}else{
						reader.readUntilEOL(sb);
						System.err.println("Skip : "+sb);
					}
				}else
					reader.readUntilEOL(sb);

				reader.skipEOL();
			}
		
		reader.readUntilEOL(sb);
		if(sb.toString().trim().equals("COMMAND EXECUTED")){
			done();
			return;
		}
		
	}
	
	private boolean parse(Map<String, Object> map, Parser reader, ConfiguredHeader[] header) throws IOException {		
		StringBuilder sb = new StringBuilder();
		int lastHeader = header.length-1;
		for (int i = 0; i < header.length; i++) {
			if(lastHeader==i){
				reader.readUntilEOL(sb);
			}else
				reader.read(sb, header[i].getLength());
			if(i==0 && sb.toString().startsWith("COMMAND")){
				return true;
			}
			map.put(header[i].getName(), sb.toString().trim());
		}
		return false;
	}
}
