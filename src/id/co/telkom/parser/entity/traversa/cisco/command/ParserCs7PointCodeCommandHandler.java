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


public class ParserCs7PointCodeCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private ConfiguredHeader[] headers;
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	private final String T_NAME;
	private final GlobalBuffer gb;
	private static final Logger logger = Logger.getLogger(ParserCs7PointCodeCommandHandler.class);
	
	public ParserCs7PointCodeCommandHandler(
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
		String Instance=null;
		while(!reader.isEOF() && !reader.isEqual('>')){
			reader.read(sb);
			if(reader.isEOL()){
				reader.skipEOL();
				String line = sb.toString();
				char c = line.length()>0 ? line.charAt(0):'x';
				if(c >= '0' && c <= '9'){//number
					Parse(line);
					map.put("INSTANCE", Instance);//ready here
					listener.onReadyData(ctx, map, reader.getLine());
						//set buffer
						Object pc = map.get("POINT_CODE");
						Object status = map.get("STATUS");
						if(status!=null && status.toString().contains("active"))
							gb.getIitpBuf().setCsPointCode(ctx.ne_id, pc!=null?pc.toString():"-", status!=null?status.toString():"-");
					map = new LinkedHashMap<String, Object>();
				}else
					if(line.startsWith("Instance Number")){
						Instance=line.replace("Instance Number","").trim();
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
