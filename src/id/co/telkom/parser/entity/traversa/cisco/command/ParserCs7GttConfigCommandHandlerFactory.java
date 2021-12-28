package id.co.telkom.parser.entity.traversa.cisco.command;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserCs7GttConfigCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "CS7_GTT_CONFIG";
	private ConfiguredHeader[] headers;
	
	public ParserCs7GttConfigCommandHandlerFactory() {
	}

	@Override
	public String getCommand() {
		return command;
	}
	

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx, 
			AbstractInitiator cynapseInit) {
			return new ParserCs7GttConfigCommandHandler( extractor, listener, command, params,headers,cynapseInit);
	}
	
	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return null;
	}
	
	@Override
	public String getTableSchema() {
		return null;
	}

	
	
}
