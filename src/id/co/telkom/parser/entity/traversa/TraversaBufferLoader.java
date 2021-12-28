package id.co.telkom.parser.entity.traversa;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;
import id.co.telkom.parser.entity.traversa.common.EdgeContext;
import id.co.telkom.parser.entity.traversa.model.CItpBuffer;
import id.co.telkom.parser.entity.traversa.model.EMscBuffer;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;
import id.co.telkom.parser.entity.traversa.model.SStpBuffer;
import id.co.telkom.parser.entity.traversa.model.Vertex;

public class TraversaBufferLoader {
	private static final Logger logger = Logger.getLogger(TraversaBufferLoader.class);
	public static void Load(final DataListener listener, final Context ctx, final GlobalBuffer buf){
		LoadVertex(listener,ctx,buf);
	}
	
	public static void LoadVertex(final DataListener listener, final Context ctx, final GlobalBuffer buf){
		ctx.setTableName("VERTEX");
		ctx.setLoadWithPrefix(false);
		for(Map.Entry<String, Vertex> v : buf.getNeOfVertex().entrySet()){
			Map<String, Object> map = new LinkedHashMap<String, Object>();
				ctx.setNe_id(v.getValue().getNE_ID());
				map.put("NE_NAME", v.getValue().getNE_ID());
				map.put("VENDOR", v.getValue().getVENDOR());
				map.put("OWN_SP_DEC", v.getValue().getOWN_SP_DEC().toString().replace("[", "").replace("]", ""));
				map.put("OWN_GT", v.getValue().getOWN_GT().toString().replace("[", "").replace("]", ""));
				map.put("OWN_MSRN", v.getValue().getOWN_MSRN().toString().replace("[", "").replace("]", ""));
				map.put("IP", v.getValue().getIP().toString().replace("[", "").replace("]", ""));
			listener.onReadyData(ctx, map, 0);
		}
	}
	
	//v1.0, all node (including BSC & RNC) already in vertex manifest
	public static void LoadEMscTopologyV10(final DataListener listener, final Context ctx, final GlobalBuffer buf){
		EMscBuffer emscbuf = buf.geteMscBuf();
		EdgeContext edctx = new EdgeContext();
		final String T_NAME_EDGE="EDGE_TOPOLOGY";
		for(Map.Entry<String, List<Map<String, Object>>> exrops : emscbuf.getExrop().entrySet()){
			String ne = exrops.getKey();
			ctx.setNe_id(ne);
			Map<String, Object> nrgwp =emscbuf.getNrgwp(ne);
			for (Map<String, Object> exrop : exrops.getValue()){
				//Generate Topology here
				//get MG from ngrwp
				Object dety = exrop.get("DETY");
				if(dety!=null && (
							dety.toString().equals("UPDR")||
							dety.toString().equals("BID")||
							dety.toString().equals("MRALT")||
							dety.toString().equals("MUIUCM")||
							dety.toString().equals("MAIPCM")
					)){
					Object sp = exrop.get("SP");
					if(sp!=null){
						Integer pc = getPC(sp.toString());
						if(pc!=0 ){
							Vertex dest = buf.getOwnSpOfVertex().get(pc);
							//dapet destinationnya..
							if(dest!=null){
								edctx.setTableName(T_NAME_EDGE);
								edctx.setEdge_source(ne);
								edctx.setEdge_destPC(pc.toString());
								edctx.setEdge_additional_param(exrop.toString());
								//ke BSC/RNC
								if(dest.getVENDOR().toUpperCase().contains("BSC")||dest.getVENDOR().toUpperCase().contains("RNC")){
									Object MGG = exrop.get("MGG");
									//Find MGW from nrgwp
									if(MGG!=null && nrgwp!=null){
										Object MGW = nrgwp.get(MGG.toString());
										if(MGW!=null){
											//MSS-MGW
											edctx.setEdge_type("MSS-MGW");
											edctx.setEdgeWeight(2);
											edctx.setEdge_dest(MGW.toString());
											edctx.setEdge_id(ne+"-"+MGW.toString());
											listener.onTopologyData(ctx, edctx);
											//MGW-BSC/RNC
											edctx.setEdge_type("MGW-BSC/RNC");
											edctx.setEdgeWeight(1);
											edctx.setEdge_source(MGW.toString());
											edctx.setEdge_id(MGW.toString()+"-"+dest.getNE_ID());
											edctx.setEdge_dest(dest.getNE_ID());
											listener.onTopologyData(ctx, edctx);
										}
									}else
										logger.error("Cannot find MGG from exrop:"+ne+exrop);
									
								}else{//selain ke BSC/RNC
									if(dest.getVENDOR().toUpperCase().contains("MSC")||dest.getVENDOR().toUpperCase().contains("MSS")){
										edctx.setEdge_type("MSS-MSS");
									}
									edctx.setEdge_dest(dest.getNE_ID());
									edctx.setEdge_id(ne+"-"+dest.getNE_ID());
									edctx.setEdgeWeight(3);
									listener.onTopologyData(ctx, edctx);
								}
								
							}else{
								//System.out.println("Cannot find Destination from "+ne+" with PC:"+pc+exrop);
								logger.error("Cannot find Destination from "+ne+" with PC:"+pc+exrop);
							}
						}
					}
				}
			}
		}
	}

	//TODO: finish it..
	//v1.1, BSC & RNC from c7spp
	@SuppressWarnings("unused")
	public static void LoadEMscTopologyV11(final DataListener listener, final Context ctx, final GlobalBuffer buf){
		EMscBuffer emscbuf = buf.geteMscBuf();
		EdgeContext edctx = new EdgeContext();
		final String T_NAME_EDGE="EDGE_TOPOLOGY";
		for(Map.Entry<String, List<Map<String, Object>>> exrops : emscbuf.getExrop().entrySet()){
			String ne = exrops.getKey();
			ctx.setNe_id(ne);
			Map<String, Object> nrgwp =emscbuf.getNrgwp(ne);
			Map<String, Object> c7spp =emscbuf.getC7spp(ne);
			for (Map<String, Object> exrop : exrops.getValue()){
				//Generate Topology here
				//get MG from ngrwp
				Object dety = exrop.get("DETY");
				if(dety!=null && (
							dety.toString().equals("UPDR")||
							dety.toString().equals("BID")||
							dety.toString().equals("MRALT")||
							dety.toString().equals("MUIUCM")||
							dety.toString().equals("MAIPCM")
					)){
					Object sp = exrop.get("SP");
					if(sp!=null){
						Integer pc = getPC(sp.toString());
						if(pc!=0 ){
							Vertex dest = buf.getOwnSpOfVertex().get(pc);
							//dapet destinationnya..
							if(dest!=null){
								edctx.setTableName(T_NAME_EDGE);
								edctx.setEdge_source(ne);
								edctx.setEdge_destPC(pc.toString());
								edctx.setEdge_additional_param(exrop.toString());
								if(dest.getVENDOR().toUpperCase().contains("MSC")||dest.getVENDOR().toUpperCase().contains("MSS")){
									edctx.setEdge_type("MSS-MSS");
								}else if(dest.getVENDOR().toUpperCase().contains("TP")){
									edctx.setEdge_type("MSS-TP");
								}
								edctx.setEdge_dest(dest.getNE_ID());
								edctx.setEdge_id(ne+"-"+dest.getNE_ID());
								edctx.setEdgeWeight(3);
								listener.onTopologyData(ctx, edctx);
								
							}else{
								//System.out.println("Cannot find Destination from "+ne+" with PC:"+pc+exrop);
								logger.error("Cannot find Destination from "+ne+" with PC:"+pc+exrop);
							}
						}
						//Lookup to C7SPP
					}
				}
			}
		}
		
	}
	
	
	public static void LoadCItpTopologyV10(final DataListener listener, final Context ctx, final GlobalBuffer buf){
		CItpBuffer itpbuf = buf.getIitpBuf();
		for(Map.Entry<String, List<Map<String, Object>>> cs7AppGroups  : itpbuf.getAppGroups().entrySet()){
			String ne = cs7AppGroups.getKey();
			ctx.setNe_id(ne);
			for (Map<String, Object> cs7AppGroup : cs7AppGroups.getValue()){
				Object pc = cs7AppGroup.get("PC");
				if(pc!=null){
					if(pc.toString().equals("0")){//lookup to cs7as
						//....
						Object asname = cs7AppGroup.get("ASNAME");
						if(asname!=null){
							Map<String, Object> cs7as = itpbuf.getCs7as(ne);
							Object routing_key_dpc = cs7as!=null ? cs7as.get(asname.toString()):null;
							if(routing_key_dpc!=null){
								ProcessDestinationCItp(ne, routing_key_dpc.toString(), buf, listener, ctx);
							}
						}
					}else{//lookup to vertex
						//....
						ProcessDestinationCItp(ne, pc.toString(), buf, listener, ctx);
					}
				}
			}
		}
	}
	
	private static void ProcessDestinationCItp(final String ne, final String pc, final GlobalBuffer buf, final DataListener listener, final Context ctx){
		final String T_NAME_EDGE="EDGE_TOPOLOGY";
		EdgeContext edctx = new EdgeContext();
		Vertex dest = buf.getOwnSpOfVertex().get(getPC(pc));
		//dapet destinationnya..
		if(dest!=null){
			edctx.setTableName(T_NAME_EDGE);
			edctx.setEdge_source(ne);
			edctx.setEdge_destPC(pc.toString());
			if(dest.getVENDOR().toUpperCase().contains("MSC")||dest.getVENDOR().toUpperCase().contains("MSS")){
				edctx.setEdge_type("TP-MSS");
			}else if(dest.getVENDOR().toUpperCase().contains("TP")){
				edctx.setEdge_type("TP-TP");
			}else if(dest.getNE_ID().contains("HLR")){
				edctx.setEdge_type("TP-HLR");
			}
			edctx.setEdge_dest(dest.getNE_ID());
			edctx.setEdge_id(ne+"-"+dest.getNE_ID());
			edctx.setEdgeWeight(3);
			ctx.setLoadWithPrefix(true);
			listener.onTopologyData(ctx, edctx);
			
		}else{
			logger.error("Cannot find Cisco Destination from "+ne+" with PC:"+pc);
		}
	}
	
	public static void LoadSStpTopologyV10(final DataListener listener, final Context ctx, final GlobalBuffer buf){
		SStpBuffer stpBuf = buf.getSstpBuf();
		for (Map.Entry<String, Map<String, Object>> m : stpBuf.getSpenHSs().entrySet()){
			String ne = m.getKey();
			ctx.setNe_id(ne);
			for(Map.Entry<String, Object> spenHsName : m.getValue().entrySet()){
				Object dpc =  stpBuf.getSpLnkRemDpc(ne, spenHsName.getKey());
				if(dpc!=null){
					ProcessDestinationSStp(ne, dpc.toString(),buf,listener,ctx);
				}
			}
		}
	}
	
	private static void ProcessDestinationSStp(final String ne, final String pc, final GlobalBuffer buf, final DataListener listener, final Context ctx){
		final String T_NAME_EDGE="EDGE_TOPOLOGY";
		EdgeContext edctx = new EdgeContext();
		Vertex dest = buf.getOwnSpOfVertex().get(getPC(pc));
		//dapet destinationnya..
		if(dest!=null){
			edctx.setTableName(T_NAME_EDGE);
			edctx.setEdge_source(ne);
			edctx.setEdge_destPC(pc.toString());
			final String sourceType = ne.contains("STP") ? "TP" : "MSS";
			
			if(dest.getNE_ID().contains("HLR")){
				edctx.setEdge_type(sourceType+"-HLR");
			} else
			if((dest.getNE_ID().toUpperCase().startsWith("STP")||dest.getNE_ID().toUpperCase().startsWith("ITP"))){
				edctx.setEdge_type(sourceType+"-TP");
			} else
				edctx.setEdge_type(sourceType+"-MSS");
			
			edctx.setEdge_dest(dest.getNE_ID());
			edctx.setEdge_id(ne+"-"+dest.getNE_ID());
			edctx.setEdgeWeight(3);
			ctx.setLoadWithPrefix(true);
			listener.onTopologyData(ctx, edctx);
			
		}else{
			logger.error("Cannot find Siemens STP/MSS Destination from "+ne+" with PC:"+pc);
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
