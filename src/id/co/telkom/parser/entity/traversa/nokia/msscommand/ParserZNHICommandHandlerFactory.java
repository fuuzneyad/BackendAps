package id.co.telkom.parser.entity.traversa.nokia.msscommand;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserZNHICommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "ZNHI";
	private final ConfiguredHeader[] headers;
	
	public ParserZNHICommandHandlerFactory() {
		headers = new ConfiguredHeader[]{
				new ConfiguredHeader("NO_HD","NO H/D  ".length()),
				new ConfiguredHeader("SS_NAME  "),
				new ConfiguredHeader("STATE  "),
				new ConfiguredHeader("RM  "),
				new ConfiguredHeader("ROUTING_NET","NET  ".length()),
				new ConfiguredHeader("SP_CODE_H_D         "),
				new ConfiguredHeader("NAME              "),
				new ConfiguredHeader("STATE ")
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
			return new ParserZNHICommandHandler( extractor, listener, getCommand(), params,headers,cynapseInit);
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
