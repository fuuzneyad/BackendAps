package id.co.telkom.parser.entity.traversa.siemens.command;


import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.CommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserDISPSPRNSCommandHandlerFactory implements CommandHandlerFactory {
	private static final String command = "DISPSPRNS";
	private final ConfiguredHeader[][] headers;
	
	public ParserDISPSPRNSCommandHandlerFactory(){
		headers = new ConfiguredHeader[][]{
				new ConfiguredHeader[]{
					new ConfiguredHeader("SPRNS_ID"," ID   ".length()),
					new ConfiguredHeader("SPRNS_NAME"," Name     ".length()),					
					new ConfiguredHeader("PRIMARY_1_ID"," ID   ".length()),
					new ConfiguredHeader("PRIMARY_1_NAME"," Name         ".length()), 					
					new ConfiguredHeader("PRIMARY_2_ID"," ID   ".length()),
					new ConfiguredHeader("PRIMARY_2_NAME"," Name         ".length()),
					new ConfiguredHeader("BACKUP_1_ID"," ID   ".length()),
					new ConfiguredHeader("BACKUP_1_NAME"," Name         ".length()),					
					new ConfiguredHeader("BACKUP_2_ID"," ID   ".length()),
					new ConfiguredHeader("BACKUP_2_NAME"," Name         ".length())
				},
				new ConfiguredHeader[]{
					new ConfiguredHeader("SPRNS_ID"," ID   ".length()),
					new ConfiguredHeader("SPRNS_NAME"," Name     ".length()),			
					new ConfiguredHeader("SHARING_MODE"," Sharing              ".length()),					
					new ConfiguredHeader("SSID"," SSID                      ".length()),					
					new ConfiguredHeader("SSID_PRESENT"," SSID Present".length())
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
		return new ParserDISPSPRNSCommandHandler(extractor, command, params, ctx, headers, listener, cynapseInit);
	}

	@Override
	public String getTableSchema() {
		return "";
	}
	

}
