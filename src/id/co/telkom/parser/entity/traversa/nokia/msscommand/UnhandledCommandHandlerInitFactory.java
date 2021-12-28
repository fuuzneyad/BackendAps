package id.co.telkom.parser.entity.traversa.nokia.msscommand;

import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.entity.traversa.common.InitiatorCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.ericsson.msscommand.UnhandledCommandHandler;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;

public class UnhandledCommandHandlerInitFactory implements InitiatorCommandHandlerFactory {
	private final String command="*";

	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, Context ctx, GlobalBuffer buf) {
		return new UnhandledCommandHandler(extractor, command, params);
	}
	

}
