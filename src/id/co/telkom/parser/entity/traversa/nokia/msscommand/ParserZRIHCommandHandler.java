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


public class ParserZRIHCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	private final String T_NAME;
	private static final Logger logger = Logger.getLogger(ParserZRIHCommandHandler.class);
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	private Map<String, Object> mapFirst = new LinkedHashMap<String, Object>();
	@SuppressWarnings("unused")
	private final GlobalBuffer gb;
	@SuppressWarnings("unused")
	private String Param;
	private boolean isStartExecution;
	private Map<String, String> buffer = new LinkedHashMap<String, String>();
	
	public ParserZRIHCommandHandler(
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
		this.Param=params;
	}
	@Override
	public void handle(Context ctx)
			throws IOException {
		ctx.setTableName(T_NAME);
		StringBuilder sb = new StringBuilder();
		while (!isStartExecution && !reader.isEOL() && !reader.isEqual('<')){
			reader.skipEOLs().readUntilEOL(sb);
			
			if (sb.toString().startsWith("LOADING PROGRAM")) {
				isStartExecution = true;
				reader.skipEOLs().readUntil(' ', sb);
				if(sb.toString().equals("MSCi")){
					reader.skipWhile(' ').readUntil(' ', sb).skipUntilEOL();
					ctx.setNe_id(sb.toString());
				}
			} else if(sb.toString().startsWith("/*** COMMAND NOT FOUND ***/")){
				listener.onError(reader.getLine(), ctx, reader.getColumn(), sb.toString());
				reader.skipEOLs();
				done();
				return;
			} else{
				System.err.println("Skip : "+sb);
				logger.error("Skip : "+sb);
			}
			reader.skipEOLs();
		}
		
		while (!reader.isEOL() && !reader.isEqual('<')) {
			if(reader.isEqual('T')){
				reader.readUntilEOL(sb).skipEOLs();
				String ln = sb.toString();
				if(ln.startsWith("TREE")){
					String[] arr1 = ln.split("\\s+");
					mapFirst = new LinkedHashMap<String, Object>();
					for (int i=0;i<arr1.length;i++){
						if(i==0)
							mapFirst.put(arr1[0].replace("=", ""), arr1[1]);
						else
						if(arr1[i].contains("=")){
							String[] split=arr1[i].split("=");
							mapFirst.put(split[0], split[1]);
						}
					}
				}
			}if(reader.isEqual('D')){
				reader.readUntilEOL(sb).skipEOL();
				if(sb.toString().startsWith("DIGITS")){
					ConfiguredHeader[] header = headersMap.get("DIGITS");
					while(!reader.isEOL()){
						map.putAll(mapFirst);
						Parse(header);
						reader.skipEOL();
						listener.onReadyData(ctx, map, reader.getLine());
						map = new LinkedHashMap<String, Object>();
					}
				}
			}else{
				reader.readUntilEOL(sb).skipEOL();
			}
			reader.skipEOLs();
		}
		done();
		return;
	}
	
	private void Parse(ConfiguredHeader[] header) throws IOException{
		StringBuilder sb = new StringBuilder();
		final int lastIdx = header.length-1;
		for (int i = 0; !isDone() && i <= lastIdx; i++) {
			ConfiguredHeader configuredHeader = header[i];
			if(lastIdx==i){
				reader.readUntilEOL(sb);
			}
			else{
				reader.read(sb, configuredHeader.getLength());
				if(i==0){
					if(sb.toString().startsWith("COMMAND")){
						done();
					}
				}else if(configuredHeader.getName().equals("AL") && !isNumber(sb.toString())){//TODO:check
					reader.readUntilEOL(sb);
					map.put("TCN", sb.toString().contains("=") ? sb.toString().split("=")[1].trim():sb);
					break;
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
	
	private boolean isNumber(String s){
		try{
			Integer.parseInt(s.trim());
			return true;
		}catch(NumberFormatException e){
			return false;
		}
		
	}
	
}
