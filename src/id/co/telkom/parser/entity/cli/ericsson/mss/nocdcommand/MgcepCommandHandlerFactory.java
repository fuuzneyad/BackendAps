package id.co.telkom.parser.entity.cli.ericsson.mss.nocdcommand;

import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.entity.cli.ericsson.ConfigurableTableCommandHandlerFactory;

public class MgcepCommandHandlerFactory extends ConfigurableTableCommandHandlerFactory implements MscCommandHandlerFactory{

	public MgcepCommandHandlerFactory() {
		super("MGCEP", 2, new ConfiguredHeader[]{
				new ConfiguredHeader("CELL     "),
				new ConfiguredHeader("CGI                 "),
				new ConfiguredHeader("BSC      "),
				new ConfiguredHeader("CO     "),
				new ConfiguredHeader("RO     "),
				new ConfiguredHeader("NCS  "),
				new ConfiguredHeader("EA   ")
		});
	}

}
