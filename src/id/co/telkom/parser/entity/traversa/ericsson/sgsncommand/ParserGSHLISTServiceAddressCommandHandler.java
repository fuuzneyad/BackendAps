package id.co.telkom.parser.entity.traversa.ericsson.sgsncommand;


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

public class ParserGSHLISTServiceAddressCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	@SuppressWarnings("unused")
	private Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	private final String T_NAME;
	private static final Logger logger = Logger.getLogger(ParserGSHLISTServiceAddressCommandHandler.class);
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	@SuppressWarnings("unused")
	private String Param;
	private boolean isStartExecution;
	public ParserGSHLISTServiceAddressCommandHandler(
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
		this.Param=params;
	}
	@Override
	public void handle(Context ctx)
			throws IOException {
		ctx.setTableName(T_NAME);
		StringBuilder sb = new StringBuilder();
		while (!isStartExecution){
			reader.skipEOLs().readUntilEOL(sb);
			
			if (sb.toString().startsWith("ps Class")) {
				reader.skipLines(1);
				isStartExecution=true;
			}
			else{
				logger.error("Skip : "+sb);
			}
			reader.skipEOLs();
		}
		
		if(!isStartExecution){
			done();
			return;
		}
		
		while (!reader.isEOF() && !reader.isEqual('=')) {
				while(!reader.isEqual('=')){
					reader.readUntilEOL(sb).skipEOL();
					String s = sb.toString().trim();
					String sHead = s.split("\\|")[0] ; 
					
					String[] splitted = sHead.replace("-", "").split("\\s+");
					String temp="";
					for(int i=0;i<splitted.length;i++){
						if(i%2!=0){
							map.put(temp.toUpperCase(), splitted[i]);
						}else
							temp=splitted[i];
					}
					listener.onReadyData(ctx, map, reader.getLine());
					map = new LinkedHashMap<String, Object>();
				}
				done();
				return;
		}
		done();
		return;
	}
	
}
