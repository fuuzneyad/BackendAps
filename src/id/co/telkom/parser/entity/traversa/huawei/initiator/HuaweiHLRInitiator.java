package id.co.telkom.parser.entity.traversa.huawei.initiator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import id.co.telkom.parser.common.model.Context;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;
import id.co.telkom.parser.entity.traversa.propreader.TraversaInitialMappingPropReader;

public class HuaweiHLRInitiator {
	private static final Logger logger = Logger.getLogger(HuaweiHLRInitiator.class);
	private static final String vendor="H_HLR";
	
	public static void ReadRaw(TraversaInitialMappingPropReader prop, GlobalBuffer buf) {
		String state ="\nRead mapping form raw for Huawei HLR from ["+prop.getINITIAL_RAW_H_HLR()+"]";
		System.out.println(state);
		logger.info(state);
		
		File fl = new File(prop.getINITIAL_RAW_H_HLR());
		if(fl.isDirectory()){
			for (File f:fl.listFiles()){
				if(f.isFile()){
					state="Reading Mapping of raw file ["+f.getName()+"]...";
					System.out.println(state);
					logger.info(state);
					
					Context ctx = new Context();
						ctx.setVendor(vendor);
						
					try {
						InputStreamReader reader = new InputStreamReader(new FileInputStream(f));
						HuaweiHLRParserInitiatorReader parser = new HuaweiHLRParserInitiatorReader(reader, ctx, buf);
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
