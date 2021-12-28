package id.co.telkom.parser.entity.cli.ericsson.sgsn.nocdcommand;


import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.common.charparser.AbstractCommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;


public class PdcKpiCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private final Map<String, ConfiguredHeader[]> headersMap ;
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	public static String T_NAME="PDC";
	
	public PdcKpiCommandHandler(Parser reader, DataListener listener, String command, String params,Map<String, ConfiguredHeader[]> headersMap) {
		super(command, params);
		this.reader = reader;
		this.listener=listener;
		this.headersMap=headersMap;
	}
	
	@Override
	public void handle(Context ctx)
			throws IOException {
		StringBuilder sb = new StringBuilder();
	      while (!reader.isEOF()) {
	    	  reader.readUntilEOL(sb).skipEOLs();
	        String read = sb.toString();
	        if (read.startsWith("END")) {
	        	done();
	        	return;
	        }else
	        if (read.contains("NodeType: ")) {
	          ctx.setMo_id(read.substring(read.indexOf("NodeType:")).trim());
	        } else if (read.contains("PDP Activation")) {
	          ConfiguredHeader[] header = (ConfiguredHeader[])this.headersMap.get("PDP");
	          reader.skipLines(1);
	          ParseChild(header);
	          reader.skipEOLs();

	          ctx.setTableName(T_NAME + "_PDP");
	          listener.onReadyData(ctx, map, reader.getLine());
	          this.map = new LinkedHashMap<String, Object>();
	        } else if (read.contains("Day   Time   Mbit/s")) {
	          ConfiguredHeader[] header = (ConfiguredHeader[])this.headersMap.get("PAYLOAD");
	          reader.skipLines(1);
	          ParseChild(header);
	          reader.skipEOLs();

	          ctx.setTableName(T_NAME + "_PAYLOAD");
	          listener.onReadyData(ctx, map, reader.getLine());
	          this.map = new LinkedHashMap<String, Object>();
	        } else if (read.contains("Attached Subscribers (SAU)")) {
	          ConfiguredHeader[] header = (ConfiguredHeader[])this.headersMap.get("ATTACHED_SUBS");
	          reader.skipLines(1);
	          ParseChild(header);
	          reader.skipEOLs();

	          ctx.setTableName(T_NAME + "_ATTACHED_SUBS");
	          listener.onReadyData(ctx, map, reader.getLine());
	          this.map = new LinkedHashMap<String, Object>();
	        }
	      }
	}
	
	private void ParseChild(ConfiguredHeader[] header)
    throws IOException
	  {
	    StringBuilder sb = new StringBuilder();
	    int lastHeader = header.length - 1;
	    for (int i = 0; i < header.length; i++) {
	      if (lastHeader == i)
	        reader.readUntilEOL(sb);
	      else
	    	  reader.read(sb, header[i].getLength());
	      this.map.put(header[i].getName(), sb.toString().replace("||", "").trim());
	    }
	  }
}
