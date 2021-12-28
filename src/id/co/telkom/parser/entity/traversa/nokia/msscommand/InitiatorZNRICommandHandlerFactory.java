package id.co.telkom.parser.entity.traversa.nokia.msscommand;

import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.entity.traversa.common.InitiatorCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;

public class InitiatorZNRICommandHandlerFactory implements InitiatorCommandHandlerFactory{
	private final static String command="NRI";
	private final ConfiguredHeader[] header;
	
	public InitiatorZNRICommandHandlerFactory(){
		header=new ConfiguredHeader[]{
						new ConfiguredHeader("NET  "),
						new ConfiguredHeader("SP_CODE_H_D         "),
						new ConfiguredHeader("SP_NAME  "),
						new ConfiguredHeader("SP_TYPE  "),
						new ConfiguredHeader("SS7_STAND", "STAND  ".length()),
						new ConfiguredHeader("SUBFIELD_INFO_COUNT", "COUNT  ".length()),
						new ConfiguredHeader("SUBFIELD_INFO_BIT_LENGTHS", "BIT LENGTHS".length())
					};
	}
	
	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, Context ctx, GlobalBuffer buf) {
		return new InitiatorZNRICommandHandler(extractor, command, params, buf, ctx, header);
	}

}
