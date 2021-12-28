package id.co.telkom.parser.entity.traversa.nokia.msscommand;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserZCFICommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "ZCFI";
	private final ConfiguredHeader[] headers;
	
	public ParserZCFICommandHandlerFactory() {
		headers = new ConfiguredHeader[]{
				new ConfiguredHeader("IMSI_NBR","IMSI            ".length()),
				new ConfiguredHeader("IMSI_IND","IMSI ".length()),
				new ConfiguredHeader("PLMN            "),
				new ConfiguredHeader("GT              "),
				new ConfiguredHeader("NP    "),
				new ConfiguredHeader("TON "),	 
				new ConfiguredHeader("NI  "),	 
				new ConfiguredHeader("SPC"),
				new ConfiguredHeader("SP_REMOVED_DGT"),
				new ConfiguredHeader("DIGITS_REMOVED"),
				new ConfiguredHeader("SP_ADDED_DGT"),
				new ConfiguredHeader("ADDED_DIGITS")

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
			return new ParserZCFICommandHandler( extractor, listener, getCommand(), params,headers,cynapseInit);
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
