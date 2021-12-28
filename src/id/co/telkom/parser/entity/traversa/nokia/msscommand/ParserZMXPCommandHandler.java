package id.co.telkom.parser.entity.traversa.nokia.msscommand;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.AbstractCommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;

public class ParserZMXPCommandHandler extends AbstractCommandHandler implements MscCommandHandler{

	private final Parser reader;
	private final DataListener listener;
	private ConfiguredHeader[][] headers;
	private final String T_NAME;
	private static final Logger logger = Logger.getLogger(ParserZMXPCommandHandler.class);
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	
	@SuppressWarnings("unused")
	private final GlobalBuffer gb;
	@SuppressWarnings("unused")
	private String Param;
	private boolean isStartExecution;
	
	
	public ParserZMXPCommandHandler(
			Parser reader, 
			DataListener listener, 
			String command, 
			String params, 
			ConfiguredHeader[][] headers,
			AbstractInitiator cynapseInit) {
		super(command, params);
		this.reader = reader;
		this.listener=listener;
		this.headers=headers;
		this.T_NAME=command;
		this.gb=(GlobalBuffer)cynapseInit.getMappingModel();
		this.Param=params;
	}
	
	
	
	
	@Override
	public void handle(Context ctx) throws IOException {
		// TODO Auto-generated method stub
		logger.info(T_NAME);
		ctx.setTableName(T_NAME);
		StringBuilder sb = new StringBuilder();

		String parent="";
		int line=0;
		
		while (!isStartExecution && !reader.isEOL() && !reader.isEOF() && !reader.isEqual('<')){
			reader.skipEOLs().readUntilEOL(sb);
			
			if (sb.toString().startsWith("LOADING PROGRAM")) {	
				
				while(reader.isEOL() || reader.isEqual(' '))
					reader.read();
				reader.readUntil(' ', sb);
				if(sb.toString().equals("MSCi")){
					reader.skipWhile(' ').readUntil(' ', sb);
					ctx.setNe_id(sb.toString());
					reader.skipWhile(' ').readUntil(' ', sb).skipUntilEOL();
					ctx.setDatetimeid(sb.toString());
				}else{
					reader.readUntilEOL(sb);
				}
			} else 
			if (sb.toString().contains("PLMN PARAMETERS")) {
				isStartExecution = true;
			} 
			else if(sb.toString().startsWith("/*** UNKNOWN COMMAND ***/")){
				listener.onError(reader.getLine(), ctx, reader.getColumn(), sb.toString());
				reader.skipEOLs();
				done();
				return;
			} 
				//System.err.println("Skip : "+sb);
			
			reader.skipEOLs();
		}
		if(!isStartExecution){
			done();
			return;
		}
		
		
		while (!reader.isEOF() && !reader.isEOL() && !reader.isEqual('<')) {
			reader.readUntilEOL(sb);
			////System.out.println("SB PERTAMA = " + sb +" = " + parent);
			//line= reader.getLine();

			if(sb.toString().contains("VISITOR PLMN")||sb.toString().contains("HOME PLMN")){
				//System.out.println("isi  nya");
				String [] PLMN=sb.toString().split(" ");
				map.put("PLMN_TYPE", PLMN[0]);
				map.put("PLMN", PLMN[2]);
				line= reader.getLine();
			
			}else
			if(sb.toString().contains("NETWORK ACCESS RIGHTS")){
				reader.skipLines(1);
				//System.out.println("SB TO = " + sb.toString() +reader.getLine());
				parse(headers[0],reader, map);
			 
				if(!sb.toString().equals(" ")){
					//reader.skipLines(0);
					reader.skipEOL();
					//System.out.println("SB TO1 = " + sb.toString() +reader.getLine());
					parse(headers[0],reader, map);
		//			//System.out.println("MAP TO GSM = " + map);
				}
			}else	
			if(sb.toString().contains("MSCi")){
					reader.skipWhile(' ').readUntil(' ', sb).skipUntilEOL();
			}else 
			if(sb.toString().startsWith("  TMSI ALLOCATION")){
				reader.skipEOL().readUntilEOL(sb);
				String content=sb.toString();
				parent="TMSI ALLOCATION";
				parseTitik(parent, content, map);

			}else
			if(sb.toString().startsWith("  AUTHENTICATION")){
				reader.skipEOL().readUntilEOL(sb);
				String content=sb.toString();
				parent="AUTHENTICATION";
				parseTitik(parent, content, map);
			}else
			if(sb.toString().startsWith("  IMEI CHECKING")){
				reader.skipEOL().readUntilEOL(sb);
				String content=sb.toString();
				parent="IMEI CHECKING";
				parseTitik(parent, content, map);
			}else
			if(sb.toString().startsWith("EQUAL ACCESS")){	
				parent="";	
			}else
			if(sb.toString().contains(":")){
				parseTitik(parent, sb.toString(),map);


				if(sb.toString().contains("DECAF CODE")){
					listener.onReadyData(ctx, map, line);
					map = new LinkedHashMap<String, Object>();
					
				}
				
			}

			reader.skipEOLs();
		}
		
		done();
		return;
		

	}
	
	
	private static String replaceParam(String s){
		return s.replace(" ", "_").
				replace("-", "_").
				replace("/", "_").
				replace("DEFAULT_PREFERRED_INTEREXCHANGE_CARRIER_(PIC)", "PIC").
				replace("UMTS_SUBSCRIBERS_(USIM)", "USIM").
				replace("INDEX", "INDX");
	}
	
	
	private static void parseTitik(String parent,String content,Map<String, Object> map){
		String param_before="";
		String param_after="";
		String param="";
		String command1="";
		if(parent.equals("")){
			parent="";
		}else{
			parent=replaceParam(parent+"_");
		}
		String [] split_titik=content.split(":");
		if(split_titik.length>2){
			param_before=split_titik[0].trim();
			for(int i=1;i<split_titik.length;i++){
				String[] split_spasi=split_titik[i].trim().split("  +");
				command1=split_spasi[0].trim();
				
				if(split_spasi.length>1){
					param_after=split_spasi[1].trim();
					param=param_before;
					param_before=param_after;

					
				}
				else{
					param=param_before;
				}
				
				

				map.put(parent+replaceParam(param), command1);
				
					
			}
		}else{
			param = split_titik[0].trim();
			command1=split_titik[1].trim();
			map.put(parent+replaceParam(param), command1);
		}
		//System.out.println("MAP = " +map);
		
	}
	
	private void parse(ConfiguredHeader[] headers, Parser reader, Map<String, Object> map) throws IOException {
		final int lastColsIndex = headers.length-1;
		String [] gsm;
		String firstHead="";
		String head="";
		String value="";
		StringBuilder sb = new StringBuilder();
		for (int colIdx = 0; colIdx < headers.length; colIdx++) {
			ConfiguredHeader header = headers[colIdx];
			
			if(colIdx == lastColsIndex){
				reader.readUntilEOL(sb);
			}else{
				reader.read(sb, header.length);
			}
			String s = sb.toString().trim();
			
			if(s.contains("GSM SUBSCRIBERS")||s.contains("UMTS SUBSCRIBERS")){
				gsm=sb.toString().trim().split("  +");
				firstHead=replaceParam(gsm[0]);
				value=gsm[1];

				head=firstHead+"_"+header.getName();

				map.put(head, value);
			}else{
				map.put(firstHead+"_"+header.getName(), s);
			}
			
		}

	}

}
