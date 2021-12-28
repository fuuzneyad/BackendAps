package id.co.telkom.parser.entity.traversa.nokia.msscommand;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserZNBICommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "ZNBI";
	private final ConfiguredHeader[] headers;
	
	public ParserZNBICommandHandlerFactory() {
		headers = new ConfiguredHeader[]{
				new ConfiguredHeader("SS7  "),
				new ConfiguredHeader("GTI  "),
				new ConfiguredHeader("TT  "),
				new ConfiguredHeader("NP "),
				new ConfiguredHeader("NAI  "),
				new ConfiguredHeader("DIGITS                    "),
				new ConfiguredHeader("RECORD  "),
				new ConfiguredHeader("STATE  "),
				new ConfiguredHeader("TYPE")
			};
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
			return new ParserZNBICommandHandler( extractor, listener, getCommand(), params,headers,cynapseInit);
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
