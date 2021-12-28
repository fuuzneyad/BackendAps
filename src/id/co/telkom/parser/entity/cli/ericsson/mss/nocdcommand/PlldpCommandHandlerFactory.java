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

public class PlldpCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "PLLDP";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	
	public PlldpCommandHandlerFactory() {
		headersMap.put("FIRST", 
				new ConfiguredHeader[] {
				new ConfiguredHeader("INT_ ","INT ".length()),
				new ConfiguredHeader("PLOAD "),
				new ConfiguredHeader("CALIM "),
				new ConfiguredHeader("OFFDO "),				
				new ConfiguredHeader("OFFDI "),
				new ConfiguredHeader("FTCHDO "),
				new ConfiguredHeader("FTCHDI "),
				new ConfiguredHeader("OFFMPH "),
				new ConfiguredHeader("OFFMPL "),
				new ConfiguredHeader("FTCHMPH "),
				new ConfiguredHeader("FTCHMPL")
			}
		);
		headersMap.put("SECOND", 
				new ConfiguredHeader[] {
				new ConfiguredHeader("INT_ ","INT ".length()),
				new ConfiguredHeader("OFFTCAP "),
				new ConfiguredHeader("FTDTCAP")
			}
		);
	}

	@Override
	public String getCommand() {
		return command;
	}
	
	public String[] getTableName(){
		return new String[] {command+"_1", command+"_2"};
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
			return new PlldpCommandHandler( extractor, listener, command, params, headersMap);
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
