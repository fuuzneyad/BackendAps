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
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;


public class ParserNRGWPCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private ConfiguredHeader[] headers;
	private final String T_NAME;
	private final GlobalBuffer gb;
	private static final Logger logger = Logger.getLogger(ParserNRGWPCommandHandler.class);
	private Map<String, String> buffer = new LinkedHashMap<String, String>();
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	
	public ParserNRGWPCommandHandler(
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
			}else if(reader.isEqual('M')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("MEDIA GATEWAY DATA")){
					isStartExecution = true;
				}
			}else{
				reader.readUntilEOL(sb);
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
				Parse();
				ctx.setTableName(T_NAME);
				listener.onReadyData(ctx, map, reader.getLine());
				putBufferNrgwp(map, ctx);
				map = new LinkedHashMap<String, Object>();
				reader.skipEOLs();
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
				if(configuredHeader.copied){
					if(s.equals(""))
						map.put(configuredHeader.getName(), buffer.get(configuredHeader.getName()));
					else
						buffer.put(configuredHeader.getName(), s);
				}
			}
		}
	}
	
	private void putBufferNrgwp(Map<String,Object>map, Context ctx){
		if(map.get("MGG")!=null&&map.get("MG")!=null)
			this.gb.geteMscBuf().setNrgwp(ctx.ne_id, map.get("MGG").toString(), map.get("MG").toString());
	}
	
}
