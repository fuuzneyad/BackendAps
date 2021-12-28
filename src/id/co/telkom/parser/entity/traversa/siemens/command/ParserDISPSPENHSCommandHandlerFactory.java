package id.co.telkom.parser.entity.traversa.siemens.command;


import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.CommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserDISPSPENHSCommandHandlerFactory implements CommandHandlerFactory {
	private static final String command = "DISPSPENHS";
	private final ConfiguredHeader[][] headers;
	
	public ParserDISPSPENHSCommandHandlerFactory(){
		headers = new ConfiguredHeader[][]{
				new ConfiguredHeader[]{
					new ConfiguredHeader("SPENHS_ID"," SPENHS ID ".length()),
					new ConfiguredHeader("SPENHS_NAME"," SPENHS Name  ".length()),
					
					new ConfiguredHeader("SCCP_ENTITY_SET_TYPE"," Sccp Entity  ".length()+30," Sccp Entity  ".length()),
					new ConfiguredHeader("DEST_TYPE"," Dest. Type ".length()),
					new ConfiguredHeader("PRIM_LIST_ID","  ID       ".length()),					
					new ConfiguredHeader("PRIM_LIST_NAME"," Name         ".length()),		
					new ConfiguredHeader("PRIM_LIST_RATE_CONTROL"," Rate Control ".length()),					
					new ConfiguredHeader("PRIM_LIST_MSG_RATE"," Msg Rate  ".length())
					
				},
				new ConfiguredHeader[]{
					new ConfiguredHeader("SPENHS_ID"," SPENHS ID ".length()),
					new ConfiguredHeader("SPENHS_NAME"," SPENHS Name  ".length()),
						
					new ConfiguredHeader("SCCP_ENTITY_SET_TYPE"," Sccp Entity  ".length()+30," Sccp Entity  ".length()),
					new ConfiguredHeader("DEST_TYPE"," Dest. Type ".length()),
					
					new ConfiguredHeader("BACKUP_LIST_ID","  ID       ".length()),					
					new ConfiguredHeader("BACKUP_LIST_NAME"," Name         ".length()),			
					new ConfiguredHeader("BACKUP_LIST_RATE_CONTROL"," Rate Control ".length()),					
					new ConfiguredHeader("BACKUP_LIST_MSG_RATE"," Msg Rate  ".length()),
					new ConfiguredHeader("SSID_OR_NOT"," SSID Or Not     ".length()+30," SSID Or Not     ".length())	
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
		return new ParserDISPSPENHSCommandHandler(extractor, command, params, ctx, headers, listener, cynapseInit);
	}

	@Override
	public String getTableSchema() {
		return "";
	}
	

}
