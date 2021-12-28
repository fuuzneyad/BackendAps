package id.co.telkom.parser.entity.traversa.ericsson.msscommand;


import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class UnhandledCommandHandlerFactory implements MscCommandHandlerFactory{

	private final String command;
	
	public UnhandledCommandHandlerFactory(String command) {
		this.command = command;
	}


	public String getCommand() {
		return command;
	}


	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx, AbstractInitiator cynapseInit) {
		return new UnhandledCommandHandler(extractor, command,params);
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return null;
	}
	
	@Override
	public String getTableSchema() {
		return "";
	}

}
