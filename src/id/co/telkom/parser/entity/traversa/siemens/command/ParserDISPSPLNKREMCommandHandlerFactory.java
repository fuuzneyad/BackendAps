package id.co.telkom.parser.entity.traversa.siemens.command;


import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.CommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserDISPSPLNKREMCommandHandlerFactory implements CommandHandlerFactory {
	private static final String command = "DISPSPLNKREM";
	private final ConfiguredHeader[][] headers;
	
	public ParserDISPSPLNKREMCommandHandlerFactory(){
		headers = new ConfiguredHeader[][]{
				new ConfiguredHeader[]{
					new ConfiguredHeader("SPLNKREM_IDENT_ID"," ID   ".length()),
					new ConfiguredHeader("SPLNKREM_IDENT_NAME"," Name         ".length()),
					
					new ConfiguredHeader("SPLNKREM_IDENT_NET_ID"," Net ID ".length()),
					new ConfiguredHeader("SPLNKREM_IDENT_NET_NAME"," Net name     ".length()),
					new ConfiguredHeader("SPLNKREM_IDENT_DPC"," DPC           ".length()),
					new ConfiguredHeader("SPLNKREM_IDENT_DPC_NAME"," DPC name     ".length()),
					new ConfiguredHeader("SPLNKREM_IDENT_DPC_TYPE"," Type ".length()),					
					new ConfiguredHeader("BROADCAST_SCOPE"," Scope     ".length()),					
					new ConfiguredHeader("SPCAREA_ID"," ID   ".length()),
					new ConfiguredHeader("SPCAREA_NAME"," Name         ".length()),
					
				},
				new ConfiguredHeader[]{
					new ConfiguredHeader("SPLNKREM_IDENT_ID"," ID   ".length()),
					new ConfiguredHeader("SPLNKREM_IDENT_NAME"," Name         ".length()),
					
					new ConfiguredHeader("ORIG_SPLNKLOC_ID"," ID   ".length()),
					new ConfiguredHeader("ORIG_SPLNKLOC_NAME"," Name         ".length()),	
					new ConfiguredHeader("SCTP_ASSOC_ID"," ID   ".length()),
					new ConfiguredHeader("SCTP_ASSOC_NAME"," Name         ".length()),
					new ConfiguredHeader("SUA_VERSION"," Version ".length()),
					new ConfiguredHeader("SUA_ASP_INIT"," ASP Init ".length()),
					new ConfiguredHeader("SUA_AS_NOTIFY"," AS Notify ".length()),					
					new ConfiguredHeader("SUA_BUNDLE_FACTOR"," SPC Bundle Factor ".length())

				},	
				new ConfiguredHeader[]{
					new ConfiguredHeader("SPLNKREM_IDENT_ID"," ID   ".length()),
					new ConfiguredHeader("SPLNKREM_IDENT_NAME"," Name         ".length()),
					
					new ConfiguredHeader("SPENHS_ID"," ID        ".length()),
					new ConfiguredHeader("SPENHS_NAME"," Name         ".length()),
					new ConfiguredHeader("ROUTING_CONTEXT"," Routing Context ".length())
				
				},	
				new ConfiguredHeader[]{//new mss
						new ConfiguredHeader("SPLNKREM_IDENT_ID"," SPLNKREM ID ".length()),
						new ConfiguredHeader("SPLNKREM_IDENT_NAME"," SPLNKREM Name ".length()),
						
						new ConfiguredHeader("NET_ID"," Net ID ".length()),
						new ConfiguredHeader("NET_NAME"," Net name     ".length()),
						new ConfiguredHeader("SPLNKREM_IDENT_DPC"," DPC           ".length()),
						new ConfiguredHeader("DPC_NAME"," DPC name      ".length()),
						new ConfiguredHeader("ASS_FLAG"," Ass.Conn.SectionFl ".length())
					
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
		return new ParserDISPSPLNKREMCommandHandler(extractor, command, params, ctx, headers, listener, cynapseInit);
	}

	@Override
	public String getTableSchema() {
		return "";
	}
	

}
