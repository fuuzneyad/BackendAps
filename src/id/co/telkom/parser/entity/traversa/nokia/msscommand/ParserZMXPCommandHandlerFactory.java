package id.co.telkom.parser.entity.traversa.nokia.msscommand;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserZMXPCommandHandlerFactory implements MscCommandHandlerFactory {

	private static final String command = "ZMXP";
	private final ConfiguredHeader[][] headers;
	
	
	
	public ParserZMXPCommandHandlerFactory() {
		headers = new ConfiguredHeader[][]{
				//jgn ubh urutannya
		
		new ConfiguredHeader[]{
				new ConfiguredHeader("TO_GSM","                          TO GSM    ".length()),
				new ConfiguredHeader("TO_GSM_REJECT_CODE ","REJECT CODE      ".length()),
				new ConfiguredHeader("TO_UMTS","TO UMTS   ".length()),
				new ConfiguredHeader("TO_UMTS_REJECT_CODE","REJECT CODE ".length())
			}
				
		};
	}
	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return command;
	}
	
	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx,
			AbstractInitiator cynapseInit) {
		// TODO Auto-generated method stub
		return new ParserZMXPCommandHandler( extractor, listener, command, params,headers,cynapseInit);
	}


	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public String getTableSchema() {
		// TODO Auto-generated method stub
		return null;
	}

}
