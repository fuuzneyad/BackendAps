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

public class ParserC7NCPCommandHandlerFactory  implements MscCommandHandlerFactory {
	public static final String command = "C7NCP";
	private final Map <String, ConfiguredHeader[]> headers =  new LinkedHashMap<String, ConfiguredHeader[]>();
	
	public ParserC7NCPCommandHandlerFactory(){
		headers.put("SP", new ConfiguredHeader[] {
				new ConfiguredHeader("SP             "),
				new ConfiguredHeader("SPID     "),
				new ConfiguredHeader("SPSTATE     "),
				new ConfiguredHeader("BROADCASTSTATUS  "),
				new ConfiguredHeader("SCCPSTATE")
			});
		headers.put("SSN", new ConfiguredHeader[] {
				new ConfiguredHeader("                        SSN         "),
				new ConfiguredHeader("SUBSYSTEMSTATE   "),
				new ConfiguredHeader("SST")
			});
		
	}
	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return new ParserC7NCPCommandHandler(extractor, listener, command, params, headers, null);
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx,
			AbstractInitiator cynapseInit) {
		return new ParserC7NCPCommandHandler(extractor, listener, command, params, headers, cynapseInit);
	}

	@Override
	public String getTableSchema() {
		return null;
	}

}
