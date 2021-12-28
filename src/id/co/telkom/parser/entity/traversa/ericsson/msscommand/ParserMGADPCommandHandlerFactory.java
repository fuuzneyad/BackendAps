package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.model.ConfiguredHeader;

public class ParserMGADPCommandHandlerFactory extends ConfigurableCommandHandlerFactory implements MscCommandHandlerFactory {
	public ParserMGADPCommandHandlerFactory(){
		super("MGADP", 2, new ConfiguredHeader[]{
				new ConfiguredHeader("TDD")
		});
	}
}
