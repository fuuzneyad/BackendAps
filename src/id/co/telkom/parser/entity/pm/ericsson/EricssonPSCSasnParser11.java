package id.co.telkom.parser.entity.pm.ericsson;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.StandardMeasurementModel;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class EricssonPSCSasnParser11 extends AbstractParser{
	private ParserPropReader cynapseProp;
	private Map<String, StandardMeasurementModel> modelMap;
	private static final Logger logger = Logger.getLogger(EricssonPSCSasnParser11.class);
	
	@SuppressWarnings("unchecked")
	public EricssonPSCSasnParser11(ParserPropReader cynapseProp, AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
		this.modelMap = (Map<String, StandardMeasurementModel>)cynapseInit.getMappingModel();
		this.cynapseProp=cynapseProp;
	}
	
	
	@Override
	public synchronized void  ProcessFile(File file,  final LoaderHandlerManager loader,  final Context ctx) throws Exception {
		logger.debug("Processing file "+file.getName());
		loader.onBeginFile();
//		StandardMeasurementModel defMdl = modelMap.get("default_mapping_field");
		StandardMeasurementModel mdl=null;
		for (Map.Entry<String, StandardMeasurementModel> entry : modelMap.entrySet())
		{
			mdl = null ;
		  	  if((StandardMeasurementModel)entry.getValue()!=null && file.getName().matches(((StandardMeasurementModel)entry.getValue()).getMeasurementType())){
		  		mdl=(StandardMeasurementModel)entry.getValue();
		   		  break;
		   	  }
	   }
		if (mdl!=null){
			final String TABLENAME=mdl.getTableName();
			ctx.setTableName(TABLENAME);
			String[] ss=file.getName().split("-");
			for(String s:ss){
				if(s.startsWith("sajk")){
					ctx.setNe_id(s);
					break;
				}
			}
			ctx.setGranularity(15);
			int line=0;
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			Map<String, String> header = new LinkedHashMap<String, String>();
			FileInputStream fstream;
			try {
				fstream = new FileInputStream(file);
				BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));
				String strLine;
			    while ((strLine = br.readLine()) != null) { 
			    	line++;
			    	String[] spt = strLine.split("\\s+");
			    	if(line==1 && !strLine.trim().equals("")){
			    		for(int i=0;i<spt.length;i++)
			    			header.put("H"+i, spt[i].trim().replace("-", "_").replace("%", ""));
			    	}else{
			    		if(!header.isEmpty()&&header.size()==spt.length){
			    			for(int i=0;i<spt.length;i++){
			    				String data=spt[i].trim().replace("\"","");
			    				map.put(header.get("H"+i), data);
			    				PutModel(TABLENAME, header.get("H"+i), data);

			    				if(i==1){
			    					String date=map.get("DATE").toString();
					    			String time=map.get("TIME").toString();
					    			map.put("TIME_",time);
					    			if(date!=null && time!=null){
					    				ctx.setDatetimeid(convertDate(date+" "+time.split("\\+")[0]));
					    			}
			    				}
			    			}
			    			
			    			String manageObject=ctx.ne_id;
			    			if(mdl.getMoIdMapping()!=null && !mdl.getMoIdMapping().trim().equals("")){
			    				manageObject="";
			    				String[] mos = mdl.getMoIdMapping().split(",");
				    			for(String mo:mos){
				    				if(mo.equals("NE_ID"))
				    					manageObject+=ctx.ne_id;else
				    						manageObject+="/"+map.get(mo);
				    			}
			    			}
			    			ctx.setMo_id(manageObject);
					    	if(!cynapseProp.isGENERATE_SCHEMA_MODE()&& !map.isEmpty()){
					    		loader.onReadyModel(map, ctx);
					    		map = new LinkedHashMap<String, Object>();
					    	}

			    		}
			    	}
			    }
			    br.close();
			} finally {
				loader.onEndFile();
			}
		}
	}
	
	@Override
	public void LoadBuffer(LoaderHandlerManager loader,
			Context ctx) throws Exception {
	}
	
	@Override
	public void CreateSchemaFromMap(){
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"EricssonSasnSchema.sql";
			System.out.println("Generating Schema to "+location+"..");
			FileWriter out = new FileWriter(location);
			
			StringBuilder sb = new StringBuilder();
			
			for (Map.Entry<String, Map<String,String>> entry : tableModel.entrySet()) {
				
				sb.append("/*Schema for "+entry.getKey()+"*/\n");
				sb.append("DROP TABLE IF EXISTS "+cynapseProp.getTABLE_PREFIX()+entry.getKey()+";\n");
				sb.append("CREATE TABLE "+cynapseProp.getTABLE_PREFIX()+entry.getKey()+" (\n");
				sb.append("\t`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n");
				sb.append("\t`SOURCE_ID` varchar(100) DEFAULT NULL,\n");
				sb.append("\t`DATETIME_ID` datetime NULL DEFAULT NULL,\n");
				sb.append("\t`GRANULARITY` int(40) ,\n");
				sb.append("\t`NE_ID` varchar(300) DEFAULT NULL,\n");
				sb.append("\t`MO_ID` varchar(400) DEFAULT NULL,\n");
				
				for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
					
					if(entry2.getKey().length()>30)
						System.err.println("warning table "+entry.getKey()+" field "+entry2.getKey()+"'s  lenght >30, Mapping field is recommended!!");
					
					String typeData = isDouble(entry2.getValue()) ? "DOUBLE,\n" : "VARCHAR("+(entry2.getValue().toString().length()+20)+"),\n"; 
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

	
	private boolean isDouble(String s){
		try{
			Double.parseDouble(s);
			return true;
		}catch (NumberFormatException e){
			return false;
		}
	}
	
	private static String convertDate(String val) {
		String format;
		if(val.length()=="yyyyMMdd HH:mm:ss".length())
			format="yyyyMMdd HH:mm:ss";else
		if(val.length()=="yyyyMMddHHmmss".length())
			format="yyyyMMddHHmmss";else
		if(val.length()=="yyyyMMddHHmm".length())
			format="yyyyMMddHHmm";else
		format="yyyyMMddHHmmss";
		
		SimpleDateFormat fromUser = new SimpleDateFormat(format);
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
		try{
			return myFormat.format(fromUser.parse(val));
		}catch(ParseException e){return null;}
	}
	
	
}
