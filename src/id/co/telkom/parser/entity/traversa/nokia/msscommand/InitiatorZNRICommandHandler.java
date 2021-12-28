package id.co.telkom.parser.entity.traversa.nokia.msscommand;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.common.charparser.AbstractCommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;

public class InitiatorZNRICommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final ConfiguredHeader[] header;
	private GlobalBuffer buf;
	private Context ctx;
	
	public InitiatorZNRICommandHandler(Parser reader, 
			String command, 
			String params, 
			GlobalBuffer buf,
			Context ctx,
			ConfiguredHeader[] header) {
		super(command, params);
		this.reader = reader;
		this.header=header;
		this.ctx=ctx;
		this.buf=buf;
	}

	@Override
	public void handle(Context ctx) throws IOException {
		try{
		StringBuilder sb = new StringBuilder();
		boolean isStartExecution = false;
		
		while (!isStartExecution && !reader.isEOF() && !reader.isEqual('<')){
			reader.skipEOLs().readUntilEOL(sb);
			if(sb.toString().startsWith("INTERROGATING")){
				isStartExecution =true;
			}else if(sb.toString().startsWith("/*** COMMAND NOT FOUND ***/")){
				reader.skipEOLs();
				done();
				return;
			} else{
				System.err.println("Skip : "+sb);
			}
			reader.skipEOLs();
		}

		if(!isStartExecution){
			done();
			return;
		}
		
		while(!reader.isEOF() && !reader.isEqual('<')){
			if(reader.isEqual('C'))
			{
				reader.readUntilEOL(sb);
				if(!sb.toString().equals("COMMAND EXECUTED")){
					System.err.println("Unexpected character in line "+reader.getLine()+" : "+sb);
				}
				done();
				return;
				
			}else if(reader.isEqual('N')){
				Parse(header, reader);
			}else{
				reader.readUntilEOL(sb);
			}
			
			reader.skipEOLs();
		}
		done();
		return;
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void Parse(ConfiguredHeader[] header, Parser reader) throws IOException{
		StringBuilder sb = new StringBuilder();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		int lastHeader = header.length-1;
		for (int i = 0; i < header.length && !reader.isEOL(); i++) {
			if(i==lastHeader)
				reader.readUntilEOL(sb);
			else
				reader.read(sb, header[i].getLength());
			map.put(header[i].getName(), sb.toString().trim());
		}
		//get it!
		Object s = map.get("SUBFIELD_INFO_BIT_LENGTHS");
		if(s!=null && s.toString().endsWith("OWN SP")){
			String sp = map.get("SP_CODE_H_D").toString();
				sp=sp.contains("/")?sp.substring(sp.indexOf("/")+1):sp;
				System.out.println("Set OWN SP ["+sp+"] to ["+ctx.ne_id+"]");
				buf.setSPToVertex(ctx.ne_id, convertInt(sp));
		}
		
	}
	private static Integer convertInt(String s){
		try{
			return Integer.parseInt(s);
		}catch (NumberFormatException e){
			return 0;
		}
	}
}
