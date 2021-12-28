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

public class TelkomHaudCDRv3  extends AbstractParser {

	private static final Logger logger = Logger.getLogger(TelkomHaudCDRv3.class);
	private Map<String,String> header = new LinkedHashMap <String,String>();
	Map<String, Object> map = new LinkedHashMap<String, Object>();
	Map<String, Object> tmp = new LinkedHashMap<String, Object>();
	
	public TelkomHaudCDRv3(ParserPropReader cynapseProp, AbstractInitiator parserInit) {
		super(cynapseProp, parserInit);
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
		
		String filenameRewrite = "rep_"+file.getName();
		//write header
		FileWriter out= new FileWriter(cynapseProp.getLOCAL_DIR()+"/"+filenameRewrite,true);
		
		FileReader fr;
		fr = new FileReader(file.getAbsolutePath());
		
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
	    			if(header.get("H"+col).equals("supplier_message_id"))
	    				tmp.put("supplier_message_id", splitted[col]);
	    		}
	    	  }//end for cols
	    	  Object message_id=tmp.get("message_id");
	    	  if(message_id!=null &&  (tmp.get("client_connection_name").toString().toLowerCase().contains("mitracom"))) {//TODO: UNTUK mitracom
	    		  
	    		  	  map.put("SUBMIT_TIME",tmp.get("received_time"));
	    			  map.put("SENDER",tmp.get("base64_source_addr"));
	    			  map.put("BNUMBER",tmp.get("destination_addr"));
	    			  map.put("CLIENT_NAME",tmp.get("client_connection_name"));
	    			  map.put("DESTINATION", tmp.get("supplier_connection_name"));
	    			  map.put("MSG", tmp.get("base64_message_data"));
	    			  map.put("CHANNEL", "SMS");
	    			  map.put("MSG_ID", message_id);
	    			  map.put("SUP_MSG_ID", tmp.get("supplier_message_id"));
	    			  
	    			  loader.onReadyModel(map, ctx);
	    			  
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
	
}
