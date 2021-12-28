package id.co.telkom.parser.entity.pm.zte.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class ZteMeasurementModel {
	private String measurementType, tableName, delimiter, moIdMapping;
	private boolean isUseDelimiter;
	private int lineHeader, columnHeader;
	private int granularityPeriod=0;
	private Map<String, String> fieldMap = new LinkedHashMap<String, String>();
	
	
	public int getGranularityPeriod() {
		return granularityPeriod;
	}
	public void setGranularityPeriod(int granularityPeriod) {
		this.granularityPeriod = granularityPeriod;
	}
	public String getMoIdMapping() {
		return moIdMapping;
	}
	public void setMoIdMapping(String moIdMapping) {
		this.moIdMapping = moIdMapping;
	}
	public String getDelimiter() {
		return delimiter;
	}
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
	public boolean isUseDelimiter() {
		return isUseDelimiter;
	}
	public void setUseDelimiter(boolean isUseDelimiter) {
		this.isUseDelimiter = isUseDelimiter;
	}
	public int getLineHeader() {
		return lineHeader;
	}
	public void setLineHeader(int lineHeader) {
		this.lineHeader = lineHeader;
	}
	public int getColumnHeader() {
		return columnHeader;
	}
	public void setColumnHeader(int columnHeader) {
		this.columnHeader = columnHeader;
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
	@Override
	public String toString() {
		return "DelimitedMeasurementModel [measurementType=" + measurementType
				+ ", tableName=" + tableName + ", isUseDelimiter="
				+ isUseDelimiter + ", lineHeader=" + lineHeader
				+ ", columnHeader=" + columnHeader + ", fieldMap=" + fieldMap
				+ "]";
	}
	
}
