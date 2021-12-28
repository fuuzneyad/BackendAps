package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import id.co.telkom.parser.common.charparser.Predicates;
import id.co.telkom.parser.common.model.ConfiguredHeader;

public class ParserC7LTPCommandHandlerFactory extends ConfigurableCommandHandlerFactory{
	public ParserC7LTPCommandHandlerFactory(){
		super("C7LTP", 2, new ConfiguredHeader[]{
				new ConfiguredHeader("LS             "),
				new ConfiguredHeader("SPID    "),
				new ConfiguredHeader("SLC   "),
				new ConfiguredHeader("STATE      "),
				new ConfiguredHeader("INHIBST  "),
				new ConfiguredHeader("FCODE  ",false, false, Predicates.WHITESPACE),
				new ConfiguredHeader("INFO",false, false, Predicates.WHITESPACE)
		});
	}

}
