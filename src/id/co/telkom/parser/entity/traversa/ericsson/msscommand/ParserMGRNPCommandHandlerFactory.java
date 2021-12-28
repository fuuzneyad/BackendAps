package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.model.ConfiguredHeader;

public class ParserMGRNPCommandHandlerFactory extends ConfigurableCommandHandlerFactory implements MscCommandHandlerFactory {
	public ParserMGRNPCommandHandlerFactory() {
		super("MGRNP", 2, new ConfiguredHeader[]{
				new ConfiguredHeader("MSRN              "),
				new ConfiguredHeader("CONNSTATE"),
				new ConfiguredHeader("STATE", false, 20)});
	}

	
}	