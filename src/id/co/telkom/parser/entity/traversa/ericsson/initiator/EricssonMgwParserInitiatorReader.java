package id.co.telkom.parser.entity.traversa.ericsson.initiator;

import java.io.IOException;
import java.io.Reader;

import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;

public class EricssonMgwParserInitiatorReader extends Parser {
	private final Context ctx;
	private GlobalBuffer buf;
	
	public EricssonMgwParserInitiatorReader(Reader reader, Context ctx, GlobalBuffer buf) {
		super(reader);
		this.ctx=ctx;
		this.buf=buf;
	}
	
	public void parse() throws IOException{
		StringBuilder sb = new StringBuilder();
		read();
		while (!isEOF()) {
			//commandHandler not necessary
			readUntilEOL(sb);
			if(sb.toString().trim().startsWith("ntpServerAddress")){
				String ip = getVal(sb.toString().trim());
				if(!ip.equals("0.0.0.0"))
					buf.setIPToVertex(ctx.ne_id, ip);
			}else
			if(sb.toString().trim().startsWith("signallingPointCode")){
				Integer sp = toInt(getVal(sb.toString().trim()));
				if(sp!=0)
					buf.setSPToVertex(ctx.ne_id, sp);
			}
			skipEOLs();
		}
	}

	private String getVal(String v){
		String[] x = v.split("\\s+");
		if(x.length>1)
			return x[1];
		return x[0];
	}
	
	private Integer toInt(String s){
		try{
			return Integer.parseInt(s);
		}catch (NumberFormatException e) {
			return 0;
		}
	}
}
