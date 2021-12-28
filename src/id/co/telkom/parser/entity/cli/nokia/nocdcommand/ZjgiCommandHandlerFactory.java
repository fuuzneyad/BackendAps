package id.co.telkom.parser.entity.cli.nokia.nocdcommand;


import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ZjgiCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "JGI";
	private final ConfiguredHeader[] headers;	
	
	public ZjgiCommandHandlerFactory() {
		headers = new ConfiguredHeader[]{
				new ConfiguredHeader("MGW_ID  "),
				new ConfiguredHeader("MGW_NAME         "),
				new ConfiguredHeader("MGW_TYPE  "),
				new ConfiguredHeader("CTRL_UNIT_ID  "),
				new ConfiguredHeader("REG_STATUS    "),
				new ConfiguredHeader("PARSET_USED_DEF"),
				new ConfiguredHeader("NBCRCT  "),
				new ConfiguredHeader("MGW_IP_ADDRESS_DOMAIN_NAME")
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
			return new ZjgiCommandHandler(getTableName()[0], extractor,  listener, command, params, headers);
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
