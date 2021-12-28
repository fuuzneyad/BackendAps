package id.co.telkom.parser.entity.traversa.siemens.command;


import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.CommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserDISPSIGLSETCommandHandlerFactory implements CommandHandlerFactory {
	private static final String command = "DISPSIGLSET";
	private final ConfiguredHeader[][] headers;
	
	public ParserDISPSIGLSETCommandHandlerFactory(){
		headers = new ConfiguredHeader[][]{
				new ConfiguredHeader[]{
					new ConfiguredHeader("NR"," Nr.  ".length()),
					new ConfiguredHeader("NET_NAME"," Net name       ".length()),
					new ConfiguredHeader("NET_ID"," Net ID ".length()),
					new ConfiguredHeader("LINKSET_NAME"," Link set name  ".length()),
					new ConfiguredHeader("LINKSET_ID"," Link set  ".length()),
					new ConfiguredHeader("ADJACENT_DPC"," Adjacent DPC  ".length()),
					new ConfiguredHeader("OP_STATE"," Operational  ".length()),						
					new ConfiguredHeader("PERIODIC_LINKSET_TEST"," Periodic  ".length()),					
					new ConfiguredHeader("ALARM_STATUS"," Alarm status      ".length()),
					new ConfiguredHeader("VMS"," VMS   ".length()),
					new ConfiguredHeader("LOAD_SHARE_ALGRTM"," Load share algorithm        ".length()),
					new ConfiguredHeader("LINKSET_TYPE"," Link set  ".length())
						
					},	
				new ConfiguredHeader[]{
					new ConfiguredHeader("NR"," Nr.  ".length()),
					new ConfiguredHeader("NET_NAME"," Net name       ".length()),
					new ConfiguredHeader("NET_ID"," Net ID ".length()),
					new ConfiguredHeader("LINKSET_NAME"," Link set name  ".length()),
					new ConfiguredHeader("LINKSET_ID"," Link set  ".length()),
					new ConfiguredHeader("ADJACENT_DPC"," Adjacent DPC     ".length()),
					new ConfiguredHeader("OP_STATE"," Operational  ".length()),
					new ConfiguredHeader("PERIODIC_LINKSET_TEST"," Periodic  ".length()),					
					new ConfiguredHeader("ALARM_STATUS"," Alarm status      ".length()),
					new ConfiguredHeader("VMS"," VMS ".length()),
					new ConfiguredHeader("LOAD_SHARE_ALGRTM"," algorithm ".length()),
					new ConfiguredHeader("LINKSET_TYPE"," Link set  ".length()),
					new ConfiguredHeader("MAX_MSU_LENGTH"," Maximum ".length())
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
		return new ParserDISPSIGLSETCommandHandler(extractor, command, params, ctx, headers, listener, cynapseInit);
	}

	@Override
	public String getTableSchema() {
		return "";
	}
	

}
