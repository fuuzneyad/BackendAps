package id.co.telkom.parser.entity.cli.ericsson;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.AbstractTableCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;


public class ConfigurableTableCommandHandlerFactory extends AbstractTableCommandHandlerFactory {
	private final ConfiguredHeader[] headers;
	private int skipLines;

	public ConfigurableTableCommandHandlerFactory(String command, int skipLines, ConfiguredHeader[] headers) {
		super(command, skipLines);
		this.headers = headers;
		this.skipLines=skipLines;
	}

	public ConfiguredHeader[] getHeaders() {
		return headers;
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return new ConfigurableTableCommandHandler(ctx,extractor, listener, command, params, headers, skipLines);
	}
	
	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx, AbstractInitiator cynapseInit) {
		return new ConfigurableTableCommandHandler(ctx,extractor, listener, command, params, headers, skipLines);
	}

	@Override
	public String getTableSchema() {
		StringBuilder current = new StringBuilder();
		{
			current.append("CREATE TABLE ").append(getCommand()).append(" (\n");
			current.append("\tENTRY_DATE TIMESTAMP DEFAULT NOW(),\n");
			current.append("\tNE_ID VARCHAR(9),\n");
			current.append("\tCOMMAND_PARAM VARCHAR(50),\n");
			current.append("\tLINE BIGINT(9),\n");

		}
		
		for (ConfiguredHeader configuredHeader : headers) {
			current.append("\t"+configuredHeader.getName()).append(' ').append("VARCHAR(").append(configuredHeader.getDbLength()).append("),\n");
		}
		current.setLength(current.length() - 2);
		
		current.append("\n);\r\n");
		return current.toString();
	}
}
