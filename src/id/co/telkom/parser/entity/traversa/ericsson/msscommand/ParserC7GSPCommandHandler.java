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


public class ParserC7GSPCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private ConfiguredHeader[][] headers;
	private static final Logger logger = Logger.getLogger(ParserC7GSPCommandHandler.class);
	private final String TNAME;
	public ParserC7GSPCommandHandler(
			Parser reader, 
			DataListener listener, 
			String command, 
			String params, 
			ConfiguredHeader[][] headers,
			AbstractInitiator cynapseInit) {
		super(command, params);
		this.reader = reader;
		this.listener=listener;
		this.headers=headers;
		TNAME=command;
	}
	
	@Override
	public void handle(Context ctx)
			throws IOException {
		ctx.setTableName(TNAME);
		StringBuilder sb = new StringBuilder();
		boolean isStartExecution = false;
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		
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
			}else if(reader.isEqual('O')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("OPERATING")){
					isStartExecution = true;
					reader.skipLines(1);
				}
			}else{
				reader.readUntilEOL(sb);
				logger.error("Skip : "+sb);
			}
			reader.skipEOLs();
		}
		if(!isStartExecution){
			done();
			return;
		}
		
		while(!reader.isEOF() && !reader.isEqual('<')){
			if(reader.isEqual('E'))
			{
				reader.readUntilEOL(sb);
				if(!sb.toString().equals("END")){
					System.err.println("Unexpected character in line "+reader.getLine()+" : "+sb);
				}
				done();
				return;
			}
			else{
				
				if(!map.isEmpty()){
					listener.onReadyData(ctx, map, reader.getLine());
				}
				map = new LinkedHashMap<String, Object>();
				parse(map, reader, headers);
			}
			reader.skipEOLs();
		}
	}
	protected void parse(Map<String, Object> map, Parser reader, ConfiguredHeader[][] header) throws IOException{
		StringBuilder sb = new StringBuilder();
		ConfiguredHeader[] head = header[0];
		
		for(int i=0; i< head.length ; i++){
			if(i==head.length-1)
				reader.readUntilEOL(sb).skipEOL();
			else {
				reader.read(sb, head[i].getLength());//MTT
				if(i==0)
					if (sb.toString().trim().length()==0)
						head = header[1];
					else
						head = header[0];
				
			}
			
			String s = sb.toString().trim();
			map.put(head[i].getName(), s);
			
		}
		
		if(head==header[1] && map.get("MNS")!=null && !isIntegerFirstChar(map.get("MNS").toString())){
			map.clear();
		}
	}
	
	private boolean isIntegerFirstChar(String s){
		if(s.equals(""))
			return false;
		char c = s.charAt(0);
		return c >='0' && c<='9';
	}
}
