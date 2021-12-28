package id.co.telkom.parser.entity.traversa.ericsson.msscommand;


import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.entity.traversa.common.InitiatorCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;

public class InitiatorMGCAPCommandHandlerFactory implements InitiatorCommandHandlerFactory {
	private static final String command = "MGCAP";
	private final ConfiguredHeader[] header;
	
	public InitiatorMGCAPCommandHandlerFactory() {
		header= new ConfiguredHeader[]{
				new ConfiguredHeader("INT                    "),
				new ConfiguredHeader("NAT             ")
		};
	}

	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, Context ctx,  GlobalBuffer buf) {
		return new InitiatorMGCAPCommandHandler(extractor, command, params, buf, ctx, header);
	}
	
}
