package id.co.telkom.parser.entity.pm.telkom;

import java.util.LinkedHashMap;
import java.util.Map;

public class DRModelMap {
	private Map<String, DRModel> modelMap = new LinkedHashMap<String, DRModel>();
	
	public Map<String, DRModel> getModelMap() {
		return modelMap;
	}

	public void putModelMap(String messageId, DRModel model) {
		this.modelMap.put(messageId,model);
	}

	@Override
	public String toString() {
		return "DRModelMap [modelMap=" + modelMap +"]";
	}
	
}
