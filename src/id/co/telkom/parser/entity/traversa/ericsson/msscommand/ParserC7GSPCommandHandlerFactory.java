package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserC7GSPCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "C7GSP";
	private ConfiguredHeader[][] headers;
	
	public ParserC7GSPCommandHandlerFactory() {
		headers = 
			new ConfiguredHeader[][]{
			new ConfiguredHeader[] {
				new ConfiguredHeader("TT   "),
				new ConfiguredHeader("NP  "),
				new ConfiguredHeader("NA   "),
				new ConfiguredHeader("NS                                      "),
				new ConfiguredHeader("GTRC   ")//,
//				new ConfiguredHeader("         MTT  "),//
//				new ConfiguredHeader("MNP  "),
//				new ConfiguredHeader("MNA  "),
//				new ConfiguredHeader("MNS", 30)
			}
			,
			new ConfiguredHeader[] {
					new ConfiguredHeader("    MTT  "),//
					new ConfiguredHeader("MNP  "),
					new ConfiguredHeader("MNA  "),
					new ConfiguredHeader("MNS", 30)
				},
			}
		;
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
			return new ParserC7GSPCommandHandler( extractor, listener, command, params,headers,cynapseInit);
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
