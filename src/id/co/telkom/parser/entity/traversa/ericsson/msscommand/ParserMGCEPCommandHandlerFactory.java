package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.model.ConfiguredHeader;

public class ParserMGCEPCommandHandlerFactory extends ConfigurableCommandHandlerFactory implements MscCommandHandlerFactory {
	public ParserMGCEPCommandHandlerFactory(){
		super("MGCEP", 2, new ConfiguredHeader[]{
				new ConfiguredHeader("CELL     "),
				new ConfiguredHeader("CGI                 "),
				new ConfiguredHeader("BSC      "),
				new ConfiguredHeader("CO     "),
				new ConfiguredHeader("RO     "),
				new ConfiguredHeader("NCS  "),
				new ConfiguredHeader("EA   ")
		});
	}
}
