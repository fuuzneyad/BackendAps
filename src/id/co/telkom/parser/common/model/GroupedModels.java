package id.co.telkom.parser.common.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GroupedModels {
	
	public Map<String, List<Map<String, Object>>> groupedModel;
	
	public GroupedModels(){
		groupedModel = new LinkedHashMap<String, List<Map<String, Object>>>();
	}
		
	public void PutIt(CommonModel mdl){
		String name =mdl.getName();
		if (mdl!=null && name!=null){
			List<Map<String, Object>> lst = groupedModel.get(name);
			
			if(lst!=null)
				lst.add(mdl.getValues());
			else{
				lst = new ArrayList<Map<String, Object>>();
				lst.add(mdl.getValues());
			}
			groupedModel.put(name, lst);
		}
	}
	
//	public void MergeIt(CommonModel mdl){
//		String name =mdl.getName();
//		if (mdl!=null && name!=null){
//			List<Map<String, Object>> lst = groupedModel.get(name);
//			
//			if(lst!=null)
//				lst.add(mdl.getValues());
//			else{
//				lst = new ArrayList<Map<String, Object>>();
//				lst.add(mdl.getValues());
//			}
//			
//			groupedModel.put(name, lst);
//		}
//	}

//	public Map<String, Object>[] GetIt(String name){
//		List<Map<String, Object>> list = groupedModel.get(name);
//		Map<String, Object>[] sss = list.toArray(new LinkedHashMap[0]);
//		return sss;
//	}
	
}
