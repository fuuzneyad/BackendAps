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

public class ParserDBTSPCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "DBTSP";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	
	public ParserDBTSPCommandHandlerFactory() {
		headersMap.put("FIRST", 
				new ConfiguredHeader[] {
				new ConfiguredHeader("BLOCK   "),
				new ConfiguredHeader("TAB_", "TAB             ".length()),
				new ConfiguredHeader("TABLE_", "TABLE                           ".length()),
				new ConfiguredHeader("WRAPPED")
			}
		);
		headersMap.put("SECOND", 
				new ConfiguredHeader[] {
				new ConfiguredHeader("NAME            "),
				new ConfiguredHeader("SETNAME         "),
				new ConfiguredHeader("PARID      "),
				new ConfiguredHeader("VALUE_"),
				new ConfiguredHeader("UNIT "),
				new ConfiguredHeader("CLASS_  "),
				new ConfiguredHeader("DISTRIB")
			}
		);
		headersMap.put("THIRD", 
				new ConfiguredHeader[] {
				new ConfiguredHeader("                                STATUS  "),
				new ConfiguredHeader("FCVSET "),
				new ConfiguredHeader("FCVALUE "),
				new ConfiguredHeader("DCINFO "),
				new ConfiguredHeader("FCODE")
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
			return new ParserDBTSPCommandHandler( extractor, listener, command, params,headersMap,cynapseInit);
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
