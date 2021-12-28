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

public class ParserLSTRNCCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "LST_RNC";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();

	public ParserLSTRNCCommandHandlerFactory() {
		headersMap.put("MASTER", new ConfiguredHeader[]{
				new ConfiguredHeader("RNC_INDEX", " RNC index  ".length()),
				new ConfiguredHeader("RNC_NAME","RNC name           ".length()),
				new ConfiguredHeader("MCC","Mobile country code  ".length()),
				new ConfiguredHeader("MNC","Mobile network code  ".length()),
				new ConfiguredHeader("RNC_IDENTIFIER","RNC identifier  ".length()),
				new ConfiguredHeader("NETWORK_INDICATOR","Network indicator  ".length()),
				new ConfiguredHeader("SPC","Signaling point code  ".length()),
				new ConfiguredHeader("RELOC_ALOC_TIMER","Relocation alloc timer(ms)  ".length()),
				new ConfiguredHeader("RELOC_COMPLETE_TIMER","Relocation complete timer(ms)  ".length()),
				new ConfiguredHeader("RAB_ASSIG_TIMER","RAB assignment timer(ms)  ".length()),
				new ConfiguredHeader("IGNORED_OVERLOAD_TIMER","Ignored overload timer(s)  ".length()),
				new ConfiguredHeader("INCREASE_TRAFFIC_TIMER","Increase traffic timer(s) ".length()),
				new ConfiguredHeader("RESET_TIMER","Reset timer(s)  ".length()),
				new ConfiguredHeader("RESET_ACK_TIMER","Reset ack timer(s)  ".length()),
				new ConfiguredHeader("CORE_MCC","Core mobile country code  ".length()),
				new ConfiguredHeader("CORE_MNC","Core mobile network code  ".length()),
				new ConfiguredHeader("CORE_NET_IDENTFR","Core network identifier  ".length()),
				new ConfiguredHeader("RNC_SUPPORT_IMS","RNC support IMS  ".length()),
				new ConfiguredHeader("RNC_SUPPORT_IUFLEX","RNC support IU-FLEX  ".length()),
				new ConfiguredHeader("RNC_SUPPORT_RAN_SHARE","RNC support RAN share  ".length()),
				new ConfiguredHeader("RESERVED_PARAM","Reserved Parameter  ".length()),
				new ConfiguredHeader("RNC_PROTOCOL_VER","RNC protocol version  ".length()),
				new ConfiguredHeader("RNC_SUPPORT_SPID","RNC support SPID  ".length()),
				new ConfiguredHeader("RNC_SUPPORT_OUT_OF_RAN","RNC support Out Of UTRAN  ".length()),
				new ConfiguredHeader("MOST_TIME_MSG_SEND_RESET","The most times for message of sending RESET  ".length()),
				new ConfiguredHeader("RNC_SUPPOR_IPV6","RNC support IPv6 address  ".length()),
				new ConfiguredHeader("SEND_OVERLOAD_MSG","Sending OVERLOAD message to RNC  ".length()),
				new ConfiguredHeader("RNC_SUPPORT_ONE_TUNNEL","RNC support OneTunnel  ".length()),
				new ConfiguredHeader("RNC_SUPPORT_R7_QOS","RNC support R7 QoS  ".length()),
				new ConfiguredHeader("NEGOTIABLE_RAB_QOS","Negotiate RAB QoS  ".length()),
				new ConfiguredHeader("ALERNATIVE_BITRATE_TYPE","Alternative Bitrate Type  ".length()),
				new ConfiguredHeader("CHG_SYM_ASYM","Change RAB symmetric to asymmetric bidirection  ".length()),
				new ConfiguredHeader("SGSN_BUFFER_3G_DOWNLINK","SGSN Buffer 3G Downlink Packets".length())
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
		return new ParserLSTRNCCommandHandler( extractor, listener, getCommand(), params,headersMap, cynapseInit);
	}
	
	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return new ParserLSTRNCCommandHandler( extractor, listener, getCommand(), params, headersMap, null);
	}
	
	@Override
	public String getTableSchema() {
		return "";
	}
	
}
