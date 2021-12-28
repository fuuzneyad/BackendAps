package id.co.telkom.parser.entity.cm.siemens;

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

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class SiemensCMParser10 extends AbstractParser{
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	
	public SiemensCMParser10(ParserPropReader cynapseProp,
			AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		
		String filename=file.getName().toUpperCase();
		String br_type=filename.contains("BR") && filename.contains("-") ? filename.substring(filename.indexOf("BR"), filename.indexOf("-")):null;
		String ne_id=filename.contains("SBS") && filename.contains(".") ? filename.substring(filename.indexOf("SBS"), filename.indexOf(".")):null;
		
		ctx.setNe_id(ne_id);
		ctx.setDatetimeid(convertDate(filename.substring(filename.lastIndexOf("_")+1)));
		
		  FileInputStream fstream = new FileInputStream(file);
	      BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));	
	      String stringLine;
	      while ((stringLine = br.readLine()) != null)
	      {
	    	  
	    	  if(stringLine.indexOf(":")>0){
	    		  String tableName=cynapseProp.getTABLE_PREFIX()+stringLine.split(":")[0].replace(" ", "_").trim();
	    		  if(!cynapseProp.isGENERATE_SCHEMA_MODE())
	    			  loader.onNewTable(tableName, ctx);
	    		  String[] split =stringLine.split(",");
	    		  int i=0;
	    		  for(String a:split){
	    			  if(i==0 && a.contains("NAME=")){
	    				  String NAME=a.substring(a.indexOf("NAME=")+5);
	    				  ctx.setMo_id(br_type+"/"+NAME);
	    				  if(cynapseProp.isGENERATE_SCHEMA_MODE())
	    					  PutModel(tableName, "NAME", NAME);
	    				  else
	    					  map.put("NAME", NAME);
	    				  	  
	    				  }else{
	    					  
	    					  if(a.contains("=")){
	    						  String[] split2=a.split("=");
	    						  String param=split2[0];
	    						  String value=split2[1].contains("<NULL>")? null : split2[1].replace(";", "").replace("\"", ""); 
	    						  if(cynapseProp.isGENERATE_SCHEMA_MODE())
	    							  PutModel(tableName, param, value);
	    						  else
	    							  map.put(param, value) ;
	    							 
	    					  }
	    				  }	  
	    			  i++;
	    		  }
	    		  if(!cynapseProp.isGENERATE_SCHEMA_MODE()){
	    			  loader.onReadyModel(map, ctx);
	    		  	  map = new LinkedHashMap<String, Object>();
	    		  }
	    	  }
	      }
	      loader.onEndFile();
	      fstream.close();		  
	}

	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx)
			throws Exception {
		
	}

	@Override
	protected void CreateSchemaFromMap() {
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"SiemensCMSchema.sql";
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
				sb.append("\t`MO_ID` varchar(200) DEFAULT NULL,\n");
				sb.append("\t`BR_TYPE` varchar(200) DEFAULT NULL,\n");
				
				for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
					sb.append("\t`"+entry2.getKey()+"` VARCHAR("+((entry2.getValue().length()+20))+"),\n");
				}
				sb.setLength(sb.length()-2);
				sb.append("\n)Engine=MyIsam;\n");
				
				out.write(sb.toString());	
				out.flush();
				sb = new StringBuilder();

			}
			out.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	protected static String convertDate(String val) {
		String format;
		if(val.length()=="yyyyMMddHHmmss".length())
			format="yyyyMMddHHmmss";else
		if(val.length()=="yyyyMMddHHmm".length())
			format="yyyyMMddHHmm";else
		format="yyyyMMddHHmm";
		
		SimpleDateFormat fromUser = new SimpleDateFormat(format);
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		try{
			return myFormat.format(fromUser.parse(val));
		}catch(ParseException e){return val;}
	}

}
