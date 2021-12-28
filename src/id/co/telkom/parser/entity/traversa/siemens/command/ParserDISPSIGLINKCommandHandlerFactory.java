package id.co.telkom.parser.entity.traversa.siemens.command;


import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.CommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserDISPSIGLINKCommandHandlerFactory implements CommandHandlerFactory {
	private static final String command = "DISPSIGLINK";
	private final ConfiguredHeader[] headers;
	
	public ParserDISPSIGLINKCommandHandlerFactory(){
		headers = new ConfiguredHeader[]{
				new ConfiguredHeader("NR"," Nr.  ".length()),
				new ConfiguredHeader("NET_NAME"," Net name       ".length()),
				new ConfiguredHeader("NET_ID"," Net ID   ".length()),
				new ConfiguredHeader("LINKSET_NAME"," Link set name  ".length()), 
				new ConfiguredHeader("LINKSET_ID"," Link set ".length()),
				new ConfiguredHeader("LINK_CODE"," Link code ".length()),
				new ConfiguredHeader("ADMIN_STATE"," Admin. sta ".length()),					
				new ConfiguredHeader("OPR_STATE"," Operational ".length()),
				new ConfiguredHeader("DATA_LINK_NAME"," Data link name ".length()),
				new ConfiguredHeader("DATA_LINK_ID"," Data link ".length()),				
				new ConfiguredHeader("BW_IDTFR"," Bandwidth  ".length()+10," Bandwidth  ".length()),
				new ConfiguredHeader("PROT_PROF_NAME"," Protocol profi ".length()),
				new ConfiguredHeader(" VMS        "),
				new ConfiguredHeader("EXT_PROBLEM_LIST"," Extended current problem list                                ".length()+150," Extended current problem list                                ".length())
			
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
		return new ParserDISPSIGLINKCommandHandler(extractor, command, params, ctx, headers, listener, cynapseInit);
	}

	@Override
	public String getTableSchema() {
		return "";
	}
	

}
