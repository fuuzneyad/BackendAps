package id.co.telkom.parser.entity.pm.ericsson.model;

import java.util.LinkedHashMap;
import java.util.Map;
public class StructuredEricsson3GModel {
	/*structuredModel
	 * Pattern :
	 * 	 - table
	 * 	   - map
	 * */
	
	/*structuredModel2
	 * Pattern :
	 * 	 - table=(table|MO)
	 * 	   - map
	 * */
	public Map<String,Map<String,Object>> structuredModel2;
	private Map<String,Map<String,Object>> structuredModel;
	
	public StructuredEricsson3GModel(){
		structuredModel = new  LinkedHashMap<String,Map<String,Object>>();
		structuredModel2 = new  LinkedHashMap<String,Map<String,Object>>();
	}
	
	public Map<String,Map<String,Object>> getStructuredModelEri(){
		return structuredModel;
	}
	
	public void setMap(String tableName, String param, String value){
		Map<String,Object> map = structuredModel.get(tableName);
		if (map==null)
			map=new LinkedHashMap<String,Object>();
		map.put(param, value);
		structuredModel.put(tableName, map);
	}
	
	public void putMap(String mergedObjects, String param, String value){
		Map<String,Object> map = structuredModel2.get(mergedObjects);
		if (map==null)
			map=new LinkedHashMap<String,Object>();
		map.put(param, value);
		structuredModel2.put(mergedObjects, map);
	}
}
