package id.co.telkom.parser.entity.traversa.ericsson.msscommand;


import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.common.charparser.AbstractCommandHandler;
import id.co.telkom.parser.common.charparser.MscCommandHandler;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.ConfiguredHeader;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;


public class InitiatorC7SPPCommandHandler extends AbstractCommandHandler implements MscCommandHandler {
	private final Parser reader;
	private final ConfiguredHeader[] header;
	private GlobalBuffer buf;
	private Context ctx;
	
	public InitiatorC7SPPCommandHandler(Parser reader, 
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
	public void handle(Context ctx)
			throws IOException {
		boolean isStartExecution = false;
		StringBuilder sb = new StringBuilder();
		while(!isStartExecution && !reader.isEOF() && !reader.isEqual('<')){
			if(reader.isEqual('/')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("/*** COMMAND NOT FOUND ***/")){
					done();
					return;
				}else{
					System.err.println("Skip : "+sb);
				}
			}else if(reader.isEqual('C')){
				reader.readUntilEOL(sb);
				if(sb.toString().startsWith("CCITT7 SIGNALLING POINT DATA")){
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
				System.err.println("Skip : "+sb);
			}
			reader.skipEOLs();
		}
		if(!isStartExecution){
			done();
			return;
		}
		
		if(isStartExecution){
			reader.skipLines(1).skipEOL();
			while(reader.isNumber() && !reader.isEOF() && !reader.isEqual('<')){
				Parse();
				reader.skipEOL();
			}
		}
		done();
		return;
	}

	private void Parse() throws IOException{
		StringBuilder sb = new StringBuilder();
		Map<String, String> map = new LinkedHashMap<String, String>();
		final int lastIdx = header.length-1;
		for (int i = 0; !isDone() && i <= lastIdx; i++) {
			ConfiguredHeader configuredHeader = header[i];
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
			}
		}
		//hooray, we get OwnSP of this NE
		if(map.get("SP")!=null){
			if(map.get("OWNSP").equals("OWNSP")){
				String sp = map.get("SP");
				sp=sp.indexOf("-")>-1?sp.substring(sp.indexOf("-")+1):sp;
				System.out.println("Set OWN SP ["+sp+"] to ["+ctx.ne_id+"]");
				buf.setSPToVertex(ctx.ne_id, convertInt(sp));
			}
//			else{
////				TODO: Tobe checked : ga valid..
//				String sp = map.get("SP");
//				sp=sp.indexOf("-")>-1?sp.substring(sp.indexOf("-")+1):sp;
//				Object ne=map.get("SPID");
//				Integer s = convertInt(sp);
//				if(ne!=null && ((ne.toString().startsWith("B")||ne.toString().startsWith("R"))) && s!=0){
//					buf.setVendorToVertex(ne.toString(), "BSC/RNC");
//					buf.setNEToVertex(ne.toString());
//					buf.setSPToVertex(ne.toString(), convertInt(sp));
//				}
//			}
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
