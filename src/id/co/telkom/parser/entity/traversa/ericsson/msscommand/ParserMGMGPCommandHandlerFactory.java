package id.co.telkom.parser.entity.traversa.ericsson.msscommand;



import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.model.ConfiguredHeader;

public class ParserMGMGPCommandHandlerFactory extends ConfigurableCommandHandlerFactory implements MscCommandHandlerFactory{
	public ParserMGMGPCommandHandlerFactory(){
		super("MGMGP", 2, new ConfiguredHeader[]{
				new ConfiguredHeader("MSCG     ", true),
				new ConfiguredHeader("MSC", 10)
		});
	}

}
