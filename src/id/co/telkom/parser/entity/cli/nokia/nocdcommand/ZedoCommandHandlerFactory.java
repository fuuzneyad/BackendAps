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


public class ZedoCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "EDO";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	
	public ZedoCommandHandlerFactory() {
		headersMap.put(command, new ConfiguredHeader[] {
				new ConfiguredHeader("BSC_NAME", 30),
				new ConfiguredHeader("BSC_NUMBER", 30) });
		headersMap.put("PROPERTIES", new ConfiguredHeader[] {
				new ConfiguredHeader("PARAM", 50),
				new ConfiguredHeader("VALUE", 30), });
		headersMap.put("CIRCUIT_POOLS", new ConfiguredHeader[] {
				new ConfiguredHeader("PARAM", 50),
				new ConfiguredHeader("VALUE", 30), });
		headersMap.put("BTSS", new ConfiguredHeader[] {
				new ConfiguredHeader("    BTS_NAME      "),
				new ConfiguredHeader("   NO    "),
				new ConfiguredHeader("  LAC    "),
				new ConfiguredHeader("MCC    "),
				new ConfiguredHeader("MNC    "),
				new ConfiguredHeader("   CI    "),
				new ConfiguredHeader("BTS_ADM_STATE") });
	}

	@Override
	public String getCommand() {
		return command;
	}
	
	public String[] getTableName(){
		return new String[] {command, command + "_" + "PROPERTIES",
				command + "_" + "CIRCUIT_POOLS", command + "_" + "BTSS" };
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return new ZedoCommandHandler(extractor, listener, command, params, headersMap, getCommand());
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
