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

public class ZifoCommandHandlerFactory implements MscCommandHandlerFactory {
	private String command="IFO";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	
	public ZifoCommandHandlerFactory(){
		headersMap.put("IFO",
				new ConfiguredHeader[]{
				new ConfiguredHeader("FILE_NUMBER ", "NUMBER ".length()),
				new ConfiguredHeader("STATUS       "),
				new ConfiguredHeader("ORIG_COMPRESSED","COMPRESSED        ".length()),
				new ConfiguredHeader("FILLING_TIME          "),
				new ConfiguredHeader("TRANSFER_TIME ",false,30)
			}
		);
	}
	

	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return new ZifoCommandHandler(extractor,command, params, listener, getTableName()[0], headersMap);
	}
	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx, AbstractInitiator cynapseInit) {
		return null;
	}
	private String[] getTableName() {
		return new String[] {command}; 
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
