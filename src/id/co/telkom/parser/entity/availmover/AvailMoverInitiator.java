package id.co.telkom.parser.entity.availmover;

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

public class AvailMoverInitiator extends AbstractInitiator {
	private ParserPropReader cynapseProp;
	
	public AvailMoverInitiator(ParserPropReader cynapseProp,
			OutputMethodPropReader om) {
		super(cynapseProp, om);
		this.cynapseProp=cynapseProp;
		this.mappingModel=ReadMappingModel();
	}

	@Override
	public Object ReadMappingModel() {
		System.out.println("Reading Mapping Config..");
		Map<String, AvailMoverModel> modelMap = new LinkedHashMap<String, AvailMoverModel>();
		try {
			  FileInputStream fstream = new FileInputStream(cynapseProp.getMAPPING_CONFIG());
		      BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));	
		      String stringLine;
		      AvailMoverModel mdl;
		      //read mapping config
		      while (( stringLine = br.readLine()) != null)
		      {
		    	  if(!stringLine.startsWith("#") && !stringLine.trim().equals("") && stringLine.contains("[")){
		    		  String[] arr = stringLine.split("\\[*\\]");
		    		  mdl = new AvailMoverModel();
		    		  String type=null;
		    		  for (int x=0; x<arr.length; x++){
		    			  String s=arr[x].trim().replace("[", "").trim();
		    			  if(x==0){
		    				  type=s;
		    				  mdl.setShema(s);
		    			  }else
		    			  if(x==1){
		    				  mdl.setIp(s);
		    			  }else
		    			  if(x==2 ){
		    				  mdl.setPort(s);
		    			  }else
		    			  if(x==3){
		    				  mdl.setUsername(s);
				    	  }else
			    		  if(x==4){
			    			 mdl.setPassword(s);
					      }else
				    	  if(x==5){
					    	mdl.setKtrFile(s);
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

}
