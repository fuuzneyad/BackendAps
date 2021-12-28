package id.co.telkom.parser.entity.traversa.nokia.msscommand;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserZNAICommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "ZNAI";
	private final ConfiguredHeader[] headers;
	
	public ParserZNAICommandHandlerFactory() {
		headers = new ConfiguredHeader[]{
				//jgn ubh urutannya
				new ConfiguredHeader("RESULT_RECORD","RECORD ".length()),
				new ConfiguredHeader("STA "),
				new ConfiguredHeader("LS_IND","IND ".length()),
				new ConfiguredHeader("NET "),
				new ConfiguredHeader("SP_CODE_H_D","SP CODE H/D        ".length()),
				new ConfiguredHeader("SP_NAME","NAME             ".length()),
				new ConfiguredHeader("RI  "),
				new ConfiguredHeader("ENTITY_STATE","STATE  ".length()),
				new ConfiguredHeader("LS_PR","PR ".length()),
				new ConfiguredHeader("WGHT")
			};
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
			return new ParserZNAICommandHandler( extractor, listener, getCommand(), params,headers,cynapseInit);
	}
	
	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return null;
	}
	
	@Override
	public String getTableSchema() {
		return "";
	}
	
}
