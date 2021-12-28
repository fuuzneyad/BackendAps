package id.co.telkom.parser.entity.traversa.siemens.command;


import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.CommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserDISPMECommandHandlerFactory implements CommandHandlerFactory {
	private static final String command = "DISPME";
	private final ConfiguredHeader[] headers;
	
	public ParserDISPMECommandHandlerFactory(){
		headers = new ConfiguredHeader[]{
				new ConfiguredHeader("ME_ID","Managed element ID   |".length()+20,"Managed element ID   |".length()),
				new ConfiguredHeader("VENDOR_NAME", "Vendor name          |".length()+20,"Vendor name          |".length()),
				new ConfiguredHeader("SYSTEM_TIME", "System time          | ".length()+20,"System time          | ".length())
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
		return new ParserDISPMECommandHandler(extractor, command, params, ctx, headers, listener, cynapseInit);
	}

	@Override
	public String getTableSchema() {
		return "";
	}
	

}
