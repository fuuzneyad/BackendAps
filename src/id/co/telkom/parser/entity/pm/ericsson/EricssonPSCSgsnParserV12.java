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

public class EricssonPSCSgsnParserV12 extends AbstractParser{
	private ParserPropReader cynapseProp;
	private Map<String, StandardMeasurementModel> modelMap;
	private static final Logger logger = Logger.getLogger(EricssonPSCSgsnParserV12.class);
	@SuppressWarnings("unchecked")
	public EricssonPSCSgsnParserV12(ParserPropReader cynapseProp, AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
		this.modelMap = (Map<String, StandardMeasurementModel>)cynapseInit.getMappingModel();
		this.cynapseProp=cynapseProp;
	}
	
	
	@Override
	public synchronized void  ProcessFile(File file,  final LoaderHandlerManager loader,  final Context ctx) throws Exception {
		logger.debug("Processing file "+file.getName());
		
		String filename=file.getName();
		String tabel;
		StandardMeasurementModel mapField =modelMap.get("default_mapping_field");
		Map<String, String> meas = new LinkedHashMap<String, String>();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		int measCounter=1;
		StandardMeasurementModel mdl=null;
		for (Map.Entry<String, StandardMeasurementModel> entry : modelMap.entrySet())
		{
			 mdl = null ;
			 if((StandardMeasurementModel)entry.getValue()!=null && filename.matches(((StandardMeasurementModel)entry.getValue()).getMeasurementType())){
				 mdl=(StandardMeasurementModel)entry.getValue();
				 break;
			 }
			 
		}
		if (mdl!=null){
			String ne = (filename.indexOf("_")>-1)?filename.substring(filename.lastIndexOf("_")+1):null;
				ne=ne.indexOf(".")>-1?ne.split("\\.")[0]:ne;
			ctx.setNe_id(ne);
			ctx.setTableName(mdl.getTableName());
			tabel=mdl.getTableName();
			FileInputStream fstream =null;
			BufferedReader br=null;
			try {
				fstream = new FileInputStream(file);
			    br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));	
			      String stringLine;
			      while ((stringLine = br.readLine()) != null)
			      {
			    	  if(stringLine.startsWith("<cbt>")){
						  ctx.setDatetimeid(convertDate(GetMeas(stringLine)));
			    	  }else
			    	  if(stringLine.startsWith("<mts>")){ 
			    		  meas = new LinkedHashMap<String, String>();
						  measCounter=1;
				      }else
				      if(stringLine.startsWith("<moid>")){ 
				    	 ctx.setMo_id(GetMeas(stringLine).trim());
						 measCounter=1;
					  }else
					  if(stringLine.startsWith("<gp>")){ 
						  ctx.setGranularity(getGranularity(GetMeas(stringLine)));
					  }else
					  if(stringLine.startsWith("<mt>")){ 
						  meas.put("m"+measCounter, GetMeas(stringLine));
					      measCounter++;
					  }else
					  if(stringLine.startsWith("<r>")){
						  String read = GetMeas(stringLine);
						  String ctr=meas.get("m"+measCounter);
						  if(ctr!=null){
							  String param = mapField.getFieldMap().get(tabel+"|"+ctr.toUpperCase());
							  if( param==null){
									param=meas.get("m"+measCounter).toUpperCase();
								}
								param=param.trim().toUpperCase();
									
								if(cynapseProp.isGENERATE_SCHEMA_MODE()){
									PutModel(tabel, param, read);
								}else
									map.put(param, read.equals("")?null:read);
						  }
						  measCounter++;
					  } else  if((stringLine.startsWith("</mdc>")||stringLine.startsWith("</mv>")) && !cynapseProp.isGENERATE_SCHEMA_MODE() && !map.isEmpty() ){
						  if(ctx.mo_id!=null && ctx.mo_id.equals("") )
							  ctx.setMo_id(ctx.ne_id);
						  parserInit.CompareRowsMax(ctx.t_name+"|"+ctx.dateTimeid+"|"+ctx.ne_id+"|"+ctx.mo_id+"|"+ctx.granularity,map);
						  map = new LinkedHashMap<String, Object>();
					  }
			      }
			      
			      
			} finally {
				try {
					br.close();
					fstream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
		}else
			System.out.println(filename+" Not Matched!!");
	}
	
	
	@Override
	public void LoadBuffer(final LoaderHandlerManager loader, final Context ctx) throws Exception {
		loader.onBeginFile();
		for(Map.Entry<String, Object> m : parserInit.mapBuffer.entrySet() ){
			String[] ct = m.getKey().split("\\|");
			ctx.setTableName(ct[0]);
			ctx.setDatetimeid(convertDate(ct[1]));
			ctx.setNe_id(ct[2]);
			ctx.setMo_id(ct[3]);
			ctx.setGranularity(getGranularity(ct[4]));
			@SuppressWarnings("unchecked")
			final Map<String, Object> map = (Map<String, Object>)m.getValue();
			loader.onReadyModel(map, ctx);
		}
		loader.onEndFile();
	}
	
	private String GetMeas(String ln){
		String tag=ln.substring(ln.indexOf("<")+1, ln.indexOf(">"));
		return ln.replace("<"+tag+">", "").replace("</"+tag+">", "").trim();
	}
	
	@Override
	public void CreateSchemaFromMap(){
		CreateMappingConfigFromMap();
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"EricssonPSCSgsnSchema.sql";
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
					
					String typeData = isDouble(entry2.getValue()) ? "DOUBLE,\n" : "VARCHAR(80),\n"; 
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
	private void CreateMappingConfigFromMap(){
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"EricssonPSCSgsnMappings.cfg";
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
	
	private int getGranularity(String s){
		try{
			int a=Integer.parseInt(s);
			return a;
		}catch(NumberFormatException e){
			return 0;
		}
	}
	
	private static String convertDate(String val) {
		String format;
		if(val.length()=="yyyyMMddHHmmss".length())
			format="yyyyMMddHHmmss";else
		if(val.length()=="yyyyMMddHHmm".length())
			format="yyyyMMddHHmm";else
		if(val.length()=="yyyy-MM-dd HH:mm:ss".length())
			format="yyyy-MM-dd HH:mm:ss";else		
		format="yyyyMMddHHmmss";
		
		SimpleDateFormat fromUser = new SimpleDateFormat(format);
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
		try{
			return myFormat.format(fromUser.parse(val));
		}catch(ParseException e){return "0000-00-00 00:00:00";}
	}
	
}
