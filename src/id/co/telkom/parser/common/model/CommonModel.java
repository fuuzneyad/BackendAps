package id.co.telkom.parser.common.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CommonModel implements Serializable {
	private static final long serialVersionUID = -4638648470506311656L;
	private String name;
	private Map<String, Object> values;
	protected CommonModel(String name, Map<String, Object> values) {
		this.name = name;
		this.values = values;
	}
	public CommonModel() {
		this.values = new HashMap<String, Object>();
	}
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append(values);
		return sb.toString();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map<String, Object> getValues() {
		return values;
	}
	public void put(String name, Object value) {
		this.values.put(name, value);		
	}
	public Object get(String name) {
		return this.values.get(name);		
	}
	public Long getId() {
		return ((Long) values.get("ID")).longValue();
	}
	public void setId(Long id) {
		values.put("ID", Long.valueOf(id));
	}
	public int getOrder() {
		Integer value = ((Integer) values.get("ORDER_NUM"));
		return value == null ? -1 : value.intValue();
	}
	public void setOrder(int order) {
		if (order != -1)
			values.put("ORDER_NUM", Integer.valueOf(order));
	}
	
}
