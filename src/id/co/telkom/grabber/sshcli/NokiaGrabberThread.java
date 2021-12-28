package id.co.telkom.grabber.sshcli;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import com.jcraft.jsch.*;

import id.co.telkom.parser.common.propreader.NokiaCliPropReader;
import id.co.telkom.parser.common.util.ThreadTrackerCliNok;


public class NokiaGrabberThread extends Thread {
	private ThreadTrackerCliNok threadTracker;
	private final String tanggal;
	private Session session;
	private String NE;
	private NokiaCliPropReader propreader;
	private ArrayList<String>COMMAND;
	private ArrayList<String> SpecParam;
	private String buffer="";
	private int Counter=0;
	private int sleepTime;
	private static Random generator = new Random();
	private static final Logger log = Logger.getLogger(NokiaGrabberThread.class);
	private NokiaCliPropReader conf;
	private Map<String, Integer> attempt = new LinkedHashMap<String, Integer>();
	
	public NokiaGrabberThread(/*Session session,*/ String NE, String tanggal, ThreadTrackerCliNok threadTracker, NokiaCliPropReader propreader, ArrayList<String>COMMAND, NokiaCliPropReader conf) {
		//this.session =session;
		this.tanggal=tanggal;
		this.NE=NE;
		this.threadTracker=threadTracker;
		this.propreader=propreader;
		this.COMMAND=COMMAND;
		this.conf=conf;
		try {
			this.session=(new JSch()).getSession(conf.getSsh_Username(), 
					  conf.getSsh_Host(), conf.getSsh_Port());
			this.session.setPassword(conf.getSsh_Password());  
			this.session.setConfig("StrictHostKeyChecking", "no");
			connect();
			
		} catch (JSchException e) {
			e.printStackTrace();
		}
	}

	private void connect(){
		try {
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
		session.disconnect();
	}
	
	@Override
	public void run() {
		threadTracker.incrementCounter();
		long start = System.currentTimeMillis();		
		log.info("Grabber for ["+NE+"] Started..");
//		String OSSDate =execCommand("date '+%Y%m%d%M%S'");
//			   OSSDate = OSSDate.equals("")?OSSDate:"_"+OSSDate;
		try {
			
		      for (String command : COMMAND){
		    	  	
		    	  	//isat case 12235#MS, -s -n ==> -s -i
		    	  	String NEName=NE;
		    	  	if(NE.contains("_")){
		    	  		NE=NE.split("_")[0];
		    	  	}
		    	  	
		    	  	String cmdString = command.split(":")[0].replace(";", "").replace(",", "");//added 2014-12-08 for traversa purpose
		    	  	String FN=propreader.getLogFolder()+"/"+NEName+"_"+cmdString+propreader.getLogFileName()+tanggal+".txt";
		    	  	
		    	  	sleepTime = generator.nextInt( 2000 );
		    	  	Thread.sleep( sleepTime );	    	  	
		    	  	
	    	  		log.info("exemmlmx -s -n "+NE+" -c \""+command+"\"");
	    	  		grab("exemmlmx -s -n "+NE+" -c \""+command+"\"",
	    	  				FN, command);
	    	  		
	    	  		if (command.equalsIgnoreCase("JFL;") && SpecParam!=null){
		    	  		for (String Params : SpecParam){
		    	  			  log.info(">exemmlmx -s -n "+NE+" -c \"JFI:UPD="+Params+";\"");
		  		  	    	  grab("exemmlmx -s -n "+NE+" -c \"JFI:UPD="+Params+";\"",
		  		  	    			FN, "XXX");
		    	  		}
	    	  		}else
		   	  		if (command.equalsIgnoreCase("JUQ;") && SpecParam!=null){
		    	  		for (String Params : SpecParam){
		    	  				log.info(">exemmlmx -s -n "+NE+" -c \"JUQ:NAME="+Params+";\"");
		    	  				grab("exemmlmx -s -n "+NE+" -c \"JUQ:NAME="+Params+";\"",
		    	  					FN, "XXX");
		    	  		}
		    	  	}else    	  
	    	  		if (command.equalsIgnoreCase("JUI:::;") && SpecParam!=null){
		    	  		for (String Params : SpecParam){
		    	  				log.info(">exemmlmx -s -n "+NE+" -c \"JUI:NAME="+Params+";\"");
		    	  				grab("exemmlmx -s -n "+NE+" -c \"JUI:NAME="+Params+";\"",
		    	  						FN, "XXX");
		    	  		}
		    	  	}else
	    	  		if (command.equalsIgnoreCase("RCI;") && SpecParam!=null){
	    	  			if (buffer!=null && !buffer.equals(""))
	    	  				SpecParam.add(buffer.charAt(buffer.length()-1)=='&' ? buffer.substring(0,buffer.lastIndexOf("&")) : buffer);
		    	  		for (String Params : SpecParam){
		    	  				log.info(">exemmlmx -s -n "+NE+" -c \"CEL:CGR="+Params+";\"");
		    	  				grab("exemmlmx -s -n "+NE+" -c \"CEL:CGR="+Params+";\"",
		    	  						FN, "XXX");
		    	  		}
		    	  		 Counter=0;
		    	  		 buffer="";
		    	  	}else
		    	  		if (command.equalsIgnoreCase("E3S;") && SpecParam!=null){
			    	  		for (String Params : SpecParam){			    	  				
			    	  			log.info(">exemmlmx -s -n "+NE+" -c \"E3J:POOLNAME="+Params+";\"");
			    	  				grab("exemmlmx -s -n "+NE+" -c \"E3J:POOLNAME="+Params+";\"",
			    	  					FN, "XXX");
			    	  		}
			    	}else
		    	  		if (command.equalsIgnoreCase("FWO:NSEI=0&&65535::;") && SpecParam!=null){
			    	  		for (String Params : SpecParam){			    	  				
			    	  			log.info(">exemmlmx -s -n "+NE+" -c \"EJL:NSEI="+Params+";\"");
			    	  				grab("exemmlmx -s -n "+NE+" -c \"EJL:NSEI="+Params+";\"",
			    	  					FN, "XXX");
			    	  		}
			    	  	}	
		      }

		} catch (Exception e) {
			log.info("Exception :"+e.getMessage());
		} finally {			
			log.info("NE ["+NE+"] Have been finished. Elapsed: "+(System.currentTimeMillis() - start));
			disconnect();
			threadTracker.decreaseCounter();						
		}
	}

	private synchronized void grab(String Runcommand, String FN, String command){
		  try {			  	
			  if(!session.isConnected())
				  connect();
			  
			  boolean IsSpecialCommand = false;

			  Channel channel = session.openChannel("exec");
	    	  	
		      channel.setInputStream(null);
		      ((ChannelExec)channel).setErrStream(System.err);
		   	  ((ChannelExec)channel).setCommand(Runcommand); 
		   	  channel.connect();
		      
		      InputStream is = channel.getInputStream();
		      BufferedReader in = new BufferedReader(new InputStreamReader(is));
		      FileWriter out; 
		      out= new FileWriter(FN, true);
		      
		     
		      if (command.equals("JFL;") || 
		    		  command.equals("JUQ;") || 
		    		  command.equals("JUI:::;") || 
		    		  command.equals("RCI;") || 
		    		  command.equals("E3S;") ||
		    		  command.equals("FWO:NSEI=0&&65535::;")
		    	  )
		    	  IsSpecialCommand=true;
		      
		      if (IsSpecialCommand){
		//1st	      	  
		    	  SpecParam = new ArrayList<String>();
		    	  String line;    	      	  
			       while ((line = in.readLine()) != null  && !channel.isClosed()) {
			         if (line != null) {
			        	 out.write(line +'\n');
			        	 out.flush();
			        	 ProcessSpecialCommand(line, command);
			         }
			       }
		      }else{
		//2nd     
		      byte[] tmp=new byte[1024];
		      while(true){
		    	  while(is.available()>0){
		    		  int i=is.read(tmp, 0, 1024);
		    		  if(i<0)break;
		    		  String tempStr = new String(tmp, 0, i);
		    		  out.write(tempStr);
		    		  out.flush();
		    		  
		    	  }
		    	  if(channel.isClosed()){
		    		  break;
		    	  }
		    	  try
		          {
//		             	Thread.sleep(1000);
			    	  	sleepTime = generator.nextInt( 1000 );
			    	  	Thread.sleep( sleepTime );
		          }
		          catch (Exception ee)
		          {
		        	  System.err.println("sleep exp "+ee);
		          }
		      }
		    }
		      
		    out.close();
		    in.close();
		    is.close();
		     
		    channel.disconnect();
		  	} catch (Exception e){
		  		log.error("An exception occured ["+NE+"]:"+e.getMessage());	
		  		int cek = checkAttempt("["+command+"]");
		  		if(cek>0){
		  			log.info("Regrabbing ["+NE+"]["+command+"] attempt "+cek+"..");
		  			grab(Runcommand, FN, command);
		  		}else
		  			log.error("Give Up Regrabbing ["+NE+"]["+command+"]");
		  		e.printStackTrace();
		    }
		}
		
		private synchronized void ProcessSpecialCommand(String Str, String command){
		try {
			if (command.equalsIgnoreCase("JFL;")){//"JFL;""./jfl.sh"
				if (Str.indexOf("AAL2")>-1 || Str.indexOf("IPV4")> -1)
				{
					String[] Splitted;
					Splitted= Str.split("\\s+");
					for(int i=0; i<Splitted.length; i++){
						if (i==1 && Splitted[i]!=null)
						{
							SpecParam.add(Splitted[i]);
							break;
						}
					}
				}
			}
			else 
				if (command.equalsIgnoreCase("JUQ;")){//"JUQ;""./juq.sh"
					if (Str.indexOf("RESULT NAME")>-1)
					{
						String[] Splitted;
						Splitted= Str.split("\\s+");
						for(int i=0; i<Splitted.length; i++){
							if (i==3 && Splitted[i]!=null)
							{
								SpecParam.add(Splitted[i]);
								break;
							}
						}
					}
				}	
			else 
			if (command.equalsIgnoreCase("JUI:::;")){//"JUI:::;""cat jui.txt"
				if (Str.indexOf("DETERMINATION")>-1)
				{
					String[] Splitted;
					Splitted= Str.split("\\s+");
					for(int i=0; i<Splitted.length; i++){
						if (i==0 && Splitted[i]!=null)
						{
							SpecParam.add(Splitted[i]);
							break;
						}
					}
				}
			}
			else 
			if (command.equalsIgnoreCase("RCI;")){//"RCI;""cat rci.txt"
				if ((Str.indexOf("BI")>-1 || Str.indexOf("OUT")>-1) && (Str.indexOf("WO-EX")>-1 || Str.indexOf("BA-US")>-1))
				{
					String[] Splitted;
					Splitted= Str.split("\\s+");
					for(int i=0; i<Splitted.length; i++){
						if (i==0 && Splitted[i]!=null){
							Counter++;
							buffer+=Splitted[i];
							if (Counter % 10 ==0){
								SpecParam.add(buffer);
								buffer="";
							}else
								buffer+="&";
							break;
						}
			
					}
				}
			}
			else 
				if (command.equalsIgnoreCase("E3S;")){//"E3S;""cat e3s.txt"
					if (Str.indexOf("POOLNAME . :")>-1)
					{
						String[] Splitted;
						Splitted= Str.split("\\s+");
						for(int i=0; i<Splitted.length; i++){
							if (i==6 && Splitted[i]!=null)
							{						
								SpecParam.add(Splitted[i].trim());
								break;
							}
						}
					}
				}
//			else
//				if (command.equalsIgnoreCase("FWO:NSEI=0&&65535::;")){
//					if(Str.startsWith(prefix))
//				}
			}catch (Exception e){
				log.error("EXCEPTIONS "+e);
			}
			
		}
		
		@SuppressWarnings("unused")
		private  String execCommand(String cmd){
		String res="";
		  try {
			  Channel channel = session.openChannel("exec");
		      channel.setInputStream(null);
		      ((ChannelExec)channel).setErrStream(System.err);
		   	  ((ChannelExec)channel).setCommand(cmd); 
		   	  channel.connect();
		      InputStream is = channel.getInputStream();
		      BufferedReader in = new BufferedReader(new InputStreamReader(is));
		      String line;
		      while ((line = in.readLine()) != null &&  !channel.isClosed()){
		    	 res+=line;
		      }
	      
		      in.close();
		      is.close();
		      channel.disconnect();
		      return res;
		  } catch (Exception e) {
			  log.info("EXCEPTIONS "+e);
			  return "";
		}
	}

	private int checkAttempt(String neCommand){
		
		final int maxAttempt = 3;
		Integer att = attempt.get(neCommand);
		if(att==null){
			attempt.put(neCommand, maxAttempt);
			return maxAttempt;
		}else
			attempt.put(neCommand, att-1);
		
		return att;
			
	}
}
