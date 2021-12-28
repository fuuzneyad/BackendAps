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


public class ParserMGCVPCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "MGCVP";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	
	public ParserMGCVPCommandHandlerFactory() {
		headersMap.put("FIRST", 
				new ConfiguredHeader[] {
				new ConfiguredHeader("VLR      ", true),
				new ConfiguredHeader("VLRADDR            ", true),
				new ConfiguredHeader("MAPV     ", true),
				new ConfiguredHeader("NRIV"),

			}
		);

	}

	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return command;
	}

	
	public String[] getTableName(){
		return new String[] {command};
	}
	
	
	@Override
	public CommandHandler create(Parser extractor, String command,String params, DataListener listener, Context ctx,
			AbstractInitiator cynapseInit){

		return new ParserMGCVPCommandHandler(extractor, listener, command, params, headersMap, cynapseInit);
	}

	
	@Override
	public String getTableSchema() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		// TODO Auto-generated method stub
		return null;
	}
}

