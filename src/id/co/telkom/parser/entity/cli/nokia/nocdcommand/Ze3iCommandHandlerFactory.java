package id.co.telkom.parser.entity.cli.nokia.nocdcommand;


import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;


public class Ze3iCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "E3I";
	private final ConfiguredHeader[] headers;
	
	public Ze3iCommandHandlerFactory() {
		headers = new ConfiguredHeader[]{
				new ConfiguredHeader("POOLNAME",15),
				new ConfiguredHeader("NRILEN", 15),				
				new ConfiguredHeader("MSSNAME", 15),
				
				//MAINTENANCE MODE PARAMETERS A
				new ConfiguredHeader("MAINT", 15),
				new ConfiguredHeader("MNRI", 15),
				new ConfiguredHeader("PNRI", 15),
				new ConfiguredHeader("NRIVALC", 15),
				new ConfiguredHeader("STOPLEV", 15),
				new ConfiguredHeader("TIMER", 15),
				//MSS PARAMETERS B
				new ConfiguredHeader("TRA", 15),
				new ConfiguredHeader("VER", 15),
				new ConfiguredHeader("WF", 15),		
				//BOTH A B
				new ConfiguredHeader("NBLAC", 15),
				new ConfiguredHeader("NBMCC", 15),
				new ConfiguredHeader("NBMNC", 15),//
				new ConfiguredHeader("OFFLAC", 15),
				new ConfiguredHeader("OFFMNC", 15),
				//VLR MSS ADDR
				new ConfiguredHeader("VDIG", 15),
				new ConfiguredHeader("NP_V", 15),
				new ConfiguredHeader("TON_V", 15),
				new ConfiguredHeader("VNI", 15),
				new ConfiguredHeader("VSPC", 15),
				new ConfiguredHeader("MDIG", 15),
				new ConfiguredHeader("NP_M", 15),
				new ConfiguredHeader("TON_M", 15),
				new ConfiguredHeader("MNI", 15),
				new ConfiguredHeader("MSPC", 15),

				//NRI LIST VAL
				new ConfiguredHeader("NRILIST_VAL", 15),
				//PARAM
				new ConfiguredHeader("PARAM_TYPE", 15)
		};
	}

	@Override
	public String getCommand() {
		return command;
	}
	
	public String[] getTableName(){
		return new String[] {command };
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return new Ze3iCommandHandler(extractor, listener, command, params, headers, getCommand());
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
				current.append("\tID BIGINT(9) PRIMARY KEY,\n");
				current.append("\tNE VARCHAR(9),\n");
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
