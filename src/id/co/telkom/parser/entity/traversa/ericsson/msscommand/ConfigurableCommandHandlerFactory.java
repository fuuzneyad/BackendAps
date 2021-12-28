package id.co.telkom.parser.entity.traversa.ericsson.msscommand;


import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ConfigurableCommandHandlerFactory extends AbstractCommandHandlerFactory {
	private final ConfiguredHeader[] headers;
	
	public ConfigurableCommandHandlerFactory(String command, int skipLine, ConfiguredHeader[] headers) {
		super(command,skipLine);
		this.headers=headers;
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return new ConfigurableCommandHandler(extractor, listener, command, params, headers, null, getSkipLines());
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx,
			AbstractInitiator cynapseInit) {
		return new ConfigurableCommandHandler(extractor, listener, command, params, headers, cynapseInit, getSkipLines());
	}

	@Override
	public String getTableSchema() {
		return null;
	}

}
