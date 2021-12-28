package id.co.telkom.parser.entity.cli.ericsson.mss.nocdcommand;

import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Predicates;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.entity.cli.ericsson.ConfigurableTableCommandHandlerFactory;

public class NrgwpCommandHandlerFactory extends ConfigurableTableCommandHandlerFactory implements MscCommandHandlerFactory{

	public NrgwpCommandHandlerFactory() {
		super("NRGWP", 2, new ConfiguredHeader[]{
				new ConfiguredHeader("MG       ", false, false, Predicates.WHITESPACE),
				new ConfiguredHeader("BCUID       "),
				new ConfiguredHeader("STATUS  "),
				new ConfiguredHeader("MGG      ", false, 200),
				new ConfiguredHeader("MGS      "),
				new ConfiguredHeader("INFO            ", false, true, null,100),
				new ConfiguredHeader("MC")
		});
	}

}
