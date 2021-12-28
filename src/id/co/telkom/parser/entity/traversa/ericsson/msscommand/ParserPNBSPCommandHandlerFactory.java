package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.model.ConfiguredHeader;

public class ParserPNBSPCommandHandlerFactory extends ConfigurableCommandHandlerFactory implements MscCommandHandlerFactory{

	public ParserPNBSPCommandHandlerFactory() {
		super("PNBSP", 3, new ConfiguredHeader[]{
				new ConfiguredHeader("BO   "),
				new ConfiguredHeader("NAPI  "),
				new ConfiguredHeader("BNT    "),
				new ConfiguredHeader("RESULT     ")});
	}
}