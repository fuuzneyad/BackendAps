package id.co.telkom.parser.entity.pm.siemens.model;

import java.util.LinkedHashMap;
import java.util.Map;
public class StructuredSiemensModel {
	/*
	 * Pattern
	 * 	 - table
	 * 	   - moId
	 * 		 - objAdj
	 * 		   - map
	 * */
	private Map<String,Map<String,Map<String,Map<String,Object>>>> structuredModel;
	
	public StructuredSiemensModel(){
		structuredModel = new  LinkedHashMap<String,Map<String,Map<String,Map<String,Object>>>>();
	}
	
	public Map<String,Map<String,Map<String,Map<String,Object>>>> getStructuredModelSiemens(){
		return structuredModel;
	}
	
	public void setMap(String tableName, String moId, String objAdj, Map<String, Object> m){
		Map<String,Map<String,Map<String,Object>>> tableMap = structuredModel.get(tableName);
		if (tableMap==null)
			tableMap=new LinkedHashMap<String,Map<String,Map<String,Object>>>();
		Map<String, Map<String, Object>> moMap= tableMap.get(moId);
		if(moMap==null)
			moMap = new LinkedHashMap<String, Map<String,Object>>();
		Map<String,Object> map = moMap.get(objAdj);
		if (map==null)
			map = new LinkedHashMap<String, Object>();
		
		map.putAll(m);
		moMap.put(objAdj, map);
		tableMap.put(moId, moMap);
		structuredModel.put(tableName,tableMap);
	}
	
}
