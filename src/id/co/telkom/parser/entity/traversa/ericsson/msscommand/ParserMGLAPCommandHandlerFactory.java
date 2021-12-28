package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.model.ConfiguredHeader;



public class ParserMGLAPCommandHandlerFactory extends ConfigurableCommandHandlerFactory implements MscCommandHandlerFactory{
	public ParserMGLAPCommandHandlerFactory(){
		super("MGLAP", 2, new ConfiguredHeader[]{
				new ConfiguredHeader("LAI           "),
				new ConfiguredHeader("PFC  "),
				new ConfiguredHeader("PRL  "),
				new ConfiguredHeader("POOL  "),
				new ConfiguredHeader("AIDX               "),
				new ConfiguredHeader("NSUB       "),
				new ConfiguredHeader("NSUBA       ")
		});
	}
	

}
