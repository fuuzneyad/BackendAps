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


public class ZmviCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "MVI";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	
	public ZmviCommandHandlerFactory() {
		headersMap.put("MVI",
				new ConfiguredHeader[]{
				new ConfiguredHeader("VLR         "),
				new ConfiguredHeader("ACTUAL           "),
				new ConfiguredHeader("MAXIMUM ")
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
		return new ZmviCommandHandler(extractor, listener, command, params, headersMap, getCommand());
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
			current.append("\tNE_ID VARCHAR(9),\n");
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
