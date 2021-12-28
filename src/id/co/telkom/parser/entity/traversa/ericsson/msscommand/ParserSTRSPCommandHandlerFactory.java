package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.model.ConfiguredHeader;

public class ParserSTRSPCommandHandlerFactory extends ConfigurableCommandHandlerFactory implements MscCommandHandlerFactory {
	public ParserSTRSPCommandHandlerFactory(){
		super("STRSP", 2, new ConfiguredHeader[]{
				new ConfiguredHeader("R        "),
				new ConfiguredHeader("NDV         "),
				new ConfiguredHeader("NOCC        "),
				new ConfiguredHeader("NIDL        "),
				new ConfiguredHeader("NBLO        "),
				new ConfiguredHeader("RSTAT")
		});
	}
}
