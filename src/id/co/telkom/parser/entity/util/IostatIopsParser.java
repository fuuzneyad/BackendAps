package id.co.telkom.parser.entity.util;

import java.io.BufferedReader;
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

public class IostatIopsParser extends AbstractParser{
	private Map<String, String> header = new LinkedHashMap<String, String>();
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	
	public IostatIopsParser(ParserPropReader cynapseProp,
			AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
	}
	
	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		loader.onBeginFile();
		ctx.setNe_id(file.getName().split("_")[0]);
		FileInputStream fstream = new FileInputStream(file);
		  InputStreamReader s = new InputStreamReader(fstream);
	      BufferedReader br = new BufferedReader(s);	
	      String stringLine;
	      while ((stringLine = br.readLine()) != null)
	      {
//	    	  System.out.println(stringLine);
	    	  if(stringLine.startsWith("JAM=")){
	    		  ctx.setDatetimeid(convertDate(stringLine.replace("JAM=", "")));
	    	  }
	    	  if(stringLine.startsWith("avg-cpu:")){
	    		  final String t_name="AVG_CPU";
	    		  String[] head = stringLine.split("\\s+");
	    		  for(int i=0;i<head.length;i++)
	    			  header.put("H"+i, head[i].replace("%", "").replace("-", "").replace(":", "").toUpperCase());
	    		  stringLine = br.readLine();
	    		  String[] split=stringLine.split("\\s+");
	    		  ctx.setTableName(t_name);
	    		  for(int i=1;i<split.length;i++){
	    			  if(cynapseProp.isGENERATE_SCHEMA_MODE())
	    				  PutModel(t_name, header.get("H"+i), "30");
	    			  map.put(header.get("H"+i), split[i]);
	    		  }
//	    		  System.out.println(map);
	    		  if(!cynapseProp.isGENERATE_SCHEMA_MODE())
	    			  loader.onReadyModel(map, ctx);
	    		  map = new LinkedHashMap<String, Object>();
	    	  }else
	    		  if(stringLine.startsWith("Device:")){
	    			  final String t_name="DEV_IO";
	    			  String[] head = stringLine.split("\\s+");
	    			  header.clear();
		    		  for(int i=0;i<head.length;i++)
		    			  header.put("H"+i, head[i].replace("/", "_").replace(":", "").toUpperCase());
		    		  while ((stringLine = br.readLine()) != null)
		    	      {
		    			  if(stringLine.equals(""))
		    				  break;
	//		    			  System.out.println(stringLine);
			    			  String[] split=stringLine.split("\\s+");
				    		  ctx.setTableName(t_name);
				    		  for(int i=0;i<split.length;i++){
				    			  if(cynapseProp.isGENERATE_SCHEMA_MODE())
				    				  PutModel(t_name, header.get("H"+i), "30");
				    			  map.put(header.get("H"+i), split[i]);
				    		  }
//				    		  System.out.println(map);
				    		  if(!cynapseProp.isGENERATE_SCHEMA_MODE())
				    			  loader.onReadyModel(map, ctx);
				    		  map = new LinkedHashMap<String, Object>();
		    			  }
		    	      }
		    	  
	      }
	      br.close();
	      s.close();
		loader.onEndFile();
	}

	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx)
			throws Exception {
		
	}

	@Override
	protected void CreateSchemaFromMap() {
		 try{
				String location=cynapseProp.getFILE_SCHEMA_LOC()+"IostatSchema.sql";
				System.out.println("Generating Schema to "+location+"..");
				FileWriter out = new FileWriter(location);
				
				StringBuilder sb = new StringBuilder();
				
				for (Map.Entry<String, Map<String,String>> entry : tableModel.entrySet()) {
					
					sb.append("/*Schema for "+cynapseProp.getTABLE_PREFIX()+entry.getKey()+"*/\n");
					sb.append("CREATE TABLE "+cynapseProp.getTABLE_PREFIX()+entry.getKey()+" (\n");
					sb.append("\t`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n");
					sb.append("\t`DATETIME_ID` datetime NULL DEFAULT NULL,\n");
					
					for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
						
						String typeData = isDouble(entry2.getValue()) ? "DOUBLE,\n" : "VARCHAR("+entry2.getValue().length()+20+"),\n"; 
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

	protected static String convertDate(String val) {
		String format;
		if(val.length()=="yyyyMMddHHmmss".length())
			format="yyyyMMddHHmmss";else
		if(val.length()=="yyyyMMddHHmm".length())
			format="yyyyMMddHHmm";else
		format="yyyyMMddHHmmss";
		
		SimpleDateFormat fromUser = new SimpleDateFormat(format);
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try{
			return myFormat.format(fromUser.parse(val));
		}catch(ParseException e){return null;}
	}
}
