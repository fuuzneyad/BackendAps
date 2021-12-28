package id.co.telkom.parser.entity.traversa.huawei.command;


import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.CommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserSccpGTCommandHandlerFactory  implements CommandHandlerFactory{
	private static final String command = "SCCPGTTABLE";
	private final ConfiguredHeader[] headers;
	
	public ParserSccpGTCommandHandlerFactory(){
		headers = new ConfiguredHeader[]{
				new ConfiguredHeader("GT_NAME"," GT name    ".length()),//0
				new ConfiguredHeader("NETWORK_INDICATOR","Network indicator  ".length()),//1
				new ConfiguredHeader("GT_INDICATOR","GT indicator  ".length()),//2
				new ConfiguredHeader("TRANSLATION_TYPE","Translation type  ".length()),//3
				new ConfiguredHeader("NUM_PLAN","Numbering plan                 ".length()),//4
				new ConfiguredHeader("ADDR_NATURE_IDCTR","Address nature indicator  ".length()),//5
				new ConfiguredHeader("GT_ADDR_INFO","GT address information  ".length()),//6
				new ConfiguredHeader("GT_LENGTH","GT length  ".length()),//7
				new ConfiguredHeader("TRANS_RSLT_TYP","Translation result type  ".length()),//8
				new ConfiguredHeader("SPC","SPC       ".length()),//9				
				new ConfiguredHeader("SSN","SSN        ".length()),//10
				new ConfiguredHeader("LOAD_SHARE_DSP_GR_NAME","Load share DSP group name  ".length()),//11
				new ConfiguredHeader("NEW_GT_NAME","New GT name  ".length()),//12
				new ConfiguredHeader("GT_INDEX","GT index".length())//13
		};
	}
	
	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return new ParserSccpGTCommandHandler(extractor, command, params, ctx, headers, listener);
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx,
			AbstractInitiator cynapseInit) {
		return new ParserSccpGTCommandHandler(extractor, command, params, ctx, headers, listener);
	}

	@Override
	public String getTableSchema() {
		return null;
	}

}
