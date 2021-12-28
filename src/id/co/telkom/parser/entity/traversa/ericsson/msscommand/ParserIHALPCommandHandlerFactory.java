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

public class ParserIHALPCommandHandlerFactory  implements MscCommandHandlerFactory {
	public static final String command = "IHALP";
	private final Map <String, ConfiguredHeader[]> headers =  new LinkedHashMap<String, ConfiguredHeader[]>();
	
	public ParserIHALPCommandHandlerFactory(){
		headers.put("EPID", new ConfiguredHeader[] {
				new ConfiguredHeader("EPID    "),
				new ConfiguredHeader("EPSTATE         "),
				new ConfiguredHeader("LPN              "),
				new ConfiguredHeader("USER")
			});
		headers.put("LIP", new ConfiguredHeader[] {
				new ConfiguredHeader("LIP")
			});
		headers.put("SAID", new ConfiguredHeader[] {
				new ConfiguredHeader("SAID            "),
				new ConfiguredHeader("SASTATE            "),
				new ConfiguredHeader("RPN   "),
				new ConfiguredHeader("OS    "),
				new ConfiguredHeader("MODE")
			});
		headers.put("RIP", new ConfiguredHeader[] {
				new ConfiguredHeader("RIP                                      "),
				new ConfiguredHeader("RIPSTATE")
			});
		
	}
	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return new ParserIHALPCommandHandler(extractor, listener, command, params, headers, null);
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx,
			AbstractInitiator cynapseInit) {
		return new ParserIHALPCommandHandler(extractor, listener, command, params, headers, cynapseInit);
	}

	@Override
	public String getTableSchema() {
		return null;
	}

}
