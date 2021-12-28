package id.co.telkom.executor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import id.co.telkom.grabber.ftp.Ericsson3GGetFilesThread;
import id.co.telkom.grabber.ftp.JvftpGetFiles;
import id.co.telkom.parser.common.loader.DBFileListWriter;
import id.co.telkom.parser.common.propreader.FTPPropReader;
import id.co.telkom.parser.common.util.ThreadTracker;

public class GetFilesEri3GExecutor {
	private Properties prop = new Properties();
	private JvftpGetFiles ftp;
	private FTPPropReader ftpProp;
	private static ApplicationContext context;
	private static final long start = System.currentTimeMillis();
	private static ExecutorService poolRef;
	private final int maxThread = 12;
	
	public static void main(String[] args){

		if(args.length<1){
			System.out.println("Usage with [config file]..");
			System.exit(0);
		}
			
		GetFilesEri3GExecutor ftpexe = new GetFilesEri3GExecutor(args[0]);
		
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
	
	public GetFilesEri3GExecutor(String config){
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
		
		GetFilesEri3GExecutor.poolRef =  Executors.newFixedThreadPool(maxThread);
		  
	}
	
	private void GetFiles(String manualArgument, ThreadTracker threadTracker){
		for (int source=1; source<=10; source++){
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
						this.ftp = new JvftpGetFiles( ftpProp);
						
					String host 	= ftpProp.getFTP_HOST();
					String username	=ftpProp.getFTP_USERNAME();
					String port = ftpProp.getFTP_PORT();
					String password = ftpProp.getFTP_PASSWD();
					String remotePath = ftpProp.getFTP_REMOTE_DIR();
					String localPath = ftpProp.getFTP_LOCAL_DIR();
					String regexPattern = ftpProp.getFTP_FILEPATTERN();
					String dateArgument =manualArgument != null ? "(.*)"+manualArgument+"(.*)" : "(.*)";
					String datePattern =ftpProp.getFTP_DATEPATTERN()==null || ftpProp.getFTP_DATEPATTERN().trim().equals("") ? ftpProp.getFTP_DATEPATTERN() : dateArgument;
					
					ArrayList<String> paths = ftp.GetListDirEriscsson3GXml(host, username, password,  Integer.parseInt(port), remotePath, null, ftpProp.getFTP_SUBREMOTE_DIR(), convertDate(datePattern));
					for (String s : paths){
						Ericsson3GGetFilesThread thread = new Ericsson3GGetFilesThread(threadTracker,ftpProp,  dbWriter, s, localPath, regexPattern, datePattern);
						poolRef.execute(thread);
					}
					
				}
		}
	}
	
	private long convertDate(String val){
		val = getNumber(val);
		try {
			SimpleDateFormat datetimeFormatter1 = new SimpleDateFormat("yyyyMMdd");
			Date lFromDate1 = datetimeFormatter1.parse(val);
			return lFromDate1.getTime();
		}catch(Exception e){
			return new Date().getTime();
		}
	}
	
	private String getNumber(String s){
		StringBuilder sb = new StringBuilder();
		for (char c:s.toCharArray()){
			if(c >= '0' && c <= '9')
				sb.append(c);
		}
		return sb.toString();
	}
}

