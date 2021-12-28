package id.co.telkom.parser.entity.traversa.ericsson.msscommand;



import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.model.ConfiguredHeader;

public class ParserMGNMPCommandHandlerFactory extends ConfigurableCommandHandlerFactory implements MscCommandHandlerFactory{
	public ParserMGNMPCommandHandlerFactory(){
		super("MGNMP", 2, new ConfiguredHeader[]{
				new ConfiguredHeader("MSC      ", true),
				new ConfiguredHeader("MSCADDR            ", true),
				new ConfiguredHeader("R        ", true),
				new ConfiguredHeader("MSCCAPA", 50)
		});
	}

}
