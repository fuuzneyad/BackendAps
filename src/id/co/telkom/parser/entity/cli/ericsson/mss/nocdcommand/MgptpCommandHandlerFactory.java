package id.co.telkom.parser.entity.cli.ericsson.mss.nocdcommand;

import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.entity.cli.ericsson.ConfigurableTableCommandHandlerFactory;

public class MgptpCommandHandlerFactory extends ConfigurableTableCommandHandlerFactory implements MscCommandHandlerFactory{

	public MgptpCommandHandlerFactory() {
		super("MGPTP", 2, new ConfiguredHeader[]{
				new ConfiguredHeader("VLRADDR            "),
				new ConfiguredHeader("NRIV1","NRIV  ".length()),
				new ConfiguredHeader("NRIV2","NRIV  ".length()),
				new ConfiguredHeader("NRIV3","NRIV  ".length()),
				new ConfiguredHeader("NRIV4","NRIV  ".length()),
				new ConfiguredHeader("NRIV5","NRIV  ".length()),
				new ConfiguredHeader("NRIV6","NRIV  ".length()),
				new ConfiguredHeader("NRIV7","NRIV  ".length()),
				new ConfiguredHeader("NRIV8","NRIV".length())
		});
	}

}
