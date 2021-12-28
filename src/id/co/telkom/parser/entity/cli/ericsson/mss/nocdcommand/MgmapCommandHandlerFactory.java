package id.co.telkom.parser.entity.cli.ericsson.mss.nocdcommand;

import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.entity.cli.ericsson.ConfigurableTableCommandHandlerFactory;

public class MgmapCommandHandlerFactory extends ConfigurableTableCommandHandlerFactory implements MscCommandHandlerFactory{

	public MgmapCommandHandlerFactory() {
		super("MGMAP", 2, new ConfiguredHeader[]{
				new ConfiguredHeader("RNC     ", true),
				new ConfiguredHeader("LAI",30)
		});
	}

}
