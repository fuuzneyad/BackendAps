package id.co.telkom.parser.entity.traversa.common;

import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;


public interface InitiatorCommandHandlerFactory  {
	
	String getCommand();
	CommandHandler create(Parser extractor, String command, String params, Context ctx, GlobalBuffer buf);
}
