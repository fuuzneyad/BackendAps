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

public class ParserMGSVPCommandHandlerFactory implements MscCommandHandlerFactory{
	private static final String command = "MGSVP";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();

	public ParserMGSVPCommandHandlerFactory() {
		headersMap.put("FIRST", 
				new ConfiguredHeader[] {
				new ConfiguredHeader("HLRADDR             "),
				new ConfiguredHeader("NSUB       "),
				new ConfiguredHeader("NSUBA", 20)
			}
		);
		
	}

	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return command;
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx,
			AbstractInitiator cynapseInit) {
		// TODO Auto-generated method stub
		return new ParserMGSVPCommandHandler(extractor, listener, command, params, headersMap, cynapseInit);
	}

	@Override
	public String getTableSchema() {
		// TODO Auto-generated method stub
		return null;
	}
}
