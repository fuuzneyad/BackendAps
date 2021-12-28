package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Predicates;
import id.co.telkom.parser.common.model.ConfiguredHeader;

public class ParserMGISPCommandHandlerFactory extends ConfigurableCommandHandlerFactory implements MscCommandHandlerFactory {
	public ParserMGISPCommandHandlerFactory(){
		super("MGISP", 3, new ConfiguredHeader[]{
				new ConfiguredHeader("IMSIS            ", false, false, Predicates.WHITESPACE),
				new ConfiguredHeader("M                   "),
				new ConfiguredHeader("NA  "),
				new ConfiguredHeader("ANRES", false, 100)
		});
	}
}
