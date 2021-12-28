package id.co.telkom.parser.entity.traversa.nokia.msscommand;

import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserZW7ICommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "ZW7I";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	
	public ParserZW7ICommandHandlerFactory() {
		headersMap.put("FEAT",
				new ConfiguredHeader[]{
				new ConfiguredHeader("FEATURE_CODE",50),
				new ConfiguredHeader("FEATURE_NAME",50),
				new ConfiguredHeader("FEATURE_STATE",50),
				new ConfiguredHeader("FEATURE_CAPACITY",50)
			}
		);
		headersMap.put("CAP",
				new ConfiguredHeader[]{
				new ConfiguredHeader("FEATURE_CODE"),
				new ConfiguredHeader("CAPACITY_USAGE")//,
				//new ConfiguredHeader("REQUEST_STATUS")
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
		if(params.contains("FEA,FULL"))
			return new ParserZW7IFeaCommandHandler( extractor, listener, getCommand(), params,headersMap.get("FEAT"),cynapseInit);
		else
			return new ParserZW7ILicCommandHandler( extractor, listener, getCommand(), params,headersMap.get("CAP"),cynapseInit);
			
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
