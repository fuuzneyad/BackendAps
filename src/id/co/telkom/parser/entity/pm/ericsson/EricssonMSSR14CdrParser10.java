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
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.util.ASN1Dump;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.StandardMeasurementModel;
import id.co.telkom.parser.common.propreader.ParserPropReader;

public class EricssonMSSR14CdrParser10 extends AbstractParser{
	private ParserPropReader cynapseProp;
	private Map<String, StandardMeasurementModel> modelMap;
	private static final Logger logger = Logger.getLogger(EricssonMSSR14CdrParser10.class);
	
	@SuppressWarnings("unchecked")
	public EricssonMSSR14CdrParser10(ParserPropReader cynapseProp, AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
		this.modelMap = (Map<String, StandardMeasurementModel>)cynapseInit.getMappingModel();
		this.cynapseProp=cynapseProp;
	}
	
	
	@Override
	public synchronized void  ProcessFile(File file,  final LoaderHandlerManager loader,  final Context ctx) throws Exception {
		logger.debug("Processing file "+file.getName());
//		loader.onBeginFile();
		String fileName=file.getName();
		String tbl="";
		String ne = fileName.substring(fileName.indexOf("_") + 1);
			ne=ne.indexOf(":") > 0? ne.substring(0, ne.indexOf(":")):ne;
			ne=ne.indexOf("_") > 0? ne.substring(0, ne.indexOf("_")):ne;
			ne=ne.indexOf(".") > 0? ne.substring(0, ne.indexOf(".")):ne;
		ctx.setNe_id(ne);
		int tIdx=1;
		int bIdx=1;
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Map<String, String> header = new LinkedHashMap<String, String>();
		Map<String, Object> buffer = new LinkedHashMap<String, Object>();
		FileInputStream fstream;
		try {
			fstream = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));
			String strLine;
		    while ((strLine = br.readLine()) != null) {
		    	int idx=strLine.indexOf("<");
		    	if(idx==24){//field
		    		if(!strLine.contains("SEQUENCE") && strLine.indexOf("<P")>0){
		    			header.put("H"+tIdx, getChar(strLine));
		    			tIdx++;
		    		}
		    	}else
		    	if(idx==8){
			    	if(strLine.indexOf("<P")>0&&!strLine.contains("&#x")&&strLine.contains("T=\"[4]\"")){//datetime_id
			    			ctx.setDatetimeid(convertDate(getChar(strLine).replace("Z", "")));
			    	}
			    }else
		    	if(idx==20){
		    		if(strLine.indexOf("<P")>0&&!strLine.contains("&#x")){//datetime_id
//		    			ctx.setDatetimeid(convertDate(getChar(strLine).replace("Z", "")));
		    			header = new LinkedHashMap<String, String>();
		    			tIdx=1;
		    		}else if(strLine.indexOf("<P")>0&&strLine.contains("&#x")){
		    			Integer s =HexToDec(getChar(strLine).replaceAll("&#x", "").replaceAll("&#X", "").replaceAll(";", ""));
		    			ctx.setGranularity(s>60?s/60:s);
		    		}
		    	}
		    	else
		    	if(idx==28){//table & mo
		    		if(strLine.indexOf("<P")>0&&!strLine.contains("&#x")){
		    			bIdx=1;
		    			buffer = new LinkedHashMap<String, Object>();
		    			String a=getChar(strLine);
		    			tbl = a.split("\\.")[0];
		    			String mo = a.split("\\.").length>1?a.split("\\.")[1]:a;
		    				   mo=mo.equals("")||mo.equals("-")||mo.equals(".")?ne:mo;
		    			ctx.setMo_id(mo);
		    		}else if(strLine.indexOf("<P")>0&&strLine.contains("&#x")) {
		    			//ready model
		    			if(!buffer.isEmpty()&&buffer.size()==header.size()){
		    				if(!cynapseProp.isGENERATE_SCHEMA_MODE()){
		    					StandardMeasurementModel std = modelMap.get(tbl);
		    					if (std!=null){
				    				for(Map.Entry<String, String> ocols:header.entrySet()){
							    		map.put(ocols.getValue(), buffer.get(ocols.getKey()));
							    	}
				    				ctx.setTableName(std.getTableName());
				    				
//				    				loader.onReadyModel((Map<String, Object>)parserInit.CompareRowsSum(ctx.t_name+"|"+ctx.dateTimeid+"|"+ctx.ne_id+"|"+ctx.mo_id,map), ctx);
				    				map.put("FILENAME", file.getName());
				    				parserInit.CompareRowsMax(ctx.t_name+"|"+ctx.dateTimeid+"|"+ctx.ne_id+"|"+ctx.mo_id+"|"+ctx.granularity,map);
				    				map=new LinkedHashMap<String, Object>();
		    					}
//		    					else
//		    						logger.error("Measurement "+tbl+" have not been mapped yet!!");
		    					
			    				buffer = new LinkedHashMap<String, Object>();
			    				map = new LinkedHashMap<String, Object>();
		    				} else {
		    					for(Map.Entry<String, String> ocols:header.entrySet()){
		    						Object o =buffer.get(ocols.getKey());
		    							o=o==null?"":"0";
						    		PutModel(tbl,ocols.getValue(),o.toString());
						    	}
		    				}
		    			}
		    				
		    		}
			    }else
		    	if(idx==32){//isi
		    		String isi=getChar(strLine);
		    		buffer.put("H"+bIdx, nilaiNya(isi));
		    		bIdx++;
			    }
		    }
		    br.close();
		} finally {
//			loader.onEndFile();
		}
	}
	
	@Override
	public void LoadBuffer(final LoaderHandlerManager loader, final Context ctx) throws Exception {
		loader.onBeginFile();
		for(Map.Entry<String, Object> m : parserInit.mapBuffer.entrySet() ){
			String[] ct = m.getKey().split("\\|");
			ctx.setTableName(ct[0]);
			ctx.setDatetimeid(ct[1]);
			ctx.setNe_id(ct[2]);
			ctx.setMo_id(ct[3]);
			ctx.setGranularity(getGranularity(ct[4]));
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>)m.getValue();
			loader.onReadyModel(map, ctx);
		}
		loader.onEndFile();
	}
	
	private int getGranularity(String s){
		try{
			int a=Integer.parseInt(s);
			return a>60?a/60:a;
		}catch(NumberFormatException e){
			return 0;
		}
	}
	
	@Override
	public void CreateSchemaFromMap(){
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"Ericsson2GSchema.sql";
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
	
	private String getChar(String s){
		String c=s.substring(s.indexOf(">")+1);
		return (c.contains("</P>")?c.replace("</P>", ""):c);
	}
	
	private static Integer HexToDec(String str){
		try{
			return Integer.parseInt(str.replaceAll("%", ""), 16);
		} catch(NumberFormatException e){
			return 0;
		}
	}
	
	private static String nilaiNya(String ini) {
	    ini.trim();
	    String str1 = "";
	    for (int i = 0; i < ini.length(); i++) {
	      if (ini.charAt(i) == '&') {
	        str1 = str1 + Character.toString(ini.charAt(i + 3)) + Character.toString(ini.charAt(i + 4));
	        i += 5;
	      } else {
	        str1 = str1 + Integer.toHexString(ini.charAt(i));
	      }
	    }
	    try
	    {
	      str1 = Long.toString(Long.parseLong(str1, 16));
	    } catch (Exception e) {
	      return null;
	    }
	    return str1;
	}
	
	public static void main(String[] args) throws IOException {
//		System.out.println(Long.toString(Long.parseLong("15F41017702B49", 16)));
//		File file = new File("D:/cdr/syniverse/MACH DAILY period 6-11Nov 2015/CIBCERIC_TLSTC_151105_024038");
//		FileInputStream fstream;
//		try {
//			fstream = new FileInputStream(file);
//			
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
		ASN1InputStream ais = new ASN1InputStream(
		        new FileInputStream(new File("D:/cdr/syniverse/MACH DAILY period 6-11Nov 2015/CIBCERIC_TLSTC_151105_024038")));
		    while (ais.available() > 0) {
		        ASN1Primitive obj = ais.readObject();
		        System.out.println(ASN1Dump.dumpAsString(obj, true));
		    }
		    ais.close();
	}
	
}
