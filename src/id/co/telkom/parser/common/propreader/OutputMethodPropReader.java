package id.co.telkom.parser.common.propreader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class OutputMethodPropReader {
	private Properties propOutput;
	private String METADATA_IS_ACTIVE, METADATA_METHOD,METADATA_FILELOC, METADATA_JDBC_SCHEMA, METADATA_JDBC_TABLE;
	private String JDBC_IS_ACTIVE,JDBC_LOAD, JDBC_METHOD, JDBC_URL;
	private String DELIMITED_IS_ACTIVE,DELIMITED_DELIMITER,DELIMITED_FILE_LOCATION, DELIMITED_PRINT_SCHEMA_HEADER_MODE,DELIMITED_HEADER_FILE_LOCATION;
	private String JSON_IS_ACTIVE, JSON_FILE_LOCATION,JSON_HEADER_FILE;
	private String RPC_IS_ACTIVE, RPC_HOST,RPC_PORT, RPC_PATH, RPC_SOURCE_ID;
	private String TIF_IS_ACTIVE,TIF_FILE_LOCATION;
	private String LOA_IS_ACTIVE, LOA_FILE_LOCATION, LOA_TYPE, LOA_FILE_PATTERN, LOA_SIZE, LOA_IS_WITH_HEADER;
	private boolean isLoaWithHeader=false;
	private boolean isLoaMysql10=false;
	private boolean isLoaOra10=false;
	private boolean isLoaMysql11=false;
	private String CSV_FILE_LOCATION, CSV_FILE_PATTERN;
	private boolean isCsvActive,isJsonActive=false;
	
	public OutputMethodPropReader (Properties fileProp){
		this.propOutput = fileProp;
	    ReadProperties();
	}
	
	public OutputMethodPropReader (String fileLoc){
		Properties prop = new Properties();
    	try {
			prop.load(new FileInputStream(fileLoc));
			this.propOutput = prop;
		    ReadProperties();
    	} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
	
	private void ReadProperties(){
		METADATA_IS_ACTIVE=propOutput.getProperty("METADATA_IS_ACTIVE");
		METADATA_METHOD=propOutput.getProperty("METADATA_METHOD");
		METADATA_FILELOC=propOutput.getProperty("METADATA_FILELOC");
		METADATA_JDBC_SCHEMA=propOutput.getProperty("METADATA_JDBC_SCHEMA");
		METADATA_JDBC_TABLE=propOutput.getProperty("METADATA_JDBC_TABLE");
		
		JDBC_IS_ACTIVE=propOutput.getProperty("JDBC_IS_ACTIVE");
		JDBC_LOAD=propOutput.getProperty("JDBC_LOAD");
		JDBC_METHOD=propOutput.getProperty("JDBC_METHOD");
		JDBC_URL=propOutput.getProperty("JDBC_URL");
		
		DELIMITED_IS_ACTIVE=propOutput.getProperty("DELIMITED_IS_ACTIVE");
		DELIMITED_DELIMITER=propOutput.getProperty("DELIMITED_DELIMITER");
		DELIMITED_FILE_LOCATION=propOutput.getProperty("DELIMITED_FILE_LOCATION");
		DELIMITED_PRINT_SCHEMA_HEADER_MODE=propOutput.getProperty("DELIMITED_PRINT_SCHEMA_HEADER_MODE");
		DELIMITED_HEADER_FILE_LOCATION=propOutput.getProperty("DELIMITED_HEADER_FILE_LOCATION");
		
		
		JSON_IS_ACTIVE=propOutput.getProperty("JSON_IS_ACTIVE");
		if(JSON_IS_ACTIVE!=null && (JSON_IS_ACTIVE.trim().equalsIgnoreCase("Y")||JSON_IS_ACTIVE.trim().equalsIgnoreCase("YES")))
			isJsonActive=true;
		JSON_FILE_LOCATION=propOutput.getProperty("JSON_FILE_LOCATION");
		JSON_HEADER_FILE=propOutput.getProperty("JSON_HEADER_FILE");
		
		TIF_IS_ACTIVE=propOutput.getProperty("TIF_IS_ACTIVE");
		TIF_FILE_LOCATION=propOutput.getProperty("TIF_FILE_LOCATION");
		
		RPC_IS_ACTIVE=propOutput.getProperty("RPC_IS_ACTIVE");
		RPC_HOST=propOutput.getProperty("RPC_HOST");
		RPC_PORT=propOutput.getProperty("RPC_PORT");
		RPC_PATH=propOutput.getProperty("RPC_PATH");
		RPC_SOURCE_ID=propOutput.getProperty("RPC_SOURCE_ID");
		
		LOA_IS_ACTIVE=propOutput.getProperty("LOA_IS_ACTIVE");
		LOA_FILE_LOCATION=propOutput.getProperty("LOA_FILE_LOCATION");
		LOA_TYPE=propOutput.getProperty("LOA_TYPE");
		LOA_FILE_PATTERN=propOutput.getProperty("LOA_FILE_PATTERN");
		LOA_SIZE=propOutput.getProperty("LOA_SIZE");
		LOA_IS_WITH_HEADER=propOutput.getProperty("LOA_IS_WITH_HEADER");
		
		
		if(LOA_IS_WITH_HEADER!=null&&(LOA_IS_WITH_HEADER.trim().equalsIgnoreCase("Y") || LOA_IS_WITH_HEADER.trim().equalsIgnoreCase("YES")))
			isLoaWithHeader=true;
		if(LOA_TYPE!=null&&LOA_TYPE.trim().equals("mysql1.0"))
			isLoaMysql10=true;
		if(LOA_TYPE!=null&&LOA_TYPE.trim().equals("mysql1.1"))
			isLoaMysql11=true;
		if(LOA_TYPE!=null&&LOA_TYPE.trim().equals("ora1.0"))
			isLoaOra10=true;
		

		String CSV_IS_ACTIVE=propOutput.getProperty("CSV_IS_ACTIVE");
		if(CSV_IS_ACTIVE!=null && (CSV_IS_ACTIVE.trim().equalsIgnoreCase("Y")||CSV_IS_ACTIVE.trim().equalsIgnoreCase("YES")))
			isCsvActive=true;
		CSV_FILE_LOCATION=propOutput.getProperty("CSV_FILE_LOCATION");
		CSV_FILE_PATTERN=propOutput.getProperty("CSV_FILE_PATTERN");
	};
	
	
	public boolean isLoaMysql11() {
		return isLoaMysql11;
	}

	public void setLoaMysql11(boolean isLoaMysql11) {
		this.isLoaMysql11 = isLoaMysql11;
	}

	public boolean isCsv() {
		return isCsvActive;
	}
	
	public String getCSV_FILE_LOCATION() {
		return CSV_FILE_LOCATION;
	}

	public String getCSV_FILE_PATTERN() {
		return CSV_FILE_PATTERN;
	}

	public boolean isLoaMysql10() {
		return isLoaMysql10;
	}

	public boolean isLoaOra10() {
		return isLoaOra10;
	}

	public String getJDBC_METHOD() {
		return JDBC_METHOD;
	}

	public String getJDBC_URL() {
		return JDBC_URL;
	}

	public String getMETADATA_JDBC_SCHEMA() {
		return METADATA_JDBC_SCHEMA;
	}


	public String getMETADATA_JDBC_TABLE() {
		return METADATA_JDBC_TABLE;
	}


	public String getMETADATA_METHOD() {
		return METADATA_METHOD;
	}

	public String getMETADATA_FILELOC() {
		return METADATA_FILELOC;
	}

	public String getTIF_IS_ACTIVE() {
		return TIF_IS_ACTIVE;
	}

	public String getLOA_FILE_LOCATION() {
		return LOA_FILE_LOCATION;
	}

	public String getLOA_TYPE() {
		return LOA_TYPE;
	}

	public String getLOA_FILE_PATTERN() {
		return LOA_FILE_PATTERN;
	}

	public boolean isOutputMapping(){
		if(METADATA_IS_ACTIVE==null)
			return false;
		return METADATA_IS_ACTIVE.trim().equalsIgnoreCase("Y") || METADATA_IS_ACTIVE.trim().equalsIgnoreCase("YES");
	}
	
	public boolean isLoaWithHeader(){
		return isLoaWithHeader;
	}
	
	public boolean isJdbc(){
		if(JDBC_IS_ACTIVE==null)
			return false;
		return JDBC_IS_ACTIVE.trim().equalsIgnoreCase("Y") || JDBC_IS_ACTIVE.trim().equalsIgnoreCase("YES");
	}
	
	public boolean isDelimited(){
		if (DELIMITED_IS_ACTIVE==null)
			return false;
		return  DELIMITED_IS_ACTIVE.trim().equalsIgnoreCase("Y") || DELIMITED_IS_ACTIVE.trim().equalsIgnoreCase("YES");
	}
	
	public boolean isJson(){
		return isJsonActive;
	}
	
	public boolean isRpc(){
		if (RPC_IS_ACTIVE==null)
			return false;
		return  RPC_IS_ACTIVE.trim().equalsIgnoreCase("Y") || RPC_IS_ACTIVE.trim().equalsIgnoreCase("YES");
	}
	
	public boolean isLoa(){
		if (LOA_IS_ACTIVE==null)
			return false;
		return  LOA_IS_ACTIVE.trim().equalsIgnoreCase("Y") || LOA_IS_ACTIVE.trim().equalsIgnoreCase("YES");
	}
	
	
	public boolean isDelimitedPrintHeader(){
		if (DELIMITED_PRINT_SCHEMA_HEADER_MODE==null)
			return false;
		return DELIMITED_PRINT_SCHEMA_HEADER_MODE.trim().equalsIgnoreCase("Y") || DELIMITED_PRINT_SCHEMA_HEADER_MODE.trim().equalsIgnoreCase("YES");
	}
	
	public boolean isTif(){
		return TIF_IS_ACTIVE!=null && TIF_IS_ACTIVE.equalsIgnoreCase("Y") || TIF_IS_ACTIVE.equalsIgnoreCase("YES");
	}
	
	
	public int GetJdbcLoad(){
		try{
			return Integer.parseInt(JDBC_LOAD.trim());
		}catch(Exception e){
			return 1000;
		}
	}
	
	public int GetLoaSize(){
		try{
			return Integer.parseInt(LOA_SIZE.trim());
		}catch(Exception e){
			return 50000;
		}
	}
	
	public String GetDelimitedDelimiter(){
		if(DELIMITED_DELIMITER!=null)
			return DELIMITED_DELIMITER.trim();
		return ",";
	}
	
	public String GetDelimitedFileLocation(){
		if (DELIMITED_FILE_LOCATION!=null)
			return DELIMITED_FILE_LOCATION.trim();
		return "";
	}
	
	public String GetTifFileLocation(){
		if(TIF_FILE_LOCATION!=null)
			return TIF_FILE_LOCATION.trim();
		return "";
	}
	public String GetDelimitedHeaderFileLocation(){
		if (DELIMITED_HEADER_FILE_LOCATION!=null)
			return DELIMITED_HEADER_FILE_LOCATION;
		return "";
	}
	public String GetJsonHeaderFile(){
		if (JSON_HEADER_FILE!=null)
			return JSON_HEADER_FILE;
		return "";
	}
	public String GetJsonFileLocation(){
		if (JSON_FILE_LOCATION!=null)
			return JSON_FILE_LOCATION;
		return "";
	}
	
	
	public String getRPC_HOST() {
		if (RPC_HOST!=null)
			return RPC_HOST;
		return "";
	}
	
	public String getRPC_SOURCE_ID() {
		return RPC_SOURCE_ID;
	}
	
	public int getRPC_PORT() {
		try {
			return Integer.parseInt(RPC_PORT);
		}catch (NumberFormatException e) {
			return 4444;
		}
	}
	
	public String getRPC_PATH() {
		if(RPC_PATH==null)
			return "/tmp/t_hourly/source_id=[$source]/datetime_h=[$datetime]";
		return RPC_PATH;
	}

	@Override
	public String toString() {
		return "OutputMethodPropReader [propOutput=" + propOutput
				+ ", METADATA_IS_ACTIVE="
				+ METADATA_IS_ACTIVE
				+ ", METADATA_METHOD="
				+ METADATA_METHOD
				+ ", METADATA_FILELOC="
				+ METADATA_FILELOC + ", JDBC_IS_ACTIVE="
				+ JDBC_IS_ACTIVE + ", JDBC_LOAD=" + JDBC_LOAD
				+ ", DELIMITED_IS_ACTIVE=" + DELIMITED_IS_ACTIVE
				+ ", DELIMITED_DELIMITER=" + DELIMITED_DELIMITER
				+ ", DELIMITED_FILE_LOCATION=" + DELIMITED_FILE_LOCATION
				+ ", DELIMITED_PRINT_SCHEMA_HEADER_MODE="
				+ DELIMITED_PRINT_SCHEMA_HEADER_MODE
				+ ", DELIMITED_HEADER_FILE_LOCATION="
				+ DELIMITED_HEADER_FILE_LOCATION + ", JSON_IS_ACTIVE="
				+ JSON_IS_ACTIVE + ", JSON_FILE_LOCATION=" + JSON_FILE_LOCATION
				+ ", JSON_HEADER_FILE=" + JSON_HEADER_FILE + ", RPC_IS_ACTIVE="
				+ RPC_IS_ACTIVE + ", RPC_HOST=" + RPC_HOST + ", RPC_PORT="
				+ RPC_PORT + ", RPC_PATH=" + RPC_PATH + ", TIF_IS_ACTIVE="
				+ TIF_IS_ACTIVE + ", TIF_FILE_LOCATION=" + TIF_FILE_LOCATION
				+ ", LOA_IS_ACTIVE=" + LOA_IS_ACTIVE + ", LOA_FILE_LOCATION="
				+ LOA_FILE_LOCATION + "]";
	}
	
}
