package id.co.telkom.parser.entity.traversa.huawei.command;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import id.co.telkom.parser.common.charparser.AbstractCommandHandler;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;
import id.co.telkom.parser.entity.traversa.common.EdgeContext;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;
import id.co.telkom.parser.entity.traversa.model.Vertex;

public class ParserSccpDSPCommandHandler extends AbstractCommandHandler implements CommandHandler{
	private ConfiguredHeader[] headers;
	private Parser reader;
	private boolean isStartExecution;
	Map<String, Object> map = new LinkedHashMap<String, Object>();
	private  DataListener listener;
	private static final Logger logger = Logger.getLogger(ParserSccpDSPCommandHandler.class);
	private GlobalBuffer buf;
	
	public ParserSccpDSPCommandHandler(Parser reader, String command,
			String params, Context ctx, ConfiguredHeader[] headers, DataListener listener) {
		super(command, params);
		this.headers=headers;
		this.reader=reader;
		this.listener=listener;
	}
	
	public ParserSccpDSPCommandHandler(Parser reader, String command,
			String params, Context ctx, ConfiguredHeader[] headers, DataListener listener, 
			GlobalBuffer buf) {
		super(command, params);
		this.headers=headers;
		this.reader=reader;
		this.listener=listener;
		this.buf=buf;
	}

	@Override
	public void handle(Context ctx) throws IOException {
		StringBuilder sb = new StringBuilder();
		
		while (!isStartExecution && !reader.isEOL() && !reader.isEqual('(')){
			if(reader.isEqual(' ')){
				reader.readUntilEOL(sb);
				if(sb.toString().trim().contains("DSP name")){
					isStartExecution = true;
				}else{
					System.err.println("Skip : "+sb);
				}
			} 
			else if(reader.isEqual('(')){
					done();
					return;
			}
			else{
				reader.readUntilEOL(sb);
			}

			reader.skipEOLs();
		}
		
		if(reader.isEqual('(')){
				done();
				return;
		}	
		if(!isStartExecution){
			done();
			return;
		} 
			while(!reader.isEOF() && !reader.isEqual('(') && isStartExecution){					
					listener.onBeginTable(reader.getLine(), ctx);
				while(!reader.isEOL() && !reader.isEqual('(')){
					map = new LinkedHashMap<String, Object>();
					parse(map, reader, headers);
					ctx.setTableName(getCommand());
					listener.onReadyData(ctx, map,reader.getLine());
					Object pc = map.get("NAT_NET_DPC");
					if(pc!=null)
						ProcessDestination(ctx.ne_id, pc.toString(), buf, listener, ctx);
				}

				
				while(reader.isEOL() || reader.isEqual(' '))
					reader.read();
				if (reader.isEqual('(')){
					done();
					return;
				}
			}
		
			if (reader.isEqual('(')){
				done();
				return;
			}
		reader.readUntilEOL(sb);
		if(sb.toString().trim().startsWith("(Number")){
			done();
			return;
		}
		
	}
	
	private void parse(Map<String, Object> map, Parser reader, ConfiguredHeader[] header) throws IOException {		
		StringBuilder sb = new StringBuilder();
		int lastHeader = header.length-1;
	
		for (int i = 0; i < header.length; i++) {
			if(lastHeader==i){
				reader.readUntilEOL(sb);
			}else
				reader.read(sb, header[i].getLength());

			if (i==1 || i==2 || i==3 || i==4 || i==5)
				map.put(header[i].getName(), convertToDEc(sb.toString().trim()));
			else
				map.put(header[i].getName(), sb.toString().trim());
	
		}

	}
	
	private int convertToDEc(String Str){
		return Integer.parseInt(Str.substring(2).trim(),16);
	}
	
	@SuppressWarnings("unused")
	private void processTopology(final Map<String, Object> localMap){
		
	}
	
	private static void ProcessDestination(final String ne, final String pc, final GlobalBuffer buf, final DataListener listener, final Context ctx){
		final String T_NAME_EDGE="EDGE_TOPOLOGY";
		EdgeContext edctx = new EdgeContext();
		Vertex dest = buf.getOwnSpOfVertex().get(getPC(pc));
		//dapet destinationnya..
		if(dest!=null){
			edctx.setTableName(T_NAME_EDGE);
			edctx.setEdge_source(ne);
			edctx.setEdge_destPC(pc.toString());
			if(dest.getVENDOR().toUpperCase().contains("MSC")||dest.getVENDOR().toUpperCase().contains("MSS")){
				edctx.setEdge_type("HLR-MSS");
			}else if(dest.getVENDOR().toUpperCase().contains("TP")){
				edctx.setEdge_type("HLR-TP");
			}else if(dest.getNE_ID().contains("HLR")){
				edctx.setEdge_type("HLR-HLR");
			}
			edctx.setEdge_dest(dest.getNE_ID());
			edctx.setEdge_id(ne+"-"+dest.getNE_ID());
			edctx.setEdgeWeight(3);
			ctx.setLoadWithPrefix(true);
			listener.onTopologyData(ctx, edctx);
			
		}else{
			logger.error("Cannot find Huawei Destination from "+ne+" with PC:"+pc);
		}
	}
	
	private static Integer getPC(String s){
		boolean isValid=s.contains("-");
		if(isValid){
			try{
				return Integer.parseInt(s.split("-")[1]);
			}catch (NumberFormatException e){
				return 0;
			}
		}else
			try{
				return Integer.parseInt(s);
			}catch (NumberFormatException e){
				return 0;
			}
	}
}
