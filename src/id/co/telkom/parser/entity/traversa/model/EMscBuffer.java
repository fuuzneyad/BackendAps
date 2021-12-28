package id.co.telkom.parser.entity.traversa.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EMscBuffer {
	//--NE			   
	//	--map(MGG,MG)
	Map<String, Map<String, Object>> nrgwp = new LinkedHashMap<String, Map<String,Object>>();
	//--NE			   
	//	--map(SP,SPID)
	Map<String, Map<String, Object>> c7spp = new LinkedHashMap<String, Map<String,Object>>();
	//--NE
	//--List of Counter
	Map<String, List<Map<String, Object>>> exrop = new LinkedHashMap<String, List<Map<String,Object>>>();
	
	public Map<String, List<Map<String, Object>>> getExrop(){
		return exrop;
	}
	public List<Map<String, Object>> getListExrop(String ne){
		return exrop.get(ne);
	}
	public void setExrop(String ne, Map<String, Object> map){
		List<Map<String, Object>> data =exrop.get(ne);
		data = data==null ? new ArrayList<Map<String,Object>>():data;
		data.add(map);
		exrop.put(ne, data);
	}
	public void setNrgwp(String ne, String MGG, String  MG){
		Map<String, Object> m = nrgwp.get(ne);
		m = m==null? new LinkedHashMap<String, Object>():m;
		m.put(MGG, MG);
		nrgwp.put(ne, m);
	}
	public void setC7spp(String ne, String SP, String  SPID){
		Map<String, Object> m = c7spp.get(ne);
		m = m==null? new LinkedHashMap<String, Object>():m;
		m.put(SP, SPID);
		c7spp.put(ne, m);
	}
	public Map<String, Object> getNrgwp(String ne){
		return nrgwp.get(ne);
	}
	public Map<String, Object> getC7spp(String ne){
		return c7spp.get(ne);
	}
}
