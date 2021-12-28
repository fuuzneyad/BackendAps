package id.co.telkom.parser.common.model;

import java.util.Map;

import org.apache.log4j.Logger;

import id.co.telkom.parser.common.charparser.ParserException;
import id.co.telkom.parser.entity.traversa.common.EdgeContext;


public class DataListener {
	private static final Logger logger = Logger.getLogger(DataListener.class);
	
	//data Listener to be Override
	public void onReadyData(Context ctx, Map<String, Object> map, int line){
	}

	public void onTopologyData(Context ctx, EdgeContext edctx){
	}
	
	public void onBeginCommand(Context ctx, String command, String param, int line){
		logger.info("Begin command:["+line+"]"+command+":"+param);
	}
	
	public void onEndCommand(Context ctx, String command, String param, int line){
		logger.info("End command:["+line+"]"+command+":"+param);
	}
	
	public void onError(int line, Context ctx, int column, String string) {
		logger.error("["+line+","+column+"]"+ctx+":"+string);
	}

	public void onParseError(int line, Context ctx, int column,
			String string, ParserException ex) {
		logger.error("["+line+","+column+"]"+ctx+":"+string);
		
	}

	public void onNodeElement(int line, Context ctx, String nodeElement,
			String desc) {
		logger.info("NE:"+nodeElement+" "+desc);
		
	}

	public void onBeginTable(int line, Context ctx) {
		logger.info("Begin Table:["+line+"]"+ctx.t_name);
	}

	public void onTableHeader(int line, Context ctx,
			ConfiguredHeader[] headers) {
		logger.info("Table Header:["+line+"]"+headers);
	}
}
