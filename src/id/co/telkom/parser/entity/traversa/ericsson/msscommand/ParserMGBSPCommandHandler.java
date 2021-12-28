package id.co.telkom.parser.entity.traversa.ericsson.msscommand;


import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.AbstractCommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserMGBSPCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private ConfiguredHeader[] headers;
	private final String T_NAME;
	
	public ParserMGBSPCommandHandler(
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
	}
	
	@Override
	public void handle(Context ctx)
			throws IOException {
		ctx.setTableName(T_NAME);
		StringBuilder sb = new StringBuilder();
		reader.skipEOLs().readUntilEOL(sb);
		if (sb.toString().equals("NOT ACCEPTED")) {
			reader.skipEOL().readUntilEOL(sb);
			listener.onError(reader.getLine(), ctx, reader.getColumn(), sb.toString());
			reader.skipEOLs();
			done();
			return;
		}
		reader.skipLines(1);
		listener.onBeginTable(reader.getLastReadLine(), ctx);

		Map<String, Object> map = new LinkedHashMap<String, Object>();
		int headerGroupIdx = 0;
		boolean newEntry = false;
		while(!isDone()&&!reader.isEqual('<') && !reader.isEOF()) {
			for(int i=0; i<6&&!reader.isEOL(); i++) {
				ConfiguredHeader header = headers[i];
				if (i==5) {
					reader.readUntilEOL(sb);
				} else {
					reader.read(sb, header.length);
				}
				Parser.trimRight(sb);

				if(sb.length()!=0) {
					if(headerGroupIdx==0) {
						if(i==0 && map.size()>0) {
							if (sb.indexOf("END")>-1) {
								done();
								break;
							}
							newEntry = true;
						} else if(i==1&&sb.indexOf("BSCDATA")>-1) {
							reader.readUntilEOL(sb);
							headerGroupIdx = 1;
						} else if(sb.indexOf("END")>-1) {
							done();
							break;
						} else {
							map.put(header.getName(), sb.toString().trim());
						}
					} else if(headerGroupIdx==1) {
						if(i==0) {
							newEntry = true;
							if(sb.indexOf("END")>-1) {
								done();
								break;
							}
						} else if(i==1&&sb.indexOf("BSCCODEC")>-1) {
							reader.readUntilEOL(sb);
							headerGroupIdx = 2;
						} else if(i==1){
							if (!reader.isEOL()) {
								String s = sb.toString();
								reader.readUntilEOL(sb);
								sb.insert(0, s);
							}
							String b = (String)map.get(headers[6].getName());
							if (b!=null&&b.length()>0) {
								sb.insert(0, b + ", ");
								map.put(headers[6].getName(), sb.toString().trim());
							} else
								map.put(headers[6].getName(), sb.toString().trim());
						}
					} else {
						if(i==0) {
							newEntry = true;
							if(sb.indexOf("END")>-1) {
								listener.onReadyData(ctx, map, reader.getLine());
								map = new LinkedHashMap<String, Object>();
								done();
								break;
							}
						} else {
							if (!reader.isEOL()) {
								String s = sb.toString();
								reader.readUntilEOL(sb);
								sb.insert(0, s);
							}
							String b = (String)map.get(headers[7].getName());
							if (b!=null&&b.length()>0) {
								sb.insert(0, b + ", ");
								map.put(headers[7].getName(), sb.toString().trim());
							} else
								map.put(headers[7].getName(), sb.toString().trim());
						}
					}
				}
				if (newEntry) {
					ctx.setTableName(getCommand());
					listener.onReadyData(ctx, map, reader.getLine());
					map = new LinkedHashMap<String, Object>();
					map.put(header.getName(), sb.toString().trim());
					headerGroupIdx = 0;
					newEntry = false;
				}
			}
			reader.skipEOLs();
		}
	}
	
}
