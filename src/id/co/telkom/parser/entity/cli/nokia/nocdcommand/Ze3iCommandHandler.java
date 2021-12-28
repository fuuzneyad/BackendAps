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


public class Ze3iCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private String commandParams, marker;
	@SuppressWarnings("unused")
	private final ConfiguredHeader[] headers;
	private final String TABLENAME;
	private final DataListener listener;
	private boolean isStartExecution;
	
	public Ze3iCommandHandler(Parser reader, DataListener listener, String command, String params, ConfiguredHeader[] headers, String tableName) {
		super(command, params);
		this.TABLENAME=tableName;
		this.reader = reader;
		this.listener = listener;
		this.headers = headers;
		this.commandParams=params;
	}
	
	@Override
	public void handle(Context ctx)
			throws IOException {
		ctx.setTableName(TABLENAME);
		StringBuilder sb = new StringBuilder();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
			
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
				if (sb.toString().startsWith("VLR ADDRESS"))
					marker="V";else
				if (sb.toString().startsWith("MSS ADDRESS"))
					marker="M";
				
				if (sb.indexOf(". ")>-1)
				{
				String Get = sb.toString().substring(sb.indexOf(". ")+1).trim();
				String[] Hasil=Pisah(Get);
					//	System.out.println ("Hasil:"+Hasil[0]+", Val:"+Hasil[1]);
					if (Hasil[0]!=null && Hasil[0].equals("POOLNAME") && map.get("NRILIST_VAL")!=null)
						{
						if (commandParams.contains("TYPE=OWN"))
							map.put("PARAM_TYPE", "OWN"); else
						if (commandParams.contains("TYPE=PAR"))		
							map.put("PARAM_TYPE", "PAR");
						listener.onReadyData(ctx, map, reader.getLine());
						map = new LinkedHashMap<String, Object>();
						map.put(Hasil[0], Hasil[1]);						
						}
//						else if (Hasil[0]!=null && Hasil[0].equals("OFFLAC")){							
//							if (reader.isEOL()){
//								reader.skipEOL();
//								if (reader.isEqual(' ')){
//									reader.readUntilEOL(sb).skipEOL();
//									map.put(Hasil[0], Hasil[1]+sb.toString().trim());
//								}else
//									map.put(Hasil[0], Hasil[1]);	
//							}
//						}
						else
						if (Hasil[0]!=null){
							if(Hasil[0].equals("NP") || Hasil[0].equals("TON") && marker!=null)
								Hasil[0]+="_"+marker;
							
							if (reader.isEOL()){
								reader.skipEOL();
								if (reader.isEqual(' ')){
									reader.readUntilEOL(sb).skipEOL();
									Hasil[1]+=sb.toString().trim();
								}
							}
							map.put(Hasil[0], Hasil[1]);
						}

				}
				else if (sb.toString().startsWith("NRI LIST:")){	
					reader.skipLines(1);
					reader.skipEOL().readUntilEOL(sb).skipEOL();
					map.put("NRILIST_VAL", sb.toString().trim());					
				}

			}			
			else if (sb.toString().trim().startsWith("COMMAND EXECUTED")){	
				if (commandParams.contains("TYPE=OWN"))
					map.put("PARAM_TYPE", "OWN"); else
				if (commandParams.contains("TYPE=PAR"))		
					map.put("PARAM_TYPE", "PAR");
				listener.onReadyData(ctx, map, reader.getLine());
				done();
				return;
			}			
			else{				
//				System.err.println("Skip : "+sb);
			}
			reader.skipEOLs();
		}
		if (!isStartExecution){
			done();
			return;
		}
	}
	private String[] Pisah(String word){
		String[] hasil={null, null};
			hasil[0]=word.indexOf(" .")>-1 ? word.substring(0,word.indexOf(" .")).trim() : null;
			hasil[1]=word.substring(word.indexOf(":")+1).trim();
			hasil[1]=hasil[1].indexOf(" ") >-1 ? hasil[1].substring(0,hasil[1].indexOf(" ")).trim() : hasil[1];	
		return hasil;
	}
}
