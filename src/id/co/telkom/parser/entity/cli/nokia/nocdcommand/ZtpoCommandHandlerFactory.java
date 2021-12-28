package id.co.telkom.parser.entity.cli.nokia.nocdcommand;


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


public class ZtpoCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "TPO";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	
	public ZtpoCommandHandlerFactory() {
		headersMap.put("CTR_GLOBAL",
				new ConfiguredHeader[]{
				new ConfiguredHeader("COUNTERS","COUNTERS    ".length()),
				new ConfiguredHeader("NAME                        "),
				new ConfiguredHeader("LAST       "),
				new ConfiguredHeader("SECOND_LAST","LAST       ".length()),
				new ConfiguredHeader("AVG_LAST_SMPL",50,"LAST SAMPLES ".length())
			}
		);
		
		headersMap.put("MEAS_SERIES",
				new ConfiguredHeader[]{
				new ConfiguredHeader("MEAS_SERIES","SERIES      ".length()),
				new ConfiguredHeader("PERIOD_INFO_NAME","NAME                         ".length()),
				new ConfiguredHeader("LAST_PERIOD","PERIOD     ".length()),
				new ConfiguredHeader("SAMPLE_TIME ")
			}
		);
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
		return new ZtpoCommandHandler(extractor, listener, command, params, headersMap, getCommand());
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
		
		for (int i = 0; i < tables.length; i++) {
			current.append("CREATE TABLE ").append(tables[i]).append(" (\n");
			current.append("\tENTRY_DATE TIMESTAMP DEFAULT NOW(),\n");
			current.append("\tNE_ID VARCHAR(20),\n");
			current.append("\tMO_ID VARCHAR(40),\n");
			current.append("\tLINE BIGINT(9),\n");
			
			ConfiguredHeader[] header = iterator.next();
			for (ConfiguredHeader configuredHeader : header) {
				current.append("\t"+configuredHeader.getName()).append(' ').append("VARCHAR(").append(configuredHeader.getDbLength()).append("),\n");
			}
			
			current.setLength(current.length() - 2);
			current.append("\n);\r\n");
		}
		return current.toString();
	}

}
