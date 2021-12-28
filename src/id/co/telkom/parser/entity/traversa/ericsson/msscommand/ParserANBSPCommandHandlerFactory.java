package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Predicates;
import id.co.telkom.parser.common.model.ConfiguredHeader;

public class ParserANBSPCommandHandlerFactory extends ConfigurableCommandHandlerFactory implements MscCommandHandlerFactory {
	public ParserANBSPCommandHandlerFactory(){
		super("ANBSP", 3, new ConfiguredHeader[]{
				new ConfiguredHeader("B_NUMBER           ", false, false, Predicates.WHITESPACE),
				new ConfiguredHeader("MISCELL   ", false, 100),
				new ConfiguredHeader("F_N    "), //F/N error in database
				new ConfiguredHeader("ROUTE      "),
				new ConfiguredHeader("CHARGE "),
				new ConfiguredHeader("L       "),
				new ConfiguredHeader("A")
		});
	}
}
