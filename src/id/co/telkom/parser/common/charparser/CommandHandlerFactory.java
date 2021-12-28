package id.co.telkom.parser.common.charparser;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;


public interface CommandHandlerFactory  {
	
	String getCommand();
	CommandHandler create(Parser extractor, String command, String params, DataListener listener, Context ctx);
	CommandHandler create(Parser extractor, String command, String params, DataListener listener, Context ctx, AbstractInitiator cynapseInit);
	String getTableSchema();
}
