package id.co.telkom.parser.entity.dashboard.oss.command;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class PrstatCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "PRSTAT";
	private final ConfiguredHeader[] header ;
	
	public PrstatCommandHandlerFactory() {
		header = new ConfiguredHeader[] {
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
			String params, DataListener listener, Context ctx) {
			return new PrstatCommandHandler( extractor, listener, command, params, header);
	}
	
	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx, AbstractInitiator cynapseInit) {
		return null;
	}
	
	@Override
	public String getTableSchema() {
		return "";
	}

}
