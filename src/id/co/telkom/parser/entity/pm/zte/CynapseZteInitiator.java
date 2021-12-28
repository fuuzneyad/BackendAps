package id.co.telkom.parser.entity.pm.zte;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.propreader.OutputMethodPropReader;
import id.co.telkom.parser.common.propreader.ParserPropReader;
import id.co.telkom.parser.entity.pm.zte.model.ZteMeasurementModel;

public  class CynapseZteInitiator extends AbstractInitiator {
	private ParserPropReader cynapseProp;
	
	public CynapseZteInitiator(ParserPropReader cynapseProp, OutputMethodPropReader om) {
		super(cynapseProp, om);
		this.cynapseProp=cynapseProp;
		this.mappingModel=ReadMappingModel();
	}
	
	@Override
	public Object ReadMappingModel(){
		System.out.println("Reading Mapping Config..");
		Map<String, ZteMeasurementModel> modelMap = new LinkedHashMap<String, ZteMeasurementModel>();
		try {
			  FileInputStream fstream = new FileInputStream(cynapseProp.getMAPPING_CONFIG());
		      BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));	
		      String stringLine;
		      ZteMeasurementModel mdl;
		      //read mapping config
		      while (( stringLine = br.readLine()) != null)
		      {
		    	  if(!stringLine.startsWith("#") && !stringLine.trim().equals("") && stringLine.contains("[")){
		    		  String[] arr = stringLine.split("\\[*\\]");
		    		  mdl = new ZteMeasurementModel();
		    		  String type=null;
		    		  for (int x=0; x<arr.length; x++){
		    			  String s=arr[x].trim().replace("[", "").trim();
		    			  if(x==0){
		    				  type=s;
		    				  mdl.setMeasurementType(s);
		    			  }else
		    			  if(x==1){
		    				  mdl.setTableName(s);
		    			  }else
		    			  if(x==2){
			    			 mdl.setDelimiter(s);
			    		 }else
		    			  if(x==3){
		    				  if(s.contains("|")){
		    					  String S[] = s.split("\\|");
		    					  for (int c=0; c<S.length;c++){
		    						  if(c==0)
		    							  mdl.setUseDelimiter (S[c].equalsIgnoreCase("Y"));else
		    						  if(c==1)
		    							  mdl.setLineHeader( getIntLineCol(S[c]));else
		    						  if(c==2)
		    							  mdl.setColumnHeader(getIntLineCol(S[c]));
		    					  }
		    				  }
			    		  }else
		    			  if(x==4){
		    				  Map<String, String> fieldMap= GetMappingCounter(s);
		    				  mdl.setFieldMap(fieldMap);
		    			  }else
			    		  if(x==5){
			    			  mdl.setMoIdMapping(s);
			    		  }else
			    		  if(x==6){
				    		  mdl.setGranularityPeriod(getGranularity(s));
				    	  }	  
		    		  }
		    		  
		    		  if(type!=null){
		    			  if(mdl.getGranularityPeriod()==0)
		    				  mdl.setGranularityPeriod(60);
		    			  modelMap.put(type, mdl);
		    		  }
		    		  
		    	  }
		      }
		      br.close();
		      return modelMap;
			} catch (IOException  e){
				System.err.println("Cannot Find Mapping model "+e.getMessage());
				return null;
			}
	}
	
	private Integer getGranularity(String s){
		try{
			return Integer.parseInt(s);
		}catch (NumberFormatException e){
			return 0;
		}
	}
	
	private Map<String, String> GetMappingCounter(String fileloc){
		try {
			  Map<String, String> map = new LinkedHashMap<String, String> ();
			  FileInputStream fstream = new FileInputStream(fileloc);
		      BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));	
		      String stringLine;
		      String key=null, val=null;
		      while (( stringLine = br.readLine()) != null)
		      {
		    	  if(!stringLine.startsWith("#") && !stringLine.trim().equals("") && stringLine.contains("[")){
		    		  String[] arr = stringLine.split("\\[*\\]");
		    		  for (int x=0; x<arr.length; x++){
		    			  String s=arr[x].trim().replace("[", "").trim();
		    			  if(x==0){
		    				  key=s;
		    			  }else
		    			  if(x==1){
		    				  val=s;
		    			  }
		    		  }
		    		  if (key!=null && val!=null && !key.equals("") && !val.equals("") ){
		    			  map.put(key, val);
		    			  key=null; val=null;
		    		  }
		    		  
		    	  }
		      }
		      fstream.close();
		      return map;
		}catch (IOException  e){
			return null;
		} 
	}
	
	private int getIntLineCol(String s){
		try{
			return Integer.parseInt(s);
		}catch (Exception e){
			return 1;
		}
	}

}
