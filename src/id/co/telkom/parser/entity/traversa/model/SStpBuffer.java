package id.co.telkom.parser.entity.traversa.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class SStpBuffer {
	Map<String, Map<String, Object>> spLnkRem = new LinkedHashMap<String, Map<String,Object>>();
	Map<String, Map<String, Object>> spenHS = new LinkedHashMap<String, Map<String,Object>>();
	
	public void setSpLnkRem(String ne, String  spLnkRemName, String dpc){
		Map<String, Object> m = spLnkRem.get(ne);
		m = m==null ? new LinkedHashMap<String, Object>() : m;
		m.put(spLnkRemName, dpc);
		spLnkRem.put(ne, m);
	}
	
	public Map<String, Object> getSpLnkRem(String ne){
		return spLnkRem.get(ne);
	}
	
	public Object getSpLnkRemDpc(String ne, String spLnkRemName){
		Map<String, Object> o = spLnkRem.get(ne);
		return o==null ? "0" : o.get(spLnkRemName);
	}
	
	public void setspenHS(String ne, String  spenHSName){
		Map<String, Object> m = spenHS.get(ne);
		m = m==null ? new LinkedHashMap<String, Object>() : m;
		m.put(spenHSName, "-");
		spenHS.put(ne, m);
	}
	
	public Map<String, Object> getspenHS(String ne){
		return spenHS.get(ne);
	}
	
	public Map<String, Map<String, Object>> getSpenHSs(){
		return spenHS;
	}
}
