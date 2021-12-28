package id.co.telkom.parser.entity.cli.ericsson.sgsn.nocdcommand;

import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.entity.cli.ericsson.ConfigurableTableCommandHandlerFactory;

public class GshCommandHandlerFactory extends ConfigurableTableCommandHandlerFactory implements MscCommandHandlerFactory{

	public GshCommandHandlerFactory() {
		super("GSH", 2, new ConfiguredHeader[]{
				new ConfiguredHeader("PTP_BVC_NSEI_BVCI",50,"PTP BVC [NSEI-BVCI]      ".length()),
				new ConfiguredHeader("CELL_MCC_MNC_LAC_RAC_CI", 50,"Cell [MCC-MNC-LAC-RAC-CI]".length()),
				new ConfiguredHeader("OP_STATE",50,"Operational State        ".length()),
				new ConfiguredHeader("BLOCKING_STATE",50, "Blocking State           ".length()),
				new ConfiguredHeader("BSC_NAME", 50)
		});
	}

}
