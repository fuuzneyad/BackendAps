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


public class ParserZNAICommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private ConfiguredHeader[] headers;
	private final String T_NAME;
	private static final Logger logger = Logger.getLogger(ParserZNAICommandHandler.class);
	private boolean isStartExecution;
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	private String RESULT_RECORD;
	private String STA;
	@SuppressWarnings("unused")
	private final GlobalBuffer gb;
	
	public ParserZNAICommandHandler(
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
		StringBuilder sb = new StringBuilder();
		ctx.setTableName(T_NAME);
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
					logger.error("Skip : "+sb);
				}
			}else if(reader.isEqual('R')){
				reader.readUntilEOL(sb);
				if(sb.toString().trim().startsWith("RECORD")){
					String hdr=sb.toString().replace("SP CODE H/D", "SP_CODE_H_D");
//					System.out.println(">>>>>"+hdr);
					String[] header = hdr.split(" \\b");
					for(int i=0; i<headers.length-1; i++) {
						//reset header
						if(headers[i].getName().contains(header[i].trim())){
							headers[i].setLength(header[i].length()+1);
						}else
							break;
					}
					isStartExecution = true;
				}else{
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
					System.err.println("Skip : "+sb);
				}		
			}
			else{
				reader.readUntilEOL(sb);
				System.err.println("Skip : "+sb);
			}

			reader.skipEOLs();
		}
		
		if(!isStartExecution){
			done();
			return;
		} 
			while(!reader.isEOF() && !reader.isEqual('C') && !reader.isEqual('<') && !reader.isEqual('-') && isStartExecution){					
				while(!reader.isEOL()){
					map = new LinkedHashMap<String, Object>();//org
					parse(map, reader, headers);					
//					if (isGTMFIL)
//						map.put("CALLED_GTMFIL", CALLED_GTMFIL);
					if(map.get("NET")!=null && !map.get("NET").equals(""))
						listener.onReadyData(ctx, map, reader.getLine());
					reader.skipEOL();
				}
				if (isStartExecution) {isStartExecution = false;}
				while(reader.isEOL() || reader.isEqual(' '))
					reader.read();
				//break;
			}
		
		reader.readUntilEOL(sb);
		if(sb.toString().trim().equals("COMMAND EXECUTED")){
			done();
			return;
		}
	}
	
	private void parse(Map<String, Object> map, Parser reader, ConfiguredHeader[] header) throws IOException {		
		StringBuilder sb = new StringBuilder();
		int lastHeader = header.length-1;
		String value=null;
		for (int i = 0; i < header.length; i++) {
			if(lastHeader==i){
				reader.readUntilEOL(sb);
			}else{
				reader.read(sb, header[i].getLength());
				value=sb.toString().trim();
			}
			map.put(header[i].getName(), value);//the org
			
			if(i==0){
				if(value.length()!=0)
					RESULT_RECORD=sb.toString().trim();else
					map.put(header[i].getName(), RESULT_RECORD);
			}else if(i==1){
				if(value.length()!=0)
					STA=sb.toString().trim();else
					map.put(header[i].getName(), STA);
			}			
		}
	}
}
