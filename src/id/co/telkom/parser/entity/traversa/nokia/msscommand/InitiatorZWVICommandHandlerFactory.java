package id.co.telkom.parser.entity.traversa.nokia.msscommand;

import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.entity.traversa.common.InitiatorCommandHandlerFactory;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;

public class InitiatorZWVICommandHandlerFactory implements InitiatorCommandHandlerFactory{
	private final static String command="WVI";
	private final ConfiguredHeader[] header;
	
	public InitiatorZWVICommandHandlerFactory(){
		header=	new ConfiguredHeader[]{
				new ConfiguredHeader("MOBILE_COUNTRY_CODE",5),
				new ConfiguredHeader("MOBILE_NETWORK_CODE",5),
				new ConfiguredHeader("PLMN_DEFAULT_CHARGING_AREA",5),
				new ConfiguredHeader("CORE_NETWORK_NODE_IDTFR",5),
				new ConfiguredHeader("OPERATOR_SERVICE_NUMBER_IMSI",5),	//batas
				new ConfiguredHeader("TYPE", 15),	 
				new ConfiguredHeader("NUMBER              "),	 
				new ConfiguredHeader("NP        "),
				new ConfiguredHeader("TON                ")//,

			};
				
	}
	
	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, Context ctx, GlobalBuffer buf) {
		return new InitiatorZWVICommandHandler(extractor, command, params, buf, ctx, header);
	}

}
