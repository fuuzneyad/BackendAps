package id.co.telkom.parser.entity.traversa.siemens.command;


import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.CommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserDISPLTUCommandHandlerFactory implements CommandHandlerFactory {
	private static final String command = "DISPLTU";
	private final ConfiguredHeader[] headers;
	
	public ParserDISPLTUCommandHandlerFactory(){
		headers = new ConfiguredHeader[]{
				new ConfiguredHeader("LTG     "),
				new ConfiguredHeader("LTU  "),
				new ConfiguredHeader("TYPE  "),
				new ConfiguredHeader("APPLIC  "),
				new ConfiguredHeader("MODVAR", 50 ,"MODVAR ".length())
		};
	}

	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return null;
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx,
			AbstractInitiator cynapseInit) {
		return new ParserDISPLTUCommandHandler(extractor, command, params, ctx, headers, listener, cynapseInit);
	}

	@Override
	public String getTableSchema() {
		return "";
	}
	

}
