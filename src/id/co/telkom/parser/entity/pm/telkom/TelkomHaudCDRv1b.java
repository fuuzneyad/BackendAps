package id.co.telkom.parser.entity.pm.telkom;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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

public class TelkomHaudCDRv1b  extends AbstractParser {

	private static final Logger logger = Logger.getLogger(TelkomHaudCDRv1b.class);
	private Map<String,String> header = new LinkedHashMap <String,String>();
	Map<String, Object> map = new LinkedHashMap<String, Object>();
	Map<String, Object> tmp = new LinkedHashMap<String, Object>();
	private DRModelMap modelMap ;
	
	public TelkomHaudCDRv1b(ParserPropReader cynapseProp, AbstractInitiator parserInit) {
		super(cynapseProp, parserInit);
		this.modelMap = (DRModelMap)parserInit.getMappingModel();
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader, Context ctx) throws Exception {
		ctx.setTableName("CDR");
		
		loader.onBeginFile();
		logger.info("processing CDR "+file.getName());	
		String[] splt = file.getName().split("-");
		String datetime = splt[2]+splt[3];
		ctx.setDatetimeid(convertDate(datetime,0));
		ctx.setGranularity(0);
		
		boolean isHeaderWrited=false;
		String filenameRewrite = "rep_"+file.getName();
		//write header
		FileWriter out= new FileWriter(cynapseProp.getLOCAL_DIR()+"/"+filenameRewrite,true);
		
		FileReader fr;
		fr = new FileReader(file.getAbsolutePath());
//		CSVReader reader = new CSVReader(fr, ',' ,'"','\n');
		CSVReader reader;
		
		if(file.getName().startsWith("rep"))
			reader = new CSVReader(fr, ',');
		else
			reader = new CSVReader(fr, ',' ,'"','\n');
		
		int line =0;
		String [] splitted;
		while ((splitted = reader.readNext()) != null) {
			line++;
	    	  for (int col=0; col<splitted.length; col++){
	    		if(line==1)//header
	    		  header.put("H"+col, splitted[col]);
	    		else {
	    			if(header.get("H"+col).equals("message_id"))
	    				tmp.put("message_id", splitted[col]);else
	    			if(header.get("H"+col).equals("received_time"))
	    				tmp.put("received_time", splitted[col]);else
	    			if(header.get("H"+col).equals("base64_source_addr"))
	    				tmp.put("base64_source_addr", splitted[col]);else
	    			if(header.get("H"+col).equals("destination_addr"))
	    				tmp.put("destination_addr", splitted[col]);else
	    			if(header.get("H"+col).equals("supplier_connection_name"))
	    				tmp.put("supplier_connection_name", splitted[col]);else
	    			if(header.get("H"+col).equals("client_connection_name"))
	    				tmp.put("client_connection_name", splitted[col]);else
	    			if(header.get("H"+col).equals("base64_message_data"))
	    				tmp.put("base64_message_data", splitted[col]);
	    		}
	    	  }//end for cols
	    	  Map<String, DRModel> modelMp = modelMap.getModelMap();
	    	  Object message_id=tmp.get("message_id");
	    	  if(message_id!=null) {
	    		  
	    		  DRModel drModel = modelMp.get(message_id.toString());
	    		  if(drModel!=null) {//found it
	    			  map.put("MSG_STATUS",drModel.getStatus());
	    			  map.put("LAST_ERROR_CODE",drModel.getError_code());
	    			  map.put("SENDER",tmp.get("base64_source_addr"));
	    			  map.put("BNUMBER",tmp.get("destination_addr"));
	    			  map.put("SUBMIT_TIME",tmp.get("received_time"));
	    			  map.put("DELIVER_TIME",drModel.getProcessed_time());
	    			  map.put("CLIENT_NAME",tmp.get("client_connection_name"));
	    			  map.put("DESTINATION", tmp.get("supplier_connection_name"));
	    			  map.put("MSG_LENGTH", tmp.get("base64_message_data").toString().length());
	    			  map.put("CHANNEL", "SMS");
	    			  map.put("MSG_ID", message_id);
	    			  
	    			  //marked it true on DR
	    			  drModel.setFoundOnCDR(true);
	    			  loader.onReadyModel(map, ctx);
	    			  
	    		  } else {
	    			  //rewrite expired cdr
	    			  //TODO: handle content, parrent message id..
	    			  if(!isSelisihKurangDrSehari(tmp.get("received_time").toString())) {
	    				  //Tulis output
	    				  logger.info("Expired Transaction "+message_id.toString());
	    				  
	    				  map.put("MSG_STATUS","EXPIRED");
	    				  map.put("LAST_ERROR_CODE","000");
		    			  map.put("SENDER",tmp.get("base64_source_addr"));
		    			  map.put("BNUMBER",tmp.get("destination_addr"));
		    			  map.put("SUBMIT_TIME",tmp.get("received_time"));
		    			  map.put("DELIVER_TIME","0000-00-00 00:00:00.000+00");
		    			  map.put("CLIENT_NAME",tmp.get("client_connection_name"));
		    			  map.put("DESTINATION", tmp.get("supplier_connection_name"));
		    			  map.put("MSG_LENGTH", tmp.get("base64_message_data").toString().length());
		    			  map.put("CHANNEL", "SMS");
		    			  map.put("MSG_ID", message_id);
		    			  
		    			  loader.onReadyModel(map, ctx);
		    			  
	    			  } else {
	    				  
	    				  logger.info("Not Found DR still not 24 hour, writing reprosess file "+message_id.toString());
	    				  //write header
	    				  if(!isHeaderWrited) {
	    					  out.write("message_id,received_time,base64_source_addr,destination_addr,supplier_connection_name,client_connection_name,base64_message_data\n");
	    					  isHeaderWrited=true;
	    				  }else{//write content
	    					  
	    					  String content = "\""+message_id.toString()+"\","
	    							  + "\""+tmp.get("received_time")+"\","
	    					  		  + "\""+tmp.get("base64_source_addr")+"\","
	    					  		  + "\""+tmp.get("destination_addr")+"\","
	    					  		  + "\""+tmp.get("supplier_connection_name")+"\","
	    					  		  + "\""+tmp.get("client_connection_name")+"\","
	    					  		  + "\""+tmp.get("base64_message_data").toString().replace("\"","\\\"")+"\"\n";
//	    					  if(file.getName().startsWith("rep"))
//	    						  content+= "\""+tmp.get("base64_message_data").toString()+"\"\n";
//	    					  else
//	    						  content+= "\""+tmp.get("base64_message_data").toString().replace("\"","\\\"")+"\"\n";
	    					  
	    					  out.write(content);
	    					 
	    				  }
	    				  
	    			  }
	    			  
	    		  }
	    		  map = new LinkedHashMap<String, Object>();
    			  tmp = new LinkedHashMap<String, Object>();
	    	  }
		}
		reader.close();
		out.close();
		fr.close();
		loader.onEndFile();
		
	}
	
	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx) throws Exception {
		boolean lakukan = true;
		//TODO: check this DR FILE (x)
		if(lakukan) {
			logger.info("write not found DR..");
			Map<String, DRModel> modelMp = modelMap.getModelMap();
			Map<String, FileWriter> fileWriterMap = new LinkedHashMap<String, FileWriter>();
			for(Map.Entry<String, DRModel> entry : modelMp.entrySet()) {
				DRModel dr = entry.getValue();
				if(!dr.isFoundOnCDR) {
					logger.info("write unfound DR "+entry.getKey());
					String filename = "rep_"+dr.getFilename();
					if(fileWriterMap.get(filename)==null) {//initiate
						FileWriter out = new FileWriter(cynapseProp.getMAPPING_CONFIG()+"/"+filename,true);
						//write header here
						out.write("message_id,supplier_message_id,received_time,processed_time,status,error_code,message_found,is_smsc_dlr\n");
						//write first row 
						out.write(dr.getMessage_id()+","
								+dr.getSupplier_message_id()+","
								+"\""+dr.getReceived_time()+"\","
								+"\""+dr.getProcessed_time()+"\","
								+"\""+dr.getStatus()+"\","
								+"\""+dr.getError_code()+"\","
								+"\""+dr.getMessage_found()+"\","
								+"\""+dr.getIs_smsc_dlr()+"\"\n"
								);
						fileWriterMap.put(filename, out);
					}else {//already initiated
						FileWriter out  = fileWriterMap.get(filename);
						out.write(dr.getMessage_id()+","
								+dr.getSupplier_message_id()+","
								+"\""+dr.getReceived_time()+"\","
								+"\""+dr.getProcessed_time()+"\","
								+"\""+dr.getStatus()+"\","
								+"\""+dr.getError_code()+"\","
								+"\""+dr.getMessage_found()+"\","
								+"\""+dr.getIs_smsc_dlr()+"\"\n"
								);
					}
					
				}
			}
			// close the out
			for(Map.Entry<String, FileWriter> entry:fileWriterMap.entrySet()) {
				entry.getValue().close();
			}
		}
	}

	@Override
	protected void CreateSchemaFromMap() {
	}
	
	protected static String convertDate(String val, int timeDiff) {
		String format;
		if(val.length()=="yyyyMMddHHmmss".length())
			format="yyyyMMddHHmmss";else
		if(val.length()=="yyyyMMddHHmm".length())
			format="yyyyMMddHHmm";
		if(val.length()=="yyMMddHHmm00".length())
			format="yyMMddHHmm";else
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
	
	private static boolean isSelisihKurangDrSehari(String val) {// TODO: check utc+7 
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S+00");
		 try {
			Date d = myFormat.parse(val);
			//get last day of date
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			//TODO check this: tambah 7 jam
			c.add(Calendar.HOUR_OF_DAY, 7);
//			System.out.println(c);
			int tanggal = c.get(Calendar.DATE);
			c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
			int tanggalMaksimum = c.get(Calendar.DATE);
			
			long sehari = 24*60*60*1000;
//			long jam6pagi = 5*60*60*1000;
			long sekarang = System.currentTimeMillis();
			
			
			Date date = new Date(); 
			Calendar c2 = Calendar.getInstance();
			c2.setTime(date);
			int tanggalhariini =c2.get(Calendar.DATE);
			int jamhariini =c2.get(Calendar.HOUR_OF_DAY);
//			System.out.println(tanggalhariini +" "+jamhariini);
			
			//TODO: check this, Request om Welly khusus akhir bulan tunggu DR sampai jam 6 saja
//			if(tanggal==tanggalMaksimum && sekarang-d.getTime()<jam6pagi)//or && jam sekarang
			if(tanggal==tanggalMaksimum && (tanggalhariini==1 && jamhariini<5))
				return true; 
			else
			if(tanggal!=tanggalMaksimum && sekarang-d.getTime()<sehari)
				return true;
			return false;
		} catch (ParseException e) {
			return false;
		}
	}
	
	
//	public static void main(String[] args) {
//		System.out.println(isSelisihKurangDrSehari("2022-03-26 11:00:00.065+00"));
//	}
	
}
