package id.co.telkom.parser.entity.traversa.siemens.initiator;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;
import id.co.telkom.parser.entity.traversa.propreader.TraversaInitialMappingPropReader;

public class SiemensRGInitiator {
	private static final Logger logger = Logger.getLogger(SiemensRGInitiator.class);
	private static final String vendor="S_RG";
	
	public static void ReadRaw(TraversaInitialMappingPropReader prop,GlobalBuffer buf){
		String state ="\nRead mapping form raw for Siemens RG from ["+prop.getINITIAL_RAW_S_RG()+"]";
		System.out.println(state);
		logger.info(state);
		
		File fl = new File(prop.getINITIAL_RAW_S_RG());
		if(fl.isDirectory()){
			for (File f:fl.listFiles()){
				if(f.isFile()){
					state="Reading Mapping of raw file ["+f.getName()+"]...";
					System.out.println(state);
					logger.info(state);
					
					Context ctx = new Context();
						ctx.setVendor(vendor);
						//get ne and set it to context and buffer
						final String[] nes = f.getName().split(".");
						final String ne = nes.length>=3 ? nes[3].split("_")[0]:nes[0];
						ctx.setNe_id(ne);
						buf.setNEToVertex(ne);
						buf.setGTToVertex(ne, "-");
						buf.setVendorToVertex(ctx.ne_id, vendor);
					try {
						InputStreamReader reader = new InputStreamReader(new FileInputStream(f));

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
