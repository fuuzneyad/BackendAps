package id.co.telkom.parser.entity.cli.nokia.nocdcommand;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class Zw7iCommandHandlerFactory implements MscCommandHandlerFactory {
	private String command="W7I";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	
	public Zw7iCommandHandlerFactory(){
		headersMap.put("FEAT",
				new ConfiguredHeader[]{
				new ConfiguredHeader("FEATURE_CODE",50),
				new ConfiguredHeader("FEATURE_NAME",50),
				new ConfiguredHeader("FEATURE_STATE",50),
				new ConfiguredHeader("FEATURE_CAPACITY",50)
			}
		);
		headersMap.put("CAP",
				new ConfiguredHeader[]{
				new ConfiguredHeader("FEATURE_CODE"),
				new ConfiguredHeader("CAPACITY_USAGE")//,
				//new ConfiguredHeader("REQUEST_STATUS")
			}
		);
	}
	

	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		System.out.println(command+" "+params);
		if(params.contains("FEA,FULL"))
			return new Zw7iCommandHandlerFea(extractor,command, params, listener, getTableName()[0], headersMap);
		else 
		if(params.contains("UCAP,LIM:FEA=")||params.contains("UCAP,FULL:FEA="))
			return new Zw7iCommandHandlerCap(extractor,command, params, listener, getTableName()[1], headersMap);	
		else
			return new UnhandledCommandHandler(extractor, command, params);
	}
	
	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx, AbstractInitiator cynapseInit) {
		return null;
	}
	
	private String[] getTableName() {
		return new String[] {command+"_FEAT", command+"_CAP"}; 
	}
	
	@Override
	public String getTableSchema() {

		StringBuilder current = new StringBuilder();
		
		final String[] tables = getTableName();
		Collection<ConfiguredHeader[]> headers = headersMap.values();
		Iterator<ConfiguredHeader[]> iterator = headers.iterator();
		
		for (int i = 0; i < tables.length; i++) {
			current.append("CREATE TABLE ").append(tables[i]).append(" (\n");
			current.append("\tENTRY_DATE TIMESTAMP DEFAULT NOW(),\n");
			current.append("\tNE_ID VARCHAR(9),\n");
			current.append("\tCOMMAND_PARAM VARCHAR(30),\n");
			current.append("\tLINE BIGINT(9),\n");
			
			ConfiguredHeader[] header = iterator.next();
			for (ConfiguredHeader configuredHeader : header) {
				current.append("\t"+configuredHeader.getName()).append(' ').append("VARCHAR(").append(configuredHeader.getDbLength()).append("),\n");
			}
			
			current.setLength(current.length() - 2);
			current.append("\n);\r\n");
		}
		return current.toString();
	}

}
