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

public class ParserPCORPCommandHandlerFactory  implements MscCommandHandlerFactory {
	public static final String command = "PCORP";
	private final Map <String, ConfiguredHeader[]> headers =  new LinkedHashMap<String, ConfiguredHeader[]>();
	
	public ParserPCORPCommandHandlerFactory(){
		headers.put("FIRST", new ConfiguredHeader[] {
				new ConfiguredHeader("BLOCK    "),
				new ConfiguredHeader("SUID                               "),
				new ConfiguredHeader("CA    "),
				new ConfiguredHeader("CAF")
			});
		headers.put("SECOND", new ConfiguredHeader[] {
				new ConfiguredHeader("CI               "),
				new ConfiguredHeader("S  "),
				new ConfiguredHeader("TYPE_ "),
				new ConfiguredHeader("POSITION         "),
				new ConfiguredHeader("SIZE")
			});
	}
	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return new ParserPCORPCommandHandler(extractor, listener, command, params, headers, null);
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx,
			AbstractInitiator cynapseInit) {
		return new ParserPCORPCommandHandler(extractor, listener, command, params, headers, cynapseInit);
	}

	@Override
	public String getTableSchema() {
		return null;
	}

}
