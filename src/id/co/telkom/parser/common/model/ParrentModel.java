package id.co.telkom.parser.common.model;

import java.util.Map;

import  java.sql.Timestamp;


public class ParrentModel extends CommonModel{
	private static final long serialVersionUID = -6193421255518783960L;
	public ParrentModel(String name, Map<String, Object> values) {
		super(name, values);
	}
	public void SetEntryDate(Timestamp ts){
		put("ENTRY_DATE", ts);		
	}
	public void SetTimestampDatetTime(Timestamp ts){
		put("DATETIME_ID", ts);		
	}
	public void SetSource(String oss){
		put("SOURCE_ID", oss);		
	}
	public void SetDateTimeId(String datetime){
		put("DATETIME_ID", datetime);
	}
	public void SetNeId(String neid){
		put("NE_ID", neid);
	}
	public void SetSubNeId(String subNeid){
		put("SUB_NE_ID", subNeid);
	}
	public void SetMoId(String moid){
		put("MO_ID", moid);
	}
	public void SetHashValue(String hash){
		put("HASH_VAL", hash);
	}
	
	public void SetGranularity(int glan){
		put("GRANULARITY", glan);
	}
	public void SetVersion(String version){
		put("VERSION", version);
	}
}
