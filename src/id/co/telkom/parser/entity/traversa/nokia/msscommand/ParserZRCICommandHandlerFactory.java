package id.co.telkom.parser.entity.traversa.nokia.msscommand;

import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserZRCICommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "ZRCI";
	private final Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();

	
	public ParserZRCICommandHandlerFactory() {
		//CONFIGURABLE
		headersMap.put("CONFIGURABLE", new ConfiguredHeader[]{
				new ConfiguredHeader("CGR       "),
				new ConfiguredHeader("NCGR      "),
				new ConfiguredHeader("TYPE      "),
				new ConfiguredHeader("DIR       "),
				new ConfiguredHeader("TREE      "),
				new ConfiguredHeader("NBCRCT    "),
				new ConfiguredHeader("STATE     ")});
		
		//MERGED
		headersMap.put("MERGED", new ConfiguredHeader[]{
				//CIRCUIT GROUP(S)
				new ConfiguredHeader("CGR", 30),
				new ConfiguredHeader("NCGR", 30),//The KWRD
				new ConfiguredHeader("TYPE", 30),
				new ConfiguredHeader("STATE", 30),
				new ConfiguredHeader("HUNTED", 30),
				new ConfiguredHeader("FORMAT", 30),
				new ConfiguredHeader("HM1", 30),
				new ConfiguredHeader("HM2", 30),
				new ConfiguredHeader("NBCRCT", 30),				
				new ConfiguredHeader("TREE", 30),				
				new ConfiguredHeader("DIR", 30),
				new ConfiguredHeader("LSI", 30),
				new ConfiguredHeader("INR", 30),
				new ConfiguredHeader("NCCP", 30),
				new ConfiguredHeader("METHOD", 30),
				new ConfiguredHeader("NET", 30),
				new ConfiguredHeader("SPC_H_D", 30),
				new ConfiguredHeader("SPC_H_D_DEC", 30),
				new ConfiguredHeader("ASI", 30),
				new ConfiguredHeader("UPART", 30),
				new ConfiguredHeader("MGW", 30),
				new ConfiguredHeader("FQDN", 30),
				//AUTOMATIC CONGESTION CONTROL  
				new ConfiguredHeader("ATYPE", 30),
				new ConfiguredHeader("ACLSET", 30),
				new ConfiguredHeader("REJ11", 30),
				new ConfiguredHeader("REJ12", 30),
				new ConfiguredHeader("REJ21", 30),
				new ConfiguredHeader("REJ22", 30),
				new ConfiguredHeader("ACT11", 30),
				new ConfiguredHeader("ACT12", 30),
				new ConfiguredHeader("ACT21", 30),
				new ConfiguredHeader("ACT22", 30),
				//SELECTIVE CIRCUIT RESERVATION 
				new ConfiguredHeader("TRES1", 30),
				new ConfiguredHeader("TRES2", 30),
				new ConfiguredHeader("DR1A", 30),
				new ConfiguredHeader("DR2A", 30),
				new ConfiguredHeader("ART1A", 30),
				new ConfiguredHeader("ART2A", 30),
				new ConfiguredHeader("ACTA", 30),
				new ConfiguredHeader("DR1B", 30),
				new ConfiguredHeader("DR2B", 30),
				new ConfiguredHeader("ART1B", 30),
				new ConfiguredHeader("ART2B", 30),
				new ConfiguredHeader("ACTB", 30),
				new ConfiguredHeader("DR1C", 30),
				new ConfiguredHeader("DR2C", 30),
				new ConfiguredHeader("ART1C", 30),
				new ConfiguredHeader("ART2C", 30),
				new ConfiguredHeader("ACTC", 30),
				//PRINT=3
				new ConfiguredHeader("AREA", 30),
				new ConfiguredHeader("STD", 30),
				new ConfiguredHeader("MAN", 30),
				new ConfiguredHeader("AAN", 30),
				new ConfiguredHeader("SSET", 30),
				new ConfiguredHeader("CLI", 30),
				new ConfiguredHeader("CAC", 30),
				new ConfiguredHeader("CACI", 30),
				new ConfiguredHeader("REMN", 30),
				new ConfiguredHeader("RFCL", 30),
				new ConfiguredHeader("ICLI", 30),
				new ConfiguredHeader("CHRN", 30),
				new ConfiguredHeader("ATV", 30),
				new ConfiguredHeader("EC", 30),
				new ConfiguredHeader("DBA", 30),
				new ConfiguredHeader("PRI", 30),
				new ConfiguredHeader("CORG", 30),
				new ConfiguredHeader("DCC", 30),
				new ConfiguredHeader("LOC", 30),
				new ConfiguredHeader("DNN", 30),
				new ConfiguredHeader("RDQ", 30),
				new ConfiguredHeader("DDQ", 30),
				new ConfiguredHeader("IGOR", 30),				
				new ConfiguredHeader("ECAT", 30),
				new ConfiguredHeader("EOS", 30),
				new ConfiguredHeader("UPDR", 30),
				new ConfiguredHeader("T_IND", 30),
				new ConfiguredHeader("DCA", 30),
				new ConfiguredHeader("ARF", 30),
				new ConfiguredHeader("CX", 30),
				new ConfiguredHeader("CTR", 30),
				new ConfiguredHeader("NUCO", 30),
				new ConfiguredHeader("TA", 30),
				new ConfiguredHeader("ATME", 30),
				new ConfiguredHeader("DCME", 30),
				new ConfiguredHeader("ECHO", 30),
				new ConfiguredHeader("RSU", 30),
				new ConfiguredHeader("CLIR", 30),
				new ConfiguredHeader("LC", 30),
				new ConfiguredHeader("CHRG", 30),
				new ConfiguredHeader("OAOC", 30),
				new ConfiguredHeader("SCBM", 30),
				new ConfiguredHeader("SCIE", 30),
				new ConfiguredHeader("SCIC", 30),
				new ConfiguredHeader("CA", 30),
				new ConfiguredHeader("CAI", 30),
				new ConfiguredHeader("ASTC", 30),
				new ConfiguredHeader("APRI", 30),
				new ConfiguredHeader("ACOR", 30),
				new ConfiguredHeader("RDR", 30),
				new ConfiguredHeader("RNPR", 30),
				new ConfiguredHeader("PLOCK", 30),
				new ConfiguredHeader("HB", 30),
				new ConfiguredHeader("ETFO", 30)				
				});
		
		//OTHERS
		headersMap.put("PCM_TSL", new ConfiguredHeader[]{
				new ConfiguredHeader("PCM_TSL              "),
				new ConfiguredHeader("ORD      "),
				new ConfiguredHeader("CTRL     "),
				new ConfiguredHeader("HGR      "),
				new ConfiguredHeader("STATE    "),
				new ConfiguredHeader("LSI         ")
				});
		
		headersMap.put("TERMID", new ConfiguredHeader[]{
				new ConfiguredHeader("TERMID               "),
				new ConfiguredHeader("ORD      "),
				new ConfiguredHeader("CTRL     "),
				new ConfiguredHeader("HGR      "),
				new ConfiguredHeader("STATE    "),
				new ConfiguredHeader("CCSPCM   "),
				new ConfiguredHeader("UNIT   ",30)
				});
		headersMap.put("CIC", new ConfiguredHeader[]{
				new ConfiguredHeader("CIC            "),
				new ConfiguredHeader("ORD      "),
				new ConfiguredHeader("CTRL     "),
				new ConfiguredHeader("HGR      "),
				new ConfiguredHeader("STATE     "),
				new ConfiguredHeader("UNIT", 30)
				});
		headersMap.put("VCRCT", new ConfiguredHeader[]{
				new ConfiguredHeader("VCRCT          "),
				new ConfiguredHeader("ORD      "),
				new ConfiguredHeader("CTRL     "),
				new ConfiguredHeader("HGR      "),
				new ConfiguredHeader("STATE     "),
				new ConfiguredHeader("UNIT", 30)
				});
		
		headersMap.put("CICNUMBER", new ConfiguredHeader[]{				
				new ConfiguredHeader("E1",8),
				new ConfiguredHeader("TS",8),				
				new ConfiguredHeader("CIC",8)
				});
	}

	@Override
	public String getCommand() {
		return command;
	}
	
	public String[] getTableName(){
		return new String[] {command};
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx, AbstractInitiator cynapseInit) {
			return new ParserZRCICommandHandler( extractor, listener, getCommand(), params,headersMap,cynapseInit);
	}
	
	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		return null;
	}
	
	@Override
	public String getTableSchema() {
		return "";
	}
	
}
