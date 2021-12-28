package id.co.telkom.parser.entity.cli.ericsson.mss.nocdcommand;

import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.entity.cli.ericsson.ConfigurableTableCommandHandlerFactory;

public class RaeppCommandHandlerFactory extends ConfigurableTableCommandHandlerFactory implements MscCommandHandlerFactory{

	public RaeppCommandHandlerFactory() {
		super("RAEPP", 3, new ConfiguredHeader[]{
				new ConfiguredHeader("PROP                   "),
				new ConfiguredHeader("TYPE", 20,"TYPE".length())
		});
	}

}
