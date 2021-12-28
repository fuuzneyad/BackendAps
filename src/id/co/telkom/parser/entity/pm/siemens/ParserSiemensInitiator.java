package id.co.telkom.parser.entity.pm.siemens;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.propreader.OutputMethodPropReader;
import id.co.telkom.parser.common.propreader.ParserPropReader;
import id.co.telkom.parser.entity.pm.siemens.model.SiemensPMCommandModel;

public  class ParserSiemensInitiator extends AbstractInitiator {
	private ParserPropReader cynapseProp;
	
	public ParserSiemensInitiator(ParserPropReader cynapseProp, OutputMethodPropReader om) {
		super(cynapseProp, om);
		this.cynapseProp=cynapseProp;
		this.mappingModel=ReadMappingModel();
	}
	
	@Override
	public Object ReadMappingModel(){
		System.out.println("Reading Mapping Config..");
		Map<String, SiemensPMCommandModel> modelMap = new LinkedHashMap<String, SiemensPMCommandModel>();
		try {
			String stringLine;
			FileInputStream fstream = new FileInputStream(cynapseProp.getMAPPING_CONFIG());
		    BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));	
		    SiemensPMCommandModel mdl;   
		    modelMap = new LinkedHashMap<String, SiemensPMCommandModel>();
		    while (( stringLine = br.readLine()) != null)
		    {
		    	  if(!stringLine.startsWith("#") && !stringLine.trim().equals("") && stringLine.contains("[")){
		    		  String[] arr = stringLine.split("\\[*\\]");
		    		  mdl = new SiemensPMCommandModel();
		    		  String type=null;
		    		  for (int x=0; x<arr.length; x++){
		    			  String s=arr[x].trim().replace("[", "").trim();
		    			  if(x==0){
		    				  type=s;
		    				  mdl.setType(s); 
		    			  }else
		    			  if(x==1){
		    				  mdl.setTable_name(s);
		    			  }else
		    			  if(x==2 && s.contains("|")){
		    				  String[] spt =s.split("\\|");
		    				  int idx;
		    				  try {
		    					  idx=Integer.parseInt(spt[1]);
		    				  }catch (NumberFormatException e){idx=1;}

		    				  mdl.setCounter_id(spt[0].trim());
		    				  mdl.setIdxCounter(idx);
		    			  }else
		    			  if(x==3)
		    				  mdl.setComment(s);
		    		  }
		    		  if(type!=null)
		    			  modelMap.put(type, mdl);
		    	  }
		      }
		    	br.close();
		    	return modelMap;
		    } catch (Exception  e){
		    	return null;
			}
	}
	

}
