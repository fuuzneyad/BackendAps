package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.model.ConfiguredHeader;


public class ParserIHSTPCommandHandlerFactory extends ConfigurableCommandHandlerFactory implements MscCommandHandlerFactory {
	public ParserIHSTPCommandHandlerFactory(){
		super("IHSTP", 2, new ConfiguredHeader[]{
				new ConfiguredHeader("IPPORT         "),
				new ConfiguredHeader("OPSTATE  "),
				new ConfiguredHeader("BLSTATE")
		});
	}

}
