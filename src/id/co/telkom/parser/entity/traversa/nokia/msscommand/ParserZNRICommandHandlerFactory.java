package id.co.telkom.parser.entity.traversa.nokia.msscommand;

import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserZNRICommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "ZNRI";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();

	public ParserZNRICommandHandlerFactory() {
		headersMap.put("MASTER", new ConfiguredHeader[]{
				new ConfiguredHeader("NET  "),
				new ConfiguredHeader("SP_CODE_H_D         "),
				new ConfiguredHeader("NAME              "),
				new ConfiguredHeader(" RS_STATE "),
				new ConfiguredHeader(" PAR_SET ")
		});
		headersMap.put("ROUTES", new ConfiguredHeader[]{
				new ConfiguredHeader("ROUTES_SP_CODE_H_D          "),
				new ConfiguredHeader("ROUTE_NAME        "),
				new ConfiguredHeader(" STATE  "),
				new ConfiguredHeader(" PRIO ")
		});
	}

	@Override
	public String getCommand() {
		return command;
	}
	
	public String[] getTableName(){
		return new String[] {command};
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx, AbstractInitiator cynapseInit) {
		return new ParserZNRICommandHandler( extractor, listener, getCommand(), params,headersMap,cynapseInit);
	}
	
	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return new ParserZNRICommandHandler( extractor, listener, getCommand(), params,headersMap, null);
	}
	
	@Override
	public String getTableSchema() {
		return "";
	}
	
}
