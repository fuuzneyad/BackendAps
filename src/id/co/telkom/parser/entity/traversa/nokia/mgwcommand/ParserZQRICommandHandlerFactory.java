package id.co.telkom.parser.entity.traversa.nokia.mgwcommand;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserZQRICommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "ZQRI";
	private final ConfiguredHeader[] headers;
	
	public ParserZQRICommandHandlerFactory() {
		headers = new ConfiguredHeader[] {
				new ConfiguredHeader("UNIT      ", true),
				new ConfiguredHeader("NAME     ", true),
				new ConfiguredHeader("ADM_STATE", "STATE  ".length(), true),
				new ConfiguredHeader("MTU  ", true),
				new ConfiguredHeader("PRIORITY  ", true),
				new ConfiguredHeader("IF_TYPE","TYPE ".length()),
				new ConfiguredHeader("ADDR_TYPE", "TYPE ".length(), "TYPE ".length(), true),
				new ConfiguredHeader("IP_ADDRESS"),
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
			return new ParserZQRICommandHandler( extractor, listener, getCommand(), params,headers,cynapseInit);
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
