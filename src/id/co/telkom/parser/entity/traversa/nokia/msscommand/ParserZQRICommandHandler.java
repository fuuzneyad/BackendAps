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

public class ParserZQRICommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private ConfiguredHeader[] headers;
	private final String T_NAME;
	private static final Logger logger = Logger.getLogger(ParserZQRICommandHandler.class);
	@SuppressWarnings("unused")
	private final GlobalBuffer gb;
	@SuppressWarnings("unused")
	private String Param;
	private boolean isStartExecution;
	
	public ParserZQRICommandHandler(
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
		this.Param=params;
	}
	@Override
	public void handle(Context ctx)
			throws IOException {
		logger.info(T_NAME);
		ctx.setTableName(T_NAME);
		StringBuilder sb = new StringBuilder();
		while(!isStartExecution && !reader.isEOF() && !reader.isEqual('<')){
			if(reader.isEqual('/')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("/*** COMMAND NOT FOUND ***/")){
					listener.onError(reader.getLine(), ctx, reader.getColumn(), sb.toString());
					done();
					return;
				}else{
					System.err.println("Skip : "+sb);
				}
			}else if(reader.isEqual('M')){
				reader.readUntil(' ', sb);
				if(sb.toString().equals("MSCi")){
					reader.skipWhile(' ').readUntil(' ', sb).skipUntilEOL();
					ctx.setNe_id(sb.toString());
					isStartExecution = true;
				}else{
					reader.readUntilEOL(sb);
					//System.err.println("Skip : "+sb);
				}
			}else{
				reader.readUntilEOL(sb);
				//System.err.println("Skip : "+sb);
			}
			reader.skipEOLs();
		}
		if(!isStartExecution){
			done();
			return;
		}

		reader.readUntilEOL(sb);
		if (sb.toString().equals("NOT ACCEPTED")) {
			reader.skipEOL().readUntilEOL(sb);
			listener.onError(reader.getLine(), ctx, reader.getColumn(), sb.toString());
			reader.skipEOLs();
			done();
			return;
		}
		listener.onBeginTable(reader.getLine(), ctx);
		final int lastIndex = headers.length - 1;
		ConfiguredHeader firstHeader = headers[0];

		int tableType = 0;
		int[] colLength = new int[5];
		while(!reader.isEqual('<') && !reader.isEOF()) {
			reader.readUntilEOL(sb).skipEOLs();	// header line
			String s = sb.toString().trim();
			if(s.contains("--")) {
				if(tableType==0) {
					for(int i=0, t1=0; i<headers.length; i++) {
						int t2 = s.indexOf(" -",t1)+1;
						if(t2>0) {
							headers[i].setLength(t2-t1);
							t1 = t2;
						} else
							break;
					}
				} else {
					for(int i=0, t1=0; i<colLength.length; i++) {
						int t2 = s.indexOf(" -",t1)+1;
						if(t2>0) {
							colLength[i] = t2-t1;
							t1 = t2;
						} else
							break;
					}
				}
				break;
			} else if(s.contains("ADMIN"))
				tableType = 1;
		}

		if(tableType==0) {
			int end = 0;
			boolean pendingNotification = false;
			int line = reader.getLine();
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			Map<String, Object> buffer = new LinkedHashMap<String, Object>();
			boolean newEntry = true;
			while (!reader.isEOL() && end == 0 && !reader.isEqual('<')) {
				for (int j = 0; j < headers.length && !reader.isEOL(); j++) {
					ConfiguredHeader header = headers[j];
					if (j == lastIndex) {
						reader.readUntilEOL(sb);
					} else {
						reader.read(sb, header.length);
						if (!reader.isEOL() && sb.length() > 0 && sb.charAt(sb.length() - 1) != ' ') {
							String s = sb.toString();
							reader.readUntil(' ', sb);
							sb.insert(0, s);
						}
						if (sb.length() > 0)
							Parser.trimRight(sb);
					}
					final String s = sb.toString().trim();
					if (newEntry) {
						if ("COMMAND EXECUTED".equals(s)) {
							end = 2;
							isStartExecution = false;
							break;
						}
						if (s.length() > 0) {
							map.put(header.getName(), s);
							if (header.copied)
								buffer.put(header.getName(), s);
						} else if (header.copied){
							map.put(header.getName(), buffer.get(header.getName()));
						}
					} else if (s.length() > 0) {
						String last = (String) map.get(header.getName());
						map.put(header.getName(), last == null ? s : (last + "," + s));
						if (header.copied)
							buffer.put(header.getName(), s);
					} else if (header.copied){
						map.put(header.getName(), buffer.get(header.getName()));
					}
					pendingNotification = true;
				}
				if (end == 0) {
					end = reader.getColumn() == 0 ? 1 : 0;
					reader.skipEOLs();
					newEntry = firstHeader.isNewEntry(reader);
					if (newEntry) {
						if(map.get("NAME")!=null)
							listener.onReadyData(ctx, map, line);
						map = new LinkedHashMap<String, Object>();
						pendingNotification = false;
						line = reader.getLine();
					}
				}
			}
			if (pendingNotification && map.get("NAME")!=null)
				listener.onReadyData(ctx, map, line);
			reader.skipEOL();
			if (end == 1)
				reader.skipLines(1);	// END line
		} else {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			colLength[3]--;
			String unitType = "";
			String name = "";
			String state = "";
			String mtu = "";
			while (!reader.isEOL() && !reader.isEqual('<')) {
				if(reader.isEqual(' ')) {
					map.put("UNIT", unitType);
					reader.read(sb, colLength[0]);
					if(!sb.toString().trim().equals(""))
						name = sb.toString().trim();
					map.put("NAME", name);
					reader.read(sb, colLength[1]);
					if(!sb.toString().trim().equals(""))
						state = sb.toString().trim();
					map.put("ADM_STATE", state);
					reader.read(sb, colLength[2]);
					if(!sb.toString().trim().equals(""))
						mtu = sb.toString().trim();
					map.put("MTU", mtu);
					reader.read(sb, colLength[3]);
					map.put("ADDR_TYPE", sb.toString().trim());
					reader.readUntilEOL(sb).skipEOLs();
					map.put("IP_ADDRESS", sb.toString().trim());
					if(!name.contains("->"))
						listener.onReadyData(ctx, map, reader.getLine());
					map = new LinkedHashMap<String, Object>();
				} else {
					reader.readUntilEOL(sb).skipEOL();
					unitType = sb.toString().trim();
					if(unitType.startsWith("COMMAND EXECUTED"))
						break;
				}
			}
		}
		done();
	}
}
