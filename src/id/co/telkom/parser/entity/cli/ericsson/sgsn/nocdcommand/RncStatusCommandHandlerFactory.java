package id.co.telkom.parser.entity.cli.ericsson.sgsn.nocdcommand;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class RncStatusCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "RNC_STATUS";
	private final ConfiguredHeader[] headers;
	
	public RncStatusCommandHandlerFactory() {
		headers = new ConfiguredHeader[] {
				new ConfiguredHeader("RN     ",30),
				new ConfiguredHeader("RI  ",30),
				new ConfiguredHeader("SPC  ",30),
				new ConfiguredHeader("GRC ",30),
				new ConfiguredHeader("GRNLAC",30),
				new ConfiguredHeader("RAC",150),
				new ConfiguredHeader("RS",50),
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
			return new RncStatusCommandHandler( extractor, listener, command, params, headers);
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
				current.append("\tCOMMAND_PARAM VARCHAR(30),\n");
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
