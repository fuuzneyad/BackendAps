package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.model.ConfiguredHeader;

public class ParserSAAEPCommandHandlerFactory extends ConfigurableCommandHandlerFactory implements MscCommandHandlerFactory {
	public ParserSAAEPCommandHandlerFactory(){
		super("SAAEP", 3, new ConfiguredHeader[]{
				new ConfiguredHeader("SAE    "),
				new ConfiguredHeader("BLOCK    "),
				new ConfiguredHeader("CNTRTYP  "),
				new ConfiguredHeader("NI          "),
				new ConfiguredHeader("NIU         "),
				new ConfiguredHeader("NIE         "),
				new ConfiguredHeader("NIR         ")
		});
	}
}
