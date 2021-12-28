package id.co.telkom.parser.entity.lte.pm.ericsson;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Map;



import org.apache.log4j.Logger;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.StandardMeasurementModel;
import id.co.telkom.parser.common.propreader.ParserPropReader;
import id.co.telkom.parser.entity.pm.ericsson.model.StructuredEricsson3GModel;

public class EricssonPMLTEEriV10 extends AbstractParser{
	private ParserPropReader cynapseProp;
	private Map<String, StandardMeasurementModel> modelMap;
	private static final Logger logger = Logger.getLogger(EricssonPMLTEEriV10.class);
	private StructuredEricsson3GModel strEri = new StructuredEricsson3GModel();
	private String newsw;
	
	@SuppressWarnings("unchecked")
	public EricssonPMLTEEriV10(ParserPropReader cynapseProp, AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
		this.modelMap = (Map<String, StandardMeasurementModel>)cynapseInit.getMappingModel();
		this.cynapseProp=cynapseProp;
	}
	
	
	@Override
	public synchronized void  ProcessFile(File file,  final LoaderHandlerManager loader,  final Context ctx) throws Exception {
		logger.debug("Buffering file "+file.getName());
		try{
			loader.onBeginFile();
			StandardMeasurementModel mapField =modelMap.get("default_mapping_field");
			Map<String, String> meas = new LinkedHashMap<String, String>();
			int measCounter=1;
			String  tabel="-----";
			FileInputStream fstream =null;
			BufferedReader br=null;
			fstream = new FileInputStream(file);
		    br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));	
		    String stringLine;
		    while ((stringLine = br.readLine()) != null)
		    {
		    	  if(stringLine.startsWith("<cbt>")){
					  ctx.setDatetimeid(convertDate(GetMeas(stringLine), cynapseProp.getTIME_DIFF()));
		    	  }else
		    	  if(stringLine.startsWith("<nedn>")){
					ctx.setNe_id(GetMeas(stringLine));
					if(ctx.ne_id.equals("") && file.getName().contains("SubNetwork=") && file.getName().contains("_statsfile.xml") )
						ctx.setNe_id(file.getName().substring(file.getName().indexOf("SubNetwork="), file.getName().indexOf("_statsfile.xml")));
		    	  }else
		    	  if(stringLine.startsWith("<nesw>")){
		    		  newsw=GetMeas(stringLine);
				  }else
				  if(stringLine.startsWith("<mts>")){
						meas = new LinkedHashMap<String, String>();
						measCounter=1;
				  }else
				  if(stringLine.startsWith("<gp>")){
					  String gp=GetMeas(stringLine);
					  ctx.setGranularity(Integer.parseInt(gp)>60?Integer.parseInt(gp)/60:Integer.parseInt(gp));
				  }else
			      if(stringLine.startsWith("<mt>")){
			    	  meas.put("m"+measCounter, GetMeas(stringLine));
					  measCounter++;
				  }else
				  if(stringLine.startsWith("<moid>")){
					  	String read=GetMeas(stringLine);
						measCounter=1;
						ctx.setMo_id(read);
						if(read.contains(",") && read.contains("=")){
							String[] t_name=read.substring(read.lastIndexOf(",")+1).split("=");
							tabel=t_name[0].toUpperCase();
						}
				  }else
				  if(stringLine.startsWith("<r>")){
					  String read=GetMeas(stringLine);
						if(modelMap.get(tabel)!=null || cynapseProp.isGENERATE_SCHEMA_MODE())
							{
								//content
								String ctr=meas.get("m"+measCounter);
								if(ctr!=null){
									String param = mapField.getFieldMap().get(tabel+"|"+ctr.toUpperCase());
									if( param==null){
										param=meas.get("m"+measCounter).toUpperCase();
									}
									param=param.trim().toUpperCase();
									
									String tidx=null;
									if(param.contains("|")){
										String[] splitted=param.split("\\|");
										param=splitted[0].trim();
										tidx=splitted[1].trim();
									}
									String tableIndexed=(tidx==null || tidx.equals("0")) ? tabel : tabel+"_"+tidx ;
									strEri.putMap(tableIndexed+"|"+ctx.mo_id, param, read.equals("")?null:read);
										
									if(cynapseProp.isGENERATE_SCHEMA_MODE())
										PutModel(tableIndexed, param, read);
									
								}
							}
							measCounter++;
				  }
		      }
		      br.close();
		      fstream.close();
		} finally {
			
			//ready model here
			if(!cynapseProp.isGENERATE_SCHEMA_MODE() ){
				for(Map.Entry<String,Map<String,Object>> mp : strEri.structuredModel2.entrySet()){

					String[] spt = mp.getKey().split("\\|");
					String tbl = spt.length>=0?spt[0]:"SOMETHING_WRONG";
					String mo = spt.length>=1?spt[1]:"SOMETHING_WRONG";
					String[] moValue=mo.substring(mo.lastIndexOf(",")+1).split("=");
					
					Map<String,Object> map = mp.getValue();
						map.put("NESW", newsw);
						map.put(moValue[0].toUpperCase(), moValue.length>0?moValue[1]:null);
						
					StandardMeasurementModel mdl = 	modelMap.get(tbl);
					String tableFix = (mdl==null||mdl.getTableName()==null)?tbl:mdl.getTableName();
					ctx.setTableName(tableFix);
					ctx.setMo_id(mo);
					loader.onReadyModel(map, ctx);
				}
			}
			loader.onEndFile();
		}
	}
	
	@Override
	public void LoadBuffer(LoaderHandlerManager loader,
			Context ctx) throws Exception {
	}
	
	private String GetMeas(String ln){
		String tag=ln.substring(ln.indexOf("<")+1, ln.indexOf(">"));
		return ln.replace("<"+tag+">", "").replace("</"+tag+">", "").trim();
	}
	@Override
	public void CreateSchemaFromMap(){
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"/EricssonLTESchema.sql";
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
					
					String typeData = isDouble(entry2.getValue()) ? "DOUBLE,\n" : "VARCHAR("+(entry2.getValue().toString().length()+200)+"),\n"; 
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
		createCounterMapping();
	}

	private void createCounterMapping(){
		String location=cynapseProp.getFILE_SCHEMA_LOC()+"/EricssonLTECounterMapping.cfg";
		System.out.println("Generating Schema to "+location+"..");
		try {
			FileWriter out = new FileWriter(location);
		
			StringBuilder sb = new StringBuilder();
			sb.append("###########################################################\n");
			sb.append("#Praxis Cynapse Configuration\n");
			sb.append("#v1.0\n");
			sb.append("#Copyright 2013 @SMLTechnologies\n");
			sb.append("#\n");
			sb.append("#counter mapping mapping Ericsson LTE\n");
			sb.append("#used for simplified counter name\n");
			sb.append("#format [counter_id][counter_field/database_column][Splitted_table_if_needed]\n");
			sb.append("#example: [PMCAPACITYALLOCATTSERVEDCHUSERS|PMCAPALLOCATTSRVCHUSERS][0]\n");
			sb.append("###########################################################\n\n");
			out.write(sb.toString());
			out.flush();
			sb = new StringBuilder();
			
			for (Map.Entry<String, Map<String,String>> entry : tableModel.entrySet()) {
				sb = new StringBuilder();
				int counterId=1;
				sb.append("#Table "+entry.getKey()+"\n");
				for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
					int idx = ceilingDown(counterId, 300);
					String idxStr = idx==0 ? "" : "|"+idx;
					sb.append("["+entry.getKey()+"|"+entry2.getKey()+"][C"+counterId+idxStr+"]\n");
					counterId++;
				}
				sb.append("\n");
				out.write(sb.toString());
				out.flush();
			}
			out.close();
		} catch (IOException e) {
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
	
	protected static String convertDate(String val, int timeDiff) {
		val=val.replace("Z", "");
		String format;
		if(val.length()=="yyyyMMddHHmmss".length())
			format="yyyyMMddHHmmss";else
		if(val.length()=="yyyyMMddHHmm".length())
			format="yyyyMMddHHmm";else
		format="yyyyMMddHHmmss";
		
		SimpleDateFormat fromUser = new SimpleDateFormat(format);
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try{
			Date d = fromUser.parse(val);
			Calendar gc = new GregorianCalendar();
				gc.setTime(d);
				gc.add(Calendar.HOUR, timeDiff);
			return myFormat.format(gc.getTime());
		}catch(ParseException e){return null;}
	}
	
	private static int ceilingDown(int x, int y){
		return x%y!=0 ? (x/y) : x/y-1;
	}
	
}
