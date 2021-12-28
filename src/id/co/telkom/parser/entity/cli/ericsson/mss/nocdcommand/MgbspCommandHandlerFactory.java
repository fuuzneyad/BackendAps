package id.co.telkom.parser.entity.cli.ericsson.mss.nocdcommand;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class MgbspCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "MGBSP";
	private final ConfiguredHeader[] headers;
	
	public MgbspCommandHandlerFactory() {
		headers = new ConfiguredHeader[] {
				new ConfiguredHeader("BSC               "),
				new ConfiguredHeader("R1       "),
				new ConfiguredHeader("R2       "),
				new ConfiguredHeader("MGG       "),
				new ConfiguredHeader("TB        "),
				new ConfiguredHeader("RPBSD"),
				
				new ConfiguredHeader("BSCDATA", 100,"BSCDATA".length()),
				new ConfiguredHeader("BSCCODEC"),
				new ConfiguredHeader("BSCID"),
			};
	}

	@Override
	public String getCommand() {
		return command;
	}
	
	public String[] getTableName(){
		return new String[] {command};
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
			return new MgbspCommandHandler( extractor, listener, command, params, headers);
	}
	
	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx, AbstractInitiator cynapseInit) {
		return null;
	}
	
	@Override
	public String getTableSchema() {
		
		StringBuilder current = new StringBuilder();
		
		final String[] tables = getTableName();
		for (int i = 0; i < tables.length; i++) {
			{
				current.append("CREATE TABLE ").append(tables[i]).append(" (\n");
				current.append("\tENTRY_DATE TIMESTAMP DEFAULT NOW(),\n");
				current.append("\tNE_ID VARCHAR(9),\n");
				current.append("\tLINE BIGINT(9),\n");

			}
			
			for (ConfiguredHeader configuredHeader : headers) {
				current.append("\t"+configuredHeader.getName()).append(' ').append("VARCHAR(").append(configuredHeader.getDbLength()).append("),\n");
			}
			current.setLength(current.length() - 2);
			
			current.append("\n);\r\n");
		}
		return current.toString();
	}

}
