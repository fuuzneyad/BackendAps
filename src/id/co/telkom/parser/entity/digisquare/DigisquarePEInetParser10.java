package id.co.telkom.parser.entity.digisquare;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.StandardMeasurementModel;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class DigisquarePEInetParser10  extends AbstractParser{
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	private Map<String, String> bandwidth = new LinkedHashMap<String, String>();
	private Map<String, StandardMeasurementModel> modelMap;
	private String tempBWName;
	
	@SuppressWarnings("unchecked")
	public DigisquarePEInetParser10(ParserPropReader cynapseProp,
			AbstractInitiator parserInit) {
		super(cynapseProp, parserInit);
		this.modelMap = (Map<String, StandardMeasurementModel>)parserInit.getMappingModel();
	}
	
	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		
		StandardMeasurementModel mapField = this.modelMap.get("TNAME");
		
		if(mapField!=null){
			
			final String T_NAME=mapField.getTableName();
			loader.onBeginFile();
			ctx.setTableName(T_NAME);
			FileInputStream fstream = new FileInputStream(file);
		    BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));
		    String stringLine;
		    while ((stringLine = br.readLine()) != null)
		    {
		    	stringLine=stringLine.trim();
		    	if(stringLine.startsWith("interface")){
		    		String ifaceFull = stringLine.contains(" ") ? stringLine.split(" ")[1] : stringLine;
		    		String iface = ifaceFull.contains(".")?ifaceFull.split("\\.")[0]:ifaceFull;
		    		String vlan =  ifaceFull.contains(".")?ifaceFull.split("\\.")[1]:"-";
		    		map.put("INTERFACE", iface);
		    		map.put("VLAN", vlan);
		    	}else
		    	if(stringLine.startsWith("description")){
		    		String desc = stringLine.contains(" ") ? stringLine.split(" ")[1] : stringLine;
		    		map.put("SID",getSID2(desc));
		    		map.put("DESCRIPTION",desc);
		    	}else
		    	if(stringLine.startsWith("ip address")){
		    		String ipNetmask = stringLine.replace("ip address","").trim();
		    		String ip = ipNetmask.contains(" ") ? ipNetmask.split(" ")[0] : ipNetmask;
		    		String netmask = ipNetmask.contains(" ") ? ipNetmask.split(" ")[1] : ipNetmask;
		    		map.put("IP", ip);
		    		map.put("NETMASK",netmask);
		    	}else
		    	if(stringLine.startsWith("service-policy")){
		    		stringLine=stringLine.replace("service-policy","").trim();
		    		if(stringLine.contains("input")){
		    			stringLine=stringLine.replace("input","").trim();
		    			map.put("INPUT_RATE", getBandwidth(bandwidth.get(stringLine)));
		    		}else
		    		if(stringLine.contains("output")){
			    		stringLine=stringLine.replace("output","").trim();
		    			map.put("OUTPUT_RATE", getBandwidth(bandwidth.get(stringLine)));
			    	}
		    	}else
		    	if(stringLine.equals("shutdown")){
		    		map.put("STATE", "shutdown");
			    }else 
			    if(stringLine.startsWith("policy-map ")){
			    	tempBWName = stringLine.replace("policy-map ", "").trim();
				}else 
				if(stringLine.startsWith("police cir ")||stringLine.startsWith("bandwidth")){
				   	bandwidth.put(tempBWName, stringLine.replace("police cir ", "").replace("bandwidth", "").trim().split(" ")[0]);
				}else
		    	if(stringLine.startsWith("!")&& !map.isEmpty()){
		    		if(!cynapseProp.isGENERATE_SCHEMA_MODE()){
		    			if(map.get("STATE")==null){
		    				map.put("STATE", "up");
		    			}
		    			loader.onReadyModel(map, ctx);
		    		}else{
		    			for(Map.Entry<String, Object> mp:map.entrySet()){
		    				PutModel(T_NAME, mp.getKey(), mp.getValue()==null? "" : mp.getValue().toString());
		    			}
		    		}
		    		map =new LinkedHashMap<String, Object>();
		    	}
		    }
		    br.close();
			fstream.close();
			loader.onEndFile();
		}else
			System.out.println("Mapping field not valid!!");
	}

	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx)
			throws Exception {
		
	}

	@Override
	protected void CreateSchemaFromMap() {
		
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"DigiSquare.sql";
			System.out.println("Generating Schema to "+location+"..");
			FileWriter out = new FileWriter(location);
			
			StringBuilder sb = new StringBuilder();
			
			for (Map.Entry<String, Map<String,String>> entry : tableModel.entrySet()) {
				
				sb.append("/*Schema for "+entry.getKey()+"*/\n");
				sb.append("CREATE TABLE "+entry.getKey()+" (\n");
				sb.append("\tENTRY_DATE DATE,\n");
				sb.append("\tSOURCE_ID varchar2(100),\n");
				
				for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
					
					if(entry2.getKey().length()>30)
						System.err.println("warning field "+entry2.getKey()+"'s  lenght >30, Mapping field is recommended!!");
					String typeTxt = entry2.getValue().toString().length() > 300 ? "TEXT,\n" : "VARCHAR2("+(entry2.getValue().toString().length()+20)+"),\n" ;
					String typeData = isDouble(entry2.getValue()) ? "NUMBER,\n" : typeTxt; 
					sb.append("\t"+entry2.getKey()+" "+typeData);
				}
				sb.setLength(sb.length()-2);
				sb.append("\n);\n");
				out.write(sb.toString());
				out.flush();
				sb = new StringBuilder();
				
			}
			out.close();
		} catch (IOException e){
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
	
	private static String getSID(String s){
		s=s.replace("_", "-");
		String ret="";
		for(String a:s.split("-")){
			if(isNumber(a)&&(a.length()==5||a.length()==10||a.length()==4)){
				ret=ret.equals("")?ret+""+a:ret+"-"+a;
			}
		}
		return ret.equals("")?null:ret;
	}
	
	private static String getSID2(String s){
		String ret = s.split("_")[0].trim();
		return (ret.contains("-")&& ret.length()=="2000023-PKG000400010".length()) ? ret:getSID(s);
	}
	
	private static boolean isNumber(String s){
		try{
			Integer.parseInt(s);
			return true;
		}catch (Exception e) {
			return false;
		}
	}
	
	private static double getBandwidth(String s){
		double ret=0;
		if(s!=null){
			s=s.replace("000", "");
			ret=Double.parseDouble(s);
		}
		return ret;
		
	}
}
