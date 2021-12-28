package id.co.telkom.parser.entity.cli.ericsson.mss.nocdcommand;

import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.entity.cli.ericsson.ConfigurableTableCommandHandlerFactory;

public class MgcapCommandHandlerFactory extends ConfigurableTableCommandHandlerFactory implements MscCommandHandlerFactory{

	public MgcapCommandHandlerFactory() {
		super("MGCAP", 2, new ConfiguredHeader[]{
				new ConfiguredHeader("INT_                   "),
				new ConfiguredHeader("NAT             ")
		});
	}

}
