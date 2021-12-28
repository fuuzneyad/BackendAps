package id.co.telkom.grabber.sshcli;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import com.jcraft.jsch.*;

import id.co.telkom.parser.common.propreader.NokiaCliPropReader;
import id.co.telkom.parser.common.util.ThreadTrackerCliNok;


public class EricssonGrabberThreadv2 extends Thread {
	private ThreadTrackerCliNok threadTracker;
	private final String tanggal;
	private Session session;
	private String NE;
	private NokiaCliPropReader propreader;
	private ArrayList<String>COMMAND;
	@SuppressWarnings("unused")
	private ArrayList<String> SpecParam;
	private int sleepTime;
	private static Random generator = new Random();
	private static final Logger log = Logger.getLogger(EricssonGrabberThreadv2.class);
	private NokiaCliPropReader conf;
	private Map<String, Integer> attempt = new LinkedHashMap<String, Integer>();
	private InputStream is;
	private OutputStream writer;
	private static String encoding = "UTF-8";
	private Channel channel;
	 
	public EricssonGrabberThreadv2(Session session, String NE, String tanggal, ThreadTrackerCliNok threadTracker, NokiaCliPropReader propreader, ArrayList<String>COMMAND, NokiaCliPropReader conf) {
		this.session =session;
		this.tanggal=tanggal;
		this.NE=NE;
		this.threadTracker=threadTracker;
		this.propreader=propreader;
		this.COMMAND=COMMAND;
		this.conf=conf;
		
	}

	@SuppressWarnings("unused")
	private void connect(){
		try {
			this.session=(new JSch()).getSession(conf.getSsh_Username(), 
					  conf.getSsh_Host(), conf.getSsh_Port());
			this.session.setPassword(conf.getSsh_Password());  
			this.session.setConfig("StrictHostKeyChecking", "no");
			this.session.setConfig("PreferredAuthentications",//kerberos bypass 
								   "publickey,keyboard-interactive,password");	
		
			log.info("Connecting to "+conf.getSsh_Username()+"@"
					  +conf.getSsh_Host()+":"+conf.getSsh_Port());
			this.session.connect(10000);
			log.info("Connected.");	
		} catch (JSchException e) {
			e.printStackTrace();
		}   
	}
	
	private void disconnect(){
		log.info("Disconnecting "+conf.getSsh_Username()+"@"
				  +conf.getSsh_Host()+":"+conf.getSsh_Port());
		try {
			is.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		channel.disconnect();
	}
	
	@Override
	public void run() {
		log.info("Grabber for ["+NE+"] Started..");
		threadTracker.incrementCounter();
		long start = System.currentTimeMillis();
		try {
			try{Thread.sleep(2000);}catch (InterruptedException e){}
			if(eaw()){
				  log.info("Eaw Success "+NE);
				  try{Thread.sleep(2000);}catch (InterruptedException e){}
			      for (String command : COMMAND){
			    	  	
			    	  	if(NE.contains("_")){
			    	  		NE=NE.split("_")[0];
			    	  	}
			    	  	
			    	  	String cmdString = command.split(":")[0].replace(";", "").replace(",", "");
			    	  	String FN=propreader.getLogFolder()+"/"+NE+"_"+cmdString+propreader.getLogFileName()+tanggal+".txt";
		    	  		grab(command+";", FN, command, true);
		    	  		try{Thread.sleep(2000);}catch (InterruptedException e){}
		    	  		
			      }
			      grab("exit;", null, "exit", false);
			}else
				log.error("cannot eaw " +NE);

		} catch (Exception e) {
			log.info("Exception :"+e.getMessage());
		} finally {			
			log.info("NE ["+NE+"] Have been finished. Elapsed: "+(System.currentTimeMillis() - start));
			disconnect();
			threadTracker.decreaseCounter();						
		}
	}
	private boolean eaw() throws Exception{
			channel = session.openChannel("shell");
			channel.connect();
			if(writer != null)
				writer.close();
			if(is != null)
				is.close();
			is = channel.getInputStream();
			writer = channel.getOutputStream();
			
			try{Thread.sleep(1000);}catch (InterruptedException e){}
			
			writer.write(("eaw "+NE+"\n").getBytes(encoding));
			writer.flush();
			while(true){
				while (is.available() > 0)
				{
					int i = is.read();
					if(i<0)break;
					if(i=='<')
						return true; 
//					else
//					if(i=='>')
//							return false;
				}
				if(channel.isClosed()){
			  		  break;
			  	}
				try
		        {
			    	  	sleepTime = generator.nextInt( 1000 );
			    	  	Thread.sleep( sleepTime );
		        }
		        catch (Exception ee)
		        {
		      	  System.err.println("sleep exp "+ee);
		        }
			}
			return false;
		
	}
	private synchronized void grab(String runcommand, String FN, String command, boolean writeOutput) throws IOException, JSchException{
		log.info("Grabbing ["+NE+"] "+command);
		writer.write((runcommand+"\n").getBytes(encoding));
		writer.flush();
		is.mark(32);
		FileWriter out=null; 
	    if(writeOutput){
	    	out= new FileWriter(FN, true);
	    	out.write("<");
	    }
		    byte[] tmp=new byte[1024];
		    String s="";
		    while (true){
		    	String tempStr=null;
				while(is.available()>0){
					  int i=is.read(tmp, 0, 1024);
					  tempStr = new String(tmp, 0, i);
					  if(tempStr.contains("<"))
						  s=tempStr;
					  if(writeOutput && out!=null){
						  out.write(tempStr);
					      out.flush();
				  	  }
					  
					  if(i<0){
						  break;
					  }
				}
				if(channel.isClosed())
		    		  break;
				if(s.contains("<")){
					break;
				}
				
				try{
					 sleepTime = generator.nextInt( 500 );
					 Thread.sleep( sleepTime );
				}catch (Exception ee){
				      System.err.println("sleep exp "+ee);
				}
				 
		    }
			if(writeOutput){
				out.close();
			}
			resetAttempt();
	}
	
	
	
	@SuppressWarnings("unused")
	private synchronized void grab2(String runcommand, String FN, String command, boolean writeOutput) throws IOException, JSchException{
		log.info("Grabbing "+command+" "+ NE);
		writer.write((runcommand+"\n").getBytes(encoding));
		writer.flush();
		is.mark(32);
		FileWriter out=null; 
	    if(writeOutput){
	    	out= new FileWriter(FN, true);
	    	out.write("<");
	    }
		    byte[] tmp=new byte[1024];
		    while (true){
		    	int c=0;
		    	String tempStr=null;
				while(is.available()>0){
					  int i=is.read(tmp, 0, 1024);
					  if(i>0)
						  c=i;
					  tempStr = new String(tmp, 0, i);
					  if(writeOutput && out!=null){
						  out.write(tempStr);
					      out.flush();
				  	  }
					  if(i<0 || i=='<'){
						  try{Thread.sleep(1000);} catch (InterruptedException e){}
						  break;
					  }
				}
				if(channel.isClosed())
		    		  break;
				if(c=='<' || c==0x03)
					break;
				if(checkAttempt(NE)==0 && c==0)
					break;
				try{
					 sleepTime = generator.nextInt( 1000 );
					 Thread.sleep( sleepTime );
				}catch (Exception ee){
				      System.err.println("sleep exp "+ee);
				}
				 
		    }
			if(writeOutput){
				out.close();
			}
			resetAttempt();
	}
	
	//TODO: check there is more stream anymore..
	private int checkAttempt(String neCommand){
		
		final int maxAttempt = 5;
		Integer att = attempt.get(neCommand);
		if(att==null){
			attempt.put(neCommand, maxAttempt);
			return maxAttempt;
		}else
			attempt.put(neCommand, att-1);
		
		return att;
			
	}
	
	private void resetAttempt(){
		attempt = new LinkedHashMap<String, Integer>();
	}

}
