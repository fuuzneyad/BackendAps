package id.co.telkom.parser.entity.traversa.siemens.command;


import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.CommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserDISPSPENSCommandHandlerFactory implements CommandHandlerFactory {
	private static final String command = "DISPSPENS";
	private final ConfiguredHeader[] headers;
	
	public ParserDISPSPENSCommandHandlerFactory(){
		headers = new ConfiguredHeader[]{
				new ConfiguredHeader(" SPENS_ID "," SPENS ID ".length()),
				new ConfiguredHeader(" SPENS_NAME "," SPENS Name ".length()),
				new ConfiguredHeader("PRIMARY_1_ID"," Primary 1 ID ".length()),
				new ConfiguredHeader("PRIMARY_1_NAME"," Primary 1 Name ".length()),
				new ConfiguredHeader("PRIMARY_2_ID"," Primary 2 ID ".length()),
				new ConfiguredHeader("PRIMARY_2_NAME"," Primary 2 Name ".length()),
				new ConfiguredHeader("SHARED_MODE"," Sharing Mode     ".length())
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
		return new ParserDISPSPENSCommandHandler(extractor, command, params, ctx, headers, listener, cynapseInit);
	}

	@Override
	public String getTableSchema() {
		return "";
	}
	

}
