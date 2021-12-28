package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.model.ConfiguredHeader;

public class ParserMGEPPCommandHandlerFactory extends ConfigurableCommandHandlerFactory implements MscCommandHandlerFactory {
	public ParserMGEPPCommandHandlerFactory(){
		super("MGEPP", 2, new ConfiguredHeader[]{
				new ConfiguredHeader("PROP                   "),
				new ConfiguredHeader("TYPE_", "TYPE".length())
		});
	}
}
