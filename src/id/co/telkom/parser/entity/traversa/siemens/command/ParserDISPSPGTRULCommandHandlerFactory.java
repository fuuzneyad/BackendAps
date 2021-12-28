package id.co.telkom.parser.entity.traversa.siemens.command;


import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.CommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserDISPSPGTRULCommandHandlerFactory implements CommandHandlerFactory {
	private static final String command = "DISPSPGTRUL";
	private final ConfiguredHeader[][] headers;
	
	public ParserDISPSPGTRULCommandHandlerFactory(){
		headers = new ConfiguredHeader[][]{
				new ConfiguredHeader[]{
					new ConfiguredHeader("SPGTTRL_ID"," ID  ".length()),
					new ConfiguredHeader("SPGTTRL_NAME"," Name         ".length()),
					new ConfiguredHeader("SPGTRUL_ID"," ID     ".length()),
					new ConfiguredHeader("SPGTRUL_NAME"," Name         ".length()), 
					new ConfiguredHeader("GT_ADDR_INFO_RANGE"," Address Information (Range)                                  ".length()),
					new ConfiguredHeader("SPGTCRUL_ID"," ID   ".length()),
					new ConfiguredHeader("SPGTCRUL_NAME"," Name         ".length())
				},
				new ConfiguredHeader[]{
					new ConfiguredHeader("SPGTTRL_ID"," ID  ".length()),
					new ConfiguredHeader("SPGTTRL_NAME"," Name         ".length()),
					new ConfiguredHeader("SPGTRUL_ID"," ID     ".length()),
					new ConfiguredHeader("SPGTRUL_NAME"," Name         ".length()), 
					new ConfiguredHeader("POINTER_TYPE"," Type     ".length()),
					new ConfiguredHeader("POINTER_ID"," ID   ".length()),
					new ConfiguredHeader("POINTER_NAME"," Name         ".length()),
					new ConfiguredHeader("INCLUDE_SPC"," SPC     ".length()),
					new ConfiguredHeader("POSSIBLE_PORTED_NUMBER"," Ported Number ".length()),
					new ConfiguredHeader("TRAFFIC_TYPE"," Type          ".length()),
					new ConfiguredHeader("RESUME_AT_DIGIT"," At Digit ".length())
						
				}
		};
	}

	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return null;
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx,
			AbstractInitiator cynapseInit) {
		return new ParserDISPSPGTRULCommandHandler(extractor, command, params, ctx, headers, listener, cynapseInit);
	}

	@Override
	public String getTableSchema() {
		return "";
	}
	

}
