package id.co.telkom.parser.entity.traversa.huawei.command;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.common.charparser.AbstractCommandHandler;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;

public class ParserLocalinfoCommandHandler extends AbstractCommandHandler implements CommandHandler{
	private ConfiguredHeader[] headers;
	private Parser reader;
	private String[] TheParam={"Local office name", "International network valid", "International reserved network valid", "National network valid",
			"National reserved network valid", "First search network", "Second search network", "Third search network", "Fourth search network",
			"International network structure", "International reserved network structure", "National network structure", "National reserved network structure",
			"STP function", "Restart function", "Local GT"};
	Map<String, Object> map = new LinkedHashMap<String, Object>();//orig
	private  DataListener listener;
	public ParserLocalinfoCommandHandler(Parser reader, String command,
			String params, Context ctx, ConfiguredHeader[] headers, DataListener listener) {
		super(command, params);
		this.headers=headers;
		this.reader=reader;
		this.listener=listener;
	}

	@Override
	public void handle(Context ctx) throws IOException {
		ctx.setTableName(getCommand());
		StringBuilder sb = new StringBuilder();
		
		while (!reader.isEOL() && !reader.isEqual('(')){				
				reader.readUntilEOL(sb);
				String line =sb.toString().trim();
				parse(line, map, headers, ctx);
				reader.skipEOLs();
		}

		if(reader.isEqual('(')){
			reader.readUntilEOL(sb);
			if(sb.toString().trim().startsWith("(")){		
				listener.onBeginTable(reader.getLine(), ctx);
				listener.onReadyData(ctx, map, reader.getLine());
				done();
				return;
			}else{
				reader.readUntilEOL(sb);
				System.err.println("Skip : "+sb);
			}		
		}
		
	}
	
	private void parse(String line, Map<String, Object> map, ConfiguredHeader[] headers, Context context){

		int j = line.indexOf('=');
		String param = j == -1 ? null : line.substring(0, line.indexOf('=')).trim();
		String value = j == -1 ? null : line.substring(line.indexOf('=')+1).trim();
		
		if (param!=null && isValidHeader(param, TheParam)!=-1 )
			map.put(headers[isValidHeader(param, TheParam)].getName(), value);
		
		if (param!=null && value!=null && isValidHeader(param, TheParam)==0 ){//NE
			context.setNe_id(value.replace("-", ""));
		}
	}
	
	private int isValidHeader(String ln, String[] TheParam){
		for (int i=0; i<TheParam.length; i++){
			if (ln.equals(TheParam[i]))
				return i;
		}
		return -1;
	}
	
}
