package id.co.telkom.parser.entity.traversa.siemens.command;


import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.CommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserDISPSPCLGPACommandHandlerFactory implements CommandHandlerFactory {
	private static final String command = "DISPSPCLGPA";
	private final ConfiguredHeader[][] headers;
	
	public ParserDISPSPCLGPACommandHandlerFactory(){
		headers = 	new ConfiguredHeader[][]{
				new ConfiguredHeader[]{
					new ConfiguredHeader("SPAPLOC_ID"," ID   ".length()),
					new ConfiguredHeader("SPAPLOC_NAME"," Name         ".length()),
					new ConfiguredHeader("SPCLGPA_ID"," ID   ".length()),
					new ConfiguredHeader("SPCLGPA_NAME", " Name         ".length()), 
					
					new ConfiguredHeader("INCLUDE_SPC", " SPC   ".length()),
					new ConfiguredHeader("INCLUDE_GT", " GT    ".length()),
					new ConfiguredHeader("GT_NA"," NA            ".length()+10," NA            ".length()),
					new ConfiguredHeader("GT_TTID"," TTID                      ".length()),
					new ConfiguredHeader("GT_NP"," NP                 ".length()+10," NP                 ".length())
				},
				new ConfiguredHeader[]{
					new ConfiguredHeader("SPAPLOC_ID"," ID   ".length()),
					new ConfiguredHeader("SPAPLOC_NAME"," Name         ".length()),
					new ConfiguredHeader("SPCLGPA_ID"," ID   ".length()),
					new ConfiguredHeader("SPCLGPA_NAME", " Name         ".length()),
					
					new ConfiguredHeader("ADDR_TYPE", " Indicator    ".length()),
					new ConfiguredHeader("SPLNK_ID"," ID    ".length()),
					new ConfiguredHeader("SPLNK_NAME"," Name         ".length()),					
//					new ConfiguredHeader("SPLNK_PTR_ID", " ID    ".length()),
//					new ConfiguredHeader("SPLNK_PTR_NAME", " Name         ".length()),						
					new ConfiguredHeader("GT"," Address Information      ".length())

				},		
				
				//STP
				new ConfiguredHeader[]{
						new ConfiguredHeader("SPAPLOC_ID"," ID   ".length()),
						new ConfiguredHeader("SPAPLOC_NAME"," Name         ".length()),
						new ConfiguredHeader("SPCLGPA_ID"," ID   ".length()),
						new ConfiguredHeader("SPCLGPA_NAME", " Name         ".length()),
						
						new ConfiguredHeader("GT_NA"," NA            ".length()+10," NA            ".length()),
						new ConfiguredHeader("GT_TTID"," TTID                      ".length()),
						new ConfiguredHeader("GT_NP"," NP                 ".length()+10," NP                 ".length())

				},
				new ConfiguredHeader[]{
						new ConfiguredHeader("SPAPLOC_ID"," ID   ".length()),
						new ConfiguredHeader("SPAPLOC_NAME"," Name         ".length()),
						new ConfiguredHeader("SPCLGPA_ID"," ID   ".length()),
						new ConfiguredHeader("SPCLGPA_NAME", " Name         ".length()),
						
						new ConfiguredHeader("INCL_SPC4SSN"," SPC for SSN ".length()),
						new ConfiguredHeader("INCL_SPC4GT"," SPC for GT  ".length()),
						new ConfiguredHeader("INCL_GT"," GT    ".length()),

				}
//				,				
//				new ConfiguredHeader[]{
//						new ConfiguredHeader("SPAPLOC_ID"," ID   ".length()),
//						new ConfiguredHeader("SPAPLOC_NAME"," Name         ".length()),
//						new ConfiguredHeader("SPCLGPA_ID"," ID   ".length()),
//						new ConfiguredHeader("SPCLGPA_NAME", " Name         ".length()),
//						
//						new ConfiguredHeader("ADDR_TYPE_INDTR"," Address Type ".length()),
//						new ConfiguredHeader("SPLNK_ID"," ID    ".length()),
//						new ConfiguredHeader("SPLNK_NAME"," Name         ".length()),
//						new ConfiguredHeader("GT"," Address Information      ".length())
//
//				}				
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
		return new ParserDISPSPCLGPACommandHandler(extractor, command, params, ctx, headers, listener, cynapseInit);
	}

	@Override
	public String getTableSchema() {
		return "";
	}
	

}
