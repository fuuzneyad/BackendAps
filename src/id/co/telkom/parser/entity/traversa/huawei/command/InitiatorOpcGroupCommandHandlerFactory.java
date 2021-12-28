package id.co.telkom.parser.entity.traversa.huawei.command;


import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.entity.traversa.common.InitiatorCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;

public class InitiatorOpcGroupCommandHandlerFactory  implements InitiatorCommandHandlerFactory{
	private static final String command = "OPCGROUP";
	private final ConfiguredHeader[] headers;
	
	public InitiatorOpcGroupCommandHandlerFactory(){
		headers = new ConfiguredHeader[]{
				new ConfiguredHeader("LO_OFFICE_NAME",15),
				new ConfiguredHeader("OPC_INDEX", 7),
				new ConfiguredHeader("INT_NET_CODE", 10),
				new ConfiguredHeader("INT_RSV_NET_CODE", 10),
				
				new ConfiguredHeader("NAT_NET_CODE", 10),
				new ConfiguredHeader("NAT_RSV_NET_CODE", 10),
				new ConfiguredHeader("ASS_CODE_1", 15),
				new ConfiguredHeader("ASS_CODE_2", 15),
				new ConfiguredHeader("ASS_CODE_3", 15),
				new ConfiguredHeader("ASS_CODE_4", 15),
				new ConfiguredHeader("ASS_CODE_5", 15),
				new ConfiguredHeader("ASS_CODE_6", 15),
				new ConfiguredHeader("ASS_CODE_7", 15),
				new ConfiguredHeader("ASS_CODE_8", 15)
		};
	}
	
	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, Context ctx, GlobalBuffer buf) {
		return new InitiatorOpcGroupCommandHandler(extractor, command, params, ctx, buf, headers);
	}

}
