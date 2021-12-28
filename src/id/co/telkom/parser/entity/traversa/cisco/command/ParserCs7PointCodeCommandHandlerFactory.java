package id.co.telkom.parser.entity.traversa.cisco.command;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserCs7PointCodeCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "CS7_POINT_CODE";
	private ConfiguredHeader[] headers;
	
	public ParserCs7PointCodeCommandHandlerFactory() {
		this.headers=new ConfiguredHeader[]{
				new ConfiguredHeader("POINT_CODE","CS7 Point Code      ".length()),
				new ConfiguredHeader("TYPE_           "),
				new ConfiguredHeader("STATUS               "),
				new ConfiguredHeader("CONGESTION")};
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
			String params, DataListener listener, Context ctx, 
			AbstractInitiator cynapseInit) {
			return new ParserCs7PointCodeCommandHandler( extractor, listener, command, params,headers,cynapseInit);
	}
	
	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return null;
	}
	
	@Override
	public String getTableSchema() {
		StringBuilder current = new StringBuilder();
		
		final String[] tables = getTableName();
		for (int i = 0; i < tables.length; i++) {
			{
				current.append("CREATE TABLE ").append(tables[i]).append(" (\n");
				current.append("\tENTRY_DATE TIMESTAMP DEFAULT NOW(),\n");
				current.append("\tNE_ID VARCHAR(9),\n");
				current.append("\tLINE BIGINT(9),\n");
			}
			
			for (ConfiguredHeader configuredHeader : headers) {
				current.append("\t"+configuredHeader.getName()).append(' ').append("VARCHAR(").append(configuredHeader.getDbLength()).append("),\n");
			}
			current.setLength(current.length() - 2);
			
			current.append("\n);\r\n");
		}
		return current.toString();
	}

	
	
}
