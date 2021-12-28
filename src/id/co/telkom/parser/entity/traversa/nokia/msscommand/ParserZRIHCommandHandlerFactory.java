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

public class ParserZRIHCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "ZRIH";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();

	
	public ParserZRIHCommandHandlerFactory() {
		headersMap.put("DIGITS", new ConfiguredHeader[]{
				new ConfiguredHeader("DIGITS                   ", true),
				new ConfiguredHeader("AL "),
				new ConfiguredHeader("NDEST        "),
				new ConfiguredHeader("CHI "),
				new ConfiguredHeader("CNT "),
				new ConfiguredHeader("NSDEST       "),
				new ConfiguredHeader("CORG  "),
				new ConfiguredHeader("NCHA   ")
		});
		headersMap.put("TREE", new ConfiguredHeader[]{
				new ConfiguredHeader("TREE"),
				new ConfiguredHeader("ATYPE"),
				new ConfiguredHeader("TON")
		});
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
			return new ParserZRIHCommandHandler( extractor, listener, getCommand(), params,headersMap,cynapseInit);
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
