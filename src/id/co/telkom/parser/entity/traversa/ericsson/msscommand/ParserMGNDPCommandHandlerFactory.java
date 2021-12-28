package id.co.telkom.parser.entity.traversa.ericsson.msscommand;



import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.model.ConfiguredHeader;

public class ParserMGNDPCommandHandlerFactory extends ConfigurableCommandHandlerFactory implements MscCommandHandlerFactory{
	public ParserMGNDPCommandHandlerFactory(){
		super("MGNDP", 2, new ConfiguredHeader[]{
				new ConfiguredHeader("NRIL  "),
				new ConfiguredHeader("NRIV  "),
				new ConfiguredHeader("NULLNRIV")
		});
	}

}
