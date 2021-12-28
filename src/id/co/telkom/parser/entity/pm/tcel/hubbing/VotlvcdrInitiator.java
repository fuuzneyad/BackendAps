package id.co.telkom.parser.entity.pm.tcel.hubbing;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.model.StandardMeasurementModel;
import id.co.telkom.parser.common.propreader.OutputMethodPropReader;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class VotlvcdrInitiator extends AbstractInitiator{
	private ParserPropReader cynapseProp;
	public VotlvcdrInitiator(ParserPropReader cynapseProp,
			OutputMethodPropReader om) {
		super(cynapseProp, om);
		this.cynapseProp=cynapseProp;
	}

	
	@Override
	public Object ReadMappingModel() {
		VotlvcdrModels  ret = new VotlvcdrModels();

		//untuk mapping file reader
		Map<String, StandardMeasurementModel> modelMap = new LinkedHashMap<String, StandardMeasurementModel>();
		try {
			  FileInputStream fstream = new FileInputStream(cynapseProp.getMAPPING_CONFIG());
		      BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));	
		      String stringLine;
		      StandardMeasurementModel mdl;
		      //read mapping config
		      while (( stringLine = br.readLine()) != null)
		      {
		    	  if(!stringLine.startsWith("#") && !stringLine.trim().equals("") && stringLine.contains("[")){
		    		  String[] arr = stringLine.split("\\[*\\]");
		    		  mdl = new StandardMeasurementModel();
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
				    	   }	  
		    		  }
		    		  
		    		  if(type!=null)
		    			  modelMap.put(type, mdl);
		    	  }
		      }
		      br.close();
			} catch (IOException  e){
				System.out.println("IOException"+e);
				modelMap=null;
			}
			
		//untuk mapping country
		Map<String, RateModel> ratemodelmap = new LinkedHashMap<String, RateModel>();
		DataSource ds = getDs();
		String SQL = "SELECT COUNTRY_CODE, COUNTRY_NAME, OPERATOR_NAME, RATE, "
				+ "ID_PARAMETER_COUNTRY FROM PARAMETER_COUNTRY_CODE";
		try{
			Connection conn = ds.getConnection();
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(SQL);
			//kosong
			while (rs.next()) {
				RateModel mdl = new RateModel();
						mdl.setCountryCode(rs.getString("COUNTRY_CODE"));
						mdl.setCountryName(rs.getString("COUNTRY_NAME"));
						mdl.setOperatorName(rs.getString("OPERATOR_NAME"));
						mdl.setRate(rs.getDouble("RATE"));
						mdl.setIdparametercountry(rs.getString("ID_PARAMETER_COUNTRY"));
						
				ratemodelmap.put(rs.getString("COUNTRY_CODE"), mdl);
			}
			rs.close();
			st.close();
			conn.close();
			
		}catch(SQLException e){
			e.printStackTrace();
			ratemodelmap= null;
		}
		
		ret.setModelMap(modelMap);
		ret.setRateMap(ratemodelmap);
		return ret;
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
