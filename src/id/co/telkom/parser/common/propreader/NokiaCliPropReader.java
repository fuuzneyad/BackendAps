package id.co.telkom.parser.common.propreader;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class NokiaCliPropReader {
	//Var
	String DB_Host, DB_Username, DB_Password, DB_Port, DB_Name;
	String Ssh_Username, Ssh_Host, Ssh_Password, Protocol;
	String Auto_Login, LogFolder, LogDateFormat,LogFileName, NeList, CommandList, MultiThreading;
	int Login_Attempt, Ssh_Port, MaxThread;
	String Log4JConfig,OutputMethod,SourceId;
	

  private void getIt(String config){
    Properties prop = new Properties();
    String fileName = config;
    try { 
	    prop.load(new FileInputStream(fileName)); 
    } catch (IOException e) { System.out.println("Do you have the config file?\n"); System.exit(0);}
                  
    DB_Host 		= 	prop.getProperty("DB_Host");
    DB_Username 	= 	prop.getProperty("DB_Username"); 
    DB_Password 	= 	prop.getProperty("DB_Password");
    DB_Port 		= 	prop.getProperty("DB_Port");
    DB_Name 		= 	prop.getProperty("DB_Name");		
	Protocol 		= 	prop.getProperty("Protocol");	    
    Ssh_Username 	= 	prop.getProperty("Ssh_Username");
    Ssh_Password 	= 	prop.getProperty("Ssh_Password");
    Ssh_Host 		= 	prop.getProperty("Ssh_Host");    
	Auto_Login 		= 	prop.getProperty("Auto_Login");
	LogFolder 		= 	prop.getProperty("LogFolder");
	LogDateFormat 	= 	prop.getProperty("LogDateFormat");
	NeList 			= 	prop.getProperty("NeList");
	CommandList 	= 	prop.getProperty("CommandList");
	LogFileName 	= 	prop.getProperty("LogFileName");
	Log4JConfig 	= 	prop.getProperty("Log4JConfig");
	OutputMethod 	= 	prop.getProperty("OutputMethod");
	SourceId 		= 	prop.getProperty("SourceId");
	
    try{
    	MaxThread=Integer.parseInt(prop.getProperty("MaxThread"));
    }catch(NumberFormatException e)
	{MaxThread=1;}
    
	MultiThreading 	= 	prop.getProperty("MultiThreading");
	
	
    try{
        Login_Attempt=Integer.parseInt(prop.getProperty("Login_Attempt"));
    }catch(NumberFormatException e)
	{Login_Attempt=1;}

    try{
    	Ssh_Port=Integer.parseInt(prop.getProperty("Ssh_Port"));
    }catch(NumberFormatException e)
	{Ssh_Port=22;}
    
    try {
		new SimpleDateFormat(LogDateFormat);				
	}
    catch(Exception e)
	{
    	System.out.println("Wrong date Format Configuration!!");
    	LogDateFormat= "yyyyMMdd"; 
    }

  }
  
  public NokiaCliPropReader(String config){
  	  getIt(config);
  }
  
  public String getDB_Host(){
	if (DB_Host==null)
	  DB_Host="localhost";
	  return DB_Host;
  }
  
  public String getDB_Username(){
	if (DB_Username==null)
	  DB_Username="root";
	  return DB_Username;
  }
  
  public String getDB_Password(){
	if (DB_Password==null)
	  DB_Password="root";
	  return DB_Password;
  }
  
  public String getDB_Port(){
	if (DB_Port==null)
	  DB_Port="3306";
	  return DB_Port;
  }
  
  public String getDB_Name(){
	if (DB_Name==null)
	  DB_Name="traversa2_ph1";	  
	  return DB_Name;
  }
  
  public String getSsh_Username(){
  	if (Ssh_Username==null)
	  Ssh_Username="teezdumk";
	  return Ssh_Username;
  }
  
  public String getSsh_Password(){ 
	  	return Ssh_Password;
  }

  public String getMultiThreading(){
	  	if (MultiThreading==null)
	  		{
	  		MultiThreading="no";
	  		this.MaxThread=1;
	  		}else
	  	if (!MultiThreading.trim().equalsIgnoreCase("yes"))	
	  		this.MaxThread=1;
	  	
		  return MultiThreading.trim();
	  }  
  
  public String getSsh_Host(){
	  	if (Ssh_Host==null)
		  Ssh_Host="localhost";	  
		  return Ssh_Host;
  } 
  
  public String getAuto_Login(){
	if (Auto_Login==null)
	  Auto_Login="no";	
	  return Auto_Login;
  }

  public String getProtocol(){
	if (Protocol==null)
	  Protocol="SSH";
	  return Protocol;
  }
  
  public String getLogFolder(){
		if (LogFolder==null)
			LogFolder="raws";
		  return LogFolder;
  }
  
  public String getLogFileName(){
		if (LogFileName==null)
			LogFileName="_";
		  return "_"+LogFileName+"_";
  }
  public String getLogDateFormat(){
		if (LogDateFormat==null)
			LogDateFormat="xxx";
		return LogDateFormat;
  }
  
  public String getNeList(){
		if (NeList==null)
			NeList="Ne.lst";
		  return NeList;
  }
  
  public String getCommandList(){
		if (CommandList==null)
			CommandList="Command.lst";
		  return CommandList;
  }	
  
  public int getLogin_Attempt(){
      return Login_Attempt;
  }
  
  public int getSsh_Port(){
      return Ssh_Port;
  }

  public int getMaxThread(){
	  if (MaxThread>8)
		  MaxThread=8;
      return MaxThread;
  }

	public String getLog4JConfig() {
		return Log4JConfig;
	}
	
	public String getOutputMethod() {
		return OutputMethod;
	}
	
	public String getSourceId() {
		return SourceId;
	}

	@Override
	public String toString() {
		return "NokiaCliPropReader [DB_Host=" + DB_Host + ", DB_Username="
				+ DB_Username + ", DB_Password=" + DB_Password + ", DB_Port="
				+ DB_Port + ", DB_Name=" + DB_Name + ", Ssh_Username="
				+ Ssh_Username + ", Ssh_Host=" + Ssh_Host + ", Ssh_Password="
				+ Ssh_Password + ", Protocol=" + Protocol + ", Auto_Login="
				+ Auto_Login + ", LogFolder=" + LogFolder + ", LogDateFormat="
				+ LogDateFormat + ", LogFileName=" + LogFileName + ", NeList="
				+ NeList + ", CommandList=" + CommandList + ", MultiThreading="
				+ MultiThreading + ", Login_Attempt=" + Login_Attempt
				+ ", Ssh_Port=" + Ssh_Port + ", MaxThread=" + MaxThread
				+ ", Log4JConfig=" + Log4JConfig + ", OutputMethod="
				+ OutputMethod + ", SourceId=" + SourceId + "]";
	}
  
  
}
