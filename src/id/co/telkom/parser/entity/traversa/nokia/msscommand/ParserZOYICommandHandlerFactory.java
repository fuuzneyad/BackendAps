package id.co.telkom.parser.entity.traversa.nokia.msscommand;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserZOYICommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "ZOYI";
	private final ConfiguredHeader[][] headers;
	
	public ParserZOYICommandHandlerFactory() {
		headers = new ConfiguredHeader[][]{
				new ConfiguredHeader[]{
					new ConfiguredHeader("ASSOCIATION_SET_NAME   "),
					new ConfiguredHeader("ASSOC_SET_ID   "),
					new ConfiguredHeader("SCTP_USER   "),
					new ConfiguredHeader("ROLE", 8, 8)
				},
				new ConfiguredHeader[]{
					new ConfiguredHeader("ASSOC_IND", "    IND    ".length()),
					new ConfiguredHeader("UNIT    "),
					new ConfiguredHeader("ASSOC_ID_IN_UNIT", "IN UNIT   ".length()),
					new ConfiguredHeader("PARAMETER_SET_NAME", "NAME             ".length()),
					new ConfiguredHeader("STATE", 20, 20)
				}
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
			return new ParserZOYICommandHandler( extractor, listener, getCommand(), params,headers,cynapseInit);
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
