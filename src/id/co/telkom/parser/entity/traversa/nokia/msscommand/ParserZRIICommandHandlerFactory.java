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

public class ParserZRIICommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "ZRII";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();

	
	public ParserZRIICommandHandlerFactory() {
		headersMap.put("DIGITS", new ConfiguredHeader[]{
				new ConfiguredHeader("DIGITS                   ", true),
				new ConfiguredHeader("AL "),
				new ConfiguredHeader("NBR    "),
				new ConfiguredHeader("RT   "),
				new ConfiguredHeader("CT  "),
				new ConfiguredHeader("SP "),
				new ConfiguredHeader("NL "),
				new ConfiguredHeader("RC      "),
				new ConfiguredHeader("DEST "),
				new ConfiguredHeader("CHI  "),
				new ConfiguredHeader("CNT  "),
				new ConfiguredHeader("SDEST")
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
			return new ParserZRIICommandHandler( extractor, listener, getCommand(), params,headersMap,cynapseInit);
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