package id.co.telkom.parser.entity.traversa.nokia.msscommand;

import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserZNETCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "ZNET";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	
	public ParserZNETCommandHandlerFactory() {
		headersMap.put("SIGNALLING ROUTE SETS", 
				new ConfiguredHeader[]{
					new ConfiguredHeader("SP_CODE_H_D        ",true),
					new ConfiguredHeader("NAME   ", true),
					new ConfiguredHeader("STATE  ", true, 70),
					new ConfiguredHeader("SIGNALLING_ROUTES_IN_ROUTE_SET", "IN_ROUTE_SET        ".length()),
					new ConfiguredHeader("SIGNALLING_ROUTES_STATE  ", "STATE  ".length()),
					new ConfiguredHeader("INFO", false, 30),
					new ConfiguredHeader("NETWORK", false, 30),
				}
			);
			headersMap.put("SIGNALLING LINK SETS", 
				new ConfiguredHeader[]{
					new ConfiguredHeader("NET  ", true),
					new ConfiguredHeader("SP_CODE_H_D          ", true),
					new ConfiguredHeader("LINK_SET   ", true),
					new ConfiguredHeader("STATE  ", true),
					new ConfiguredHeader("SIGNALLING_LINKS_IN_LINK_SET", 20)
				}
			);
			headersMap.put("SIGNALLING LINKS", 
				new ConfiguredHeader[]{
					new ConfiguredHeader(" LINK"),
					new ConfiguredHeader("    LINK_SET "),
					new ConfiguredHeader("LINK_STATE", "STATE      ".length()),
					new ConfiguredHeader("UNIT   "),
					new ConfiguredHeader("TERM "),
					new ConfiguredHeader("TERM_FUNCT", "FUNCT ".length()),
					new ConfiguredHeader("LOG_TERM", "TERM ".length()),
					new ConfiguredHeader("EXTERN_PCM_TSL", "PCM-TSL     ".length()),
					new ConfiguredHeader("INTERN_PCM_TSL", "PCM-TSL    ".length()),
					new ConfiguredHeader("BIT_RATE", 5),
				}
			);
			headersMap.put("M3UA BASED LINKS", 
				new ConfiguredHeader[]{
					new ConfiguredHeader("M3UA_LINK", "LINK   ".length()),
					new ConfiguredHeader("LINK_SET   "),
					new ConfiguredHeader("ASSOCIATION_SET  ", "SET          ".length()),
					new ConfiguredHeader("LINK_STATE", 10)
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
			//This is OK
			//return new ParserZNETCommandHandler( extractor, listener, getCommand(), params,headersMap,parserInit);
			//But we want something else in topology
			return new ParserZNETRetouchCommandHandler( extractor, listener, getCommand(), params,headersMap,cynapseInit);
	}
	
	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return null;
	}
	
	@Override
	public String getTableSchema() {
		return "";
	}
	
}
