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

public class ParserMGPTPCommandHandlerFactory implements MscCommandHandlerFactory{
	private static final String command = "MGPTP";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	
	public ParserMGPTPCommandHandlerFactory() {
		headersMap.put(command,
				new ConfiguredHeader[] {
				new ConfiguredHeader("VLRADDR            "),
				new ConfiguredHeader("NRIV1","NRIV  ".length()),
				new ConfiguredHeader("NRIV2","NRIV  ".length()),
				new ConfiguredHeader("NRIV3","NRIV  ".length()),
				new ConfiguredHeader("NRIV4","NRIV  ".length()),
				new ConfiguredHeader("NRIV5","NRIV  ".length()),
				new ConfiguredHeader("NRIV6","NRIV  ".length()),
				new ConfiguredHeader("NRIV7","NRIV  ".length()),
				new ConfiguredHeader("NRIV8","NRIV".length())
			}
		);
	
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
		return new ParserMGPTPCommandHandler( extractor, listener, command, params,headersMap,cynapseInit);
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
