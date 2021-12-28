package id.co.telkom.parser.entity.pm.huawei;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.StandardMeasurementModel;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class HuaweiScpParser10 extends AbstractParser{
	private ParserPropReader cynapseProp;
	@SuppressWarnings("unused")
	private Map<String, StandardMeasurementModel> modelMap;
	private String[] header={"PROTOCOL", "RECV_Q","SEND_Q", "LOCAL_ADDRESS", "FOREIGN_ADDRESS", "STATE"};
	private final String T_NAME_PREFIX="SCP";
	
	@SuppressWarnings("unchecked")
	public HuaweiScpParser10(ParserPropReader cynapseProp, AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
		this.modelMap = (Map<String, StandardMeasurementModel>)cynapseInit.getMappingModel();
		this.cynapseProp=cynapseProp;
	}
	
	
	@Override
	public synchronized void  ProcessFile(File file,  final LoaderHandlerManager loader,  final Context ctx) throws Exception {
		loader.onBeginFile();
		
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		FileInputStream fstream = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));
		ctx.setDatetimeid(convertDate(file.lastModified()));
		ctx.setGranularity(15);
		String mo="";
        String stringLine;
        if(file.getName().startsWith("diameter_"))
        {
        	final String T_NAME=T_NAME_PREFIX+"_DIAMETER";
        	ctx.setTableName(T_NAME);
	        while ((stringLine = br.readLine()) != null)
	        {
	        	if(stringLine.startsWith("*******")&&stringLine.contains("SCP")){
	        		ctx.setNe_id(stringLine.replaceAll("\\*","").replaceAll(" FR State", "").replaceAll("SCP ", "SCP_"));
	        	}else
	        	if(stringLine.startsWith("----") ){
	        		if(stringLine.contains("SCP")){
	        			mo=stringLine.replaceAll("-", "");
	        		}
	        	}else
	        	if(stringLine.startsWith("tcp")){
	            	String[] splitted = stringLine.split("\\s+");
	            	for(int i=0; i<header.length;i++){
	            		if(i==3)
	            			ctx.setMo_id(mo+"/"+splitted[i]);
	            		map.put(header[i], splitted[i]);
	            		PutModel(T_NAME, header[i], splitted[i]);
	            	}
	            	
	            	if(!cynapseProp.isGENERATE_SCHEMA_MODE() && !map.isEmpty()){
	        			loader.onReadyModel(map, ctx);
	        			map = new LinkedHashMap<String, Object>();
	    			}
	        	}
	        }
        }else
        	if(file.getName().startsWith("cpu_"))
            {
            	final String T_NAME=T_NAME_PREFIX+"_CPU";
            	ctx.setTableName(T_NAME);
    	        while ((stringLine = br.readLine()) != null)
    	        {
    	        	if(stringLine.startsWith("*******")&&stringLine.contains("SCP")){
    	        		ctx.setNe_id(stringLine.replaceAll("\\*","").replaceAll(" CPU Usage", "").replaceAll("SCP ", "SCP_"));
    	        	}else
    	        	if(stringLine.startsWith("SCP") && stringLine.contains("|")){
    	        		String [] spts=stringLine.split("\\|"); 
    	        		for(int i=0;i<spts.length;i++){
    	        			if(i==0)
    	        				ctx.setMo_id(spts[i]);else
    	        			if(i==1){
    	        				map.put("CPU", spts[i].trim());
    	        				PutModel(T_NAME, "CPU",spts[i]);
    	        			}
    	        		}
    	        	}
    	        	if(!cynapseProp.isGENERATE_SCHEMA_MODE() && !map.isEmpty()){
	        			loader.onReadyModel(map, ctx);
	        			map = new LinkedHashMap<String, Object>();
	    			}
    	        }
            }
        	else
            	if(file.getName().startsWith("mem_"))
                {
                	final String T_NAME=T_NAME_PREFIX+"_MEM";
                	ctx.setTableName(T_NAME);
        	        while ((stringLine = br.readLine()) != null)
        	        {
        	        	if(stringLine.startsWith("*******")&&stringLine.contains("SCP")){
        	        		ctx.setNe_id(stringLine.replaceAll("\\*","").replaceAll(" Memory Usage", "").replaceAll("SCP ", "SCP_"));
        	        	}else
        	        	if(stringLine.startsWith("SCP") && stringLine.contains("|")){
        	        		String [] spts=stringLine.split("\\|"); 
        	        		for(int i=0;i<spts.length;i++){
        	        			if(i==0)
        	        				ctx.setMo_id(spts[i]);else
        	        			if(i==1){
        	        				map.put("CPU", spts[i].trim());
        	        				PutModel(T_NAME, "CPU",spts[i].trim());
        	        			}
        	        		}
        	        	}
        	        	if(!cynapseProp.isGENERATE_SCHEMA_MODE() && !map.isEmpty()){
		        			loader.onReadyModel(map, ctx);
		        			map = new LinkedHashMap<String, Object>();
		    			}
        	        }
                }
            	else
                	if(file.getName().startsWith("caps_"))
                    {
                    	final String T_NAME=T_NAME_PREFIX+"_CAPS";
                    	ctx.setTableName(T_NAME);
                    	while ((stringLine = br.readLine()) != null)
            	        {
                    		if(stringLine.startsWith("SCP") && stringLine.contains("|")){
            	        		String [] spts=stringLine.split("\\|"); 
            	        		for(int i=0;i<spts.length;i++){
            	        			if(i==0){
            	        				ctx.setMo_id(spts[i].trim());
            	        				ctx.setNe_id(spts[i].trim());
            	        			}
            	        			else {
            	        				map.put("CAPS"+i, spts[i].trim());
            	        				PutModel(T_NAME, "CAPS"+i,spts[i]);
            	        			}
            	        		}
            	        	}
                    		if(!cynapseProp.isGENERATE_SCHEMA_MODE() && !map.isEmpty()){
    		        			loader.onReadyModel(map, ctx);
    		        			map = new LinkedHashMap<String, Object>();
    		    			}
            	        }
                    }
                	else
                    	if(file.getName().startsWith("disk_usage_"))
                        {
                        	final String T_NAME=T_NAME_PREFIX+"_DISK_USAGE";
                        	ctx.setTableName(T_NAME);
                        	while ((stringLine = br.readLine()) != null)
                	        {
                        		if(stringLine.startsWith("======")&&stringLine.contains("Disk Usage")){
                	        		ctx.setNe_id(stringLine.replaceAll("\\=","").replaceAll("Disk Usage ", ""));
                	        	}if (!stringLine.startsWith("Filesystem")){
                	        		String[] splitted = stringLine.split("\\s+");
                	        		for (int i=0; i<splitted.length; i++){
                	        			if(i==0){
                	        				map.put("FILESYSTEM", splitted[i]);
                	        				PutModel(T_NAME, "FILESYSTEM", splitted[i]);
                	        			}else
                	        			if(i==1){
                    	        			map.put("SIZE", splitted[i]);
                    	        			PutModel(T_NAME, "SIZE", splitted[i]);
                    	        		}else
                    	        		if(i==2){
                        	        		map.put("USED", splitted[i]);
                        	        		PutModel(T_NAME, "USED", splitted[i]);
                        	        	}else
                        	        	if(i==3){
                            	        	map.put("AVAIL", splitted[i]);
                            	        	PutModel(T_NAME, "AVAIL", splitted[i]);
                            	        }else
                            	        if(i==4){
                                	       	map.put("USE_PERCENT", splitted[i]);
                                	       	PutModel(T_NAME, "USE_PERCENT", splitted[i]);
                                	    }else
                                	    if(i==5){
                                    	   	map.put("MOUNTED_ON", splitted[i]);
                                    	   	PutModel(T_NAME, "MOUNTED_ON", splitted[i]);
                                    	   	ctx.setMo_id(splitted[i]);
//                                    	   	System.out.println(map);
                                    	   	if(!cynapseProp.isGENERATE_SCHEMA_MODE() && !map.isEmpty()){
                    		        			loader.onReadyModel(map, ctx);
                    		        			map = new LinkedHashMap<String, Object>();
                    		    			}
                                    	}	
                	        		}
                	        	}
                	        }
                        } 
        br.close();
        fstream.close();
		loader.onEndFile();
	}
	
	@Override
	public void CreateSchemaFromMap(){
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"HuaweiSCPSchema.sql";
			System.out.println("Generating Schema to "+location+"..");
			FileWriter out = new FileWriter(location);
			
			StringBuilder sb = new StringBuilder();
			
			for (Map.Entry<String, Map<String,String>> entry : tableModel.entrySet()) {
				
				sb.append("DROP TABLE IF EXISTS "+entry.getKey()+";\n");
				sb.append("CREATE TABLE "+entry.getKey()+" (\n");
				sb.append("\t`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n");
				sb.append("\t`SOURCE_ID` varchar(100) DEFAULT '',\n");
				sb.append("\t`DATETIME_ID` datetime NULL DEFAULT '0000-00-00 00:00:00',\n");
				sb.append("\t`GRANULARITY` int(40) ,\n");
				sb.append("\t`NE_ID` varchar(200) DEFAULT '',\n");
				sb.append("\t`MO_ID` varchar(300) DEFAULT '',\n");
				
				for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
					
					if(entry2.getKey().length()>30)
						System.err.println("warning field "+entry2.getKey()+"'s  lenght >30, Mapping field is recommended!!");
					
					String typeData = isDouble(entry2.getValue()) ? "DOUBLE,\n" : "VARCHAR("+(entry2.getValue().length()+20)+"),\n"; 
					
					typeData="PERIOD_START_TIME|PERIOD_STOP_TIME|PERIOD_REAL_START_TIME|PERIOD_REAL_STOP_TIME".contains(entry2.getKey()) ? "DATETIME NULL DEFAULT NULL,\n" :typeData;
					sb.append("\t`"+entry2.getKey()+"` "+typeData);
				}
				sb.setLength(sb.length()-2);
				sb.append("\n)Engine=MyIsam;\n");
				out.write(sb.toString());
				out.flush();
				sb = new StringBuilder();
					
			}
			out.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	@Override
	public void LoadBuffer(LoaderHandlerManager loader,
			Context ctx) throws Exception {
	}
	
	
	private boolean isDouble(String s){
		try{
			Double.parseDouble(s);
			return true;
		}catch (NumberFormatException e){
			return false;
		}
	}
	
	private static String convertDate(Long time) {
		Date date = new Date(time);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
		try{
			return format.format(date).toString();
		}catch(Exception e){
			return "0000-00-00 00:00:00";
		}
	}
}
