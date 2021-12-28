package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.model.ConfiguredHeader;

public class ParserMGRLPCommandHandlerFactory extends ConfigurableCommandHandlerFactory implements MscCommandHandlerFactory{
	public ParserMGRLPCommandHandlerFactory(){
		super("MGRLP", 2, new ConfiguredHeader[]{
				new ConfiguredHeader("LAI            ", true, "LAI            ".length()),
				new ConfiguredHeader("CELLCON   ", true, "CELLCON   ".length()),
				new ConfiguredHeader("MSRNS           ", true, "MSRNS           ".length()), // error in mySql
				new ConfiguredHeader("R          "),
				new ConfiguredHeader("USAGE")
		});
	}
	
	

	

}
