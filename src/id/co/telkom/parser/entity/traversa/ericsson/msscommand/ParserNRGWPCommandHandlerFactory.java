package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserNRGWPCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "NRGWP";
	private ConfiguredHeader[] headers;
	
	public ParserNRGWPCommandHandlerFactory() {
		headers=new ConfiguredHeader[] {
				new ConfiguredHeader("MG       ",true),
				new ConfiguredHeader("BCUID       ",true),
				new ConfiguredHeader("STATUS  ",true),
				new ConfiguredHeader("MGG      "),
				new ConfiguredHeader("MGS      "),
				new ConfiguredHeader("INFO            "),
				new ConfiguredHeader("MC")
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
			return new ParserNRGWPCommandHandler( extractor, listener, command, params,headers,cynapseInit);
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
