package id.co.telkom.parser.entity.traversa.ericsson.sgsncommand;

import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserGSHLISTServiceAddressCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "GSH_LIST_IP_SERVICE_ADDRESS";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();

	public ParserGSHLISTServiceAddressCommandHandlerFactory() {
		headersMap.put("MASTER", new ConfiguredHeader[]{
				new ConfiguredHeader(" A       "),
				new ConfiguredHeader("SN     "),
				new ConfiguredHeader("IP     ")
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
		return new ParserGSHLISTServiceAddressCommandHandler( extractor, listener, getCommand(), params,headersMap, cynapseInit);
	}
	
	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return new ParserGSHLISTServiceAddressCommandHandler( extractor, listener, getCommand(), params, headersMap, null);
	}
	
	@Override
	public String getTableSchema() {
		return "";
	}
	
}
