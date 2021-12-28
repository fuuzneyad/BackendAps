package id.co.telkom.parser.entity.traversa;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
//import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import id.co.telkom.parser.AbstractInitiator;
import id.co.telkom.parser.common.propreader.OutputMethodPropReader;
import id.co.telkom.parser.common.propreader.ParserPropReader;
import id.co.telkom.parser.entity.traversa.cisco.inititator.CiscoITPInitiator;
import id.co.telkom.parser.entity.traversa.ericsson.initiator.EricssonMGWInitiator;
import id.co.telkom.parser.entity.traversa.ericsson.initiator.EricssonMSCInitiator;
import id.co.telkom.parser.entity.traversa.huawei.initiator.HuaweiHLRInitiator;
import id.co.telkom.parser.entity.traversa.model.GlobalBuffer;
import id.co.telkom.parser.entity.traversa.nokia.initiator.NokiaMGWInitiator;
import id.co.telkom.parser.entity.traversa.nokia.initiator.NokiaMSCInitiator;
import id.co.telkom.parser.entity.traversa.propreader.TraversaInitialMappingPropReader;
import id.co.telkom.parser.entity.traversa.siemens.initiator.SiemensSTPInitiator;

public class TraversaCommonInitiator extends AbstractInitiator{
	private ParserPropReader cynapseProp;
	private static final Logger logger = Logger.getLogger(TraversaCommonInitiator.class);
	
	public TraversaCommonInitiator(
			ParserPropReader cynapseProp,
			OutputMethodPropReader om) {
		super(cynapseProp, om);
		this.cynapseProp=cynapseProp;
		this.mappingModel=ReadMappingModel();
	}

	@Override
	public Object ReadMappingModel() {
		System.out.println("Reading Traversa Mapping Config..");
		logger.info("Reading Traversa Mapping Config..");
		
		Properties prop = new Properties();
		final GlobalBuffer buf = new GlobalBuffer();
		try {
			prop.load(new FileInputStream(cynapseProp.getMAPPING_CONFIG()));
			TraversaInitialMappingPropReader traversaProp = new TraversaInitialMappingPropReader(prop);
			//reading raw mode
			if(traversaProp.getREADING_MODE().equals(traversaProp.READING_MODE_RAW)||
				traversaProp.getREADING_MODE().equals(traversaProp.READING_MODE_CONVERT_RAW2CONF)){
				
//				//Read each initial data to buffer
				buf.empty();
				EricssonMSCInitiator.ReadRaw(traversaProp, buf);//e_msc
				EricssonMGWInitiator.ReadRaw(traversaProp, buf);//e_mgw
				NokiaMSCInitiator.ReadRaw(traversaProp, buf);//n_msc
				NokiaMGWInitiator.ReadRaw(traversaProp, buf);//n_mgw
				CiscoITPInitiator.ReadRaw(traversaProp, buf);//c_itp
				SiemensSTPInitiator.ReadRaw(traversaProp, buf);//s_stp
				HuaweiHLRInitiator.ReadRaw(traversaProp, buf);//h_hlr
				//Reformat to other form
				buf.transform();
					
			}else if(traversaProp.getREADING_MODE().equals(traversaProp.READING_MODE_XML)){
				//reading xml mode
				buf.empty();
				TraversaXmlInitiatorReader.readXML(buf, traversaProp);
				//Reformat to other form
				buf.transform();
			}
			//convert raw to config
			if(traversaProp.getREADING_MODE().equals(traversaProp.READING_MODE_CONVERT_RAW2CONF)){
				//write toXML
				TraversaXmlInitiatorReader.writeXML(buf,traversaProp);
				System.out.println("Exiting..");
				System.exit(1);
			}
			
			//test
//			System.out.println("NEOfVertex");
//			for(Map.Entry<String,Vertex> lcl : buf.getNeOfVertex().entrySet()){
//				System.out.println(">>>>"+lcl.getKey()+"---"+lcl.getValue().toString());
//			}
//			System.out.println("OwnSPOfVertex");
//			for(Map.Entry<Integer,Vertex> lcl : buf.getOwnSpOfVertex().entrySet()){
//				System.out.println(">>>>"+lcl.getKey()+"---"+lcl.getValue().toString());
//			}
//			System.out.println("OwnGTOfVertex");
//			for(Map.Entry<String,Vertex> lcl : buf.getOwnGTOfVertex().entrySet()){
//				System.out.println(">>>>"+lcl.getKey()+"---"+lcl.getValue().toString());
//			}
//			System.out.println("OwnMsrnOfVertex");
//			for(Map.Entry<String,Vertex> lcl : buf.getOwnMSRNOfVertex().entrySet()){
//				System.out.println(">>>>"+lcl.getKey()+"---"+lcl.getValue().toString());
//			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return buf;
	}

}
