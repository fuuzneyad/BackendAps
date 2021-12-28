package id.co.telkom.parser.entity.traversa.ericsson.msscommand;


import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.entity.traversa.common.InitiatorCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;

public class InitiatorMGRRPCommandHandlerFactory implements InitiatorCommandHandlerFactory {
	private static final String command = "MGRRP";
	private final ConfiguredHeader[] header;
	
	public InitiatorMGRRPCommandHandlerFactory() {
		header= new ConfiguredHeader[]{
				new ConfiguredHeader("MSRNS           ", true, "MSRNS           ".length()),
				new ConfiguredHeader("R          ", true, "R          ".length()),
				new ConfiguredHeader("USAGES           ", true, "USAGES           ".length()), // error in mySql
				new ConfiguredHeader("LAI            "),
				new ConfiguredHeader("CELLCON  "),
				new ConfiguredHeader("AIDX")
		};
	}

	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, Context ctx,  GlobalBuffer buf) {
		return new InitiatorMGRRPCommandHandler(extractor, command, params, buf, ctx, header);
	}
	
}
