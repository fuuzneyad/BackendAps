package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserEXSCPCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "EXSCP";
	private final Map<String, ConfiguredHeader[]> headers = new LinkedHashMap<String, ConfiguredHeader[]>();

	public ParserEXSCPCommandHandlerFactory(){
		headers.put("FIRST", 
				new ConfiguredHeader[] {
				new ConfiguredHeader("NAME                      ",true),
				new ConfiguredHeader("CSTATE   ",true),
				new ConfiguredHeader("DISTC   ",true),
				new ConfiguredHeader("MISC        ",true),
				new ConfiguredHeader("NUMCH",true)

			}
		);

		
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
			return new ParserEXSCPCommandHandler( extractor, listener, command, params,headers,cynapseInit);
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