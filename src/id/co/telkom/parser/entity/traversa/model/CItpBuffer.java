package id.co.telkom.parser.entity.traversa.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CItpBuffer {
	//--NE			   
	//	--map(AsName, dpc)
	Map<String, Map<String, Object>> cs7as = new LinkedHashMap<String, Map<String,Object>>();
	//--NE			   
	//	--map(cs7poincode,status)
	Map<String, Map<String, Object>> cs7PointCode = new LinkedHashMap<String, Map<String,Object>>();
	//--NE
	//--List of Counter
	Map<String, List<Map<String, Object>>> appGroups = new LinkedHashMap<String, List<Map<String,Object>>>();
	
	public Map<String, List<Map<String, Object>>> getAppGroups(){
		return appGroups;
	}
	public List<Map<String, Object>> getListAppGroups(String ne){
		return appGroups.get(ne);
	}
	public void setAppGroups(String ne, Map<String, Object> map){
		List<Map<String, Object>> data =appGroups.get(ne);
		data = data==null ? new ArrayList<Map<String,Object>>():data;
		data.add(map);
		appGroups.put(ne, data);
	}
	
	public void setCs7as(String ne, String  asName, String dpc){
		Map<String, Object> m = cs7as.get(ne);
		m = m==null ? new LinkedHashMap<String, Object>() : m;
		m.put(asName, dpc);
		cs7as.put(ne, m);
	}
	
	public void setCsPointCode(String ne, String pc, String  status){
		Map<String, Object> m = cs7PointCode.get(ne);
		m = m==null ? new LinkedHashMap<String, Object>() : m;
		m.put(pc, status);
		cs7as.put(ne, m);
	}
	
	public Map<String, Object> getCs7as(String ne){
		return cs7as.get(ne);
	}
	
	public Map<String, Object> getCs7PointCode(String ne){
		return cs7PointCode.get(ne);
	}

	public Map<String, Map<String, Object>> getCs7as() {
		return cs7as;
	}

	public Map<String, Map<String, Object>> getCs7PointCode() {
		return cs7PointCode;
	}
	
}
