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

public class ParserZEDOCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "ZEDO";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();

	
	public ParserZEDOCommandHandlerFactory() {
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
		return new String[] {command};
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx, AbstractInitiator cynapseInit) {
			return new ParserZEDOCommandHandler( extractor, listener, getCommand(), params,headersMap,cynapseInit);
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
