package id.co.telkom.parser.entity.cli.huawei.sgsn.nocdcommand;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandlerFactory;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class DspCommandHandlerFactory implements MscCommandHandlerFactory {
	private static final String command = "DSP";
	private final Map<String,ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	
	public DspCommandHandlerFactory() {
		
		headersMap.put("CHGCG", new ConfiguredHeader[] {
				new ConfiguredHeader("PROCESS_ID","Process id  ".length()),
				new ConfiguredHeader("IP_ADDR_CG","IP address of CG  ".length()),
				new ConfiguredHeader("CG_STATUS             "),
				new ConfiguredHeader("GTP_BEARING_PROT","GTP bearing protocol  ".length()),
				new ConfiguredHeader("CG_RECEIVING_PORT","CG receiving port No.".length()),
				new ConfiguredHeader("TCP_LISTEN_PORT","TCP listen port No.".length())
		});
		
		headersMap.put("CHGFILE", new ConfiguredHeader[] {
				new ConfiguredHeader("PROCESS_ID","Process id  ".length()),
				new ConfiguredHeader("FREE_SPACE"),
				new ConfiguredHeader("FREE_SPACE_RATE"),
				new ConfiguredHeader("CDR_FILE_NUM_IN_DISK"),
				new ConfiguredHeader("R7CDR_FILE_NUM_IN_DISK"),
				new ConfiguredHeader("R6CDR_FILE_NUM_IN_DISK"),
				new ConfiguredHeader("R5CDR_FILE_NUM_IN_DISK"),
				new ConfiguredHeader("R4CDR_FILE_NUM_IN_DISK"),
				new ConfiguredHeader("R99CDR_FILE_NUM_IN_DISK"),
				new ConfiguredHeader("R98CDR_FILE_NUM_IN_DISK"),
				new ConfiguredHeader("DAM_FILE_NUM_IN_DISK"),
				new ConfiguredHeader("DECODE_ERRFILE_NUM_IN_DISK")
		});	
		
		headersMap.put("CHGGA", new ConfiguredHeader[] {
				new ConfiguredHeader("SUBRACK_NO"," Subrack No.  ".length()),
				new ConfiguredHeader("SLOT_NO","Slot No.  ".length()),
				
				new ConfiguredHeader("PROCESS_ID","Process id  ".length()),
				new ConfiguredHeader("PROCESS_TYPE","Process type  ".length()),
				new ConfiguredHeader("OPERATE_RESULT","Operate Result  ".length()),
				new ConfiguredHeader("GPRS_CDR_RELEASE","GPRS CDR release  ".length()),
				new ConfiguredHeader("UMTS_CDR_RELEASE","UMTS CDR release  ".length()),
				new ConfiguredHeader("R98_CDR_VERSION","R98 CDR version       ".length()),
				new ConfiguredHeader("R99_CDR_VERSION","R99 CDR version     ".length()),
				new ConfiguredHeader("R4_CDR_VERSION","R4 CDR version      ".length()),
				new ConfiguredHeader("R5_CDR_VERSION","R5 CDR version      ".length()),
				new ConfiguredHeader("R6_CDR_VERSION","R6 CDR version      ".length()),
				new ConfiguredHeader("R7_CDR_VERSION","R7 CDR version      ".length()),
				new ConfiguredHeader("R9_CDR_VERSION","R9 CDR version      ".length()),
				
				new ConfiguredHeader("CDR_RESEND_INTERVAL","CDR resend interval(s)  ".length()),
				new ConfiguredHeader("MAX_CDR_RESENDING","Max. CDR resending  ".length()),
				new ConfiguredHeader("HDD_ERROR_THRESHOLD","Hard Disk Error Threshold  ".length()),
				new ConfiguredHeader("HDD_SPACE_LACK","HDD space lack(%)  ".length()),
				new ConfiguredHeader("MAX_OCCUPY_RATE_REDIRECT_FRAME","Max Occupy Rate of Redirect Frame(%)".length())
		});
	}

	@Override
	public String getCommand() {
		return command;
	}
	
	public String[] getTableName(){
		return new String[] {command+"_CHGCG",command+"_CHGFILE",command+"_CHGGA"};
	}

	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx) {
		if(params.startsWith("CHGCG:SRN")){
			ctx.setTableName(getTableName()[0]);
			return new DspChgTableCommandHandler( extractor, listener, command, params, headersMap.get("CHGCG"));
		}else
		if(params.startsWith("CHGGA:")){
			ctx.setTableName(getTableName()[2]);
			return new DspChgTableCommandHandler( extractor, listener, command, params, headersMap.get("CHGGA"));
		}else			
		if(params.startsWith("CHGFILE")){
			ctx.setTableName(getTableName()[1]);
			return new DspChgfileCommandHandler( extractor, listener, command, params, headersMap.get("CHGFILE"));
		}else
			return new UnhandledCommandHandler(extractor, command,params);
	}
	
	@Override
	public CommandHandler create(Parser extractor, String command,
			String params, DataListener listener, Context ctx, AbstractInitiator cynapseInit) {
		return null;
	}
	
	@Override
	public String getTableSchema() {
		
		StringBuilder current = new StringBuilder();
		
		final String[] tables = getTableName();
		Collection<ConfiguredHeader[]> headers = headersMap.values();
		Iterator<ConfiguredHeader[]> iterator = headers.iterator();
		
		for (int i = 0; i < tables.length; i++) {
			{
				current.append("CREATE TABLE ").append(tables[i]).append(" (\n");
				current.append("\tENTRY_DATE TIMESTAMP DEFAULT NOW(),\n");
				current.append("\tDATETIME_ID DATETIME DEFAULT '0000-00-00 00:00:00',\n");
				current.append("\tNE_ID VARCHAR(9),\n");
				current.append("\tCOMMAND_PARAM VARCHAR(30),\n");
				current.append("\tLINE BIGINT(9),\n");
				
			}
			ConfiguredHeader[] header = iterator.next();
			for (ConfiguredHeader configuredHeader : header) {
				current.append("\t"+configuredHeader.getName()).append(' ').append("VARCHAR(").append(configuredHeader.getDbLength()).append("),\n");
			}
			current.setLength(current.length() - 2);
			
			current.append("\n);\r\n");
		}
		return current.toString();
	}

}
