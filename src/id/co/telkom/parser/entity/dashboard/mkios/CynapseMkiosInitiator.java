package id.co.telkom.parser.entity.dashboard.mkios;

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
import id.co.telkom.parser.entity.dashboard.mkios.model.MkiosMeasurementModel;
import id.co.telkom.parser.entity.dashboard.mkios.model.MkiosTableMappingMeasurementModel;

public  class CynapseMkiosInitiator extends AbstractInitiator {
	private ParserPropReader cynapseProp;
	
	public CynapseMkiosInitiator(ParserPropReader cynapseProp, OutputMethodPropReader om) {
		super(cynapseProp, om);
		this.cynapseProp=cynapseProp;
		this.mappingModel=ReadMappingModel();
	}
	
	@Override
	public Object ReadMappingModel(){
		System.out.println("Reading Mapping Config..");
		Map<String, MkiosMeasurementModel> modelMap = new LinkedHashMap<String, MkiosMeasurementModel>();
		try {
			  FileInputStream fstream = new FileInputStream(cynapseProp.getMAPPING_CONFIG());
		      BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));	
		      String stringLine;
		      MkiosMeasurementModel mdl;
		      //read mapping config
		      while (( stringLine = br.readLine()) != null)
		      {
		    	  if(!stringLine.startsWith("#") && !stringLine.trim().equals("") && stringLine.contains("[")){
		    		  String[] arr = stringLine.split("\\[*\\]");
		    		  mdl = new MkiosMeasurementModel();
		    		  String type=null;
		    		  for (int x=0; x<arr.length; x++){
		    			  String s=arr[x].trim().replace("[", "").trim();
		    			  if(x==0){
		    				  type=s;
		    				  mdl.setSheetName(s);
		    			  }else
		    			  if(x==1){
		    				  mdl.setSheetTableMap(GetMappingCounter(s));
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
	
	private Map <String, MkiosTableMappingMeasurementModel> GetMappingCounter(String fileloc){
		try {
			  MkiosTableMappingMeasurementModel ms = new MkiosTableMappingMeasurementModel();
			  Map <String, MkiosTableMappingMeasurementModel> ret = new LinkedHashMap<String, MkiosTableMappingMeasurementModel>();
			  FileInputStream fstream = new FileInputStream(fileloc);
		      BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));	
		      String stringLine;
		      int counter=0;
		      while (( stringLine = br.readLine()) != null)
		      {
		    	  if(!stringLine.startsWith("#") && !stringLine.trim().equals("") && stringLine.contains("[")){
		    		  ms = new MkiosTableMappingMeasurementModel();
		    		  String[] arr = stringLine.split("\\[*\\]");
		    		  for (int x=0; x<arr.length; x++){
		    			  String s=arr[x].trim().replace("[", "").trim();
		    			  if(x==0){
		    				  ms.setxPos(convertToInt(s));
		    			  }else
		    			  if(x==1){
		    				  ms.setyPos(convertToInt(s));
		    			  }else
			    		  if(x==2){
			    			  ms.setWidht(convertToInt(s));
			    		  }else
				    	  if(x==3){
				    		  ms.setDepth(convertToInt(s));
				    	  }else
					      if(x==4){
					    	  ms.setTableName(s);
						  }else
						  if(x==5){
							  ms.setFieldSequence(s);
						 }
		    		  }
		    		  if(ms.getTableName()!=null){
		    			  counter++;
		    			  ret.put("C"+counter, ms);
		    		  }
		    	  }
		      }
		      br.close();
		      fstream.close();
		      return ret;
		}catch (IOException  e){
			System.out.println("No Counter map for "+fileloc);
			return null;
		} 
	}

	private Integer convertToInt(String s){
		try{
			return Integer.parseInt(s);
		}catch (NumberFormatException e){
			return -1;
		}
	}
}
