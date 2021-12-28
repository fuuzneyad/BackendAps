package id.co.telkom.parser.entity.dashboard.oss.command;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class PerformanceCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "PERFORMANCE.SH";
	private final ConfiguredHeader[] header ;
	
	public PerformanceCommandHandlerFactory() {
		header = new ConfiguredHeader[] {
				new ConfiguredHeader("ACCESS"),
				new ConfiguredHeader("IDX"),
				new ConfiguredHeader("OWNER"),
				new ConfiguredHeader("GROUP_OWNER"),
				new ConfiguredHeader("PID"),
				new ConfiguredHeader("MONTH_"),
				new ConfiguredHeader("DATE_"),
				new ConfiguredHeader("TIME"),
				new ConfiguredHeader("FILE")
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
			String params, DataListener listener, Context ctx) {
			return new PerformanceCommandHandler( extractor, listener, command, params, header);
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
