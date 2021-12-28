package id.co.telkom.parser.entity.traversa.ericsson.msscommand;


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


public class ParserEXROPCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	@SuppressWarnings("unused")
	private ConfiguredHeader[] headers;
	private final EdgeContext edctx = new EdgeContext();
	private final String T_NAME;
	private final String T_NAME_EDGE="EDGE_TOPOLOGY";
	private final GlobalBuffer gb;
	private static final Logger logger = Logger.getLogger(ParserEXROPCommandHandler.class);
	
	public ParserEXROPCommandHandler(
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
		boolean isStartExecution = false;
		StringBuilder sb = new StringBuilder();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		boolean isFirst=true;
		
		while(!isStartExecution && !reader.isEOF() && !reader.isEqual('<')){
			if(reader.isEqual('/')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("/*** COMMAND NOT FOUND ***/")){
					logger.error(sb.toString());
					done();
					return;
				}else{
					System.err.println("Skip : "+sb);
				}
			}else if(reader.isEqual('R')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("ROUTE DATA")){
					isStartExecution = true;
				}
			}else{
				reader.readUntilEOL(sb);
				System.err.println("Skip : "+sb);
			}
			reader.skipEOLs();
		}
		if(!isStartExecution){
			done();
			return;
		}
		
		while(!isDone() && !reader.isEOF() && !reader.isEqual('<')){
			if(reader.isEqual('E')){
				reader.readUntilEOL(sb).skipEOL();
				if(sb.toString().trim().equals("END")){
					//ready last
					//init context..
					ctx.setTableName(T_NAME);
					edctx.setTableName(T_NAME_EDGE);
					edctx.setEdge_type("USER_PLANE");
					edctx.setEdge_source(ctx.ne_id);
					listener.onReadyData(ctx, map, reader.getLine());
					// topology 
//					processTopology(map,listener,ctx, edctx);
					putBufferExrop(map, ctx);
					map = new LinkedHashMap<String, Object>();
					done();
					return;
				}
					
			}		
			else{
				reader.readUntilEOL(sb);
				if(sb.toString().contains("=")){
					String[] ss = sb.toString().split("\\s+");
					if(sb.toString().contains("DETY=")){
						String R=ss[0];
						//ok
						if(!isFirst){
							//init context..
							ctx.setTableName(T_NAME);
							edctx.setTableName(T_NAME_EDGE);
							edctx.setEdge_type("USER_PLANE");
							edctx.setEdge_source(ctx.ne_id);
							
							listener.onReadyData(ctx, map, reader.getLine());
							// topology
//							processTopology(map, listener, ctx, edctx);
							putBufferExrop(map, ctx);
							map = new LinkedHashMap<String, Object>();
						}
						map.put("R", R);
						isFirst=false;
					}
					for (String s:ss){
						if(s.contains("=")){
							String k =s.split("=")[0].trim();
							k=k.equals("R")? "R_OPPOSITE":k;
							k=k.equals("TO")? "TO_":k;
							map.put(k,s.split("=")[1].trim());
						}
					}
				}else
				if(sb.toString().trim().startsWith("END"))//yyn
				{
					done(); 
					return;
				}
//				else
//				System.err.println("Skip : "+sb);
				reader.skipEOLs();
			}
			
		}
	}
	
	private void putBufferExrop(final Map<String, Object> map, final Context ctx){
		gb.geteMscBuf().setExrop(ctx.ne_id,map);
	}
	
	@SuppressWarnings("unused")
	private void processTopology(Map<String, Object> map, DataListener listener,Context ctx, EdgeContext edctx){
		Object dety = map.get("DETY");
		if(dety!=null && (
					dety.toString().equals("UPDR")||
					dety.toString().equals("BID")||
					dety.toString().equals("MRALT")||
					dety.toString().equals("MUIUCM")||
					dety.toString().equals("MAIPCM")
			)){
			Object sp = map.get("SP");
			if(sp!=null){
				Integer pc = getPC(sp.toString());
				if(pc!=0 ){
					Vertex v = gb.getOwnSpOfVertex().get(pc);
					if(v!=null){
						edctx.setEdge_destPC(pc.toString());
						edctx.setEdge_dest(v.getNE_ID());
						edctx.setEdge_id(ctx.ne_id+"-"+v.getNE_ID());
						listener.onTopologyData(ctx, edctx);
					}else{
						System.out.println("Cannot find Destination from "+ctx.ne_id+" with PC:"+pc+"exrop:"+map);
						logger.error("Cannot find Destination from "+ctx.ne_id+" with PC:"+pc+"exrop:"+map);
					}
				}
			}
		}
	}
	
	private Integer getPC(String s){
		boolean isValid=s.contains("-");
		if(isValid){
			try{
				return Integer.parseInt(s.split("-")[1]);
			}catch (NumberFormatException e){
				return 0;
			}
		}
		return 0;
	}
}
