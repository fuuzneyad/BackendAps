package id.co.telkom.parser.common.propreader;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ParserPropReader {
	private List<String> acquiredDatetimes; //Bagir for identified unique datetime_id
	private List<String> hashData; 
	private int source;
	public final String BKP_RM_FILE="RM";
	public final String BKP_MV_FILE="MV";
	private Properties prop;
	private   String 
					PARSER_ID,
					PARSER_USERNAME,
					PARSER_KEY,
					GENERATE_SCHEMA_MODE,
					REWRITE_METADATA_FL_MODE,
					SOURCE_ID,
					FILE_PATTERN,
					DATE_PATTERN,
					LOCAL_DIR,
					MAPPING_CONFIG,
					TABLE_PREFIX,
					CHECK_ALREADY_PROC,
					OUTPUT_CONFIG,
					FILE_SCHEMA_LOC,
					PARSER_APP_CONTEXT,
					PARSER_LOG4J_CONFIG,
					BACKUP_MECHANISM,
					BACKUP_DIR
					;
	private int MAX_THREAD, TIME_DIFF;
	
	public ParserPropReader(Properties prop){
		System.out.println("Reading Parser Configuration... ");
		this.prop=prop;
		this.PARSER_ID= prop.getProperty("PARSER_ID");
		this.PARSER_USERNAME= prop.getProperty("PARSER_USERNAME");
		this.PARSER_KEY= prop.getProperty("PARSER_KEY");
		this.GENERATE_SCHEMA_MODE=prop.getProperty("GENERATE_SCHEMA_MODE");
		this.REWRITE_METADATA_FL_MODE=prop.getProperty("REWRITE_METADATA_FL_MODE");
		this.FILE_SCHEMA_LOC = prop.getProperty("FILE_SCHEMA_LOC");
		this.MAX_THREAD=GetMaxThread(prop.getProperty("MAX_THREAD"));
		this.PARSER_APP_CONTEXT=prop.getProperty("PARSER_APP_CONTEXT");
		this.PARSER_LOG4J_CONFIG=prop.getProperty("PARSER_LOG4J_CONFIG");
		this.MAPPING_CONFIG=prop.getProperty("MAPPING_CONFIG");
		this.acquiredDatetimes = new ArrayList<String>();
	}
	
	public  ParserPropReader(Properties prop,int source){
		this.prop=prop;
		this.source=source;
		this.PARSER_ID= prop.getProperty("PARSER_ID");
		this.PARSER_USERNAME= prop.getProperty("PARSER_USERNAME");
		this.PARSER_KEY= prop.getProperty("PARSER_KEY");
		this.GENERATE_SCHEMA_MODE=prop.getProperty("GENERATE_SCHEMA_MODE");
		this.REWRITE_METADATA_FL_MODE=prop.getProperty("REWRITE_METADATA_FL_MODE");
		this.FILE_SCHEMA_LOC = prop.getProperty("FILE_SCHEMA_LOC");
		this.MAX_THREAD=GetMaxThread(prop.getProperty("MAX_THREAD"));
		this.PARSER_APP_CONTEXT=prop.getProperty("PARSER_APP_CONTEXT");
		this.PARSER_LOG4J_CONFIG=prop.getProperty("PARSER_LOG4J_CONFIG");
		this.MAPPING_CONFIG=prop.getProperty("MAPPING_CONFIG");
		
		this.SOURCE_ID= this.prop.getProperty("SOURCE_ID_"+source) ;
		this.FILE_PATTERN = this.prop.getProperty("FILE_PATTERN_"+source);
		this.DATE_PATTERN = this.prop.getProperty("DATE_PATTERN_"+source);
		this.LOCAL_DIR = this.prop.getProperty("LOCAL_DIR_"+source);
		if(this.MAPPING_CONFIG==null)
			this.MAPPING_CONFIG = this.prop.getProperty("MAPPING_CONFIG_"+source);
		this.TABLE_PREFIX = this.prop.getProperty("TABLE_PREFIX_"+source);
		this.CHECK_ALREADY_PROC  = this.prop.getProperty("CHECK_ALREADY_PROC_"+source);
		this.OUTPUT_CONFIG = this.prop.getProperty("OUTPUT_CONFIG_"+source);
		this.BACKUP_MECHANISM = this.prop.getProperty("BACKUP_MECHANISM_"+source);
		this.BACKUP_DIR = this.prop.getProperty("BACKUP_DIR_"+source);
		this.TIME_DIFF= GetGetTimeDiff(this.prop.getProperty("TIME_DIFF_"+source));
		this.acquiredDatetimes = new ArrayList<String>();
		this.hashData = new ArrayList<String>();
		
	}
	
	public boolean isValid(){
		boolean check =
				PARSER_ID!=null &&
				PARSER_USERNAME!=null&&
				PARSER_KEY!=null&&
				SOURCE_ID  !=null &&
				PARSER_APP_CONTEXT!=null&&
				FILE_PATTERN!=null &&
				DATE_PATTERN !=null &&
				LOCAL_DIR !=null &&
				MAPPING_CONFIG !=null &&
				TABLE_PREFIX  !=null &&
				CHECK_ALREADY_PROC  !=null &&
				OUTPUT_CONFIG !=null &&
				GENERATE_SCHEMA_MODE !=null &&
				REWRITE_METADATA_FL_MODE !=null &&
				FILE_SCHEMA_LOC !=null &&
				BACKUP_MECHANISM !=null &&
				BACKUP_DIR  !=null
				;
		return check;
	}
	
	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	private int GetMaxThread(String s){
		try{
			return Integer.parseInt(s);
		} catch (NumberFormatException e){
			return 1;
		}
	}
	
	private int GetGetTimeDiff(String s){
		try{
			return Integer.parseInt(s);
		} catch (NumberFormatException e){
			return 0;
		}
	}
	
	public String getPARSER_LOG4J_CONFIG() {
		return PARSER_LOG4J_CONFIG;
	}

	public String getPARSER_APP_CONTEXT() {
		return this.PARSER_APP_CONTEXT;
	}

	public String getPARSER_ID() {
		return this.PARSER_ID;
	}

	public String getPARSER_USERNAME() {
		return this.PARSER_USERNAME;
	}

	public String getPARSER_KEY() {
		return this.PARSER_KEY;
	}

	public String getBACKUP_MECHANISM() {
		if(!(this.BACKUP_MECHANISM.trim().equalsIgnoreCase("MV")||this.BACKUP_MECHANISM.trim().equalsIgnoreCase("N")))
			return "RM";
		return this.BACKUP_MECHANISM;
	}

	public String getBACKUP_DIR() {
		return this.BACKUP_DIR;
	}

	public int getMAX_THREAD() {
		return this.MAX_THREAD;
	}

	public int getTIME_DIFF() {
		return TIME_DIFF;
	}

	public String getSOURCE_ID() {
		return this.SOURCE_ID.trim();
	}

	public String getTABLE_PREFIX() {
		return this.TABLE_PREFIX.trim();
	}

	public boolean isCHECK_ALREADY_PROC() {
		return this.CHECK_ALREADY_PROC.trim().equalsIgnoreCase("Y") || this.CHECK_ALREADY_PROC.trim().equalsIgnoreCase("YES");
	}

	public boolean isGENERATE_SCHEMA_MODE() {
		if (this.GENERATE_SCHEMA_MODE==null)
			return false;
		return this.GENERATE_SCHEMA_MODE.trim().equalsIgnoreCase("Y") || this.GENERATE_SCHEMA_MODE.trim().equalsIgnoreCase("YES");
	}

	public boolean isREWRITE_METADATA_FL_MODE() {
		if (this.REWRITE_METADATA_FL_MODE==null)
			return false;
		return this.REWRITE_METADATA_FL_MODE.trim().equalsIgnoreCase("Y") || this.REWRITE_METADATA_FL_MODE.trim().equalsIgnoreCase("YES");
	}
	public String getOUTPUT_CONFIG() {
		return this.OUTPUT_CONFIG.trim();
	}

	public String getFILE_PATTERN() {
		return this.FILE_PATTERN;
	}

	public String getDATE_PATTERN() {
		return this.DATE_PATTERN.trim();
	}

	public String getLOCAL_DIR() {
		return this.LOCAL_DIR.trim();
	}

	public String getMAPPING_CONFIG() {
		return this.MAPPING_CONFIG.trim();
	}

	public String getFILE_SCHEMA_LOC() {
		return this.FILE_SCHEMA_LOC.trim();
	}
	
	public void addAcquiredDatetime(String datetime) {
		this.acquiredDatetimes.add(datetime);
	}

	public List<String> getAcquiredDatetimes() {
		return acquiredDatetimes;
	}
	
	public List<String> getHashData() {
		return hashData;
	}

	@Override
	public String toString() {
		return "ParserPropReader [BKP_RM_FILE=" + BKP_RM_FILE
				+ ", BKP_MV_FILE=" + BKP_MV_FILE /*+ ", prop=" + prop*/
				+ ", PARSER_ID=" + PARSER_ID + ", PARSER_USERNAME="
				+ PARSER_USERNAME + ", PARSER_KEY=" + PARSER_KEY
				+ ", GENERATE_SCHEMA_MODE=" + GENERATE_SCHEMA_MODE
				+ ", SOURCE_ID=" + SOURCE_ID + ", FILE_PATTERN="
				+ FILE_PATTERN + ", DATE_PATTERN=" + DATE_PATTERN
				+ ", LOCAL_DIR=" + LOCAL_DIR + ", MAPPING_CONFIG="
				+ MAPPING_CONFIG + ", TABLE_PREFIX=" + TABLE_PREFIX
				+ ", CHECK_ALREADY_PROC=" + CHECK_ALREADY_PROC
				+ ", OUTPUT_CONFIG=" + OUTPUT_CONFIG + ", FILE_SCHEMA_LOC="
				+ FILE_SCHEMA_LOC + ", PARSER_APP_CONTEXT="
				+ PARSER_APP_CONTEXT + ", BACKUP_MECHANISM="
				+ BACKUP_MECHANISM + ", BACKUP_DIR=" + BACKUP_DIR
				+ ", MAX_THREAD=" + MAX_THREAD + "]";
	}

}
