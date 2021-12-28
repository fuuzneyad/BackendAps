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

public class ParserZWVJCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "ZWVJ";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	
	public ParserZWVJCommandHandlerFactory() {
		headersMap.put("HANDOVER NUMBERS", 
				new ConfiguredHeader[]{
					new ConfiguredHeader("FIRST_NUMBER        "),
					new ConfiguredHeader("LAST_NUMBER         "),
					new ConfiguredHeader("TON              "),
					new ConfiguredHeader("        TOTAL")
				}
			);
			headersMap.put("ROAMING NUMBERS", 
				new ConfiguredHeader[]{
					new ConfiguredHeader("FIRST_NUMBER        "),
					new ConfiguredHeader("LAST_NUMBER         "),
					new ConfiguredHeader("RNGP             "),
					new ConfiguredHeader("        TOTAL")
				}
			);
			headersMap.put("EMERGENCY SERVICES ROUTING KEYS", 
				new ConfiguredHeader[]{
					new ConfiguredHeader("FIRST_NUMBER        "),
					new ConfiguredHeader("LAST_NUMBER         "),
					new ConfiguredHeader("INDEXS           "),
					new ConfiguredHeader("        TOTAL")
				}
			);
			headersMap.put("IU SIGNALLING CONNECTION ID RANGE", 
				new ConfiguredHeader[]{
					new ConfiguredHeader("FIRST_NUMBER        "),
					new ConfiguredHeader("LAST_NUMBER                          "),
					new ConfiguredHeader("        TOTAL")
				}
			);
			headersMap.put("AOIP CALL IDENTIFIER RANGE", 
					new ConfiguredHeader[]{
						new ConfiguredHeader("FIRST_NUMBER        "),
						new ConfiguredHeader("LAST_NUMBER                          "),
						new ConfiguredHeader("        TOTAL")
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
			return new ParserZWVJCommandHandler( extractor, listener, getCommand(), params,headersMap,cynapseInit);
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
