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

public class ParserLSTVRFBRDIPBINDCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "LST_VRFBRDIPBIND";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();

	public ParserLSTVRFBRDIPBINDCommandHandlerFactory() {
		headersMap.put("MASTER", new ConfiguredHeader[]{
				new ConfiguredHeader("SUBRACK", " Subrack No.  ".length()),
				new ConfiguredHeader("SLOT","Slot No.  ".length()),
				new ConfiguredHeader("BOARD_IP_ADDR", "Board ip address  ".length()),
				new ConfiguredHeader("VRF_NAME", "VRF Name     ".length()),
				new ConfiguredHeader("Description")
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
		return new ParserLSTVRFBRDIPBINDCommandHandler( extractor, listener, getCommand(), params,headersMap, cynapseInit);
	}
	
	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return new ParserLSTVRFBRDIPBINDCommandHandler( extractor, listener, getCommand(), params, headersMap, null);
	}
	
	@Override
	public String getTableSchema() {
		return "";
	}
	
}
