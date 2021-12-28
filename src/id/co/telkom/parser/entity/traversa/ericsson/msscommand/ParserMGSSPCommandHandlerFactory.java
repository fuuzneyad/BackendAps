package id.co.telkom.parser.entity.traversa.ericsson.msscommand;



import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.model.ConfiguredHeader;

public class ParserMGSSPCommandHandlerFactory extends ConfigurableCommandHandlerFactory implements MscCommandHandlerFactory{
	public ParserMGSSPCommandHandlerFactory(){
		super("MGSSP", 3, new ConfiguredHeader[]{
				new ConfiguredHeader("IMSI             ", true),
				new ConfiguredHeader("MSISDN           "),
				new ConfiguredHeader("STATE")
		});
	}

}
