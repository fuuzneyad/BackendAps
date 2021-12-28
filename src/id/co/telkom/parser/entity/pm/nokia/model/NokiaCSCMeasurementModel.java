package id.co.telkom.parser.entity.pm.nokia.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class NokiaCSCMeasurementModel {
	private String measurementType, tableName,moIdMapping;
	private Map<String, String> fieldMap = new LinkedHashMap<String, String>();
	private boolean isTableBuffered=false;
	
	public boolean isTableBuffered() {
		return isTableBuffered;
	}
	public void setTableBuffered(boolean isTableBuffered) {
		this.isTableBuffered = isTableBuffered;
	}
	public String getMeasurementType() {
		return measurementType;
	}
	public void setMeasurementType(String measurementType) {
		this.measurementType = measurementType;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public Map<String, String> getFieldMap() {
		return fieldMap;
	}
	public void setFieldMap(Map<String, String> fieldMap) {
		this.fieldMap = fieldMap;
	}
	public String getMoIdMapping() {
		return moIdMapping;
	}
	public void setMoIdMapping(String moIdMapping) {
		this.moIdMapping = moIdMapping;
	}
	@Override
	public String toString() {
		return "StandardMeasurementModel [measurementType=" + measurementType
				+ ", tableName=" + tableName + ", fieldMap=" + fieldMap + "]";
	}
	
}
