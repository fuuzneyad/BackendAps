package id.co.telkom.grabber.sshcli;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.apache.oro.text.regex.MalformedPatternException;

import com.jcraft.jsch.*;

import expect4j.Closure;
import expect4j.Expect4j;
import expect4j.ExpectState;
import expect4j.matches.Match;
import expect4j.matches.RegExpMatch;
import id.co.telkom.parser.common.propreader.NokiaCliPropReader;
import id.co.telkom.parser.common.util.ThreadTrackerCliNok;


public class EricssonGrabberThread extends Thread {
	private ThreadTrackerCliNok threadTracker;
	private ChannelShell channel;
    private Session session ;
	private String NE;
	private NokiaCliPropReader propreader;
	private ArrayList<String>COMMAND;
	@SuppressWarnings("unused")
	private ArrayList<String> SpecParam;
	@SuppressWarnings("unused")
	private String buffer="";
	@SuppressWarnings("unused")
	private int Counter=0;
	private static final Logger log = Logger.getLogger(EricssonGrabberThread.class);
	private NokiaCliPropReader conf;
	private static final int COMMAND_EXECUTION_SUCCESS_OPCODE = -2;
    private static String ENTER_CHARACTER = "\r";
    private Expect4j expect = null;
    private static Random generator = new Random();
    private static String[] linuxPromptRegEx = new String[]{"\\>","#", "~#", "\\<"};
	private String tanggal;
	private String FN;
	FileWriter out;
	
	public EricssonGrabberThread(/*Session session,*/ String NE, String tanggal, ThreadTrackerCliNok threadTracker, NokiaCliPropReader propreader, ArrayList<String>COMMAND, NokiaCliPropReader conf) {
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
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			config.put("PreferredAuthentications",//kerberos bypass 
            "publickey,keyboard-interactive,password");
			session.setConfig(config);
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
		try {
//			if(!session.isConnected())
//				  connect();
			if(getIt()){
			}else
				System.out.println("Cannot EAW "+NE+"!!");

		} catch (Exception e) {
			log.info("Exception :"+e.getMessage());
		} finally {			
			log.info("NE ["+NE+"] Have been finished. Elapsed: "+(System.currentTimeMillis() - start));
			disconnect();
			threadTracker.decreaseCounter();						
		}
	}

	
	@SuppressWarnings("unused")
	private synchronized void ProcessSpecialCommand(String Str, String command){
	try {
				
		}catch (Exception e){
			log.error("EXCEPTIONS "+e);
		}
			
	}
		

	
	private boolean getIt() throws IOException{
		System.out.println("eawing "+NE);
		String eawCommand = "eaw "+NE;
		Closure closure = new Closure() {
            public void run(ExpectState expectState) throws Exception {
            	out.write(expectState.getBuffer());
            }
        };
        boolean isSuccess = true;
        List<Match> lstPattern =  new ArrayList<Match>();
        for (String regexElement : linuxPromptRegEx) {
            try {
                Match mat = new RegExpMatch(regexElement, closure);
                lstPattern.add(mat);
            } catch (MalformedPatternException e) {
                e.printStackTrace();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        
        try {
            expect = SSH();
            isSuccess = isSuccess(lstPattern,eawCommand);
            if (!isSuccess) {
               isSuccess = isSuccess(lstPattern,eawCommand);
            }
            for(String command : COMMAND){
	    	  	String cmdString = command.split(":")[0].replace(";", "").replace(",", "");//added 2014-12-08 for traversa purpose
	    	  	FN=propreader.getLogFolder()+"/"+NE+"_"+cmdString+propreader.getLogFileName()+tanggal+".txt";
	    	  	out = new FileWriter(FN, true);
            	isSuccess = isSuccess(lstPattern,command+";");
                if (!isSuccess) {
                   isSuccess = isSuccess(lstPattern,command+";");
                }
                out.close();
	    	  	long sleepTime = generator.nextInt( 2000 );
	    	  	Thread.sleep( sleepTime );	
            }
            closeConnection();
 
        } catch (Exception ex) {
            ex.printStackTrace();
        }
		return isSuccess;
	}
	
	
	private boolean isSuccess(List<Match> objPattern,String strCommandPattern) {
        try {
            boolean isFailed = checkResult(expect.expect(objPattern));
 
            if (!isFailed) {
                expect.send(strCommandPattern);
                expect.send(ENTER_CHARACTER);
                return true;
            }
            return false;
        } catch (MalformedPatternException ex) {
            ex.printStackTrace();
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
	
	 private boolean checkResult(int intRetVal) {
	        if (intRetVal == COMMAND_EXECUTION_SUCCESS_OPCODE) {
	            return true;
	        }
	        return false;
	    }
	    /**
	     *
	     */
	    private void closeConnection() {
	        if (expect!=null) 
	            expect.close();
	        if(channel.isConnected())
	        	channel.disconnect();
	        if(session.isConnected())
	        	session.disconnect();
	    }
	    
	    private Expect4j SSH() throws Exception {
	        channel = (ChannelShell) session.openChannel("shell");
	        Expect4j expect = new Expect4j(channel.getInputStream(), channel.getOutputStream());
	        channel.connect();
	        return expect;
	    }
}
