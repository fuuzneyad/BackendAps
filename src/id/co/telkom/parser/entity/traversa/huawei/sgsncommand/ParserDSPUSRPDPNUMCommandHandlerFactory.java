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

public class ParserDSPUSRPDPNUMCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "DSP_USRPDPNUM";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();

	public ParserDSPUSRPDPNUMCommandHandlerFactory() {
		headersMap.put("MASTER", new ConfiguredHeader[]{
				new ConfiguredHeader("SUBRACK_NO", " Subrack No.  ".length()),
				new ConfiguredHeader("SLOT_NO","Slot No.  ".length()),
				new ConfiguredHeader("PROCESS_NO","Process No.  ".length()),
				new ConfiguredHeader("STATIC_USER_NUMBER","Static user number  ".length()),
				new ConfiguredHeader("APN_CONF_NUMBER","APN configuration number  ".length()),
				new ConfiguredHeader("NUMBER_OF_DYNAMIC_2G_MMS","Number of dynamic 2G MMs  ".length()),
				new ConfiguredHeader("NUMBER_OF_DYNAMIC_3G_MMS","Number of dynamic 3G MMs  ".length()),
				new ConfiguredHeader("NUMBER_OF_DYNAMIC_4G_MMS","Number of dynamic 4G MMs  ".length()),
				new ConfiguredHeader("NUMBER_OF_DYNAMIC_2G_PDP","Number of dynamic 2G PDPs  ".length()),
				new ConfiguredHeader("NUMBER_OF_DYNAMIC_3G_PDP","Number of dynamic 3G PDPs  ".length()),
				new ConfiguredHeader("NUMBER_OF_DYNAMIC_4G_BEARER","Number of Dynamic 4G bearers".length())
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
		return new ParserDSPUSRPDPNUMCommandHandler( extractor, listener, getCommand(), params,headersMap, cynapseInit);
	}
	
	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return new ParserDSPUSRPDPNUMCommandHandler( extractor, listener, getCommand(), params, headersMap, null);
	}
	
	@Override
	public String getTableSchema() {
		return "";
	}
	
}
