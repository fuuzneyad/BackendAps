package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserC7LDPCommandHandlerFactory implements MscCommandHandlerFactory {

	private static final String command = "C7LDP";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	
	public ParserC7LDPCommandHandlerFactory() {
		headersMap.put("FIRST", new ConfiguredHeader[]{
				new ConfiguredHeader("LS             "),
				new ConfiguredHeader("SPID1    "),
				new ConfiguredHeader("ASP            ",true),
				new ConfiguredHeader("SPID2",true)
			}
		);
		
		headersMap.put("SECOND", new ConfiguredHeader[]{
				new ConfiguredHeader("SLC "),
				new ConfiguredHeader("ACL "),
				new ConfiguredHeader("PARMG "),
				new ConfiguredHeader("ST                   "),
				new ConfiguredHeader("SDL                             "),
				new ConfiguredHeader("SLI")
			}
		);
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
			return new ParserC7LDPCommandHandler( extractor, listener, command, params, headersMap, cynapseInit);
	}
	
	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return null;
	}
	
	@Override
	public String getTableSchema() {
//		StringBuilder current = new StringBuilder();
//		
//		final String[] tables = getTableName();
//		for (int i = 0; i < tables.length; i++) {
//			{
//				current.append("CREATE TABLE ").append(tables[i]).append(" (\n");
//				current.append("\tENTRY_DATE TIMESTAMP DEFAULT NOW(),\n");
//				current.append("\tNE_ID VARCHAR(9),\n");
//				current.append("\tLINE BIGINT(9),\n");
//			}
//			
//			for (ConfiguredHeader configuredHeader : headers) {
//				current.append("\t"+configuredHeader.getName()).append(' ').append("VARCHAR(").append(configuredHeader.getDbLength()).append("),\n");
//			}
//			current.setLength(current.length() - 2);
//			
//			current.append("\n);\r\n");
//		}
//		return current.toString();
		return "";
	}
	
}
