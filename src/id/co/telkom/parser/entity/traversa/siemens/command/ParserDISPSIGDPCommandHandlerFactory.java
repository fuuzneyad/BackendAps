package id.co.telkom.parser.entity.traversa.siemens.command;


import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.CommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserDISPSIGDPCommandHandlerFactory implements CommandHandlerFactory {
	private static final String command = "DISPSIGDP";
	private final ConfiguredHeader[] headers;
	
	public ParserDISPSIGDPCommandHandlerFactory(){
		headers = 	new ConfiguredHeader[]{
				new ConfiguredHeader("NR"," Nr.  ".length()),
				new ConfiguredHeader("NET_NAME"," Net name       ".length()),
				new ConfiguredHeader("NET_ID"," Net ID   ".length()),
				new ConfiguredHeader("DPC_NAME", " DPC name       ".length()), 
				new ConfiguredHeader(" DPC           "),
				new ConfiguredHeader("ADMIN_STATE", " Admin. sta ".length()),
				new ConfiguredHeader("OP_STATE", " Operational ".length()),
				new ConfiguredHeader("LOADSHARING", " Loadsharing  ".length()),					
				new ConfiguredHeader("ALARM_STAT", " Alarm status   ".length()),
				new ConfiguredHeader("ALARM_PROFILE_MP", " Alarm profile  ".length()),
				new ConfiguredHeader("ALARM_SMOOT_TIME", " Alarm smoo ".length()),
				new ConfiguredHeader("EXT_CUR_PROBLEM", " Extended current problem list                      ".length()+150, " Extended current problem list                      ".length()),
				new ConfiguredHeader("CONGESTION_STT", " Congested state  /   ".length())
				
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
		return new ParserDISPSIGDPCommandHandler(extractor, command, params, ctx, headers, listener, cynapseInit);
	}

	@Override
	public String getTableSchema() {
		return "";
	}
	

}
