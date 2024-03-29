package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.model.ConfiguredHeader;

public class ParserMGIDPCommandHandlerFactory extends ConfigurableCommandHandlerFactory implements MscCommandHandlerFactory {
	public ParserMGIDPCommandHandlerFactory(){
		super("MGIDP", 2, new ConfiguredHeader[]{
				new ConfiguredHeader("BTDM        "),
				new ConfiguredHeader("GTDM")
		});
	}
}
