package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import id.co.telkom.parser.common.model.ConfiguredHeader;



public class ParserMGORPCommandHandlerFactory extends ConfigurableCommandHandlerFactory{
	public static final String command = "MGORP";

	public ParserMGORPCommandHandlerFactory() {
		super("MGORP", 2, new ConfiguredHeader[]{
				new ConfiguredHeader("MSCG     "),
				new ConfiguredHeader("RNC      "),
				new ConfiguredHeader("RNCID")
		});
	}
}
