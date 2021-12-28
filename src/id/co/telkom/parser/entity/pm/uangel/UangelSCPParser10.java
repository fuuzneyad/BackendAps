package id.co.telkom.parser.entity.pm.uangel;

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

public class UangelSCPParser10 extends AbstractParser{
	private ParserPropReader cynapseProp;
	private Map<String, StandardMeasurementModel> modelMap;
	
	@SuppressWarnings("unchecked")
	public UangelSCPParser10(ParserPropReader cynapseProp, AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
		this.modelMap = (Map<String, StandardMeasurementModel>)cynapseInit.getMappingModel();
		this.cynapseProp=cynapseProp;
	}
	
	
	@Override
	public synchronized void  ProcessFile(File file,  final LoaderHandlerManager loader,  final Context ctx) throws Exception {
		loader.onBeginFile();
		
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		@SuppressWarnings("unused")
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
			ctx.setGranularity(15);
			int line =0;
			try {
				FileReader fr = new FileReader(file);
				CSVReader reader = new CSVReader(fr, ',');
				String [] splitted;
				while ((splitted = reader.readNext()) != null) {
					line++;
					int ctrIdx=0;
			    		if(line!=0){
			    		  String tgl="";
			    		  for (String x:splitted){
			    			  ctrIdx++;
			    			  x=x.trim();
			    			  if(ctrIdx==1){
			    				  ctx.setNe_id(x);
			    				  ctx.setMo_id(x);
			    			  }else
			    			  if(ctrIdx==2){
			    				  tgl=x;
				    		  }else
				    		  if(ctrIdx==3){
				    			  tgl+=" "+((getInt(x)<10)?"0"+x:x);
					    	  }else	
					    	  if(ctrIdx==4){
					    		  tgl+=":"+((getInt(x)==0)?"00":x)+":00";
					    		  ctx.setDatetimeid(convertDate(tgl));
						      }else{
						    	  int idx=(ctrIdx-4);
						    	  if(cynapseProp.isGENERATE_SCHEMA_MODE()){
						    		  PutModel(ctx.t_name, "C"+(idx), x);
						    	  }else{
						    		  if(idx==1){
						    			  if(!isDouble(x.trim()))
						    				  ctx.setMo_id(ctx.ne_id+"/"+x);
//						    			  else
//						    				  ctx.setMo_id(ctx.ne_id);
						    		  }
						    		  map.put("C"+(idx), x);
						    	  }
						      } 
			    		  }
			    		  if(!cynapseProp.isGENERATE_SCHEMA_MODE() && !map.isEmpty()){
		    				  loader.onReadyModel(map, ctx);
		    				  map = new LinkedHashMap<String, Object>();
		    			 }
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
	public void LoadBuffer(LoaderHandlerManager loader,
			Context ctx) throws Exception {
	}
	
	@Override
	public void CreateSchemaFromMap(){
		CreateMappingConfigFromMap();
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"UangelScpSchema.sql";
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
					
					String typeData = isDouble(entry2.getValue()) ? "DOUBLE,\n" : "TEXT,\n"; 
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

	private int getInt(String s){
		try{
			return Integer.parseInt(s);
		}catch (NumberFormatException e){
			return 0;
		}
	}
	
	
	private void CreateMappingConfigFromMap(){
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"HuaweiPSCSgsnMappings.cfg";
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
	
	private String convertDate(String val) {

		SimpleDateFormat fromUser = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try{
			return myFormat.format(fromUser.parse(val));
		}catch(ParseException e){return "";}
		
	}
}
