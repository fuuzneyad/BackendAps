package id.co.telkom.parser.entity.dashboard.uangel;

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
import id.co.telkom.parser.common.model.StandardMeasurementModel;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class UangelScpCdrParser10 extends AbstractParser{
	private static final Logger logger = Logger.getLogger(UangelScpCdrParser10.class);
	private ParserPropReader cynapseProp;
	private Map<String, StandardMeasurementModel> modelMap;
	
	@SuppressWarnings("unchecked")
	public UangelScpCdrParser10(ParserPropReader cynapseProp,
			AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
		this.modelMap = (Map<String, StandardMeasurementModel>)cynapseInit.getMappingModel();
		this.cynapseProp=cynapseProp;
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		String fileloc=file.getName();
		Map<String, Object> map = new LinkedHashMap<String, Object>();      
		StandardMeasurementModel delimModel=null;
		for (Map.Entry<String, StandardMeasurementModel> entry : modelMap.entrySet())
		{
		 	  delimModel = null ;
		  	  if((StandardMeasurementModel)entry.getValue()!=null && fileloc.matches(((StandardMeasurementModel)entry.getValue()).getMeasurementType())){
		  		  delimModel=(StandardMeasurementModel)entry.getValue();
		   		  break;
		   	  }
	    }
		
		if (delimModel!=null){
			String[] dts =file.getName().split("_");
			logger.info("Processsing "+file.getName());
			  if(dts.length>=4)
				 ctx.setDatetimeid(convertDate(dts[4],0));
	    	  FileReader fr = new FileReader(file);
	    	  ctx.setTableName(delimModel.getTableName());
	    	  CSVReader reader = new CSVReader(fr, '|');
	    	  loader.onBeginFile();
    	  	  String [] splitted;
			  while ((splitted = reader.readNext()) != null) {
			      for(int i=0;i<splitted.length;i++){
			    	if(i==0){
			    		ctx.setNe_id(splitted[i]);
			    		ctx.setMo_id(splitted[i]);
			    	}else{
			    		map.put("C"+i, splitted[i]);
			    		PutModel(delimModel.getTableName(), "C"+i, splitted[i]);
			    	}	  
			      }
			      if(!cynapseProp.isGENERATE_SCHEMA_MODE()){
			    	  loader.onReadyModel(map, ctx);
			    	  map = new LinkedHashMap<String, Object>(); 
			      }
			  }
			  reader.close();
    	  	  loader.onEndFile();
	    	  fr.close();
		}else
			logger.error(file.getName()+" Not Matched!!");
		
		     
	}
	
	@Override
	public void LoadBuffer(LoaderHandlerManager loader,
			Context ctx) throws Exception {
	}

	 @Override
	 public void CreateSchemaFromMap(){
		 try{
				String location=cynapseProp.getFILE_SCHEMA_LOC()+"UangelCdrSchema.sql";
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
						
						String typeData = isDouble(entry2.getValue()) ? "DOUBLE,\n" : "VARCHAR("+(entry2.getValue().length()+20)+"),\n"; 
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
