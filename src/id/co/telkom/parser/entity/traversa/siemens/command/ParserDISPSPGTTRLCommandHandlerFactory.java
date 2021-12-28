package id.co.telkom.parser.entity.traversa.siemens.command;


import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.CommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserDISPSPGTTRLCommandHandlerFactory implements CommandHandlerFactory {
	private static final String command = "DISPSPGTTRL";
	private final ConfiguredHeader[][] headers;
	
	public ParserDISPSPGTTRLCommandHandlerFactory(){
		headers = new ConfiguredHeader[][]{
				new ConfiguredHeader[]{
					new ConfiguredHeader("SPGTTRL_ID"," SPGTTRL ID ".length()),
					new ConfiguredHeader("SPGTTRL_Name".toUpperCase()," SPGTTRL Name ".length()),
					new ConfiguredHeader(" NP                       "),
					new ConfiguredHeader(" TTID                            "), 
					new ConfiguredHeader(" NA                 "),
					new ConfiguredHeader("Base_Partition".toUpperCase()," Base Partition ".length())
				}
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
		return new ParserDISPSPGTTRLCommandHandler(extractor, command, params, ctx, headers, listener, cynapseInit);
	}

	@Override
	public String getTableSchema() {
		return "";
	}
	

}
