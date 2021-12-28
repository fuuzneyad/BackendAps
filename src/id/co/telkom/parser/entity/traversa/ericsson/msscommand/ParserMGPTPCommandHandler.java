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

public class ParserMGPTPCommandHandler extends AbstractCommandHandler implements MscCommandHandler{
	private final Parser reader;
	private final DataListener listener;
	private final Map<String, ConfiguredHeader[]> headersMap;
	private Map<String, Object> buffer = new LinkedHashMap<String, Object>();

	private final String T_NAME;
	private static final Logger logger = Logger.getLogger(ParserMGPTPCommandHandler.class);
	
	public ParserMGPTPCommandHandler(
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
	}

	@Override
	public void handle(Context ctx) throws IOException {
		logger.info(T_NAME);
		ctx.setTableName(T_NAME);
		StringBuilder sb = new StringBuilder();
		boolean isStartExecution = false;
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		ConfiguredHeader[] header = null;
		boolean check = false;
		
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
				if(sb.toString().startsWith("MOBILE")){
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
			if(reader.isEqual('V')){//DEV
				reader.readUntilEOL(sb).skipEOL();
				if(sb.indexOf("NRIV") >-1){
					{
						header = headersMap.get(getCommand());
					}
				}else
				{
					header = headersMap.get(getCommand());
				}
				while(!reader.isEOL() && !reader.isEqual('<')){
					map = new LinkedHashMap<String, Object>();
					check = reader.isEqual('E') || reader.isEqual('N');//yyn
					parse(map, reader, header, check);
					listener.onReadyData(ctx, map, reader.getLine());
					reader.skipEOL();
				}
			}else{
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("END"))//yyn
					{
						done();
						return;
					}else //yyn
				System.err.println("Skip : "+sb);
			}
			reader.skipEOLs();
		}
	}
	
	protected void parse(Map<String, Object> map, Parser reader, ConfiguredHeader[] header, boolean check) throws IOException{
		StringBuilder sb = new StringBuilder();
		final int lastIdx = header.length-1;
		for (int i = 0; !isDone() && i <= lastIdx; i++) {
			ConfiguredHeader configuredHeader = header[i];
			if(lastIdx==i)
				reader.readUntilEOL(sb);
			else{
				reader.read(sb, configuredHeader.getLength());
				if(check && i==0){
					if(sb.toString().startsWith("END") || sb.toString().startsWith("NONE")){ 
						done();						
					}
				}
			}
			if(!isDone()){
				String s = sb.toString().trim();
				
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
