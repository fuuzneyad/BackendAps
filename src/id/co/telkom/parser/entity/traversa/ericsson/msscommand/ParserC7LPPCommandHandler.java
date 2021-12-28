package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.AbstractCommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserC7LPPCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private static final String PARAM = "PARAM";
	private static final String VALUE = "VALUE";
	private static final String PARMG = "PARMG";
	
	private final Parser reader;
	private final DataListener listener;
	private final Map<String, ConfiguredHeader[]> headers;
	
	private final String T_NAME;
	
	private static final Logger logger = Logger.getLogger(ParserC7LPPCommandHandler.class);
	
	String parent="";
	String parent2="";
	
	public ParserC7LPPCommandHandler(
			Parser reader,
			DataListener listener,
			String command,
			String params,
			Map<String, ConfiguredHeader[]> headers,
			AbstractInitiator cynapseInit) {
		super(command,params);
		this.reader=reader;
		this.listener=listener;
		this.headers=headers;
		this.T_NAME=command;
	}
	
	
	@Override
	public void handle(Context ctx) throws IOException {
		logger.info(T_NAME);
		ctx.setTableName(T_NAME);
		StringBuilder sb = new StringBuilder();
		boolean isStartExecution = false;
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		List<String> parList = new ArrayList<String>();
		StringBuilder values = new StringBuilder();
		String param_parent="";
		String param_temp="";
		String param_child="";
		
		while(!isStartExecution && !reader.isEOF() && !reader.isEqual('<')){
			if(reader.isEqual('/')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("/*** COMMAND NOT FOUND ***/")){
					done();
					return;
				}else{
					System.err.println("Skip : "+sb);
				}
			}else if(reader.isEqual('C')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("CCITT7 SIGNALLING LINK PARAMETER GROUPS")){
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

		while(!reader.isEOF() && !reader.isEqual('<')){
			if(parList.isEmpty() && reader.isEqual('P')){//PARMG
				reader.readUntil(' ', sb);
				while(!reader.isEOL()){
					reader.skipWhile(' ').readUntil(' ', sb);
					parList.add(sb.toString().trim());
				}
			} else {


				boolean child = false;
				if(reader.isEqual(' ')){
					child = true;
				}else{
					parent=(String)map.get(PARAM);
				}
				
						
				values.setLength(0);

				for(int i=0; i<headers.get("VALUE").length;i++){
					
					ConfiguredHeader header1=headers.get("VALUE")[i];
					String key=header1.getName();
					String angkaKolom="";
				
					if(key.trim().equals("PARAM")){
						angkaKolom="";
					}else{
						angkaKolom=parList.get(i-1);
					}
				
					reader.read(sb, header1.getLength());
					
					if(child){
						if(key.equals("PARAM")){
							param_temp=param_parent;
							param_parent=param_temp;
							param_child=param_parent+"_"+sb.toString().trim();
						
						}else
						if(Pattern.matches("\\d", key.trim())&&!sb.toString().isEmpty()&&!sb.toString().trim().equals(""))
						{
							map.put(PARAM, param_child);
							map.put(PARMG, angkaKolom);
							map.put(VALUE, sb.toString().trim());
						}
					}else{
			
						if(key.equals("PARAM")){
							
							param_parent=sb.toString().trim();
							if(param_parent.equals("END")){
								done();
								return;
							}
								
						}else
						if(Pattern.matches("\\d", key.trim())&&!sb.toString().trim().isEmpty()&&!sb.toString().trim().equals(""))
						{
							map.put(PARAM, param_parent);
							map.put(PARMG, angkaKolom);
							map.put(VALUE, sb.toString().trim());
						}
					
				}
						
					listener.onReadyData(ctx, map, reader.getLine());
					map=new LinkedHashMap<String, Object>();
	
				}
				map=new LinkedHashMap<String, Object>();
				
			}
			reader.skipEOLs();
		}
	}
	
}
