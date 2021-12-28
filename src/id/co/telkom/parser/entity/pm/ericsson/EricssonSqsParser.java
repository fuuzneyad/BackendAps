package id.co.telkom.parser.entity.pm.ericsson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


import org.apache.log4j.Logger;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.propreader.ParserPropReader;
import id.co.telkom.parser.common.util.CharParserExtender;

public class EricssonSqsParser extends AbstractParser {
	private ParserPropReader cynapseProp;
	private static final Logger logger = Logger.getLogger(EricssonSqsParser.class);
	private static final String T_NAME="SQS";
	private int lineNbr=0;
	
	public EricssonSqsParser(ParserPropReader cynapseProp, AbstractInitiator cynapseInit) {
		super(cynapseProp, cynapseInit);
		this.cynapseProp=cynapseProp;
	}
	
	
	@Override
	public synchronized void  ProcessFile(File file,  final LoaderHandlerManager loader,  final Context ctx) throws Exception {
		logger.debug("Processing file "+file.getName());
		loader.onBeginFile();
		
		String fileName=file.getName();
		String[] sptNe = fileName.split("\\.") ;
		String ne = sptNe.length >=2 ? sptNe[sptNe.length-2]: "-";
			ctx.setNe_id(ne);
			ctx.setTableName(T_NAME);
			ctx.setGranularity(60);
		InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
		final CharParserExtender sqs = new CharParserExtender(reader){};
		boolean isFirstObjEvent=true;
		final Set<String> ignoredDict=new HashSet<String>(Arrays.asList(
													new String[]{
															"A-REPLACEMENTS","TIME OUTS", 
															"B-NUMBER STATUS", "LINE AND DEVICE TECHNICAL FAULTS",
															"HARDWARE TECHNICAL FAULTS", "FAULTS DUE TO CONGESTION",
															"CALLS BLOCKED DUE TO NETWORK MANAGEMENT ACTIONS",
															"CALLS WITH END-OF-SELECTION CODES",
															"NOT CONNECTED TO ANY SPECIFIED EVENT GROUP"
															}
															));
			Map<String,Object> sqsMaster = new LinkedHashMap <String,Object>();
			Map<String,Object> sqsChild = new LinkedHashMap <String,Object>();
			String OBJECT_EVENTS=null;
			try {
				sqs.read();
				while (!sqs.isEOF()) {
					
					StringBuilder sb = new StringBuilder();
					sqs.readUntilEOL(sb).skipEOLs();
					String line = sb.toString();
					lineNbr++;
					
					if(lineNbr==1){
						String[] spt = line.split(" ");
						for(int x=0;x<spt.length;x++){
							if(spt[x].equals("TIME") && spt.length>=x+2){
								ctx.setDatetimeid(convertDate(spt[x+1]+""+spt[x+2]));
								break;
							}
						}
					}
					
					if(!ignoredDict.contains(line.trim()))
						if(line.startsWith("MP   ")||line.startsWith("ACC  ") ){//SQS_MASTER
							sqsMaster.putAll(processHeaderChild(sqs,line));
							isFirstObjEvent=true;
							
							if(line.startsWith("ACC  ")){//header here
								
							}else if(line.startsWith("MP   ")){

							}
						}else if(line.trim().startsWith("N")){
							sqsChild.putAll(processHeaderChild(sqs,line));
						}else if(line.trim().startsWith("OBJECT   EVENTS")|| line.trim().startsWith("END")){
							
							if(line.trim().startsWith("OBJECT   EVENTS")){
								sqs.readUntilEOL(sb).skipEOLs();
								OBJECT_EVENTS=sb.toString().trim().replaceAll(" +", "_");
							}
							
							if(!isFirstObjEvent){//ready model here
								if(!cynapseProp.isGENERATE_SCHEMA_MODE()){
									//mix header & childs
									sqsChild.putAll(sqsMaster);
									loader.onReadyModel(sqsChild, ctx);
								}
								sqsChild = new LinkedHashMap <String,Object>();
							}
							
							ctx.setMo_id("MP="+sqsMaster.get("MP")+"/OBJECT_EVENTS="+OBJECT_EVENTS);
							sqsChild.put("OBJECT_EVENTS", OBJECT_EVENTS);
							PutModel(T_NAME, "OBJECT_EVENTS", OBJECT_EVENTS);
							isFirstObjEvent=false;
							
						}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} 
		reader.close();
		loader.onEndFile();
	}
	@Override
	public void LoadBuffer(LoaderHandlerManager loader,
			Context ctx) throws Exception {
	}
	
	private Map<String, Object> processHeaderChild(final CharParserExtender sqs,String header) throws IOException{
		lineNbr++;
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		String[] hdr = header.split(" \\b");
		for (int i=0;i<hdr.length;i++){
			if(i==hdr.length-1)
				sqs.readUntilEOL(sb).skipEOLs();
			else
				sqs.read(sb,(hdr[i]).length()+1);
			
			if(!hdr[i].trim().equals(""))
				if(!cynapseProp.isGENERATE_SCHEMA_MODE())
					map.put(hdr[i].trim(), sb.toString().trim());else
						PutModel(T_NAME,hdr[i].trim(), sb.toString().trim());
		}

		return map;
	}
	@Override
	public void CreateSchemaFromMap(){
		try{
			String location=cynapseProp.getFILE_SCHEMA_LOC()+"EricssonSQSSchema.sql";
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
	
	private String convertDate(String val) {
		String format;
		if(val.length()=="yyyyMMddHHmmss".length())
			format="yyyyMMddHHmmss";else
		if(val.length()=="yyyyMMddHHmm".length())
			format="yyyyMMddHHmm";else
		format="yyyyMMddHHmmss";
		if(val.length()=="yyMMddHHmm".length())
			format="yyMMddHHmm";
		
		SimpleDateFormat fromUser = new SimpleDateFormat(format);
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
		try{
			Date d = fromUser.parse(val);
			Calendar gc = new GregorianCalendar();
				gc.setTime(d);
				gc.add(Calendar.HOUR, -1);
			return myFormat.format(gc.getTime());
		}catch(ParseException e){return "0000-00-00 00:00:00";}
	}

}
