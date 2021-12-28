package id.co.telkom.parser.common.loader;

import java.util.LinkedHashMap;
import java.util.Map;


public class HdfsMapper {
	private Map<String, Object> mapper = new LinkedHashMap<String, Object>();
	
	public HdfsMapper(){
	}

	public Map<String, Object> getMapper() {
		return mapper;
	}

	public void setMapper(Map<String, Object> mapper) {
		this.mapper = mapper;
	}
	
}
