package id.co.telkom.parser.common.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class StandardMeasurementModel {
	private String measurementType, tableName,moIdMapping;
	private Map<String, String> fieldMap = new LinkedHashMap<String, String>();
	
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
