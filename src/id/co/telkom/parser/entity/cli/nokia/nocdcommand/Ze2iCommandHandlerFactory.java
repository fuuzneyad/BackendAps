package id.co.telkom.parser.entity.cli.nokia.nocdcommand;


import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class Ze2iCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "E2I";
	private final ConfiguredHeader[] headers;	
	
	public Ze2iCommandHandlerFactory() {
		headers = new ConfiguredHeader[]{
				new ConfiguredHeader("RNCID",30),
				new ConfiguredHeader("MCC", 30),			
				new ConfiguredHeader("MNC", 30),
				new ConfiguredHeader("RNCNAME", 30),
				new ConfiguredHeader("STATE", 30),
				new ConfiguredHeader("OPSTATE", 30),
				new ConfiguredHeader("UPD", 30),
				new ConfiguredHeader("NUPD", 30),
				new ConfiguredHeader("UTYPE", 30),
				new ConfiguredHeader("VER", 30),
				new ConfiguredHeader("AMR", 30),
				new ConfiguredHeader("DIG", 30),
				new ConfiguredHeader("NP", 30),
				new ConfiguredHeader("TON", 30),
				new ConfiguredHeader("NI", 30),
				new ConfiguredHeader("SPC", 30),
				new ConfiguredHeader("AMR_CODEC", 60)			
				
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
			return new Ze2iCommandHandler(getTableName()[0], extractor,  listener, command, params, headers);
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
