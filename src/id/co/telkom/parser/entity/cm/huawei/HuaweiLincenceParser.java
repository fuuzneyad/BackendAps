package id.co.telkom.parser.entity.cm.huawei;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;


import org.apache.log4j.Logger;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.StandardMeasurementModel;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class HuaweiLincenceParser extends AbstractParser{
	private ParserPropReader cynapseProp;
	@SuppressWarnings("unused")
	private Map<String, StandardMeasurementModel> modelMap;
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	private static final Logger logger = Logger.getLogger(HuaweiLincenceParser.class);
	
	@SuppressWarnings("unchecked")
	public HuaweiLincenceParser(ParserPropReader cynapseProp, AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
		this.modelMap = (Map<String, StandardMeasurementModel>)cynapseInit.getMappingModel();
		this.cynapseProp=cynapseProp;
	}
	
	
	@Override
	public synchronized void  ProcessFile(File file,  final LoaderHandlerManager loader,  final Context ctx) throws Exception {

			  loader.onBeginFile();
			  final String T_NAME="HUAWEILIC";
			  ctx.setTableName(T_NAME);
			  ctx.setDatetimeid(convertDate(ctx.date));
			  String filename=file.getName();
				int pos = filename.indexOf("Regional_");
				String reg= pos >-1 ? filename.substring(pos+"Regional_".length(), pos+"Regional_".length()+1):null;
				if(filename.contains("_")){
					String[] spt=filename.split("_");
					for (int x=0; x<spt.length;x++){
						if((spt[x].contains("BSC")||spt[x].contains("RNC"))&&spt.length>x){
							ctx.setNe_id(spt[x]+"_"+spt[x+1]);
							ctx.setMo_id("-");
							break;
						}
							
					}
				}
					  FileInputStream fstream = new FileInputStream(file);
				      BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));	
				      String stringLine;
				      while ((stringLine = br.readLine()) != null)
				      {
				    		  if(stringLine.startsWith("Product=")){
				    			  final String meas="PRODUCT";
					    		  String val=stringLine.substring(stringLine.indexOf("=")+1);
					    		  if(!cynapseProp.isGENERATE_SCHEMA_MODE())
					    			  map.put(meas, val);else
					    				  PutModel(T_NAME, meas, val);
					    	  }else if(stringLine.startsWith("Feature=")){
				    			  final String meas="FEATURE";
					    		  String val=stringLine.substring(stringLine.indexOf("=")+1);
					    		  if(!cynapseProp.isGENERATE_SCHEMA_MODE())
					    			  map.put(meas, val);else
					    				  PutModel(T_NAME, meas, val);
					    	  }else if(stringLine.startsWith("Esn=")){
				    			  final String meas="ESN";
					    		  String val=stringLine.substring(stringLine.indexOf("=")+1);
					    		  if(!cynapseProp.isGENERATE_SCHEMA_MODE())
					    			  map.put(meas, val);else
					    				  PutModel(T_NAME, meas, val);
					    	  }else if(stringLine.startsWith("Attrib=")){
				    			  final String meas="ATTRIB";
					    		  String val=stringLine.substring(stringLine.indexOf("=")+1);
					    		  if(!cynapseProp.isGENERATE_SCHEMA_MODE())
					    			  map.put(meas, val);else
					    				  PutModel(T_NAME, meas, val);
					    	  }else if(stringLine.startsWith("Resource=")){
					    		  String val=(stringLine.substring(stringLine.indexOf("=")+1)).replace("\"", "");
					    		  String[] arrPair=val.split(",");
					    		  for(String pair:arrPair){
					    			  pair=pair.trim();
					    			  if(pair.contains("=")){
					    				  String key=pair.split("=")[0];
				    				  	  String nil=pair.split("=")[1];
					    				  if(!cynapseProp.isGENERATE_SCHEMA_MODE())
					    					  map.put("RES_"+key, nil);else
					    						  PutModel(T_NAME, "RES_"+key, nil);
					    			  }
					    		  }
					    	  }else if(stringLine.startsWith("Function=")){
					    		  String val=(stringLine.substring(stringLine.indexOf("=")+1)).replace("\"", "");
					    		  String[] arrPair=val.split(",");
					    		  for(String pair:arrPair){
					    			  pair=pair.trim();
					    			  if(pair.contains("=")){
					    				  String key=pair.split("=")[0];
				    				  	  String nil=pair.split("=")[1];
					    				  if(!cynapseProp.isGENERATE_SCHEMA_MODE())
					    					  map.put("FUNCT_"+key, nil);else
					    						  PutModel(T_NAME, "FUNCT_"+key, nil);
					    			  }
					    		  }
					    	  }else if(stringLine.startsWith("Version=")){
				    			  final String meas="VERSION";
					    		  String val=stringLine.substring(stringLine.indexOf("=")+1);
					    		  if(!cynapseProp.isGENERATE_SCHEMA_MODE())
					    			  map.put(meas, val);else
					    				  PutModel(T_NAME, meas, val);
					    	  }else if(stringLine.startsWith("Libver=")){
				    			  final String meas="LIBVER";
					    		  String val=stringLine.substring(stringLine.indexOf("=")+1);
					    		  if(!cynapseProp.isGENERATE_SCHEMA_MODE())
					    			  map.put(meas, val);else
					    				  PutModel(T_NAME, meas, val);
					    	  }else if(stringLine.startsWith("Sign=")){
					    		  if(!cynapseProp.isGENERATE_SCHEMA_MODE()){
					    			  map.put("REGIONAL", reg);
					    			  loader.onReadyModel(map, ctx);
					    		  }
					    	  }
				    		  
				      }
				      fstream.close();
		      loader.onEndFile();
	}
	
	@Override
	public void LoadBuffer(LoaderHandlerManager loader,
			Context ctx) throws Exception {
	}
	
	@Override
	public void CreateSchemaFromMap(){
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"HuaweiCMSchema.sql";
			System.out.println("Generating Schema to "+location+"..");
			FileWriter out = new FileWriter(location);
			
			StringBuilder sb = new StringBuilder();
			
			for (Map.Entry<String, Map<String,String>> entry : tableModel.entrySet()) {
				
				sb.append("/*Schema for "+entry.getKey()+"*/\n");
				sb.append("CREATE TABLE "+entry.getKey()+" (\n");
				sb.append("\t`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n");
				sb.append("\t`SOURCE_ID` varchar(100) DEFAULT NULL,\n");
				sb.append("\t`DATETIME_ID` datetime NULL DEFAULT NULL,\n");
				sb.append("\t`NE_ID` varchar(200) DEFAULT NULL,\n");
				sb.append("\t`MO_ID` varchar(300) DEFAULT NULL,\n");
				sb.append("\t`REGIONAL` varchar(300) DEFAULT NULL,\n");
				
				for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
					
					if(entry2.getKey().length()>30)
						System.err.println("warning field "+entry2.getKey()+"'s  lenght >30, Mapping field is recommended!!");
					String typeTxt = entry2.getValue().toString().length() > 300 ? "TEXT,\n" : "VARCHAR("+(entry2.getValue().toString().length()+20)+"),\n" ;
					String typeData = isDouble(entry2.getValue()) ? "DOUBLE,\n" : typeTxt; 
					sb.append("\t`"+entry2.getKey()+"` "+typeData);
				}
				sb.setLength(sb.length()-2);
				sb.append("\n)Engine=MyIsam;\n");
				out.write(sb.toString());
				out.flush();
				sb = new StringBuilder();
					
			}
			out.close();
		} catch (IOException e){
			logger.error(e);
			e.printStackTrace();
		}
	}

	private boolean isDouble(String s){
		try{
			Double.parseDouble(s);
			return true;
		}catch (NumberFormatException e){
			return false;
		}
	}

	private static String convertDate(Timestamp date) {
		try{
			return new SimpleDateFormat("yyyy-MM-dd 00:00:00").format(date);
		} catch(Exception e){return date.toString();}
	}	
}
