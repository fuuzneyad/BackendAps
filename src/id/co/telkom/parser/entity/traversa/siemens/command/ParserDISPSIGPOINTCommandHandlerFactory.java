package id.co.telkom.parser.entity.traversa.siemens.command;


import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.CommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserDISPSIGPOINTCommandHandlerFactory implements CommandHandlerFactory {
	private static final String command = "DISPSIGPOINT";
	private final ConfiguredHeader[] headers;
	
	public ParserDISPSIGPOINTCommandHandlerFactory(){
		headers = 	new ConfiguredHeader[]{
				new ConfiguredHeader("NR"," Nr. ".length()),
				new ConfiguredHeader("NET_NAME"," Net name       ".length()),
				new ConfiguredHeader("NET_ID"," Net ID   ".length()),
				new ConfiguredHeader("NET_INDICATOR"," Net indicator  ".length()+10," Net indicator  ".length()),
				new ConfiguredHeader(" SPC           "), 
				new ConfiguredHeader("OP_STATE"," Operational ".length()),
				new ConfiguredHeader(" ALARM_STATUS   "),
				new ConfiguredHeader(" ALARM_PROFILE_MP  ", " Alarm Profile  ".length())
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
		return new ParserDISPSIGPOINTCommandHandler(extractor, command, params, ctx, headers, listener, cynapseInit);
	}

	@Override
	public String getTableSchema() {
		return "";
	}
	

}
