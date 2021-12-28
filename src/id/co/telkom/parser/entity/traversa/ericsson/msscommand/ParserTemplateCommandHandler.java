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


public class ParserTemplateCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private ConfiguredHeader[] headers;
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	private final EdgeContext edctx = new EdgeContext();
	private final String T_NAME;
	private final String T_NAME_EDGE="EDGE_TOPOLOGY";
	private final GlobalBuffer gb;
	private static final Logger logger = Logger.getLogger(ParserTemplateCommandHandler.class);
	
	public ParserTemplateCommandHandler(
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
		//init context..
		ctx.setTableName(T_NAME);
		edctx.setTableName(T_NAME_EDGE);
		edctx.setEdge_type("MSS-TP:CONTROL_PLANE");
		edctx.setEdge_source(ctx.ne_id);
		
		boolean isStartExecution = false;
		StringBuilder sb = new StringBuilder();
		while(!isStartExecution && !reader.isEOF() && !reader.isEqual('<')){
			if(reader.isEqual('/')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("/*** COMMAND NOT FOUND ***/")){
					done();
					return;
				}else{
					System.err.println("Skip : "+sb);
				}
			}else if(reader.isEqual('C')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("CCITT7 GLOBAL TITLE ROUTING CASE DATA")){
					isStartExecution = true;
				}
			}else if(reader.isEqual('E')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("END")){
					done();
					return;
				}
			}else{
				reader.readUntilEOL(sb);
//				System.err.println("Skip : "+sb);
				if (sb.toString().equals("NOT ACCEPTED")) {
					reader.skipEOL().readUntilEOL(sb);
					listener.onError(reader.getLine(), ctx, reader.getColumn(), sb.toString());
					reader.skipEOLs();
					done();
					return;
				}
			}
			reader.skipEOLs();
		}
		if(!isStartExecution){
			done();
			return;
		}
		
		if(isStartExecution){
			reader.skipLines(1).skipEOL();
			while(!reader.isEOF() && !isDone() && !reader.isEqual('<') && !reader.isEqual('E')){
				reader.readUntilEOL(sb).skipEOL();
				Parse();
				//hooray we got it..
				if(!map.isEmpty()){
					//to raw
					listener.onReadyData(ctx, map, reader.getLine());
					//adjacent signalling edge
					Object psp =map.get("PSP");
					if(psp!=null){
						Integer pcPrim = getPC(psp.toString());
						Integer pcSec = getPC(map.get("SSP").toString());
						//get primary destination route
						if(pcPrim!=0){
							Vertex dest = gb.getOwnSpOfVertex().get(pcPrim);
							if(dest!=null){
								edctx.setEdge_destPC(pcPrim.toString());
								edctx.setEdge_dest(dest.getNE_ID());
								edctx.setEdge_id(ctx.ne_id+"-"+dest.getNE_ID());
								listener.onTopologyData(ctx, edctx);
							}else
								logger.error("Cannot find Primary Destination from "+ctx.ne_id+" with PC:"+pcPrim);
						}
						//get secondary destination route
						if(pcSec!=0){
							Vertex dest = gb.getOwnSpOfVertex().get(pcSec);
							if(dest!=null){
								edctx.setEdge_destPC(pcSec.toString());
								edctx.setEdge_dest(dest.getNE_ID());
								edctx.setEdge_id(ctx.ne_id+"-"+dest.getNE_ID());
								listener.onTopologyData(ctx, edctx);
							}else
								logger.error("Cannot find Secondary Destination from "+ctx.ne_id+" with PC:"+pcSec);
						}
					}
				}
				map=new LinkedHashMap<String, Object>();
			}
		}
		done();
		return;
		
	}
	private void Parse() throws IOException{
		StringBuilder sb = new StringBuilder();
		final int lastIdx = headers.length-1;
		for (int i = 0; !isDone() && i <= lastIdx; i++) {
			ConfiguredHeader configuredHeader = headers[i];
			if(lastIdx==i)
				reader.readUntilEOL(sb);
			else{
				reader.read(sb, configuredHeader.getLength());
				if( i==0){
					if(sb.toString().startsWith("END")) {
						done();	
					}
				}
			}
			if(!isDone()){
				String s = sb.toString().trim();
				map.put(configuredHeader.getName(), s);
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
