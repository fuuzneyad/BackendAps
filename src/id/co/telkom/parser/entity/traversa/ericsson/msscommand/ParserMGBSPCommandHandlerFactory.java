package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserMGBSPCommandHandlerFactory  implements MscCommandHandlerFactory {
	public static final String command = "MGBSP";
	private final ConfiguredHeader[] headers;
	
	public ParserMGBSPCommandHandlerFactory(){
		headers = new ConfiguredHeader[] {
				new ConfiguredHeader("BSC               "),
				new ConfiguredHeader("R1       "),
				new ConfiguredHeader("R2       "),
				new ConfiguredHeader("MGG       "),
				new ConfiguredHeader("TB        "),
				new ConfiguredHeader("RPBSD"),
				
				new ConfiguredHeader("BSCDATA", 100,"BSCDATA".length()),
				new ConfiguredHeader("BSCCODEC"),
				new ConfiguredHeader("BSCID"),
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
		return new ParserMGBSPCommandHandler(extractor, listener, command, params, headers, cynapseInit);
	}

	@Override
	public String getTableSchema() {
		return null;
	}

}
