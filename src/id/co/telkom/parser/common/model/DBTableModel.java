package id.co.telkom.parser.common.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class DBTableModel {
	/*
	 * -- Table
	 * 	-- Column
	 * 		-- MetaData
	 * */
		
	public  Map<String, Map<String,DBMetadata>> tableModel = new LinkedHashMap<String, Map<String,DBMetadata>>();
	
	public void PutModel(String tableName, String columnName, DBMetadata meta){
		Map<String,DBMetadata> columnMeta=tableModel.get(tableName);
		if(columnMeta==null)
			columnMeta = new LinkedHashMap<String,DBMetadata>();
		
		columnMeta.put(columnName, meta);
		tableModel.put(tableName, columnMeta);
	}
	
}
