package id.co.telkom.parser.common.propreader;

import java.util.Properties;

public class FTPPropReader {
	private String MODUL_NAME
					;
	private String GET_FTP,
				   FTP_HOST,
				   FTP_PORT,
				   FTP_USERNAME,
				   FTP_PASSWD,
				   FTP_FILEPATTERN,
				   FTP_DATEPATTERN,
				   FTP_REMOTE_DIR,
				   FTP_SUBREMOTE_DIR,
				   FTP_LOCAL_DIR,
				   FTP_CHECK_ALREADY_DWL,
				   OUTPUT_CONFIG,
				   PARSER_CONFIG,
				   MAX_THREAD;
	
	public FTPPropReader(Properties prop, int oss){
		
		this.PARSER_CONFIG=prop.getProperty("PARSER_CONFIG");
		this.MAX_THREAD=prop.getProperty("MAX_THREAD");
		
		this.MODUL_NAME= prop.getProperty("MODUL_NAME_"+oss) ;
		
		this.GET_FTP=prop.getProperty("GET_FTP_"+oss) ;
		this.FTP_HOST=prop.getProperty("FTP_HOST_"+oss) ;
		this.FTP_PORT=prop.getProperty("FTP_PORT_"+oss) ;
		this.FTP_USERNAME=prop.getProperty("FTP_USERNAME_"+oss) ;
		this.FTP_PASSWD=prop.getProperty("FTP_PASSWD_"+oss) ;
		this.FTP_FILEPATTERN=prop.getProperty("FTP_FILEPATTERN_"+oss) ;
		this.FTP_DATEPATTERN=prop.getProperty("FTP_DATEPATTERN_"+oss) ;
		this.FTP_REMOTE_DIR=prop.getProperty("FTP_REMOTE_DIR_"+oss) ;
		this.FTP_SUBREMOTE_DIR=prop.getProperty("FTP_SUBREMOTE_DIR_"+oss) ;
		this.FTP_LOCAL_DIR=prop.getProperty("FTP_LOCAL_DIR_"+oss) ;
		this.FTP_CHECK_ALREADY_DWL=prop.getProperty("FTP_CHECK_ALREADY_DWL_"+oss) ;
		this.OUTPUT_CONFIG=prop.getProperty("OUTPUT_CONFIG_"+oss) ;

	}
	
	public boolean isValid(){
		boolean check =
				MODUL_NAME  !=null &&
				GET_FTP !=null &&
				FTP_HOST !=null &&
				FTP_PORT !=null &&
				FTP_USERNAME !=null &&
				FTP_PASSWD !=null &&
				FTP_FILEPATTERN !=null &&
				FTP_DATEPATTERN !=null &&
				FTP_REMOTE_DIR !=null &&
				FTP_SUBREMOTE_DIR !=null &&
				FTP_LOCAL_DIR !=null &&
				FTP_CHECK_ALREADY_DWL !=null &&
				OUTPUT_CONFIG !=null 
				;
		if (check)
			return true;
		return false;
	}
	
	public int getMAX_THREAD(){
		try{
			return Integer.parseInt(MAX_THREAD);
		}catch(NumberFormatException e){
			return 1;
		}
	}

	public boolean isFTP_CHECK_ALREADY_DWL() {
		return FTP_CHECK_ALREADY_DWL.trim().equalsIgnoreCase("Y") || FTP_CHECK_ALREADY_DWL.trim().equalsIgnoreCase("YES");
	}
	
	public boolean isGET_FTP() 
	{
		if (GET_FTP==null)
			return false;
		return GET_FTP.trim().equalsIgnoreCase("Y") || GET_FTP.trim().equalsIgnoreCase("YES");
	}
	
	public String getOUTPUT_CONFIG() {
		return OUTPUT_CONFIG;
	}

	public String getPARSER_CONFIG() {
		return PARSER_CONFIG;
	}
	
	public String getMODUL_NAME() {
		return MODUL_NAME.trim();
	}


	public String getFTP_HOST() {
		return FTP_HOST;
	}

	public String getFTP_PORT() {
		return FTP_PORT;
	}
	
	public String getFTP_USERNAME() {
		return FTP_USERNAME;
	}

	public String getFTP_PASSWD() {
		return FTP_PASSWD;
	}

	public String getFTP_FILEPATTERN() {
		return FTP_FILEPATTERN;
	}

	public String getFTP_DATEPATTERN() {
		return FTP_DATEPATTERN;
	}

	public String getFTP_REMOTE_DIR() {
		return FTP_REMOTE_DIR;
	}

	public String getFTP_SUBREMOTE_DIR() {
		return FTP_SUBREMOTE_DIR;
	}

	public String getFTP_LOCAL_DIR() {
		return FTP_LOCAL_DIR;
	}

	@Override
	public String toString() {
		return "SiemensFTPPropReader [MODUL_NAME=" + MODUL_NAME + ", GET_FTP="
				+ GET_FTP + ", FTP_HOST=" + FTP_HOST + ", FTP_PORT=" + FTP_PORT
				+ ", FTP_USERNAME=" + FTP_USERNAME + ", FTP_PASSWD="
				+ FTP_PASSWD + ", FTP_FILEPATTERN=" + FTP_FILEPATTERN
				+ ", FTP_DATEPATTERN=" + FTP_DATEPATTERN + ", FTP_REMOTE_DIR="
				+ FTP_REMOTE_DIR + ", FTP_SUBREMOTE_DIR=" + FTP_SUBREMOTE_DIR
				+ ", FTP_LOCAL_DIR=" + FTP_LOCAL_DIR
				+ ", FTP_CHECK_ALREADY_DWL=" + FTP_CHECK_ALREADY_DWL + "]";
	}



}
