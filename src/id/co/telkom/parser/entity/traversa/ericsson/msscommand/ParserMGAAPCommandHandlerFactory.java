package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.model.ConfiguredHeader;

public class ParserMGAAPCommandHandlerFactory extends ConfigurableCommandHandlerFactory implements MscCommandHandlerFactory {
	public ParserMGAAPCommandHandlerFactory(){
		super("MGAAP", 2, new ConfiguredHeader[]{
				new ConfiguredHeader("AREA     ", true),
				new ConfiguredHeader("ACET  ", true),
				new ConfiguredHeader("AREAID               "),
				new ConfiguredHeader("RO   ", true),
				new ConfiguredHeader("CO   ", true),
				new ConfiguredHeader("EA ", true)
		});
	}
}
