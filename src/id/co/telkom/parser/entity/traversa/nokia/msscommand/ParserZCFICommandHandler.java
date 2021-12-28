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


public class ParserZCFICommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private ConfiguredHeader[] headers;
	private final String T_NAME;
	private static final Logger logger = Logger.getLogger(ParserZCFICommandHandler.class);
	private boolean isStartExecution;
	@SuppressWarnings("unused")
	private final GlobalBuffer gb;
	Map<String, Object> map = new LinkedHashMap<String, Object>();
	
	public ParserZCFICommandHandler(
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
		
		while (!isStartExecution && !reader.isEOL() && !reader.isEqual('<')){
			if(reader.isEqual('/')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("/*** COMMAND NOT FOUND ***/")){
					listener.onError(reader.getLine(), ctx, reader.getColumn(), sb.toString());
					done();
					return;
				}else{
					System.err.println("Skip : "+sb);
					logger.error("Skip : "+sb);
				}
			}else if(reader.isEqual('M')){
				reader.readUntil(' ', sb);
				if(sb.toString().equals("MSCi")){
					reader.skipWhile(' ').readUntil(' ', sb).skipUntilEOL();
					//ctx.setNe_id(sb.toString());
				}		
				else
				{
					reader.readUntilEOL(sb);
					System.err.println("Skip : "+sb);
				}
			}
			else if(reader.isEqual('C')){
				reader.readUntilEOL(sb);
				if(sb.toString().trim().equals("COMMAND EXECUTED")){
					done();
					return;
				}else{
					reader.readUntilEOL(sb);
					//System.err.println("Skip : "+sb);
				}		
			}
			else if(reader.isEqual('N')){
				reader.readUntilEOL(sb);
				if(sb.toString().trim().startsWith("NBR.")){
					isStartExecution=true;
				}else{
					reader.readUntilEOL(sb);
					//System.err.println("Skip : "+sb);
				}		
			}
			else{
				reader.readUntilEOL(sb);
				//System.err.println("Skip : "+sb);
			}

			reader.skipEOLs();
			
		}
		
		if(!isStartExecution){
			done();
			return;
		}
		while(!reader.isEOF() && !reader.isEqual('C') && !reader.isEqual('<') && isStartExecution){					
			
			if(!reader.isEOL() && isStartExecution && !reader.isEqual(' ')){
				
				parse(reader, headers, ctx);//							
				reader.skipEOL();
			}
			if(isDone()){
//				listener.onEndTable(reader.getLine(), ctx);
			}
			while(reader.isEOL() || reader.isEqual(' '))
				reader.read();

		}
		
		reader.readUntilEOL(sb);
		if(sb.toString().trim().equals("COMMAND EXECUTED")){
			done();
			return;
		}
		
	}
	
	private void parse(Parser reader, ConfiguredHeader[] header, Context context) throws IOException {		
		StringBuilder sb = new StringBuilder();
		int lastHeader = header.length-4;

		if (reader.isEqual('A'))
		{
			reader.skipUntil(':').readUntilEOL(sb);
			if (map.get(header[0].getName())!=null){
				map.put("ADDED_DIGITS", sb.toString().trim());
				listener.onReadyData(context, map, reader.getLine());
//				map.remove(key)
			}
			
		}else 
		if (reader.isEqual('N') || reader.isEqual('S')){
			reader.readUntil(':', sb);
			if(sb.toString().contains("NUMBER MODIFICATION RULES"))
			{
				reader.skipUntil(':').readUntilEOL(sb);
				map.put("SP_REMOVED_DGT", sb.toString().trim());
			}else
			if(sb.toString().contains("NUMBER OF DIGITS REMOVED"))
			{
				reader.readUntilEOL(sb);
				map.put("DIGITS_REMOVED", sb.toString().trim());
			}else
			if(sb.toString().contains("START POINT OF ADDED DIGITS"))
			{
				reader.readUntilEOL(sb);
				map.put("SP_ADDED_DGT", sb.toString().trim());
			}else{	
			reader.readUntilEOL(sb);
			System.err.println("fzn: "+sb.toString());
			}
		}else{
			map = new LinkedHashMap<String, Object>();
			for (int i = 0; i < header.length-4; i++) {
				if(lastHeader==i){
					reader.readUntilEOL(sb);
				}else
					reader.read(sb, header[i].getLength());
				map.put(header[i].getName(), sb.toString().trim());
			}
		}
	
	}

}
