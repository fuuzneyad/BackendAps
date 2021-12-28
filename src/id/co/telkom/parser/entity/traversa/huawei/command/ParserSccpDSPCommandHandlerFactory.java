package id.co.telkom.parser.entity.traversa.huawei.command;


import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.CommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;

public class ParserSccpDSPCommandHandlerFactory  implements CommandHandlerFactory{
	private static final String command = "SCCPDSPTABLE";
	private final ConfiguredHeader[] headers;
	
	public ParserSccpDSPCommandHandlerFactory(){
		headers = new ConfiguredHeader[]{
				new ConfiguredHeader("DSP_NAME"," DSP name ".length()),
				new ConfiguredHeader("INT_NET_DPC","International network DPC  ".length()),				
				new ConfiguredHeader("INT_RSV_NET_DPC","International reserved network DPC  ".length()),				
				new ConfiguredHeader("NAT_NET_DPC","National network DPC  ".length()),				
				new ConfiguredHeader("NAT_RSV_NET_DPC","National reserved network DPC  ".length()),				
				new ConfiguredHeader("OPC","OPC       ".length()),
				new ConfiguredHeader("SIG_SPEC","Signaling specification  ".length()),				
				new ConfiguredHeader("SCCP_DSP_IDX","SCCP DSP index".length())
		};
	}
	
	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return new ParserSccpDSPCommandHandler(extractor, command, params, ctx, headers, listener);
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx,
			AbstractInitiator cynapseInit) {
		return new ParserSccpDSPCommandHandler(extractor, command, params, ctx, headers, listener, (GlobalBuffer)cynapseInit.getMappingModel());
	}

	@Override
	public String getTableSchema() {
		return null;
	}

}
