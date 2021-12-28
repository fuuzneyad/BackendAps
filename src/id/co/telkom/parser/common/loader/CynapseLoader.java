package id.co.telkom.parser.common.loader;

import java.util.Map;

import id.co.telkom.parser.common.model.Context;

// to be implemented someday..
public interface CynapseLoader {
	public void onBeginFile() ;
	public void onEndFile();
	public void onNewTable(String tableName, Context context);
	public void onEndTable();
	public void onParserEnded(Context context);
	public void onReadyModel( Map<String, Object> map, Context context);
	//public void onRouteModel( Map<String, Object> map, Context context);
}
