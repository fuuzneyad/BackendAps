package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.model.ConfiguredHeader;

public class ParserMGRRPCommandHandlerFactory extends ConfigurableCommandHandlerFactory implements MscCommandHandlerFactory {
	public ParserMGRRPCommandHandlerFactory(){
		super("MGRRP", 2, new ConfiguredHeader[]{
				new ConfiguredHeader("MSRNS           ", true, "MSRNS           ".length()),
				new ConfiguredHeader("R          ", true, "R          ".length()),
				new ConfiguredHeader("USAGES           ", true, "USAGES           ".length()), // error in mySql
				new ConfiguredHeader("LAI            "),
				new ConfiguredHeader("CELLCON  "),
				new ConfiguredHeader("AIDX")
		});
	}
}
