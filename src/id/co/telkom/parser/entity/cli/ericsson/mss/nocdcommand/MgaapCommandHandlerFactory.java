package id.co.telkom.parser.entity.cli.ericsson.mss.nocdcommand;

import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.entity.cli.ericsson.ConfigurableTableCommandHandlerFactory;

public class MgaapCommandHandlerFactory extends ConfigurableTableCommandHandlerFactory implements MscCommandHandlerFactory{

	public MgaapCommandHandlerFactory() {
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
