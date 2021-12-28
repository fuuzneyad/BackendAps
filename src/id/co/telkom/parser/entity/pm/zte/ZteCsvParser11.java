package id.co.telkom.parser.entity.pm.zte;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;
import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.propreader.ParserPropReader;
import id.co.telkom.parser.entity.pm.zte.model.ZteMeasurementModel;

public class ZteCsvParser11 extends AbstractParser{
	private static final Logger logger = Logger.getLogger(ZteCsvParser11.class);
	private ParserPropReader cynapseProp;
	private Map<String, ZteMeasurementModel> modelMap;
	
	@SuppressWarnings("unchecked")
	public ZteCsvParser11(ParserPropReader cynapseProp,
			AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
		this.modelMap = (Map<String, ZteMeasurementModel>)cynapseInit.getMappingModel();
		this.cynapseProp=cynapseProp;
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Map<String,String> header = new LinkedHashMap <String,String>();
		String fileloc=file.getName();
		      
		ZteMeasurementModel delimModel=null;
		for (Map.Entry<String, ZteMeasurementModel> entry : modelMap.entrySet())
		{
		 	  delimModel = null ;
		  	  if((ZteMeasurementModel)entry.getValue()!=null && fileloc.matches(((ZteMeasurementModel)entry.getValue()).getMeasurementType())){
		  		  delimModel=(ZteMeasurementModel)entry.getValue();
		   		  break;
		   	  }
	    }
		
		      if (delimModel!=null){
		    	  final String delimiter = delimModel.getDelimiter();
	    	  	  FileReader fr = new FileReader(file.getAbsolutePath());
	    	  	  CSVReader reader = new CSVReader(fr, delimiter.charAt(0));
	    	  	  
		    	  try{	
		    	  	  loader.onBeginFile();
		    	  	  int line =0;
				      
				      ctx.setTableName(delimModel.getTableName());
				      ctx.setGranularity(delimModel.getGranularityPeriod());
				      String [] splitted;
					  while ((splitted = reader.readNext()) != null) {
					    	  line++;
					    		  //header
//					    		  System.out.println(stringLine);
					    		  if(line==delimModel.getLineHeader()){
					    				  for (int col=0; col<splitted.length; col++){
					    					  header.put("H"+col, splitted[col]);
					    				  }
					    		  }
					    		  else
					    			  for (int cl=0; cl<splitted.length; cl++){
					    			  //here we go..
					    				  String field =header.get("H"+cl)!=null ? header.get("H"+cl).toUpperCase():null;
					    				  if (field!=null){
					    					  if(!cynapseProp.isGENERATE_SCHEMA_MODE()){ 
					    						if(cl <6){
					    							if(field.contains("BSC") || field.contains("RNC") || field.contains("ME")){
					    								ctx.setNe_id(splitted[cl]);
					    							}
						    							if(field.endsWith("ID")){
						    								String MOID=map.get("MO_ID")==null ? field.toString().trim()+"="+splitted[cl] : map.get("MO_ID") +"/"+field.toString().trim()+"="+splitted[cl];
						    								map.put("MO_ID", MOID);
						    								ctx.setMo_id(MOID);
						    							}
					    						}
					    						
//				    							if(field.length()> 1&& !field.equals("COLLECTTIME")&& !(field.charAt(1) >= '0' &&  field.charAt(1) <= '9')){
//				    								String MOID=map.get("MO_ID")==null ? field.toString().trim()+"="+splitted[cl] : map.get("MO_ID") +"/"+field.toString().trim()+"="+splitted[cl];
//				    								map.put("MO_ID", MOID);
//				    								ctx.setMo_id(MOID);
//			    								}
					    						
					    						
					    						if(field.equals("COLLECTTIME")){
					    							ctx.setDatetimeid(convertDate(splitted[cl],delimModel.getGranularityPeriod()));
					    						}
					    						map.put(field, splitted[cl]);
					    					  } else
					    						PutModel(delimModel.getTableName(),field, splitted[cl]);
					    				  }
					    					  
					    			  }
					    		  
					    		  if(!cynapseProp.isGENERATE_SCHEMA_MODE() && map!=null && !map.isEmpty()){
					    			  if(delimModel.getMoIdMapping()!=null && !delimModel.getMoIdMapping().trim().equals("")){
					    				  String MO="";
					    				  String[] arrS = delimModel.getMoIdMapping().split(",");
					    				  for (int i=0; i<arrS.length; i++){
					    					  String a=arrS[i].trim();
					    					  Object o =map.get(arrS[i].trim());
					    					  if(o!=null)
					    						  if(i!=0)
					    							  MO+="/"+a+"="+o.toString();
					    						  else
					    							  MO=a+"="+o.toString();
					    				  }
					    				  ctx.setMo_id(MO);
					    			  }
					    			  if(ctx.dateTimeid!=null)
					    				  loader.onReadyModel(map, ctx);
					    			  else
					    				  throw new IOException("Corrupt File!");
					    			  map = new LinkedHashMap<String, Object>();
					    		  }
						}
				      

		    	  } finally {
		    		  loader.onEndFile();
				      try{
				    	  reader.close();
				    	  fr.close();
				      } catch (Exception e){
				    	  logger.error(e.getMessage());
				    	  System.err.println(e.getMessage());
				      }
				}
		      }
		
		     
	}
	
	@Override
	public void LoadBuffer(LoaderHandlerManager loader,
			Context ctx) throws Exception {
	}

	 @Override
	 public void CreateSchemaFromMap(){
		 try{
				String location=cynapseProp.getFILE_SCHEMA_LOC()+"ZteSchema.sql";
				System.out.println("Generating Schema to "+location+"..");
				FileWriter out = new FileWriter(location);
				
				StringBuilder sb = new StringBuilder();
				
				for (Map.Entry<String, Map<String,String>> entry : tableModel.entrySet()) {
					
					sb.append("/*Schema for "+cynapseProp.getTABLE_PREFIX()+entry.getKey()+"*/\n");
					sb.append("CREATE TABLE "+cynapseProp.getTABLE_PREFIX()+entry.getKey()+" (\n");
					sb.append("\t`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n");
					sb.append("\t`HASH_VAL` varchar(100) DEFAULT NULL,\n");
					sb.append("\t`SOURCE_ID` varchar(100) DEFAULT NULL,\n");
					sb.append("\t`DATETIME_ID` datetime NULL DEFAULT NULL,\n");
					sb.append("\t`GRANULARITY` int(40) ,\n");
					sb.append("\t`NE_ID` varchar(200) DEFAULT NULL,\n");
					sb.append("\t`MO_ID` varchar(300) DEFAULT NULL,\n");
					
					for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
						
						if(entry2.getKey().length()>30)
							System.err.println("warning field "+entry2.getKey()+"'s  lenght >30, Mapping field is recommended!!");
						
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

	protected static String convertDate(String val, int timeDiff) {
		String format;
		if(val.length()=="yyyyMMddHHmmss".length())
			format="yyyyMMddHHmmss";else
		if(val.length()=="yyyyMMddHHmm".length())
			format="yyyyMMddHHmm";else
		format="yyyyMMddHHmmss";
		
		SimpleDateFormat fromUser = new SimpleDateFormat(format);
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try{
			Date d = fromUser.parse(val);
			Calendar gc = new GregorianCalendar();
				gc.setTime(d);
				gc.add(Calendar.MINUTE, (-1)*timeDiff);
			return myFormat.format(gc.getTime());
		}catch(ParseException e){return null;}
	}
}
