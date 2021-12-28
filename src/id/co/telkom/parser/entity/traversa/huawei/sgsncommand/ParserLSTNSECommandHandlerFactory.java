package id.co.telkom.parser.entity.traversa.huawei.sgsncommand;

import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserLSTNSECommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "LST_NSE";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();

	public ParserLSTNSECommandHandlerFactory() {
		headersMap.put("MASTER", new ConfiguredHeader[]{
				new ConfiguredHeader("NSE_DIRECTION", " NSE connection direction  ".length()),
				new ConfiguredHeader("NSE_ID","NSE ID  ".length()),
				new ConfiguredHeader("BVCI    "),
				new ConfiguredHeader("SUBRACK_NO","Subrack No.  ".length()),
				new ConfiguredHeader("SLOT_NO","Slot No.  ".length()),
				new ConfiguredHeader("PROCESS_NO","Process No.  ".length()),
				new ConfiguredHeader("BSS_NO","BSS No.  ".length()),
				new ConfiguredHeader("FLUSH_TIMER","Flush Timer(ms)  ".length()),
				new ConfiguredHeader("SUPPORT_PFC","Support PFC                           ".length()),
				new ConfiguredHeader("SUPPORT_CBL","Support CBL                           ".length()),
				new ConfiguredHeader("SUPPORT_INR","Support INR  ".length()),
				new ConfiguredHeader("SUPPORT_LCS","Support LCS                               ".length()),
				new ConfiguredHeader("SUPPORT_RIM","Support RIM                               ".length()),
				new ConfiguredHeader("SUPPORT_PFCFC","Support PFCFC                             ".length()),
				new ConfiguredHeader("INCLUDE_ARP_IE","Include ARP IE  ".length()),
				new ConfiguredHeader("INCLUDE_RA_CAP_IE","Include RA Capability IE  ".length()),
				new ConfiguredHeader("BEARER_TYPE","Bearer type  ".length()),
				new ConfiguredHeader("NSE_CONFIG_TYPE","NSE config type  ".length()),
				new ConfiguredHeader("SUPPORT_GB_FLEX","Support Gb-Flex  ".length()),
				new ConfiguredHeader("SUPPORT_SPEC_SC","Support special service class  ".length()),
				new ConfiguredHeader("BSS_SUPPORT_QOS_VER","BSS support Qos version  ".length()),
				new ConfiguredHeader("SUPPORT_MOCN","Support MOCN                              ".length()),
				new ConfiguredHeader("SUPPORT_SPID","Support SPID".length())
		});
	}

	@Override
	public String getCommand() {
		return command;
	}
	
	public String[] getTableName(){
		return new String[] {command};
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx, AbstractInitiator cynapseInit) {
		return new ParserLSTNSECommandHandler( extractor, listener, getCommand(), params,headersMap, cynapseInit);
	}
	
	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return new ParserLSTNSECommandHandler( extractor, listener, getCommand(), params, headersMap, null);
	}
	
	@Override
	public String getTableSchema() {
		return "";
	}
	
}
