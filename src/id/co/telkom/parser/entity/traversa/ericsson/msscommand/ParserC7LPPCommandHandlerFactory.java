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

public class ParserC7LPPCommandHandlerFactory implements MscCommandHandlerFactory{
	private static final String command="C7LPP";
	private final Map<String, ConfiguredHeader[]> headers = new LinkedHashMap<String, ConfiguredHeader[]>();
	
	public ParserC7LPPCommandHandlerFactory() {
		headers.put("VALUE",
				new ConfiguredHeader[]{
				new ConfiguredHeader("PARAM  "),
				new ConfiguredHeader("       0"),
				new ConfiguredHeader("       1"),
				new ConfiguredHeader("       2"),
				new ConfiguredHeader("       3"),
				new ConfiguredHeader("       4"),
				new ConfiguredHeader("       5"),
				new ConfiguredHeader("       6"),
				new ConfiguredHeader("       7")
			}
		);

	}
	
	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return command;
	}

	public String[] getTableName(){
		return new String[] {command};
	}
	
	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return null;
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx,
			AbstractInitiator cynapseInit) {
		// TODO Auto-generated method stub
		return new ParserC7LPPCommandHandler(extractor, listener, command, params, headers, cynapseInit);
	}

	@Override
	public String getTableSchema() {
		
		return null;
	}

}
