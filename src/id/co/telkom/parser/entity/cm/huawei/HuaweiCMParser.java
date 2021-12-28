package id.co.telkom.parser.entity.cm.huawei;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
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

public class HuaweiCMParser extends AbstractParser{
	private ParserPropReader cynapseProp;
	@SuppressWarnings("unused")
	private Map<String, StandardMeasurementModel> modelMap;
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	private static final Logger logger = Logger.getLogger(HuaweiCMParser.class);
	
	@SuppressWarnings("unchecked")
	public HuaweiCMParser(ParserPropReader cynapseProp, AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
		this.modelMap = (Map<String, StandardMeasurementModel>)cynapseInit.getMappingModel();
		this.cynapseProp=cynapseProp;
	}
	
	
	@Override
	public synchronized void  ProcessFile(File file,  final LoaderHandlerManager loader,  final Context ctx) throws Exception {

			  loader.onBeginFile();
			  
			  int pos = file.getName().indexOf("Regional_");
			  String reg= pos >-1 ? file.getName().substring(pos+"Regional_".length(), pos+"Regional_".length()+1):null;
			  String datetime = file.getName().contains("_") && file.getName().contains(".")? convertDate(file.getName().substring(file.getName().lastIndexOf("_")+1, file.getName().lastIndexOf("."))):null; 
			  ctx.setDatetimeid(datetime);
			  FileInputStream fstream = new FileInputStream(file);
		      BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));	
		      String stringLine;
		      while ((stringLine = br.readLine()) != null)
		      {
		    	  if(stringLine.startsWith("/")){
//		    		  System.out.println(stringLine);
		    		  int idxDateTime=stringLine.indexOf("Export start time:");
		    		  if(idxDateTime>-1){
//		    			  requested, not used
//		    			  ctx.setDatetimeid(convertDate(stringLine.substring(idxDateTime+"Export start time:".length())));
		    		  }
		    		  
		    	  }else
		    	  if(stringLine.indexOf(":")>0){
		    		  String tableName=stringLine.split(":")[0].replace(" ", "_").trim();
		    		  ctx.setTableName(tableName);
		    		  String[] split =stringLine.split(":")[1].split(",");
		    		  @SuppressWarnings("unused")
					int i=0;
		    		  for(String a:split){
		    				  {
		    					  if(a.contains("=")){
		    						  String[] split2=a.split("=");
		    						  String param=(split2[0].trim())+"_";
		    						  String value=split2[1].contains("<NULL>")? null : split2[1].replace(";", "").replace("\"", "").trim();
		    						  
		    						  if(tableName.equals("SET_SYS") && param.equals("SYSOBJECTID_")){
		    							  ctx.setNe_id(value.replace("\"", ""));
		    							  ctx.setMo_id(value.replace("\"", ""));
		    						  }  
			    						  if(cynapseProp.isGENERATE_SCHEMA_MODE()){
			    							  PutModel(tableName, param, value);
			    						  }
			    						  else{
			    						      map.put(param, value);
			    						  }
		    					  }
		    				  }	  
		    			  i++;
		    		  }
		    		  if(!cynapseProp.isGENERATE_SCHEMA_MODE()){
		    			  map.put("REGIONAL", reg);
		    			  loader.onReadyModel(map, ctx);
		    			  map = new LinkedHashMap<String, Object>();
		    		  }
		    	  }
		      }
		      br.close();
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

	private static String convertDate(String val) {
		String format;
		if(val.length()=="yyyyMMdd".length())
			format="yyyyMMdd";
		else
			format="yyyyMMddHHmm";
		
		SimpleDateFormat fromUser = new SimpleDateFormat(format);
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		try{
			return myFormat.format(fromUser.parse(val));
		}catch(ParseException e){return val;}
	}

	
}
