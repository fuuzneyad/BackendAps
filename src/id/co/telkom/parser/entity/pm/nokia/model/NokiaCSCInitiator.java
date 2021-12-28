package id.co.telkom.parser.entity.pm.nokia.model;

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

public class NokiaCSCInitiator extends AbstractInitiator {
	private ParserPropReader cynapseProp;
	
	
	public NokiaCSCInitiator(ParserPropReader cynapseProp,
			OutputMethodPropReader om) {
		super(cynapseProp, om);
		this.cynapseProp=cynapseProp;
		this.mappingModel=ReadMappingModel();
	}

	@Override
	public Object ReadMappingModel(){
		System.out.println("Reading Mapping Config..");
		Map<String, NokiaCSCMeasurementModel> modelMap = new LinkedHashMap<String, NokiaCSCMeasurementModel>();
		try {
			  FileInputStream fstream = new FileInputStream(cynapseProp.getMAPPING_CONFIG());
		      BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));	
		      String stringLine;
		      NokiaCSCMeasurementModel mdl;
		      //read mapping config
		      while (( stringLine = br.readLine()) != null)
		      {
		    	  if(!stringLine.startsWith("#") && !stringLine.trim().equals("") && stringLine.contains("[")){
		    		  String[] arr = stringLine.split("\\[*\\]");
		    		  mdl = new NokiaCSCMeasurementModel();
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
		    			  if(x==2 ){
		    				  Map<String, String> fieldMap= GetMappingCounter(s);
		    				  mdl.setFieldMap(fieldMap);
		    			  }else
		    			  if(x==3){
		    				  mdl.setMoIdMapping(s);
				    	  }else
				    	  if(x==4){
				    		  if(s.equalsIgnoreCase("Y"))
				    			  mdl.setTableBuffered(true);
				    		  else
				    			  mdl.setTableBuffered(false);
				    	  }	  
		    		  }
		    		  
		    		  if(type!=null)
		    			  modelMap.put(type, mdl);
		    	  }
		      }
		      br.close();
		      return modelMap;  
			} catch (IOException  e){
			  return null;
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
			System.out.println("No Counter map for "+fileloc);
			return null;
		} 
	}

}
