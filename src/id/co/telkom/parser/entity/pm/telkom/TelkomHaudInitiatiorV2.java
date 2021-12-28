package id.co.telkom.parser.entity.pm.telkom;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;
import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.loader.DBFileListWriter;
import id.co.telkom.parser.common.propreader.OutputMethodPropReader;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class TelkomHaudInitiatiorV2  extends AbstractInitiator {
	private ParserPropReader parserProp;
	private static final Logger logger = Logger.getLogger(TelkomHaudInitiatiorV2.class);
	Map<String,String> header = new LinkedHashMap <String,String>();
	
	public TelkomHaudInitiatiorV2(ParserPropReader cynapseProp, OutputMethodPropReader om) {
		super(cynapseProp, om);
		this.parserProp=cynapseProp;
	}

	@Override
	public Object ReadMappingModel() {
		
		DRModelMap modelMap = new DRModelMap();
		DBFileListWriter dbWriter =getDBFileListWriter();
		//Baca DR disini
		File files = new File(parserProp.getMAPPING_CONFIG());
		if(files.exists() && files.isDirectory()) {
			for(File file: files.listFiles()) {
				if(file.getName().contains("dlr")){
							FileReader fr;
							try {
								logger.info("Reading DLR Files "+file.getName());
									fr = new FileReader(file.getAbsolutePath());
									CSVReader reader = new CSVReader(fr,',');
									int line =0;
									String [] splitted;
									while ((splitted = reader.readNext()) != null) {
										line++;
										DRModel model = new DRModel();
								        for (int col=0; col<splitted.length; col++){
								    		if(line==1){//header
								    		  header.put("H"+col, splitted[col]);
								    		} else {//isi
									 			 if(header.get("H"+col).equals("message_id"))
									 				 model.setMessage_id(splitted[col]);else
						     				     if(header.get("H"+col).equals("supplier_message_id"))
						   	     					 model.setSupplier_message_id(splitted[col]);else		
						   	     				 if(header.get("H"+col).equals("received_time"))
						  	   	     				 model.setReceived_time(splitted[col]);else
						  	   	     			 if(header.get("H"+col).equals("processed_time"))
							  	   	     			 model.setProcessed_time(splitted[col]);else	 
							  	   	     		 if(header.get("H"+col).equals("status"))
								  	   	     		 model.setStatus(splitted[col]);else	
								  	   	     	 if(header.get("H"+col).equals("error_code"))
									  	   	     	 model.setError_code(splitted[col]);
									 			 
									 			 //TODO: check this DR FILE (x)
									 			 model.setFilename(file.getName());
						    				}
						        		 }
								    	  if(model.getMessage_id()!=null) {//found it
								    		  modelMap.putModelMap(model.getMessage_id(),model);
								    	  }
								    	  
								    }
									reader.close();
									fr.close();
									
									
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								}  catch (IOException e) {
									e.printStackTrace();
								} 
							Date date = new Date();
							Timestamp ts = new Timestamp(date.getTime());
							
						dbWriter.writeFileLog(file.getName(), parserProp.getSOURCE_ID(), ts, "-", "SUCCESS");
							
						//backup file DLR
						if(parserProp.getBACKUP_MECHANISM().equals(parserProp.BKP_RM_FILE)){
							if(!file.delete()){
								logger.error("Error deleting "+file.getName());
								System.err.println("Error deleting "+file.getName());
							}
						}else
							if(parserProp.getBACKUP_MECHANISM().equals(parserProp.BKP_MV_FILE)){
								if(!file.renameTo(new File(parserProp.getBACKUP_DIR()+"/"+file.getName()))){
									System.err.println("Error moving "+file.getName());
									logger.error("Error moving "+file.getName());
								}
						}
						
				}else//end of contain dlrs
					logger.info("seems not DLR File "+file.getName());
			  }//end for
			
		}else {
			logger.info("DR file location "+ files.getName()+" not exists");
			System.exit(0);
		}
		
		return modelMap;
	}

//	private static String convertDate(String val, int timeDiff) {
//		String format;
//		if(val.length()=="yyyyMMddHHmmss".length())
//			format="yyyyMMddHHmmss";else
//		if(val.length()=="yyyyMMddHHmm".length())
//			format="yyyyMMddHHmm";
//		if(val.length()=="yyMMddHHmm00".length())
//			format="yyMMddHHmm";else
//		format="yyyyMMddHHmmss";
//		
//		SimpleDateFormat fromUser = new SimpleDateFormat(format);
//		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		try{
//			Date d = fromUser.parse(val);
//			Calendar gc = new GregorianCalendar();
//				gc.setTime(d);
//				gc.add(Calendar.MINUTE, (-1)*timeDiff);
//			return myFormat.format(gc.getTime());
//		}catch(ParseException e){return null;}
//	}
//	
//	private static boolean isSelisihKurangDrSehari(String val) {
//		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		 try {
//			Date d = myFormat.parse(val);
//			long sehari = 24*60*60*1000;
//			long sekarang = System.currentTimeMillis();
//			if(sekarang-d.getTime()<sehari)
//				return true;
//			return false;
//		} catch (ParseException e) {
//			return false;
//		}
//	}
}
