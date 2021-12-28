package id.co.telkom.parser.entity.traversa.siemens.command;


import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.CommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserDISPSPGTCRULCommandHandlerFactory implements CommandHandlerFactory {
	private static final String command = "DISPSPGTCRUL";
	private final ConfiguredHeader[] headers;
	
	public ParserDISPSPGTCRULCommandHandlerFactory(){
		headers = 	new ConfiguredHeader[]{
				new ConfiguredHeader("SPGTCRUL_ID"," ID   ".length()),
				new ConfiguredHeader("SPGTCRUL_NAME"," Name         ".length()),
				new ConfiguredHeader("CONVERSION_RULE"," Conversion Rule                                  ".length()),
				new ConfiguredHeader("NP", " NP                 ".length()+10," NP                 ".length()), 
				new ConfiguredHeader(" NA            "),
				new ConfiguredHeader(" TTID                      ")
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
		return new ParserDISPSPGTCRULCommandHandler(extractor, command, params, ctx, headers, listener, cynapseInit);
	}

	@Override
	public String getTableSchema() {
		return "";
	}
	

}
