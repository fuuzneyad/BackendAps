package id.co.telkom.parser.entity.traversa.ericsson;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.CommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class UnhandledSgsnCommandHandlerFactory implements CommandHandlerFactory {
	private final String command="*";

	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return new UnhandledSgsnCommandHandler(extractor, command, params);
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx,
			AbstractInitiator cynapseInit) {
		return new UnhandledSgsnCommandHandler(extractor, command, params);
	}

	@Override
	public String getTableSchema() {
		return null;
	}

	

}
