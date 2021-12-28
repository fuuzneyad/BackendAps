package id.co.telkom.parser.entity.traversa.huawei.command;


import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.entity.traversa.common.InitiatorCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;

public class InitiatorLocalinfoCommandHandlerFactory  implements InitiatorCommandHandlerFactory{
	private static final String command = "LOCALINFO";
	private final ConfiguredHeader[] headers;
	
	public InitiatorLocalinfoCommandHandlerFactory(){
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
			String params, Context ctx, GlobalBuffer buf) {
		return new InitiatorLocalinfoCommandHandler(extractor, command, params, ctx, buf, headers);
	}

}
