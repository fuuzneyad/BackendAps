package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserANESPCommandHandlerFactory  implements MscCommandHandlerFactory {
	public static final String command = "ANESP";
	private final  ConfiguredHeader[] headers;
	
	public ParserANESPCommandHandlerFactory(){
		headers = new ConfiguredHeader[]{
				new ConfiguredHeader("ES     "),
				new ConfiguredHeader("BE          ", false, 100),
				new ConfiguredHeader("M                          "), //F/N error in database
				new ConfiguredHeader("EOSRES")
				};
		
	}
	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return new ParserANESPCommandHandler(extractor, listener, command, params, headers, null);
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx,
			AbstractInitiator cynapseInit) {
		return new ParserANESPCommandHandler(extractor, listener, command, params, headers, cynapseInit);
	}

	@Override
	public String getTableSchema() {
		return null;
	}

}
