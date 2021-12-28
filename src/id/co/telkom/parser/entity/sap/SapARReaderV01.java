package id.co.telkom.parser.entity.sap;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class SapARReaderV01 extends AbstractParser{
	public SapARReaderV01(ParserPropReader cynapseProp,
			AbstractInitiator parserInit) {
		super(cynapseProp, parserInit);
		
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		loader.onBeginFile();
		Map<String, Object> header = new LinkedHashMap<String, Object>();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		  FileInputStream fstream = new FileInputStream(file);
	      BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));	
	      String stringLine;
	      while ((stringLine = br.readLine()) != null)
	      {
	    	  stringLine=stringLine.trim();
	    	  if(stringLine.startsWith("Customer")){
	    		  header = new LinkedHashMap<String, Object>();
	    		  ctx.setTableName("AR_header".toUpperCase());
	    		  header.put("CUSTOMER", stringLine.replace("Customer", "").trim());
	    	  }
	    	  if(stringLine.startsWith("Company Code"))
	    		  header.put("COMPANY_CODE", stringLine.replace("Company Code", "").trim());
	    	  if(stringLine.startsWith("Name"))
	    		  header.put("NAME", stringLine.replace("Name", "").trim());
	    	  if(stringLine.startsWith("City")){
	    		  header.put("CITY", stringLine.replace("City", "").trim());
	    		  loader.onReadyModel(header, ctx);
	    	  }
	    	  if(stringLine.contains("Assignment")){
	    		  br.readLine();//skip line
	    		  while ((stringLine = br.readLine()) != null && !stringLine.startsWith("---")){
	    			  ctx.setTableName("AR_Detail".toUpperCase());
	    			  
		    		  map.put("CUSTOMER", header.get("CUSTOMER"));
	    			  String[] arr = stringLine.split("\\|");
	    			  for (int i=0; i<arr.length; i++){
	    				  String s = arr[i].trim();
	    				  if(i==1)
	    					  map.put("Assignment".toUpperCase(), s);
	    				  if(i==2)
	    					  map.put("Reference".toUpperCase(), s);
	    				  if(i==3)
	    					  map.put("PERIOD".toUpperCase(), s);
	    				  if(i==4)
	    					  map.put("DOCUMENT_NO".toUpperCase(), s);
	    				  if(i==5)
	    					  map.put("TYP".toUpperCase(), s);
	    				  if(i==6)
	    					  map.put("Pstng_Date".toUpperCase(), s);
	    				  if(i==7)
	    					  map.put("DOC_DATE".toUpperCase(), s);
	    				  if(i==8)
	    					  map.put("AMT_IN_LCURR".toUpperCase(), toLong(s.replace(".", "").replace(",", ".").trim()));
	    				  if(i==9)
	    					  map.put("LCURR".toUpperCase(), s);
	    				  if(i==10)
	    					  map.put("CURR".toUpperCase(), s);
	    				  if(i==11)
	    					  map.put("AMOUNT_IN_DC".toUpperCase(), toLong(s.replace(".", "").replace(",", ".").trim()));
	    				  if(i==12){
	    					  map.put("TEXT".toUpperCase(), s);
	    					  map.put("UPDATE_DATE".toUpperCase(), TanggalSekarang());
	    					  loader.onReadyModel(map, ctx);
	    					  map = new LinkedHashMap<String, Object>();
	    				  }
	    				  
	    			  }
//	    			  System.exit(0);
	    		  }
	    	  }
	    	  
	    		  
	      }
	      br.close();
	      fstream.close();
		loader.onEndFile();
	}

	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx)
			throws Exception {
		
	}

	@Override
	protected void CreateSchemaFromMap() {
		
	}
	private static String TanggalSekarang() {
    	Calendar cal = Calendar.getInstance();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    	return sdf.format(cal.getTime());
    }
	
	private float toLong(String s){
		if(s.endsWith("-"))
			s="-"+s.replace("-", "");
		try{
			return Float.parseFloat(s);
		}catch (NumberFormatException e){
			return 0;
		}
	}
}
