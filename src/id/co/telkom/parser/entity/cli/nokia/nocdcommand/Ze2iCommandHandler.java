package id.co.telkom.parser.entity.cli.nokia.nocdcommand;


import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.common.charparser.AbstractCommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;


public class Ze2iCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	@SuppressWarnings("unused")
	private String commandParams;
	private final String TABLENAME;
	private final DataListener listener;
	private final ConfiguredHeader[] header;
	private boolean isStartExecution;
	
	public Ze2iCommandHandler(String tableName,Parser reader, DataListener listener, String command, String params,ConfiguredHeader[] header) {
		super(command, params);
		this.TABLENAME=tableName;
		this.reader = reader;
		this.listener = listener;
		this.header = header;
		this.commandParams=params;
	}
	
	@Override
	public void handle(Context ctx)
			throws IOException {
		ctx.setTableName(TABLENAME);
		StringBuilder sb = new StringBuilder();
		Map<String, Object> map = null;
		while (!isStartExecution && !reader.isEOL() && !reader.isEqual('<')){
			reader.skipEOLs().readUntilEOL(sb);
			
			if (sb.toString().startsWith("LOADING PROGRAM")) {				
				while(reader.isEOL() || reader.isEqual(' '))
					reader.read();
				reader.readUntil(' ', sb);
				if(sb.toString().equals("MSCi")){
					reader.skipWhile(' ').readUntil(' ', sb).skipUntilEOL();
					ctx.setNe_id(sb.toString());					
				}else{
					reader.readUntilEOL(sb);
					System.err.println("Skip : "+sb);
				}
			} else if(sb.toString().startsWith("/*** COMMAND NOT FOUND ***/")){
				listener.onError(reader.getLine(), ctx, reader.getColumn(), sb.toString());
				reader.skipEOLs();
				done();
				return;
			}else if(sb.toString().trim().equals("COMMAND EXECUTION FAILED")){
				listener.onError(reader.getLine(), ctx, reader.getColumn(), sb.toString());
				reader.skipEOLs();
				done();
				return;
			}else if(sb.indexOf(":")>-1){
				if (sb.indexOf(". ")>-1)
				{
				String Get = sb.toString().substring(sb.indexOf(". ")+1).trim();
//				System.out.println("Get->"+Get);
				String[] Hasil=Pisah(Get);
					if (Hasil[0]!=null){
						map.put(Hasil[0], Hasil[1]);
					}
				}

			}			
			else if (sb.toString().trim().startsWith("RNC IN OWN RADIO NETWORK")){	
				if (map!=null && map.get(header[0].getName())!=null)
					listener.onReadyData(ctx, map, reader.getLine());
				map=new LinkedHashMap<String, Object>();
				
			}	
			else if (sb.toString().trim().startsWith("COMMAND EXECUTED")){	
				if (map!=null && map.get(header[0].getName())!=null)
					listener.onReadyData(ctx, map, reader.getLine());
				done();
				return;
			}
			else if (sb.toString().trim().startsWith("===========================================")){
				
			}
			else{				
//				System.err.println("Skip : "+sb);
				if(sb.toString().trim().startsWith("UMTS2 "))
					map.put("AMR_CODEC", sb.toString().trim());
			}
			reader.skipEOLs();
		}
	}
	private String[] Pisah(String word){
		String[] hasil={"test", "test2"};
			hasil[0]=word.indexOf(" .")>-1 ? word.substring(0,word.indexOf(" .")).trim() : null;
			hasil[1]=word.substring(word.indexOf(":")+1).trim();
		return hasil;
	}

}
