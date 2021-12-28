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
import id.co.telkom.parser.entity.traversa.common.EdgeContext;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;
import id.co.telkom.parser.entity.traversa.model.Vertex;


public class ParserZNRICommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private Map<String, ConfiguredHeader[]> headersMap = new LinkedHashMap<String, ConfiguredHeader[]>();
	private final String T_NAME;
	private static final Logger logger = Logger.getLogger(ParserZNRICommandHandler.class);
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	private Map<String, Object> mapFirst = new LinkedHashMap<String, Object>();
	private final GlobalBuffer gb;
	@SuppressWarnings("unused")
	private String Param;
	private boolean isStartExecution;
	private Map<String, String> buffer = new LinkedHashMap<String, String>();
	private final String T_NAME_EDGE="EDGE_TOPOLOGY";
	private final EdgeContext edctx = new EdgeContext();
	
	public ParserZNRICommandHandler(
			Parser reader, 
			DataListener listener, 
			String command, 
			String params, 
			Map<String, ConfiguredHeader[]> headersMap,
			AbstractInitiator cynapseInit) {
		super(command, params);
		this.reader = reader;
		this.listener=listener;
		this.headersMap=headersMap;
		this.T_NAME=command;
		this.gb=(GlobalBuffer)cynapseInit.getMappingModel();
		this.Param=params;
	}
	@Override
	public void handle(Context ctx)
			throws IOException {
		StringBuilder sb = new StringBuilder();
		while (!isStartExecution && !reader.isEOL() && !reader.isEqual('<')){
			reader.skipEOLs().readUntilEOL(sb);
			
			if (sb.toString().startsWith("LOADING PROGRAM")) {
				reader.skipEOLs().readUntil(' ', sb);
				if(sb.toString().equals("MSCi")){
					reader.skipWhile(' ').readUntil(' ', sb).skipUntilEOL();
					ctx.setNe_id(sb.toString());
				}
			} else if(sb.toString().startsWith("/*** COMMAND NOT FOUND ***/")){
				listener.onError(reader.getLine(), ctx, reader.getColumn(), sb.toString());
				reader.skipEOLs();
				done();
				return;
			}if (sb.toString().startsWith("INTERROGATING SIGNALLING")) {
				isStartExecution=true;
			}
			else{
				logger.error("Skip : "+sb);
			}
			reader.skipEOLs();
		}
		
		if(!isStartExecution){
			done();
			return;
		}
		
		while (!reader.isEOL() && !reader.isEqual('<')) {
			if(reader.isEqual('N')){
				reader.readUntilEOL(sb).skipEOLs();
				if(sb.toString().startsWith("NET  SP CODE")){
					reader.skipLines(1);
					if(reader.isEqual('N')){//here the master
						ParseHeader(headersMap.get("MASTER"));
					}else
						reader.readUntilEOL(sb);
				}
			}if(reader.isEqual('R')){
				reader.readUntilEOL(sb).skipEOLs();
				if(sb.toString().startsWith("ROUTES:")){
					reader.skipLines(1);
					while(!(reader.isEqual('<')||reader.isEqual('N')||reader.isEqual('C'))){
						map = new LinkedHashMap<String, Object>(mapFirst);
						Parse(headersMap.get("ROUTES"));
						reader.skipEOL();
						if(reader.isEOL())
							reader.skipEOL();
						Object o =map.get("ROUTES_SP_CODE_H_D"); 
						if(o!=null&&!o.toString().equals("")){//ready
							ctx.setLoadWithPrefix(true);
							ctx.setTableName(T_NAME);
							listener.onReadyData(ctx, map, reader.getLine());
							//TODO: topology here
							processTopology(ctx);
						}
					}
				}
			}else{
				reader.readUntilEOL(sb).skipEOL();
			}
			reader.skipEOLs();
		}
		done();
		return;
	}
	
	private void processTopology(Context ctx){
		ctx.setLoadWithPrefix(true);
		Object dest = map.get("SP_CODE_H_D");
		if(dest!=null){
			edctx.setTableName(T_NAME_EDGE);
			Vertex vdest = gb.getOwnSpOfVertex().get(getPointCode(dest.toString()));
			if(vdest==null){
				vdest = new Vertex();
				Object name = map.get("NAME");
				if(name!=null){
					vdest.setNE_ID(name.toString());
					vdest.setNE_NAME(name.toString());
				}
				vdest.setOWN_SP_DEC(getPointCode(dest.toString()));
				
				String message = "N_MSC "+ctx.ne_id+" destination not found for PC:"+dest.toString()+
								", using original dest applied:"+name.toString();
				
				System.out.println(message);
				logger.info(message);
			}
			
			Object destRoute = map.get("ROUTES_SP_CODE_H_D");
				
			if(destRoute!=null && // MSS-MSS/MSS-STP 
				dest.toString().equals(destRoute.toString())){
					edctx.setEdge_source(ctx.ne_id);
					if(vdest.getNE_ID().startsWith("W")||vdest.getNE_ID().startsWith("M")||vdest.getNE_ID().startsWith("Z")){
						edctx.setEdge_type("MSS-MGW");
						edctx.setEdgeWeight(2);
					}if(vdest.getNE_ID().startsWith("S")||vdest.getNE_ID().startsWith("I")){
						edctx.setEdge_type("MSS-TP");
						edctx.setEdgeWeight(3);
					}else{
						edctx.setEdge_type("MSS-MSS");
						edctx.setEdgeWeight(3);
					}
					
					edctx.setEdge_destPC(getPointCode(dest.toString()).toString());
					edctx.setEdge_dest(vdest.getNE_ID());
					edctx.setEdge_id(ctx.ne_id+"-"+vdest.getNE_ID());
					listener.onTopologyData(ctx, edctx);
			}else if(vdest.getNE_NAME().startsWith("B")||vdest.getNE_NAME().startsWith("R")){//MSS-MGW-BSC/RNC
				//MSS-MGW
				edctx.setEdge_type("MSS-MGW");
				edctx.setEdgeWeight(2);
				edctx.setEdge_source(ctx.ne_id);
				Integer pointCodeMGW = getPointCode(dest.toString());
				edctx.setEdge_destPC(pointCodeMGW.toString());
				Vertex mgw = gb.getOwnSpOfVertex().get(pointCodeMGW);
				String mgwDest;
				if(mgw!=null)
					mgwDest = mgw.getNE_NAME();
				else
					mgwDest = map.get("ROUTE_NAME")==null?"":map.get("ROUTE_NAME").toString();
				edctx.setEdge_dest(mgwDest);
				edctx.setEdge_id(ctx.ne_id+"-"+mgwDest);
				listener.onTopologyData(ctx, edctx);
				
				//MGW-BSC/RNC
				edctx.setEdge_type("MGW-BSC/RNC");
				edctx.setEdgeWeight(1);
				edctx.setEdge_destPC(getPointCode(dest.toString()).toString());
				edctx.setEdge_dest(vdest.getNE_ID());
				edctx.setEdge_source(mgwDest);
				edctx.setEdge_id(mgwDest+"-"+vdest.getNE_ID());
				listener.onTopologyData(ctx, edctx);
			}
		}
	}
	
	private Integer getPointCode(String s){
		if(s!=null && s.contains("/"))
			try{
				return Integer.parseInt(s.split("/")[1]);
			}catch (NumberFormatException e){
				return 0;
			}
		return 0;
	}
	
	private void ParseHeader(ConfiguredHeader[] header) throws IOException{
		StringBuilder sb = new StringBuilder();
		final int lastIdx = header.length-1;
		for (int i = 0; !isDone() && i <= lastIdx; i++) {
			ConfiguredHeader configuredHeader = header[i];
			if(lastIdx==i){
				reader.readUntilEOL(sb);
			}
			else{
				reader.read(sb, configuredHeader.getLength());
				if(i==0){
					if(sb.toString().startsWith("COMMAND")){
						done();
					}
				}
			}
			if(!isDone()){
				String s = sb.toString().trim();
				
				if(configuredHeader.copied && s.length() > 0){
					buffer.put(configuredHeader.getName(), s);
				}
				if(configuredHeader.copied)
					mapFirst.put(configuredHeader.getName(), buffer.get(configuredHeader.getName()));
				else
					mapFirst.put(configuredHeader.getName(), s);
			}
		}
	}
	private void Parse(ConfiguredHeader[] header) throws IOException{
		StringBuilder sb = new StringBuilder();
		final int lastIdx = header.length-1;
		for (int i = 0; !isDone() && i <= lastIdx; i++) {
			ConfiguredHeader configuredHeader = header[i];
			if(lastIdx==i){
				reader.readUntilEOL(sb);
			}
			else{
				reader.read(sb, configuredHeader.getLength());
				if(i==0){
					if(sb.toString().startsWith("COMMAND")){
						done();
					}
				}
			}
			if(!isDone()){
				String s = sb.toString().trim();
				
				if(configuredHeader.copied && s.length() > 0){
					buffer.put(configuredHeader.getName(), s);
				}
				if(configuredHeader.copied)
					map.put(configuredHeader.getName(), buffer.get(configuredHeader.getName()));
				else
					map.put(configuredHeader.getName(), s);
			}
		}
	}
	
}
