package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.model.ConfiguredHeader;

public class ParserIHRDPCommandHandlerFactory extends ConfigurableCommandHandlerFactory implements MscCommandHandlerFactory{
	public ParserIHRDPCommandHandlerFactory(){
		super("IHRDP", 2, new ConfiguredHeader[]{
				new ConfiguredHeader("IPPORT   "),
				new ConfiguredHeader("IPADD   "),
				new ConfiguredHeader("DEST_MASK          "),
				new ConfiguredHeader("PREF  "),
				new ConfiguredHeader("GW              "),
				new ConfiguredHeader("DGD")
		});
	}

}
