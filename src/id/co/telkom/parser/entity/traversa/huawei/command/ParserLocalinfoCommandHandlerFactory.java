package id.co.telkom.parser.entity.traversa.huawei.command;


import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.CommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserLocalinfoCommandHandlerFactory  implements CommandHandlerFactory{
	private static final String command = "LOCALINFO";
	private final ConfiguredHeader[] headers;
	
	public ParserLocalinfoCommandHandlerFactory(){
		headers = new ConfiguredHeader[]{
				new ConfiguredHeader("LO_OFFICE_NAME",15),
				new ConfiguredHeader("INT_NET_VALID", 7),
				new ConfiguredHeader("INT_RSV_NET_VALID", 7),
				new ConfiguredHeader("NAT_NET_VALID", 7),
				new ConfiguredHeader("NAT_RSV_NET_VALID", 7),
				new ConfiguredHeader("FIRST_SRC_NET", 30),
				new ConfiguredHeader("SECOND_SRC_NET", 30),
				new ConfiguredHeader("THIRD_SRC_NET", 30),
				new ConfiguredHeader("FOURTH_SRC_NET", 30),
				new ConfiguredHeader("INT_NET_STRUCT", 30),
				new ConfiguredHeader("INT_RSV_NET_STRUCT", 30),
				new ConfiguredHeader("NAT_NET_STRUCT", 30),
				new ConfiguredHeader("NAT_RSV_NET_STRUCT", 30),
				new ConfiguredHeader("STP_FUNC", 7),
				new ConfiguredHeader("RESTART_FUNC", 7),
				new ConfiguredHeader("LOCAL_GT", 25)
		};
	}
	
	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return new ParserLocalinfoCommandHandler(extractor, command, params, ctx, headers, listener);
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx,
			AbstractInitiator cynapseInit) {
		// TODO Auto-generated method stub
		return new ParserLocalinfoCommandHandler(extractor, command, params, ctx, headers, listener);
	}

	@Override
	public String getTableSchema() {
		// TODO Auto-generated method stub
		return null;
	}

}
