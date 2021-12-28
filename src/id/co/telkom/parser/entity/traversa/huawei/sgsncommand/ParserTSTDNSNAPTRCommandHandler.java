package id.co.telkom.parser.entity.traversa.huawei.sgsncommand;


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

public class ParserTSTDNSNAPTRCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	private final String T_NAME;
	private static final Logger logger = Logger.getLogger(ParserTSTDNSNAPTRCommandHandler.class);
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	@SuppressWarnings("unused")
	private String Param;
	private boolean isStartExecution;
	public ParserTSTDNSNAPTRCommandHandler(
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
		ctx.setTableName(T_NAME+"_NAPTR");
		StringBuilder sb = new StringBuilder();
		while (!isStartExecution){
			reader.skipEOLs().readUntilEOL(sb);
			
			if (sb.toString().startsWith("RETCODE = 0")) {

			}else if (sb.toString().startsWith("The result is as follows:")) {
				reader.skipLines(2);
				isStartExecution=true;
				reader.readUntilEOL(sb).skipEOL();
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
		
		while (!reader.isEOF() && !reader.isEqual('-')) {
				while(!reader.isEqual('(')){
						reader.readUntilEOL(sb);
						reader.skipEOL();
						String ss[] = sb.toString().split("\\s+");
						parse(map, ss, headersMap.get("MASTER"));
						listener.onReadyData(ctx, map, reader.getLine() );
						map = new LinkedHashMap<String, Object>();
				}
				reader.readUntilEOL(sb).skipEOLs();
				done();
				return;
		}
		done();
		return;
	}
	
	protected void parse(Map<String, Object> map, Parser reader, Context context, ConfiguredHeader[] header) throws IOException{
		StringBuilder sb = new StringBuilder();
		int lastHeader = header.length-1;
		for (int i = 0; i < header.length && !reader.isEOL(); i++) {
			if(i==lastHeader)
				reader.readUntilEOL(sb);
			else
				reader.read(sb, header[i].getLength());
			map.put(header[i].getName(), sb.toString().trim());
		}
	}
	
	protected void parse(Map<String, Object> map,String[] data,  ConfiguredHeader[] header){
		for (int i = 0; i < header.length; i++){
			if(i<=data.length)
				map.put(header[i].getName(), data[i+1]);
		}
	}
}
