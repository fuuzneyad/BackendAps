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

public class ParserZELOCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "ZELO";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();

	
	public ParserZELOCommandHandlerFactory() {
		headersMap.put(command, 
				new ConfiguredHeader[]{
					new ConfiguredHeader("LA_NAME", 30),
					new ConfiguredHeader("LAC", 30)
				}
			);
			headersMap.put("PROPERTIES", 
				new ConfiguredHeader[]{
					new ConfiguredHeader("PARAM", 50),
					new ConfiguredHeader("VALUE", 30)
				}
			);
			headersMap.put("BTSS", 
				new ConfiguredHeader[]{
					new ConfiguredHeader("BSC_NAME   ", true),
					new ConfiguredHeader("BSC_NUMBER ", true),
					new ConfiguredHeader("BSC_ADM_STATE ", true),
					new ConfiguredHeader("BTS_NAME    "),
					new ConfiguredHeader("BTS_NUMBER", 8),
					new ConfiguredHeader("BTS_CI", 11),
					new ConfiguredHeader("BTS_ADM_STATE")
				}
			);
			headersMap.put("SERVICE", 
				new ConfiguredHeader[]{
					new ConfiguredHeader("MGW_NAME   ", true),
					new ConfiguredHeader("MGW_NUMBER ", true),
					new ConfiguredHeader("MGW_ADM_STATE ", true),
					new ConfiguredHeader("SA_NAME", 12),
					new ConfiguredHeader("SA_NUMBER", 8),
					new ConfiguredHeader("SA_SAC", 11),
					new ConfiguredHeader("SDA_ADM_STATE"),
					new ConfiguredHeader("GROUPNAME", 50)
				}
			);
			headersMap.put("RNCs", 
				new ConfiguredHeader[]{
					new ConfiguredHeader("MGW_NAME   ", true),
					new ConfiguredHeader("MGW_NUMBER ", true),
					new ConfiguredHeader("MGW_ADM_STATE ", true),
					new ConfiguredHeader("RNC_NAME    "),
					new ConfiguredHeader("RNC_ID  "),
					new ConfiguredHeader("MCC  "),
					new ConfiguredHeader("MNC  "),
					new ConfiguredHeader("RNC_STATE")
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
			return new ParserZELOCommandHandler( extractor, listener, getCommand(), params,headersMap,cynapseInit);
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
