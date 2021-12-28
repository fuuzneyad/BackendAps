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

public class ParserM3RSPCommandHandlerFactory implements MscCommandHandlerFactory{
	private static final String command = "M3RSP";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	
	public ParserM3RSPCommandHandlerFactory() {
				
		headersMap.put("M3RSP", 
				new ConfiguredHeader[] {
				new ConfiguredHeader("DEST           "),
				new ConfiguredHeader("SPID         "),
				new ConfiguredHeader("DST    "),
				new ConfiguredHeader("LSHM"),

			}
		);

		headersMap.put(command+"_DEST", 
				new ConfiguredHeader[] {
				new ConfiguredHeader("SAID             "),
				new ConfiguredHeader("PRIO  "),
				new ConfiguredHeader("RST              "),
				new ConfiguredHeader("CW     "),
				new ConfiguredHeader("CWU"),
				

			}
		);

	
	}

	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return command;
	}

	public String[] getTableName(){
		return new String[] {
				command,
				command+"_DEST"	};
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx, AbstractInitiator cynapseInit) {
			return new ParserM3RSPCommandHandler( extractor, listener, command, params,headersMap,cynapseInit);
	}
	
	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return null;
	}
	
	@Override
	public String getTableSchema() {
		// TODO Auto-generated method stub
		return null;
	}

}