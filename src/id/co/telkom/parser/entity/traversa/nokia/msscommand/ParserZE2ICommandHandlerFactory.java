package id.co.telkom.parser.entity.traversa.nokia.msscommand;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserZE2ICommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "ZE2I";
	private final ConfiguredHeader[] headers;
	
	public ParserZE2ICommandHandlerFactory() {
		headers = new ConfiguredHeader[]{
				new ConfiguredHeader("RNCID",30),
				new ConfiguredHeader("MCC", 30),			
				new ConfiguredHeader("MNC", 30),
				new ConfiguredHeader("RNCNAME", 30),
				new ConfiguredHeader("STATE", 30),
				new ConfiguredHeader("OPSTATE", 30),
				new ConfiguredHeader("UPD", 30),
				new ConfiguredHeader("NUPD", 30),
				new ConfiguredHeader("UTYPE", 30),
				new ConfiguredHeader("VER", 30),
				new ConfiguredHeader("AMR", 30),
				new ConfiguredHeader("DIG", 30),
				new ConfiguredHeader("NP", 30),
				new ConfiguredHeader("TON", 30),
				new ConfiguredHeader("NI", 30),
				new ConfiguredHeader("SPC", 30),
				new ConfiguredHeader("AMR_CODEC", 60)			
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
			return new ParserZE2ICommandHandler( extractor, listener, getCommand(), params,headers,cynapseInit);
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
