package id.co.telkom.parser.entity.cli.ericsson.mss.nocdcommand;

import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.entity.cli.ericsson.ConfigurableTableCommandHandlerFactory;

public class MgmgpCommandHandlerFactory extends ConfigurableTableCommandHandlerFactory implements MscCommandHandlerFactory{

	public MgmgpCommandHandlerFactory() {
		super("MGMGP", 2, new ConfiguredHeader[]{
				new ConfiguredHeader("MSCG     ", true),
				new ConfiguredHeader("MSC", 10)
		});
	}

}
