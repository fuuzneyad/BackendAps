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

public class DbtspCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "DBTSP";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	
	public DbtspCommandHandlerFactory() {
		headersMap.put("FIRST", 
				new ConfiguredHeader[] {
				new ConfiguredHeader("BLOCK   "),
				new ConfiguredHeader("TAB_", "TAB             ".length()),
				new ConfiguredHeader("TABLE_", "TABLE                           ".length()),
				new ConfiguredHeader("WRAPPED")
			}
		);
		headersMap.put("SECOND", 
				new ConfiguredHeader[] {
				new ConfiguredHeader("NAME            "),
				new ConfiguredHeader("SETNAME         "),
				new ConfiguredHeader("PARID      "),
				new ConfiguredHeader("VALUE_"),
				new ConfiguredHeader("UNIT "),
				new ConfiguredHeader("CLASS_  "),
				new ConfiguredHeader("DISTRIB")
			}
		);
		headersMap.put("THIRD", 
				new ConfiguredHeader[] {
				new ConfiguredHeader("                                STATUS  "),
				new ConfiguredHeader("FCVSET "),
				new ConfiguredHeader("FCVALUE "),
				new ConfiguredHeader("DCINFO "),
				new ConfiguredHeader("FCODE")
			}
		);
	}

	@Override
	public String getCommand() {
		return command;
	}
	
	public String[] getTableName(){
		return new String[] {command, command+"_BLOCK"};
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
			return new DbtspCommandHandler( extractor, listener, command, params, headersMap);
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
		for (int t = 0; t <tables.length; t++) {
			current.append("CREATE TABLE ").append(tables[t]).append(" (\n");
			current.append("\tENTRY_DATE TIMESTAMP DEFAULT NOW(),\n");
			current.append("\tNE_ID VARCHAR(9),\n");
			current.append("\tCOMMAND_PARAM VARCHAR(50),\n");
			current.append("\tLINE BIGINT(9),\n");
			
				ConfiguredHeader[] header = iterator.next();
				for (ConfiguredHeader configuredHeader : header) {
					current.append("\t"+configuredHeader.getName()).append(" ").append("VARCHAR(").append(configuredHeader.getDbLength()).append( "),\n");
				}
				
			current.setLength(current.length() - 2);
			current.append("\n);\r\n");
		}

		return current.toString();
	}

}
