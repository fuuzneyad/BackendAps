package id.co.telkom.parser.entity.cm.nokia;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.StandardMeasurementModel;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class NokiaCMFeatureLicenceZW7I extends NokiaCMFeatureLicence {
	private ParserPropReader cynapseProp;
	@SuppressWarnings("unused")
	private Map<String, StandardMeasurementModel> modelMap;
	
	@SuppressWarnings("unchecked")
	public NokiaCMFeatureLicenceZW7I(ParserPropReader cynapseProp,
			AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
		this.modelMap = (Map<String, StandardMeasurementModel>)cynapseInit.getMappingModel();
		this.cynapseProp=cynapseProp;
	}

	@SuppressWarnings("resource")
	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		loader.onBeginFile();
		FileInputStream fstream;
		fstream = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));	
	      String stringLine;
	      String tableName;
	      if(file.getName().startsWith("ZW7I_FULL"))
	    	  tableName="ZW71_FULL";else
	      if(file.getName().startsWith("ZW7I_OFF"))
	    	  tableName="ZW7I_OFF";else
	      if(file.getName().startsWith("ZW7I_ON"))
	    	  tableName="ZW71_ON";
	      else
	      throw new Exception(file.getName()+" is not valid file!");		  
	      
	      Map<String, Object> map = new LinkedHashMap<String, Object>();
	      ctx.setTableName(tableName);
	      while ((stringLine = br.readLine()) != null)
	      {
	    	  if((stringLine.trim().toUpperCase().startsWith("BSC")||
	    		  stringLine.trim().startsWith("FLEXIBSC")||
	    		  stringLine.trim().toUpperCase().startsWith("RNC")) 
//	    		  && stringLine.contains(" ")
	    		  ){
	    		  //ne
	    		  String ne=stringLine.split("\\s+")[1].trim();
//	    		  System.out.println(stringLine);
//	    		  System.out.println(ne);
	    		  ctx.setNe_id(ne);
	    		  ctx.setMo_id(ne);
	    	  }
	    	  else 
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
	      br.close();
	    //last
	      if(!cynapseProp.isGENERATE_SCHEMA_MODE() && !map.isEmpty()){
    		  loader.onReadyModel(map, ctx);
    		  map = new LinkedHashMap<String, Object>();
		  }
	      
	      loader.onEndFile();
	      br.close();
	      fstream.close();
		loader.onEndFile();
	}
}
