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

public class ParserMGSVPCommandHandler extends AbstractCommandHandler implements MscCommandHandler {

	private final Parser reader;
	private final DataListener listener;
	private final Map<String, ConfiguredHeader[]> headersMap;
	private Map<String, Object> buffer = new LinkedHashMap<String, Object>();
	private Map<String,Object> sum = new LinkedHashMap<String, Object>();
	
	private final String T_NAME;
	private static final Logger logger = Logger.getLogger(ParserMGSVPCommandHandler.class);
	
	public ParserMGSVPCommandHandler(
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
	}

	@Override
	public void handle(Context ctx) throws IOException {
		logger.info(T_NAME);
		ctx.setTableName(T_NAME);
		StringBuilder sb = new StringBuilder();
		boolean isStartExecution = false;
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		ConfiguredHeader[] header = null;
		boolean check = false;
		int line=0;
		while(!isStartExecution && !reader.isEOF() && !reader.isEqual('<')){
			if(reader.isEqual('/')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("/*** COMMAND NOT FOUND ***/")){
					done();
					return;
				}else{
					System.err.println("Skip : "+sb);
				}
			}else if(reader.isEqual('M')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("MT MOBILE")){
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
			if(reader.isEqual('H')){
				reader.readUntilEOL(sb).skipEOL();
				if(sb.indexOf("HLRADDR") >-1){
						header = headersMap.get("FIRST");
				}
				while(!reader.isEOL() && !reader.isEqual('<')){
					map = new LinkedHashMap<String, Object>();
					check = reader.isEqual('E');//yyn
					parse(map, reader, header, check);
					listener.onReadyData(ctx, map, reader.getLine());
					reader.skipEOL();
				}
			}else{
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("END")){//yyn	
					listener.onReadyData(ctx, sum, line);
					done();
					return;
				}else 
				if(sb.toString().trim().equals("TOTNSUB"))	{
					reader.skipEOL().readUntilEOL(sb);
					sum.put("TOTNSUB", sb.toString());
				}else
				if(sb.toString().trim().equals("TOTNSUBA"))	{
					reader.skipEOL().readUntilEOL(sb);
					sum.put("TOTNSUBA", sb.toString());
				}else
				if(sb.toString().startsWith("NSUBPR"))	{
					reader.skipEOL().readUntilEOL(sb);
					sum.put("NSUBPR", sb.toString());
				}else
				if(sb.toString().startsWith("NSUBXP"))	{
					reader.skipEOL().readUntilEOL(sb);
					sum.put("NSUBXP", sb.toString());
					line=reader.getLine();
				}else
				if(sb.toString().startsWith("NSUBPXOU")){
					reader.skipEOL().readUntilEOL(sb);
					sum.put("NSUBPXOU", sb.toString());
					line=reader.getLine();
				}else	
				if(sb.toString().startsWith("NSUBGS"))	{
					reader.skipEOL().readUntilEOL(sb);
					sum.put("NSUBGS", sb.toString());
					line=reader.getLine();
				}else
				if(sb.toString().startsWith("NSUBSGS")){
					reader.skipEOL().readUntilEOL(sb);
					sum.put("NSUBSGS", sb.toString());
					line=reader.getLine();
					//listener.onReadyData(ctx, sum, reader.getLine());
				}else
				System.err.println("Skipx : "+sb);
			}
			reader.skipEOLs();
		}
	}
	
	protected void parse(Map<String, Object> map, Parser reader, ConfiguredHeader[] header, boolean check) throws IOException{
		StringBuilder sb = new StringBuilder();
		final int lastIdx = header.length-1;
		for (int i = 0; !isDone() && i <= lastIdx; i++) {
			ConfiguredHeader configuredHeader = header[i];
			if(lastIdx==i)
				reader.readUntilEOL(sb);
			else{
				reader.read(sb, configuredHeader.getLength());
				if(check && i==0){
					if(sb.toString().startsWith("END")){ 
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
