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


public class ParserCs7GttConfigCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	private final String T_NAME;
	private final GlobalBuffer gb;
	private static final Logger logger = Logger.getLogger(ParserCs7GttConfigCommandHandler.class);
	private Map<String, Object> headerGtt = new LinkedHashMap<String, Object>();
	private Map<String, Object> headerGta = new LinkedHashMap<String, Object>();
	
	public ParserCs7GttConfigCommandHandler(
			Parser reader, 
			DataListener listener, 
			String command, 
			String params, 
			ConfiguredHeader[] headers,
			AbstractInitiator cynapseInit) {
		super(command, params);
		this.reader = reader;
		this.listener=listener;
		this.T_NAME=command;
		this.gb=(GlobalBuffer)cynapseInit.getMappingModel();
	}
	private String[] getTable(){
		return new String[] {"CS7_POINT_CODE", "CS7_GTT_GTA", "CS7_GTT_APP_GROUPS"};
	}
	@Override
	public void handle(Context ctx)
			throws IOException {
		logger.info(getCommand());
		
		ctx.setTableName(T_NAME);
		StringBuilder sb = new StringBuilder();
		while(!reader.isEOF() && !reader.isEqual('>')){
			reader.read(sb);
			if(reader.isEOL()){
				reader.skipEOL();
				String line = sb.toString();
				if(line.startsWith("cs7 ")&& line.contains("selector")){//gtt gta
					String[] ss=line.trim().split("\\s+");
					for(int i=0;i<ss.length;i++){
						if(ss[i].startsWith("instance") && ss.length>i+1){
							headerGta.put("INSTANCE", ss[i+1]);
						}else
						if(ss[i].startsWith("selector")&& ss.length>i+1){
							headerGta.put("SELECTOR_NAME", ss[i+1]);
						}else
						if(ss[i].startsWith("tt")&& ss.length>i+1){
							headerGta.put("TT", ss[i+1]);
						}else
						if(ss[i].startsWith("tt")&& ss.length>i+1){
							headerGta.put("TT", ss[i+1]);
						}else
						if(ss[i].startsWith("gti")&& ss.length>i+1){
							headerGta.put("GTI", ss[i+1]);
						}else
						if(ss[i].startsWith("np")&& ss.length>i+1){
							headerGta.put("NP", ss[i+1]);
						}else
						if(ss[i].startsWith("nai")&& ss.length>i+1){
							headerGta.put("NAI", ss[i+1]);
						}
						
					}
					//System.out.println(sb);
					//System.out.println(headerGta);
					//anaknya
					while(!reader.isEqual('!')){
						reader.readUntilEOL(sb);
						String[] mm=sb.toString().trim().split("\\s+");
						for(int i=0;i<mm.length;i++){
							if(mm[i].trim().startsWith("gta")&& mm.length>i+1){
								map.put("GTA", mm[i+1]);
							}else
							if(mm[i].trim().startsWith("app-grp")&& mm.length>i+1){
								map.put("APP_GRP", mm[i+1].trim());
							}else
							if(mm[i].trim().startsWith("qos")&& mm.length>i+1){
								map.put("QOS", mm[i+1]);
							}else
							if(mm[i].trim().startsWith("pcssn")&& mm.length>i+1){
								if(isValidPcSsn(mm[i+1].trim())){
									map.put("PCSSN", mm[i+1].trim());
								}
							}else
							if(mm[i].trim().startsWith("ssn") && mm.length>i+1){
								if(isValidPcSsn(mm[i+1].trim())){
									map.put("SSN", mm[i+1].trim());
								}
							}
						}
						//ready
						if(!map.isEmpty()){
							map.putAll(headerGta);
							ctx.setTableName(getTable()[1]);
							if(map.get("APP_GRP")==null)
								map.put("APP_GRP", "-");
							//System.out.println(map);
							listener.onReadyData(ctx, map, reader.getLine());
							map = new LinkedHashMap<String, Object>();
						}
						reader.skipEOLs();
					}
					if(reader.isEqual('!')){
						headerGta.clear();
					}
				}else
				if(line.startsWith("cs7 ") && line.contains("gtt application-group")){//gtt_appgroup
					String[] ss = line.split("\\s+");
					for(int i=0;i<ss.length;i++){
						if(ss[i].startsWith("instance"))
								headerGtt.put("INSTANCE",ss[i+1]);else
						if(ss[i].startsWith("application-group"))
								headerGtt.put("GROUP_NAME",ss[i+1]);
					}
					//anaknya
					reader.skipEOLs();
					while(!reader.isEqual('!')){
						reader.readUntilEOL(sb);
						if(sb.toString().trim().startsWith("multiplicity")){
							headerGtt.put("MULT_2",sb.toString().replace("multiplicity", "").trim()
									.replace("share", "sha").replace("cost", "cos"));
						}else{
							String[] abc = sb.toString().trim().split("\\s+");
							for (int i=0;i<abc.length;i++){
								if(abc[i].startsWith("instance")){
									map.put("INSTANCE_CH", abc[i+1]);
								}else
								if(abc[i].trim().startsWith("pc")&&abc.length>=i+3){
									map.put("PC", abc[i+1]);
									map.put("COST", abc[i+2]);
									map.put("RI", abc[i+3]);
									map.putAll(headerGtt);
									//ready
									//System.out.println("ayeee1 "+map);//ready
									ctx.setTableName(getTable()[2]);
									listener.onReadyData(ctx, map, reader.getLine());
									//set buffer
									setBuffer(ctx.ne_id,map);
									map = new LinkedHashMap<String, Object>();									
									break;
								}else
									if(abc[i].trim().startsWith("asname")&&abc.length>=i+3){
										map.put("ASNAME", abc[i+1]);
										map.put("COST", abc[i+2]);
										map.put("RI", abc[i+3]);
										map.put("PC", "0");
										map.putAll(headerGtt);
										//ready
										ctx.setTableName(getTable()[2]);
										listener.onReadyData(ctx, map, reader.getLine());
										//set buffer
										setBuffer(ctx.ne_id,map);
										map = new LinkedHashMap<String, Object>();	
										break;
									}
							}
						}
						reader.skipEOLs();
					}
					if(reader.isEqual('!')){
						headerGtt.clear();
					}
				}

			}
		}
		done();
		return;
		
	}
	private boolean isValidPcSsn(String s){
		if(s.equals(""))
			return false;
		try{
			Integer.parseInt(s);
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}
	
	private void setBuffer(final String ne, final Map<String, Object> localMap){
		gb.getIitpBuf().setAppGroups(ne, localMap);
	}
}
