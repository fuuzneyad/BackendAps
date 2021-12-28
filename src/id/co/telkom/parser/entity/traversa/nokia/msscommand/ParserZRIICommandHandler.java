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


public class ParserZRIICommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	private final String T_NAME;
	private static final Logger logger = Logger.getLogger(ParserZRIICommandHandler.class);
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	private Map<String, Object> mapFirst = new LinkedHashMap<String, Object>();
	@SuppressWarnings("unused")
	private final GlobalBuffer gb;
	@SuppressWarnings("unused")
	private String Param;
	private boolean isStartExecution;
	private Map<String, String> buffer = new LinkedHashMap<String, String>();
	private Map<String, Object> bufferLoop = new LinkedHashMap<String, Object>();
	
	public ParserZRIICommandHandler(
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
						genereateLoop(ctx);
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
				boolean isNumber = i==0 && reader.isNumber() ? true : false;
				if(i==0){
					if(!isNumber){
						//TODO: handle not Numeric Digit
						logger.info("What todo with ZRII non numeric digit?? ask Baim..");
					}
					if(sb.toString().startsWith("COMMAND")){
						done();
					}
				}else
				if(sb.toString().trim().equals("SSET")){//TODO:check
					reader.readUntilEOL(sb);
					map.put("SSET", sb.toString().contains("=") ? sb.toString().split("=")[1].trim():sb);
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
	
	private void genereateLoop(Context ctx){
		ctx.setTableName(T_NAME);
		listener.onReadyData(ctx, map, reader.getLine());
		bufferLoop=map;
		ctx.setTableName(T_NAME+"_LOOP");
		Object digits = bufferLoop.get("DIGITS");
		map = new LinkedHashMap<String, Object>(bufferLoop);
		if(digits!=null && (digits.toString().charAt(0) >= '0' && digits.toString().charAt(0) <= '9' )){//TODO:check,filter only Number Digits
			String digs = digits.toString();
			String[] arr1 = digs.split("&");
			String firstDigits = arr1[0];
			
			map = new LinkedHashMap<String, Object>(bufferLoop);
			map.put("DIGIT_LOOP", firstDigits);
			listener.onReadyData(ctx, map, 0);
			
			@SuppressWarnings("unused")
			int loop=1;
			for(int i=0;i<arr1.length;i++){
				if(i!=0){
					if(arr1[i].equals("") && arr1.length>=i+1){
						String next = arr1[i+1];
						if(next.contains("-")){
							Integer a = toInt(firstDigits.substring(firstDigits.length()-1));
							Integer b = toInt(next.trim().replace("-", ""));
							String c = firstDigits.substring(0,firstDigits.length()-1);
							for(Integer x=a+1;x<=b;x++){
								String dig = c+x;
								map = new LinkedHashMap<String, Object>(bufferLoop);
								map.put("DIGIT_LOOP", dig);
								listener.onReadyData(ctx, map, 0);
								loop++;
								firstDigits=dig;
							}
							
						}else{
							Integer lastLoop = toInt(arr1[i+1]);
							for(Integer x=toInt(firstDigits)+1; x<=lastLoop; x++){
								if(toInt(firstDigits)==-1)//long int exception
									break;
								map = new LinkedHashMap<String, Object>(bufferLoop);
								map.put("DIGIT_LOOP", x);
								listener.onReadyData(ctx, map, 0);
								loop++;
								firstDigits=x.toString();
							}
						}
						break;
					}else{
						arr1[i]=arr1[i].replace("-", "");
						String a = arr1[0].substring(0,arr1[0].length()-1)+arr1[i];
						map = new LinkedHashMap<String, Object>(bufferLoop);
						map.put("DIGIT_LOOP", a);
						listener.onReadyData(ctx, map, 0);
						firstDigits=a;
						loop++;
					}
				
				}
			}
			
		}
	}
	
	private Integer toInt(String s){
		try{
			return Integer.parseInt(s);
		}catch(NumberFormatException e){
			return -1;
		}
	}
}
