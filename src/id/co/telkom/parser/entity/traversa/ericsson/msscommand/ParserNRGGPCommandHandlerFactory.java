package id.co.telkom.parser.entity.traversa.ericsson.msscommand;


import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Predicates;
import id.co.telkom.parser.common.model.ConfiguredHeader;

public class ParserNRGGPCommandHandlerFactory extends ConfigurableCommandHandlerFactory implements MscCommandHandlerFactory{
	public ParserNRGGPCommandHandlerFactory(){
		super("NRGGP", 2, new ConfiguredHeader[]{
				new ConfiguredHeader("MGG      ", false, false, Predicates.WHITESPACE),
				new ConfiguredHeader("MG       ", false, 100),
				new ConfiguredHeader("RESTRICTED       "),
				new ConfiguredHeader("DEFAULTS   "), // DEFAULT error in mySql change to DEFAULTS
				new ConfiguredHeader("MISC      "),
				new ConfiguredHeader("MGP       ")});
	}

}
