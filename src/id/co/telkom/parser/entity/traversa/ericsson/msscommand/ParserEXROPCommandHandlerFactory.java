package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserEXROPCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "EXROP";
	private ConfiguredHeader[] headers;
	
	public ParserEXROPCommandHandlerFactory() {
		headers=new ConfiguredHeader[] {
				new ConfiguredHeader("R", 30),
				new ConfiguredHeader("DETY", 30),
				new ConfiguredHeader("FNC", 30),
				new ConfiguredHeader("SPXNI",30),
				new ConfiguredHeader("BLAO",30),
				new ConfiguredHeader("DIS",30),
				new ConfiguredHeader("BLDSPC",30),
				new ConfiguredHeader("BLDNI",30),
				new ConfiguredHeader("SPXSPC",30),
				
				new ConfiguredHeader("TTRANS",30),
				new ConfiguredHeader("ST",30),
				new ConfiguredHeader("OWNSP",30),
				new ConfiguredHeader("SP",30),
				new ConfiguredHeader("OOBTC",30),
				new ConfiguredHeader("FBBS",30),
				
				new ConfiguredHeader("PRI",30),
				new ConfiguredHeader("MGG",30),
				new ConfiguredHeader("ECIC",30),
				new ConfiguredHeader("PBSD",30)
				
				
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
			String params, DataListener listener, Context ctx, AbstractInitiator cynapseInit) {
			return new ParserEXROPCommandHandler( extractor, listener, command, params,headers,cynapseInit);
	}
	
	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
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
