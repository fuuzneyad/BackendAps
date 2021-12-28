package id.co.telkom.executor;

import com.jcraft.jsch.*;

import id.co.telkom.grabber.sshcli.EricssonGrabberThreadv2;
import id.co.telkom.parser.common.propreader.NokiaCliPropReader;
import id.co.telkom.parser.common.util.ThreadTrackerCliNok;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.util.Date;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class EricssonSshGrabberMain {

	public final ExecutorService poolRef;
	public static int maxThread = 1;
	private static NokiaCliPropReader conf;
	private final ThreadTrackerCliNok threadTracker;
	private static final Logger log = Logger.getLogger(EricssonSshGrabberMain.class);
	private final Properties l4jPr = new Properties();
	private static Session session;
	
public EricssonSshGrabberMain(ThreadTrackerCliNok threadTracker, ExecutorService poolRef/*, Session Session*/){
	this.poolRef=poolRef;
	this.threadTracker = threadTracker;
	try {
		l4jPr.load(new FileInputStream(conf.getLog4JConfig()));
		} catch (FileNotFoundException e1) {
			System.err.println("log4j Configurator "+conf.getLog4JConfig()+" not found");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	System.setProperty("SOURCEID", conf.getSourceId());
	PropertyConfigurator.configure(l4jPr);
}	
 public static void main(String[] arg){
     try{
     String config=null;
     if(arg.length>=1)
    	config = arg[0];
     else
    	config =  "Grabber.cfg";
     conf = new NokiaCliPropReader(config);  
     
     DateFormat tanggal = new SimpleDateFormat (conf.getLogDateFormat());
     String sekarang = tanggal.format(new Date());
     
     conf.getMultiThreading();
     maxThread=conf.getMaxThread();
   		 
	 connect();
	  
      //NE List File
      FileInputStream fis = new FileInputStream(conf.getNeList());
      DataInputStream in = new DataInputStream(fis);
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      String StrLine;
      ArrayList<String>NE=new ArrayList<String>();
      while ((StrLine = br.readLine()) != null )   {
	      if (!"".equalsIgnoreCase(StrLine.trim()) && !StrLine.startsWith("#")){		 
		      NE.add(StrLine.trim());
	      }
      }
      fis.close();
      in.close();
      
      //Command List File
      fis = new FileInputStream(conf.getCommandList());
      in = new DataInputStream(fis);
      br = new BufferedReader(new InputStreamReader(in));
      ArrayList<String>COMMAND=new ArrayList<String>();
      while ((StrLine = br.readLine()) != null)   {
	      if (!"".equalsIgnoreCase(StrLine.trim()) && !StrLine.startsWith("#")){
	    	  COMMAND.add(StrLine.trim());
	      }    	  
      }
	  fis.close();
	  in.close();
	  
	  final ExecutorService pool = Executors.newFixedThreadPool(maxThread);
	  
	  final ThreadTrackerCliNok threadTracker = new ThreadTrackerCliNok(){		  	
			public void allThreadsHaveFinished(){
					log.info("Finish...");
					session.disconnect();
					getPool().shutdown();
			}
		};
		
	  threadTracker.setMaxthread(maxThread);
	  threadTracker.setCountNE(NE.size());
	  threadTracker.setPool(pool);
	  EricssonSshGrabberMain ericssonGrabber = new EricssonSshGrabberMain(threadTracker, pool/*, session*/);
	  ericssonGrabber.execute(session, sekarang, conf, NE, COMMAND);
	}
	catch(Exception e){
	  log.error(e.getMessage());
	}
}
 
public void execute(Session session, String tanggal, NokiaCliPropReader conf,  ArrayList<String>NE,  ArrayList<String>COMMAND){
	
	  for (String ne : NE){		  
		  EricssonGrabberThreadv2 thread = new EricssonGrabberThreadv2(session, ne, tanggal, threadTracker, conf, COMMAND, conf);
		  poolRef.execute(thread);
	  }
  		
}
private static void connect(){
	try {
		session=(new JSch()).getSession(conf.getSsh_Username(), 
				  conf.getSsh_Host(), conf.getSsh_Port());
		session.setPassword(conf.getSsh_Password());  
		session.setConfig("StrictHostKeyChecking", "no");
		session.setConfig("PreferredAuthentications",//kerberos bypass 
							   "publickey,keyboard-interactive,password");	
	
		log.info("Connecting to "+conf.getSsh_Username()+"@"
				  +conf.getSsh_Host()+":"+conf.getSsh_Port());
		session.connect(10000);
		log.info("Connected.");	
	} catch (JSchException e) {
		e.printStackTrace();
	}   
}
}