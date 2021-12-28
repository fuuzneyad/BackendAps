package id.co.telkom.parser.entity.traversa.nokia.sgsncommand;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserWVICommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "ZWVI";
	private final ConfiguredHeader[] headers;
	
	public ParserWVICommandHandlerFactory() {
		headers = new ConfiguredHeader[]{
				new ConfiguredHeader("MOBILE_COUNTRY_CODE",5),
				new ConfiguredHeader("MOBILE_NETWORK_CODE",5),
				new ConfiguredHeader("PLMN_DEFAULT_CHARGING_AREA",5),
				new ConfiguredHeader("CORE_NETWORK_NODE_IDTFR",5),
				new ConfiguredHeader("OPERATOR_SERVICE_NUMBER_IMSI",5),	//batas
				new ConfiguredHeader("TYPE", 15),	 
				new ConfiguredHeader("NUMBER              "),	 
				new ConfiguredHeader("NP        "),
				new ConfiguredHeader("TON                ")

			};
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
			return new ParserWVICommandHandler( extractor, listener, getCommand(), params,headers,cynapseInit);
	}
	
	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return null;
	}
	
	@Override
	public String getTableSchema() {
		return "";
	}
	
}
