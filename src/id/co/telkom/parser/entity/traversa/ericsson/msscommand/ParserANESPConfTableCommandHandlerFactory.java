package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.model.ConfiguredHeader;

public class ParserANESPConfTableCommandHandlerFactory extends ConfigurableCommandHandlerFactory implements MscCommandHandlerFactory {
	public ParserANESPConfTableCommandHandlerFactory(){
		super("ANESP", 3, new ConfiguredHeader[]{
				new ConfiguredHeader("ES     "),
				new ConfiguredHeader("BE          ", false, 100),
				new ConfiguredHeader("M                          "), //F/N error in database
				new ConfiguredHeader("EOSRES")
		});
	}
}
