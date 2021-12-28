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

public class ParserLSTS1APLNKCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "DSP_S1PAGING";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();

	public ParserLSTS1APLNKCommandHandlerFactory() {
		headersMap.put("MASTER", new ConfiguredHeader[]{
				new ConfiguredHeader("SUBRACK_NO", " Subrack No.  ".length()),
				new ConfiguredHeader("SLOT_NO","Slot No.  ".length()),
				new ConfiguredHeader("PROCESS_NO","Process No.  ".length()),
				new ConfiguredHeader("MCC_MNC  "),
				new ConfiguredHeader("ENODEB_ID_TYPE","eNodeB ID type  ".length()),
				new ConfiguredHeader("ENODEB_ID","eNodeB ID  ".length()),
				new ConfiguredHeader("TRACKING_AREA_ID","Tracking area ID".length())
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
		return new ParserLSTS1PAGINGCommandHandler( extractor, listener, getCommand(), params,headersMap, cynapseInit);
	}
	
	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return new ParserLSTS1PAGINGCommandHandler( extractor, listener, getCommand(), params, headersMap, null);
	}
	
	@Override
	public String getTableSchema() {
		return "";
	}
	
}
