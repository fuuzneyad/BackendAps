package id.co.telkom.parser.entity.traversa.huawei.sgsncommand;

import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserLSTTAILAICommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "LST_TAILAI";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();

	public ParserLSTTAILAICommandHandlerFactory() {
		headersMap.put("MASTER", new ConfiguredHeader[]{
				new ConfiguredHeader(" BEGIN_TAI  "),
				new ConfiguredHeader("END_TAI    "),
				new ConfiguredHeader("Subscriber_Range  ".toUpperCase()),
				new ConfiguredHeader("IMSI_Prefix  ".toUpperCase()),
				new ConfiguredHeader("LAI"),
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
		return new ParserLSTTAILAICommandHandler( extractor, listener, getCommand(), params,headersMap, cynapseInit);
	}
	
	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return new ParserLSTTAILAICommandHandler( extractor, listener, getCommand(), params, headersMap, null);
	}
	
	@Override
	public String getTableSchema() {
		return "";
	}
	
}
