package id.co.telkom.parser.entity.traversa.propreader;

import java.util.Properties;

public class TraversaInitialMappingPropReader {
	public final String READING_MODE_RAW="R";
	public final String READING_MODE_XML="X";
	public final String READING_MODE_CONVERT_RAW2CONF="R2X";
	
	private String 
		READING_MODE,
		INITIAL_RAW_E_MSC,
		INITIAL_RAW_E_MGW,
		INITIAL_RAW_N_MSC,
		INITIAL_RAW_N_MGW,
		INITIAL_RAW_S_STP,
		INITIAL_RAW_S_RG,
		INITIAL_RAW_C_ITP,
		INITIAL_RAW_H_HLR,
		INITIAL_CONFIG_FILE;
	
	public TraversaInitialMappingPropReader(Properties prop){
		System.out.println("Reading Traversa Initial Mapping Configuration... ");
		this.READING_MODE= prop.getProperty("READING_MODE");
		this.INITIAL_RAW_E_MSC= prop.getProperty("INITIAL_RAW_E_MSC");
		this.INITIAL_RAW_E_MGW= prop.getProperty("INITIAL_RAW_E_MGW");
		this.INITIAL_RAW_N_MSC= prop.getProperty("INITIAL_RAW_N_MSC");
		this.INITIAL_RAW_N_MGW= prop.getProperty("INITIAL_RAW_N_MGW");
		this.INITIAL_RAW_S_STP= prop.getProperty("INITIAL_RAW_S_STP");
		this.INITIAL_RAW_S_RG= prop.getProperty("INITIAL_RAW_S_RG");
		this.INITIAL_RAW_C_ITP= prop.getProperty("INITIAL_RAW_C_ITP");
		this.INITIAL_RAW_H_HLR= prop.getProperty("INITIAL_RAW_H_HLR");
		this.INITIAL_CONFIG_FILE= prop.getProperty("INITIAL_CONFIG_FILE");
	}

	public String getINITIAL_CONFIG_FILE() {
		return INITIAL_CONFIG_FILE.trim();
	}

	public String getREADING_MODE() {
		return READING_MODE.trim();
	}

	public String getINITIAL_RAW_E_MSC() {
		return INITIAL_RAW_E_MSC;
	}

	public String getINITIAL_RAW_E_MGW() {
		return INITIAL_RAW_E_MGW;
	}

	public String getINITIAL_RAW_N_MSC() {
		return INITIAL_RAW_N_MSC;
	}

	public String getINITIAL_RAW_N_MGW() {
		return INITIAL_RAW_N_MGW;
	}

	public String getINITIAL_RAW_S_STP() {
		return INITIAL_RAW_S_STP;
	}

	public String getINITIAL_RAW_S_RG() {
		return INITIAL_RAW_S_RG;
	}

	public String getINITIAL_RAW_C_ITP() {
		return INITIAL_RAW_C_ITP;
	}

	public String getINITIAL_RAW_H_HLR() {
		return INITIAL_RAW_H_HLR;
	}
	
}
