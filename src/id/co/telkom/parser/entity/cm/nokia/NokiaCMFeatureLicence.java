package id.co.telkom.parser.entity.cm.nokia;

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
import id.co.telkom.parser.common.model.StandardMeasurementModel;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class NokiaCMFeatureLicence extends AbstractParser{
//	private static final Logger logger = Logger.getLogger(NokiaCMFeatureLicence.class);
	private ParserPropReader cynapseProp;
	@SuppressWarnings("unused")
	private Map<String, StandardMeasurementModel> modelMap;
	
	@SuppressWarnings("unchecked")
	public NokiaCMFeatureLicence(ParserPropReader cynapseProp,
			AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
		this.modelMap = (Map<String, StandardMeasurementModel>)cynapseInit.getMappingModel();
		this.cynapseProp=cynapseProp;
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		String[] s=file.getName().split("_");
		String ne="";
		for(int i=0;i<s.length;i++){
			if(i!=0 && i!=s.length-1 && !s[i].equals("") && !s[i].equals("ON") && !s[i].equals("OFF") && !s[i].equals("FULL"))
				ne+=s[i]+"_";
			
			if(i==s.length-1 && s[i].contains("."))
				ctx.setDatetimeid((convertDate(s[i].split("\\.")[0])));
		}
		
		ne=ne.length()>0?ne.substring(0, ne.length()-1):ne;
		ctx.setNe_id(ne);
		ctx.setMo_id(ne);
		
		FileInputStream fstream;
		fstream = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));	
	      String stringLine;
	      if(file.getName().startsWith("ZW7I_FULL"))
	    	  ctx.setTableName("ZW7I_FULL");else
	      if(file.getName().startsWith("ZW7I_OFF"))
	    	  ctx.setTableName("ZW7I_OFF");else
	      if(file.getName().startsWith("ZW7I_ON"))
	    	  ctx.setTableName("ZW7I_ON");else
	      if(file.getName().startsWith("ZWOS"))
	    	  ctx.setTableName("ZWOS");
	      
	      Map<String, Object> map = new LinkedHashMap<String, Object>();
	      if(ctx.t_name!=null)
	      while ((stringLine = br.readLine()) != null)
	      {
	       if (ctx.t_name.startsWith("ZW7I")){
//	    	  if(ctx.ne_id==null &&(stringLine.trim().toUpperCase().startsWith("BSC")||
//		    		  stringLine.trim().toUpperCase().startsWith("FLEXIBSC")||
//		    		  stringLine.trim().toUpperCase().startsWith("RNC")) 
//		    		  && stringLine.contains(" ")
//		    		  ){
//		    		  //ne
//		    		  ne=stringLine.split("\\s+")[1].trim();
//		    		  ctx.setNe_id(ne);
//		    		  ctx.setMo_id(ne);
//		    	  }
//	    	  else 
	    		  if( stringLine.contains(":.")){
	    		  int pos =stringLine.indexOf(":.");
	    		  String param=stringLine.substring(0,pos).trim().replace(" ", "_");
	    		  String val=stringLine.substring(pos).replace(".", "").replace(":", "").trim();
	    		  if(cynapseProp.isGENERATE_SCHEMA_MODE()){
	    			  PutModel(ctx.t_name, param, val);
	    		  }else
	    			  map.put(param, val);
	    	  }else if( stringLine.startsWith("------------")){
	    		  //ready model
	    		  if(!cynapseProp.isGENERATE_SCHEMA_MODE() && !map.isEmpty()){
		    		  loader.onReadyModel(map, ctx);
		    		  map = new LinkedHashMap<String, Object>();
	    		  }
	    	  }
	       } 
	       else if (ctx.t_name.startsWith("ZWOS") && stringLine.contains(" ")){
	    			  String[] spt=stringLine.split("\\s+");
	    			  if(spt.length>0&&isDouble(spt[0])){
	    				  if(!cynapseProp.isGENERATE_SCHEMA_MODE()){
		    				  map.put("IDENTIFIER", spt[0]);
		    				  map.put("NAME_OF_PARAMETER", spt[1]);
		    				  map.put("ACTIVATION_STATUS", spt[2]);
		    				  loader.onReadyModel(map, ctx);
		    				  map = new LinkedHashMap<String, Object>();
	    				  }else{
	    					  PutModel(ctx.t_name, "IDENTIFIER", "1");
	    					  PutModel(ctx.t_name, "NAME_OF_PARAMETER", "NAME_OF_PARAMETERKJHSDSASDASDJAKS");
	    					  PutModel(ctx.t_name, "NAME_OF_PARAMETER", "NAME_OF_PARAMETERKIJAHSDKJS");
	    				  }
	    			  }
	    		  }
	      }
	    //last
	      if(!cynapseProp.isGENERATE_SCHEMA_MODE() && !map.isEmpty()){
    		  loader.onReadyModel(map, ctx);
    		  map = new LinkedHashMap<String, Object>();
		  }
	      loader.onEndFile();
	      br.close();
	      fstream.close();
	      loader.onEndFile();
//		if(file.getName().startsWith("ZW7I"))
//			new NokiaCMFeatureLicenceZW7I(cynapseProp, parserInit).ProcessFile(file, loader, ctx);
	}
	
	@Override
	public void LoadBuffer(LoaderHandlerManager loader,
			Context ctx) throws Exception {
	}

	 @Override
	 public void CreateSchemaFromMap()
	  {
	    try {
	      String location = this.cynapseProp.getFILE_SCHEMA_LOC() + "NokiaLicenceSchema.sql";
	      System.out.println("Generating Schema to " + location + "..");
	      FileWriter out = new FileWriter(location);

	      StringBuilder sb = new StringBuilder();

	      for (Map.Entry<String, Map<String,String>> entry : this.tableModel.entrySet())
	      {
	        sb.append("DROP TABLE IF EXISTS " + this.cynapseProp.getTABLE_PREFIX() + (String)entry.getKey() + ";\n");
	        sb.append("CREATE TABLE " + this.cynapseProp.getTABLE_PREFIX() + (String)entry.getKey() + " (\n");
	        sb.append("\t`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n");
	        sb.append("\t`SOURCE_ID` varchar(100) DEFAULT '',\n");
	        sb.append("\t`DATETIME_ID` datetime NULL DEFAULT '0000-00-00 00:00:00',\n");
	        sb.append("\t`GRANULARITY` int(40) ,\n");
	        sb.append("\t`NE_ID` varchar(200) DEFAULT '',\n");
	        sb.append("\t`MO_ID` varchar(300) DEFAULT '',\n");

	        for (Map.Entry<String, String> entry2 : entry.getValue().entrySet())
	        {
	          if (((String)entry2.getKey()).length() > 30) {
	            System.err.println("warning field " + (String)entry2.getKey() + "'s  lenght >30, Mapping field is recommended!!");
	          }
	          String typeData = isDouble((String)entry2.getValue())? "DOUBLE DEFAULT NULL,\n" : "VARCHAR(" + ((String)entry2.getValue()).length() + 20 + "),\n";

	          typeData = "PERIOD_START_TIME|PERIOD_STOP_TIME|PERIOD_REAL_START_TIME|PERIOD_REAL_STOP_TIME".contains(entry2.getKey()) ? "DATETIME NULL DEFAULT NULL,\n" : typeData;
	          sb.append("\t`" + entry2.getKey() + "` " + typeData);
	        }
	        sb.setLength(sb.length() - 2);
	        sb.append("\n)Engine=MyIsam;\n");
	        out.write(sb.toString());
	        out.flush();
	        sb = new StringBuilder();
	      }

	      out.close();
	    } catch (IOException e) {
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
			format="yyyyMMddHHmm";
		else if(val.length()=="ddMMyy".length())
			format="ddMMyy";else
		format="yyyyMMddHHmmss";
		
		SimpleDateFormat fromUser = new SimpleDateFormat(format);
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		try{
			return myFormat.format(fromUser.parse(val));
		}catch(ParseException e){return val;}
	}
	
	
}
