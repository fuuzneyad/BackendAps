package id.co.telkom.parser.entity.traversa.ericsson.msscommand;


import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.entity.traversa.common.InitiatorCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;

public class InitiatorC7SPPCommandHandlerFactory implements InitiatorCommandHandlerFactory {
	private static final String command = "C7SPP";
	private final ConfiguredHeader[] header;
	
	public InitiatorC7SPPCommandHandlerFactory() {
		header= new ConfiguredHeader[]{
				new ConfiguredHeader("SP             "),
				new ConfiguredHeader("OWNSP  "),
				new ConfiguredHeader("SPID     "),
				new ConfiguredHeader("LMSG  "),
				new ConfiguredHeader("NET  "),
				new ConfiguredHeader("PREF  "),
				new ConfiguredHeader("MODE ")
		};
	}

	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, Context ctx,  GlobalBuffer buf) {
		return new InitiatorC7SPPCommandHandler(extractor, command, params, buf, ctx, header);
	}
	
}
