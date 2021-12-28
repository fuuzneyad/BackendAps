package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserMGRIPCommandHandlerFactory  implements MscCommandHandlerFactory {
	public static final String command = "MGRIP";
	private final ConfiguredHeader[] headers;
	
	public ParserMGRIPCommandHandlerFactory(){
		headers = new ConfiguredHeader[] {
				new ConfiguredHeader("RNC      "),
				new ConfiguredHeader("RNCID         "),
				new ConfiguredHeader("R1       "),
				new ConfiguredHeader("R2       "),
				new ConfiguredHeader("GLCNID     "),

				new ConfiguredHeader("RNCCODEC                                           "),
				new ConfiguredHeader("TB  ")
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
		return new ParserMGRIPCommandHandler(extractor, listener, command, params, headers, cynapseInit);
	}

	@Override
	public String getTableSchema() {
		return null;
	}

}
