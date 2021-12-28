package id.co.telkom.parser.entity.dashboard.mkios.model;

import java.util.Map;

public class MkiosMeasurementModel {
	private String sheetName, moIdMapping;
	private String sheetId;
	private Map<String, MkiosTableMappingMeasurementModel> sheetTableMap;
	
	public Map<String, MkiosTableMappingMeasurementModel> getSheetTableMap() {
		return sheetTableMap;
	}
	public void setSheetTableMap(
			Map<String, MkiosTableMappingMeasurementModel> sheetTableMap) {
		this.sheetTableMap = sheetTableMap;
	}
	public String getSheetName() {
		return sheetName;
	}
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	public String getMoIdMapping() {
		return moIdMapping;
	}
	public void setMoIdMapping(String moIdMapping) {
		this.moIdMapping = moIdMapping;
	}
	public String getSheetId() {
		return sheetId;
	}
	public void setSheetId(String sheetId) {
		this.sheetId = sheetId;
	}
	
	
}
