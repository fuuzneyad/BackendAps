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

public class ParserIHCOPCommandHandlerFactory  implements MscCommandHandlerFactory {
	public static final String command = "IHCOP";
	private final Map <String, ConfiguredHeader[]> headers =  new LinkedHashMap<String, ConfiguredHeader[]>();
	
	public ParserIHCOPCommandHandlerFactory(){
		headers.put("IPPORT", new ConfiguredHeader[] {
				new ConfiguredHeader("IPPORT  "),
				new ConfiguredHeader("MHROLE   "),
				new ConfiguredHeader("MHRELPORT  "),
				new ConfiguredHeader("CURROLE")
			});
		headers.put("IPADD", new ConfiguredHeader[] {
				new ConfiguredHeader("IPADD             "),
				new ConfiguredHeader("SUBMASK")
			});
		headers.put("MTU", new ConfiguredHeader[] {
				new ConfiguredHeader("MTU")
			});
		headers.put("IPMIGR", new ConfiguredHeader[] {
				new ConfiguredHeader("IPMIGR          "),
				new ConfiguredHeader("IPBK"),
			});
		headers.put("SVRATE", new ConfiguredHeader[] {
				new ConfiguredHeader("SVRATE  "),
				new ConfiguredHeader("SVTO  "),
				new ConfiguredHeader("SVMAXTX  "),
				new ConfiguredHeader("SVMINRX")
			});
		headers.put("SVI", new ConfiguredHeader[] {
				new ConfiguredHeader("SVI  "),
				new ConfiguredHeader("SVR")
			});
		headers.put("SVGW", new ConfiguredHeader[] {
				new ConfiguredHeader("SVGW")
			});
		
	}
	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return new ParserIHCOPCommandHandler(extractor, listener, command, params, headers, null);
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx,
			AbstractInitiator cynapseInit) {
		return new ParserIHCOPCommandHandler(extractor, listener, command, params, headers, cynapseInit);
	}

	@Override
	public String getTableSchema() {
		return null;
	}

}
