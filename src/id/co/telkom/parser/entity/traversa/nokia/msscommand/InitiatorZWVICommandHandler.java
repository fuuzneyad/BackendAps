package id.co.telkom.parser.entity.traversa.nokia.msscommand;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.common.charparser.AbstractCommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;

/*
 * NOTE:
 * Type										di DB
 * ---------								----------
 * EIR NUMBER								EIR_NUMBER
 * DEFAULT CALLING LINE IDENTITY			DEF_CALL_IDTY
 * MSC NUMBER								MSC_NUMBER
 * VLR NUMBER								VLR_NUMBER
 * EXTRA CALL FORWARDING NUMBER				XTR_F_NUM
 * GMLC ADDRESS FOR EMERGENCY SERVICES		GMLC_ADDR
 * 
 * */

public class InitiatorZWVICommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final ConfiguredHeader[] header;
	private GlobalBuffer buf;
	private String MCountryCode, MNetworkCode, PLMNChargingArea, CoreNetIdtf, Type;
	
	public InitiatorZWVICommandHandler(Parser reader, 
			String command, 
			String params, 
			GlobalBuffer buf,
			Context ctx,
			ConfiguredHeader[] header) {
		super(command, params);
		this.reader = reader;
		this.header=header;
		this.buf=buf;
	}

	@Override
	public void handle(Context ctx) throws IOException {
		StringBuilder sb = new StringBuilder();
		Map<String, Object> map = null;
		boolean isStartExecution=false;
		while (!isStartExecution && !reader.isEOF() && !reader.isEqual('<')) {
			reader.skipEOLs();
			if (reader.isEqual('M')) {
				reader.readUntil(' ', sb);
				if (sb.toString().equals("MSCi")) {
					reader.skipWhile(' ').readUntil(' ', sb).skipUntilEOL();
					isStartExecution = true;
				} else {
					reader.readUntilEOL(sb);
					System.err.println("Skip : " + sb);
				}
			} else if (reader.isEqual('/')) {
				reader.readUntilEOL(sb);
				if (sb.toString().equals("/*** COMMAND NOT FOUND ***/")) {
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
						parse(map, reader, header);
						map.put("Type", Type);
						map.put("MOBILE_COUNTRY_CODE", MCountryCode);
						map.put("MOBILE_NETWORK_CODE", MNetworkCode);
						map.put("PLMN_DEFAULT_CHARGING_AREA", PLMNChargingArea);
						map.put("CORE_NETWORK_NODE_IDTFR", CoreNetIdtf);
						map.put("PLMN_DEFAULT_CHARGING_AREA", PLMNChargingArea);
						if(map!=null && !map.isEmpty()){
							//get Own GT
							if(Type.equals("MSC_NUMBER")){
								buf.setGTToVertex(ctx.ne_id, map.get("NUMBER").toString());
							}
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
