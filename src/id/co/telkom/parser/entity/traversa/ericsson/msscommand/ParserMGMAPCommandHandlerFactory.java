package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.model.ConfiguredHeader;

public class ParserMGMAPCommandHandlerFactory extends ConfigurableCommandHandlerFactory implements MscCommandHandlerFactory {
	public ParserMGMAPCommandHandlerFactory(){
		super("MGMAP", 2, new ConfiguredHeader[]{
				new ConfiguredHeader("RNC     ", true),
				new ConfiguredHeader("LAI",30)
		});
	}
}
