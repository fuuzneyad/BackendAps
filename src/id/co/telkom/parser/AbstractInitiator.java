package id.co.telkom.parser;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import id.co.telkom.parser.common.loader.DBFileListWriter;
import id.co.telkom.parser.common.model.DBTableModel;
import id.co.telkom.parser.common.propreader.OutputMethodPropReader;
import id.co.telkom.parser.common.propreader.ParserPropReader;
import id.co.telkom.parser.common.util.KeyException;
import id.co.telkom.parser.common.util.KeyVal;
import id.co.telkom.parser.common.util.RowCounter;

public abstract class AbstractInitiator {
	
	protected Object mappingModel ;
	protected DBTableModel dbTableModel ;
	private ParserPropReader cynapseProp;
	private Map<String, String> datetimeMap = new LinkedHashMap<String, String>();
	public RowCounter rowCounter;
	public DBFileListWriter dbWriter;
	private DataSource ds;
	//global buffer
	public Map<String, Object> mapBuffer= new LinkedHashMap<String, Object>();
	
	private static final Logger logger = Logger.getLogger(AbstractInitiator.class);
	
	public  AbstractInitiator(ParserPropReader cynapseProp, OutputMethodPropReader om)  {
		this.cynapseProp=cynapseProp;
		this.rowCounter=new RowCounter(om.GetLoaSize());
		try {
			KeyVal.PKV_CheckKey(cynapseProp.getPARSER_KEY(), cynapseProp.getPARSER_USERNAME()+cynapseProp.getPARSER_ID());
		} catch (KeyException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}
	
	public synchronized void setDatetimeMap(String datetime){
		if(datetimeMap.get(datetime)==null)
			datetimeMap.put(datetime, "");
	}
	
	public Map<String, String> getDatetimeMap(){
		return datetimeMap;
	}
	
	public DataSource getDs() {
		return ds;
	}

	public void setDs(DataSource ds) {
		this.ds = ds;
	}

	public abstract Object ReadMappingModel();
	
	public void SetMappingOutput(DBTableModel dbTableModel){
		this.dbTableModel=dbTableModel;
	}
	
	public void SetDBFileListWriter(DBFileListWriter dbWriter){
		this.dbWriter=dbWriter;
	}
	public DBFileListWriter getDBFileListWriter() {
		return this.dbWriter;
	}

	public Object getMappingModel() {
		return mappingModel;
	}
	
	public void setMappingModel(Object obj) {
		this.mappingModel=obj;
	}

	public DBTableModel getDBTableModel() {
		return dbTableModel;
	}

	public ParserPropReader getParserProp() {
		return cynapseProp;
	}

	public synchronized boolean CheckDuplicateRows(String s){
		if(mapBuffer.get(s)==null){
			mapBuffer.put(s, 1);
			return false;
		}else{
			logger.error("Found Duplicate :"+s);
			return true;
		}
	}
	
	@SuppressWarnings("unchecked")
	public synchronized void  MergeRows (String s, Map<String, Object> map){
		Map<String, Object>  ret = (Map<String, Object>)mapBuffer.get(s);
		if(ret==null){
			mapBuffer.put(s, map);
		}else{
			ret.putAll(map);
			mapBuffer.put(s,ret);
		}
	}
	
	public synchronized Object  CompareRows (String s, Map<String, Object> map){
		Object  ret = mapBuffer.get(s);
		if(ret==null){
			mapBuffer.put(s, map);
		}else{
//			System.out.println("Found duplicate rows for "+s+":\n"+ret+"\n"+map);
//			logger.error("Found duplicate rows for "+s+":\n"+ret+"\n"+map);
		}
		return ret==null?map:ret;	
	}
	
	public synchronized Map<String, Object>  CompareRowsSum (String s, Map<String, Object> map){
		@SuppressWarnings("unchecked")
		Map<String, Object>  ret = (Map<String, Object>)mapBuffer.get(s);
		if(ret==null){
			mapBuffer.put(s, map);
			ret=map;
		}else{
			for(Map.Entry<String, Object> c : ret.entrySet()){
				String k = c.getKey();
				Object o = map.get(k);
				if(isDouble(c.getValue())){
					Double v = ConvertDouble(c.getValue());
					ret.put(k, String.valueOf(v+ConvertDouble(o)) );
				}
			}
//			System.out.println("Summarizing for "+s+":"+"\n"+map+"\n"+ret+"\n");
		}
		return ret;	
	}
	
	
	public synchronized void  CompareRowsMax (String s, Map<String, Object> map){
		
		@SuppressWarnings("unchecked")
		Map<String, Object>  ret = (Map<String, Object>)mapBuffer.get(s);
		
		if(ret==null){
			mapBuffer.put(s, map);
		}else{
			logger.error("Found Duplicate :"+map.get("FILENAME")+" & "+ret.get("FILENAME")+"|"+s/*+":\n"+map+"\n"+ret*/);
			Set<String> list =new HashSet<String>();
			list.addAll(ret.keySet());
			list.addAll(map.keySet());
			for (String key:list){
				Object o=ret.get(key);
				if(isDouble(o)){
					Double o1=ConvertDouble(o);
					Double o2=ConvertDouble(map.get(key));
					ret.put(key, o1>02?o1.toString():o2.toString());
				}
			}
			mapBuffer.put(s, ret);
		}
	}
	
	private static Double ConvertDouble(Object o){
		try {
			if(o==null)
			     return 0.0;
			 return Double.parseDouble((String)o.toString());
		}catch (NumberFormatException e){
			return 0.0;
		}
	}
	
	private static Boolean isDouble(Object o){
		try {
			if(o==null)
		     return true;
			Double.parseDouble((String)o.toString());
			 return true;
		}catch (NumberFormatException e){
			return false;
		}
	}
}

