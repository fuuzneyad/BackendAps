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

public class EricssonPSCSasnParser10 extends AbstractParser{
	private ParserPropReader cynapseProp;
	private Map<String, StandardMeasurementModel> modelMap;
	private static final Logger logger = Logger.getLogger(EricssonPSCSasnParser10.class);
	
	@SuppressWarnings("unchecked")
	public EricssonPSCSasnParser10(ParserPropReader cynapseProp, AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
		this.modelMap = (Map<String, StandardMeasurementModel>)cynapseInit.getMappingModel();
		this.cynapseProp=cynapseProp;
	}
	
	
	@Override
	public synchronized void  ProcessFile(File file,  final LoaderHandlerManager loader,  final Context ctx) throws Exception {
		logger.debug("Processing file "+file.getName());
		loader.onBeginFile();
		StandardMeasurementModel defMdl = modelMap.get("default_mapping_field");
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
			String measFix="";
			ctx.setTableName(TABLENAME);
			ctx.setGranularity(15);
			int line=0;
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			FileInputStream fstream;
			try {
				fstream = new FileInputStream(file);
				BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));
				String strLine;
			    while ((strLine = br.readLine()) != null) { 
			    	line++;
			    	String[] spt = strLine.split("\\|");
			    	String tgl="";
			    	for (int x=0;x<spt.length;x++){
			    		if(x==0){
			    			tgl=spt[x];
			    		}else
			    		if(x==1){
				    		tgl+=" "+spt[x];
				    		ctx.setDatetimeid(convertDate(tgl));
				    	}else
				    	if(x==2){
				    		//ready
					    	if(line!=1 && ctx.ne_id!=null && !ctx.ne_id.equals(spt[x].toUpperCase())&& !cynapseProp.isGENERATE_SCHEMA_MODE()){
					    		loader.onReadyModel(map, ctx);
					    		map = new LinkedHashMap<String, Object>();
					    	}	
				    		ctx.setNe_id(spt[x].toUpperCase());
				    		ctx.setMo_id(spt[x].toUpperCase());
				    		
				    	}else
					    if(x==3){
					    	measFix=defMdl!=null?defMdl.getFieldMap().get(TABLENAME+"|"+spt[x].toUpperCase()):null;
					        measFix=measFix==null?spt[x].toUpperCase():measFix;
					      
					    }else
					    if(x==4){
					    	if (cynapseProp.isGENERATE_SCHEMA_MODE()){
					    		PutModel(TABLENAME, measFix, spt[x]);
					    	}else{
					    		map.put(measFix, isDouble(spt[x])?spt[x]:null);
					    	}
						}
			    	}
			    }
			    //last 
		    	if(!cynapseProp.isGENERATE_SCHEMA_MODE()&& !map.isEmpty()){
		    		loader.onReadyModel(map, ctx);
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
		if(val.length()=="yyyy-MM-dd HH:mm:ss".length())
			format="yyyy-MM-dd HH:mm:ss";else
		if(val.length()=="yyyyMMddHHmmss".length())
			format="yyyyMMddHHmmss";else
		if(val.length()=="yyyyMMddHHmm".length())
			format="yyyyMMddHHmm";else
		format="yyyyMMddHHmmss";
		
		SimpleDateFormat fromUser = new SimpleDateFormat(format);
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try{
			return myFormat.format(fromUser.parse(val));
		}catch(ParseException e){return val;}
	}
	
	
}
