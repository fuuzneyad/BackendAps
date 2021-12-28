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
import id.co.telkom.parser.entity.traversa.huawei.UnhandledCommandHandler;

public class ParserTSTDNSCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "TST_DNS";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();

	public ParserTSTDNSCommandHandlerFactory() {
		headersMap.put("MASTER", new ConfiguredHeader[]{
				new ConfiguredHeader(" FQDN                                                     "),
				new ConfiguredHeader("Resolve_type  ".toUpperCase()),
				new ConfiguredHeader("Hostname_Num  ".toUpperCase()),
				new ConfiguredHeader("Hostname                                               ".toUpperCase()),
				new ConfiguredHeader("Entity  ".toUpperCase()),
				new ConfiguredHeader("Interface  ".toUpperCase()),
				new ConfiguredHeader("S5_Protocol  ".toUpperCase()),
				new ConfiguredHeader("S8_Protocol  ".toUpperCase()),
				new ConfiguredHeader("TTL     ".toUpperCase()),
				new ConfiguredHeader("IP_Num  ".toUpperCase()),
				new ConfiguredHeader("IP_ADDR1       ".toUpperCase()),
				new ConfiguredHeader("OTHER_IP_ADDR       ",250)
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
		if(params.contains("QRYTYPE=A"))
			return new ParserTSTDNSCommandHandler( extractor, listener, getCommand(), params,headersMap, cynapseInit);
		else if(params.contains("QRYTYPE=NAPTR"))
			return new ParserTSTDNSNAPTRCommandHandler(extractor, listener, getCommand(), params, headersMap, null);
		else
			return new UnhandledCommandHandler(extractor, command, params);
	}
	
	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		if(params.contains("QRYTYPE=A"))
			return new ParserTSTDNSCommandHandler( extractor, listener, getCommand(), params, headersMap, null);
		else if(params.contains("QRYTYPE=NAPTR"))
			return new ParserTSTDNSNAPTRCommandHandler(extractor, listener, getCommand(), params, headersMap, null);
		else
			return new UnhandledCommandHandler(extractor, command, params);
	}
	
	@Override
	public String getTableSchema() {
		return "";
	}
	
}
