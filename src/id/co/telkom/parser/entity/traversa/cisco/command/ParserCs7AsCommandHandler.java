package id.co.telkom.parser.entity.traversa.cisco.command;


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


public class ParserCs7AsCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private ConfiguredHeader[] headers;
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	private final String T_NAME;
	private final GlobalBuffer gb;
	private static final Logger logger = Logger.getLogger(ParserCs7AsCommandHandler.class);
	
	public ParserCs7AsCommandHandler(
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
		logger.info(getCommand());
		ctx.setTableName(T_NAME);
		StringBuilder sb = new StringBuilder();
		while(!reader.isEOF() && !reader.isEqual('>')){
			reader.read(sb);
			if(reader.isEOL()){
				reader.skipEOL();
				String line = sb.toString();
				char c = line.length()>0 ? line.charAt(0) : '-';
				if(line.startsWith("AS Name   ")){
					//do nothing..
				}
				else
				if(!line.trim().equals("") &&  
					(
						(c >= 'A' && c <= 'Z') || 
						(c >= 'a' && c <= 'z') ||
						(c >= '0' && c <= '9')
					)){
					Parse(line);
					Object o = map.get("AS_NAME");
					if(o!=null && !o.toString().equals("AS Name")){
						listener.onReadyData(ctx, map, reader.getLine());
						//set buffer
						Object dpc = map.get("ROUTING_KEY_DPC");
						Object asname = map.get("AS_NAME");
						gb.getIitpBuf().setCs7as(ctx.ne_id, asname!=null ? asname.toString(): "-", dpc!=null ? dpc.toString():"-");
					}
					map = new LinkedHashMap<String, Object>();
				}

			}
		}
		done();
		return;
	}
	
	private void Parse(String line){
		StringBuilder sb = new StringBuilder();
		char[] chars = line.toCharArray();
		final int lastIdx = headers.length-1;
		int k=0 ;
		for (int i = 0; i <= lastIdx; i++) {
			ConfiguredHeader configuredHeader = headers[i];
			for (int j = 0; j <configuredHeader.length; j++){
				if(k<chars.length)
					sb.append(chars[k]);
				k++;
			}
			map.put(configuredHeader.getName(), sb.toString().trim());
			sb.setLength(0);
		}
	}
	
	@SuppressWarnings("unused")
	private void Parse() throws IOException{
		StringBuilder sb = new StringBuilder();
		final int lastIdx = headers.length-1;
		for (int i = 0; !isDone() && i <= lastIdx; i++) {
			ConfiguredHeader configuredHeader = headers[i];
			if(lastIdx==i)
				reader.readUntilEOL(sb);
			else{
				reader.read(sb, configuredHeader.getLength());
			}
			if(!isDone()){
				String s = sb.toString().trim();
				map.put(configuredHeader.getName(), s);
			}
		}
	}
	
}
