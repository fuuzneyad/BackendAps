package id.co.telkom.parser.entity.pm.tcel;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.AbstractParser;
import id.co.telkom.parser.common.loader.LoaderHandlerManager;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.propreader.ParserPropReader;
import id.co.telkom.parser.common.util.CharParserExtender;

public class BscAlarmA2Parser extends AbstractParser{
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	private final String ALARM_CONTENT = "ALARM_CONTENT";
	private final String ALARM_HEADER = "ALARM_HEADER";
	private boolean isFirst=true;
	private boolean isAlarmSite = false;
	private boolean isAlarmSite2 = false;
	private boolean isAlarmCell = false;
	
	public BscAlarmA2Parser(ParserPropReader cynapseProp,
			AbstractInitiator parserInit) {
		super(cynapseProp, parserInit);
	}

	@Override
	protected void ProcessFile(File file, LoaderHandlerManager loader,
			Context ctx) throws Exception {
		loader.onBeginFile();
		ctx.setTableName("SITEDOWN");
		InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
		CharParserExtender chr = new CharParserExtender(reader){};
		StringBuilder sb = new StringBuilder();
		chr.read();
		
		while (!chr.isEOF()) {
			chr.readUntilEOL(sb);
			chr.skipEOL();
			String line = sb.toString();
			if(line.startsWith("A2")){
				processHeader(line,loader,ctx);
				isFirst=false;
			}else
				if(!isFirst)
					processContent(line);
		}
		//last
		if(!cynapseProp.isGENERATE_SCHEMA_MODE()){
			loader.onReadyModel(map,ctx);
		}
		loader.onEndFile();
	}
	
	private void processHeader(String line, LoaderHandlerManager loader,
			Context ctx){
		if(map.get(ALARM_HEADER)!=null){
			if(!cynapseProp.isGENERATE_SCHEMA_MODE()){
				loader.onReadyModel(map,ctx);
			}
			map = new LinkedHashMap<String, Object>();
		}
		
		String[] arr =  line.split("\\s+");
		
		map.put("TIME",  arr[arr.length-2]+" "+arr[arr.length-1]);
		
		map.put(ALARM_HEADER, line.trim());
		map.put(ALARM_CONTENT, line.trim());
	}
	
	private void processContent(String line){
		Object o = map.get(ALARM_CONTENT);
		String put = o == null ? line : o.toString()+"\n"+line;
		map.put(ALARM_CONTENT, put);
		//proses detil mo 1
		  String detectMo = "MO                RSITE            CLASS";
		  if(line.startsWith(detectMo))
			  isAlarmSite = true;
		  
		  if(isAlarmSite && !line.startsWith(detectMo)){
			  parse(detectMo,line,map);
			  isAlarmSite = false;
		  }
		  
		//proses detil mo 2
		   String detectMo2 = "MO                SCGR  SC         RSITE           ALARM SLOGAN";
		  if(line.startsWith(detectMo2))
			  isAlarmSite2 = true;
		  
		  if(isAlarmSite2 && !line.startsWith(detectMo2)){
			  parse(detectMo2,line,map);
			  isAlarmSite2 = false;
		  }
		  
		//process Cell
		  detectMo = "CELL       SCTYPE      CHTYPE       CHRATE    SPV";
		  if(line.startsWith(detectMo))
			  isAlarmCell = true;
		  
		  if(isAlarmCell && !line.startsWith(detectMo)){
			  String[] arr = line.split("\\s+");
			  if(arr.length>0)
				  map.put("CELL", arr[0]);
			  if(arr.length>1)
				  map.put("SCTYPE", arr[1]);
			  if(arr.length>2)
				  map.put("CHTYPE", arr[2]);
			  if(arr.length>3)
				  map.put("CHRATE", arr[3]);
			  if(arr.length>4)
				  map.put("SPV", arr[4]);
			  isAlarmCell = false;
		  }
	}

	@Override
	public void LoadBuffer(LoaderHandlerManager loader, Context ctx)
			throws Exception {
		
	}

	@Override
	protected void CreateSchemaFromMap() {
		
	}
	
	private void parse(String header, String data, Map<String, Object> map){
		header = header.replace("ALARM SLOGAN", "ALARM_SLOGAN");
		String[] ss = header.split(" \\b");
		int i = 0;
		int start = 0;
		int end = 0;
		for(String c:ss){
			i++;
			end = start+c.length()+1;
			String dt = i==ss.length ? data.substring(start) : data.substring(start, end);
			map.put(c.trim(), dt.trim());
			start = end;
		}
	}

	
	public static void main(String[] args){
		String a = "CELL       SCTYPE      CHTYPE       CHRATE    SPV";
		String b = "BZ005G3                BCCH";
		String[] ss = a.split(" \\b");
		int i = 0;
		int start = 0;
		int end = 0;
		for(String c:ss){
			i++;
//			System.out.println("["+c+"]"+c.length());
			end = start+c.length()+1;
			
			String dt = i== ss.length ? b.substring(start) : b.substring(start, end);
			
			System.out.println("["+c+"]-->"+c.length()+"->["+dt+"]"+dt.length());
			start = end;
			
		}
	}
}
