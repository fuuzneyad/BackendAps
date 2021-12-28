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

public class ParserTSTDNSCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	
	@SuppressWarnings("unused")
	private Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	private final String T_NAME;
	private static final Logger logger = Logger.getLogger(ParserTSTDNSCommandHandler.class);
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	private Map<String, Object> temp = new LinkedHashMap<String, Object>();
	
	@SuppressWarnings("unused")
	private String Param;
	private boolean isStartExecution;
	public ParserTSTDNSCommandHandler(
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
			
			if (sb.toString().startsWith("RETCODE = 0")) {

			}else if (sb.toString().startsWith("The result is as follows:")) {
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
		
		while (!reader.isEOF() && !reader.isEqual('-')) {
				while(!reader.isEqual('(')){
					reader.readUntilEOL(sb).skipEOL();
					String s = sb.toString().trim();
					if(s.contains("=")){
						map.put(s.split("=")[0].toUpperCase().trim().replace(" ", "_"), 
								s.split("=")[1].trim());
						if(s.contains("FQDN")){
							temp.put("FQDN", s.split("=")[1].trim());
							for(String x : s.split("=")[1].split("\\.")){
								if(x.contains("MNC"))
									temp.put("MNC", x.replace("MNC", "").trim());
								else
								if(x.contains("MCC"))
									temp.put("MCC", x.replace("MCC", "").trim());
								else
								if(x.contains("RAC"))
									temp.put("RAC", x.replace("RAC", "").trim());
								else
								if(x.contains("LAC"))
									temp.put("LAC", x.replace("LAC", "").trim());
								
							}
						}
						if(s.contains("IP ADDR")&&!s.contains("0.0.0.0")){
							map.putAll(temp);
							//ready
//							System.out.println(map);
							listener.onReadyData(ctx, map, reader.getLine());
						}
						map = new LinkedHashMap<String, Object>();
					}
				}
				done();
				return;
		}
		done();
		return;
	}
	
}
