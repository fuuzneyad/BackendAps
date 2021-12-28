package id.co.telkom.parser.entity.traversa.ericsson.msscommand;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ConfigurableCommandHandler implements CommandHandler{
	private final Parser reader; 
	private final DataListener listener; 
	private final String T_NAME,params; 
	private final  ConfiguredHeader[] headers;
	private final int skipLines;
	private boolean done;
	public ConfigurableCommandHandler(
			Parser reader, 
			DataListener listener, 
			String command, 
			String params, 
			ConfiguredHeader[] headers,
			AbstractInitiator cynapseInit,
			int skipLines
			){
		this.reader = reader;
		this.listener=listener;
		this.headers=headers;
		this.T_NAME=command;
		this.skipLines=skipLines;
		this.params=params;
	}
	@Override
	public boolean isDone() {
		return done;
	}

	@Override
	public String getCommand() {
		return T_NAME;
	}

	@Override
	public String getParams() {
		return params;
	}

	@Override
	public void handle(Context ctx) throws IOException {
		ctx.setTableName(T_NAME);
		StringBuilder sb = new StringBuilder();
		if (skipLines > 0) {
			for(int i=0; i<skipLines; i++) {
				reader.skipEOLs().readUntilEOL(sb);
				if (sb.toString().equals("NOT ACCEPTED")|| 
					sb.toString().startsWith("FAULT CODE")|| 
					sb.toString().startsWith("SESSION LOCKED")|| 
					sb.toString().startsWith("UNKNOWN NEIGHBOURING")|| 
					sb.toString().contains("UNKNOWN NEIGHBOURING")) 
				{
					reader.skipEOL().readUntilEOL(sb);
					listener.onError(reader.getLine(), ctx, reader.getColumn(), sb.toString());
					reader.skipEOLs();
					done = true;
					return;
				} else if(sb.toString().equals("NONE")) {
					done = true;
					return;
				}
			}
//			reader.skipLines(skipLines - 1);	// TODO parameterize this
		} else {
			reader.skipLines(skipLines);	// TODO parameterize this
		}
		listener.onBeginTable(reader.getLine(), ctx);

		final int lastIndex = headers.length - 1;
		ConfiguredHeader firstHeader = headers[0];
		listener.onTableHeader(reader.getLine(), ctx, headers);
		reader.skipUntilEOL().skipEOLs();	// header line

		int end = 0;
		boolean pendingNotification = false;
		int line = reader.getLine();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Map<String, Object> buffer = new LinkedHashMap<String, Object>();
		boolean newEntry = true;
		while (!reader.isEOL() && end == 0) {
			for (int j = 0; j < headers.length && !reader.isEOL(); j++) {
				ConfiguredHeader header = headers[j];
				if (j == lastIndex) {
					reader.readUntilEOL(sb);
				} else {
//					reader.readUntil(' ', sb).skipWhile(' ');
					
					//*****
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
					if ("END".equals(s) || "NONE".equals(s)) {/*yyn, || "NONE".equals(s)*/
						end = 2;
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
					listener.onReadyData(ctx, map, line);
					map = new LinkedHashMap<String, Object>();
					pendingNotification = false;
					line = reader.getLine();
				}
			}
		}//end of WHILE
		
		
		if (pendingNotification)
			listener.onReadyData(ctx, map, reader.getLine());
		reader.skipEOL();
		if (end == 1)
			reader.skipLines(1);	// END line
		done = true;
		return;
	}
	

}
