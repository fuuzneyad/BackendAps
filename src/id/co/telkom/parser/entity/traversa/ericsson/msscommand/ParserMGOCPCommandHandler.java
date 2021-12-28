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

public class ParserMGOCPCommandHandler extends AbstractCommandHandler implements MscCommandHandler{

	private final Parser reader;
	private final DataListener listener;
	private final Map<String, ConfiguredHeader[]> headersMap;
	private final String T_NAME;
	@SuppressWarnings("unused")
	private final GlobalBuffer gb;
	private static final Logger logger = Logger.getLogger(ParserMGOCPCommandHandler.class);
	private Map<String, String> buffer = new LinkedHashMap<String, String>();
	
	public ParserMGOCPCommandHandler(
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
		ConfiguredHeader[] header = null;
		
		while(!isStartExecution && !reader.isEOF() && !reader.isEqual('<')){
			if(reader.isEqual('/')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("/*** COMMAND NOT FOUND ***/")){
					done();
					return;
				}else{
					//System.err.println("Skip : "+sb);
				}
			}else if(reader.isEqual('M')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("MT OUTER CELL DATA")){
					isStartExecution = true;
				}
			}else if(reader.isEqual('N')){
				reader.readUntilEOL(sb).skipEOL();
				if(sb.toString().startsWith("NOT ACCEPTED")){									
					done();
					return;
				}else{
					//System.err.println("Skip : "+sb);
				}
			}else{
				reader.readUntilEOL(sb);
			}
			reader.skipEOLs();
		}
		if(!isStartExecution){
			done();
			return;
		}
		
		while(!isDone() && !reader.isEOF() && !reader.isEqual('<')){
			if(reader.isEqual('C')){
				reader.readUntilEOL(sb).skipEOL();
				if(sb.toString().startsWith("CELL")){
					if(sb.toString().contains("MSC   ") && sb.toString().contains("   NCS")){
						header=headersMap.get("FIRST");
					}
					
					else
					if(sb.toString().contains("MSCG  ") && sb.toString().contains("   NCS")){
						header=headersMap.get("SECOND");
					}
					
					
					
					if(header!=null)
					while(!isDone() && !reader.isEOF() && !reader.isEqual('<') && !reader.isEqual('C') ){
						parse(map, reader, header, true);
						if(map.get(header[0].getName())!=null){
							listener.onReadyData(ctx, map, reader.getLine());
						}
						map = new LinkedHashMap<String, Object>();
						reader.skipEOLs();
					}
					map=new LinkedHashMap<String, Object>();
				}else{
					reader.readUntilEOL(sb);
				}
			}else if(reader.isEqual('E')){
				reader.readUntilEOL(sb).skipEOL();
				if(sb.toString().startsWith("END")){									
					done();
					return;
				}else{
				}
			}else{
				reader.readUntilEOL(sb);
				if(sb.toString().trim().startsWith("NONE")){
					done();
					return;
				}else{
				}
				
				
			}
		}
		reader.skipEOLs();
		
	}
	
	
	
	protected void parse(Map<String, Object> map, Parser reader, ConfiguredHeader[] header, boolean check) throws IOException{
		StringBuilder sb = new StringBuilder();
		final int lastIdx = header.length-1;
		for (int i = 0; !isDone() && i <= lastIdx; i++) {
			ConfiguredHeader configuredHeader = header[i];
			if(lastIdx==i){
				reader.readUntilEOL(sb).skipEOL();
			}
			else{
				reader.read(sb, configuredHeader.getLength());
				if(check && i==0){
					if(sb.toString().startsWith("END")){
						done();
					}
				}
			}
			if(!isDone()){
				String s = sb.toString().trim();
				
				if(s.length()>0){
					if(configuredHeader.copied && s.length() > 0){
						buffer.put(configuredHeader.getName(), s);
					}
					if(configuredHeader.copied)
						map.put(configuredHeader.getName(), buffer.get(configuredHeader.getName()));
					else
						map.put(configuredHeader.getName(), s);
				}
			}
		}			
	}

}
