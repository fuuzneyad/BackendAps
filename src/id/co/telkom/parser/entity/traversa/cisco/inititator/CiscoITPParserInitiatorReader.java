package id.co.telkom.parser.entity.traversa.cisco.inititator;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.Map;

import id.co.telkom.parser.common.charparser.Parser;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;

public class CiscoITPParserInitiatorReader extends Parser {
	private GlobalBuffer buf;
	private Context ctx;
	
	public CiscoITPParserInitiatorReader(Reader reader, Context ctx, GlobalBuffer buf) {
		super(reader);
		this.buf=buf;
		this.ctx=ctx;
	}

	public void Parse() throws IOException{
		StringBuilder sb = new StringBuilder();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Map<String, Object> headerGtt = new LinkedHashMap<String, Object>();
		Map<String, Object> headerGta = new LinkedHashMap<String, Object>();
		String Instance=null;
		boolean isPC=false;
		boolean isGTT=false;
		read();
		while (!isEOF()) {
			readUntilEOL(sb);
			String line=sb.toString().trim();
			if(line.contains(">") && line.contains("show cs7 point-code")){
				isPC=true;
			}else if(line.contains(">") && line.contains("show cs7 gtt config")){
				if(!map.isEmpty()){
					//ready
//					System.out.println(">>"+map);
					Object pc1 = map.get("PRIMARY_PC");
					Object pc2 = map.get("SECONDARY_PC");
					if(pc1!=null){
						buf.setSPToVertex(ctx.ne_id, convertToInt(pc1.toString()));
					}if(pc2!=null){
						buf.setSPToVertex(ctx.ne_id, convertToInt(pc2.toString()));
					}
					buf.setGTToVertex(ctx.ne_id, "-");
					map = new LinkedHashMap<String, Object>();
				}
				isGTT=true;
				isPC=false;
			}else{
				if(isPC && line.startsWith("Instance Number")){
					Instance=line.replace("Instance Number","").trim();
					if(!map.isEmpty()){
						//ready
//						System.out.println(map);
						Object pc1 = map.get("PRIMARY_PC");
						Object pc2 = map.get("SECONDARY_PC");
						if(pc1!=null)
							buf.setSPToVertex(ctx.ne_id, convertToInt(pc1.toString()));
						if(pc2!=null)
							buf.setSPToVertex(ctx.ne_id, convertToInt(pc2.toString()));
						map = new LinkedHashMap<String, Object>();
					}
				}else
				if(isPC && line.contains("CS7 Point Code      Type")){
//					System.out.println(line);
					skipLines(1).skipEOL();
					while(isNumber()){
						readUntilEOL(sb);
						String k =sb.toString().trim();
						String[] l = k.split("\\s+");
						if(k.contains("active")){
//							System.out.println(k);
							if(l.length==4){
									if(l[1].equalsIgnoreCase("local")){
										map.put("PRIMARY_PC", l[0]);
									}else
									if(l[1].equalsIgnoreCase("secondary")){
										map.put("SECONDARY_PC", l[0]);
									}
									map.put("INSTANCE", Instance);
							}
						}
						
						skipEOLs();
					}
				}else if(isGTT){
					if(line.startsWith("cs7 ")&& line.contains("selector")){//gtt gta
						String[] ss=line.trim().split("\\s+");
						for(int i=0;i<ss.length;i++){
							if(ss[i].startsWith("instance") && ss.length>i+1){
								headerGta.put("INSTANCE", ss[i+1]);
							}else
							if(ss[i].startsWith("selector")&& ss.length>i+1){
								headerGta.put("SELECTOR_NAME", ss[i+1]);
							}else
							if(ss[i].startsWith("tt")&& ss.length>i+1){
								headerGta.put("TT", ss[i+1]);
							}else
							if(ss[i].startsWith("tt")&& ss.length>i+1){
									headerGta.put("TT", ss[i+1]);
							}else
							if(ss[i].startsWith("gti")&& ss.length>i+1){
									headerGta.put("GTI", ss[i+1]);
							}else
							if(ss[i].startsWith("np")&& ss.length>i+1){
									headerGta.put("NP", ss[i+1]);
							}else
							if(ss[i].startsWith("nai")&& ss.length>i+1){
									headerGta.put("NAI", ss[i+1]);
							}
							
						}
						//System.out.println(sb);
						//System.out.println(headerGta);
						//anaknya
					}else
					if(line.startsWith("cs7 ") && line.contains("gtt application-group")){//gtt_appgroup
						String[] ss = line.split("\\s+");
						for(int i=0;i<ss.length;i++){
							if(ss[i].startsWith("instance"))
									headerGtt.put("INSTANCE",ss[i+1]);else
							if(ss[i].startsWith("application-group"))
									headerGtt.put("GROUP_NAME",ss[i+1]);
						}
					}	
				}
			}
			skipEOLs();
		}
		
	}
	
	private static Integer convertToInt(String s){
		 try{
			 return Integer.parseInt(s);
		 }catch(NumberFormatException e){
			 return 0;
		 }
	}
}
