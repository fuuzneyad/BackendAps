package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserMGOCPCommandHandlerFactory implements MscCommandHandlerFactory{
	private static final String command = "MGOCP";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	
	public ParserMGOCPCommandHandlerFactory() {
		headersMap.put("FIRST", 
				new ConfiguredHeader[] {
				new ConfiguredHeader("CELL     "),
				new ConfiguredHeader("AREAID              "),
				new ConfiguredHeader("MSC      "),
				new ConfiguredHeader("NCS")
			}
		);
		headersMap.put("SECOND", 
				new ConfiguredHeader[] {
				new ConfiguredHeader("CELL     "),
				new ConfiguredHeader("AREAID              "),
				new ConfiguredHeader("MSCG    "),
				new ConfiguredHeader("NCS")
			}
		);
	}
	
	
	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return command;
	}
	
	public String[] getTableName(){
		return new String[] {command};
	}
	
	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx,
			AbstractInitiator cynapseInit) {
		// TODO Auto-generated method stub
	return new ParserMGOCPCommandHandler( extractor, listener, command, params,headersMap,cynapseInit);
	}
	
	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getTableSchema() {
		// TODO Auto-generated method stub
		return null;
	}

}
