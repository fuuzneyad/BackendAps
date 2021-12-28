package id.co.telkom.parser.entity.traversa.siemens;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.common.model.DataListener;


public class SiemensTraversaRGParserReader extends Parser  {
	private final Context ctx;
	private Map<String, Object> map = new HashMap<String, Object>();
	private static final Logger logger = Logger.getLogger(SiemensTraversaRGParserReader.class);
	@SuppressWarnings("unused")
	private AbstractInitiator cynapseInit;
	private DataListener listener;
	
	
	public SiemensTraversaRGParserReader(Reader reader, DataListener listener, Context ctx, AbstractInitiator cynapseInit, String fileName) {
		super(reader);
		this.ctx=ctx;
		this.cynapseInit=cynapseInit;
		this.listener=listener;
		setNeID(fileName, ctx);
		logger.info("S_RG "+fileName);
	}
	
	private void setNeID(final String fileName, final Context ctx){
		for (String candidateNE : fileName.split("\\.")){
			if(candidateNE.contains("_")){
				ctx.setNe_id(candidateNE.split("_")[0]);
				break;
			}
		}
	}
	
	public void parse() throws IOException{
		StringBuilder sb = new StringBuilder();
		read();
		while (!isEOF()){
			readUntilEOL(sb);
			if(sb.toString().contains(":")){
				String table = sb.toString().split(":")[0].replace("<", "");
				String line = sb.toString().split(":")[1].replace(";", "");
				if(sb.toString().startsWith("<"))
					ctx.setTableName(table);
				map = new LinkedHashMap<String, Object>();
				for(String keyVal : line.split(",")){
					if(keyVal.contains("=")){
						String[] kv = keyVal.split("=");
						map.put(kv[0], kv[1].trim().replace("!", ""));
					}
				}
				listener.onReadyData(ctx, map, getLine());
			}
			skipEOL();
		}
	}
	
}
