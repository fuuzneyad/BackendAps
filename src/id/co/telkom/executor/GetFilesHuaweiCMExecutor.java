package id.co.telkom.executor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import id.co.telkom.grabber.ftp.JvftpGetFilesHuaweiCM;
import id.co.telkom.parser.common.loader.DBFileListWriter;
import id.co.telkom.parser.common.propreader.FTPPropReader;
import id.co.telkom.parser.common.util.ThreadTracker;

public class GetFilesHuaweiCMExecutor {
	private Properties prop = new Properties();
	private JvftpGetFilesHuaweiCM ftp;
	private FTPPropReader ftpProp;
	private static ApplicationContext context;
	private static final long start = System.currentTimeMillis();
	private static ExecutorService poolRef;
	
	public static void main(String[] args){

		if(args.length<1){
			System.out.println("Usage with [config file]..");
			System.exit(0);
		}
			
		GetFilesHuaweiCMExecutor ftpexe = new GetFilesHuaweiCMExecutor(args[0]);
		
		final ThreadTracker threadTracker = new ThreadTracker(){
			@Override
			public void allThreadsHaveFinished() {
				getPool().shutdown();
				long finish =System.currentTimeMillis()-start;
				final long secondInMillis = 1000;
				final long minuteInMillis = secondInMillis * 60;
				final long hourInMillis = minuteInMillis * 60;
				final long dayInMillis = hourInMillis * 24;
				final long elapsedDays = finish / dayInMillis;
					finish = finish % dayInMillis;
				final long elapsedHours = finish / hourInMillis;
					finish = finish % hourInMillis;
				final  long elapsedMinutes = finish / minuteInMillis;
					finish = finish % minuteInMillis;
				final long elapsedSeconds = finish / secondInMillis;
				
				String elapsed = elapsedDays+" Days "+elapsedHours+" Hour " +elapsedMinutes+" Minutes "+elapsedSeconds+" Seconds ";
				System.out.println("\nFinish!! elapsed: "+elapsed);
			}
		};
		threadTracker.setPool(poolRef);
		
		ftpexe.GetFiles(args[1], threadTracker);
	}
	
	public GetFilesHuaweiCMExecutor(String config){
		String fileName = "01_config/CynapseFTP.cfg";
		File configFile = new File(fileName);
		if(!configFile.exists() || !configFile.isFile()) {
			fileName = config;
			configFile = new File(fileName);
			
			if(!configFile.exists() || !configFile.isFile()){
				System.err.println("Configuration file Not Found");
				System.exit(1);
			}
			
		}
		
		 try {
		    	prop.load(new FileInputStream(fileName)); 	
		    }catch (IOException e) { System.out.println("No "+fileName+" file?\n"); System.exit(0);}
		
		GetFilesHuaweiCMExecutor.poolRef =  Executors.newFixedThreadPool(10);
		  
	}
	
	private void GetFiles(String manualArgument, ThreadTracker threadTracker){
		for (int source=1; source<=20; source++){
			this.ftpProp = new FTPPropReader(prop, source);
				if(ftpProp.isValid() && ftpProp.isGET_FTP()){
					System.out.println("Getting FTP for "+ftpProp.getMODUL_NAME());
					
						//READ OUTPUT CONFIG
					 	Properties propOutput = new Properties();
					 
					    try {
					    	propOutput.load(new FileInputStream(ftpProp.getOUTPUT_CONFIG())); 
					    }catch (IOException e) { e.printStackTrace();}
					    
					    String appContext= ftpProp.getPARSER_CONFIG();
					    System.setProperty("JDBC_OUTPUTMETHOD", ftpProp.getOUTPUT_CONFIG());
						context = new FileSystemXmlApplicationContext (appContext);
						DataSource ds = (DataSource)context.getBean("dataSource");
						DBFileListWriter dbWriter = new DBFileListWriter(ds);
						this.ftp = new JvftpGetFilesHuaweiCM( dbWriter,ftpProp);
						
					String host 	= ftpProp.getFTP_HOST();
					String username	=ftpProp.getFTP_USERNAME();
					String port = ftpProp.getFTP_PORT();
					String password = ftpProp.getFTP_PASSWD();
					String remotePath = ftpProp.getFTP_REMOTE_DIR();
					String localPath = ftpProp.getFTP_LOCAL_DIR();
					String regexPattern = ftpProp.getFTP_FILEPATTERN();
					String dateArgument =manualArgument != null ? "(.*)"+manualArgument+"(.*)" : "(.*)";
					String datePattern =ftpProp.getFTP_DATEPATTERN()==null || ftpProp.getFTP_DATEPATTERN().trim().equals("") ? ftpProp.getFTP_DATEPATTERN() : dateArgument;
					
					String subRemoteDir=ftpProp.getFTP_SUBREMOTE_DIR()+manualArgument;
					
					ftp.GetFilesHuaweiCM(host, 
							username, 
							password, 
							Integer.parseInt(port), 
							remotePath+"/"+subRemoteDir, 
							localPath, 
							regexPattern, 
							datePattern
							);
					
				}
		}
	}
}

