package id.co.telkom.parser.entity.cli.ericsson.mss.nocdcommand;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class EreppCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "EREPP";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	
	public EreppCommandHandlerFactory() {
		headersMap.put("FIRST", 
				new ConfiguredHeader[] {
				new ConfiguredHeader("ENUM "),
				new ConfiguredHeader("HEADER                          "),
				new ConfiguredHeader("BLOCK   "),
				new ConfiguredHeader("DATE_   "),
				new ConfiguredHeader("TIME_", 30,"TIME_".length())
			}
		);
	}

	@Override
	public String getCommand() {
		return command;
	}
	
	public String[] getTableName(){
		return new String[] {command,command+"_PARAM"};
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
			return new EreppCommandHandler( extractor, listener, command, params, headersMap);
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

		Collection<ConfiguredHeader[]> headers = headersMap.values();
		Iterator<ConfiguredHeader[]> iterator = headers.iterator();
			current.append("CREATE TABLE ").append(tables[0]).append(" (\n");
			current.append("\tENTRY_DATE TIMESTAMP DEFAULT NOW(),\n");
			current.append("\tNE_ID VARCHAR(9),\n");
			current.append("\tCOMMAND_PARAM VARCHAR(50),\n");
			current.append("\tLINE BIGINT(9),\n");
			current.append("\tPARAM_ID BIGINT(9),\n");
			
				ConfiguredHeader[] header = iterator.next();
				for (ConfiguredHeader configuredHeader : header) {
					current.append("\t"+configuredHeader.getName()).append(" ").append("VARCHAR(").append(configuredHeader.getDbLength()).append( "),\n");
				}
				
			current.setLength(current.length() - 2);
			current.append("\n);\r\n");

			
			current.append("CREATE TABLE ").append(tables[1]).append(" (\n");
			current.append("\tENTRY_DATE TIMESTAMP DEFAULT NOW(),\n");
			current.append("\tNE_ID VARCHAR(9),\n");
			current.append("\tCOMMAND_PARAM VARCHAR(50),\n");
			current.append("\tLINE BIGINT(9),\n");
			current.append("\tPARAM_ID BIGINT(9),\n");
			current.append("\tPARAM_KEY VARCHAR(100),\n");	
			current.append("\tPARAM_VALUE VARCHAR(200),\n");	
			current.setLength(current.length() - 2);
			current.append("\n);\r\n");
		return current.toString();
	}

}
