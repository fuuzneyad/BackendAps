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

public class ParserDSPS1APLNKCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "DSP_S1APLNK";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();

	public ParserDSPS1APLNKCommandHandlerFactory() {
		headersMap.put("MASTER", new ConfiguredHeader[]{
				new ConfiguredHeader("SUBRACK_NO", " Subrack No.  ".length()),
				new ConfiguredHeader("SLOT_NO","Slot No.  ".length()),
				new ConfiguredHeader("PROCESS_NO","Process No.  ".length()),
				new ConfiguredHeader("MCC","Mobile Country Code  ".length()),
				new ConfiguredHeader("MNC","Mobile Network Code  ".length()),
				new ConfiguredHeader("IP_ADDR_TYPE1","IP Address Type1  ".length()),
				new ConfiguredHeader("ENODEB_IP_ADDR1","eNodeB IP address1  ".length()),
				new ConfiguredHeader("IP_ADDR_TYPE2","IP Address Type2  ".length()),
				new ConfiguredHeader("ENODEB_IP_ADDR2","eNodeB IP address2  ".length()),
				new ConfiguredHeader("ENODEB_PORT","eNodeB port  ".length()),
				new ConfiguredHeader("ENODEB_ID_TYPE","eNodeB ID type  ".length()),
				new ConfiguredHeader("ENODEB_ID","eNodeB ID  ".length()),
				new ConfiguredHeader("ENODEB_NAME","eNodeB Name                        ".length()),
				new ConfiguredHeader("S1APLE_INDEX","S1APLE Index  ".length()),
				new ConfiguredHeader("LINK_STATUS","Link status  ".length()),
				new ConfiguredHeader("SUBRACK1_S1AP","Subrack No. 1 for S1AP Board  ".length()),
				new ConfiguredHeader("SLOT1_S1AP","Slot No. 1 for S1AP Board  ".length()),
				new ConfiguredHeader("SUBRACK2_S1AP","Subrack No. 2 for S1AP Board  ".length()),
				new ConfiguredHeader("SLOT2_S1AP","Slot No. 2 for S1AP Board  ".length()),
				new ConfiguredHeader("SUBRACK3_S1AP","Subrack No. 3 for S1AP Board  ".length()),
				new ConfiguredHeader("SLOT3_S1AP","Slot No. 3 for S1AP Board  ".length()),
				new ConfiguredHeader("SUBRACK4_S1AP","Subrack No. 3 for S1AP Board  ".length()),
				new ConfiguredHeader("SLOT4_S1AP","Slot No. 3 for S1AP Board  ".length()),
				new ConfiguredHeader("NUM_OF_INTMITTEN_DISCONECT","Number of Intermittent Disconnections".length())
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
		return new ParserDSPS1APLNKCommandHandler( extractor, listener, getCommand(), params,headersMap, cynapseInit);
	}
	
	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return new ParserDSPS1APLNKCommandHandler( extractor, listener, getCommand(), params, headersMap, null);
	}
	
	@Override
	public String getTableSchema() {
		return "";
	}
	
}
