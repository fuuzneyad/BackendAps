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


public class ParserZWVICommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private ConfiguredHeader[] headers;
	private final String T_NAME;
	private static final Logger logger = Logger.getLogger(ParserZWVICommandHandler.class);
	private String MCountryCode, MNetworkCode, PLMNChargingArea, CoreNetIdtf, Type;
	@SuppressWarnings("unused")
	private final GlobalBuffer gb;
	
	public ParserZWVICommandHandler(
			Parser reader, 
			DataListener listener, 
			String command, 
			String params, 
			ConfiguredHeader[] headers,
			AbstractInitiator cynapseInit) {
		super(command, params);
		this.reader = reader;
		this.listener=listener;
		this.headers=headers;
		this.T_NAME=command;
		this.gb=(GlobalBuffer)cynapseInit.getMappingModel();
	}
	@Override
	public void handle(Context ctx)
			throws IOException {
		ctx.setTableName(T_NAME);
		StringBuilder sb = new StringBuilder();
		Map<String, Object> map = null;
		boolean isStartExecution=false;
		while (!isStartExecution && !reader.isEOF() && !reader.isEqual('<')) {
			reader.skipEOLs();
			if (reader.isEqual('M')) {
				reader.readUntil(' ', sb);
				if (sb.toString().equals("MSCi")) {
					reader.skipWhile(' ').readUntil(' ', sb).skipUntilEOL();
					ctx.setNe_id(sb.toString());
					isStartExecution = true;
				} else {
					reader.readUntilEOL(sb);
					System.err.println("Skip : " + sb);
					logger.error("Skip : " + sb);
				}
			} else if (reader.isEqual('/')) {
				reader.readUntilEOL(sb);
				if (sb.toString().equals("/*** COMMAND NOT FOUND ***/")) {
					listener.onError(reader.getLine(), ctx,
							reader.getColumn(), sb.toString());
					reader.skipEOLs();
					done();
					return;
				} else {
					System.err.println("Skip : " + sb);
				}
			} else {
				reader.readUntilEOL(sb);
				System.err.println("Skipss : " + sb);
			}
			reader.skipEOLs();
		}

		if (!isStartExecution) {
			done();
			return;
		}else{
			while(!reader.isEOF() && !reader.isEqual('<')){
				reader.readUntilEOL(sb);
				if(sb.toString().trim().equals("MOBILE COUNTRY CODE")){
					reader.skipEOL().readUntilEOL(sb);
					MCountryCode=sb.toString().trim();
				}else
				if(sb.toString().trim().equals("MOBILE NETWORK CODE")){
					reader.skipEOL().readUntilEOL(sb);
					MNetworkCode=sb.toString().trim();
				}else
				if(sb.toString().trim().equals("MSC NUMBER")){
					Type="MSC_NUMBER";
				}else
				if(sb.toString().trim().equals("EIR NUMBER")){
					Type="EIR_NUMBER";
				}else
				if(sb.toString().trim().equals("DEFAULT CALLING LINE IDENTITY")){
					Type="DEF_CALL_IDTY";
				}else
				if(sb.toString().trim().equals("GMLC ADDRESS FOR EMERGENCY SERVICES")){
					Type="GMLC_ADDR";
				}else
				if(sb.toString().trim().equals("EXTRA CALL FORWARDING NUMBER")){
					Type="XTR_F_NUM";
				}else
				if(sb.toString().trim().equals("VLR NUMBER")){
					Type="VLR_NUMBER";
				}else
				if(sb.toString().trim().startsWith("NUMBER              NP")){
					reader.skipEOL();
					while(!reader.isEOL()  && !reader.isEqual('<')){
						map = new LinkedHashMap<String, Object>();
						parse(map, reader, headers);
						map.put("TYPE", Type);
						map.put("MOBILE_COUNTRY_CODE", MCountryCode);
						map.put("MOBILE_NETWORK_CODE", MNetworkCode);
						map.put("PLMN_DEFAULT_CHARGING_AREA", PLMNChargingArea);
						map.put("CORE_NETWORK_NODE_IDTFR", CoreNetIdtf);
						map.put("PLMN_DEFAULT_CHARGING_AREA", PLMNChargingArea);
						if(map!=null && !map.isEmpty()){
							listener.onReadyData(ctx, map, reader.getLine());
							map = new LinkedHashMap<String, Object>();
						}
						reader.skipEOL();
					}
				}else
				if(sb.toString().trim().equals("COMMAND EXECUTED")){
					done();
					return;
				}					
				reader.skipEOLs();
			}
		}
		
	}
	
	private void parse(Map<String, Object> map, Parser reader, ConfiguredHeader[] header) throws IOException {		
		StringBuilder sb = new StringBuilder();
		int lastHeader = header.length-1;
		for (int i = 6; i < header.length; i++) {
			if(lastHeader==i){
				reader.readUntilEOL(sb);
			}else
				reader.read(sb, header[i].getLength());
			map.put(header[i].getName(), sb.toString().trim());
		}
	}
}
