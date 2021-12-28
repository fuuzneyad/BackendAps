package id.co.telkom.parser.entity.dashboard.rao;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.propreader.ParserPropReader;
import id.co.telkom.parser.common.util.CharParserExtender;

public class RaoE2G3GActiveAlarm extends AbstractParser {
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	Map<String, Object> map = new LinkedHashMap<String, Object>();
	Map<String, Object> buffer = new LinkedHashMap<String, Object>();
	ConfiguredHeader[] configuredHeaders;
	final static String[] t_name = {"E2G3GRRACTRAW"};
	private CharParserExtender chr=null;
	
	public RaoE2G3GActiveAlarm(ParserPropReader cynapseProp,
			AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
		headersMap.put("MASTER", new ConfiguredHeader[]{
				new ConfiguredHeader(" LOGID       "),
				new ConfiguredHeader("PROBABLE_CAUSE "),
				new ConfiguredHeader("ACL "),
				new ConfiguredHeader("EVENTTYPE        "),				
				new ConfiguredHeader("SEVERITY      "),
				new ConfiguredHeader("EVENT_TIME            "),	
				new ConfiguredHeader("OBJ_REFERENCE                                                                                                                                                                                                                                                   "),
				new ConfiguredHeader("SPECIFIC_PROBLEM "),
				new ConfiguredHeader("PROBLEM_TEXT                                                                                                                                                                                                                                                    ")
		});

		headersMap.put("ES", new ConfiguredHeader[]{//SES
				new ConfiguredHeader("DIP      "),
				new ConfiguredHeader("DIPPART  "),
				new ConfiguredHeader("ESL2   "),
				new ConfiguredHeader("QSV    "),
				new ConfiguredHeader("SECTION  "),
				new ConfiguredHeader("DATE    "),
				new ConfiguredHeader("TIME")
		});
		headersMap.put("DIP_NOHDR", new ConfiguredHeader[]{
				new ConfiguredHeader("DIP      "),
				new ConfiguredHeader("DIPEND   "),
				new ConfiguredHeader("FAULT     "),
				new ConfiguredHeader("SECTION   "),
				new ConfiguredHeader("HG  "),
				new ConfiguredHeader("DATE    "),
				new ConfiguredHeader("TIME")
		});		
		headersMap.put("SF", new ConfiguredHeader[]{
				new ConfiguredHeader("DIP      "),
				new ConfiguredHeader("DIPPART  "),
				new ConfiguredHeader("SFL    "),
				new ConfiguredHeader("QSV    ")
		});
		
		headersMap.put("MOF", new ConfiguredHeader[]{
				new ConfiguredHeader("MO                                 "),
				new ConfiguredHeader("RSITE           "),
				new ConfiguredHeader("ALARM_SLOGAN")
		});
		
		headersMap.put("GETTABLE", new ConfiguredHeader[]{
				new ConfiguredHeader("EXTERNAL_ALARM"),
				new ConfiguredHeader("AP_FAULT"),
				new ConfiguredHeader("FAN_ALARM")
		});
		
//		headersMap.put("CELLLOGICAL", new ConfiguredHeader[]{
//				new ConfiguredHeader("CELL       "),
//				new ConfiguredHeader("SCTYPE   "),
//				new ConfiguredHeader("CHTYPE   ")
//		});
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		loader.onBeginFile();
		ctx.setTableName(t_name[0]);
		configuredHeaders = headersMap.get("MASTER");
		int lastIndex = configuredHeaders.length-1;
		StringBuilder sb = new StringBuilder();
		InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
		chr = new CharParserExtender(reader){};
		chr.read();
		chr.skipLines(2).skipEOLs();
		while (!chr.isEOF()) {
                            while(!chr.isEOL()){
                            	chr.read(sb, configuredHeaders[0].length);
                            	String First =sb.toString();
                            	//check if first field is Numeric
                            	if (isNumericString(First.trim())){
                            		buffer.put(configuredHeaders[0].getName(),First.trim());
                            		//sisanya
                            		for (int j = 1; j < configuredHeaders.length; j++) {
                            			ConfiguredHeader header = configuredHeaders[j];
                            			if (j == lastIndex ) {
                            				chr.readUntilEOL(sb).skipEOLs();
                            				buffer.put(header.getName(), sb.toString().trim());
                            				
                            				String ACL = buffer.get("ACL").toString().trim();
                            				boolean is3G = (ACL!=null && ACL.equals(""));
                            				if(!is3G ){
                            					buffer.put("TECH_VERSION", "2G");
	                            				//After Problem text handling, named it to problem_exp
                            					chr.readUntilEOL(sb).skipEOLs();
	                            				buffer.put("PROBLEM_EXP", sb.toString().trim());
	                            				
	                            				if (sb.toString().equals("RADIO X-CEIVER ADMINISTRATION"))
	                            				{
	                            					chr.skipLines(1).skipEOLs();
	                            				}
	                            				
	                            				//handle the childs
	                            				chr.readUntilEOL(sb).skipEOLs();
	                            				String strChildChk =sb.toString().trim();
	                            				if (strChildChk.equals("ES") || strChildChk.equals("ESR")// atas, skipped
	                            						|| strChildChk.equals("SES") || strChildChk.equals("SESR") 
	                            						|| strChildChk.equals("SF") ){
	
	                            					chr.readUntilEOL(sb).skipEOLs();
	                            					parseChild(sb.toString());
	                            				}
	                            				else          
	                                    		if(strChildChk.startsWith("LS")){
	                                    				parseChild(strChildChk);
	                                    				chr.readUntilEOL(sb).skipEOLs();
	                                    				parseChild(sb.toString());
	                                    				chr.readUntilEOL(sb).skipEOLs();
	                                    				parseChild(sb.toString());
	                                    		}
	                                    		else
	                            				if(strChildChk.startsWith("MO                RSITE            CLASS")){
	                                				parseChild(strChildChk);
	                                				chr.readUntilEOL(sb).skipEOLs();
	                                				parseChild(sb.toString());
	                            				}      
	                            				else
	                                				if(strChildChk.startsWith("AP    APNAME         NODE      NODENAME")){
	                                    				parseChild(strChildChk);
	                                    				chr.skipEOLs().readUntilEOL(sb).skipEOLs();
	                                    				buffer.put("AP_FAULT", sb.toString());
	                                				}
	                                    		else
	                                    			parseChild(strChildChk);
                            				}else
                            					buffer.put("TECH_VERSION", "3G");
                            						
                            				
                            				
                            				//READY
//                            				handlers.onTreeEntry(normalize(buffer, context, getLine()));
                            				loader.onReadyModel(buffer, ctx);
                            				buffer = new LinkedHashMap<String, Object>();
                            			}else{
                            				chr.read(sb, header.length);
                            				buffer.put(header.getName(), sb.toString().trim());                            				
                            			}
                            		}
                            	}else{
                            		chr.readUntilEOL(sb).skipEOLs();  
                            		if (!First.contains("END"))
                            		System.out.println("luar.."+First+sb.toString());
                            	}
                                
                            }

		}
		reader.close();
		loader.onEndFile();
	}
	private void parseChild(String StrHeader){
		if(!StrHeader.equals("END")){
			try {
				StringBuilder sb = new StringBuilder();
				//split header
				String[] splitted = StrHeader.split(" \\b");
				for (int i=0; i<splitted.length; i++){
					boolean isReadToEOL=(StrHeader.contains("ALARM SLOGAN") && splitted[i].startsWith("ALARM"))
										||
										(StrHeader.contains("EXTERNAL ALARM") && splitted[i].startsWith("EXTERNAL"))
										||
										(StrHeader.contains("FAN ALARM") && splitted[i].startsWith("FAN"))
										;
					
					if(i==splitted.length-1 || isReadToEOL){
						chr.readUntilEOL(sb).skipEOLs();
					}else{														
						chr.read(sb, splitted[i].length()+1);
					}
					//process
					if(isReadToEOL)
						{	
							if (splitted[i].startsWith("ALARM"))
								buffer.put("ALARM_SLOGAN", sb.toString().trim());else
							if (splitted[i].startsWith("EXTERNAL"))
								buffer.put("EXTERNAL_ALARM", sb.toString().trim());else
							if (splitted[i].startsWith("FAN"))
								buffer.put("FAN_ALARM", sb.toString().trim());		
							break;
						}
						else	
							buffer.put(splitted[i].trim(), sb.toString().trim());//here the master
											
				}
				
			}catch (IOException e){
				e.printStackTrace();
			}
		}
	}
	
	 boolean isNumericString (String S){
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
	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx)
			throws Exception {
		
	}

	@Override
	protected void CreateSchemaFromMap() {
		final Set<String> keySet = headersMap.keySet();
		Iterator<String> iterator = keySet.iterator();
		int c = -1;
		final String[] headerMapName= new String[keySet.size()];
		while(iterator.hasNext()){
			c++;
			headerMapName[c] = iterator.next();
		}
    	
		StringBuilder current = new StringBuilder();
		String[] tables = t_name;
		for (int i = 0; i < tables.length; i++) {

			current.append("CREATE TABLE ").append(tables[i]).append(" (\n");
			current.append("\tENTRY_DATE TIMESTAMP DEFAULT NOW(),\n");
			current.append("\tLINE BIGINT(9),\n");
			current.append("\tREGIONAL VARCHAR(9),\n");
			
			for (String hName : headerMapName){
				ConfiguredHeader[] cfh =headersMap.get(hName);
				for (ConfiguredHeader configuredHeader : cfh) {
						if (current.indexOf("\t"+configuredHeader.getName()+" ")<0){
                          if (configuredHeader.getDbLength()>250)
                            current.append("\t"+configuredHeader.getName()).append(' ').append("VARCHAR(250)").append(",\n");else
                            current.append("\t"+configuredHeader.getName()).append(' ').append("VARCHAR(").append(configuredHeader.getDbLength()).append("),\n");
                          if (hName.equals("MASTER") && configuredHeader.getName().equals("PROBLEM_TEXT")){
                        	  current.append("\t"+"PROBLEM_EXP").append(' ').append("VARCHAR(").append("250").append("),\n");
                        	  current.append("\t"+"DETAIL_TPY").append(' ').append("VARCHAR(").append("50").append("),\n");
                          }
						}
                          
                }
			}
			current.setLength(current.length() - 2);
			current.append("\n);\r\n");
		}
		System.out.println(current);
	}

}
