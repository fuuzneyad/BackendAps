package id.co.telkom.parser.entity.traversa.huawei.command;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.common.charparser.AbstractCommandHandler;
import id.co.telkom.parser.common.charparser.CommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;

public class InitiatorOpcGroupCommandHandler extends AbstractCommandHandler implements CommandHandler{
	private Context ctx;
	private GlobalBuffer buf;
	private ConfiguredHeader[] headers;
	private Parser reader;
	private String[] TheParam={"Local office name", "OPC index", "International network code", "International reserved network code",
			"National network code", "National reserved network code", "Assistant node 1", "Assistant node 2", "Assistant node 3",
			"Assistant node 4", "Assistant node 5", "Assistant node 6", "Assistant node 7",
			"Assistant node 8"};
	
	public InitiatorOpcGroupCommandHandler(Parser reader, String command,
			String params, Context ctx, GlobalBuffer buf, ConfiguredHeader[] headers) {
		super(command, params);
		this.headers=headers;
		this.reader=reader;
		this.buf=buf;
		this.ctx=ctx;
	}

	@Override
	public void handle(Context ctx) throws IOException {
		StringBuilder sb = new StringBuilder();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		while (!reader.isEOL() && !reader.isEqual('(')){				
			reader.readUntilEOL(sb);
			String line =sb.toString().trim();
			parse(line, map, headers);
			reader.skipEOLs();
		}
		
		if(reader.isEqual('(')){
			reader.readUntilEOL(sb);
			if(sb.toString().trim().startsWith("(")){
				Object o = map.get("NAT_NET_CODE");
				if(o!=null){
					System.out.println("Adding SP: "+toInt(o.toString())+" to ["+ctx.ne_id+"]");
					buf.setSPToVertex(ctx.ne_id, toInt(o.toString()));
				}
				map= new LinkedHashMap<String, Object>();
				done();
				return;
			}else{
				reader.readUntilEOL(sb);
				System.err.println("Skip : "+sb);
			}		
		}
	}
	
	private void parse(String line, Map<String, Object> map, ConfiguredHeader[] headers){
		int j = line.indexOf('=');
		String param = j == -1 ? null : line.substring(0, line.indexOf('=')).trim();
		String value = j == -1 ? null : line.substring(line.indexOf('=')+1).trim();
		int how;		
		if (param!=null)	
		how=isValidHeader(param, TheParam);else
		how=-1;	
		
		if (param!=null && how!=-1)
			if ((how==2 || how==3 || how==4 || how==5))
				map.put(headers[how].getName(), convertToDEc(value));else
				map.put(headers[how].getName(), value);
		
		if (param!=null && value!=null && how==0 ){//NE
			ctx.setNe_id(value.replace("-", ""));
			buf.setVendorToVertex(ctx.ne_id, ctx.vendor);
			buf.setNEToVertex(ctx.ne_id);
		}
	}
	
	private int isValidHeader(String ln, String[] TheParam){
		for (int i=0; i<TheParam.length; i++){
			if (ln.equals(TheParam[i]))
				return i;
		}
		return -1;
	}
	
	private int convertToDEc(String Str){
		return Integer.parseInt(Str.substring(2).trim(),16);
	}
	
	private int toInt(String s){
		try{return Integer.parseInt(s);}catch (NumberFormatException e){return 0;}
	}

}
