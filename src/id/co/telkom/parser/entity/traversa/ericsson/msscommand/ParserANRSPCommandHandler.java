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


public class ParserANRSPCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	private ConfiguredHeader[] headers;
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	private final String T_NAME;
	@SuppressWarnings("unused")
	private final GlobalBuffer gb;
	private static final Logger logger = Logger.getLogger(ParserANRSPCommandHandler.class);
	
	public ParserANRSPCommandHandler(
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
		ctx.setTableName(T_NAME);
		boolean isStartExecution = false;
		StringBuilder sb = new StringBuilder();
		while(!isStartExecution && !reader.isEOF() && !reader.isEqual('<')){
			if(reader.isEqual('/')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("/*** COMMAND NOT FOUND ***/")){
					logger.error("ANRSP :"+sb);
					done();
					return;
				}else{
					System.err.println("Skip : "+sb);
				}
			}else if(reader.isEqual('O')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("OPERATING AREA")){
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
			while(!reader.isEOF() && !isDone() && !reader.isEqual('<')){
				if(reader.isEqual('R')){
					reader.readUntilEOL(sb);
				}else 
				if(reader.isNumber()){
					//System.out.println(sb.toString());
					Parse(headers,map);
					map.remove("DATA");
//					System.out.println(map);
					listener.onReadyData(ctx, map, reader.getLastReadLine());
					map = new LinkedHashMap<String, Object>();
				}else{
					reader.readUntilEOL(sb);
				}
					
				reader.skipEOL();
			}
		}
		done();
		return;
	}
	private void Parse(ConfiguredHeader[] header, Map<String, Object> map) throws IOException{
		StringBuilder sb = new StringBuilder();
		final int lastIdx = header.length-1;
		for (int i = 0; !isDone() && i <= lastIdx; i++) {
			ConfiguredHeader configuredHeader = header[i];
			if(lastIdx==i){//DATA
				int colPos = reader.getColumn();
				reader.readUntilEOL(sb).skipEOL();
				String firstFields = sb.toString().trim();
				if(!firstFields.equals("")){
					//first
					reader.skipSomeChar(colPos);//skip whitespace
					ParseComponent(firstFields);
					//sisanya
					for(int j=1;j<=3;j++){
						reader.skipSomeChar(colPos);//skip whitespace
						reader.readUntilEOL(sb).skipEOL();
						reader.skipSomeChar(colPos);
						ParseComponent(sb.toString());
					}
				}
			}else{
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
				if(s.contains("=")){
					String[] spt1 = s.split("\\s+");
					for(String ss:spt1){
						String[] spt2 = ss.split("=");
						map.put(spt2[0], spt2[1]);
					}
				}
			}
		}
	}
	
	private void ParseComponent(String header) throws IOException{
		reader.skipEOL();
		StringBuilder sb = new StringBuilder();
		String[] hdr = header.split(" \\b");
		for (int i=0;i<hdr.length;i++){
			if(i==hdr.length-1)
				reader.readUntilEOL(sb).skipEOL();
			else{
				int pjg  =(hdr[i]).length()+1;
				reader.read(sb,pjg);
			}
			
			if(!hdr[i].trim().equals(""))
					map.put(hdr[i].trim(), sb.toString().trim());
		}
	}
	
}
