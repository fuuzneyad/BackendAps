package id.co.telkom.parser.entity.cli.ericsson.mss.nocdcommand;


import java.io.IOException;
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

import id.co.telkom.parser.common.charparser.AbstractCommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;


public class SqrepCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private static final String T_NAME="SQS";
	@SuppressWarnings("unused")
	private int lineNbr=0;
	private Map<String, Map<String, String>> model = new LinkedHashMap<String, Map<String, String>>();
	
	public SqrepCommandHandler(Parser reader, DataListener listener, String command, String params) {
		super(command, params);
		this.reader = reader;
		this.listener=listener;
	}
	
	@Override
	public void handle(Context ctx)
			throws IOException {
		ctx.setTableName(T_NAME);
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
			boolean isStartExec=false;
			while (!isStartExec && !reader.isEOF() && !reader.isEqual('<')) {
				StringBuilder sb = new StringBuilder();
				reader.readUntilEOL(sb).skipEOLs();
				if(sb.toString().trim().equals("ORDERED")){
					reader.skipLines(3).skipEOLs();
					isStartExec=true;
					break;
				}else
				if(sb.toString().trim().equals("NOT ACCEPTED"));{
					isStartExec=false;
					break;
				}	
			}
			
			if(!isStartExec){
				done();
				return;
			}
			while (!reader.isEOF() && !reader.isEqual('<')) {
					StringBuilder sb = new StringBuilder();
					reader.readUntilEOL(sb).skipEOLs();
					String line = sb.toString();
					lineNbr++;
						
						if(line.startsWith("END")){
							if(sqsChild.size()>4)
								listener.onReadyData(ctx, sqsChild, reader.getLine());
							sqsChild = new LinkedHashMap <String,Object>();
							ctx.setMo_id(null);
							done();
							return;
						}
						if(!ignoredDict.contains(line.trim()))
							if(line.startsWith("MP   ")||line.startsWith("ACC  ") ){//SQS_MASTER
								sqsMaster.putAll(processHeaderChild(reader,line));
								ctx.setDatetimeid(convertDate(sqsMaster.get("DATE")+""+sqsMaster.get("TIME")));
								
								isFirstObjEvent=true;
								
								if(line.startsWith("ACC  ")){//header here
									
								}else if(line.startsWith("MP   ")){
	
								}
							}else if(line.trim().startsWith("N")){
								sqsChild.putAll(processHeaderChild(reader,line));
							}else if(line.trim().startsWith("OBJECT   EVENTS")|| line.trim().startsWith("END")){
								
								if(line.trim().startsWith("OBJECT   EVENTS")){
									reader.readUntilEOL(sb).skipEOLs();
									OBJECT_EVENTS=sb.toString().trim().replaceAll(" +", "_");
								}
								
								if(!isFirstObjEvent){//ready model here
										//mix header & childs
										sqsChild.putAll(sqsMaster);
										listener.onReadyData(ctx, sqsChild, reader.getLine());
										sqsChild = new LinkedHashMap <String,Object>();
										ctx.setMo_id(null);
								}
								
									ctx.setMo_id("MP="+sqsMaster.get("MP")+"/OBJECT_EVENTS="+OBJECT_EVENTS);
									sqsChild.put("OBJECT_EVENTS", OBJECT_EVENTS);
									PutModel(T_NAME, "OBJECT_EVENTS", OBJECT_EVENTS);
									isFirstObjEvent=false;
								
							}
				}
				done();
				return;
	}
	
	private Map<String, Object> processHeaderChild(Parser reader,String header) throws IOException{
		lineNbr++;
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		String[] hdr = header.split(" \\b");
		for (int i=0;i<hdr.length;i++){
			if(i==hdr.length-1)
				reader.readUntilEOL(sb).skipEOLs();
			else
				reader.read(sb,(hdr[i]).length()+1);
			
			if(!hdr[i].trim().equals(""))
					map.put(hdr[i].trim(), sb.toString().trim());
					PutModel(T_NAME,hdr[i].trim(), sb.toString().trim());
		}

		return map;
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
//				gc.add(Calendar.HOUR, -1);
			return myFormat.format(gc.getTime());
		}catch(ParseException e){return "0000-00-00 00:00:00";}
	}
	
	private void PutModel(String tableName,String a, String b){
		Map<String, String> mp = model.get(tableName);
		if(mp==null)
			mp = new LinkedHashMap<String, String>();
		mp.put(a, b);
		model.put(tableName, mp);
	}

	public Map<String, Map<String, String>> getModel() {
		return model;
	}
	
	
}
