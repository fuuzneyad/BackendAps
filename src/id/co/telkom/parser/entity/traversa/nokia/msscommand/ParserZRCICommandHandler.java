package id.co.telkom.parser.entity.traversa.nokia.msscommand;


import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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


public class ParserZRCICommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	private final String T_NAME;
	private static final Logger logger = Logger.getLogger(ParserZRCICommandHandler.class);
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	private Map<String, Object> map1 = new LinkedHashMap<String, Object>();
	private Map<String, Object> map2 = new LinkedHashMap<String, Object>();
	private Map<String, Object> map3 = new LinkedHashMap<String, Object>();
	private final List<String> listIgnore = new ArrayList<String>();
	@SuppressWarnings("unused")
	private final GlobalBuffer gb;
	private Long cgrId;
	private final String parentName = "CGR";
	private final String parentNCGR = "NCGR";
	private String Param;
	
	public ParserZRCICommandHandler(
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
		initIgnoreResult();
	}
	private void initIgnoreResult(){
		listIgnore.add("AUTOMATIC CONGESTION CONTROL"); 
		listIgnore.add("SELECTIVE CIRCUIT RESERVATION"); 
		listIgnore.add("ADDITIONAL PARAMETERS:"); 
		listIgnore.add("CIC(S)");
		listIgnore.add("CIRCUIT(S)");
	}
	@Override
	public void handle(Context ctx)
			throws IOException {
		StringBuilder sb = new StringBuilder();

		boolean isStartExecution = false;
		boolean isConfigurableTable = true;
		boolean isCandidateExit;
		boolean isInit = true;		
		int countCGR = 0;	
		while(!isStartExecution && !reader.isEOF() && !reader.isEqual('<')){
			if(reader.isEqual('/')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("/*** COMMAND NOT FOUND ***/")){
					listener.onError(reader.getLine(), ctx, reader.getColumn(), sb.toString());
					done();
					return;
				}else{
					System.err.println("Skip : "+sb);
					logger.error("Skip : "+sb);
				}
			}else if(reader.isEqual('M')){
				reader.readUntil(' ', sb);
				if(sb.toString().equals("MSCi")){
					reader.skipWhile(' ').readUntil(' ', sb).skipUntilEOL();
					ctx.setNe_id(sb.toString());
				}else{
					reader.readUntilEOL(sb);
					System.err.println("Skip : "+sb);
					logger.error("Skip : "+sb);
				}
			}else if(reader.isEqual('C')){
				reader.readUntilEOL(sb);
				if(sb.toString().trim().equals("CIRCUIT GROUP(S)")){
					isStartExecution = true;
				}else{
					System.err.println("Skip : "+sb);
					logger.error("Skip : "+sb);
				}
			}else{
				reader.readUntilEOL(sb);
				System.err.println("Skip : "+sb);
				logger.error("Skip : "+sb);
			}
			reader.skipEOLs();
		}
		if(!isStartExecution){
			done();
			return;
		}else{
			while(!reader.isEOF() && !reader.isEqual('<')){
				isCandidateExit = reader.isEqual('C');
				reader.readUntilEOL(sb).skipEOLs();
				final String s = sb.toString().trim();
				if(isCandidateExit && s.equals("COMMAND EXECUTED")){
					//if (!Param.contains("PRINT=5"))
					if (map.get(parentName)!=null || map.get(parentNCGR)!=null)//last
					{	
						//TODO:CHECK THIS
						String t_name = "MERGED";
						ctx.setTableName(T_NAME+"_"+t_name);
						listener.onReadyData(ctx, map, reader.getLastReadLine());
						map = new LinkedHashMap<String, Object>();
					}
					done();
					return;
				} else if((sb.toString().trim()).length()>0) {
					if(isInit) isConfigurableTable = isConfigurableTable(sb);
					isInit = false;
					String t_name = "";
					if(isConfigurableTable && !Param.contains("PRINT")){
						reader.skipEOLs();
						ConfiguredHeader[] configuredHeaders = headersMap.get("CONFIGURABLE");
						if(sb.toString().trim().startsWith(configuredHeaders[0].getName())) {
							String[] header = sb.toString().split(" \\b");
							for(int i=0; i<configuredHeaders.length-1; i++) {
								//reset header
//								System.out.println(configuredHeaders[i].getName() + " : " + configuredHeaders[i].length);
//								System.out.println(header[i] + " : " + header[i].length());
								if(configuredHeaders[i].getName().equalsIgnoreCase(header[i].trim()))
									configuredHeaders[i].setLength(header[i].length()+1);
								else
									break;
							}
						}
						if(reader.isEqual(' '))
							reader.read().skipEOLs();
						while(!reader.isEOL() && !reader.isEqual('<') && !reader.isEqual('C')){
							parse(map, sb, configuredHeaders);
							//TODO:CHECK THIS
							t_name = "MERGED";
							ctx.setTableName(T_NAME+"_"+t_name);
							listener.onReadyData(ctx, map, reader.getLastReadLine());
							map = new LinkedHashMap<String, Object>();
							reader.skipEOLs();
							if(reader.isEqual(' '))
								reader.read().skipEOLs();
						}
						done();
						return;
					}else if(isProperty(sb) && !sb.toString().trim().equals("ADDITIONAL PARAMETERS:")){	//Here we go..
												
						if(sb.toString().trim().startsWith(parentName) || sb.toString().trim().contains(parentNCGR)){//PARENT CGR	
							t_name = "MERGED";
							if (countCGR!=0){
									ctx.setTableName(T_NAME+"_"+t_name);
									listener.onReadyData(ctx, map, reader.getLastReadLine());
									map = new LinkedHashMap<String, Object>();
									parse(map, sb);
								}else
									parse(map, sb);
								
								countCGR++;
							if(map.get("parentId")!=null)
								cgrId = Long.parseLong(map.get("parentId").toString());								
						}
						else
							parse(map, sb);
						
					}
					else if(sb.toString().trim().startsWith("PCM-TSL")){	//<yyn>		
						if (map.get(parentName)!=null || map.get(parentNCGR)!=null)//last
						{	
							t_name = "MERGED";
							ctx.setTableName(T_NAME+"_"+t_name);
							listener.onReadyData(ctx, map, reader.getLastReadLine());
							if(map.get("parentId")!=null)
								cgrId = Long.parseLong(map.get("parentId").toString());							
							map = new LinkedHashMap<String, Object>();
						}														
						countCGR=0;
						
						t_name = "PCM_TSL";								
						map1= new LinkedHashMap<String, Object>();
						ConfiguredHeader[] configuredHeaders = headersMap.get("PCM_TSL");
						while(!reader.isEOL() && !reader.isEqual('<') && !reader.isEqual('C') && !reader.isEqual(' ')){							
							map1.put("CGR_ID", cgrId);
							parse(map1, sb, configuredHeaders);
							ctx.setTableName(T_NAME+"_"+t_name);
							listener.onReadyData(ctx, map1, reader.getLastReadLine());
							map1 = new LinkedHashMap<String, Object>();//
							reader.skipEOLs();

						}
					}
					else if(sb.toString().trim().startsWith("TERMID")){
						String Termid;
						String CCSPM;
						String BLKG;
						
						if (map.get(parentName)!=null || map.get(parentNCGR)!=null)//last
						{	
							t_name = "MERGED";
							ctx.setTableName(T_NAME+"_"+t_name);
							listener.onReadyData(ctx, map, reader.getLastReadLine());
							if(map.get("parentId")!=null)
								cgrId = Long.parseLong(map.get("parentId").toString());								
							map = new LinkedHashMap<String, Object>();
						}
						countCGR=0;

						ConfiguredHeader[] configuredHeaders = headersMap.get("TERMID");
						t_name = "TERMID";										
						map2= new LinkedHashMap<String, Object>();
						while(!reader.isEOL() && !reader.isEqual('<') && !reader.isEqual('C') && !reader.isEqual(' ')){							
							map2.put("CGR_ID", cgrId);
							parse(map2, sb, configuredHeaders);
							
							Termid=map2.get("TERMID")!=null ?  map2.get("TERMID").toString().trim() : null;
							CCSPM=map2.get("CCSPCM")!=null ?  map2.get("CCSPCM").toString().trim() : null;
							
							if (Termid!=null && CCSPM!=null && isNumericString(CCSPM) && Termid.indexOf("-")>-1){
								BLKG=Termid.substring(Termid.indexOf("-")+1);
								if (isNumericString(BLKG)){
									int cspm=Integer.parseInt(CCSPM);
									int ts=Integer.parseInt(BLKG);
									map2.put("E1", cspm+1);
									map2.put("TS", ts);
									map2.put("CIC", cspm*32+ts);
								}
							}
									
							ctx.setTableName(T_NAME+"_"+t_name);
							listener.onReadyData(ctx, map2, reader.getLastReadLine());
							reader.skipEOLs();
						
						}
					}
					else if(sb.toString().trim().startsWith("CIC") && sb.toString().trim().contains("ORD")){//CIC            ORD
						if (map.get(parentName)!=null || map.get(parentNCGR)!=null)//last
						{	
							t_name = "MERGED";
							ctx.setTableName(T_NAME+"_"+t_name);
							listener.onReadyData(ctx, map, reader.getLastReadLine());
							if(map.get("parentId")!=null)
								cgrId = Long.parseLong(map.get("parentId").toString());								
							map = new LinkedHashMap<String, Object>();
						}
						countCGR=0;

						ConfiguredHeader[] configuredHeaders = headersMap.get("CIC");
						t_name = "CIC";								
						map3= new LinkedHashMap<String, Object>();
						while(!reader.isEOL() && !reader.isEqual('<') && !reader.isEqual('C') && !reader.isEqual(' ')){
							map3.put("CGR_ID", cgrId);
							parse(map3, sb, configuredHeaders);
							ctx.setTableName(T_NAME+"_"+t_name);
							listener.onReadyData(ctx, map3, reader.getLastReadLine());
							reader.skipEOLs();
						
						}
					}	
					else if(sb.toString().trim().startsWith("VCRCT") && sb.toString().trim().contains("ORD")){//VCRCT          ORD
						if (map.get(parentName)!=null || map.get(parentNCGR)!=null)//last
						{	
							t_name = "MERGED";
							ctx.setTableName(T_NAME+"_"+t_name);
							listener.onReadyData(ctx, map, reader.getLastReadLine());
							if(map.get("parentId")!=null)
								cgrId = Long.parseLong(map.get("parentId").toString());								
							map = new LinkedHashMap<String, Object>();
						}
						countCGR=0;
						
						ConfiguredHeader[] configuredHeaders = headersMap.get("VCRCT");
						t_name = "VCRCT";							
						map3= new LinkedHashMap<String, Object>();
						while(!reader.isEOL() && !reader.isEqual('<') && !reader.isEqual('C') && !reader.isEqual(' ')){
							map3.put("CGR_ID", cgrId);
							parse(map3, sb, configuredHeaders);
							ctx.setTableName(T_NAME+"_"+t_name);
							listener.onReadyData(ctx, map3, reader.getLastReadLine());
							map3 = new LinkedHashMap<String, Object>();
							reader.skipEOLs();
						
						}
					}
					//</yyn>
					else{
						if (!isIgnoredResult(sb.toString()))
							System.err.println("Skip "+ sb);
					}
//					if(reader.equals(' '))
//						reader.read().skipEOLs();
				}
			}
		}
	}
	protected void parse(Map<String, Object> map, StringBuilder sb, ConfiguredHeader[] headers) throws IOException{
		int lastIndex = headers.length-1;	
		for (int j = 0; j < headers.length; j++) {
			ConfiguredHeader header = headers[j];
			if (j == lastIndex) {
				reader.readUntilEOL(sb);
			}else
				reader.read(sb, header.length);
			
			map.put(header.getName(), sb.toString().trim());
		}
	}
	
	protected void parse(Map<String, Object> map, StringBuilder sb){
		final String bannedParam = "SPC(H/D)";
		final String successorParam = "SPC_H_D";

		while(sb.length() > 0){
			int lastIdx = sb.lastIndexOf(":");
			final String val = sb.substring(lastIdx+1).trim();
			sb.delete(lastIdx-1, sb.length());
			while(sb.charAt(sb.length()-1)==' ') sb.delete(sb.length()-1,sb.length());
			lastIdx = sb.lastIndexOf(" ");
			final String param = lastIdx==-1 ? sb.toString().trim() : sb.substring(lastIdx+1).trim();
			sb.delete(lastIdx==-1 ? 0 : lastIdx, sb.length());
			
			if (param.charAt(0)=='S' && param.equals(bannedParam) && !"-".equals(val))
				map.put("SPC_H_D_DEC", val.indexOf("/")>-1 ? Integer.parseInt(val.substring(val.indexOf("/")+1)) : val);
			
			if (!"-".equals(val))
				map.put(param.charAt(0)=='S' && param.equals(bannedParam) ? successorParam : param, val);
			
		}
	}
	
	protected boolean isIgnoredResult(String s){
		return (s.charAt(0)=='A' || s.charAt(0)=='S' || s.charAt(0)=='N' || s.charAt(0)=='C') && listIgnore.contains(s.trim());
	}
	
	protected boolean isConfigurableTable(StringBuilder sb){
		return sb.indexOf(":")==-1 && sb.toString().contains("NCGR");
	}
	
	protected boolean isProperty(StringBuilder sb){
		return !(sb.indexOf(":")==-1) && !isIgnoredResult(sb.toString());
	}
	protected boolean isNumericString (String S){
		try
		{
			Integer.parseInt( S );  
			return true;
		} 
		catch ( Exception e)
		{
			return false;
		}
	}
}
