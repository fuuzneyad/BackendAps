package id.co.telkom.parser.entity.pm.huawei;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;



import au.com.bytecode.opencsv.CSVReader;
import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.StandardMeasurementModel;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class HuaweiCSCParser extends AbstractParser{
	private ParserPropReader cynapseProp;
	private Map<String, StandardMeasurementModel> modelMap;
	
	@SuppressWarnings("unchecked")
	public HuaweiCSCParser(ParserPropReader cynapseProp, AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
		this.modelMap = (Map<String, StandardMeasurementModel>)cynapseInit.getMappingModel();
		this.cynapseProp=cynapseProp;
	}
	
	
	@Override
	public synchronized void  ProcessFile(File file,  final LoaderHandlerManager loader,  final Context ctx) throws Exception {
		loader.onBeginFile();

		Map<String, String> header = new LinkedHashMap<String, String>();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		
		StandardMeasurementModel mapField = this.modelMap.get("default_mapping_field");
		
		StandardMeasurementModel mdl=null;
		for (Map.Entry<String, StandardMeasurementModel>entry : this.modelMap.entrySet()){
			mdl = null ;
			if((StandardMeasurementModel)entry.getValue()!=null &&
				file.getName().matches(((StandardMeasurementModel)entry.getValue()).getMeasurementType())
			){
				 mdl=(StandardMeasurementModel)entry.getValue();
				 break;
			 }
		}
		
		if (mdl!=null){
			ctx.setTableName(mdl.getTableName());
			int line =0;
			try {
				FileReader fr = new FileReader(file.getAbsolutePath());
				CSVReader reader = new CSVReader(fr, ',');
				String [] splitted;
				while ((splitted = reader.readNext()) != null) {
					line++;
					int ctrIdx=0;
					if(line==1){//the header
			    		  for (String x:splitted){
			    			  ctrIdx++;
			    			  x=x.trim();
			    			  if(!(x.equals("Granularity Period")||
			    				   x.equals("Object Name")||
			    				   x.equals("Reliability")||
			    				   x.equals("Result Time")
			    				  ) && cynapseProp.isGENERATE_SCHEMA_MODE()
			    				 ){
//			    				  System.out.println("["+x+"]");
			    				  PutModel(mdl.getTableName(), x, "0");
			    			  }
			    			  header.put("X"+ctrIdx,x);
			    		  }
			    	  }else if(line!=2 && !cynapseProp.isGENERATE_SCHEMA_MODE()){//isi..
			    		  for (String x:splitted){
			    			  ctrIdx++;
			    			  x=x.trim();
			    			  if(header.get("X"+ctrIdx)!=null && header.get("X"+ctrIdx).equals("Granularity Period")){
			    				  ctx.setGranularity(ParseGran(x));
			    			  }else
			    			  if(header.get("X"+ctrIdx).equals("Object Name")){
			    				  String mo=x.replace("\"", "");
			    				  ctx.setMo_id(mo);
			    				  ctx.setNe_id(mo.split("/")[0]);
			    			  }else
			    			  if(header.get("X"+ctrIdx).equals("Result Time"))	{
			    				  ctx.setDatetimeid(convertDate(x));
	//		    				  System.out.println("result time="+x.substring(0,x.indexOf("+")));
			    			  }else{
			    				  String counter = mapField.getFieldMap().get(mdl.getTableName()+"|"+header.get("X"+ctrIdx));
				    			  if(counter!=null && !x.equals("")){
	//			    				  System.out.println(counter+"="+x.trim());
				    				  map.put(counter, x);
				    			  }
			    			  }
			    		  }
			    		  loader.onReadyModel(map, ctx);
			    		  map = new LinkedHashMap<String, Object>();
			    	  }
			    }
				reader.close();
				fr.close();
			} 
			finally{
				loader.onEndFile();
			}
		}else
			System.out.println(file.getName()+" Not Matched!!");

			loader.onEndFile();
	}
	
	@Override
	public void CreateSchemaFromMap(){
		CreateMappingConfigFromMap();
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"HuaweiCSCSchema.sql";
			System.out.println("Generating Schema to "+location+"..");
			FileWriter out = new FileWriter(location);
			
			StringBuilder sb = new StringBuilder();
			
			for (Map.Entry<String, Map<String,String>> entry : tableModel.entrySet()) {
				
				sb.append("/*Schema for "+entry.getKey()+"*/\n");
				sb.append("CREATE TABLE "+cynapseProp.getTABLE_PREFIX()+entry.getKey()+" (\n");
				sb.append("\t`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n");
				sb.append("\t`SOURCE_ID` varchar(100) DEFAULT NULL,\n");
				sb.append("\t`DATETIME_ID` datetime NULL DEFAULT NULL,\n");
				sb.append("\t`GRANULARITY` int(40) ,\n");
				sb.append("\t`NE_ID` varchar(300) DEFAULT NULL,\n");
				sb.append("\t`MO_ID` varchar(400) DEFAULT NULL,\n");
				
				int counterId=1;
				for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
					
					if(entry2.getKey().length()>30)
						System.err.println("warning table "+entry.getKey()+" field "+entry2.getKey()+"'s  lenght >30, Mapping field is recommended!!");
					
//					System.out.println(entry.getKey()+"|||"+entry2.getValue()+"|||"+entry2.getValue().toString().length());
//					String typeData = isDouble(entry2.getValue()) ? "DOUBLE,\n" : "VARCHAR("+entry2.getValue().toString().length()+20+"),\n"; 
					String typeData = isDouble(entry2.getValue()) ? "DOUBLE,\n" : "TEXT,\n"; 
//					String typeData = "VARCHAR (600),\n";
//					sb.append("\t`"+entry2.getKey()+"` "+typeData);
					sb.append("\t`C"+counterId+"` "+typeData);
					counterId++;
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
	
	private void CreateMappingConfigFromMap(){
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"HuaweiCSCMappings.cfg";
			System.out.println("Generating mapping config to "+location+"..");
			FileWriter out = new FileWriter(location);
			
			StringBuilder sb = new StringBuilder();
			
			for (Map.Entry<String, Map<String,String>> entry : tableModel.entrySet()) {
				
				sb.append("\n#Table :"+cynapseProp.getTABLE_PREFIX()+entry.getKey()+"\n");
				int counterId=1;
				for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
					sb.append("["+entry.getKey()+"|"+entry2.getKey()+"][C"+counterId+"]\n");	
					counterId++;
				}
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
	private static Integer ParseGran(String s){
		try{
			return Integer.parseInt(s);
		}catch (NumberFormatException e){
			return 0;
		}
	}
	
	private String convertDate(String val) {

		SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try{
			return myFormat.format(fromUser.parse(val));
		}catch(ParseException e){return "";}
		
	}
}
