package id.co.telkom.parser.entity.traversa.cisco.inititator;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;
import id.co.telkom.parser.entity.traversa.propreader.TraversaInitialMappingPropReader;

public class CiscoITPInitiator {
	private static final Logger logger = Logger.getLogger(CiscoITPInitiator.class);
	private static final String vendor="C_ITP";
	
	public static void ReadRaw(TraversaInitialMappingPropReader prop,GlobalBuffer buf){
		String state ="\nRead mapping form raw for Cisco ITP from ["+prop.getINITIAL_RAW_C_ITP()+"]";
		System.out.println(state);
		logger.info(state);
		
		File fl = new File(prop.getINITIAL_RAW_C_ITP());
		if(fl.isDirectory()){
			for (File f:fl.listFiles()){
				if(f.isFile()){
					state="Reading Mapping of raw file ["+f.getName()+"]...";
					System.out.println(state);
					logger.info(state);
					
					Context ctx = new Context();
						ctx.setVendor(vendor);
					
					//get ne and set it to context and buffer
						final String[] NE=f.getName().toUpperCase().replace("CISCO-", "").split("-");
						if(NE.length>=2){
							final String ne = (NE[0]+NE[1]);
							ctx.setNe_id(ne);
							buf.setNEToVertex(ne);
						}
						
						buf.setVendorToVertex(ctx.ne_id, vendor);
						
					try {
						InputStreamReader reader = new InputStreamReader(new FileInputStream(f));
						CiscoITPParserInitiatorReader parser = new CiscoITPParserInitiatorReader(reader, ctx, buf);
						parser.Parse();
						reader.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
