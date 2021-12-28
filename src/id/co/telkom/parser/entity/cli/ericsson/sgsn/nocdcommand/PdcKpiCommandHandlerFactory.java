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

public class PdcKpiCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "PDC_KPI.PL";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	
	public PdcKpiCommandHandlerFactory() {
		this.headersMap.put("PDP", 
			      new ConfiguredHeader[] { 
			      new ConfiguredHeader("DAY_  "), 
			      new ConfiguredHeader("TIME_  "), 
			      new ConfiguredHeader("ATTACH   "), 
			      new ConfiguredHeader("PDP_ACTIVATION   "), 
			      new ConfiguredHeader("INTRA_SGSN_RAU   "), 
			      new ConfiguredHeader("ISRAU   "), 
			      new ConfiguredHeader("PAGING   "), 
			      new ConfiguredHeader("CUT_OFF   "), 
			      new ConfiguredHeader("RAB      "), 
			      new ConfiguredHeader("SERV  ") });

			    this.headersMap.put("PAYLOAD", 
			      new ConfiguredHeader[] { 
			      new ConfiguredHeader("DAY_  "), 
			      new ConfiguredHeader("TIME_  "), 
			      new ConfiguredHeader("UP_PLD_MBPS", "Mbit/s   ".length()), 
			      new ConfiguredHeader("UP_PLD_KPPS", "kpps/s   ".length()), 
			      new ConfiguredHeader("UP_AVG_PKT_BYTE", "Avg pkt (byte)     ||     ".length()), 
			      new ConfiguredHeader("DW_PLD_MBPS", "Mbit/s   ".length()), 
			      new ConfiguredHeader("DW_PLD_KPPS", "kpps/s   ".length()), 
			      new ConfiguredHeader("DW_AVG_PKT_BYTE", "Avg pkt (byte)".length()) });

			    this.headersMap.put("ATTACHED_SUBS", 
			      new ConfiguredHeader[] { 
			      new ConfiguredHeader("DAY   "), 
			      new ConfiguredHeader("TIME   "), 
			      new ConfiguredHeader("ATTCH_SUBS_SAU", "Attached Subscribers (SAU)   ".length()), 
			      new ConfiguredHeader("ACTIVE_PDP_CONX", "Active PDP Contexts   ".length()), 
			      new ConfiguredHeader("PDP_CTX_PER_SGSN", "PDP Contexts per SGSN (SDS)".length()) });	}

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
		return new PdcKpiCommandHandler( extractor, listener, command, params, headersMap);
	}
	
	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx, AbstractInitiator cynapseInit) {
		return null;
	}
	
	@Override
	public String getTableSchema() {
		
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<String, ConfiguredHeader[]>mp:headersMap.entrySet()){
	        sb.append("CREATE TABLE " +PdcKpiCommandHandler.T_NAME+"_"+ mp.getKey() + " (\n");
	        sb.append("\t`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n");
	        sb.append("\t`HASH_VAL` varchar(100) DEFAULT NULL,\n");
	        sb.append("\t`SOURCE_ID` varchar(100) DEFAULT NULL,\n");
	        //sb.append("\t`DATETIME_ID` datetime NULL DEFAULT NULL,\n");
	        sb.append("\t`GRANULARITY` int(40) ,\n");
	        sb.append("\t`NE_ID` varchar(200) DEFAULT NULL,\n");
	        sb.append("\t`MO_ID` varchar(300) DEFAULT NULL,\n");
	        for(ConfiguredHeader c:mp.getValue()){
	        	 sb.append("\t`"+c.getName()+"` varchar(30) DEFAULT NULL,\n");
	        }
	        sb.setLength(sb.length() - 2);
	        sb.append("\n)Engine=MyIsam;\n\n");
		}
		
		return sb.toString();
	}

}
