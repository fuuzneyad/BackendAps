package id.co.telkom.parser.entity.dashboard.rao;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.propreader.ParserPropReader;
import id.co.telkom.parser.common.util.CharParserExtender;

public class RaoE3GRLogAlarm extends AbstractParser {
	Map<String, Object> map = new LinkedHashMap<String, Object>();
	Map<String, Object> buffer = new LinkedHashMap<String, Object>();
	ConfiguredHeader[] configuredHeaders;
	final static String[] t_name = {"E3GRLOGRAW"};
	private CharParserExtender chr=null;
	private final List<String> inputToField= new ArrayList<String>();
	
	public RaoE3GRLogAlarm(ParserPropReader cynapseProp,
			AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
		initinputToField();    
		configuredHeaders= new ConfiguredHeader[]{
        		new ConfiguredHeader(" LOGID      "),
				new ConfiguredHeader(" PROBABLE_CAUSE "),
				new ConfiguredHeader("ACL "),
				new ConfiguredHeader("EVENTTYPE        "),				
				new ConfiguredHeader("SEVERITY      "),
				new ConfiguredHeader("EVENT_TIME            "),
				new ConfiguredHeader("CLEAR_TIME            "),
				new ConfiguredHeader("OBJ_REFERENCE                                                                                                                                                                                                                                                   "),
				new ConfiguredHeader("SPECIFIC_PROBLEM "),
				new ConfiguredHeader("PROBLEM_TEXT                                                                                                                                                                                                                                                    ")
		};
	}
	private void initinputToField(){
		inputToField.add("SubNetwork");
		inputToField.add("MeContext");
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		loader.onBeginFile();
		ctx.setTableName(t_name[0]);
		int lastIndex = configuredHeaders.length-1;
		StringBuilder sb = new StringBuilder();
		InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
		chr = new CharParserExtender(reader){};
		chr.read();		
		chr.skipLines(2).skipEOLs();
		while (!chr.isEOF()) {
                            while(!chr.isEOL()){
                            	chr.read(sb, configuredHeaders[0].length);
                            	String First =sb.toString().trim();
                            	//check if first field is Numeric
                            	if (isNumericString(First)){
                            		                            		
                            		if (buffer.get(configuredHeaders[lastIndex].getName())!=null){
//                            			log.info(buffer);
                            			//READY
                            			loader.onReadyModel(buffer, ctx);
                            			buffer = new LinkedHashMap<String, Object>();
                            		}
                            		buffer.put(configuredHeaders[0].getName(),First);
                            		//sisanya
                            		for (int j = 1; j < configuredHeaders.length; j++) {
                            			ConfiguredHeader header = configuredHeaders[j];
                            			if (j == lastIndex) {
                            				chr.readUntilEOL(sb).skipEOLs();
                            				buffer.put(header.getName(), sb.toString().trim());
//                            				handlers.onTreeEntry(normalize(map, context, getLine()));
                            			} else{
                            				chr.read(sb, header.length);  
                            				if(header.getName().equals("OBJ_REFERENCE")){
//                            					buffer.put(header.getName(), sb.toString().trim()); 
                            					ProcessObjReference(sb.toString().trim());
                            				}else
                            					buffer.put(header.getName(), sb.toString().trim());                            				
                            			}
                            		}
                            	}else{
                            		chr.readUntilEOL(sb).skipEOLs();
                            		if (configuredHeaders[lastIndex].getName()!=null)
                    					buffer.put(configuredHeaders[lastIndex].getName(), buffer.get(configuredHeaders[lastIndex].getName())+" "+First+sb.toString().trim());
                            	}
                                
                            }

		}
		//last
		if (buffer.get(configuredHeaders[lastIndex].getName())!=null){
			loader.onReadyModel(buffer, ctx);
		}
		reader.close();
		loader.onEndFile();
	}

	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx)
			throws Exception {
		
	}
	 private void ProcessObjReference(String str){        	
     	if(str.indexOf(",")>0){
     		String[] splitted=str.split(",");
     		int indicateSubnet=0;
     		for(int i=0; i<splitted.length;i++){       			
     			if(splitted[i].indexOf("=")>0){
     				String[] paramValue = splitted[i].split("=");  
     				if(paramValue.length<2)
     					paramValue=new String[] {paramValue[0],""};
     				
     				if (paramValue[0].contains("SubNetwork")){       
     					indicateSubnet++;
     					buffer.put(paramValue[0]+indicateSubnet, paramValue[1]);         					
     				}else{
     					if(inputToField.contains(paramValue[0])){
     						buffer.put(paramValue[0], paramValue[1]);
     					}else{
     						if (buffer.get("ADD_INFO")==null)
     							buffer.put("ADD_INFO", paramValue[0]+"="+paramValue[1]);else
     							buffer.put("ADD_INFO", buffer.get("ADD_INFO")+","+paramValue[0]+"="+paramValue[1]);
     					}
     				}
     			}
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
	protected void CreateSchemaFromMap() {
		StringBuilder current = new StringBuilder();
		String[] tables = t_name;
		for (int i = 0; i < tables.length; i++) {

				current.append("CREATE TABLE ").append(tables[i]).append(" (\n");
				current.append("\tENTRY_DATE TIMESTAMP DEFAULT NOW(),\n");
				current.append("\tLINE BIGINT(9),\n");

				for (ConfiguredHeader configuredHeader : configuredHeaders) {
                    if (configuredHeader.getDbLength()>250)
                    	current.append("\t"+configuredHeader.getName()).append(' ').append("TEXT ").append(",\n");else
                        current.append("\t"+configuredHeader.getName()).append(' ').append("VARCHAR(").append(configuredHeader.getDbLength()).append("),\n");
                    }
				current.append("\t"+"ADD_INFO").append(' ').append("TEXT ").append(",\n");
				for (String TheStr : inputToField ){
					if(TheStr.equalsIgnoreCase("SubNetwork")){
						current.append("\t"+TheStr.toUpperCase()+"1" ).append(' ').append("VARCHAR(50)").append(",\n");
						current.append("\t"+TheStr.toUpperCase()+"2" ).append(' ').append("VARCHAR(50)").append(",\n");
					}else
						current.append("\t"+TheStr.toUpperCase() ).append(' ').append("VARCHAR(50)").append(",\n");
				}
				current.setLength(current.length() - 2);
				current.append("\n);\r\n");
			
		}
		System.out.println(current);
	}
	
	

}
