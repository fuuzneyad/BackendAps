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
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;


public class ParserZE2ICommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final DataListener listener;
	ConfiguredHeader[] headers;
	private final String T_NAME;
	private static final Logger logger = Logger.getLogger(ParserZE2ICommandHandler.class);
	private Map<String, Object> map = new LinkedHashMap<String, Object>();
	@SuppressWarnings("unused")
	private final GlobalBuffer gb;
	@SuppressWarnings("unused")
	private String Param;
	private boolean isStartExecution;
	
	public ParserZE2ICommandHandler(
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
		this.Param=params;
	}
	@Override
	public void handle(Context ctx)
			throws IOException {
		ctx.setTableName(T_NAME);
		StringBuilder sb = new StringBuilder();
		while (!isStartExecution && !reader.isEOL() && !reader.isEqual('<')){
			reader.skipEOLs().readUntilEOL(sb);
			
			if (sb.toString().startsWith("LOADING PROGRAM")) {
				isStartExecution = true;
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
			} else{
				System.err.println("Skip : "+sb);
				logger.error("Skip : "+sb);
			}
			reader.skipEOLs();
		}
		while (isStartExecution && !reader.isEOL() && !reader.isEqual('<')) {
			reader.readUntilEOL(sb);
			 if(sb.indexOf(":")>-1){
				if (sb.indexOf(". ")>-1)
				{
				String Get = sb.toString().substring(sb.indexOf(". ")+1).trim();
				String[] Hasil=SplitIt(Get);
					if (Hasil[0]!=null)
						map.put(Hasil[0], Hasil[1]);
				}

			}else if (sb.toString().trim().startsWith("COMMAND EXECUTED")){	
				if (map!=null && map.get(headers[0].getName())!=null)
					listener.onReadyData(ctx, map, reader.getLine());
				done();
				return;
			}
			if (sb.toString().trim().startsWith("AMR CODEC CAPABILITY AND MODE SETS")){	
				if (map!=null && map.get(headers[0].getName())!=null)
					listener.onReadyData(ctx, map, reader.getLine());
				map=new LinkedHashMap<String, Object>();
				
			}	
			
			reader.skipEOLs();
		}
		done();
		return;
	}
	
	private String[] SplitIt(String word){
		String[] hasil={"test", "test2"};
			hasil[0]=word.indexOf(" .")>-1 ? word.substring(0,word.indexOf(" .")).trim() : null;
			hasil[1]=word.substring(word.indexOf(":")+1).trim();
		return hasil;
	}
}
