package id.co.telkom.parser.entity.cli.ericsson.sgsn.nocdcommand;

import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class GbipStatusCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "GBIP_STATUS";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	
	public GbipStatusCommandHandlerFactory() {
		headersMap.put("L", new ConfiguredHeader[] {
				new ConfiguredHeader("                 BSC"),
				new ConfiguredHeader("NSEI",30)
			});
		headersMap.put("CONFIGURED", new ConfiguredHeader[] {
				new ConfiguredHeader("REMOTE_IP_ENDPOINT","Remote IP-end-point    	".length()),
				new ConfiguredHeader("SW   	"),
				new ConfiguredHeader("DW   	"),
				new ConfiguredHeader("STATUS",30),
			});
		headersMap.put("CONNECTED", new ConfiguredHeader[] {
				new ConfiguredHeader("PTP_BVC_NSEI_BVCI",50,"PTP BVC [NSEI-BVCI]      ".length()),
				new ConfiguredHeader("CELL_MCC_MNC_LAC_RAC_CI", 50,"Cell [MCC-MNC-LAC-RAC-CI]".length()),
				new ConfiguredHeader("OP_STATE",50,"Operational State        ".length()),
				new ConfiguredHeader("BLOCKING_STATE",50, "Blocking State           ".length())
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
			String params, DataListener listener, Context ctx) {
		if(params.contains("-L"))
			return new GbipStatusLCommandHandler( extractor, listener, command, params, headersMap);
		else
		if(params.contains("-S"))
			return new GbipStatusSCommandHandler( extractor, listener, command, params, headersMap);
		else
			return new UnhandledCommandHandler(extractor, command, params);
	}
	
	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx, AbstractInitiator cynapseInit) {
		return null;
	}
	
	@Override
	public String getTableSchema() {
		
		StringBuilder current = new StringBuilder();
			for(Map.Entry<String, ConfiguredHeader[]> mp :headersMap.entrySet()){
				current.append("CREATE TABLE ").append(getTableName()[0]+"_"+mp.getKey()).append(" (\n");
				current.append("\tENTRY_DATE TIMESTAMP DEFAULT NOW(),\n");
				current.append("\tNE_ID VARCHAR(9),\n");
				current.append("\tCOMMAND_PARAM VARCHAR(30),\n");
				current.append("\tLINE BIGINT(9),\n");
			
			for (ConfiguredHeader configuredHeader : mp.getValue()) {
				current.append("\t"+configuredHeader.getName()).append(' ').append("VARCHAR(").append(configuredHeader.getDbLength()).append("),\n");
			}
			
			current.setLength(current.length() - 2);
			
			current.append("\n);\r\n");
		}
		return current.toString();
	}

}
