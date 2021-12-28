package id.co.telkom.parser.entity.traversa.nokia.initiator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import id.co.telkom.parser.common.charparser.InputStreamWrapper;
import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;
import id.co.telkom.parser.entity.traversa.propreader.TraversaInitialMappingPropReader;

public class NokiaMSCInitiator {
	private static final Logger logger = Logger.getLogger(NokiaMSCInitiator.class);
	private static final String vendor="N_MSC";
	
	public static void ReadRaw(TraversaInitialMappingPropReader prop, GlobalBuffer buf) {
		String state ="\nRead mapping form raw for Nokia MSC from ["+prop.getINITIAL_RAW_N_MSC()+"]";
		System.out.println(state);
		logger.info(state);
		
		File fl = new File(prop.getINITIAL_RAW_N_MSC());
		if(fl.isDirectory()){
			for (File f:fl.listFiles()){
				if(f.isFile()){
					state="Reading Mapping of raw file ["+f.getName()+"]...";
					System.out.println(state);
					logger.info(state);
					
					Context ctx = new Context();
						ctx.setVendor(vendor);
					
					//get ne and set it to context and buffer
					final String ne = f.getName().indexOf("_")>-1?f.getName().split("_")[0]:f.getName();
						ctx.setNe_id(ne);
					
						buf.setNEToVertex(ne);
						buf.setVendorToVertex(ctx.ne_id, vendor);
						
					try {
						InputStreamReader reader = new InputStreamReader(new InputStreamWrapper(new FileInputStream(f)));
						NokiaMscParserInitiatorReader parser = new NokiaMscParserInitiatorReader(reader, ctx, buf);
						parser.parse();
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
