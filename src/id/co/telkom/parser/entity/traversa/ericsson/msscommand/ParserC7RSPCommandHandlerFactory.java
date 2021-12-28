package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import id.co.telkom.parser.common.model.ConfiguredHeader;

public class ParserC7RSPCommandHandlerFactory extends ConfigurableCommandHandlerFactory{
	
	public ParserC7RSPCommandHandlerFactory(){
		super("C7RSP",2, new ConfiguredHeader[]{
				new ConfiguredHeader("DEST           "),
				new ConfiguredHeader("SPID     "),
				new ConfiguredHeader("DST "),
				new ConfiguredHeader("PRIO  "),
				new ConfiguredHeader("LSHB  "),
				new ConfiguredHeader("LS             "),
				new ConfiguredHeader("SPID     "),
				new ConfiguredHeader("RST")
		});
	}

}
