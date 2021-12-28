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

public class ParserMGCVPCommandHandler extends AbstractCommandHandler implements MscCommandHandler {

	private final Parser reader;
	private final DataListener listener;
	private final Map<String, ConfiguredHeader[]> headersMap;
	private final String T_NAME;
	@SuppressWarnings("unused")
	private final GlobalBuffer gb;
	private static final Logger logger = Logger.getLogger(ParserMGCVPCommandHandler.class);
	private Map<String, String> buffer = new LinkedHashMap<String, String>();
	
	private String NRIL, LAI;

	
	public ParserMGCVPCommandHandler(
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
				if(sb.toString().startsWith("MT COOPERATING VLR DATA")){
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
			if(reader.isEqual('L')){
				reader.readUntilEOL(sb).skipEOL();
				if (sb.toString().equals("LAI")){
					reader.skipEOL().readUntilEOL(sb);
				}
					
					if (sb.toString().trim().equals("NRIL"))
					{						
						done();
						return;
					}
					LAI=sb.toString().trim();
					map.put("LAI", sb.toString().trim());
					
			}else
			if(reader.isEqual('N')){
					reader.readUntilEOL(sb).skipEOL();
					System.out.println("SB N =" + sb);
					if (sb.toString().equals("NRIL"))
						reader.skipEOL().readUntilEOL(sb);
						NRIL=sb.toString().trim();
						
						
						map.put("NRIL", sb.toString().trim());
						
			}else
			if(reader.isEqual('V')){
				reader.readUntilEOL(sb).skipEOL();

				
				if(sb.toString().contains("MAPV     NRIV")){
					header = headersMap.get("FIRST");
					while(!reader.isEOL() && !reader.isEqual('<') && !reader.isEqual('L')){
						check = reader.isEqual('E');
						parse(map, reader, header, check);
						map.put("LAI", LAI);
						map.put("NRIL", NRIL);						
						if (map.get(header[0].getName())!=null){
							listener.onReadyData(ctx, map, reader.getLine());
							map = new LinkedHashMap<String, Object>();
						}
						reader.skipEOL();
					}
				}

			}

			else{
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("END"))//yyn
				{
					done(); 
					return;
				}else
					System.err.println("Skip : "+sb);
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
					if(sb.toString().startsWith("END")) {
						done();	
						return;
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
