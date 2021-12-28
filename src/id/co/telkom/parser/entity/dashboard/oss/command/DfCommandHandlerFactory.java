package id.co.telkom.parser.entity.dashboard.oss.command;

import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class DfCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "DF";
	private final Map<String,ConfiguredHeader[]> headerMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	
	public DfCommandHandlerFactory() {
		headerMap.put("-H", new ConfiguredHeader[] {
				new ConfiguredHeader("FILESYSTEM             "),
				new ConfiguredHeader("SIZE   "),
				new ConfiguredHeader("USED  "),
				new ConfiguredHeader("AVAIL "),
				new ConfiguredHeader("CAPACIITY  "),
				new ConfiguredHeader("MOUNTED_ON")
		});
		headerMap.put("-K", new ConfiguredHeader[] {
				new ConfiguredHeader("FILESYSTEM    "),
				new ConfiguredHeader("SIZE   "),
				new ConfiguredHeader("USED  "),
				new ConfiguredHeader("AVAIL "),
				new ConfiguredHeader("CAPACIITY  "),
				new ConfiguredHeader("MOUNTED_ON")
		});
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
			String params, DataListener listener, Context ctx) {
		if(params.contains("H"))
			return new DfCommandHandler( extractor, listener, command, params, headerMap.get("-H"));
		else if(params.contains("K"))
			return new DfCommandHandler( extractor, listener, command, params, headerMap.get("-K"));
		return new UnhandledCommandHandler(extractor, command, params);
	}
	
	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx, AbstractInitiator cynapseInit) {
		return null;
	}
	
	@Override
	public String getTableSchema() {
		return "";
	}

}
